#!/usr/bin/env sh
# Minimal, robust Gradle wrapper launcher for POSIX shells.

set -e

# Resolve the directory this script lives in (no symlink gymnastics needed for CI)
SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"

# Use the wrapper JAR committed in the repo
CLASSPATH="$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar"

# Prefer JAVA_HOME if set; fall back to `java` on PATH (actions/setup-java adds this)
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  JAVA_CMD="$JAVA_HOME/bin/java"
else
  JAVA_CMD="java"
fi

exec "$JAVA_CMD" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
