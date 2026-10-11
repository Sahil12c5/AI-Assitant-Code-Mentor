<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Faculty & Admin Console — Adaptive AI Coding Mentor</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=2.1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body class="dashboard-page">
    <!-- Navbar -->
    <header class="navbar">
        <div class="nav-container">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-brand">
                <i class="fa-solid fa-brain nav-brand-icon"></i>
                <span>Adaptive <span class="highlight">Admin Console</span></span>
            </a>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link active">
                    <i class="fa-solid fa-chart-line mr-1"></i> Telemetry
                </a>
                <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">
                    <i class="fa-solid fa-graduation-cap mr-1"></i> Student View
                </a>
                <a href="${pageContext.request.contextPath}/app/" class="nav-link">
                    <i class="fa-solid fa-code mr-1"></i> Workspace
                </a>
                <div class="user-pill ml-3">
                    <span class="user-avatar" style="background: rgba(245, 158, 11, 0.2); color: #f59e0b;">
                        <i class="fa-solid fa-shield-halved"></i>
                    </span>
                    <span class="user-name"><c:out value="${sessionScope.userName}" /></span>
                    <span class="badge badge-warning badge-sm">ADMIN</span>
                </div>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm ml-2">
                    <i class="fa-solid fa-arrow-right-from-bracket"></i>
                </a>
            </div>
        </div>
    </header>

    <main class="dashboard-main py-6">
        <div class="container">
            <!-- Header -->
            <div class="flex justify-between items-center mb-6">
                <div>
                    <div class="badge badge-accent mb-2">Faculty & Pedagogical Supervisor Dashboard</div>
                    <h1 class="text-3xl font-bold">Platform-Wide Learning Telemetry</h1>
                    <p class="text-secondary mt-1">Aggregated insights across all student cohort submissions and error frequencies</p>
                </div>
            </div>

            <!-- Stats Overview -->
            <div class="stats-overview-grid mb-6">
                <div class="stat-card">
                    <div class="stat-icon-wrapper" style="background: rgba(99, 102, 241, 0.15); color: #818cf8;">
                        <i class="fa-solid fa-users"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value"><c:out value="${totalStudents}" /></div>
                        <div class="stat-label">Enrolled Students</div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon-wrapper" style="background: rgba(6, 182, 212, 0.15); color: #06b6d4;">
                        <i class="fa-solid fa-code-commit"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value"><c:out value="${totalSubmissions}" /></div>
                        <div class="stat-label">Total Code Submissions</div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon-wrapper" style="background: rgba(16, 185, 129, 0.15); color: #10b981;">
                        <i class="fa-solid fa-shield-halved"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">ProcessBuilder</div>
                        <div class="stat-label">Isolated Execution Sandbox</div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon-wrapper" style="background: rgba(245, 158, 11, 0.15); color: #f59e0b;">
                        <i class="fa-solid fa-robot"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">Gemini + Mock</div>
                        <div class="stat-label">Dual Engine Scaffolding</div>
                    </div>
                </div>
            </div>

            <!-- Two-Column Grid: Top Mistakes + Recent Submissions Velocity -->
            <div class="dashboard-two-col-grid mb-6">
                <!-- Top Cohort Mistakes -->
                <div class="card">
                    <div class="card-header mb-4">
                        <h2 class="card-title text-lg font-bold">
                            <i class="fa-solid fa-triangle-exclamation text-danger mr-2"></i> Top Cohort Mistake Patterns
                        </h2>
                        <p class="text-sm text-secondary">Identifies systemic misconceptions across all students to guide classroom lectures</p>
                    </div>

                    <div class="table-container">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Mistake Category</th>
                                    <th>Topic</th>
                                    <th>Frequency</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty topMistakes}">
                                        <tr>
                                            <td colspan="3" class="text-center text-muted py-4">No mistake telemetry recorded yet.</td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="m" items="${topMistakes}">
                                            <tr>
                                                <td class="font-medium text-primary"><c:out value="${m[0]}" /></td>
                                                <td><span class="badge badge-secondary"><c:out value="${m[1]}" /></span></td>
                                                <td class="text-accent font-semibold"><c:out value="${m[2]}" /> occurrences</td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Daily Submission Velocity -->
                <div class="card">
                    <div class="card-header mb-4">
                        <h2 class="card-title text-lg font-bold">
                            <i class="fa-solid fa-calendar-days text-accent mr-2"></i> 7-Day Activity Velocity
                        </h2>
                        <p class="text-sm text-secondary">Daily submission volumes tracking student engagement</p>
                    </div>

                    <div class="table-container">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Submissions Count</th>
                                    <th>Activity Bar</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty submissionsPerDay}">
                                        <tr>
                                            <td colspan="3" class="text-center text-muted py-4">No recent activity logged in 7 days.</td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="day" items="${submissionsPerDay}">
                                            <tr>
                                                <td class="font-mono text-sm"><c:out value="${day[0]}" /></td>
                                                <td class="font-semibold text-primary"><c:out value="${day[1]}" /></td>
                                                <td>
                                                    <div class="progress-bar-bg" style="height: 8px;">
                                                        <div class="progress-bar-fill" style="width: ${day[1] * 10}%;"></div>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Student Cohort Directory -->
            <div class="card">
                <div class="card-header mb-4">
                    <h2 class="card-title text-lg font-bold">
                        <i class="fa-solid fa-user-graduate text-primary mr-2"></i> Registered Student Cohort
                    </h2>
                </div>

                <div class="table-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Student ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Calibrated Skill Level</th>
                                <th>Registered Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${students}">
                                <tr>
                                    <td class="font-mono text-muted">#<c:out value="${s.id}" /></td>
                                    <td class="font-semibold text-primary"><c:out value="${s.name}" /></td>
                                    <td class="text-secondary"><c:out value="${s.email}" /></td>
                                    <td>
                                        <span class="badge badge-accent"><c:out value="${s.level}" /></span>
                                    </td>
                                    <td class="text-muted text-sm"><c:out value="${s.createdAt}" /></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</body>
</html>
