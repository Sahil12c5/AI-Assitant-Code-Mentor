package com.aimentor.api;

import com.aimentor.model.User;
import com.aimentor.service.AuthService;
import com.aimentor.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * /api/login - JSON login endpoint for React SPA.
 */
@WebServlet(name = "LoginApiServlet", urlPatterns = {"/api/login"})
public class LoginApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginApiServlet.class.getName());
    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Map<String, Object> body = JsonUtil.readBodyAsMap(request);
            String email = (String) body.get("email");
            String password = (String) body.get("password");

            User user = authService.login(email, password);

            // Session fixation protection
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) oldSession.invalidate();
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userRole", user.getRole().name());
            session.setAttribute("userLevel", user.getLevel().name());

            JsonUtil.writeJson(response, Map.of(
                "success", true,
                "user", Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", user.getRole().name(),
                    "level", user.getLevel().name()
                )
            ));
        } catch (AuthService.AuthException e) {
            JsonUtil.writeError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "API login error", e);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred");
        }
    }
}
