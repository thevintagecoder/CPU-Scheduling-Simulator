package com.example.cpuschedulingsimulator.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RoundRobinSchedulerTest {

    @Test
    public void schedule_usesTimeQuantumAndReadyQueue() {

        ArrayList<CpuProcess> processes =
                new ArrayList<>();

        processes.add(
                new CpuProcess("P1", 0, 5, 0)
        );

        processes.add(
                new CpuProcess("P2", 1, 3, 0)
        );

        processes.add(
                new CpuProcess("P3", 2, 1, 0)
        );

        int timeQuantum = 2;

        RoundRobinScheduler scheduler =
                new RoundRobinScheduler(timeQuantum);

        ScheduleResult result =
                scheduler.schedule(processes);

        CpuProcess p1 = findProcess(result, "P1");
        CpuProcess p2 = findProcess(result, "P2");
        CpuProcess p3 = findProcess(result, "P3");

        assertEquals(0, p1.getStartTime());
        assertEquals(9, p1.getCompletionTime());
        assertEquals(9, p1.getTurnaroundTime());
        assertEquals(4, p1.getWaitingTime());

        assertEquals(2, p2.getStartTime());
        assertEquals(8, p2.getCompletionTime());
        assertEquals(7, p2.getTurnaroundTime());
        assertEquals(4, p2.getWaitingTime());

        assertEquals(4, p3.getStartTime());
        assertEquals(5, p3.getCompletionTime());
        assertEquals(3, p3.getTurnaroundTime());
        assertEquals(2, p3.getWaitingTime());

        assertEquals(
                10.0 / 3.0,
                result.getAverageWaitingTime(),
                0.001
        );

        assertEquals(
                19.0 / 3.0,
                result.getAverageTurnaroundTime(),
                0.001
        );

        List<GanttBlock> blocks =
                result.getGanttBlocks();

        assertEquals(6, blocks.size());

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

        assertEquals(
                "P1",
                blocks.get(3).getProcessId()
        );

        assertEquals(
                "P2",
                blocks.get(4).getProcessId()
        );

        assertEquals(
                "P1",
                blocks.get(5).getProcessId()
        );

        /*
         * Check the first time block.
         */
        assertEquals(
                0,
                blocks.get(0).getStartTime()
        );

        assertEquals(
                2,
                blocks.get(0).getEndTime()
        );
    }

    @Test
    public void constructor_rejectsZeroQuantum() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new RoundRobinScheduler(0)
        );
    }

    @Test
    public void schedule_addsIdleBlockBeforeFirstArrival() {

        ArrayList<CpuProcess> processes =
                new ArrayList<>();

        processes.add(
                new CpuProcess("P1", 3, 2, 0)
        );

        ScheduleResult result =
                new RoundRobinScheduler(1)
                        .schedule(processes);

        List<GanttBlock> blocks =
                result.getGanttBlocks();

        assertEquals(
                "IDLE",
                blocks.get(0).getProcessId()
        );

        assertEquals(
                0,
                blocks.get(0).getStartTime()
        );

        assertEquals(
                3,
                blocks.get(0).getEndTime()
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