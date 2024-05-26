package android.support.v4.widget;

import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

/* loaded from: classes.dex */
public abstract class AutoScrollHelper implements View.OnTouchListener {
    private static final int DEFAULT_ACTIVATION_DELAY = ViewConfiguration.getTapTimeout();
    private int mActivationDelay;
    private boolean mAlreadyDelayed;
    private boolean mAnimating;
    private int mEdgeType;
    private boolean mEnabled;
    private boolean mExclusive;
    private boolean mNeedsCancel;
    private boolean mNeedsReset;
    private Runnable mRunnable;
    private final View mTarget;
    private final ClampedScroller mScroller = new ClampedScroller();
    private final Interpolator mEdgeInterpolator = new AccelerateInterpolator();
    private float[] mRelativeEdges = {0.0f, 0.0f};
    private float[] mMaximumEdges = {Float.MAX_VALUE, Float.MAX_VALUE};
    private float[] mRelativeVelocity = {0.0f, 0.0f};
    private float[] mMinimumVelocity = {0.0f, 0.0f};
    private float[] mMaximumVelocity = {Float.MAX_VALUE, Float.MAX_VALUE};

    public abstract boolean canTargetScrollVertically(int i);

    public abstract void scrollTargetBy$255f295(int i);

    static /* synthetic */ boolean access$102$2149d4c8(AutoScrollHelper x0) {
        x0.mAnimating = false;
        return false;
    }

    static /* synthetic */ boolean access$202$2149d4c8(AutoScrollHelper x0) {
        x0.mNeedsReset = false;
        return false;
    }

    static /* synthetic */ boolean access$502$2149d4c8(AutoScrollHelper x0) {
        x0.mNeedsCancel = false;
        return false;
    }

    public AutoScrollHelper(View target) {
        this.mTarget = target;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int maxVelocity = (int) ((1575.0f * metrics.density) + 0.5f);
        int minVelocity = (int) ((315.0f * metrics.density) + 0.5f);
        float f = maxVelocity;
        this.mMaximumVelocity[0] = f / 1000.0f;
        this.mMaximumVelocity[1] = f / 1000.0f;
        float f2 = minVelocity;
        this.mMinimumVelocity[0] = f2 / 1000.0f;
        this.mMinimumVelocity[1] = f2 / 1000.0f;
        this.mEdgeType = 1;
        this.mMaximumEdges[0] = Float.MAX_VALUE;
        this.mMaximumEdges[1] = Float.MAX_VALUE;
        this.mRelativeEdges[0] = 0.2f;
        this.mRelativeEdges[1] = 0.2f;
        this.mRelativeVelocity[0] = 0.001f;
        this.mRelativeVelocity[1] = 0.001f;
        this.mActivationDelay = DEFAULT_ACTIVATION_DELAY;
        this.mScroller.mRampUpDuration = 500;
        this.mScroller.mRampDownDuration = 500;
    }

    public final AutoScrollHelper setEnabled(boolean enabled) {
        if (this.mEnabled && !enabled) {
            requestStop();
        }
        this.mEnabled = enabled;
        return this;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0058  */
    @Override // android.view.View.OnTouchListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouch(android.view.View r9, android.view.MotionEvent r10) {
        /*
            r8 = this;
            r2 = 0
            r3 = 1
            boolean r4 = r8.mEnabled
            if (r4 != 0) goto L7
        L6:
            return r2
        L7:
            int r4 = android.support.v4.view.MotionEventCompat.getActionMasked(r10)
            switch(r4) {
                case 0: goto L18;
                case 1: goto L7e;
                case 2: goto L1c;
                case 3: goto L7e;
                default: goto Le;
            }
        Le:
            boolean r4 = r8.mExclusive
            if (r4 == 0) goto L6
            boolean r4 = r8.mAnimating
            if (r4 == 0) goto L6
            r2 = r3
            goto L6
        L18:
            r8.mNeedsCancel = r3
            r8.mAlreadyDelayed = r2
        L1c:
            float r4 = r10.getX()
            int r5 = r9.getWidth()
            float r5 = (float) r5
            android.view.View r6 = r8.mTarget
            int r6 = r6.getWidth()
            float r6 = (float) r6
            float r0 = r8.computeTargetVelocity(r2, r4, r5, r6)
            float r4 = r10.getY()
            int r5 = r9.getHeight()
            float r5 = (float) r5
            android.view.View r6 = r8.mTarget
            int r6 = r6.getHeight()
            float r6 = (float) r6
            float r1 = r8.computeTargetVelocity(r3, r4, r5, r6)
            android.support.v4.widget.AutoScrollHelper$ClampedScroller r4 = r8.mScroller
            r4.mTargetVelocityX = r0
            r4.mTargetVelocityY = r1
            boolean r4 = r8.mAnimating
            if (r4 != 0) goto Le
            boolean r4 = r8.shouldAnimate()
            if (r4 == 0) goto Le
            java.lang.Runnable r4 = r8.mRunnable
            if (r4 != 0) goto L5f
            android.support.v4.widget.AutoScrollHelper$ScrollAnimationRunnable r4 = new android.support.v4.widget.AutoScrollHelper$ScrollAnimationRunnable
            r4.<init>(r8, r2)
            r8.mRunnable = r4
        L5f:
            r8.mAnimating = r3
            r8.mNeedsReset = r3
            boolean r4 = r8.mAlreadyDelayed
            if (r4 != 0) goto L78
            int r4 = r8.mActivationDelay
            if (r4 <= 0) goto L78
            android.view.View r4 = r8.mTarget
            java.lang.Runnable r5 = r8.mRunnable
            int r6 = r8.mActivationDelay
            long r6 = (long) r6
            android.support.v4.view.ViewCompat.postOnAnimationDelayed(r4, r5, r6)
        L75:
            r8.mAlreadyDelayed = r3
            goto Le
        L78:
            java.lang.Runnable r4 = r8.mRunnable
            r4.run()
            goto L75
        L7e:
            r8.requestStop()
            goto Le
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.AutoScrollHelper.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldAnimate() {
        ClampedScroller scroller = this.mScroller;
        int verticalDirection = (int) (scroller.mTargetVelocityY / Math.abs(scroller.mTargetVelocityY));
        int horizontalDirection = (int) (scroller.mTargetVelocityX / Math.abs(scroller.mTargetVelocityX));
        if (verticalDirection != 0 && canTargetScrollVertically(verticalDirection)) {
            return true;
        }
        if (horizontalDirection != 0) {
        }
        return false;
    }

    private void requestStop() {
        if (this.mNeedsReset) {
            this.mAnimating = false;
            return;
        }
        ClampedScroller clampedScroller = this.mScroller;
        long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        int i = (int) (currentAnimationTimeMillis - clampedScroller.mStartTime);
        int i2 = clampedScroller.mRampDownDuration;
        if (i <= i2) {
            i2 = i < 0 ? 0 : i;
        }
        clampedScroller.mEffectiveRampDown = i2;
        clampedScroller.mStopValue = clampedScroller.getValueAt(currentAnimationTimeMillis);
        clampedScroller.mStopTime = currentAnimationTimeMillis;
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0041  */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0032  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private float computeTargetVelocity(int r12, float r13, float r14, float r15) {
        /*
            r11 = this;
            r8 = 0
            float[] r7 = r11.mRelativeEdges
            r3 = r7[r12]
            float[] r7 = r11.mMaximumEdges
            r0 = r7[r12]
            float r7 = r3 * r14
            float r7 = constrain(r7, r8, r0)
            float r9 = r11.constrainEdgeValue(r13, r7)
            float r10 = r14 - r13
            float r7 = r11.constrainEdgeValue(r10, r7)
            float r7 = r7 - r9
            int r9 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r9 >= 0) goto L34
            android.view.animation.Interpolator r9 = r11.mEdgeInterpolator
            float r7 = -r7
            float r7 = r9.getInterpolation(r7)
            float r7 = -r7
        L26:
            r9 = -1082130432(0xffffffffbf800000, float:-1.0)
            r10 = 1065353216(0x3f800000, float:1.0)
            float r6 = constrain(r7, r9, r10)
        L2e:
            int r7 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r7 != 0) goto L41
            r7 = r8
        L33:
            return r7
        L34:
            int r9 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r9 <= 0) goto L3f
            android.view.animation.Interpolator r9 = r11.mEdgeInterpolator
            float r7 = r9.getInterpolation(r7)
            goto L26
        L3f:
            r6 = r8
            goto L2e
        L41:
            float[] r7 = r11.mRelativeVelocity
            r4 = r7[r12]
            float[] r7 = r11.mMinimumVelocity
            r2 = r7[r12]
            float[] r7 = r11.mMaximumVelocity
            r1 = r7[r12]
            float r5 = r4 * r15
            int r7 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r7 <= 0) goto L5a
            float r7 = r6 * r5
            float r7 = constrain(r7, r2, r1)
            goto L33
        L5a:
            float r7 = -r6
            float r7 = r7 * r5
            float r7 = constrain(r7, r2, r1)
            float r7 = -r7
            goto L33
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.AutoScrollHelper.computeTargetVelocity(int, float, float, float):float");
    }

    private float constrainEdgeValue(float current, float leading) {
        if (leading == 0.0f) {
            return 0.0f;
        }
        switch (this.mEdgeType) {
            case 0:
            case 1:
                if (current >= leading) {
                    return 0.0f;
                }
                if (current >= 0.0f) {
                    return 1.0f - (current / leading);
                }
                return (this.mAnimating && this.mEdgeType == 1) ? 1.0f : 0.0f;
            case 2:
                if (current < 0.0f) {
                    return current / (-leading);
                }
                return 0.0f;
            default:
                return 0.0f;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float constrain(float value, float min, float max) {
        if (value > max) {
            return max;
        }
        return value < min ? min : value;
    }

    /* loaded from: classes.dex */
    private class ScrollAnimationRunnable implements Runnable {
        private ScrollAnimationRunnable() {
        }

        /* synthetic */ ScrollAnimationRunnable(AutoScrollHelper x0, byte b) {
            this();
        }

        @Override // java.lang.Runnable
        public final void run() {
            boolean z = false;
            if (AutoScrollHelper.this.mAnimating) {
                if (AutoScrollHelper.this.mNeedsReset) {
                    AutoScrollHelper.access$202$2149d4c8(AutoScrollHelper.this);
                    ClampedScroller clampedScroller = AutoScrollHelper.this.mScroller;
                    clampedScroller.mStartTime = AnimationUtils.currentAnimationTimeMillis();
                    clampedScroller.mStopTime = -1L;
                    clampedScroller.mDeltaTime = clampedScroller.mStartTime;
                    clampedScroller.mStopValue = 0.5f;
                    clampedScroller.mDeltaX = 0;
                    clampedScroller.mDeltaY = 0;
                }
                ClampedScroller scroller = AutoScrollHelper.this.mScroller;
                if (scroller.mStopTime > 0 && AnimationUtils.currentAnimationTimeMillis() > scroller.mStopTime + scroller.mEffectiveRampDown) {
                    z = true;
                }
                if (!z && AutoScrollHelper.this.shouldAnimate()) {
                    if (AutoScrollHelper.this.mNeedsCancel) {
                        AutoScrollHelper.access$502$2149d4c8(AutoScrollHelper.this);
                        AutoScrollHelper.access$600(AutoScrollHelper.this);
                    }
                    if (scroller.mDeltaTime == 0) {
                        throw new RuntimeException("Cannot compute scroll delta before calling start()");
                    }
                    long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
                    float valueAt = scroller.getValueAt(currentAnimationTimeMillis);
                    float f = (valueAt * 4.0f) + ((-4.0f) * valueAt * valueAt);
                    long j = currentAnimationTimeMillis - scroller.mDeltaTime;
                    scroller.mDeltaTime = currentAnimationTimeMillis;
                    scroller.mDeltaX = (int) (((float) j) * f * scroller.mTargetVelocityX);
                    scroller.mDeltaY = (int) (((float) j) * f * scroller.mTargetVelocityY);
                    int deltaY = scroller.mDeltaY;
                    AutoScrollHelper.this.scrollTargetBy$255f295(deltaY);
                    ViewCompat.postOnAnimation(AutoScrollHelper.this.mTarget, this);
                    return;
                }
                AutoScrollHelper.access$102$2149d4c8(AutoScrollHelper.this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ClampedScroller {
        int mEffectiveRampDown;
        int mRampDownDuration;
        int mRampUpDuration;
        float mStopValue;
        float mTargetVelocityX;
        float mTargetVelocityY;
        long mStartTime = Long.MIN_VALUE;
        long mStopTime = -1;
        long mDeltaTime = 0;
        int mDeltaX = 0;
        int mDeltaY = 0;

        final float getValueAt(long currentTime) {
            if (currentTime < this.mStartTime) {
                return 0.0f;
            }
            if (this.mStopTime < 0 || currentTime < this.mStopTime) {
                long elapsedSinceStart = currentTime - this.mStartTime;
                return 0.5f * AutoScrollHelper.constrain(((float) elapsedSinceStart) / this.mRampUpDuration, 0.0f, 1.0f);
            }
            long elapsedSinceEnd = currentTime - this.mStopTime;
            return (1.0f - this.mStopValue) + (this.mStopValue * AutoScrollHelper.constrain(((float) elapsedSinceEnd) / this.mEffectiveRampDown, 0.0f, 1.0f));
        }
    }

    static /* synthetic */ void access$600(AutoScrollHelper x0) {
        long uptimeMillis = SystemClock.uptimeMillis();
        MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
        x0.mTarget.onTouchEvent(obtain);
        obtain.recycle();
    }
}
