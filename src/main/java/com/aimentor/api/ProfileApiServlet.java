package com.aimentor.api;

import com.aimentor.dao.MasteryDAO;
import com.aimentor.dao.ProgressDAO;
import com.aimentor.dao.SubmissionDAO;
import com.aimentor.dao.UserDAO;
import com.aimentor.model.TopicMastery;
import com.aimentor.model.User;
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
 * /api/profile - Returns Student Coding DNA, topic mastery breakdown, and learning metrics.
 */
@WebServlet(name = "ProfileApiServlet", urlPatterns = {"/api/profile"})
public class ProfileApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ProfileApiServlet.class.getName());

    private final UserDAO userDAO = new UserDAO();
    private final MasteryDAO masteryDAO = new MasteryDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();

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
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<TopicMastery> masteries = masteryDAO.findByUserId(userId);
            int totalSubmissions = submissionDAO.countByUserId(userId);
            int solvedProblems = progressDAO.countSolvedByUser(userId);

            double avgMastery = masteries.isEmpty() ? 50.0 :
                    masteries.stream().mapToDouble(TopicMastery::getScore).average().orElse(50.0);

            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("name", user.getName());
            data.put("email", user.getEmail());
            data.put("role", user.getRole().name());
            data.put("overallLevel", user.getLevel().name());
            data.put("averageMastery", Math.round(avgMastery * 10.0) / 10.0);
            data.put("totalSubmissions", totalSubmissions);
            data.put("solvedProblems", solvedProblems);
            data.put("topicMastery", masteries);

            JsonUtil.writeJson(response, HttpServletResponse.SC_OK, data);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Profile API error", e);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to fetch profile: " + e.getMessage());
        }
    }
}
