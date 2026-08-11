@echo off
setlocal
set DIR=%~dp0
set WRAPPER_JAR=%DIR%gradle\wrapper\gradle-wrapper.jar
if exist "%WRAPPER_JAR%" (
  java -jar "%WRAPPER_JAR%" %*
) else (
  where gradle >nul 2>nul
  if %ERRORLEVEL% EQU 0 (
    echo Note: gradle-wrapper.jar not present; using system Gradle instead.
    gradle %*
  ) else (
    echo gradle-wrapper.jar is missing and no system 'gradle' was found.
    echo Open this project in Android Studio ^(it will regenerate the wrapper automatically^),
    echo or run: gradle wrapper --gradle-version 8.4
    exit /b 1
  )
)
