package com.nuance.swype.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.nuance.connect.common.Integers;
import com.nuance.swype.util.DrawDebug;

/* loaded from: classes.dex */
public class SimpleTextView extends View {
    private Rect bounds;
    private ColorStateList color;
    private final DrawDebug drawDebug;
    private int gravity;
    private Paint paint;
    private String text;
    private boolean useFullWidth;

    public SimpleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.useFullWidth = false;
        this.gravity = 1;
        this.paint = new TextPaint(1);
        this.bounds = new Rect();
        this.drawDebug = null;
        this.paint.setTextAlign(Paint.Align.LEFT);
    }

    public SimpleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleTextView(Context context) {
        this(context, null, 0);
    }

    public void setTextColor(int color) {
        this.color = ColorStateList.valueOf(color);
        invalidate();
    }

    public ColorStateList getTextColors() {
        return this.color;
    }

    public void setTypeface(Typeface tf) {
        if (this.paint.getTypeface() != tf) {
            this.paint.setTypeface(tf);
            requestLayout();
        }
    }

    public Typeface getTypeface() {
        return this.paint.getTypeface();
    }

    public void setText(CharSequence text) {
        this.text = text.toString();
        requestLayout();
    }

    public CharSequence getText() {
        return this.text;
    }

    public void setTextSize(int unit, float size) {
        Resources res = getContext().getResources();
        setRawTextSize(TypedValue.applyDimension(unit, size, res.getDisplayMetrics()));
    }

    public float getTextSize() {
        return this.paint.getTextSize();
    }

    private void setRawTextSize(float size) {
        if (size != this.paint.getTextSize()) {
            this.paint.setTextSize(size);
            requestLayout();
        }
    }

    public void setGravity(int gravity) {
        if (this.gravity != gravity) {
            this.gravity = gravity;
            requestLayout();
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int x;
        this.paint.setColor(this.color.getDefaultColor());
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        int internalWidth = getWidth() - (getPaddingLeft() + getPaddingRight());
        String text = getText().toString();
        int textWidth = this.useFullWidth ? getFullWidth(text) : this.bounds.width();
        switch (this.gravity & 7) {
            case 8388611:
                x = 0;
                break;
            case 8388612:
            default:
                x = (internalWidth - textWidth) / 2;
                break;
            case 8388613:
                x = internalWidth - textWidth;
                break;
        }
        int x2 = x + (-this.bounds.left);
        Paint.FontMetricsInt fm = this.paint.getFontMetricsInt();
        int fontHeight = Math.max(fm.bottom, this.bounds.bottom) - Math.min(this.bounds.top, fm.top);
        int yTop = (getHeight() - fontHeight) / 2;
        int yOffsetToBaseline = -Math.min(this.bounds.top, fm.top);
        int yBaseline = yTop + yOffsetToBaseline;
        canvas.drawText(text, x2, yBaseline, this.paint);
        if (this.drawDebug != null) {
            this.drawDebug.drawOriginLines$38ef7fb0(canvas, (getWidth() - getPaddingLeft()) - getPaddingRight(), (getHeight() - getPaddingTop()) - getPaddingBottom(), x2, yBaseline);
        }
        canvas.restore();
        if (this.drawDebug != null) {
            this.drawDebug.drawPaddingOutline(this, canvas);
        }
    }

    private int getFullWidth(String text) {
        int advanceWidth = (int) Math.ceil(this.paint.measureText(text));
        int left = Math.min(this.bounds.left, 0);
        return Math.max(this.bounds.right, advanceWidth) - left;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        String text = getText().toString();
        this.paint.getTextBounds(text, 0, text.length(), this.bounds);
        int textWidth = this.useFullWidth ? getFullWidth(text) : this.bounds.width();
        int paddingWidth = getPaddingLeft() + getPaddingRight();
        int minNeededWidth = textWidth + paddingWidth;
        switch (widthMode) {
            case Integers.STATUS_SUCCESS /* -2147483648 */:
                width = Math.max(widthSize, minNeededWidth);
                break;
            case 1073741824:
                width = widthSize;
                break;
            default:
                width = minNeededWidth;
                break;
        }
        Paint.FontMetricsInt fm = this.paint.getFontMetricsInt();
        int verPadding = getPaddingTop() + getPaddingBottom();
        int minNeededHeight = (Math.max(fm.bottom, this.bounds.bottom) - Math.min(this.bounds.top, fm.top)) + verPadding;
        switch (heightMode) {
            case Integers.STATUS_SUCCESS /* -2147483648 */:
                height = Math.max(heightSize, minNeededHeight);
                break;
            case 1073741824:
                height = heightSize;
                break;
            default:
                height = minNeededHeight;
                break;
        }
        setMeasuredDimension(width, height);
    }
}
