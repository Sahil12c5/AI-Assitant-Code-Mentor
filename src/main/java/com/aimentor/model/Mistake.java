package com.aimentor.model;

import java.sql.Timestamp;

/**
 * Represents a detected mistake in a student's code submission.
 * Part of the Mistake Memory system.
 */
public class Mistake {
    private long id;
    private long userId;
    private long submissionId;
    private String mistakeType;
    private String topic;
    private Integer lineNumber;
    private String description;
    private Timestamp createdAt;

    public Mistake() {}

    public Mistake(long userId, long submissionId, String mistakeType, String topic,
                   Integer lineNumber, String description) {
        this.userId = userId;
        this.submissionId = submissionId;
        this.mistakeType = mistakeType;
        this.topic = topic;
        this.lineNumber = lineNumber;
        this.description = description;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getSubmissionId() { return submissionId; }
    public void setSubmissionId(long submissionId) { this.submissionId = submissionId; }

    public String getMistakeType() { return mistakeType; }
    public void setMistakeType(String mistakeType) { this.mistakeType = mistakeType; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public Integer getLineNumber() { return lineNumber; }
    public void setLineNumber(Integer lineNumber) { this.lineNumber = lineNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Mistake{id=" + id + ", type='" + mistakeType + "', topic='" + topic + "', line=" + lineNumber + "}";
    }
}
