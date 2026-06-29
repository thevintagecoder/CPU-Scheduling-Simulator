package com.example.cpuschedulingsimulator.model;

import java.io.Serializable;

/**
 * Represents one process entered by the user.
 *
 * The first four fields are input values.
 * The remaining fields are calculated by a scheduling algorithm.
 */
public class CpuProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    // Input values
    private final String processId;
    private final int arrivalTime;
    private final int burstTime;
    private final int priority;

    // Calculated values
    private int startTime;
    private int completionTime;
    private int turnaroundTime;
    private int waitingTime;
    private int remainingTime;

    public CpuProcess(String processId, int arrivalTime, int burstTime, int priority) {
        this.processId = processId;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;

        // -1 means the process has not started yet.
        this.startTime = -1;

        // Round Robin needs to know how much burst time is still left.
        this.remainingTime = burstTime;
    }

    /**
     * Creates a fresh copy of a process.
     * Scheduling classes use this so the original user input is not modified.
     */
    public CpuProcess(CpuProcess other) {
        this(
                other.processId,
                other.arrivalTime,
                other.burstTime,
                other.priority
        );
    }

    public String getProcessId() {
        return processId;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
}
