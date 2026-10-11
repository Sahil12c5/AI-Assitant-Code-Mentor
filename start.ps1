# Adaptive AI Coding Mentor — PowerShell Startup Script
$ErrorActionPreference = "Continue"

Write-Host "==================================================================" -ForegroundColor Cyan
Write-Host "  Adaptive AI Coding Mentor — Startup" -ForegroundColor Cyan
Write-Host "==================================================================" -ForegroundColor Cyan

# 1. Ensure MySQL is running
Write-Host "`n[1/3] Checking MySQL Server..." -ForegroundColor Yellow
$mysqlAdmin = "C:\xampp\mysql\bin\mysqladmin.exe"
$mysqlExe = "C:\xampp\mysql\bin\mysql.exe"

$pingResult = & $mysqlAdmin -u root ping 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "[!] MySQL is not running. Launching XAMPP MySQL..." -ForegroundColor DarkYellow
    if (Test-Path "C:\xampp\mysql_start.bat") {
        Start-Process "C:\xampp\mysql_start.bat" -WindowStyle Hidden
        Start-Sleep -Seconds 3
    } else {
        Write-Host "[!] Please start MySQL in XAMPP Control Panel!" -ForegroundColor Red
    }
} else {
    Write-Host "[+] MySQL is running." -ForegroundColor Green
}

# 2. Check and import database
Write-Host "`n[2/3] Checking Database Schema..." -ForegroundColor Yellow
$checkDb = & $mysqlExe -u root -e "USE ai_coding_mentor; SELECT COUNT(*) FROM users;" 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "[*] Importing schema.sql..." -ForegroundColor DarkYellow
    Get-Content "database\schema.sql" | & $mysqlExe -u root
    Write-Host "[*] Importing seed.sql..." -ForegroundColor DarkYellow
    Get-Content "database\seed.sql" | & $mysqlExe -u root
    Write-Host "[+] Database ai_coding_mentor populated successfully!" -ForegroundColor Green
} else {
    Write-Host "[+] Database ai_coding_mentor is already initialized." -ForegroundColor Green
}

# 3. Launch Web Server
Write-Host "`n[3/3] Starting Embedded Tomcat 10 Server..." -ForegroundColor Yellow
Write-Host "[*] App URL:       http://localhost:8080" -ForegroundColor Cyan
Write-Host "[*] Admin Console: http://localhost:8080/admin/dashboard" -ForegroundColor Cyan
Write-Host "[*] React IDE:     http://localhost:8080/app/" -ForegroundColor Cyan
Write-Host "`nDemo Accounts: alice@student.com / password123 | admin@aimentor.com / password123" -ForegroundColor Gray
Write-Host "(Press Ctrl+C to stop the server)`n" -ForegroundColor Gray

mvn exec:java
