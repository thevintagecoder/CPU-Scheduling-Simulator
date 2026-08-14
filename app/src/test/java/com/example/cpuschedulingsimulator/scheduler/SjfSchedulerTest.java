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

public class SjfSchedulerTest {

    private final SjfScheduler scheduler = new SjfScheduler();

    @Test
    public void schedule_selectsShortestArrivedProcess() {

        ArrayList<CpuProcess> processes = new ArrayList<>();
        processes.add(new CpuProcess("P1", 0, 7, 0));
        processes.add(new CpuProcess("P2", 2, 4, 0));
        processes.add(new CpuProcess("P3", 4, 1, 0));
        processes.add(new CpuProcess("P4", 5, 4, 0));

        ScheduleResult result = scheduler.schedule(processes);

        CpuProcess p1 = findProcess(result, "P1");
        CpuProcess p2 = findProcess(result, "P2");
        CpuProcess p3 = findProcess(result, "P3");
        CpuProcess p4 = findProcess(result, "P4");

        assertEquals(0, p1.getStartTime());
        assertEquals(7, p1.getCompletionTime());
        assertEquals(7, p1.getTurnaroundTime());
        assertEquals(0, p1.getWaitingTime());

        assertEquals(7, p3.getStartTime());
        assertEquals(8, p3.getCompletionTime());
        assertEquals(4, p3.getTurnaroundTime());
        assertEquals(3, p3.getWaitingTime());

        assertEquals(8, p2.getStartTime());
        assertEquals(12, p2.getCompletionTime());
        assertEquals(10, p2.getTurnaroundTime());
        assertEquals(6, p2.getWaitingTime());

        assertEquals(12, p4.getStartTime());
        assertEquals(16, p4.getCompletionTime());
        assertEquals(11, p4.getTurnaroundTime());
        assertEquals(7, p4.getWaitingTime());

        assertEquals(4.0, result.getAverageWaitingTime(), 0.001);
        assertEquals(8.0, result.getAverageTurnaroundTime(), 0.001);

        List<GanttBlock> blocks = result.getGanttBlocks();
        assertEquals(4, blocks.size());
        assertBlock(blocks.get(0), "P1", 0, 7);
        assertBlock(blocks.get(1), "P3", 7, 8);
        assertBlock(blocks.get(2), "P2", 8, 12);
        assertBlock(blocks.get(3), "P4", 12, 16);
    }

    @Test
    public void schedule_addsIdleBlockWhenNecessary() {

        ArrayList<CpuProcess> processes = new ArrayList<>();
        processes.add(new CpuProcess("P1", 3, 2, 0));

        ScheduleResult result = scheduler.schedule(processes);

        List<GanttBlock> blocks = result.getGanttBlocks();
        assertEquals(2, blocks.size());
        assertBlock(blocks.get(0), "IDLE", 0, 3);
        assertBlock(blocks.get(1), "P1", 3, 5);

        CpuProcess p1 = findProcess(result, "P1");
        assertEquals(3, p1.getStartTime());
        assertEquals(5, p1.getCompletionTime());
        assertEquals(2, p1.getTurnaroundTime());
        assertEquals(0, p1.getWaitingTime());
    }

    @Test
    public void schedule_prefersEarlierArrivalWhenBurstTimesEqual() {

        ArrayList<CpuProcess> processes = new ArrayList<>();
        processes.add(new CpuProcess("P1", 0, 3, 0));
        processes.add(new CpuProcess("P2", 1, 3, 0));

        ScheduleResult result = scheduler.schedule(processes);

        List<GanttBlock> blocks = result.getGanttBlocks();
        assertEquals(2, blocks.size());
        assertBlock(blocks.get(0), "P1", 0, 3);
        assertBlock(blocks.get(1), "P2", 3, 6);

        CpuProcess p1 = findProcess(result, "P1");
        CpuProcess p2 = findProcess(result, "P2");
        assertEquals(0, p1.getStartTime());
        assertEquals(3, p1.getCompletionTime());
        assertEquals(3, p2.getStartTime());
        assertEquals(6, p2.getCompletionTime());
    }

    @Test
    public void schedule_singleProcess() {

        ArrayList<CpuProcess> processes = new ArrayList<>();
        processes.add(new CpuProcess("P1", 0, 4, 0));

        ScheduleResult result = scheduler.schedule(processes);

        CpuProcess p1 = findProcess(result, "P1");
        assertEquals(0, p1.getStartTime());
        assertEquals(4, p1.getCompletionTime());
        assertEquals(4, p1.getTurnaroundTime());
        assertEquals(0, p1.getWaitingTime());

        List<GanttBlock> blocks = result.getGanttBlocks();
        assertEquals(1, blocks.size());
        assertBlock(blocks.get(0), "P1", 0, 4);
    }

    @Test
    public void schedule_rejectsNullProcessList() {
        assertThrows(
                IllegalArgumentException.class,
                () -> scheduler.schedule(null)
        );
    }

    @Test
    public void schedule_rejectsEmptyProcessList() {
        assertThrows(
                IllegalArgumentException.class,
                () -> scheduler.schedule(new ArrayList<>())
        );
    }

    @Test
    public void schedule_doesNotModifyOriginalInput() {

        ArrayList<CpuProcess> processes = new ArrayList<>();
        processes.add(new CpuProcess("P1", 0, 3, 0));

        CpuProcess original = processes.get(0);
        int originalRemaining = original.getRemainingTime();

        scheduler.schedule(processes);

        assertEquals(-1, original.getStartTime());
        assertEquals(originalRemaining, original.getRemainingTime());
    }

    private void assertBlock(
            GanttBlock block,
            String processId,
            int startTime,
            int endTime
    ) {
        assertEquals(processId, block.getProcessId());
        assertEquals(startTime, block.getStartTime());
        assertEquals(endTime, block.getEndTime());
    }

    private CpuProcess findProcess(
            ScheduleResult result,
            String processId
    ) {
        for (CpuProcess process : result.getProcesses()) {
            if (processId.equals(process.getProcessId())) {
                return process;
            }
        }

        fail("Process " + processId + " was not found.");
        return null;
    }
}
