package com.example.cpuschedulingsimulator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The complete result returned by a scheduling algorithm.
 */
public class ScheduleResult implements Serializable {

    private static final long serialVersionUID = 1L;

    // Stores the complete output of one scheduling run.
    // The process list contains calculated process values.
    // The Gantt list contains the execution timeline.
    private final ArrayList<CpuProcess> processes;
    private final ArrayList<GanttBlock> ganttBlocks;
    private final double averageWaitingTime;
    private final double averageTurnaroundTime;

    /**
     * Creates the final scheduling result.
     * New list objects are created so the result does not directly store
     * the scheduler's original list containers.
     *
     * The CpuProcess and GanttBlock elements themselves are not copied here.
     */
    public ScheduleResult(
            List<CpuProcess> processes,
            List<GanttBlock> ganttBlocks,
            double averageWaitingTime,
            double averageTurnaroundTime
    ) {
        this.processes = new ArrayList<>(processes);
        this.ganttBlocks = new ArrayList<>(ganttBlocks);
        this.averageWaitingTime = averageWaitingTime;
        this.averageTurnaroundTime = averageTurnaroundTime;
    }

    public List<CpuProcess> getProcesses() {
        return processes;
    }

    public List<GanttBlock> getGanttBlocks() {
        return ganttBlocks;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public double getAverageTurnaroundTime() {
        return averageTurnaroundTime;
    }
}
