package com.example.cpuschedulingsimulator.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PrioritySchedulerTest {

    @Test
    public void schedule_selectsHighestPriorityProcess() {

        ArrayList<CpuProcess> processes =
                new ArrayList<>();

        /*
         * Smaller priority number means higher priority.
         */
        processes.add(
                new CpuProcess("P1", 0, 4, 2)
        );

        processes.add(
                new CpuProcess("P2", 1, 3, 1)
        );

        processes.add(
                new CpuProcess("P3", 2, 2, 3)
        );

        ScheduleResult result =
                new PriorityScheduler().schedule(processes);

        CpuProcess p1 = findProcess(result, "P1");
        CpuProcess p2 = findProcess(result, "P2");
        CpuProcess p3 = findProcess(result, "P3");

        /*
         * P1 begins at time 0 because it is the
         * only available process.
         *
         * P2 cannot interrupt P1 because the
         * algorithm is non-preemptive.
         */
        assertEquals(0, p1.getStartTime());
        assertEquals(4, p1.getCompletionTime());
        assertEquals(4, p1.getTurnaroundTime());
        assertEquals(0, p1.getWaitingTime());

        /*
         * At time 4, P2 and P3 are available.
         * P2 has priority 1, so it runs first.
         */
        assertEquals(4, p2.getStartTime());
        assertEquals(7, p2.getCompletionTime());
        assertEquals(6, p2.getTurnaroundTime());
        assertEquals(3, p2.getWaitingTime());

        assertEquals(7, p3.getStartTime());
        assertEquals(9, p3.getCompletionTime());
        assertEquals(7, p3.getTurnaroundTime());
        assertEquals(5, p3.getWaitingTime());

        assertEquals(
                8.0 / 3.0,
                result.getAverageWaitingTime(),
                0.001
        );

        assertEquals(
                17.0 / 3.0,
                result.getAverageTurnaroundTime(),
                0.001
        );

        List<GanttBlock> blocks =
                result.getGanttBlocks();

        assertEquals(3, blocks.size());

        assertEquals(
                "P1",
                blocks.get(0).getProcessId()
        );

        assertEquals(
                "P2",
                blocks.get(1).getProcessId()
        );

        assertEquals(
                "P3",
                blocks.get(2).getProcessId()
        );
    }

    @Test
    public void schedule_usesArrivalTimeWhenPrioritiesMatch() {

        ArrayList<CpuProcess> processes =
                new ArrayList<>();

        processes.add(
                new CpuProcess("P1", 0, 2, 1)
        );

        processes.add(
                new CpuProcess("P2", 1, 2, 2)
        );

        processes.add(
                new CpuProcess("P3", 0, 2, 2)
        );

        ScheduleResult result =
                new PriorityScheduler().schedule(processes);

        List<GanttBlock> blocks =
                result.getGanttBlocks();

        /*
         * P2 and P3 have the same priority.
         * P3 arrived earlier, so it runs first.
         */
        assertEquals(
                "P1",
                blocks.get(0).getProcessId()
        );

        assertEquals(
                "P3",
                blocks.get(1).getProcessId()
        );

        assertEquals(
                "P2",
                blocks.get(2).getProcessId()
        );
    }

    private CpuProcess findProcess(
            ScheduleResult result,
            String processId
    ) {
        for (CpuProcess process
                : result.getProcesses()) {

            if (processId.equals(
                    process.getProcessId()
            )) {
                return process;
            }
        }

        fail(
                "Process "
                        + processId
                        + " was not found."
        );

        return null;
    }
}