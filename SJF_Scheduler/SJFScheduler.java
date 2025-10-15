package SJF_Scheduler;

import utilities.Process;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class SJFScheduler {

    public static class SJFResult {
        public final List<Process> completedProcesses;
        public final String timeChart;
        public final Metrics metrics;

        public SJFResult(List<Process> completedProcesses, String timeChart, Metrics metrics) {
            this.completedProcesses = completedProcesses;
            this.timeChart = timeChart;
            this.metrics = metrics;
        }
    }

    public static class Metrics {
        public float avgTurnaroundTime;
        public float avgWaitTime;
        public float avgResponseTime;
        public float throughput;

        public Metrics(float avgTurnaroundTime, float avgWaitTime, float avgResponseTime, float throughput) {
            this.avgTurnaroundTime = avgTurnaroundTime;
            this.avgWaitTime = avgWaitTime;
            this.avgResponseTime = avgResponseTime;
            this.throughput = throughput;
        }
    }

    public SJFResult schedule(List<Process> processes) {
        List<Process> processesCopy = new ArrayList<>(processes);
        // Sort processes by arrival time initially
        Collections.sort(processesCopy, Comparator.comparing(Process::getArrivalTime));

        // PriorityQueue to hold processes that have arrived, ordered by runtime (SJF)
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(Comparator.comparing(Process::getRuntime));

        float currentTime = 0;
        int completedProcesses = 0;
        int n = processesCopy.size();

        float totalTurnaroundTime = 0;
        float totalWaitTime = 0;
        float totalResponseTime = 0;
        List<Process> completedProcessList = new ArrayList<>();
        StringBuilder timeChart = new StringBuilder();

        // Index for processes that have not yet arrived
        int processIndex = 0;

        while (completedProcesses < n) {
            // Add processes that have arrived by the current time to the ready queue
            while (processIndex < n && processesCopy.get(processIndex).getArrivalTime() <= currentTime) {
                if (processesCopy.get(processIndex).getArrivalTime() >= 100) {
                    processIndex++; // Drop the process
                    continue;
                }
                readyQueue.add(processesCopy.get(processIndex));
                processIndex++;
            }

            if (readyQueue.isEmpty()) {
                // If no processes are in the ready queue, advance time to the arrival of the next process
                if (processIndex < n) {
                    if (processesCopy.get(processIndex).getArrivalTime() >= 100) {
                        break; // Stop scheduling
                    }
                    currentTime = processesCopy.get(processIndex).getArrivalTime();
                } else {
                    // Should not happen if workload is properly generated and verified
                    break;
                }
            } else {
                // Get the process with the shortest runtime from the ready queue (non-preemptive)
                Process currentProcess = readyQueue.poll();

                if (currentTime >= 100) {
                    break; // Stop scheduling if current time exceeds the limit
                }

                // Set response time if not already set (first time CPU is allocated)
                if (currentProcess.getResponseTime() == -1.0f) {
                    currentProcess.setResponseTime(currentTime);
                }

                // Execute the process
                // Execute the process
                float startTime = currentTime;
                currentTime += currentProcess.getRuntime();
                currentProcess.setCompletionTime(currentTime);
                completedProcesses++;
                completedProcessList.add(currentProcess);

                // Record execution for time chart
                for (float i = startTime; i < currentTime; i++) {
                    timeChart.append(currentProcess.getProcessName());
                }

                totalTurnaroundTime += currentProcess.getTurnaroundTime();
                totalWaitTime += currentProcess.getWaitTime();
                totalResponseTime += currentProcess.getResponseTimeValue();
            }
        }

        if (completedProcesses == 0) {
            return new SJFResult(new ArrayList<>(), "", new Metrics(0, 0, 0, 0)); // Avoid division by zero
        }

        float throughput = (float) completedProcesses / currentTime;

        Metrics metrics = new Metrics(
                totalTurnaroundTime / completedProcesses,
                totalWaitTime / completedProcesses,
                totalResponseTime / completedProcesses,
                throughput
        );

        return new SJFResult(completedProcessList, timeChart.toString(), metrics);
    }
}