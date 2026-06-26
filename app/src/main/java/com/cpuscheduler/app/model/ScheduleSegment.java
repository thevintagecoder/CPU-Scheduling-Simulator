package com.cpuscheduler.app.model;

/**
 * A contiguous block of the Gantt timeline. {@code pid} is null when {@code idle}
 * is true. {@code end} is exclusive. {@code end} is package-mutable only so the
 * engine can extend a run while collapsing ticks into segments.
 */
public final class ScheduleSegment {
    public final Integer pid;
    public final boolean idle;
    public final int start;
    public int end;

    public ScheduleSegment(Integer pid, boolean idle, int start, int end) {
        this.pid = pid;
        this.idle = idle;
        this.start = start;
        this.end = end;
    }

    public int length() {
        return end - start;
    }
}
