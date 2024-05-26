package com.nuance.swype.input.settings;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import com.nuance.swype.input.R;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/* loaded from: classes.dex */
public class HorizontalListView extends AdapterView<ListAdapter> {
    private static final String BUNDLE_ID_CURRENT_X = "BUNDLE_ID_CURRENT_X";
    private static final String BUNDLE_ID_PARENT_STATE = "BUNDLE_ID_PARENT_STATE";
    private static final long FAKE_FRAME_TIME = 10;
    private static final int INSERT_AT_END_OF_LIST = -1;
    private static final int INSERT_AT_START_OF_LIST = 0;
    private static boolean isRTL = false;
    protected ListAdapter mAdapter;
    private DataSetObserver mAdapterDataObserver;
    private boolean mBlockTouchAction;
    private OnScrollStateChangedListener.ScrollState mCurrentScrollState;
    protected int mCurrentX;
    private int mCurrentlySelectedAdapterIndex;
    private boolean mDataChanged;
    private Runnable mDelayedLayout;
    private int mDisplayOffset;
    private Drawable mDivider;
    private int mDividerWidth;
    private EdgeEffectCompat mEdgeGlowLeft;
    private EdgeEffectCompat mEdgeGlowRight;
    protected Scroller mFlingTracker;
    private GestureDetector mGestureDetector;
    private int mHeightMeasureSpec;
    private boolean mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent;
    private int mLeftViewAdapterIndex;
    private int mMaxX;
    protected int mNextX;
    private View.OnClickListener mOnClickListener;
    private Rect mRect;
    private List<Queue<View>> mRemovedViewsCache;
    private Integer mRestoreX;
    private int mRightViewAdapterIndex;
    private View mViewBeingTouched;

    /* loaded from: classes.dex */
    public interface OnScrollStateChangedListener {

        /* loaded from: classes.dex */
        public enum ScrollState {
            SCROLL_STATE_IDLE,
            SCROLL_STATE_TOUCH_SCROLL,
            SCROLL_STATE_FLING
        }
    }

    public HorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFlingTracker = new Scroller(getContext());
        this.mRemovedViewsCache = new ArrayList();
        this.mDataChanged = false;
        this.mRect = new Rect();
        this.mViewBeingTouched = null;
        this.mDividerWidth = 0;
        this.mDivider = null;
        this.mRestoreX = null;
        this.mMaxX = Integer.MAX_VALUE;
        this.mCurrentScrollState = OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE;
        this.mBlockTouchAction = false;
        this.mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent = false;
        this.mAdapterDataObserver = new DataSetObserver() { // from class: com.nuance.swype.input.settings.HorizontalListView.2
            @Override // android.database.DataSetObserver
            public void onChanged() {
                HorizontalListView.this.mDataChanged = true;
                HorizontalListView.this.unpressTouchedChild();
                HorizontalListView.this.invalidate();
                HorizontalListView.this.requestLayout();
            }

            @Override // android.database.DataSetObserver
            public void onInvalidated() {
                HorizontalListView.this.unpressTouchedChild();
                HorizontalListView.this.reset();
                HorizontalListView.this.invalidate();
                HorizontalListView.this.requestLayout();
            }
        };
        this.mDelayedLayout = new Runnable() { // from class: com.nuance.swype.input.settings.HorizontalListView.3
            @Override // java.lang.Runnable
            public void run() {
                HorizontalListView.this.requestLayout();
            }
        };
        this.mEdgeGlowLeft = new EdgeEffectCompat(context);
        this.mEdgeGlowRight = new EdgeEffectCompat(context);
        GestureListener gestureListener = new GestureListener();
        this.mGestureDetector = new GestureDetector(context, gestureListener);
        this.mGestureDetector.setIsLongpressEnabled(false);
        bindGestureDetector();
        initView();
        retrieveXmlConfiguration(context, attrs);
        setHorizontalFadingEdgeEnabled(true);
        setVerticalScrollBarEnabled(false);
        setDividerWidth(getResources().getDimensionPixelSize(R.dimen.horizontal_division_width));
        setWillNotDraw(false);
        setIAPBackgroundColor();
    }

    private void setIAPBackgroundColor() {
        setBackgroundColor(ThemesCategory.theme_color);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void bindGestureDetector() {
        View.OnTouchListener gestureListenerHandler = new View.OnTouchListener() { // from class: com.nuance.swype.input.settings.HorizontalListView.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                return HorizontalListView.this.mGestureDetector.onTouchEvent(event);
            }
        };
        setOnTouchListener(gestureListenerHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestParentListViewToNotInterceptTouchEvents(Boolean disallowIntercept) {
        if (this.mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent != disallowIntercept.booleanValue()) {
            for (View view = this; view.getParent() instanceof View; view = (View) view.getParent()) {
                if ((view.getParent() instanceof ListView) || (view.getParent() instanceof ScrollView)) {
                    view.getParent().requestDisallowInterceptTouchEvent(disallowIntercept.booleanValue());
                    this.mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent = disallowIntercept.booleanValue();
                    return;
                }
            }
        }
    }

    private void retrieveXmlConfiguration(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalListView);
            Drawable d = a.getDrawable(R.styleable.HorizontalListView_android_divider);
            if (d != null) {
                setDivider(d);
            }
            int dividerWidth = a.getDimensionPixelSize(R.styleable.HorizontalListView_dividerWidth, 0);
            if (dividerWidth != 0) {
                setDividerWidth(dividerWidth);
            }
            a.recycle();
        }
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_ID_PARENT_STATE, super.onSaveInstanceState());
        bundle.putInt(BUNDLE_ID_CURRENT_X, this.mCurrentX);
        return bundle;
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.mRestoreX = Integer.valueOf(bundle.getInt(BUNDLE_ID_CURRENT_X));
            super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_ID_PARENT_STATE));
        }
    }

    public void setDivider(Drawable divider) {
        this.mDivider = divider;
        if (divider != null) {
            setDividerWidth(divider.getIntrinsicWidth());
        } else {
            setDividerWidth(0);
        }
    }

    public void setDividerWidth(int width) {
        if (isRTL) {
            this.mDividerWidth = 0;
        } else {
            this.mDividerWidth = width;
        }
        requestLayout();
        invalidate();
    }

    private void initView() {
        this.mLeftViewAdapterIndex = -1;
        this.mRightViewAdapterIndex = -1;
        this.mDisplayOffset = 0;
        this.mCurrentX = 0;
        this.mNextX = 0;
        this.mMaxX = Integer.MAX_VALUE;
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reset() {
        initView();
        removeAllViewsInLayout();
        requestLayout();
    }

    @Override // android.widget.AdapterView
    public void setSelection(int position) {
        this.mCurrentlySelectedAdapterIndex = position;
    }

    @Override // android.widget.AdapterView
    public View getSelectedView() {
        return getChild(this.mCurrentlySelectedAdapterIndex);
    }

    public static void setHorizontalRTL(boolean rtl) {
        isRTL = rtl;
    }

    @Override // android.widget.AdapterView
    public void setAdapter(ListAdapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mAdapterDataObserver);
        }
        if (adapter != null) {
            this.mAdapter = adapter;
            this.mAdapter.registerDataSetObserver(this.mAdapterDataObserver);
            initializeRecycledViewCache(this.mAdapter.getViewTypeCount());
        }
        reset();
    }

    @Override // android.widget.AdapterView
    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    private void initializeRecycledViewCache(int viewTypeCount) {
        this.mRemovedViewsCache.clear();
        for (int i = 0; i < viewTypeCount; i++) {
            this.mRemovedViewsCache.add(new LinkedList());
        }
    }

    private View getRecycledView(int adapterIndex) {
        int itemViewType = this.mAdapter.getItemViewType(adapterIndex);
        if (isItemViewTypeValid(itemViewType)) {
            return this.mRemovedViewsCache.get(itemViewType).poll();
        }
        return null;
    }

    private void recycleView(int adapterIndex, View view) {
        int itemViewType = this.mAdapter.getItemViewType(adapterIndex);
        if (isItemViewTypeValid(itemViewType)) {
            this.mRemovedViewsCache.get(itemViewType).offer(view);
        }
    }

    private boolean isItemViewTypeValid(int itemViewType) {
        return itemViewType < this.mRemovedViewsCache.size();
    }

    private void addAndMeasureChild(View child, int viewPos) {
        ViewGroup.LayoutParams params = getLayoutParams(child);
        addViewInLayout(child, viewPos, params, true);
        measureChild(child);
    }

    private void measureChild(View child) {
        int childWidthSpec;
        ViewGroup.LayoutParams childLayoutParams = getLayoutParams(child);
        if (childLayoutParams.width > 0) {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(childLayoutParams.width, 1073741824);
        } else {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        }
        child.measure(childWidthSpec, 0);
        this.mHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), 1073741824);
        child.measure(childWidthSpec, this.mHeightMeasureSpec);
    }

    private ViewGroup.LayoutParams getLayoutParams(View child) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (layoutParams == null) {
            return new ViewGroup.LayoutParams(-2, -1);
        }
        return layoutParams;
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    @SuppressLint({"WrongCall"})
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mAdapter != null) {
            invalidate();
            if (this.mDataChanged) {
                int oldCurrentX = this.mCurrentX;
                initView();
                removeAllViewsInLayout();
                this.mNextX = oldCurrentX;
                this.mDataChanged = false;
            }
            if (this.mRestoreX != null) {
                this.mNextX = this.mRestoreX.intValue();
                this.mRestoreX = null;
            }
            if (this.mFlingTracker.computeScrollOffset()) {
                this.mNextX = this.mFlingTracker.getCurrX();
            }
            if (this.mNextX < 0) {
                this.mNextX = 0;
                if (this.mEdgeGlowLeft.isFinished()) {
                    this.mEdgeGlowLeft.onAbsorb((int) determineFlingAbsorbVelocity());
                }
                this.mFlingTracker.forceFinished(true);
                setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
            } else if (this.mNextX > this.mMaxX) {
                this.mNextX = this.mMaxX;
                if (this.mEdgeGlowRight.isFinished()) {
                    this.mEdgeGlowRight.onAbsorb((int) determineFlingAbsorbVelocity());
                }
                this.mFlingTracker.forceFinished(true);
                setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
            }
            int dx = this.mCurrentX - this.mNextX;
            removeNonVisibleChildren(dx);
            fillList(dx);
            positionChildren(dx);
            this.mCurrentX = this.mNextX;
            if (determineMaxX()) {
                onLayout(changed, left, top, right, bottom);
            } else {
                if (this.mFlingTracker.isFinished()) {
                    if (this.mCurrentScrollState == OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING) {
                        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
                        return;
                    }
                    return;
                }
                postDelayed(this.mDelayedLayout, FAKE_FRAME_TIME);
            }
        }
    }

    @Override // android.view.View
    protected float getLeftFadingEdgeStrength() {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
        if (this.mCurrentX == 0) {
            return 0.0f;
        }
        if (this.mCurrentX < horizontalFadingEdgeLength) {
            return this.mCurrentX / horizontalFadingEdgeLength;
        }
        return 1.0f;
    }

    @Override // android.view.View
    protected float getRightFadingEdgeStrength() {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
        if (this.mCurrentX == this.mMaxX) {
            return 0.0f;
        }
        if (this.mMaxX - this.mCurrentX < horizontalFadingEdgeLength) {
            return (this.mMaxX - this.mCurrentX) / horizontalFadingEdgeLength;
        }
        return 1.0f;
    }

    private float determineFlingAbsorbVelocity() {
        return IceCreamSandwichPlus.getCurrVelocity(this.mFlingTracker);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mHeightMeasureSpec > heightMeasureSpec) {
            heightMeasureSpec = this.mHeightMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean determineMaxX() {
        View rightView;
        if (!isLastItemInAdapter(this.mRightViewAdapterIndex) || (rightView = getRightmostChild()) == null) {
            return false;
        }
        int oldMaxX = this.mMaxX;
        this.mMaxX = (this.mCurrentX + (rightView.getRight() - getPaddingLeft())) - getRenderWidth();
        if (this.mMaxX < 0) {
            this.mMaxX = 0;
        }
        return this.mMaxX != oldMaxX;
    }

    private void fillList(int dx) {
        int edge = 0;
        View child = getRightmostChild();
        if (child != null) {
            edge = child.getRight();
        }
        fillListRight(edge, dx);
        int edge2 = 0;
        View child2 = getLeftmostChild();
        if (child2 != null) {
            edge2 = child2.getLeft();
        }
        fillListLeft(edge2, dx);
    }

    private void removeNonVisibleChildren(int dx) {
        View child = getLeftmostChild();
        while (child != null && child.getRight() + dx <= 0) {
            this.mDisplayOffset = (isLastItemInAdapter(this.mLeftViewAdapterIndex) ? child.getMeasuredWidth() : this.mDividerWidth + child.getMeasuredWidth()) + this.mDisplayOffset;
            recycleView(this.mLeftViewAdapterIndex, child);
            removeViewInLayout(child);
            this.mLeftViewAdapterIndex++;
            child = getLeftmostChild();
        }
        while (true) {
            View child2 = getRightmostChild();
            if (child2 != null && child2.getLeft() + dx >= getWidth()) {
                recycleView(this.mRightViewAdapterIndex, child2);
                removeViewInLayout(child2);
                this.mRightViewAdapterIndex--;
            } else {
                return;
            }
        }
    }

    private void fillListRight(int rightEdge, int dx) {
        while (rightEdge + dx + this.mDividerWidth < getWidth() && this.mRightViewAdapterIndex + 1 < this.mAdapter.getCount()) {
            this.mRightViewAdapterIndex++;
            if (this.mLeftViewAdapterIndex < 0) {
                this.mLeftViewAdapterIndex = this.mRightViewAdapterIndex;
            }
            View child = this.mAdapter.getView(this.mRightViewAdapterIndex, getRecycledView(this.mRightViewAdapterIndex), this);
            addAndMeasureChild(child, -1);
            rightEdge += (this.mRightViewAdapterIndex == 0 ? 0 : this.mDividerWidth) + child.getMeasuredWidth();
        }
    }

    private void fillListLeft(int leftEdge, int dx) {
        while ((leftEdge + dx) - this.mDividerWidth > 0 && this.mLeftViewAdapterIndex > 0) {
            this.mLeftViewAdapterIndex--;
            View child = this.mAdapter.getView(this.mLeftViewAdapterIndex, getRecycledView(this.mLeftViewAdapterIndex), this);
            addAndMeasureChild(child, 0);
            leftEdge -= this.mLeftViewAdapterIndex == 0 ? child.getMeasuredWidth() : this.mDividerWidth + child.getMeasuredWidth();
            this.mDisplayOffset -= leftEdge + dx == 0 ? child.getMeasuredWidth() : this.mDividerWidth + child.getMeasuredWidth();
        }
    }

    private void positionChildren(int dx) {
        int childCount = getChildCount();
        if (childCount > 0) {
            this.mDisplayOffset += dx;
            int leftOffset = this.mDisplayOffset;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                int left = leftOffset + getPaddingLeft();
                int top = getPaddingTop();
                int right = left + child.getMeasuredWidth();
                int bottom = top + child.getMeasuredHeight();
                child.layout(left, top, right, bottom);
                leftOffset += child.getMeasuredWidth() + this.mDividerWidth;
            }
        }
    }

    private View getLeftmostChild() {
        return getChildAt(0);
    }

    private View getRightmostChild() {
        return getChildAt(getChildCount() - 1);
    }

    private View getChild(int adapterIndex) {
        if (adapterIndex < this.mLeftViewAdapterIndex || adapterIndex > this.mRightViewAdapterIndex) {
            return null;
        }
        return getChildAt(adapterIndex - this.mLeftViewAdapterIndex);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getChildIndex(int x, int y) {
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            getChildAt(index).getHitRect(this.mRect);
            if (this.mRect.contains(x, y)) {
                return index;
            }
        }
        return -1;
    }

    private boolean isLastItemInAdapter(int index) {
        return index == this.mAdapter.getCount() + (-1);
    }

    private int getRenderHeight() {
        return (getHeight() - getPaddingTop()) - getPaddingBottom();
    }

    private int getRenderWidth() {
        return (getWidth() - getPaddingLeft()) - getPaddingRight();
    }

    public void scrollTo(int x) {
        this.mFlingTracker.startScroll(this.mNextX, 0, x - this.mNextX, 0);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING);
        requestLayout();
    }

    @Override // android.widget.AdapterView
    public int getFirstVisiblePosition() {
        return this.mLeftViewAdapterIndex;
    }

    @Override // android.widget.AdapterView
    public int getLastVisiblePosition() {
        return this.mRightViewAdapterIndex;
    }

    private void drawEdgeGlow(Canvas canvas) {
        if (this.mEdgeGlowLeft != null && !this.mEdgeGlowLeft.isFinished() && isEdgeGlowEnabled()) {
            int restoreCount = canvas.save();
            int height = getHeight();
            canvas.rotate(-90.0f, 0.0f, 0.0f);
            canvas.translate((-height) + getPaddingBottom(), 0.0f);
            this.mEdgeGlowLeft.setSize(getRenderHeight(), getRenderWidth());
            if (this.mEdgeGlowLeft.draw(canvas)) {
                invalidate();
            }
            canvas.restoreToCount(restoreCount);
            return;
        }
        if (this.mEdgeGlowRight != null && !this.mEdgeGlowRight.isFinished() && isEdgeGlowEnabled()) {
            int restoreCount2 = canvas.save();
            int width = getWidth();
            canvas.rotate(90.0f, 0.0f, 0.0f);
            canvas.translate(getPaddingTop(), -width);
            this.mEdgeGlowRight.setSize(getRenderHeight(), getRenderWidth());
            if (this.mEdgeGlowRight.draw(canvas)) {
                invalidate();
            }
            canvas.restoreToCount(restoreCount2);
        }
    }

    private void drawDividers(Canvas canvas) {
        int count = getChildCount();
        Rect bounds = this.mRect;
        this.mRect.top = getPaddingTop();
        this.mRect.bottom = this.mRect.top + getRenderHeight();
        for (int i = 0; i < count; i++) {
            if (i != count - 1 || !isLastItemInAdapter(this.mRightViewAdapterIndex)) {
                View child = getChildAt(i);
                bounds.left = child.getRight();
                bounds.right = child.getRight() + this.mDividerWidth;
                if (bounds.left < getPaddingLeft()) {
                    bounds.left = getPaddingLeft();
                }
                if (bounds.right > getWidth() - getPaddingRight()) {
                    bounds.right = getWidth() - getPaddingRight();
                }
                drawDivider(canvas, bounds);
                if (i == 0 && child.getLeft() > getPaddingLeft()) {
                    bounds.left = getPaddingLeft();
                    bounds.right = child.getLeft();
                    drawDivider(canvas, bounds);
                }
            }
        }
    }

    private void drawDivider(Canvas canvas, Rect bounds) {
        if (this.mDivider != null) {
            this.mDivider.setBounds(bounds);
            this.mDivider.draw(canvas);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDividers(canvas);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawEdgeGlow(canvas);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchSetPressed(boolean pressed) {
    }

    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        this.mFlingTracker.fling(this.mNextX, 0, (int) (-velocityX), 0, 0, this.mMaxX, 0, 0);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING);
        requestLayout();
        return true;
    }

    protected boolean onDown(MotionEvent e) {
        int index;
        if (e.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        this.mBlockTouchAction = !this.mFlingTracker.isFinished();
        this.mFlingTracker.forceFinished(true);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
        unpressTouchedChild();
        if (!this.mBlockTouchAction && (index = getChildIndex((int) e.getX(), (int) e.getY())) >= 0) {
            this.mViewBeingTouched = getChildAt(index);
            if (this.mViewBeingTouched != null) {
                this.mViewBeingTouched.setPressed(true);
                refreshDrawableState();
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unpressTouchedChild() {
        if (this.mViewBeingTouched != null) {
            this.mViewBeingTouched.setPressed(false);
            refreshDrawableState();
            this.mViewBeingTouched = null;
        }
    }

    /* loaded from: classes.dex */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private GestureListener() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            return HorizontalListView.this.onDown(e);
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityY) <= Math.abs(velocityX)) {
                return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
            }
            HorizontalListView.this.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceY) <= Math.abs(distanceX)) {
                HorizontalListView.this.requestParentListViewToNotInterceptTouchEvents(true);
                HorizontalListView.this.setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_TOUCH_SCROLL);
                HorizontalListView.this.unpressTouchedChild();
                HorizontalListView.this.mNextX += (int) distanceX;
                HorizontalListView.this.updateOverscrollAnimation(Math.round(distanceX));
                HorizontalListView.this.requestLayout();
                return true;
            }
            HorizontalListView.this.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onSingleTapConfirmed(MotionEvent e) {
            HorizontalListView.this.unpressTouchedChild();
            AdapterView.OnItemClickListener onItemClickListener = HorizontalListView.this.getOnItemClickListener();
            int index = HorizontalListView.this.getChildIndex((int) e.getX(), (int) e.getY());
            if (index >= 0 && !HorizontalListView.this.mBlockTouchAction) {
                View child = HorizontalListView.this.getChildAt(index);
                int adapterIndex = HorizontalListView.this.mLeftViewAdapterIndex + index;
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(HorizontalListView.this, child, adapterIndex, HorizontalListView.this.mAdapter.getItemId(adapterIndex));
                    return true;
                }
            }
            if (HorizontalListView.this.mOnClickListener != null && !HorizontalListView.this.mBlockTouchAction) {
                HorizontalListView.this.mOnClickListener.onClick(HorizontalListView.this);
            }
            return false;
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 1) {
            if (this.mFlingTracker == null || this.mFlingTracker.isFinished()) {
                setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
            }
            requestParentListViewToNotInterceptTouchEvents(false);
            releaseEdgeGlow();
        } else if (event.getAction() == 3) {
            unpressTouchedChild();
            releaseEdgeGlow();
            requestParentListViewToNotInterceptTouchEvents(false);
        }
        return super.onTouchEvent(event);
    }

    private void releaseEdgeGlow() {
        if (this.mEdgeGlowLeft != null) {
            this.mEdgeGlowLeft.onRelease();
        }
        if (this.mEdgeGlowRight != null) {
            this.mEdgeGlowRight.onRelease();
        }
    }

    @Override // android.widget.AdapterView, android.view.View
    public void setOnClickListener(View.OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCurrentScrollState(OnScrollStateChangedListener.ScrollState newScrollState) {
        this.mCurrentScrollState = newScrollState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateOverscrollAnimation(int scrolledOffset) {
        if (this.mEdgeGlowLeft != null && this.mEdgeGlowRight != null) {
            int nextScrollPosition = this.mCurrentX + scrolledOffset;
            if (this.mFlingTracker == null || this.mFlingTracker.isFinished()) {
                if (nextScrollPosition < 0) {
                    int overscroll = Math.abs(scrolledOffset);
                    this.mEdgeGlowLeft.onPull(overscroll / getRenderWidth());
                    if (!this.mEdgeGlowRight.isFinished()) {
                        this.mEdgeGlowRight.onRelease();
                        return;
                    }
                    return;
                }
                if (nextScrollPosition > this.mMaxX) {
                    int overscroll2 = Math.abs(scrolledOffset);
                    this.mEdgeGlowRight.onPull(overscroll2 / getRenderWidth());
                    if (!this.mEdgeGlowLeft.isFinished()) {
                        this.mEdgeGlowLeft.onRelease();
                    }
                }
            }
        }
    }

    private boolean isEdgeGlowEnabled() {
        return (this.mAdapter == null || this.mAdapter.isEmpty() || this.mMaxX <= 0) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @TargetApi(14)
    /* loaded from: classes.dex */
    public static final class IceCreamSandwichPlus {
        private IceCreamSandwichPlus() {
        }

        public static float getCurrVelocity(Scroller scroller) {
            return scroller.getCurrVelocity();
        }
    }
}
