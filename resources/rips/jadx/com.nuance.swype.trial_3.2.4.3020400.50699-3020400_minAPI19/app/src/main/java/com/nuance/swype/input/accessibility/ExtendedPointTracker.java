package com.nuance.swype.input.accessibility;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import com.nuance.android.util.WindowCallbackWrapper;

/* loaded from: classes.dex */
public class ExtendedPointTracker extends WindowCallbackWrapper {
    private final Window.Callback baseCallback;
    private final int[] outLocation = new int[2];
    Point extendedPoint = new Point();

    public ExtendedPointTracker(Window.Callback baseCallback) {
        this.baseCallback = baseCallback;
        setTarget(baseCallback);
    }

    public Point getExtendedPoint() {
        return this.extendedPoint;
    }

    public MotionEvent getExtendedEventForView(MotionEvent event, View view) {
        MotionEvent extendedEvent = MotionEvent.obtainNoHistory(event);
        view.getLocationInWindow(this.outLocation);
        extendedEvent.setLocation(this.extendedPoint.x - this.outLocation[0], this.extendedPoint.y - this.outLocation[1]);
        return extendedEvent;
    }

    @Override // com.nuance.android.util.WindowCallbackWrapper, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent event) {
        this.extendedPoint.x = (int) event.getX();
        this.extendedPoint.y = (int) event.getY();
        try {
            return this.baseCallback.dispatchTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
