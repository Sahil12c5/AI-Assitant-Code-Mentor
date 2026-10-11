package com.aimentor.model;

import java.sql.Timestamp;

/**
 * Represents a hint session for a specific submission.
 * Tracks the progressive hint level (1-4) and whether the solution was revealed.
 * 
 * Hint levels:
 *   1 - Guiding question pointing at the problem area
 *   2 - Stronger hint explaining the concept involved
 *   3 - Full explanation of why the code is wrong
 *   4 - Corrected solution (only via explicit "Show Solution" request)
 */
public class HintSession {
    public static final int MIN_LEVEL = 1;
    public static final int MAX_HINT_LEVEL = 3;
    public static final int SOLUTION_LEVEL = 4;

    private long id;
    private long userId;
    private long submissionId;
    private int currentLevel;
    private boolean solutionRevealed;
    private Timestamp createdAt;

    public HintSession() {
        this.currentLevel = MIN_LEVEL;
        this.solutionRevealed = false;
    }

    public HintSession(long userId, long submissionId) {
        this.userId = userId;
        this.submissionId = submissionId;
        this.currentLevel = MIN_LEVEL;
        this.solutionRevealed = false;
    }

    /**
     * Advance to the next hint level (max 3). Level 4 requires explicit solution request.
     * @return true if the level was advanced, false if already at max hint level.
     */
    public boolean advanceLevel() {
        if (currentLevel < MAX_HINT_LEVEL) {
            currentLevel++;
            return true;
        }
        return false;
    }

    /**
     * Reveal the solution (level 4). Only called from the explicit /api/solution endpoint.
     */
    public void revealSolution() {
        this.currentLevel = SOLUTION_LEVEL;
        this.solutionRevealed = true;
    }

    /**
     * Check if more hints are available (below level 3).
     */
    public boolean canAdvance() {
        return currentLevel < MAX_HINT_LEVEL;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getSubmissionId() { return submissionId; }
    public void setSubmissionId(long submissionId) { this.submissionId = submissionId; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public boolean isSolutionRevealed() { return solutionRevealed; }
    public void setSolutionRevealed(boolean solutionRevealed) { this.solutionRevealed = solutionRevealed; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "HintSession{id=" + id + ", submissionId=" + submissionId +
               ", level=" + currentLevel + ", solutionRevealed=" + solutionRevealed + "}";
    }
}
