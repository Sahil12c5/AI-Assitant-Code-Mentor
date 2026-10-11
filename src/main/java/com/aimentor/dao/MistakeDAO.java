package com.aimentor.dao;

import com.aimentor.model.Mistake;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Data Access Object for Mistake operations.
 * Supports the Mistake Memory system including trend analysis.
 */
public class MistakeDAO {
    private static final Logger LOGGER = Logger.getLogger(MistakeDAO.class.getName());

    /**
     * Create a new mistake record.
     */
    public long create(Mistake mistake) throws SQLException {
        String sql = "INSERT INTO mistakes (user_id, submission_id, mistake_type, topic, line_number, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, mistake.getUserId());
            ps.setLong(2, mistake.getSubmissionId());
            ps.setString(3, mistake.getMistakeType());
            ps.setString(4, mistake.getTopic());
            if (mistake.getLineNumber() != null) {
                ps.setInt(5, mistake.getLineNumber());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setString(6, mistake.getDescription());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    mistake.setId(id);
                    return id;
                }
            }
            throw new SQLException("Failed to retrieve generated mistake ID");
        }
    }

    /**
     * Batch-insert multiple mistakes for a submission.
     */
    public void createBatch(List<Mistake> mistakes) throws SQLException {
        String sql = "INSERT INTO mistakes (user_id, submission_id, mistake_type, topic, line_number, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Mistake m : mistakes) {
                ps.setLong(1, m.getUserId());
                ps.setLong(2, m.getSubmissionId());
                ps.setString(3, m.getMistakeType());
                ps.setString(4, m.getTopic());
                if (m.getLineNumber() != null) {
                    ps.setInt(5, m.getLineNumber());
                } else {
                    ps.setNull(5, Types.INTEGER);
                }
                ps.setString(6, m.getDescription());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Get all mistakes for a user, most recent first.
     */
    public List<Mistake> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM mistakes WHERE user_id = ? ORDER BY created_at DESC";
        return queryList(sql, userId);
    }

    /**
     * Get recent mistakes for a user with limit.
     */
    public List<Mistake> findRecentByUserId(long userId, int limit) throws SQLException {
        String sql = "SELECT * FROM mistakes WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        List<Mistake> list = new ArrayList<>();
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
     * Get mistakes for a specific submission.
     */
    public List<Mistake> findBySubmissionId(long submissionId) throws SQLException {
        String sql = "SELECT * FROM mistakes WHERE submission_id = ? ORDER BY line_number";
        List<Mistake> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Count mistakes per type for a user (for profile display).
     * Returns a map of mistake_type -> count, ordered by count descending.
     */
    public Map<String, Integer> countByTypeForUser(long userId) throws SQLException {
        String sql = "SELECT mistake_type, COUNT(*) as cnt FROM mistakes " +
                     "WHERE user_id = ? GROUP BY mistake_type ORDER BY cnt DESC";
        Map<String, Integer> counts = new LinkedHashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    counts.put(rs.getString("mistake_type"), rs.getInt("cnt"));
                }
            }
        }
        return counts;
    }

    /**
     * Count mistakes per type for a user within a given number of days.
     * Used for trend analysis.
     */
    public Map<String, Integer> countByTypeForUserInDays(long userId, int days) throws SQLException {
        String sql = "SELECT mistake_type, COUNT(*) as cnt FROM mistakes " +
                     "WHERE user_id = ? AND created_at >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                     "GROUP BY mistake_type ORDER BY cnt DESC";
        Map<String, Integer> counts = new LinkedHashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    counts.put(rs.getString("mistake_type"), rs.getInt("cnt"));
                }
            }
        }
        return counts;
    }

    /**
     * Count mistakes per type and topic for a user (for DNA profile).
     */
    public Map<String, Integer> countByTypeForUserAndTopic(long userId, String topic) throws SQLException {
        String sql = "SELECT mistake_type, COUNT(*) as cnt FROM mistakes " +
                     "WHERE user_id = ? AND topic = ? GROUP BY mistake_type ORDER BY cnt DESC";
        Map<String, Integer> counts = new LinkedHashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, topic);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    counts.put(rs.getString("mistake_type"), rs.getInt("cnt"));
                }
            }
        }
        return counts;
    }

    /**
     * Get top mistake types across all students (for admin dashboard).
     */
    public List<Object[]> topMistakeTypesAllStudents(int limit) throws SQLException {
        String sql = "SELECT mistake_type, topic, COUNT(*) as cnt FROM mistakes " +
                     "GROUP BY mistake_type, topic ORDER BY cnt DESC LIMIT ?";
        List<Object[]> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[]{
                        rs.getString("mistake_type"),
                        rs.getString("topic"),
                        rs.getInt("cnt")
                    });
                }
            }
        }
        return results;
    }

    /**
     * Weekly mistake counts for trend analysis.
     * Returns counts for each of the last 3 weeks per mistake type.
     */
    public List<Object[]> weeklyCountsByUser(long userId) throws SQLException {
        String sql = "SELECT mistake_type, " +
                     "SUM(CASE WHEN created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 1 ELSE 0 END) as week1, " +
                     "SUM(CASE WHEN created_at >= DATE_SUB(NOW(), INTERVAL 14 DAY) AND created_at < DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 1 ELSE 0 END) as week2, " +
                     "SUM(CASE WHEN created_at >= DATE_SUB(NOW(), INTERVAL 21 DAY) AND created_at < DATE_SUB(NOW(), INTERVAL 14 DAY) THEN 1 ELSE 0 END) as week3 " +
                     "FROM mistakes WHERE user_id = ? AND created_at >= DATE_SUB(NOW(), INTERVAL 21 DAY) " +
                     "GROUP BY mistake_type";
        List<Object[]> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[]{
                        rs.getString("mistake_type"),
                        rs.getInt("week1"),
                        rs.getInt("week2"),
                        rs.getInt("week3")
                    });
                }
            }
        }
        return results;
    }

    private List<Mistake> queryList(String sql, long userId) throws SQLException {
        List<Mistake> list = new ArrayList<>();
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

    private Mistake mapRow(ResultSet rs) throws SQLException {
        Mistake m = new Mistake();
        m.setId(rs.getLong("id"));
        m.setUserId(rs.getLong("user_id"));
        m.setSubmissionId(rs.getLong("submission_id"));
        m.setMistakeType(rs.getString("mistake_type"));
        m.setTopic(rs.getString("topic"));
        int line = rs.getInt("line_number");
        m.setLineNumber(rs.wasNull() ? null : line);
        m.setDescription(rs.getString("description"));
        m.setCreatedAt(rs.getTimestamp("created_at"));
        return m;
    }
}
