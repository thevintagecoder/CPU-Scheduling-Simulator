package com.example.cpuschedulingsimulator.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import java.util.List;

/**
 * Shared invariant checks for all scheduling algorithms.
 * Call from any scheduler test to verify common correctness rules.
 */
public final class SchedulerInvariantChecker {

    private SchedulerInvariantChecker() {
    }

    /**
     * Verifies all invariants for a scheduling result.
     */
    public static void checkAllInvariants(
            ScheduleResult result,
            boolean expectMergedGantt
    ) {
        checkPerProcessMetrics(result);
        checkNonNegativeWaitingTimes(result);
        checkGanttContiguity(result, expectMergedGantt);
        checkGanttSpanMatchesMaxCompletion(result);
    }

    /**
     * For every process: TAT == CT - AT, WT == TAT - BT.
     */
    private static void checkPerProcessMetrics(
            ScheduleResult result
    ) {
        for (CpuProcess p : result.getProcesses()) {
            int expectedTAT =
                    p.getCompletionTime() - p.getArrivalTime();
            int expectedWT =
                    expectedTAT - p.getBurstTime();

            assertEquals(
                    p.getProcessId() + " TAT mismatch",
                    expectedTAT,
                    p.getTurnaroundTime()
            );

            assertEquals(
                    p.getProcessId() + " WT mismatch",
                    expectedWT,
                    p.getWaitingTime()
            );
        }
    }

    /**
     * Waiting time must never be negative for valid scheduling.
     */
    private static void checkNonNegativeWaitingTimes(
            ScheduleResult result
    ) {
        for (CpuProcess p : result.getProcesses()) {
            assertTrue(
                    p.getProcessId()
                            + " WT is negative ("
                            + p.getWaitingTime() + ")",
                    p.getWaitingTime() >= 0
            );
        }
    }

    /**
     * Verifies Gantt chart contiguity: end of block N
     * == start of block N+1.
     */
    private static void checkGanttContiguity(
            ScheduleResult result,
            boolean expectMerged
    ) {
        List<GanttBlock> blocks = result.getGanttBlocks();

        for (int i = 1; i < blocks.size(); i++) {
            int prevEnd = blocks.get(i - 1).getEndTime();
            int currStart = blocks.get(i).getStartTime();

            if (expectMerged) {
                assertEquals(
                        "Gantt gap at block " + i,
                        prevEnd,
                        currStart
                );
            } else {
                assertTrue(
                        "Gantt block " + i
                                + " starts before previous ends",
                        currStart >= prevEnd
                );
            }
        }
    }

    /**
     * The last Gantt block end time must equal the
     * maximum completion time across all processes.
     */
    private static void checkGanttSpanMatchesMaxCompletion(
            ScheduleResult result
    ) {
        int maxCT = 0;

        for (CpuProcess p : result.getProcesses()) {
            maxCT = Math.max(maxCT, p.getCompletionTime());
        }

        List<GanttBlock> blocks = result.getGanttBlocks();

        if (!blocks.isEmpty()) {
            int ganttEnd =
                    blocks.get(blocks.size() - 1).getEndTime();

            assertEquals(
                    "Gantt span does not match max CT",
                    maxCT,
                    ganttEnd
            );
        }
    }

    /**
     * Verifies that the average values are computed correctly.
     */
    public static void checkAverages(ScheduleResult result) {
        double totalWT = 0;
        double totalTAT = 0;

        for (CpuProcess p : result.getProcesses()) {
            totalWT += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
        }

        int count = result.getProcesses().size();

        assertEquals(
                "Average WT mismatch",
                totalWT / count,
                result.getAverageWaitingTime(),
                0.001
        );

        assertEquals(
                "Average TAT mismatch",
                totalTAT / count,
                result.getAverageTurnaroundTime(),
                0.001
        );
    }
}
