package com.nuance.swype.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/* loaded from: classes.dex */
public class RightAlignedHorizontalScrollView extends HorizontalScrollView {
    public float mPercentOfWidth;

    public RightAlignedHorizontalScrollView(Context context) {
        super(context);
    }

    public RightAlignedHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RightAlignedHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.mPercentOfWidth >= 0.0f && this.mPercentOfWidth <= 1.0d) {
            scrollTo((int) (getChildAt(0).getMeasuredWidth() * this.mPercentOfWidth), 0);
        }
    }
}
