package com.example.cpuschedulingsimulator.scheduler.util;

import static org.junit.Assert.assertEquals;

import com.example.cpuschedulingsimulator.model.GanttBlock;

import org.junit.Test;

import java.util.ArrayList;

public class GanttChartBuilderTest {

    @Test
    public void build_singleBlock() {
        GanttChartBuilder builder = new GanttChartBuilder();
        builder.appendExecutionUnit("P1", 0);

        ArrayList<GanttBlock> blocks = builder.build();

        assertEquals(1, blocks.size());
        assertEquals("P1", blocks.get(0).getProcessId());
        assertEquals(0, blocks.get(0).getStartTime());
        assertEquals(1, blocks.get(0).getEndTime());
    }

    @Test
    public void build_mergesConsecutiveSameProcess() {
        GanttChartBuilder builder = new GanttChartBuilder();
        builder.appendExecutionUnit("P1", 0);
        builder.appendExecutionUnit("P1", 1);
        builder.appendExecutionUnit("P1", 2);

        ArrayList<GanttBlock> blocks = builder.build();

        assertEquals(1, blocks.size());
        assertEquals("P1", blocks.get(0).getProcessId());
        assertEquals(0, blocks.get(0).getStartTime());
        assertEquals(3, blocks.get(0).getEndTime());
    }

    @Test
    public void build_splitsWhenDifferentProcess() {
        GanttChartBuilder builder = new GanttChartBuilder();
        builder.appendExecutionUnit("P1", 0);
        builder.appendExecutionUnit("P1", 1);
        builder.appendExecutionUnit("P2", 2);
        builder.appendExecutionUnit("P2", 3);

        ArrayList<GanttBlock> blocks = builder.build();

        assertEquals(2, blocks.size());
        assertEquals("P1", blocks.get(0).getProcessId());
        assertEquals(0, blocks.get(0).getStartTime());
        assertEquals(2, blocks.get(0).getEndTime());
        assertEquals("P2", blocks.get(1).getProcessId());
        assertEquals(2, blocks.get(1).getStartTime());
        assertEquals(4, blocks.get(1).getEndTime());
    }

    @Test
    public void appendIdlePeriod_addsIdleBlock() {
        GanttChartBuilder builder = new GanttChartBuilder();
        builder.appendExecutionUnit("P1", 0);
        builder.appendIdlePeriod(1, 3);
        builder.appendExecutionUnit("P1", 3);

        ArrayList<GanttBlock> blocks = builder.build();

        assertEquals(3, blocks.size());
        assertEquals("P1", blocks.get(0).getProcessId());
        assertEquals(0, blocks.get(0).getStartTime());
        assertEquals(1, blocks.get(0).getEndTime());
        assertEquals("IDLE", blocks.get(1).getProcessId());
        assertEquals(1, blocks.get(1).getStartTime());
        assertEquals(3, blocks.get(1).getEndTime());
        assertEquals("P1", blocks.get(2).getProcessId());
        assertEquals(3, blocks.get(2).getStartTime());
        assertEquals(4, blocks.get(2).getEndTime());
    }

    @Test
    public void appendIdlePeriod_flushesOpenBlock() {
        GanttChartBuilder builder = new GanttChartBuilder();
        builder.appendExecutionUnit("P1", 0);
        builder.appendIdlePeriod(1, 2);
        builder.appendExecutionUnit("P1", 2);

        ArrayList<GanttBlock> blocks = builder.build();

        assertEquals(3, blocks.size());
        assertEquals("P1", blocks.get(0).getProcessId());
        assertEquals(0, blocks.get(0).getStartTime());
        assertEquals(1, blocks.get(0).getEndTime());
    }

    @Test
    public void build_emptyReturnsEmptyList() {
        GanttChartBuilder builder = new GanttChartBuilder();
        ArrayList<GanttBlock> blocks = builder.build();
        assertEquals(0, blocks.size());
    }

    @Test
    public void build_alternatingProcesses() {
        GanttChartBuilder builder = new GanttChartBuilder();
        builder.appendExecutionUnit("P1", 0);
        builder.appendExecutionUnit("P2", 1);
        builder.appendExecutionUnit("P1", 2);
        builder.appendExecutionUnit("P2", 3);

        ArrayList<GanttBlock> blocks = builder.build();

        assertEquals(4, blocks.size());
        assertEquals("P1", blocks.get(0).getProcessId());
        assertEquals(0, blocks.get(0).getStartTime());
        assertEquals(1, blocks.get(0).getEndTime());
        assertEquals("P2", blocks.get(1).getProcessId());
        assertEquals(1, blocks.get(1).getStartTime());
        assertEquals(2, blocks.get(1).getEndTime());
        assertEquals("P1", blocks.get(2).getProcessId());
        assertEquals(2, blocks.get(2).getStartTime());
        assertEquals(3, blocks.get(2).getEndTime());
        assertEquals("P2", blocks.get(3).getProcessId());
        assertEquals(3, blocks.get(3).getStartTime());
        assertEquals(4, blocks.get(3).getEndTime());
    }
}
