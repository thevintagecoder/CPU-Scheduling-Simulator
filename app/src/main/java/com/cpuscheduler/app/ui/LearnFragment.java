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

import com.cpuscheduler.app.engine.Algorithm;
import com.cpuscheduler.app.engine.Theory;
import com.cpuscheduler.app.engine.TheoryCard;

import java.util.List;

/** Learn tab: a single-open accordion of theory cards (FCFS open by default). */
public class LearnFragment extends Fragment {

    private static final int MATCH = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int WRAP = ViewGroup.LayoutParams.WRAP_CONTENT;

    private Algorithm openKey = Algorithm.FCFS;
    private LinearLayout list;

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

        TextView intro = Ui.tv(c, "The five policies in this simulator — tap to expand.", 13, Ui.muted(c), false, false);
        intro.setLineSpacing(0, 1.3f);
        intro.setPadding(Ui.dp(c, 2), 0, Ui.dp(c, 2), 0);
        LinearLayout.LayoutParams il = new LinearLayout.LayoutParams(MATCH, WRAP);
        il.topMargin = Ui.dp(c, 8);
        il.bottomMargin = Ui.dp(c, 14);
        col.addView(intro, il);

        list = new LinearLayout(c);
        list.setOrientation(LinearLayout.VERTICAL);
        col.addView(list, new LinearLayout.LayoutParams(MATCH, WRAP));
        rebuild();
        return scroll;
    }

    private void rebuild() {
        Context c = requireContext();
        list.removeAllViews();
        List<TheoryCard> cards = Theory.cards();
        for (int i = 0; i < cards.size(); i++) {
            TheoryCard card = cards.get(i);
            View v = cardView(c, card);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH, WRAP);
            if (i > 0) {
                lp.topMargin = Ui.dp(c, 9);
            }
            list.addView(v, lp);
        }
    }

    private View cardView(Context c, TheoryCard card) {
        LinearLayout container = new LinearLayout(c);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setBackground(Ui.round(Ui.surface(c), Ui.dp(c, 14), Ui.border(c), Ui.dp(c, 1)));

        // header (toggle)
        LinearLayout header = new LinearLayout(c);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        int p = Ui.dp(c, 14);
        header.setPadding(p, p, p, p);
        header.setClickable(true);
        header.setOnClickListener(v -> {
            openKey = openKey == card.key ? null : card.key;
            rebuild();
        });

        View dot = new View(c);
        dot.setBackground(Ui.round(card.accent, Ui.dp(c, 3), 0, 0));
        LinearLayout.LayoutParams dl = new LinearLayout.LayoutParams(Ui.dp(c, 9), Ui.dp(c, 9));
        dl.rightMargin = Ui.dp(c, 11);
        header.addView(dot, dl);

        LinearLayout mid = new LinearLayout(c);
        mid.setOrientation(LinearLayout.VERTICAL);
        mid.addView(Ui.tv(c, card.name, 15, Ui.text(c), true, false));
        mid.addView(Ui.tv(c, card.tag, 10.5f, Ui.muted(c), false, true));
        header.addView(mid, new LinearLayout.LayoutParams(0, WRAP, 1f));

        boolean open = openKey == card.key;
        header.addView(Ui.tv(c, open ? "▾" : "▸", 13, Ui.muted(c), false, false));
        container.addView(header, new LinearLayout.LayoutParams(MATCH, WRAP));

        if (open) {
            LinearLayout body = new LinearLayout(c);
            body.setOrientation(LinearLayout.VERTICAL);
            body.setPadding(Ui.dp(c, 14), 0, Ui.dp(c, 14), Ui.dp(c, 15));

            TextView desc = Ui.tv(c, card.description, 13, Ui.text(c), false, false);
            desc.setLineSpacing(0, 1.35f);
            LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(MATCH, WRAP);
            dlp.bottomMargin = Ui.dp(c, 12);
            body.addView(desc, dlp);

            LinearLayout pc = new LinearLayout(c);
            pc.setOrientation(LinearLayout.HORIZONTAL);
            View pro = proCon(c, "PRO", Theory.PRO_COLOR, card.pro);
            View con = proCon(c, "CON", Theory.CON_COLOR, card.con);
            LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(0, WRAP, 1f);
            pl.rightMargin = Ui.dp(c, 8);
            LinearLayout.LayoutParams cl = new LinearLayout.LayoutParams(0, WRAP, 1f);
            pc.addView(pro, pl);
            pc.addView(con, cl);
            body.addView(pc, new LinearLayout.LayoutParams(MATCH, WRAP));

            container.addView(body, new LinearLayout.LayoutParams(MATCH, WRAP));
        }
        return container;
    }

    private View proCon(Context c, String kind, int color, String text) {
        LinearLayout box = new LinearLayout(c);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setBackground(Ui.round(Ui.withAlpha(color, 0x17), Ui.dp(c, 10), Ui.withAlpha(color, 0x4D), Ui.dp(c, 1)));
        int p = Ui.dp(c, 10);
        box.setPadding(p, p, p, p);

        TextView label = Ui.tv(c, kind, 9.5f, color, true, false);
        label.setLetterSpacing(0.08f);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(WRAP, WRAP);
        ll.bottomMargin = Ui.dp(c, 5);
        box.addView(label, ll);

        TextView body = Ui.tv(c, text, 11.5f, Ui.text(c), false, false);
        body.setLineSpacing(0, 1.25f);
        box.addView(body);
        return box;
    }
}
