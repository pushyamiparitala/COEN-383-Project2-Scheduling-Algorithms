import java.util.List;
import utilities.Process;
import utilities.WorkloadGenerator;

/**
 * Main class to run FCFS scheduling simulation
 * Runs multiple iterations and calculates average statistics
 */
public class FCFSSimulation {
    
    private static final int NUM_ITERATIONS = 5;
    
    public static void main(String[] args) {
        // Variables to accumulate statistics over all iterations
        double totalAvgTurnaroundTime = 0.0;
        double totalAvgWaitTime = 0.0;
        double totalAvgResponseTime = 0.0;
        double totalThroughput = 0.0;
        
        // Run the simulation for specified number of iterations
        for (int iteration = 1; iteration <= NUM_ITERATIONS; iteration++) {
            // Generate verified workload for this iteration
            List<Process> processes = WorkloadGenerator.generateAndVerifyWorkload();
            
            // Run FCFS scheduling algorithm
            FCFSScheduler.simulateFCFS(processes);
            
            // Display iteration header and process execution order
            System.out.println("Iteration " + iteration + " - First Come First Serve:");
            for (Process p : processes) {
                System.out.print(p.getProcessName());
            }
            System.out.println();
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
            
            // Display table header
            System.out.println("Process Name\t| Arrival Time | Start Time | End Time | Run Time | Response Time | Wait Time | Turn Around Time | Priority |");
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
            
            // Variables to accumulate statistics for this iteration
            double avgResponseTime = 0.0;
            double avgWaitTime = 0.0;
            double avgTurnaroundTime = 0.0;
            
            // Display each process details and calculate statistics
            for (int i = 0; i < processes.size(); i++) {
                Process p = processes.get(i);
                
                float turnaroundTime = p.getTurnaroundTime();
                float responseTime = p.getResponseTimeValue();
                float waitTime = p.getWaitTime();
                
                // Accumulate statistics
                avgResponseTime += responseTime;
                avgWaitTime += waitTime;
                avgTurnaroundTime += turnaroundTime;
                
                // Print process details
                System.out.printf("%15s|%15.1f|%12.1f|%10.1f|%10.1f|%15.1f|%11.1f|%17.1f|%10d|%n",
                    Character.toString((char)('A' + i)),
                    p.getArrivalTime(),
                    p.getResponseTime(),
                    p.getCompletionTime(),
                    p.getRuntime(),
                    responseTime,
                    waitTime,
                    turnaroundTime,
                    p.getPriority());
            }
            
            // Calculate average statistics for this iteration
            avgResponseTime /= processes.size();
            avgWaitTime /= processes.size();
            avgTurnaroundTime /= processes.size();
            
            // Accumulate totals
            totalAvgResponseTime += avgResponseTime;
            totalAvgWaitTime += avgWaitTime;
            totalAvgTurnaroundTime += avgTurnaroundTime;
            
            // Calculate throughput (processes completed per unit time)
            double throughput = (double) processes.size() / processes.get(processes.size() - 1).getCompletionTime();
            totalThroughput += throughput;
            
            // Display iteration averages
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%15s|%15.1f|%12.1f|%10.1f|%10.1f|%15.1f|%11.1f|%17.1f|%10.1f|%n",
                "Average", 0.0, 0.0, 0.0, 0.0, avgResponseTime, avgWaitTime, avgTurnaroundTime, 0.0);
            
            System.out.println();
        }
        
        // Calculate overall averages
        totalAvgResponseTime /= NUM_ITERATIONS;
        totalAvgWaitTime /= NUM_ITERATIONS;
        totalAvgTurnaroundTime /= NUM_ITERATIONS;
        totalThroughput /= NUM_ITERATIONS;
        
        // Display final summary statistics
        System.out.println("The Calculated statistics of the 5 iterations of all algorithms");
        System.out.println("----------------------------------------------------------------");
        System.out.println("        First-come first-served(FCFS) [non-preemptive]:");
        System.out.println("----------------------------------------------------------------");
        System.out.printf("Average Turn Around Time(TAT) : %.1f%n", totalAvgTurnaroundTime);
        System.out.printf("Average Wait Time(WT) : %.1f%n", totalAvgWaitTime);
        System.out.printf("Average Response Time(RT) : %.1f%n", totalAvgResponseTime);
        System.out.printf("Average Throughput : %.1f%n", totalThroughput);
    }
}

