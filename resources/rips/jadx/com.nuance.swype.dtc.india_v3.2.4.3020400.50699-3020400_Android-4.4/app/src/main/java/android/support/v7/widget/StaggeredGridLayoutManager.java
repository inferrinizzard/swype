package android.support.v7.widget;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import com.nuance.connect.common.Integers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/* loaded from: classes.dex */
public class StaggeredGridLayoutManager extends RecyclerView.LayoutManager {
    private int mFullSizeSpec;
    private boolean mLastLayoutFromEnd;
    private boolean mLastLayoutRTL;
    private final LayoutState mLayoutState;
    private int mOrientation;
    private SavedState mPendingSavedState;
    OrientationHelper mPrimaryOrientation;
    private BitSet mRemainingSpans;
    OrientationHelper mSecondaryOrientation;
    private int mSizePerSpan;
    private Span[] mSpans;
    private int mSpanCount = -1;
    private boolean mReverseLayout = false;
    boolean mShouldReverseLayout = false;
    int mPendingScrollPosition = -1;
    int mPendingScrollPositionOffset = Integers.STATUS_SUCCESS;
    LazySpanLookup mLazySpanLookup = new LazySpanLookup();
    private int mGapStrategy = 2;
    private final Rect mTmpRect = new Rect();
    private final AnchorInfo mAnchorInfo = new AnchorInfo();
    private boolean mLaidOutInvalidFullSpan = false;
    private boolean mSmoothScrollbarEnabled = true;
    private final Runnable mCheckForGapsRunnable = new Runnable() { // from class: android.support.v7.widget.StaggeredGridLayoutManager.1
        @Override // java.lang.Runnable
        public final void run() {
            StaggeredGridLayoutManager.this.checkForGaps();
        }
    };

    public StaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        RecyclerView.LayoutManager.Properties properties = getProperties(context, attrs, defStyleAttr, defStyleRes);
        int i = properties.orientation;
        if (i != 0 && i != 1) {
            throw new IllegalArgumentException("invalid orientation.");
        }
        assertNotInLayoutOrScroll(null);
        if (i != this.mOrientation) {
            this.mOrientation = i;
            OrientationHelper orientationHelper = this.mPrimaryOrientation;
            this.mPrimaryOrientation = this.mSecondaryOrientation;
            this.mSecondaryOrientation = orientationHelper;
            requestLayout();
        }
        setSpanCount(properties.spanCount);
        setReverseLayout(properties.reverseLayout);
        this.mAutoMeasure = this.mGapStrategy != 0;
        this.mLayoutState = new LayoutState();
        createOrientationHelpers();
    }

    public StaggeredGridLayoutManager(int spanCount, int orientation) {
        this.mOrientation = orientation;
        setSpanCount(spanCount);
        this.mAutoMeasure = this.mGapStrategy != 0;
        this.mLayoutState = new LayoutState();
        createOrientationHelpers();
    }

    private void createOrientationHelpers() {
        this.mPrimaryOrientation = OrientationHelper.createOrientationHelper(this, this.mOrientation);
        this.mSecondaryOrientation = OrientationHelper.createOrientationHelper(this, 1 - this.mOrientation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkForGaps() {
        int minPos;
        int maxPos;
        if (getChildCount() == 0 || this.mGapStrategy == 0 || !this.mIsAttachedToWindow) {
            return false;
        }
        if (this.mShouldReverseLayout) {
            minPos = getLastChildPosition();
            maxPos = getFirstChildPosition();
        } else {
            minPos = getFirstChildPosition();
            maxPos = getLastChildPosition();
        }
        if (minPos == 0 && hasGapsToFix() != null) {
            this.mLazySpanLookup.clear();
            this.mRequestedSimpleAnimations = true;
            requestLayout();
            return true;
        }
        if (!this.mLaidOutInvalidFullSpan) {
            return false;
        }
        int invalidGapDir = this.mShouldReverseLayout ? -1 : 1;
        LazySpanLookup.FullSpanItem invalidFsi = this.mLazySpanLookup.getFirstFullSpanItemInRange$7b524a3(minPos, maxPos + 1, invalidGapDir);
        if (invalidFsi == null) {
            this.mLaidOutInvalidFullSpan = false;
            this.mLazySpanLookup.forceInvalidateAfter(maxPos + 1);
            return false;
        }
        LazySpanLookup.FullSpanItem validFsi = this.mLazySpanLookup.getFirstFullSpanItemInRange$7b524a3(minPos, invalidFsi.mPosition, invalidGapDir * (-1));
        if (validFsi == null) {
            this.mLazySpanLookup.forceInvalidateAfter(invalidFsi.mPosition);
        } else {
            this.mLazySpanLookup.forceInvalidateAfter(validFsi.mPosition + 1);
        }
        this.mRequestedSimpleAnimations = true;
        requestLayout();
        return true;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onScrollStateChanged(int state) {
        if (state == 0) {
            checkForGaps();
        }
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        removeCallbacks(this.mCheckForGapsRunnable);
        for (int i = 0; i < this.mSpanCount; i++) {
            this.mSpans[i].clear();
        }
        view.requestLayout();
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x00fe  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x00b8 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private android.view.View hasGapsToFix() {
        /*
            Method dump skipped, instructions count: 425
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.StaggeredGridLayoutManager.hasGapsToFix():android.view.View");
    }

    private void setSpanCount(int spanCount) {
        assertNotInLayoutOrScroll(null);
        if (spanCount == this.mSpanCount) {
            return;
        }
        this.mLazySpanLookup.clear();
        requestLayout();
        this.mSpanCount = spanCount;
        this.mRemainingSpans = new BitSet(this.mSpanCount);
        this.mSpans = new Span[this.mSpanCount];
        for (int i = 0; i < this.mSpanCount; i++) {
            this.mSpans[i] = new Span(this, i, (byte) 0);
        }
        requestLayout();
    }

    private void setReverseLayout(boolean reverseLayout) {
        assertNotInLayoutOrScroll(null);
        if (this.mPendingSavedState != null && this.mPendingSavedState.mReverseLayout != reverseLayout) {
            this.mPendingSavedState.mReverseLayout = reverseLayout;
        }
        this.mReverseLayout = reverseLayout;
        requestLayout();
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void assertNotInLayoutOrScroll(String message) {
        if (this.mPendingSavedState == null) {
            super.assertNotInLayoutOrScroll(message);
        }
    }

    private void resolveShouldLayoutReverse() {
        boolean z = true;
        if (this.mOrientation == 1 || !isLayoutRTL()) {
            z = this.mReverseLayout;
        } else if (this.mReverseLayout) {
            z = false;
        }
        this.mShouldReverseLayout = z;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
        int width;
        int height;
        int horizontalPadding = getPaddingLeft() + getPaddingRight();
        int verticalPadding = getPaddingTop() + getPaddingBottom();
        if (this.mOrientation == 1) {
            int usedHeight = childrenBounds.height() + verticalPadding;
            height = chooseSize(hSpec, usedHeight, ViewCompat.getMinimumHeight(this.mRecyclerView));
            width = chooseSize(wSpec, (this.mSizePerSpan * this.mSpanCount) + horizontalPadding, ViewCompat.getMinimumWidth(this.mRecyclerView));
        } else {
            int usedWidth = childrenBounds.width() + horizontalPadding;
            width = chooseSize(wSpec, usedWidth, ViewCompat.getMinimumWidth(this.mRecyclerView));
            height = chooseSize(hSpec, (this.mSizePerSpan * this.mSpanCount) + verticalPadding, ViewCompat.getMinimumHeight(this.mRecyclerView));
        }
        setMeasuredDimension(width, height);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        boolean z;
        int i;
        int startAfterPadding;
        float f;
        int startLine;
        boolean z2 = true;
        while (true) {
            boolean z3 = z2;
            AnchorInfo anchorInfo = this.mAnchorInfo;
            if ((this.mPendingSavedState != null || this.mPendingScrollPosition != -1) && state.getItemCount() == 0) {
                removeAndRecycleAllViews(recycler);
                anchorInfo.reset();
                return;
            }
            if (!anchorInfo.mValid || this.mPendingScrollPosition != -1 || this.mPendingSavedState != null) {
                anchorInfo.reset();
                if (this.mPendingSavedState == null) {
                    resolveShouldLayoutReverse();
                    anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
                } else {
                    if (this.mPendingSavedState.mSpanOffsetsSize > 0) {
                        if (this.mPendingSavedState.mSpanOffsetsSize == this.mSpanCount) {
                            for (int i2 = 0; i2 < this.mSpanCount; i2++) {
                                this.mSpans[i2].clear();
                                int i3 = this.mPendingSavedState.mSpanOffsets[i2];
                                if (i3 != Integer.MIN_VALUE) {
                                    if (this.mPendingSavedState.mAnchorLayoutFromEnd) {
                                        i3 += this.mPrimaryOrientation.getEndAfterPadding();
                                    } else {
                                        i3 += this.mPrimaryOrientation.getStartAfterPadding();
                                    }
                                }
                                this.mSpans[i2].setLine(i3);
                            }
                        } else {
                            SavedState savedState = this.mPendingSavedState;
                            savedState.mSpanOffsets = null;
                            savedState.mSpanOffsetsSize = 0;
                            savedState.mSpanLookupSize = 0;
                            savedState.mSpanLookup = null;
                            savedState.mFullSpanItems = null;
                            this.mPendingSavedState.mAnchorPosition = this.mPendingSavedState.mVisibleAnchorPosition;
                        }
                    }
                    this.mLastLayoutRTL = this.mPendingSavedState.mLastLayoutRTL;
                    setReverseLayout(this.mPendingSavedState.mReverseLayout);
                    resolveShouldLayoutReverse();
                    if (this.mPendingSavedState.mAnchorPosition != -1) {
                        this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
                        anchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;
                    } else {
                        anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
                    }
                    if (this.mPendingSavedState.mSpanLookupSize > 1) {
                        this.mLazySpanLookup.mData = this.mPendingSavedState.mSpanLookup;
                        this.mLazySpanLookup.mFullSpanItems = this.mPendingSavedState.mFullSpanItems;
                    }
                }
                if (state.mInPreLayout || this.mPendingScrollPosition == -1) {
                    z = false;
                } else if (this.mPendingScrollPosition < 0 || this.mPendingScrollPosition >= state.getItemCount()) {
                    this.mPendingScrollPosition = -1;
                    this.mPendingScrollPositionOffset = Integers.STATUS_SUCCESS;
                    z = false;
                } else {
                    if (this.mPendingSavedState == null || this.mPendingSavedState.mAnchorPosition == -1 || this.mPendingSavedState.mSpanOffsetsSize <= 0) {
                        View findViewByPosition = findViewByPosition(this.mPendingScrollPosition);
                        if (findViewByPosition != null) {
                            anchorInfo.mPosition = this.mShouldReverseLayout ? getLastChildPosition() : getFirstChildPosition();
                            if (this.mPendingScrollPositionOffset != Integer.MIN_VALUE) {
                                if (anchorInfo.mLayoutFromEnd) {
                                    anchorInfo.mOffset = (this.mPrimaryOrientation.getEndAfterPadding() - this.mPendingScrollPositionOffset) - this.mPrimaryOrientation.getDecoratedEnd(findViewByPosition);
                                } else {
                                    anchorInfo.mOffset = (this.mPrimaryOrientation.getStartAfterPadding() + this.mPendingScrollPositionOffset) - this.mPrimaryOrientation.getDecoratedStart(findViewByPosition);
                                }
                                z = true;
                            } else if (this.mPrimaryOrientation.getDecoratedMeasurement(findViewByPosition) > this.mPrimaryOrientation.getTotalSpace()) {
                                if (anchorInfo.mLayoutFromEnd) {
                                    startAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
                                } else {
                                    startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
                                }
                                anchorInfo.mOffset = startAfterPadding;
                            } else {
                                int decoratedStart = this.mPrimaryOrientation.getDecoratedStart(findViewByPosition) - this.mPrimaryOrientation.getStartAfterPadding();
                                if (decoratedStart < 0) {
                                    anchorInfo.mOffset = -decoratedStart;
                                } else {
                                    int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding() - this.mPrimaryOrientation.getDecoratedEnd(findViewByPosition);
                                    if (endAfterPadding < 0) {
                                        anchorInfo.mOffset = endAfterPadding;
                                    } else {
                                        anchorInfo.mOffset = Integers.STATUS_SUCCESS;
                                    }
                                }
                            }
                        } else {
                            anchorInfo.mPosition = this.mPendingScrollPosition;
                            if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE) {
                                anchorInfo.mLayoutFromEnd = calculateScrollDirectionForPosition(anchorInfo.mPosition) == 1;
                                anchorInfo.mOffset = anchorInfo.mLayoutFromEnd ? StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding() : StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
                            } else {
                                int i4 = this.mPendingScrollPositionOffset;
                                if (anchorInfo.mLayoutFromEnd) {
                                    anchorInfo.mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding() - i4;
                                } else {
                                    anchorInfo.mOffset = i4 + StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
                                }
                            }
                            anchorInfo.mInvalidateOffsets = true;
                        }
                    } else {
                        anchorInfo.mOffset = Integers.STATUS_SUCCESS;
                        anchorInfo.mPosition = this.mPendingScrollPosition;
                    }
                    z = true;
                }
                if (!z) {
                    if (this.mLastLayoutFromEnd) {
                        int itemCount = state.getItemCount();
                        int childCount = getChildCount() - 1;
                        while (true) {
                            if (childCount >= 0) {
                                i = getPosition(getChildAt(childCount));
                                if (i >= 0 && i < itemCount) {
                                    break;
                                } else {
                                    childCount--;
                                }
                            } else {
                                i = 0;
                                break;
                            }
                        }
                    } else {
                        int itemCount2 = state.getItemCount();
                        int childCount2 = getChildCount();
                        int i5 = 0;
                        while (true) {
                            if (i5 < childCount2) {
                                i = getPosition(getChildAt(i5));
                                if (i >= 0 && i < itemCount2) {
                                    break;
                                } else {
                                    i5++;
                                }
                            } else {
                                i = 0;
                                break;
                            }
                        }
                    }
                    anchorInfo.mPosition = i;
                    anchorInfo.mOffset = Integers.STATUS_SUCCESS;
                }
                anchorInfo.mValid = true;
            }
            if (this.mPendingSavedState == null && this.mPendingScrollPosition == -1 && (anchorInfo.mLayoutFromEnd != this.mLastLayoutFromEnd || isLayoutRTL() != this.mLastLayoutRTL)) {
                this.mLazySpanLookup.clear();
                anchorInfo.mInvalidateOffsets = true;
            }
            if (getChildCount() > 0 && (this.mPendingSavedState == null || this.mPendingSavedState.mSpanOffsetsSize <= 0)) {
                if (anchorInfo.mInvalidateOffsets) {
                    for (int i6 = 0; i6 < this.mSpanCount; i6++) {
                        this.mSpans[i6].clear();
                        if (anchorInfo.mOffset != Integer.MIN_VALUE) {
                            this.mSpans[i6].setLine(anchorInfo.mOffset);
                        }
                    }
                } else {
                    for (int i7 = 0; i7 < this.mSpanCount; i7++) {
                        Span span = this.mSpans[i7];
                        boolean z4 = this.mShouldReverseLayout;
                        int i8 = anchorInfo.mOffset;
                        if (z4) {
                            startLine = span.getEndLine(Integers.STATUS_SUCCESS);
                        } else {
                            startLine = span.getStartLine(Integers.STATUS_SUCCESS);
                        }
                        span.clear();
                        if (startLine != Integer.MIN_VALUE && ((!z4 || startLine >= StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding()) && (z4 || startLine <= StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding()))) {
                            if (i8 != Integer.MIN_VALUE) {
                                startLine += i8;
                            }
                            span.mCachedEnd = startLine;
                            span.mCachedStart = startLine;
                        }
                    }
                }
            }
            detachAndScrapAttachedViews(recycler);
            this.mLayoutState.mRecycle = false;
            this.mLaidOutInvalidFullSpan = false;
            updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
            updateLayoutState(anchorInfo.mPosition, state);
            if (anchorInfo.mLayoutFromEnd) {
                setLayoutStateDirection(-1);
                fill(recycler, this.mLayoutState, state);
                setLayoutStateDirection(1);
                this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                fill(recycler, this.mLayoutState, state);
            } else {
                setLayoutStateDirection(1);
                fill(recycler, this.mLayoutState, state);
                setLayoutStateDirection(-1);
                this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                fill(recycler, this.mLayoutState, state);
            }
            if (this.mSecondaryOrientation.getMode() != 1073741824) {
                float f2 = 0.0f;
                int childCount3 = getChildCount();
                int i9 = 0;
                while (i9 < childCount3) {
                    View childAt = getChildAt(i9);
                    float decoratedMeasurement = this.mSecondaryOrientation.getDecoratedMeasurement(childAt);
                    if (decoratedMeasurement >= f2) {
                        f = Math.max(f2, ((LayoutParams) childAt.getLayoutParams()).mFullSpan ? (1.0f * decoratedMeasurement) / this.mSpanCount : decoratedMeasurement);
                    } else {
                        f = f2;
                    }
                    i9++;
                    f2 = f;
                }
                int i10 = this.mSizePerSpan;
                int round = Math.round(this.mSpanCount * f2);
                if (this.mSecondaryOrientation.getMode() == Integer.MIN_VALUE) {
                    round = Math.min(round, this.mSecondaryOrientation.getTotalSpace());
                }
                updateMeasureSpecs(round);
                if (this.mSizePerSpan != i10) {
                    for (int i11 = 0; i11 < childCount3; i11++) {
                        View childAt2 = getChildAt(i11);
                        LayoutParams layoutParams = (LayoutParams) childAt2.getLayoutParams();
                        if (!layoutParams.mFullSpan) {
                            if (isLayoutRTL() && this.mOrientation == 1) {
                                childAt2.offsetLeftAndRight(((-((this.mSpanCount - 1) - layoutParams.mSpan.mIndex)) * this.mSizePerSpan) - ((-((this.mSpanCount - 1) - layoutParams.mSpan.mIndex)) * i10));
                            } else {
                                int i12 = layoutParams.mSpan.mIndex * this.mSizePerSpan;
                                int i13 = layoutParams.mSpan.mIndex * i10;
                                if (this.mOrientation == 1) {
                                    childAt2.offsetLeftAndRight(i12 - i13);
                                } else {
                                    childAt2.offsetTopAndBottom(i12 - i13);
                                }
                            }
                        }
                    }
                }
            }
            if (getChildCount() > 0) {
                if (this.mShouldReverseLayout) {
                    fixEndGap(recycler, state, true);
                    fixStartGap(recycler, state, false);
                } else {
                    fixStartGap(recycler, state, true);
                    fixEndGap(recycler, state, false);
                }
            }
            boolean z5 = false;
            if (z3 && !state.mInPreLayout) {
                if (this.mGapStrategy != 0 && getChildCount() > 0 && (this.mLaidOutInvalidFullSpan || hasGapsToFix() != null)) {
                    removeCallbacks(this.mCheckForGapsRunnable);
                    if (checkForGaps()) {
                        z5 = true;
                    }
                }
            }
            if (state.mInPreLayout) {
                this.mAnchorInfo.reset();
            }
            this.mLastLayoutFromEnd = anchorInfo.mLayoutFromEnd;
            this.mLastLayoutRTL = isLayoutRTL();
            if (!z5) {
                return;
            }
            this.mAnchorInfo.reset();
            z2 = false;
        }
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integers.STATUS_SUCCESS;
        this.mPendingSavedState = null;
        this.mAnchorInfo.reset();
    }

    private void updateMeasureSpecs(int totalSpace) {
        this.mSizePerSpan = totalSpace / this.mSpanCount;
        this.mFullSizeSpec = View.MeasureSpec.makeMeasureSpec(totalSpace, this.mSecondaryOrientation.getMode());
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final int computeHorizontalScrollOffset(RecyclerView.State state) {
        return computeScrollOffset(state);
    }

    private int computeScrollOffset(RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        return ScrollbarHelper.computeScrollOffset(state, this.mPrimaryOrientation, findFirstVisibleItemClosestToStart$2930a1b7(!this.mSmoothScrollbarEnabled), findFirstVisibleItemClosestToEnd$2930a1b7(this.mSmoothScrollbarEnabled ? false : true), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final int computeVerticalScrollOffset(RecyclerView.State state) {
        return computeScrollOffset(state);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final int computeHorizontalScrollExtent(RecyclerView.State state) {
        return computeScrollExtent(state);
    }

    private int computeScrollExtent(RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        return ScrollbarHelper.computeScrollExtent(state, this.mPrimaryOrientation, findFirstVisibleItemClosestToStart$2930a1b7(!this.mSmoothScrollbarEnabled), findFirstVisibleItemClosestToEnd$2930a1b7(this.mSmoothScrollbarEnabled ? false : true), this, this.mSmoothScrollbarEnabled);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final int computeVerticalScrollExtent(RecyclerView.State state) {
        return computeScrollExtent(state);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final int computeHorizontalScrollRange(RecyclerView.State state) {
        return computeScrollRange(state);
    }

    private int computeScrollRange(RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        return ScrollbarHelper.computeScrollRange(state, this.mPrimaryOrientation, findFirstVisibleItemClosestToStart$2930a1b7(!this.mSmoothScrollbarEnabled), findFirstVisibleItemClosestToEnd$2930a1b7(this.mSmoothScrollbarEnabled ? false : true), this, this.mSmoothScrollbarEnabled);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final int computeVerticalScrollRange(RecyclerView.State state) {
        return computeScrollRange(state);
    }

    private void measureChildWithDecorationsAndMargin$1bb98217(View child, int widthSpec, int heightSpec) {
        calculateItemDecorationsForChild(child, this.mTmpRect);
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int widthSpec2 = updateSpecWithExtra(widthSpec, lp.leftMargin + this.mTmpRect.left, lp.rightMargin + this.mTmpRect.right);
        int heightSpec2 = updateSpecWithExtra(heightSpec, lp.topMargin + this.mTmpRect.top, lp.bottomMargin + this.mTmpRect.bottom);
        if (shouldMeasureChild(child, widthSpec2, heightSpec2, lp)) {
            child.measure(widthSpec2, heightSpec2);
        }
    }

    private static int updateSpecWithExtra(int spec, int startInset, int endInset) {
        if (startInset != 0 || endInset != 0) {
            int mode = View.MeasureSpec.getMode(spec);
            if (mode == Integer.MIN_VALUE || mode == 1073741824) {
                return View.MeasureSpec.makeMeasureSpec(Math.max(0, (View.MeasureSpec.getSize(spec) - startInset) - endInset), mode);
            }
            return spec;
        }
        return spec;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            this.mPendingSavedState = (SavedState) state;
            requestLayout();
        }
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final Parcelable onSaveInstanceState() {
        int line;
        if (this.mPendingSavedState != null) {
            return new SavedState(this.mPendingSavedState);
        }
        SavedState state = new SavedState();
        state.mReverseLayout = this.mReverseLayout;
        state.mAnchorLayoutFromEnd = this.mLastLayoutFromEnd;
        state.mLastLayoutRTL = this.mLastLayoutRTL;
        if (this.mLazySpanLookup != null && this.mLazySpanLookup.mData != null) {
            state.mSpanLookup = this.mLazySpanLookup.mData;
            state.mSpanLookupSize = state.mSpanLookup.length;
            state.mFullSpanItems = this.mLazySpanLookup.mFullSpanItems;
        } else {
            state.mSpanLookupSize = 0;
        }
        if (getChildCount() > 0) {
            state.mAnchorPosition = this.mLastLayoutFromEnd ? getLastChildPosition() : getFirstChildPosition();
            View findFirstVisibleItemClosestToEnd$2930a1b7 = this.mShouldReverseLayout ? findFirstVisibleItemClosestToEnd$2930a1b7(true) : findFirstVisibleItemClosestToStart$2930a1b7(true);
            state.mVisibleAnchorPosition = findFirstVisibleItemClosestToEnd$2930a1b7 == null ? -1 : getPosition(findFirstVisibleItemClosestToEnd$2930a1b7);
            state.mSpanOffsetsSize = this.mSpanCount;
            state.mSpanOffsets = new int[this.mSpanCount];
            for (int i = 0; i < this.mSpanCount; i++) {
                if (this.mLastLayoutFromEnd) {
                    line = this.mSpans[i].getEndLine(Integers.STATUS_SUCCESS);
                    if (line != Integer.MIN_VALUE) {
                        line -= this.mPrimaryOrientation.getEndAfterPadding();
                    }
                } else {
                    line = this.mSpans[i].getStartLine(Integers.STATUS_SUCCESS);
                    if (line != Integer.MIN_VALUE) {
                        line -= this.mPrimaryOrientation.getStartAfterPadding();
                    }
                }
                state.mSpanOffsets[i] = line;
            }
            return state;
        }
        state.mAnchorPosition = -1;
        state.mVisibleAnchorPosition = -1;
        state.mSpanOffsetsSize = 0;
        return state;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View host, AccessibilityNodeInfoCompat info) {
        int i;
        int i2;
        int i3 = -1;
        ViewGroup.LayoutParams lp = host.getLayoutParams();
        if (!(lp instanceof LayoutParams)) {
            super.onInitializeAccessibilityNodeInfoForItem(host, info);
            return;
        }
        LayoutParams sglp = (LayoutParams) lp;
        if (this.mOrientation == 0) {
            i = sglp.getSpanIndex();
            i2 = sglp.mFullSpan ? this.mSpanCount : 1;
            r2 = -1;
        } else {
            int spanIndex = sglp.getSpanIndex();
            if (sglp.mFullSpan) {
                r2 = this.mSpanCount;
                i = -1;
                i3 = spanIndex;
                i2 = -1;
            } else {
                i = -1;
                i3 = spanIndex;
                i2 = -1;
            }
        }
        info.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain$430787b1(i, i2, i3, r2, sglp.mFullSpan));
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (getChildCount() > 0) {
            AccessibilityRecordCompat record = AccessibilityEventCompat.asRecord(event);
            View start = findFirstVisibleItemClosestToStart$2930a1b7(false);
            View end = findFirstVisibleItemClosestToEnd$2930a1b7(false);
            if (start != null && end != null) {
                int startPos = getPosition(start);
                int endPos = getPosition(end);
                if (startPos < endPos) {
                    record.setFromIndex(startPos);
                    record.setToIndex(endPos);
                } else {
                    record.setFromIndex(endPos);
                    record.setToIndex(startPos);
                }
            }
        }
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final int getRowCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        return this.mOrientation == 0 ? this.mSpanCount : super.getRowCountForAccessibility(recycler, state);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final int getColumnCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        return this.mOrientation == 1 ? this.mSpanCount : super.getColumnCountForAccessibility(recycler, state);
    }

    private View findFirstVisibleItemClosestToStart$2930a1b7(boolean fullyVisible) {
        int boundsStart = this.mPrimaryOrientation.getStartAfterPadding();
        int boundsEnd = this.mPrimaryOrientation.getEndAfterPadding();
        int limit = getChildCount();
        View partiallyVisible = null;
        for (int i = 0; i < limit; i++) {
            View child = getChildAt(i);
            int childStart = this.mPrimaryOrientation.getDecoratedStart(child);
            if (this.mPrimaryOrientation.getDecoratedEnd(child) > boundsStart && childStart < boundsEnd) {
                if (childStart < boundsStart && fullyVisible) {
                    if (partiallyVisible == null) {
                        partiallyVisible = child;
                    }
                } else {
                    return child;
                }
            }
        }
        return partiallyVisible;
    }

    private View findFirstVisibleItemClosestToEnd$2930a1b7(boolean fullyVisible) {
        int boundsStart = this.mPrimaryOrientation.getStartAfterPadding();
        int boundsEnd = this.mPrimaryOrientation.getEndAfterPadding();
        View partiallyVisible = null;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            int childStart = this.mPrimaryOrientation.getDecoratedStart(child);
            int childEnd = this.mPrimaryOrientation.getDecoratedEnd(child);
            if (childEnd > boundsStart && childStart < boundsEnd) {
                if (childEnd > boundsEnd && fullyVisible) {
                    if (partiallyVisible == null) {
                        partiallyVisible = child;
                    }
                } else {
                    return child;
                }
            }
        }
        return partiallyVisible;
    }

    private void fixEndGap(RecyclerView.Recycler recycler, RecyclerView.State state, boolean canOffsetChildren) {
        int gap;
        int maxEndLine = getMaxEnd(Integers.STATUS_SUCCESS);
        if (maxEndLine != Integer.MIN_VALUE && (gap = this.mPrimaryOrientation.getEndAfterPadding() - maxEndLine) > 0) {
            int fixOffset = -scrollBy(-gap, recycler, state);
            int gap2 = gap - fixOffset;
            if (canOffsetChildren && gap2 > 0) {
                this.mPrimaryOrientation.offsetChildren(gap2);
            }
        }
    }

    private void fixStartGap(RecyclerView.Recycler recycler, RecyclerView.State state, boolean canOffsetChildren) {
        int gap;
        int minStartLine = getMinStart(Integer.MAX_VALUE);
        if (minStartLine != Integer.MAX_VALUE && (gap = minStartLine - this.mPrimaryOrientation.getStartAfterPadding()) > 0) {
            int fixOffset = scrollBy(gap, recycler, state);
            int gap2 = gap - fixOffset;
            if (canOffsetChildren && gap2 > 0) {
                this.mPrimaryOrientation.offsetChildren(-gap2);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0033  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0071  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void updateLayoutState(int r8, android.support.v7.widget.RecyclerView.State r9) {
        /*
            r7 = this;
            r3 = 1
            r4 = 0
            android.support.v7.widget.LayoutState r5 = r7.mLayoutState
            r5.mAvailable = r4
            android.support.v7.widget.LayoutState r5 = r7.mLayoutState
            r5.mCurrentPosition = r8
            r1 = 0
            r0 = 0
            boolean r5 = r7.isSmoothScrolling()
            if (r5 == 0) goto L24
            int r2 = r9.mTargetPosition
            r5 = -1
            if (r2 == r5) goto L24
            boolean r6 = r7.mShouldReverseLayout
            if (r2 >= r8) goto L66
            r5 = r3
        L1c:
            if (r6 != r5) goto L68
            android.support.v7.widget.OrientationHelper r5 = r7.mPrimaryOrientation
            int r0 = r5.getTotalSpace()
        L24:
            android.support.v7.widget.RecyclerView r5 = r7.mRecyclerView
            if (r5 == 0) goto L6f
            android.support.v7.widget.RecyclerView r5 = r7.mRecyclerView
            boolean r5 = android.support.v7.widget.RecyclerView.access$5400(r5)
            if (r5 == 0) goto L6f
            r5 = r3
        L31:
            if (r5 == 0) goto L71
            android.support.v7.widget.LayoutState r5 = r7.mLayoutState
            android.support.v7.widget.OrientationHelper r6 = r7.mPrimaryOrientation
            int r6 = r6.getStartAfterPadding()
            int r6 = r6 - r1
            r5.mStartLine = r6
            android.support.v7.widget.LayoutState r5 = r7.mLayoutState
            android.support.v7.widget.OrientationHelper r6 = r7.mPrimaryOrientation
            int r6 = r6.getEndAfterPadding()
            int r6 = r6 + r0
            r5.mEndLine = r6
        L49:
            android.support.v7.widget.LayoutState r5 = r7.mLayoutState
            r5.mStopInFocusable = r4
            android.support.v7.widget.LayoutState r5 = r7.mLayoutState
            r5.mRecycle = r3
            android.support.v7.widget.LayoutState r5 = r7.mLayoutState
            android.support.v7.widget.OrientationHelper r6 = r7.mPrimaryOrientation
            int r6 = r6.getMode()
            if (r6 != 0) goto L82
            android.support.v7.widget.OrientationHelper r6 = r7.mPrimaryOrientation
            int r6 = r6.getEnd()
            if (r6 != 0) goto L82
        L63:
            r5.mInfinite = r3
            return
        L66:
            r5 = r4
            goto L1c
        L68:
            android.support.v7.widget.OrientationHelper r5 = r7.mPrimaryOrientation
            int r1 = r5.getTotalSpace()
            goto L24
        L6f:
            r5 = r4
            goto L31
        L71:
            android.support.v7.widget.LayoutState r5 = r7.mLayoutState
            android.support.v7.widget.OrientationHelper r6 = r7.mPrimaryOrientation
            int r6 = r6.getEnd()
            int r6 = r6 + r0
            r5.mEndLine = r6
            android.support.v7.widget.LayoutState r5 = r7.mLayoutState
            int r6 = -r1
            r5.mStartLine = r6
            goto L49
        L82:
            r3 = r4
            goto L63
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.StaggeredGridLayoutManager.updateLayoutState(int, android.support.v7.widget.RecyclerView$State):void");
    }

    private void setLayoutStateDirection(int direction) {
        this.mLayoutState.mLayoutDirection = direction;
        this.mLayoutState.mItemDirection = this.mShouldReverseLayout != (direction == -1) ? -1 : 1;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void offsetChildrenHorizontal(int dx) {
        super.offsetChildrenHorizontal(dx);
        for (int i = 0; i < this.mSpanCount; i++) {
            this.mSpans[i].onOffset(dx);
        }
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void offsetChildrenVertical(int dy) {
        super.offsetChildrenVertical(dy);
        for (int i = 0; i < this.mSpanCount; i++) {
            this.mSpans[i].onOffset(dy);
        }
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onItemsRemoved$5927c743(int positionStart, int itemCount) {
        handleUpdate(positionStart, itemCount, 2);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onItemsAdded$5927c743(int positionStart, int itemCount) {
        handleUpdate(positionStart, itemCount, 1);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onItemsChanged$57043c5d() {
        this.mLazySpanLookup.clear();
        requestLayout();
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onItemsMoved$342e6be0(int from, int to) {
        handleUpdate(from, to, 8);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onItemsUpdated$783f8c5f$5927c743(int positionStart, int itemCount) {
        handleUpdate(positionStart, itemCount, 4);
    }

    private void handleUpdate(int positionStart, int itemCountOrToPosition, int cmd) {
        int affectedRangeStart;
        int affectedRangeEnd;
        int minPosition = this.mShouldReverseLayout ? getLastChildPosition() : getFirstChildPosition();
        if (cmd == 8) {
            if (positionStart < itemCountOrToPosition) {
                affectedRangeEnd = itemCountOrToPosition + 1;
                affectedRangeStart = positionStart;
            } else {
                affectedRangeEnd = positionStart + 1;
                affectedRangeStart = itemCountOrToPosition;
            }
        } else {
            affectedRangeStart = positionStart;
            affectedRangeEnd = positionStart + itemCountOrToPosition;
        }
        this.mLazySpanLookup.invalidateAfter(affectedRangeStart);
        switch (cmd) {
            case 1:
                this.mLazySpanLookup.offsetForAddition(positionStart, itemCountOrToPosition);
                break;
            case 2:
                this.mLazySpanLookup.offsetForRemoval(positionStart, itemCountOrToPosition);
                break;
            case 8:
                this.mLazySpanLookup.offsetForRemoval(positionStart, 1);
                this.mLazySpanLookup.offsetForAddition(itemCountOrToPosition, 1);
                break;
        }
        if (affectedRangeEnd > minPosition) {
            int maxPosition = this.mShouldReverseLayout ? getFirstChildPosition() : getLastChildPosition();
            if (affectedRangeStart <= maxPosition) {
                requestLayout();
            }
        }
    }

    private int fill(RecyclerView.Recycler recycler, LayoutState layoutState, RecyclerView.State state) {
        int targetLine;
        int defaultNewViewLine;
        int diff;
        int spanIndex;
        Span currentSpan;
        int end;
        int start;
        int otherStart;
        int otherEnd;
        boolean z;
        boolean hasInvalidGap;
        boolean z2;
        int i;
        int i2;
        int i3;
        Span span;
        Span span2;
        this.mRemainingSpans.set(0, this.mSpanCount, true);
        if (this.mLayoutState.mInfinite) {
            if (layoutState.mLayoutDirection == 1) {
                targetLine = Integer.MAX_VALUE;
            } else {
                targetLine = Integers.STATUS_SUCCESS;
            }
        } else if (layoutState.mLayoutDirection == 1) {
            targetLine = layoutState.mEndLine + layoutState.mAvailable;
        } else {
            targetLine = layoutState.mStartLine - layoutState.mAvailable;
        }
        updateAllRemainingSpans(layoutState.mLayoutDirection, targetLine);
        if (this.mShouldReverseLayout) {
            defaultNewViewLine = this.mPrimaryOrientation.getEndAfterPadding();
        } else {
            defaultNewViewLine = this.mPrimaryOrientation.getStartAfterPadding();
        }
        boolean added = false;
        while (true) {
            if (!(layoutState.mCurrentPosition >= 0 && layoutState.mCurrentPosition < state.getItemCount()) || (!this.mLayoutState.mInfinite && this.mRemainingSpans.isEmpty())) {
                break;
            }
            View view = recycler.getViewForPosition(layoutState.mCurrentPosition);
            layoutState.mCurrentPosition += layoutState.mItemDirection;
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            int position = lp.mViewHolder.getLayoutPosition();
            LazySpanLookup lazySpanLookup = this.mLazySpanLookup;
            if (lazySpanLookup.mData == null || position >= lazySpanLookup.mData.length) {
                spanIndex = -1;
            } else {
                spanIndex = lazySpanLookup.mData[position];
            }
            boolean assignSpan = spanIndex == -1;
            if (assignSpan) {
                if (lp.mFullSpan) {
                    currentSpan = this.mSpans[0];
                } else {
                    if (preferLastSpan(layoutState.mLayoutDirection)) {
                        i = this.mSpanCount - 1;
                        i2 = -1;
                        i3 = -1;
                    } else {
                        i = 0;
                        i2 = this.mSpanCount;
                        i3 = 1;
                    }
                    if (layoutState.mLayoutDirection == 1) {
                        Span span3 = null;
                        int i4 = Integer.MAX_VALUE;
                        int startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
                        int i5 = i;
                        while (i5 != i2) {
                            Span span4 = this.mSpans[i5];
                            int endLine = span4.getEndLine(startAfterPadding);
                            if (endLine < i4) {
                                span2 = span4;
                            } else {
                                endLine = i4;
                                span2 = span3;
                            }
                            i5 += i3;
                            span3 = span2;
                            i4 = endLine;
                        }
                        currentSpan = span3;
                    } else {
                        Span span5 = null;
                        int i6 = Integers.STATUS_SUCCESS;
                        int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
                        int i7 = i;
                        while (i7 != i2) {
                            Span span6 = this.mSpans[i7];
                            int startLine = span6.getStartLine(endAfterPadding);
                            if (startLine > i6) {
                                span = span6;
                            } else {
                                startLine = i6;
                                span = span5;
                            }
                            i7 += i3;
                            span5 = span;
                            i6 = startLine;
                        }
                        currentSpan = span5;
                    }
                }
                LazySpanLookup lazySpanLookup2 = this.mLazySpanLookup;
                lazySpanLookup2.ensureSize(position);
                lazySpanLookup2.mData[position] = currentSpan.mIndex;
            } else {
                currentSpan = this.mSpans[spanIndex];
            }
            lp.mSpan = currentSpan;
            if (layoutState.mLayoutDirection == 1) {
                super.addViewInt(view, -1, false);
            } else {
                super.addViewInt(view, 0, false);
            }
            if (lp.mFullSpan) {
                if (this.mOrientation == 1) {
                    measureChildWithDecorationsAndMargin$1bb98217(view, this.mFullSizeSpec, getChildMeasureSpec(this.mHeight, this.mHeightMode, 0, lp.height, true));
                } else {
                    measureChildWithDecorationsAndMargin$1bb98217(view, getChildMeasureSpec(this.mWidth, this.mWidthMode, 0, lp.width, true), this.mFullSizeSpec);
                }
            } else if (this.mOrientation == 1) {
                measureChildWithDecorationsAndMargin$1bb98217(view, getChildMeasureSpec(this.mSizePerSpan, this.mWidthMode, 0, lp.width, false), getChildMeasureSpec(this.mHeight, this.mHeightMode, 0, lp.height, true));
            } else {
                measureChildWithDecorationsAndMargin$1bb98217(view, getChildMeasureSpec(this.mWidth, this.mWidthMode, 0, lp.width, true), getChildMeasureSpec(this.mSizePerSpan, this.mHeightMode, 0, lp.height, false));
            }
            if (layoutState.mLayoutDirection == 1) {
                start = lp.mFullSpan ? getMaxEnd(defaultNewViewLine) : currentSpan.getEndLine(defaultNewViewLine);
                end = start + this.mPrimaryOrientation.getDecoratedMeasurement(view);
                if (assignSpan && lp.mFullSpan) {
                    LazySpanLookup.FullSpanItem fullSpanItem = new LazySpanLookup.FullSpanItem();
                    fullSpanItem.mGapPerSpan = new int[this.mSpanCount];
                    for (int i8 = 0; i8 < this.mSpanCount; i8++) {
                        fullSpanItem.mGapPerSpan[i8] = start - this.mSpans[i8].getEndLine(start);
                    }
                    fullSpanItem.mGapDir = -1;
                    fullSpanItem.mPosition = position;
                    this.mLazySpanLookup.addFullSpanItem(fullSpanItem);
                }
            } else {
                end = lp.mFullSpan ? getMinStart(defaultNewViewLine) : currentSpan.getStartLine(defaultNewViewLine);
                start = end - this.mPrimaryOrientation.getDecoratedMeasurement(view);
                if (assignSpan && lp.mFullSpan) {
                    LazySpanLookup.FullSpanItem fullSpanItem2 = new LazySpanLookup.FullSpanItem();
                    fullSpanItem2.mGapPerSpan = new int[this.mSpanCount];
                    for (int i9 = 0; i9 < this.mSpanCount; i9++) {
                        fullSpanItem2.mGapPerSpan[i9] = this.mSpans[i9].getStartLine(end) - end;
                    }
                    fullSpanItem2.mGapDir = 1;
                    fullSpanItem2.mPosition = position;
                    this.mLazySpanLookup.addFullSpanItem(fullSpanItem2);
                }
            }
            if (lp.mFullSpan && layoutState.mItemDirection == -1) {
                if (!assignSpan) {
                    if (layoutState.mLayoutDirection == 1) {
                        int endLine2 = this.mSpans[0].getEndLine(Integers.STATUS_SUCCESS);
                        int i10 = 1;
                        while (true) {
                            if (i10 < this.mSpanCount) {
                                if (this.mSpans[i10].getEndLine(Integers.STATUS_SUCCESS) == endLine2) {
                                    i10++;
                                } else {
                                    z2 = false;
                                    break;
                                }
                            } else {
                                z2 = true;
                                break;
                            }
                        }
                        hasInvalidGap = !z2;
                    } else {
                        int startLine2 = this.mSpans[0].getStartLine(Integers.STATUS_SUCCESS);
                        int i11 = 1;
                        while (true) {
                            if (i11 < this.mSpanCount) {
                                if (this.mSpans[i11].getStartLine(Integers.STATUS_SUCCESS) == startLine2) {
                                    i11++;
                                } else {
                                    z = false;
                                    break;
                                }
                            } else {
                                z = true;
                                break;
                            }
                        }
                        hasInvalidGap = !z;
                    }
                    if (hasInvalidGap) {
                        LazySpanLookup.FullSpanItem fullSpanItem3 = this.mLazySpanLookup.getFullSpanItem(position);
                        if (fullSpanItem3 != null) {
                            fullSpanItem3.mHasUnwantedGapAfter = true;
                        }
                    }
                }
                this.mLaidOutInvalidFullSpan = true;
            }
            if (layoutState.mLayoutDirection == 1) {
                if (lp.mFullSpan) {
                    for (int i12 = this.mSpanCount - 1; i12 >= 0; i12--) {
                        this.mSpans[i12].appendToSpan(view);
                    }
                } else {
                    lp.mSpan.appendToSpan(view);
                }
            } else if (lp.mFullSpan) {
                for (int i13 = this.mSpanCount - 1; i13 >= 0; i13--) {
                    this.mSpans[i13].prependToSpan(view);
                }
            } else {
                lp.mSpan.prependToSpan(view);
            }
            if (isLayoutRTL() && this.mOrientation == 1) {
                otherEnd = lp.mFullSpan ? this.mSecondaryOrientation.getEndAfterPadding() : this.mSecondaryOrientation.getEndAfterPadding() - (((this.mSpanCount - 1) - currentSpan.mIndex) * this.mSizePerSpan);
                otherStart = otherEnd - this.mSecondaryOrientation.getDecoratedMeasurement(view);
            } else {
                otherStart = lp.mFullSpan ? this.mSecondaryOrientation.getStartAfterPadding() : (currentSpan.mIndex * this.mSizePerSpan) + this.mSecondaryOrientation.getStartAfterPadding();
                otherEnd = otherStart + this.mSecondaryOrientation.getDecoratedMeasurement(view);
            }
            if (this.mOrientation == 1) {
                layoutDecoratedWithMargins(view, otherStart, start, otherEnd, end);
            } else {
                layoutDecoratedWithMargins(view, start, otherStart, end, otherEnd);
            }
            if (lp.mFullSpan) {
                updateAllRemainingSpans(this.mLayoutState.mLayoutDirection, targetLine);
            } else {
                updateRemainingSpans(currentSpan, this.mLayoutState.mLayoutDirection, targetLine);
            }
            recycle(recycler, this.mLayoutState);
            if (this.mLayoutState.mStopInFocusable && view.isFocusable()) {
                if (lp.mFullSpan) {
                    this.mRemainingSpans.clear();
                } else {
                    this.mRemainingSpans.set(currentSpan.mIndex, false);
                }
            }
            added = true;
        }
        if (!added) {
            recycle(recycler, this.mLayoutState);
        }
        if (this.mLayoutState.mLayoutDirection == -1) {
            int minStart = getMinStart(this.mPrimaryOrientation.getStartAfterPadding());
            diff = this.mPrimaryOrientation.getStartAfterPadding() - minStart;
        } else {
            diff = getMaxEnd(this.mPrimaryOrientation.getEndAfterPadding()) - this.mPrimaryOrientation.getEndAfterPadding();
        }
        if (diff > 0) {
            return Math.min(layoutState.mAvailable, diff);
        }
        return 0;
    }

    private void recycle(RecyclerView.Recycler recycler, LayoutState layoutState) {
        int line;
        int line2;
        int i = 1;
        if (layoutState.mRecycle && !layoutState.mInfinite) {
            if (layoutState.mAvailable == 0) {
                if (layoutState.mLayoutDirection == -1) {
                    recycleFromEnd(recycler, layoutState.mEndLine);
                    return;
                } else {
                    recycleFromStart(recycler, layoutState.mStartLine);
                    return;
                }
            }
            if (layoutState.mLayoutDirection == -1) {
                int i2 = layoutState.mStartLine;
                int i3 = layoutState.mStartLine;
                int startLine = this.mSpans[0].getStartLine(i3);
                while (i < this.mSpanCount) {
                    int startLine2 = this.mSpans[i].getStartLine(i3);
                    if (startLine2 > startLine) {
                        startLine = startLine2;
                    }
                    i++;
                }
                int scrolled = i2 - startLine;
                if (scrolled < 0) {
                    line2 = layoutState.mEndLine;
                } else {
                    line2 = layoutState.mEndLine - Math.min(scrolled, layoutState.mAvailable);
                }
                recycleFromEnd(recycler, line2);
                return;
            }
            int i4 = layoutState.mEndLine;
            int endLine = this.mSpans[0].getEndLine(i4);
            while (i < this.mSpanCount) {
                int endLine2 = this.mSpans[i].getEndLine(i4);
                if (endLine2 < endLine) {
                    endLine = endLine2;
                }
                i++;
            }
            int scrolled2 = endLine - layoutState.mEndLine;
            if (scrolled2 < 0) {
                line = layoutState.mStartLine;
            } else {
                line = layoutState.mStartLine + Math.min(scrolled2, layoutState.mAvailable);
            }
            recycleFromStart(recycler, line);
        }
    }

    private void updateAllRemainingSpans(int layoutDir, int targetLine) {
        for (int i = 0; i < this.mSpanCount; i++) {
            if (!this.mSpans[i].mViews.isEmpty()) {
                updateRemainingSpans(this.mSpans[i], layoutDir, targetLine);
            }
        }
    }

    private int getMinStart(int def) {
        int minStart = this.mSpans[0].getStartLine(def);
        for (int i = 1; i < this.mSpanCount; i++) {
            int spanStart = this.mSpans[i].getStartLine(def);
            if (spanStart < minStart) {
                minStart = spanStart;
            }
        }
        return minStart;
    }

    private int getMaxEnd(int def) {
        int maxEnd = this.mSpans[0].getEndLine(def);
        for (int i = 1; i < this.mSpanCount; i++) {
            int spanEnd = this.mSpans[i].getEndLine(def);
            if (spanEnd > maxEnd) {
                maxEnd = spanEnd;
            }
        }
        return maxEnd;
    }

    private void recycleFromStart(RecyclerView.Recycler recycler, int line) {
        while (getChildCount() > 0) {
            View child = getChildAt(0);
            if (this.mPrimaryOrientation.getDecoratedEnd(child) <= line && this.mPrimaryOrientation.getTransformedEndWithDecoration(child) <= line) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.mFullSpan) {
                    for (int j = 0; j < this.mSpanCount; j++) {
                        if (this.mSpans[j].mViews.size() == 1) {
                            return;
                        }
                    }
                    for (int j2 = 0; j2 < this.mSpanCount; j2++) {
                        this.mSpans[j2].popStart();
                    }
                } else if (lp.mSpan.mViews.size() != 1) {
                    lp.mSpan.popStart();
                } else {
                    return;
                }
                removeAndRecycleView(child, recycler);
            } else {
                return;
            }
        }
    }

    private void recycleFromEnd(RecyclerView.Recycler recycler, int line) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (this.mPrimaryOrientation.getDecoratedStart(child) >= line && this.mPrimaryOrientation.getTransformedStartWithDecoration(child) >= line) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.mFullSpan) {
                    for (int j = 0; j < this.mSpanCount; j++) {
                        if (this.mSpans[j].mViews.size() == 1) {
                            return;
                        }
                    }
                    for (int j2 = 0; j2 < this.mSpanCount; j2++) {
                        this.mSpans[j2].popEnd();
                    }
                } else if (lp.mSpan.mViews.size() != 1) {
                    lp.mSpan.popEnd();
                } else {
                    return;
                }
                removeAndRecycleView(child, recycler);
            } else {
                return;
            }
        }
    }

    private boolean preferLastSpan(int layoutDir) {
        if (this.mOrientation == 0) {
            return (layoutDir == -1) != this.mShouldReverseLayout;
        }
        return ((layoutDir == -1) == this.mShouldReverseLayout) == isLayoutRTL();
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final boolean canScrollVertically() {
        return this.mOrientation == 1;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final boolean canScrollHorizontally() {
        return this.mOrientation == 0;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollBy(dx, recycler, state);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollBy(dy, recycler, state);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int calculateScrollDirectionForPosition(int position) {
        if (getChildCount() == 0) {
            return this.mShouldReverseLayout ? 1 : -1;
        }
        int firstChildPos = getFirstChildPosition();
        return (position < firstChildPos) != this.mShouldReverseLayout ? -1 : 1;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void smoothScrollToPosition$7d69765f(RecyclerView recyclerView, int position) {
        LinearSmoothScroller scroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: android.support.v7.widget.StaggeredGridLayoutManager.2
            @Override // android.support.v7.widget.LinearSmoothScroller
            public final PointF computeScrollVectorForPosition(int targetPosition) {
                int direction = StaggeredGridLayoutManager.this.calculateScrollDirectionForPosition(targetPosition);
                if (direction != 0) {
                    if (StaggeredGridLayoutManager.this.mOrientation == 0) {
                        return new PointF(direction, 0.0f);
                    }
                    return new PointF(0.0f, direction);
                }
                return null;
            }
        };
        scroller.mTargetPosition = position;
        startSmoothScroll(scroller);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void scrollToPosition(int position) {
        if (this.mPendingSavedState != null && this.mPendingSavedState.mAnchorPosition != position) {
            SavedState savedState = this.mPendingSavedState;
            savedState.mSpanOffsets = null;
            savedState.mSpanOffsetsSize = 0;
            savedState.mAnchorPosition = -1;
            savedState.mVisibleAnchorPosition = -1;
        }
        this.mPendingScrollPosition = position;
        this.mPendingScrollPositionOffset = Integers.STATUS_SUCCESS;
        requestLayout();
    }

    private int scrollBy(int dt, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int layoutDir;
        int referenceChildPosition;
        int totalScroll;
        if (dt > 0) {
            layoutDir = 1;
            referenceChildPosition = getLastChildPosition();
        } else {
            layoutDir = -1;
            referenceChildPosition = getFirstChildPosition();
        }
        this.mLayoutState.mRecycle = true;
        updateLayoutState(referenceChildPosition, state);
        setLayoutStateDirection(layoutDir);
        this.mLayoutState.mCurrentPosition = this.mLayoutState.mItemDirection + referenceChildPosition;
        int absDt = Math.abs(dt);
        this.mLayoutState.mAvailable = absDt;
        int consumed = fill(recycler, this.mLayoutState, state);
        if (absDt < consumed) {
            totalScroll = dt;
        } else if (dt < 0) {
            totalScroll = -consumed;
        } else {
            totalScroll = consumed;
        }
        this.mPrimaryOrientation.offsetChildren(-totalScroll);
        this.mLastLayoutFromEnd = this.mShouldReverseLayout;
        return totalScroll;
    }

    private int getLastChildPosition() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return 0;
        }
        return getPosition(getChildAt(childCount - 1));
    }

    private int getFirstChildPosition() {
        if (getChildCount() == 0) {
            return 0;
        }
        return getPosition(getChildAt(0));
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return this.mOrientation == 0 ? new LayoutParams(-2, -1) : new LayoutParams(-1, -2);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        return new LayoutParams(c, attrs);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return lp instanceof ViewGroup.MarginLayoutParams ? new LayoutParams((ViewGroup.MarginLayoutParams) lp) : new LayoutParams(lp);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        return lp instanceof LayoutParams;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final View onFocusSearchFailed(View focused, int direction, RecyclerView.Recycler recycler, RecyclerView.State state) {
        View directChild;
        int layoutDir;
        int referenceChildPosition;
        View view;
        if (getChildCount() == 0 || (directChild = findContainingItemView(focused)) == null) {
            return null;
        }
        resolveShouldLayoutReverse();
        switch (direction) {
            case 1:
                if (this.mOrientation == 1) {
                    layoutDir = -1;
                    break;
                } else if (isLayoutRTL()) {
                    layoutDir = 1;
                    break;
                } else {
                    layoutDir = -1;
                    break;
                }
            case 2:
                if (this.mOrientation == 1) {
                    layoutDir = 1;
                    break;
                } else if (isLayoutRTL()) {
                    layoutDir = -1;
                    break;
                } else {
                    layoutDir = 1;
                    break;
                }
            case 17:
                if (this.mOrientation == 0) {
                    layoutDir = -1;
                    break;
                } else {
                    layoutDir = Integers.STATUS_SUCCESS;
                    break;
                }
            case 33:
                if (this.mOrientation == 1) {
                    layoutDir = -1;
                    break;
                } else {
                    layoutDir = Integers.STATUS_SUCCESS;
                    break;
                }
            case 66:
                if (this.mOrientation == 0) {
                    layoutDir = 1;
                    break;
                } else {
                    layoutDir = Integers.STATUS_SUCCESS;
                    break;
                }
            case 130:
                if (this.mOrientation == 1) {
                    layoutDir = 1;
                    break;
                } else {
                    layoutDir = Integers.STATUS_SUCCESS;
                    break;
                }
            default:
                layoutDir = Integers.STATUS_SUCCESS;
                break;
        }
        if (layoutDir == Integer.MIN_VALUE) {
            return null;
        }
        LayoutParams prevFocusLayoutParams = (LayoutParams) directChild.getLayoutParams();
        boolean prevFocusFullSpan = prevFocusLayoutParams.mFullSpan;
        Span prevFocusSpan = prevFocusLayoutParams.mSpan;
        if (layoutDir == 1) {
            referenceChildPosition = getLastChildPosition();
        } else {
            referenceChildPosition = getFirstChildPosition();
        }
        updateLayoutState(referenceChildPosition, state);
        setLayoutStateDirection(layoutDir);
        this.mLayoutState.mCurrentPosition = this.mLayoutState.mItemDirection + referenceChildPosition;
        this.mLayoutState.mAvailable = (int) (0.33333334f * this.mPrimaryOrientation.getTotalSpace());
        this.mLayoutState.mStopInFocusable = true;
        this.mLayoutState.mRecycle = false;
        fill(recycler, this.mLayoutState, state);
        this.mLastLayoutFromEnd = this.mShouldReverseLayout;
        if (prevFocusFullSpan || (view = prevFocusSpan.getFocusableViewAfter(referenceChildPosition, layoutDir)) == null || view == directChild) {
            if (preferLastSpan(layoutDir)) {
                for (int i = this.mSpanCount - 1; i >= 0; i--) {
                    View view2 = this.mSpans[i].getFocusableViewAfter(referenceChildPosition, layoutDir);
                    if (view2 != null && view2 != directChild) {
                        return view2;
                    }
                }
            } else {
                for (int i2 = 0; i2 < this.mSpanCount; i2++) {
                    View view3 = this.mSpans[i2].getFocusableViewAfter(referenceChildPosition, layoutDir);
                    if (view3 != null && view3 != directChild) {
                        return view3;
                    }
                }
            }
            return null;
        }
        return view;
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends RecyclerView.LayoutParams {
        boolean mFullSpan;
        Span mSpan;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public final int getSpanIndex() {
            if (this.mSpan == null) {
                return -1;
            }
            return this.mSpan.mIndex;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class Span {
        int mCachedEnd;
        int mCachedStart;
        int mDeletedSize;
        final int mIndex;
        ArrayList<View> mViews;

        /* synthetic */ Span(StaggeredGridLayoutManager x0, int x1, byte b) {
            this(x1);
        }

        private Span(int index) {
            this.mViews = new ArrayList<>();
            this.mCachedStart = Integers.STATUS_SUCCESS;
            this.mCachedEnd = Integers.STATUS_SUCCESS;
            this.mDeletedSize = 0;
            this.mIndex = index;
        }

        final int getStartLine(int def) {
            if (this.mCachedStart != Integer.MIN_VALUE) {
                int def2 = this.mCachedStart;
                return def2;
            }
            if (this.mViews.size() != 0) {
                calculateCachedStart();
                int def3 = this.mCachedStart;
                return def3;
            }
            return def;
        }

        private void calculateCachedStart() {
            LazySpanLookup.FullSpanItem fsi;
            View startView = this.mViews.get(0);
            LayoutParams lp = (LayoutParams) startView.getLayoutParams();
            this.mCachedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(startView);
            if (lp.mFullSpan && (fsi = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(lp.mViewHolder.getLayoutPosition())) != null && fsi.mGapDir == -1) {
                this.mCachedStart -= fsi.getGapForSpan(this.mIndex);
            }
        }

        final int getStartLine() {
            if (this.mCachedStart != Integer.MIN_VALUE) {
                return this.mCachedStart;
            }
            calculateCachedStart();
            return this.mCachedStart;
        }

        final int getEndLine(int def) {
            if (this.mCachedEnd != Integer.MIN_VALUE) {
                int def2 = this.mCachedEnd;
                return def2;
            }
            if (this.mViews.size() != 0) {
                calculateCachedEnd();
                int def3 = this.mCachedEnd;
                return def3;
            }
            return def;
        }

        private void calculateCachedEnd() {
            LazySpanLookup.FullSpanItem fsi;
            View endView = this.mViews.get(this.mViews.size() - 1);
            LayoutParams lp = (LayoutParams) endView.getLayoutParams();
            this.mCachedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(endView);
            if (lp.mFullSpan && (fsi = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(lp.mViewHolder.getLayoutPosition())) != null && fsi.mGapDir == 1) {
                this.mCachedEnd += fsi.getGapForSpan(this.mIndex);
            }
        }

        final int getEndLine() {
            if (this.mCachedEnd != Integer.MIN_VALUE) {
                return this.mCachedEnd;
            }
            calculateCachedEnd();
            return this.mCachedEnd;
        }

        final void clear() {
            this.mViews.clear();
            this.mCachedStart = Integers.STATUS_SUCCESS;
            this.mCachedEnd = Integers.STATUS_SUCCESS;
            this.mDeletedSize = 0;
        }

        final void setLine(int line) {
            this.mCachedStart = line;
            this.mCachedEnd = line;
        }

        final void popEnd() {
            int size = this.mViews.size();
            View end = this.mViews.remove(size - 1);
            LayoutParams lp = (LayoutParams) end.getLayoutParams();
            lp.mSpan = null;
            if (lp.mViewHolder.isRemoved() || lp.mViewHolder.isUpdated()) {
                this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(end);
            }
            if (size == 1) {
                this.mCachedStart = Integers.STATUS_SUCCESS;
            }
            this.mCachedEnd = Integers.STATUS_SUCCESS;
        }

        final void popStart() {
            View start = this.mViews.remove(0);
            LayoutParams lp = (LayoutParams) start.getLayoutParams();
            lp.mSpan = null;
            if (this.mViews.size() == 0) {
                this.mCachedEnd = Integers.STATUS_SUCCESS;
            }
            if (lp.mViewHolder.isRemoved() || lp.mViewHolder.isUpdated()) {
                this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(start);
            }
            this.mCachedStart = Integers.STATUS_SUCCESS;
        }

        final void onOffset(int dt) {
            if (this.mCachedStart != Integer.MIN_VALUE) {
                this.mCachedStart += dt;
            }
            if (this.mCachedEnd != Integer.MIN_VALUE) {
                this.mCachedEnd += dt;
            }
        }

        public final View getFocusableViewAfter(int referenceChildPosition, int layoutDir) {
            View candidate = null;
            if (layoutDir == -1) {
                int limit = this.mViews.size();
                for (int i = 0; i < limit; i++) {
                    View view = this.mViews.get(i);
                    if (!view.isFocusable()) {
                        break;
                    }
                    if ((StaggeredGridLayoutManager.getPosition(view) > referenceChildPosition) != StaggeredGridLayoutManager.this.mReverseLayout) {
                        break;
                    }
                    candidate = view;
                }
            } else {
                for (int i2 = this.mViews.size() - 1; i2 >= 0; i2--) {
                    View view2 = this.mViews.get(i2);
                    if (!view2.isFocusable()) {
                        break;
                    }
                    if ((StaggeredGridLayoutManager.getPosition(view2) > referenceChildPosition) != (!StaggeredGridLayoutManager.this.mReverseLayout)) {
                        break;
                    }
                    candidate = view2;
                }
            }
            return candidate;
        }

        final void prependToSpan(View view) {
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.mSpan = this;
            this.mViews.add(0, view);
            this.mCachedStart = Integers.STATUS_SUCCESS;
            if (this.mViews.size() == 1) {
                this.mCachedEnd = Integers.STATUS_SUCCESS;
            }
            if (lp.mViewHolder.isRemoved() || lp.mViewHolder.isUpdated()) {
                this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
        }

        final void appendToSpan(View view) {
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.mSpan = this;
            this.mViews.add(view);
            this.mCachedEnd = Integers.STATUS_SUCCESS;
            if (this.mViews.size() == 1) {
                this.mCachedStart = Integers.STATUS_SUCCESS;
            }
            if (lp.mViewHolder.isRemoved() || lp.mViewHolder.isUpdated()) {
                this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class LazySpanLookup {
        int[] mData;
        List<FullSpanItem> mFullSpanItems;

        LazySpanLookup() {
        }

        final int forceInvalidateAfter(int position) {
            if (this.mFullSpanItems != null) {
                for (int i = this.mFullSpanItems.size() - 1; i >= 0; i--) {
                    if (this.mFullSpanItems.get(i).mPosition >= position) {
                        this.mFullSpanItems.remove(i);
                    }
                }
            }
            return invalidateAfter(position);
        }

        /* JADX WARN: Removed duplicated region for block: B:24:0x0045  */
        /* JADX WARN: Removed duplicated region for block: B:26:0x0056  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        final int invalidateAfter(int r6) {
            /*
                r5 = this;
                r2 = -1
                int[] r1 = r5.mData
                if (r1 != 0) goto L7
                r1 = r2
            L6:
                return r1
            L7:
                int[] r1 = r5.mData
                int r1 = r1.length
                if (r6 < r1) goto Le
                r1 = r2
                goto L6
            Le:
                java.util.List<android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem> r1 = r5.mFullSpanItems
                if (r1 == 0) goto L54
                android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem r1 = r5.getFullSpanItem(r6)
                if (r1 == 0) goto L1d
                java.util.List<android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem> r3 = r5.mFullSpanItems
                r3.remove(r1)
            L1d:
                java.util.List<android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem> r1 = r5.mFullSpanItems
                int r4 = r1.size()
                r3 = 0
            L24:
                if (r3 >= r4) goto L60
                java.util.List<android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem> r1 = r5.mFullSpanItems
                java.lang.Object r1 = r1.get(r3)
                android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem r1 = (android.support.v7.widget.StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem) r1
                int r1 = r1.mPosition
                if (r1 < r6) goto L51
            L32:
                if (r3 == r2) goto L54
                java.util.List<android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem> r1 = r5.mFullSpanItems
                java.lang.Object r1 = r1.get(r3)
                android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem r1 = (android.support.v7.widget.StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem) r1
                java.util.List<android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem> r4 = r5.mFullSpanItems
                r4.remove(r3)
                int r0 = r1.mPosition
            L43:
                if (r0 != r2) goto L56
                int[] r1 = r5.mData
                int[] r3 = r5.mData
                int r3 = r3.length
                java.util.Arrays.fill(r1, r6, r3, r2)
                int[] r1 = r5.mData
                int r1 = r1.length
                goto L6
            L51:
                int r3 = r3 + 1
                goto L24
            L54:
                r0 = r2
                goto L43
            L56:
                int[] r1 = r5.mData
                int r3 = r0 + 1
                java.util.Arrays.fill(r1, r6, r3, r2)
                int r1 = r0 + 1
                goto L6
            L60:
                r3 = r2
                goto L32
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.StaggeredGridLayoutManager.LazySpanLookup.invalidateAfter(int):int");
        }

        final void ensureSize(int position) {
            if (this.mData == null) {
                this.mData = new int[Math.max(position, 10) + 1];
                Arrays.fill(this.mData, -1);
            } else if (position >= this.mData.length) {
                int[] old = this.mData;
                int length = this.mData.length;
                while (length <= position) {
                    length *= 2;
                }
                this.mData = new int[length];
                System.arraycopy(old, 0, this.mData, 0, old.length);
                Arrays.fill(this.mData, old.length, this.mData.length, -1);
            }
        }

        final void clear() {
            if (this.mData != null) {
                Arrays.fill(this.mData, -1);
            }
            this.mFullSpanItems = null;
        }

        final void offsetForRemoval(int positionStart, int itemCount) {
            if (this.mData != null && positionStart < this.mData.length) {
                ensureSize(positionStart + itemCount);
                System.arraycopy(this.mData, positionStart + itemCount, this.mData, positionStart, (this.mData.length - positionStart) - itemCount);
                Arrays.fill(this.mData, this.mData.length - itemCount, this.mData.length, -1);
                if (this.mFullSpanItems == null) {
                    return;
                }
                int i = positionStart + itemCount;
                for (int size = this.mFullSpanItems.size() - 1; size >= 0; size--) {
                    FullSpanItem fullSpanItem = this.mFullSpanItems.get(size);
                    if (fullSpanItem.mPosition >= positionStart) {
                        if (fullSpanItem.mPosition < i) {
                            this.mFullSpanItems.remove(size);
                        } else {
                            fullSpanItem.mPosition -= itemCount;
                        }
                    }
                }
            }
        }

        final void offsetForAddition(int positionStart, int itemCount) {
            if (this.mData != null && positionStart < this.mData.length) {
                ensureSize(positionStart + itemCount);
                System.arraycopy(this.mData, positionStart, this.mData, positionStart + itemCount, (this.mData.length - positionStart) - itemCount);
                Arrays.fill(this.mData, positionStart, positionStart + itemCount, -1);
                if (this.mFullSpanItems == null) {
                    return;
                }
                for (int size = this.mFullSpanItems.size() - 1; size >= 0; size--) {
                    FullSpanItem fullSpanItem = this.mFullSpanItems.get(size);
                    if (fullSpanItem.mPosition >= positionStart) {
                        fullSpanItem.mPosition += itemCount;
                    }
                }
            }
        }

        public final void addFullSpanItem(FullSpanItem fullSpanItem) {
            if (this.mFullSpanItems == null) {
                this.mFullSpanItems = new ArrayList();
            }
            int size = this.mFullSpanItems.size();
            for (int i = 0; i < size; i++) {
                FullSpanItem other = this.mFullSpanItems.get(i);
                if (other.mPosition == fullSpanItem.mPosition) {
                    this.mFullSpanItems.remove(i);
                }
                if (other.mPosition >= fullSpanItem.mPosition) {
                    this.mFullSpanItems.add(i, fullSpanItem);
                    return;
                }
            }
            this.mFullSpanItems.add(fullSpanItem);
        }

        public final FullSpanItem getFullSpanItem(int position) {
            if (this.mFullSpanItems == null) {
                return null;
            }
            for (int i = this.mFullSpanItems.size() - 1; i >= 0; i--) {
                FullSpanItem fsi = this.mFullSpanItems.get(i);
                if (fsi.mPosition == position) {
                    return fsi;
                }
            }
            return null;
        }

        public final FullSpanItem getFirstFullSpanItemInRange$7b524a3(int minPos, int maxPos, int gapDir) {
            if (this.mFullSpanItems == null) {
                return null;
            }
            int limit = this.mFullSpanItems.size();
            for (int i = 0; i < limit; i++) {
                FullSpanItem fsi = this.mFullSpanItems.get(i);
                if (fsi.mPosition >= maxPos) {
                    return null;
                }
                if (fsi.mPosition >= minPos && (gapDir == 0 || fsi.mGapDir == gapDir || fsi.mHasUnwantedGapAfter)) {
                    return fsi;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public static class FullSpanItem implements Parcelable {
            public static final Parcelable.Creator<FullSpanItem> CREATOR = new Parcelable.Creator<FullSpanItem>() { // from class: android.support.v7.widget.StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem.1
                @Override // android.os.Parcelable.Creator
                public final /* bridge */ /* synthetic */ FullSpanItem[] newArray(int i) {
                    return new FullSpanItem[i];
                }

                @Override // android.os.Parcelable.Creator
                public final /* bridge */ /* synthetic */ FullSpanItem createFromParcel(Parcel parcel) {
                    return new FullSpanItem(parcel);
                }
            };
            int mGapDir;
            int[] mGapPerSpan;
            boolean mHasUnwantedGapAfter;
            int mPosition;

            public FullSpanItem(Parcel in) {
                this.mPosition = in.readInt();
                this.mGapDir = in.readInt();
                this.mHasUnwantedGapAfter = in.readInt() == 1;
                int spanCount = in.readInt();
                if (spanCount > 0) {
                    this.mGapPerSpan = new int[spanCount];
                    in.readIntArray(this.mGapPerSpan);
                }
            }

            public FullSpanItem() {
            }

            final int getGapForSpan(int spanIndex) {
                if (this.mGapPerSpan == null) {
                    return 0;
                }
                return this.mGapPerSpan[spanIndex];
            }

            @Override // android.os.Parcelable
            public int describeContents() {
                return 0;
            }

            @Override // android.os.Parcelable
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.mPosition);
                dest.writeInt(this.mGapDir);
                dest.writeInt(this.mHasUnwantedGapAfter ? 1 : 0);
                if (this.mGapPerSpan != null && this.mGapPerSpan.length > 0) {
                    dest.writeInt(this.mGapPerSpan.length);
                    dest.writeIntArray(this.mGapPerSpan);
                } else {
                    dest.writeInt(0);
                }
            }

            public String toString() {
                return "FullSpanItem{mPosition=" + this.mPosition + ", mGapDir=" + this.mGapDir + ", mHasUnwantedGapAfter=" + this.mHasUnwantedGapAfter + ", mGapPerSpan=" + Arrays.toString(this.mGapPerSpan) + '}';
            }
        }
    }

    /* loaded from: classes.dex */
    public static class SavedState implements Parcelable {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.support.v7.widget.StaggeredGridLayoutManager.SavedState.1
            @Override // android.os.Parcelable.Creator
            public final /* bridge */ /* synthetic */ SavedState[] newArray(int i) {
                return new SavedState[i];
            }

            @Override // android.os.Parcelable.Creator
            public final /* bridge */ /* synthetic */ SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }
        };
        boolean mAnchorLayoutFromEnd;
        int mAnchorPosition;
        List<LazySpanLookup.FullSpanItem> mFullSpanItems;
        boolean mLastLayoutRTL;
        boolean mReverseLayout;
        int[] mSpanLookup;
        int mSpanLookupSize;
        int[] mSpanOffsets;
        int mSpanOffsetsSize;
        int mVisibleAnchorPosition;

        public SavedState() {
        }

        SavedState(Parcel in) {
            this.mAnchorPosition = in.readInt();
            this.mVisibleAnchorPosition = in.readInt();
            this.mSpanOffsetsSize = in.readInt();
            if (this.mSpanOffsetsSize > 0) {
                this.mSpanOffsets = new int[this.mSpanOffsetsSize];
                in.readIntArray(this.mSpanOffsets);
            }
            this.mSpanLookupSize = in.readInt();
            if (this.mSpanLookupSize > 0) {
                this.mSpanLookup = new int[this.mSpanLookupSize];
                in.readIntArray(this.mSpanLookup);
            }
            this.mReverseLayout = in.readInt() == 1;
            this.mAnchorLayoutFromEnd = in.readInt() == 1;
            this.mLastLayoutRTL = in.readInt() == 1;
            this.mFullSpanItems = in.readArrayList(LazySpanLookup.FullSpanItem.class.getClassLoader());
        }

        public SavedState(SavedState other) {
            this.mSpanOffsetsSize = other.mSpanOffsetsSize;
            this.mAnchorPosition = other.mAnchorPosition;
            this.mVisibleAnchorPosition = other.mVisibleAnchorPosition;
            this.mSpanOffsets = other.mSpanOffsets;
            this.mSpanLookupSize = other.mSpanLookupSize;
            this.mSpanLookup = other.mSpanLookup;
            this.mReverseLayout = other.mReverseLayout;
            this.mAnchorLayoutFromEnd = other.mAnchorLayoutFromEnd;
            this.mLastLayoutRTL = other.mLastLayoutRTL;
            this.mFullSpanItems = other.mFullSpanItems;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mAnchorPosition);
            dest.writeInt(this.mVisibleAnchorPosition);
            dest.writeInt(this.mSpanOffsetsSize);
            if (this.mSpanOffsetsSize > 0) {
                dest.writeIntArray(this.mSpanOffsets);
            }
            dest.writeInt(this.mSpanLookupSize);
            if (this.mSpanLookupSize > 0) {
                dest.writeIntArray(this.mSpanLookup);
            }
            dest.writeInt(this.mReverseLayout ? 1 : 0);
            dest.writeInt(this.mAnchorLayoutFromEnd ? 1 : 0);
            dest.writeInt(this.mLastLayoutRTL ? 1 : 0);
            dest.writeList(this.mFullSpanItems);
        }
    }

    /* loaded from: classes.dex */
    class AnchorInfo {
        boolean mInvalidateOffsets;
        boolean mLayoutFromEnd;
        int mOffset;
        int mPosition;
        boolean mValid;

        public AnchorInfo() {
            reset();
        }

        final void reset() {
            this.mPosition = -1;
            this.mOffset = Integers.STATUS_SUCCESS;
            this.mLayoutFromEnd = false;
            this.mInvalidateOffsets = false;
            this.mValid = false;
        }
    }

    private boolean isLayoutRTL() {
        return ViewCompat.getLayoutDirection(this.mRecyclerView) == 1;
    }

    private void updateRemainingSpans(Span span, int layoutDir, int targetLine) {
        int deletedSize = span.mDeletedSize;
        if (layoutDir == -1) {
            if (span.getStartLine() + deletedSize <= targetLine) {
                this.mRemainingSpans.set(span.mIndex, false);
            }
        } else if (span.getEndLine() - deletedSize >= targetLine) {
            this.mRemainingSpans.set(span.mIndex, false);
        }
    }
}
