package com.example.cpuschedulingsimulator.model;

import java.io.Serializable;

/**
 * Represents one visible block in the Gantt chart.
 *
 * Example:
 * processId = "P1"
 * startTime = 0
 * endTime = 5
 */
public class GanttBlock implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String processId;
    private final int startTime;
    private final int endTime;

    //the fields here are final because once created the time cannot change

    public GanttBlock(String processId, int startTime, int endTime) {
        this.processId = processId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getProcessId() {
        return processId;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }


    /**
     * Calculates the length of this block.
     * Duration is derived instead of stored separately.
     */
    public int getDuration() {
        return endTime - startTime;
    }
}
