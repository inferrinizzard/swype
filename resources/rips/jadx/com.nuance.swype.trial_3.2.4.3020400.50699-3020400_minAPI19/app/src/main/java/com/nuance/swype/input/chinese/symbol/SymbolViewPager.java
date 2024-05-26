package com.nuance.swype.input.chinese.symbol;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/* loaded from: classes.dex */
public class SymbolViewPager extends ViewPager {
    private boolean allowScroll;

    public SymbolViewPager(Context context) {
        super(context);
        this.allowScroll = true;
    }

    public SymbolViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.allowScroll = true;
    }

    public void setAllowScroll(boolean allowScroll) {
        this.allowScroll = allowScroll;
    }

    @Override // android.support.v4.view.ViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.allowScroll) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.view.ViewPager, android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
