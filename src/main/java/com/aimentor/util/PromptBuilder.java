package com.aimentor.util;

import com.aimentor.model.Problem;
import com.aimentor.model.TopicMastery;
import com.aimentor.model.User;

import java.util.Map;

/**
 * Constructs pedagogically-grounded prompts for the LLM.
 * Injects:
 * 1. Student Coding DNA (overall level, topic mastery score, assistance level)
 * 2. Mistake Memory (past mistake frequencies for this topic)
 * 3. Strict Server-Enforced Progressive Hint Level constraints (Level 1, 2, 3, or 4)
 * 4. Strict JSON schema rules to prevent conversational fluff or solution leakage.
 */
public class PromptBuilder {

    private PromptBuilder() {
        // Utility
    }

    /**
     * Build the system prompt that defines the AI Mentor's pedagogical role and constraints.
     */
    public static String buildSystemPrompt() {
        return """
                You are the "Adaptive AI Coding Mentor", an intelligent, pedagogically calibrated computer science tutor.
                YOUR PRIMARY DIRECTIVE: YOU DELIBERATELY DO NOT HAND OVER THE SOLUTION BEFORE THE AUTHORIZED HINT LEVEL.
                
                Your job is to cultivate computational thinking, algorithmic intuition, and debugging skills through
                the Socratic method, scaffolding, and targeted conceptual hints.
                
                CORE RULES:
                1. STRICT HINT-LEVEL CONSTRAINTS:
                   - Level 1: Only ask a guiding question pointing at the root cause or suspect code region. DO NOT write code. DO NOT explain the full bug.
                   - Level 2: Explain the underlying programming concept or invariant (e.g. 0-indexing, loop guards, accumulator pattern). DO NOT provide fixed code.
                   - Level 3: Fully explain WHY the student's code fails and describe the logical change needed in plain English. Still DO NOT provide full copy-paste solutions.
                   - Level 4: ONLY allowed when the prompt explicitly specifies LEVEL 4. Provide the full corrected code with comments.
                
                2. ADAPTIVE PERSONALIZATION:
                   - Calibrate tone and terminology according to the Student Coding DNA (Beginner, Intermediate, Advanced).
                   - If the student has repeated mistake history on this topic, acknowledge their pattern constructively.
                
                3. STRICT JSON OUTPUT:
                   - You must respond ONLY with a single valid JSON object.
                   - Never include preamble, apologies, conversational greeting, or markdown code fences outside JSON.
                """;
    }

    /**
     * Build the user prompt containing student code, problem context, DNA profile, and target hint level.
     */
    public static String buildAnalysisPrompt(
            String code,
            String language,
            Problem problem,
            User student,
            TopicMastery mastery,
            int hintLevel) {

        StringBuilder sb = new StringBuilder();

        sb.append("=== PROBLEM CONTEXT ===\n");
        if (problem != null) {
            sb.append("Title: ").append(problem.getTitle()).append("\n");
            sb.append("Topic: ").append(problem.getTopic()).append("\n");
            sb.append("Difficulty: ").append(problem.getDifficulty()).append("\n");
            sb.append("Description: ").append(problem.getDescription()).append("\n");
        } else {
            sb.append("Topic: ").append(mastery != null ? mastery.getTopic() : "General Programming").append("\n");
            sb.append("Description: Ad-hoc code submission.\n");
        }

        sb.append("\n=== STUDENT CODING DNA PROFILE ===\n");
        sb.append("Student Name: ").append(student != null ? student.getName() : "Learner").append("\n");
        sb.append("Skill Level: ").append(student != null ? student.getLevel().name() : "BEGINNER").append("\n");

        if (mastery != null) {
            sb.append("Topic Mastery Score: ").append(String.format("%.1f", mastery.getScore())).append("/100\n");
            sb.append("Assistance Level: ").append(mastery.getSuggestedAssistanceLevel()).append("\n");
            sb.append("Historical Attempts: ").append(mastery.getAttempts())
              .append(", Successes: ").append(mastery.getSuccesses()).append("\n");

            Map<String, Integer> commonMistakes = mastery.getCommonMistakes();
            if (commonMistakes != null && !commonMistakes.isEmpty()) {
                sb.append("Frequent Past Mistakes on this topic: ").append(commonMistakes).append("\n");
            }
        }

        sb.append("\n=== SERVER-ENFORCED HINT LEVEL ===\n");
        sb.append("AUTHORIZATION: LEVEL ").append(hintLevel).append(" of 4\n");
        switch (hintLevel) {
            case 1 -> sb.append("CONSTRAINT: Level 1 Guiding Question only. Point to the problem area without fixing it.\n");
            case 2 -> sb.append("CONSTRAINT: Level 2 Conceptual Explanation. Clarify the computing principle involved.\n");
            case 3 -> sb.append("CONSTRAINT: Level 3 Deep Diagnosis. Explain why the logic is flawed and what step to take.\n");
            case 4 -> sb.append("CONSTRAINT: Level 4 Explicit Solution Request. Provide the full corrected code and explanation in BOTH Java and Python.\n");
            default -> sb.append("CONSTRAINT: Level 1 Guiding Question only.\n");
        }

        sb.append("\n=== SUBMITTED STUDENT CODE (").append(language.toUpperCase()).append(") ===\n");
        sb.append(code).append("\n");

        sb.append("\n=== OUTPUT JSON SCHEMA REQUIRED ===\n");
        sb.append("""
                {
                  "isCorrect": boolean,
                  "concept": "Core concept related to this problem/mistake",
                  "hint": "The hint text strictly conforming to the AUTHORIZED HINT LEVEL",
                  "mistakes": [
                    {
                      "type": "MistakeCategory (e.g. OffByOne, InfiniteLoop, NullPointer, SyntaxError, MissingBaseCase)",
                      "topic": "TopicName",
                      "line": integer_or_null,
                      "description": "Specific concise description of the flaw"
                    }
                  ]
                """);
        if (hintLevel == 4) {
            sb.append("  \"solution\": \"Full corrected source code formatted with both Java and Python\",\n");
            sb.append("  \"solutionJava\": \"Complete corrected Java solution class\",\n");
            sb.append("  \"solutionPython\": \"Complete corrected Python solution class\"\n");
        }
        sb.append("}\n");

        return sb.toString();
    }

    /**
     * Build prompt for generating an isomorphic "Similar but Different" problem.
     */
    public static String buildSimilarProblemPrompt(Problem originalProblem, TopicMastery weakTopic) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== GENERATE ISOMORPHIC 'SIMILAR BUT DIFFERENT' PROBLEM ===\n");
        if (originalProblem != null) {
            sb.append("Source Problem Title: ").append(originalProblem.getTitle()).append("\n");
            sb.append("Topic: ").append(originalProblem.getTopic()).append("\n");
            sb.append("Difficulty: ").append(originalProblem.getDifficulty()).append("\n");
            sb.append("Description: ").append(originalProblem.getDescription()).append("\n");
        } else if (weakTopic != null) {
            sb.append("Topic To Target: ").append(weakTopic.getTopic()).append("\n");
            sb.append("Student Current Score: ").append(weakTopic.getScore()).append("/100\n");
        }

        sb.append("""
                Create a completely new, unique programming challenge that tests the exact same underlying
                algorithmic concept/pattern as the source problem, but with a different narrative, variable names,
                and edge cases.
                
                Respond ONLY with JSON matching this schema:
                {
                  "title": "Novel Problem Title",
                  "topic": "Topic Name",
                  "difficulty": "EASY|MEDIUM|HARD",
                  "language": "java",
                  "description": "Clear problem statement with inputs, outputs, and examples",
                  "starter_code": "public class Solution { ... }"
                }
                """);

        return sb.toString();
    }
}
