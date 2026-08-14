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

public class SrtfSchedulerTest {

    private final SrtfScheduler scheduler = new SrtfScheduler();

    @Test
    public void schedule_basicPreemptiveExample() {

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
        assertEquals(16, p1.getCompletionTime());
        assertEquals(16, p1.getTurnaroundTime());
        assertEquals(9, p1.getWaitingTime());

        assertEquals(2, p2.getStartTime());
        assertEquals(7, p2.getCompletionTime());
        assertEquals(5, p2.getTurnaroundTime());
        assertEquals(1, p2.getWaitingTime());

        assertEquals(4, p3.getStartTime());
        assertEquals(5, p3.getCompletionTime());
        assertEquals(1, p3.getTurnaroundTime());
        assertEquals(0, p3.getWaitingTime());

        assertEquals(7, p4.getStartTime());
        assertEquals(11, p4.getCompletionTime());
        assertEquals(6, p4.getTurnaroundTime());
        assertEquals(2, p4.getWaitingTime());

        assertEquals(3.0, result.getAverageWaitingTime(), 0.001);
        assertEquals(7.0, result.getAverageTurnaroundTime(), 0.001);

        List<GanttBlock> blocks = result.getGanttBlocks();

        assertEquals(6, blocks.size());

        assertBlock(blocks.get(0), "P1", 0, 2);
        assertBlock(blocks.get(1), "P2", 2, 4);
        assertBlock(blocks.get(2), "P3", 4, 5);
        assertBlock(blocks.get(3), "P2", 5, 7);
        assertBlock(blocks.get(4), "P4", 7, 11);
        assertBlock(blocks.get(5), "P1", 11, 16);
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
    public void schedule_handlesSimultaneousArrivals() {

        ArrayList<CpuProcess> processes = new ArrayList<>();

        processes.add(new CpuProcess("P1", 0, 5, 0));
        processes.add(new CpuProcess("P2", 0, 3, 0));

        ScheduleResult result = scheduler.schedule(processes);

        CpuProcess p1 = findProcess(result, "P1");
        CpuProcess p2 = findProcess(result, "P2");

        assertEquals(0, p2.getStartTime());
        assertEquals(3, p2.getCompletionTime());

        assertEquals(3, p1.getStartTime());
        assertEquals(8, p1.getCompletionTime());

        List<GanttBlock> blocks = result.getGanttBlocks();

        assertEquals(2, blocks.size());
        assertBlock(blocks.get(0), "P2", 0, 3);
        assertBlock(blocks.get(1), "P1", 3, 8);
    }

    @Test
    public void schedule_breaksEqualRemainingTimeByArrival() {

        ArrayList<CpuProcess> processes = new ArrayList<>();

        processes.add(new CpuProcess("P1", 0, 4, 0));
        processes.add(new CpuProcess("P2", 2, 2, 0));

        ScheduleResult result = scheduler.schedule(processes);

        List<GanttBlock> blocks = result.getGanttBlocks();

        assertEquals(2, blocks.size());
        assertBlock(blocks.get(0), "P1", 0, 4);
        assertBlock(blocks.get(1), "P2", 4, 6);

        CpuProcess p1 = findProcess(result, "P1");
        CpuProcess p2 = findProcess(result, "P2");

        assertEquals(0, p1.getStartTime());
        assertEquals(4, p1.getCompletionTime());

        assertEquals(4, p2.getStartTime());
        assertEquals(6, p2.getCompletionTime());
    }

    @Test
    public void schedule_breaksEqualRemainingTimeByInputOrder() {

        ArrayList<CpuProcess> processes = new ArrayList<>();

        processes.add(new CpuProcess("P1", 0, 3, 0));
        processes.add(new CpuProcess("P2", 0, 3, 0));

        ScheduleResult result = scheduler.schedule(processes);

        List<GanttBlock> blocks = result.getGanttBlocks();

        assertEquals(2, blocks.size());
        assertBlock(blocks.get(0), "P1", 0, 3);
        assertBlock(blocks.get(1), "P2", 3, 6);
    }

    @Test
    public void schedule_keepsCurrentProcessWhenFullyTied() {

        ArrayList<CpuProcess> processes = new ArrayList<>();

        processes.add(new CpuProcess("P1", 0, 2, 0));
        processes.add(new CpuProcess("P2", 0, 2, 0));

        ScheduleResult result = scheduler.schedule(processes);

        List<GanttBlock> blocks = result.getGanttBlocks();

        assertEquals(2, blocks.size());
        assertBlock(blocks.get(0), "P1", 0, 2);
        assertBlock(blocks.get(1), "P2", 2, 4);
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
    public void schedule_allProcessesArriveTogether() {

        ArrayList<CpuProcess> processes = new ArrayList<>();

        processes.add(new CpuProcess("P1", 0, 2, 0));
        processes.add(new CpuProcess("P2", 0, 4, 0));
        processes.add(new CpuProcess("P3", 0, 1, 0));

        ScheduleResult result = scheduler.schedule(processes);

        List<GanttBlock> blocks = result.getGanttBlocks();

        assertEquals(3, blocks.size());
        assertBlock(blocks.get(0), "P3", 0, 1);
        assertBlock(blocks.get(1), "P1", 1, 3);
        assertBlock(blocks.get(2), "P2", 3, 7);
    }

    @Test
    public void schedule_preemptsWhenShorterProcessArrivesDuringExecution() {

        ArrayList<CpuProcess> processes = new ArrayList<>();

        processes.add(new CpuProcess("P1", 0, 8, 0));
        processes.add(new CpuProcess("P2", 1, 4, 0));

        ScheduleResult result = scheduler.schedule(processes);

        List<GanttBlock> blocks = result.getGanttBlocks();

        assertEquals(3, blocks.size());
        assertBlock(blocks.get(0), "P1", 0, 1);
        assertBlock(blocks.get(1), "P2", 1, 5);
        assertBlock(blocks.get(2), "P1", 5, 12);

        CpuProcess p1 = findProcess(result, "P1");
        CpuProcess p2 = findProcess(result, "P2");

        assertEquals(0, p1.getStartTime());
        assertEquals(12, p1.getCompletionTime());
        assertEquals(12, p1.getTurnaroundTime());
        assertEquals(4, p1.getWaitingTime());

        assertEquals(1, p2.getStartTime());
        assertEquals(5, p2.getCompletionTime());
        assertEquals(4, p2.getTurnaroundTime());
        assertEquals(0, p2.getWaitingTime());
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
