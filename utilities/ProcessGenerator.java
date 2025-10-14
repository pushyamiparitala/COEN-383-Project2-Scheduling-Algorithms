package utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Utility class for generating processes for simulation
 */
public class ProcessGenerator {
    
    /**
     * Generate a list of processes with random attributes
     * Processes are sorted by arrival time
     * 
     * @param totalProcesses - number of processes to generate
     * @param seed - random seed for reproducibility
     * @return List of generated processes
     */
    public static List<Process> generateProcesses(int totalProcesses, int seed) {
        Random rand = new Random(seed);
        List<Process> processes = new ArrayList<>();
        
        // Create processes with random attributes
        for (int i = 0; i < totalProcesses; i++) {
            processes.add(new Process(rand));
        }
        
        // Sort processes by arrival time
        Collections.sort(processes, new Comparator<Process>() {
            @Override
            public int compare(Process p1, Process p2) {
                return Float.compare(p1.getArrivalTime(), p2.getArrivalTime());
            }
        });
        
        // Assign process IDs after sorting
        for (int i = 0; i < totalProcesses; i++) {
            processes.get(i).setPid(i);
        }
        
        return processes;
    }
    
    /**
     * Create a dummy process
     */
    public static Process getDummyProcess() {
        return new Process(-1, 1);
    }
}

