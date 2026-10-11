package com.aimentor.service.llm;

/**
 * Exception thrown when LLM generation fails or encounters an unrecoverable error.
 */
public class LlmException extends Exception {
    private final int statusCode;

    public LlmException(String message) {
        super(message);
        this.statusCode = 500;
    }

    public LlmException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
    }

    public LlmException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
