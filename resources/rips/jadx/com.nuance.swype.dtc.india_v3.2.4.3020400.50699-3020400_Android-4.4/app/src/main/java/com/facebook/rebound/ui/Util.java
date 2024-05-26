package com.facebook.rebound.ui;

import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.FrameLayout;

/* loaded from: classes.dex */
public abstract class Util {
    public static final int dpToPx(float dp, Resources res) {
        return (int) TypedValue.applyDimension(1, dp, res.getDisplayMetrics());
    }

    public static final FrameLayout.LayoutParams createLayoutParams(int width, int height) {
        return new FrameLayout.LayoutParams(width, height);
    }

    public static final FrameLayout.LayoutParams createMatchParams() {
        return createLayoutParams(-1, -1);
    }

    public static final FrameLayout.LayoutParams createWrapParams() {
        return createLayoutParams(-2, -2);
    }

    public static final FrameLayout.LayoutParams createWrapMatchParams() {
        return createLayoutParams(-2, -1);
    }

    public static final FrameLayout.LayoutParams createMatchWrapParams() {
        return createLayoutParams(-1, -2);
    }
}
