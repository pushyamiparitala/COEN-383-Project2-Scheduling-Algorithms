#!/bin/bash

# Move to the parent directory (so HPF and Utilities are both visible).
cd ..

# Compile all Java files
javac HPF/*.java utilities/*.java

# Run the HPFSimulation class (from within the HPF package)
java HPF.HPFSimulation
