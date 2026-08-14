package com.example.cpuschedulingsimulator.scheduler.util;

import static org.junit.Assert.assertEquals;

import com.example.cpuschedulingsimulator.model.CpuProcess;

import org.junit.Test;

public class ProcessMetricsTest {

    @Test
    public void applyCompletionMetrics_setsAllFields() {
        CpuProcess process =
                new CpuProcess("P1", 2, 5, 1);

        ProcessMetrics.applyCompletionMetrics(process, 10);

        assertEquals(10, process.getCompletionTime());
        assertEquals(8, process.getTurnaroundTime());
        assertEquals(3, process.getWaitingTime());
        assertEquals(0, process.getRemainingTime());
    }

    @Test
    public void applyCompletionMetrics_zeroArrivalTime() {
        CpuProcess process =
                new CpuProcess("P1", 0, 4, 1);

        ProcessMetrics.applyCompletionMetrics(process, 7);

        assertEquals(7, process.getCompletionTime());
        assertEquals(7, process.getTurnaroundTime());
        assertEquals(3, process.getWaitingTime());
        assertEquals(0, process.getRemainingTime());
    }

    @Test
    public void applyCompletionMetrics_singleUnitProcess() {
        CpuProcess process =
                new CpuProcess("P1", 3, 1, 1);

        ProcessMetrics.applyCompletionMetrics(process, 4);

        assertEquals(4, process.getCompletionTime());
        assertEquals(1, process.getTurnaroundTime());
        assertEquals(0, process.getWaitingTime());
        assertEquals(0, process.getRemainingTime());
    }
}
