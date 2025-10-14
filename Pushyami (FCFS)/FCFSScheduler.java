import java.util.List;
import utilities.Process;

/**
 * First-Come First-Served (FCFS) CPU Scheduling Algorithm
 * FCFS is a non-preemptive scheduling algorithm where processes are executed
 * in the order they arrive in the ready queue
 */
public class FCFSScheduler {
    
    /**
     * Simulate the FCFS scheduling algorithm
     * 
     * @param processes - list of processes to schedule
     */
    public static void simulateFCFS(List<Process> processes) {
        float currentTime = 0.0f;
        
        for (Process process : processes) {
            // If CPU is idle and process hasn't arrived yet, fast forward time
            if (currentTime < process.getArrivalTime()) {
                currentTime = process.getArrivalTime();
            }
            
            // Process starts execution
            process.setResponseTime(currentTime);
            
            // Process completes execution
            process.setCompletionTime(currentTime + process.getRuntime());
            
            // Update current time to completion time
            currentTime = process.getCompletionTime();
        }
    }
}
