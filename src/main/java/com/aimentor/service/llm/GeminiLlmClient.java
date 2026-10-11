package com.aimentor.service.llm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Production Gemini API Client using standard Java 17 HttpClient.
 * Implements single retry, request timeouts, and structured JSON output configuration.
 */
public class GeminiLlmClient implements LlmClient {
    private static final Logger LOGGER = Logger.getLogger(GeminiLlmClient.class.getName());
    private static final Gson GSON = new Gson();

    private final String apiKey;
    private final String model;
    private final int timeoutSeconds;
    private final int maxRetries;
    private final HttpClient httpClient;

    public GeminiLlmClient(String apiKey, String model, int timeoutSeconds, int maxRetries) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API Key cannot be null or empty for GeminiLlmClient");
        }
        this.apiKey = apiKey.trim();
        this.model = (model == null || model.trim().isEmpty()) ? "gemini-2.0-flash" : model.trim();
        this.timeoutSeconds = timeoutSeconds > 0 ? timeoutSeconds : 30;
        this.maxRetries = maxRetries >= 0 ? maxRetries : 1;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public String generate(String systemPrompt, String userPrompt) throws LlmException {
        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/" 
                + model + ":generateContent?key=" + apiKey;

        JsonObject requestBody = buildRequestBody(systemPrompt, userPrompt);
        String jsonPayload = GSON.toJson(requestBody);

        int attempts = 0;
        Exception lastException = null;

        while (attempts <= maxRetries) {
            attempts++;
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(endpoint))
                        .header("Content-Type", "application/json")
                        .timeout(Duration.ofSeconds(timeoutSeconds))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                int status = response.statusCode();

                if (status >= 200 && status < 300) {
                    return extractTextFromResponse(response.body());
                } else {
                    LOGGER.warning("Gemini API returned HTTP " + status + ": " + response.body());
                    if (status == 429 || status >= 500) {
                        // Transient error, eligible for retry
                        if (attempts <= maxRetries) {
                            LOGGER.info("Retrying Gemini API call (attempt " + attempts + "/" + maxRetries + ")...");
                            Thread.sleep(1000);
                            continue;
                        }
                    }
                    throw new LlmException("Gemini API error (HTTP " + status + "): " + response.body(), status);
                }
            } catch (IOException | InterruptedException e) {
                lastException = e;
                LOGGER.log(Level.WARNING, "Gemini API request failed on attempt " + attempts, e);
                if (attempts <= maxRetries) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new LlmException("Interrupted while retrying LLM request", ie);
                    }
                }
            }
        }

        throw new LlmException("Gemini API call failed after " + (maxRetries + 1) + " attempts", lastException);
    }

    private JsonObject buildRequestBody(String systemPrompt, String userPrompt) {
        JsonObject root = new JsonObject();

        // System instructions
        if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
            JsonObject systemInstruction = new JsonObject();
            JsonArray systemParts = new JsonArray();
            JsonObject part = new JsonObject();
            part.addProperty("text", systemPrompt);
            systemParts.add(part);
            systemInstruction.add("parts", systemParts);
            root.add("system_instruction", systemInstruction);
        }

        // Contents
        JsonArray contents = new JsonArray();
        JsonObject userContent = new JsonObject();
        userContent.addProperty("role", "user");
        JsonArray userParts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", userPrompt);
        userParts.add(part);
        userContent.add("parts", userParts);
        contents.add(userContent);
        root.add("contents", contents);

        // Generation config requesting JSON output
        JsonObject genConfig = new JsonObject();
        genConfig.addProperty("temperature", 0.2);
        genConfig.addProperty("responseMimeType", "application/json");
        root.add("generationConfig", genConfig);

        return root;
    }

    private String extractTextFromResponse(String responseJson) throws LlmException {
        try {
            JsonObject obj = JsonParser.parseString(responseJson).getAsJsonObject();
            JsonArray candidates = obj.getAsJsonArray("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new LlmException("No candidates returned from Gemini API");
            }

            JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
            JsonObject content = firstCandidate.getAsJsonObject("content");
            if (content == null) {
                throw new LlmException("Empty content in Gemini candidate");
            }

            JsonArray parts = content.getAsJsonArray("parts");
            if (parts == null || parts.isEmpty()) {
                throw new LlmException("No parts in Gemini content");
            }

            return parts.get(0).getAsJsonObject().get("text").getAsString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to parse Gemini API JSON response", e);
            throw new LlmException("Malformed response from Gemini API: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "Gemini API (" + model + ")";
    }

    @Override
    public boolean isLive() {
        return true;
    }
}
