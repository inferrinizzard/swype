package android.support.v7.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.nuance.connect.common.Integers;

/* loaded from: classes.dex */
public abstract class OrientationHelper {
    int mLastTotalSpace;
    protected final RecyclerView.LayoutManager mLayoutManager;
    final Rect mTmpRect;

    public abstract int getDecoratedEnd(View view);

    public abstract int getDecoratedMeasurement(View view);

    public abstract int getDecoratedMeasurementInOther(View view);

    public abstract int getDecoratedStart(View view);

    public abstract int getEnd();

    public abstract int getEndAfterPadding();

    public abstract int getEndPadding();

    public abstract int getMode();

    public abstract int getModeInOther();

    public abstract int getStartAfterPadding();

    public abstract int getTotalSpace();

    public abstract int getTransformedEndWithDecoration(View view);

    public abstract int getTransformedStartWithDecoration(View view);

    public abstract void offsetChildren(int i);

    /* synthetic */ OrientationHelper(RecyclerView.LayoutManager x0, byte b) {
        this(x0);
    }

    private OrientationHelper(RecyclerView.LayoutManager layoutManager) {
        this.mLastTotalSpace = Integers.STATUS_SUCCESS;
        this.mTmpRect = new Rect();
        this.mLayoutManager = layoutManager;
    }

    public final int getTotalSpaceChange() {
        if (Integer.MIN_VALUE == this.mLastTotalSpace) {
            return 0;
        }
        return getTotalSpace() - this.mLastTotalSpace;
    }

    public static OrientationHelper createOrientationHelper(RecyclerView.LayoutManager layoutManager, int orientation) {
        switch (orientation) {
            case 0:
                return new OrientationHelper(layoutManager) { // from class: android.support.v7.widget.OrientationHelper.1
                    {
                        byte b = 0;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getEndAfterPadding() {
                        return this.mLayoutManager.mWidth - this.mLayoutManager.getPaddingRight();
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getEnd() {
                        return this.mLayoutManager.mWidth;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final void offsetChildren(int amount) {
                        this.mLayoutManager.offsetChildrenHorizontal(amount);
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getStartAfterPadding() {
                        return this.mLayoutManager.getPaddingLeft();
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getDecoratedMeasurement(View view) {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                        return RecyclerView.LayoutManager.getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getDecoratedMeasurementInOther(View view) {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                        return RecyclerView.LayoutManager.getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getDecoratedEnd(View view) {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                        return RecyclerView.LayoutManager.getDecoratedRight(view) + params.rightMargin;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getDecoratedStart(View view) {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                        return RecyclerView.LayoutManager.getDecoratedLeft(view) - params.leftMargin;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getTransformedEndWithDecoration(View view) {
                        this.mLayoutManager.getTransformedBoundingBox$38b72182(view, this.mTmpRect);
                        return this.mTmpRect.right;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getTransformedStartWithDecoration(View view) {
                        this.mLayoutManager.getTransformedBoundingBox$38b72182(view, this.mTmpRect);
                        return this.mTmpRect.left;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getTotalSpace() {
                        return (this.mLayoutManager.mWidth - this.mLayoutManager.getPaddingLeft()) - this.mLayoutManager.getPaddingRight();
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getEndPadding() {
                        return this.mLayoutManager.getPaddingRight();
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getMode() {
                        return this.mLayoutManager.mWidthMode;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getModeInOther() {
                        return this.mLayoutManager.mHeightMode;
                    }
                };
            case 1:
                return new OrientationHelper(layoutManager) { // from class: android.support.v7.widget.OrientationHelper.2
                    {
                        byte b = 0;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getEndAfterPadding() {
                        return this.mLayoutManager.mHeight - this.mLayoutManager.getPaddingBottom();
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getEnd() {
                        return this.mLayoutManager.mHeight;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final void offsetChildren(int amount) {
                        this.mLayoutManager.offsetChildrenVertical(amount);
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getStartAfterPadding() {
                        return this.mLayoutManager.getPaddingTop();
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getDecoratedMeasurement(View view) {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                        return RecyclerView.LayoutManager.getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getDecoratedMeasurementInOther(View view) {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                        return RecyclerView.LayoutManager.getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getDecoratedEnd(View view) {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                        return RecyclerView.LayoutManager.getDecoratedBottom(view) + params.bottomMargin;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getDecoratedStart(View view) {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                        return RecyclerView.LayoutManager.getDecoratedTop(view) - params.topMargin;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getTransformedEndWithDecoration(View view) {
                        this.mLayoutManager.getTransformedBoundingBox$38b72182(view, this.mTmpRect);
                        return this.mTmpRect.bottom;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getTransformedStartWithDecoration(View view) {
                        this.mLayoutManager.getTransformedBoundingBox$38b72182(view, this.mTmpRect);
                        return this.mTmpRect.top;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getTotalSpace() {
                        return (this.mLayoutManager.mHeight - this.mLayoutManager.getPaddingTop()) - this.mLayoutManager.getPaddingBottom();
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getEndPadding() {
                        return this.mLayoutManager.getPaddingBottom();
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getMode() {
                        return this.mLayoutManager.mHeightMode;
                    }

                    @Override // android.support.v7.widget.OrientationHelper
                    public final int getModeInOther() {
                        return this.mLayoutManager.mWidthMode;
                    }
                };
            default:
                throw new IllegalArgumentException("invalid orientation");
        }
    }
}
