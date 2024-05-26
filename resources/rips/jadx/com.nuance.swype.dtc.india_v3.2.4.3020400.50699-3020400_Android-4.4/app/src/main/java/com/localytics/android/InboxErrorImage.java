package com.localytics.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;

/* loaded from: classes.dex */
class InboxErrorImage extends View {
    private static final int DEFAULT_STROKE_WIDTH_DP = 4;
    private int mColor;
    private Paint mPaint;
    private float mStart;
    private float mStrokeWidth;

    /* JADX INFO: Access modifiers changed from: protected */
    public InboxErrorImage(Context context) {
        super(context, null, 0);
        this.mPaint = new Paint(1);
        this.mColor = ContextCompat.getColor(getContext(), android.R.color.darker_gray);
        this.mStrokeWidth = TypedValue.applyDimension(1, 4.0f, getResources().getDisplayMetrics());
        double hypotenuse = Math.sqrt(Math.pow(this.mStrokeWidth, 2.0d) / 2.0d);
        double side = this.mStrokeWidth / 2.0d;
        this.mStart = (float) Math.sqrt(Math.pow(hypotenuse, 2.0d) - Math.pow(side, 2.0d));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.mPaint.setStrokeCap(Paint.Cap.BUTT);
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
        this.mPaint.setColor(this.mColor);
        canvas.drawLine(this.mStart, this.mStart, getWidth() - this.mStart, getHeight() - this.mStart, this.mPaint);
        canvas.drawLine(this.mStart, getHeight() - this.mStart, getWidth() - this.mStart, this.mStart, this.mPaint);
    }
}
