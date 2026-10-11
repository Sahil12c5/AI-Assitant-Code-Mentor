package com.aimentor.api;

import com.aimentor.dao.ProblemDAO;
import com.aimentor.dao.SubmissionDAO;
import com.aimentor.model.Problem;
import com.aimentor.model.Submission;
import com.aimentor.util.JsonUtil;

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
 * /api/submissions - Retrieves code submission history for student analytics and workspace review.
 */
@WebServlet(name = "SubmissionsApiServlet", urlPatterns = {"/api/submissions"})
public class SubmissionsApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(SubmissionsApiServlet.class.getName());

    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final ProblemDAO problemDAO = new ProblemDAO();

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
}
