package com.example.cpuschedulingsimulator.scheduler.util;

import static org.junit.Assert.assertEquals;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class IdleCpuHelperTest {

    @Test
    public void findNextArrivalAmongIncomplete_returnsEarliest() {
        List<CpuProcess> processes = new ArrayList<>();
        processes.add(new CpuProcess("P1", 3, 2, 0));
        processes.add(new CpuProcess("P2", 1, 2, 0));
        processes.add(new CpuProcess("P3", 5, 2, 0));

        int next = IdleCpuHelper.findNextArrivalAmongIncomplete(
                processes, 0
        );

        assertEquals(1, next);
    }

    @Test
    public void findNextArrivalAmongIncomplete_skipsCompleted() {
        List<CpuProcess> processes = new ArrayList<>();
        CpuProcess p1 = new CpuProcess("P1", 3, 2, 0);
        p1.setRemainingTime(0);
        processes.add(p1);
        processes.add(new CpuProcess("P2", 5, 2, 0));

        int next = IdleCpuHelper.findNextArrivalAmongIncomplete(
                processes, 0
        );

        assertEquals(5, next);
    }

    @Test
    public void findNextArrivalAmongIncomplete_skipsAlreadyArrived() {
        List<CpuProcess> processes = new ArrayList<>();
        processes.add(new CpuProcess("P1", 0, 5, 0));
        processes.add(new CpuProcess("P2", 3, 2, 0));

        int next = IdleCpuHelper.findNextArrivalAmongIncomplete(
                processes, 3
        );

        assertEquals(Integer.MAX_VALUE, next);
    }

    @Test
    public void findNextArrivalTime_returnsEarliest() {
        List<CpuProcess> processes = new ArrayList<>();
        processes.add(new CpuProcess("P1", 5, 2, 0));
        processes.add(new CpuProcess("P2", 2, 3, 0));

        int next = IdleCpuHelper.findNextArrivalTime(processes);

        assertEquals(2, next);
    }

    @Test
    public void addIdleBlockIfNeeded_addsBlockWhenGap() {
        List<GanttBlock> blocks = new ArrayList<>();

        IdleCpuHelper.addIdleBlockIfNeeded(blocks, 0, 3);

        assertEquals(1, blocks.size());
        assertEquals("IDLE", blocks.get(0).getProcessId());
        assertEquals(0, blocks.get(0).getStartTime());
        assertEquals(3, blocks.get(0).getEndTime());
    }

    @Test
    public void addIdleBlockIfNeeded_noBlockWhenNoGap() {
        List<GanttBlock> blocks = new ArrayList<>();

        IdleCpuHelper.addIdleBlockIfNeeded(blocks, 3, 3);

        assertEquals(0, blocks.size());
    }

    @Test
    public void addIdleBlockIfNeeded_noBlockWhenNegativeGap() {
        List<GanttBlock> blocks = new ArrayList<>();

        IdleCpuHelper.addIdleBlockIfNeeded(blocks, 5, 3);

        assertEquals(0, blocks.size());
    }
}
