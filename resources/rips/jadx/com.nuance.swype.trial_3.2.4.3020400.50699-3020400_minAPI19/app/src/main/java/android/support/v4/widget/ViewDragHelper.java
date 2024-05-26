package android.support.v4.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import java.util.Arrays;
import jp.co.omronsoft.openwnn.JAJP.OpenWnnEngineJAJP;

/* loaded from: classes.dex */
public final class ViewDragHelper {
    private static final Interpolator sInterpolator = new Interpolator() { // from class: android.support.v4.widget.ViewDragHelper.1
        @Override // android.animation.TimeInterpolator
        public final float getInterpolation(float t) {
            float t2 = t - 1.0f;
            return (t2 * t2 * t2 * t2 * t2) + 1.0f;
        }
    };
    private final Callback mCallback;
    View mCapturedView;
    int mDragState;
    private int[] mEdgeDragsInProgress;
    private int[] mEdgeDragsLocked;
    int mEdgeSize;
    private int[] mInitialEdgesTouched;
    float[] mInitialMotionX;
    float[] mInitialMotionY;
    float[] mLastMotionX;
    float[] mLastMotionY;
    private float mMaxVelocity;
    float mMinVelocity;
    private final ViewGroup mParentView;
    private int mPointersDown;
    private boolean mReleaseInProgress;
    private ScrollerCompat mScroller;
    int mTouchSlop;
    int mTrackingEdges;
    private VelocityTracker mVelocityTracker;
    private int mActivePointerId = -1;
    private final Runnable mSetIdleRunnable = new Runnable() { // from class: android.support.v4.widget.ViewDragHelper.2
        @Override // java.lang.Runnable
        public final void run() {
            ViewDragHelper.this.setDragState(0);
        }
    };

    /* loaded from: classes.dex */
    public static abstract class Callback {
        public abstract boolean tryCaptureView$5359dc96(View view);

        public void onViewDragStateChanged(int state) {
        }

        public void onViewPositionChanged$5b6f797d(View changedView, int left) {
        }

        public void onViewCaptured$5359dc9a(View capturedChild) {
        }

        public void onViewReleased$17e2ac03(View releasedChild, float xvel) {
        }

        public void onEdgeTouched$255f295() {
        }

        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
        }

        public int getViewHorizontalDragRange(View child) {
            return 0;
        }

        public int clampViewPositionHorizontal$17e143b0(View child, int left) {
            return 0;
        }

        public int clampViewPositionVertical$17e143b0(View child) {
            return 0;
        }
    }

    private ViewDragHelper(Context context, ViewGroup forParent, Callback cb) {
        if (forParent == null) {
            throw new IllegalArgumentException("Parent view may not be null");
        }
        if (cb == null) {
            throw new IllegalArgumentException("Callback may not be null");
        }
        this.mParentView = forParent;
        this.mCallback = cb;
        ViewConfiguration vc = ViewConfiguration.get(context);
        float density = context.getResources().getDisplayMetrics().density;
        this.mEdgeSize = (int) ((20.0f * density) + 0.5f);
        this.mTouchSlop = vc.getScaledTouchSlop();
        this.mMaxVelocity = vc.getScaledMaximumFlingVelocity();
        this.mMinVelocity = vc.getScaledMinimumFlingVelocity();
        this.mScroller = ScrollerCompat.create(context, sInterpolator);
    }

    public final void captureChildView(View childView, int activePointerId) {
        if (childView.getParent() != this.mParentView) {
            throw new IllegalArgumentException("captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view (" + this.mParentView + ")");
        }
        this.mCapturedView = childView;
        this.mActivePointerId = activePointerId;
        this.mCallback.onViewCaptured$5359dc9a(childView);
        setDragState(1);
    }

    public final void cancel() {
        this.mActivePointerId = -1;
        if (this.mInitialMotionX != null) {
            Arrays.fill(this.mInitialMotionX, 0.0f);
            Arrays.fill(this.mInitialMotionY, 0.0f);
            Arrays.fill(this.mLastMotionX, 0.0f);
            Arrays.fill(this.mLastMotionY, 0.0f);
            Arrays.fill(this.mInitialEdgesTouched, 0);
            Arrays.fill(this.mEdgeDragsInProgress, 0);
            Arrays.fill(this.mEdgeDragsLocked, 0);
            this.mPointersDown = 0;
        }
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public final void abort() {
        cancel();
        if (this.mDragState == 2) {
            this.mScroller.getCurrX();
            this.mScroller.getCurrY();
            this.mScroller.abortAnimation();
            int newX = this.mScroller.getCurrX();
            this.mScroller.getCurrY();
            this.mCallback.onViewPositionChanged$5b6f797d(this.mCapturedView, newX);
        }
        setDragState(0);
    }

    public final boolean smoothSlideViewTo(View child, int finalLeft, int finalTop) {
        this.mCapturedView = child;
        this.mActivePointerId = -1;
        boolean continueSliding = forceSettleCapturedViewAt(finalLeft, finalTop, 0, 0);
        if (!continueSliding && this.mDragState == 0 && this.mCapturedView != null) {
            this.mCapturedView = null;
        }
        return continueSliding;
    }

    public final boolean settleCapturedViewAt(int finalLeft, int finalTop) {
        if (!this.mReleaseInProgress) {
            throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased");
        }
        return forceSettleCapturedViewAt(finalLeft, finalTop, (int) VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), (int) VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId));
    }

    private boolean forceSettleCapturedViewAt(int finalLeft, int finalTop, int xvel, int yvel) {
        int startLeft = this.mCapturedView.getLeft();
        int startTop = this.mCapturedView.getTop();
        int dx = finalLeft - startLeft;
        int dy = finalTop - startTop;
        if (dx == 0 && dy == 0) {
            this.mScroller.abortAnimation();
            setDragState(0);
            return false;
        }
        View view = this.mCapturedView;
        int clampMag = clampMag(xvel, (int) this.mMinVelocity, (int) this.mMaxVelocity);
        int clampMag2 = clampMag(yvel, (int) this.mMinVelocity, (int) this.mMaxVelocity);
        int abs = Math.abs(dx);
        int abs2 = Math.abs(dy);
        int abs3 = Math.abs(clampMag);
        int abs4 = Math.abs(clampMag2);
        int i = abs3 + abs4;
        int i2 = abs + abs2;
        int duration = (int) (((clampMag2 != 0 ? abs4 / i : abs2 / i2) * computeAxisDuration(dy, clampMag2, 0)) + ((clampMag != 0 ? abs3 / i : abs / i2) * computeAxisDuration(dx, clampMag, this.mCallback.getViewHorizontalDragRange(view))));
        this.mScroller.startScroll(startLeft, startTop, dx, dy, duration);
        setDragState(2);
        return true;
    }

    private int computeAxisDuration(int delta, int velocity, int motionRange) {
        int duration;
        if (delta == 0) {
            return 0;
        }
        int width = this.mParentView.getWidth();
        int halfWidth = width / 2;
        float distanceRatio = Math.min(1.0f, Math.abs(delta) / width);
        float distance = halfWidth + (halfWidth * ((float) Math.sin((float) ((distanceRatio - 0.5f) * 0.4712389167638204d))));
        int velocity2 = Math.abs(velocity);
        if (velocity2 > 0) {
            duration = Math.round(1000.0f * Math.abs(distance / velocity2)) * 4;
        } else {
            duration = (int) (((Math.abs(delta) / motionRange) + 1.0f) * 256.0f);
        }
        return Math.min(duration, OpenWnnEngineJAJP.FREQ_LEARN);
    }

    private static int clampMag(int value, int absMin, int absMax) {
        int absValue = Math.abs(value);
        if (absValue < absMin) {
            return 0;
        }
        return absValue > absMax ? value <= 0 ? -absMax : absMax : value;
    }

    private static float clampMag(float value, float absMin, float absMax) {
        float absValue = Math.abs(value);
        if (absValue < absMin) {
            return 0.0f;
        }
        return absValue > absMax ? value <= 0.0f ? -absMax : absMax : value;
    }

    public final boolean continueSettling$138603() {
        if (this.mDragState == 2) {
            boolean keepGoing = this.mScroller.computeScrollOffset();
            int x = this.mScroller.getCurrX();
            int y = this.mScroller.getCurrY();
            int dx = x - this.mCapturedView.getLeft();
            int dy = y - this.mCapturedView.getTop();
            if (dx != 0) {
                ViewCompat.offsetLeftAndRight(this.mCapturedView, dx);
            }
            if (dy != 0) {
                ViewCompat.offsetTopAndBottom(this.mCapturedView, dy);
            }
            if (dx != 0 || dy != 0) {
                this.mCallback.onViewPositionChanged$5b6f797d(this.mCapturedView, x);
            }
            if (keepGoing && x == this.mScroller.getFinalX() && y == this.mScroller.getFinalY()) {
                this.mScroller.abortAnimation();
                keepGoing = false;
            }
            if (!keepGoing) {
                this.mParentView.post(this.mSetIdleRunnable);
            }
        }
        return this.mDragState == 2;
    }

    private void dispatchViewReleased$2548a35(float xvel) {
        this.mReleaseInProgress = true;
        this.mCallback.onViewReleased$17e2ac03(this.mCapturedView, xvel);
        this.mReleaseInProgress = false;
        if (this.mDragState == 1) {
            setDragState(0);
        }
    }

    private void clearMotionHistory(int pointerId) {
        if (this.mInitialMotionX != null && isPointerDown(pointerId)) {
            this.mInitialMotionX[pointerId] = 0.0f;
            this.mInitialMotionY[pointerId] = 0.0f;
            this.mLastMotionX[pointerId] = 0.0f;
            this.mLastMotionY[pointerId] = 0.0f;
            this.mInitialEdgesTouched[pointerId] = 0;
            this.mEdgeDragsInProgress[pointerId] = 0;
            this.mEdgeDragsLocked[pointerId] = 0;
            this.mPointersDown &= (1 << pointerId) ^ (-1);
        }
    }

    private void saveLastMotion(MotionEvent ev) {
        int pointerCount = MotionEventCompat.getPointerCount(ev);
        for (int i = 0; i < pointerCount; i++) {
            int pointerId = MotionEventCompat.getPointerId(ev, i);
            if (isValidPointerForActionMove(pointerId)) {
                float x = MotionEventCompat.getX(ev, i);
                float y = MotionEventCompat.getY(ev, i);
                this.mLastMotionX[pointerId] = x;
                this.mLastMotionY[pointerId] = y;
            }
        }
    }

    public final boolean isPointerDown(int pointerId) {
        return (this.mPointersDown & (1 << pointerId)) != 0;
    }

    final void setDragState(int state) {
        this.mParentView.removeCallbacks(this.mSetIdleRunnable);
        if (this.mDragState != state) {
            this.mDragState = state;
            this.mCallback.onViewDragStateChanged(state);
            if (this.mDragState == 0) {
                this.mCapturedView = null;
            }
        }
    }

    private boolean tryCaptureViewForDrag(View toCapture, int pointerId) {
        if (toCapture == this.mCapturedView && this.mActivePointerId == pointerId) {
            return true;
        }
        if (toCapture != null && this.mCallback.tryCaptureView$5359dc96(toCapture)) {
            this.mActivePointerId = pointerId;
            captureChildView(toCapture, pointerId);
            return true;
        }
        return false;
    }

    public final boolean shouldInterceptTouchEvent(MotionEvent ev) {
        View toCapture;
        int action = MotionEventCompat.getActionMasked(ev);
        int actionIndex = MotionEventCompat.getActionIndex(ev);
        if (action == 0) {
            cancel();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        switch (action) {
            case 0:
                float x = ev.getX();
                float y = ev.getY();
                int pointerId = MotionEventCompat.getPointerId(ev, 0);
                saveInitialMotion(x, y, pointerId);
                View toCapture2 = findTopChildUnder((int) x, (int) y);
                if (toCapture2 == this.mCapturedView && this.mDragState == 2) {
                    tryCaptureViewForDrag(toCapture2, pointerId);
                }
                if ((this.mInitialEdgesTouched[pointerId] & this.mTrackingEdges) != 0) {
                    this.mCallback.onEdgeTouched$255f295();
                    break;
                }
                break;
            case 1:
            case 3:
                cancel();
                break;
            case 2:
                if (this.mInitialMotionX != null && this.mInitialMotionY != null) {
                    int pointerCount = MotionEventCompat.getPointerCount(ev);
                    for (int i = 0; i < pointerCount; i++) {
                        int pointerId2 = MotionEventCompat.getPointerId(ev, i);
                        if (isValidPointerForActionMove(pointerId2)) {
                            float x2 = MotionEventCompat.getX(ev, i);
                            float y2 = MotionEventCompat.getY(ev, i);
                            float dx = x2 - this.mInitialMotionX[pointerId2];
                            float dy = y2 - this.mInitialMotionY[pointerId2];
                            View toCapture3 = findTopChildUnder((int) x2, (int) y2);
                            boolean pastSlop = toCapture3 != null && checkTouchSlop$17e2abff(toCapture3, dx);
                            if (pastSlop) {
                                int oldLeft = toCapture3.getLeft();
                                int targetLeft = oldLeft + ((int) dx);
                                int newLeft = this.mCallback.clampViewPositionHorizontal$17e143b0(toCapture3, targetLeft);
                                toCapture3.getTop();
                                this.mCallback.clampViewPositionVertical$17e143b0(toCapture3);
                                int horizontalDragRange = this.mCallback.getViewHorizontalDragRange(toCapture3);
                                if (horizontalDragRange != 0) {
                                    if (horizontalDragRange > 0 && newLeft == oldLeft) {
                                    }
                                }
                                saveLastMotion(ev);
                                break;
                            }
                            reportNewEdgeDrags(dx, dy, pointerId2);
                            if (this.mDragState != 1) {
                                if (pastSlop && tryCaptureViewForDrag(toCapture3, pointerId2)) {
                                }
                            }
                            saveLastMotion(ev);
                        }
                    }
                    saveLastMotion(ev);
                }
                break;
            case 5:
                int pointerId3 = MotionEventCompat.getPointerId(ev, actionIndex);
                float x3 = MotionEventCompat.getX(ev, actionIndex);
                float y3 = MotionEventCompat.getY(ev, actionIndex);
                saveInitialMotion(x3, y3, pointerId3);
                if (this.mDragState == 0) {
                    if ((this.mInitialEdgesTouched[pointerId3] & this.mTrackingEdges) != 0) {
                        this.mCallback.onEdgeTouched$255f295();
                        break;
                    }
                } else if (this.mDragState == 2 && (toCapture = findTopChildUnder((int) x3, (int) y3)) == this.mCapturedView) {
                    tryCaptureViewForDrag(toCapture, pointerId3);
                    break;
                }
                break;
            case 6:
                clearMotionHistory(MotionEventCompat.getPointerId(ev, actionIndex));
                break;
        }
        return this.mDragState == 1;
    }

    public final void processTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        int actionIndex = MotionEventCompat.getActionIndex(ev);
        if (action == 0) {
            cancel();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        switch (action) {
            case 0:
                float x = ev.getX();
                float y = ev.getY();
                int pointerId = MotionEventCompat.getPointerId(ev, 0);
                View toCapture = findTopChildUnder((int) x, (int) y);
                saveInitialMotion(x, y, pointerId);
                tryCaptureViewForDrag(toCapture, pointerId);
                if ((this.mInitialEdgesTouched[pointerId] & this.mTrackingEdges) != 0) {
                    this.mCallback.onEdgeTouched$255f295();
                    return;
                }
                return;
            case 1:
                if (this.mDragState == 1) {
                    releaseViewForPointerUp();
                }
                cancel();
                return;
            case 2:
                if (this.mDragState == 1) {
                    if (isValidPointerForActionMove(this.mActivePointerId)) {
                        int index = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                        float x2 = MotionEventCompat.getX(ev, index);
                        float y2 = MotionEventCompat.getY(ev, index);
                        int idx = (int) (x2 - this.mLastMotionX[this.mActivePointerId]);
                        int idy = (int) (y2 - this.mLastMotionY[this.mActivePointerId]);
                        int left = this.mCapturedView.getLeft() + idx;
                        this.mCapturedView.getTop();
                        int left2 = this.mCapturedView.getLeft();
                        int top = this.mCapturedView.getTop();
                        if (idx != 0) {
                            left = this.mCallback.clampViewPositionHorizontal$17e143b0(this.mCapturedView, left);
                            ViewCompat.offsetLeftAndRight(this.mCapturedView, left - left2);
                        }
                        if (idy != 0) {
                            ViewCompat.offsetTopAndBottom(this.mCapturedView, this.mCallback.clampViewPositionVertical$17e143b0(this.mCapturedView) - top);
                        }
                        if (idx != 0 || idy != 0) {
                            this.mCallback.onViewPositionChanged$5b6f797d(this.mCapturedView, left);
                        }
                        saveLastMotion(ev);
                        return;
                    }
                    return;
                }
                int pointerCount = MotionEventCompat.getPointerCount(ev);
                for (int i = 0; i < pointerCount; i++) {
                    int pointerId2 = MotionEventCompat.getPointerId(ev, i);
                    if (isValidPointerForActionMove(pointerId2)) {
                        float x3 = MotionEventCompat.getX(ev, i);
                        float y3 = MotionEventCompat.getY(ev, i);
                        float dx = x3 - this.mInitialMotionX[pointerId2];
                        float dy = y3 - this.mInitialMotionY[pointerId2];
                        reportNewEdgeDrags(dx, dy, pointerId2);
                        if (this.mDragState != 1) {
                            View toCapture2 = findTopChildUnder((int) x3, (int) y3);
                            if (checkTouchSlop$17e2abff(toCapture2, dx) && tryCaptureViewForDrag(toCapture2, pointerId2)) {
                            }
                        }
                        saveLastMotion(ev);
                        return;
                    }
                }
                saveLastMotion(ev);
                return;
            case 3:
                if (this.mDragState == 1) {
                    dispatchViewReleased$2548a35(0.0f);
                }
                cancel();
                return;
            case 4:
            default:
                return;
            case 5:
                int pointerId3 = MotionEventCompat.getPointerId(ev, actionIndex);
                float x4 = MotionEventCompat.getX(ev, actionIndex);
                float y4 = MotionEventCompat.getY(ev, actionIndex);
                saveInitialMotion(x4, y4, pointerId3);
                if (this.mDragState == 0) {
                    tryCaptureViewForDrag(findTopChildUnder((int) x4, (int) y4), pointerId3);
                    if ((this.mInitialEdgesTouched[pointerId3] & this.mTrackingEdges) != 0) {
                        this.mCallback.onEdgeTouched$255f295();
                        return;
                    }
                    return;
                }
                if (isViewUnder(this.mCapturedView, (int) x4, (int) y4)) {
                    tryCaptureViewForDrag(this.mCapturedView, pointerId3);
                    return;
                }
                return;
            case 6:
                int pointerId4 = MotionEventCompat.getPointerId(ev, actionIndex);
                if (this.mDragState == 1 && pointerId4 == this.mActivePointerId) {
                    int newActivePointer = -1;
                    int pointerCount2 = MotionEventCompat.getPointerCount(ev);
                    int i2 = 0;
                    while (true) {
                        if (i2 < pointerCount2) {
                            int id = MotionEventCompat.getPointerId(ev, i2);
                            if (id != this.mActivePointerId) {
                                if (findTopChildUnder((int) MotionEventCompat.getX(ev, i2), (int) MotionEventCompat.getY(ev, i2)) == this.mCapturedView && tryCaptureViewForDrag(this.mCapturedView, id)) {
                                    newActivePointer = this.mActivePointerId;
                                }
                            }
                            i2++;
                        }
                    }
                    if (newActivePointer == -1) {
                        releaseViewForPointerUp();
                    }
                }
                clearMotionHistory(pointerId4);
                return;
        }
    }

    private void reportNewEdgeDrags(float dx, float dy, int pointerId) {
        int dragsStarted = 0;
        if (checkNewEdgeDrag(dx, dy, pointerId, 1)) {
            dragsStarted = 1;
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, 4)) {
            dragsStarted |= 4;
        }
        if (checkNewEdgeDrag(dx, dy, pointerId, 2)) {
            dragsStarted |= 2;
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, 8)) {
            dragsStarted |= 8;
        }
        if (dragsStarted != 0) {
            int[] iArr = this.mEdgeDragsInProgress;
            iArr[pointerId] = iArr[pointerId] | dragsStarted;
            this.mCallback.onEdgeDragStarted(dragsStarted, pointerId);
        }
    }

    private boolean checkNewEdgeDrag(float delta, float odelta, int pointerId, int edge) {
        float absDelta = Math.abs(delta);
        float absODelta = Math.abs(odelta);
        if ((this.mInitialEdgesTouched[pointerId] & edge) != edge || (this.mTrackingEdges & edge) == 0 || (this.mEdgeDragsLocked[pointerId] & edge) == edge || (this.mEdgeDragsInProgress[pointerId] & edge) == edge) {
            return false;
        }
        return (absDelta > ((float) this.mTouchSlop) || absODelta > ((float) this.mTouchSlop)) && (this.mEdgeDragsInProgress[pointerId] & edge) == 0 && absDelta > ((float) this.mTouchSlop);
    }

    private boolean checkTouchSlop$17e2abff(View child, float dx) {
        if (child == null) {
            return false;
        }
        return (this.mCallback.getViewHorizontalDragRange(child) > 0) && Math.abs(dx) > ((float) this.mTouchSlop);
    }

    private void releaseViewForPointerUp() {
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxVelocity);
        float xvel = clampMag(VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity);
        clampMag(VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity);
        dispatchViewReleased$2548a35(xvel);
    }

    public static boolean isViewUnder(View view, int x, int y) {
        return view != null && x >= view.getLeft() && x < view.getRight() && y >= view.getTop() && y < view.getBottom();
    }

    public final View findTopChildUnder(int x, int y) {
        for (int i = this.mParentView.getChildCount() - 1; i >= 0; i--) {
            View child = this.mParentView.getChildAt(i);
            if (x >= child.getLeft() && x < child.getRight() && y >= child.getTop() && y < child.getBottom()) {
                return child;
            }
        }
        return null;
    }

    private boolean isValidPointerForActionMove(int pointerId) {
        if (isPointerDown(pointerId)) {
            return true;
        }
        Log.e("ViewDragHelper", "Ignoring pointerId=" + pointerId + " because ACTION_DOWN was not received for this pointer before ACTION_MOVE. It likely happened because  ViewDragHelper did not receive all the events in the event stream.");
        return false;
    }

    public static ViewDragHelper create(ViewGroup forParent, float sensitivity, Callback cb) {
        ViewDragHelper helper = new ViewDragHelper(forParent.getContext(), forParent, cb);
        helper.mTouchSlop = (int) (helper.mTouchSlop * (1.0f / sensitivity));
        return helper;
    }

    private void saveInitialMotion(float x, float y, int pointerId) {
        if (this.mInitialMotionX == null || this.mInitialMotionX.length <= pointerId) {
            float[] fArr = new float[pointerId + 1];
            float[] fArr2 = new float[pointerId + 1];
            float[] fArr3 = new float[pointerId + 1];
            float[] fArr4 = new float[pointerId + 1];
            int[] iArr = new int[pointerId + 1];
            int[] iArr2 = new int[pointerId + 1];
            int[] iArr3 = new int[pointerId + 1];
            if (this.mInitialMotionX != null) {
                System.arraycopy(this.mInitialMotionX, 0, fArr, 0, this.mInitialMotionX.length);
                System.arraycopy(this.mInitialMotionY, 0, fArr2, 0, this.mInitialMotionY.length);
                System.arraycopy(this.mLastMotionX, 0, fArr3, 0, this.mLastMotionX.length);
                System.arraycopy(this.mLastMotionY, 0, fArr4, 0, this.mLastMotionY.length);
                System.arraycopy(this.mInitialEdgesTouched, 0, iArr, 0, this.mInitialEdgesTouched.length);
                System.arraycopy(this.mEdgeDragsInProgress, 0, iArr2, 0, this.mEdgeDragsInProgress.length);
                System.arraycopy(this.mEdgeDragsLocked, 0, iArr3, 0, this.mEdgeDragsLocked.length);
            }
            this.mInitialMotionX = fArr;
            this.mInitialMotionY = fArr2;
            this.mLastMotionX = fArr3;
            this.mLastMotionY = fArr4;
            this.mInitialEdgesTouched = iArr;
            this.mEdgeDragsInProgress = iArr2;
            this.mEdgeDragsLocked = iArr3;
        }
        float[] fArr5 = this.mInitialMotionX;
        this.mLastMotionX[pointerId] = x;
        fArr5[pointerId] = x;
        float[] fArr6 = this.mInitialMotionY;
        this.mLastMotionY[pointerId] = y;
        fArr6[pointerId] = y;
        int[] iArr4 = this.mInitialEdgesTouched;
        int i = (int) x;
        int i2 = (int) y;
        int i3 = i < this.mParentView.getLeft() + this.mEdgeSize ? 1 : 0;
        if (i2 < this.mParentView.getTop() + this.mEdgeSize) {
            i3 |= 4;
        }
        if (i > this.mParentView.getRight() - this.mEdgeSize) {
            i3 |= 2;
        }
        if (i2 > this.mParentView.getBottom() - this.mEdgeSize) {
            i3 |= 8;
        }
        iArr4[pointerId] = i3;
        this.mPointersDown |= 1 << pointerId;
    }
}
