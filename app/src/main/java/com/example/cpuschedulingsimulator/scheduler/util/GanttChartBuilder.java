package com.example.cpuschedulingsimulator.scheduler.util;

import com.example.cpuschedulingsimulator.model.GanttBlock;

import java.util.ArrayList;

/**
 * Builds a Gantt chart and merges consecutive blocks for the same process.
 */
public class GanttChartBuilder {

    private final ArrayList<GanttBlock> blocks = new ArrayList<>();

    private String openProcessId;
    private int openStartTime = -1;
    private int openEndTime = -1;

    public void appendExecutionUnit(
            String processId,
            int startTime
    ) {
        appendInterval(processId, startTime, startTime + 1);
    }

    public void appendIdlePeriod(int startTime, int endTime) {
        flushOpenBlock();

        if (startTime < endTime) {
            blocks.add(
                    new GanttBlock("IDLE", startTime, endTime)
            );
        }
    }

    public ArrayList<GanttBlock> build() {
        flushOpenBlock();
        return blocks;
    }

    private void appendInterval(
            String processId,
            int startTime,
            int endTime
    ) {
        if (openProcessId != null
                && openProcessId.equals(processId)
                && openEndTime == startTime) {

            openEndTime = endTime;
            return;
        }

        flushOpenBlock();

        openProcessId = processId;
        openStartTime = startTime;
        openEndTime = endTime;
    }

    private void flushOpenBlock() {
        if (openProcessId == null) {
            return;
        }

        blocks.add(
                new GanttBlock(
                        openProcessId,
                        openStartTime,
                        openEndTime
                )
        );

        openProcessId = null;
        openStartTime = -1;
        openEndTime = -1;
    }
}
