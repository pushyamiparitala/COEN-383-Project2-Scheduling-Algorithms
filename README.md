# COEN-383-Project2-Scheduling-Algorithms
This project explores the 6 scheduling algorithms. You should be able to run all schedulers by running main_compile_and_run.sh.  In the terminal just type "./main_compile_and_run.sh".  When that happens 6 output files would be created(or already there from a previous run). The names of the files should clearly indicate which scheduling algorithm is associated with what output file. The output files contain statistical summaries for each run and the average for each run.  In addition, it tells us what process is being run at each quanta. The final report is also included here and cleanly includes all the final statistics.

Executable for compilation:
file name: main_compile_and_run.sh
content:
#!/bin/bash

# Compile all Java files
javac RR/*.java SRT/*.java Utilities/*.java  HPFPreventive/*.java Driver.java

# Run the Driver class
java Driver