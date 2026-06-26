package com.cpuscheduler.app.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.cpuscheduler.app.R;

/** Small styling toolkit: dp conversion, theme tokens, and reusable view factories. */
public final class Ui {

    private Ui() {}

    public static int dp(Context c, float v) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v, c.getResources().getDisplayMetrics()));
    }

    // ---- theme tokens (resolve the active light/dark variant automatically) ----
    public static int bg(Context c) { return ContextCompat.getColor(c, R.color.col_bg); }
    public static int surface(Context c) { return ContextCompat.getColor(c, R.color.col_surface); }
    public static int surface2(Context c) { return ContextCompat.getColor(c, R.color.col_surface2); }
    public static int border(Context c) { return ContextCompat.getColor(c, R.color.col_border); }
    public static int text(Context c) { return ContextCompat.getColor(c, R.color.col_text); }
    public static int muted(Context c) { return ContextCompat.getColor(c, R.color.col_muted); }
    public static int primary(Context c) { return ContextCompat.getColor(c, R.color.col_primary); }
    public static int onPrimary(Context c) { return ContextCompat.getColor(c, R.color.col_on_primary); }
    public static int fog(Context c) { return ContextCompat.getColor(c, R.color.col_fog); }

    public static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    /** Rounded-rectangle background with an optional stroke (px units). */
    public static GradientDrawable round(int fill, int radiusPx, int strokeColor, int strokeWidthPx) {
        GradientDrawable g = new GradientDrawable();
        g.setShape(GradientDrawable.RECTANGLE);
        g.setColor(fill);
        g.setCornerRadius(radiusPx);
        if (strokeWidthPx > 0) {
            g.setStroke(strokeWidthPx, strokeColor);
        }
        return g;
    }

    /** Dashed rounded-rectangle stroke (transparent fill) — used for idle Gantt blocks elsewhere. */
    public static GradientDrawable dashed(Context c, int strokeColor, int strokeWidthPx, int radiusPx) {
        GradientDrawable g = new GradientDrawable();
        g.setShape(GradientDrawable.RECTANGLE);
        g.setColor(0x00000000);
        g.setCornerRadius(radiusPx);
        g.setStroke(strokeWidthPx, strokeColor, dp(c, 4), dp(c, 3));
        return g;
    }

    public static TextView tv(Context c, String s, float spSize, int color, boolean bold, boolean mono) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, spSize);
        t.setTextColor(color);
        t.setTypeface(mono ? Typeface.MONOSPACE : Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL);
        t.setIncludeFontPadding(false);
        return t;
    }

    /** A standard surface Card container (rounded 16, 1dp border, 14 padding). */
    public static android.widget.LinearLayout card(Context c) {
        android.widget.LinearLayout l = new android.widget.LinearLayout(c);
        l.setOrientation(android.widget.LinearLayout.VERTICAL);
        l.setBackground(round(surface(c), dp(c, 16), border(c), dp(c, 1)));
        int p = dp(c, 14);
        l.setPadding(p, p, p, p);
        return l;
    }

    /** Selectable mono pill (algorithm / speed / compare toggle). */
    public static TextView pill(Context c, String label, boolean active, boolean mutedWhenInactive) {
        TextView t = new TextView(c);
        t.setText(label);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        t.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);
        int padH = dp(c, 13), padV = dp(c, 8);
        t.setPadding(padH, padV, padH, padV);
        t.setBackground(round(active ? primary(c) : surface2(c), dp(c, 10),
                active ? primary(c) : border(c), dp(c, 1)));
        t.setTextColor(active ? onPrimary(c) : (mutedWhenInactive ? muted(c) : text(c)));
        return t;
    }

    /** Process token chip. Filled (ready/running) or outline (completed). */
    public static TextView chip(Context c, String label, int color, boolean outline, float fontSp) {
        TextView t = new TextView(c);
        t.setText(label);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSp);
        t.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);
        t.setIncludeFontPadding(false);
        if (outline) {
            t.setBackground(round(0x00000000, dp(c, 7), color, dp(c, 1)));
            t.setTextColor(color);
            t.setAlpha(0.6f);
        } else {
            t.setBackground(round(color, dp(c, 7), 0, 0));
            t.setTextColor(onPrimary(c));
        }
        return t;
    }
}
