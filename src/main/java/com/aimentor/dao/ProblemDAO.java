package com.aimentor.dao;

import com.aimentor.model.Problem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Data Access Object for Problem operations.
 */
public class ProblemDAO {
    private static final Logger LOGGER = Logger.getLogger(ProblemDAO.class.getName());

    /**
     * Create a new problem. Returns the generated ID.
     */
    public long create(Problem problem) throws SQLException {
        String sql = "INSERT INTO problems (title, description, topic, difficulty, language, starter_code, generated_from_problem_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, problem.getTitle());
            ps.setString(2, problem.getDescription());
            ps.setString(3, problem.getTopic());
            ps.setString(4, problem.getDifficulty().name());
            ps.setString(5, problem.getLanguage());
            ps.setString(6, problem.getStarterCode());
            if (problem.getGeneratedFromProblemId() != null) {
                ps.setLong(7, problem.getGeneratedFromProblemId());
            } else {
                ps.setNull(7, Types.BIGINT);
            }
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    problem.setId(id);
                    return id;
                }
            }
            throw new SQLException("Failed to retrieve generated problem ID");
        }
    }

    /**
     * Find a problem by ID.
     */
    public Optional<Problem> findById(long id) throws SQLException {
        String sql = "SELECT * FROM problems WHERE id = ?";
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
     * Get all problems.
     */
    public List<Problem> findAll() throws SQLException {
        String sql = "SELECT * FROM problems ORDER BY topic, difficulty, id";
        List<Problem> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Get problems by topic.
     */
    public List<Problem> findByTopic(String topic) throws SQLException {
        String sql = "SELECT * FROM problems WHERE topic = ? ORDER BY difficulty, id";
        List<Problem> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, topic);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Get problems by topic and difficulty.
     */
    public List<Problem> findByTopicAndDifficulty(String topic, Problem.Difficulty difficulty) throws SQLException {
        String sql = "SELECT * FROM problems WHERE topic = ? AND difficulty = ? ORDER BY id";
        List<Problem> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, topic);
            ps.setString(2, difficulty.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Get all distinct topics.
     */
    public List<String> findAllTopics() throws SQLException {
        String sql = "SELECT DISTINCT topic FROM problems ORDER BY topic";
        List<String> topics = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                topics.add(rs.getString("topic"));
            }
        }
        return topics;
    }

    /**
     * Count problems.
     */
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM problems";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /**
     * Delete a problem by ID.
     */
    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM problems WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Problem mapRow(ResultSet rs) throws SQLException {
        Problem p = new Problem();
        p.setId(rs.getLong("id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setTopic(rs.getString("topic"));
        p.setDifficulty(Problem.Difficulty.valueOf(rs.getString("difficulty")));
        p.setLanguage(rs.getString("language"));
        p.setStarterCode(rs.getString("starter_code"));
        long genId = rs.getLong("generated_from_problem_id");
        p.setGeneratedFromProblemId(rs.wasNull() ? null : genId);
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
