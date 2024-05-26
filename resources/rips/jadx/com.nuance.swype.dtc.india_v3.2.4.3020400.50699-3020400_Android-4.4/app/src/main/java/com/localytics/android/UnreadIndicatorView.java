package com.localytics.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/* loaded from: classes.dex */
public class UnreadIndicatorView extends View {
    private static final String DEFAULT_COLOR = "#007AFF";
    private int mColor;
    private Paint mPaint;

    public UnreadIndicatorView(Context context) {
        this(context, null);
    }

    public UnreadIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnreadIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPaint = new Paint(1);
        this.mColor = Color.parseColor(DEFAULT_COLOR);
    }

    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float centerY = getHeight() / 2.0f;
        float centerX = getWidth() / 2.0f;
        float radius = Math.min(centerX, centerY);
        this.mPaint.setStrokeWidth(1.0f);
        this.mPaint.setColor(this.mColor);
        this.mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, radius, this.mPaint);
    }
}
