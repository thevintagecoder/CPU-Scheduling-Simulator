package com.example.cpuschedulingsimulator.scheduler;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Shortest Job First scheduling, non-preemptive.
 *
 * Among all processes that have already arrived,
 * the process with the smallest burst time is selected.
 */
public class SjfScheduler implements Scheduler {

    @Override
    public ScheduleResult schedule(List<CpuProcess> processes) {

        validateInput(processes);

        /*
         * Copy the input processes so the scheduler does not modify
         * the original objects supplied by the caller.
         */
        ArrayList<CpuProcess> resultProcesses =
                copyProcesses(processes);

        /*
         * This list contains processes that have not yet completed.
         */
        ArrayList<CpuProcess> remainingProcesses =
                new ArrayList<>(resultProcesses);

        ArrayList<GanttBlock> ganttBlocks =
                new ArrayList<>();

        int currentTime = 0;

        /*
         * Continue until every process has been selected and completed.
         */
        while (!remainingProcesses.isEmpty()) {

            CpuProcess selectedProcess = null;

            /*
             * Search for the shortest process that has already arrived.
             */
            for (CpuProcess candidate : remainingProcesses) {

                if (candidate.getArrivalTime() <= currentTime) {

                    /*
                     * Select the candidate when:
                     *
                     * 1. No process has been selected yet, or
                     * 2. Candidate has a shorter burst time, or
                     * 3. Burst times are equal but candidate arrived earlier.
                     */
                    if (selectedProcess == null
                            || candidate.getBurstTime()
                            < selectedProcess.getBurstTime()
                            || (candidate.getBurstTime()
                            == selectedProcess.getBurstTime()
                            && candidate.getArrivalTime()
                            < selectedProcess.getArrivalTime())) {

                        selectedProcess = candidate;
                    }
                }
            }

            /*
             * selectedProcess remains null when no process has arrived yet.
             * In that case, the CPU must remain idle.
             */
            if (selectedProcess == null) {

                int nextArrivalTime =
                        findNextArrivalTime(remainingProcesses);

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
                    startTime + selectedProcess.getBurstTime();

            int turnaroundTime =
                    completionTime
                            - selectedProcess.getArrivalTime();

            int waitingTime =
                    turnaroundTime
                            - selectedProcess.getBurstTime();

            selectedProcess.setStartTime(startTime);
            selectedProcess.setCompletionTime(completionTime);
            selectedProcess.setTurnaroundTime(turnaroundTime);
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

            /*
             * The selected process has finished, so remove it.
             */
            remainingProcesses.remove(selectedProcess);
        }

        return buildResult(resultProcesses, ganttBlocks);
    }

    private int findNextArrivalTime(
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
            totalWaitingTime += process.getWaitingTime();
            totalTurnaroundTime +=
                    process.getTurnaroundTime();
        }

        double averageWaitingTime =
                totalWaitingTime / processes.size();

        double averageTurnaroundTime =
                totalTurnaroundTime / processes.size();

        return new ScheduleResult(
                processes,
                ganttBlocks,
                averageWaitingTime,
                averageTurnaroundTime
        );
    }
}