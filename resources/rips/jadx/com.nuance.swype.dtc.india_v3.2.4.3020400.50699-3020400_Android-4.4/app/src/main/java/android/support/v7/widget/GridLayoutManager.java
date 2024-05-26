package android.support.v7.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: classes.dex */
public class GridLayoutManager extends LinearLayoutManager {
    int[] mCachedBorders;
    final Rect mDecorInsets;
    boolean mPendingSpanCountChange;
    final SparseIntArray mPreLayoutSpanIndexCache;
    final SparseIntArray mPreLayoutSpanSizeCache;
    View[] mSet;
    int mSpanCount;
    SpanSizeLookup mSpanSizeLookup;

    public GridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mPendingSpanCountChange = false;
        this.mSpanCount = -1;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        RecyclerView.LayoutManager.Properties properties = getProperties(context, attrs, defStyleAttr, defStyleRes);
        setSpanCount(properties.spanCount);
    }

    public GridLayoutManager(Context context, int spanCount) {
        super(context);
        this.mPendingSpanCountChange = false;
        this.mSpanCount = -1;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        setSpanCount(spanCount);
    }

    public GridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.mPendingSpanCountChange = false;
        this.mSpanCount = -1;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        setSpanCount(spanCount);
    }

    @Override // android.support.v7.widget.LinearLayoutManager
    public final void setStackFromEnd(boolean stackFromEnd) {
        if (stackFromEnd) {
            throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
        }
        super.setStackFromEnd(false);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final int getRowCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (this.mOrientation == 0) {
            return this.mSpanCount;
        }
        if (state.getItemCount() <= 0) {
            return 0;
        }
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final int getColumnCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (this.mOrientation == 1) {
            return this.mSpanCount;
        }
        if (state.getItemCount() <= 0) {
            return 0;
        }
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View host, AccessibilityNodeInfoCompat info) {
        boolean z = false;
        ViewGroup.LayoutParams lp = host.getLayoutParams();
        if (!(lp instanceof LayoutParams)) {
            super.onInitializeAccessibilityNodeInfoForItem(host, info);
            return;
        }
        LayoutParams glp = (LayoutParams) lp;
        int spanGroupIndex = getSpanGroupIndex(recycler, state, glp.mViewHolder.getLayoutPosition());
        if (this.mOrientation == 0) {
            int i = glp.mSpanIndex;
            int i2 = glp.mSpanSize;
            if (this.mSpanCount > 1 && glp.mSpanSize == this.mSpanCount) {
                z = true;
            }
            info.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain$430787b1(i, i2, spanGroupIndex, 1, z));
            return;
        }
        int i3 = glp.mSpanIndex;
        int i4 = glp.mSpanSize;
        if (this.mSpanCount > 1 && glp.mSpanSize == this.mSpanCount) {
            z = true;
        }
        info.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain$430787b1(spanGroupIndex, 1, i3, i4, z));
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
    public final void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        this.mPendingSpanCountChange = false;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onItemsAdded$5927c743(int positionStart, int itemCount) {
        this.mSpanSizeLookup.mSpanIndexCache.clear();
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onItemsChanged$57043c5d() {
        this.mSpanSizeLookup.mSpanIndexCache.clear();
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onItemsRemoved$5927c743(int positionStart, int itemCount) {
        this.mSpanSizeLookup.mSpanIndexCache.clear();
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onItemsUpdated$783f8c5f$5927c743(int positionStart, int itemCount) {
        this.mSpanSizeLookup.mSpanIndexCache.clear();
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public final void onItemsMoved$342e6be0(int from, int to) {
        this.mSpanSizeLookup.mSpanIndexCache.clear();
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
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
    public final void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
        int width;
        int height;
        if (this.mCachedBorders == null) {
            super.setMeasuredDimension(childrenBounds, wSpec, hSpec);
        }
        int horizontalPadding = getPaddingLeft() + getPaddingRight();
        int verticalPadding = getPaddingTop() + getPaddingBottom();
        if (this.mOrientation == 1) {
            int usedHeight = childrenBounds.height() + verticalPadding;
            height = chooseSize(hSpec, usedHeight, ViewCompat.getMinimumHeight(this.mRecyclerView));
            width = chooseSize(wSpec, this.mCachedBorders[this.mCachedBorders.length - 1] + horizontalPadding, ViewCompat.getMinimumWidth(this.mRecyclerView));
        } else {
            int usedWidth = childrenBounds.width() + horizontalPadding;
            width = chooseSize(wSpec, usedWidth, ViewCompat.getMinimumWidth(this.mRecyclerView));
            height = chooseSize(hSpec, this.mCachedBorders[this.mCachedBorders.length - 1] + verticalPadding, ViewCompat.getMinimumHeight(this.mRecyclerView));
        }
        setMeasuredDimension(width, height);
    }

    private void calculateItemBorders(int totalSpace) {
        int i;
        int i2 = 0;
        int[] iArr = this.mCachedBorders;
        int i3 = this.mSpanCount;
        if (iArr == null || iArr.length != i3 + 1 || iArr[iArr.length - 1] != totalSpace) {
            iArr = new int[i3 + 1];
        }
        iArr[0] = 0;
        int i4 = totalSpace / i3;
        int i5 = totalSpace % i3;
        int i6 = 0;
        for (int i7 = 1; i7 <= i3; i7++) {
            i2 += i5;
            if (i2 <= 0 || i3 - i2 >= i5) {
                i = i4;
            } else {
                i = i4 + 1;
                i2 -= i3;
            }
            i6 += i;
            iArr[i7] = i6;
        }
        this.mCachedBorders = iArr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.widget.LinearLayoutManager
    public final void onAnchorReady(RecyclerView.Recycler recycler, RecyclerView.State state, LinearLayoutManager.AnchorInfo anchorInfo, int itemDirection) {
        super.onAnchorReady(recycler, state, anchorInfo, itemDirection);
        updateMeasurements();
        if (state.getItemCount() > 0 && !state.mInPreLayout) {
            boolean z = itemDirection == 1;
            int spanIndex = getSpanIndex(recycler, state, anchorInfo.mPosition);
            if (z) {
                while (spanIndex > 0 && anchorInfo.mPosition > 0) {
                    anchorInfo.mPosition--;
                    spanIndex = getSpanIndex(recycler, state, anchorInfo.mPosition);
                }
            } else {
                int itemCount = state.getItemCount() - 1;
                int i = anchorInfo.mPosition;
                int i2 = spanIndex;
                while (i < itemCount) {
                    int spanIndex2 = getSpanIndex(recycler, state, i + 1);
                    if (spanIndex2 <= i2) {
                        break;
                    }
                    i++;
                    i2 = spanIndex2;
                }
                anchorInfo.mPosition = i;
            }
        }
        ensureViewSet();
    }

    private void ensureViewSet() {
        if (this.mSet == null || this.mSet.length != this.mSpanCount) {
            this.mSet = new View[this.mSpanCount];
        }
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
    public final int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
    public final int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    @Override // android.support.v7.widget.LinearLayoutManager
    final View findReferenceChild(RecyclerView.Recycler recycler, RecyclerView.State state, int start, int end, int itemCount) {
        ensureLayoutState();
        View invalidMatch = null;
        View outOfBoundsMatch = null;
        int boundsStart = this.mOrientationHelper.getStartAfterPadding();
        int boundsEnd = this.mOrientationHelper.getEndAfterPadding();
        int diff = end > start ? 1 : -1;
        for (int i = start; i != end; i += diff) {
            View view = getChildAt(i);
            int position = getPosition(view);
            if (position >= 0 && position < itemCount && getSpanIndex(recycler, state, position) == 0) {
                if (((RecyclerView.LayoutParams) view.getLayoutParams()).mViewHolder.isRemoved()) {
                    if (invalidMatch == null) {
                        invalidMatch = view;
                    }
                } else {
                    if (this.mOrientationHelper.getDecoratedStart(view) < boundsEnd && this.mOrientationHelper.getDecoratedEnd(view) >= boundsStart) {
                        return view;
                    }
                    if (outOfBoundsMatch == null) {
                        outOfBoundsMatch = view;
                    }
                }
            }
        }
        return outOfBoundsMatch != null ? outOfBoundsMatch : invalidMatch;
    }

    /* JADX WARN: Code restructure failed: missing block: B:30:0x00e5, code lost:            r42.mFinished = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00ea, code lost:            return;     */
    @Override // android.support.v7.widget.LinearLayoutManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    final void layoutChunk(android.support.v7.widget.RecyclerView.Recycler r39, android.support.v7.widget.RecyclerView.State r40, android.support.v7.widget.LinearLayoutManager.LayoutState r41, android.support.v7.widget.LinearLayoutManager.LayoutChunkResult r42) {
        /*
            Method dump skipped, instructions count: 1049
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.GridLayoutManager.layoutChunk(android.support.v7.widget.RecyclerView$Recycler, android.support.v7.widget.RecyclerView$State, android.support.v7.widget.LinearLayoutManager$LayoutState, android.support.v7.widget.LinearLayoutManager$LayoutChunkResult):void");
    }

    private void measureChildWithDecorationsAndMargin(View child, int widthSpec, int heightSpec, boolean capBothSpecs, boolean alreadyMeasured) {
        boolean measure = true;
        calculateItemDecorationsForChild(child, this.mDecorInsets);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        if (capBothSpecs || this.mOrientation == 1) {
            widthSpec = updateSpecWithExtra(widthSpec, lp.leftMargin + this.mDecorInsets.left, lp.rightMargin + this.mDecorInsets.right);
        }
        if (capBothSpecs || this.mOrientation == 0) {
            heightSpec = updateSpecWithExtra(heightSpec, lp.topMargin + this.mDecorInsets.top, lp.bottomMargin + this.mDecorInsets.bottom);
        }
        if (!alreadyMeasured) {
            measure = shouldMeasureChild(child, widthSpec, heightSpec, lp);
        } else if (this.mMeasurementCacheEnabled && RecyclerView.LayoutManager.isMeasurementUpToDate(child.getMeasuredWidth(), widthSpec, lp.width) && RecyclerView.LayoutManager.isMeasurementUpToDate(child.getMeasuredHeight(), heightSpec, lp.height)) {
            measure = false;
        }
        if (measure) {
            child.measure(widthSpec, heightSpec);
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

    private void assignSpans$1d107c69(RecyclerView.Recycler recycler, RecyclerView.State state, int count, boolean layingOutInPrimaryDirection) {
        int start;
        int end;
        int diff;
        int span;
        int spanDiff;
        if (layingOutInPrimaryDirection) {
            start = 0;
            end = count;
            diff = 1;
        } else {
            start = count - 1;
            end = -1;
            diff = -1;
        }
        if (this.mOrientation == 1 && isLayoutRTL()) {
            span = this.mSpanCount - 1;
            spanDiff = -1;
        } else {
            span = 0;
            spanDiff = 1;
        }
        for (int i = start; i != end; i += diff) {
            View view = this.mSet[i];
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            params.mSpanSize = getSpanSize(recycler, state, getPosition(view));
            if (spanDiff != -1 || params.mSpanSize <= 1) {
                params.mSpanIndex = span;
            } else {
                params.mSpanIndex = span - (params.mSpanSize - 1);
            }
            span += params.mSpanSize * spanDiff;
        }
    }

    private void setSpanCount(int spanCount) {
        if (spanCount != this.mSpanCount) {
            this.mPendingSpanCountChange = true;
            if (spanCount <= 0) {
                throw new IllegalArgumentException("Span count should be at least 1. Provided " + spanCount);
            }
            this.mSpanCount = spanCount;
            this.mSpanSizeLookup.mSpanIndexCache.clear();
            requestLayout();
        }
    }

    /* loaded from: classes.dex */
    public static abstract class SpanSizeLookup {
        final SparseIntArray mSpanIndexCache = new SparseIntArray();
        private boolean mCacheSpanIndices = false;

        final int getCachedSpanIndex(int position, int spanCount) {
            if (!this.mCacheSpanIndices) {
                return getSpanIndex(position, spanCount);
            }
            int existing = this.mSpanIndexCache.get(position, -1);
            if (existing == -1) {
                int value = getSpanIndex(position, spanCount);
                this.mSpanIndexCache.put(position, value);
                return value;
            }
            return existing;
        }

        public int getSpanIndex(int position, int spanCount) {
            int prevKey;
            if (1 == spanCount) {
                return 0;
            }
            int span = 0;
            int startPos = 0;
            if (this.mCacheSpanIndices && this.mSpanIndexCache.size() > 0) {
                int size = this.mSpanIndexCache.size() - 1;
                int i = 0;
                while (i <= size) {
                    int i2 = (i + size) >>> 1;
                    if (this.mSpanIndexCache.keyAt(i2) < position) {
                        i = i2 + 1;
                    } else {
                        size = i2 - 1;
                    }
                }
                int i3 = i - 1;
                if (i3 >= 0 && i3 < this.mSpanIndexCache.size()) {
                    prevKey = this.mSpanIndexCache.keyAt(i3);
                } else {
                    prevKey = -1;
                }
                if (prevKey >= 0) {
                    span = this.mSpanIndexCache.get(prevKey) + 1;
                    startPos = prevKey + 1;
                }
            }
            for (int i4 = startPos; i4 < position; i4++) {
                span++;
                if (span == spanCount) {
                    span = 0;
                } else if (span > spanCount) {
                    span = 1;
                }
            }
            if (span + 1 > spanCount) {
                return 0;
            }
            return span;
        }

        public static int getSpanGroupIndex(int adapterPosition, int spanCount) {
            int span = 0;
            int group = 0;
            for (int i = 0; i < adapterPosition; i++) {
                span++;
                if (span == spanCount) {
                    span = 0;
                    group++;
                } else if (span > spanCount) {
                    span = 1;
                    group++;
                }
            }
            if (span + 1 > spanCount) {
                return group + 1;
            }
            return group;
        }
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
    public final View onFocusSearchFailed(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int start;
        int inc;
        int limit;
        View prevFocusedChild = findContainingItemView(focused);
        if (prevFocusedChild == null) {
            return null;
        }
        LayoutParams lp = (LayoutParams) prevFocusedChild.getLayoutParams();
        int prevSpanStart = lp.mSpanIndex;
        int prevSpanEnd = lp.mSpanIndex + lp.mSpanSize;
        if (super.onFocusSearchFailed(focused, focusDirection, recycler, state) == null) {
            return null;
        }
        if ((convertFocusDirectionToLayoutDirection(focusDirection) == 1) != this.mShouldReverseLayout) {
            start = getChildCount() - 1;
            inc = -1;
            limit = -1;
        } else {
            start = 0;
            inc = 1;
            limit = getChildCount();
        }
        boolean preferLastSpan = this.mOrientation == 1 && isLayoutRTL();
        View weakCandidate = null;
        int weakCandidateSpanIndex = -1;
        int weakCandidateOverlap = 0;
        for (int i = start; i != limit; i += inc) {
            View candidate = getChildAt(i);
            if (candidate == prevFocusedChild) {
                break;
            }
            if (candidate.isFocusable()) {
                LayoutParams candidateLp = (LayoutParams) candidate.getLayoutParams();
                int candidateStart = candidateLp.mSpanIndex;
                int candidateEnd = candidateLp.mSpanIndex + candidateLp.mSpanSize;
                if (candidateStart != prevSpanStart || candidateEnd != prevSpanEnd) {
                    boolean assignAsWeek = false;
                    if (weakCandidate == null) {
                        assignAsWeek = true;
                    } else {
                        int maxStart = Math.max(candidateStart, prevSpanStart);
                        int overlap = Math.min(candidateEnd, prevSpanEnd) - maxStart;
                        if (overlap > weakCandidateOverlap) {
                            assignAsWeek = true;
                        } else if (overlap == weakCandidateOverlap) {
                            if (preferLastSpan == (candidateStart > weakCandidateSpanIndex)) {
                                assignAsWeek = true;
                            }
                        }
                    }
                    if (assignAsWeek) {
                        weakCandidate = candidate;
                        weakCandidateSpanIndex = candidateLp.mSpanIndex;
                        weakCandidateOverlap = Math.min(candidateEnd, prevSpanEnd) - Math.max(candidateStart, prevSpanStart);
                    }
                } else {
                    return candidate;
                }
            }
        }
        return weakCandidate;
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
    public final boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null && !this.mPendingSpanCountChange;
    }

    /* loaded from: classes.dex */
    public static final class DefaultSpanSizeLookup extends SpanSizeLookup {
        @Override // android.support.v7.widget.GridLayoutManager.SpanSizeLookup
        public final int getSpanIndex(int position, int spanCount) {
            return position % spanCount;
        }
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends RecyclerView.LayoutParams {
        int mSpanIndex;
        int mSpanSize;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
    public final void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.mInPreLayout) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                LayoutParams layoutParams = (LayoutParams) getChildAt(i).getLayoutParams();
                int layoutPosition = layoutParams.mViewHolder.getLayoutPosition();
                this.mPreLayoutSpanSizeCache.put(layoutPosition, layoutParams.mSpanSize);
                this.mPreLayoutSpanIndexCache.put(layoutPosition, layoutParams.mSpanIndex);
            }
        }
        super.onLayoutChildren(recycler, state);
        this.mPreLayoutSpanSizeCache.clear();
        this.mPreLayoutSpanIndexCache.clear();
    }

    private void updateMeasurements() {
        int totalSpace;
        if (this.mOrientation == 1) {
            totalSpace = (this.mWidth - getPaddingRight()) - getPaddingLeft();
        } else {
            totalSpace = (this.mHeight - getPaddingBottom()) - getPaddingTop();
        }
        calculateItemBorders(totalSpace);
    }

    private int getSpanGroupIndex(RecyclerView.Recycler recycler, RecyclerView.State state, int viewPosition) {
        if (!state.mInPreLayout) {
            return SpanSizeLookup.getSpanGroupIndex(viewPosition, this.mSpanCount);
        }
        int adapterPosition = recycler.convertPreLayoutPositionToPostLayout(viewPosition);
        if (adapterPosition == -1) {
            Log.w("GridLayoutManager", "Cannot find span size for pre layout position. " + viewPosition);
            return 0;
        }
        return SpanSizeLookup.getSpanGroupIndex(adapterPosition, this.mSpanCount);
    }

    private int getSpanIndex(RecyclerView.Recycler recycler, RecyclerView.State state, int pos) {
        if (!state.mInPreLayout) {
            return this.mSpanSizeLookup.getCachedSpanIndex(pos, this.mSpanCount);
        }
        int cached = this.mPreLayoutSpanIndexCache.get(pos, -1);
        if (cached == -1) {
            int adapterPosition = recycler.convertPreLayoutPositionToPostLayout(pos);
            if (adapterPosition == -1) {
                Log.w("GridLayoutManager", "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + pos);
                return 0;
            }
            return this.mSpanSizeLookup.getCachedSpanIndex(adapterPosition, this.mSpanCount);
        }
        return cached;
    }

    private int getSpanSize(RecyclerView.Recycler recycler, RecyclerView.State state, int pos) {
        if (!state.mInPreLayout) {
            return 1;
        }
        int cached = this.mPreLayoutSpanSizeCache.get(pos, -1);
        if (cached == -1) {
            if (recycler.convertPreLayoutPositionToPostLayout(pos) != -1) {
                return 1;
            }
            Log.w("GridLayoutManager", "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + pos);
            return 1;
        }
        return cached;
    }
}
