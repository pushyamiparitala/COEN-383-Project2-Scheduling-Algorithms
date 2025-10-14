#!/bin/bash

# Script to compile and run SRT (Shortest Remaining Time) Scheduling Simulation

echo "=========================================="
echo "  Compiling SRT Scheduling Simulation"
echo "=========================================="

# Navigate to project root
cd "$(dirname "$0")/.." || exit

# Clean previous builds
echo "Cleaning previous builds..."
rm -f "Vishwas (SRT)"/*.class
rm -f utilities/*.class

# Compile utilities
echo "Compiling utilities..."
javac utilities/Process.java
javac utilities/ProcessGenerator.java
javac utilities/WorkloadGenerator.java

# Compile SRT classes
echo "Compiling SRT scheduler..."
javac -cp . "Vishwas (SRT)"/SRTScheduler.java
javac -cp .:"Vishwas (SRT)" "Vishwas (SRT)"/SRTSimulation.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "=========================================="
    echo "  Compilation Successful!"
    echo "=========================================="
    echo ""
    echo "Running SRT Simulation..."
    echo ""
    
    # Run the simulation
    java -cp .:"Vishwas (SRT)" SRTSimulation
    
    # Save output to file
    echo ""
    echo "=========================================="
    echo "Saving output to SRT_output.txt..."
    java -cp .:"Vishwas (SRT)" SRTSimulation > "Vishwas (SRT)"/SRT_output.txt
    echo "Output saved successfully!"
    echo "=========================================="
else
    echo "=========================================="
    echo "  Compilation Failed!"
    echo "=========================================="
    exit 1
fi

