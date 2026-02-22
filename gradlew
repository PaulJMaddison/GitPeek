#!/usr/bin/env sh

set -eu

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -f "$WRAPPER_JAR" ]; then
  if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
  else
    JAVACMD=java
  fi
  exec "$JAVACMD" -Dorg.gradle.appname=gradlew -jar "$WRAPPER_JAR" "$@"
fi

if command -v gradle >/dev/null 2>&1; then
  echo "gradle-wrapper.jar not found; falling back to system Gradle." >&2
  exec gradle "$@"
fi

echo "Gradle Wrapper JAR is missing and no system 'gradle' command is available." >&2
echo "Run 'gradle wrapper' to regenerate gradle/wrapper/gradle-wrapper.jar." >&2
exit 1
