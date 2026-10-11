package com.aimentor.controller;

import com.aimentor.dao.SubmissionDAO;
import com.aimentor.dao.MistakeDAO;
import com.aimentor.dao.MasteryDAO;
import com.aimentor.dao.ProgressDAO;
import com.aimentor.model.TopicMastery;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Student dashboard page controller.
 * Shows quick stats and links to the React SPA workspace.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());
    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();
    private final MasteryDAO masteryDAO = new MasteryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        long userId = (long) session.getAttribute("userId");

        try {
            // Quick stats
            int totalSubmissions = submissionDAO.countByUserId(userId);
            int solvedProblems = progressDAO.countSolvedByUser(userId);
            List<TopicMastery> masteryList = masteryDAO.findByUserId(userId);

            // Calculate average mastery
            double avgMastery = masteryList.isEmpty() ? 50.0 :
                    masteryList.stream().mapToDouble(TopicMastery::getScore).average().orElse(50.0);

            request.setAttribute("totalSubmissions", totalSubmissions);
            request.setAttribute("solvedProblems", solvedProblems);
            request.setAttribute("masteryList", masteryList);
            request.setAttribute("avgMastery", String.format("%.0f", avgMastery));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Dashboard error", e);
            request.setAttribute("totalSubmissions", 0);
            request.setAttribute("solvedProblems", 0);
            request.setAttribute("avgMastery", "50");
        }

        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }
}
