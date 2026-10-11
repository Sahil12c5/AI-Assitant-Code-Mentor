package com.aimentor.service.llm;

import java.util.HashMap;
import java.util.Map;

/**
 * High-performance catalog providing verified, idiomatic Python 3 solutions
 * paired with Java solutions for all curriculum, Blind 75, and interview challenge problems.
 */
public class PythonSolutionCatalog {

    private static final Map<String, String> PYTHON_GUIDES = new HashMap<>();

    static {
        registerCurriculumPython();
        registerChallengePatternPython();
        registerLeetCodePython();
    }

    public static String getPythonSolution(String promptOrTitle, String javaFallback) {
        if (promptOrTitle != null) {
            String lower = promptOrTitle.toLowerCase();
            for (Map.Entry<String, String> entry : PYTHON_GUIDES.entrySet()) {
                if (lower.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return convertJavaToPython(javaFallback);
    }

    private static void put(String key, String pythonCode) {
        PYTHON_GUIDES.put(key.toLowerCase(), pythonCode.trim());
    }

    // =========================================================================
    // 1. CURRICULUM STARTER PROBLEMS (PYTHON 3)
    // =========================================================================
    private static void registerCurriculumPython() {
        put("sum of n numbers",
                """
                class Solution:
                    @staticmethod
                    def sum_of_n(n: int) -> int:
                        if n <= 0:
                            return 0
                        total = 0
                        for i in range(1, n + 1):
                            total += i
                        return total

                if __name__ == "__main__":
                    print("sum_of_n(5) =", Solution.sum_of_n(5))   # 15
                    print("sum_of_n(10) =", Solution.sum_of_n(10)) # 55
                """);

        put("reverse a number",
                """
                class Solution:
                    @staticmethod
                    def reverse_number(num: int) -> int:
                        sign = -1 if num < 0 else 1
                        num = abs(num)
                        rev = 0
                        while num > 0:
                            rev = rev * 10 + (num % 10)
                            num //= 10
                        return sign * rev

                if __name__ == "__main__":
                    print("1234 ->", Solution.reverse_number(1234)) # 4321
                    print("900 ->", Solution.reverse_number(900))   # 9
                """);

        put("fizzbuzz",
                """
                class Solution:
                    @staticmethod
                    def fizz_buzz(n: int) -> list[str]:
                        result = []
                        for i in range(1, n + 1):
                            if i % 15 == 0:
                                result.append("FizzBuzz")
                            elif i % 3 == 0:
                                result.append("Fizz")
                            elif i % 5 == 0:
                                result.append("Buzz")
                            else:
                                result.append(str(i))
                        return result

                if __name__ == "__main__":
                    for val in Solution.fizz_buzz(15):
                        print(val)
                """);

        put("palindrome number",
                """
                class Solution:
                    @staticmethod
                    def is_palindrome(x: int) -> bool:
                        if x < 0 or (x % 10 == 0 and x != 0):
                            return False
                        rev = 0
                        while x > rev:
                            rev = rev * 10 + (x % 10)
                            x //= 10
                        return x == rev or x == rev // 10

                if __name__ == "__main__":
                    print("121 is palindrome:", Solution.is_palindrome(121))   # True
                    print("-121 is palindrome:", Solution.is_palindrome(-121)) # False
                """);

        put("prime number check",
                """
                class Solution:
                    @staticmethod
                    def is_prime(n: int) -> bool:
                        if n <= 1:
                            return False
                        if n <= 3:
                            return True
                        if n % 2 == 0 or n % 3 == 0:
                            return False
                        i = 5
                        while i * i <= n:
                            if n % i == 0 or n % (i + 2) == 0:
                                return False
                            i += 6
                        return True

                if __name__ == "__main__":
                    print("29 is prime:", Solution.is_prime(29)) # True
                    print("15 is prime:", Solution.is_prime(15)) # False
                """);

        put("factorial calculation",
                """
                class Solution:
                    @staticmethod
                    def factorial(n: int) -> int:
                        if n < 0:
                            raise ValueError("Negative input not allowed")
                        result = 1
                        for i in range(2, n + 1):
                            result *= i
                        return result

                if __name__ == "__main__":
                    print("5! =", Solution.factorial(5)) # 120
                """);

        put("fibonacci",
                """
                class Solution:
                    @staticmethod
                    def fibonacci(n: int) -> int:
                        if n <= 0:
                            return 0
                        if n == 1:
                            return 1
                        a, b = 0, 1
                        for _ in range(2, n + 1):
                            a, b = b, a + b
                        return b

                if __name__ == "__main__":
                    print("F(7) =", Solution.fibonacci(7)) # 13
                """);

        put("greatest common divisor",
                """
                class Solution:
                    @staticmethod
                    def gcd(a: int, b: int) -> int:
                        while b != 0:
                            a, b = b, a % b
                        return abs(a)

                if __name__ == "__main__":
                    print("gcd(48, 18) =", Solution.gcd(48, 18)) # 6
                """);

        put("array maximum",
                """
                class Solution:
                    @staticmethod
                    def find_max(arr: list[int]) -> int:
                        if not arr:
                            raise ValueError("Array cannot be empty")
                        max_val = arr[0]
                        for x in arr[1:]:
                            if x > max_val:
                                max_val = x
                        return max_val

                if __name__ == "__main__":
                    print("Max:", Solution.find_max([3, 7, 2, 9, 4])) # 9
                """);

        put("reverse an array",
                """
                class Solution:
                    @staticmethod
                    def reverse_array(arr: list[int]) -> list[int]:
                        left, right = 0, len(arr) - 1
                        while left < right:
                            arr[left], arr[right] = arr[right], arr[left]
                            left += 1
                            right -= 1
                        return arr

                if __name__ == "__main__":
                    print(Solution.reverse_array([1, 2, 3, 4, 5])) # [5, 4, 3, 2, 1]
                """);

        put("linear search",
                """
                class Solution:
                    @staticmethod
                    def linear_search(arr: list[int], target: int) -> int:
                        for i, x in enumerate(arr):
                            if x == target:
                                return i
                        return -1

                if __name__ == "__main__":
                    print("Index:", Solution.linear_search([10, 25, 42, 8], 42)) # 2
                """);

        put("binary search",
                """
                class Solution:
                    @staticmethod
                    def binary_search(nums: list[int], target: int) -> int:
                        low, high = 0, len(nums) - 1
                        while low <= high:
                            mid = low + (high - low) // 2
                            if nums[mid] == target:
                                return mid
                            elif nums[mid] < target:
                                low = mid + 1
                            else:
                                high = mid - 1
                        return -1

                if __name__ == "__main__":
                    print("Index:", Solution.binary_search([-1, 0, 3, 5, 9, 12], 9)) # 4
                """);

        put("count vowels",
                """
                class Solution:
                    @staticmethod
                    def count_vowels(s: str) -> int:
                        vowels = set("aeiouAEIOU")
                        return sum(1 for ch in s if ch in vowels)

                if __name__ == "__main__":
                    print("Vowels:", Solution.count_vowels("Hello World")) # 3
                """);

        put("string reverse",
                """
                class Solution:
                    @staticmethod
                    def reverse_string(s: str) -> str:
                        return s[::-1]

                if __name__ == "__main__":
                    print(Solution.reverse_string("adaptive")) # "evitpada"
                """);

        put("anagram check",
                """
                from collections import Counter

                class Solution:
                    @staticmethod
                    def is_anagram(s: str, t: str) -> bool:
                        return Counter(s) == Counter(t)

                if __name__ == "__main__":
                    print(Solution.is_anagram("anagram", "nagaram")) # True
                """);

        put("matrix addition",
                """
                class Solution:
                    @staticmethod
                    def add_matrices(a: list[list[int]], b: list[list[int]]) -> list[list[int]]:
                        rows, cols = len(a), len(a[0])
                        return [[a[r][c] + b[r][c] for c in range(cols)] for r in range(rows)]

                if __name__ == "__main__":
                    print(Solution.add_matrices([[1, 2], [3, 4]], [[5, 6], [7, 8]])) # [[6, 8], [10, 12]]
                """);

        put("bubble sort",
                """
                class Solution:
                    @staticmethod
                    def bubble_sort(arr: list[int]) -> list[int]:
                        n = len(arr)
                        for i in range(n):
                            swapped = False
                            for j in range(0, n - i - 1):
                                if arr[j] > arr[j + 1]:
                                    arr[j], arr[j + 1] = arr[j + 1], arr[j]
                                    swapped = True
                            if not swapped:
                                break
                        return arr

                if __name__ == "__main__":
                    print(Solution.bubble_sort([64, 34, 25, 12, 22]))
                """);
    }

    // =========================================================================
    // 2. INTERVIEW CHALLENGE PATTERNS (Covers all 180 programmatic challenges)
    // =========================================================================
    private static void registerChallengePatternPython() {
        put("optimal state compression",
                """
                class Solution:
                    @staticmethod
                    def compress_state(elements: list[int]) -> int:
                        mask = 0
                        for x in elements:
                            if 0 <= x < 31:
                                mask |= (1 << x)
                        return mask

                if __name__ == "__main__":
                    sample = [1, 3, 5]
                    print("Bitmask state:", bin(Solution.compress_state(sample)))
                """);

        put("monotonic invariant verification",
                """
                class Solution:
                    @staticmethod
                    def next_greater_elements(nums: list[int]) -> list[int]:
                        n = len(nums)
                        result = [-1] * n
                        stack = []
                        for i in range(n - 1, -1, -1):
                            while stack and stack[-1] <= nums[i]:
                                stack.pop()
                            result[i] = stack[-1] if stack else -1
                            stack.append(nums[i])
                        return result

                if __name__ == "__main__":
                    print("Next greater:", Solution.next_greater_elements([2, 1, 2, 4, 3])) # [4, 2, 4, -1, -1]
                """);

        put("two-pass boundary",
                """
                class Solution:
                    @staticmethod
                    def boundary_scan(arr: list[int]) -> list[int]:
                        n = len(arr)
                        left = [0] * n
                        right = [0] * n
                        left[0] = arr[0]
                        for i in range(1, n):
                            left[i] = max(left[i - 1], arr[i])
                        right[-1] = arr[-1]
                        for i in range(n - 2, -1, -1):
                            right[i] = min(right[i + 1], arr[i])
                        return left

                if __name__ == "__main__":
                    print("Boundary scan:", Solution.boundary_scan([3, 1, 4, 1, 5, 9]))
                """);

        put("prefix-suffix product aggregation",
                """
                class Solution:
                    @staticmethod
                    def aggregate_products(nums: list[int]) -> list[int]:
                        n = len(nums)
                        result = [1] * n
                        for i in range(1, n):
                            result[i] = result[i - 1] * nums[i - 1]
                        right = 1
                        for i in range(n - 1, -1, -1):
                            result[i] *= right
                            right *= nums[i]
                        return result

                if __name__ == "__main__":
                    print("Aggregate products:", Solution.aggregate_products([1, 2, 3, 4])) # [24, 12, 8, 6]
                """);

        put("sliding window partition",
                """
                class Solution:
                    @staticmethod
                    def max_subarray_len(nums: list[int], target: int) -> int:
                        left = 0
                        curr_sum = 0
                        max_len = 0
                        for right in range(len(nums)):
                            curr_sum += nums[right]
                            while curr_sum > target and left <= right:
                                curr_sum -= nums[left]
                                left += 1
                            if curr_sum <= target:
                                max_len = max(max_len, right - left + 1)
                        return max_len

                if __name__ == "__main__":
                    print("Max window:", Solution.max_subarray_len([1, 2, 1, 0, 1, 1, 0], 4))
                """);

        put("topological order validation",
                """
                from collections import deque, defaultdict

                class Solution:
                    @staticmethod
                    def topological_order(n: int, edges: list[list[int]]) -> list[int]:
                        adj = defaultdict(list)
                        in_degree = [0] * n
                        for u, v in edges:
                            adj[u].append(v)
                            in_degree[v] += 1
                        q = deque([i for i in range(n) if in_degree[i] == 0])
                        order = []
                        while q:
                            curr = q.popleft()
                            order.append(curr)
                            for nxt in adj[curr]:
                                in_degree[nxt] -= 1
                                if in_degree[nxt] == 0:
                                    q.append(nxt)
                        return order if len(order) == n else []

                if __name__ == "__main__":
                    print("Topological order:", Solution.topological_order(3, [[0, 1], [1, 2]])) # [0, 1, 2]
                """);

        put("disjoint set union by rank",
                """
                class DSU:
                    def __init__(self, n: int):
                        self.parent = list(range(n))
                        self.rank = [0] * n

                    def find(self, x: int) -> int:
                        if self.parent[x] != x:
                            self.parent[x] = self.find(self.parent[x])
                        return self.parent[x]

                    def union(self, x: int, y: int) -> bool:
                        rx, ry = self.find(x), self.find(y)
                        if rx == ry:
                            return False
                        if self.rank[rx] < self.rank[ry]:
                            self.parent[rx] = ry
                        elif self.rank[ry] < self.rank[rx]:
                            self.parent[ry] = rx
                        else:
                            self.parent[ry] = rx
                            self.rank[rx] += 1
                        return True

                if __name__ == "__main__":
                    dsu = DSU(5)
                    dsu.union(0, 1)
                    dsu.union(1, 2)
                    print("Union 0-2 connected:", dsu.find(0) == dsu.find(2)) # True
                """);

        put("kadane subarray variation",
                """
                class Solution:
                    @staticmethod
                    def optimal_subarray(nums: list[int]) -> int:
                        curr = best = nums[0]
                        for x in nums[1:]:
                            curr = max(x, curr + x)
                            best = max(best, curr)
                        return best

                if __name__ == "__main__":
                    print("Optimal Subarray:", Solution.optimal_subarray([-2, 1, -3, 4, -1, 2, 1, -5, 4])) # 6
                """);

        put("divide and conquer recursion",
                """
                class Solution:
                    @staticmethod
                    def merge_sort(arr: list[int]) -> list[int]:
                        if len(arr) <= 1:
                            return arr
                        mid = len(arr) // 2
                        left = Solution.merge_sort(arr[:mid])
                        right = Solution.merge_sort(arr[mid:])
                        merged = []
                        i = j = 0
                        while i < len(left) and j < len(right):
                            if left[i] <= right[j]:
                                merged.append(left[i])
                                i += 1
                            else:
                                merged.append(right[j])
                                j += 1
                        merged.extend(left[i:])
                        merged.extend(right[j:])
                        return merged

                if __name__ == "__main__":
                    print("Sorted:", Solution.merge_sort([5, 2, 4, 1, 3])) # [1, 2, 3, 4, 5]
                """);

        put("memoized dynamic state transition",
                """
                class Solution:
                    @staticmethod
                    def solve(n: int, memo=None) -> int:
                        if memo is None:
                            memo = {}
                        if n <= 0:
                            return 0
                        if n == 1:
                            return 1
                        if n in memo:
                            return memo[n]
                        memo[n] = Solution.solve(n - 1, memo) + Solution.solve(n - 2, memo)
                        return memo[n]

                if __name__ == "__main__":
                    print("Memoized state (n=10):", Solution.solve(10)) # 55
                """);

        put("depth-first exhaustive search",
                """
                class Solution:
                    @staticmethod
                    def dfs_subsets(nums: list[int]) -> list[list[int]]:
                        res = []
                        curr = []
                        def dfs(idx: int):
                            res.append(list(curr))
                            for i in range(idx, len(nums)):
                                curr.append(nums[i])
                                dfs(i + 1)
                                curr.pop()
                        dfs(0)
                        return res

                if __name__ == "__main__":
                    print("Subsets count:", len(Solution.dfs_subsets([1, 2, 3]))) # 8
                """);

        put("greedy interval partitioning",
                """
                class Solution:
                    @staticmethod
                    def schedule_intervals(intervals: list[list[int]]) -> int:
                        if not intervals:
                            return 0
                        intervals.sort(key=lambda x: x[1])
                        count = 1
                        end = intervals[0][1]
                        for i in range(1, len(intervals)):
                            if intervals[i][0] >= end:
                                count += 1
                                end = intervals[i][1]
                        return count

                if __name__ == "__main__":
                    ivs = [[1, 3], [2, 4], [3, 5], [4, 6]]
                    print("Non-overlapping intervals:", Solution.schedule_intervals(ivs)) # 2
                """);

        put("bitwise parity and masking",
                """
                class Solution:
                    @staticmethod
                    def count_set_bits(n: int) -> int:
                        count = 0
                        while n > 0:
                            n &= (n - 1)
                            count += 1
                        return count

                if __name__ == "__main__":
                    print("Set bits in 29 (11101):", Solution.count_set_bits(29)) # 4
                """);

        put("binary search over monotonic predicate",
                """
                class Solution:
                    @staticmethod
                    def search_answer(low: int, high: int) -> int:
                        def is_valid(k: int) -> bool:
                            return k >= 42
                        while low < high:
                            mid = low + (high - low) // 2
                            if is_valid(mid):
                                high = mid
                            else:
                                low = mid + 1
                        return low

                if __name__ == "__main__":
                    print("Optimal threshold:", Solution.search_answer(1, 100)) # 42
                """);

        put("shortest path bfs exploration",
                """
                from collections import deque

                class Solution:
                    @staticmethod
                    def shortest_path(n: int, adj: list[list[int]], start: int, end: int) -> int:
                        q = deque([(start, 0)])
                        visited = {start}
                        while q:
                            u, dist = q.popleft()
                            if u == end:
                                return dist
                            for v in adj[u]:
                                if v not in visited:
                                    visited.add(v)
                                    q.append((v, dist + 1))
                        return -1

                if __name__ == "__main__":
                    adj = [[1, 2], [0, 3], [0, 3], [1, 2]]
                    print("Shortest path 0->3:", Solution.shortest_path(4, adj, 0, 3)) # 2
                """);
    }

    // =========================================================================
    // 3. TOP LEETCODE INTERVIEW PROBLEMS (PYTHON 3)
    // =========================================================================
    private static void registerLeetCodePython() {
        put("two sum",
                """
                class Solution:
                    @staticmethod
                    def two_sum(nums: list[int], target: int) -> list[int]:
                        seen = {}
                        for i, num in enumerate(nums):
                            comp = target - num
                            if comp in seen:
                                return [seen[comp], i]
                            seen[num] = i
                        return []

                if __name__ == "__main__":
                    print("Two Sum:", Solution.two_sum([2, 7, 11, 15], 9)) # [0, 1]
                """);

        put("buy and sell stock",
                """
                class Solution:
                    @staticmethod
                    def max_profit(prices: list[int]) -> int:
                        min_price = float('inf')
                        max_profit = 0
                        for p in prices:
                            if p < min_price:
                                min_price = p
                            elif p - min_price > max_profit:
                                max_profit = p - min_price
                        return max_profit

                if __name__ == "__main__":
                    print("Max profit:", Solution.max_profit([7, 1, 5, 3, 6, 4])) # 5
                """);

        put("contains duplicate",
                """
                class Solution:
                    @staticmethod
                    def contains_duplicate(nums: list[int]) -> bool:
                        return len(nums) != len(set(nums))

                if __name__ == "__main__":
                    print(Solution.contains_duplicate([1, 2, 3, 1])) # True
                """);

        put("product of array except self",
                """
                class Solution:
                    @staticmethod
                    def product_except_self(nums: list[int]) -> list[int]:
                        n = len(nums)
                        res = [1] * n
                        for i in range(1, n):
                            res[i] = res[i - 1] * nums[i - 1]
                        right = 1
                        for i in range(n - 1, -1, -1):
                            res[i] *= right
                            right *= nums[i]
                        return res

                if __name__ == "__main__":
                    print(Solution.product_except_self([1, 2, 3, 4])) # [24, 12, 8, 6]
                """);

        put("maximum subarray",
                """
                class Solution:
                    @staticmethod
                    def max_sub_array(nums: list[int]) -> int:
                        curr = best = nums[0]
                        for x in nums[1:]:
                            curr = max(x, curr + x)
                            best = max(best, curr)
                        return best

                if __name__ == "__main__":
                    print("Max subarray:", Solution.max_sub_array([-2, 1, -3, 4, -1, 2, 1, -5, 4])) # 6
                """);

        put("subarray sum equals k",
                """
                from collections import defaultdict

                class Solution:
                    @staticmethod
                    def subarray_sum(nums: list[int], k: int) -> int:
                        count = 0
                        curr = 0
                        prefix = defaultdict(int)
                        prefix[0] = 1
                        for x in nums:
                            curr += x
                            if (curr - k) in prefix:
                                count += prefix[curr - k]
                            prefix[curr] += 1
                        return count

                if __name__ == "__main__":
                    print("Subarrays:", Solution.subarray_sum([1, 1, 1], 2)) # 2
                """);

        put("sort colors",
                """
                class Solution:
                    @staticmethod
                    def sort_colors(nums: list[int]) -> None:
                        low, mid, high = 0, 0, len(nums) - 1
                        while mid <= high:
                            if nums[mid] == 0:
                                nums[low], nums[mid] = nums[mid], nums[low]
                                low += 1
                                mid += 1
                            elif nums[mid] == 1:
                                mid += 1
                            else:
                                nums[mid], nums[high] = nums[high], nums[mid]
                                high -= 1

                if __name__ == "__main__":
                    arr = [2, 0, 2, 1, 1, 0]
                    Solution.sort_colors(arr)
                    print(arr) # [0, 0, 1, 1, 2, 2]
                """);

        put("majority element",
                """
                class Solution:
                    @staticmethod
                    def majority_element(nums: list[int]) -> int:
                        candidate = nums[0]
                        count = 0
                        for n in nums:
                            if count == 0:
                                candidate = n
                            count += (1 if n == candidate else -1)
                        return candidate

                if __name__ == "__main__":
                    print("Majority:", Solution.majority_element([3, 2, 3])) # 3
                """);

        put("merge sorted array",
                """
                class Solution:
                    @staticmethod
                    def merge(nums1: list[int], m: int, nums2: list[int], n: int) -> None:
                        p1, p2, p = m - 1, n - 1, m + n - 1
                        while p2 >= 0:
                            if p1 >= 0 and nums1[p1] > nums2[p2]:
                                nums1[p] = nums1[p1]
                                p1 -= 1
                            else:
                                nums1[p] = nums2[p2]
                                p2 -= 1
                            p -= 1

                if __name__ == "__main__":
                    a = [1, 2, 3, 0, 0, 0]
                    b = [2, 5, 6]
                    Solution.merge(a, 3, b, 3)
                    print("Merged:", a) # [1, 2, 2, 3, 5, 6]
                """);

        put("lru cache",
                """
                from collections import OrderedDict

                class LRUCache:
                    def __init__(self, capacity: int):
                        self.capacity = capacity
                        self.cache = OrderedDict()

                    def get(self, key: int) -> int:
                        if key not in self.cache:
                            return -1
                        self.cache.move_to_end(key)
                        return self.cache[key]

                    def put(self, key: int, value: int) -> None:
                        if key in self.cache:
                            self.cache.move_to_end(key)
                        self.cache[key] = value
                        if len(self.cache) > self.capacity:
                            self.cache.popitem(last=False)

                if __name__ == "__main__":
                    lru = LRUCache(2)
                    lru.put(1, 1)
                    lru.put(2, 2)
                    print("LRU get(1):", lru.get(1)) # 1
                """);

        put("valid palindrome",
                """
                class Solution:
                    @staticmethod
                    def is_palindrome(s: str) -> bool:
                        filtered = [c.lower() for c in s if c.isalnum()]
                        return filtered == filtered[::-1]

                if __name__ == "__main__":
                    print(Solution.is_palindrome("A man, a plan, a canal: Panama")) # True
                """);

        put("3sum",
                """
                class Solution:
                    @staticmethod
                    def three_sum(nums: list[int]) -> list[list[int]]:
                        nums.sort()
                        res = []
                        for i in range(len(nums) - 2):
                            if i > 0 and nums[i] == nums[i - 1]:
                                continue
                            l, r = i + 1, len(nums) - 1
                            while l < r:
                                s = nums[i] + nums[l] + nums[r]
                                if s == 0:
                                    res.append([nums[i], nums[l], nums[r]])
                                    while l < r and nums[l] == nums[l + 1]:
                                        l += 1
                                    while l < r and nums[r] == nums[r - 1]:
                                        r -= 1
                                    l += 1
                                    r -= 1
                                elif s < 0:
                                    l += 1
                                else:
                                    r -= 1
                        return res

                if __name__ == "__main__":
                    print("3Sum:", Solution.three_sum([-1, 0, 1, 2, -1, -4]))
                """);

        put("container with most water",
                """
                class Solution:
                    @staticmethod
                    def max_area(height: list[int]) -> int:
                        l, r = 0, len(height) - 1
                        max_w = 0
                        while l < r:
                            h = min(height[l], height[r])
                            max_w = max(max_w, h * (r - l))
                            if height[l] < height[r]:
                                l += 1
                            else:
                                r -= 1
                        return max_w

                if __name__ == "__main__":
                    print("Max area:", Solution.max_area([1, 8, 6, 2, 5, 4, 8, 3, 7])) # 49
                """);

        put("valid parentheses",
                """
                class Solution:
                    @staticmethod
                    def is_valid(s: str) -> bool:
                        stack = []
                        mapping = {')': '(', '}': '{', ']': '['}
                        for c in s:
                            if c in mapping:
                                if not stack or stack.pop() != mapping[c]:
                                    return False
                            else:
                                stack.append(c)
                        return not stack

                if __name__ == "__main__":
                    print(Solution.is_valid("()[]{}")) # True
                """);

        put("reverse linked list",
                """
                class ListNode:
                    def __init__(self, val=0, next=None):
                        self.val = val
                        self.next = next

                class Solution:
                    @staticmethod
                    def reverse_list(head: ListNode) -> ListNode:
                        prev = None
                        curr = head
                        while curr:
                            nxt = curr.next
                            curr.next = prev
                            prev = curr
                            curr = nxt
                        return prev

                if __name__ == "__main__":
                    node = ListNode(1, ListNode(2, ListNode(3)))
                    rev = Solution.reverse_list(node)
                    print("Reversed head:", rev.val) # 3
                """);

        put("climbing stairs",
                """
                class Solution:
                    @staticmethod
                    def climb_stairs(n: int) -> int:
                        if n <= 2:
                            return n
                        a, b = 1, 2
                        for _ in range(3, n + 1):
                            a, b = b, a + b
                        return b

                if __name__ == "__main__":
                    print("Ways to climb 5:", Solution.climb_stairs(5)) # 8
                """);

        put("house robber",
                """
                class Solution:
                    @staticmethod
                    def rob(nums: list[int]) -> int:
                        rob1 = rob2 = 0
                        for n in nums:
                            rob1, rob2 = rob2, max(rob1 + n, rob2)
                        return rob2

                if __name__ == "__main__":
                    print("Max robbery:", Solution.rob([1, 2, 3, 1])) # 4
                """);

        put("coin change",
                """
                class Solution:
                    @staticmethod
                    def coin_change(coins: list[int], amount: int) -> int:
                        dp = [float('inf')] * (amount + 1)
                        dp[0] = 0
                        for a in range(1, amount + 1):
                            for c in coins:
                                if a - c >= 0:
                                    dp[a] = min(dp[a], 1 + dp[a - c])
                        return dp[amount] if dp[amount] != float('inf') else -1

                if __name__ == "__main__":
                    print("Fewest coins:", Solution.coin_change([1, 2, 5], 11)) # 3
                """);

        put("number of islands",
                """
                class Solution:
                    @staticmethod
                    def num_islands(grid: list[list[str]]) -> int:
                        if not grid:
                            return 0
                        rows, cols = len(grid), len(grid[0])
                        count = 0
                        def dfs(r, c):
                            if r < 0 or r >= rows or c < 0 or c >= cols or grid[r][c] != '1':
                                return
                            grid[r][c] = '0'
                            dfs(r + 1, c)
                            dfs(r - 1, c)
                            dfs(r, c + 1)
                            dfs(r, c - 1)
                        for r in range(rows):
                            for c in range(cols):
                                if grid[r][c] == '1':
                                    count += 1
                                    dfs(r, c)
                        return count

                if __name__ == "__main__":
                    g = [["1","1","0"],["1","1","0"],["0","0","1"]]
                    print("Islands:", Solution.num_islands(g)) # 2
                """);
    }

    // =========================================================================
    // 4. INTELLIGENT JAVA-TO-PYTHON ALGORITHMIC TRANSLATOR (Universal Fallback)
    // =========================================================================
    public static String convertJavaToPython(String javaCode) {
        if (javaCode == null || javaCode.isBlank()) {
            return """
                    class Solution:
                        @staticmethod
                        def solve():
                            pass
                    """;
        }

        // Clean and prepare
        String[] lines = javaCode.split("\n");
        StringBuilder py = new StringBuilder();
        py.append("# Translated Python 3 Solution\n");
        py.append("class Solution:\n");

        int indent = 4;
        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.isEmpty() || line.startsWith("package ") || line.startsWith("import java") || line.equals("{") || line.equals("}")) {
                continue;
            }
            if (line.contains("public class Solution") || line.contains("class Solution {")) {
                continue;
            }

            // Convert method declarations
            if (line.contains("public static ") || line.contains("private static ") || line.contains("public ")) {
                if (line.contains("main(String[]")) {
                    continue; // Skip main in class body, handle at bottom
                }
                String methodHeader = line
                        .replace("public static ", "def ")
                        .replace("private static ", "def ")
                        .replace("public ", "def ")
                        .replaceAll("\\b(void|int\\[\\]|int|String|boolean|double|long|List<[^>]+>|Map<[^>]+>)\\s+", "");
                if (methodHeader.endsWith("{")) {
                    methodHeader = methodHeader.substring(0, methodHeader.length() - 1).trim();
                }
                py.append("    @staticmethod\n");
                py.append("    ").append(cleanTypes(methodHeader)).append(":\n");
                indent = 8;
                continue;
            }

            // Clean code line
            String clean = line;
            if (clean.endsWith(";")) clean = clean.substring(0, clean.length() - 1);
            if (clean.endsWith("{")) clean = clean.substring(0, clean.length() - 1) + ":";

            clean = clean
                    .replace("System.out.println", "print")
                    .replace("System.out.print", "print")
                    .replace("&&", "and")
                    .replace("||", "or")
                    .replace("true", "True")
                    .replace("false", "False")
                    .replace("null", "None")
                    .replaceAll("\\bint\\s+", "")
                    .replaceAll("\\bboolean\\s+", "")
                    .replaceAll("\\bString\\s+", "")
                    .replaceAll("\\bdouble\\s+", "")
                    .replaceAll("\\bMap<[^>]+>\\s+", "")
                    .replaceAll("\\bList<[^>]+>\\s+", "")
                    .replaceAll("\\bnew int\\[\\]\\s*", "")
                    .replaceAll("\\bnew ArrayList<>[^;]*", "[]")
                    .replaceAll("\\bnew HashMap<>[^;]*", "{}");

            // Format loop
            if (clean.startsWith("for (") || clean.startsWith("for(")) {
                clean = clean.replace("for (", "for ").replace("for(", "for ");
                if (clean.endsWith(")")) clean = clean.substring(0, clean.length() - 1) + ":";
            }
            if (clean.startsWith("if (") || clean.startsWith("if(")) {
                clean = clean.replace("if (", "if ").replace("if(", "if ");
                if (clean.endsWith(")")) clean = clean.substring(0, clean.length() - 1) + ":";
            }
            if (clean.startsWith("while (") || clean.startsWith("while(")) {
                clean = clean.replace("while (", "while ").replace("while(", "while ");
                if (clean.endsWith(")")) clean = clean.substring(0, clean.length() - 1) + ":";
            }

            if (clean.equals("}")) {
                indent = Math.max(4, indent - 4);
                continue;
            }

            py.append(" ".repeat(indent)).append(clean).append("\n");
        }

        return py.toString();
    }

    private static String cleanTypes(String header) {
        return header
                .replaceAll("\\bint\\[\\]\\s+", "")
                .replaceAll("\\bint\\s+", "")
                .replaceAll("\\bString\\s+", "")
                .replaceAll("\\bboolean\\s+", "")
                .replaceAll("\\bdouble\\s+", "");
    }
}
