package utilities;

import java.util.Random;

/**
 * Process class representing a process in the CPU scheduling simulation
 * Each process has arrival time, runtime, priority, process ID, completion time, and response time
 */
public class Process {
    private float arrivalTime;
    private float runtime;
    private int priority;
    private int pid;
    private float completionTime;
    private float responseTime;
    private float remainingTime;  // For preemptive algorithms (RR, SRT)

    /**
     * Default constructor - generates random process attributes
     */
    public Process(Random rand) {
        this.arrivalTime = rand.nextInt(100);
        this.runtime = rand.nextInt(10) + 1; // Service time 1-10 quantum
        this.priority = rand.nextInt(4) + 1;
        this.pid = -1;
        this.completionTime = -1.0f;
        this.responseTime = -1.0f;
        this.remainingTime = this.runtime;  // Initially, remaining time equals runtime
    }

    /**
     * Constructor for creating a dummy process
     */
    public Process(int id, int dummy) {
        this.arrivalTime = -1.0f;
        this.runtime = -1.0f;
        this.priority = -1;
        this.pid = -1;
        this.completionTime = -1.0f;
        this.responseTime = -1.0f;
        this.remainingTime = -1.0f;
    }

    // Getters
    public float getArrivalTime() {
        return arrivalTime;
    }

    public float getRuntime() {
        return runtime;
    }

    public int getPriority() {
        return priority;
    }

    public int getPid() {
        return pid;
    }

    public float getCompletionTime() {
        return completionTime;
    }

    public float getResponseTime() {
        return responseTime;
    }

    public float getRemainingTime() {
        return remainingTime;
    }

    // Setters
    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setCompletionTime(float completionTime) {
        this.completionTime = completionTime;
    }

    public void setResponseTime(float responseTime) {
        this.responseTime = responseTime;
    }

    public void setRemainingTime(float remainingTime) {
        this.remainingTime = remainingTime;
    }

    /**
     * Reset remaining time to original runtime (useful for multiple iterations)
     */
    public void resetRemainingTime() {
        this.remainingTime = this.runtime;
    }

    /**
     * Check if process is completed
     */
    public boolean isCompleted() {
        return remainingTime <= 0;
    }

    /**
     * Calculate turnaround time = completion time - arrival time
     */
    public float getTurnaroundTime() {
        return completionTime - arrivalTime;
    }

    /**
     * Calculate response time = response time - arrival time
     */
    public float getResponseTimeValue() {
        return responseTime - arrivalTime;
    }

    /**
     * Calculate wait time = turnaround time - runtime
     */
    public float getWaitTime() {
        float tat = getTurnaroundTime();
        return tat - runtime;
    }

    /**
     * Get process name based on pid.
     */
    public char getProcessName() {
        return (char) ('A' + pid);
    }
}