# Adaptive AI Coding Mentor
### Intelligent Programming Tutor with Socratic Scaffolding & Longitudinal Learning Profiles
**Final Year B.E. Information Technology Capstone Project**

---

## 1. Executive Summary & Core Innovation

**Adaptive AI Coding Mentor** is an automated programming learning platform engineered specifically to address the pedagogical pitfalls of generative AI. 

Conventional AI tools (ChatGPT, GitHub Copilot) act as *answer machines*—they immediately generate complete solutions, short-circuiting the cognitive struggle essential for developing deep computational intuition. In contrast, our system acts as a **Socratic AI Tutor**: it deliberately refuses to reveal the solution upfront. Instead, it diagnoses semantic mistakes, maintains a longitudinal behavioral profile of the student's weaknesses, and dispenses guidance through a **server-enforced 4-level progressive hint ladder**.

### Four Core Innovations
1. **Progressive Hint Ladder (4 Tiers):**
   - **Level 1:** Socratic guiding question pointing out the suspect area or boundary condition (No code, no answers).
   - **Level 2:** Core conceptual explanation and algorithmic invariant intuition.
   - **Level 3:** Deep logic failure diagnosis explaining *why* the student's code fails.
   - **Level 4:** Complete reference solution, unlocked **only** via an explicit confirmation dialog.
   - *Server-Enforced:* The maximum hint level and authorization constraints are verified on the server in the prompt sent to the LLM—not merely hidden in the UI.
2. **Student Coding DNA Profile:**
   - Tracks a personalized mastery score (0–100) per topic (Loops, Arrays, OOP, Exceptions, Recursion, etc.) using a diminishing-returns mathematical model.
   - The LLM prompt injects the student's historical mistake frequency and calibrated assistance level (`HIGH`, `MEDIUM`, `LOW`), ensuring two students with the same error receive totally different pedagogical advice.
3. **Mistake Memory & 3-Week Trend Engine:**
   - Logs every mistake type, line number, and submission.
   - Analyzes error frequency across rolling 7-day windows to categorize competencies as `IMPROVING`, `STABLE`, or `DETERIORATING`.
   - Automatically prescribes targeted concept refreshers when errors persist.
4. **"Similar but Different" Isomorphic Problem Generator:**
   - Synthesizes fresh algorithmic challenges sharing the exact same computational invariant as a student's weak area, but with novel narratives, variables, and edge cases to verify genuine mastery transfer.

---

## 2. Technology Stack

| Layer | Technologies Used |
|---|---|
| **Backend Core** | Java 17 LTS, Jakarta EE 10 (Servlet API 6.0), JSTL 3.0 |
| **Persistence** | MySQL 8.0+, HikariCP 5.1.0 High-Performance Connection Pool |
| **Security** | BCrypt Password Hashing (`jBCrypt`), Session Fixation Protection, Input Sanitization |
| **AI / LLM Layer** | Google Gemini 2.0 Flash (`java.net.http.HttpClient`), Dual-Engine Fallback to `MockLlmClient` |
| **Code Runner Sandbox** | Sandboxed `ProcessBuilder` (Java & Python 3) with AST static security filter, 5s timeout & directory cleanup |
| **Frontend SPA** | React 18, Monaco IDE Editor (`@monaco-editor/react`), Vite 5, Lucide Icons |
| **Frontend SSR** | JSP Views (`index.jsp`, `login.jsp`, `register.jsp`, `dashboard.jsp`, `admin/dashboard.jsp`) |
| **Styling** | Vanilla CSS3 Design System with Glassmorphism, CSS Custom Properties & Responsive Flex/Grid |
| **Build & Tooling** | Apache Maven 3.8+, Node.js 18+ / npm 10+ |

---

## 3. Demo Credentials

The database is pre-seeded with sample student history, mistake trends, and starter problems:

| Role | Email Address | Password | Calibrated DNA Level | Purpose |
|---|---|---|---|---|
| **Administrator** | `admin@aimentor.com` | `password123` | ADVANCED | Platform telemetry & cohort analytics |
| **Student (Beginner)** | `alice@student.com` | `password123` | BEGINNER | Demonstrates high-scaffolding hints & beginner DNA |
| **Student (Intermediate)** | `bob@student.com` | `password123` | INTERMEDIATE | Demonstrates medium assistance & mistake trends |

*Note: The login page includes 1-Click Demo Login buttons for instant sign-in during evaluation.*

---

## 4. Setup & Installation Instructions

### Prerequisites
1. **Java Development Kit (JDK 17 or higher):** `javac -version`
2. **Apache Maven (3.8 or higher):** `mvn -v`
3. **Node.js (18 or higher) & npm:** `node -v`
4. **MySQL Server (8.0 or higher):** running on `localhost:3306`
5. **Apache Tomcat 10.1+** (for deploying the `.war` archive)

---

### Step 1: Database Initialization
Open MySQL Workbench, phpMyAdmin, or your terminal MySQL client:

```sql
-- 1. Create schema and tables
source database/schema.sql;

-- 2. Populate starter problems, demo students, and mistake telemetry
source database/seed.sql;
```

*Default database connection settings in `src/main/resources/config.properties`:*
- URL: `jdbc:mysql://localhost:3306/ai_coding_mentor`
- Username: `root`
- Password: `root` (or override via environment variables below)

---

### Step 2: Configure Environment Variables (Optional)
The application works **completely offline** with `MockLlmClient` out of the box. To connect live Google Gemini AI, set:

```bash
# Windows PowerShell
$env:LLM_API_KEY="your-gemini-api-key-here"
$env:LLM_MODEL="gemini-2.0-flash"

# Optional Database Overrides (if different from root/root)
$env:DB_USERNAME="your_mysql_user"
$env:DB_PASSWORD="your_mysql_password"
```

---

### Step 3: Build the React Frontend SPA
```bash
cd frontend
npm install
npm run build
cd ..
```
*This compiles the React SPA directly into `src/main/webapp/app/`.*

---

### Step 4: Build the Jakarta EE Web Archive (WAR)
From the project root:
```bash
mvn clean package
```
This generates `target/ai-coding-mentor-1.0-SNAPSHOT.war`.

---

### Step 5: Deployment to Apache Tomcat 10.1+
1. Copy `target/ai-coding-mentor-1.0-SNAPSHOT.war` into your Tomcat `webapps/` directory (rename to `ROOT.war` for root deployment, or keep as `ai-coding-mentor.war`).
2. Start Tomcat (`bin/startup.bat` on Windows or `bin/startup.sh` on Linux/macOS).
3. Open your browser and navigate to:
   - Root deployment: `http://localhost:8080/`
   - Context deployment: `http://localhost:8080/ai-coding-mentor/`

---

## 5. Viva Presentation & Live Demo Script (5-Minute Walkthrough)

Follow this exact walkthrough during your project demonstration for maximum impact:

1. **The Problem Statement & Landing Page (`/`):**
   - Show the landing page and highlight the comparison table: *"Unlike ChatGPT which gives students the answer immediately, our system enforces a Socratic learning loop."*
2. **Student Login (`/login`):**
   - Click the 1-click demo button for **Alice (Beginner)**.
   - Show the Student Portal Dashboard (`/dashboard`) displaying her Coding DNA (Topic Mastery bars) and solved counts.
3. **Launch Monaco IDE Workspace (`/app/`):**
   - Select the problem **"Sum of N Numbers"**.
   - Show the starter code with `return 0;`.
   - Click **"Run Code"**: Output console shows compilation and execution output from our sandboxed `ProcessBuilder`.
4. **Demonstrating Innovation A (Progressive Hint Ladder):**
   - Click **"Analyze with AI Mentor"**.
   - Point out that **Level 1** provides *only* a Socratic guiding question pointing out the accumulator pattern.
   - Click **"Need a stronger hint"** $\rightarrow$ advances to **Level 2** (conceptual explanation of accumulator and loop invariants).
   - Click **"Need a stronger hint"** $\rightarrow$ advances to **Level 3** (deep logic failure diagnosis).
   - Show that `/api/hint` is capped on the server at Level 3.
   - Click **"Show Solution"**: A confirmation modal warns that revealing the solution penalizes mastery. Confirm to reveal **Level 4** solution code.
5. **Demonstrating Innovation B & C (Student DNA & Mistake Memory):**
   - Navigate to **"Coding DNA & Trends"** tab in the navbar (`#/profile`).
   - Show the Topic Mastery meters and the **3-Week Longitudinal Mistake Trajectories table**.
   - Point to the **Deteriorating Alert Banner**: highlight how the system detected deteriorating mistake patterns and prescribed targeted concept lessons.
6. **Demonstrating Innovation D (Isomorphic Problem Synthesis):**
   - Navigate to **"Practice Catalog"** (`#/practice`).
   - Click **"Similar"** next to any problem: the AI synthesizes a novel isomorphic variation on the fly with a fresh narrative.
7. **Demonstrating Faculty Telemetry (`/admin/dashboard`):**
   - Sign in as `admin@aimentor.com`.
   - Show the cohort analytics: Top Mistake Patterns across all students and 7-day submission velocity.

---

## 6. Top 10 Viva Questions & Expert Answers

### Q1: What makes this project different from existing AI coding assistants like ChatGPT or GitHub Copilot?
**Answer:** ChatGPT and Copilot are generative completion tools designed for software engineering productivity—they write code for the user, which bypasses the learning process. Our system is designed for **pedagogy**: it actively withholds the solution, diagnoses semantic misconceptions, provides progressive scaffolding through 4 levels, and adapts its explanations based on the student's historical Coding DNA profile.

### Q2: How do you prevent the AI from revealing the answer prematurely?
**Answer:** We enforce this on the **server**, not just in the UI. When the client calls `/api/analyze` or `/api/hint`, the server evaluates the current session state stored in the database (`hint_sessions`). The prompt sent to the LLM explicitly includes strict system constraints: `AUTHORIZATION: LEVEL X OF 4`. Level 1-3 prompts strictly forbid returning code snippets. Only the dedicated `/api/solution` endpoint sends a Level 4 prompt after logging user confirmation and applying a mastery penalty.

### Q3: How is student code executed securely?
**Answer:** Code execution is managed by `CodeRunnerService`:
1. A **static lexical filter** checks for prohibited security tokens (`System.exit`, `Runtime.getRuntime`, `ProcessBuilder`, `java.io.File`, `Socket`, etc.) before compilation.
2. Code is written into an ephemeral temporary directory (`Files.createTempDirectory()`).
3. Execution runs via `ProcessBuilder` with a **hard 5-second timeout** (`waitFor(5, TimeUnit.SECONDS)`); non-terminating processes are forcibly killed (`destroyForcibly()`).
4. Output is capped at 10,000 characters to prevent memory exhaustion.
5. All temporary directories and `.class` files are deleted in a `finally` block.

### Q4: How is the Student Coding DNA modeled and updated?
**Answer:** For each student and topic, the `topic_mastery` table tracks a score from 0 to 100, attempts, successes, and a frequency map of mistake types. On a correct solution, we apply a diminishing-returns formula: $M_{new} = \min(100, M_{old} + (100 - M_{old}) \times 0.15)$. On a mistake, we apply a proportional reduction: $M_{new} = \max(10, M_{old} - M_{old} \times 0.10)$. This dynamic score calibrates the assistance level (`HIGH`, `MEDIUM`, `LOW`) injected into future AI prompts.

### Q5: What is Mistake Memory and how do you calculate 3-week trends?
**Answer:** Every error is logged in the `mistakes` table with a timestamp, type, and topic. `MistakeTrendService` aggregates these records across three rolling 7-day windows ($W_1$: Days 0–7, $W_2$: Days 7–14, $W_3$: Days 14–21). If $W_1 < W_3$, the trend is classified as `IMPROVING`. If $W_1 > W_3$, it is `DETERIORATING`. When deteriorating, the system flags the student and prescribes targeted remediation lessons.

### Q6: What is an "Isomorphic Problem" and how is it generated?
**Answer:** An isomorphic ("Similar but Different") problem tests the exact same algorithmic invariant (e.g., zero-indexed array traversal, recursion base case) as an original problem, but utilizes a completely different real-world story, variable names, and constraints. When a student struggles, `ProblemGeneratorService` prompts the LLM with the source problem and the student's mastery profile to synthesize a novel exercise, linking it via `generated_from_problem_id` to evaluate concept transfer.

### Q7: What happens if the Gemini API is down, rate-limited, or there is no internet connection during evaluation?
**Answer:** The application incorporates a **Dual-Engine LLM Architecture**. If `LLM_API_KEY` is omitted or invalid, `LlmClientFactory` automatically switches to `MockLlmClient`. The mock engine uses lexical analysis to detect common bugs (infinite loops, array index out of bounds, missing return statements) and provides authentic Socratic hints across all 4 levels, ensuring 100% offline demonstrability without external dependencies.

### Q8: How does the application prevent SQL Injection and Session Fixation attacks?
**Answer:** 
1. **SQL Injection:** 100% of database interactions across all DAOs use parameterized JDBC `PreparedStatement`. No string-concatenated SQL queries exist in the codebase.
2. **Session Fixation:** In `LoginServlet` and `LoginApiServlet`, upon successful authentication, the existing session is invalidated (`session.invalidate()`) and a fresh session is instantiated (`request.getSession(true)`), resetting the session identifier cookie.

### Q9: Why did you choose a hybrid architecture (JSP + React SPA) instead of a pure SPA or pure JSP?
**Answer:** 
- **JSP (Server-Side Rendering):** Provides instantaneous, SEO-friendly landing pages, simple login/registration flows, and an isolated administrative portal with zero client bundle overhead.
- **React SPA (Client-Side Rendering):** Delivers a responsive, desktop-grade IDE experience (Monaco editor, dynamic hint ladders, split consoles, and interactive DNA dashboards) without triggering disruptive full-page reloads during the tight coding loop.

### Q10: How could this system be scaled for production university deployment?
**Answer:**
1. **Container Isolation:** Replace local `ProcessBuilder` execution with micro-VMs or lightweight containers (Docker, gVisor, or AWS Firecracker) with memory and CPU cgroups.
2. **Asynchronous Execution:** Use RabbitMQ or Kafka to decouple submission ingestion from code compilation and LLM generation.
3. **Database Caching:** Add Redis caching for problem definitions, session data, and static DNA snapshots.
