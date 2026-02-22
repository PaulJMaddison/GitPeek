#!/usr/bin/env sh

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "Gradle is required but was not found on PATH. Install Gradle or add the Gradle Wrapper." >&2
exit 1
