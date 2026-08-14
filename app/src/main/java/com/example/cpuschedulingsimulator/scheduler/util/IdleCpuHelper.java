package com.example.cpuschedulingsimulator.scheduler.util;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;

import java.util.List;

/**
 * Handles CPU idle periods when no process has arrived yet.
 */
public final class IdleCpuHelper {

    private IdleCpuHelper() {
    }

    /**
     * Finds the earliest arrival time among processes that have not finished.
     */
    public static int findNextArrivalAmongIncomplete(
            List<CpuProcess> processes,
            int currentTime
    ) {
        int nextArrivalTime = Integer.MAX_VALUE;

        for (CpuProcess process : processes) {
            if (process.getRemainingTime() <= 0) {
                continue;
            }

            if (process.getArrivalTime() > currentTime) {
                nextArrivalTime = Math.min(
                        nextArrivalTime,
                        process.getArrivalTime()
                );
            }
        }

        return nextArrivalTime;
    }

    /**
     * Finds the earliest arrival time in the given process list.
     */
    public static int findNextArrivalTime(
            List<CpuProcess> processes
    ) {
        int nextArrivalTime = Integer.MAX_VALUE;

        for (CpuProcess process : processes) {
            nextArrivalTime = Math.min(
                    nextArrivalTime,
                    process.getArrivalTime()
            );
        }

        return nextArrivalTime;
    }

    public static void addIdleBlockIfNeeded(
            List<GanttBlock> ganttBlocks,
            int currentTime,
            int nextArrivalTime
    ) {
        if (currentTime < nextArrivalTime) {
            ganttBlocks.add(
                    new GanttBlock(
                            "IDLE",
                            currentTime,
                            nextArrivalTime
                    )
            );
        }
    }
}
