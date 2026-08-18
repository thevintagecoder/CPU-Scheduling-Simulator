package com.example.cpuschedulingsimulator.scheduler;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Shortest Job First scheduling, preemptive.
 *
 * Also known as Shortest Remaining Time First (SRTF).
 *
 * At every time unit, the process with the smallest remaining
 * burst time among the processes that have already arrived is
 * selected. A running process is preempted as soon as a newly
 * arrived process has a shorter remaining time.
 */
public class PreemptiveSjfScheduler implements Scheduler {

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

            /*
             * Whenever the CPU switches to a different process
             * (or to/from idle), close the previous Gantt block
             * and start a new one.
             */
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
     * Selects the arrived, unfinished process with the smallest
     * remaining time. Ties are broken by the earlier arrival time.
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
                    || candidate.getRemainingTime()
                    < selected.getRemainingTime()
                    || (candidate.getRemainingTime()
                    == selected.getRemainingTime()
                    && candidate.getArrivalTime()
                    < selected.getArrivalTime())) {

                selected = candidate;
            }
        }

        return selected;
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
