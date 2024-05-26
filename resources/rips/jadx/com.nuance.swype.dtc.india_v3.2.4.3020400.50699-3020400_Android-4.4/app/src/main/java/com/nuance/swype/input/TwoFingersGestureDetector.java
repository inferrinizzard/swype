package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class TwoFingersGestureDetector {
    private final float MAX_SCROLL_OFFSET;
    private final float MIN_LEFT_RIGHT_GESTURE_DISTANCE;
    private final float MIN_SCROLL_DISTANCE;
    private final float MIN_UP_DOWN_GESTURE_DISTANCE;
    private boolean mDetectingGesture;
    private Finger mFirstFinger;
    private GestureDetectionNotification mGestureDetectionNotification = GestureDetectionNotification.NONE;
    private GestureListener mGestureListener;
    private Finger mSecondFinger;

    @SuppressLint({"RtlHardcoded"})
    /* loaded from: classes.dex */
    public enum GestureDetectionNotification {
        NONE,
        LEFT,
        RIGHT,
        UP,
        DOWN,
        HORIZONTAL_PINCH,
        HORIZONTAL_SPREAD
    }

    /* loaded from: classes.dex */
    public interface GestureListener {
        void onGestureDown();

        void onGestureHorizonalPinch(Point point, Point point2);

        void onGestureHorizontalSpread(Point point, Point point2);

        void onGestureLeft();

        void onGestureRight();

        void onGestureUp();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Finger {
        final int mPointerId;
        ArrayList<Point> mPoints = new ArrayList<>();

        Finger(int pointerId) {
            this.mPointerId = pointerId;
        }

        void addPoint(float x, float y) {
            this.mPoints.add(new Point((int) x, (int) y));
        }

        boolean isMovingLeft() {
            if (this.mPoints.size() <= 0) {
                return false;
            }
            Point startPoint = this.mPoints.get(0);
            Point endPoint = this.mPoints.get(this.mPoints.size() - 1);
            return ((float) (startPoint.x - endPoint.x)) > TwoFingersGestureDetector.this.MIN_SCROLL_DISTANCE && ((float) Math.abs(endPoint.y - startPoint.y)) < TwoFingersGestureDetector.this.MAX_SCROLL_OFFSET;
        }

        boolean isMovingRight() {
            if (this.mPoints.size() <= 0) {
                return false;
            }
            Point startPoint = this.mPoints.get(0);
            Point endPoint = this.mPoints.get(this.mPoints.size() - 1);
            return ((float) (endPoint.x - startPoint.x)) > TwoFingersGestureDetector.this.MIN_SCROLL_DISTANCE && ((float) Math.abs(endPoint.y - startPoint.y)) < TwoFingersGestureDetector.this.MAX_SCROLL_OFFSET;
        }

        boolean isMovingUp() {
            if (this.mPoints.size() <= 0) {
                return false;
            }
            Point startPoint = this.mPoints.get(0);
            Point endPoint = this.mPoints.get(this.mPoints.size() - 1);
            return ((float) (startPoint.y - endPoint.y)) > TwoFingersGestureDetector.this.MIN_SCROLL_DISTANCE && ((float) Math.abs(endPoint.x - startPoint.x)) < TwoFingersGestureDetector.this.MAX_SCROLL_OFFSET;
        }

        boolean isMovingDown() {
            if (this.mPoints.size() <= 0) {
                return false;
            }
            Point startPoint = this.mPoints.get(0);
            Point endPoint = this.mPoints.get(this.mPoints.size() - 1);
            return ((float) (endPoint.y - startPoint.y)) > TwoFingersGestureDetector.this.MIN_SCROLL_DISTANCE && ((float) Math.abs(endPoint.x - startPoint.x)) < TwoFingersGestureDetector.this.MAX_SCROLL_OFFSET;
        }

        boolean isGestureDown() {
            if (this.mPoints.size() <= 0) {
                return false;
            }
            Point startPoint = this.mPoints.get(0);
            return ((float) (this.mPoints.get(this.mPoints.size() + (-1)).y - startPoint.y)) > TwoFingersGestureDetector.this.MIN_UP_DOWN_GESTURE_DISTANCE;
        }

        boolean isGestureUp() {
            if (this.mPoints.size() <= 0) {
                return false;
            }
            Point startPoint = this.mPoints.get(0);
            Point endPoint = this.mPoints.get(this.mPoints.size() - 1);
            return ((float) (startPoint.y - endPoint.y)) > TwoFingersGestureDetector.this.MIN_UP_DOWN_GESTURE_DISTANCE;
        }

        boolean isGestureLeft() {
            if (this.mPoints.size() <= 0) {
                return false;
            }
            Point startPoint = this.mPoints.get(0);
            Point endPoint = this.mPoints.get(this.mPoints.size() - 1);
            return ((float) (startPoint.x - endPoint.x)) > TwoFingersGestureDetector.this.MIN_LEFT_RIGHT_GESTURE_DISTANCE;
        }

        boolean isGestureRight() {
            if (this.mPoints.size() <= 0) {
                return false;
            }
            Point startPoint = this.mPoints.get(0);
            return ((float) (this.mPoints.get(this.mPoints.size() + (-1)).x - startPoint.x)) > TwoFingersGestureDetector.this.MIN_LEFT_RIGHT_GESTURE_DISTANCE;
        }

        public Point getStartPoint() {
            if (this.mPoints.size() > 0) {
                return this.mPoints.get(0);
            }
            return null;
        }

        public Point getEndPoint() {
            if (this.mPoints.size() > 0) {
                return this.mPoints.get(this.mPoints.size() - 1);
            }
            return null;
        }
    }

    public TwoFingersGestureDetector(Context context, int keyboardWidth, int keyboardHeight) {
        float density = context.getResources().getDisplayMetrics().density;
        this.MIN_SCROLL_DISTANCE = 20.0f * density;
        this.MAX_SCROLL_OFFSET = 60.0f * density;
        this.MIN_LEFT_RIGHT_GESTURE_DISTANCE = 120.0f * density;
        this.MIN_UP_DOWN_GESTURE_DISTANCE = keyboardHeight / 2.0f;
    }

    public void setListener(GestureListener listener) {
        this.mGestureListener = listener;
    }

    private void addPoint(int pointerId, float x, float y) {
        if (this.mFirstFinger != null) {
            if (pointerId == 0) {
                this.mFirstFinger.addPoint(x, y);
            } else if (this.mSecondFinger != null) {
                this.mSecondFinger.addPoint(x, y);
            }
        }
    }

    private void notifyGestureDetection() {
        if (this.mGestureListener != null) {
            switch (this.mGestureDetectionNotification) {
                case UP:
                    this.mGestureListener.onGestureUp();
                    return;
                case DOWN:
                    this.mGestureListener.onGestureDown();
                    return;
                case LEFT:
                    this.mGestureListener.onGestureLeft();
                    return;
                case RIGHT:
                    this.mGestureListener.onGestureRight();
                    return;
                case HORIZONTAL_PINCH:
                    this.mGestureListener.onGestureHorizonalPinch(this.mFirstFinger.getEndPoint(), this.mSecondFinger.getEndPoint());
                    return;
                case HORIZONTAL_SPREAD:
                    this.mGestureListener.onGestureHorizontalSpread(this.mFirstFinger.getEndPoint(), this.mSecondFinger.getEndPoint());
                    return;
                default:
                    return;
            }
        }
    }

    private void reset() {
        this.mFirstFinger = null;
        this.mSecondFinger = null;
        this.mGestureDetectionNotification = GestureDetectionNotification.NONE;
        this.mDetectingGesture = false;
    }

    private void detectingGesture() {
        if (this.mFirstFinger != null && this.mSecondFinger != null) {
            if (this.mFirstFinger.isMovingDown() && this.mSecondFinger.isMovingDown()) {
                this.mDetectingGesture = true;
                if (this.mFirstFinger.isGestureDown() && this.mSecondFinger.isGestureDown()) {
                    this.mGestureDetectionNotification = GestureDetectionNotification.DOWN;
                    return;
                }
                return;
            }
            if (this.mFirstFinger.isMovingUp() && this.mSecondFinger.isMovingUp()) {
                this.mDetectingGesture = true;
                if (this.mFirstFinger.isGestureUp() && this.mSecondFinger.isGestureUp()) {
                    this.mGestureDetectionNotification = GestureDetectionNotification.UP;
                    return;
                }
                return;
            }
            if (this.mFirstFinger.isMovingRight() && this.mSecondFinger.isMovingRight()) {
                this.mDetectingGesture = true;
                if (this.mFirstFinger.isGestureRight() && this.mSecondFinger.isGestureRight()) {
                    this.mGestureDetectionNotification = GestureDetectionNotification.RIGHT;
                    return;
                }
                return;
            }
            if (this.mFirstFinger.isMovingLeft() && this.mSecondFinger.isMovingLeft()) {
                this.mDetectingGesture = true;
                if (this.mFirstFinger.isGestureLeft() && this.mSecondFinger.isGestureLeft()) {
                    this.mGestureDetectionNotification = GestureDetectionNotification.LEFT;
                    return;
                }
                return;
            }
            if (this.mFirstFinger.isMovingRight() && this.mSecondFinger.isMovingLeft()) {
                this.mDetectingGesture = true;
                if (this.mFirstFinger.getEndPoint().x < this.mSecondFinger.getEndPoint().x) {
                    this.mGestureDetectionNotification = GestureDetectionNotification.HORIZONTAL_PINCH;
                    return;
                } else {
                    this.mGestureDetectionNotification = GestureDetectionNotification.HORIZONTAL_SPREAD;
                    return;
                }
            }
            if (this.mFirstFinger.isMovingLeft() && this.mSecondFinger.isMovingRight()) {
                this.mDetectingGesture = true;
                if (this.mFirstFinger.getEndPoint().x > this.mSecondFinger.getEndPoint().x) {
                    this.mGestureDetectionNotification = GestureDetectionNotification.HORIZONTAL_PINCH;
                } else {
                    this.mGestureDetectionNotification = GestureDetectionNotification.HORIZONTAL_SPREAD;
                }
            }
        }
    }

    public Point getFirstFingerCurrent() {
        return this.mFirstFinger.getEndPoint();
    }

    public Point getSecondFingerCurrent() {
        return this.mSecondFinger.getEndPoint();
    }

    public boolean isGesturingDetected() {
        return this.mDetectingGesture;
    }

    public GestureDetectionNotification getCurrentGesture() {
        return this.mGestureDetectionNotification;
    }

    public boolean onTouchEvent(MotionEvent me) {
        int action = me.getAction();
        int actionCode = action & 255;
        int pointerIndex = action >> 8;
        int pointerId = MotionEventWrapper.getPointerId(me, pointerIndex);
        switch (actionCode) {
            case 0:
                reset();
                this.mFirstFinger = new Finger(pointerId);
                this.mFirstFinger.addPoint(MotionEventWrapper.getX(me, pointerIndex), MotionEventWrapper.getY(me, pointerIndex));
                break;
            case 1:
            case 6:
                float x = MotionEventWrapper.getX(me, pointerIndex);
                float y = MotionEventWrapper.getY(me, pointerIndex);
                addPoint(pointerId, x, y);
                break;
            case 2:
                int pointerCount = MotionEventWrapper.getPointerCount(me);
                for (int index = 0; index < pointerCount; index++) {
                    int thisPointerId = MotionEventWrapper.getPointerId(me, index);
                    int histories = me.getHistorySize();
                    for (int i = 0; i < histories; i++) {
                        float x2 = MotionEventWrapper.getHistoricalX(me, index, i);
                        float y2 = MotionEventWrapper.getHistoricalY(me, index, i);
                        addPoint(thisPointerId, x2, y2);
                    }
                    float x3 = MotionEventWrapper.getX(me, index);
                    float y3 = MotionEventWrapper.getY(me, index);
                    addPoint(thisPointerId, x3, y3);
                }
                break;
            case 5:
                this.mSecondFinger = new Finger(pointerId);
                this.mSecondFinger.addPoint(MotionEventWrapper.getX(me, pointerIndex), MotionEventWrapper.getY(me, pointerIndex));
                break;
        }
        detectingGesture();
        if (actionCode == 1 && this.mGestureDetectionNotification != GestureDetectionNotification.NONE) {
            notifyGestureDetection();
            reset();
        }
        return this.mDetectingGesture;
    }
}
