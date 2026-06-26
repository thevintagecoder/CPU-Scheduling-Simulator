package com.cpuscheduler.app.model;

/** Mean waiting / turnaround / response across all processes (rounded to 2dp). */
public final class Averages {
    public final double waiting;
    public final double turnaround;
    public final double response;

    public Averages(double waiting, double turnaround, double response) {
        this.waiting = waiting;
        this.turnaround = turnaround;
        this.response = response;
    }
}
