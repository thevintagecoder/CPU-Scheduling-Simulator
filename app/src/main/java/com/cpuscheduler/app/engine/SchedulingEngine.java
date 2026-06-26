package com.cpuscheduler.app.engine;

import com.cpuscheduler.app.model.Averages;
import com.cpuscheduler.app.model.CompareEntry;
import com.cpuscheduler.app.model.CompareResponse;
import com.cpuscheduler.app.model.ProcessInput;
import com.cpuscheduler.app.model.ProcessMetrics;
import com.cpuscheduler.app.model.ScheduleSegment;
import com.cpuscheduler.app.model.SimulationResult;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * On-device CPU-scheduling engine. A faithful port of the original TypeScript
 * engine: same tie-breaking, same Round Robin queue discipline, same metric
 * formulas, so the numbers are identical (Classic preset under FCFS → avg wait
 * 4.75, total time 16; across all policies SRTF wins with avg wait 3.0).
 */
public final class SchedulingEngine {

    private SchedulingEngine() {}

    private static final int GUARD_LIMIT = 100_000;

    /** Mutable working copy of a process during a run. */
    private static final class WP {
        final int id;
        final int arrival;
        final int burst;
        final int priority;
        int remaining;
        boolean started;
        int response;
        int completion;

        WP(ProcessInput p) {
            this.id = p.id;
            this.arrival = p.arrival;
            this.burst = p.burst;
            this.priority = p.priority;
            this.remaining = p.burst;
            this.started = false;
            this.response = -1;
            this.completion = 0;
        }
    }

    /** Runs one algorithm against a process set and returns the full result. */
    public static SimulationResult simulate(List<ProcessInput> list, Algorithm algo, int quantum) {
        List<WP> ps = new ArrayList<>();
        for (ProcessInput p : list) {
            ps.add(new WP(p));
        }
        List<Integer> ticks = algo == Algorithm.RR ? runRoundRobin(ps, quantum) : runGeneric(ps, algo);

        List<ProcessMetrics> rows = new ArrayList<>();
        double sumW = 0, sumT = 0, sumR = 0;
        for (WP p : ps) {
            int turnaround = p.completion - p.arrival;
            int waiting = turnaround - p.burst;
            rows.add(new ProcessMetrics(p.id, p.arrival, p.burst, p.priority,
                    p.completion, turnaround, waiting, p.response));
            sumW += waiting;
            sumT += turnaround;
            sumR += p.response;
        }
        int n = ps.isEmpty() ? 1 : ps.size();
        Averages averages = new Averages(round2(sumW / n), round2(sumT / n), round2(sumR / n));
        return new SimulationResult(toSegments(ticks), rows, averages, ticks.size());
    }

    /** Runs several algorithms over the same set, flagging the lowest average wait. */
    public static CompareResponse compare(List<ProcessInput> list, List<Algorithm> algos, int quantum) {
        List<SimulationResult> results = new ArrayList<>();
        Double bestWaiting = null;
        for (Algorithm a : algos) {
            SimulationResult r = simulate(list, a, quantum);
            results.add(r);
            if (bestWaiting == null || r.averages.waiting < bestWaiting) {
                bestWaiting = r.averages.waiting;
            }
        }
        double best = bestWaiting == null ? 0.0 : bestWaiting;

        List<CompareEntry> entries = new ArrayList<>();
        Algorithm bestKey = null;
        for (int i = 0; i < algos.size(); i++) {
            Algorithm a = algos.get(i);
            SimulationResult r = results.get(i);
            boolean isBest = r.averages.waiting == best;
            entries.add(new CompareEntry(a, r, isBest));
            if (isBest && bestKey == null) {
                bestKey = a;
            }
        }
        return new CompareResponse(entries, bestKey, quantum);
    }

    /** Orders the candidate set for non-RR policies; first element wins the CPU. */
    private static WP pick(List<WP> available, Algorithm algo) {
        List<WP> sorted = new ArrayList<>(available);
        Comparator<WP> c;
        switch (algo) {
            case SJF:
                c = Comparator.<WP>comparingInt(x -> x.burst)
                        .thenComparingInt(x -> x.arrival).thenComparingInt(x -> x.id);
                break;
            case SRTF:
                c = Comparator.<WP>comparingInt(x -> x.remaining)
                        .thenComparingInt(x -> x.arrival).thenComparingInt(x -> x.id);
                break;
            case PRI:
                c = Comparator.<WP>comparingInt(x -> x.priority)
                        .thenComparingInt(x -> x.arrival).thenComparingInt(x -> x.id);
                break;
            case FCFS:
            default:
                c = Comparator.<WP>comparingInt(x -> x.arrival).thenComparingInt(x -> x.id);
                break;
        }
        sorted.sort(c);
        return sorted.get(0);
    }

    /** Tick-by-tick driver for FCFS / SJF / SRTF / Priority. null tick = idle. */
    private static List<Integer> runGeneric(List<WP> ps, Algorithm algo) {
        List<Integer> ticks = new ArrayList<>();
        int n = ps.size();
        boolean preempt = algo.preemptive;
        int done = 0, t = 0, guard = 0;
        WP current = null;

        while (done < n && guard < GUARD_LIMIT) {
            guard++;
            List<WP> available = new ArrayList<>();
            for (WP p : ps) {
                if (p.arrival <= t && p.remaining > 0) {
                    available.add(p);
                }
            }
            if (available.isEmpty()) {
                ticks.add(null);
                t++;
                continue;
            }
            if (preempt || !(current != null && current.remaining > 0)) {
                current = pick(available, algo);
            }
            WP p = current;
            if (!p.started) {
                p.started = true;
                p.response = t - p.arrival;
            }
            p.remaining--;
            ticks.add(p.id);
            t++;
            if (p.remaining == 0) {
                p.completion = t;
                done++;
                current = null;
            }
        }
        return ticks;
    }

    /** Round Robin with a fixed quantum over a cyclic FIFO queue. */
    private static List<Integer> runRoundRobin(List<WP> ps, int quantum) {
        List<Integer> ticks = new ArrayList<>();
        Deque<Integer> queue = new ArrayDeque<>();
        Set<Integer> inQueue = new HashSet<>();
        int n = ps.size(), t = 0, done = 0, guard = 0;

        // Seed everything available at (or before) time 0.
        List<WP> seed = new ArrayList<>();
        for (WP p : ps) {
            if (p.arrival <= 0 && p.remaining > 0) {
                seed.add(p);
            }
        }
        seed.sort(Comparator.<WP>comparingInt(a -> a.arrival).thenComparingInt(a -> a.id));
        for (WP p : seed) {
            queue.addLast(p.id);
            inQueue.add(p.id);
        }

        while (done < n && guard < GUARD_LIMIT) {
            guard++;
            if (queue.isEmpty()) {
                ticks.add(null);
                t++;
                admit(ps, t, queue, inQueue);
                continue;
            }
            int pid = queue.pollFirst();
            inQueue.remove(pid);
            WP p = byId(ps, pid);

            int q = 0;
            while (q < quantum && p.remaining > 0) {
                if (!p.started) {
                    p.started = true;
                    p.response = t - p.arrival;
                }
                p.remaining--;
                ticks.add(pid);
                t++;
                q++;
                admit(ps, t, queue, inQueue);
            }
            if (p.remaining > 0) {
                queue.addLast(pid);
                inQueue.add(pid);
            } else {
                p.completion = t;
                done++;
            }
        }
        return ticks;
    }

    private static void admit(List<WP> ps, int time, Deque<Integer> queue, Set<Integer> inQueue) {
        List<WP> arriving = new ArrayList<>();
        for (WP p : ps) {
            if (p.arrival == time && p.remaining > 0 && !inQueue.contains(p.id)) {
                arriving.add(p);
            }
        }
        arriving.sort(Comparator.comparingInt(a -> a.id));
        for (WP p : arriving) {
            queue.addLast(p.id);
            inQueue.add(p.id);
        }
    }

    private static WP byId(List<WP> ps, int id) {
        for (WP p : ps) {
            if (p.id == id) {
                return p;
            }
        }
        return null;
    }

    /** Collapses the per-tick timeline into contiguous Gantt segments. */
    private static List<ScheduleSegment> toSegments(List<Integer> ticks) {
        List<ScheduleSegment> segments = new ArrayList<>();
        for (int i = 0; i < ticks.size(); i++) {
            Integer pid = ticks.get(i);
            boolean idle = pid == null;
            ScheduleSegment last = segments.isEmpty() ? null : segments.get(segments.size() - 1);
            boolean merge = false;
            if (last != null && last.idle == idle) {
                merge = idle || (last.pid != null && last.pid == pid.intValue());
            }
            if (merge) {
                last.end = i + 1;
            } else {
                segments.add(new ScheduleSegment(idle ? null : pid, idle, i, i + 1));
            }
        }
        return segments;
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
