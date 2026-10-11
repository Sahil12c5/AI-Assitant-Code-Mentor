package com.aimentor.model;

import java.sql.Timestamp;

/**
 * Represents a code submission by a student.
 */
public class Submission {
    private long id;
    private long userId;
    private Long problemId;
    private String language;
    private String code;
    private Status status;
    private String output;
    private String aiResponse;
    private Timestamp createdAt;

    public enum Status {
        PENDING, CORRECT, INCORRECT, ERROR, TIMEOUT
    }

    public Submission() {}

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public Long getProblemId() { return problemId; }
    public void setProblemId(Long problemId) { this.problemId = problemId; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }

    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Submission{id=" + id + ", userId=" + userId + ", problemId=" + problemId + ", status=" + status + "}";
    }
}
