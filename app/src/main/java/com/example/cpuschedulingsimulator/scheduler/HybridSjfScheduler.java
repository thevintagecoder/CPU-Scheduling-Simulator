package com.example.cpuschedulingsimulator.scheduler;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Hybrid SJF scheduling, preemptive.
 *
 * The primary selection rule is Shortest Remaining Time First:
 * the arrived, unfinished process with the smallest remaining
 * burst time runs next.
 *
 * When two or more candidates have the same remaining burst
 * time, Priority Scheduling breaks the tie: the process with
 * the smaller priority number (higher priority) is selected.
 *
 * If burst time and priority are both equal, the process that
 * arrived earlier is selected.
 */
public class HybridSjfScheduler implements Scheduler {

    @Override
    public ScheduleResult schedule(List<CpuProcess> processes) {

        validateInput(processes);

        ArrayList<CpuProcess> resultProcesses = copyProcesses(processes);
        ArrayList<CpuProcess> allProcesses = new ArrayList<>(resultProcesses);

        ArrayList<GanttBlock> ganttBlocks = new ArrayList<>();

        int totalProcesses = allProcesses.size();
        int completedProcesses = 0;
        int currentTime = 0;

        String currentBlockProcessId = null;
        int currentBlockStartTime = 0;

        while (completedProcesses < totalProcesses) {

            CpuProcess selectedProcess =
                    selectProcess(allProcesses, currentTime);

            String activeId =
                    (selectedProcess == null)
                            ? "IDLE"
                            : selectedProcess.getProcessId();

            if (!activeId.equals(currentBlockProcessId)) {

                if (currentBlockProcessId != null) {
                    ganttBlocks.add(
                            new GanttBlock(
                                    currentBlockProcessId,
                                    currentBlockStartTime,
                                    currentTime
                            )
                    );
                }

                currentBlockProcessId = activeId;
                currentBlockStartTime = currentTime;
            }

            if (selectedProcess == null) {
                currentTime++;
                continue;
            }

            if (selectedProcess.getStartTime() == -1) {
                selectedProcess.setStartTime(currentTime);
            }

            selectedProcess.setRemainingTime(
                    selectedProcess.getRemainingTime() - 1
            );

            currentTime++;

            if (selectedProcess.getRemainingTime() == 0) {

                int completionTime = currentTime;

                int turnaroundTime =
                        completionTime - selectedProcess.getArrivalTime();

                int waitingTime =
                        turnaroundTime - selectedProcess.getBurstTime();

                selectedProcess.setCompletionTime(completionTime);
                selectedProcess.setTurnaroundTime(turnaroundTime);
                selectedProcess.setWaitingTime(waitingTime);

                completedProcesses++;
            }
        }

        if (currentBlockProcessId != null) {
            ganttBlocks.add(
                    new GanttBlock(
                            currentBlockProcessId,
                            currentBlockStartTime,
                            currentTime
                    )
            );
        }

        return buildResult(resultProcesses, ganttBlocks);
    }

    /**
     * Selects the arrived, unfinished process using, in order:
     *
     * 1. Smallest remaining time (SJF).
     * 2. Smallest priority number (Priority), when remaining
     *    times are equal.
     * 3. Earlier arrival time, when both are equal.
     */
    private CpuProcess selectProcess(
            List<CpuProcess> processes,
            int currentTime
    ) {
        CpuProcess selected = null;

        for (CpuProcess candidate : processes) {

            if (candidate.getArrivalTime() > currentTime
                    || candidate.getRemainingTime() <= 0) {
                continue;
            }

            if (selected == null
                    || isBetterCandidate(candidate, selected)) {
                selected = candidate;
            }
        }

        return selected;
    }

    private boolean isBetterCandidate(
            CpuProcess candidate,
            CpuProcess current
    ) {
        if (candidate.getRemainingTime()
                != current.getRemainingTime()) {
            return candidate.getRemainingTime()
                    < current.getRemainingTime();
        }

        if (candidate.getPriority() != current.getPriority()) {
            return candidate.getPriority() < current.getPriority();
        }

        return candidate.getArrivalTime()
                < current.getArrivalTime();
    }

    private void validateInput(List<CpuProcess> processes) {
        if (processes == null || processes.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one process is required."
            );
        }
    }

    private ArrayList<CpuProcess> copyProcesses(
            List<CpuProcess> processes
    ) {
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

        return new ScheduleResult(
                processes,
                ganttBlocks,
                totalWaitingTime / processes.size(),
                totalTurnaroundTime / processes.size()
        );
    }
}
