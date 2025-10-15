import java.util.ArrayList;
import java.util.List;

import FIRSTCOMEFIRSTSERVE.FCFSScheduler;
import HPFNoPreventive.HPFNoPreventiveScheduler;
import HPFPreventive.HPFPreventiveScheduler;
import utilities.Process;
import utilities.ProcessGenerator;
import utilities.WorkloadGenerator;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Main class to run SRT (Shortest Remaining Time) scheduling simulation
 * Runs multiple iterations and calculates average statistics
 */
public class Driver {

    private static final int NUM_ITERATIONS = 5;
    private static final int NUM_PROCESSES = 50; // Generate enough to keep CPU busy

    public static void main(String[] args) {

        try {
            // Create a PrintStream that writes to a file
            PrintStream out = new PrintStream("RRSimulationOutput.txt");

            // Redirect System.out to file
            System.setOut(out);
            System.out.println("=======================================================================================================");
            System.out.println("                    Round-Robin (RR) SCHEDULING SIMULATION");
            System.out.println("========================================================================================================");
            System.out.println();

            // Variables to accumulate statistics over all iterations
            double totalAvgTurnaroundTime = 0.0;
            double totalAvgWaitTime = 0.0;
            double totalAvgResponseTime = 0.0;
            double totalThroughput = 0.0;

            // Run the simulation for specified number of iterations
            for (int iteration = 1; iteration <= NUM_ITERATIONS; iteration++) {
                int seed = iteration * 100; // Different seed for each iteration

                // Generate processes for this iteration
                List<Process> processes = WorkloadGenerator.generateAndVerifyWorkload(iteration + 10);

                // Run SRT scheduling algorithm
                RR.RRScheduler scheduler = new RR.RRScheduler(processes);
                String timeline = scheduler.simulate();

                // Get processes that actually ran
                List<Process> ranProcesses = scheduler.getProcessesThatRan();

                // Display iteration header
                System.out.println("========================================================================================================");
                System.out.println("ITERATION " + iteration + " - Round Robin (RR) [Preemptive]");
                System.out.println("========================================================================================================");
                System.out.println();

                // Display generated processes
                System.out.println("Generated Processes:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Process | Arrival Time | Expected Run Time | Priority");
                System.out.println("----------------------------------------------------------------------------------------------------");

                for (int i = 0; i < ranProcesses.size(); i++) {
                    Process p = ranProcesses.get(i);
                    System.out.printf("   %c    |     %.0f       |        %.0f          |    %d%n",
                            p.getProcessName(),
                            p.getArrivalTime(),
                            p.getRuntime(),
                            p.getPriority());
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

                // Display timeline
                System.out.println("Timeline (showing which process runs at each quantum):");
                System.out.println("----------------------------------------------------------------------------------------------------");

                // Print timeline in chunks of 100 characters for readability
                int timelineLength = timeline.length();
                for (int i = 0; i < timelineLength; i += 100) {
                    int end = Math.min(i + 100, timelineLength);
                    System.out.printf("Quanta %3d-%3d: %s%n", i, end - 1, timeline.substring(i, end));
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

                // Display detailed process statistics
                System.out.println("Process Statistics:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Process | Arrival | Start | End | Runtime | Response | Wait | Turnaround | Priority");
                System.out.println("----------------------------------------------------------------------------------------------------");

                // Variables to accumulate statistics for this iteration
                double avgResponseTime = 0.0;
                double avgWaitTime = 0.0;
                double avgTurnaroundTime = 0.0;

                // Display each process details and calculate statistics
                for (int i = 0; i < ranProcesses.size(); i++) {
                    Process p = ranProcesses.get(i);

                    float turnaroundTime = p.getTurnaroundTime();
                    float responseTime = p.getResponseTimeValue();
                    float waitTime = p.getWaitTime();

                    // Accumulate statistics
                    avgResponseTime += responseTime;
                    avgWaitTime += waitTime;
                    avgTurnaroundTime += turnaroundTime;

                    // Print process details
                    System.out.printf("   %c    |  %.1f   | %.1f  | %.1f |   %.1f   |   %.1f    | %.1f  |    %.1f     |    %d%n",
                            p.getProcessName(),
                            p.getArrivalTime(),
                            p.getResponseTime(),
                            p.getCompletionTime(),
                            p.getRuntime(),
                            responseTime,
                            waitTime,
                            turnaroundTime,
                            p.getPriority());
                }

                System.out.println("----------------------------------------------------------------------------------------------------");

                // Calculate average statistics for this iteration
                int numRanProcesses = ranProcesses.size();
                avgResponseTime /= numRanProcesses;
                avgWaitTime /= numRanProcesses;
                avgTurnaroundTime /= numRanProcesses;

                // Calculate throughput (processes completed per quantum)
                float lastCompletionTime = 0;
                for (Process p : ranProcesses) {
                    if (p.getCompletionTime() > lastCompletionTime) {
                        lastCompletionTime = p.getCompletionTime();
                    }
                }
                double throughput = numRanProcesses / lastCompletionTime;

                // Accumulate totals
                totalAvgResponseTime += avgResponseTime;
                totalAvgWaitTime += avgWaitTime;
                totalAvgTurnaroundTime += avgTurnaroundTime;
                totalThroughput += throughput;

                // Display iteration summary
                System.out.println();
                System.out.println("Iteration " + iteration + " Summary:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.printf("Processes Completed: %d%n", numRanProcesses);
                System.out.printf("Total Quanta: %.0f%n", lastCompletionTime);
                System.out.printf("Average Turnaround Time: %.2f quanta%n", avgTurnaroundTime);
                System.out.printf("Average Wait Time: %.2f quanta%n", avgWaitTime);
                System.out.printf("Average Response Time: %.2f quanta%n", avgResponseTime);
                System.out.printf("Throughput: %.4f processes/quantum%n", throughput);
                System.out.println("====================================================================================================");
                System.out.println();
            }

            // Calculate overall averages
            totalAvgResponseTime /= NUM_ITERATIONS;
            totalAvgWaitTime /= NUM_ITERATIONS;
            totalAvgTurnaroundTime /= NUM_ITERATIONS;
            totalThroughput /= NUM_ITERATIONS;

            // Display final summary statistics
            System.out.println();
            System.out.println("========================================================================================================");
            System.out.println("                    FINAL STATISTICS (Average over " + NUM_ITERATIONS + " iterations)");
            System.out.println("======================================================================================================");
            System.out.println();
            System.out.println("Algorithm:  Round Robin (RR) [Preemptive]");
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.printf("Average Turnaround Time (TAT): %.2f quanta%n", totalAvgTurnaroundTime);
            System.out.printf("Average Wait Time (WT): %.2f quanta%n", totalAvgWaitTime);
            System.out.printf("Average Response Time (RT): %.2f quanta%n", totalAvgResponseTime);
            System.out.printf("Average Throughput: %.4f processes/quantum%n", totalThroughput);
            System.out.println("========================================================================================================");
        }
        catch(FileNotFoundException e) {
            System.err.println("Error: Unable to create output file.");
            e.printStackTrace();
        }

        try {
            // Create a PrintStream that writes to a file
            PrintStream out = new PrintStream("SRTSimulationOutput.txt");

            // Redirect System.out to file
            System.setOut(out);
            System.out.println("=======================================================================================================");
            System.out.println("                    Shortest Remaining Time (SRT) SCHEDULING SIMULATION");
            System.out.println("========================================================================================================");
            System.out.println();

            // Variables to accumulate statistics over all iterations
            double totalAvgTurnaroundTime = 0.0;
            double totalAvgWaitTime = 0.0;
            double totalAvgResponseTime = 0.0;
            double totalThroughput = 0.0;

            // Run the simulation for specified number of iterations
            for (int iteration = 1; iteration <= NUM_ITERATIONS; iteration++) {
                int seed = iteration * 100; // Different seed for each iteration

                // Generate processes for this iteration
                List<Process> processes = WorkloadGenerator.generateAndVerifyWorkload(iteration + 10);

                // Run SRT scheduling algorithm
                SRT.SRTScheduler scheduler = new SRT.SRTScheduler(processes);
                String timeline = scheduler.simulate();

                // Get processes that actually ran
                List<Process> ranProcesses = scheduler.getProcessesThatRan();

                // Display iteration header
                System.out.println("========================================================================================================");
                System.out.println("ITERATION " + iteration + " - Shortest Remaining Time (SRT) [Preemptive]");
                System.out.println("========================================================================================================");
                System.out.println();

                // Display generated processes
                System.out.println("Generated Processes:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Process | Arrival Time | Expected Run Time | Priority");
                System.out.println("----------------------------------------------------------------------------------------------------");

                for (int i = 0; i < ranProcesses.size(); i++) {
                    Process p = ranProcesses.get(i);
                    System.out.printf("   %c    |     %.0f       |        %.0f          |    %d%n",
                            p.getProcessName(),
                            p.getArrivalTime(),
                            p.getRuntime(),
                            p.getPriority());
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

                // Display timeline
                System.out.println("Timeline (showing which process runs at each quantum):");
                System.out.println("----------------------------------------------------------------------------------------------------");

                // Print timeline in chunks of 100 characters for readability
                int timelineLength = timeline.length();
                for (int i = 0; i < timelineLength; i += 100) {
                    int end = Math.min(i + 100, timelineLength);
                    System.out.printf("Quanta %3d-%3d: %s%n", i, end - 1, timeline.substring(i, end));
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

                // Display detailed process statistics
                System.out.println("Process Statistics:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Process | Arrival | Start | End | Runtime | Response | Wait | Turnaround | Priority");
                System.out.println("----------------------------------------------------------------------------------------------------");

                // Variables to accumulate statistics for this iteration
                double avgResponseTime = 0.0;
                double avgWaitTime = 0.0;
                double avgTurnaroundTime = 0.0;

                // Display each process details and calculate statistics
                for (int i = 0; i < ranProcesses.size(); i++) {
                    Process p = ranProcesses.get(i);

                    float turnaroundTime = p.getTurnaroundTime();
                    float responseTime = p.getResponseTimeValue();
                    float waitTime = p.getWaitTime();

                    // Accumulate statistics
                    avgResponseTime += responseTime;
                    avgWaitTime += waitTime;
                    avgTurnaroundTime += turnaroundTime;

                    // Print process details
                    System.out.printf("   %c    |  %.1f   | %.1f  | %.1f |   %.1f   |   %.1f    | %.1f  |    %.1f     |    %d%n",
                            p.getProcessName(),
                            p.getArrivalTime(),
                            p.getResponseTime(),
                            p.getCompletionTime(),
                            p.getRuntime(),
                            responseTime,
                            waitTime,
                            turnaroundTime,
                            p.getPriority());
                }

                System.out.println("----------------------------------------------------------------------------------------------------");

                // Calculate average statistics for this iteration
                int numRanProcesses = ranProcesses.size();
                avgResponseTime /= numRanProcesses;
                avgWaitTime /= numRanProcesses;
                avgTurnaroundTime /= numRanProcesses;

                // Calculate throughput (processes completed per quantum)
                float lastCompletionTime = 0;
                for (Process p : ranProcesses) {
                    if (p.getCompletionTime() > lastCompletionTime) {
                        lastCompletionTime = p.getCompletionTime();
                    }
                }
                double throughput = numRanProcesses / lastCompletionTime;

                // Accumulate totals
                totalAvgResponseTime += avgResponseTime;
                totalAvgWaitTime += avgWaitTime;
                totalAvgTurnaroundTime += avgTurnaroundTime;
                totalThroughput += throughput;

                // Display iteration summary
                System.out.println();
                System.out.println("Iteration " + iteration + " Summary:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.printf("Processes Completed: %d%n", numRanProcesses);
                System.out.printf("Total Quanta: %.0f%n", lastCompletionTime);
                System.out.printf("Average Turnaround Time: %.2f quanta%n", avgTurnaroundTime);
                System.out.printf("Average Wait Time: %.2f quanta%n", avgWaitTime);
                System.out.printf("Average Response Time: %.2f quanta%n", avgResponseTime);
                System.out.printf("Throughput: %.4f processes/quantum%n", throughput);
                System.out.println("====================================================================================================");
                System.out.println();
            }

            // Calculate overall averages
            totalAvgResponseTime /= NUM_ITERATIONS;
            totalAvgWaitTime /= NUM_ITERATIONS;
            totalAvgTurnaroundTime /= NUM_ITERATIONS;
            totalThroughput /= NUM_ITERATIONS;

            // Display final summary statistics
            System.out.println();
            System.out.println("========================================================================================================");
            System.out.println("                    FINAL STATISTICS (Average over " + NUM_ITERATIONS + " iterations)");
            System.out.println("======================================================================================================");
            System.out.println();
            System.out.println("Algorithm:  Shortest Remaining Time (SRT) [Preemptive]");
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.printf("Average Turnaround Time (TAT): %.2f quanta%n", totalAvgTurnaroundTime);
            System.out.printf("Average Wait Time (WT): %.2f quanta%n", totalAvgWaitTime);
            System.out.printf("Average Response Time (RT): %.2f quanta%n", totalAvgResponseTime);
            System.out.printf("Average Throughput: %.4f processes/quantum%n", totalThroughput);
            System.out.println("========================================================================================================");
        }
        catch(FileNotFoundException e) {
            System.err.println("Error: Unable to create output file.");
            e.printStackTrace();
        }

        try {
            // Create a PrintStream that writes to a file
            PrintStream out = new PrintStream("FCFSSimulationOutput.txt");

            // Redirect System.out to file
            System.setOut(out);
            System.out.println("=======================================================================================================");
            System.out.println("                    First-Come First Serve (FCFS) SCHEDULING SIMULATION");
            System.out.println("========================================================================================================");
            System.out.println();

            // Variables to accumulate statistics over all iterations
            double totalAvgTurnaroundTime = 0.0;
            double totalAvgWaitTime = 0.0;
            double totalAvgResponseTime = 0.0;
            double totalThroughput = 0.0;

            // Run the simulation for specified number of iterations
            for (int iteration = 1; iteration <= NUM_ITERATIONS; iteration++) {
                int seed = iteration * 100; // Different seed for each iteration

                // Generate processes for this iteration
                List<Process> processes = WorkloadGenerator.generateAndVerifyWorkload(iteration + 10);

                // Run FCFS scheduling algorithm
                FCFSScheduler scheduler = new FCFSScheduler(processes);
                String timeline = scheduler.simulateFCFS();

                // Get processes that actually ran
                List<Process> ranProcesses = scheduler.getProcessesThatRan();

                // Display iteration header
                System.out.println("========================================================================================================");
                System.out.println("ITERATION " + iteration + " - First Come First Serve (FCFS) [Non-Preemptive]");
                System.out.println("========================================================================================================");
                System.out.println();

                // Display generated processes
                System.out.println("Generated Processes:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Process | Arrival Time | Expected Run Time | Priority");
                System.out.println("----------------------------------------------------------------------------------------------------");

                for (int i = 0; i < ranProcesses.size(); i++) {
                    Process p = ranProcesses.get(i);
                    System.out.printf("   %c    |     %.0f       |        %.0f          |    %d%n",
                            p.getProcessName(),
                            p.getArrivalTime(),
                            p.getRuntime(),
                            p.getPriority());
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

                // Display timeline
                System.out.println("Timeline (showing which process runs at each quantum):");
                System.out.println("----------------------------------------------------------------------------------------------------");

                // Print timeline in chunks of 100 characters for readability
                int timelineLength = timeline.length();
                for (int i = 0; i < timelineLength; i += 100) {
                    int end = Math.min(i + 100, timelineLength);
                    System.out.printf("Quanta %3d-%3d: %s%n", i, end - 1, timeline.substring(i, end));
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

                // Display detailed process statistics
                System.out.println("Process Statistics:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Process | Arrival | Start | End | Runtime | Response | Wait | Turnaround | Priority");
                System.out.println("----------------------------------------------------------------------------------------------------");

                // Variables to accumulate statistics for this iteration
                double avgResponseTime = 0.0;
                double avgWaitTime = 0.0;
                double avgTurnaroundTime = 0.0;

                // Display each process details and calculate statistics
                for (int i = 0; i < ranProcesses.size(); i++) {
                    Process p = ranProcesses.get(i);

                    float turnaroundTime = p.getTurnaroundTime();
                    float responseTime = p.getResponseTimeValue();
                    float waitTime = p.getWaitTime();

                    // Accumulate statistics
                    avgResponseTime += responseTime;
                    avgWaitTime += waitTime;
                    avgTurnaroundTime += turnaroundTime;

                    // Print process details
                    System.out.printf("   %c    |  %.1f   | %.1f  | %.1f |   %.1f   |   %.1f    | %.1f  |    %.1f     |    %d%n",
                            p.getProcessName(),
                            p.getArrivalTime(),
                            p.getResponseTime(),
                            p.getCompletionTime(),
                            p.getRuntime(),
                            responseTime,
                            waitTime,
                            turnaroundTime,
                            p.getPriority());
                }

                System.out.println("----------------------------------------------------------------------------------------------------");

                // Calculate average statistics for this iteration
                int numRanProcesses = ranProcesses.size();
                avgResponseTime /= numRanProcesses;
                avgWaitTime /= numRanProcesses;
                avgTurnaroundTime /= numRanProcesses;

                // Calculate throughput (processes completed per quantum)
                float lastCompletionTime = 0;
                for (Process p : ranProcesses) {
                    if (p.getCompletionTime() > lastCompletionTime) {
                        lastCompletionTime = p.getCompletionTime();
                    }
                }
                double throughput = numRanProcesses / lastCompletionTime;

                // Accumulate totals
                totalAvgResponseTime += avgResponseTime;
                totalAvgWaitTime += avgWaitTime;
                totalAvgTurnaroundTime += avgTurnaroundTime;
                totalThroughput += throughput;

                // Display iteration summary
                System.out.println();
                System.out.println("Iteration " + iteration + " Summary:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.printf("Processes Completed: %d%n", numRanProcesses);
                System.out.printf("Total Quanta: %.0f%n", lastCompletionTime);
                System.out.printf("Average Turnaround Time: %.2f quanta%n", avgTurnaroundTime);
                System.out.printf("Average Wait Time: %.2f quanta%n", avgWaitTime);
                System.out.printf("Average Response Time: %.2f quanta%n", avgResponseTime);
                System.out.printf("Throughput: %.4f processes/quantum%n", throughput);
                System.out.println("====================================================================================================");
                System.out.println();
            }

            // Calculate overall averages
            totalAvgResponseTime /= NUM_ITERATIONS;
            totalAvgWaitTime /= NUM_ITERATIONS;
            totalAvgTurnaroundTime /= NUM_ITERATIONS;
            totalThroughput /= NUM_ITERATIONS;

            // Display final summary statistics
            System.out.println();
            System.out.println("========================================================================================================");
            System.out.println("                    FINAL STATISTICS (Average over " + NUM_ITERATIONS + " iterations)");
            System.out.println("======================================================================================================");
            System.out.println();
            System.out.println("Algorithm:  First Come First Serve (FCFS) [Non-Preemptive]");
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.printf("Average Turnaround Time (TAT): %.2f quanta%n", totalAvgTurnaroundTime);
            System.out.printf("Average Wait Time (WT): %.2f quanta%n", totalAvgWaitTime);
            System.out.printf("Average Response Time (RT): %.2f quanta%n", totalAvgResponseTime);
            System.out.printf("Average Throughput: %.4f processes/quantum%n", totalThroughput);
            System.out.println("========================================================================================================");
        }
        catch(FileNotFoundException e) {
            System.err.println("Error: Unable to create output file.");
            e.printStackTrace();
        }

        try {
            // Create a PrintStream that writes to a file
            PrintStream out = new PrintStream("HPFPreemptiveSimulationOutput.txt");

            // Redirect System.out to file
            System.setOut(out);
            System.out.println("=======================================================================================================");
            System.out.println("                    Highest Priority First - Preemptive (HPF) SCHEDULING SIMULATION");
            System.out.println("========================================================================================================");
            System.out.println();

            // Variables to accumulate statistics over all iterations
            double totalAvgTurnaroundTime = 0.0;
            double totalAvgWaitTime = 0.0;
            double totalAvgResponseTime = 0.0;
            double totalThroughput = 0.0;

            double[] grandTotalResponseByPriority = new double[4];
            double[] grandTotalWaitByPriority = new double[4];
            double[] grandTotalTurnaroundByPriority = new double[4];
            double[] grandTotalThroughput = new double[4];


            // Run the simulation for specified number of iterations
            for (int iteration = 1; iteration <= NUM_ITERATIONS; iteration++) {
                int seed = iteration * 100; // Different seed for each iteration

                // Generate processes for this iteration
                List<Process> processes = WorkloadGenerator.generateAndVerifyWorkload(iteration + 10);

                // Run HPF(Premptive) scheduling algorithm
                HPFPreventiveScheduler scheduler = new HPFPreventiveScheduler(processes);
                String timeline = scheduler.simulate();

                // Get processes that actually ran
                List<Process> ranProcesses = scheduler.getProcessesThatRan();

                // Display iteration header
                System.out.println("========================================================================================================");
                System.out.println("ITERATION " + iteration + " - Highest Priority First (HPF) [Preemptive]");
                System.out.println("========================================================================================================");
                System.out.println();

                // Display generated processes
                System.out.println("Generated Processes:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Process | Arrival Time | Expected Run Time | Priority");
                System.out.println("----------------------------------------------------------------------------------------------------");

                for (int i = 0; i < ranProcesses.size(); i++) {
                    Process p = ranProcesses.get(i);
                    System.out.printf("   %c    |     %.0f       |        %.0f          |    %d%n",
                            p.getProcessName(),
                            p.getArrivalTime(),
                            p.getRuntime(),
                            p.getPriority());
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

                // Display timeline
                System.out.println("Timeline (showing which process runs at each quantum):");
                System.out.println("----------------------------------------------------------------------------------------------------");

                // Print timeline in chunks of 100 characters for readability
                int timelineLength = timeline.length();
                for (int i = 0; i < timelineLength; i += 100) {
                    int end = Math.min(i + 100, timelineLength);
                    System.out.printf("Quanta %3d-%3d: %s%n", i, end - 1, timeline.substring(i, end));
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

                // Display detailed process statistics
                System.out.println("Process Statistics:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Process | Arrival | Start | End | Runtime | Response | Wait | Turnaround | Priority");
                System.out.println("----------------------------------------------------------------------------------------------------");

                // Variables to accumulate statistics for this iteration
                double avgResponseTime = 0.0;
                double avgWaitTime = 0.0;
                double avgTurnaroundTime = 0.0;

                //store for each priority level
                double[] totalResponseByPriority = new double[4];
                double[] totalWaitByPriority = new double[4];
                double[] totalTurnaroundByPriority = new double[4];
                int[] countByPriority = new int[4];

                // Display each process details and calculate statistics
                for (int i = 0; i < ranProcesses.size(); i++) {
                    Process p = ranProcesses.get(i);

                    float turnaroundTime = p.getTurnaroundTime();
                    float responseTime = p.getResponseTimeValue();
                    float waitTime = p.getWaitTime();
                    int priority = p.getPriority();

                    // Accumulate statistics
                    avgResponseTime += responseTime;
                    avgWaitTime += waitTime;
                    avgTurnaroundTime += turnaroundTime;
                    //accumulate statistics for different priority levels.
                    totalResponseByPriority[priority - 1] += responseTime;
                    totalWaitByPriority[priority - 1] += waitTime;
                    totalTurnaroundByPriority[priority - 1] += turnaroundTime;
                    countByPriority[priority - 1]++;

                    // Print process details
                    System.out.printf("   %c    |  %.1f   | %.1f  | %.1f |   %.1f   |   %.1f    | %.1f  |    %.1f     |    %d%n",
                            p.getProcessName(),
                            p.getArrivalTime(),
                            p.getResponseTime(),
                            p.getCompletionTime(),
                            p.getRuntime(),
                            responseTime,
                            waitTime,
                            turnaroundTime,
                            p.getPriority());
                }

                System.out.println("----------------------------------------------------------------------------------------------------");

                // Calculate average statistics for this iteration
                int numRanProcesses = ranProcesses.size();
                avgResponseTime /= numRanProcesses;
                avgWaitTime /= numRanProcesses;
                avgTurnaroundTime /= numRanProcesses;

                // Calculate throughput (processes completed per quantum)
                float lastCompletionTime = 0;
                for (Process p : ranProcesses) {
                    if (p.getCompletionTime() > lastCompletionTime) {
                        lastCompletionTime = p.getCompletionTime();
                    }
                }
                double throughput = numRanProcesses / lastCompletionTime;

                // Accumulate totals
                totalAvgResponseTime += avgResponseTime;
                totalAvgWaitTime += avgWaitTime;
                totalAvgTurnaroundTime += avgTurnaroundTime;
                totalThroughput += throughput;

                // Display iteration summary
                System.out.println();
                System.out.println("Iteration " + iteration + " Summary:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.printf("Processes Completed: %d%n", numRanProcesses);
                System.out.printf("Total Quanta: %.0f%n", lastCompletionTime);
                System.out.printf("Average Turnaround Time: %.2f quanta%n", avgTurnaroundTime);
                System.out.printf("Average Wait Time: %.2f quanta%n", avgWaitTime);
                System.out.printf("Average Response Time: %.2f quanta%n", avgResponseTime);
                System.out.printf("Throughput: %.4f processes/quantum%n", throughput);
                System.out.println("====================================================================================================");
                System.out.println();

                System.out.println();
                System.out.println("Per-Priority Queue Statistics:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Priority | Avg Turnaround | Avg Wait | Avg Response | Throughput | # processes");
                System.out.println("----------------------------------------------------------------------------------------------------");

                for (int i = 1; i <= 4; i++) {
                    if (countByPriority[i - 1] > 0) {
                        double avgTAT = totalTurnaroundByPriority[i - 1] / countByPriority[i - 1];
                        double avgWait = totalWaitByPriority[i - 1] / countByPriority[i - 1];
                        double avgResp = totalResponseByPriority[i - 1] / countByPriority[i - 1];
                        double throughput2 = countByPriority[i - 1]/lastCompletionTime;

                        grandTotalResponseByPriority[i - 1] += avgResp;
                        grandTotalWaitByPriority[i - 1] += avgWait;
                        grandTotalTurnaroundByPriority[i - 1] += avgTAT;
                        grandTotalThroughput[i - 1] += throughput2;


                        System.out.printf("   %d      |     %.2f        |   %.2f   |     %.2f     |     %.2f |    %d%n",
                                i, avgTAT, avgWait, avgResp, throughput2, countByPriority[i - 1]);
                    } else {
                        System.out.printf("   %d      |     N/A         |   N/A    |     N/A      |     0%n", i);
                    }
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

            }

            // Calculate overall averages
            totalAvgResponseTime /= NUM_ITERATIONS;
            totalAvgWaitTime /= NUM_ITERATIONS;
            totalAvgTurnaroundTime /= NUM_ITERATIONS;
            totalThroughput /= NUM_ITERATIONS;

            for (int i = 1; i <= 4; i++) {
                grandTotalResponseByPriority[i - 1] /= NUM_ITERATIONS;
                grandTotalWaitByPriority[i - 1] /= NUM_ITERATIONS;
                grandTotalTurnaroundByPriority[i - 1] /= NUM_ITERATIONS;
                grandTotalThroughput[i - 1] /= NUM_ITERATIONS;
            }




            // Display final summary statistics
            System.out.println();
            System.out.println("========================================================================================================");
            System.out.println("                    FINAL STATISTICS (Average over " + NUM_ITERATIONS + " iterations)");
            System.out.println("======================================================================================================");
            System.out.println();
            System.out.println("Algorithm:  Highest Priority First (HPF) [Preemptive]");
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.printf("Average Turnaround Time (TAT): %.2f quanta%n", totalAvgTurnaroundTime);
            System.out.printf("Average Wait Time (WT): %.2f quanta%n", totalAvgWaitTime);
            System.out.printf("Average Response Time (RT): %.2f quanta%n", totalAvgResponseTime);
            System.out.printf("Average Throughput: %.4f processes/quantum%n", totalThroughput);
            System.out.println("========================================================================================================");

            //Calculate overall averages per priority queue
            System.out.println("Priority-Level Breakdown:");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.printf("%-10s %-25s %-25s %-25s %-25s%n",
                    "Priority", "Avg Turnaround (TAT)", "Avg Wait (WT)", "Avg Response (RT)", "Throughput");

            for (int i = 0; i < 4; i++) {
                System.out.printf("%-10d %-25.2f %-25.2f %-25.2f %-25.4f%n",
                        i + 1,
                        grandTotalTurnaroundByPriority[i],
                        grandTotalWaitByPriority[i],
                        grandTotalResponseByPriority[i],
                        grandTotalThroughput[i]);
            }

            System.out.println("========================================================================================================");
        }
        catch(FileNotFoundException e) {
            System.err.println("Error: Unable to create output file.");
            e.printStackTrace();
        }


        try {
            // Create a PrintStream that writes to a file
            PrintStream out = new PrintStream("HPFNonPreemptiveSimulationOutput.txt");

            // Redirect System.out to file
            System.setOut(out);
            System.out.println("=======================================================================================================");
            System.out.println("                    Highest Priority First (HPF) Non Premptive SCHEDULING SIMULATION");
            System.out.println("========================================================================================================");
            System.out.println();

            // Variables to accumulate statistics over all iterations
            double totalAvgTurnaroundTime = 0.0;
            double totalAvgWaitTime = 0.0;
            double totalAvgResponseTime = 0.0;
            double totalThroughput = 0.0;

            double[] grandTotalResponseByPriority = new double[4];
            double[] grandTotalWaitByPriority = new double[4];
            double[] grandTotalTurnaroundByPriority = new double[4];
            double[] grandTotalThroughput = new double[4];


            // Run the simulation for specified number of iterations
            for (int iteration = 1; iteration <= NUM_ITERATIONS; iteration++) {
                int seed = iteration * 100; // Different seed for each iteration

                // Generate processes for this iteration
                List<Process> processes = WorkloadGenerator.generateAndVerifyWorkload(iteration + 10);

                // Run HPF(nonPremptive) scheduling algorithm
                HPFNoPreventiveScheduler scheduler = new HPFNoPreventiveScheduler(processes);
                String timeline = scheduler.simulate();

                // Get processes that actually ran
                List<Process> ranProcesses = scheduler.getProcessesThatRan();

                // Display iteration header
                System.out.println("========================================================================================================");
                System.out.println("ITERATION " + iteration + " - Highest Priority First (HPF) [nonPreemptive]");
                System.out.println("========================================================================================================");
                System.out.println();

                // Display generated processes
                System.out.println("Generated Processes:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Process | Arrival Time | Expected Run Time | Priority");
                System.out.println("----------------------------------------------------------------------------------------------------");

                for (int i = 0; i < ranProcesses.size(); i++) {
                    Process p = ranProcesses.get(i);
                    System.out.printf("   %c    |     %.0f       |        %.0f          |    %d%n",
                            p.getProcessName(),
                            p.getArrivalTime(),
                            p.getRuntime(),
                            p.getPriority());
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

                // Display timeline
                System.out.println("Timeline (showing which process runs at each quantum):");
                System.out.println("----------------------------------------------------------------------------------------------------");

                // Print timeline in chunks of 100 characters for readability
                int timelineLength = timeline.length();
                for (int i = 0; i < timelineLength; i += 100) {
                    int end = Math.min(i + 100, timelineLength);
                    System.out.printf("Quanta %3d-%3d: %s%n", i, end - 1, timeline.substring(i, end));
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

                // Display detailed process statistics
                System.out.println("Process Statistics:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Process | Arrival | Start | End | Runtime | Response | Wait | Turnaround | Priority");
                System.out.println("----------------------------------------------------------------------------------------------------");

                // Variables to accumulate statistics for this iteration
                double avgResponseTime = 0.0;
                double avgWaitTime = 0.0;
                double avgTurnaroundTime = 0.0;

                //store for each priority level
                double[] totalResponseByPriority = new double[4];
                double[] totalWaitByPriority = new double[4];
                double[] totalTurnaroundByPriority = new double[4];
                int[] countByPriority = new int[4];

                // Display each process details and calculate statistics
                for (int i = 0; i < ranProcesses.size(); i++) {
                    Process p = ranProcesses.get(i);

                    float turnaroundTime = p.getTurnaroundTime();
                    float responseTime = p.getResponseTimeValue();
                    float waitTime = p.getWaitTime();
                    int priority = p.getPriority();

                    // Accumulate statistics
                    avgResponseTime += responseTime;
                    avgWaitTime += waitTime;
                    avgTurnaroundTime += turnaroundTime;
                    //accumulate statistics for different priority levels.
                    totalResponseByPriority[priority - 1] += responseTime;
                    totalWaitByPriority[priority - 1] += waitTime;
                    totalTurnaroundByPriority[priority - 1] += turnaroundTime;
                    countByPriority[priority - 1]++;

                    // Print process details
                    System.out.printf("   %c    |  %.1f   | %.1f  | %.1f |   %.1f   |   %.1f    | %.1f  |    %.1f     |    %d%n",
                            p.getProcessName(),
                            p.getArrivalTime(),
                            p.getResponseTime(),
                            p.getCompletionTime(),
                            p.getRuntime(),
                            responseTime,
                            waitTime,
                            turnaroundTime,
                            p.getPriority());
                }

                System.out.println("----------------------------------------------------------------------------------------------------");

                // Calculate average statistics for this iteration
                int numRanProcesses = ranProcesses.size();
                avgResponseTime /= numRanProcesses;
                avgWaitTime /= numRanProcesses;
                avgTurnaroundTime /= numRanProcesses;

                // Calculate throughput (processes completed per quantum)
                float lastCompletionTime = 0;
                for (Process p : ranProcesses) {
                    if (p.getCompletionTime() > lastCompletionTime) {
                        lastCompletionTime = p.getCompletionTime();
                    }
                }
                double throughput = numRanProcesses / lastCompletionTime;

                // Accumulate totals
                totalAvgResponseTime += avgResponseTime;
                totalAvgWaitTime += avgWaitTime;
                totalAvgTurnaroundTime += avgTurnaroundTime;
                totalThroughput += throughput;

                // Display iteration summary
                System.out.println();
                System.out.println("Iteration " + iteration + " Summary:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.printf("Processes Completed: %d%n", numRanProcesses);
                System.out.printf("Total Quanta: %.0f%n", lastCompletionTime);
                System.out.printf("Average Turnaround Time: %.2f quanta%n", avgTurnaroundTime);
                System.out.printf("Average Wait Time: %.2f quanta%n", avgWaitTime);
                System.out.printf("Average Response Time: %.2f quanta%n", avgResponseTime);
                System.out.printf("Throughput: %.4f processes/quantum%n", throughput);
                System.out.println("====================================================================================================");
                System.out.println();

                System.out.println();
                System.out.println("Per-Priority Queue Statistics:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Priority | Avg Turnaround | Avg Wait | Avg Response | Throughput | # processes");
                System.out.println("----------------------------------------------------------------------------------------------------");

                for (int i = 1; i <= 4; i++) {
                    if (countByPriority[i - 1] > 0) {
                        double avgTAT = totalTurnaroundByPriority[i - 1] / countByPriority[i - 1];
                        double avgWait = totalWaitByPriority[i - 1] / countByPriority[i - 1];
                        double avgResp = totalResponseByPriority[i - 1] / countByPriority[i - 1];
                        double throughput2 = countByPriority[i - 1]/lastCompletionTime;

                        grandTotalResponseByPriority[i - 1] += avgResp;
                        grandTotalWaitByPriority[i - 1] += avgWait;
                        grandTotalTurnaroundByPriority[i - 1] += avgTAT;
                        grandTotalThroughput[i - 1] += throughput2;


                        System.out.printf("   %d      |     %.2f        |   %.2f   |     %.2f     |     %.2f |    %d%n",
                                i, avgTAT, avgWait, avgResp, throughput2, countByPriority[i - 1]);
                    } else {
                        System.out.printf("   %d      |     N/A         |   N/A    |     N/A      |     0%n", i);
                    }
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

            }

            // Calculate overall averages
            totalAvgResponseTime /= NUM_ITERATIONS;
            totalAvgWaitTime /= NUM_ITERATIONS;
            totalAvgTurnaroundTime /= NUM_ITERATIONS;
            totalThroughput /= NUM_ITERATIONS;

            for (int i = 1; i <= 4; i++) {
                grandTotalResponseByPriority[i - 1] /= NUM_ITERATIONS;
                grandTotalWaitByPriority[i - 1] /= NUM_ITERATIONS;
                grandTotalTurnaroundByPriority[i - 1] /= NUM_ITERATIONS;
                grandTotalThroughput[i - 1] /= NUM_ITERATIONS;
            }




            // Display final summary statistics
            System.out.println();
            System.out.println("========================================================================================================");
            System.out.println("                    FINAL STATISTICS (Average over " + NUM_ITERATIONS + " iterations)");
            System.out.println("======================================================================================================");
            System.out.println();
            System.out.println("Algorithm:  Highest Priority First (HPF) [nonPreemptive]");
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.printf("Average Turnaround Time (TAT): %.2f quanta%n", totalAvgTurnaroundTime);
            System.out.printf("Average Wait Time (WT): %.2f quanta%n", totalAvgWaitTime);
            System.out.printf("Average Response Time (RT): %.2f quanta%n", totalAvgResponseTime);
            System.out.printf("Average Throughput: %.4f processes/quantum%n", totalThroughput);
            System.out.println("========================================================================================================");

            //Calculate overall averages per priority queue
            System.out.println("Priority-Level Breakdown:");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.printf("%-10s %-25s %-25s %-25s %-25s%n",
                    "Priority", "Avg Turnaround (TAT)", "Avg Wait (WT)", "Avg Response (RT)", "Throughput");

            for (int i = 0; i < 4; i++) {
                System.out.printf("%-10d %-25.2f %-25.2f %-25.2f %-25.4f%n",
                        i + 1,
                        grandTotalTurnaroundByPriority[i],
                        grandTotalWaitByPriority[i],
                        grandTotalResponseByPriority[i],
                        grandTotalThroughput[i]);
            }

            System.out.println("========================================================================================================");
        }
        catch(FileNotFoundException e) {
            System.err.println("Error: Unable to create output file.");
            e.printStackTrace();
        }


        try {
            // Create a PrintStream that writes to a file
            PrintStream out = new PrintStream("SJSSimulationOutput.txt");

            // Redirect System.out to file
            System.setOut(out);
            System.out.println("=======================================================================================================");
            System.out.println("                    Shortest Job First (SJF) SCHEDULING SIMULATION");
            System.out.println("========================================================================================================");
            System.out.println();

            // Variables to accumulate statistics over all iterations
            double totalAvgTurnaroundTime = 0.0;
            double totalAvgWaitTime = 0.0;
            double totalAvgResponseTime = 0.0;
            double totalThroughput = 0.0;

            // Run the simulation for specified number of iterations
            for (int iteration = 1; iteration <= NUM_ITERATIONS; iteration++) {
                int seed = iteration * 100; // Different seed for each iteration

                // Generate processes for this iteration
                List<Process> processes = WorkloadGenerator.generateAndVerifyWorkload(iteration + 10);

                // Run SJF scheduling algorithm
                SJF_Scheduler.SJFScheduler scheduler = new SJF_Scheduler.SJFScheduler();
                SJF_Scheduler.SJFScheduler.SJFResult result = scheduler.schedule(processes);
                String timeline = result.timeChart;

                // Get processes that actually ran
                List<Process> ranProcesses = result.completedProcesses;

                // Display iteration header
                System.out.println("========================================================================================================");
                System.out.println("ITERATION " + iteration + " - Shortest Job First (SJF) [Non-Preemptive]");
                System.out.println("========================================================================================================");
                System.out.println();

                // Display generated processes
                System.out.println("Generated Processes:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Process | Arrival Time | Expected Run Time | Priority");
                System.out.println("----------------------------------------------------------------------------------------------------");

                for (int i = 0; i < ranProcesses.size(); i++) {
                    Process p = ranProcesses.get(i);
                    System.out.printf("   %c    |     %.0f       |        %.0f          |    %d%n",
                            p.getProcessName(),
                            p.getArrivalTime(),
                            p.getRuntime(),
                            p.getPriority());
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

                // Display timeline
                System.out.println("Timeline (showing which process runs at each quantum):");
                System.out.println("----------------------------------------------------------------------------------------------------");

                // Print timeline in chunks of 100 characters for readability
                int timelineLength = timeline.length();
                for (int i = 0; i < timelineLength; i += 100) {
                    int end = Math.min(i + 100, timelineLength);
                    System.out.printf("Quanta %3d-%3d: %s%n", i, end - 1, timeline.substring(i, end));
                }
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();

                // Display detailed process statistics
                System.out.println("Process Statistics:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println("Process | Arrival | Start | End | Runtime | Response | Wait | Turnaround | Priority");
                System.out.println("----------------------------------------------------------------------------------------------------");

                // Variables for this iteration (obtained from SJFResult)
                double avgResponseTime = result.metrics.avgResponseTime;
                double avgWaitTime = result.metrics.avgWaitTime;
                double avgTurnaroundTime = result.metrics.avgTurnaroundTime;

                // Display each process details and calculate statistics
                for (int i = 0; i < ranProcesses.size(); i++) {
                    Process p = ranProcesses.get(i);

                    float turnaroundTime = p.getTurnaroundTime();
                    float responseTime = p.getResponseTimeValue();
                    float waitTime = p.getWaitTime();

                    // Statistics are already calculated in SJFScheduler, no need to accumulate here
                    // avgResponseTime += responseTime;
                    // avgWaitTime += waitTime;
                    // avgTurnaroundTime += turnaroundTime;

                    // Print process details
                    System.out.printf("   %c    |  %.1f   | %.1f  | %.1f |   %.1f   |   %.1f    | %.1f  |    %.1f     |    %d%n",
                            p.getProcessName(),
                            p.getArrivalTime(),
                            p.getResponseTime(),
                            p.getCompletionTime(),
                            p.getRuntime(),
                            responseTime,
                            waitTime,
                            turnaroundTime,
                            p.getPriority());
                }

                System.out.println("----------------------------------------------------------------------------------------------------");

                // Statistics are already calculated in SJFScheduler, no need to recalculate here
                int numRanProcesses = ranProcesses.size();
                // avgResponseTime /= numRanProcesses;
                // avgWaitTime /= numRanProcesses;
                // avgTurnaroundTime /= numRanProcesses;

                float lastCompletionTime = 0;
                for (Process p : ranProcesses) {
                    if (p.getCompletionTime() > lastCompletionTime) {
                        lastCompletionTime = p.getCompletionTime();
                    }
                }
                double throughput = result.metrics.throughput;

                // Accumulate totals
                totalAvgResponseTime += avgResponseTime;
                totalAvgWaitTime += avgWaitTime;
                totalAvgTurnaroundTime += avgTurnaroundTime;
                totalThroughput += throughput;

                // Display iteration summary
                System.out.println();
                System.out.println("Iteration " + iteration + " Summary:");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.printf("Processes Completed: %d%n", numRanProcesses);
                System.out.printf("Total Quanta: %.0f%n", lastCompletionTime);
                System.out.printf("Average Turnaround Time: %.2f quanta%n", avgTurnaroundTime);
                System.out.printf("Average Wait Time: %.2f quanta%n", avgWaitTime);
                System.out.printf("Average Response Time: %.2f quanta%n", avgResponseTime);
                System.out.printf("Throughput: %.4f processes/quantum%n", throughput);
                System.out.println("====================================================================================================");
                System.out.println();
            }

            // Calculate overall averages
            totalAvgResponseTime /= NUM_ITERATIONS;
            totalAvgWaitTime /= NUM_ITERATIONS;
            totalAvgTurnaroundTime /= NUM_ITERATIONS;
            totalThroughput /= NUM_ITERATIONS;

            // Display final summary statistics
            System.out.println();
            System.out.println("========================================================================================================");
            System.out.println("                    FINAL STATISTICS (Average over " + NUM_ITERATIONS + " iterations)");
            System.out.println("======================================================================================================");
            System.out.println();
            System.out.println("Algorithm:  Shortest Job First (SJF) [Non-Preemptive]");
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.printf("Average Turnaround Time (TAT): %.2f quanta%n", totalAvgTurnaroundTime);
            System.out.printf("Average Wait Time (WT): %.2f quanta%n", totalAvgWaitTime);
            System.out.printf("Average Response Time (RT): %.2f quanta%n", totalAvgResponseTime);
            System.out.printf("Average Throughput: %.4f processes/quantum%n", totalThroughput);
            System.out.println("========================================================================================================");
        }
        catch(FileNotFoundException e) {
            System.err.println("Error: Unable to create output file.");
            e.printStackTrace();
        }

    }
}

