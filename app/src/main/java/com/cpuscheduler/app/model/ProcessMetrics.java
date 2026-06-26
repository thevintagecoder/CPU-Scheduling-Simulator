package com.cpuscheduler.app.model;

/** Per-process metrics after a run. AT/BT/CT/TAT/WT/RT in the UI map here. */
public final class ProcessMetrics {
    public final int id;
    public final int arrival;     // AT
    public final int burst;       // BT
    public final int priority;
    public final int completion;  // CT
    public final int turnaround;  // TAT
    public final int waiting;     // WT
    public final int response;    // RT

    public ProcessMetrics(int id, int arrival, int burst, int priority,
                          int completion, int turnaround, int waiting, int response) {
        this.id = id;
        this.arrival = arrival;
        this.burst = burst;
        this.priority = priority;
        this.completion = completion;
        this.turnaround = turnaround;
        this.waiting = waiting;
        this.response = response;
    }
}
