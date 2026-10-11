package com.aimentor.model;

import java.sql.Timestamp;

/**
 * Represents a coding problem in the system.
 */
public class Problem {
    private long id;
    private String title;
    private String description;
    private String topic;
    private Difficulty difficulty;
    private String language;
    private String starterCode;
    private Long generatedFromProblemId;
    private Timestamp createdAt;

    public enum Difficulty {
        BEGINNER, EASY, MEDIUM, HARD, INTERVIEW;

        /**
         * Returns the next difficulty level, or the same if already at maximum.
         */
        public Difficulty next() {
            return switch (this) {
                case BEGINNER -> EASY;
                case EASY -> MEDIUM;
                case MEDIUM -> HARD;
                case HARD, INTERVIEW -> INTERVIEW;
            };
        }

        /**
         * Returns the previous difficulty level, or the same if already at minimum.
         */
        public Difficulty previous() {
            return switch (this) {
                case BEGINNER, EASY -> BEGINNER;
                case MEDIUM -> EASY;
                case HARD -> MEDIUM;
                case INTERVIEW -> HARD;
            };
        }
    }

    public Problem() {}

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getStarterCode() { return starterCode; }
    public void setStarterCode(String starterCode) { this.starterCode = starterCode; }

    public Long getGeneratedFromProblemId() { return generatedFromProblemId; }
    public void setGeneratedFromProblemId(Long generatedFromProblemId) { this.generatedFromProblemId = generatedFromProblemId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Problem{id=" + id + ", title='" + title + "', topic='" + topic + "', difficulty=" + difficulty + "}";
    }
}
