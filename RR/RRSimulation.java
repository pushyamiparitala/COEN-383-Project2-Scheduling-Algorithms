package RR;
import java.util.List;
import utilities.Process;
import utilities.ProcessGenerator;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Main class to run SRT (Shortest Remaining Time) scheduling simulation
 * Runs multiple iterations and calculates average statistics
 */
public class RRSimulation {

    private static final int NUM_ITERATIONS = 5;
    private static final int NUM_PROCESSES = 50; // Generate enough to keep CPU busy

    public static void main(String[] args) {
        try {
            // Create a PrintStream that writes to a file
            PrintStream out = new PrintStream("RRSimulationOutput.txt");

            // Redirect System.out to file
            System.setOut(out);
            System.out.println("========================================================================================================");
            System.out.println("                    Round Robin (RR) SCHEDULING SIMULATION");
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
                List<Process> processes = ProcessGenerator.generateProcesses(NUM_PROCESSES, seed);

                // Run SRT scheduling algorithm
                RRScheduler scheduler = new RRScheduler(processes);
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
                            (char)('A' + i),
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
                            (char)('A' + i),
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
            System.out.println("=======================================================================================================");
            System.out.println();
            System.out.println("Algorithm: Shortest Remaining Time (SRT) [Preemptive]");
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

