<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Dashboard — Adaptive AI Coding Mentor</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=2.1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body class="dashboard-page">
    <!-- Navbar -->
    <header class="navbar">
        <div class="nav-container">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-brand">
                <i class="fa-solid fa-brain nav-brand-icon"></i>
                <span>Adaptive <span class="highlight">AI Mentor</span></span>
            </a>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/dashboard" class="nav-link active">
                    <i class="fa-solid fa-gauge mr-1"></i> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/app/" class="nav-link">
                    <i class="fa-solid fa-code mr-1"></i> AI Workspace
                </a>
                <c:if test="${sessionScope.userRole == 'ADMIN'}">
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link text-warning">
                        <i class="fa-solid fa-shield-halved mr-1"></i> Admin Panel
                    </a>
                </c:if>
                <div class="user-pill ml-3">
                    <span class="user-avatar"><i class="fa-solid fa-user-astronaut"></i></span>
                    <span class="user-name"><c:out value="${sessionScope.userName}" /></span>
                    <span class="badge badge-accent badge-sm"><c:out value="${sessionScope.userLevel}" /></span>
                </div>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm ml-2">
                    <i class="fa-solid fa-arrow-right-from-bracket"></i>
                </a>
            </div>
        </div>
    </header>

    <!-- Main Dashboard Content -->
    <main class="dashboard-main py-6">
        <div class="container">
            <!-- Welcome Banner -->
            <div class="card welcome-banner mb-6">
                <div class="welcome-content">
                    <div class="badge badge-accent mb-2">Student DNA Activated</div>
                    <h1 class="welcome-title">
                        Welcome back, <span class="gradient-text"><c:out value="${sessionScope.userName}" /></span>!
                    </h1>
                    <p class="welcome-subtitle">
                        Your adaptive mentor is continuously learning your coding patterns. Solve problems, request 
                        progressive hints, and watch your topic mastery elevate.
                    </p>
                    <div class="welcome-actions mt-4">
                        <a href="${pageContext.request.contextPath}/app/" class="btn btn-primary btn-lg">
                            <i class="fa-solid fa-play mr-2"></i> Launch React AI Workspace
                        </a>
                        <a href="${pageContext.request.contextPath}/app/#/practice" class="btn btn-secondary btn-lg ml-3">
                            <i class="fa-solid fa-list-check mr-2"></i> Browse Problems
                        </a>
                    </div>
                </div>
                <div class="welcome-graphic">
                    <div class="dna-orbit-animation">
                        <i class="fa-solid fa-brain glow-brain"></i>
                    </div>
                </div>
            </div>

            <!-- Stats Overview Cards -->
            <div class="stats-overview-grid mb-6">
                <div class="stat-card">
                    <div class="stat-icon-wrapper" style="background: rgba(99, 102, 241, 0.15); color: #818cf8;">
                        <i class="fa-solid fa-code-commit"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value"><c:out value="${totalSubmissions}" /></div>
                        <div class="stat-label">Total Submissions</div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon-wrapper" style="background: rgba(16, 185, 129, 0.15); color: #10b981;">
                        <i class="fa-solid fa-circle-check"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value"><c:out value="${solvedProblems}" /></div>
                        <div class="stat-label">Problems Solved</div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon-wrapper" style="background: rgba(6, 182, 212, 0.15); color: #06b6d4;">
                        <i class="fa-solid fa-chart-pie"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value"><c:out value="${avgMastery}" />%</div>
                        <div class="stat-label">Average Mastery</div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon-wrapper" style="background: rgba(245, 158, 11, 0.15); color: #f59e0b;">
                        <i class="fa-solid fa-layer-group"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value"><c:out value="${sessionScope.userLevel}" /></div>
                        <div class="stat-label">DNA Calibration</div>
                    </div>
                </div>
            </div>

            <!-- Two Columns: Topic Mastery & Workspace Features -->
            <div class="dashboard-two-col-grid">
                <!-- Column 1: Topic Mastery Progress -->
                <div class="card mastery-overview-card">
                    <div class="card-header flex justify-between items-center mb-4">
                        <h2 class="card-title text-lg font-bold">
                            <i class="fa-solid fa-dna text-accent mr-2"></i> Topic Mastery (Coding DNA)
                        </h2>
                        <a href="${pageContext.request.contextPath}/app/#/profile" class="text-accent text-sm hover:underline">
                            View Full DNA &rarr;
                        </a>
                    </div>

                    <c:choose>
                        <c:when test="${empty masteryList}">
                            <div class="empty-state text-center py-6 text-muted">
                                <i class="fa-solid fa-dna fa-2x mb-2"></i>
                                <p>No topic history yet. Start solving in the workspace to construct your profile!</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="mastery-bars-list">
                                <c:forEach var="item" items="${masteryList}">
                                    <div class="mastery-item mb-3">
                                        <div class="flex justify-between items-center mb-1 text-sm">
                                            <span class="font-medium text-primary"><c:out value="${item.topic}" /></span>
                                            <span class="text-accent font-semibold"><c:out value="${item.score}" />%</span>
                                        </div>
                                        <div class="progress-bar-bg">
                                            <div class="progress-bar-fill" style="width: ${item.score}%;"></div>
                                        </div>
                                        <div class="flex justify-between items-center mt-1 text-xs text-muted">
                                            <span><c:out value="${item.successes}" /> solved / <c:out value="${item.attempts}" /> attempts</span>
                                            <span>
                                                <c:choose>
                                                    <c:when test="${item.score >= 80}"><span class="text-success">Proficient</span></c:when>
                                                    <c:when test="${item.score >= 50}"><span class="text-warning">Developing</span></c:when>
                                                    <c:otherwise><span class="text-danger">Needs Focus</span></c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Column 2: Quick Innovation Workflows -->
                <div class="card workflow-card">
                    <div class="card-header mb-4">
                        <h2 class="card-title text-lg font-bold">
                            <i class="fa-solid fa-compass text-secondary mr-2"></i> Core Modules
                        </h2>
                    </div>

                    <div class="workflow-links-grid">
                        <a href="${pageContext.request.contextPath}/app/" class="workflow-item">
                            <div class="workflow-icon" style="color: #818cf8;">
                                <i class="fa-solid fa-laptop-code"></i>
                            </div>
                            <div class="workflow-text">
                                <h3>Full Monaco IDE Workspace</h3>
                                <p>Code in Java or Python with live sandbox execution & 4-level progressive hints.</p>
                            </div>
                        </a>

                        <a href="${pageContext.request.contextPath}/app/#/profile" class="workflow-item">
                            <div class="workflow-icon" style="color: #10b981;">
                                <i class="fa-solid fa-chart-line"></i>
                            </div>
                            <div class="workflow-text">
                                <h3>Mistake Memory & Trends</h3>
                                <p>Review 3-week trend graphs (improving vs deteriorating) and recommended refreshers.</p>
                            </div>
                        </a>

                        <a href="${pageContext.request.contextPath}/app/#/practice" class="workflow-item">
                            <div class="workflow-icon" style="color: #f59e0b;">
                                <i class="fa-solid fa-arrows-split-up-and-left"></i>
                            </div>
                            <div class="workflow-text">
                                <h3>Similar-But-Different Practice</h3>
                                <p>Practice isomorphic questions generated dynamically based on your weak spots.</p>
                            </div>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container footer-content">
            <div class="footer-left">
                <span class="text-muted">Adaptive AI Coding Mentor &mdash; B.E. IT Final Year Capstone</span>
            </div>
            <div class="footer-right">
                <span class="text-muted">Logged in as: <strong class="text-primary"><c:out value="${sessionScope.userEmail}" /></strong></span>
            </div>
        </div>
    </footer>
</body>
</html>
