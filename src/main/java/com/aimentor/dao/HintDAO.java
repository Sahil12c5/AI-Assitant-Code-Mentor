package com.aimentor.dao;

import com.aimentor.model.Hint;
import com.aimentor.model.HintSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Data Access Object for HintSession and Hint operations.
 * Supports the Progressive Hint System.
 */
public class HintDAO {
    private static final Logger LOGGER = Logger.getLogger(HintDAO.class.getName());

    // ======================== HintSession ========================

    /**
     * Create a new hint session.
     */
    public long createSession(HintSession session) throws SQLException {
        String sql = "INSERT INTO hint_sessions (user_id, submission_id, current_level, solution_revealed) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, session.getUserId());
            ps.setLong(2, session.getSubmissionId());
            ps.setInt(3, session.getCurrentLevel());
            ps.setBoolean(4, session.isSolutionRevealed());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    session.setId(id);
                    return id;
                }
            }
            throw new SQLException("Failed to retrieve generated hint session ID");
        }
    }

    /**
     * Find hint session by submission ID.
     */
    public Optional<HintSession> findSessionBySubmissionId(long submissionId) throws SQLException {
        String sql = "SELECT * FROM hint_sessions WHERE submission_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapSession(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Find hint session by ID.
     */
    public Optional<HintSession> findSessionById(long id) throws SQLException {
        String sql = "SELECT * FROM hint_sessions WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapSession(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Update hint session level and solution revealed status.
     */
    public void updateSession(HintSession session) throws SQLException {
        String sql = "UPDATE hint_sessions SET current_level = ?, solution_revealed = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, session.getCurrentLevel());
            ps.setBoolean(2, session.isSolutionRevealed());
            ps.setLong(3, session.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Get or create a hint session for a submission.
     */
    public HintSession getOrCreateSession(long userId, long submissionId) throws SQLException {
        Optional<HintSession> existing = findSessionBySubmissionId(submissionId);
        if (existing.isPresent()) {
            return existing.get();
        }
        HintSession session = new HintSession(userId, submissionId);
        createSession(session);
        return session;
    }

    // ======================== Hint ========================

    /**
     * Create a new hint record.
     */
    public long createHint(Hint hint) throws SQLException {
        String sql = "INSERT INTO hints (hint_session_id, level, content) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, hint.getHintSessionId());
            ps.setInt(2, hint.getLevel());
            ps.setString(3, hint.getContent());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    hint.setId(id);
                    return id;
                }
            }
            throw new SQLException("Failed to retrieve generated hint ID");
        }
    }

    /**
     * Get all hints for a session.
     */
    public List<Hint> findHintsBySessionId(long sessionId) throws SQLException {
        String sql = "SELECT * FROM hints WHERE hint_session_id = ? ORDER BY level";
        List<Hint> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapHint(rs));
                }
            }
        }
        return list;
    }

    /**
     * Get a specific hint by session and level.
     */
    public Optional<Hint> findHintBySessionAndLevel(long sessionId, int level) throws SQLException {
        String sql = "SELECT * FROM hints WHERE hint_session_id = ? AND level = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            ps.setInt(2, level);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapHint(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Delete a hint by its ID (used to clear stale/invalid cached hints).
     */
    public void deleteHint(long hintId) throws SQLException {
        String sql = "DELETE FROM hints WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, hintId);
            ps.executeUpdate();
        }
    }

    // ======================== Mappers ========================

    private HintSession mapSession(ResultSet rs) throws SQLException {
        HintSession s = new HintSession();
        s.setId(rs.getLong("id"));
        s.setUserId(rs.getLong("user_id"));
        s.setSubmissionId(rs.getLong("submission_id"));
        s.setCurrentLevel(rs.getInt("current_level"));
        s.setSolutionRevealed(rs.getBoolean("solution_revealed"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }

    private Hint mapHint(ResultSet rs) throws SQLException {
        Hint h = new Hint();
        h.setId(rs.getLong("id"));
        h.setHintSessionId(rs.getLong("hint_session_id"));
        h.setLevel(rs.getInt("level"));
        h.setContent(rs.getString("content"));
        h.setCreatedAt(rs.getTimestamp("created_at"));
        return h;
    }
}
