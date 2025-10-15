#!/bin/bash

# Compile all Java files
echo "Compiling Java files..."
echo "Compiling utilities package..."
javac -d ../utilities ../utilities/Process.java ../utilities/ProcessGenerator.java ../utilities/WorkloadGenerator.java

if [ $? -eq 0 ]; then
    echo "Compiling main classes..."
    javac -cp .:..:../utilities FCFSScheduler.java FCFSSimulation.java
    
    # Check if compilation was successful
    if [ $? -eq 0 ]; then
        echo "Compilation successful!"
        echo ""
        echo "Running FCFS Simulation..."
        echo "=========================================="
        java -cp .:..:../utilities FCFSSimulation
    else
        echo "Main classes compilation failed!"
        exit 1
    fi
else
    echo "Utilities compilation failed!"
    exit 1
fi

