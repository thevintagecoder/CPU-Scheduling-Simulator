package com.cpuscheduler.app.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;

import com.cpuscheduler.app.engine.Presets;
import com.cpuscheduler.app.model.ScheduleSegment;
import com.cpuscheduler.app.model.SimulationResult;

import java.util.TreeSet;

/** The full Gantt chart: bars, fog overlay, playhead, and a boundary time axis. */
public class GanttView extends View {

    private SimulationResult result;
    private int currentTime;

    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint label = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axis = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final int unit;
    private final int barH;
    private final int topPad;
    private final int r7;
    private final int axisGap;
    private final int axisH;

    public GanttView(Context c) {
        super(c);
        unit = Ui.dp(c, 26);
        barH = Ui.dp(c, 44);
        topPad = Ui.dp(c, 3);
        r7 = Ui.dp(c, 7);
        axisGap = Ui.dp(c, 3);
        axisH = Ui.dp(c, 16);
        stroke.setStyle(Paint.Style.STROKE);
        label.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        label.setTextAlign(Paint.Align.CENTER);
        label.setTextSize(sp(c, 11));
        axis.setTypeface(Typeface.MONOSPACE);
        axis.setTextAlign(Paint.Align.CENTER);
        axis.setTextSize(sp(c, 9.5f));
    }

    private float sp(Context c, float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, v, c.getResources().getDisplayMetrics());
    }

    public void setData(SimulationResult result, int currentTime) {
        this.result = result;
        this.currentTime = currentTime;
        requestLayout();
        invalidate();
    }

    private int totalTime() {
        return result == null ? 0 : result.totalTime;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        setMeasuredDimension(Math.max(unit, totalTime() * unit), topPad + barH + axisGap + axisH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (result == null) {
            return;
        }
        Context c = getContext();
        int y0 = topPad;

        for (ScheduleSegment seg : result.segments) {
            float left = seg.start * unit;
            float right = seg.end * unit;
            if (seg.idle || seg.pid == null) {
                stroke.setColor(Ui.border(c));
                stroke.setStrokeWidth(Ui.dp(c, 1.5f));
                stroke.setPathEffect(new DashPathEffect(new float[]{Ui.dp(c, 4), Ui.dp(c, 3)}, 0));
                float inset = Ui.dp(c, 0.75f);
                canvas.drawRoundRect(new RectF(left + inset, y0 + inset, right - inset, y0 + barH - inset), r7, r7, stroke);
                stroke.setPathEffect(null);
            } else {
                fill.setColor(Presets.processColor(seg.pid));
                canvas.drawRoundRect(new RectF(left, y0, right, y0 + barH), r7, r7, fill);
                label.setColor(Ui.onPrimary(c));
                float cx = (left + right) / 2f;
                float cy = y0 + barH / 2f - (label.descent() + label.ascent()) / 2f;
                canvas.drawText("P" + seg.pid, cx, cy, label);
            }
        }

        float totalW = totalTime() * unit;
        float fogLeft = currentTime * unit;
        if (fogLeft < totalW) {
            fill.setColor(Ui.fog(c));
            canvas.drawRoundRect(new RectF(fogLeft, y0, totalW, y0 + barH), r7, r7, fill);
        }

        fill.setColor(Ui.primary(c));
        float px = currentTime * unit;
        canvas.drawRect(px - Ui.dp(c, 1), y0 - Ui.dp(c, 3), px + Ui.dp(c, 1), y0 + barH + Ui.dp(c, 8), fill);

        TreeSet<Integer> ticks = new TreeSet<>();
        ticks.add(0);
        for (ScheduleSegment seg : result.segments) {
            ticks.add(seg.start);
            ticks.add(seg.end);
        }
        axis.setColor(Ui.muted(c));
        float ty = y0 + barH + axisGap + axisH * 0.8f;
        for (int t : ticks) {
            canvas.drawText(String.valueOf(t), t * unit, ty, axis);
        }
    }
}
