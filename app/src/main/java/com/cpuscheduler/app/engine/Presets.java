package com.cpuscheduler.app.engine;

import com.cpuscheduler.app.model.ProcessInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Process-set presets, playback timing, and the process color palette. */
public final class Presets {

    private Presets() {}

    /** Base tick interval at 1× (design used 620ms / speed). */
    public static final int BASE_TICK_MS = 620;
    public static final double[] PLAYBACK_SPEEDS = { 0.5, 1, 2, 4 };

    private static final Random RANDOM = new Random();

    /** Process chip palette; processColor(id) = PALETTE[(id - 1) % 8]. */
    private static final int[] PALETTE = {
            0xFFF472B6, 0xFFFBBF24, 0xFFA78BFA, 0xFF34D399,
            0xFF60A5FA, 0xFFFB7185, 0xFF2DD4BF, 0xFFFACC15,
    };

    public static int processColor(int id) {
        int idx = ((id - 1) % PALETTE.length + PALETTE.length) % PALETTE.length;
        return PALETTE[idx];
    }

    public enum PresetKey { CLASSIC, CONVOY, RANDOM }

    private static List<ProcessInput> classic() {
        List<ProcessInput> out = new ArrayList<>();
        out.add(new ProcessInput(1, 0, 7, 2));
        out.add(new ProcessInput(2, 2, 4, 1));
        out.add(new ProcessInput(3, 4, 1, 3));
        out.add(new ProcessInput(4, 5, 4, 2));
        return out;
    }

    private static List<ProcessInput> convoy() {
        List<ProcessInput> out = new ArrayList<>();
        out.add(new ProcessInput(1, 0, 12, 2));
        out.add(new ProcessInput(2, 1, 3, 1));
        out.add(new ProcessInput(3, 2, 3, 3));
        out.add(new ProcessInput(4, 3, 2, 2));
        return out;
    }

    /** Default process set shown at startup (the Classic preset). */
    public static List<ProcessInput> defaultProcesses() {
        return classic();
    }

    /** Builds a process set for a named preset; RANDOM is freshly generated. */
    public static List<ProcessInput> build(PresetKey key) {
        if (key == PresetKey.CLASSIC) {
            return classic();
        }
        if (key == PresetKey.CONVOY) {
            return convoy();
        }
        int n = 3 + RANDOM.nextInt(2);
        List<ProcessInput> out = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            out.add(new ProcessInput(i + 1, RANDOM.nextInt(7), 1 + RANDOM.nextInt(7), 1 + RANDOM.nextInt(4)));
        }
        return out;
    }

    /** A new process appended in the editor: arrival 0, burst 1–5, priority 1–4. */
    public static ProcessInput newProcess(int id) {
        return new ProcessInput(id, 0, 1 + RANDOM.nextInt(5), 1 + RANDOM.nextInt(4));
    }
}
