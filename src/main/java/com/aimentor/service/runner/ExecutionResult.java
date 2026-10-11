package com.aimentor.service.runner;

/**
 * Result of sandboxed code compilation and execution.
 */
public class ExecutionResult {

    public enum Status {
        SUCCESS,
        COMPILATION_ERROR,
        RUNTIME_ERROR,
        TIMEOUT,
        SECURITY_VIOLATION,
        INTERNAL_ERROR
    }

    private Status status;
    private String stdout;
    private String stderr;
    private long executionTimeMs;
    private int exitCode;

    public ExecutionResult() {
        this.status = Status.INTERNAL_ERROR;
        this.stdout = "";
        this.stderr = "";
        this.executionTimeMs = 0;
        this.exitCode = -1;
    }

    public ExecutionResult(Status status, String stdout, String stderr, long executionTimeMs, int exitCode) {
        this.status = status;
        this.stdout = stdout != null ? stdout : "";
        this.stderr = stderr != null ? stderr : "";
        this.executionTimeMs = executionTimeMs;
        this.exitCode = exitCode;
    }

    public static ExecutionResult securityViolation(String reason) {
        return new ExecutionResult(Status.SECURITY_VIOLATION, "", reason, 0, -1);
    }

    public static ExecutionResult timeout(long timeoutSeconds) {
        return new ExecutionResult(Status.TIMEOUT, "", "Execution timed out after " + timeoutSeconds + " seconds.", timeoutSeconds * 1000, -1);
    }

    public static ExecutionResult compilationError(String error) {
        return new ExecutionResult(Status.COMPILATION_ERROR, "", error, 0, 1);
    }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getStdout() { return stdout; }
    public void setStdout(String stdout) { this.stdout = stdout; }

    public String getStderr() { return stderr; }
    public void setStderr(String stderr) { this.stderr = stderr; }

    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public int getExitCode() { return exitCode; }
    public void setExitCode(int exitCode) { this.exitCode = exitCode; }
}
