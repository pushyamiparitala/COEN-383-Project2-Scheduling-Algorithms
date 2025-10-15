package utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Unified workload generator that creates the same workload for all algorithms
 * as required by the assignment specification.
 */
public class UnifiedWorkloadGenerator {

    private static final int SIMULATION_TIME = 100;
    private static final int MAX_IDLE_QUANTA = 2;
    private static final long SEED = 0; // Fixed seed for reproducibility as specified in assignment

    /**
     * Generates a single workload that will be used across all algorithms.
     * This ensures fair comparison between different scheduling algorithms.
     *
     * @return A list of processes that meets the criteria, sorted by arrival time.
     */
    public static List<Process> generateUnifiedWorkload() {
        int numberOfJobs = 25; // Start with a reasonable number that should work
        List<Process> processes = generateProcesses(numberOfJobs, SEED);
        
        // Assign process IDs after sorting and validation
        for (int i = 0; i < processes.size(); i++) {
            processes.get(i).setPid(i);
        }

        return processes;
    }

    /**
     * Generates a list of processes with the specified seed.
     *
     * @param totalProcesses The number of processes to generate.
     * @param seed           The seed for the random number generator.
     * @return A list of generated processes, sorted by arrival time.
     */
    private static List<Process> generateProcesses(int totalProcesses, long seed) {
        Random rand = new Random(seed);
        List<Process> processes = new ArrayList<>();
        
        for (int i = 0; i < totalProcesses; i++) {
            Process p = new Process(rand);
            // No process is allowed if start time > 99, so arrival time must be < 100
            if (p.getArrivalTime() < SIMULATION_TIME) {
                processes.add(p);
            }
        }

        // Sort processes by arrival time
        Collections.sort(processes, Comparator.comparing(Process::getArrivalTime));
        
        return processes;
    }

    /**
     * Verifies if the generated workload keeps the CPU busy enough.
     * This simulation assumes a non-preemptive FCFS-like execution for idle time calculation.
     *
     * @param processes The list of processes to verify.
     * @return true if the CPU is never idle for more than MAX_IDLE_QUANTA, false otherwise.
     */
    private static boolean verifyWorkload(List<Process> processes) {
        if (processes.isEmpty()) {
            return false;
        }

        float cpuFreeTime = 0; // Time when the CPU becomes free after processing the previous job

        for (Process currentProcess : processes) {
            float arrivalTime = currentProcess.getArrivalTime();
            float runtime = currentProcess.getRuntime();

            // Calculate when the CPU can start processing the current job
            // It's the maximum of its arrival time and when the CPU becomes free
            float startTime = Math.max(arrivalTime, cpuFreeTime);

            // Calculate idle time before this process starts
            float idleDuration = startTime - cpuFreeTime;

            // If the CPU was idle for more than MAX_IDLE_QUANTA, the workload is not valid
            if (idleDuration > MAX_IDLE_QUANTA) {
                return false;
            }

            // Update the CPU free time after this process completes
            cpuFreeTime = startTime + runtime;
        }

        return true;
    }
}
