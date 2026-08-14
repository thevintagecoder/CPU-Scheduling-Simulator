package com.example.cpuschedulingsimulator.scheduler.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SchedulerUtilsTest {

    @Test
    public void validateInput_rejectsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SchedulerUtils.validateInput(null)
        );
    }

    @Test
    public void validateInput_rejectsEmpty() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SchedulerUtils.validateInput(new ArrayList<>())
        );
    }

    @Test
    public void validateInput_acceptsValidList() {
        List<CpuProcess> processes = new ArrayList<>();
        processes.add(new CpuProcess("P1", 0, 5, 1));

        SchedulerUtils.validateInput(processes);
    }

    @Test
    public void copyProcesses_createsIndependentCopy() {
        List<CpuProcess> original = new ArrayList<>();
        original.add(new CpuProcess("P1", 0, 5, 1));

        ArrayList<CpuProcess> copies =
                SchedulerUtils.copyProcesses(original);

        assertEquals(1, copies.size());
        assertEquals("P1", copies.get(0).getProcessId());
        assertEquals(0, copies.get(0).getArrivalTime());
        assertEquals(5, copies.get(0).getBurstTime());
        assertEquals(1, copies.get(0).getPriority());

        copies.get(0).setStartTime(10);
        assertEquals(-1, original.get(0).getStartTime());
    }

    @Test
    public void copyProcesses_preservesAllFields() {
        List<CpuProcess> original = new ArrayList<>();
        original.add(new CpuProcess("P1", 2, 7, 3));

        ArrayList<CpuProcess> copies =
                SchedulerUtils.copyProcesses(original);

        CpuProcess copy = copies.get(0);
        assertEquals("P1", copy.getProcessId());
        assertEquals(2, copy.getArrivalTime());
        assertEquals(7, copy.getBurstTime());
        assertEquals(3, copy.getPriority());
        assertEquals(7, copy.getRemainingTime());
        assertEquals(-1, copy.getStartTime());
    }

    @Test
    public void buildResult_computesAverages() {
        List<CpuProcess> processes = new ArrayList<>();
        CpuProcess p1 = new CpuProcess("P1", 0, 5, 0);
        p1.setWaitingTime(3);
        p1.setTurnaroundTime(8);
        CpuProcess p2 = new CpuProcess("P2", 1, 3, 0);
        p2.setWaitingTime(1);
        p2.setTurnaroundTime(4);

        processes.add(p1);
        processes.add(p2);

        List<GanttBlock> ganttBlocks = new ArrayList<>();
        ganttBlocks.add(new GanttBlock("P1", 0, 5));
        ganttBlocks.add(new GanttBlock("P2", 5, 8));

        ScheduleResult result =
                SchedulerUtils.buildResult(processes, ganttBlocks);

        assertEquals(2.0, result.getAverageWaitingTime(), 0.001);
        assertEquals(6.0, result.getAverageTurnaroundTime(), 0.001);
        assertEquals(2, result.getProcesses().size());
        assertEquals(2, result.getGanttBlocks().size());
    }

    @Test
    public void buildResult_singleProcess() {
        List<CpuProcess> processes = new ArrayList<>();
        CpuProcess p1 = new CpuProcess("P1", 0, 5, 0);
        p1.setWaitingTime(0);
        p1.setTurnaroundTime(5);

        processes.add(p1);

        List<GanttBlock> ganttBlocks = new ArrayList<>();
        ganttBlocks.add(new GanttBlock("P1", 0, 5));

        ScheduleResult result =
                SchedulerUtils.buildResult(processes, ganttBlocks);

        assertEquals(0.0, result.getAverageWaitingTime(), 0.001);
        assertEquals(5.0, result.getAverageTurnaroundTime(), 0.001);
    }
}
