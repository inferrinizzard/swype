package android.support.v4.view;

import android.view.View;
import android.view.ViewParent;

/* loaded from: classes.dex */
public final class NestedScrollingChildHelper {
    public boolean mIsNestedScrollingEnabled;
    private ViewParent mNestedScrollingParent;
    private int[] mTempNestedScrollConsumed;
    private final View mView;

    public NestedScrollingChildHelper(View view) {
        this.mView = view;
    }

    public final void setNestedScrollingEnabled(boolean enabled) {
        if (this.mIsNestedScrollingEnabled) {
            ViewCompat.stopNestedScroll(this.mView);
        }
        this.mIsNestedScrollingEnabled = enabled;
    }

    public final boolean hasNestedScrollingParent() {
        return this.mNestedScrollingParent != null;
    }

    public final boolean startNestedScroll(int axes) {
        if (hasNestedScrollingParent()) {
            return true;
        }
        if (this.mIsNestedScrollingEnabled) {
            View child = this.mView;
            for (ViewParent p = this.mView.getParent(); p != null; p = p.getParent()) {
                if (ViewParentCompat.onStartNestedScroll(p, child, this.mView, axes)) {
                    this.mNestedScrollingParent = p;
                    ViewParentCompat.onNestedScrollAccepted(p, child, this.mView, axes);
                    return true;
                }
                if (p instanceof View) {
                    child = (View) p;
                }
            }
        }
        return false;
    }

    public final void stopNestedScroll() {
        if (this.mNestedScrollingParent != null) {
            ViewParentCompat.onStopNestedScroll(this.mNestedScrollingParent, this.mView);
            this.mNestedScrollingParent = null;
        }
    }

    public final boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        if (this.mIsNestedScrollingEnabled && this.mNestedScrollingParent != null) {
            if (dxConsumed != 0 || dyConsumed != 0 || dxUnconsumed != 0 || dyUnconsumed != 0) {
                int startX = 0;
                int startY = 0;
                if (offsetInWindow != null) {
                    this.mView.getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }
                ViewParentCompat.onNestedScroll(this.mNestedScrollingParent, this.mView, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
                if (offsetInWindow != null) {
                    this.mView.getLocationInWindow(offsetInWindow);
                    offsetInWindow[0] = offsetInWindow[0] - startX;
                    offsetInWindow[1] = offsetInWindow[1] - startY;
                }
                return true;
            }
            if (offsetInWindow != null) {
                offsetInWindow[0] = 0;
                offsetInWindow[1] = 0;
            }
        }
        return false;
    }

    public final boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        if (!this.mIsNestedScrollingEnabled || this.mNestedScrollingParent == null) {
            return false;
        }
        if (dx != 0 || dy != 0) {
            int startX = 0;
            int startY = 0;
            if (offsetInWindow != null) {
                this.mView.getLocationInWindow(offsetInWindow);
                startX = offsetInWindow[0];
                startY = offsetInWindow[1];
            }
            if (consumed == null) {
                if (this.mTempNestedScrollConsumed == null) {
                    this.mTempNestedScrollConsumed = new int[2];
                }
                consumed = this.mTempNestedScrollConsumed;
            }
            consumed[0] = 0;
            consumed[1] = 0;
            ViewParentCompat.onNestedPreScroll(this.mNestedScrollingParent, this.mView, dx, dy, consumed);
            if (offsetInWindow != null) {
                this.mView.getLocationInWindow(offsetInWindow);
                offsetInWindow[0] = offsetInWindow[0] - startX;
                offsetInWindow[1] = offsetInWindow[1] - startY;
            }
            return (consumed[0] == 0 && consumed[1] == 0) ? false : true;
        }
        if (offsetInWindow == null) {
            return false;
        }
        offsetInWindow[0] = 0;
        offsetInWindow[1] = 0;
        return false;
    }

    public final boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        if (!this.mIsNestedScrollingEnabled || this.mNestedScrollingParent == null) {
            return false;
        }
        return ViewParentCompat.onNestedFling(this.mNestedScrollingParent, this.mView, velocityX, velocityY, consumed);
    }

    public final boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        if (!this.mIsNestedScrollingEnabled || this.mNestedScrollingParent == null) {
            return false;
        }
        return ViewParentCompat.onNestedPreFling(this.mNestedScrollingParent, this.mView, velocityX, velocityY);
    }
}
