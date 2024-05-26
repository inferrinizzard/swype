package com.nuance.swype.input;

import android.view.MotionEvent;

/* loaded from: classes.dex */
public class MotionEventWrapper {
    public static final int ACTION_MASK = 255;
    public static final int ACTION_POINTER_DOWN = 5;
    public static final int ACTION_POINTER_INDEX_MASK = 65280;
    public static final int ACTION_POINTER_INDEX_SHIFT = 8;
    public static final int ACTION_POINTER_UP = 6;

    public static int getPointerCount(MotionEvent me) {
        return me.getPointerCount();
    }

    public static int getPointerId(MotionEvent me, int pointerIndex) {
        return me.getPointerId(pointerIndex);
    }

    public static int findPointerIndex(MotionEvent me, int pointerId) {
        return me.findPointerIndex(pointerId);
    }

    public static float getX(MotionEvent me, int pointerIndex) {
        return me.getX(pointerIndex);
    }

    public static float getY(MotionEvent me, int pointerIndex) {
        return me.getY(pointerIndex);
    }

    public static float getHistoricalX(MotionEvent me, int pointerIndex, int pos) {
        return me.getHistoricalX(pointerIndex, pos);
    }

    public static float getHistoricalY(MotionEvent me, int pointerIndex, int pos) {
        return me.getHistoricalY(pointerIndex, pos);
    }

    public static float getRawX(MotionEvent me) {
        return me.getRawX();
    }

    public static float getRawY(MotionEvent me) {
        return me.getRawY();
    }
}
