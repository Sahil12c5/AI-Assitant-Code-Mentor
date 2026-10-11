<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In — Adaptive AI Coding Mentor</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=2.1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body class="auth-page">
    <div class="auth-container">
        <!-- Brand Header -->
        <div class="auth-header text-center">
            <a href="${pageContext.request.contextPath}/" class="nav-brand inline-flex items-center justify-center mb-2">
                <i class="fa-solid fa-brain nav-brand-icon"></i>
                <span>Adaptive <span class="highlight">AI Mentor</span></span>
            </a>
            <h1 class="auth-title">Welcome Back</h1>
            <p class="auth-subtitle">Sign in to resume your adaptive programming journey</p>
        </div>

        <!-- Auth Card -->
        <div class="card auth-card shadow-xl">
            <c:if test="${not empty error}">
                <div class="alert alert-danger mb-4">
                    <i class="fa-solid fa-circle-exclamation mr-2"></i>
                    <span><c:out value="${error}" /></span>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="POST" class="auth-form" id="loginForm">
                <div class="form-group mb-3">
                    <label for="email" class="form-label">Email Address</label>
                    <div class="input-icon-wrapper">
                        <i class="fa-solid fa-envelope input-icon"></i>
                        <input type="email" id="email" name="email" class="form-control" 
                               placeholder="student@example.com" value="<c:out value='${email}'/>" required autofocus>
                    </div>
                </div>

                <div class="form-group mb-4">
                    <div class="flex justify-between items-center mb-1">
                        <label for="password" class="form-label mb-0">Password</label>
                    </div>
                    <div class="input-icon-wrapper">
                        <i class="fa-solid fa-lock input-icon"></i>
                        <input type="password" id="password" name="password" class="form-control" 
                               placeholder="••••••••" required>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary btn-block btn-lg mb-3">
                    <i class="fa-solid fa-arrow-right-to-bracket mr-2"></i> Sign In
                </button>
            </form>

            <!-- Quick Demo Credentials Box -->
            <div class="demo-accounts-box mt-4 pt-3 border-top">
                <p class="text-xs text-muted text-center uppercase tracking-wide font-semibold mb-2">
                    <i class="fa-solid fa-bolt text-warning mr-1"></i> Quick Demo Fill
                </p>
                <div class="demo-buttons-grid">
                    <button type="button" class="btn btn-secondary btn-sm" 
                            onclick="fillCredentials('alice@student.com', 'password123')">
                        <i class="fa-solid fa-user-graduate mr-1"></i> Alice (Beginner)
                    </button>
                    <button type="button" class="btn btn-secondary btn-sm" 
                            onclick="fillCredentials('bob@student.com', 'password123')">
                        <i class="fa-solid fa-user mr-1"></i> Bob (Intermediate)
                    </button>
                    <button type="button" class="btn btn-secondary btn-sm" 
                            onclick="fillCredentials('admin@aimentor.com', 'password123')">
                        <i class="fa-solid fa-shield-halved mr-1"></i> Admin
                    </button>
                </div>
            </div>

            <div class="auth-footer text-center mt-4">
                <p class="text-secondary text-sm">
                    Don't have an account? 
                    <a href="${pageContext.request.contextPath}/register" class="text-accent font-medium hover:underline">
                        Create Account
                    </a>
                </p>
                <p class="text-muted text-xs mt-2">
                    <a href="${pageContext.request.contextPath}/" class="text-muted hover:underline">
                        <i class="fa-solid fa-arrow-left mr-1"></i> Back to Homepage
                    </a>
                </p>
            </div>
        </div>
    </div>

    <script>
        function fillCredentials(email, pass) {
            document.getElementById('email').value = email;
            document.getElementById('password').value = pass;
        }
    </script>
</body>
</html>
