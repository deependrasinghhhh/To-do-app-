#!/usr/bin/env sh
set -eu

# A small Maven bootstrapper for macOS/Linux users of this learning project.
MAVEN_VERSION="3.9.12"
WRAPPER_ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
MAVEN_HOME="$WRAPPER_ROOT/.mvn/apache-maven-$MAVEN_VERSION"

if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
  echo "Downloading Apache Maven $MAVEN_VERSION for this project..."
  mkdir -p "$WRAPPER_ROOT/.mvn"
  ARCHIVE="${TMPDIR:-/tmp}/apache-maven-$MAVEN_VERSION-bin.tar.gz"
  URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.tar.gz"

  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$URL" -o "$ARCHIVE"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$URL" -O "$ARCHIVE"
  else
    echo "Please install curl or wget, then run this command again." >&2
    exit 1
  fi

  tar -xzf "$ARCHIVE" -C "$WRAPPER_ROOT/.mvn"
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
