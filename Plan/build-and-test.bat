@echo off
echo ========================================
echo Team Productivity Backend - Build Test
echo ========================================
echo.

echo [1/3] Cleaning previous builds...
call mvn clean
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven clean failed!
    exit /b 1
)
echo.

echo [2/3] Compiling project...
call mvn compile
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed!
    exit /b 1
)
echo.

echo [3/3] Packaging application...
call mvn package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Packaging failed!
    exit /b 1
)
echo.

echo ========================================
echo BUILD SUCCESSFUL!
echo ========================================
echo.
echo JAR file created at: target\team-productivity-backend-1.0.0.jar
echo.
echo To run the application:
echo   java -jar target\team-productivity-backend-1.0.0.jar
echo.
echo Or use Maven:
echo   mvn spring-boot:run
echo.
