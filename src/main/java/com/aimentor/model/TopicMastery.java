package com.aimentor.model;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Represents a student's mastery level for a specific topic.
 * Part of the Student Coding DNA system.
 *
 * Mastery scoring formula:
 * - On correct solution: score += (100 - score) * 0.15  (diminishing returns)
 * - On mistake: score -= score * 0.10 (proportional decrease)
 * - Score is clamped to [0, 100]
 */
public class TopicMastery {
    private long id;
    private long userId;
    private String topic;
    private double score;
    private int attempts;
    private int successes;
    private Map<String, Integer> commonMistakes;
    private Timestamp updatedAt;

    public TopicMastery() {
        this.score = 50.0; // Default starting score
    }

    public TopicMastery(long userId, String topic) {
        this.userId = userId;
        this.topic = topic;
        this.score = 50.0;
        this.attempts = 0;
        this.successes = 0;
    }

    /**
     * Increase mastery on a correct solution.
     * Uses diminishing returns: the higher the score, the smaller the gain.
     */
    public void recordSuccess() {
        this.attempts++;
        this.successes++;
        this.score = Math.min(100.0, this.score + (100.0 - this.score) * 0.15);
    }

    /**
     * Decrease mastery on a mistake.
     * Proportional decrease: the higher the score, the larger the penalty.
     */
    public void recordMistake() {
        this.attempts++;
        this.score = Math.max(0.0, this.score - this.score * 0.10);
    }

    /**
     * Determine the suggested assistance level based on the current score.
     */
    public String getSuggestedAssistanceLevel() {
        if (score < 30) return "HIGH";
        if (score < 60) return "MEDIUM";
        return "LOW";
    }

    /**
     * Derive the overall level from score.
     */
    public String getDerivedLevel() {
        if (score < 35) return "BEGINNER";
        if (score < 70) return "INTERMEDIATE";
        return "ADVANCED";
    }

    /**
     * Get success rate as a percentage.
     */
    public double getSuccessRate() {
        if (attempts == 0) return 0.0;
        return (double) successes / attempts * 100.0;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public int getSuccesses() { return successes; }
    public void setSuccesses(int successes) { this.successes = successes; }

    public Map<String, Integer> getCommonMistakes() { return commonMistakes; }
    public void setCommonMistakes(Map<String, Integer> commonMistakes) { this.commonMistakes = commonMistakes; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "TopicMastery{userId=" + userId + ", topic='" + topic + "', score=" + score +
               ", level=" + getDerivedLevel() + ", assistance=" + getSuggestedAssistanceLevel() + "}";
    }
}
