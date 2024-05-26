package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class MaterialProgressDrawable extends Drawable implements Animatable {
    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private static final Interpolator MATERIAL_INTERPOLATOR = new FastOutSlowInInterpolator();
    private Animation mAnimation;
    boolean mFinishing;
    private double mHeight;
    private View mParent;
    private Resources mResources;
    private float mRotation;
    private float mRotationCount;
    private double mWidth;
    private final int[] COLORS = {-16777216};
    private final ArrayList<Animation> mAnimators = new ArrayList<>();
    private final Drawable.Callback mCallback = new Drawable.Callback() { // from class: android.support.v4.widget.MaterialProgressDrawable.3
        @Override // android.graphics.drawable.Drawable.Callback
        public final void invalidateDrawable(Drawable d) {
            MaterialProgressDrawable.this.invalidateSelf();
        }

        @Override // android.graphics.drawable.Drawable.Callback
        public final void scheduleDrawable(Drawable d, Runnable what, long when) {
            MaterialProgressDrawable.this.scheduleSelf(what, when);
        }

        @Override // android.graphics.drawable.Drawable.Callback
        public final void unscheduleDrawable(Drawable d, Runnable what) {
            MaterialProgressDrawable.this.unscheduleSelf(what);
        }
    };
    final Ring mRing = new Ring(this.mCallback);

    public MaterialProgressDrawable(Context context, View parent) {
        this.mParent = parent;
        this.mResources = context.getResources();
        this.mRing.setColors(this.COLORS);
        updateSizes(1);
        final Ring ring = this.mRing;
        Animation animation = new Animation() { // from class: android.support.v4.widget.MaterialProgressDrawable.1
            @Override // android.view.animation.Animation
            public final void applyTransformation(float interpolatedTime, Transformation t) {
                if (!MaterialProgressDrawable.this.mFinishing) {
                    float minProgressArc = MaterialProgressDrawable.getMinProgressArc(ring);
                    float startingEndTrim = ring.mStartingEndTrim;
                    float startingTrim = ring.mStartingStartTrim;
                    float startingRotation = ring.mStartingRotation;
                    MaterialProgressDrawable.updateRingColor(interpolatedTime, ring);
                    if (interpolatedTime <= 0.5f) {
                        float scaledTime = interpolatedTime / 0.5f;
                        float startTrim = startingTrim + ((0.8f - minProgressArc) * MaterialProgressDrawable.MATERIAL_INTERPOLATOR.getInterpolation(scaledTime));
                        ring.setStartTrim(startTrim);
                    }
                    if (interpolatedTime > 0.5f) {
                        float minArc = 0.8f - minProgressArc;
                        float scaledTime2 = (interpolatedTime - 0.5f) / 0.5f;
                        float endTrim = startingEndTrim + (MaterialProgressDrawable.MATERIAL_INTERPOLATOR.getInterpolation(scaledTime2) * minArc);
                        ring.setEndTrim(endTrim);
                    }
                    float rotation = startingRotation + (0.25f * interpolatedTime);
                    ring.setRotation(rotation);
                    float groupRotation = (216.0f * interpolatedTime) + (1080.0f * (MaterialProgressDrawable.this.mRotationCount / 5.0f));
                    MaterialProgressDrawable.this.setRotation(groupRotation);
                    return;
                }
                MaterialProgressDrawable.access$000$7dc75d4f(interpolatedTime, ring);
            }
        };
        animation.setRepeatCount(-1);
        animation.setRepeatMode(1);
        animation.setInterpolator(LINEAR_INTERPOLATOR);
        animation.setAnimationListener(new Animation.AnimationListener() { // from class: android.support.v4.widget.MaterialProgressDrawable.2
            @Override // android.view.animation.Animation.AnimationListener
            public final void onAnimationStart(Animation animation2) {
                MaterialProgressDrawable.this.mRotationCount = 0.0f;
            }

            @Override // android.view.animation.Animation.AnimationListener
            public final void onAnimationEnd(Animation animation2) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public final void onAnimationRepeat(Animation animation2) {
                ring.storeOriginals();
                Ring ring2 = ring;
                ring2.setColorIndex(ring2.getNextColorIndex());
                ring.setStartTrim(ring.mEndTrim);
                if (MaterialProgressDrawable.this.mFinishing) {
                    MaterialProgressDrawable.this.mFinishing = false;
                    animation2.setDuration(1332L);
                    ring.setShowArrow(false);
                } else {
                    MaterialProgressDrawable.this.mRotationCount = (MaterialProgressDrawable.this.mRotationCount + 1.0f) % 5.0f;
                }
            }
        });
        this.mAnimation = animation;
    }

    private void setSizeParameters(double progressCircleWidth, double progressCircleHeight, double centerRadius, double strokeWidth, float arrowWidth, float arrowHeight) {
        float ceil;
        Ring ring = this.mRing;
        float screenDensity = this.mResources.getDisplayMetrics().density;
        this.mWidth = screenDensity * progressCircleWidth;
        this.mHeight = screenDensity * progressCircleHeight;
        float f = ((float) strokeWidth) * screenDensity;
        ring.mStrokeWidth = f;
        ring.mPaint.setStrokeWidth(f);
        ring.invalidateSelf();
        ring.mRingCenterRadius = screenDensity * centerRadius;
        ring.setColorIndex(0);
        ring.mArrowWidth = (int) (arrowWidth * screenDensity);
        ring.mArrowHeight = (int) (arrowHeight * screenDensity);
        float min = Math.min((int) this.mWidth, (int) this.mHeight);
        if (ring.mRingCenterRadius <= 0.0d || min < 0.0f) {
            ceil = (float) Math.ceil(ring.mStrokeWidth / 2.0f);
        } else {
            ceil = (float) ((min / 2.0f) - ring.mRingCenterRadius);
        }
        ring.mStrokeInset = ceil;
    }

    public final void updateSizes(int size) {
        if (size == 0) {
            setSizeParameters(56.0d, 56.0d, 12.5d, 3.0d, 12.0f, 6.0f);
        } else {
            setSizeParameters(40.0d, 40.0d, 8.75d, 2.5d, 10.0f, 5.0f);
        }
    }

    public final void showArrow(boolean show) {
        this.mRing.setShowArrow(show);
    }

    public final void setArrowScale(float scale) {
        Ring ring = this.mRing;
        if (scale == ring.mArrowScale) {
            return;
        }
        ring.mArrowScale = scale;
        ring.invalidateSelf();
    }

    public final void setStartEndTrim$2548a35(float endAngle) {
        this.mRing.setStartTrim(0.0f);
        this.mRing.setEndTrim(endAngle);
    }

    public final void setBackgroundColor(int color) {
        this.mRing.mBackgroundColor = color;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicHeight() {
        return (int) this.mHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicWidth() {
        return (int) this.mWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public final void draw(Canvas c) {
        Rect bounds = getBounds();
        int saveCount = c.save();
        c.rotate(this.mRotation, bounds.exactCenterX(), bounds.exactCenterY());
        Ring ring = this.mRing;
        RectF rectF = ring.mTempBounds;
        rectF.set(bounds);
        rectF.inset(ring.mStrokeInset, ring.mStrokeInset);
        float f = 360.0f * (ring.mStartTrim + ring.mRotation);
        float f2 = ((ring.mEndTrim + ring.mRotation) * 360.0f) - f;
        ring.mPaint.setColor(ring.mCurrentColor);
        c.drawArc(rectF, f, f2, false, ring.mPaint);
        if (ring.mShowArrow) {
            if (ring.mArrow == null) {
                ring.mArrow = new Path();
                ring.mArrow.setFillType(Path.FillType.EVEN_ODD);
            } else {
                ring.mArrow.reset();
            }
            float f3 = (((int) ring.mStrokeInset) / 2) * ring.mArrowScale;
            float cos = (float) ((ring.mRingCenterRadius * Math.cos(0.0d)) + bounds.exactCenterX());
            float sin = (float) ((ring.mRingCenterRadius * Math.sin(0.0d)) + bounds.exactCenterY());
            ring.mArrow.moveTo(0.0f, 0.0f);
            ring.mArrow.lineTo(ring.mArrowWidth * ring.mArrowScale, 0.0f);
            ring.mArrow.lineTo((ring.mArrowWidth * ring.mArrowScale) / 2.0f, ring.mArrowHeight * ring.mArrowScale);
            ring.mArrow.offset(cos - f3, sin);
            ring.mArrow.close();
            ring.mArrowPaint.setColor(ring.mCurrentColor);
            c.rotate((f + f2) - 5.0f, bounds.exactCenterX(), bounds.exactCenterY());
            c.drawPath(ring.mArrow, ring.mArrowPaint);
        }
        if (ring.mAlpha < 255) {
            ring.mCirclePaint.setColor(ring.mBackgroundColor);
            ring.mCirclePaint.setAlpha(255 - ring.mAlpha);
            c.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), bounds.width() / 2, ring.mCirclePaint);
        }
        c.restoreToCount(saveCount);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int alpha) {
        this.mRing.mAlpha = alpha;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getAlpha() {
        return this.mRing.mAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter colorFilter) {
        Ring ring = this.mRing;
        ring.mPaint.setColorFilter(colorFilter);
        ring.invalidateSelf();
    }

    final void setRotation(float rotation) {
        this.mRotation = rotation;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Animatable
    public final boolean isRunning() {
        ArrayList<Animation> animators = this.mAnimators;
        int N = animators.size();
        for (int i = 0; i < N; i++) {
            Animation animator = animators.get(i);
            if (animator.hasStarted() && !animator.hasEnded()) {
                return true;
            }
        }
        return false;
    }

    @Override // android.graphics.drawable.Animatable
    public final void start() {
        this.mAnimation.reset();
        this.mRing.storeOriginals();
        if (this.mRing.mEndTrim != this.mRing.mStartTrim) {
            this.mFinishing = true;
            this.mAnimation.setDuration(666L);
            this.mParent.startAnimation(this.mAnimation);
        } else {
            this.mRing.setColorIndex(0);
            this.mRing.resetOriginals();
            this.mAnimation.setDuration(1332L);
            this.mParent.startAnimation(this.mAnimation);
        }
    }

    @Override // android.graphics.drawable.Animatable
    public final void stop() {
        this.mParent.clearAnimation();
        setRotation(0.0f);
        this.mRing.setShowArrow(false);
        this.mRing.setColorIndex(0);
        this.mRing.resetOriginals();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateRingColor(float interpolatedTime, Ring ring) {
        if (interpolatedTime > 0.75f) {
            float f = (interpolatedTime - 0.75f) / 0.25f;
            int i = ring.mColors[ring.mColorIndex];
            int i2 = ring.mColors[ring.getNextColorIndex()];
            int intValue = Integer.valueOf(i).intValue();
            int i3 = (intValue >> 24) & 255;
            int i4 = (intValue >> 16) & 255;
            int i5 = (intValue >> 8) & 255;
            int intValue2 = Integer.valueOf(i2).intValue();
            ring.mCurrentColor = (((int) (f * ((intValue2 & 255) - r1))) + (intValue & 255)) | ((i3 + ((int) ((((intValue2 >> 24) & 255) - i3) * f))) << 24) | ((i4 + ((int) ((((intValue2 >> 16) & 255) - i4) * f))) << 16) | ((((int) ((((intValue2 >> 8) & 255) - i5) * f)) + i5) << 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Ring {
        int mAlpha;
        Path mArrow;
        int mArrowHeight;
        float mArrowScale;
        int mArrowWidth;
        int mBackgroundColor;
        private final Drawable.Callback mCallback;
        int mColorIndex;
        int[] mColors;
        int mCurrentColor;
        double mRingCenterRadius;
        boolean mShowArrow;
        float mStartingEndTrim;
        float mStartingRotation;
        float mStartingStartTrim;
        final RectF mTempBounds = new RectF();
        final Paint mPaint = new Paint();
        final Paint mArrowPaint = new Paint();
        float mStartTrim = 0.0f;
        float mEndTrim = 0.0f;
        float mRotation = 0.0f;
        float mStrokeWidth = 5.0f;
        float mStrokeInset = 2.5f;
        final Paint mCirclePaint = new Paint(1);

        public Ring(Drawable.Callback callback) {
            this.mCallback = callback;
            this.mPaint.setStrokeCap(Paint.Cap.SQUARE);
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mArrowPaint.setStyle(Paint.Style.FILL);
            this.mArrowPaint.setAntiAlias(true);
        }

        public final void setColors(int[] colors) {
            this.mColors = colors;
            setColorIndex(0);
        }

        public final void setColorIndex(int index) {
            this.mColorIndex = index;
            this.mCurrentColor = this.mColors[this.mColorIndex];
        }

        final int getNextColorIndex() {
            return (this.mColorIndex + 1) % this.mColors.length;
        }

        public final void setStartTrim(float startTrim) {
            this.mStartTrim = startTrim;
            invalidateSelf();
        }

        public final void setEndTrim(float endTrim) {
            this.mEndTrim = endTrim;
            invalidateSelf();
        }

        public final void setRotation(float rotation) {
            this.mRotation = rotation;
            invalidateSelf();
        }

        public final void setShowArrow(boolean show) {
            if (this.mShowArrow != show) {
                this.mShowArrow = show;
                invalidateSelf();
            }
        }

        public final void storeOriginals() {
            this.mStartingStartTrim = this.mStartTrim;
            this.mStartingEndTrim = this.mEndTrim;
            this.mStartingRotation = this.mRotation;
        }

        public final void resetOriginals() {
            this.mStartingStartTrim = 0.0f;
            this.mStartingEndTrim = 0.0f;
            this.mStartingRotation = 0.0f;
            setStartTrim(0.0f);
            setEndTrim(0.0f);
            setRotation(0.0f);
        }

        final void invalidateSelf() {
            this.mCallback.invalidateDrawable(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float getMinProgressArc(Ring ring) {
        return (float) Math.toRadians(ring.mStrokeWidth / (6.283185307179586d * ring.mRingCenterRadius));
    }

    static /* synthetic */ void access$000$7dc75d4f(float x1, Ring x2) {
        updateRingColor(x1, x2);
        float floor = (float) (Math.floor(x2.mStartingRotation / 0.8f) + 1.0d);
        x2.setStartTrim((((x2.mStartingEndTrim - getMinProgressArc(x2)) - x2.mStartingStartTrim) * x1) + x2.mStartingStartTrim);
        x2.setEndTrim(x2.mStartingEndTrim);
        x2.setRotation(((floor - x2.mStartingRotation) * x1) + x2.mStartingRotation);
    }
}
