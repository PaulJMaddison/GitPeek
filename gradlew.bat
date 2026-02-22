@echo off
setlocal

set APP_HOME=%~dp0
set WRAPPER_JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar

if exist "%WRAPPER_JAR%" goto runWrapper
where gradle >NUL 2>&1
if %ERRORLEVEL%==0 goto runSystemGradle

echo Gradle Wrapper JAR is missing and no system Gradle command is available. 1>&2
echo Run "gradle wrapper" to regenerate gradle\wrapper\gradle-wrapper.jar. 1>&2
exit /b 1

:runWrapper
if defined JAVA_HOME (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
) else (
  set JAVA_EXE=java.exe
)
"%JAVA_EXE%" -Dorg.gradle.appname=gradlew -jar "%WRAPPER_JAR%" %*
exit /b %ERRORLEVEL%

:runSystemGradle
echo gradle-wrapper.jar not found; falling back to system Gradle. 1>&2
gradle %*
exit /b %ERRORLEVEL%
