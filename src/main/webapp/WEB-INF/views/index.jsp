<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Adaptive AI Coding Mentor — Intelligent Programming Tutor</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=2.1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>
    <!-- Navigation Bar -->
    <header class="navbar">
        <div class="nav-container">
            <a href="${pageContext.request.contextPath}/" class="nav-brand">
                <i class="fa-solid fa-brain nav-brand-icon"></i>
                <span>Adaptive <span class="highlight">AI Mentor</span></span>
            </a>
            <div class="nav-links">
                <a href="#innovations" class="nav-link">Innovations</a>
                <a href="#how-it-works" class="nav-link">How It Works</a>
                <a href="#features" class="nav-link">Pedagogy</a>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary btn-sm">Sign In</a>
                <a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm">Get Started</a>
            </div>
        </div>
    </header>

    <!-- Hero Section -->
    <main>
        <section class="hero-section">
            <div class="hero-container">
                <div class="badge badge-accent mb-3 animate-fade-in">
                    <i class="fa-solid fa-sparkles"></i> Final Year B.E. IT Capstone Innovation
                </div>
                <h1 class="hero-title">
                    The AI Coding Tutor That <br>
                    <span class="gradient-text">Deliberately Refuses</span> To Hand Over The Answer.
                </h1>
                <p class="hero-subtitle">
                    Unlike standard generative AI that writes solutions for you, our Adaptive Mentor guides you through 
                    Socratic questioning, 4-tier progressive scaffolding, and real-time behavioral DNA modeling.
                </p>
                <div class="hero-cta">
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-lg">
                        <i class="fa-solid fa-terminal mr-2"></i> Launch Mentor Workspace
                    </a>
                    <a href="#innovations" class="btn btn-outline btn-lg">
                        <i class="fa-solid fa-diagram-project mr-2"></i> Explore Innovations
                    </a>
                </div>

                <!-- Live Innovation Pill Grid -->
                <div class="hero-stats-grid">
                    <div class="stat-card">
                        <div class="stat-number">4-Tier</div>
                        <div class="stat-label">Progressive Hint Ladder</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number">DNA</div>
                        <div class="stat-label">Student Behavioral Profile</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number">3-Week</div>
                        <div class="stat-label">Mistake Memory & Trends</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number">Isomorphic</div>
                        <div class="stat-label">Similar-But-Different Engine</div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Socratic Loop Visual Section -->
        <section id="how-it-works" class="section bg-secondary">
            <div class="container">
                <div class="section-header text-center">
                    <h2 class="section-title">The Continuous Adaptive Learning Loop</h2>
                    <p class="section-subtitle">How pedagogy meets LLM intelligence without compromising problem-solving skill</p>
                </div>

                <div class="learning-loop-grid">
                    <div class="loop-step-card">
                        <div class="loop-step-number">1</div>
                        <div class="loop-icon"><i class="fa-solid fa-code"></i></div>
                        <h3>Student Submits Code</h3>
                        <p>Write Java or Python code in our Monaco editor. Execution runs in an isolated, secure sandbox.</p>
                    </div>
                    <div class="loop-step-connector"><i class="fa-solid fa-arrow-right"></i></div>
                    <div class="loop-step-card">
                        <div class="loop-step-number">2</div>
                        <div class="loop-icon"><i class="fa-solid fa-magnifying-glass-chart"></i></div>
                        <h3>Semantic Diagnosis</h3>
                        <p>The system detects AST bugs, edge case omissions, and anti-patterns with precise line mapping.</p>
                    </div>
                    <div class="loop-step-connector"><i class="fa-solid fa-arrow-right"></i></div>
                    <div class="loop-step-card">
                        <div class="loop-step-number">3</div>
                        <div class="loop-icon"><i class="fa-solid fa-stairs"></i></div>
                        <h3>Progressive Scaffolding</h3>
                        <p>Server-enforced 4-level hints provide Socratic clues first, never spoiling the final code.</p>
                    </div>
                    <div class="loop-step-connector"><i class="fa-solid fa-arrow-right"></i></div>
                    <div class="loop-step-card">
                        <div class="loop-step-number">4</div>
                        <div class="loop-icon"><i class="fa-solid fa-dna"></i></div>
                        <h3>DNA & Mastery Update</h3>
                        <p>Bayesian-inspired topic mastery and 3-week mistake trend update in real time.</p>
                    </div>
                </div>
            </div>
        </section>

        <!-- Core Innovations Showcase -->
        <section id="innovations" class="section">
            <div class="container">
                <div class="section-header text-center">
                    <h2 class="section-title">Four Core Architectural Innovations</h2>
                    <p class="section-subtitle">Engineered specifically to solve LLM hallucination and cognitive overreliance</p>
                </div>

                <div class="features-grid">
                    <!-- Innovation 1 -->
                    <div class="feature-card">
                        <div class="feature-icon" style="background: rgba(99, 102, 241, 0.15); color: #818cf8;">
                            <i class="fa-solid fa-layer-group"></i>
                        </div>
                        <h3>Server-Enforced 4-Level Hint Ladder</h3>
                        <p>Unlike ordinary chat UIs where the LLM immediately gives the answer:</p>
                        <ul class="feature-list">
                            <li><strong>Level 1:</strong> Guiding question pointing to the root problem zone</li>
                            <li><strong>Level 2:</strong> Core conceptual explanation and algorithmic intuition</li>
                            <li><strong>Level 3:</strong> Line-by-line breakdown of why the current logic fails</li>
                            <li><strong>Level 4:</strong> Corrected solution (strictly requires explicit confirmation)</li>
                        </ul>
                    </div>

                    <!-- Innovation 2 -->
                    <div class="feature-card">
                        <div class="feature-icon" style="background: rgba(6, 182, 212, 0.15); color: #06b6d4;">
                            <i class="fa-solid fa-dna"></i>
                        </div>
                        <h3>Student Coding DNA (Cognitive Profile)</h3>
                        <p>Personalized per student per topic (Loops, Arrays, OOP, Exceptions, Recursion, etc.):</p>
                        <ul class="feature-list">
                            <li>Dynamic mastery score (0–100) updated after every attempt</li>
                            <li>Assistance level calibration (Beginner, Intermediate, Advanced)</li>
                            <li>LLM prompt injects historical mistake frequencies for personalized response</li>
                            <li>Two students with the same error receive totally different guidance</li>
                        </ul>
                    </div>

                    <!-- Innovation 3 -->
                    <div class="feature-card">
                        <div class="feature-icon" style="background: rgba(16, 185, 129, 0.15); color: #10b981;">
                            <i class="fa-solid fa-chart-line"></i>
                        </div>
                        <h3>Mistake Memory & 3-Week Trend Engine</h3>
                        <p>Longitudinal analytics tracking how recurring anti-patterns evolve over time:</p>
                        <ul class="feature-list">
                            <li>Categorizes mistake patterns across rolling 3-week evaluation windows</li>
                            <li>Classifies trends as <span class="badge badge-success">Improving</span>, <span class="badge badge-warning">Stable</span>, or <span class="badge badge-danger">Deteriorating</span></li>
                            <li>Automatically prescribes targeted concept refreshers when errors persist</li>
                        </ul>
                    </div>

                    <!-- Innovation 4 -->
                    <div class="feature-card">
                        <div class="feature-icon" style="background: rgba(245, 158, 11, 0.15); color: #f59e0b;">
                            <i class="fa-solid fa-shuffle"></i>
                        </div>
                        <h3>"Similar but Different" Problem Synthesis</h3>
                        <p>Isomorphic problem generation targeting specific conceptual weaknesses:</p>
                        <ul class="feature-list">
                            <li>Extracts underlying algorithmic pattern (e.g. two-pointer, recursion)</li>
                            <li>Synthesizes a fresh scenario and novel test cases on the fly</li>
                            <li>Verifies mastery transfer before allowing the student to advance</li>
                        </ul>
                    </div>
                </div>
            </div>
        </section>

        <!-- Comparison Table: ChatGPT vs Adaptive AI Mentor -->
        <section id="features" class="section bg-secondary">
            <div class="container">
                <div class="section-header text-center">
                    <h2 class="section-title">The Pedagogical Difference</h2>
                    <p class="section-subtitle">Why traditional LLM chatbots fail computer science education</p>
                </div>

                <div class="table-container">
                    <table class="comparison-table">
                        <thead>
                            <tr>
                                <th>Pedagogical Dimension</th>
                                <th>Standard ChatGPT / Copilot</th>
                                <th>Adaptive AI Coding Mentor</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><strong>Solution Delivery</strong></td>
                                <td class="text-danger"><i class="fa-solid fa-circle-xmark mr-1"></i> Immediate spoon-feeding; bypasses cognitive struggle</td>
                                <td class="text-success"><i class="fa-solid fa-circle-check mr-1"></i> 4-Level Socratic scaffolding; solution locked by default</td>
                            </tr>
                            <tr>
                                <td><strong>Student Memory</strong></td>
                                <td class="text-danger"><i class="fa-solid fa-circle-xmark mr-1"></i> Ephemeral chat session; resets every prompt</td>
                                <td class="text-success"><i class="fa-solid fa-circle-check mr-1"></i> Persistent Student DNA & 3-week longitudinal mistake tracking</td>
                            </tr>
                            <tr>
                                <td><strong>Code Verification</strong></td>
                                <td class="text-danger"><i class="fa-solid fa-circle-xmark mr-1"></i> Plausible-sounding text hallucination without sandbox</td>
                                <td class="text-success"><i class="fa-solid fa-circle-check mr-1"></i> Sandboxed ProcessBuilder compilation & test validation</td>
                            </tr>
                            <tr>
                                <td><strong>Adaptive Practice</strong></td>
                                <td class="text-danger"><i class="fa-solid fa-circle-xmark mr-1"></i> Generic, uncalibrated question dumps</td>
                                <td class="text-success"><i class="fa-solid fa-circle-check mr-1"></i> Dynamic difficulty scaling & isomorphic problem synthesis</td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <!-- Call to Action Banner -->
                <div class="cta-banner mt-5">
                    <div class="cta-content">
                        <h2>Ready to Experience Adaptive AI Mentorship?</h2>
                        <p>Sign in with one of our pre-configured demo student accounts or create a new student profile.</p>
                    </div>
                    <div class="cta-actions">
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-lg">
                            <i class="fa-solid fa-arrow-right-to-bracket mr-2"></i> Enter Platform
                        </a>
                    </div>
                </div>
            </div>
        </section>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container footer-content">
            <div class="footer-left">
                <div class="nav-brand">
                    <i class="fa-solid fa-brain"></i>
                    <span>Adaptive AI Coding Mentor</span>
                </div>
                <p class="text-muted mt-1">Final-Year B.E. Information Technology Capstone Project</p>
            </div>
            <div class="footer-right text-muted">
                <span>Architecture: Jakarta EE + React + Gemini LLM + MySQL</span>
            </div>
        </div>
    </footer>
</body>
</html>
