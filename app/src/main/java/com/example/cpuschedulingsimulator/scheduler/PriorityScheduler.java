package com.example.cpuschedulingsimulator.scheduler;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Priority Scheduling, non-preemptive.
 *
 * A smaller priority number means a higher priority.
 *
 * Priority 1 is higher than Priority 2.
 */
public class PriorityScheduler implements Scheduler {

    @Override
    public ScheduleResult schedule(
            List<CpuProcess> processes
    ) {
        validateInput(processes);

        ArrayList<CpuProcess> resultProcesses =
                copyProcesses(processes);

        ArrayList<CpuProcess> remainingProcesses =
                new ArrayList<>(resultProcesses);

        ArrayList<GanttBlock> ganttBlocks =
                new ArrayList<>();

        int currentTime = 0;

        while (!remainingProcesses.isEmpty()) {

            CpuProcess selectedProcess = null;

            for (CpuProcess candidate
                    : remainingProcesses) {

                if (candidate.getArrivalTime()
                        <= currentTime) {

                    /*
                     * A smaller number represents
                     * a higher priority.
                     */
                    if (selectedProcess == null
                            || candidate.getPriority()
                            < selectedProcess.getPriority()
                            || (candidate.getPriority()
                            == selectedProcess.getPriority()
                            && candidate.getArrivalTime()
                            < selectedProcess.getArrivalTime())) {

                        selectedProcess = candidate;
                    }
                }
            }

            /*
             * No process has arrived yet.
             */
            if (selectedProcess == null) {

                int nextArrivalTime =
                        findNextArrivalTime(
                                remainingProcesses
                        );

                if (currentTime < nextArrivalTime) {
                    ganttBlocks.add(
                            new GanttBlock(
                                    "IDLE",
                                    currentTime,
                                    nextArrivalTime
                            )
                    );
                }

                currentTime = nextArrivalTime;
                continue;
            }

            int startTime = currentTime;

            int completionTime =
                    startTime
                            + selectedProcess.getBurstTime();

            int turnaroundTime =
                    completionTime
                            - selectedProcess.getArrivalTime();

            int waitingTime =
                    turnaroundTime
                            - selectedProcess.getBurstTime();

            selectedProcess.setStartTime(startTime);
            selectedProcess.setCompletionTime(
                    completionTime
            );
            selectedProcess.setTurnaroundTime(
                    turnaroundTime
            );
            selectedProcess.setWaitingTime(waitingTime);
            selectedProcess.setRemainingTime(0);

            ganttBlocks.add(
                    new GanttBlock(
                            selectedProcess.getProcessId(),
                            startTime,
                            completionTime
                    )
            );

            currentTime = completionTime;

            remainingProcesses.remove(
                    selectedProcess
            );
        }

        return buildResult(
                resultProcesses,
                ganttBlocks
        );
    }

    private int findNextArrivalTime(
            List<CpuProcess> processes
    ) {
        int nextArrivalTime =
                Integer.MAX_VALUE;

        for (CpuProcess process : processes) {
            nextArrivalTime = Math.min(
                    nextArrivalTime,
                    process.getArrivalTime()
            );
        }

        return nextArrivalTime;
    }

    private void validateInput(
            List<CpuProcess> processes
    ) {
        if (processes == null || processes.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one process is required."
            );
        }
    }

    private ArrayList<CpuProcess> copyProcesses(
            List<CpuProcess> processes
    ) {
        ArrayList<CpuProcess> copies =
                new ArrayList<>();

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
            totalWaitingTime +=
                    process.getWaitingTime();

            totalTurnaroundTime +=
                    process.getTurnaroundTime();
        }

        return new ScheduleResult(
                processes,
                ganttBlocks,
                totalWaitingTime / processes.size(),
                totalTurnaroundTime / processes.size()
        );
    }
}