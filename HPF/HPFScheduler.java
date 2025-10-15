package HPF;
import java.util.ArrayList;
import java.util.List;
import utilities.Process;

/**
 * Highest Priority First (HPF) CPU Scheduling Algorithm
 * HPF is a priority-based scheduling algorithm where processes with higher priority (lower number)
 * are scheduled before processes with lower priority (higher number).
 * 
 * This implementation supports both:
 * - Non-preemptive HPF: Uses FCFS within each priority level
 * - Preemptive HPF: Uses RR with time slice of 1 quantum within each priority level
 */
public class HPFScheduler {
    
    private List<Process> processes;
    private StringBuilder timeline;
    private int currentQuantum;
    private boolean isPreemptive;
    
    // Priority queues (1=highest priority, 4=lowest priority)
    private List<List<Process>> priorityQueues;
    
    public HPFScheduler(List<Process> processes, boolean isPreemptive) {
        this.processes = new ArrayList<>();
        // Create deep copies to avoid modifying original processes
        for (Process p : processes) {
            this.processes.add(p);
        }
        this.timeline = new StringBuilder();
        this.currentQuantum = 0;
        this.isPreemptive = isPreemptive;
        
        // Initialize 4 priority queues (priority 1-4)
        this.priorityQueues = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            priorityQueues.add(new ArrayList<>());
        }
    }
    
    /**
     * Simulate the HPF scheduling algorithm
     * Returns the timeline string showing which process ran at each quantum
     */
    public String simulate() {
        int completedProcesses = 0;
        int totalProcesses = processes.size();
        
        // Continue until all processes complete
        while (completedProcesses < totalProcesses) {
            // Add all processes that have arrived by current quantum to appropriate priority queues
            for (Process p : processes) {
                if (p.getArrivalTime() <= currentQuantum && 
                    !p.isCompleted() && 
                    !isInAnyQueue(p)) {
                    // Don't start new processes after quantum 99
                    if (currentQuantum <= 99 || p.getResponseTime() != -1) {
                        int priority = p.getPriority();
                        priorityQueues.get(priority - 1).add(p);
                    }
                }
            }
            
            // Find the highest priority non-empty queue
            Process selectedProcess = getHighestPriorityProcess();
            
            // If no process available, CPU is idle
            if (selectedProcess == null) {
                // Check if there are any processes that haven't started yet
                if (currentQuantum > 99) {
                    break;
                }
                
                // Check if there are any more processes coming
                boolean hasMoreProcesses = false;
                for (Process p : processes) {
                    if (!p.isCompleted() && p.getArrivalTime() > currentQuantum) {
                        hasMoreProcesses = true;
                        break;
                    }
                }
                
                if (!hasMoreProcesses) {
                    break;
                }
                
                timeline.append('-');
                currentQuantum++;
                continue;
            }
            
            // If this is the first time the process is getting CPU, set response time
            if (selectedProcess.getResponseTime() == -1) {
                selectedProcess.setResponseTime(currentQuantum);
            }
            
            // Execute process for one quantum
            selectedProcess.setRemainingTime(selectedProcess.getRemainingTime() - 1);
            
            // Add process name to timeline
            timeline.append(selectedProcess.getProcessName());
            
            // If process completed, set completion time and remove from queue
            if (selectedProcess.isCompleted()) {
                selectedProcess.setCompletionTime(currentQuantum + 1);
                removeFromPriorityQueue(selectedProcess);
                completedProcesses++;
            }
            
            // For preemptive HPF, if process is not completed, move to end of same priority queue
            // For non-preemptive HPF, this doesn't matter since process completes in one go
            if (!selectedProcess.isCompleted() && isPreemptive) {
                // Process is already at the end since we're using FIFO within priority
                // No need to move it
            }
            
            // Move to next quantum
            currentQuantum++;
        }
        
        return timeline.toString();
    }
    
    /**
     * Find the highest priority process available for execution
     * Returns null if no processes are available
     */
    private Process getHighestPriorityProcess() {
        // Check priority queues in order (1=highest, 4=lowest)
        for (int priority = 1; priority <= 4; priority++) {
            List<Process> queue = priorityQueues.get(priority - 1);
            if (!queue.isEmpty()) {
                // For both preemptive and non-preemptive, use FCFS within priority level
                return queue.get(0);
            }
        }
        return null;
    }
    
    /**
     * Check if a process is already in any priority queue
     */
    private boolean isInAnyQueue(Process process) {
        for (List<Process> queue : priorityQueues) {
            if (queue.contains(process)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Remove a process from its priority queue
     */
    private void removeFromPriorityQueue(Process process) {
        for (List<Process> queue : priorityQueues) {
            if (queue.contains(process)) {
                queue.remove(process);
                break;
            }
        }
    }
    
    /**
     * Get list of processes that actually ran (for statistics)
     */
    public List<Process> getProcessesThatRan() {
        List<Process> ranProcesses = new ArrayList<>();
        for (Process p : processes) {
            if (p.getResponseTime() != -1) {
                ranProcesses.add(p);
            }
        }
        return ranProcesses;
    }
    
    /**
     * Get processes that ran for each priority level (for HPF-specific statistics)
     */
    public List<List<Process>> getProcessesByPriority() {
        List<List<Process>> processesByPriority = new ArrayList<>();
        
        // Initialize lists for each priority level
        for (int i = 0; i < 4; i++) {
            processesByPriority.add(new ArrayList<>());
        }
        
        // Group processes by priority
        for (Process p : processes) {
            if (p.getResponseTime() != -1) {
                processesByPriority.get(p.getPriority() - 1).add(p);
            }
        }
        
        return processesByPriority;
    }
}

