package com.aimentor.service.llm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.util.logging.Logger;

/**
 * Intelligent Mock LLM client for offline development, local demos, and automated testing.
 * Accurately simulates progressive hints (Levels 1-4), mistake diagnosis, problem generation,
 * and semantic code correctness verification without requiring an external Gemini API key.
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
        String studentCode = extractStudentCode(userPrompt);
        String lowerCode = studentCode.toLowerCase();

        // 1. Detect hint level requested (strictly based on prompt header, not Java class names)
        int level = 1;
        if (lowerPrompt.contains("authorization: level 4") || lowerPrompt.contains("level 4 of 4") || lowerPrompt.contains("level 4 explicit solution") || lowerPrompt.contains("hint level: 4")) {
            level = 4;
        } else if (lowerPrompt.contains("authorization: level 3") || lowerPrompt.contains("level 3 of 4") || lowerPrompt.contains("hint level: 3")) {
            level = 3;
        } else if (lowerPrompt.contains("authorization: level 2") || lowerPrompt.contains("level 2 of 4") || lowerPrompt.contains("hint level: 2")) {
            level = 2;
        } else if (lowerPrompt.contains("authorization: level 1") || lowerPrompt.contains("level 1 of 4") || lowerPrompt.contains("hint level: 1")) {
            level = 1;
        }

        // 2. Fetch guide from ProblemSolutionCatalog
        ProblemSolutionCatalog.ProblemGuide guide = ProblemSolutionCatalog.findGuide(userPrompt);
        String concept = (guide != null) ? guide.concept : "Algorithmic Logic and Control Flow";

        // 3. Check for specific code flaws
        boolean hasLoopWithoutIncrement = lowerCode.contains("while") 
                && !lowerCode.contains("++") && !lowerCode.contains("+=") 
                && !lowerCode.contains("--") && !lowerCode.contains("-=") 
                && !lowerCode.contains(".next") && !lowerCode.contains("break") 
                && !lowerCode.contains("return");

        boolean hasArrayIndexOOB = lowerCode.contains("<= arr.length") 
                || lowerCode.contains("<= length") 
                || lowerCode.contains("<= s.length()") 
                || lowerCode.contains("<= nums.length");

        boolean isStub = isStubCode(studentCode);

        // Check brace balance for syntax errors
        boolean hasUnbalancedBraces = false;
        if (lowerPrompt.contains("java")) {
            int openBraces = countOccurrences(studentCode, '{');
            int closeBraces = countOccurrences(studentCode, '}');
            if (openBraces > 0 && openBraces != closeBraces) {
                hasUnbalancedBraces = true;
            }
        }

        JsonObject root = new JsonObject();
        JsonArray mistakes = new JsonArray();
        root.addProperty("concept", concept);

        // LEVEL 4: Explicit Solution Request
        if (level == 4) {
            root.addProperty("isCorrect", false);
            root.addProperty("hint", "Here is the optimal reference solution in both Java and Python: study the logic and try to rewrite it from scratch.");
            ProblemSolutionCatalog.ProblemGuide solGuide = (guide != null) ? guide : ProblemSolutionCatalog.findGuide(userPrompt);
            if (solGuide != null) {
                root.addProperty("solution", solGuide.solution);
                root.addProperty("solutionJava", solGuide.solutionJava);
                root.addProperty("solutionPython", solGuide.solutionPython);
            }
            root.add("mistakes", mistakes);
            return GSON.toJson(root);
        }

        // INCOMPLETE / STUB IMPLEMENTATION
        if (isStub) {
            root.addProperty("isCorrect", false);
            JsonObject m = new JsonObject();
            m.addProperty("type", "IncompleteImplementation");
            m.addProperty("topic", "Algorithm Design");
            m.addProperty("line", 3);
            m.addProperty("description", "Method contains starter placeholder code. Review the problem requirements and implement the algorithm.");
            mistakes.add(m);

            String h = switch (level) {
                case 1 -> (guide != null) ? guide.hintLevel1 : "Take a closer look at the problem input and determine what data structure or loop is needed.";
                case 2 -> (guide != null) ? guide.hintLevel2 : "Initialize your variables and set up the core loop to traverse the inputs.";
                case 3 -> (guide != null) ? guide.hintLevel3 : "Implement the main logic step by step, returning the computed result.";
                default -> "Implement the core logic.";
            };
            root.addProperty("hint", h);
            root.add("mistakes", mistakes);
            return GSON.toJson(root);
        }

        // KNOWN CODE FLAWS
        if (hasArrayIndexOOB) {
            root.addProperty("isCorrect", false);
            JsonObject m = new JsonObject();
            m.addProperty("type", "ArrayIndexOutOfBounds");
            m.addProperty("topic", "Arrays");
            m.addProperty("line", 4);
            m.addProperty("description", "Loop condition uses '<=' with array length, which accesses an index equal to the array length.");
            mistakes.add(m);

            String h = switch (level) {
                case 1 -> "Look closely at your loop termination condition. Does the last iteration attempt to access a valid array index?";
                case 2 -> "In Java and Python, arrays are 0-indexed. An array with N elements has valid indices from 0 up to N - 1 only.";
                case 3 -> "Change `i <= arr.length` to `i < arr.length` to avoid accessing the non-existent index `arr[arr.length]`.";
                default -> "Check your loop upper bound.";
            };
            root.addProperty("hint", h);
            root.add("mistakes", mistakes);
            return GSON.toJson(root);
        }

        if (hasLoopWithoutIncrement) {
            root.addProperty("isCorrect", false);
            JsonObject m = new JsonObject();
            m.addProperty("type", "InfiniteLoop");
            m.addProperty("topic", "Loops");
            m.addProperty("line", 5);
            m.addProperty("description", "Loop condition variable is not updated within loop body, causing an infinite loop.");
            mistakes.add(m);

            String h = switch (level) {
                case 1 -> "What happens to your loop control variable during each iteration of the loop?";
                case 2 -> "A while loop will run forever unless at least one variable in the boolean guard condition changes value inside the loop.";
                case 3 -> "Add an increment statement like `i++` or update the state variable before the end of the while loop block.";
                default -> "Ensure loop variables are updated.";
            };
            root.addProperty("hint", h);
            root.add("mistakes", mistakes);
            return GSON.toJson(root);
        }

        if (hasUnbalancedBraces) {
            root.addProperty("isCorrect", false);
            JsonObject m = new JsonObject();
            m.addProperty("type", "SyntaxError");
            m.addProperty("topic", "Syntax");
            m.addProperty("line", 1);
            m.addProperty("description", "Unbalanced curly braces in source code. Check opening and closing brackets.");
            mistakes.add(m);
            root.addProperty("hint", "Check that every opening brace '{' has a matching closing brace '}'.");
            root.add("mistakes", mistakes);
            return GSON.toJson(root);
        }

        // SEMANTIC CORRECTNESS VERIFICATION
        boolean isCorrect = evaluateSemanticCorrectness(lowerPrompt, lowerCode);

        if (isCorrect) {
            root.addProperty("isCorrect", true);
            root.addProperty("hint", "Excellent work! Your solution is correct, satisfies all problem invariants, and passes the criteria.");
            root.add("mistakes", mistakes); // Empty mistakes
        } else {
            root.addProperty("isCorrect", false);
            String h = switch (level) {
                case 1 -> (guide != null) ? guide.hintLevel1 : "Review your loop bounds and edge cases. Are all inputs handled properly?";
                case 2 -> (guide != null) ? guide.hintLevel2 : "Consider the computational invariant: what state must be maintained at each step?";
                case 3 -> (guide != null) ? guide.hintLevel3 : "Trace your code with a small example to ensure your return value matches expectations.";
                default -> "Review your implementation.";
            };
            root.addProperty("hint", h);
            root.add("mistakes", mistakes);
        }

        return GSON.toJson(root);
    }

    /**
     * Inspects submitted code against problem-specific invariants to verify correctness.
     */
    private boolean evaluateSemanticCorrectness(String lowerPrompt, String lowerCode) {
        // 1. Find Maximum / Minimum Element
        if (lowerPrompt.contains("find maximum element") || lowerPrompt.contains("findmax") || lowerPrompt.contains("maximum element")) {
            boolean hasTraversal = lowerCode.contains("for") || lowerCode.contains("while") || lowerCode.contains("stream") || lowerCode.contains("in arr");
            boolean hasComparison = lowerCode.contains(">") || lowerCode.contains("<") || lowerCode.contains("math.max") || lowerCode.contains("max(");
            boolean hasReturn = lowerCode.contains("return max") || lowerCode.contains("return arr[") || lowerCode.contains("return m");
            return hasTraversal && hasComparison && hasReturn;
        }

        // 2. Sum of N Numbers
        if (lowerPrompt.contains("sum of n numbers") || lowerPrompt.contains("sumofn")) {
            boolean hasLoopAccumulator = (lowerCode.contains("for") || lowerCode.contains("while")) && (lowerCode.contains("+=") || lowerCode.contains("sum +"));
            boolean hasFormula = lowerCode.contains("n * (n + 1)") || lowerCode.contains("n*(n+1)");
            boolean hasReturn = lowerCode.contains("return");
            return (hasLoopAccumulator || hasFormula) && hasReturn;
        }

        // 3. Reverse a Number
        if (lowerPrompt.contains("reverse a number") || lowerPrompt.contains("reversenumber")) {
            boolean hasModulo = lowerCode.contains("% 10") || lowerCode.contains("%10");
            boolean hasMultiply = lowerCode.contains("* 10") || lowerCode.contains("*10") || lowerCode.contains("reverse");
            return hasModulo && hasMultiply && lowerCode.contains("return");
        }

        // 4. FizzBuzz
        if (lowerPrompt.contains("fizzbuzz")) {
            return (lowerCode.contains("% 3") || lowerCode.contains("%3")) 
                    && (lowerCode.contains("% 5") || lowerCode.contains("%5")) 
                    && lowerCode.contains("fizz") && lowerCode.contains("buzz");
        }

        // 5. Two Sum
        if (lowerPrompt.contains("two sum") || lowerPrompt.contains("twosum")) {
            boolean hasMap = (lowerCode.contains("map.containskey") || lowerCode.contains("map.get")) 
                    && (lowerCode.contains("target -") || lowerCode.contains("target-") || lowerCode.contains("complement"));
            boolean hasNestedLoop = lowerCode.contains("nums[i] + nums[j]") || lowerCode.contains("nums[j] + nums[i]");
            return (hasMap || hasNestedLoop) && lowerCode.contains("return");
        }

        // 6. Palindrome Check
        if (lowerPrompt.contains("palindrome check") || lowerPrompt.contains("ispalindrome")) {
            boolean hasTwoPointers = (lowerCode.contains("charat") || lowerCode.contains("s[l]") || lowerCode.contains("s[left]")) 
                    && (lowerCode.contains("<") || lowerCode.contains("==") || lowerCode.contains("!="));
            boolean hasReverse = lowerCode.contains("reverse") || lowerCode.contains("stringbuilder");
            return (hasTwoPointers || hasReverse) && lowerCode.contains("return");
        }

        // 7. Count Vowels and Consonants
        if (lowerPrompt.contains("count vowels") || lowerPrompt.contains("countvowels")) {
            return (lowerCode.contains("'a'") || lowerCode.contains("\"a\"") || lowerCode.contains("aeiou")) 
                    && (lowerCode.contains("vowel") || lowerCode.contains("vowels"));
        }

        // 8. Safe Division
        if (lowerPrompt.contains("safe division") || lowerPrompt.contains("safedivide")) {
            boolean hasTryCatch = lowerCode.contains("try") && lowerCode.contains("arithmeticexception");
            boolean hasGuard = lowerCode.contains("b == 0") || lowerCode.contains("b==0");
            return (hasTryCatch || hasGuard) && lowerCode.contains("return");
        }

        // 9. Factorial
        if (lowerPrompt.contains("factorial")) {
            boolean hasRecursion = lowerCode.contains("factorial(") && lowerCode.contains("*");
            boolean hasLoop = (lowerCode.contains("for") || lowerCode.contains("while")) && lowerCode.contains("*=");
            return (hasRecursion || hasLoop) && lowerCode.contains("return");
        }

        // 10. Fibonacci
        if (lowerPrompt.contains("fibonacci") || lowerPrompt.contains("fib(")) {
            boolean hasRecurrence = lowerCode.contains("fib(") && lowerCode.contains("+");
            boolean hasIterative = lowerCode.contains("for") || lowerCode.contains("dp");
            return (hasRecurrence || hasIterative) && lowerCode.contains("return");
        }

        // 11. Best Time to Buy and Sell Stock
        if (lowerPrompt.contains("buy and sell stock") || lowerPrompt.contains("maxprofit")) {
            boolean hasMin = lowerCode.contains("min") || lowerCode.contains("buy");
            boolean hasProfit = lowerCode.contains("profit") || lowerCode.contains("max");
            return hasMin && hasProfit && lowerCode.contains("return");
        }

        // 12. Valid Parentheses
        if (lowerPrompt.contains("valid parentheses") || lowerPrompt.contains("isvalid(")) {
            boolean hasStack = lowerCode.contains("stack") || lowerCode.contains("deque") || lowerCode.contains("push") || lowerCode.contains("pop");
            boolean hasReplace = lowerCode.contains("replace");
            return (hasStack || hasReplace) && lowerCode.contains("return");
        }

        // 13. Reverse Linked List
        if (lowerPrompt.contains("reverse linked list") || lowerPrompt.contains("reverselist")) {
            return (lowerCode.contains("prev") || lowerCode.contains("next") || lowerCode.contains("curr")) 
                    && lowerCode.contains("return");
        }

        // 14. Binary Search
        if (lowerPrompt.contains("binary search") || lowerPrompt.contains("search(")) {
            return (lowerCode.contains("<=") || lowerCode.contains("<")) 
                    && lowerCode.contains("mid") 
                    && lowerCode.contains("return");
        }

        // 15. Contains Duplicate
        if (lowerPrompt.contains("contains duplicate") || lowerPrompt.contains("containsduplicate")) {
            return (lowerCode.contains("set") || lowerCode.contains("hashset") || lowerCode.contains("sort")) 
                    && lowerCode.contains("return");
        }

        // 16. Bank Account Class
        if (lowerPrompt.contains("bank account") || lowerPrompt.contains("bankaccount")) {
            return lowerCode.contains("balance") && lowerCode.contains("deposit") && lowerCode.contains("withdraw");
        }

        // 17. Shape Hierarchy
        if (lowerPrompt.contains("shape hierarchy") || lowerPrompt.contains("shape")) {
            return lowerCode.contains("area") && (lowerCode.contains("circle") || lowerCode.contains("rectangle"));
        }

        // 18. Custom Exception
        if (lowerPrompt.contains("custom exception") || lowerPrompt.contains("agevalidationexception")) {
            return lowerCode.contains("extends") && lowerCode.contains("exception") && lowerCode.contains("throw");
        }

        // Universal Fallback:
        // If code has meaningful structure (control flow, return statement, class/def),
        // and is not empty or stubbed, mark it correct.
        boolean hasControlFlow = lowerCode.contains("for") || lowerCode.contains("while") 
                || lowerCode.contains("if") || lowerCode.contains("return");
        boolean hasMeaningfulLength = lowerCode.length() > 60;

        return hasControlFlow && hasMeaningfulLength;
    }

    /**
     * Extracts student code cleanly from prompt between delimiters.
     */
    private static String extractStudentCode(String prompt) {
        if (prompt == null) return "";
        String marker = "=== SUBMITTED STUDENT CODE";
        int idx = prompt.indexOf(marker);
        if (idx != -1) {
            int newline = prompt.indexOf('\n', idx);
            if (newline != -1) {
                int schemaIdx = prompt.indexOf("=== OUTPUT JSON SCHEMA", newline);
                if (schemaIdx != -1) {
                    return prompt.substring(newline + 1, schemaIdx).trim();
                }
                return prompt.substring(newline + 1).trim();
            }
        }
        return prompt;
    }

    /**
     * Identifies if the code is just an untouched starter stub or empty skeleton.
     */
    private static boolean isStubCode(String code) {
        if (code == null || code.isBlank()) return true;

        // Strip comments
        String clean = code.replaceAll("//.*", "").replaceAll("/\\*.*?\\*/", "").trim();

        // Check if there is any real control flow or logic
        boolean hasLogic = clean.contains("for") || clean.contains("while")
                || clean.contains("if") || clean.contains("switch")
                || clean.contains("try") || clean.contains("Math.")
                || clean.contains("Collections.") || clean.contains("Arrays.")
                || clean.contains("Set") || clean.contains("Map")
                || clean.contains("Stack") || clean.contains("Queue")
                || clean.contains("List") || clean.contains("StringBuilder")
                || clean.contains("->") || clean.contains("%")
                || clean.contains(">>") || clean.contains("<<");

        // If it has NO logic, check if it just returns default literals
        if (!hasLogic) {
            String lower = clean.toLowerCase();
            return lower.contains("return 0;")
                    || lower.contains("return null;")
                    || lower.contains("return false;")
                    || lower.contains("return -1;")
                    || lower.contains("return new int[]")
                    || lower.contains("return \"\";")
                    || lower.contains("pass");
        }

        return false;
    }

    private static int countOccurrences(String text, char ch) {
        if (text == null) return 0;
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ch) count++;
        }
        return count;
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
