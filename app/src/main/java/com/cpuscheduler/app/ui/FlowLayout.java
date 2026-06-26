package com.cpuscheduler.app.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/** Minimal wrapping container (flex-wrap) with horizontal/vertical gaps. */
public class FlowLayout extends ViewGroup {

    private int hGap;
    private int vGap;

    public FlowLayout(Context c) {
        super(c);
    }

    public void setGaps(int h, int v) {
        this.hGap = h;
        this.vGap = v;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int width = MeasureSpec.getSize(widthSpec);
        int maxW = width - getPaddingLeft() - getPaddingRight();
        int x = 0, y = 0, rowH = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View ch = getChildAt(i);
            if (ch.getVisibility() == GONE) {
                continue;
            }
            measureChild(ch,
                    MeasureSpec.makeMeasureSpec(maxW, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int cw = ch.getMeasuredWidth();
            int chh = ch.getMeasuredHeight();
            if (x > 0 && x + cw > maxW) {
                x = 0;
                y += rowH + vGap;
                rowH = 0;
            }
            x += cw + hGap;
            rowH = Math.max(rowH, chh);
        }
        int height = getPaddingTop() + getPaddingBottom() + y + rowH;
        height = Math.max(height, getSuggestedMinimumHeight());
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int padL = getPaddingLeft();
        int padT = getPaddingTop();
        int maxW = getWidth() - padL - getPaddingRight();
        int x = padL, y = padT, rowH = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View c = getChildAt(i);
            if (c.getVisibility() == GONE) {
                continue;
            }
            int cw = c.getMeasuredWidth();
            int chh = c.getMeasuredHeight();
            if (x > padL && x + cw > padL + maxW) {
                x = padL;
                y += rowH + vGap;
                rowH = 0;
            }
            c.layout(x, y, x + cw, y + chh);
            x += cw + hGap;
            rowH = Math.max(rowH, chh);
        }
    }
}
