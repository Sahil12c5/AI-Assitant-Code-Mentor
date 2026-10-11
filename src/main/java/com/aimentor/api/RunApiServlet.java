package com.aimentor.api;

import com.aimentor.service.CodeRunnerService;
import com.aimentor.service.runner.ExecutionResult;
import com.aimentor.util.JsonUtil;
import com.aimentor.util.ValidationUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * /api/run - Compiles and executes student code in an isolated sandbox.
 */
@WebServlet(name = "RunApiServlet", urlPatterns = {"/api/run"})
public class RunApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RunApiServlet.class.getName());
    private final CodeRunnerService runnerService = new CodeRunnerService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        try {
            Map<String, Object> body = JsonUtil.readBodyAsMap(request);
            String code = (String) body.get("code");
            String language = (String) body.getOrDefault("language", "java");
            String stdin = (String) body.getOrDefault("input", "");

            String codeError = ValidationUtil.validateCode(code);
            if (codeError != null) {
                JsonUtil.writeError(response, HttpServletResponse.SC_BAD_REQUEST, codeError);
                return;
            }

            ExecutionResult result = runnerService.execute(code, language, stdin);

            Map<String, Object> respData = new HashMap<>();
            respData.put("success", true);
            respData.put("status", result.getStatus().name());
            respData.put("stdout", result.getStdout());
            respData.put("stderr", result.getStderr());
            respData.put("executionTimeMs", result.getExecutionTimeMs());
            respData.put("exitCode", result.getExitCode());

            JsonUtil.writeJson(response, HttpServletResponse.SC_OK, respData);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Code execution error", e);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Code runner error: " + e.getMessage());
        }
    }
}
