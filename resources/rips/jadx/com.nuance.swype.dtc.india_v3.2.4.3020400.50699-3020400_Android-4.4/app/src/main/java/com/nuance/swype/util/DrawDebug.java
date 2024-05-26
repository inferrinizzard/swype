package com.nuance.swype.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

/* loaded from: classes.dex */
public final class DrawDebug {
    private final Paint baselinePaint;
    private final Drawable borderDrawable;
    private final Drawable paddingDrawable;

    public final void drawOriginLines$38ef7fb0(Canvas canvas, int width, int height, float xOriginOffset, float baselineOffset) {
        float xOrigin = 0.0f + xOriginOffset;
        float yBaseline = 0.0f + baselineOffset;
        canvas.drawLine(0.0f, yBaseline, width + 0, yBaseline, this.baselinePaint);
        canvas.drawLine(xOrigin, 0.0f, xOrigin, height + 0, this.baselinePaint);
    }

    public final void drawPaddingOutline(View view, Canvas canvas) {
        int left = view.getPaddingLeft();
        int top = view.getPaddingTop();
        int right = view.getWidth() - view.getPaddingRight();
        int bottom = view.getHeight() - view.getPaddingBottom();
        this.paddingDrawable.setBounds(left, top, right, bottom);
        this.paddingDrawable.draw(canvas);
    }

    public final void drawBorderOutline$1be95c50(Canvas canvas, int width, int height) {
        this.borderDrawable.setBounds(0, 0, width + 0, height + 0);
        this.borderDrawable.draw(canvas);
    }
}
