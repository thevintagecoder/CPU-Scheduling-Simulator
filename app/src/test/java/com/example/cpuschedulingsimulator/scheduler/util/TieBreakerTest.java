package com.example.cpuschedulingsimulator.scheduler.util;

import static org.junit.Assert.assertEquals;

import com.example.cpuschedulingsimulator.model.CpuProcess;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TieBreakerTest {

    private Map<String, Integer> buildInputOrder(
            List<CpuProcess> processes
    ) {
        Map<String, Integer> order = new HashMap<>();
        for (int i = 0; i < processes.size(); i++) {
            order.put(processes.get(i).getProcessId(), i);
        }
        return order;
    }

    @Test
    public void selectShortestRemainingTime_picksShortest() {
        List<CpuProcess> candidates = new ArrayList<>();
        candidates.add(new CpuProcess("P1", 0, 5, 0));
        candidates.add(new CpuProcess("P2", 0, 3, 0));
        candidates.add(new CpuProcess("P3", 0, 7, 0));

        CpuProcess result = TieBreaker.selectShortestRemainingTime(
                candidates, null, buildInputOrder(candidates)
        );

        assertEquals("P2", result.getProcessId());
    }

    @Test
    public void selectShortestRemainingTime_tieByArrival() {
        List<CpuProcess> candidates = new ArrayList<>();
        candidates.add(new CpuProcess("P1", 0, 3, 0));
        candidates.add(new CpuProcess("P2", 2, 3, 0));

        CpuProcess result = TieBreaker.selectShortestRemainingTime(
                candidates, null, buildInputOrder(candidates)
        );

        assertEquals("P1", result.getProcessId());
    }

    @Test
    public void selectShortestRemainingTime_tieByInputOrder() {
        List<CpuProcess> candidates = new ArrayList<>();
        candidates.add(new CpuProcess("P1", 0, 3, 0));
        candidates.add(new CpuProcess("P2", 0, 3, 0));

        CpuProcess result = TieBreaker.selectShortestRemainingTime(
                candidates, null, buildInputOrder(candidates)
        );

        assertEquals("P1", result.getProcessId());
    }

    @Test
    public void selectShortestRemainingTime_prefersLowerInputOrderWhenTied() {
        List<CpuProcess> candidates = new ArrayList<>();
        CpuProcess p1 = new CpuProcess("P1", 0, 3, 0);
        CpuProcess p2 = new CpuProcess("P2", 0, 3, 0);
        candidates.add(p1);
        candidates.add(p2);

        CpuProcess result = TieBreaker.selectShortestRemainingTime(
                candidates, p2, buildInputOrder(candidates)
        );

        assertEquals("P1", result.getProcessId());
    }

    @Test
    public void selectShortestRemainingTime_singleCandidate() {
        List<CpuProcess> candidates = new ArrayList<>();
        candidates.add(new CpuProcess("P1", 0, 5, 0));

        CpuProcess result = TieBreaker.selectShortestRemainingTime(
                candidates, null, buildInputOrder(candidates)
        );

        assertEquals("P1", result.getProcessId());
    }

    @Test
    public void selectHighestPriority_picksHighest() {
        List<CpuProcess> candidates = new ArrayList<>();
        candidates.add(new CpuProcess("P1", 0, 4, 3));
        candidates.add(new CpuProcess("P2", 0, 4, 1));
        candidates.add(new CpuProcess("P3", 0, 4, 2));

        CpuProcess result = TieBreaker.selectHighestPriority(
                candidates, null, buildInputOrder(candidates)
        );

        assertEquals("P2", result.getProcessId());
    }

    @Test
    public void selectHighestPriority_tieByArrival() {
        List<CpuProcess> candidates = new ArrayList<>();
        candidates.add(new CpuProcess("P1", 0, 4, 1));
        candidates.add(new CpuProcess("P2", 2, 4, 1));

        CpuProcess result = TieBreaker.selectHighestPriority(
                candidates, null, buildInputOrder(candidates)
        );

        assertEquals("P1", result.getProcessId());
    }

    @Test
    public void selectHighestPriority_tieByInputOrder() {
        List<CpuProcess> candidates = new ArrayList<>();
        candidates.add(new CpuProcess("P1", 0, 4, 1));
        candidates.add(new CpuProcess("P2", 0, 4, 1));

        CpuProcess result = TieBreaker.selectHighestPriority(
                candidates, null, buildInputOrder(candidates)
        );

        assertEquals("P1", result.getProcessId());
    }

    @Test
    public void selectHighestPriority_prefersLowerInputOrderWhenTied() {
        List<CpuProcess> candidates = new ArrayList<>();
        CpuProcess p1 = new CpuProcess("P1", 0, 4, 1);
        CpuProcess p2 = new CpuProcess("P2", 0, 4, 1);
        candidates.add(p1);
        candidates.add(p2);

        CpuProcess result = TieBreaker.selectHighestPriority(
                candidates, p2, buildInputOrder(candidates)
        );

        assertEquals("P1", result.getProcessId());
    }

    @Test
    public void selectHighestPriority_singleCandidate() {
        List<CpuProcess> candidates = new ArrayList<>();
        candidates.add(new CpuProcess("P1", 0, 4, 2));

        CpuProcess result = TieBreaker.selectHighestPriority(
                candidates, null, buildInputOrder(candidates)
        );

        assertEquals("P1", result.getProcessId());
    }
}
