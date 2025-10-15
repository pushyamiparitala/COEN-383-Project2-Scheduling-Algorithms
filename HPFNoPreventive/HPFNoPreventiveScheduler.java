package HPFNoPreventive;

import java.util.*;
import utilities.Process;

/**
 * Highest Priority First (Non-Preemptive) Scheduling (no Round Robin)
 */
public class HPFNoPreventiveScheduler {

    private List<Process> processes;
    private StringBuilder timeline;
    private int currentTime;
    private int processIndex;

    // 4 priority queues (highest=1, lowest=4)
    private List<Queue<Process>> priorityQueues;

    public HPFNoPreventiveScheduler(List<Process> processes) {
        this.processes = new ArrayList<>();
        for (Process p : processes) {
            this.processes.add(p);
        }
        this.timeline = new StringBuilder();
        this.currentTime = 0;
        this.processIndex = 0;

        priorityQueues = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            priorityQueues.add(new LinkedList<>());
        }
    }

    public String simulate() {
        int totalProcesses = processes.size();
        int completed = 0;

        while (completed < totalProcesses) {
            //Add newly arrived processes to their corresponding priority queue
            for (int i = processIndex; i < processes.size(); i++) {
                Process p = processes.get(i);
                if (p.getArrivalTime() <= currentTime) {
                    int priority = Math.max(1, Math.min(4, p.getPriority()));
                    priorityQueues.get(priority - 1).add(p);
                    processIndex++;
                } else {
                    break;
                }
            }

            // Find the highest priority non-empty queue
            Process currentProcess = null;
            int currentPriority = -1;
            for (int i = 0; i < 4; i++) {
                if (!priorityQueues.get(i).isEmpty()) {
                    currentProcess = priorityQueues.get(i).poll();
                    currentPriority = i + 1;
                    break;
                }
            }

            // Dealing with idle CPU
            if (currentProcess == null) {
                if (currentTime > 99) {
                    break;
                }
                timeline.append('-');
                currentTime++;
                continue;
            }

            // Set response time
            if (currentProcess.getResponseTime() == -1) {
                if (currentTime > 99) {
                    priorityQueues.get(currentPriority - 1).remove(currentProcess);
                    continue;
                }
                currentProcess.setResponseTime(currentTime);
            }

            //Run process to completion
            while (!currentProcess.isCompleted()) {
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
                timeline.append(currentProcess.getProcessName());
                currentTime++;
            }

            currentProcess.setCompletionTime(currentTime);
            completed++;
        }

        return timeline.toString();
    }

    public List<Process> getProcessesThatRan() {
        List<Process> ran = new ArrayList<>();
        for (Process p : processes) {
            if (p.getResponseTime() != -1) {
                ran.add(p);
            }
        }
        return ran;
    }
}
