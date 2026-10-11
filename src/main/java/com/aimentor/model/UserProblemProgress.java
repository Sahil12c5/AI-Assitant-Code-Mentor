package com.aimentor.model;

import java.sql.Timestamp;

/**
 * Tracks a student's progress on a specific problem.
 */
public class UserProblemProgress {
    private long id;
    private long userId;
    private long problemId;
    private boolean solved;
    private int hintsUsed;
    private int attempts;
    private Timestamp completedAt;

    public UserProblemProgress() {}

    public UserProblemProgress(long userId, long problemId) {
        this.userId = userId;
        this.problemId = problemId;
        this.solved = false;
        this.hintsUsed = 0;
        this.attempts = 0;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getProblemId() { return problemId; }
    public void setProblemId(long problemId) { this.problemId = problemId; }

    public boolean isSolved() { return solved; }
    public void setSolved(boolean solved) { this.solved = solved; }

    public int getHintsUsed() { return hintsUsed; }
    public void setHintsUsed(int hintsUsed) { this.hintsUsed = hintsUsed; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public Timestamp getCompletedAt() { return completedAt; }
    public void setCompletedAt(Timestamp completedAt) { this.completedAt = completedAt; }

    @Override
    public String toString() {
        return "UserProblemProgress{userId=" + userId + ", problemId=" + problemId +
               ", solved=" + solved + ", attempts=" + attempts + "}";
    }
}
