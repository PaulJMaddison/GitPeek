@ECHO OFF
SETLOCAL
where gradle >nul 2>nul
IF %ERRORLEVEL% NEQ 0 (
  ECHO Gradle is required but was not found on PATH. Install Gradle or add the Gradle Wrapper.
  EXIT /B 1
)
gradle %*
