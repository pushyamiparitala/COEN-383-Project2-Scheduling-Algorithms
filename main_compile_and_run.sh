#!/bin/bash

# Compile all Java files in RR, SRT, Utilities, Pushyami (FCFS), and HPFPreventive
javac RR/*.java SRT/*.java Utilities/*.java  HPFPreventive/*.java Driver.java

# Run the Driver class
java Driver

