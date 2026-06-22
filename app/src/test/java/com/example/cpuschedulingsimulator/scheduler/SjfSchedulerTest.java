package com.example.cpuschedulingsimulator.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SjfSchedulerTest {

    @Test
    public void schedule_selectsShortestArrivedProcess() {

        ArrayList<CpuProcess> processes =
                new ArrayList<>();

        processes.add(
                new CpuProcess("P1", 0, 7, 0)
        );

        processes.add(
                new CpuProcess("P2", 2, 4, 0)
        );

        processes.add(
                new CpuProcess("P3", 4, 1, 0)
        );

        processes.add(
                new CpuProcess("P4", 5, 4, 0)
        );

        SjfScheduler scheduler =
                new SjfScheduler();

        ScheduleResult result =
                scheduler.schedule(processes);

        CpuProcess p1 = findProcess(result, "P1");
        CpuProcess p2 = findProcess(result, "P2");
        CpuProcess p3 = findProcess(result, "P3");
        CpuProcess p4 = findProcess(result, "P4");

        // P1 runs from 0 to 7.
        assertEquals(0, p1.getStartTime());
        assertEquals(7, p1.getCompletionTime());
        assertEquals(7, p1.getTurnaroundTime());
        assertEquals(0, p1.getWaitingTime());

        // P3 is the shortest available process at time 7.
        assertEquals(7, p3.getStartTime());
        assertEquals(8, p3.getCompletionTime());
        assertEquals(4, p3.getTurnaroundTime());
        assertEquals(3, p3.getWaitingTime());

        // P2 runs before P4 because it arrived earlier.
        assertEquals(8, p2.getStartTime());
        assertEquals(12, p2.getCompletionTime());
        assertEquals(10, p2.getTurnaroundTime());
        assertEquals(6, p2.getWaitingTime());

        assertEquals(12, p4.getStartTime());
        assertEquals(16, p4.getCompletionTime());
        assertEquals(11, p4.getTurnaroundTime());
        assertEquals(7, p4.getWaitingTime());

        assertEquals(
                4.0,
                result.getAverageWaitingTime(),
                0.001
        );

        assertEquals(
                8.0,
                result.getAverageTurnaroundTime(),
                0.001
        );

        /*
         * Expected Gantt chart:
         *
         * P1: 0 to 7
         * P3: 7 to 8
         * P2: 8 to 12
         * P4: 12 to 16
         */
        List<GanttBlock> blocks =
                result.getGanttBlocks();

        assertEquals(4, blocks.size());

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

        assertEquals(
                "P4",
                blocks.get(3).getProcessId()
        );
    }

    @Test
    public void schedule_addsIdleBlockWhenNecessary() {

        ArrayList<CpuProcess> processes =
                new ArrayList<>();

        processes.add(
                new CpuProcess("P1", 3, 2, 0)
        );

        ScheduleResult result =
                new SjfScheduler().schedule(processes);

        List<GanttBlock> blocks =
                result.getGanttBlocks();

        assertEquals(2, blocks.size());

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

        assertEquals(
                "P1",
                blocks.get(1).getProcessId()
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