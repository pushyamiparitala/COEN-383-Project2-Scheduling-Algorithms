#!/bin/bash

# Compile all Java files in RR, SRT, Utilities, and Pushyami (FCFS)
javac RR/*.java SRT/*.java Utilities/*.java "Pushyami (FCFS)"/*.java Driver.java

# Run the Driver class
java Driver
