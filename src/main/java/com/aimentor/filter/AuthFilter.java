package com.aimentor.filter;

import com.aimentor.util.JsonUtil;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Authentication filter protecting /app/* and /api/* endpoints.
 * Allows access to /api/login, /api/register, and /api/me (for session check).
 * Redirects browser requests to /login; returns 401 JSON for API requests.
 */
@WebFilter(urlPatterns = {"/app/*", "/api/*"})
public class AuthFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(AuthFilter.class.getName());

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        String relativePath = path.substring(contextPath.length());

        // Allow public API endpoints and static assets
        if (relativePath.equals("/api/login") ||
            relativePath.equals("/api/register") ||
            relativePath.equals("/api/me") ||
            relativePath.startsWith("/app/assets/") ||
            relativePath.endsWith(".js") ||
            relativePath.endsWith(".css") ||
            relativePath.endsWith(".svg") ||
            relativePath.endsWith(".png") ||
            relativePath.endsWith(".ico") ||
            relativePath.endsWith(".woff") ||
            relativePath.endsWith(".woff2")) {
            chain.doFilter(req, res);
            return;
        }

        // Check session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            if (relativePath.startsWith("/api/")) {
                // JSON error for API requests
                JsonUtil.writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Authentication required. Please log in.");
            } else {
                // Redirect browser requests to login
                response.sendRedirect(contextPath + "/login");
            }
            return;
        }

        chain.doFilter(req, res);
    }
}
