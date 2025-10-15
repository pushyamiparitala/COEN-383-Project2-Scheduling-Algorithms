import java.util.ArrayList;
import java.util.List;
import utilities.Process;

/**
 * First-Come First-Served (FCFS) CPU Scheduling Algorithm
 * FCFS is a non-preemptive scheduling algorithm where processes are executed
 * in the order they arrive in the ready queue
 */
public class FCFSScheduler {
    private List<Process> processes;
    private StringBuilder timeline;
    private int currentQuantum;

    public FCFSScheduler(List<Process> processes) {
        this.processes = new ArrayList<>();
        // Create deep copies to avoid modifying original processes
        for (Process p : processes) {
            this.processes.add(p);
        }
        this.timeline = new StringBuilder();
        this.currentQuantum = 0;
    }
    /**
     * Simulate the FCFS scheduling algorithm
     *
     * @param processes - list of processes to schedule
     */
    public String simulateFCFS() {

        for (Process process : processes) {
            // If CPU is idle and process hasn't arrived yet, fast forward time
            if (currentQuantum < process.getArrivalTime()) {
                for (int i = 0; i < process.getArrivalTime() - currentQuantum; i++) {
                    timeline.append('-');
                }
                currentQuantum = (int)process.getArrivalTime();

            }

            // Process starts execution
            process.setResponseTime(currentQuantum);

            // Process completes execution
            process.setCompletionTime(currentQuantum + process.getRuntime());
            for (int i = 0; i < process.getRuntime(); i++) {
                timeline.append(process.getProcessName());
            }
            // To indicate the process is done.
            process.setRemainingTime(0);

            // Update current time to completion time
            currentQuantum = (int)process.getCompletionTime();
            if (currentQuantum > 99) {
                break;
            }
        }
        return timeline.toString();

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
}