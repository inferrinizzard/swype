package com.nuance.swype.input;

import android.view.View;

/* loaded from: classes.dex */
public class DragGesture implements View.OnTouchListener {
    private int lastTouchX;
    private int lastTouchY;
    private final IDragGestureListener listener;

    /* loaded from: classes.dex */
    public interface IDragGestureListener {
        void onBeginDrag(View view);

        void onDrag(View view, int i, int i2);

        void onEndDrag(View view);
    }

    public DragGesture(IDragGestureListener listener) {
        this.listener = listener;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:3:0x0012, code lost:            return true;     */
    @Override // android.view.View.OnTouchListener
    @android.annotation.SuppressLint({"ClickableViewAccessibility"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouch(android.view.View r8, android.view.MotionEvent r9) {
        /*
            r7 = this;
            r6 = 1
            int r0 = r9.getAction()
            float r3 = r9.getRawX()
            int r1 = (int) r3
            float r3 = r9.getRawY()
            int r2 = (int) r3
            switch(r0) {
                case 0: goto L13;
                case 1: goto L3a;
                case 2: goto L24;
                default: goto L12;
            }
        L12:
            return r6
        L13:
            r7.lastTouchX = r1
            r7.lastTouchY = r2
            com.nuance.swype.input.DragGesture$IDragGestureListener r3 = r7.listener
            if (r3 == 0) goto L20
            com.nuance.swype.input.DragGesture$IDragGestureListener r3 = r7.listener
            r3.onBeginDrag(r8)
        L20:
            r8.setPressed(r6)
            goto L12
        L24:
            com.nuance.swype.input.DragGesture$IDragGestureListener r3 = r7.listener
            if (r3 == 0) goto L35
            com.nuance.swype.input.DragGesture$IDragGestureListener r3 = r7.listener
            int r4 = r7.lastTouchX
            int r4 = r1 - r4
            int r5 = r7.lastTouchY
            int r5 = r2 - r5
            r3.onDrag(r8, r4, r5)
        L35:
            r7.lastTouchX = r1
            r7.lastTouchY = r2
            goto L12
        L3a:
            r7.lastTouchX = r1
            r7.lastTouchY = r2
            com.nuance.swype.input.DragGesture$IDragGestureListener r3 = r7.listener
            if (r3 == 0) goto L47
            com.nuance.swype.input.DragGesture$IDragGestureListener r3 = r7.listener
            r3.onEndDrag(r8)
        L47:
            r3 = 0
            r8.setPressed(r3)
            goto L12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.DragGesture.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }
}
