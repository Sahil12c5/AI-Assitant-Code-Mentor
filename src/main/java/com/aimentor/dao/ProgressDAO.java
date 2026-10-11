package com.aimentor.dao;

import com.aimentor.model.UserProblemProgress;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Data Access Object for UserProblemProgress.
 */
public class ProgressDAO {
    private static final Logger LOGGER = Logger.getLogger(ProgressDAO.class.getName());

    /**
     * Get or create progress for user+problem.
     */
    public UserProblemProgress getOrCreate(long userId, long problemId) throws SQLException {
        Optional<UserProblemProgress> existing = findByUserAndProblem(userId, problemId);
        if (existing.isPresent()) {
            return existing.get();
        }
        UserProblemProgress progress = new UserProblemProgress(userId, problemId);
        create(progress);
        return progress;
    }

    /**
     * Create a new progress record.
     */
    public long create(UserProblemProgress progress) throws SQLException {
        String sql = "INSERT INTO user_problem_progress (user_id, problem_id, solved, hints_used, attempts, completed_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE solved = VALUES(solved), hints_used = VALUES(hints_used), " +
                     "attempts = VALUES(attempts), completed_at = VALUES(completed_at)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, progress.getUserId());
            ps.setLong(2, progress.getProblemId());
            ps.setBoolean(3, progress.isSolved());
            ps.setInt(4, progress.getHintsUsed());
            ps.setInt(5, progress.getAttempts());
            if (progress.getCompletedAt() != null) {
                ps.setTimestamp(6, progress.getCompletedAt());
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    progress.setId(id);
                    return id;
                }
            }
        }
        return progress.getId();
    }

    /**
     * Find progress for a specific user and problem.
     */
    public Optional<UserProblemProgress> findByUserAndProblem(long userId, long problemId) throws SQLException {
        String sql = "SELECT * FROM user_problem_progress WHERE user_id = ? AND problem_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, problemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Check if a specific problem is already solved by a user.
     */
    public boolean isSolvedByUser(long userId, long problemId) throws SQLException {
        Optional<UserProblemProgress> p = findByUserAndProblem(userId, problemId);
        return p.isPresent() && p.get().isSolved();
    }

    /**
     * Get all progress records for a user.
     */
    public List<UserProblemProgress> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM user_problem_progress WHERE user_id = ? ORDER BY problem_id";
        List<UserProblemProgress> list = new ArrayList<>();
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

    /**
     * Update progress (increment attempts, update solved status, hints used).
     */
    public void update(UserProblemProgress progress) throws SQLException {
        String sql = "UPDATE user_problem_progress SET solved = ?, hints_used = ?, attempts = ?, " +
                     "completed_at = ? WHERE user_id = ? AND problem_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, progress.isSolved());
            ps.setInt(2, progress.getHintsUsed());
            ps.setInt(3, progress.getAttempts());
            if (progress.getCompletedAt() != null) {
                ps.setTimestamp(4, progress.getCompletedAt());
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }
            ps.setLong(5, progress.getUserId());
            ps.setLong(6, progress.getProblemId());
            ps.executeUpdate();
        }
    }

    /**
     * Get recent progress records for a user (last N solved/attempted).
     */
    public List<UserProblemProgress> findRecentByUserId(long userId, int limit) throws SQLException {
        String sql = "SELECT * FROM user_problem_progress WHERE user_id = ? " +
                     "ORDER BY COALESCE(completed_at, CURRENT_TIMESTAMP) DESC LIMIT ?";
        List<UserProblemProgress> list = new ArrayList<>();
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
     * Count solved problems for a user.
     */
    public int countSolvedByUser(long userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_problem_progress WHERE user_id = ? AND solved = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    private UserProblemProgress mapRow(ResultSet rs) throws SQLException {
        UserProblemProgress p = new UserProblemProgress();
        p.setId(rs.getLong("id"));
        p.setUserId(rs.getLong("user_id"));
        p.setProblemId(rs.getLong("problem_id"));
        p.setSolved(rs.getBoolean("solved"));
        p.setHintsUsed(rs.getInt("hints_used"));
        p.setAttempts(rs.getInt("attempts"));
        p.setCompletedAt(rs.getTimestamp("completed_at"));
        return p;
    }
}
