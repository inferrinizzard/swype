package android.support.v7.widget;

import android.os.SystemClock;
import android.support.v7.view.menu.ShowableListMenu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/* loaded from: classes.dex */
public abstract class ForwardingListener implements View.OnTouchListener {
    private int mActivePointerId;
    private Runnable mDisallowIntercept;
    private boolean mForwarding;
    private final float mScaledTouchSlop;
    private final View mSrc;
    private Runnable mTriggerLongPress;
    private final int[] mTmpLocation = new int[2];
    private final int mTapTimeout = ViewConfiguration.getTapTimeout();
    private final int mLongPressTimeout = (this.mTapTimeout + ViewConfiguration.getLongPressTimeout()) / 2;

    public abstract ShowableListMenu getPopup();

    public ForwardingListener(View src) {
        this.mSrc = src;
        this.mScaledTouchSlop = ViewConfiguration.get(src.getContext()).getScaledTouchSlop();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0093  */
    @Override // android.view.View.OnTouchListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouch(android.view.View r14, android.view.MotionEvent r15) {
        /*
            Method dump skipped, instructions count: 308
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.ForwardingListener.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }

    public boolean onForwardingStarted() {
        ShowableListMenu popup = getPopup();
        if (popup != null && !popup.isShowing()) {
            popup.show();
            return true;
        }
        return true;
    }

    protected boolean onForwardingStopped() {
        ShowableListMenu popup = getPopup();
        if (popup != null && popup.isShowing()) {
            popup.dismiss();
            return true;
        }
        return true;
    }

    private void clearCallbacks() {
        if (this.mTriggerLongPress != null) {
            this.mSrc.removeCallbacks(this.mTriggerLongPress);
        }
        if (this.mDisallowIntercept != null) {
            this.mSrc.removeCallbacks(this.mDisallowIntercept);
        }
    }

    /* loaded from: classes.dex */
    private class DisallowIntercept implements Runnable {
        private DisallowIntercept() {
        }

        /* synthetic */ DisallowIntercept(ForwardingListener x0, byte b) {
            this();
        }

        @Override // java.lang.Runnable
        public final void run() {
            ForwardingListener.this.mSrc.getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    /* loaded from: classes.dex */
    private class TriggerLongPress implements Runnable {
        private TriggerLongPress() {
        }

        /* synthetic */ TriggerLongPress(ForwardingListener x0, byte b) {
            this();
        }

        @Override // java.lang.Runnable
        public final void run() {
            ForwardingListener.access$300(ForwardingListener.this);
        }
    }

    static /* synthetic */ void access$300(ForwardingListener x0) {
        x0.clearCallbacks();
        View view = x0.mSrc;
        if (!view.isEnabled() || view.isLongClickable() || !x0.onForwardingStarted()) {
            return;
        }
        view.getParent().requestDisallowInterceptTouchEvent(true);
        long uptimeMillis = SystemClock.uptimeMillis();
        MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
        view.onTouchEvent(obtain);
        obtain.recycle();
        x0.mForwarding = true;
    }
}
