package android.support.v7.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/* loaded from: classes.dex */
public abstract class LinearSmoothScroller extends RecyclerView.SmoothScroller {
    private final float MILLISECONDS_PER_PX;
    protected PointF mTargetVector;
    protected final LinearInterpolator mLinearInterpolator = new LinearInterpolator();
    protected final DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator();
    protected int mInterimTargetDx = 0;
    protected int mInterimTargetDy = 0;

    public abstract PointF computeScrollVectorForPosition(int i);

    public LinearSmoothScroller(Context context) {
        this.MILLISECONDS_PER_PX = 25.0f / context.getResources().getDisplayMetrics().densityDpi;
    }

    @Override // android.support.v7.widget.RecyclerView.SmoothScroller
    protected final void onStop() {
        this.mInterimTargetDy = 0;
        this.mInterimTargetDx = 0;
        this.mTargetVector = null;
    }

    private int calculateTimeForScrolling(int dx) {
        return (int) Math.ceil(Math.abs(dx) * this.MILLISECONDS_PER_PX);
    }

    private static int clampApplyScroll(int tmpDt, int dt) {
        int tmpDt2 = tmpDt - dt;
        if (tmpDt * tmpDt2 <= 0) {
            return 0;
        }
        return tmpDt2;
    }

    private static int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
        switch (snapPreference) {
            case -1:
                return boxStart - viewStart;
            case 0:
                int dtStart = boxStart - viewStart;
                if (dtStart <= 0) {
                    int dtEnd = boxEnd - viewEnd;
                    if (dtEnd < 0) {
                        return dtEnd;
                    }
                    return 0;
                }
                return dtStart;
            case 1:
                return boxEnd - viewEnd;
            default:
                throw new IllegalArgumentException("snap preference should be one of the constants defined in SmoothScroller, starting with SNAP_");
        }
    }

    @Override // android.support.v7.widget.RecyclerView.SmoothScroller
    protected final void onTargetFound$68abd3fe(View targetView, RecyclerView.SmoothScroller.Action action) {
        int dx;
        int dy;
        int i = (this.mTargetVector == null || this.mTargetVector.x == 0.0f) ? 0 : this.mTargetVector.x > 0.0f ? 1 : -1;
        RecyclerView.LayoutManager layoutManager = this.mLayoutManager;
        if (layoutManager == null || !layoutManager.canScrollHorizontally()) {
            dx = 0;
        } else {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) targetView.getLayoutParams();
            dx = calculateDtToFit(RecyclerView.LayoutManager.getDecoratedLeft(targetView) - layoutParams.leftMargin, layoutParams.rightMargin + RecyclerView.LayoutManager.getDecoratedRight(targetView), layoutManager.getPaddingLeft(), layoutManager.mWidth - layoutManager.getPaddingRight(), i);
        }
        int i2 = (this.mTargetVector == null || this.mTargetVector.y == 0.0f) ? 0 : this.mTargetVector.y > 0.0f ? 1 : -1;
        RecyclerView.LayoutManager layoutManager2 = this.mLayoutManager;
        if (layoutManager2 == null || !layoutManager2.canScrollVertically()) {
            dy = 0;
        } else {
            RecyclerView.LayoutParams layoutParams2 = (RecyclerView.LayoutParams) targetView.getLayoutParams();
            dy = calculateDtToFit(RecyclerView.LayoutManager.getDecoratedTop(targetView) - layoutParams2.topMargin, layoutParams2.bottomMargin + RecyclerView.LayoutManager.getDecoratedBottom(targetView), layoutManager2.getPaddingTop(), layoutManager2.mHeight - layoutManager2.getPaddingBottom(), i2);
        }
        int distance = (int) Math.sqrt((dx * dx) + (dy * dy));
        int time = (int) Math.ceil(calculateTimeForScrolling(distance) / 0.3356d);
        if (time > 0) {
            action.update(-dx, -dy, time, this.mDecelerateInterpolator);
        }
    }

    @Override // android.support.v7.widget.RecyclerView.SmoothScroller
    protected final void onSeekTargetStep$64702b56(int dx, int dy, RecyclerView.SmoothScroller.Action action) {
        if (this.mRecyclerView.mLayout.getChildCount() == 0) {
            stop();
            return;
        }
        this.mInterimTargetDx = clampApplyScroll(this.mInterimTargetDx, dx);
        this.mInterimTargetDy = clampApplyScroll(this.mInterimTargetDy, dy);
        if (this.mInterimTargetDx == 0 && this.mInterimTargetDy == 0) {
            PointF computeScrollVectorForPosition = computeScrollVectorForPosition(this.mTargetPosition);
            if (computeScrollVectorForPosition == null || (computeScrollVectorForPosition.x == 0.0f && computeScrollVectorForPosition.y == 0.0f)) {
                Log.e("LinearSmoothScroller", "To support smooth scrolling, you should override \nLayoutManager#computeScrollVectorForPosition.\nFalling back to instant scroll");
                action.mJumpToPosition = this.mTargetPosition;
                stop();
                return;
            }
            double sqrt = Math.sqrt((computeScrollVectorForPosition.x * computeScrollVectorForPosition.x) + (computeScrollVectorForPosition.y * computeScrollVectorForPosition.y));
            computeScrollVectorForPosition.x = (float) (computeScrollVectorForPosition.x / sqrt);
            computeScrollVectorForPosition.y = (float) (computeScrollVectorForPosition.y / sqrt);
            this.mTargetVector = computeScrollVectorForPosition;
            this.mInterimTargetDx = (int) (computeScrollVectorForPosition.x * 10000.0f);
            this.mInterimTargetDy = (int) (computeScrollVectorForPosition.y * 10000.0f);
            action.update((int) (this.mInterimTargetDx * 1.2f), (int) (this.mInterimTargetDy * 1.2f), (int) (calculateTimeForScrolling(10000) * 1.2f), this.mLinearInterpolator);
        }
    }
}
