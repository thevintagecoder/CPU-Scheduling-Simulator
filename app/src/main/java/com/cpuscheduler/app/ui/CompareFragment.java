package com.cpuscheduler.app.ui;

import android.content.Context;
import android.os.Bundle;
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
import com.cpuscheduler.app.engine.SchedulingEngine;
import com.cpuscheduler.app.model.CompareEntry;
import com.cpuscheduler.app.model.CompareResponse;
import com.cpuscheduler.app.model.ProcessInput;
import com.cpuscheduler.app.state.SchedulerViewModel;
import com.cpuscheduler.app.state.SimState;

import java.util.ArrayList;
import java.util.List;

/** Compare tab: same process set across selected policies; lowest avg wait wins. */
public class CompareFragment extends Fragment {

    private static final int MATCH = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int WRAP = ViewGroup.LayoutParams.WRAP_CONTENT;

    private SchedulerViewModel vm;
    private FlowLayout selector;
    private LinearLayout cards;
    private String lastSig = null;

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

        TextView intro = Ui.tv(c, "Same process set, every policy. Lowest average wait wins.", 13, Ui.muted(c), false, false);
        intro.setLineSpacing(0, 1.3f);
        intro.setPadding(Ui.dp(c, 2), 0, Ui.dp(c, 2), 0);
        LinearLayout.LayoutParams il = new LinearLayout.LayoutParams(MATCH, WRAP);
        il.topMargin = Ui.dp(c, 8);
        il.bottomMargin = Ui.dp(c, 12);
        col.addView(intro, il);

        selector = new FlowLayout(c);
        selector.setGaps(Ui.dp(c, 7), Ui.dp(c, 7));
        LinearLayout.LayoutParams sl = new LinearLayout.LayoutParams(MATCH, WRAP);
        sl.bottomMargin = Ui.dp(c, 16);
        col.addView(selector, sl);

        cards = new LinearLayout(c);
        cards.setOrientation(LinearLayout.VERTICAL);
        col.addView(cards, new LinearLayout.LayoutParams(MATCH, WRAP));

        vm.state().observe(getViewLifecycleOwner(), this::render);
        return scroll;
    }

    private void render(SimState st) {
        String sig = sig(st);
        if (sig.equals(lastSig)) {
            return;
        }
        lastSig = sig;
        Context c = requireContext();

        // selector pills
        selector.removeAllViews();
        for (Algorithm a : Algorithm.ORDER) {
            boolean active = st.selected.contains(a);
            TextView pill = Ui.pill(c, a.shortLabel, active, false);
            pill.setOnClickListener(v -> vm.toggleCompare(a));
            selector.addView(pill);
        }

        // ordered selection (canonical order keeps cards stable)
        List<Algorithm> ordered = new ArrayList<>();
        for (Algorithm a : Algorithm.ORDER) {
            if (st.selected.contains(a)) {
                ordered.add(a);
            }
        }
        CompareResponse resp = SchedulingEngine.compare(st.processes, ordered, st.quantum);

        cards.removeAllViews();
        for (int i = 0; i < resp.entries.size(); i++) {
            View card = compareCard(c, resp.entries.get(i));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH, WRAP);
            if (i > 0) {
                lp.topMargin = Ui.dp(c, 11);
            }
            cards.addView(card, lp);
        }
    }

    private String sig(SimState st) {
        StringBuilder sb = new StringBuilder();
        for (ProcessInput p : st.processes) {
            sb.append(p.id).append(':').append(p.arrival).append(':')
                    .append(p.burst).append(':').append(p.priority).append('|');
        }
        sb.append('#').append(st.quantum).append('#');
        for (Algorithm a : st.selected) {
            sb.append(a).append(',');
        }
        return sb.toString();
    }

    private View compareCard(Context c, CompareEntry entry) {
        LinearLayout card = new LinearLayout(c);
        card.setOrientation(LinearLayout.VERTICAL);
        int borderColor = entry.best ? Ui.primary(c) : Ui.border(c);
        card.setBackground(Ui.round(Ui.surface(c), Ui.dp(c, 16), borderColor, Ui.dp(c, 1)));
        int p = Ui.dp(c, 14);
        card.setPadding(p, p, p, p);

        // top row
        LinearLayout top = new LinearLayout(c);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams tl = new LinearLayout.LayoutParams(MATCH, WRAP);
        tl.bottomMargin = Ui.dp(c, 12);
        card.addView(top, tl);

        LinearLayout names = new LinearLayout(c);
        names.setOrientation(LinearLayout.VERTICAL);
        names.addView(Ui.tv(c, entry.algorithm.shortLabel, 15, Ui.text(c), true, false));
        names.addView(Ui.tv(c, entry.algorithm.fullLabel, 10.5f, Ui.muted(c), false, false));
        top.addView(names, new LinearLayout.LayoutParams(0, WRAP, 1f));

        if (entry.best) {
            TextView badge = Ui.tv(c, "BEST WT", 10, Ui.onPrimary(c), true, false);
            badge.setLetterSpacing(0.05f);
            badge.setBackground(Ui.round(Ui.primary(c), Ui.dp(c, 20), 0, 0));
            badge.setPadding(Ui.dp(c, 9), Ui.dp(c, 4), Ui.dp(c, 9), Ui.dp(c, 4));
            top.addView(badge);
        }

        // mini gantt
        HorizontalScrollView hs = new HorizontalScrollView(c);
        hs.setHorizontalScrollBarEnabled(false);
        MiniGanttView mini = new MiniGanttView(c);
        mini.setData(entry.result);
        hs.addView(mini);
        LinearLayout.LayoutParams ml = new LinearLayout.LayoutParams(MATCH, WRAP);
        ml.bottomMargin = Ui.dp(c, 12);
        card.addView(hs, ml);

        // stats
        LinearLayout stats = new LinearLayout(c);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        card.addView(stats, new LinearLayout.LayoutParams(MATCH, WRAP));
        stats.addView(compareStat(c, fmt(entry.result.averages.waiting), "wait", true, 0));
        stats.addView(compareStat(c, fmt(entry.result.averages.turnaround), "turn", false, Ui.dp(c, 8)));
        stats.addView(compareStat(c, fmt(entry.result.averages.response), "resp", false, Ui.dp(c, 8)));
        return card;
    }

    private View compareStat(Context c, String value, String label, boolean accent, int leftMargin) {
        LinearLayout s = new LinearLayout(c);
        s.setOrientation(LinearLayout.VERTICAL);
        s.setGravity(Gravity.CENTER_HORIZONTAL);
        s.setBackground(Ui.round(Ui.surface2(c), Ui.dp(c, 10), 0, 0));
        s.setPadding(Ui.dp(c, 4), Ui.dp(c, 8), Ui.dp(c, 4), Ui.dp(c, 8));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, WRAP, 1f);
        lp.leftMargin = leftMargin;
        s.setLayoutParams(lp);
        s.addView(Ui.tv(c, value, 17, accent ? Ui.primary(c) : Ui.text(c), true, true));
        TextView l = Ui.tv(c, label, 9.5f, Ui.muted(c), false, false);
        s.addView(l);
        return s;
    }

    private String fmt(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }
}
