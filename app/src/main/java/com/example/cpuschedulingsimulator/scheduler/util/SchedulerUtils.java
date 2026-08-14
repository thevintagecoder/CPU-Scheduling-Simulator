package com.example.cpuschedulingsimulator.scheduler.util;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared helpers used by scheduling algorithms.
 */
public final class SchedulerUtils {

    private SchedulerUtils() {
    }

    public static void validateInput(List<CpuProcess> processes) {
        if (processes == null || processes.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one process is required."
            );
        }
    }

    public static ArrayList<CpuProcess> copyProcesses(
            List<CpuProcess> processes
    ) {
        ArrayList<CpuProcess> copies = new ArrayList<>();

        for (CpuProcess process : processes) {
            copies.add(new CpuProcess(process));
        }

        return copies;
    }

    public static ScheduleResult buildResult(
            List<CpuProcess> processes,
            List<GanttBlock> ganttBlocks
    ) {
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        for (CpuProcess process : processes) {
            totalWaitingTime += process.getWaitingTime();
            totalTurnaroundTime += process.getTurnaroundTime();
        }

        return new ScheduleResult(
                processes,
                ganttBlocks,
                totalWaitingTime / processes.size(),
                totalTurnaroundTime / processes.size()
        );
    }
}
