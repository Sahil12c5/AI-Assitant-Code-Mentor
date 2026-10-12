package com.aimentor.api;

import com.aimentor.dao.MasteryDAO;
import com.aimentor.dao.ProblemDAO;
import com.aimentor.dao.ProgressDAO;
import com.aimentor.dao.SubmissionDAO;
import com.aimentor.model.Problem;
import com.aimentor.model.Submission;
import com.aimentor.model.TopicMastery;
import com.aimentor.service.AnalysisService;
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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * /api/submissions and /api/submit
 * - GET: Retrieves code submission history for student analytics and workspace review.
 * - POST: Explicitly submits student code, executes in sandbox, evaluates correctness,
 *         and immediately updates topic mastery (attempts, solved) and problem progress.
 */
@WebServlet(name = "SubmissionsApiServlet", urlPatterns = {"/api/submissions", "/api/submit"})
public class SubmissionsApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(SubmissionsApiServlet.class.getName());

    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final ProblemDAO problemDAO = new ProblemDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();
    private final MasteryDAO masteryDAO = new MasteryDAO();
    private final AnalysisService analysisService = new AnalysisService();
    private final CodeRunnerService runnerService = new CodeRunnerService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        long userId = (long) session.getAttribute("userId");
        int limit = 50;
        String limitParam = request.getParameter("limit");
        if (limitParam != null && !limitParam.isBlank()) {
            try {
                limit = Math.min(100, Integer.parseInt(limitParam));
            } catch (NumberFormatException ignored) {}
        }

        try {
            List<Submission> submissions = submissionDAO.findRecentByUserId(userId, limit);

            // Preload problem titles
            Map<Long, String> problemTitleMap = new HashMap<>();
            List<Map<String, Object>> resultList = new ArrayList<>();

            for (Submission s : submissions) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("problemId", s.getProblemId());
                map.put("language", s.getLanguage());
                map.put("code", s.getCode());
                map.put("status", s.getStatus().name());
                map.put("output", s.getOutput());
                map.put("createdAt", s.getCreatedAt() != null ? s.getCreatedAt().toString() : "");

                if (s.getProblemId() != null) {
                    if (!problemTitleMap.containsKey(s.getProblemId())) {
                        Optional<Problem> p = problemDAO.findById(s.getProblemId());
                        problemTitleMap.put(s.getProblemId(), p.map(Problem::getTitle).orElse("Custom Problem"));
                    }
                    map.put("problemTitle", problemTitleMap.get(s.getProblemId()));
                } else {
                    map.put("problemTitle", "Scratchpad / Ad-Hoc");
                }

                resultList.add(map);
            }

            JsonUtil.writeJson(response, HttpServletResponse.SC_OK, Map.of("submissions", resultList));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Submissions API error", e);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to fetch submissions: " + e.getMessage());
        }
    }

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

            // 1. Run in isolated process sandbox for execution metrics
            ExecutionResult runResult = runnerService.execute(code, language, "");

            // 2. Perform official graded analysis and record submission
            AnalysisService.AnalysisResult result = analysisService.analyzeCode(userId, problemId, code, language);

            // 3. Attach full stdout/stderr to submission record
            if (result.getSubmissionId() > 0) {
                String fullOutput = (runResult.getStdout() != null ? runResult.getStdout() : "") +
                        (runResult.getStderr() != null && !runResult.getStderr().isBlank() ? "\n" + runResult.getStderr() : "");
                submissionDAO.updateResult(
                        result.getSubmissionId(),
                        result.isCorrect() ? Submission.Status.CORRECT : Submission.Status.INCORRECT,
                        fullOutput,
                        result.getHint()
                );
            }

            // 4. Fetch freshly updated dashboard metrics
            String topic = (result.getTopic() != null && !result.getTopic().isBlank()) ? result.getTopic() : "General Programming";
            TopicMastery topicMastery = masteryDAO.getOrCreate(userId, topic);
            int totalSolved = progressDAO.countSolvedByUser(userId);
            int totalSubmissions = submissionDAO.countByUserId(userId);

            Map<String, Object> respData = new HashMap<>();
            respData.put("success", true);
            respData.put("submissionId", result.getSubmissionId());
            respData.put("hintSessionId", result.getHintSessionId());
            respData.put("currentLevel", result.getCurrentLevel());
            respData.put("isCorrect", result.isCorrect());
            respData.put("status", result.isCorrect() ? "CORRECT" : "INCORRECT");
            respData.put("concept", result.getConcept());
            respData.put("hint", result.getHint());
            respData.put("mistakes", result.getMistakes());
            respData.put("masteryScore", result.getUpdatedMasteryScore());
            respData.put("topic", topic);
            respData.put("topicSolved", topicMastery.getSuccesses());
            respData.put("topicAttempts", topicMastery.getAttempts());
            respData.put("totalSolved", totalSolved);
            respData.put("totalSubmissions", totalSubmissions);
            respData.put("stdout", runResult.getStdout());
            respData.put("stderr", runResult.getStderr());
            respData.put("executionTimeMs", runResult.getExecutionTimeMs());
            respData.put("exitCode", runResult.getExitCode());

            JsonUtil.writeJson(response, HttpServletResponse.SC_OK, respData);

        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Submission API endpoint failure", e);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to process submission: " + e.getMessage());
        }
    }
}
