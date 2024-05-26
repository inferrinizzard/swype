package com.nuance.swype.input.store;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/* loaded from: classes.dex */
public class ThemeViewPager extends ViewPager {
    private boolean mDisableScroll;

    public ThemeViewPager(Context context) {
        super(context);
    }

    public ThemeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.support.v4.view.ViewPager, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            if (this.mDisableScroll) {
                return false;
            }
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    @Override // android.support.v4.view.ViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case 2:
                if (this.mDisableScroll) {
                    return false;
                }
            case 0:
            case 1:
            default:
                return super.onInterceptTouchEvent(ev);
        }
    }

    public void setScrollDisabled(boolean disabled) {
        this.mDisableScroll = disabled;
    }
}
