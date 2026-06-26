package com.cpuscheduler.app.state;

import com.cpuscheduler.app.engine.Algorithm;
import com.cpuscheduler.app.model.ProcessInput;
import com.cpuscheduler.app.model.SimulationResult;

import java.util.Collections;
import java.util.List;

/** Immutable snapshot of all simulator + compare state, emitted to observers. */
public final class SimState {
    public final List<ProcessInput> processes;
    public final Algorithm algo;
    public final int quantum;
    public final SimulationResult result;   // nullable — null means "not run / invalidated"
    public final String error;              // nullable
    public final boolean playing;
    public final int currentTime;
    public final double speed;
    public final List<Algorithm> selected;  // Compare-screen selection

    public SimState(List<ProcessInput> processes, Algorithm algo, int quantum,
                    SimulationResult result, String error, boolean playing,
                    int currentTime, double speed, List<Algorithm> selected) {
        this.processes = Collections.unmodifiableList(processes);
        this.algo = algo;
        this.quantum = quantum;
        this.result = result;
        this.error = error;
        this.playing = playing;
        this.currentTime = currentTime;
        this.speed = speed;
        this.selected = Collections.unmodifiableList(selected);
    }
}
