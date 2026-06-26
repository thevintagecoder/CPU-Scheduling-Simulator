package com.example.cpuschedulingsimulator.model;

import java.io.Serializable; //full library given by java

//hence we do not create it with @Override

/**
 * Represents one process entered by the user.
 *
 * The first four fields are input values.
 * The remaining fields are calculated by a scheduling algorithm.
 */
public class CpuProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    // Input values
    //this states the entire process
    private final String processId; //stores the processID
    private final int arrivalTime; //when the process enters the system
    private final int burstTime;
    private final int priority; //we will need it for priority scheduling

    // Calculated values
    //these are not final because scheduler will update them
    private int startTime;
    private int completionTime;
    private int turnaroundTime;
    private int waitingTime;
    private int remainingTime;

    /**
     * Creates a separate process object containing the same input values.
     * Calculated values are reset so a scheduler can perform a fresh calculation.
     */

    //this is the constructor- creates and initializes the object
    public CpuProcess(String processId, int arrivalTime, int burstTime, int priority) {
        this.processId = processId;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;

        // -1 means the process has not started yet.
        this.startTime = -1; //we do not use 0 because process can also start at 0 time

        // Round Robin needs to know how much burst time is still left.
        this.remainingTime = burstTime; //this is for round robin
        //we subtract the burst time everytime so this works that way
    }

    //this is another constructor if we want to copy an object
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

    //we might need the copy constructor to make changes

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
