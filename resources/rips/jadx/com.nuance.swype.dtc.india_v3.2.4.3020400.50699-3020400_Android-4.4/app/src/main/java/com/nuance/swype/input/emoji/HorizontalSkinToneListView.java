package com.nuance.swype.input.emoji;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class HorizontalSkinToneListView extends RecyclerView {
    private static final LogManager.Log log = LogManager.getLog("MyRecyclerView");
    private int desiredHeight;
    private int desiredWidth;

    public HorizontalSkinToneListView(Context context) {
        super(context);
        this.desiredWidth = 0;
        this.desiredHeight = 0;
    }

    public HorizontalSkinToneListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.desiredWidth = 0;
        this.desiredHeight = 0;
    }

    public HorizontalSkinToneListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.desiredWidth = 0;
        this.desiredHeight = 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.RecyclerView, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        log.d("onMeasure()", " >>>>>>>> called ==");
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        log.d("onMeasure()", " >>>>>>>> widthMode=" + widthMode + " , widthSize=" + widthSize + " , heightMode=" + heightMode + " , heightSize=" + heightSize);
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
        log.d("onMeasure()", " >>>>>>>> width=" + width + " , height=" + height);
        setMeasuredDimension(getPaddingLeft() + width + getPaddingRight(), getPaddingTop() + height + getPaddingBottom());
    }
}
