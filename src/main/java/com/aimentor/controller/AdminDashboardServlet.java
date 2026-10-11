package com.aimentor.controller;

import com.aimentor.dao.MistakeDAO;
import com.aimentor.dao.SubmissionDAO;
import com.aimentor.dao.UserDAO;
import com.aimentor.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Admin portal controller showing platform-wide analytics:
 * - Aggregate student counts
 * - Total code submissions & 7-day velocity
 * - Most common mistake anti-patterns across student population
 * - Registered students directory
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminDashboardServlet.class.getName());

    private final UserDAO userDAO = new UserDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final MistakeDAO mistakeDAO = new MistakeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<User> students = userDAO.findByRole(User.Role.STUDENT);
            int totalSubmissions = submissionDAO.countAll();
            List<Object[]> submissionsPerDay = submissionDAO.countPerDay(7);
            List<Object[]> topMistakes = mistakeDAO.topMistakeTypesAllStudents(10);

            request.setAttribute("students", students);
            request.setAttribute("totalStudents", students.size());
            request.setAttribute("totalSubmissions", totalSubmissions);
            request.setAttribute("submissionsPerDay", submissionsPerDay);
            request.setAttribute("topMistakes", topMistakes);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Admin dashboard error", e);
            request.setAttribute("error", "Failed to compile admin telemetry: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
