package com.example.cpuschedulingsimulator.scheduler.util;

import com.example.cpuschedulingsimulator.model.CpuProcess;

import java.util.List;
import java.util.Map;

/**
 * Resolves ties when multiple processes compete for the CPU.
 */
public final class TieBreaker {

    private TieBreaker() {
    }

    /**
     * Selects the process with the shortest remaining time.
     *
     * Tie-breaking order:
     * 1. Shorter remaining time
     * 2. Earlier arrival time
     * 3. Lower input order
     * 4. Keep the currently running process
     */
    public static CpuProcess selectShortestRemainingTime(
            List<CpuProcess> candidates,
            CpuProcess currentRunning,
            Map<String, Integer> inputOrder
    ) {
        CpuProcess best = null;

        for (CpuProcess candidate : candidates) {
            if (best == null) {
                best = candidate;
                continue;
            }

            if (isPreferredOver(
                    candidate,
                    best,
                    currentRunning,
                    inputOrder
            )) {
                best = candidate;
            }
        }

        return best;
    }

    /**
     * Selects the process with the highest priority.
     *
     * A smaller priority number means a higher priority.
     *
     * Tie-breaking order:
     * 1. Higher priority (smaller priority number)
     * 2. Earlier arrival time
     * 3. Lower input order
     * 4. Keep the currently running process
     */
    public static CpuProcess selectHighestPriority(
            List<CpuProcess> candidates,
            CpuProcess currentRunning,
            Map<String, Integer> inputOrder
    ) {
        CpuProcess best = null;

        for (CpuProcess candidate : candidates) {
            if (best == null) {
                best = candidate;
                continue;
            }

            if (isPriorityPreferredOver(
                    candidate,
                    best,
                    currentRunning,
                    inputOrder
            )) {
                best = candidate;
            }
        }

        return best;
    }

    private static boolean isPriorityPreferredOver(
            CpuProcess candidate,
            CpuProcess incumbent,
            CpuProcess currentRunning,
            Map<String, Integer> inputOrder
    ) {
        if (candidate.getPriority()
                != incumbent.getPriority()) {

            return candidate.getPriority()
                    < incumbent.getPriority();
        }

        if (candidate.getArrivalTime()
                != incumbent.getArrivalTime()) {

            return candidate.getArrivalTime()
                    < incumbent.getArrivalTime();
        }

        int candidateOrder =
                inputOrder.get(candidate.getProcessId());

        int incumbentOrder =
                inputOrder.get(incumbent.getProcessId());

        if (candidateOrder != incumbentOrder) {
            return candidateOrder < incumbentOrder;
        }

        if (currentRunning == candidate) {
            return true;
        }

        if (currentRunning == incumbent) {
            return false;
        }

        return false;
    }

    private static boolean isPreferredOver(
            CpuProcess candidate,
            CpuProcess incumbent,
            CpuProcess currentRunning,
            Map<String, Integer> inputOrder
    ) {
        if (candidate.getRemainingTime()
                != incumbent.getRemainingTime()) {

            return candidate.getRemainingTime()
                    < incumbent.getRemainingTime();
        }

        if (candidate.getArrivalTime()
                != incumbent.getArrivalTime()) {

            return candidate.getArrivalTime()
                    < incumbent.getArrivalTime();
        }

        int candidateOrder =
                inputOrder.get(candidate.getProcessId());

        int incumbentOrder =
                inputOrder.get(incumbent.getProcessId());

        if (candidateOrder != incumbentOrder) {
            return candidateOrder < incumbentOrder;
        }

        if (currentRunning == candidate) {
            return true;
        }

        if (currentRunning == incumbent) {
            return false;
        }

        return false;
    }
}
