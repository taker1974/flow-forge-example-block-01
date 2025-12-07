#!/bin/bash

# Script to build and copy the module with dependencies
# Builds the package with dependencies and copies to flow-forge-backend-instance blocks directory

TARGET_DIR="$HOME/.local/share/flow-forge-backend-instance/blocks/example-01"

mvn clean package -Pwith-dependencies
if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

mkdir -p "$TARGET_DIR"

MAIN_JAR=$(find target -name "flow-forge-example-block-01-*.jar" ! -name "*-fat.jar" ! -name "*-sources.jar" ! -name "*-javadoc.jar" | head -1)
if [ -z "$MAIN_JAR" ]; then
    echo "Main JAR file not found!"
    exit 1
fi

cp -f "$MAIN_JAR" "$TARGET_DIR/"

# Copy all dependency JARs from target directory
# The with-dependencies profile copies all runtime dependencies to target/
DEPENDENCY_COUNT=0
for jar in target/*.jar; do
    # Skip if it's the main jar, sources, javadoc, or fat jar
    if [[ "$jar" == "$MAIN_JAR" ]] || \
       [[ "$jar" == *"-sources.jar" ]] || \
       [[ "$jar" == *"-javadoc.jar" ]] || \
       [[ "$jar" == *"-fat.jar" ]]; then
        continue
    fi
    
    if [ -f "$jar" ]; then
        cp -f "$jar" "$TARGET_DIR/"
        ((DEPENDENCY_COUNT++))
    fi
done

echo "Successfully copied module and $DEPENDENCY_COUNT dependencies to:"
echo "$TARGET_DIR"
