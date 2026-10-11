package com.aimentor.api;

import com.aimentor.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * /api/me - Returns current user info from session.
 * Used by the React SPA to check authentication state.
 */
@WebServlet(name = "MeApiServlet", urlPatterns = {"/api/me"})
public class MeApiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.writeJson(response, Map.of("authenticated", false));
            return;
        }

        Map<String, Object> user = new HashMap<>();
        user.put("authenticated", true);
        user.put("id", session.getAttribute("userId"));
        user.put("name", session.getAttribute("userName"));
        user.put("email", session.getAttribute("userEmail"));
        user.put("role", session.getAttribute("userRole"));
        user.put("level", session.getAttribute("userLevel"));
        JsonUtil.writeJson(response, user);
    }
}
