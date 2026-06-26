package com.cpuscheduler.app.engine;

import com.cpuscheduler.app.model.PlaybackView;
import com.cpuscheduler.app.model.ProcessMetrics;
import com.cpuscheduler.app.model.ScheduleSegment;
import com.cpuscheduler.app.model.SimulationResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Derives the CPU / ready-queue / completed view at a given playback time.
 * Pure function ported from the reference design.
 */
public final class Playback {

    private Playback() {}

    public static PlaybackView derive(SimulationResult result, int currentTime) {
        int ct = currentTime;

        Integer runningPid = null;
        for (ScheduleSegment s : result.segments) {
            if (ct >= s.start && ct < s.end) {
                runningPid = s.idle ? null : s.pid;
                break;
            }
        }

        // How much each process has executed up to (and including) the current time.
        Map<Integer, Integer> executed = new HashMap<>();
        for (ScheduleSegment seg : result.segments) {
            if (seg.idle || seg.pid == null) {
                continue;
            }
            int overlap = Math.max(0, Math.min(seg.end, ct) - seg.start);
            executed.merge(seg.pid, overlap, Integer::sum);
        }

        List<ProcessMetrics> readyRows = new ArrayList<>();
        for (ProcessMetrics row : result.rows) {
            int ex = executed.containsKey(row.id) ? executed.get(row.id) : 0;
            boolean isRunning = runningPid != null && row.id == runningPid;
            if (row.arrival <= ct && ex < row.burst && !isRunning) {
                readyRows.add(row);
            }
        }
        readyRows.sort(Comparator.<ProcessMetrics>comparingInt(a -> a.arrival).thenComparingInt(a -> a.id));
        List<Integer> readyIds = new ArrayList<>();
        for (ProcessMetrics row : readyRows) {
            readyIds.add(row.id);
        }

        List<Integer> doneIds = new ArrayList<>();
        for (ProcessMetrics row : result.rows) {
            int ex = executed.containsKey(row.id) ? executed.get(row.id) : 0;
            if (ex >= row.burst) {
                doneIds.add(row.id);
            }
        }

        PlaybackView.RunMeta meta;
        if (runningPid != null) {
            meta = PlaybackView.RunMeta.EXECUTING;
        } else if (ct >= result.totalTime) {
            meta = PlaybackView.RunMeta.COMPLETE;
        } else {
            meta = PlaybackView.RunMeta.IDLE;
        }

        double progressPct = result.totalTime > 0 ? (double) ct / result.totalTime * 100.0 : 0.0;
        return new PlaybackView(runningPid, readyIds, doneIds, meta, progressPct);
    }
}
