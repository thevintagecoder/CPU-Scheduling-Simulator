package com.example.cpuschedulingsimulator.scheduler.util;

import com.example.cpuschedulingsimulator.model.CpuProcess;

/**
 * Calculates completion, turnaround, and waiting time for a process.
 */
public final class ProcessMetrics {

    private ProcessMetrics() {
    }

    public static void applyCompletionMetrics(
            CpuProcess process,
            int completionTime
    ) {
        int turnaroundTime =
                completionTime - process.getArrivalTime();

        int waitingTime =
                turnaroundTime - process.getBurstTime();

        process.setCompletionTime(completionTime);
        process.setTurnaroundTime(turnaroundTime);
        process.setWaitingTime(waitingTime);
        process.setRemainingTime(0);
    }
}
