package com.nuance.swype.input.emoji;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class SkinToneView extends View {
    private static final LogManager.Log log = LogManager.getLog("SkinToneCustomView");
    private int desiredHeight;
    private int desiredWidth;
    private boolean hasPosition;
    private Paint paint;
    private int[] pos;
    private Rect r;
    private float textScale;
    private String value;

    public SkinToneView(Context context) {
        super(context);
        this.paint = new Paint();
        this.pos = new int[2];
        this.r = new Rect();
        this.textScale = 1.0f;
    }

    public SkinToneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.paint = new Paint();
        this.pos = new int[2];
        this.r = new Rect();
        this.textScale = 1.0f;
        setWillNotDraw(false);
    }

    public SkinToneView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.paint = new Paint();
        this.pos = new int[2];
        this.r = new Rect();
        this.textScale = 1.0f;
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (this.value != null) {
            drawTextData(canvas, this.paint, this.value);
        }
        super.onDraw(canvas);
    }

    private void drawTextData(Canvas canvas, Paint paint, String text) {
        canvas.getClipBounds(this.r);
        int cHeight = this.r.height();
        int cWidth = this.r.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), this.r);
        float x = ((cWidth / 2.0f) - (this.r.width() / 2.0f)) - this.r.left;
        float y = ((cHeight / 2.0f) + (this.r.height() / 2.0f)) - this.r.bottom;
        log.d("drawCenter()", " called >>>>> x =" + x + " , y::" + y);
        if (this.hasPosition) {
            canvas.drawText(text, this.pos[0] - paint.getTextSize(), this.pos[1], paint);
        } else {
            canvas.drawText(text, x, y, paint);
        }
    }

    public void setPaintTextSize(float textSizeUnscaled) {
        int textSize = (int) (this.textScale * textSizeUnscaled);
        this.paint.setTextSize(textSize);
    }

    public void setTextValue(String value) {
        this.value = value;
        invalidate();
    }

    public void setPosition(int xPos, int yPos) {
        this.hasPosition = true;
        this.pos[0] = xPos;
        this.pos[1] = yPos;
    }

    public void setWidth(int desiredWidth) {
        this.desiredWidth = desiredWidth;
    }

    public void setHeight(int desiredHeight) {
        this.desiredHeight = desiredHeight;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        new StringBuilder(" >>>>>>>> widthMode=").append(widthMode).append(" , widthSize=").append(widthSize).append(" , heightMode=").append(heightMode).append(" , heightSize=").append(heightSize);
        if (widthMode == 1073741824) {
            width = widthSize;
        } else if (widthMode == Integer.MIN_VALUE) {
            width = Math.min(this.desiredWidth, widthSize);
        } else {
            width = this.desiredWidth;
        }
        if (heightMode == 1073741824) {
            height = heightSize;
        } else if (heightMode == Integer.MIN_VALUE) {
            height = Math.min(this.desiredHeight, heightSize);
        } else {
            height = this.desiredHeight;
        }
        new StringBuilder(" >>>>>>>> width=").append(width).append(" , height=").append(height);
        setMeasuredDimension(getPaddingLeft() + width + getPaddingRight(), getPaddingTop() + height + getPaddingBottom());
    }
}
