package com.aimentor.service.llm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Factory for creating and caching the singleton LlmClient instance.
 * Automatically chooses GeminiLlmClient if LLM_API_KEY is configured,
 * otherwise falls back seamlessly to MockLlmClient.
 */
public class LlmClientFactory {
    private static final Logger LOGGER = Logger.getLogger(LlmClientFactory.class.getName());
    private static volatile LlmClient clientInstance;

    private LlmClientFactory() {
        // Utility
    }

    public static LlmClient getInstance() {
        if (clientInstance == null) {
            synchronized (LlmClientFactory.class) {
                if (clientInstance == null) {
                    clientInstance = buildClient();
                }
            }
        }
        return clientInstance;
    }

    private static LlmClient buildClient() {
        Properties props = loadProperties();

        String apiKey = System.getenv("LLM_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = props.getProperty("llm.api.key", "");
            // If placeholder ${LLM_API_KEY} wasn't substituted
            if (apiKey.startsWith("${") && apiKey.endsWith("}")) {
                apiKey = "";
            }
        }

        String model = System.getenv("LLM_MODEL");
        if (model == null || model.isBlank()) {
            model = props.getProperty("llm.model", "gemini-2.0-flash");
            if (model.startsWith("${")) {
                model = "gemini-2.0-flash";
            }
        }

        int timeout = 30;
        try {
            timeout = Integer.parseInt(props.getProperty("llm.timeout.seconds", "30"));
        } catch (NumberFormatException ignored) {}

        int retries = 1;
        try {
            retries = Integer.parseInt(props.getProperty("llm.max.retries", "1"));
        } catch (NumberFormatException ignored) {}

        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("YOUR_GEMINI_API_KEY")) {
            LOGGER.info("Configuring live GeminiLlmClient with model: " + model);
            return new GeminiLlmClient(apiKey, model, timeout, retries);
        } else {
            LOGGER.info("No LLM_API_KEY detected. Initialized MockLlmClient for offline/pedagogical simulation.");
            return new MockLlmClient();
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = LlmClientFactory.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            LOGGER.warning("Could not read config.properties: " + e.getMessage());
        }
        return props;
    }
}
