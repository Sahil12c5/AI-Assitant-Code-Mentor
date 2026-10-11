package com.aimentor.service.llm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.util.logging.Logger;

/**
 * Intelligent Mock LLM client for offline development, local demos, and automated testing.
 * Accurately simulates progressive hints (Levels 1-4), mistake diagnosis, and problem generation
 * without requiring an external internet connection or Gemini API key.
 */
public class MockLlmClient implements LlmClient {
    private static final Logger LOGGER = Logger.getLogger(MockLlmClient.class.getName());
    private static final Gson GSON = new Gson();

    @Override
    public String generate(String systemPrompt, String userPrompt) throws LlmException {
        LOGGER.info("MockLlmClient generating contextual response for prompt...");

        // Check if this is a Problem Generation prompt
        if (userPrompt != null && (userPrompt.contains("Generate a new coding problem") || userPrompt.contains("generate_problem"))) {
            return generateMockProblem(userPrompt);
        }

        // Otherwise, it is a code analysis / progressive hint prompt
        return generateMockAnalysis(userPrompt);
    }

    private String generateMockAnalysis(String userPrompt) {
        String lowerPrompt = (userPrompt != null) ? userPrompt.toLowerCase() : "";

        // Detect hint level requested (strictly based on prompt header, not Java class names)
        int level = 1;
        if (lowerPrompt.contains("authorization: level 4") || lowerPrompt.contains("level 4 of 4") || lowerPrompt.contains("level 4 explicit solution") || lowerPrompt.contains("hint level: 4") || lowerPrompt.contains("level 4")) {
            level = 4;
        } else if (lowerPrompt.contains("authorization: level 3") || lowerPrompt.contains("level 3 of 4") || lowerPrompt.contains("hint level: 3") || lowerPrompt.contains("level 3")) {
            level = 3;
        } else if (lowerPrompt.contains("authorization: level 2") || lowerPrompt.contains("level 2 of 4") || lowerPrompt.contains("hint level: 2") || lowerPrompt.contains("level 2")) {
            level = 2;
        } else if (lowerPrompt.contains("authorization: level 1") || lowerPrompt.contains("level 1 of 4") || lowerPrompt.contains("hint level: 1") || lowerPrompt.contains("level 1")) {
            level = 1;
        }

        // Check if user is asking about Two Sum or specific interview problems
        boolean isTwoSum = lowerPrompt.contains("two sum") || lowerPrompt.contains("twosum");
        boolean isBuyStock = lowerPrompt.contains("buy and sell stock") || lowerPrompt.contains("maxprofit");
        boolean isPalindrome = lowerPrompt.contains("valid palindrome") || lowerPrompt.contains("ispalindrome");
        boolean isParentheses = lowerPrompt.contains("valid parentheses") || lowerPrompt.contains("isvalid(");
        boolean isReverseList = lowerPrompt.contains("reverse linked list") || lowerPrompt.contains("reverselist");
        boolean isEmptyCode = lowerPrompt.contains("code: \"\"") || lowerPrompt.contains("code: ''") || lowerPrompt.contains("write your code here") || lowerPrompt.contains("write your solution here");
        boolean hasLoopWithoutIncrement = lowerPrompt.contains("while") && !lowerPrompt.contains("++") && !lowerPrompt.contains("+=");
        boolean hasArrayIndexOOB = lowerPrompt.contains("<= arr.length") || lowerPrompt.contains("<= length");
        boolean hasReturnZeroStub = lowerPrompt.contains("return 0;") && (lowerPrompt.contains("// write your code") || lowerPrompt.contains("// todo"));
        boolean hasOffByOne = lowerPrompt.contains("for") && lowerPrompt.contains("i <= n") && lowerPrompt.contains("arr[i]");

        JsonObject root = new JsonObject();
        JsonArray mistakes = new JsonArray();

        // 1. Check if problem matches verified solutions catalog
        ProblemSolutionCatalog.ProblemGuide guide = ProblemSolutionCatalog.findGuide(userPrompt);
        if (guide != null) {
            root.addProperty("concept", guide.concept);

            // Check if code looks like a starter stub or incomplete implementation
            boolean isStub = lowerPrompt.contains("return 0;")
                    || lowerPrompt.contains("return null;")
                    || lowerPrompt.contains("return false;")
                    || lowerPrompt.contains("return -1;")
                    || lowerPrompt.contains("return new int[]{};")
                    || lowerPrompt.contains("write your code here")
                    || lowerPrompt.contains("write your solution here")
                    || lowerPrompt.contains("implement your solution here")
                    || lowerPrompt.contains("code: \"\"")
                    || lowerPrompt.contains("code: ''");

            if (level == 4) {
                root.addProperty("isCorrect", false);
                root.addProperty("hint", "Here is the optimal reference solution in both Java and Python: study the logic and try to rewrite it from scratch.");
                root.addProperty("solution", guide.solution);
                root.addProperty("solutionJava", guide.solutionJava);
                root.addProperty("solutionPython", guide.solutionPython);
            } else if (isStub) {
                root.addProperty("isCorrect", false);
                JsonObject m = new JsonObject();
                m.addProperty("type", "IncompleteImplementation");
                m.addProperty("topic", "Algorithm Design");
                m.addProperty("line", 3);
                m.addProperty("description", "Method contains starter placeholder code. Review the problem invariants and implement core logic.");
                mistakes.add(m);

                switch (level) {
                    case 1 -> root.addProperty("hint", guide.hintLevel1);
                    case 2 -> root.addProperty("hint", guide.hintLevel2);
                    case 3 -> root.addProperty("hint", guide.hintLevel3);
                }
            } else {
                // Code written by student - check for common bugs or guide progressively
                if (hasArrayIndexOOB) {
                    root.addProperty("isCorrect", false);
                    JsonObject m = new JsonObject();
                    m.addProperty("type", "ArrayIndexOutOfBounds");
                    m.addProperty("topic", "Arrays");
                    m.addProperty("line", 4);
                    m.addProperty("description", "Loop condition accesses out-of-bounds index (<= length instead of < length).");
                    mistakes.add(m);
                    root.addProperty("hint", "Check your loop termination bound: arrays are 0-indexed up to length - 1.");
                } else if (hasLoopWithoutIncrement) {
                    root.addProperty("isCorrect", false);
                    JsonObject m = new JsonObject();
                    m.addProperty("type", "InfiniteLoop");
                    m.addProperty("topic", "Loops");
                    m.addProperty("line", 5);
                    m.addProperty("description", "Loop condition variable is not updated within loop body.");
                    mistakes.add(m);
                    root.addProperty("hint", "Ensure your loop control variable is incremented/updated on each pass.");
                } else {
                    root.addProperty("isCorrect", false);
                    switch (level) {
                        case 1 -> root.addProperty("hint", guide.hintLevel1);
                        case 2 -> root.addProperty("hint", guide.hintLevel2);
                        case 3 -> root.addProperty("hint", guide.hintLevel3);
                    }
                }
            }
        } else if (isTwoSum) {
            root.addProperty("isCorrect", lowerPrompt.contains("map.containskey") && lowerPrompt.contains("target - nums[i]"));
            root.addProperty("concept", "Hash Table & Complement Lookup (Time: O(N), Space: O(N))");

            if (!root.get("isCorrect").getAsBoolean()) {
                JsonObject m = new JsonObject();
                m.addProperty("type", "OptimalComplexityTradeoff");
                m.addProperty("topic", "Arrays & Hashing");
                m.addProperty("line", 3);
                m.addProperty("description", "A nested O(N^2) brute-force loop will exceed time limits. Use a single-pass HashMap to store visited numbers and look up the complement in O(1).");
                mistakes.add(m);

                switch (level) {
                    case 1 -> root.addProperty("hint", "To solve Two Sum in optimal linear time, consider what value you need at each step: as you inspect nums[i], what complement would add up to target?");
                    case 2 -> root.addProperty("hint", "Instead of scanning the whole array with a second loop, you can store numbers you've already seen in a HashMap<Integer, Integer> (mapping value to index) for O(1) lookup.");
                    case 3 -> root.addProperty("hint", "Single-pass algorithm: for each index i, compute `int complement = target - nums[i];`. If `map.containsKey(complement)`, return `new int[]{map.get(complement), i}`. Otherwise, store `map.put(nums[i], i);`.");
                    case 4 -> {
                        root.addProperty("hint", "Here is the optimal O(N) HashMap solution for Two Sum:");
                        root.addProperty("solution", """
                                import java.util.HashMap;
                                import java.util.Map;

                                public class Solution {
                                    public static int[] twoSum(int[] nums, int target) {
                                        Map<Integer, Integer> map = new HashMap<>();
                                        for (int i = 0; i < nums.length; i++) {
                                            int complement = target - nums[i];
                                            if (map.containsKey(complement)) {
                                                return new int[] { map.get(complement), i };
                                            }
                                            map.put(nums[i], i);
                                        }
                                        return new int[] {};
                                    }

                                    public static void main(String[] args) {
                                        int[] nums = {2, 7, 11, 15};
                                        int target = 9;
                                        int[] result = twoSum(nums, target);
                                        System.out.println("Output indices: [" + result[0] + ", " + result[1] + "]");
                                    }
                                }
                                """);
                    }
                }
            } else {
                root.addProperty("hint", "Excellent work! Your Two Sum solution uses the optimal single-pass HashMap approach with O(N) runtime and O(N) auxiliary space.");
            }
        } else if (hasArrayIndexOOB) {
            root.addProperty("isCorrect", false);
            root.addProperty("concept", "Zero-Indexed Array Boundaries");

            JsonObject m1 = new JsonObject();
            m1.addProperty("type", "ArrayIndexOutOfBounds");
            m1.addProperty("topic", "Arrays");
            m1.addProperty("line", 4);
            m1.addProperty("description", "Loop condition uses '<=' with array length, which accesses an index equal to the array length, throwing ArrayIndexOutOfBoundsException.");
            mistakes.add(m1);

            switch (level) {
                case 1 -> root.addProperty("hint", "Look closely at your loop termination condition. Does the last iteration attempt to access a valid array index?");
                case 2 -> root.addProperty("hint", "In Java and Python, arrays are 0-indexed. An array with N elements has valid indices from 0 up to N - 1 only.");
                case 3 -> root.addProperty("hint", "Change `i <= arr.length` to `i < arr.length` to avoid accessing the non-existent index `arr[arr.length]`.");
                case 4 -> {
                    root.addProperty("hint", "Here is the corrected loop avoiding the index out of bounds exception:");
                    root.addProperty("solution", """
                            for (int i = 0; i < arr.length; i++) {
                                // Safe array access
                                System.out.println(arr[i]);
                            }
                            """);
                }
            }
        } else if (hasLoopWithoutIncrement) {
            root.addProperty("isCorrect", false);
            root.addProperty("concept", "Loop Termination and State Mutation");

            JsonObject m1 = new JsonObject();
            m1.addProperty("type", "InfiniteLoop");
            m1.addProperty("topic", "Loops");
            m1.addProperty("line", 5);
            m1.addProperty("description", "The loop condition variable is never updated inside the loop body, causing an infinite loop.");
            mistakes.add(m1);

            switch (level) {
                case 1 -> root.addProperty("hint", "What happens to your loop control variable during each iteration of the loop?");
                case 2 -> root.addProperty("hint", "A while loop will run forever unless at least one variable in the boolean guard condition changes value inside the loop.");
                case 3 -> root.addProperty("hint", "Add an increment statement like `i++` or update the state variable before the end of the while loop block.");
                case 4 -> {
                    root.addProperty("hint", "Here is the corrected loop with state progression:");
                    root.addProperty("solution", """
                            int i = 0;
                            while (i < n) {
                                // Processing
                                i++; // Advances state towards loop exit
                            }
                            """);
                }
            }
        } else if (lowerPrompt.contains("syntax") || lowerPrompt.contains("error")) {
            root.addProperty("isCorrect", false);
            root.addProperty("concept", "Syntax & Type Compatibility");

            JsonObject m1 = new JsonObject();
            m1.addProperty("type", "SyntaxError");
            m1.addProperty("topic", "General");
            m1.addProperty("line", 2);
            m1.addProperty("description", "Syntax error or unexpected token encountered in code structure.");
            mistakes.add(m1);

            root.addProperty("hint", level == 1 
                ? "Check your matching braces, parentheses, and semicolons near the top of the method."
                : "Ensure every opened block '{' has a closing '}' and statements end with semicolons.");
        } else {
            // Default case: generic code submission
            root.addProperty("concept", "Algorithmic Logic and Control Flow");

            if (level == 4) {
                root.addProperty("isCorrect", false);
                root.addProperty("hint", "Here is the optimal reference solution in both Java and Python: study the logic and try to rewrite it from scratch.");
                ProblemSolutionCatalog.ProblemGuide solGuide = (guide != null) ? guide : ProblemSolutionCatalog.findGuide(userPrompt);
                root.addProperty("solution", solGuide != null ? solGuide.solution : ProblemSolutionCatalog.findGuide(userPrompt).solution);
                root.addProperty("solutionJava", solGuide != null ? solGuide.solutionJava : ProblemSolutionCatalog.findGuide(userPrompt).solutionJava);
                root.addProperty("solutionPython", solGuide != null ? solGuide.solutionPython : ProblemSolutionCatalog.findGuide(userPrompt).solutionPython);
            } else {
                root.addProperty("isCorrect", false);
                root.addProperty("hint", switch (level) {
                    case 1 -> "Take a closer look at your loop boundaries and return value. Are you handling all edge cases?";
                    case 2 -> "Consider the time complexity of your approach. Could a different data structure (HashMap, Set, Stack) reduce redundant work?";
                    case 3 -> "Review your algorithm step by step: verify initialization, loop invariant, and final return.";
                    default -> "Review the problem description carefully.";
                });
            }
        }

        root.add("mistakes", mistakes);

        // ===== SAFETY NET: Guarantee valid, verified "solution" key exists at Level 4 =====
        if (level == 4 && (!root.has("solution") || root.get("solution").getAsString().isBlank()
                || root.get("solution").getAsString().contains("Implement your solution here")
                || root.get("solution").getAsString().contains("Solution executed successfully"))) {
            ProblemSolutionCatalog.ProblemGuide fallbackGuide = (guide != null) ? guide : ProblemSolutionCatalog.findGuide(userPrompt);
            if (fallbackGuide != null && fallbackGuide.solution != null) {
                root.addProperty("solution", fallbackGuide.solution);
                root.addProperty("solutionJava", fallbackGuide.solutionJava);
                root.addProperty("solutionPython", fallbackGuide.solutionPython);
            }
            if (!root.has("hint") || root.get("hint").getAsString().isEmpty()) {
                root.addProperty("hint", "Here is the optimal reference solution in both Java and Python: study the logic and try to rewrite it from scratch.");
            }
        }

        return GSON.toJson(root);
    }

    private String generateMockProblem(String userPrompt) {
        String topic = "Loops";
        if (userPrompt.contains("Arrays")) topic = "Arrays";
        else if (userPrompt.contains("OOP")) topic = "OOP";
        else if (userPrompt.contains("Recursion")) topic = "Recursion";
        else if (userPrompt.contains("Strings")) topic = "Strings";

        JsonObject problem = new JsonObject();
        problem.addProperty("title", "Adaptive Challenge: " + topic + " Mastery Transfer");
        problem.addProperty("topic", topic);
        problem.addProperty("difficulty", "EASY");
        problem.addProperty("language", "java");
        problem.addProperty("description", "Synthesized by Adaptive AI Mentor: Write a Java method `process" + topic + "(int[] data)` that demonstrates mastery over " + topic + " by returning the count of valid elements.");
        String starterTemplate = "public class Solution {\n" +
                "    public static int process" + topic + "(int[] data) {\n" +
                "        // Implement your adaptive solution here\n" +
                "        return 0;\n" +
                "    }\n\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(process" + topic + "(new int[]{1, 2, 3, 4}));\n" +
                "    }\n" +
                "}";
        problem.addProperty("starter_code", starterTemplate);

        return GSON.toJson(problem);
    }

    @Override
    public String getProviderName() {
        return "Offline Mock AI Tutor (Pedagogy Calibrated)";
    }

    @Override
    public boolean isLive() {
        return false;
    }
}
