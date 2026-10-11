package com.aimentor.service.llm;

/**
 * Interface for LLM communication.
 * Allows transparent switching between real Gemini API and offline Mock client.
 */
public interface LlmClient {

    /**
     * Send a generation prompt to the LLM and receive the raw string response (expected to be JSON).
     *
     * @param systemPrompt The overarching pedagogical instructions and behavioral constraints.
     * @param userPrompt   The student code, profile DNA, and context.
     * @return Generated string response from the LLM.
     * @throws LlmException If request fails, times out, or returns an error status.
     */
    String generate(String systemPrompt, String userPrompt) throws LlmException;

    /**
     * @return Name or identifier of this client implementation (e.g. "Gemini-2.0-Flash", "Mock-Client").
     */
    String getProviderName();

    /**
     * @return True if using real LLM API with an active key, false if offline mock mode.
     */
    boolean isLive();
}
