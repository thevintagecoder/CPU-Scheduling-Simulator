package com.cpuscheduler.app.state;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cpuscheduler.app.engine.Algorithm;
import com.cpuscheduler.app.engine.Presets;
import com.cpuscheduler.app.engine.SchedulingEngine;
import com.cpuscheduler.app.model.ProcessInput;
import com.cpuscheduler.app.model.SimulationResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity-scoped shared state for the Simulator and Compare screens. Mirrors
 * the original SchedulerContext: process editing, a run/playback model with a
 * local ticker, and the Compare selection. Observers receive immutable
 * {@link SimState} snapshots.
 */
public class SchedulerViewModel extends ViewModel {

    private List<ProcessInput> processes = Presets.defaultProcesses();
    private Algorithm algo = Algorithm.FCFS;
    private int quantum = 2;
    private SimulationResult result = null;
    private String error = null;
    private boolean playing = false;
    private int currentTime = 0;
    private double speed = 1;
    private List<Algorithm> selected =
            new ArrayList<>(Arrays.asList(Algorithm.FCFS, Algorithm.SJF, Algorithm.RR));

    private final MutableLiveData<SimState> state = new MutableLiveData<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable ticker;

    public SchedulerViewModel() {
        emit();
    }

    public LiveData<SimState> state() {
        return state;
    }

    private void emit() {
        state.setValue(new SimState(new ArrayList<>(processes), algo, quantum, result,
                error, playing, currentTime, speed, new ArrayList<>(selected)));
    }

    /** Editing inputs invalidates any computed schedule, exactly like the design. */
    private void invalidate() {
        result = null;
        playing = false;
        currentTime = 0;
        stopTicker();
    }

    // ---------------- process editing ----------------

    public void addProcess() {
        int nextId = 0;
        for (ProcessInput p : processes) {
            nextId = Math.max(nextId, p.id);
        }
        List<ProcessInput> next = new ArrayList<>(processes);
        next.add(Presets.newProcess(nextId + 1));
        processes = next;
        invalidate();
        emit();
    }

    public void removeProcess(int id) {
        if (processes.size() <= 1) {
            return;
        }
        List<ProcessInput> next = new ArrayList<>();
        for (ProcessInput p : processes) {
            if (p.id != id) {
                next.add(p);
            }
        }
        processes = next;
        invalidate();
        emit();
    }

    public void adjust(int id, String field, int delta) {
        List<ProcessInput> next = new ArrayList<>();
        for (ProcessInput p : processes) {
            if (p.id != id) {
                next.add(p);
                continue;
            }
            switch (field) {
                case "arrival":
                    next.add(p.withArrival(Math.max(0, p.arrival + delta)));
                    break;
                case "burst":
                    next.add(p.withBurst(Math.max(1, p.burst + delta)));
                    break;
                case "priority":
                    next.add(p.withPriority(Math.max(1, p.priority + delta)));
                    break;
                default:
                    next.add(p);
            }
        }
        processes = next;
        invalidate();
        emit();
    }

    public void loadPreset(Presets.PresetKey key) {
        processes = Presets.build(key);
        invalidate();
        emit();
    }

    public void setAlgo(Algorithm a) {
        algo = a;
        invalidate();
        emit();
    }

    public void setQuantum(int delta) {
        quantum = Math.max(1, quantum + delta);
        invalidate();
        emit();
    }

    // ---------------- run / playback ----------------

    public void run() {
        try {
            result = SchedulingEngine.simulate(processes, algo, quantum);
            currentTime = 0;
            playing = true;
            error = null;
            startTicker();
        } catch (Exception e) {
            playing = false;
            result = null;
            error = e.getMessage() != null ? e.getMessage() : "Simulation failed. Please try again.";
            stopTicker();
        }
        emit();
    }

    public void togglePlay() {
        if (result == null) {
            run();
            return;
        }
        if (playing) {
            playing = false;
            stopTicker();
            emit();
            return;
        }
        if (currentTime >= result.totalTime) {
            currentTime = 0;
        }
        playing = true;
        startTicker();
        emit();
    }

    public void step(int delta) {
        playing = false;
        stopTicker();
        if (result != null) {
            currentTime = Math.max(0, Math.min(result.totalTime, currentTime + delta));
        }
        emit();
    }

    public void reset() {
        playing = false;
        currentTime = 0;
        stopTicker();
        emit();
    }

    public void setSpeed(double s) {
        speed = s;
        if (playing) {
            startTicker();
        }
        emit();
    }

    public void clearError() {
        error = null;
        emit();
    }

    // ---------------- compare ----------------

    public void toggleCompare(Algorithm a) {
        List<Algorithm> next = new ArrayList<>(selected);
        if (next.contains(a)) {
            if (next.size() <= 1) {
                return; // MIN_SELECTED
            }
            next.remove(a);
        } else {
            if (next.size() >= 5) {
                return; // MAX_SELECTED
            }
            next.add(a);
        }
        selected = next;
        emit();
    }

    // ---------------- ticker ----------------

    private void startTicker() {
        stopTicker();
        if (result == null) {
            return;
        }
        final int interval = (int) Math.round(Presets.BASE_TICK_MS / speed);
        ticker = new Runnable() {
            @Override
            public void run() {
                if (!playing || result == null) {
                    return;
                }
                if (currentTime + 1 >= result.totalTime) {
                    currentTime = result.totalTime;
                    playing = false;
                    emit();
                    return;
                }
                currentTime = currentTime + 1;
                emit();
                handler.postDelayed(this, interval);
            }
        };
        handler.postDelayed(ticker, interval);
    }

    private void stopTicker() {
        if (ticker != null) {
            handler.removeCallbacks(ticker);
            ticker = null;
        }
    }

    @Override
    protected void onCleared() {
        stopTicker();
    }
}
