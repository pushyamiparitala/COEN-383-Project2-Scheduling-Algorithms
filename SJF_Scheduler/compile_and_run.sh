#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

# Navigate to the project root directory (one level up from SJF_Scheduler)
PROJECT_ROOT=$(dirname "$SCRIPT_DIR")
cd "$PROJECT_ROOT"

echo "Compiling Java files..."
# Compile the Java files from the project root
# The -d . argument ensures that the compiled .class files are placed in their respective directories
javac -d . SJF_Scheduler/*.java utilities/*.java

# Check for compilation errors
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

echo "Compilation successful. Running simulation..."

# Run the simulation from the project root
# The -cp argument specifies the classpath, including both SJF_Scheduler and utilities directories.
java -cp .:utilities SJFSimulation