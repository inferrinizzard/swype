package android.support.v7.widget;

import android.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.text.AllCapsTransformationMethod;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CompoundButton;

/* loaded from: classes.dex */
public class SwitchCompat extends CompoundButton {
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    private final AppCompatDrawableManager mDrawableManager;
    private boolean mHasThumbTint;
    private boolean mHasThumbTintMode;
    private boolean mHasTrackTint;
    private boolean mHasTrackTintMode;
    private int mMinFlingVelocity;
    private Layout mOffLayout;
    private Layout mOnLayout;
    private ThumbAnimation mPositionAnimator;
    private boolean mShowText;
    private boolean mSplitTrack;
    private int mSwitchBottom;
    private int mSwitchHeight;
    private int mSwitchLeft;
    private int mSwitchMinWidth;
    private int mSwitchPadding;
    private int mSwitchRight;
    private int mSwitchTop;
    private TransformationMethod mSwitchTransformationMethod;
    private int mSwitchWidth;
    private final Rect mTempRect;
    private ColorStateList mTextColors;
    private CharSequence mTextOff;
    private CharSequence mTextOn;
    private TextPaint mTextPaint;
    private Drawable mThumbDrawable;
    private float mThumbPosition;
    private int mThumbTextPadding;
    private ColorStateList mThumbTintList;
    private PorterDuff.Mode mThumbTintMode;
    private int mThumbWidth;
    private int mTouchMode;
    private int mTouchSlop;
    private float mTouchX;
    private float mTouchY;
    private Drawable mTrackDrawable;
    private ColorStateList mTrackTintList;
    private PorterDuff.Mode mTrackTintMode;
    private VelocityTracker mVelocityTracker;

    static /* synthetic */ ThumbAnimation access$102$3684b1bd(SwitchCompat x0) {
        x0.mPositionAnimator = null;
        return null;
    }

    public SwitchCompat(Context context) {
        this(context, null);
    }

    public SwitchCompat(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.switchStyle);
    }

    public SwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mThumbTintList = null;
        this.mThumbTintMode = null;
        this.mHasThumbTint = false;
        this.mHasThumbTintMode = false;
        this.mTrackTintList = null;
        this.mTrackTintMode = null;
        this.mHasTrackTint = false;
        this.mHasTrackTintMode = false;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mTempRect = new Rect();
        this.mTextPaint = new TextPaint(1);
        Resources res = getResources();
        this.mTextPaint.density = res.getDisplayMetrics().density;
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, android.support.v7.appcompat.R.styleable.SwitchCompat, defStyleAttr, 0);
        this.mThumbDrawable = a.getDrawable(android.support.v7.appcompat.R.styleable.SwitchCompat_android_thumb);
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setCallback(this);
        }
        this.mTrackDrawable = a.getDrawable(android.support.v7.appcompat.R.styleable.SwitchCompat_track);
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setCallback(this);
        }
        this.mTextOn = a.getText(android.support.v7.appcompat.R.styleable.SwitchCompat_android_textOn);
        this.mTextOff = a.getText(android.support.v7.appcompat.R.styleable.SwitchCompat_android_textOff);
        this.mShowText = a.getBoolean(android.support.v7.appcompat.R.styleable.SwitchCompat_showText, true);
        this.mThumbTextPadding = a.getDimensionPixelSize(android.support.v7.appcompat.R.styleable.SwitchCompat_thumbTextPadding, 0);
        this.mSwitchMinWidth = a.getDimensionPixelSize(android.support.v7.appcompat.R.styleable.SwitchCompat_switchMinWidth, 0);
        this.mSwitchPadding = a.getDimensionPixelSize(android.support.v7.appcompat.R.styleable.SwitchCompat_switchPadding, 0);
        this.mSplitTrack = a.getBoolean(android.support.v7.appcompat.R.styleable.SwitchCompat_splitTrack, false);
        ColorStateList thumbTintList = a.getColorStateList(android.support.v7.appcompat.R.styleable.SwitchCompat_thumbTint);
        if (thumbTintList != null) {
            this.mThumbTintList = thumbTintList;
            this.mHasThumbTint = true;
        }
        PorterDuff.Mode thumbTintMode = DrawableUtils.parseTintMode(a.getInt(android.support.v7.appcompat.R.styleable.SwitchCompat_thumbTintMode, -1), null);
        if (this.mThumbTintMode != thumbTintMode) {
            this.mThumbTintMode = thumbTintMode;
            this.mHasThumbTintMode = true;
        }
        if (this.mHasThumbTint || this.mHasThumbTintMode) {
            applyThumbTint();
        }
        ColorStateList trackTintList = a.getColorStateList(android.support.v7.appcompat.R.styleable.SwitchCompat_trackTint);
        if (trackTintList != null) {
            this.mTrackTintList = trackTintList;
            this.mHasTrackTint = true;
        }
        PorterDuff.Mode trackTintMode = DrawableUtils.parseTintMode(a.getInt(android.support.v7.appcompat.R.styleable.SwitchCompat_trackTintMode, -1), null);
        if (this.mTrackTintMode != trackTintMode) {
            this.mTrackTintMode = trackTintMode;
            this.mHasTrackTintMode = true;
        }
        if (this.mHasTrackTint || this.mHasTrackTintMode) {
            applyTrackTint();
        }
        int appearance = a.getResourceId(android.support.v7.appcompat.R.styleable.SwitchCompat_switchTextAppearance, 0);
        if (appearance != 0) {
            setSwitchTextAppearance(context, appearance);
        }
        this.mDrawableManager = AppCompatDrawableManager.get();
        a.mWrapped.recycle();
        ViewConfiguration config = ViewConfiguration.get(context);
        this.mTouchSlop = config.getScaledTouchSlop();
        this.mMinFlingVelocity = config.getScaledMinimumFlingVelocity();
        refreshDrawableState();
        setChecked(isChecked());
    }

    public void setSwitchTextAppearance(Context context, int resid) {
        Typeface typeface;
        TypedArray appearance = context.obtainStyledAttributes(resid, android.support.v7.appcompat.R.styleable.TextAppearance);
        ColorStateList colors = appearance.getColorStateList(android.support.v7.appcompat.R.styleable.TextAppearance_android_textColor);
        if (colors != null) {
            this.mTextColors = colors;
        } else {
            this.mTextColors = getTextColors();
        }
        int ts = appearance.getDimensionPixelSize(android.support.v7.appcompat.R.styleable.TextAppearance_android_textSize, 0);
        if (ts != 0 && ts != this.mTextPaint.getTextSize()) {
            this.mTextPaint.setTextSize(ts);
            requestLayout();
        }
        int typefaceIndex = appearance.getInt(android.support.v7.appcompat.R.styleable.TextAppearance_android_typeface, -1);
        int styleIndex = appearance.getInt(android.support.v7.appcompat.R.styleable.TextAppearance_android_textStyle, -1);
        switch (typefaceIndex) {
            case 1:
                typeface = Typeface.SANS_SERIF;
                break;
            case 2:
                typeface = Typeface.SERIF;
                break;
            case 3:
                typeface = Typeface.MONOSPACE;
                break;
            default:
                typeface = null;
                break;
        }
        setSwitchTypeface(typeface, styleIndex);
        if (appearance.getBoolean(android.support.v7.appcompat.R.styleable.TextAppearance_textAllCaps, false)) {
            this.mSwitchTransformationMethod = new AllCapsTransformationMethod(getContext());
        } else {
            this.mSwitchTransformationMethod = null;
        }
        appearance.recycle();
    }

    public void setSwitchTypeface(Typeface tf, int style) {
        Typeface tf2;
        if (style > 0) {
            if (tf == null) {
                tf2 = Typeface.defaultFromStyle(style);
            } else {
                tf2 = Typeface.create(tf, style);
            }
            setSwitchTypeface(tf2);
            int typefaceStyle = tf2 != null ? tf2.getStyle() : 0;
            int need = style & (typefaceStyle ^ (-1));
            this.mTextPaint.setFakeBoldText((need & 1) != 0);
            this.mTextPaint.setTextSkewX((need & 2) != 0 ? -0.25f : 0.0f);
            return;
        }
        this.mTextPaint.setFakeBoldText(false);
        this.mTextPaint.setTextSkewX(0.0f);
        setSwitchTypeface(tf);
    }

    public void setSwitchTypeface(Typeface tf) {
        if (this.mTextPaint.getTypeface() != tf) {
            this.mTextPaint.setTypeface(tf);
            requestLayout();
            invalidate();
        }
    }

    public void setSwitchPadding(int pixels) {
        this.mSwitchPadding = pixels;
        requestLayout();
    }

    public int getSwitchPadding() {
        return this.mSwitchPadding;
    }

    public void setSwitchMinWidth(int pixels) {
        this.mSwitchMinWidth = pixels;
        requestLayout();
    }

    public int getSwitchMinWidth() {
        return this.mSwitchMinWidth;
    }

    public void setThumbTextPadding(int pixels) {
        this.mThumbTextPadding = pixels;
        requestLayout();
    }

    public int getThumbTextPadding() {
        return this.mThumbTextPadding;
    }

    public void setTrackDrawable(Drawable track) {
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setCallback(null);
        }
        this.mTrackDrawable = track;
        if (track != null) {
            track.setCallback(this);
        }
        requestLayout();
    }

    public void setTrackResource(int resId) {
        setTrackDrawable(this.mDrawableManager.getDrawable(getContext(), resId, false));
    }

    public Drawable getTrackDrawable() {
        return this.mTrackDrawable;
    }

    public void setTrackTintList(ColorStateList tint) {
        this.mTrackTintList = tint;
        this.mHasTrackTint = true;
        applyTrackTint();
    }

    public ColorStateList getTrackTintList() {
        return this.mTrackTintList;
    }

    public void setTrackTintMode(PorterDuff.Mode tintMode) {
        this.mTrackTintMode = tintMode;
        this.mHasTrackTintMode = true;
        applyTrackTint();
    }

    public PorterDuff.Mode getTrackTintMode() {
        return this.mTrackTintMode;
    }

    private void applyTrackTint() {
        if (this.mTrackDrawable != null) {
            if (this.mHasTrackTint || this.mHasTrackTintMode) {
                this.mTrackDrawable = this.mTrackDrawable.mutate();
                if (this.mHasTrackTint) {
                    DrawableCompat.setTintList(this.mTrackDrawable, this.mTrackTintList);
                }
                if (this.mHasTrackTintMode) {
                    DrawableCompat.setTintMode(this.mTrackDrawable, this.mTrackTintMode);
                }
                if (this.mTrackDrawable.isStateful()) {
                    this.mTrackDrawable.setState(getDrawableState());
                }
            }
        }
    }

    public void setThumbDrawable(Drawable thumb) {
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setCallback(null);
        }
        this.mThumbDrawable = thumb;
        if (thumb != null) {
            thumb.setCallback(this);
        }
        requestLayout();
    }

    public void setThumbResource(int resId) {
        setThumbDrawable(this.mDrawableManager.getDrawable(getContext(), resId, false));
    }

    public Drawable getThumbDrawable() {
        return this.mThumbDrawable;
    }

    public void setThumbTintList(ColorStateList tint) {
        this.mThumbTintList = tint;
        this.mHasThumbTint = true;
        applyThumbTint();
    }

    public ColorStateList getThumbTintList() {
        return this.mThumbTintList;
    }

    public void setThumbTintMode(PorterDuff.Mode tintMode) {
        this.mThumbTintMode = tintMode;
        this.mHasThumbTintMode = true;
        applyThumbTint();
    }

    public PorterDuff.Mode getThumbTintMode() {
        return this.mThumbTintMode;
    }

    private void applyThumbTint() {
        if (this.mThumbDrawable != null) {
            if (this.mHasThumbTint || this.mHasThumbTintMode) {
                this.mThumbDrawable = this.mThumbDrawable.mutate();
                if (this.mHasThumbTint) {
                    DrawableCompat.setTintList(this.mThumbDrawable, this.mThumbTintList);
                }
                if (this.mHasThumbTintMode) {
                    DrawableCompat.setTintMode(this.mThumbDrawable, this.mThumbTintMode);
                }
                if (this.mThumbDrawable.isStateful()) {
                    this.mThumbDrawable.setState(getDrawableState());
                }
            }
        }
    }

    public void setSplitTrack(boolean splitTrack) {
        this.mSplitTrack = splitTrack;
        invalidate();
    }

    public boolean getSplitTrack() {
        return this.mSplitTrack;
    }

    public CharSequence getTextOn() {
        return this.mTextOn;
    }

    public void setTextOn(CharSequence textOn) {
        this.mTextOn = textOn;
        requestLayout();
    }

    public CharSequence getTextOff() {
        return this.mTextOff;
    }

    public void setTextOff(CharSequence textOff) {
        this.mTextOff = textOff;
        requestLayout();
    }

    public void setShowText(boolean showText) {
        if (this.mShowText != showText) {
            this.mShowText = showText;
            requestLayout();
        }
    }

    public boolean getShowText() {
        return this.mShowText;
    }

    @Override // android.widget.TextView, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int thumbWidth;
        int thumbHeight;
        int maxTextWidth;
        int trackHeight;
        if (this.mShowText) {
            if (this.mOnLayout == null) {
                this.mOnLayout = makeLayout(this.mTextOn);
            }
            if (this.mOffLayout == null) {
                this.mOffLayout = makeLayout(this.mTextOff);
            }
        }
        Rect padding = this.mTempRect;
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.getPadding(padding);
            thumbWidth = (this.mThumbDrawable.getIntrinsicWidth() - padding.left) - padding.right;
            thumbHeight = this.mThumbDrawable.getIntrinsicHeight();
        } else {
            thumbWidth = 0;
            thumbHeight = 0;
        }
        if (this.mShowText) {
            maxTextWidth = Math.max(this.mOnLayout.getWidth(), this.mOffLayout.getWidth()) + (this.mThumbTextPadding * 2);
        } else {
            maxTextWidth = 0;
        }
        this.mThumbWidth = Math.max(maxTextWidth, thumbWidth);
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.getPadding(padding);
            trackHeight = this.mTrackDrawable.getIntrinsicHeight();
        } else {
            padding.setEmpty();
            trackHeight = 0;
        }
        int paddingLeft = padding.left;
        int paddingRight = padding.right;
        if (this.mThumbDrawable != null) {
            Rect inset = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
            paddingLeft = Math.max(paddingLeft, inset.left);
            paddingRight = Math.max(paddingRight, inset.right);
        }
        int switchWidth = Math.max(this.mSwitchMinWidth, (this.mThumbWidth * 2) + paddingLeft + paddingRight);
        int switchHeight = Math.max(trackHeight, thumbHeight);
        this.mSwitchWidth = switchWidth;
        this.mSwitchHeight = switchHeight;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() < switchHeight) {
            setMeasuredDimension(ViewCompat.getMeasuredWidthAndState(this), switchHeight);
        }
    }

    @Override // android.view.View
    @TargetApi(14)
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        CharSequence text = isChecked() ? this.mTextOn : this.mTextOff;
        if (text != null) {
            event.getText().add(text);
        }
    }

    private Layout makeLayout(CharSequence text) {
        CharSequence transformed = this.mSwitchTransformationMethod != null ? this.mSwitchTransformationMethod.getTransformation(text, this) : text;
        return new StaticLayout(transformed, this.mTextPaint, transformed != null ? (int) Math.ceil(Layout.getDesiredWidth(transformed, this.mTextPaint)) : 0, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:22:0x0074. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x000f. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:3:0x0012 A[FALL_THROUGH] */
    @Override // android.widget.TextView, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouchEvent(android.view.MotionEvent r14) {
        /*
            Method dump skipped, instructions count: 372
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.SwitchCompat.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void cancelPositionAnimator() {
        if (this.mPositionAnimator != null) {
            clearAnimation();
            this.mPositionAnimator = null;
        }
    }

    private boolean getTargetCheckedState() {
        return this.mThumbPosition > 0.5f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setThumbPosition(float position) {
        this.mThumbPosition = position;
        invalidate();
    }

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        final boolean checked2 = isChecked();
        if (getWindowToken() == null || !ViewCompat.isLaidOut(this) || !isShown()) {
            cancelPositionAnimator();
            setThumbPosition(checked2 ? 1.0f : 0.0f);
            return;
        }
        if (this.mPositionAnimator != null) {
            cancelPositionAnimator();
        }
        this.mPositionAnimator = new ThumbAnimation(this, this.mThumbPosition, checked2 ? 1.0f : 0.0f, (byte) 0);
        this.mPositionAnimator.setDuration(250L);
        this.mPositionAnimator.setAnimationListener(new Animation.AnimationListener() { // from class: android.support.v7.widget.SwitchCompat.1
            @Override // android.view.animation.Animation.AnimationListener
            public final void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public final void onAnimationEnd(Animation animation) {
                if (SwitchCompat.this.mPositionAnimator == animation) {
                    SwitchCompat.this.setThumbPosition(checked2 ? 1.0f : 0.0f);
                    SwitchCompat.access$102$3684b1bd(SwitchCompat.this);
                }
            }

            @Override // android.view.animation.Animation.AnimationListener
            public final void onAnimationRepeat(Animation animation) {
            }
        });
        startAnimation(this.mPositionAnimator);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int switchRight;
        int switchLeft;
        int switchBottom;
        int switchTop;
        super.onLayout(changed, left, top, right, bottom);
        int opticalInsetLeft = 0;
        int opticalInsetRight = 0;
        if (this.mThumbDrawable != null) {
            Rect trackPadding = this.mTempRect;
            if (this.mTrackDrawable != null) {
                this.mTrackDrawable.getPadding(trackPadding);
            } else {
                trackPadding.setEmpty();
            }
            Rect insets = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
            opticalInsetLeft = Math.max(0, insets.left - trackPadding.left);
            opticalInsetRight = Math.max(0, insets.right - trackPadding.right);
        }
        if (ViewUtils.isLayoutRtl(this)) {
            switchLeft = getPaddingLeft() + opticalInsetLeft;
            switchRight = ((this.mSwitchWidth + switchLeft) - opticalInsetLeft) - opticalInsetRight;
        } else {
            switchRight = (getWidth() - getPaddingRight()) - opticalInsetRight;
            switchLeft = (switchRight - this.mSwitchWidth) + opticalInsetLeft + opticalInsetRight;
        }
        switch (getGravity() & 112) {
            case 16:
                switchTop = (((getPaddingTop() + getHeight()) - getPaddingBottom()) / 2) - (this.mSwitchHeight / 2);
                switchBottom = switchTop + this.mSwitchHeight;
                break;
            case 80:
                switchBottom = getHeight() - getPaddingBottom();
                switchTop = switchBottom - this.mSwitchHeight;
                break;
            default:
                switchTop = getPaddingTop();
                switchBottom = switchTop + this.mSwitchHeight;
                break;
        }
        this.mSwitchLeft = switchLeft;
        this.mSwitchTop = switchTop;
        this.mSwitchBottom = switchBottom;
        this.mSwitchRight = switchRight;
    }

    @Override // android.view.View
    public void draw(Canvas c) {
        Rect thumbInsets;
        Rect padding = this.mTempRect;
        int switchLeft = this.mSwitchLeft;
        int switchTop = this.mSwitchTop;
        int switchRight = this.mSwitchRight;
        int switchBottom = this.mSwitchBottom;
        int thumbInitialLeft = switchLeft + getThumbOffset();
        if (this.mThumbDrawable != null) {
            thumbInsets = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
        } else {
            thumbInsets = DrawableUtils.INSETS_NONE;
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.getPadding(padding);
            thumbInitialLeft += padding.left;
            int trackLeft = switchLeft;
            int trackTop = switchTop;
            int trackRight = switchRight;
            int trackBottom = switchBottom;
            if (thumbInsets != null) {
                if (thumbInsets.left > padding.left) {
                    trackLeft = switchLeft + (thumbInsets.left - padding.left);
                }
                if (thumbInsets.top > padding.top) {
                    trackTop = switchTop + (thumbInsets.top - padding.top);
                }
                if (thumbInsets.right > padding.right) {
                    trackRight = switchRight - (thumbInsets.right - padding.right);
                }
                if (thumbInsets.bottom > padding.bottom) {
                    trackBottom = switchBottom - (thumbInsets.bottom - padding.bottom);
                }
            }
            this.mTrackDrawable.setBounds(trackLeft, trackTop, trackRight, trackBottom);
        }
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.getPadding(padding);
            int thumbLeft = thumbInitialLeft - padding.left;
            int thumbRight = this.mThumbWidth + thumbInitialLeft + padding.right;
            this.mThumbDrawable.setBounds(thumbLeft, switchTop, thumbRight, switchBottom);
            Drawable background = getBackground();
            if (background != null) {
                DrawableCompat.setHotspotBounds(background, thumbLeft, switchTop, thumbRight, switchBottom);
            }
        }
        super.draw(c);
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        int cX;
        super.onDraw(canvas);
        Rect padding = this.mTempRect;
        Drawable trackDrawable = this.mTrackDrawable;
        if (trackDrawable != null) {
            trackDrawable.getPadding(padding);
        } else {
            padding.setEmpty();
        }
        int switchTop = this.mSwitchTop;
        int switchBottom = this.mSwitchBottom;
        int switchInnerTop = switchTop + padding.top;
        int switchInnerBottom = switchBottom - padding.bottom;
        Drawable thumbDrawable = this.mThumbDrawable;
        if (trackDrawable != null) {
            if (this.mSplitTrack && thumbDrawable != null) {
                Rect insets = DrawableUtils.getOpticalBounds(thumbDrawable);
                thumbDrawable.copyBounds(padding);
                padding.left += insets.left;
                padding.right -= insets.right;
                int saveCount = canvas.save();
                canvas.clipRect(padding, Region.Op.DIFFERENCE);
                trackDrawable.draw(canvas);
                canvas.restoreToCount(saveCount);
            } else {
                trackDrawable.draw(canvas);
            }
        }
        int saveCount2 = canvas.save();
        if (thumbDrawable != null) {
            thumbDrawable.draw(canvas);
        }
        Layout switchText = getTargetCheckedState() ? this.mOnLayout : this.mOffLayout;
        if (switchText != null) {
            int[] drawableState = getDrawableState();
            if (this.mTextColors != null) {
                this.mTextPaint.setColor(this.mTextColors.getColorForState(drawableState, 0));
            }
            this.mTextPaint.drawableState = drawableState;
            if (thumbDrawable != null) {
                Rect bounds = thumbDrawable.getBounds();
                cX = bounds.left + bounds.right;
            } else {
                cX = getWidth();
            }
            int left = (cX / 2) - (switchText.getWidth() / 2);
            int top = ((switchInnerTop + switchInnerBottom) / 2) - (switchText.getHeight() / 2);
            canvas.translate(left, top);
            switchText.draw(canvas);
        }
        canvas.restoreToCount(saveCount2);
    }

    @Override // android.widget.CompoundButton, android.widget.TextView
    public int getCompoundPaddingLeft() {
        if (!ViewUtils.isLayoutRtl(this)) {
            return super.getCompoundPaddingLeft();
        }
        int padding = super.getCompoundPaddingLeft() + this.mSwitchWidth;
        if (!TextUtils.isEmpty(getText())) {
            return padding + this.mSwitchPadding;
        }
        return padding;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView
    public int getCompoundPaddingRight() {
        if (ViewUtils.isLayoutRtl(this)) {
            return super.getCompoundPaddingRight();
        }
        int padding = super.getCompoundPaddingRight() + this.mSwitchWidth;
        if (!TextUtils.isEmpty(getText())) {
            return padding + this.mSwitchPadding;
        }
        return padding;
    }

    private int getThumbOffset() {
        float thumbPosition;
        if (ViewUtils.isLayoutRtl(this)) {
            thumbPosition = 1.0f - this.mThumbPosition;
        } else {
            thumbPosition = this.mThumbPosition;
        }
        return (int) ((getThumbScrollRange() * thumbPosition) + 0.5f);
    }

    private int getThumbScrollRange() {
        Rect insets;
        if (this.mTrackDrawable != null) {
            Rect padding = this.mTempRect;
            this.mTrackDrawable.getPadding(padding);
            if (this.mThumbDrawable != null) {
                insets = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
            } else {
                insets = DrawableUtils.INSETS_NONE;
            }
            return ((((this.mSwitchWidth - this.mThumbWidth) - padding.left) - padding.right) - insets.left) - insets.right;
        }
        return 0;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] state = getDrawableState();
        boolean changed = false;
        Drawable thumbDrawable = this.mThumbDrawable;
        if (thumbDrawable != null && thumbDrawable.isStateful()) {
            changed = thumbDrawable.setState(state) | false;
        }
        Drawable trackDrawable = this.mTrackDrawable;
        if (trackDrawable != null && trackDrawable.isStateful()) {
            changed |= trackDrawable.setState(state);
        }
        if (changed) {
            invalidate();
        }
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public void drawableHotspotChanged(float x, float y) {
        if (Build.VERSION.SDK_INT >= 21) {
            super.drawableHotspotChanged(x, y);
        }
        if (this.mThumbDrawable != null) {
            DrawableCompat.setHotspot(this.mThumbDrawable, x, y);
        }
        if (this.mTrackDrawable != null) {
            DrawableCompat.setHotspot(this.mTrackDrawable, x, y);
        }
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mThumbDrawable || who == this.mTrackDrawable;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public void jumpDrawablesToCurrentState() {
        if (Build.VERSION.SDK_INT >= 11) {
            super.jumpDrawablesToCurrentState();
            if (this.mThumbDrawable != null) {
                this.mThumbDrawable.jumpToCurrentState();
            }
            if (this.mTrackDrawable != null) {
                this.mTrackDrawable.jumpToCurrentState();
            }
            cancelPositionAnimator();
            setThumbPosition(isChecked() ? 1.0f : 0.0f);
        }
    }

    @Override // android.view.View
    @TargetApi(14)
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName("android.widget.Switch");
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName("android.widget.Switch");
            CharSequence switchText = isChecked() ? this.mTextOn : this.mTextOff;
            if (!TextUtils.isEmpty(switchText)) {
                CharSequence oldText = info.getText();
                if (TextUtils.isEmpty(oldText)) {
                    info.setText(switchText);
                    return;
                }
                StringBuilder newText = new StringBuilder();
                newText.append(oldText).append(' ').append(switchText);
                info.setText(newText);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ThumbAnimation extends Animation {
        final float mDiff;
        final float mEndPosition;
        final float mStartPosition;

        /* synthetic */ ThumbAnimation(SwitchCompat x0, float x1, float x2, byte b) {
            this(x1, x2);
        }

        private ThumbAnimation(float startPosition, float endPosition) {
            this.mStartPosition = startPosition;
            this.mEndPosition = endPosition;
            this.mDiff = endPosition - startPosition;
        }

        @Override // android.view.animation.Animation
        protected final void applyTransformation(float interpolatedTime, Transformation t) {
            SwitchCompat.this.setThumbPosition(this.mStartPosition + (this.mDiff * interpolatedTime));
        }
    }
}
