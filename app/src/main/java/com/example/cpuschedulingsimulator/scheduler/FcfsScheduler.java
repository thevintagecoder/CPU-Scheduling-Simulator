package com.example.cpuschedulingsimulator.scheduler;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * First Come First Served scheduling.
 *
 * The process with the earliest arrival time runs first.
 * Once a process starts, it runs until it finishes.
 */
public class FcfsScheduler implements Scheduler {

    @Override
    public ScheduleResult schedule(List<CpuProcess> processes) {
        validateInput(processes);

        // resultProcesses keeps the same order in which the user entered processes.
        ArrayList<CpuProcess> resultProcesses = copyProcesses(processes);

        // executionOrder references the same copied objects, but can be sorted.
        ArrayList<CpuProcess> executionOrder = new ArrayList<>(resultProcesses);

        // Java's object sorting is stable, so equal arrival times keep input order.
        Collections.sort(
                executionOrder,
                Comparator.comparingInt(CpuProcess::getArrivalTime)
        );

        ArrayList<GanttBlock> ganttBlocks = new ArrayList<>();
        int currentTime = 0;

        for (CpuProcess process : executionOrder) {

            // The CPU is idle when no process has arrived yet.
            if (currentTime < process.getArrivalTime()) {
                ganttBlocks.add(
                        new GanttBlock("IDLE", currentTime, process.getArrivalTime())
                );
                currentTime = process.getArrivalTime();
            }

            int startTime = currentTime;
            int completionTime = startTime + process.getBurstTime();
            int turnaroundTime = completionTime - process.getArrivalTime();
            int waitingTime = turnaroundTime - process.getBurstTime();

            process.setStartTime(startTime);
            process.setCompletionTime(completionTime);
            process.setTurnaroundTime(turnaroundTime);
            process.setWaitingTime(waitingTime);
            process.setRemainingTime(0);

            ganttBlocks.add(
                    new GanttBlock(
                            process.getProcessId(),
                            startTime,
                            completionTime
                    )
            );

            currentTime = completionTime;
        }

        return buildResult(resultProcesses, ganttBlocks);
    }

    private void validateInput(List<CpuProcess> processes) {
        if (processes == null || processes.isEmpty()) {
            throw new IllegalArgumentException("At least one process is required.");
        }
    }

    private ArrayList<CpuProcess> copyProcesses(List<CpuProcess> processes) {
        ArrayList<CpuProcess> copies = new ArrayList<>();

        for (CpuProcess process : processes) {
            copies.add(new CpuProcess(process));
        }

        return copies;
    }

    private ScheduleResult buildResult(
            List<CpuProcess> processes,
            List<GanttBlock> ganttBlocks
    ) {
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        for (CpuProcess process : processes) {
            totalWaitingTime += process.getWaitingTime();
            totalTurnaroundTime += process.getTurnaroundTime();
        }

        double averageWaitingTime = totalWaitingTime / processes.size();
        double averageTurnaroundTime = totalTurnaroundTime / processes.size();

        return new ScheduleResult(
                processes,
                ganttBlocks,
                averageWaitingTime,
                averageTurnaroundTime
        );
    }
}
