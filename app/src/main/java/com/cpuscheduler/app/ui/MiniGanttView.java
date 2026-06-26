package com.cpuscheduler.app.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.cpuscheduler.app.engine.Presets;
import com.cpuscheduler.app.model.ScheduleSegment;
import com.cpuscheduler.app.model.SimulationResult;

/** Compact Gantt strip used on the Compare screen — no axis, playhead, or labels. */
public class MiniGanttView extends View {

    private SimulationResult result;

    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final int unit;
    private final int height;
    private final int r4;

    public MiniGanttView(Context c) {
        super(c);
        unit = Ui.dp(c, 12);
        height = Ui.dp(c, 22);
        r4 = Ui.dp(c, 4);
        stroke.setStyle(Paint.Style.STROKE);
    }

    public void setData(SimulationResult result) {
        this.result = result;
        requestLayout();
        invalidate();
    }

    private int totalTime() {
        return result == null ? 0 : result.totalTime;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        setMeasuredDimension(Math.max(unit, totalTime() * unit), height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (result == null) {
            return;
        }
        Context c = getContext();
        for (ScheduleSegment seg : result.segments) {
            float left = seg.start * unit;
            float right = seg.end * unit;
            if (seg.idle || seg.pid == null) {
                stroke.setColor(Ui.border(c));
                stroke.setStrokeWidth(Ui.dp(c, 1));
                stroke.setPathEffect(new DashPathEffect(new float[]{Ui.dp(c, 4), Ui.dp(c, 3)}, 0));
                float inset = Ui.dp(c, 0.5f);
                canvas.drawRoundRect(new RectF(left + inset, inset, right - inset, height - inset), r4, r4, stroke);
                stroke.setPathEffect(null);
            } else {
                fill.setColor(Presets.processColor(seg.pid));
                canvas.drawRoundRect(new RectF(left, 0, right, height), r4, r4, fill);
            }
        }
    }
}
