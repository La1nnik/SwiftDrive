#!/bin/bash
set -e


RAW_BASE="https://raw.githubusercontent.com/La1nnik/SwiftDrive/master"
SRC_BASE="$RAW_BASE/src/main/java/org/example"
DEPS_BASE="$RAW_BASE/jinputDependencies"

MAIN_CLASS="Main"

SRC_FILES=("Main.java" "UnderlightFader.java")

DEPS=(
  "SwiftBot-API-6.0.0.jar"
  "coreapi-2.0.11-SNAPSHOT.jar"
  "jinput-2.0.10.jar"
  "jinput-2.0.10-natives-all.jar"
  "linux-plugin-2.0.11-SNAPSHOT.jar"
  "libjinput-linux64.so"
)

DEPS_DIR="dependencies"

all_files_exist=true

for file in "${SRC_FILES[@]}"; do
  [ -f "$file" ] || all_files_exist=false
done

for dep in "${DEPS[@]}"; do
  [ -f "$DEPS_DIR/$dep" ] || all_files_exist=false
done



if [ "$all_files_exist" = false ]; then
  echo "Some files are missing. Starting download."

  mkdir -p "$DEPS_DIR"

  echo "Checking Java source files."
  for file in "${SRC_FILES[@]}"; do
    if [ ! -f "$file" ]; then
      echo "Downloading $file."
      wget -q -O "$file" "$SRC_BASE/$file"
    else
      echo "$file already exists."
    fi
  done

  echo "Checking dependencies."
  for dep in "${DEPS[@]}"; do
    if [ ! -f "$DEPS_DIR/$dep" ]; then
      echo "Downloading $dep."
      wget -q -O "$DEPS_DIR/$dep" "$DEPS_BASE/$dep"
    else
      echo "$dep already exists."
    fi
  done
else
  echo "All required files already exist. Skipping download."
fi



CLASSPATH="."
for jar in "$DEPS_DIR"/*.jar; do
  CLASSPATH="$CLASSPATH:$jar"
done



RECOMPILE=false
for src in "${SRC_FILES[@]}"; do
  class="${src%.java}.class"
  if [ ! -f "$class" ] || [ "$src" -nt "$class" ]; then
    RECOMPILE=true
    break
  fi
done

if [ "$RECOMPILE" = true ]; then
  echo "Compiling Java source files."
  javac -cp "$CLASSPATH" *.java
else
  echo "Java source files are already compiled."
fi



echo "Starting SwiftDrive."
sudo java \
  -Djava.library.path="$DEPS_DIR" \
  -cp "$CLASSPATH" \
  "$MAIN_CLASS"
