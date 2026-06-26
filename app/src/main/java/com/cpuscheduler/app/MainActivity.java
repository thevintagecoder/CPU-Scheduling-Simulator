package com.cpuscheduler.app;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.cpuscheduler.app.ui.CompareFragment;
import com.cpuscheduler.app.ui.HomeFragment;
import com.cpuscheduler.app.ui.LearnFragment;
import com.cpuscheduler.app.ui.SimulatorFragment;
import com.cpuscheduler.app.ui.Ui;

/** Single-activity host: header, screen container, and the custom bottom nav. */
public class MainActivity extends AppCompatActivity {

    private static final String[] KEYS = {"home", "sim", "compare", "learn"};
    private static final String[] NAV_LABELS = {"Home", "Simulate", "Compare", "Learn"};
    private static final String[] TITLES = {"CPU Scheduler", "Simulator", "Compare", "Learn"};
    private final int[] icons = {
            R.drawable.ic_nav_home, R.drawable.ic_nav_sim,
            R.drawable.ic_nav_compare, R.drawable.ic_nav_learn
    };

    private String current = "home";
    private TextView titleView;
    private final ImageView[] navIcons = new ImageView[4];
    private final TextView[] navLabels = new TextView[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View dot = findViewById(R.id.accentDot);
        dot.setBackground(Ui.round(Ui.primary(this), Ui.dp(this, 4), 0, 0));

        titleView = findViewById(R.id.title);
        buildThemeToggle();
        buildBottomNav();

        current = savedInstanceState == null ? "home" : savedInstanceState.getString("tab", "home");
        selectTab(current);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle out) {
        super.onSaveInstanceState(out);
        out.putString("tab", current);
    }

    /** Switches the visible screen and updates the header title + nav highlight. */
    public void selectTab(String key) {
        current = key;
        int idx = indexOf(key);
        titleView.setText(TITLES[idx]);

        Fragment f;
        switch (key) {
            case "sim":
                f = new SimulatorFragment();
                break;
            case "compare":
                f = new CompareFragment();
                break;
            case "learn":
                f = new LearnFragment();
                break;
            case "home":
            default:
                f = new HomeFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();

        for (int i = 0; i < 4; i++) {
            int color = i == idx ? Ui.primary(this) : Ui.muted(this);
            navIcons[i].setColorFilter(color);
            navLabels[i].setTextColor(color);
        }
    }

    private int indexOf(String key) {
        for (int i = 0; i < KEYS.length; i++) {
            if (KEYS[i].equals(key)) {
                return i;
            }
        }
        return 0;
    }

    private void buildBottomNav() {
        LinearLayout nav = findViewById(R.id.bottomNav);
        for (int i = 0; i < 4; i++) {
            final String key = KEYS[i];
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            item.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            item.setClickable(true);
            item.setOnClickListener(v -> selectTab(key));

            ImageView icon = new ImageView(this);
            int s = Ui.dp(this, 21);
            icon.setLayoutParams(new LinearLayout.LayoutParams(s, s));
            icon.setImageResource(icons[i]);
            icon.setColorFilter(Ui.muted(this));
            navIcons[i] = icon;

            TextView label = Ui.tv(this, NAV_LABELS[i], 10, Ui.muted(this), true, false);
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tp.topMargin = Ui.dp(this, 4);
            label.setLayoutParams(tp);
            navLabels[i] = label;

            item.addView(icon);
            item.addView(label);
            nav.addView(item);
        }
    }

    private void buildThemeToggle() {
        FrameLayout toggle = findViewById(R.id.themeToggle);
        boolean dark = ThemePref.isDark(this);

        View track = new View(this);
        track.setLayoutParams(new FrameLayout.LayoutParams(Ui.dp(this, 52), Ui.dp(this, 28)));
        track.setBackground(Ui.round(Ui.surface2(this), Ui.dp(this, 20), Ui.border(this), Ui.dp(this, 1)));
        toggle.addView(track);

        TextView sun = Ui.tv(this, "☀", 10, Ui.muted(this), false, false);
        FrameLayout.LayoutParams sp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL | Gravity.START);
        sp.leftMargin = Ui.dp(this, 6);
        sun.setLayoutParams(sp);
        toggle.addView(sun);

        TextView moon = Ui.tv(this, "☾", 9, Ui.muted(this), false, false);
        FrameLayout.LayoutParams mp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL | Gravity.END);
        mp.rightMargin = Ui.dp(this, 6);
        moon.setLayoutParams(mp);
        toggle.addView(moon);

        View knob = new View(this);
        int k = Ui.dp(this, 22);
        FrameLayout.LayoutParams kp = new FrameLayout.LayoutParams(k, k, Gravity.CENTER_VERTICAL | Gravity.START);
        kp.leftMargin = Ui.dp(this, dark ? 4 : 26);
        knob.setLayoutParams(kp);
        GradientDrawable kg = new GradientDrawable();
        kg.setShape(GradientDrawable.OVAL);
        kg.setColor(Ui.primary(this));
        knob.setBackground(kg);
        toggle.addView(knob);

        toggle.setOnClickListener(v -> {
            boolean newDark = !ThemePref.isDark(this);
            ThemePref.setDark(this, newDark);
            AppCompatDelegate.setDefaultNightMode(
                    newDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
    }
}
