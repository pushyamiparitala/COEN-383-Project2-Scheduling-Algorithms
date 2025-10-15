
import utilities.Process;
import utilities.WorkloadGenerator;
import java.util.List;

public class SJFSimulation {

    public static void main(String[] args) {
        // Generate workload
        List<Process> processes = WorkloadGenerator.generateAndVerifyWorkload(12345);

        // Create scheduler instance
        SJFScheduler scheduler = new SJFScheduler();

        // Run simulation and get metrics
        SJFScheduler.Metrics metrics = scheduler.schedule(processes);

        // Print results
        System.out.println("SJF (Non-Preemptive) Scheduling Simulation Results:");
        System.out.println("--------------------------------------------------");
        System.out.printf("Average Turnaround Time: %.2f%n", metrics.avgTurnaroundTime);
        System.out.printf("Average Wait Time: %.2f%n", metrics.avgWaitTime);
        System.out.printf("Average Response Time: %.2f%n", metrics.avgResponseTime);
    }
}