package com.cpuscheduler.app.model;

import java.util.Collections;
import java.util.List;

/** CPU / ready-queue / completed view derived for a given playback time. */
public final class PlaybackView {
    public enum RunMeta { EXECUTING, IDLE, COMPLETE }

    /** Process on the CPU at the current time, or null when idle. */
    public final Integer runningPid;
    /** Ready-queue process ids, ordered by arrival then id. */
    public final List<Integer> readyIds;
    /** Process ids that have fully completed by the current time. */
    public final List<Integer> doneIds;
    public final RunMeta runMeta;
    /** 0–100 playback progress. */
    public final double progressPct;

    public PlaybackView(Integer runningPid, List<Integer> readyIds, List<Integer> doneIds,
                        RunMeta runMeta, double progressPct) {
        this.runningPid = runningPid;
        this.readyIds = Collections.unmodifiableList(readyIds);
        this.doneIds = Collections.unmodifiableList(doneIds);
        this.runMeta = runMeta;
        this.progressPct = progressPct;
    }
}
