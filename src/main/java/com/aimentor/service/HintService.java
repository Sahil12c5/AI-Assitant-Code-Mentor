package com.aimentor.service;

import com.aimentor.dao.*;
import com.aimentor.model.*;
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
 * Service managing the 4-Level Progressive Hint Ladder.
 * Strictly enforces on the server that:
 * 1. Advancing via /api/hint caps at Level 3.
 * 2. Solution code (Level 4) is NEVER disclosed except through /api/solution.
 */
public class HintService {
    private static final Logger LOGGER = Logger.getLogger(HintService.class.getName());

    private final HintDAO hintDAO = new HintDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final ProblemDAO problemDAO = new ProblemDAO();
    private final UserDAO userDAO = new UserDAO();
    private final MasteryDAO masteryDAO = new MasteryDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();
    private final LlmClient llmClient = LlmClientFactory.getInstance();

    public static class HintResponse {
        private long hintSessionId;
        private int level;
        private String hint;
        private String concept;
        private boolean canAdvance;

        public HintResponse(long hintSessionId, int level, String hint, String concept, boolean canAdvance) {
            this.hintSessionId = hintSessionId;
            this.level = level;
            this.hint = hint;
            this.concept = concept;
            this.canAdvance = canAdvance;
        }

        public long getHintSessionId() { return hintSessionId; }
        public int getLevel() { return level; }
        public String getHint() { return hint; }
        public String getConcept() { return concept; }
        public boolean isCanAdvance() { return canAdvance; }
    }

    public static class SolutionResponse {
        private long hintSessionId;
        private int level;
        private String solution;
        private String solutionJava;
        private String solutionPython;
        private String explanation;

        public SolutionResponse(long hintSessionId, int level, String solution, String solutionJava, String solutionPython, String explanation) {
            this.hintSessionId = hintSessionId;
            this.level = level;
            this.solution = solution;
            this.solutionJava = solutionJava;
            this.solutionPython = solutionPython;
            this.explanation = explanation;
        }

        public SolutionResponse(long hintSessionId, int level, String solution, String explanation) {
            this(hintSessionId, level, solution, null, null, explanation);
        }

        public long getHintSessionId() { return hintSessionId; }
        public int getLevel() { return level; }
        public String getSolution() { return solution; }
        public String getSolutionJava() { return solutionJava; }
        public String getSolutionPython() { return solutionPython; }
        public String getExplanation() { return explanation; }
    }

    /**
     * Advance to the next progressive hint level (Level 2 or Level 3).
     * Rejects request if already at Level 3.
     */
    public HintResponse getNextHint(long userId, long hintSessionId) throws Exception {
        HintSession session = hintDAO.findSessionById(hintSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Hint session not found with ID: " + hintSessionId));

        if (session.getUserId() != userId) {
            throw new SecurityException("Unauthorized access to hint session");
        }

        if (!session.canAdvance()) {
            throw new IllegalStateException("Maximum hint level (" + HintSession.MAX_HINT_LEVEL + 
                    ") reached. Use 'Show Solution' if you are completely blocked.");
        }

        // Advance level
        session.advanceLevel();
        int newLevel = session.getCurrentLevel();

        // Check if hint for this level is already cached in DB
        List<Hint> existingHints = hintDAO.findHintsBySessionId(hintSessionId);
        for (Hint h : existingHints) {
            if (h.getLevel() == newLevel) {
                hintDAO.updateSession(session);
                return new HintResponse(hintSessionId, newLevel, h.getContent(), "Progressive Hint Ladder", session.canAdvance());
            }
        }

        // Generate hint via LLM with strict Level constraint
        Submission submission = submissionDAO.findById(session.getSubmissionId())
                .orElseThrow(() -> new IllegalStateException("Submission not found: " + session.getSubmissionId()));

        User user = userDAO.findById(userId).orElse(null);
        Problem problem = null;
        String topic = "General Programming";
        if (submission.getProblemId() != null) {
            problem = problemDAO.findById(submission.getProblemId()).orElse(null);
            if (problem != null) topic = problem.getTopic();
        }

        TopicMastery mastery = masteryDAO.getOrCreate(userId, topic);

        String systemPrompt = PromptBuilder.buildSystemPrompt();
        String userPrompt = PromptBuilder.buildAnalysisPrompt(
                submission.getCode(), submission.getLanguage(), problem, user, mastery, newLevel);

        String rawResponse = llmClient.generate(systemPrompt, userPrompt);
        JsonObject parsed = parseLlmJson(rawResponse);

        String hintText = parsed.has("hint") ? parsed.get("hint").getAsString() 
                : "Examine the logic flow and state transitions at the highlighted lines.";
        String concept = parsed.has("concept") ? parsed.get("concept").getAsString() : "Algorithmic Concept";

        // Persist hint
        Hint hintRecord = new Hint(hintSessionId, newLevel, hintText);
        hintDAO.createHint(hintRecord);

        // Update session
        hintDAO.updateSession(session);

        // Record hint usage in progress if linked to problem
        if (submission.getProblemId() != null) {
            UserProblemProgress progress = progressDAO.getOrCreate(userId, submission.getProblemId());
            progress.setHintsUsed(progress.getHintsUsed() + 1);
            progressDAO.update(progress);
        }

        return new HintResponse(hintSessionId, newLevel, hintText, concept, session.canAdvance());
    }

    /**
     * Reveal the solution (Level 4). This is the ONLY method allowed to generate and return working solution code.
     */
    public SolutionResponse revealSolution(long userId, long hintSessionId) throws Exception {
        HintSession session = hintDAO.findSessionById(hintSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Hint session not found with ID: " + hintSessionId));

        if (session.getUserId() != userId) {
            throw new SecurityException("Unauthorized access to hint session");
        }

        session.revealSolution();
        hintDAO.updateSession(session);

        // Check if level 4 hint already generated (and is valid and contains both Java & Python)
        List<Hint> existingHints = hintDAO.findHintsBySessionId(hintSessionId);
        for (Hint h : existingHints) {
            if (h.getLevel() == HintSession.SOLUTION_LEVEL) {
                // If the cached solution is an old single-language fallback or generic dummy, delete it and regenerate
                if (h.getContent() != null && (
                        !h.getContent().contains("PYTHON") ||
                        !h.getContent().contains("JAVA") ||
                        h.getContent().contains("Solution unavailable") ||
                        h.getContent().contains("Implement your solution here") ||
                        h.getContent().contains("Write your solution logic here") ||
                        h.getContent().contains("Solution computed successfully"))) {
                    hintDAO.deleteHint(h.getId());
                    break; // Fall through to regenerate
                }
                String[] split = splitDualSolution(h.getContent());
                return new SolutionResponse(hintSessionId, 4, h.getContent(), split[0], split[1], "Full Corrected Solution in Java and Python");
            }
        }

        Submission submission = submissionDAO.findById(session.getSubmissionId())
                .orElseThrow(() -> new IllegalStateException("Submission not found: " + session.getSubmissionId()));

        User user = userDAO.findById(userId).orElse(null);
        Problem problem = null;
        String topic = "General Programming";
        if (submission.getProblemId() != null) {
            problem = problemDAO.findById(submission.getProblemId()).orElse(null);
            if (problem != null) topic = problem.getTopic();
        }

        TopicMastery mastery = masteryDAO.getOrCreate(userId, topic);

        String systemPrompt = PromptBuilder.buildSystemPrompt();
        String userPrompt = PromptBuilder.buildAnalysisPrompt(
                submission.getCode(), submission.getLanguage(), problem, user, mastery, HintSession.SOLUTION_LEVEL);

        String rawResponse = llmClient.generate(systemPrompt, userPrompt);
        JsonObject parsed = parseLlmJson(rawResponse);

        // Catalog lookup for verified dual solutions
        com.aimentor.service.llm.ProblemSolutionCatalog.ProblemGuide guide = null;
        if (problem != null) {
            guide = com.aimentor.service.llm.ProblemSolutionCatalog.findGuide(problem.getTitle());
        }
        if (guide == null) {
            guide = com.aimentor.service.llm.ProblemSolutionCatalog.findGuide(userPrompt);
        }

        String solutionJava = (guide != null) ? guide.solutionJava : null;
        String solutionPython = (guide != null) ? guide.solutionPython : null;

        if (parsed.has("solutionJava") && !parsed.get("solutionJava").getAsString().isBlank()) {
            solutionJava = parsed.get("solutionJava").getAsString();
        }
        if (parsed.has("solutionPython") && !parsed.get("solutionPython").getAsString().isBlank()) {
            solutionPython = parsed.get("solutionPython").getAsString();
        }

        if (solutionJava == null && guide != null) {
            solutionJava = guide.solutionJava;
        }
        if (solutionPython == null && guide != null) {
            solutionPython = guide.solutionPython;
        }
        if (solutionPython == null && solutionJava != null) {
            solutionPython = com.aimentor.service.llm.PythonSolutionCatalog.getPythonSolution(problem != null ? problem.getTitle() : userPrompt, solutionJava);
        }

        String solutionCode = com.aimentor.service.llm.ProblemSolutionCatalog.formatDualSolution(solutionJava, solutionPython);
        String explanation = parsed.has("hint") ? parsed.get("hint").getAsString() : "Here is the optimal reference solution in both Java and Python: study the logic and try to rewrite it from scratch.";

        // Persist level 4 with dual code
        Hint hintRecord = new Hint(hintSessionId, HintSession.SOLUTION_LEVEL, solutionCode);
        hintDAO.createHint(hintRecord);

        // Deduct mastery slightly for revealing solution
        if (mastery != null) {
            mastery.setScore(Math.max(10.0, mastery.getScore() - 8.0));
            masteryDAO.update(mastery);
        }

        return new SolutionResponse(hintSessionId, 4, solutionCode, solutionJava, solutionPython, explanation);
    }

    private static String[] splitDualSolution(String dual) {
        if (dual == null) return new String[]{"", ""};
        int pyIdx = dual.indexOf("# ==========================================");
        if (pyIdx != -1) {
            String jPart = dual.substring(0, pyIdx).replace("// ==========================================", "")
                    .replace("// JAVA (OpenJDK 17)", "").trim();
            String pPart = dual.substring(pyIdx).replace("# ==========================================", "")
                    .replace("# PYTHON 3", "").trim();
            return new String[]{jPart, pPart};
        }
        return new String[]{dual, dual};
    }

    private JsonObject parseLlmJson(String raw) {
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
            LOGGER.log(Level.WARNING, "Failed to parse JSON in HintService: " + raw, e);
            JsonObject fallback = new JsonObject();
            fallback.addProperty("hint", "Consider breaking the problem down into smaller testable sub-cases.");
            return fallback;
        }
    }
}
