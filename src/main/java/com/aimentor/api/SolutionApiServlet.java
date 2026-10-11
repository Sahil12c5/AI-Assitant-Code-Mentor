package com.aimentor.api;

import com.aimentor.service.HintService;
import com.aimentor.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * /api/solution - The ONLY endpoint authorized to return full solution code (Level 4).
 * Requires explicit user confirmation from UI dialog before invoking.
 */
@WebServlet(name = "SolutionApiServlet", urlPatterns = {"/api/solution"})
public class SolutionApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(SolutionApiServlet.class.getName());
    private final HintService hintService = new HintService();

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
            Object sessionIdObj = body.get("hintSessionId");
            if (sessionIdObj == null) {
                JsonUtil.writeError(response, HttpServletResponse.SC_BAD_REQUEST, "hintSessionId is required");
                return;
            }

            long hintSessionId;
            if (sessionIdObj instanceof Number) {
                hintSessionId = ((Number) sessionIdObj).longValue();
            } else {
                hintSessionId = Long.parseLong(sessionIdObj.toString());
            }

            HintService.SolutionResponse solResp = hintService.revealSolution(userId, hintSessionId);

            Map<String, Object> respData = new HashMap<>();
            respData.put("success", true);
            respData.put("hintSessionId", solResp.getHintSessionId());
            respData.put("level", solResp.getLevel());
            respData.put("solution", solResp.getSolution());
            respData.put("solutionJava", solResp.getSolutionJava());
            respData.put("solutionPython", solResp.getSolutionPython());
            respData.put("explanation", solResp.getExplanation());

            JsonUtil.writeJson(response, HttpServletResponse.SC_OK, respData);

        } catch (SecurityException e) {
            JsonUtil.writeError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Solution endpoint error", e);
            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to reveal solution: " + e.getMessage());
        }
    }
}
