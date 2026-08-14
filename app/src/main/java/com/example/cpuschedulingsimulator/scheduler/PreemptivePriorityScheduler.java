package com.example.cpuschedulingsimulator.scheduler;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;
import com.example.cpuschedulingsimulator.scheduler.util.GanttChartBuilder;
import com.example.cpuschedulingsimulator.scheduler.util.IdleCpuHelper;
import com.example.cpuschedulingsimulator.scheduler.util.ProcessMetrics;
import com.example.cpuschedulingsimulator.scheduler.util.SchedulerUtils;
import com.example.cpuschedulingsimulator.scheduler.util.TieBreaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Priority Scheduling, preemptive.
 *
 * The CPU advances one time unit at a time. At each step the
 * arrived process with the highest priority runs next.
 *
 * A smaller priority number means a higher priority.
 */
public class PreemptivePriorityScheduler implements Scheduler {

    @Override
    public ScheduleResult schedule(List<CpuProcess> processes) {
        SchedulerUtils.validateInput(processes);

        ArrayList<CpuProcess> resultProcesses =
                SchedulerUtils.copyProcesses(processes);

        Map<String, Integer> inputOrder =
                buildInputOrder(resultProcesses);

        GanttChartBuilder ganttBuilder =
                new GanttChartBuilder();

        int currentTime = 0;
        int completedCount = 0;
        CpuProcess running = null;

        while (completedCount < resultProcesses.size()) {

            List<CpuProcess> arrived =
                    collectArrivedIncomplete(
                            resultProcesses,
                            currentTime
                    );

            if (arrived.isEmpty()) {

                int nextArrivalTime =
                        IdleCpuHelper.findNextArrivalAmongIncomplete(
                                resultProcesses,
                                currentTime
                        );

                ganttBuilder.appendIdlePeriod(
                        currentTime,
                        nextArrivalTime
                );

                currentTime = nextArrivalTime;
                running = null;
                continue;
            }

            CpuProcess selected =
                    TieBreaker.selectHighestPriority(
                            arrived,
                            running,
                            inputOrder
                    );

            running = selected;

            if (running.getStartTime() == -1) {
                running.setStartTime(currentTime);
            }

            ganttBuilder.appendExecutionUnit(
                    running.getProcessId(),
                    currentTime
            );

            running.setRemainingTime(
                    running.getRemainingTime() - 1
            );

            currentTime++;

            if (running.getRemainingTime() == 0) {
                ProcessMetrics.applyCompletionMetrics(
                        running,
                        currentTime
                );

                completedCount++;
                running = null;
            }
        }

        ArrayList<GanttBlock> ganttBlocks =
                ganttBuilder.build();

        return SchedulerUtils.buildResult(
                resultProcesses,
                ganttBlocks
        );
    }

    private Map<String, Integer> buildInputOrder(
            List<CpuProcess> processes
    ) {
        HashMap<String, Integer> inputOrder =
                new HashMap<>();

        for (int i = 0; i < processes.size(); i++) {
            inputOrder.put(
                    processes.get(i).getProcessId(),
                    i
            );
        }

        return inputOrder;
    }

    private List<CpuProcess> collectArrivedIncomplete(
            List<CpuProcess> processes,
            int currentTime
    ) {
        ArrayList<CpuProcess> arrived =
                new ArrayList<>();

        for (CpuProcess process : processes) {
            if (process.getArrivalTime() <= currentTime
                    && process.getRemainingTime() > 0) {

                arrived.add(process);
            }
        }

        return arrived;
    }
}
