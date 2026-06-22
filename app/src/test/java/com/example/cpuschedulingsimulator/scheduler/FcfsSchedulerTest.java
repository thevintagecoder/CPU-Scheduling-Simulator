package com.example.cpuschedulingsimulator.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FcfsSchedulerTest {

    @Test
    public void schedule_calculatesCorrectFcfsResult() {

        // Step 1: Create hard-coded processes.
        ArrayList<CpuProcess> processes = new ArrayList<>();

        processes.add(new CpuProcess("P1", 0, 5, 0));
        processes.add(new CpuProcess("P2", 1, 3, 0));
        processes.add(new CpuProcess("P3", 2, 2, 0));

        // Step 2: Create the FCFS scheduler.
        FcfsScheduler scheduler = new FcfsScheduler();

        // Step 3: Run the scheduling algorithm.
        ScheduleResult result = scheduler.schedule(processes);

        // Step 4: Find each calculated process.
        CpuProcess p1 = findProcess(result, "P1");
        CpuProcess p2 = findProcess(result, "P2");
        CpuProcess p3 = findProcess(result, "P3");

        // Step 5: Check P1 calculations.
        assertEquals(0, p1.getStartTime());
        assertEquals(5, p1.getCompletionTime());
        assertEquals(5, p1.getTurnaroundTime());
        assertEquals(0, p1.getWaitingTime());

        // Step 6: Check P2 calculations.
        assertEquals(5, p2.getStartTime());
        assertEquals(8, p2.getCompletionTime());
        assertEquals(7, p2.getTurnaroundTime());
        assertEquals(4, p2.getWaitingTime());

        // Step 7: Check P3 calculations.
        assertEquals(8, p3.getStartTime());
        assertEquals(10, p3.getCompletionTime());
        assertEquals(8, p3.getTurnaroundTime());
        assertEquals(6, p3.getWaitingTime());

        // Step 8: Check average values.
        assertEquals(
                10.0 / 3.0,
                result.getAverageWaitingTime(),
                0.001
        );

        assertEquals(
                20.0 / 3.0,
                result.getAverageTurnaroundTime(),
                0.001
        );

        // Step 9: Check the number of Gantt chart blocks.
        assertEquals(3, result.getGanttBlocks().size());

        // Expected Gantt chart:
        // P1: 0 to 5
        // P2: 5 to 8
        // P3: 8 to 10

        assertEquals(
                "P1",
                result.getGanttBlocks().get(0).getProcessId()
        );

        assertEquals(
                0,
                result.getGanttBlocks().get(0).getStartTime()
        );

        assertEquals(
                5,
                result.getGanttBlocks().get(0).getEndTime()
        );

        assertEquals(
                "P2",
                result.getGanttBlocks().get(1).getProcessId()
        );

        assertEquals(
                "P3",
                result.getGanttBlocks().get(2).getProcessId()
        );
    }

    /**
     * Searches the result list for a process with a particular ID.
     */
    private CpuProcess findProcess(
            ScheduleResult result,
            String processId
    ) {
        List<CpuProcess> calculatedProcesses =
                result.getProcesses();

        for (CpuProcess process : calculatedProcesses) {

            if (processId.equals(process.getProcessId())) {
                return process;
            }
        }

        assertNotNull(
                "Process " + processId + " was not found.",
                null
        );

        return null;
    }
}