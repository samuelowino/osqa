#!/bin/bash

DIRECTORY="${1:-/prod/osqa}"
VERSION="${2:-1.3}"
JAR_FILE="target/osqa-${VERSION}.jar"

cd "$DIRECTORY" || { echo "Failed to cd to $DIRECTORY"; exit 1; }

if [ -f "$JAR_FILE" ]; then
  echo "jar file v${VERSION} is already present in $DIRECTORY, executing"
  mvn clean package -DskipTests
  java -jar "$JAR_FILE"
else
  echo "jar file v${VERSION} is not present, attempting to build"
  mkdir -p /prod/osqa
  cp -rv "$DIRECTORY" /prod/osqa
  cd /prod/osqa/$(basename "$DIRECTORY") || exit 1
  mvn clean package -DskipTests
  java -jar "$JAR_FILE"
fi
