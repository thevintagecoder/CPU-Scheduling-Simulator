package com.cpuscheduler.app.engine;

import java.util.ArrayList;
import java.util.List;

/** Verbatim Learn-tab content, in canonical order (FCFS, SJF, SRTF, RR, PRI). */
public final class Theory {

    private Theory() {}

    /** Fixed pro/con accent colors (green / rose), shared by every card. */
    public static final int PRO_COLOR = 0xFF34D399;
    public static final int CON_COLOR = 0xFFFB7185;

    public static List<TheoryCard> cards() {
        List<TheoryCard> cards = new ArrayList<>();
        cards.add(new TheoryCard(
                Algorithm.FCFS, "First Come First Serve", "Non-preemptive", 0xFF22D3EE,
                "Processes run in the exact order they arrive in the ready queue — the simplest possible policy.",
                "Easy to implement, perfectly fair by arrival, never starves a process.",
                "Convoy effect: one long job stuck at the front delays every short job behind it."));
        cards.add(new TheoryCard(
                Algorithm.SJF, "Shortest Job First", "Non-preemptive", 0xFF34D399,
                "Among all arrived processes, the CPU picks the one with the smallest burst time and runs it to completion.",
                "Provably minimal average waiting time for a given set of jobs.",
                "Needs burst times known in advance; long jobs can starve under steady short arrivals."));
        cards.add(new TheoryCard(
                Algorithm.SRTF, "Shortest Remaining Time", "Preemptive", 0xFFA78BFA,
                "The preemptive version of SJF. Every time unit it runs the job with the least remaining time — a shorter arrival preempts the current job.",
                "Pushes average waiting time even lower than SJF.",
                "Frequent context switches and possible starvation of long jobs."));
        cards.add(new TheoryCard(
                Algorithm.RR, "Round Robin", "Preemptive", 0xFFFBBF24,
                "Each ready process gets a fixed time quantum in a cyclic FIFO queue, then yields the CPU to the next.",
                "Fair and highly responsive — ideal for interactive / time-sharing systems.",
                "Too-small quantum adds switching overhead; too-large degrades into FCFS."));
        cards.add(new TheoryCard(
                Algorithm.PRI, "Priority Scheduling", "Non-preemptive", 0xFFF472B6,
                "The CPU runs the highest-priority ready process first. Here a lower priority number means higher importance.",
                "Important / urgent jobs are handled first.",
                "Low-priority jobs can starve indefinitely — usually fixed with aging."));
        return cards;
    }
}
