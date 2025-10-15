package SJF_Scheduler;
import utilities.Process;
import utilities.WorkloadGenerator;
import java.util.List;

public class SJFSimulation {

    public static void main(String[] args) {
        // Generate workload
        List<Process> processes = WorkloadGenerator.generateAndVerifyWorkload(100);

        // Create scheduler instance
        SJFScheduler scheduler = new SJFScheduler();

        // Run simulation and get metrics
        // Run simulation and get metrics
        SJFScheduler.SJFResult result = scheduler.schedule(processes);

        // Print process details
        System.out.println("SJF (Non-Preemptive) Scheduling Simulation Results:");
        System.out.println("--------------------------------------------------");
        System.out.println("Process Details:");
        System.out.println("Name | Arrival Time | Run Time");
        for (Process p : processes) {
            System.out.printf("%-4s | %-12.2f | %-8.2f%n",
                p.getProcessName(), p.getArrivalTime(), p.getRuntime());
        }
        System.out.println();

        // Print time chart
        System.out.println("Time Chart:");
        System.out.println(result.timeChart);
        System.out.println();

        // Print calculated statistics for each process
        System.out.println("Process Statistics:");
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("Process | Arrival | Start | End   | Runtime | Response | Wait  | Turnaround");
        System.out.println("------------------------------------------------------------------------------------------");
        for (Process p : result.completedProcesses) {
            float startTime = p.getCompletionTime() - p.getRuntime() - p.getWaitTime();
            System.out.printf("%-7c | %-7.1f | %-5.1f | %-5.1f | %-7.1f | %-8.1f | %-5.1f | %-10.1f%n",
                p.getProcessName(), p.getArrivalTime(), startTime, p.getCompletionTime(), p.getRuntime(),
                p.getResponseTimeValue(), p.getWaitTime(), p.getTurnaroundTime());
        }
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println();


        // Print overall statistics
        System.out.println("Calculated Statistics:");
        System.out.println("----------------------");
        System.out.printf("Average Turnaround Time: %.2f%n", result.metrics.avgTurnaroundTime);
        System.out.printf("Average Wait Time: %.2f%n", result.metrics.avgWaitTime);
        System.out.printf("Average Response Time: %.2f%n", result.metrics.avgResponseTime);
        System.out.printf("Throughput: %.2f processes/quantum%n", result.metrics.throughput);
    }
}