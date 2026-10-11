package com.aimentor.dao;

import com.aimentor.model.Submission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Data Access Object for Submission operations.
 */
public class SubmissionDAO {
    private static final Logger LOGGER = Logger.getLogger(SubmissionDAO.class.getName());

    /**
     * Create a new submission. Returns the generated ID.
     */
    public long create(Submission sub) throws SQLException {
        String sql = "INSERT INTO submissions (user_id, problem_id, language, code, status, output, ai_response) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, sub.getUserId());
            if (sub.getProblemId() != null) {
                ps.setLong(2, sub.getProblemId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }
            ps.setString(3, sub.getLanguage());
            ps.setString(4, sub.getCode());
            ps.setString(5, sub.getStatus().name());
            ps.setString(6, sub.getOutput());
            ps.setString(7, sub.getAiResponse());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    sub.setId(id);
                    return id;
                }
            }
            throw new SQLException("Failed to retrieve generated submission ID");
        }
    }

    /**
     * Find a submission by ID.
     */
    public Optional<Submission> findById(long id) throws SQLException {
        String sql = "SELECT * FROM submissions WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get all submissions for a user, most recent first.
     */
    public List<Submission> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM submissions WHERE user_id = ? ORDER BY created_at DESC";
        return queryList(sql, userId);
    }

    /**
     * Get recent submissions for a user (limited).
     */
    public List<Submission> findRecentByUserId(long userId, int limit) throws SQLException {
        String sql = "SELECT * FROM submissions WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        List<Submission> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Get submissions for a specific user and problem.
     */
    public List<Submission> findByUserAndProblem(long userId, long problemId) throws SQLException {
        String sql = "SELECT * FROM submissions WHERE user_id = ? AND problem_id = ? ORDER BY created_at DESC";
        List<Submission> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, problemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Update submission status, output, and AI response.
     */
    public void updateResult(long id, Submission.Status status, String output, String aiResponse) throws SQLException {
        String sql = "UPDATE submissions SET status = ?, output = ?, ai_response = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, output);
            ps.setString(3, aiResponse);
            ps.setLong(4, id);
            ps.executeUpdate();
        }
    }

    /**
     * Count total submissions.
     */
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM submissions";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /**
     * Count submissions per day for the last N days (for admin dashboard).
     */
    public List<Object[]> countPerDay(int days) throws SQLException {
        String sql = "SELECT DATE(created_at) as day, COUNT(*) as cnt " +
                     "FROM submissions WHERE created_at >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                     "GROUP BY DATE(created_at) ORDER BY day";
        List<Object[]> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[]{rs.getString("day"), rs.getInt("cnt")});
                }
            }
        }
        return results;
    }

    /**
     * Count submissions by a user.
     */
    public int countByUserId(long userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM submissions WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    private List<Submission> queryList(String sql, long userId) throws SQLException {
        List<Submission> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private Submission mapRow(ResultSet rs) throws SQLException {
        Submission sub = new Submission();
        sub.setId(rs.getLong("id"));
        sub.setUserId(rs.getLong("user_id"));
        long problemId = rs.getLong("problem_id");
        sub.setProblemId(rs.wasNull() ? null : problemId);
        sub.setLanguage(rs.getString("language"));
        sub.setCode(rs.getString("code"));
        sub.setStatus(Submission.Status.valueOf(rs.getString("status")));
        sub.setOutput(rs.getString("output"));
        sub.setAiResponse(rs.getString("ai_response"));
        sub.setCreatedAt(rs.getTimestamp("created_at"));
        return sub;
    }
}
