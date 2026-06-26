package com.cpuscheduler.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cpuscheduler.app.MainActivity;

/** Home tab: hero pitch, primary CTA, two feature cards, and an algorithm summary. */
public class HomeFragment extends Fragment {

    private static final int MATCH = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int WRAP = ViewGroup.LayoutParams.WRAP_CONTENT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle b) {
        Context c = requireContext();
        ScrollView scroll = new ScrollView(c);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        int padH = Ui.dp(c, 18);
        scroll.setPadding(padH, Ui.dp(c, 4), padH, Ui.dp(c, 24));

        LinearLayout col = new LinearLayout(c);
        col.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(col, new ScrollView.LayoutParams(MATCH, WRAP));

        col.addView(hero(c));
        col.addView(cta(c));
        col.addView(featureRow(c));
        col.addView(algorithmsCard(c));
        return scroll;
    }

    private void nav(String tab) {
        ((MainActivity) requireActivity()).selectTab(tab);
    }

    private View hero(Context c) {
        LinearLayout hero = new LinearLayout(c);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(Ui.dp(c, 2), 0, Ui.dp(c, 2), 0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH, WRAP);
        lp.topMargin = Ui.dp(c, 12);
        lp.bottomMargin = Ui.dp(c, 18);
        hero.setLayoutParams(lp);

        TextView eyebrow = Ui.tv(c, "OS · PROCESS MANAGEMENT", 10, Ui.primary(c), true, true);
        eyebrow.setLetterSpacing(0.18f);
        eyebrow.setPadding(Ui.dp(c, 10), Ui.dp(c, 4), Ui.dp(c, 10), Ui.dp(c, 4));
        eyebrow.setBackground(Ui.round(0x00000000, Ui.dp(c, 20), Ui.primary(c), Ui.dp(c, 1)));
        eyebrow.setLayoutParams(new LinearLayout.LayoutParams(WRAP, WRAP));
        hero.addView(eyebrow);

        TextView headline = Ui.tv(c, "See how the CPU picks what runs next.", 34, Ui.text(c), true, false);
        headline.setLetterSpacing(-0.018f);
        headline.setLineSpacing(0, 1.05f);
        LinearLayout.LayoutParams hl = new LinearLayout.LayoutParams(MATCH, WRAP);
        hl.topMargin = Ui.dp(c, 14);
        headline.setLayoutParams(hl);
        hero.addView(headline);

        TextView sub = Ui.tv(c,
                "Build a process set, run five classic scheduling algorithms, and watch the Gantt chart form second by second.",
                14, Ui.muted(c), false, false);
        sub.setLineSpacing(0, 1.4f);
        LinearLayout.LayoutParams sl = new LinearLayout.LayoutParams(Ui.dp(c, 300), WRAP);
        sl.topMargin = Ui.dp(c, 8);
        sub.setLayoutParams(sl);
        hero.addView(sub);
        return hero;
    }

    private View cta(Context c) {
        LinearLayout cta = new LinearLayout(c);
        cta.setOrientation(LinearLayout.HORIZONTAL);
        cta.setGravity(Gravity.CENTER_VERTICAL);
        cta.setBackground(Ui.round(Ui.primary(c), Ui.dp(c, 16), 0, 0));
        int p = Ui.dp(c, 16);
        cta.setPadding(p, p, p, p);
        cta.setClickable(true);
        cta.setOnClickListener(v -> nav("sim"));

        TextView label = Ui.tv(c, "Launch Simulator", 16, Ui.onPrimary(c), true, false);
        label.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP, 1f));
        cta.addView(label);
        cta.addView(Ui.tv(c, "→", 19, Ui.onPrimary(c), false, false));

        cta.setLayoutParams(new LinearLayout.LayoutParams(MATCH, WRAP));
        return cta;
    }

    private View featureRow(Context c) {
        LinearLayout row = new LinearLayout(c);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH, WRAP);
        lp.topMargin = Ui.dp(c, 14);
        row.setLayoutParams(lp);

        View a = featureCard(c, compareGlyph(c), "Compare", "Run algorithms side by side", () -> nav("compare"));
        View b = featureCard(c, learnGlyph(c), "Learn", "Theory behind each policy", () -> nav("learn"));
        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(0, WRAP, 1f);
        ap.rightMargin = Ui.dp(c, 11);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(0, WRAP, 1f);
        row.addView(a, ap);
        row.addView(b, bp);
        return row;
    }

    private View featureCard(Context c, View glyph, String title, String subtitle, Runnable onClick) {
        LinearLayout card = new LinearLayout(c);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(Ui.round(Ui.surface(c), Ui.dp(c, 16), Ui.border(c), Ui.dp(c, 1)));
        int p = Ui.dp(c, 15);
        card.setPadding(p, p, p, p);
        card.setClickable(true);
        card.setOnClickListener(v -> onClick.run());
        card.addView(glyph);
        TextView t = Ui.tv(c, title, 15, Ui.text(c), true, false);
        card.addView(t);
        TextView s = Ui.tv(c, subtitle, 11.5f, Ui.muted(c), false, false);
        s.setLineSpacing(0, 1.2f);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(MATCH, WRAP);
        sp.topMargin = Ui.dp(c, 2);
        card.addView(s, sp);
        return card;
    }

    private View compareGlyph(Context c) {
        LinearLayout g = new LinearLayout(c);
        g.setOrientation(LinearLayout.HORIZONTAL);
        g.setGravity(Gravity.BOTTOM);
        LinearLayout.LayoutParams gp = new LinearLayout.LayoutParams(WRAP, Ui.dp(c, 24));
        gp.bottomMargin = Ui.dp(c, 10);
        g.setLayoutParams(gp);
        g.addView(bar(c, 8, 16, 0xFF34D399, 0));
        g.addView(bar(c, 8, 24, 0xFFA78BFA, Ui.dp(c, 3)));
        return g;
    }

    private View bar(Context c, int wDp, int hDp, int color, int leftMargin) {
        View v = new View(c);
        v.setBackground(Ui.round(color, Ui.dp(c, 2), 0, 0));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Ui.dp(c, wDp), Ui.dp(c, hDp));
        lp.leftMargin = leftMargin;
        v.setLayoutParams(lp);
        return v;
    }

    private View learnGlyph(Context c) {
        LinearLayout ring = new LinearLayout(c);
        ring.setGravity(Gravity.CENTER);
        ring.setBackground(Ui.round(0x00000000, Ui.dp(c, 12), 0xFFFBBF24, Ui.dp(c, 2)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Ui.dp(c, 24), Ui.dp(c, 24));
        lp.bottomMargin = Ui.dp(c, 10);
        ring.setLayoutParams(lp);
        View mark = new View(c);
        mark.setBackground(Ui.round(0xFFFBBF24, Ui.dp(c, 2), 0, 0));
        ring.addView(mark, new LinearLayout.LayoutParams(Ui.dp(c, 3), Ui.dp(c, 9)));
        return ring;
    }

    private View algorithmsCard(Context c) {
        LinearLayout card = Ui.card(c);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH, WRAP);
        lp.topMargin = Ui.dp(c, 18);
        card.setLayoutParams(lp);

        TextView label = Ui.tv(c, "SUPPORTED ALGORITHMS", 11, Ui.muted(c), true, false);
        label.setLetterSpacing(0.09f);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(MATCH, WRAP);
        ll.bottomMargin = Ui.dp(c, 11);
        card.addView(label, ll);

        FlowLayout chips = new FlowLayout(c);
        chips.setGaps(Ui.dp(c, 7), Ui.dp(c, 7));
        for (String s : new String[]{"FCFS", "SJF", "SRTF", "RR", "Priority"}) {
            TextView chip = Ui.tv(c, s, 12, Ui.text(c), false, true);
            chip.setPadding(Ui.dp(c, 12), Ui.dp(c, 7), Ui.dp(c, 12), Ui.dp(c, 7));
            chip.setBackground(Ui.round(Ui.surface2(c), Ui.dp(c, 9), Ui.border(c), Ui.dp(c, 1)));
            chips.addView(chip);
        }
        card.addView(chips, new LinearLayout.LayoutParams(MATCH, WRAP));

        View divider = new View(c);
        LinearLayout.LayoutParams dl = new LinearLayout.LayoutParams(MATCH, Ui.dp(c, 1));
        dl.topMargin = Ui.dp(c, 15);
        divider.setBackground(Ui.round(Ui.border(c), 0, 0, 0));
        card.addView(divider, dl);

        LinearLayout stats = new LinearLayout(c);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams sl = new LinearLayout.LayoutParams(MATCH, WRAP);
        sl.topMargin = Ui.dp(c, 14);
        card.addView(stats, sl);
        stats.addView(stat(c, "5", "algorithms"));
        stats.addView(stat(c, "3", "metrics"));
        stats.addView(stat(c, "∞", "process sets"));
        return card;
    }

    private View stat(Context c, String value, String labelText) {
        LinearLayout s = new LinearLayout(c);
        s.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP, WRAP);
        lp.rightMargin = Ui.dp(c, 18);
        s.setLayoutParams(lp);
        s.addView(Ui.tv(c, value, 21, Ui.primary(c), true, true));
        s.addView(Ui.tv(c, labelText, 10.5f, Ui.muted(c), false, false));
        return s;
    }
}
