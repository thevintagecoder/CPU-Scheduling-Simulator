package com.cpuscheduler.app.model;

/**
 * Immutable input row for a process. Edits return a new instance (see the
 * {@code with*} helpers) so the editor never mutates state in place.
 */
public final class ProcessInput {
    public final int id;
    public final int arrival;
    public final int burst;
    public final int priority;

    public ProcessInput(int id, int arrival, int burst, int priority) {
        this.id = id;
        this.arrival = arrival;
        this.burst = burst;
        this.priority = priority;
    }

    public ProcessInput withArrival(int value) {
        return new ProcessInput(id, value, burst, priority);
    }

    public ProcessInput withBurst(int value) {
        return new ProcessInput(id, arrival, value, priority);
    }

    public ProcessInput withPriority(int value) {
        return new ProcessInput(id, arrival, burst, value);
    }
}
