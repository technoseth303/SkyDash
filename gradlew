#!/usr/bin/env sh
##############################################################################
##
##  Gradle startup script for UN*X-like environments
##
##############################################################################

# Attempt to locate JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
  JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$(which javac)")")")"
fi

if [ -z "$JAVA_HOME" ]; then
  echo "ERROR: JAVA_HOME is not set and Java could not be found."
  exit 1
fi

exec "$JAVA_HOME/bin/java" org.gradle.wrapper.GradleWrapperMain "$@"
