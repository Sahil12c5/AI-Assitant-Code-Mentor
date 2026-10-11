package com.aimentor.model;

import java.sql.Timestamp;

/**
 * Represents a single hint within a hint session.
 */
public class Hint {
    private long id;
    private long hintSessionId;
    private int level;
    private String content;
    private Timestamp createdAt;

    public Hint() {}

    public Hint(long hintSessionId, int level, String content) {
        this.hintSessionId = hintSessionId;
        this.level = level;
        this.content = content;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getHintSessionId() { return hintSessionId; }
    public void setHintSessionId(long hintSessionId) { this.hintSessionId = hintSessionId; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Hint{id=" + id + ", sessionId=" + hintSessionId + ", level=" + level + "}";
    }
}
