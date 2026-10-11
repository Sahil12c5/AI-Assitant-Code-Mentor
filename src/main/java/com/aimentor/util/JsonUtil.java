package com.aimentor.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Utility class for JSON serialization/deserialization and
 * writing JSON HTTP responses.
 */
public class JsonUtil {
    private static final Logger LOGGER = Logger.getLogger(JsonUtil.class.getName());
    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .disableHtmlEscaping()
            .create();

    private static final Gson GSON_PRETTY = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    private JsonUtil() {}

    /**
     * Serialize an object to JSON string.
     */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * Deserialize a JSON string to an object.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * Read JSON body from request.
     */
    public static String readBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * Parse request body into a Map.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> readBodyAsMap(HttpServletRequest request) throws IOException {
        String body = readBody(request);
        return GSON.fromJson(body, Map.class);
    }

    /**
     * Write a JSON success response.
     */
    public static void writeJson(HttpServletResponse response, Object data) throws IOException {
        writeJson(response, HttpServletResponse.SC_OK, data);
    }

    /**
     * Write a JSON response with a specific status code.
     */
    public static void writeJson(HttpServletResponse response, int statusCode, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(GSON.toJson(data));
            out.flush();
        }
    }

    /**
     * Write a JSON error response with the standard { "error": "message" } format.
     */
    public static void writeError(HttpServletResponse response, int statusCode, String message) throws IOException {
        writeJson(response, statusCode, Map.of("error", message));
    }

    /**
     * Try to parse JSON, returning null if malformed.
     */
    public static <T> T tryParse(String json, Class<T> clazz) {
        try {
            return GSON.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            LOGGER.warning("Failed to parse JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get the shared Gson instance.
     */
    public static Gson getGson() {
        return GSON;
    }
}
