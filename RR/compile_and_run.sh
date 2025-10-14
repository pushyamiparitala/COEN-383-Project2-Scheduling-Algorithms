#!/bin/bash

# Move to the parent directory (so RR and Utilities are both visible)
cd ..

# Compile all Java files
javac RR/*.java Utilities/*.java

# Run the RRSimulation class (from within the RR package)
java RR.RRSimulation
