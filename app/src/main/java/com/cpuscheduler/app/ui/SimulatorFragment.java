package com.cpuscheduler.app.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cpuscheduler.app.engine.Algorithm;
import com.cpuscheduler.app.engine.Playback;
import com.cpuscheduler.app.engine.Presets;
import com.cpuscheduler.app.model.PlaybackView;
import com.cpuscheduler.app.model.ProcessInput;
import com.cpuscheduler.app.model.ProcessMetrics;
import com.cpuscheduler.app.model.SimulationResult;
import com.cpuscheduler.app.state.SchedulerViewModel;
import com.cpuscheduler.app.state.SimState;

/** Simulator tab: process editor, controls, Gantt playback, metrics, averages. */
public class SimulatorFragment extends Fragment {

    private static final int MATCH = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int WRAP = ViewGroup.LayoutParams.WRAP_CONTENT;

    private SchedulerViewModel vm;
    private LinearLayout editorHost;
    private LinearLayout resultHost;
    private TextView runIcon;

    // result references updated during playback
    private GanttView ganttView;
    private TextView timeLabel;
    private View progressFill;
    private View progressSpacer;
    private LinearLayout cpuQueueInner;
    private LinearLayout speedRow;
    private TextView transportPlayIcon;

    private String lastEditorSig = null;
    private SimulationResult lastResult = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle b) {
        Context c = requireContext();
        vm = new ViewModelProvider(requireActivity()).get(SchedulerViewModel.class);

        ScrollView scroll = new ScrollView(c);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        int padH = Ui.dp(c, 18);
        scroll.setPadding(padH, Ui.dp(c, 4), padH, Ui.dp(c, 24));

        LinearLayout col = new LinearLayout(c);
        col.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(col, new ScrollView.LayoutParams(MATCH, WRAP));

        editorHost = new LinearLayout(c);
        editorHost.setOrientation(LinearLayout.VERTICAL);
        col.addView(editorHost, new LinearLayout.LayoutParams(MATCH, WRAP));

        resultHost = new LinearLayout(c);
        resultHost.setOrientation(LinearLayout.VERTICAL);
        resultHost.setVisibility(View.GONE);
        col.addView(resultHost, new LinearLayout.LayoutParams(MATCH, WRAP));

        vm.state().observe(getViewLifecycleOwner(), this::render);
        return scroll;
    }

    private void render(SimState st) {
        Context c = requireContext();
        String sig = editorSig(st);
        if (!sig.equals(lastEditorSig)) {
            buildEditor(c, st);
            lastEditorSig = sig;
        }
        if (runIcon != null) {
            runIcon.setText(st.playing ? "❚❚" : "▶");
        }
        if (st.result == null) {
            resultHost.setVisibility(View.GONE);
            lastResult = null;
        } else {
            if (st.result != lastResult) {
                buildResult(c, st);
                lastResult = st.result;
            }
            resultHost.setVisibility(View.VISIBLE);
            updatePlayback(st);
        }
    }

    private String editorSig(SimState st) {
        StringBuilder sb = new StringBuilder();
        for (ProcessInput p : st.processes) {
            sb.append(p.id).append(':').append(p.arrival).append(':')
                    .append(p.burst).append(':').append(p.priority).append('|');
        }
        sb.append('#').append(st.algo).append('#').append(st.quantum).append('#').append(st.error);
        return sb.toString();
    }

    // ----------------------------------------------------------------- editor

    private void buildEditor(Context c, SimState st) {
        editorHost.removeAllViews();

        // Processes header
        LinearLayout header = new LinearLayout(c);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(Ui.dp(c, 2), 0, Ui.dp(c, 2), 0);
        LinearLayout.LayoutParams hl = new LinearLayout.LayoutParams(MATCH, WRAP);
        hl.topMargin = Ui.dp(c, 8);
        hl.bottomMargin = Ui.dp(c, 10);
        editorHost.addView(header, hl);
        header.addView(sectionLabel(c, "PROCESSES"), new LinearLayout.LayoutParams(0, WRAP, 1f));
        header.addView(addButton(c));

        // Process rows
        for (int i = 0; i < st.processes.size(); i++) {
            View row = processRow(c, st.processes.get(i));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH, WRAP);
            if (i > 0) {
                lp.topMargin = Ui.dp(c, 8);
            }
            editorHost.addView(row, lp);
        }

        // Presets
        LinearLayout presets = new LinearLayout(c);
        presets.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(MATCH, WRAP);
        pl.topMargin = Ui.dp(c, 9);
        editorHost.addView(presets, pl);
        presets.addView(presetButton(c, "Classic", Presets.PresetKey.CLASSIC, 0));
        presets.addView(presetButton(c, "Convoy", Presets.PresetKey.CONVOY, Ui.dp(c, 7)));
        presets.addView(presetButton(c, "Random", Presets.PresetKey.RANDOM, Ui.dp(c, 7)));

        // Algorithm label + pills
        TextView algoLabel = sectionLabel(c, "ALGORITHM");
        LinearLayout.LayoutParams al = new LinearLayout.LayoutParams(MATCH, WRAP);
        al.topMargin = Ui.dp(c, 18);
        al.bottomMargin = Ui.dp(c, 9);
        al.leftMargin = Ui.dp(c, 2);
        editorHost.addView(algoLabel, al);

        FlowLayout pills = new FlowLayout(c);
        pills.setGaps(Ui.dp(c, 7), Ui.dp(c, 7));
        for (Algorithm a : Algorithm.ORDER) {
            TextView pill = Ui.pill(c, a.shortLabel, st.algo == a, false);
            pill.setOnClickListener(v -> vm.setAlgo(a));
            pills.addView(pill);
        }
        editorHost.addView(pills, new LinearLayout.LayoutParams(MATCH, WRAP));

        // Quantum (RR only)
        if (st.algo.usesQuantum()) {
            editorHost.addView(quantumRow(c, st.quantum));
        }

        // Run button
        editorHost.addView(runButton(c, st));

        // Error
        if (st.error != null) {
            editorHost.addView(errorCard(c, st.error));
        }
    }

    private TextView sectionLabel(Context c, String s) {
        TextView t = Ui.tv(c, s, 13, Ui.muted(c), true, false);
        t.setLetterSpacing(0.04f);
        return t;
    }

    private View addButton(Context c) {
        LinearLayout b = new LinearLayout(c);
        b.setOrientation(LinearLayout.HORIZONTAL);
        b.setGravity(Gravity.CENTER_VERTICAL);
        b.setBackground(Ui.round(Ui.surface2(c), Ui.dp(c, 9), Ui.border(c), Ui.dp(c, 1)));
        b.setPadding(Ui.dp(c, 12), Ui.dp(c, 6), Ui.dp(c, 12), Ui.dp(c, 6));
        b.setClickable(true);
        b.setOnClickListener(v -> vm.addProcess());
        TextView plus = Ui.tv(c, "+", 15, Ui.primary(c), false, false);
        LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(WRAP, WRAP);
        plp.rightMargin = Ui.dp(c, 5);
        b.addView(plus, plp);
        b.addView(Ui.tv(c, "Add", 12.5f, Ui.text(c), true, false));
        return b;
    }

    private View processRow(Context c, ProcessInput p) {
        LinearLayout row = new LinearLayout(c);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackground(Ui.round(Ui.surface(c), Ui.dp(c, 13), Ui.border(c), Ui.dp(c, 1)));
        row.setPadding(Ui.dp(c, 10), Ui.dp(c, 9), Ui.dp(c, 10), Ui.dp(c, 9));

        TextView badge = Ui.tv(c, "P" + p.id, 11, Ui.onPrimary(c), true, true);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(Ui.round(Presets.processColor(p.id), Ui.dp(c, 8), 0, 0));
        row.addView(badge, new LinearLayout.LayoutParams(Ui.dp(c, 30), Ui.dp(c, 30)));

        LinearLayout steppers = new LinearLayout(c);
        steppers.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams sl = new LinearLayout.LayoutParams(0, WRAP, 1f);
        sl.leftMargin = Ui.dp(c, 8);
        row.addView(steppers, sl);
        steppers.addView(fieldStepper(c, p, "ARRIVAL", "arrival", p.arrival, 0));
        steppers.addView(fieldStepper(c, p, "BURST", "burst", p.burst, Ui.dp(c, 6)));
        steppers.addView(fieldStepper(c, p, "PRIORITY", "priority", p.priority, Ui.dp(c, 6)));

        TextView remove = Ui.tv(c, "×", 18, Ui.muted(c), false, false);
        remove.setGravity(Gravity.CENTER);
        remove.setClickable(true);
        remove.setOnClickListener(v -> vm.removeProcess(p.id));
        LinearLayout.LayoutParams rl = new LinearLayout.LayoutParams(Ui.dp(c, 20), Ui.dp(c, 20));
        rl.leftMargin = Ui.dp(c, 8);
        row.addView(remove, rl);
        return row;
    }

    private View fieldStepper(Context c, ProcessInput p, String label, String field, int value, int leftMargin) {
        LinearLayout col = new LinearLayout(c);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(0, WRAP, 1f);
        clp.leftMargin = leftMargin;
        col.setLayoutParams(clp);

        TextView lab = Ui.tv(c, label, 8.5f, Ui.muted(c), false, false);
        lab.setLetterSpacing(0.05f);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(WRAP, WRAP);
        ll.bottomMargin = Ui.dp(c, 3);
        col.addView(lab, ll);

        LinearLayout box = new LinearLayout(c);
        box.setOrientation(LinearLayout.HORIZONTAL);
        box.setGravity(Gravity.CENTER_VERTICAL);
        box.setBackground(Ui.round(Ui.surface2(c), Ui.dp(c, 8), Ui.border(c), Ui.dp(c, 1)));
        box.setPadding(Ui.dp(c, 2), Ui.dp(c, 2), Ui.dp(c, 2), Ui.dp(c, 2));
        box.addView(stepButton(c, "−", Ui.muted(c), () -> vm.adjust(p.id, field, -1)));
        TextView val = Ui.tv(c, String.valueOf(value), 13, Ui.text(c), true, true);
        val.setGravity(Gravity.CENTER);
        val.setMinWidth(Ui.dp(c, 16));
        box.addView(val);
        box.addView(stepButton(c, "+", Ui.primary(c), () -> vm.adjust(p.id, field, 1)));
        col.addView(box);
        return col;
    }

    private View stepButton(Context c, String sym, int color, Runnable onClick) {
        TextView t = Ui.tv(c, sym, 16, color, false, false);
        t.setGravity(Gravity.CENTER);
        t.setClickable(true);
        t.setOnClickListener(v -> onClick.run());
        t.setLayoutParams(new LinearLayout.LayoutParams(Ui.dp(c, 22), Ui.dp(c, 22)));
        return t;
    }

    private View presetButton(Context c, String label, Presets.PresetKey key, int leftMargin) {
        TextView t = Ui.tv(c, label, 11.5f, Ui.muted(c), true, false);
        t.setGravity(Gravity.CENTER);
        t.setPadding(0, Ui.dp(c, 7), 0, Ui.dp(c, 7));
        t.setBackground(Ui.dashed(c, Ui.border(c), Ui.dp(c, 1), Ui.dp(c, 9)));
        t.setClickable(true);
        t.setOnClickListener(v -> vm.loadPreset(key));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, WRAP, 1f);
        lp.leftMargin = leftMargin;
        t.setLayoutParams(lp);
        return t;
    }

    private View quantumRow(Context c, int quantum) {
        LinearLayout row = new LinearLayout(c);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackground(Ui.round(Ui.surface(c), Ui.dp(c, 11), Ui.border(c), Ui.dp(c, 1)));
        row.setPadding(Ui.dp(c, 14), Ui.dp(c, 9), Ui.dp(c, 14), Ui.dp(c, 9));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH, WRAP);
        lp.topMargin = Ui.dp(c, 10);
        row.setLayoutParams(lp);

        TextView title = Ui.tv(c, "Time Quantum", 13, Ui.text(c), false, false);
        row.addView(title, new LinearLayout.LayoutParams(0, WRAP, 1f));

        row.addView(quantumButton(c, "−", () -> vm.setQuantum(-1)));
        TextView val = Ui.tv(c, String.valueOf(quantum), 16, Ui.primary(c), true, true);
        val.setGravity(Gravity.CENTER);
        val.setMinWidth(Ui.dp(c, 18));
        LinearLayout.LayoutParams vl = new LinearLayout.LayoutParams(WRAP, WRAP);
        vl.leftMargin = Ui.dp(c, 10);
        vl.rightMargin = Ui.dp(c, 10);
        row.addView(val, vl);
        row.addView(quantumButton(c, "+", () -> vm.setQuantum(1)));
        return row;
    }

    private View quantumButton(Context c, String sym, Runnable onClick) {
        TextView t = Ui.tv(c, sym, 17, Ui.text(c), false, false);
        t.setGravity(Gravity.CENTER);
        t.setBackground(Ui.round(Ui.surface2(c), Ui.dp(c, 8), Ui.border(c), Ui.dp(c, 1)));
        t.setClickable(true);
        t.setOnClickListener(v -> onClick.run());
        t.setLayoutParams(new LinearLayout.LayoutParams(Ui.dp(c, 28), Ui.dp(c, 28)));
        return t;
    }

    private View runButton(Context c, SimState st) {
        LinearLayout b = new LinearLayout(c);
        b.setOrientation(LinearLayout.HORIZONTAL);
        b.setGravity(Gravity.CENTER);
        b.setBackground(Ui.round(Ui.primary(c), Ui.dp(c, 14), 0, 0));
        b.setPadding(0, Ui.dp(c, 15), 0, Ui.dp(c, 15));
        b.setClickable(true);
        b.setOnClickListener(v -> vm.togglePlay());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH, WRAP);
        lp.topMargin = Ui.dp(c, 14);
        b.setLayoutParams(lp);

        runIcon = Ui.tv(c, st.playing ? "❚❚" : "▶", 13, Ui.onPrimary(c), false, false);
        LinearLayout.LayoutParams il = new LinearLayout.LayoutParams(WRAP, WRAP);
        il.rightMargin = Ui.dp(c, 9);
        b.addView(runIcon, il);
        b.addView(Ui.tv(c, "Run Simulation", 15.5f, Ui.onPrimary(c), true, false));
        return b;
    }

    private View errorCard(Context c, String message) {
        LinearLayout box = new LinearLayout(c);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setBackground(Ui.round(Ui.withAlpha(0xFB7185, 0x1A), Ui.dp(c, 12),
                Ui.withAlpha(0xFB7185, 0x59), Ui.dp(c, 1)));
        int p = Ui.dp(c, 12);
        box.setPadding(p, p, p, p);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH, WRAP);
        lp.topMargin = Ui.dp(c, 12);
        box.setLayoutParams(lp);

        TextView msg = Ui.tv(c, message, 12.5f, 0xFFFB7185, false, false);
        msg.setLineSpacing(0, 1.3f);
        box.addView(msg);
        TextView retry = Ui.tv(c, "Retry", 12.5f, Ui.primary(c), true, false);
        retry.setClickable(true);
        retry.setOnClickListener(v -> vm.run());
        LinearLayout.LayoutParams rl = new LinearLayout.LayoutParams(WRAP, WRAP);
        rl.topMargin = Ui.dp(c, 8);
        box.addView(retry, rl);
        return box;
    }

    // ----------------------------------------------------------------- result

    private void buildResult(Context c, SimState st) {
        resultHost.removeAllViews();
        resultHost.addView(ganttCard(c));
        resultHost.addView(cpuQueueCard(c));
        resultHost.addView(metricsCard(c, st.result));
        resultHost.addView(averagesCard(c, st.result));
    }

    private View ganttCard(Context c) {
        LinearLayout card = Ui.card(c);
        LinearLayout.LayoutParams cl = new LinearLayout.LayoutParams(MATCH, WRAP);
        cl.topMargin = Ui.dp(c, 18);
        card.setLayoutParams(cl);

        LinearLayout head = new LinearLayout(c);
        head.setOrientation(LinearLayout.HORIZONTAL);
        head.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams hl = new LinearLayout.LayoutParams(MATCH, WRAP);
        hl.bottomMargin = Ui.dp(c, 12);
        card.addView(head, hl);
        head.addView(Ui.tv(c, "Gantt Chart", 13, Ui.text(c), true, false), new LinearLayout.LayoutParams(0, WRAP, 1f));
        timeLabel = Ui.tv(c, "", 12, Ui.primary(c), false, true);
        head.addView(timeLabel);

        HorizontalScrollView hs = new HorizontalScrollView(c);
        hs.setHorizontalScrollBarEnabled(false);
        ganttView = new GanttView(c);
        hs.addView(ganttView);
        card.addView(hs, new LinearLayout.LayoutParams(MATCH, WRAP));

        // transport
        LinearLayout transport = new LinearLayout(c);
        transport.setOrientation(LinearLayout.HORIZONTAL);
        transport.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams tl = new LinearLayout.LayoutParams(MATCH, WRAP);
        tl.topMargin = Ui.dp(c, 14);
        card.addView(transport, tl);

        transport.addView(transportButton(c, "◀", false, () -> vm.step(-1), 0));

        transportPlayIcon = Ui.tv(c, "▶", 13, Ui.onPrimary(c), true, false);
        transportPlayIcon.setGravity(Gravity.CENTER);
        transportPlayIcon.setBackground(Ui.round(Ui.primary(c), Ui.dp(c, 10), 0, 0));
        transportPlayIcon.setClickable(true);
        transportPlayIcon.setOnClickListener(v -> vm.togglePlay());
        LinearLayout.LayoutParams ppl = new LinearLayout.LayoutParams(Ui.dp(c, 46), Ui.dp(c, 38));
        ppl.leftMargin = Ui.dp(c, 8);
        transport.addView(transportPlayIcon, ppl);

        transport.addView(transportButton(c, "▶", false, () -> vm.step(1), Ui.dp(c, 8)));
        transport.addView(transportButton(c, "↺", true, () -> vm.reset(), Ui.dp(c, 8)));

        LinearLayout track = new LinearLayout(c);
        track.setOrientation(LinearLayout.HORIZONTAL);
        track.setBackground(Ui.round(Ui.surface2(c), Ui.dp(c, 6), 0, 0));
        LinearLayout.LayoutParams trl = new LinearLayout.LayoutParams(0, Ui.dp(c, 6), 1f);
        trl.leftMargin = Ui.dp(c, 8);
        track.setLayoutParams(trl);
        progressFill = new View(c);
        progressFill.setBackground(Ui.round(Ui.primary(c), Ui.dp(c, 6), 0, 0));
        track.addView(progressFill, new LinearLayout.LayoutParams(0, MATCH, 0f));
        progressSpacer = new View(c);
        track.addView(progressSpacer, new LinearLayout.LayoutParams(0, MATCH, 1f));
        transport.addView(track);

        // speed selector
        speedRow = new LinearLayout(c);
        speedRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams srl = new LinearLayout.LayoutParams(MATCH, WRAP);
        srl.topMargin = Ui.dp(c, 10);
        card.addView(speedRow, srl);
        return card;
    }

    private View transportButton(Context c, String sym, boolean muted, Runnable onClick, int leftMargin) {
        TextView t = Ui.tv(c, sym, 13, muted ? Ui.muted(c) : Ui.text(c), false, false);
        t.setGravity(Gravity.CENTER);
        t.setBackground(Ui.round(Ui.surface2(c), Ui.dp(c, 10), Ui.border(c), Ui.dp(c, 1)));
        t.setClickable(true);
        t.setOnClickListener(v -> onClick.run());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Ui.dp(c, 38), Ui.dp(c, 38));
        lp.leftMargin = leftMargin;
        t.setLayoutParams(lp);
        return t;
    }

    private View cpuQueueCard(Context c) {
        LinearLayout card = Ui.card(c);
        LinearLayout.LayoutParams cl = new LinearLayout.LayoutParams(MATCH, WRAP);
        cl.topMargin = Ui.dp(c, 11);
        card.setLayoutParams(cl);
        cpuQueueInner = new LinearLayout(c);
        cpuQueueInner.setOrientation(LinearLayout.HORIZONTAL);
        card.addView(cpuQueueInner, new LinearLayout.LayoutParams(MATCH, WRAP));
        return card;
    }

    private TextView columnLabel(Context c, String s) {
        TextView t = Ui.tv(c, s, 10, Ui.muted(c), false, false);
        t.setLetterSpacing(0.08f);
        return t;
    }

    private View metricsCard(Context c, SimulationResult result) {
        LinearLayout card = Ui.card(c);
        card.setPadding(Ui.dp(c, 12), Ui.dp(c, 14), Ui.dp(c, 12), Ui.dp(c, 14));
        LinearLayout.LayoutParams cl = new LinearLayout.LayoutParams(MATCH, WRAP);
        cl.topMargin = Ui.dp(c, 11);
        card.setLayoutParams(cl);

        TextView title = Ui.tv(c, "Per-process metrics", 13, Ui.text(c), true, false);
        title.setPadding(Ui.dp(c, 2), 0, Ui.dp(c, 2), 0);
        LinearLayout.LayoutParams tl = new LinearLayout.LayoutParams(MATCH, WRAP);
        tl.bottomMargin = Ui.dp(c, 11);
        card.addView(title, tl);

        // header
        LinearLayout headerRow = new LinearLayout(c);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        View spacer = new View(c);
        headerRow.addView(spacer, new LinearLayout.LayoutParams(Ui.dp(c, 30), Ui.dp(c, 1)));
        for (String h : new String[]{"AT", "BT", "CT", "TAT", "WT", "RT"}) {
            TextView cell = Ui.tv(c, h, 10.5f, Ui.muted(c), false, true);
            cell.setGravity(Gravity.CENTER);
            cell.setPadding(0, Ui.dp(c, 4), 0, Ui.dp(c, 4));
            headerRow.addView(cell, new LinearLayout.LayoutParams(0, WRAP, 1f));
        }
        card.addView(headerRow, new LinearLayout.LayoutParams(MATCH, WRAP));

        // data rows
        for (ProcessMetrics row : result.rows) {
            LinearLayout r = new LinearLayout(c);
            r.setOrientation(LinearLayout.HORIZONTAL);
            r.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rl = new LinearLayout.LayoutParams(MATCH, WRAP);
            rl.topMargin = Ui.dp(c, 2);
            card.addView(r, rl);

            TextView badge = Ui.tv(c, "P" + row.id, 10.5f, Ui.onPrimary(c), true, true);
            badge.setGravity(Gravity.CENTER);
            badge.setBackground(Ui.round(Presets.processColor(row.id), Ui.dp(c, 6), 0, 0));
            r.addView(badge, new LinearLayout.LayoutParams(Ui.dp(c, 30), Ui.dp(c, 26)));

            r.addView(metricCell(c, String.valueOf(row.arrival), Ui.muted(c), false));
            r.addView(metricCell(c, String.valueOf(row.burst), Ui.muted(c), false));
            r.addView(metricCell(c, String.valueOf(row.completion), Ui.text(c), false));
            r.addView(metricCell(c, String.valueOf(row.turnaround), Ui.text(c), false));
            r.addView(metricCell(c, String.valueOf(row.waiting), Ui.primary(c), true));
            r.addView(metricCell(c, String.valueOf(row.response), Ui.text(c), false));
        }

        TextView note = Ui.tv(c, "TAT = CT − AT · WT = TAT − BT · RT = first run − AT", 9.5f, Ui.muted(c), false, true);
        note.setLineSpacing(0, 1.2f);
        LinearLayout.LayoutParams nl = new LinearLayout.LayoutParams(MATCH, WRAP);
        nl.topMargin = Ui.dp(c, 10);
        card.addView(note, nl);
        return card;
    }

    private TextView metricCell(Context c, String s, int color, boolean bold) {
        TextView t = Ui.tv(c, s, 10.5f, color, bold, true);
        t.setGravity(Gravity.CENTER);
        t.setHeight(Ui.dp(c, 26));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, WRAP, 1f);
        t.setLayoutParams(lp);
        return t;
    }

    private View averagesCard(Context c, SimulationResult result) {
        LinearLayout row = new LinearLayout(c);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rl = new LinearLayout.LayoutParams(MATCH, WRAP);
        rl.topMargin = Ui.dp(c, 11);
        row.setLayoutParams(rl);
        row.addView(averageTile(c, fmt(result.averages.waiting), "avg wait", true, 0));
        row.addView(averageTile(c, fmt(result.averages.turnaround), "avg turn", false, Ui.dp(c, 9)));
        row.addView(averageTile(c, fmt(result.averages.response), "avg resp", false, Ui.dp(c, 9)));
        return row;
    }

    private View averageTile(Context c, String value, String label, boolean accent, int leftMargin) {
        LinearLayout tile = new LinearLayout(c);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER_HORIZONTAL);
        tile.setBackground(Ui.round(Ui.surface(c), Ui.dp(c, 14), Ui.border(c), Ui.dp(c, 1)));
        tile.setPadding(Ui.dp(c, 10), Ui.dp(c, 13), Ui.dp(c, 10), Ui.dp(c, 13));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, WRAP, 1f);
        lp.leftMargin = leftMargin;
        tile.setLayoutParams(lp);
        tile.addView(Ui.tv(c, value, 24, accent ? Ui.primary(c) : Ui.text(c), true, true));
        TextView l = Ui.tv(c, label, 10, Ui.muted(c), false, false);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(WRAP, WRAP);
        ll.topMargin = Ui.dp(c, 3);
        tile.addView(l, ll);
        return tile;
    }

    // ----------------------------------------------------------- playback tick

    private void updatePlayback(SimState st) {
        Context c = requireContext();
        SimulationResult r = st.result;
        ganttView.setData(r, st.currentTime);
        timeLabel.setText("t = " + st.currentTime + " / " + r.totalTime);
        transportPlayIcon.setText(st.playing ? "❚❚" : "▶");

        float pct = r.totalTime > 0 ? (float) st.currentTime / r.totalTime : 0f;
        ((LinearLayout.LayoutParams) progressFill.getLayoutParams()).weight = pct;
        ((LinearLayout.LayoutParams) progressSpacer.getLayoutParams()).weight = 1f - pct;
        progressFill.requestLayout();

        // speed selector
        speedRow.removeAllViews();
        for (int i = 0; i < Presets.PLAYBACK_SPEEDS.length; i++) {
            double sp = Presets.PLAYBACK_SPEEDS[i];
            String label = (sp == Math.floor(sp) ? String.valueOf((int) sp) : String.valueOf(sp)) + "x";
            TextView pill = Ui.pill(c, label, st.speed == sp, true);
            pill.setPadding(0, Ui.dp(c, 6), 0, Ui.dp(c, 6));
            pill.setOnClickListener(v -> vm.setSpeed(sp));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, WRAP, 1f);
            if (i > 0) {
                lp.leftMargin = Ui.dp(c, 6);
            }
            speedRow.addView(pill, lp);
        }

        // CPU / ready / completed
        rebuildCpuQueue(c, r, st.currentTime);
    }

    private void rebuildCpuQueue(Context c, SimulationResult r, int currentTime) {
        cpuQueueInner.removeAllViews();
        PlaybackView pv = Playback.derive(r, currentTime);

        // left: CPU
        LinearLayout left = new LinearLayout(c);
        left.setOrientation(LinearLayout.VERTICAL);
        left.setGravity(Gravity.CENTER_HORIZONTAL);
        cpuQueueInner.addView(left, new LinearLayout.LayoutParams(WRAP, WRAP));

        TextView cpuLabel = columnLabel(c, "CPU");
        LinearLayout.LayoutParams cll = new LinearLayout.LayoutParams(WRAP, WRAP);
        cll.bottomMargin = Ui.dp(c, 7);
        left.addView(cpuLabel, cll);

        LinearLayout cpuBox = new LinearLayout(c);
        cpuBox.setGravity(Gravity.CENTER);
        cpuBox.setBackground(Ui.round(0x00000000, Ui.dp(c, 14), Ui.primary(c), Ui.dp(c, 2)));
        left.addView(cpuBox, new LinearLayout.LayoutParams(Ui.dp(c, 62), Ui.dp(c, 62)));
        if (pv.runningPid != null) {
            TextView chip = Ui.chip(c, "P" + pv.runningPid, Presets.processColor(pv.runningPid), false, 14);
            cpuBox.addView(chip, new LinearLayout.LayoutParams(Ui.dp(c, 42), Ui.dp(c, 42)));
        }
        TextView meta = Ui.tv(c, pv.runMeta.name().toLowerCase(), 9.5f, Ui.muted(c), false, true);
        LinearLayout.LayoutParams ml = new LinearLayout.LayoutParams(WRAP, WRAP);
        ml.topMargin = Ui.dp(c, 6);
        left.addView(meta, ml);

        // right: ready + completed
        LinearLayout right = new LinearLayout(c);
        right.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams rl = new LinearLayout.LayoutParams(0, WRAP, 1f);
        rl.leftMargin = Ui.dp(c, 12);
        cpuQueueInner.addView(right, rl);

        right.addView(columnLabel(c, "READY QUEUE"), labelLp(c));
        FlowLayout ready = new FlowLayout(c);
        ready.setGaps(Ui.dp(c, 6), Ui.dp(c, 6));
        ready.setBackground(Ui.round(Ui.surface2(c), Ui.dp(c, 11), Ui.border(c), Ui.dp(c, 1)));
        int rp = Ui.dp(c, 9);
        ready.setPadding(rp, rp, rp, rp);
        ready.setMinimumHeight(Ui.dp(c, 42));
        for (int id : pv.readyIds) {
            TextView chip = Ui.chip(c, "P" + id, Presets.processColor(id), false, 11);
            ready.addView(chip, new ViewGroup.LayoutParams(Ui.dp(c, 34), Ui.dp(c, 24)));
        }
        right.addView(ready, new LinearLayout.LayoutParams(MATCH, WRAP));

        TextView doneLabel = columnLabel(c, "COMPLETED");
        LinearLayout.LayoutParams dll = new LinearLayout.LayoutParams(WRAP, WRAP);
        dll.topMargin = Ui.dp(c, 10);
        dll.bottomMargin = Ui.dp(c, 7);
        right.addView(doneLabel, dll);
        FlowLayout done = new FlowLayout(c);
        done.setGaps(Ui.dp(c, 6), Ui.dp(c, 6));
        done.setMinimumHeight(Ui.dp(c, 24));
        for (int id : pv.doneIds) {
            TextView chip = Ui.chip(c, "P" + id, Presets.processColor(id), true, 11);
            done.addView(chip, new ViewGroup.LayoutParams(Ui.dp(c, 34), Ui.dp(c, 24)));
        }
        right.addView(done, new LinearLayout.LayoutParams(MATCH, WRAP));
    }

    private LinearLayout.LayoutParams labelLp(Context c) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP, WRAP);
        lp.bottomMargin = Ui.dp(c, 7);
        return lp;
    }

    private String fmt(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }
}
