-- ============================================================
-- Adaptive AI Coding Mentor — Seed Data
-- Run AFTER schema.sql
-- ============================================================

USE ai_coding_mentor;

-- ============================================================
-- Users
-- Password for all demo accounts: "password123"
-- BCrypt hash of "password123" (10 rounds)
-- ============================================================
INSERT INTO users (name, email, password_hash, role, level) VALUES
('Admin User',   'admin@aimentor.com',   '$2a$10$wm6/Q.dqA1pjkuxhNqUCUOdkOqWjFFM7ZwSQk0DK1.8Cn648qimZO', 'ADMIN',   'ADVANCED'),
('Alice Student', 'alice@student.com',    '$2a$10$wm6/Q.dqA1pjkuxhNqUCUOdkOqWjFFM7ZwSQk0DK1.8Cn648qimZO', 'STUDENT', 'BEGINNER'),
('Bob Learner',   'bob@student.com',      '$2a$10$wm6/Q.dqA1pjkuxhNqUCUOdkOqWjFFM7ZwSQk0DK1.8Cn648qimZO', 'STUDENT', 'INTERMEDIATE');

-- ============================================================
-- Problems (15+ across topics and difficulties)
-- ============================================================
INSERT INTO problems (title, description, topic, difficulty, language, starter_code) VALUES
-- Loops
('Sum of N Numbers',
 'Write a Java method `sumOfN(int n)` that returns the sum of all integers from 1 to n (inclusive). If n is 0 or negative, return 0.',
 'Loops', 'BEGINNER', 'java',
 'public class Solution {\n    public static int sumOfN(int n) {\n        // Write your code here\n        return 0;\n    }\n\n    public static void main(String[] args) {\n        System.out.println(sumOfN(5));  // Expected: 15\n        System.out.println(sumOfN(10)); // Expected: 55\n    }\n}'),

('Reverse a Number',
 'Write a method `reverseNumber(int num)` that reverses the digits of a positive integer. Example: 1234 → 4321.',
 'Loops', 'EASY', 'java',
 'public class Solution {\n    public static int reverseNumber(int num) {\n        // Write your code here\n        return 0;\n    }\n\n    public static void main(String[] args) {\n        System.out.println(reverseNumber(1234)); // Expected: 4321\n        System.out.println(reverseNumber(900));  // Expected: 9\n    }\n}'),

('FizzBuzz',
 'Print numbers from 1 to n. For multiples of 3 print "Fizz", for multiples of 5 print "Buzz", for multiples of both print "FizzBuzz".',
 'Loops', 'EASY', 'java',
 'public class Solution {\n    public static void fizzBuzz(int n) {\n        // Write your code here\n    }\n\n    public static void main(String[] args) {\n        fizzBuzz(15);\n    }\n}'),

-- Arrays
('Find Maximum Element',
 'Write a method `findMax(int[] arr)` that returns the maximum element in the array. Do not use Arrays.sort() or streams.',
 'Arrays', 'BEGINNER', 'java',
 'public class Solution {\n    public static int findMax(int[] arr) {\n        // Write your code here\n        return 0;\n    }\n\n    public static void main(String[] args) {\n        int[] nums = {3, 7, 2, 9, 1, 5};\n        System.out.println(findMax(nums)); // Expected: 9\n    }\n}'),

('Two Sum',
 'Given an array of integers and a target sum, return the indices of two numbers that add up to the target. Assume exactly one solution exists.',
 'Arrays', 'MEDIUM', 'java',
 'public class Solution {\n    public static int[] twoSum(int[] nums, int target) {\n        // Write your code here\n        return new int[]{-1, -1};\n    }\n\n    public static void main(String[] args) {\n        int[] result = twoSum(new int[]{2, 7, 11, 15}, 9);\n        System.out.println(result[0] + ", " + result[1]); // Expected: 0, 1\n    }\n}'),

('Rotate Array',
 'Rotate an array to the right by k positions. Modify the array in-place.',
 'Arrays', 'HARD', 'java',
 'public class Solution {\n    public static void rotate(int[] nums, int k) {\n        // Write your code here\n    }\n\n    public static void main(String[] args) {\n        int[] arr = {1, 2, 3, 4, 5};\n        rotate(arr, 2);\n        // Expected output: 4 5 1 2 3\n        for (int n : arr) System.out.print(n + " ");\n    }\n}'),

-- Strings
('Palindrome Check',
 'Write a method `isPalindrome(String s)` that returns true if the given string is a palindrome (ignoring case and non-alphanumeric chars).',
 'Strings', 'EASY', 'java',
 'public class Solution {\n    public static boolean isPalindrome(String s) {\n        // Write your code here\n        return false;\n    }\n\n    public static void main(String[] args) {\n        System.out.println(isPalindrome("racecar"));   // true\n        System.out.println(isPalindrome("hello"));     // false\n        System.out.println(isPalindrome("A man a plan a canal Panama")); // true\n    }\n}'),

('Count Vowels and Consonants',
 'Write a method that counts and prints the number of vowels and consonants in a given string (ignore non-alphabetic characters).',
 'Strings', 'BEGINNER', 'java',
 'public class Solution {\n    public static void countVowelsConsonants(String s) {\n        // Write your code here\n    }\n\n    public static void main(String[] args) {\n        countVowelsConsonants("Hello World");\n        // Expected: Vowels: 3, Consonants: 7\n    }\n}'),

-- OOP
('Bank Account Class',
 'Create a BankAccount class with private fields for accountNumber, holderName, and balance. Implement deposit(amount), withdraw(amount) with insufficient-balance check, and getBalance(). Write a main method demonstrating all operations.',
 'OOP', 'EASY', 'java',
 'public class Solution {\n    // Define BankAccount class here\n\n    public static void main(String[] args) {\n        // Create account, deposit, withdraw, print balance\n    }\n}'),

('Shape Hierarchy',
 'Create an abstract class Shape with an abstract method area(). Implement Circle and Rectangle subclasses. Demonstrate polymorphism by storing shapes in an array and printing areas.',
 'OOP', 'MEDIUM', 'java',
 'public class Solution {\n    // Define Shape, Circle, Rectangle here\n\n    public static void main(String[] args) {\n        // Create shapes array, print areas\n    }\n}'),

-- Exception Handling
('Safe Division',
 'Write a method `safeDivide(int a, int b)` that returns a/b. Handle ArithmeticException for division by zero and return 0 in that case. Print an appropriate error message.',
 'Exception Handling', 'BEGINNER', 'java',
 'public class Solution {\n    public static int safeDivide(int a, int b) {\n        // Write your code here\n        return 0;\n    }\n\n    public static void main(String[] args) {\n        System.out.println(safeDivide(10, 2));  // 5\n        System.out.println(safeDivide(10, 0));  // 0 (with error message)\n    }\n}'),

('Custom Exception',
 'Create a custom exception AgeValidationException. Write a method setAge(int age) that throws this exception if age < 0 or > 150. Demonstrate catching it.',
 'Exception Handling', 'MEDIUM', 'java',
 'public class Solution {\n    // Define AgeValidationException and setAge here\n\n    public static void main(String[] args) {\n        // Test with valid and invalid ages\n    }\n}'),

-- Recursion
('Factorial',
 'Write a recursive method `factorial(int n)` that returns n!. Handle the base case (n <= 1 returns 1). Throw IllegalArgumentException for negative input.',
 'Recursion', 'BEGINNER', 'java',
 'public class Solution {\n    public static long factorial(int n) {\n        // Write your code here\n        return 0;\n    }\n\n    public static void main(String[] args) {\n        System.out.println(factorial(5));  // 120\n        System.out.println(factorial(0));  // 1\n    }\n}'),

('Fibonacci Sequence',
 'Write a recursive method `fibonacci(int n)` that returns the nth Fibonacci number (0-indexed: fib(0)=0, fib(1)=1). Implement memoization for efficiency.',
 'Recursion', 'MEDIUM', 'java',
 'public class Solution {\n    public static long fibonacci(int n) {\n        // Write your code here\n        return 0;\n    }\n\n    public static void main(String[] args) {\n        System.out.println(fibonacci(10)); // 55\n        System.out.println(fibonacci(20)); // 6765\n    }\n}'),

-- SQL
('Student Grade Query',
 'Write a SQL query to find students with grades above 80 from a ''students'' table with columns: id, name, grade, department. Order results by grade descending.\n\nWrite your query as a String variable and print it.',
 'SQL', 'BEGINNER', 'java',
 'public class Solution {\n    public static void main(String[] args) {\n        String query = "";\n        // Write your SQL query in the string\n        System.out.println(query);\n    }\n}'),

('Join Query',
 'Write a SQL query that joins ''orders'' and ''customers'' tables to find the total order amount per customer. Tables: orders(id, customer_id, amount), customers(id, name). Group by customer name, order by total descending.',
 'SQL', 'MEDIUM', 'java',
 'public class Solution {\n    public static void main(String[] args) {\n        String query = "";\n        // Write your SQL query in the string\n        System.out.println(query);\n    }\n}'),

-- Interview level
('LRU Cache',
 'Implement an LRU (Least Recently Used) Cache with get(key) and put(key, value) operations, both in O(1) time. Use a combination of HashMap and doubly-linked list.',
 'Arrays', 'INTERVIEW', 'java',
 'public class Solution {\n    // Implement LRU Cache here\n\n    public static void main(String[] args) {\n        // Test LRU Cache operations\n    }\n}');

-- ============================================================
-- Sample Submissions for demo students
-- ============================================================
INSERT INTO submissions (user_id, problem_id, language, code, status, output, created_at) VALUES
-- Alice's submissions
(2, 1, 'java', 'public class Solution {\n    public static int sumOfN(int n) {\n        int sum = 0;\n        for (int i = 0; i <= n; i++) {\n            sum += i;\n        }\n        return sum;\n    }\n    public static void main(String[] args) {\n        System.out.println(sumOfN(5));\n    }\n}', 'CORRECT', '15', DATE_SUB(NOW(), INTERVAL 14 DAY)),

(2, 4, 'java', 'public class Solution {\n    public static int findMax(int[] arr) {\n        int max = 0;\n        for (int i = 0; i < arr.length; i++) {\n            if (arr[i] > max) max = arr[i];\n        }\n        return max;\n    }\n    public static void main(String[] args) {\n        System.out.println(findMax(new int[]{3,7,2,9}));\n    }\n}', 'INCORRECT', '9', DATE_SUB(NOW(), INTERVAL 12 DAY)),

(2, 7, 'java', 'public class Solution {\n    public static boolean isPalindrome(String s) {\n        return s.equals(new StringBuilder(s).reverse().toString());\n    }\n    public static void main(String[] args) {\n        System.out.println(isPalindrome("racecar"));\n    }\n}', 'INCORRECT', 'true', DATE_SUB(NOW(), INTERVAL 10 DAY)),

(2, 13, 'java', 'public class Solution {\n    public static long factorial(int n) {\n        return n * factorial(n-1);\n    }\n    public static void main(String[] args) {\n        System.out.println(factorial(5));\n    }\n}', 'ERROR', 'StackOverflowError', DATE_SUB(NOW(), INTERVAL 7 DAY)),

-- Bob's submissions
(3, 1, 'java', 'public class Solution {\n    public static int sumOfN(int n) {\n        return n * (n + 1) / 2;\n    }\n    public static void main(String[] args) {\n        System.out.println(sumOfN(5));\n    }\n}', 'CORRECT', '15', DATE_SUB(NOW(), INTERVAL 20 DAY)),

(3, 5, 'java', 'public class Solution {\n    public static int[] twoSum(int[] nums, int target) {\n        for (int i = 0; i < nums.length; i++) {\n            for (int j = 0; j < nums.length; j++) {\n                if (nums[i] + nums[j] == target) return new int[]{i,j};\n            }\n        }\n        return new int[]{-1,-1};\n    }\n    public static void main(String[] args) {}\n}', 'INCORRECT', '', DATE_SUB(NOW(), INTERVAL 15 DAY)),

(3, 10, 'java', 'public class Solution {\n    abstract class Shape {\n        abstract double area();\n    }\n    class Circle extends Shape {\n        double r;\n        Circle(double r) { this.r = r; }\n        double area() { return 3.14 * r * r; }\n    }\n    public static void main(String[] args) {}\n}', 'CORRECT', '', DATE_SUB(NOW(), INTERVAL 8 DAY));

-- ============================================================
-- Sample Mistakes
-- ============================================================
INSERT INTO mistakes (user_id, submission_id, mistake_type, topic, line_number, description, created_at) VALUES
-- Alice's mistakes
(2, 2, 'Initialization Error', 'Arrays', 3, 'Initializing max to 0 fails for arrays with all negative numbers. Should initialize to arr[0] or Integer.MIN_VALUE.', DATE_SUB(NOW(), INTERVAL 12 DAY)),
(2, 3, 'Missing Edge Case', 'Strings', 3, 'Not handling case insensitivity or non-alphanumeric characters in palindrome check.', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(2, 4, 'Missing Base Case', 'Recursion', 3, 'Recursive factorial has no base case, leading to infinite recursion and StackOverflowError.', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(2, 4, 'No Negative Check', 'Recursion', 3, 'No validation for negative input in factorial function.', DATE_SUB(NOW(), INTERVAL 7 DAY)),

-- Bob's mistakes
(3, 6, 'Logic Error', 'Arrays', 4, 'Inner loop starts at j=0 instead of j=i+1, which can return the same index twice (e.g., i==j).', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(3, 6, 'Inefficiency', 'Arrays', 3, 'Brute force O(n²) approach. Consider using a HashMap for O(n) solution.', DATE_SUB(NOW(), INTERVAL 15 DAY));

-- Add more recent mistakes for trend analysis
INSERT INTO mistakes (user_id, submission_id, mistake_type, topic, line_number, description, created_at) VALUES
(2, 2, 'Initialization Error', 'Arrays', 3, 'Variable initialized to wrong default value.', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 3, 'Missing Edge Case', 'Strings', 5, 'Did not handle empty string input.', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 3, 'Missing Edge Case', 'Strings', 2, 'Failed to trim whitespace before processing.', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ============================================================
-- Sample Topic Mastery
-- ============================================================
INSERT INTO topic_mastery (user_id, topic, score, attempts, successes, common_mistakes) VALUES
-- Alice
(2, 'Loops', 72.0, 3, 2, '{"Off-by-one": 1, "Infinite Loop": 1}'),
(2, 'Arrays', 45.0, 4, 1, '{"Initialization Error": 2, "Index Out of Bounds": 1}'),
(2, 'Strings', 38.0, 3, 0, '{"Missing Edge Case": 3}'),
(2, 'Recursion', 25.0, 2, 0, '{"Missing Base Case": 1, "No Negative Check": 1}'),
(2, 'OOP', 50.0, 0, 0, NULL),
(2, 'Exception Handling', 50.0, 0, 0, NULL),
(2, 'SQL', 50.0, 0, 0, NULL),

-- Bob
(3, 'Loops', 85.0, 5, 4, '{"Off-by-one": 1}'),
(3, 'Arrays', 55.0, 4, 2, '{"Logic Error": 1, "Inefficiency": 1}'),
(3, 'OOP', 70.0, 3, 2, '{"Access Modifier": 1}'),
(3, 'Strings', 50.0, 0, 0, NULL),
(3, 'Recursion', 50.0, 0, 0, NULL),
(3, 'Exception Handling', 50.0, 0, 0, NULL),
(3, 'SQL', 50.0, 0, 0, NULL);

-- ============================================================
-- Sample Hint Sessions
-- ============================================================
INSERT INTO hint_sessions (user_id, submission_id, current_level, solution_revealed) VALUES
(2, 2, 2, FALSE),
(2, 4, 3, TRUE),
(3, 6, 1, FALSE);

INSERT INTO hints (hint_session_id, level, content) VALUES
(1, 1, 'Think about what happens when all elements in the array are negative. What should the initial value of max be?'),
(1, 2, 'When finding the maximum, initializing to 0 means you assume 0 is smaller than all elements. For negative arrays, 0 would be returned as max even though it is not in the array. Consider using the first element or Integer.MIN_VALUE.'),
(2, 1, 'Every recursive function needs a stopping condition. What happens when n reaches 0 or 1?'),
(2, 2, 'A recursive function without a base case will call itself infinitely, causing a StackOverflowError. In factorial, the base case is: if n <= 1, return 1.'),
(2, 3, 'Your factorial function calls factorial(n-1) without ever checking if n has reached the base case. Add: if (n <= 1) return 1; at the start of the method. Also consider what should happen for negative inputs.'),
(3, 1, 'Look carefully at where your inner loop starts. Can i and j ever be the same index?');

-- ============================================================
-- Sample User Problem Progress
-- ============================================================
INSERT INTO user_problem_progress (user_id, problem_id, solved, hints_used, attempts, completed_at) VALUES
(2, 1, TRUE, 0, 1, DATE_SUB(NOW(), INTERVAL 14 DAY)),
(2, 4, FALSE, 2, 2, NULL),
(2, 7, FALSE, 1, 1, NULL),
(2, 13, FALSE, 3, 2, NULL),
(3, 1, TRUE, 0, 1, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(3, 5, FALSE, 1, 1, NULL),
(3, 10, TRUE, 0, 1, DATE_SUB(NOW(), INTERVAL 8 DAY));
