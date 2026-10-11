<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 — Page Not Found</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body class="auth-page">
    <div class="auth-container text-center">
        <div class="card p-6 shadow-xl max-w-md mx-auto">
            <div class="text-accent mb-4">
                <i class="fa-solid fa-compass-slash fa-4x"></i>
            </div>
            <h1 class="text-4xl font-bold mb-2">404</h1>
            <h2 class="text-xl font-semibold mb-3">Resource Not Found</h2>
            <p class="text-secondary mb-6">The page or endpoint you requested does not exist or has been relocated.</p>
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                <i class="fa-solid fa-house mr-2"></i> Return to Safety
            </a>
        </div>
    </div>
</body>
</html>
