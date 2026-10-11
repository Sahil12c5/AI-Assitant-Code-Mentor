package com.aimentor.service;

import com.aimentor.dao.*;
import com.aimentor.model.*;
import com.aimentor.service.llm.LlmClient;
import com.aimentor.service.llm.LlmClientFactory;
import com.aimentor.util.PromptBuilder;
import com.google.gson.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service orchestrating the intelligent code analysis workflow:
 * 1. Submissions storage
 * 2. Dynamic DNA & Mistake profile assembly
 * 3. LLM pedagogical prompt synthesis (Level 1 enforcement)
 * 4. Fault-tolerant JSON parsing and repair
 * 5. Persistent mistake logging
 * 6. Mathematical topic mastery score updating
 * 7. Progressive hint session initialization
 */
public class AnalysisService {
    private static final Logger LOGGER = Logger.getLogger(AnalysisService.class.getName());
    private static final Gson GSON = new Gson();

    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final ProblemDAO problemDAO = new ProblemDAO();
    private final UserDAO userDAO = new UserDAO();
    private final MasteryDAO masteryDAO = new MasteryDAO();
    private final MistakeDAO mistakeDAO = new MistakeDAO();
    private final HintDAO hintDAO = new HintDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();
    private final LlmClient llmClient = LlmClientFactory.getInstance();

    public static class AnalysisResult {
        private long submissionId;
        private long hintSessionId;
        private int currentLevel;
        private boolean isCorrect;
        private String concept;
        private String hint;
        private List<Mistake> mistakes = new ArrayList<>();
        private double updatedMasteryScore;
        private String topic;

        public long getSubmissionId() { return submissionId; }
        public void setSubmissionId(long submissionId) { this.submissionId = submissionId; }
        public long getHintSessionId() { return hintSessionId; }
        public void setHintSessionId(long hintSessionId) { this.hintSessionId = hintSessionId; }
        public int getCurrentLevel() { return currentLevel; }
        public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }
        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { isCorrect = correct; }
        public String getConcept() { return concept; }
        public void setConcept(String concept) { this.concept = concept; }
        public String getHint() { return hint; }
        public void setHint(String hint) { this.hint = hint; }
        public List<Mistake> getMistakes() { return mistakes; }
        public void setMistakes(List<Mistake> mistakes) { this.mistakes = mistakes; }
        public double getUpdatedMasteryScore() { return updatedMasteryScore; }
        public void setUpdatedMasteryScore(double updatedMasteryScore) { this.updatedMasteryScore = updatedMasteryScore; }
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
    }

    /**
     * Analyze student code for a given problem and user.
     */
    public AnalysisResult analyzeCode(long userId, Long problemId, String code, String language)
            throws Exception {

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Problem problem = null;
        String topic = "General Programming";
        if (problemId != null) {
            problem = problemDAO.findById(problemId).orElse(null);
            if (problem != null) {
                topic = problem.getTopic();
            }
        }

        // 1. Record the submission in DB
        Submission submission = new Submission();
        submission.setUserId(userId);
        submission.setProblemId(problemId);
        submission.setLanguage(language);
        submission.setCode(code);
        submission.setStatus(Submission.Status.PENDING);
        long submissionId = submissionDAO.create(submission);

        // 2. Fetch Student DNA profile for this topic
        TopicMastery mastery = masteryDAO.getOrCreate(userId, topic);

        // 3. Assemble prompt with strict Level 1 hint constraint
        String systemPrompt = PromptBuilder.buildSystemPrompt();
        String userPrompt = PromptBuilder.buildAnalysisPrompt(code, language, problem, user, mastery, 1);

        // 4. Call LLM (Gemini or offline Mock)
        String rawLlmResponse = llmClient.generate(systemPrompt, userPrompt);
        LOGGER.fine("Raw LLM response: " + rawLlmResponse);

        // 5. Parse and repair JSON
        JsonObject parsed = parseAndRepairJson(rawLlmResponse);

        boolean isCorrect = parsed.has("isCorrect") && parsed.get("isCorrect").getAsBoolean();
        String concept = parsed.has("concept") ? parsed.get("concept").getAsString() : "Computer Science Fundamentals";
        String level1Hint = parsed.has("hint") ? parsed.get("hint").getAsString() : "Review your code carefully for edge cases.";

        // Update submission status in DB
        submissionDAO.updateResult(submissionId, 
                isCorrect ? Submission.Status.CORRECT : Submission.Status.INCORRECT, 
                "", rawLlmResponse);

        // 6. Extract and persist mistakes
        List<Mistake> mistakesList = new ArrayList<>();
        if (parsed.has("mistakes") && parsed.get("mistakes").isJsonArray()) {
            JsonArray arr = parsed.getAsJsonArray("mistakes");
            for (JsonElement el : arr) {
                if (el.isJsonObject()) {
                    JsonObject mObj = el.getAsJsonObject();
                    String mType = mObj.has("type") ? mObj.get("type").getAsString() : "LogicError";
                    String mTopic = mObj.has("topic") ? mObj.get("topic").getAsString() : topic;
                    Integer mLine = (mObj.has("line") && !mObj.get("line").isJsonNull())
                            ? mObj.get("line").getAsInt() : null;
                    String mDesc = mObj.has("description") ? mObj.get("description").getAsString() : "Unexpected logic pattern.";

                    Mistake mistake = new Mistake(userId, submissionId, mType, mTopic, mLine, mDesc);
                    mistakesList.add(mistake);
                }
            }
        }

        if (!mistakesList.isEmpty()) {
            mistakeDAO.createBatch(mistakesList);
        }

        // 7. Update Topic Mastery (DNA Score)
        if (isCorrect) {
            mastery.recordSuccess();
        } else {
            mastery.recordMistake();

            // Track mistake frequency in commonMistakes map
            Map<String, Integer> cm = mastery.getCommonMistakes();
            if (cm == null) cm = new java.util.HashMap<>();
            for (Mistake m : mistakesList) {
                cm.put(m.getMistakeType(), cm.getOrDefault(m.getMistakeType(), 0) + 1);
            }
            mastery.setCommonMistakes(cm);
        }
        masteryDAO.update(mastery);

        // 8. Update Problem Progress if linked to a problem
        if (problemId != null) {
            UserProblemProgress progress = progressDAO.getOrCreate(userId, problemId);
            progress.setAttempts(progress.getAttempts() + 1);
            if (isCorrect) {
                progress.setSolved(true);
                progress.setCompletedAt(new Timestamp(System.currentTimeMillis()));
            }
            progressDAO.update(progress);
        }

        // 9. Initialize HintSession at Level 1
        HintSession hintSession = new HintSession(userId, submissionId);
        hintSession.setCurrentLevel(1);
        hintSession.setSolutionRevealed(false);
        long hintSessionId = hintDAO.createSession(hintSession);

        // Save the Level 1 hint
        Hint hintRecord = new Hint(hintSessionId, 1, level1Hint);
        hintDAO.createHint(hintRecord);

        // 10. Build and return result
        AnalysisResult result = new AnalysisResult();
        result.setSubmissionId(submissionId);
        result.setHintSessionId(hintSessionId);
        result.setCurrentLevel(1);
        result.setCorrect(isCorrect);
        result.setConcept(concept);
        result.setHint(level1Hint);
        result.setMistakes(mistakesList);
        result.setUpdatedMasteryScore(mastery.getScore());
        result.setTopic(topic);

        return result;
    }

    /**
     * Robust parser that removes markdown wrapping and repairs common LLM JSON syntax issues.
     */
    private JsonObject parseAndRepairJson(String raw) {
        if (raw == null || raw.isBlank()) {
            return fallbackJsonObject("Empty response received from AI model.");
        }

        String cleaned = raw.trim();

        // Strip markdown backticks if present
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        cleaned = cleaned.trim();

        // Locate first '{' and last '}'
        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace > firstBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1);
        }

        try {
            return JsonParser.parseString(cleaned).getAsJsonObject();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse JSON directly: " + cleaned, e);
            return fallbackJsonObject("AI feedback could not be parsed into structured format. Please review code logic.");
        }
    }

    private JsonObject fallbackJsonObject(String fallbackHint) {
        JsonObject obj = new JsonObject();
        obj.addProperty("isCorrect", false);
        obj.addProperty("concept", "General Logic & Syntax");
        obj.addProperty("hint", fallbackHint);
        obj.add("mistakes", new JsonArray());
        return obj;
    }
}
