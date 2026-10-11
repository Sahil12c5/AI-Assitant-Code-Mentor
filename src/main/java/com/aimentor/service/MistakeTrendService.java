package com.aimentor.service;

import com.aimentor.dao.MistakeDAO;

import java.sql.SQLException;
import java.util.*;

/**
 * Service for computing longitudinal mistake memory and 3-week trend trajectories.
 * Classifies error patterns as IMPROVING, STABLE, or DETERIORATING,
 * and recommends targeted lessons for deteriorating competencies.
 */
public class MistakeTrendService {

    public enum TrendDirection {
        IMPROVING,
        STABLE,
        DETERIORATING
    }

    public static class MistakeTrend {
        private String mistakeType;
        private int currentWeekCount;  // last 7 days (week 1)
        private int previousWeekCount; // 7-14 days ago (week 2)
        private int oldestWeekCount;   // 14-21 days ago (week 3)
        private int totalCount;
        private TrendDirection direction;
        private String recommendedLesson;
        private String topic;

        public MistakeTrend(String mistakeType, int currentWeekCount, int previousWeekCount, int oldestWeekCount) {
            this.mistakeType = mistakeType;
            this.currentWeekCount = currentWeekCount;
            this.previousWeekCount = previousWeekCount;
            this.oldestWeekCount = oldestWeekCount;
            this.totalCount = currentWeekCount + previousWeekCount + oldestWeekCount;
            calculateDirection();
            assignRecommendation();
        }

        private void calculateDirection() {
            // Compare recent week to past baseline
            if (currentWeekCount < oldestWeekCount) {
                this.direction = TrendDirection.IMPROVING;
            } else if (currentWeekCount > oldestWeekCount) {
                this.direction = TrendDirection.DETERIORATING;
            } else {
                if (currentWeekCount < previousWeekCount) {
                    this.direction = TrendDirection.IMPROVING;
                } else if (currentWeekCount > previousWeekCount) {
                    this.direction = TrendDirection.DETERIORATING;
                } else {
                    this.direction = TrendDirection.STABLE;
                }
            }
        }

        private void assignRecommendation() {
            String lower = mistakeType.toLowerCase();
            if (lower.contains("offbyone") || lower.contains("boundary")) {
                this.topic = "Loops & Arrays";
                this.recommendedLesson = "Mastering Loop Termination & Zero-Indexed Arrays (Interval Arithmetic)";
            } else if (lower.contains("infiniteloop") || lower.contains("loop")) {
                this.topic = "Loops";
                this.recommendedLesson = "Loop Guards, Mutation Invariants & State Convergence";
            } else if (lower.contains("nullpointer") || lower.contains("null")) {
                this.topic = "OOP & Memory";
                this.recommendedLesson = "Defensive Null Checking and Safe Reference Initialization";
            } else if (lower.contains("indexout") || lower.contains("array")) {
                this.topic = "Arrays";
                this.recommendedLesson = "Array Length Invariants and Safe Dynamic Traversal";
            } else if (lower.contains("recursion") || lower.contains("basecase")) {
                this.topic = "Recursion";
                this.recommendedLesson = "Recursion Tree Anatomy: Formulating Guaranteed Base Cases";
            } else if (lower.contains("incomplete") || lower.contains("return")) {
                this.topic = "Functions";
                this.recommendedLesson = "Accumulator Pattern and Value Return Contracts";
            } else {
                this.topic = "General Programming";
                this.recommendedLesson = "Algorithmic Precision, Invariant Tracing, and Socratic Self-Testing";
            }
        }

        public String getMistakeType() { return mistakeType; }
        public int getCurrentWeekCount() { return currentWeekCount; }
        public int getPreviousWeekCount() { return previousWeekCount; }
        public int getOldestWeekCount() { return oldestWeekCount; }
        public int getTotalCount() { return totalCount; }
        public TrendDirection getDirection() { return direction; }
        public String getRecommendedLesson() { return recommendedLesson; }
        public String getTopic() { return topic; }
    }

    private final MistakeDAO mistakeDAO = new MistakeDAO();

    /**
     * Compute trends for a student across all logged mistake types.
     */
    public List<MistakeTrend> computeTrendsForUser(long userId) throws SQLException {
        List<Object[]> weeklyRows = mistakeDAO.weeklyCountsByUser(userId);
        Map<String, Integer> allTimeCounts = mistakeDAO.countByTypeForUser(userId);

        List<MistakeTrend> trends = new ArrayList<>();

        for (Object[] row : weeklyRows) {
            String type = (String) row[0];
            int week1 = ((Number) row[1]).intValue();
            int week2 = ((Number) row[2]).intValue();
            int week3 = ((Number) row[3]).intValue();

            MistakeTrend trend = new MistakeTrend(type, week1, week2, week3);
            trends.add(trend);
        }

        // If there are mistake types in allTimeCounts that had 0 in the last 3 weeks
        for (Map.Entry<String, Integer> entry : allTimeCounts.entrySet()) {
            boolean found = trends.stream().anyMatch(t -> t.getMistakeType().equalsIgnoreCase(entry.getKey()));
            if (!found) {
                MistakeTrend t = new MistakeTrend(entry.getKey(), 0, 0, 0);
                t.direction = TrendDirection.IMPROVING; // No mistakes recently
                trends.add(t);
            }
        }

        // Sort: Deteriorating first, then by count descending
        trends.sort((a, b) -> {
            if (a.getDirection() != b.getDirection()) {
                if (a.getDirection() == TrendDirection.DETERIORATING) return -1;
                if (b.getDirection() == TrendDirection.DETERIORATING) return 1;
                if (a.getDirection() == TrendDirection.STABLE) return -1;
                if (b.getDirection() == TrendDirection.STABLE) return 1;
            }
            return Integer.compare(b.getCurrentWeekCount(), a.getCurrentWeekCount());
        });

        return trends;
    }
}
