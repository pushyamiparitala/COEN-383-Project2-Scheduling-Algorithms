package HPF;
import java.util.List;
import utilities.Process;
import utilities.UnifiedWorkloadGenerator;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Main class to run HPF (Highest Priority First) scheduling simulation
 * Runs both non-preemptive and preemptive HPF algorithms
 * Runs multiple iterations and calculates average statistics
 */
public class HPFSimulation {

    private static final int NUM_ITERATIONS = 5;

    public static void main(String[] args) {
        try {
            // Create a PrintStream that writes to a file
            PrintStream out = new PrintStream("HPFSimulationOutput.txt");

            // Redirect System.out to file
            System.setOut(out);
            System.out.println("=======================================================================================================");
            System.out.println("                    HIGHEST PRIORITY FIRST (HPF) SCHEDULING SIMULATION");
            System.out.println("========================================================================================================");
            System.out.println();

            // Run both non-preemptive and preemptive HPF
            runHPFAlgorithm(false); // Non-preemptive HPF
            runHPFAlgorithm(true);  // Preemptive HPF
        }
        catch(FileNotFoundException e) {
            System.err.println("Error: Unable to create output file.");
            e.printStackTrace();
        }
    }
    
    /**
     * Run HPF algorithm (either non-preemptive or preemptive)
     */
    private static void runHPFAlgorithm(boolean isPreemptive) {
        String algorithmName = isPreemptive ? "HPF Preemptive" : "HPF Non-Preemptive";
        String algorithmType = isPreemptive ? "[Preemptive]" : "[Non-Preemptive]";
        
        // Variables to accumulate statistics over all iterations
        double totalAvgTurnaroundTime = 0.0;
        double totalAvgWaitTime = 0.0;
        double totalAvgResponseTime = 0.0;
        double totalThroughput = 0.0;

        // Run the simulation for specified number of iterations
        for (int iteration = 1; iteration <= NUM_ITERATIONS; iteration++) {
            // Use the same workload for all algorithms as required by assignment
            List<Process> processes = UnifiedWorkloadGenerator.generateUnifiedWorkload();

            // Run HPF scheduling algorithm
            HPFScheduler scheduler = new HPFScheduler(processes, isPreemptive);
            String timeline = scheduler.simulate();

            // Get processes that actually ran
            List<Process> ranProcesses = scheduler.getProcessesThatRan();
            List<List<Process>> processesByPriority = scheduler.getProcessesByPriority();

            // Display iteration header
            System.out.println("========================================================================================================");
            System.out.println("ITERATION " + iteration + " - " + algorithmName + " " + algorithmType);
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

                // Print process details with integer quanta and float values in brackets
                System.out.printf("   %c    |  %d (%.1f) | %d (%.1f)  | %d (%.1f) |   %.1f   |   %d (%.1f)    | %d (%.1f)  |    %d (%.1f)     |    %d%n",
                        p.getProcessName(),
                        (int)p.getArrivalTime(), p.getArrivalTime(),
                        (int)p.getResponseTime(), p.getResponseTime(),
                        (int)p.getCompletionTime(), p.getCompletionTime(),
                        p.getRuntime(),
                        (int)responseTime, responseTime,
                        (int)waitTime, waitTime,
                        (int)turnaroundTime, turnaroundTime,
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

            // Display priority-level statistics for HPF
            System.out.println();
            System.out.println("Priority-Level Statistics:");
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.println("Priority | Processes | Avg TAT | Avg WT | Avg RT | Throughput");
            System.out.println("----------------------------------------------------------------------------------------------------");
            
            for (int priority = 1; priority <= 4; priority++) {
                List<Process> priorityProcesses = processesByPriority.get(priority - 1);
                if (!priorityProcesses.isEmpty()) {
                    double priorityAvgTAT = 0.0;
                    double priorityAvgWT = 0.0;
                    double priorityAvgRT = 0.0;
                    
                    for (Process p : priorityProcesses) {
                        priorityAvgTAT += p.getTurnaroundTime();
                        priorityAvgWT += p.getWaitTime();
                        priorityAvgRT += p.getResponseTimeValue();
                    }
                    
                    priorityAvgTAT /= priorityProcesses.size();
                    priorityAvgWT /= priorityProcesses.size();
                    priorityAvgRT /= priorityProcesses.size();
                    double priorityThroughput = priorityProcesses.size() / lastCompletionTime;
                    
                    System.out.printf("    %d     |     %d     |  %d (%.2f)   |  %d (%.2f)  |  %d (%.2f)  |   %.4f%n",
                            priority,
                            priorityProcesses.size(),
                            (int)priorityAvgTAT, priorityAvgTAT,
                            (int)priorityAvgWT, priorityAvgWT,
                            (int)priorityAvgRT, priorityAvgRT,
                            priorityThroughput);
                } else {
                    System.out.printf("    %d     |     0     |   N/A   |  N/A  |  N/A  |   0.0000%n", priority);
                }
            }
            System.out.println("----------------------------------------------------------------------------------------------------");

            // Display iteration summary
            System.out.println();
            System.out.println("Iteration " + iteration + " Summary:");
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.printf("Processes Completed: %d%n", numRanProcesses);
            System.out.printf("Total Quanta: %.0f%n", lastCompletionTime);
            System.out.printf("Average Turnaround Time: %d (%.2f) quanta%n", (int)avgTurnaroundTime, avgTurnaroundTime);
            System.out.printf("Average Wait Time: %d (%.2f) quanta%n", (int)avgWaitTime, avgWaitTime);
            System.out.printf("Average Response Time: %d (%.2f) quanta%n", (int)avgResponseTime, avgResponseTime);
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
        System.out.println("Algorithm: " + algorithmName + " " + algorithmType);
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.printf("Average Turnaround Time (TAT): %d (%.2f) quanta%n", (int)totalAvgTurnaroundTime, totalAvgTurnaroundTime);
        System.out.printf("Average Wait Time (WT): %d (%.2f) quanta%n", (int)totalAvgWaitTime, totalAvgWaitTime);
        System.out.printf("Average Response Time (RT): %d (%.2f) quanta%n", (int)totalAvgResponseTime, totalAvgResponseTime);
        System.out.printf("Average Throughput: %.4f processes/quantum%n", totalThroughput);
        System.out.println("========================================================================================================");
        System.out.println();
        System.out.println();
    }
}
