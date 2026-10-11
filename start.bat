@echo off
title Adaptive AI Coding Mentor
cd /d "%~dp0"

echo ==================================================================
echo   Adaptive AI Coding Mentor - 1-Click Startup Script
echo ==================================================================
echo.

REM 1. Check MySQL
echo [1/3] Checking MySQL Server...
"C:\xampp\mysql\bin\mysqladmin.exe" -u root ping >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [!] MySQL is not currently running.
    echo [*] Starting MySQL via XAMPP...
    if exist "C:\xampp\mysql_start.bat" (
        start "" /b "C:\xampp\mysql_start.bat"
        timeout /t 3 /nobreak >nul
    ) else (
        echo [!] Please open XAMPP Control Panel and start MySQL!
        pause
    )
) else (
    echo [+] MySQL Server is active and reachable.
)

REM 2. Check and initialize Database if needed
echo.
echo [2/3] Verifying Database Schema and Seed Data...
"C:\xampp\mysql\bin\mysql.exe" -u root -e "USE ai_coding_mentor; SELECT COUNT(*) FROM users;" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [*] Importing database/schema.sql...
    "C:\xampp\mysql\bin\mysql.exe" -u root < database\schema.sql
    echo [*] Importing database/seed.sql...
    "C:\xampp\mysql\bin\mysql.exe" -u root < database\seed.sql
    echo [+] Database ai_coding_mentor initialized successfully!
) else (
    echo [+] Database ai_coding_mentor is already populated and ready.
)

REM 3. Start the Web Server
echo.
echo [3/3] Starting Embedded Tomcat 10 Server...
echo [*] Application URL: http://localhost:8080
echo [*] Admin Panel:    http://localhost:8080/admin/dashboard
echo [*] React IDE:       http://localhost:8080/app/
echo.
echo Demo Accounts:
echo   - Alice (Beginner):     alice@student.com  / password123
echo   - Bob (Intermediate):   bob@student.com    / password123
echo   - Admin (Faculty):      admin@aimentor.com / password123
echo.
echo Press Ctrl+C at any time to stop the server.
echo.

call mvn exec:java
pause
