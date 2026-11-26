#!/bin/sh
set -eu
VERSION=8.10.2
BASE="${GRADLE_USER_HOME:-$HOME/.gradle}/wrapper/dists/gradle-$VERSION-bin/manual"
GRADLE="$BASE/gradle-$VERSION/bin/gradle"
if [ ! -x "$GRADLE" ]; then
  mkdir -p "$BASE"
  ARCHIVE="$BASE/gradle-$VERSION-bin.zip"
  if command -v curl >/dev/null 2>&1; then curl -fL "https://services.gradle.org/distributions/gradle-$VERSION-bin.zip" -o "$ARCHIVE"; else wget -O "$ARCHIVE" "https://services.gradle.org/distributions/gradle-$VERSION-bin.zip"; fi
  unzip -q -o "$ARCHIVE" -d "$BASE"
  rm -f "$ARCHIVE"
fi
exec "$GRADLE" "$@"
