package com.cpuscheduler.app.model;

import com.cpuscheduler.app.engine.Algorithm;

/** One algorithm's result within a Compare run; {@code best} flags lowest avg WT. */
public final class CompareEntry {
    public final Algorithm algorithm;
    public final SimulationResult result;
    public final boolean best;

    public CompareEntry(Algorithm algorithm, SimulationResult result, boolean best) {
        this.algorithm = algorithm;
        this.result = result;
        this.best = best;
    }
}
