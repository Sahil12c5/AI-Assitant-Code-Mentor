package com.aimentor.api;

import com.aimentor.dao.MistakeDAO;
import com.aimentor.model.Mistake;
import com.aimentor.service.MistakeTrendService;
import com.aimentor.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * /api/mistakes - Returns longitudinal Mistake Memory and 3-week trend diagnostics.
 */
@WebServlet(name = "MistakesApiServlet", urlPatterns = {"/api/mistakes"})
public class MistakesApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(MistakesApiServlet.class.getName());

    private final MistakeDAO mistakeDAO = new MistakeDAO();
    private final MistakeTrendService trendService = new MistakeTrendService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        long userId = (long) session.getAttribute("userId");

        try {
            List<MistakeTrendService.MistakeTrend> trends = trendService.computeTrendsForUser(userId);
            Map<String, Integer> counts = mistakeDAO.countByTypeForUser(userId);
            List<Mistake> recentMistakes = mistakeDAO.findRecentByUserId(userId, 20);

            Map<String, Object> data = new HashMap<>();
            data.put("trends", trends);
            data.put("typeCounts", counts);
            data.put("recentMistakes", recentMistakes);

            JsonUtil.writeJson(response, HttpServletResponse.SC_OK, data);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Mistakes API error", e);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to fetch mistake trends: " + e.getMessage());
        }
    }
}
