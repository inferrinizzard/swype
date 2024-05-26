package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.nuance.connect.common.Integers;
import com.nuance.swype.input.KeyboardEx;

/* loaded from: classes.dex */
public class LinearLayoutCompat extends ViewGroup {
    private boolean mBaselineAligned;
    private int mBaselineAlignedChildIndex;
    private int mBaselineChildTop;
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerPadding;
    private int mDividerWidth;
    private int mGravity;
    private int[] mMaxAscent;
    private int[] mMaxDescent;
    private int mOrientation;
    private int mShowDividers;
    private int mTotalLength;
    private boolean mUseLargestChild;
    private float mWeightSum;

    public LinearLayoutCompat(Context context) {
        this(context, null);
    }

    public LinearLayoutCompat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearLayoutCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mBaselineAligned = true;
        this.mBaselineAlignedChildIndex = -1;
        this.mBaselineChildTop = 0;
        this.mGravity = 8388659;
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.LinearLayoutCompat, defStyleAttr, 0);
        int index = a.getInt(R.styleable.LinearLayoutCompat_android_orientation, -1);
        if (index >= 0) {
            setOrientation(index);
        }
        int index2 = a.getInt(R.styleable.LinearLayoutCompat_android_gravity, -1);
        if (index2 >= 0) {
            setGravity(index2);
        }
        boolean baselineAligned = a.getBoolean(R.styleable.LinearLayoutCompat_android_baselineAligned, true);
        if (!baselineAligned) {
            setBaselineAligned(baselineAligned);
        }
        this.mWeightSum = a.mWrapped.getFloat(R.styleable.LinearLayoutCompat_android_weightSum, -1.0f);
        this.mBaselineAlignedChildIndex = a.getInt(R.styleable.LinearLayoutCompat_android_baselineAlignedChildIndex, -1);
        this.mUseLargestChild = a.getBoolean(R.styleable.LinearLayoutCompat_measureWithLargestChild, false);
        setDividerDrawable(a.getDrawable(R.styleable.LinearLayoutCompat_divider));
        this.mShowDividers = a.getInt(R.styleable.LinearLayoutCompat_showDividers, 0);
        this.mDividerPadding = a.getDimensionPixelSize(R.styleable.LinearLayoutCompat_dividerPadding, 0);
        a.mWrapped.recycle();
    }

    public void setShowDividers(int showDividers) {
        if (showDividers != this.mShowDividers) {
            requestLayout();
        }
        this.mShowDividers = showDividers;
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public int getShowDividers() {
        return this.mShowDividers;
    }

    public Drawable getDividerDrawable() {
        return this.mDivider;
    }

    public void setDividerDrawable(Drawable divider) {
        if (divider != this.mDivider) {
            this.mDivider = divider;
            if (divider != null) {
                this.mDividerWidth = divider.getIntrinsicWidth();
                this.mDividerHeight = divider.getIntrinsicHeight();
            } else {
                this.mDividerWidth = 0;
                this.mDividerHeight = 0;
            }
            setWillNotDraw(divider == null);
            requestLayout();
        }
    }

    public void setDividerPadding(int padding) {
        this.mDividerPadding = padding;
    }

    public int getDividerPadding() {
        return this.mDividerPadding;
    }

    public int getDividerWidth() {
        return this.mDividerWidth;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int right;
        int left;
        int bottom;
        if (this.mDivider != null) {
            if (this.mOrientation == 1) {
                int virtualChildCount = getVirtualChildCount();
                for (int i = 0; i < virtualChildCount; i++) {
                    View childAt = getChildAt(i);
                    if (childAt != null && childAt.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
                        drawHorizontalDivider(canvas, (childAt.getTop() - ((LayoutParams) childAt.getLayoutParams()).topMargin) - this.mDividerHeight);
                    }
                }
                if (!hasDividerBeforeChildAt(virtualChildCount)) {
                    return;
                }
                View childAt2 = getChildAt(virtualChildCount - 1);
                if (childAt2 == null) {
                    bottom = (getHeight() - getPaddingBottom()) - this.mDividerHeight;
                } else {
                    bottom = ((LayoutParams) childAt2.getLayoutParams()).bottomMargin + childAt2.getBottom();
                }
                drawHorizontalDivider(canvas, bottom);
                return;
            }
            int virtualChildCount2 = getVirtualChildCount();
            boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
            for (int i2 = 0; i2 < virtualChildCount2; i2++) {
                View childAt3 = getChildAt(i2);
                if (childAt3 != null && childAt3.getVisibility() != 8 && hasDividerBeforeChildAt(i2)) {
                    LayoutParams layoutParams = (LayoutParams) childAt3.getLayoutParams();
                    if (isLayoutRtl) {
                        left = layoutParams.rightMargin + childAt3.getRight();
                    } else {
                        left = (childAt3.getLeft() - layoutParams.leftMargin) - this.mDividerWidth;
                    }
                    drawVerticalDivider(canvas, left);
                }
            }
            if (!hasDividerBeforeChildAt(virtualChildCount2)) {
                return;
            }
            View childAt4 = getChildAt(virtualChildCount2 - 1);
            if (childAt4 == null) {
                if (isLayoutRtl) {
                    right = getPaddingLeft();
                } else {
                    right = (getWidth() - getPaddingRight()) - this.mDividerWidth;
                }
            } else {
                LayoutParams layoutParams2 = (LayoutParams) childAt4.getLayoutParams();
                if (isLayoutRtl) {
                    right = (childAt4.getLeft() - layoutParams2.leftMargin) - this.mDividerWidth;
                } else {
                    right = layoutParams2.rightMargin + childAt4.getRight();
                }
            }
            drawVerticalDivider(canvas, right);
        }
    }

    private void drawHorizontalDivider(Canvas canvas, int top) {
        this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, top, (getWidth() - getPaddingRight()) - this.mDividerPadding, this.mDividerHeight + top);
        this.mDivider.draw(canvas);
    }

    private void drawVerticalDivider(Canvas canvas, int left) {
        this.mDivider.setBounds(left, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + left, (getHeight() - getPaddingBottom()) - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }

    public void setBaselineAligned(boolean baselineAligned) {
        this.mBaselineAligned = baselineAligned;
    }

    public void setMeasureWithLargestChildEnabled(boolean enabled) {
        this.mUseLargestChild = enabled;
    }

    @Override // android.view.View
    public int getBaseline() {
        int majorGravity;
        if (this.mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }
        if (getChildCount() <= this.mBaselineAlignedChildIndex) {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
        }
        View child = getChildAt(this.mBaselineAlignedChildIndex);
        int childBaseline = child.getBaseline();
        if (childBaseline == -1) {
            if (this.mBaselineAlignedChildIndex != 0) {
                throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
            }
            return -1;
        }
        int childTop = this.mBaselineChildTop;
        if (this.mOrientation == 1 && (majorGravity = this.mGravity & 112) != 48) {
            switch (majorGravity) {
                case 16:
                    childTop += ((((getBottom() - getTop()) - getPaddingTop()) - getPaddingBottom()) - this.mTotalLength) / 2;
                    break;
                case 80:
                    childTop = ((getBottom() - getTop()) - getPaddingBottom()) - this.mTotalLength;
                    break;
            }
        }
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return lp.topMargin + childTop + childBaseline;
    }

    public int getBaselineAlignedChildIndex() {
        return this.mBaselineAlignedChildIndex;
    }

    public void setBaselineAlignedChildIndex(int i) {
        if (i < 0 || i >= getChildCount()) {
            throw new IllegalArgumentException("base aligned child index out of range (0, " + getChildCount() + ")");
        }
        this.mBaselineAlignedChildIndex = i;
    }

    int getVirtualChildCount() {
        return getChildCount();
    }

    public float getWeightSum() {
        return this.mWeightSum;
    }

    public void setWeightSum(float weightSum) {
        this.mWeightSum = Math.max(0.0f, weightSum);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int i2;
        float f;
        boolean z;
        int i3;
        int i4;
        int i5;
        int i6;
        View view;
        int i7;
        int i8;
        boolean z2;
        float f2;
        boolean z3;
        int i9;
        int i10;
        boolean z4;
        int i11;
        int i12;
        int i13;
        int i14;
        boolean z5;
        if (this.mOrientation != 1) {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        this.mTotalLength = 0;
        int i15 = 0;
        int i16 = 0;
        int i17 = 0;
        int i18 = 0;
        boolean z6 = true;
        float f3 = 0.0f;
        int virtualChildCount = getVirtualChildCount();
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        int mode2 = View.MeasureSpec.getMode(heightMeasureSpec);
        boolean z7 = false;
        boolean z8 = false;
        int i19 = this.mBaselineAlignedChildIndex;
        boolean z9 = this.mUseLargestChild;
        int i20 = Integers.STATUS_SUCCESS;
        int i21 = 0;
        while (i21 < virtualChildCount) {
            View childAt = getChildAt(i21);
            if (childAt == null) {
                this.mTotalLength += 0;
                i13 = i21;
            } else {
                if (childAt.getVisibility() != 8) {
                    if (hasDividerBeforeChildAt(i21)) {
                        this.mTotalLength += this.mDividerHeight;
                    }
                    LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                    float f4 = f3 + layoutParams.weight;
                    if (mode2 == 1073741824 && layoutParams.height == 0 && layoutParams.weight > 0.0f) {
                        int i22 = this.mTotalLength;
                        this.mTotalLength = Math.max(i22, layoutParams.topMargin + i22 + layoutParams.bottomMargin);
                        i14 = i20;
                        z5 = true;
                    } else {
                        int i23 = Integers.STATUS_SUCCESS;
                        if (layoutParams.height == 0 && layoutParams.weight > 0.0f) {
                            i23 = 0;
                            layoutParams.height = -2;
                        }
                        int i24 = i23;
                        measureChildBeforeLayout$12802926(childAt, widthMeasureSpec, 0, heightMeasureSpec, f4 == 0.0f ? this.mTotalLength : 0);
                        if (i24 != Integer.MIN_VALUE) {
                            layoutParams.height = i24;
                        }
                        int measuredHeight = childAt.getMeasuredHeight();
                        int i25 = this.mTotalLength;
                        this.mTotalLength = Math.max(i25, i25 + measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin + 0);
                        if (z9) {
                            i14 = Math.max(measuredHeight, i20);
                            z5 = z8;
                        } else {
                            i14 = i20;
                            z5 = z8;
                        }
                    }
                    if (i19 >= 0 && i19 == i21 + 1) {
                        this.mBaselineChildTop = this.mTotalLength;
                    }
                    if (i21 < i19 && layoutParams.weight > 0.0f) {
                        throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
                    }
                    boolean z10 = false;
                    if (mode == 1073741824 || layoutParams.width != -1) {
                        z4 = z7;
                    } else {
                        z4 = true;
                        z10 = true;
                    }
                    int i26 = layoutParams.leftMargin + layoutParams.rightMargin;
                    int measuredWidth = childAt.getMeasuredWidth() + i26;
                    int max = Math.max(i15, measuredWidth);
                    i12 = ViewUtils.combineMeasuredStates(i16, ViewCompat.getMeasuredState(childAt));
                    boolean z11 = z6 && layoutParams.width == -1;
                    if (layoutParams.weight > 0.0f) {
                        int i27 = z10 ? i26 : measuredWidth;
                        f2 = f4;
                        z3 = z11;
                        i10 = i17;
                        z2 = z5;
                        i11 = max;
                        int i28 = i14;
                        i9 = Math.max(i18, i27);
                        i8 = i28;
                    } else {
                        if (!z10) {
                            i26 = measuredWidth;
                        }
                        int max2 = Math.max(i17, i26);
                        f2 = f4;
                        z3 = z11;
                        i10 = max2;
                        z2 = z5;
                        i8 = i14;
                        i9 = i18;
                        i11 = max;
                    }
                } else {
                    i8 = i20;
                    z2 = z8;
                    f2 = f3;
                    z3 = z6;
                    i9 = i18;
                    i10 = i17;
                    z4 = z7;
                    i11 = i15;
                    i12 = i16;
                }
                z6 = z3;
                i18 = i9;
                i17 = i10;
                i16 = i12;
                i15 = i11;
                i20 = i8;
                z7 = z4;
                i13 = i21 + 0;
                f3 = f2;
                z8 = z2;
            }
            i21 = i13 + 1;
        }
        if (this.mTotalLength > 0 && hasDividerBeforeChildAt(virtualChildCount)) {
            this.mTotalLength += this.mDividerHeight;
        }
        if (z9 && (mode2 == Integer.MIN_VALUE || mode2 == 0)) {
            this.mTotalLength = 0;
            int i29 = 0;
            while (i29 < virtualChildCount) {
                View childAt2 = getChildAt(i29);
                if (childAt2 == null) {
                    this.mTotalLength += 0;
                    i7 = i29;
                } else if (childAt2.getVisibility() == 8) {
                    i7 = i29 + 0;
                } else {
                    LayoutParams layoutParams2 = (LayoutParams) childAt2.getLayoutParams();
                    int i30 = this.mTotalLength;
                    this.mTotalLength = Math.max(i30, layoutParams2.bottomMargin + i30 + i20 + layoutParams2.topMargin + 0);
                    i7 = i29;
                }
                i29 = i7 + 1;
            }
        }
        this.mTotalLength += getPaddingTop() + getPaddingBottom();
        int resolveSizeAndState = ViewCompat.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumHeight()), heightMeasureSpec, 0);
        int i31 = (16777215 & resolveSizeAndState) - this.mTotalLength;
        if (z8 || (i31 != 0 && f3 > 0.0f)) {
            if (this.mWeightSum > 0.0f) {
                f3 = this.mWeightSum;
            }
            this.mTotalLength = 0;
            int i32 = 0;
            boolean z12 = z6;
            int i33 = i17;
            int i34 = i16;
            int i35 = i15;
            while (i32 < virtualChildCount) {
                View childAt3 = getChildAt(i32);
                if (childAt3.getVisibility() != 8) {
                    LayoutParams layoutParams3 = (LayoutParams) childAt3.getLayoutParams();
                    float f5 = layoutParams3.weight;
                    if (f5 > 0.0f) {
                        int i36 = (int) ((i31 * f5) / f3);
                        float f6 = f3 - f5;
                        int i37 = i31 - i36;
                        int childMeasureSpec = getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight() + layoutParams3.leftMargin + layoutParams3.rightMargin, layoutParams3.width);
                        if (layoutParams3.height != 0 || mode2 != 1073741824) {
                            i36 += childAt3.getMeasuredHeight();
                            if (i36 < 0) {
                                i36 = 0;
                            }
                            view = childAt3;
                        } else if (i36 > 0) {
                            view = childAt3;
                        } else {
                            i36 = 0;
                            view = childAt3;
                        }
                        view.measure(childMeasureSpec, View.MeasureSpec.makeMeasureSpec(i36, 1073741824));
                        i5 = i37;
                        i6 = ViewUtils.combineMeasuredStates(i34, ViewCompat.getMeasuredState(childAt3) & (-256));
                        f = f6;
                    } else {
                        f = f3;
                        i5 = i31;
                        i6 = i34;
                    }
                    int i38 = layoutParams3.leftMargin + layoutParams3.rightMargin;
                    int measuredWidth2 = childAt3.getMeasuredWidth() + i38;
                    int max3 = Math.max(i35, measuredWidth2);
                    if (!(mode != 1073741824 && layoutParams3.width == -1)) {
                        i38 = measuredWidth2;
                    }
                    int max4 = Math.max(i33, i38);
                    z = z12 && layoutParams3.width == -1;
                    int i39 = this.mTotalLength;
                    this.mTotalLength = Math.max(i39, layoutParams3.bottomMargin + childAt3.getMeasuredHeight() + i39 + layoutParams3.topMargin + 0);
                    i3 = max4;
                    i4 = max3;
                } else {
                    f = f3;
                    z = z12;
                    i3 = i33;
                    i4 = i35;
                    i5 = i31;
                    i6 = i34;
                }
                i32++;
                z12 = z;
                i33 = i3;
                i34 = i6;
                i35 = i4;
                i31 = i5;
                f3 = f;
            }
            this.mTotalLength += getPaddingTop() + getPaddingBottom();
            i = i33;
            i16 = i34;
            i2 = i35;
            z6 = z12;
        } else {
            int max5 = Math.max(i17, i18);
            if (z9 && mode2 != 1073741824) {
                int i40 = 0;
                while (true) {
                    int i41 = i40;
                    if (i41 >= virtualChildCount) {
                        break;
                    }
                    View childAt4 = getChildAt(i41);
                    if (childAt4 != null && childAt4.getVisibility() != 8 && ((LayoutParams) childAt4.getLayoutParams()).weight > 0.0f) {
                        childAt4.measure(View.MeasureSpec.makeMeasureSpec(childAt4.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(i20, 1073741824));
                    }
                    i40 = i41 + 1;
                }
            }
            i = max5;
            i2 = i15;
        }
        if (z6 || mode == 1073741824) {
            i = i2;
        }
        setMeasuredDimension(ViewCompat.resolveSizeAndState(Math.max(i + getPaddingLeft() + getPaddingRight(), getSuggestedMinimumWidth()), widthMeasureSpec, i16), resolveSizeAndState);
        if (!z7) {
            return;
        }
        forceUniformWidth(virtualChildCount, heightMeasureSpec);
    }

    private boolean hasDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0) {
            return (this.mShowDividers & 1) != 0;
        }
        if (childIndex == getChildCount()) {
            return (this.mShowDividers & 4) != 0;
        }
        if ((this.mShowDividers & 2) == 0) {
            return false;
        }
        for (int i = childIndex - 1; i >= 0; i--) {
            if (getChildAt(i).getVisibility() != 8) {
                return true;
            }
        }
        return false;
    }

    private void forceUniformWidth(int count, int heightMeasureSpec) {
        int uniformMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.width == -1) {
                    int oldHeight = lp.height;
                    lp.height = child.getMeasuredHeight();
                    measureChildWithMargins(child, uniformMeasureSpec, 0, heightMeasureSpec, 0);
                    lp.height = oldHeight;
                }
            }
        }
    }

    private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        int childBaseline;
        View child;
        int childBaseline2;
        this.mTotalLength = 0;
        int maxHeight = 0;
        int childState = 0;
        int alternativeMaxHeight = 0;
        int weightedMaxHeight = 0;
        boolean allFillParent = true;
        float totalWeight = 0.0f;
        int count = getVirtualChildCount();
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        boolean matchHeight = false;
        boolean skippedMeasure = false;
        if (this.mMaxAscent == null || this.mMaxDescent == null) {
            this.mMaxAscent = new int[4];
            this.mMaxDescent = new int[4];
        }
        int[] maxAscent = this.mMaxAscent;
        int[] maxDescent = this.mMaxDescent;
        maxAscent[3] = -1;
        maxAscent[2] = -1;
        maxAscent[1] = -1;
        maxAscent[0] = -1;
        maxDescent[3] = -1;
        maxDescent[2] = -1;
        maxDescent[1] = -1;
        maxDescent[0] = -1;
        boolean baselineAligned = this.mBaselineAligned;
        boolean useLargestChild = this.mUseLargestChild;
        boolean isExactly = widthMode == 1073741824;
        int largestChildWidth = Integers.STATUS_SUCCESS;
        int i = 0;
        while (i < count) {
            View child2 = getChildAt(i);
            if (child2 == null) {
                this.mTotalLength += 0;
            } else {
                if (child2.getVisibility() != 8) {
                    if (hasDividerBeforeChildAt(i)) {
                        this.mTotalLength += this.mDividerWidth;
                    }
                    LayoutParams lp = (LayoutParams) child2.getLayoutParams();
                    totalWeight += lp.weight;
                    if (widthMode == 1073741824 && lp.width == 0 && lp.weight > 0.0f) {
                        if (isExactly) {
                            this.mTotalLength += lp.leftMargin + lp.rightMargin;
                        } else {
                            int totalLength = this.mTotalLength;
                            this.mTotalLength = Math.max(totalLength, lp.leftMargin + totalLength + lp.rightMargin);
                        }
                        if (baselineAligned) {
                            int freeSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
                            child2.measure(freeSpec, freeSpec);
                        } else {
                            skippedMeasure = true;
                        }
                    } else {
                        int oldWidth = Integers.STATUS_SUCCESS;
                        if (lp.width == 0 && lp.weight > 0.0f) {
                            oldWidth = 0;
                            lp.width = -2;
                        }
                        measureChildBeforeLayout$12802926(child2, widthMeasureSpec, totalWeight == 0.0f ? this.mTotalLength : 0, heightMeasureSpec, 0);
                        if (oldWidth != Integer.MIN_VALUE) {
                            lp.width = oldWidth;
                        }
                        int childWidth = child2.getMeasuredWidth();
                        if (isExactly) {
                            this.mTotalLength += lp.leftMargin + childWidth + lp.rightMargin + 0;
                        } else {
                            int totalLength2 = this.mTotalLength;
                            this.mTotalLength = Math.max(totalLength2, totalLength2 + childWidth + lp.leftMargin + lp.rightMargin + 0);
                        }
                        if (useLargestChild) {
                            largestChildWidth = Math.max(childWidth, largestChildWidth);
                        }
                    }
                    boolean matchHeightLocally = false;
                    if (heightMode != 1073741824 && lp.height == -1) {
                        matchHeight = true;
                        matchHeightLocally = true;
                    }
                    int margin = lp.topMargin + lp.bottomMargin;
                    int childHeight = child2.getMeasuredHeight() + margin;
                    childState = ViewUtils.combineMeasuredStates(childState, ViewCompat.getMeasuredState(child2));
                    if (baselineAligned && (childBaseline2 = child2.getBaseline()) != -1) {
                        int index = ((((lp.gravity < 0 ? this.mGravity : lp.gravity) & 112) >> 4) & (-2)) >> 1;
                        maxAscent[index] = Math.max(maxAscent[index], childBaseline2);
                        maxDescent[index] = Math.max(maxDescent[index], childHeight - childBaseline2);
                    }
                    maxHeight = Math.max(maxHeight, childHeight);
                    allFillParent = allFillParent && lp.height == -1;
                    if (lp.weight > 0.0f) {
                        if (!matchHeightLocally) {
                            margin = childHeight;
                        }
                        weightedMaxHeight = Math.max(weightedMaxHeight, margin);
                    } else {
                        if (!matchHeightLocally) {
                            margin = childHeight;
                        }
                        alternativeMaxHeight = Math.max(alternativeMaxHeight, margin);
                    }
                }
                i += 0;
            }
            i++;
        }
        if (this.mTotalLength > 0 && hasDividerBeforeChildAt(count)) {
            this.mTotalLength += this.mDividerWidth;
        }
        if (maxAscent[1] != -1 || maxAscent[0] != -1 || maxAscent[2] != -1 || maxAscent[3] != -1) {
            int ascent = Math.max(maxAscent[3], Math.max(maxAscent[0], Math.max(maxAscent[1], maxAscent[2])));
            int descent = Math.max(maxDescent[3], Math.max(maxDescent[0], Math.max(maxDescent[1], maxDescent[2])));
            maxHeight = Math.max(maxHeight, ascent + descent);
        }
        if (useLargestChild && (widthMode == Integer.MIN_VALUE || widthMode == 0)) {
            this.mTotalLength = 0;
            int i2 = 0;
            while (i2 < count) {
                View child3 = getChildAt(i2);
                if (child3 == null) {
                    this.mTotalLength += 0;
                } else if (child3.getVisibility() == 8) {
                    i2 += 0;
                } else {
                    LayoutParams lp2 = (LayoutParams) child3.getLayoutParams();
                    if (isExactly) {
                        this.mTotalLength += lp2.leftMargin + largestChildWidth + lp2.rightMargin + 0;
                    } else {
                        int totalLength3 = this.mTotalLength;
                        this.mTotalLength = Math.max(totalLength3, totalLength3 + largestChildWidth + lp2.leftMargin + lp2.rightMargin + 0);
                    }
                }
                i2++;
            }
        }
        this.mTotalLength += getPaddingLeft() + getPaddingRight();
        int widthSizeAndState = ViewCompat.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumWidth()), widthMeasureSpec, 0);
        int delta = (16777215 & widthSizeAndState) - this.mTotalLength;
        if (skippedMeasure || (delta != 0 && totalWeight > 0.0f)) {
            float weightSum = this.mWeightSum > 0.0f ? this.mWeightSum : totalWeight;
            maxAscent[3] = -1;
            maxAscent[2] = -1;
            maxAscent[1] = -1;
            maxAscent[0] = -1;
            maxDescent[3] = -1;
            maxDescent[2] = -1;
            maxDescent[1] = -1;
            maxDescent[0] = -1;
            maxHeight = -1;
            this.mTotalLength = 0;
            for (int i3 = 0; i3 < count; i3++) {
                View child4 = getChildAt(i3);
                if (child4 != null && child4.getVisibility() != 8) {
                    LayoutParams lp3 = (LayoutParams) child4.getLayoutParams();
                    float childExtra = lp3.weight;
                    if (childExtra > 0.0f) {
                        int share = (int) ((delta * childExtra) / weightSum);
                        weightSum -= childExtra;
                        delta -= share;
                        int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom() + lp3.topMargin + lp3.bottomMargin, lp3.height);
                        if (lp3.width != 0 || widthMode != 1073741824) {
                            int childWidth2 = child4.getMeasuredWidth() + share;
                            if (childWidth2 < 0) {
                                childWidth2 = 0;
                            }
                            share = childWidth2;
                            child = child4;
                        } else if (share > 0) {
                            child = child4;
                        } else {
                            share = 0;
                            child = child4;
                        }
                        child.measure(View.MeasureSpec.makeMeasureSpec(share, 1073741824), childHeightMeasureSpec);
                        childState = ViewUtils.combineMeasuredStates(childState, ViewCompat.getMeasuredState(child4) & (-16777216));
                    }
                    if (isExactly) {
                        this.mTotalLength += child4.getMeasuredWidth() + lp3.leftMargin + lp3.rightMargin + 0;
                    } else {
                        int totalLength4 = this.mTotalLength;
                        this.mTotalLength = Math.max(totalLength4, child4.getMeasuredWidth() + totalLength4 + lp3.leftMargin + lp3.rightMargin + 0);
                    }
                    boolean matchHeightLocally2 = heightMode != 1073741824 && lp3.height == -1;
                    int margin2 = lp3.topMargin + lp3.bottomMargin;
                    int childHeight2 = child4.getMeasuredHeight() + margin2;
                    maxHeight = Math.max(maxHeight, childHeight2);
                    if (!matchHeightLocally2) {
                        margin2 = childHeight2;
                    }
                    alternativeMaxHeight = Math.max(alternativeMaxHeight, margin2);
                    allFillParent = allFillParent && lp3.height == -1;
                    if (baselineAligned && (childBaseline = child4.getBaseline()) != -1) {
                        int index2 = ((((lp3.gravity < 0 ? this.mGravity : lp3.gravity) & 112) >> 4) & (-2)) >> 1;
                        maxAscent[index2] = Math.max(maxAscent[index2], childBaseline);
                        maxDescent[index2] = Math.max(maxDescent[index2], childHeight2 - childBaseline);
                    }
                }
            }
            this.mTotalLength += getPaddingLeft() + getPaddingRight();
            if (maxAscent[1] != -1 || maxAscent[0] != -1 || maxAscent[2] != -1 || maxAscent[3] != -1) {
                int ascent2 = Math.max(maxAscent[3], Math.max(maxAscent[0], Math.max(maxAscent[1], maxAscent[2])));
                int descent2 = Math.max(maxDescent[3], Math.max(maxDescent[0], Math.max(maxDescent[1], maxDescent[2])));
                maxHeight = Math.max(maxHeight, ascent2 + descent2);
            }
        } else {
            alternativeMaxHeight = Math.max(alternativeMaxHeight, weightedMaxHeight);
            if (useLargestChild && widthMode != 1073741824) {
                for (int i4 = 0; i4 < count; i4++) {
                    View child5 = getChildAt(i4);
                    if (child5 != null && child5.getVisibility() != 8 && ((LayoutParams) child5.getLayoutParams()).weight > 0.0f) {
                        child5.measure(View.MeasureSpec.makeMeasureSpec(largestChildWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(child5.getMeasuredHeight(), 1073741824));
                    }
                }
            }
        }
        if (!allFillParent && heightMode != 1073741824) {
            maxHeight = alternativeMaxHeight;
        }
        setMeasuredDimension(((-16777216) & childState) | widthSizeAndState, ViewCompat.resolveSizeAndState(Math.max(getPaddingTop() + getPaddingBottom() + maxHeight, getSuggestedMinimumHeight()), heightMeasureSpec, childState << 16));
        if (!matchHeight) {
            return;
        }
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
        int i5 = 0;
        while (true) {
            int i6 = i5;
            if (i6 >= count) {
                return;
            }
            View childAt = getChildAt(i6);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.height == -1) {
                    int i7 = layoutParams.width;
                    layoutParams.width = childAt.getMeasuredWidth();
                    measureChildWithMargins(childAt, widthMeasureSpec, 0, makeMeasureSpec, 0);
                    layoutParams.width = i7;
                }
            }
            i5 = i6 + 1;
        }
    }

    private static int getChildrenSkipCount$5359dca7() {
        return 0;
    }

    private void measureChildBeforeLayout$12802926(View child, int widthMeasureSpec, int totalWidth, int heightMeasureSpec, int totalHeight) {
        measureChildWithMargins(child, widthMeasureSpec, totalWidth, heightMeasureSpec, totalHeight);
    }

    private static int getLocationOffset$3c7ec8d0() {
        return 0;
    }

    private static int getNextLocationOffset$3c7ec8d0() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft;
        int i;
        int i2;
        int i3;
        int i4;
        int measuredHeight;
        int paddingTop;
        int i5;
        int i6;
        if (this.mOrientation == 1) {
            int paddingLeft2 = getPaddingLeft();
            int i7 = r - l;
            int paddingRight = i7 - getPaddingRight();
            int paddingRight2 = (i7 - paddingLeft2) - getPaddingRight();
            int virtualChildCount = getVirtualChildCount();
            int i8 = this.mGravity & 112;
            int i9 = 8388615 & this.mGravity;
            switch (i8) {
                case 16:
                    paddingTop = getPaddingTop() + (((b - t) - this.mTotalLength) / 2);
                    break;
                case 80:
                    paddingTop = ((getPaddingTop() + b) - t) - this.mTotalLength;
                    break;
                default:
                    paddingTop = getPaddingTop();
                    break;
            }
            int i10 = 0;
            int i11 = paddingTop;
            while (i10 < virtualChildCount) {
                View childAt = getChildAt(i10);
                if (childAt == null) {
                    i11 += 0;
                    i5 = i10;
                } else if (childAt.getVisibility() != 8) {
                    int measuredWidth = childAt.getMeasuredWidth();
                    int measuredHeight2 = childAt.getMeasuredHeight();
                    LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                    int i12 = layoutParams.gravity;
                    if (i12 < 0) {
                        i12 = i9;
                    }
                    switch (GravityCompat.getAbsoluteGravity(i12, ViewCompat.getLayoutDirection(this)) & 7) {
                        case 1:
                            i6 = ((((paddingRight2 - measuredWidth) / 2) + paddingLeft2) + layoutParams.leftMargin) - layoutParams.rightMargin;
                            break;
                        case 5:
                            i6 = (paddingRight - measuredWidth) - layoutParams.rightMargin;
                            break;
                        default:
                            i6 = layoutParams.leftMargin + paddingLeft2;
                            break;
                    }
                    if (hasDividerBeforeChildAt(i10)) {
                        i11 += this.mDividerHeight;
                    }
                    int i13 = i11 + layoutParams.topMargin;
                    setChildFrame(childAt, i6, i13 + 0, measuredWidth, measuredHeight2);
                    i11 = i13 + layoutParams.bottomMargin + measuredHeight2 + 0;
                    i5 = i10 + 0;
                } else {
                    i5 = i10;
                }
                i10 = i5 + 1;
            }
            return;
        }
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        int paddingTop2 = getPaddingTop();
        int i14 = b - t;
        int paddingBottom = i14 - getPaddingBottom();
        int paddingBottom2 = (i14 - paddingTop2) - getPaddingBottom();
        int virtualChildCount2 = getVirtualChildCount();
        int i15 = this.mGravity & 8388615;
        int i16 = this.mGravity & 112;
        boolean z = this.mBaselineAligned;
        int[] iArr = this.mMaxAscent;
        int[] iArr2 = this.mMaxDescent;
        switch (GravityCompat.getAbsoluteGravity(i15, ViewCompat.getLayoutDirection(this))) {
            case 1:
                paddingLeft = getPaddingLeft() + (((r - l) - this.mTotalLength) / 2);
                break;
            case 5:
                paddingLeft = ((getPaddingLeft() + r) - l) - this.mTotalLength;
                break;
            default:
                paddingLeft = getPaddingLeft();
                break;
        }
        if (!isLayoutRtl) {
            i = 0;
            i2 = 1;
        } else {
            i = virtualChildCount2 - 1;
            i2 = -1;
        }
        int i17 = 0;
        while (i17 < virtualChildCount2) {
            int i18 = i + (i2 * i17);
            View childAt2 = getChildAt(i18);
            if (childAt2 == null) {
                paddingLeft += 0;
                i3 = i17;
            } else if (childAt2.getVisibility() != 8) {
                int measuredWidth2 = childAt2.getMeasuredWidth();
                int measuredHeight3 = childAt2.getMeasuredHeight();
                int i19 = -1;
                LayoutParams layoutParams2 = (LayoutParams) childAt2.getLayoutParams();
                if (z && layoutParams2.height != -1) {
                    i19 = childAt2.getBaseline();
                }
                int i20 = layoutParams2.gravity;
                if (i20 < 0) {
                    i20 = i16;
                }
                switch (i20 & 112) {
                    case 16:
                        measuredHeight = ((((paddingBottom2 - measuredHeight3) / 2) + paddingTop2) + layoutParams2.topMargin) - layoutParams2.bottomMargin;
                        break;
                    case 48:
                        i4 = layoutParams2.topMargin + paddingTop2;
                        if (i19 != -1) {
                            measuredHeight = (iArr[1] - i19) + i4;
                            break;
                        }
                        break;
                    case 80:
                        i4 = (paddingBottom - measuredHeight3) - layoutParams2.bottomMargin;
                        if (i19 != -1) {
                            measuredHeight = i4 - (iArr2[2] - (childAt2.getMeasuredHeight() - i19));
                            break;
                        }
                        break;
                    default:
                        measuredHeight = paddingTop2;
                        break;
                }
                measuredHeight = i4;
                int i21 = (hasDividerBeforeChildAt(i18) ? this.mDividerWidth + paddingLeft : paddingLeft) + layoutParams2.leftMargin;
                setChildFrame(childAt2, i21 + 0, measuredHeight, measuredWidth2, measuredHeight3);
                paddingLeft = i21 + layoutParams2.rightMargin + measuredWidth2 + 0;
                i3 = i17 + 0;
            } else {
                i3 = i17;
            }
            i17 = i3 + 1;
        }
    }

    private static void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }

    public void setOrientation(int orientation) {
        if (this.mOrientation != orientation) {
            this.mOrientation = orientation;
            requestLayout();
        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            if ((8388615 & gravity) == 0) {
                gravity |= 8388611;
            }
            if ((gravity & 112) == 0) {
                gravity |= 48;
            }
            this.mGravity = gravity;
            requestLayout();
        }
    }

    public void setHorizontalGravity(int horizontalGravity) {
        int gravity = horizontalGravity & 8388615;
        if ((this.mGravity & 8388615) != gravity) {
            this.mGravity = (this.mGravity & (-8388616)) | gravity;
            requestLayout();
        }
    }

    public void setVerticalGravity(int verticalGravity) {
        int gravity = verticalGravity & 112;
        if ((this.mGravity & 112) != gravity) {
            this.mGravity = (this.mGravity & KeyboardEx.KEYCODE_RESIZE_FULL_SCREEN) | gravity;
            requestLayout();
        }
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation == 0) {
            return new LayoutParams(-2, -2);
        }
        if (this.mOrientation == 1) {
            return new LayoutParams(-1, -2);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityEvent(event);
            event.setClassName(LinearLayoutCompat.class.getName());
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName(LinearLayoutCompat.class.getName());
        }
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public int gravity;
        public float weight;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.gravity = -1;
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.LinearLayoutCompat_Layout);
            this.weight = a.getFloat(R.styleable.LinearLayoutCompat_Layout_android_layout_weight, 0.0f);
            this.gravity = a.getInt(R.styleable.LinearLayoutCompat_Layout_android_layout_gravity, -1);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.gravity = -1;
            this.weight = 0.0f;
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
            this.gravity = -1;
        }
    }
}
