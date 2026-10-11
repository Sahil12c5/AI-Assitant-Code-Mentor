package com.aimentor.dao;

import com.aimentor.model.TopicMastery;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

/**
 * Data Access Object for TopicMastery (Student Coding DNA).
 */
public class MasteryDAO {
    private static final Logger LOGGER = Logger.getLogger(MasteryDAO.class.getName());
    private static final Gson GSON = new Gson();
    private static final Type MISTAKE_MAP_TYPE = new TypeToken<Map<String, Integer>>(){}.getType();

    /**
     * Get or create a topic mastery record for a user+topic.
     * If no record exists, creates one with default score of 50.
     */
    public TopicMastery getOrCreate(long userId, String topic) throws SQLException {
        Optional<TopicMastery> existing = findByUserAndTopic(userId, topic);
        if (existing.isPresent()) {
            return existing.get();
        }
        TopicMastery mastery = new TopicMastery(userId, topic);
        create(mastery);
        return mastery;
    }

    /**
     * Create a new topic mastery record.
     */
    public long create(TopicMastery mastery) throws SQLException {
        String sql = "INSERT INTO topic_mastery (user_id, topic, score, attempts, successes, common_mistakes) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE score = VALUES(score), attempts = VALUES(attempts), " +
                     "successes = VALUES(successes), common_mistakes = VALUES(common_mistakes)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, mastery.getUserId());
            ps.setString(2, mastery.getTopic());
            ps.setDouble(3, mastery.getScore());
            ps.setInt(4, mastery.getAttempts());
            ps.setInt(5, mastery.getSuccesses());
            String mistakesJson = mastery.getCommonMistakes() != null ?
                    GSON.toJson(mastery.getCommonMistakes()) : null;
            ps.setString(6, mistakesJson);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    mastery.setId(id);
                    return id;
                }
            }
        }
        return mastery.getId();
    }

    /**
     * Find mastery record for a specific user and topic.
     */
    public Optional<TopicMastery> findByUserAndTopic(long userId, String topic) throws SQLException {
        String sql = "SELECT * FROM topic_mastery WHERE user_id = ? AND topic = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, topic);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get all mastery records for a user (the full DNA profile).
     */
    public List<TopicMastery> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM topic_mastery WHERE user_id = ? ORDER BY topic";
        List<TopicMastery> list = new ArrayList<>();
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
     * Update mastery score and stats.
     */
    public void update(TopicMastery mastery) throws SQLException {
        String sql = "UPDATE topic_mastery SET score = ?, attempts = ?, successes = ?, " +
                     "common_mistakes = ? WHERE user_id = ? AND topic = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, mastery.getScore());
            ps.setInt(2, mastery.getAttempts());
            ps.setInt(3, mastery.getSuccesses());
            String mistakesJson = mastery.getCommonMistakes() != null ?
                    GSON.toJson(mastery.getCommonMistakes()) : null;
            ps.setString(4, mistakesJson);
            ps.setLong(5, mastery.getUserId());
            ps.setString(6, mastery.getTopic());
            ps.executeUpdate();
        }
    }

    /**
     * Add a mistake type to the common_mistakes JSON for a user+topic.
     */
    public void addCommonMistake(long userId, String topic, String mistakeType) throws SQLException {
        TopicMastery mastery = getOrCreate(userId, topic);
        Map<String, Integer> mistakes = mastery.getCommonMistakes();
        if (mistakes == null) {
            mistakes = new LinkedHashMap<>();
        }
        mistakes.merge(mistakeType, 1, Integer::sum);
        mastery.setCommonMistakes(mistakes);
        update(mastery);
    }

    private TopicMastery mapRow(ResultSet rs) throws SQLException {
        TopicMastery m = new TopicMastery();
        m.setId(rs.getLong("id"));
        m.setUserId(rs.getLong("user_id"));
        m.setTopic(rs.getString("topic"));
        m.setScore(rs.getDouble("score"));
        m.setAttempts(rs.getInt("attempts"));
        m.setSuccesses(rs.getInt("successes"));
        String mistakesJson = rs.getString("common_mistakes");
        if (mistakesJson != null && !mistakesJson.isEmpty()) {
            Map<String, Integer> map = GSON.fromJson(mistakesJson, MISTAKE_MAP_TYPE);
            m.setCommonMistakes(map);
        }
        m.setUpdatedAt(rs.getTimestamp("updated_at"));
        return m;
    }
}
