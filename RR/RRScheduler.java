import java.util.ArrayList;
import java.util.List;
import utilities.Process;

/**
 * Round Robin Scheduling algorithm 
 */
public class RRScheduler {
    
    private List<Process> processes;
    private StringBuilder timeline;
    private int currentQuantum;
    private int processIndex;
    
    public RRScheduler(List<Process> processes) {
        this.processes = new ArrayList<>();
        // Create deep copies to avoid modifying original processes
        for (Process p : processes) {
            this.processes.add(p);
        }
        this.timeline = new StringBuilder();
        this.currentQuantum = 0;
        this.processIndex = 0;
    }
    
    /**
     * Simulate the RR scheduling algorithm
     * Returns the timeline string showing which process ran at each quantum
     */
    public String simulate() {
        List<Process> readyQueue = new ArrayList<>();
        int completedProcesses = 0;
        int totalProcesses = processes.size();
        
        // Continue until all processes complete
        while (currentQuantum < 99) {
            // Add all processes that have arrived by current quantum to ready queue
            for (int i = processIndex; i < processes.size(); i++) {
                Process p = processes.get(i);
                if (p.getArrivalTime() <= currentQuantum) {
                    readyQueue.add(p); 
                    processIndex += 1;
                }
                else {
                    break;
                }
            }
            
            // If ready queue is empty, CPU is idle
            if (readyQueue.isEmpty()) {
                // Check if there are any processes that haven't started yet
                // If we're past quantum 99 and queue is empty, stop the simulation
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
            
            // Select process at the beginning of the queue
            Process selectedProcess = getProcessWithShortestRemainingTime(readyQueue);
            
            // If this is the first time the process is getting CPU, set response time
            if (selectedProcess.getResponseTime() == -1) {
                selectedProcess.setResponseTime(currentQuantum);
            }
            
            // Execute process for one quantum
            selectedProcess.setRemainingTime(selectedProcess.getRemainingTime() - 1);
            
            // Add process name to timeline
            timeline.append(selectedProcess.getProcessName());
            
            // If process completed, set completion time and remove from ready queue
            if (selectedProcess.isCompleted()) {
                selectedProcess.setCompletionTime(currentQuantum + 1);
                readyQueue.remove(selectedProcess);
                completedProcesses++;
            }
            
            // Move to next quantum
            currentQuantum++;
        }
        
        return timeline.toString();
    }
    
    /**
     * Find process with shortest remaining time in ready queue
     */
    
    
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
}