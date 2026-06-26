package com.cpuscheduler.app.model;

import com.cpuscheduler.app.engine.Algorithm;

import java.util.Collections;
import java.util.List;

/** Result of comparing several algorithms over the same process set. */
public final class CompareResponse {
    public final List<CompareEntry> entries;
    /** Lowest-average-waiting algorithm, or null when there are no entries. */
    public final Algorithm bestKey;
    public final int quantum;

    public CompareResponse(List<CompareEntry> entries, Algorithm bestKey, int quantum) {
        this.entries = Collections.unmodifiableList(entries);
        this.bestKey = bestKey;
        this.quantum = quantum;
    }
}
