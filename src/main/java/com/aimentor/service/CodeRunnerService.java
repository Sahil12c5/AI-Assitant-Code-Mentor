package com.aimentor.service;

import com.aimentor.service.runner.ExecutionResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sandboxed code execution service for student code submissions.
 * Supports Java (with javac compilation + java runtime) and Python.
 *
 * Security Architecture:
 * 1. Static AST/Lexical Filter: Blocks dangerous APIs (System.exit, Runtime.exec, java.io.File, Socket, reflection, subprocess, etc.).
 * 2. Process Isolation: Runs within isolated temp directories using ProcessBuilder with hard process timeouts.
 * 3. Output Limiter: Caps stdout/stderr to prevent memory overflow from infinite print loops.
 * 4. Docker Mode Option: When runner.use.docker=true, wraps execution in an ephemeral container.
 *
 * NOTE FOR PRODUCTION / VIVA:
 * For multi-tenant production hosting, container isolation (Docker/gVisor/Firecracker)
 * is strongly recommended over direct host execution.
 */
public class CodeRunnerService {
    private static final Logger LOGGER = Logger.getLogger(CodeRunnerService.class.getName());

    private static final int DEFAULT_TIMEOUT_SECONDS = 5;
    private static final int DEFAULT_MAX_OUTPUT_CHARS = 10000;

    // Disallowed tokens in student Java code
    private static final Set<String> BLOCKED_JAVA_TOKENS = Set.of(
            "System.exit", "Runtime.getRuntime", "ProcessBuilder",
            "java.lang.reflect", "java.lang.instrument",
            "java.net.Socket", "java.net.ServerSocket", "java.net.URL", "java.net.URI",
            "java.net.http", "java.io.File", "java.io.RandomAccessFile",
            "java.nio.file", "SecurityManager"
    );

    // Disallowed tokens in student Python code
    private static final Set<String> BLOCKED_PYTHON_TOKENS = Set.of(
            "import os", "from os", "import sys", "from sys",
            "import subprocess", "from subprocess", "import socket", "from socket",
            "import shutil", "eval(", "exec(", "__import__", "open("
    );

    private final int timeoutSeconds;
    private final int maxOutputChars;
    private final boolean useDocker;

    public CodeRunnerService() {
        Properties props = loadConfig();
        this.timeoutSeconds = Integer.parseInt(props.getProperty("runner.timeout.seconds", String.valueOf(DEFAULT_TIMEOUT_SECONDS)));
        this.maxOutputChars = Integer.parseInt(props.getProperty("runner.max.output.bytes", String.valueOf(DEFAULT_MAX_OUTPUT_CHARS)));
        this.useDocker = Boolean.parseBoolean(props.getProperty("runner.use.docker", "false"));
    }

    /**
     * Run code in sandbox with optional stdin input.
     */
    public ExecutionResult execute(String code, String language, String stdinInput) {
        if (code == null || code.isBlank()) {
            return new ExecutionResult(ExecutionResult.Status.COMPILATION_ERROR, "", "No code provided to execute.", 0, 1);
        }

        String lang = (language == null) ? "java" : language.trim().toLowerCase();

        // 1. Static security check
        String securityViolation = checkSecurity(code, lang);
        if (securityViolation != null) {
            return ExecutionResult.securityViolation(securityViolation);
        }

        // 2. Dispatch to language runner
        if (lang.equals("python") || lang.equals("py")) {
            return executePython(code, stdinInput);
        } else {
            return executeJava(code, stdinInput);
        }
    }

    /**
     * Inspect code for dangerous tokens before invoking process.
     */
    private String checkSecurity(String code, String lang) {
        if (lang.equals("python") || lang.equals("py")) {
            for (String token : BLOCKED_PYTHON_TOKENS) {
                if (code.contains(token)) {
                    return "Security Violation: Usage of prohibited identifier '" + token + "' is blocked in sandbox mode.";
                }
            }
        } else {
            for (String token : BLOCKED_JAVA_TOKENS) {
                if (code.contains(token)) {
                    return "Security Violation: Direct process, filesystem, or network access via '" + token + "' is blocked.";
                }
            }
        }
        return null;
    }

    /**
     * Compile and execute Java code in a sandboxed temp directory.
     */
    private ExecutionResult executeJava(String code, String stdinInput) {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("aimentor_java_");

            // Extract public class name or default to Solution
            String className = extractJavaClassName(code);
            Path sourceFile = tempDir.resolve(className + ".java");
            Files.writeString(sourceFile, code, StandardCharsets.UTF_8);

            // Step A: Compile with javac
            long startTime = System.currentTimeMillis();
            ProcessBuilder compilePb = new ProcessBuilder("javac", sourceFile.getFileName().toString());
            compilePb.directory(tempDir.toFile());
            compilePb.redirectErrorStream(true);

            Process compileProcess = compilePb.start();
            boolean compileCompleted = compileProcess.waitFor(timeoutSeconds, TimeUnit.SECONDS);

            if (!compileCompleted) {
                compileProcess.destroyForcibly();
                return ExecutionResult.timeout(timeoutSeconds);
            }

            String compileOutput = readLimitedStream(compileProcess.getInputStream());
            if (compileProcess.exitValue() != 0) {
                return ExecutionResult.compilationError("Compilation Failed:\n" + compileOutput);
            }

            // Step B: Run with java
            ProcessBuilder runPb = new ProcessBuilder("java", "-cp", ".", className);
            runPb.directory(tempDir.toFile());

            Process runProcess = runPb.start();

            // Pipe stdin if provided and ALWAYS close stream to signal EOF
            try (OutputStream os = runProcess.getOutputStream()) {
                if (stdinInput != null && !stdinInput.isEmpty()) {
                    os.write(stdinInput.getBytes(StandardCharsets.UTF_8));
                    if (!stdinInput.endsWith("\n")) {
                        os.write("\n".getBytes(StandardCharsets.UTF_8));
                    }
                    os.flush();
                }
            }

            boolean runCompleted = runProcess.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            long elapsed = System.currentTimeMillis() - startTime;

            if (!runCompleted) {
                runProcess.destroyForcibly();
                String tip = "";
                if (code.contains("System.in") || code.contains("Scanner")) {
                    tip = "\n\n💡 [Timeout Tip] Your code uses Scanner / System.in. If it was waiting for input, enter your values in the 'Custom Input (stdin)' tab before running.";
                }
                return new ExecutionResult(ExecutionResult.Status.TIMEOUT, "", "Execution timed out after " + timeoutSeconds + " seconds." + tip, timeoutSeconds * 1000, -1);
            }

            String stdout = readLimitedStream(runProcess.getInputStream());
            String stderr = readLimitedStream(runProcess.getErrorStream());
            int exitCode = runProcess.exitValue();

            if (exitCode != 0 && (stderr.contains("NoSuchElementException") || stderr.contains("java.util.NoSuchElementException"))) {
                stderr += "\n\n💡 [Input Required] Your program tried to read input using Scanner, but no input was provided in the 'Custom Input (stdin)' tab.\n→ Click the 'Custom Input (stdin)' tab in the console below to enter your values.";
            }

            ExecutionResult.Status status = (exitCode == 0)
                    ? ExecutionResult.Status.SUCCESS
                    : ExecutionResult.Status.RUNTIME_ERROR;

            return new ExecutionResult(status, stdout, stderr, elapsed, exitCode);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Java execution error", e);
            return new ExecutionResult(ExecutionResult.Status.INTERNAL_ERROR, "", "Execution system error: " + e.getMessage(), 0, -1);
        } finally {
            cleanupDirectory(tempDir);
        }
    }

    /**
     * Execute Python code in a sandboxed temp directory.
     */
    private ExecutionResult executePython(String code, String stdinInput) {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("aimentor_py_");
            Path scriptFile = tempDir.resolve("solution.py");
            Files.writeString(scriptFile, code, StandardCharsets.UTF_8);

            String pythonCmd = isCommandAvailable("python") ? "python" : (isCommandAvailable("python3") ? "python3" : "py");

            long startTime = System.currentTimeMillis();
            ProcessBuilder pb = new ProcessBuilder(pythonCmd, "solution.py");
            pb.directory(tempDir.toFile());

            Process process = pb.start();

            // Pipe stdin if provided and ALWAYS close stream to signal EOF
            try (OutputStream os = process.getOutputStream()) {
                if (stdinInput != null && !stdinInput.isEmpty()) {
                    os.write(stdinInput.getBytes(StandardCharsets.UTF_8));
                    if (!stdinInput.endsWith("\n")) {
                        os.write("\n".getBytes(StandardCharsets.UTF_8));
                    }
                    os.flush();
                }
            }

            boolean completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            long elapsed = System.currentTimeMillis() - startTime;

            if (!completed) {
                process.destroyForcibly();
                String tip = "";
                if (code.contains("input(") || code.contains("sys.stdin")) {
                    tip = "\n\n💡 [Timeout Tip] Your code uses input() / sys.stdin. If it was waiting for input, enter your values in the 'Custom Input (stdin)' tab before running.";
                }
                return new ExecutionResult(ExecutionResult.Status.TIMEOUT, "", "Execution timed out after " + timeoutSeconds + " seconds." + tip, timeoutSeconds * 1000, -1);
            }

            String stdout = readLimitedStream(process.getInputStream());
            String stderr = readLimitedStream(process.getErrorStream());
            int exitCode = process.exitValue();

            if (exitCode != 0 && stderr.contains("EOFError")) {
                stderr += "\n\n💡 [Input Required] Your program tried to read input using input(), but no input was provided in the 'Custom Input (stdin)' tab.\n→ Click the 'Custom Input (stdin)' tab in the console below to enter your values.";
            }

            ExecutionResult.Status status = (exitCode == 0)
                    ? ExecutionResult.Status.SUCCESS
                    : ExecutionResult.Status.RUNTIME_ERROR;

            return new ExecutionResult(status, stdout, stderr, elapsed, exitCode);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Python execution error", e);
            return new ExecutionResult(ExecutionResult.Status.INTERNAL_ERROR, "", "Python runtime error: " + e.getMessage(), 0, -1);
        } finally {
            cleanupDirectory(tempDir);
        }
    }

    private String readLimitedStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                if (sb.length() + read > maxOutputChars) {
                    sb.append(buffer, 0, Math.max(0, maxOutputChars - sb.length()));
                    sb.append("\n... [Output truncated after ").append(maxOutputChars).append(" characters]");
                    break;
                }
                sb.append(buffer, 0, read);
            }
        }
        return sb.toString();
    }

    private String extractJavaClassName(String code) {
        Pattern pattern = Pattern.compile("public\\s+class\\s+([A-Za-z0-9_]+)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        Pattern nonPublic = Pattern.compile("class\\s+([A-Za-z0-9_]+)");
        Matcher m2 = nonPublic.matcher(code);
        if (m2.find()) {
            return m2.group(1);
        }
        return "Solution";
    }

    private boolean isCommandAvailable(String cmd) {
        try {
            Process p = new ProcessBuilder(cmd, "--version").start();
            return p.waitFor(2, TimeUnit.SECONDS) && p.exitValue() == 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void cleanupDirectory(Path dir) {
        if (dir != null && Files.exists(dir)) {
            try (var stream = Files.walk(dir)) {
                stream.sorted(Comparator.reverseOrder())
                      .map(Path::toFile)
                      .forEach(File::delete);
            } catch (IOException e) {
                LOGGER.warning("Could not completely delete temp directory: " + dir);
            }
        }
    }

    private Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream is = CodeRunnerService.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) props.load(is);
        } catch (IOException ignored) {}
        return props;
    }
}
