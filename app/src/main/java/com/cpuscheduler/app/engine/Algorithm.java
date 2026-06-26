package com.cpuscheduler.app.engine;

/**
 * The five scheduling policies. {@code name()} is the canonical key
 * (FCFS/SJF/SRTF/RR/PRI), matching the original TypeScript engine.
 */
public enum Algorithm {
    FCFS("FCFS", "First Come First Serve", false),
    SJF("SJF", "Shortest Job First", false),
    SRTF("SRTF", "Shortest Remaining Time", true),
    RR("RR", "Round Robin", true),
    PRI("Priority", "Priority Scheduling", false);

    public final String shortLabel;
    public final String fullLabel;
    public final boolean preemptive;

    Algorithm(String shortLabel, String fullLabel, boolean preemptive) {
        this.shortLabel = shortLabel;
        this.fullLabel = fullLabel;
        this.preemptive = preemptive;
    }

    /** Round Robin is the only policy that exposes a time-quantum control. */
    public boolean usesQuantum() {
        return this == RR;
    }

    /** Canonical display order, matching the reference selector row. */
    public static final Algorithm[] ORDER = { FCFS, SJF, SRTF, RR, PRI };
}
