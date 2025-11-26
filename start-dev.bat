@echo off
echo ========================================
echo   TeamVista - Development Startup
echo ========================================
echo.

echo [1/2] Starting Backend (Spring Boot)...
echo.
start "TeamVista Backend" cmd /k "cd /d %~dp0 && mvn spring-boot:run"

timeout /t 5 /nobreak > nul

echo.
echo [2/2] Starting Frontend (React + Vite)...
echo.
start "TeamVista Frontend" cmd /k "cd /d %~dp0frontend && npm run dev"

echo.
echo ========================================
echo   TeamVista is starting up!
echo ========================================
echo.
echo Backend will be available at: http://localhost:8080
echo Frontend will be available at: http://localhost:3000
echo.
echo Press any key to open the application in your browser...
pause > nul

start http://localhost:3000

echo.
echo Both servers are running in separate windows.
echo Close those windows to stop the servers.
echo.
pause
