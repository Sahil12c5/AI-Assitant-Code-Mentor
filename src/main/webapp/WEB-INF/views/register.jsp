<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Student Profile — Adaptive AI Coding Mentor</title>
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
            <h1 class="auth-title">Create Student Profile</h1>
            <p class="auth-subtitle">Initialize your personalized AI Coding DNA profile</p>
        </div>

        <!-- Auth Card -->
        <div class="card auth-card shadow-xl">
            <c:if test="${not empty error}">
                <div class="alert alert-danger mb-4">
                    <i class="fa-solid fa-circle-exclamation mr-2"></i>
                    <span><c:out value="${error}" /></span>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="POST" class="auth-form" id="registerForm">
                <div class="form-group mb-3">
                    <label for="name" class="form-label">Full Name</label>
                    <div class="input-icon-wrapper">
                        <i class="fa-solid fa-user input-icon"></i>
                        <input type="text" id="name" name="name" class="form-control" 
                               placeholder="e.g. John Doe" value="<c:out value='${name}'/>" required autofocus>
                    </div>
                </div>

                <div class="form-group mb-3">
                    <label for="email" class="form-label">Email Address</label>
                    <div class="input-icon-wrapper">
                        <i class="fa-solid fa-envelope input-icon"></i>
                        <input type="email" id="email" name="email" class="form-control" 
                               placeholder="student@example.com" value="<c:out value='${email}'/>" required>
                    </div>
                </div>

                <div class="form-group mb-3">
                    <label for="password" class="form-label">Password</label>
                    <div class="input-icon-wrapper">
                        <i class="fa-solid fa-lock input-icon"></i>
                        <input type="password" id="password" name="password" class="form-control" 
                               placeholder="Minimum 6 characters" minlength="6" required>
                    </div>
                </div>

                <div class="form-group mb-4">
                    <label for="confirmPassword" class="form-label">Confirm Password</label>
                    <div class="input-icon-wrapper">
                        <i class="fa-solid fa-shield-check input-icon"></i>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" 
                               placeholder="Re-enter your password" minlength="6" required>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary btn-block btn-lg mb-3">
                    <i class="fa-solid fa-user-plus mr-2"></i> Register & Initialize DNA
                </button>
            </form>

            <div class="auth-footer text-center mt-4">
                <p class="text-secondary text-sm">
                    Already have an account? 
                    <a href="${pageContext.request.contextPath}/login" class="text-accent font-medium hover:underline">
                        Sign In
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
</body>
</html>
