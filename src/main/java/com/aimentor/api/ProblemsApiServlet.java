package com.aimentor.api;

import com.aimentor.dao.ProblemDAO;
import com.aimentor.dao.ProgressDAO;
import com.aimentor.model.Problem;
import com.aimentor.service.ProblemGeneratorService;
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
 * /api/problems - Problem catalog, adaptive problem recommendation, and isomorphic problem generation.
 */
@WebServlet(name = "ProblemsApiServlet", urlPatterns = {"/api/problems", "/api/problems/next", "/api/problems/generate"})
public class ProblemsApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ProblemsApiServlet.class.getName());

    private final ProblemDAO problemDAO = new ProblemDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();
    private final ProblemGeneratorService problemGenService = new ProblemGeneratorService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        long userId = (long) session.getAttribute("userId");
        String uri = request.getRequestURI();

        try {
            if (uri.endsWith("/next")) {
                // Next adaptive problem recommendation
                Problem nextProblem = problemGenService.getNextRecommendedProblem(userId);
                if (nextProblem == null) {
                    JsonUtil.writeError(response, HttpServletResponse.SC_NOT_FOUND, "No problems available");
                    return;
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK, nextProblem);
                return;
            }

            // General list of problems
            String topic = request.getParameter("topic");
            String difficultyStr = request.getParameter("difficulty");

            List<Problem> problems;
            if (topic != null && !topic.isBlank()) {
                if (difficultyStr != null && !difficultyStr.isBlank()) {
                    Problem.Difficulty diff = Problem.Difficulty.valueOf(difficultyStr.toUpperCase());
                    problems = problemDAO.findByTopicAndDifficulty(topic, diff);
                } else {
                    problems = problemDAO.findByTopic(topic);
                }
            } else {
                problems = problemDAO.findAll();
            }

            // Decorate with solved status per problem
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Problem p : problems) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("title", p.getTitle());
                map.put("description", p.getDescription());
                map.put("topic", p.getTopic());
                map.put("difficulty", p.getDifficulty().name());
                map.put("language", p.getLanguage());
                map.put("starterCode", p.getStarterCode());
                map.put("isGenerated", p.getGeneratedFromProblemId() != null);
                map.put("generatedFromProblemId", p.getGeneratedFromProblemId());
                map.put("isSolved", progressDAO.isSolvedByUser(userId, p.getId()));
                resultList.add(map);
            }

            List<String> allTopics = problemDAO.findAllTopics();

            Map<String, Object> responsePayload = new HashMap<>();
            responsePayload.put("problems", resultList);
            responsePayload.put("topics", allTopics);

            JsonUtil.writeJson(response, HttpServletResponse.SC_OK, responsePayload);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Problems API error", e);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to retrieve problems: " + e.getMessage());
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
        String uri = request.getRequestURI();

        if (uri.endsWith("/generate")) {
            try {
                Map<String, Object> body = JsonUtil.readBodyAsMap(request);
                Object sourceIdObj = body.get("sourceProblemId");
                if (sourceIdObj == null) {
                    JsonUtil.writeError(response, HttpServletResponse.SC_BAD_REQUEST, "sourceProblemId is required");
                    return;
                }

                long sourceId = (sourceIdObj instanceof Number) 
                        ? ((Number) sourceIdObj).longValue() 
                        : Long.parseLong(sourceIdObj.toString());

                Problem generatedProblem = problemGenService.generateSimilarProblem(userId, sourceId);

                Map<String, Object> data = new HashMap<>();
                data.put("success", true);
                data.put("problem", generatedProblem);

                JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED, data);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Problem generation error", e);
                JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Failed to generate isomorphic problem: " + e.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }
}
