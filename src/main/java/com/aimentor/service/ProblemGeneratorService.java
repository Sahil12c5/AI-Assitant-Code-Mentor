package com.aimentor.service;

import com.aimentor.dao.MasteryDAO;
import com.aimentor.dao.ProblemDAO;
import com.aimentor.dao.ProgressDAO;
import com.aimentor.model.Problem;
import com.aimentor.model.TopicMastery;
import com.aimentor.service.llm.LlmClient;
import com.aimentor.service.llm.LlmClientFactory;
import com.aimentor.util.PromptBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for Adaptive Difficulty and "Similar but Different" Isomorphic Problem Generation.
 * Analyzes student competency to synthesize custom exercises calibrated to their exact weak areas.
 */
public class ProblemGeneratorService {
    private static final Logger LOGGER = Logger.getLogger(ProblemGeneratorService.class.getName());

    private final ProblemDAO problemDAO = new ProblemDAO();
    private final MasteryDAO masteryDAO = new MasteryDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();
    private final LlmClient llmClient = LlmClientFactory.getInstance();

    /**
     * Compute the student's recommended difficulty for a topic based on DNA mastery score.
     */
    public Problem.Difficulty calculateRecommendedDifficulty(double score) {
        if (score < 35.0) return Problem.Difficulty.BEGINNER;
        if (score < 55.0) return Problem.Difficulty.EASY;
        if (score < 75.0) return Problem.Difficulty.MEDIUM;
        if (score < 90.0) return Problem.Difficulty.HARD;
        return Problem.Difficulty.INTERVIEW;
    }

    /**
     * Synthesize an isomorphic "Similar but Different" problem targeting the same concept
     * as the source problem, but with a different narrative and variable bindings.
     */
    public Problem generateSimilarProblem(long userId, long sourceProblemId) throws Exception {
        Problem source = problemDAO.findById(sourceProblemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found with ID: " + sourceProblemId));

        TopicMastery mastery = masteryDAO.getOrCreate(userId, source.getTopic());

        String systemPrompt = PromptBuilder.buildSystemPrompt();
        String userPrompt = PromptBuilder.buildSimilarProblemPrompt(source, mastery);

        String rawResponse = llmClient.generate(systemPrompt, userPrompt);
        JsonObject parsed = parseProblemJson(rawResponse);

        String title = parsed.has("title") ? parsed.get("title").getAsString() 
                : "Adaptive Variation: " + source.getTitle();
        String topic = parsed.has("topic") ? parsed.get("topic").getAsString() : source.getTopic();
        String difficultyStr = parsed.has("difficulty") ? parsed.get("difficulty").getAsString() : source.getDifficulty().name();
        String language = parsed.has("language") ? parsed.get("language").getAsString() : source.getLanguage();
        String description = parsed.has("description") ? parsed.get("description").getAsString() 
                : "Synthesized challenge matching the core concept of " + source.getTitle();
        String starterCode = parsed.has("starter_code") ? parsed.get("starter_code").getAsString() : source.getStarterCode();

        Problem.Difficulty difficulty;
        try {
            difficulty = Problem.Difficulty.valueOf(difficultyStr.toUpperCase());
        } catch (Exception e) {
            difficulty = source.getDifficulty();
        }

        Problem newProblem = new Problem();
        newProblem.setTitle(title);
        newProblem.setTopic(topic);
        newProblem.setDifficulty(difficulty);
        newProblem.setLanguage(language);
        newProblem.setDescription(description);
        newProblem.setStarterCode(starterCode);
        newProblem.setGeneratedFromProblemId(sourceProblemId);

        long newId = problemDAO.create(newProblem);
        newProblem.setId(newId);

        LOGGER.info("Generated new similar problem ID " + newId + " from source problem " + sourceProblemId);
        return newProblem;
    }

    /**
     * Find or generate the next optimal problem for a student.
     * Prioritizes topics with lower mastery score (needs focus) and unsolved exercises.
     */
    public Problem getNextRecommendedProblem(long userId) throws SQLException {
        List<TopicMastery> masteries = masteryDAO.findByUserId(userId);

        String targetTopic = "Loops";
        Problem.Difficulty targetDifficulty = Problem.Difficulty.EASY;

        if (!masteries.isEmpty()) {
            // Find topic with lowest mastery score
            TopicMastery weakest = masteries.stream()
                    .min(java.util.Comparator.comparingDouble(TopicMastery::getScore))
                    .orElse(masteries.get(0));

            targetTopic = weakest.getTopic();
            targetDifficulty = calculateRecommendedDifficulty(weakest.getScore());
        }

        // Search for an unsolved problem in this topic/difficulty
        List<Problem> candidateProblems = problemDAO.findByTopicAndDifficulty(targetTopic, targetDifficulty);
        for (Problem p : candidateProblems) {
            boolean solved = progressDAO.isSolvedByUser(userId, p.getId());
            if (!solved) {
                return p;
            }
        }

        // If all candidate problems are solved, pick any unsolved problem across all topics
        List<Problem> allProblems = problemDAO.findAll();
        for (Problem p : allProblems) {
            if (!progressDAO.isSolvedByUser(userId, p.getId())) {
                return p;
            }
        }

        // If everything is solved, return the first problem or generate a new one
        return allProblems.isEmpty() ? null : allProblems.get(0);
    }

    private JsonObject parseProblemJson(String raw) {
        if (raw == null || raw.isBlank()) return new JsonObject();
        String cleaned = raw.trim();
        if (cleaned.startsWith("```json")) cleaned = cleaned.substring(7);
        else if (cleaned.startsWith("```")) cleaned = cleaned.substring(3);
        if (cleaned.endsWith("```")) cleaned = cleaned.substring(0, cleaned.length() - 3);
        cleaned = cleaned.trim();

        int first = cleaned.indexOf('{');
        int last = cleaned.lastIndexOf('}');
        if (first != -1 && last > first) {
            cleaned = cleaned.substring(first, last + 1);
        }

        try {
            return JsonParser.parseString(cleaned).getAsJsonObject();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse generated problem JSON: " + raw, e);
            return new JsonObject();
        }
    }
}
