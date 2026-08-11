#!/bin/sh
# POSIX gradle wrapper launcher. If gradle/wrapper/gradle-wrapper.jar is missing
# (it is not checked into this project to avoid committing a binary blob), this
# script falls back to a system-installed `gradle` so `./gradlew <task>` still works.
DIR="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"
if [ -f "$WRAPPER_JAR" ]; then
  exec java -jar "$WRAPPER_JAR" "$@"
else
  if command -v gradle >/dev/null 2>&1; then
    echo "Note: gradle-wrapper.jar not present; using system Gradle instead." >&2
    exec gradle "$@"
  else
    echo "gradle-wrapper.jar is missing and no system 'gradle' was found." >&2
    echo "Open this project in Android Studio (it will regenerate the wrapper automatically)," >&2
    echo "or run: gradle wrapper --gradle-version 8.4   (requires Gradle installed some other way)." >&2
    exit 1
  fi
fi
