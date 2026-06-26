package com.cpuscheduler.app.model;

import java.util.Collections;
import java.util.List;

/** Full result of one scheduling run. */
public final class SimulationResult {
    public final List<ScheduleSegment> segments;
    public final List<ProcessMetrics> rows;
    public final Averages averages;
    public final int totalTime;

    public SimulationResult(List<ScheduleSegment> segments, List<ProcessMetrics> rows,
                            Averages averages, int totalTime) {
        this.segments = Collections.unmodifiableList(segments);
        this.rows = Collections.unmodifiableList(rows);
        this.averages = averages;
        this.totalTime = totalTime;
    }
}
