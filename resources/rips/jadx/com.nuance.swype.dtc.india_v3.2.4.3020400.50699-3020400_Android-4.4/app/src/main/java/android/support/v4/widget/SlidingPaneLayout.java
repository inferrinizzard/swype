package android.support.v4.widget;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import com.nuance.connect.common.Integers;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class SlidingPaneLayout extends ViewGroup {
    static final SlidingPanelLayoutImpl IMPL;
    private boolean mCanSlide;
    private int mCoveredFadeColor;
    private final ViewDragHelper mDragHelper;
    private boolean mFirstLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private boolean mIsUnableToDrag;
    private final int mOverhangSize;
    private PanelSlideListener mPanelSlideListener;
    private int mParallaxBy;
    private float mParallaxOffset;
    private final ArrayList<DisableLayerRunnable> mPostedRunnables;
    private boolean mPreservedOpenState;
    private Drawable mShadowDrawableLeft;
    private Drawable mShadowDrawableRight;
    private float mSlideOffset;
    private int mSlideRange;
    private View mSlideableView;
    private int mSliderFadeColor;
    private final Rect mTmpRect;

    /* loaded from: classes.dex */
    public interface PanelSlideListener {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface SlidingPanelLayoutImpl {
        void invalidateChildRegion(SlidingPaneLayout slidingPaneLayout, View view);
    }

    static {
        int deviceVersion = Build.VERSION.SDK_INT;
        if (deviceVersion >= 17) {
            IMPL = new SlidingPanelLayoutImplJBMR1();
        } else if (deviceVersion >= 16) {
            IMPL = new SlidingPanelLayoutImplJB();
        } else {
            IMPL = new SlidingPanelLayoutImplBase();
        }
    }

    public SlidingPaneLayout(Context context) {
        this(context, null);
    }

    public SlidingPaneLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingPaneLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSliderFadeColor = -858993460;
        this.mFirstLayout = true;
        this.mTmpRect = new Rect();
        this.mPostedRunnables = new ArrayList<>();
        float density = context.getResources().getDisplayMetrics().density;
        this.mOverhangSize = (int) ((32.0f * density) + 0.5f);
        ViewConfiguration.get(context);
        setWillNotDraw(false);
        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
        ViewCompat.setImportantForAccessibility(this, 1);
        this.mDragHelper = ViewDragHelper.create(this, 0.5f, new DragHelperCallback(this, (byte) 0));
        this.mDragHelper.mMinVelocity = 400.0f * density;
    }

    public void setParallaxDistance(int parallaxBy) {
        this.mParallaxBy = parallaxBy;
        requestLayout();
    }

    public int getParallaxDistance() {
        return this.mParallaxBy;
    }

    public void setSliderFadeColor(int color) {
        this.mSliderFadeColor = color;
    }

    public int getSliderFadeColor() {
        return this.mSliderFadeColor;
    }

    public void setCoveredFadeColor(int color) {
        this.mCoveredFadeColor = color;
    }

    public int getCoveredFadeColor() {
        return this.mCoveredFadeColor;
    }

    public void setPanelSlideListener(PanelSlideListener listener) {
        this.mPanelSlideListener = listener;
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0047  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    final void updateObscuredViewsVisibility(android.view.View r22) {
        /*
            r21 = this;
            boolean r12 = r21.isLayoutRtlSupport()
            if (r12 == 0) goto L93
            int r19 = r21.getWidth()
            int r20 = r21.getPaddingRight()
            int r15 = r19 - r20
        L10:
            if (r12 == 0) goto L99
            int r10 = r21.getPaddingLeft()
        L16:
            int r17 = r21.getPaddingTop()
            int r19 = r21.getHeight()
            int r20 = r21.getPaddingBottom()
            int r3 = r19 - r20
            if (r22 == 0) goto Lcd
            boolean r19 = android.support.v4.view.ViewCompat.isOpaque(r22)
            if (r19 == 0) goto La5
            r19 = 1
        L2e:
            if (r19 == 0) goto Lcd
            int r13 = r22.getLeft()
            int r14 = r22.getRight()
            int r16 = r22.getTop()
            int r2 = r22.getBottom()
        L40:
            r11 = 0
            int r5 = r21.getChildCount()
        L45:
            if (r11 >= r5) goto Lde
            r0 = r21
            android.view.View r4 = r0.getChildAt(r11)
            r0 = r22
            if (r4 == r0) goto Lde
            if (r12 == 0) goto Ld4
            r19 = r10
        L55:
            int r20 = r4.getLeft()
            int r7 = java.lang.Math.max(r19, r20)
            int r19 = r4.getTop()
            r0 = r17
            r1 = r19
            int r9 = java.lang.Math.max(r0, r1)
            if (r12 == 0) goto Ld8
            r19 = r15
        L6d:
            int r20 = r4.getRight()
            int r8 = java.lang.Math.min(r19, r20)
            int r19 = r4.getBottom()
            r0 = r19
            int r6 = java.lang.Math.min(r3, r0)
            if (r7 < r13) goto Ldb
            r0 = r16
            if (r9 < r0) goto Ldb
            if (r8 > r14) goto Ldb
            if (r6 > r2) goto Ldb
            r18 = 4
        L8b:
            r0 = r18
            r4.setVisibility(r0)
            int r11 = r11 + 1
            goto L45
        L93:
            int r15 = r21.getPaddingLeft()
            goto L10
        L99:
            int r19 = r21.getWidth()
            int r20 = r21.getPaddingRight()
            int r10 = r19 - r20
            goto L16
        La5:
            int r19 = android.os.Build.VERSION.SDK_INT
            r20 = 18
            r0 = r19
            r1 = r20
            if (r0 >= r1) goto Lc9
            android.graphics.drawable.Drawable r19 = r22.getBackground()
            if (r19 == 0) goto Lc9
            int r19 = r19.getOpacity()
            r20 = -1
            r0 = r19
            r1 = r20
            if (r0 != r1) goto Lc5
            r19 = 1
            goto L2e
        Lc5:
            r19 = 0
            goto L2e
        Lc9:
            r19 = 0
            goto L2e
        Lcd:
            r2 = 0
            r16 = r2
            r14 = r2
            r13 = r2
            goto L40
        Ld4:
            r19 = r15
            goto L55
        Ld8:
            r19 = r10
            goto L6d
        Ldb:
            r18 = 0
            goto L8b
        Lde:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.SlidingPaneLayout.updateObscuredViewsVisibility(android.view.View):void");
    }

    final void setAllChildrenVisible() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == 4) {
                child.setVisibility(0);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
        int count = this.mPostedRunnables.size();
        for (int i = 0; i < count; i++) {
            this.mPostedRunnables.get(i).run();
        }
        this.mPostedRunnables.clear();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childHeightSpec;
        int childHeightSpec2;
        int childWidthSpec;
        int childHeightSpec3;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != 1073741824) {
            if (isInEditMode()) {
                if (widthMode != Integer.MIN_VALUE && widthMode == 0) {
                    widthSize = 300;
                }
            } else {
                throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
            }
        } else if (heightMode == 0) {
            if (isInEditMode()) {
                if (heightMode == 0) {
                    heightMode = Integers.STATUS_SUCCESS;
                    heightSize = 300;
                }
            } else {
                throw new IllegalStateException("Height must not be UNSPECIFIED");
            }
        }
        int layoutHeight = 0;
        int maxLayoutHeight = -1;
        switch (heightMode) {
            case Integers.STATUS_SUCCESS /* -2147483648 */:
                maxLayoutHeight = (heightSize - getPaddingTop()) - getPaddingBottom();
                break;
            case 1073741824:
                maxLayoutHeight = (heightSize - getPaddingTop()) - getPaddingBottom();
                layoutHeight = maxLayoutHeight;
                break;
        }
        float weightSum = 0.0f;
        boolean canSlide = false;
        int widthAvailable = (widthSize - getPaddingLeft()) - getPaddingRight();
        int widthRemaining = widthAvailable;
        int childCount = getChildCount();
        if (childCount > 2) {
            Log.e("SlidingPaneLayout", "onMeasure: More than two child views are not supported.");
        }
        this.mSlideableView = null;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (child.getVisibility() == 8) {
                lp.dimWhenOffset = false;
            } else {
                if (lp.weight > 0.0f) {
                    weightSum += lp.weight;
                    if (lp.width == 0) {
                    }
                }
                int horizontalMargin = lp.leftMargin + lp.rightMargin;
                if (lp.width == -2) {
                    childWidthSpec = View.MeasureSpec.makeMeasureSpec(widthAvailable - horizontalMargin, Integers.STATUS_SUCCESS);
                } else if (lp.width == -1) {
                    childWidthSpec = View.MeasureSpec.makeMeasureSpec(widthAvailable - horizontalMargin, 1073741824);
                } else {
                    childWidthSpec = View.MeasureSpec.makeMeasureSpec(lp.width, 1073741824);
                }
                if (lp.height == -2) {
                    childHeightSpec3 = View.MeasureSpec.makeMeasureSpec(maxLayoutHeight, Integers.STATUS_SUCCESS);
                } else if (lp.height == -1) {
                    childHeightSpec3 = View.MeasureSpec.makeMeasureSpec(maxLayoutHeight, 1073741824);
                } else {
                    childHeightSpec3 = View.MeasureSpec.makeMeasureSpec(lp.height, 1073741824);
                }
                child.measure(childWidthSpec, childHeightSpec3);
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                if (heightMode == Integer.MIN_VALUE && childHeight > layoutHeight) {
                    layoutHeight = Math.min(childHeight, maxLayoutHeight);
                }
                widthRemaining -= childWidth;
                boolean z = widthRemaining < 0;
                lp.slideable = z;
                canSlide |= z;
                if (lp.slideable) {
                    this.mSlideableView = child;
                }
            }
        }
        if (canSlide || weightSum > 0.0f) {
            int fixedPanelWidthLimit = widthAvailable - this.mOverhangSize;
            for (int i2 = 0; i2 < childCount; i2++) {
                View child2 = getChildAt(i2);
                if (child2.getVisibility() != 8) {
                    LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
                    if (child2.getVisibility() != 8) {
                        boolean skippedFirstPass = lp2.width == 0 && lp2.weight > 0.0f;
                        int measuredWidth = skippedFirstPass ? 0 : child2.getMeasuredWidth();
                        if (!canSlide || child2 == this.mSlideableView) {
                            if (lp2.weight > 0.0f) {
                                if (lp2.width == 0) {
                                    if (lp2.height == -2) {
                                        childHeightSpec = View.MeasureSpec.makeMeasureSpec(maxLayoutHeight, Integers.STATUS_SUCCESS);
                                    } else if (lp2.height == -1) {
                                        childHeightSpec = View.MeasureSpec.makeMeasureSpec(maxLayoutHeight, 1073741824);
                                    } else {
                                        childHeightSpec = View.MeasureSpec.makeMeasureSpec(lp2.height, 1073741824);
                                    }
                                } else {
                                    childHeightSpec = View.MeasureSpec.makeMeasureSpec(child2.getMeasuredHeight(), 1073741824);
                                }
                                if (canSlide) {
                                    int newWidth = widthAvailable - (lp2.leftMargin + lp2.rightMargin);
                                    int childWidthSpec2 = View.MeasureSpec.makeMeasureSpec(newWidth, 1073741824);
                                    if (measuredWidth != newWidth) {
                                        child2.measure(childWidthSpec2, childHeightSpec);
                                    }
                                } else {
                                    int widthToDistribute = Math.max(0, widthRemaining);
                                    int addedWidth = (int) ((lp2.weight * widthToDistribute) / weightSum);
                                    int childWidthSpec3 = View.MeasureSpec.makeMeasureSpec(measuredWidth + addedWidth, 1073741824);
                                    child2.measure(childWidthSpec3, childHeightSpec);
                                }
                            }
                        } else if (lp2.width < 0 && (measuredWidth > fixedPanelWidthLimit || lp2.weight > 0.0f)) {
                            if (skippedFirstPass) {
                                if (lp2.height == -2) {
                                    childHeightSpec2 = View.MeasureSpec.makeMeasureSpec(maxLayoutHeight, Integers.STATUS_SUCCESS);
                                } else if (lp2.height == -1) {
                                    childHeightSpec2 = View.MeasureSpec.makeMeasureSpec(maxLayoutHeight, 1073741824);
                                } else {
                                    childHeightSpec2 = View.MeasureSpec.makeMeasureSpec(lp2.height, 1073741824);
                                }
                            } else {
                                childHeightSpec2 = View.MeasureSpec.makeMeasureSpec(child2.getMeasuredHeight(), 1073741824);
                            }
                            int childWidthSpec4 = View.MeasureSpec.makeMeasureSpec(fixedPanelWidthLimit, 1073741824);
                            child2.measure(childWidthSpec4, childHeightSpec2);
                        }
                    }
                }
            }
        }
        int measuredHeight = getPaddingTop() + layoutHeight + getPaddingBottom();
        setMeasuredDimension(widthSize, measuredHeight);
        this.mCanSlide = canSlide;
        if (this.mDragHelper.mDragState != 0 && !canSlide) {
            this.mDragHelper.abort();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft;
        int childRight;
        boolean isLayoutRtl = isLayoutRtlSupport();
        if (isLayoutRtl) {
            this.mDragHelper.mTrackingEdges = 2;
        } else {
            this.mDragHelper.mTrackingEdges = 1;
        }
        int width = r - l;
        int paddingStart = isLayoutRtl ? getPaddingRight() : getPaddingLeft();
        int paddingEnd = isLayoutRtl ? getPaddingLeft() : getPaddingRight();
        int paddingTop = getPaddingTop();
        int childCount = getChildCount();
        int xStart = paddingStart;
        int nextXStart = paddingStart;
        if (this.mFirstLayout) {
            this.mSlideOffset = (this.mCanSlide && this.mPreservedOpenState) ? 1.0f : 0.0f;
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int childWidth = child.getMeasuredWidth();
                int offset = 0;
                if (lp.slideable) {
                    int margin = lp.leftMargin + lp.rightMargin;
                    int range = (Math.min(nextXStart, (width - paddingEnd) - this.mOverhangSize) - xStart) - margin;
                    this.mSlideRange = range;
                    int lpMargin = isLayoutRtl ? lp.rightMargin : lp.leftMargin;
                    lp.dimWhenOffset = ((xStart + lpMargin) + range) + (childWidth / 2) > width - paddingEnd;
                    int pos = (int) (range * this.mSlideOffset);
                    xStart += pos + lpMargin;
                    this.mSlideOffset = pos / this.mSlideRange;
                } else {
                    if (this.mCanSlide && this.mParallaxBy != 0) {
                        offset = (int) ((1.0f - this.mSlideOffset) * this.mParallaxBy);
                    }
                    xStart = nextXStart;
                }
                if (isLayoutRtl) {
                    childRight = (width - xStart) + offset;
                    childLeft = childRight - childWidth;
                } else {
                    childLeft = xStart - offset;
                    childRight = childLeft + childWidth;
                }
                int childBottom = paddingTop + child.getMeasuredHeight();
                child.layout(childLeft, paddingTop, childRight, childBottom);
                nextXStart += child.getWidth();
            }
        }
        if (this.mFirstLayout) {
            if (this.mCanSlide) {
                if (this.mParallaxBy != 0) {
                    parallaxOtherViews(this.mSlideOffset);
                }
                if (((LayoutParams) this.mSlideableView.getLayoutParams()).dimWhenOffset) {
                    dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
                }
            } else {
                for (int i2 = 0; i2 < childCount; i2++) {
                    dimChildView(getChildAt(i2), 0.0f, this.mSliderFadeColor);
                }
            }
            updateObscuredViewsVisibility(this.mSlideableView);
        }
        this.mFirstLayout = false;
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw) {
            this.mFirstLayout = true;
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (!isInTouchMode() && !this.mCanSlide) {
            this.mPreservedOpenState = child == this.mSlideableView;
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        View secondChild;
        int action = MotionEventCompat.getActionMasked(ev);
        if (!this.mCanSlide && action == 0 && getChildCount() > 1 && (secondChild = getChildAt(1)) != null) {
            this.mPreservedOpenState = !ViewDragHelper.isViewUnder(secondChild, (int) ev.getX(), (int) ev.getY());
        }
        if (!this.mCanSlide || (this.mIsUnableToDrag && action != 0)) {
            this.mDragHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        }
        if (action == 3 || action == 1) {
            this.mDragHelper.cancel();
            return false;
        }
        boolean interceptTap = false;
        switch (action) {
            case 0:
                this.mIsUnableToDrag = false;
                float x = ev.getX();
                float y = ev.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                if (ViewDragHelper.isViewUnder(this.mSlideableView, (int) x, (int) y) && isDimmed(this.mSlideableView)) {
                    interceptTap = true;
                    break;
                }
                break;
            case 2:
                float x2 = ev.getX();
                float y2 = ev.getY();
                float adx = Math.abs(x2 - this.mInitialMotionX);
                float ady = Math.abs(y2 - this.mInitialMotionY);
                int slop = this.mDragHelper.mTouchSlop;
                if (adx > slop && ady > adx) {
                    this.mDragHelper.cancel();
                    this.mIsUnableToDrag = true;
                    return false;
                }
                break;
        }
        return this.mDragHelper.shouldInterceptTouchEvent(ev) || interceptTap;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if (!this.mCanSlide) {
            return super.onTouchEvent(ev);
        }
        this.mDragHelper.processTouchEvent(ev);
        switch (ev.getAction() & 255) {
            case 0:
                float x = ev.getX();
                float y = ev.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                break;
            case 1:
                if (isDimmed(this.mSlideableView)) {
                    float x2 = ev.getX();
                    float y2 = ev.getY();
                    float dx = x2 - this.mInitialMotionX;
                    float dy = y2 - this.mInitialMotionY;
                    int slop = this.mDragHelper.mTouchSlop;
                    if ((dx * dx) + (dy * dy) < slop * slop && ViewDragHelper.isViewUnder(this.mSlideableView, (int) x2, (int) y2)) {
                        closePane$5359dc96$134632();
                        break;
                    }
                }
                break;
        }
        return true;
    }

    private boolean closePane$5359dc96$134632() {
        if (!this.mFirstLayout && !smoothSlideTo$254957c(0.0f)) {
            return false;
        }
        this.mPreservedOpenState = false;
        return true;
    }

    private void dimChildView(View v, float mag, int fadeColor) {
        LayoutParams lp = (LayoutParams) v.getLayoutParams();
        if (mag > 0.0f && fadeColor != 0) {
            int color = (((int) ((((-16777216) & fadeColor) >>> 24) * mag)) << 24) | (16777215 & fadeColor);
            if (lp.dimPaint == null) {
                lp.dimPaint = new Paint();
            }
            lp.dimPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_OVER));
            if (ViewCompat.getLayerType(v) != 2) {
                ViewCompat.setLayerType(v, 2, lp.dimPaint);
            }
            invalidateChildRegion(v);
            return;
        }
        if (ViewCompat.getLayerType(v) != 0) {
            if (lp.dimPaint != null) {
                lp.dimPaint.setColorFilter(null);
            }
            DisableLayerRunnable dlr = new DisableLayerRunnable(v);
            this.mPostedRunnables.add(dlr);
            ViewCompat.postOnAnimation(this, dlr);
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result;
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int save = canvas.save(2);
        if (this.mCanSlide && !lp.slideable && this.mSlideableView != null) {
            canvas.getClipBounds(this.mTmpRect);
            if (isLayoutRtlSupport()) {
                this.mTmpRect.left = Math.max(this.mTmpRect.left, this.mSlideableView.getRight());
            } else {
                this.mTmpRect.right = Math.min(this.mTmpRect.right, this.mSlideableView.getLeft());
            }
            canvas.clipRect(this.mTmpRect);
        }
        if (Build.VERSION.SDK_INT < 11) {
            if (lp.dimWhenOffset && this.mSlideOffset > 0.0f) {
                if (!child.isDrawingCacheEnabled()) {
                    child.setDrawingCacheEnabled(true);
                }
                Bitmap cache = child.getDrawingCache();
                if (cache != null) {
                    canvas.drawBitmap(cache, child.getLeft(), child.getTop(), lp.dimPaint);
                    result = false;
                    canvas.restoreToCount(save);
                    return result;
                }
                Log.e("SlidingPaneLayout", "drawChild: child view " + child + " returned null drawing cache");
            } else if (child.isDrawingCacheEnabled()) {
                child.setDrawingCacheEnabled(false);
            }
        }
        result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(save);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateChildRegion(View v) {
        IMPL.invalidateChildRegion(this, v);
    }

    private boolean smoothSlideTo$254957c(float slideOffset) {
        int x;
        if (!this.mCanSlide) {
            return false;
        }
        boolean isLayoutRtl = isLayoutRtlSupport();
        LayoutParams lp = (LayoutParams) this.mSlideableView.getLayoutParams();
        if (isLayoutRtl) {
            int startBound = getPaddingRight() + lp.rightMargin;
            int childWidth = this.mSlideableView.getWidth();
            x = (int) (getWidth() - ((startBound + (this.mSlideRange * slideOffset)) + childWidth));
        } else {
            x = (int) (getPaddingLeft() + lp.leftMargin + (this.mSlideRange * slideOffset));
        }
        if (!this.mDragHelper.smoothSlideViewTo(this.mSlideableView, x, this.mSlideableView.getTop())) {
            return false;
        }
        setAllChildrenVisible();
        ViewCompat.postInvalidateOnAnimation(this);
        return true;
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mDragHelper.continueSettling$138603()) {
            if (!this.mCanSlide) {
                this.mDragHelper.abort();
            } else {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    @Deprecated
    public void setShadowDrawable(Drawable d) {
        setShadowDrawableLeft(d);
    }

    public void setShadowDrawableLeft(Drawable d) {
        this.mShadowDrawableLeft = d;
    }

    public void setShadowDrawableRight(Drawable d) {
        this.mShadowDrawableRight = d;
    }

    @Deprecated
    public void setShadowResource(int resId) {
        setShadowDrawable(getResources().getDrawable(resId));
    }

    public void setShadowResourceLeft(int resId) {
        setShadowDrawableLeft(getResources().getDrawable(resId));
    }

    public void setShadowResourceRight(int resId) {
        setShadowDrawableRight(getResources().getDrawable(resId));
    }

    @Override // android.view.View
    public void draw(Canvas c) {
        Drawable shadowDrawable;
        int right;
        int left;
        super.draw(c);
        if (isLayoutRtlSupport()) {
            shadowDrawable = this.mShadowDrawableRight;
        } else {
            shadowDrawable = this.mShadowDrawableLeft;
        }
        View shadowView = getChildCount() > 1 ? getChildAt(1) : null;
        if (shadowView != null && shadowDrawable != null) {
            int top = shadowView.getTop();
            int bottom = shadowView.getBottom();
            int shadowWidth = shadowDrawable.getIntrinsicWidth();
            if (isLayoutRtlSupport()) {
                left = shadowView.getRight();
                right = left + shadowWidth;
            } else {
                right = shadowView.getLeft();
                left = right - shadowWidth;
            }
            shadowDrawable.setBounds(left, top, right, bottom);
            shadowDrawable.draw(c);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x0020  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parallaxOtherViews(float r13) {
        /*
            r12 = this;
            r11 = 1065353216(0x3f800000, float:1.0)
            boolean r4 = r12.isLayoutRtlSupport()
            android.view.View r9 = r12.mSlideableView
            android.view.ViewGroup$LayoutParams r7 = r9.getLayoutParams()
            android.support.v4.widget.SlidingPaneLayout$LayoutParams r7 = (android.support.v4.widget.SlidingPaneLayout.LayoutParams) r7
            boolean r9 = r7.dimWhenOffset
            if (r9 == 0) goto L54
            if (r4 == 0) goto L51
            int r9 = r7.rightMargin
        L16:
            if (r9 > 0) goto L54
            r1 = 1
        L19:
            int r0 = r12.getChildCount()
            r3 = 0
        L1e:
            if (r3 >= r0) goto L5b
            android.view.View r8 = r12.getChildAt(r3)
            android.view.View r9 = r12.mSlideableView
            if (r8 == r9) goto L4e
            float r9 = r12.mParallaxOffset
            float r9 = r11 - r9
            int r10 = r12.mParallaxBy
            float r10 = (float) r10
            float r9 = r9 * r10
            int r6 = (int) r9
            r12.mParallaxOffset = r13
            float r9 = r11 - r13
            int r10 = r12.mParallaxBy
            float r10 = (float) r10
            float r9 = r9 * r10
            int r5 = (int) r9
            int r2 = r6 - r5
            if (r4 == 0) goto L3f
            int r2 = -r2
        L3f:
            r8.offsetLeftAndRight(r2)
            if (r1 == 0) goto L4e
            if (r4 == 0) goto L56
            float r9 = r12.mParallaxOffset
            float r9 = r9 - r11
        L49:
            int r10 = r12.mCoveredFadeColor
            r12.dimChildView(r8, r9, r10)
        L4e:
            int r3 = r3 + 1
            goto L1e
        L51:
            int r9 = r7.leftMargin
            goto L16
        L54:
            r1 = 0
            goto L19
        L56:
            float r9 = r12.mParallaxOffset
            float r9 = r11 - r9
            goto L49
        L5b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.SlidingPaneLayout.parallaxOtherViews(float):void");
    }

    final boolean isDimmed(View child) {
        if (child == null) {
            return false;
        }
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return this.mCanSlide && lp.dimWhenOffset && this.mSlideOffset > 0.0f;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof ViewGroup.MarginLayoutParams ? new LayoutParams((ViewGroup.MarginLayoutParams) p) : new LayoutParams(p);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return (p instanceof LayoutParams) && super.checkLayoutParams(p);
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        boolean z;
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        if (this.mCanSlide) {
            z = !this.mCanSlide || this.mSlideOffset == 1.0f;
        } else {
            z = this.mPreservedOpenState;
        }
        ss.isOpen = z;
        return ss;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.mSuperState);
        if (ss.isOpen) {
            if (this.mFirstLayout || smoothSlideTo$254957c(1.0f)) {
                this.mPreservedOpenState = true;
            }
        } else {
            closePane$5359dc96$134632();
        }
        this.mPreservedOpenState = ss.isOpen;
    }

    /* loaded from: classes.dex */
    private class DragHelperCallback extends ViewDragHelper.Callback {
        private DragHelperCallback() {
        }

        /* synthetic */ DragHelperCallback(SlidingPaneLayout x0, byte b) {
            this();
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public final boolean tryCaptureView$5359dc96(View child) {
            if (SlidingPaneLayout.this.mIsUnableToDrag) {
                return false;
            }
            return ((LayoutParams) child.getLayoutParams()).slideable;
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public final void onViewDragStateChanged(int state) {
            if (SlidingPaneLayout.this.mDragHelper.mDragState == 0) {
                if (SlidingPaneLayout.this.mSlideOffset == 0.0f) {
                    SlidingPaneLayout.this.updateObscuredViewsVisibility(SlidingPaneLayout.this.mSlideableView);
                    SlidingPaneLayout slidingPaneLayout = SlidingPaneLayout.this;
                    View unused = SlidingPaneLayout.this.mSlideableView;
                    slidingPaneLayout.sendAccessibilityEvent(32);
                    SlidingPaneLayout.this.mPreservedOpenState = false;
                    return;
                }
                SlidingPaneLayout slidingPaneLayout2 = SlidingPaneLayout.this;
                View unused2 = SlidingPaneLayout.this.mSlideableView;
                slidingPaneLayout2.sendAccessibilityEvent(32);
                SlidingPaneLayout.this.mPreservedOpenState = true;
            }
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public final void onViewCaptured$5359dc9a(View capturedChild) {
            SlidingPaneLayout.this.setAllChildrenVisible();
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public final void onViewPositionChanged$5b6f797d(View changedView, int left) {
            SlidingPaneLayout.access$600(SlidingPaneLayout.this, left);
            SlidingPaneLayout.this.invalidate();
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public final void onViewReleased$17e2ac03(View releasedChild, float xvel) {
            int left;
            LayoutParams lp = (LayoutParams) releasedChild.getLayoutParams();
            if (SlidingPaneLayout.this.isLayoutRtlSupport()) {
                int startToRight = SlidingPaneLayout.this.getPaddingRight() + lp.rightMargin;
                if (xvel < 0.0f || (xvel == 0.0f && SlidingPaneLayout.this.mSlideOffset > 0.5f)) {
                    startToRight += SlidingPaneLayout.this.mSlideRange;
                }
                int childWidth = SlidingPaneLayout.this.mSlideableView.getWidth();
                left = (SlidingPaneLayout.this.getWidth() - startToRight) - childWidth;
            } else {
                left = SlidingPaneLayout.this.getPaddingLeft() + lp.leftMargin;
                if (xvel > 0.0f || (xvel == 0.0f && SlidingPaneLayout.this.mSlideOffset > 0.5f)) {
                    left += SlidingPaneLayout.this.mSlideRange;
                }
            }
            SlidingPaneLayout.this.mDragHelper.settleCapturedViewAt(left, releasedChild.getTop());
            SlidingPaneLayout.this.invalidate();
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public final int getViewHorizontalDragRange(View child) {
            return SlidingPaneLayout.this.mSlideRange;
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public final int clampViewPositionHorizontal$17e143b0(View child, int left) {
            LayoutParams lp = (LayoutParams) SlidingPaneLayout.this.mSlideableView.getLayoutParams();
            if (SlidingPaneLayout.this.isLayoutRtlSupport()) {
                int startBound = SlidingPaneLayout.this.getWidth() - ((SlidingPaneLayout.this.getPaddingRight() + lp.rightMargin) + SlidingPaneLayout.this.mSlideableView.getWidth());
                int endBound = startBound - SlidingPaneLayout.this.mSlideRange;
                int newLeft = Math.max(Math.min(left, startBound), endBound);
                return newLeft;
            }
            int startBound2 = SlidingPaneLayout.this.getPaddingLeft() + lp.leftMargin;
            int endBound2 = startBound2 + SlidingPaneLayout.this.mSlideRange;
            int newLeft2 = Math.min(Math.max(left, startBound2), endBound2);
            return newLeft2;
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public final int clampViewPositionVertical$17e143b0(View child) {
            return child.getTop();
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public final void onEdgeDragStarted(int edgeFlags, int pointerId) {
            SlidingPaneLayout.this.mDragHelper.captureChildView(SlidingPaneLayout.this.mSlideableView, pointerId);
        }
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final int[] ATTRS = {R.attr.layout_weight};
        Paint dimPaint;
        boolean dimWhenOffset;
        boolean slideable;
        public float weight;

        public LayoutParams() {
            super(-1, -1);
            this.weight = 0.0f;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.weight = 0.0f;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
            this.weight = 0.0f;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.weight = 0.0f;
            TypedArray a = c.obtainStyledAttributes(attrs, ATTRS);
            this.weight = a.getFloat(0, 0.0f);
            a.recycle();
        }
    }

    /* loaded from: classes.dex */
    static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() { // from class: android.support.v4.widget.SlidingPaneLayout.SavedState.1
            @Override // android.support.v4.os.ParcelableCompatCreatorCallbacks
            public final /* bridge */ /* synthetic */ SavedState[] newArray(int i) {
                return new SavedState[i];
            }

            @Override // android.support.v4.os.ParcelableCompatCreatorCallbacks
            public final /* bridge */ /* synthetic */ SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader, (byte) 0);
            }
        });
        boolean isOpen;

        /* synthetic */ SavedState(Parcel x0, ClassLoader x1, byte b) {
            this(x0, x1);
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            this.isOpen = in.readInt() != 0;
        }

        @Override // android.support.v4.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.isOpen ? 1 : 0);
        }
    }

    /* loaded from: classes.dex */
    static class SlidingPanelLayoutImplBase implements SlidingPanelLayoutImpl {
        SlidingPanelLayoutImplBase() {
        }

        @Override // android.support.v4.widget.SlidingPaneLayout.SlidingPanelLayoutImpl
        public void invalidateChildRegion(SlidingPaneLayout parent, View child) {
            ViewCompat.postInvalidateOnAnimation(parent, child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        }
    }

    /* loaded from: classes.dex */
    static class SlidingPanelLayoutImplJB extends SlidingPanelLayoutImplBase {
        private Method mGetDisplayList;
        private Field mRecreateDisplayList;

        SlidingPanelLayoutImplJB() {
            try {
                this.mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", null);
            } catch (NoSuchMethodException e) {
                Log.e("SlidingPaneLayout", "Couldn't fetch getDisplayList method; dimming won't work right.", e);
            }
            try {
                this.mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
                this.mRecreateDisplayList.setAccessible(true);
            } catch (NoSuchFieldException e2) {
                Log.e("SlidingPaneLayout", "Couldn't fetch mRecreateDisplayList field; dimming will be slow.", e2);
            }
        }

        @Override // android.support.v4.widget.SlidingPaneLayout.SlidingPanelLayoutImplBase, android.support.v4.widget.SlidingPaneLayout.SlidingPanelLayoutImpl
        public final void invalidateChildRegion(SlidingPaneLayout parent, View child) {
            if (this.mGetDisplayList != null && this.mRecreateDisplayList != null) {
                try {
                    this.mRecreateDisplayList.setBoolean(child, true);
                    this.mGetDisplayList.invoke(child, null);
                } catch (Exception e) {
                    Log.e("SlidingPaneLayout", "Error refreshing display list state", e);
                }
                super.invalidateChildRegion(parent, child);
                return;
            }
            child.invalidate();
        }
    }

    /* loaded from: classes.dex */
    static class SlidingPanelLayoutImplJBMR1 extends SlidingPanelLayoutImplBase {
        SlidingPanelLayoutImplJBMR1() {
        }

        @Override // android.support.v4.widget.SlidingPaneLayout.SlidingPanelLayoutImplBase, android.support.v4.widget.SlidingPaneLayout.SlidingPanelLayoutImpl
        public final void invalidateChildRegion(SlidingPaneLayout parent, View child) {
            ViewCompat.setLayerPaint(child, ((LayoutParams) child.getLayoutParams()).dimPaint);
        }
    }

    /* loaded from: classes.dex */
    class AccessibilityDelegate extends AccessibilityDelegateCompat {
        private final Rect mTmpRect = new Rect();

        AccessibilityDelegate() {
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat
        public final void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            AccessibilityNodeInfoCompat superNode = AccessibilityNodeInfoCompat.obtain(info);
            super.onInitializeAccessibilityNodeInfo(host, superNode);
            Rect rect = this.mTmpRect;
            superNode.getBoundsInParent(rect);
            info.setBoundsInParent(rect);
            superNode.getBoundsInScreen(rect);
            info.setBoundsInScreen(rect);
            info.setVisibleToUser(superNode.isVisibleToUser());
            info.setPackageName(superNode.getPackageName());
            info.setClassName(superNode.getClassName());
            info.setContentDescription(superNode.getContentDescription());
            info.setEnabled(superNode.isEnabled());
            info.setClickable(superNode.isClickable());
            info.setFocusable(superNode.isFocusable());
            info.setFocused(superNode.isFocused());
            info.setAccessibilityFocused(superNode.isAccessibilityFocused());
            info.setSelected(superNode.isSelected());
            info.setLongClickable(superNode.isLongClickable());
            info.addAction(superNode.getActions());
            AccessibilityNodeInfoCompat.IMPL.setMovementGranularities(info.mInfo, AccessibilityNodeInfoCompat.IMPL.getMovementGranularities(superNode.mInfo));
            superNode.recycle();
            info.setClassName(SlidingPaneLayout.class.getName());
            info.setSource(host);
            Object parentForAccessibility = ViewCompat.getParentForAccessibility(host);
            if (parentForAccessibility instanceof View) {
                info.setParent((View) parentForAccessibility);
            }
            int childCount = SlidingPaneLayout.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = SlidingPaneLayout.this.getChildAt(i);
                if (!filter(child) && child.getVisibility() == 0) {
                    ViewCompat.setImportantForAccessibility(child, 1);
                    info.addChild(child);
                }
            }
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat
        public final void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(host, event);
            event.setClassName(SlidingPaneLayout.class.getName());
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat
        public final boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
            if (filter(child)) {
                return false;
            }
            return super.onRequestSendAccessibilityEvent(host, child, event);
        }

        private boolean filter(View child) {
            return SlidingPaneLayout.this.isDimmed(child);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DisableLayerRunnable implements Runnable {
        final View mChildView;

        DisableLayerRunnable(View childView) {
            this.mChildView = childView;
        }

        @Override // java.lang.Runnable
        public final void run() {
            if (this.mChildView.getParent() == SlidingPaneLayout.this) {
                ViewCompat.setLayerType(this.mChildView, 0, null);
                SlidingPaneLayout.this.invalidateChildRegion(this.mChildView);
            }
            SlidingPaneLayout.this.mPostedRunnables.remove(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isLayoutRtlSupport() {
        return ViewCompat.getLayoutDirection(this) == 1;
    }

    static /* synthetic */ void access$600(SlidingPaneLayout x0, int x1) {
        if (x0.mSlideableView == null) {
            x0.mSlideOffset = 0.0f;
            return;
        }
        boolean isLayoutRtlSupport = x0.isLayoutRtlSupport();
        LayoutParams layoutParams = (LayoutParams) x0.mSlideableView.getLayoutParams();
        int width = x0.mSlideableView.getWidth();
        if (isLayoutRtlSupport) {
            x1 = (x0.getWidth() - x1) - width;
        }
        x0.mSlideOffset = (x1 - ((isLayoutRtlSupport ? layoutParams.rightMargin : layoutParams.leftMargin) + (isLayoutRtlSupport ? x0.getPaddingRight() : x0.getPaddingLeft()))) / x0.mSlideRange;
        if (x0.mParallaxBy != 0) {
            x0.parallaxOtherViews(x0.mSlideOffset);
        }
        if (!layoutParams.dimWhenOffset) {
            return;
        }
        x0.dimChildView(x0.mSlideableView, x0.mSlideOffset, x0.mSliderFadeColor);
    }
}
