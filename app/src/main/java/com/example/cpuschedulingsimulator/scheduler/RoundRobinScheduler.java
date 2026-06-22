package com.example.cpuschedulingsimulator.scheduler;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

/**
 * Round Robin scheduling.
 *
 * Every process receives at most one time quantum
 * during each turn.
 *
 * An unfinished process returns to the end of
 * the ready queue.
 */
public class RoundRobinScheduler implements Scheduler {

    private final int timeQuantum;

    public RoundRobinScheduler(int timeQuantum) {

        if (timeQuantum <= 0) {
            throw new IllegalArgumentException(
                    "Time quantum must be greater than zero."
            );
        }

        this.timeQuantum = timeQuantum;
    }

    @Override
    public ScheduleResult schedule(
            List<CpuProcess> processes
    ) {
        validateInput(processes);

        ArrayList<CpuProcess> resultProcesses =
                copyProcesses(processes);

        /*
         * Processes need to be examined in arrival order
         * when they are added to the ready queue.
         */
        ArrayList<CpuProcess> arrivalOrder =
                new ArrayList<>(resultProcesses);

        Collections.sort(
                arrivalOrder,
                Comparator.comparingInt(
                        CpuProcess::getArrivalTime
                )
        );

        ArrayList<GanttBlock> ganttBlocks =
                new ArrayList<>();

        Queue<CpuProcess> readyQueue =
                new ArrayDeque<>();

        int currentTime = 0;

        /*
         * Position of the next process in arrivalOrder
         * that has not yet been added to the queue.
         */
        int nextArrivalIndex = 0;

        int completedProcesses = 0;

        while (completedProcesses
                < arrivalOrder.size()) {

            /*
             * When the ready queue is empty and the next
             * process arrives in the future, the CPU is idle.
             */
            if (readyQueue.isEmpty()
                    && nextArrivalIndex
                    < arrivalOrder.size()
                    && currentTime
                    < arrivalOrder
                    .get(nextArrivalIndex)
                    .getArrivalTime()) {

                int nextArrivalTime =
                        arrivalOrder
                                .get(nextArrivalIndex)
                                .getArrivalTime();

                ganttBlocks.add(
                        new GanttBlock(
                                "IDLE",
                                currentTime,
                                nextArrivalTime
                        )
                );

                currentTime = nextArrivalTime;
            }

            /*
             * Add all processes that have arrived by
             * the current time.
             */
            while (nextArrivalIndex
                    < arrivalOrder.size()
                    && arrivalOrder
                    .get(nextArrivalIndex)
                    .getArrivalTime()
                    <= currentTime) {

                readyQueue.add(
                        arrivalOrder.get(nextArrivalIndex)
                );

                nextArrivalIndex++;
            }

            if (readyQueue.isEmpty()) {
                continue;
            }

            /*
             * Remove the process at the front of the queue.
             */
            CpuProcess currentProcess =
                    readyQueue.remove();

            /*
             * Record the first time the process receives CPU.
             */
            if (currentProcess.getStartTime() == -1) {
                currentProcess.setStartTime(currentTime);
            }

            /*
             * The process executes for either:
             *
             * - one full quantum, or
             * - its remaining time, when remaining time is smaller.
             */
            int executionTime =
                    Math.min(
                            timeQuantum,
                            currentProcess.getRemainingTime()
                    );

            int blockStartTime = currentTime;

            int blockEndTime =
                    currentTime + executionTime;

            ganttBlocks.add(
                    new GanttBlock(
                            currentProcess.getProcessId(),
                            blockStartTime,
                            blockEndTime
                    )
            );

            currentTime = blockEndTime;

            currentProcess.setRemainingTime(
                    currentProcess.getRemainingTime()
                            - executionTime
            );

            /*
             * Add processes that arrived during this
             * execution period.
             */
            while (nextArrivalIndex
                    < arrivalOrder.size()
                    && arrivalOrder
                    .get(nextArrivalIndex)
                    .getArrivalTime()
                    <= currentTime) {

                readyQueue.add(
                        arrivalOrder.get(nextArrivalIndex)
                );

                nextArrivalIndex++;
            }

            if (currentProcess.getRemainingTime() > 0) {

                /*
                 * The process is unfinished, so return it
                 * to the end of the queue.
                 */
                readyQueue.add(currentProcess);

            } else {

                /*
                 * The process has completed.
                 */
                int completionTime = currentTime;

                int turnaroundTime =
                        completionTime
                                - currentProcess.getArrivalTime();

                int waitingTime =
                        turnaroundTime
                                - currentProcess.getBurstTime();

                currentProcess.setCompletionTime(
                        completionTime
                );

                currentProcess.setTurnaroundTime(
                        turnaroundTime
                );

                currentProcess.setWaitingTime(
                        waitingTime
                );

                completedProcesses++;
            }
        }

        return buildResult(
                resultProcesses,
                ganttBlocks
        );
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