package com.aimentor.api;

import com.aimentor.service.AnalysisService;
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
 * /api/analyze - Submits code for Socratic AI analysis.
 * Generates Level 1 hint, registers mistakes, and updates student DNA.
 */
@WebServlet(name = "AnalyzeApiServlet", urlPatterns = {"/api/analyze"})
public class AnalyzeApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AnalyzeApiServlet.class.getName());
    private final AnalysisService analysisService = new AnalysisService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        long userId = (long) session.getAttribute("userId");

        try {
            Map<String, Object> body = JsonUtil.readBodyAsMap(request);
            String code = (String) body.get("code");
            String language = (String) body.getOrDefault("language", "java");

            Long problemId = null;
            Object pIdObj = body.get("problemId");
            if (pIdObj instanceof Number) {
                problemId = ((Number) pIdObj).longValue();
            } else if (pIdObj instanceof String && !((String) pIdObj).isBlank()) {
                problemId = Long.parseLong((String) pIdObj);
            }

            // Input Validation
            String codeError = ValidationUtil.validateCode(code);
            if (codeError != null) {
                JsonUtil.writeError(response, HttpServletResponse.SC_BAD_REQUEST, codeError);
                return;
            }

            AnalysisService.AnalysisResult result = analysisService.analyzeCode(userId, problemId, code, language);

            Map<String, Object> respData = new HashMap<>();
            respData.put("success", true);
            respData.put("submissionId", result.getSubmissionId());
            respData.put("hintSessionId", result.getHintSessionId());
            respData.put("currentLevel", result.getCurrentLevel());
            respData.put("isCorrect", result.isCorrect());
            respData.put("concept", result.getConcept());
            respData.put("hint", result.getHint());
            respData.put("mistakes", result.getMistakes());
            respData.put("masteryScore", result.getUpdatedMasteryScore());
            respData.put("topic", result.getTopic());

            JsonUtil.writeJson(response, HttpServletResponse.SC_OK, respData);

        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Analyze endpoint failure", e);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to analyze code: " + e.getMessage());
        }
    }
}
