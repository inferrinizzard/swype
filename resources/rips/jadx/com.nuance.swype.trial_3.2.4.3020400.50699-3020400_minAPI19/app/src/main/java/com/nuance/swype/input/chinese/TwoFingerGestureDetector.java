package com.nuance.swype.input.chinese;

import android.view.MotionEvent;
import com.nuance.swype.input.MotionEventWrapper;

/* loaded from: classes.dex */
public class TwoFingerGestureDetector {
    private static final int INVALID_POINTER_ID = -1;
    private static final float MAX_SCROLL_OFFSET = 60.0f;
    private static final float MIN_SCROLL_DISTANCE = 20.0f;
    private Finger initialFirstFinger = null;
    private Finger initialSecondFinger = null;
    private OnScrollListener scrollListener = null;
    private int firstPointerId = -1;
    private int secondPointerId = -1;
    private boolean isMultiTouchBegin = false;
    private boolean isMultiTouchReleaseBegin = false;

    /* loaded from: classes.dex */
    public interface OnScrollListener {
        boolean onScrollDown();

        boolean onScrollLeft();

        boolean onScrollRight();

        boolean onScrollUp();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Finger {
        private float endX;
        private float endY;
        private float startX;
        private float startY;

        private Finger() {
            this.startX = 0.0f;
            this.startY = 0.0f;
            this.endX = 0.0f;
            this.endY = 0.0f;
        }

        public void setStartX(float aStartX) {
            this.startX = aStartX;
        }

        public void setStartY(float aStartY) {
            this.startY = aStartY;
        }

        public void setEndX(float aEndX) {
            this.endX = aEndX;
        }

        public void setEndY(float aEndY) {
            this.endY = aEndY;
        }

        public boolean isScrollLeft() {
            return this.startX - this.endX > TwoFingerGestureDetector.MIN_SCROLL_DISTANCE && Math.abs(this.endY - this.startY) < TwoFingerGestureDetector.MAX_SCROLL_OFFSET;
        }

        public boolean isScrollRight() {
            return this.endX - this.startX > TwoFingerGestureDetector.MIN_SCROLL_DISTANCE && Math.abs(this.endY - this.startY) < TwoFingerGestureDetector.MAX_SCROLL_OFFSET;
        }

        public boolean isScrollUp() {
            return this.startY - this.endY > TwoFingerGestureDetector.MIN_SCROLL_DISTANCE && Math.abs(this.endX - this.startX) < TwoFingerGestureDetector.MAX_SCROLL_OFFSET;
        }

        public boolean isScrollDown() {
            return this.endY - this.startY > TwoFingerGestureDetector.MIN_SCROLL_DISTANCE && Math.abs(this.endX - this.startX) < TwoFingerGestureDetector.MAX_SCROLL_OFFSET;
        }
    }

    public void setScrollListener(OnScrollListener aScrollListener) {
        this.scrollListener = aScrollListener;
    }

    private void clear() {
        this.initialFirstFinger = null;
        this.initialSecondFinger = null;
        this.firstPointerId = -1;
        this.secondPointerId = -1;
        this.isMultiTouchBegin = false;
        this.isMultiTouchReleaseBegin = false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean bHandled = true;
        if (this.scrollListener == null) {
            return false;
        }
        int action = ev.getAction();
        switch (action & 255) {
            case 0:
                this.firstPointerId = MotionEventWrapper.getPointerId(ev, 0);
                return false;
            case 1:
                if (this.isMultiTouchBegin && this.isMultiTouchReleaseBegin) {
                    int pointerIndex = MotionEventWrapper.findPointerIndex(ev, MotionEventWrapper.getPointerId(ev, 0));
                    float x = MotionEventWrapper.getX(ev, pointerIndex);
                    float y = MotionEventWrapper.getY(ev, pointerIndex);
                    if (this.firstPointerId != -1) {
                        this.initialFirstFinger.setEndX(x);
                        this.initialFirstFinger.setEndY(y);
                        this.firstPointerId = -1;
                    } else if (this.secondPointerId != -1) {
                        this.initialSecondFinger.setEndX(x);
                        this.initialSecondFinger.setEndY(y);
                        this.secondPointerId = -1;
                    }
                    if (this.initialFirstFinger.isScrollLeft() && this.initialSecondFinger.isScrollLeft()) {
                        bHandled = this.scrollListener.onScrollLeft();
                    } else if (this.initialFirstFinger.isScrollRight() && this.initialSecondFinger.isScrollRight()) {
                        bHandled = this.scrollListener.onScrollRight();
                    } else if (this.initialFirstFinger.isScrollUp() && this.initialSecondFinger.isScrollUp()) {
                        bHandled = this.scrollListener.onScrollUp();
                    } else if (!this.initialFirstFinger.isScrollDown() || !this.initialSecondFinger.isScrollDown() || !this.scrollListener.onScrollDown()) {
                        bHandled = false;
                    }
                    clear();
                    return bHandled;
                }
                clear();
                return false;
            case 2:
                return (this.initialFirstFinger == null && this.initialSecondFinger == null) ? false : true;
            case 3:
                clear();
                return false;
            case 4:
            default:
                clear();
                return false;
            case 5:
                if (!this.isMultiTouchBegin && this.initialFirstFinger == null && this.initialSecondFinger == null) {
                    int pointerIndex2 = MotionEventWrapper.findPointerIndex(ev, this.firstPointerId);
                    float x2 = MotionEventWrapper.getX(ev, pointerIndex2);
                    float y2 = MotionEventWrapper.getY(ev, pointerIndex2);
                    this.initialFirstFinger = new Finger();
                    this.initialFirstFinger.setStartX(x2);
                    this.initialFirstFinger.setStartY(y2);
                    int pointer2Index = (action & MotionEventWrapper.ACTION_POINTER_INDEX_MASK) >> 8;
                    this.secondPointerId = MotionEventWrapper.getPointerId(ev, pointer2Index);
                    float x3 = MotionEventWrapper.getX(ev, pointer2Index);
                    float y3 = MotionEventWrapper.getY(ev, pointer2Index);
                    this.initialSecondFinger = new Finger();
                    this.initialSecondFinger.setStartX(x3);
                    this.initialSecondFinger.setStartY(y3);
                    this.isMultiTouchBegin = true;
                    return true;
                }
                if (this.isMultiTouchReleaseBegin) {
                    this.isMultiTouchBegin = false;
                }
                return true;
            case 6:
                if (this.isMultiTouchBegin) {
                    int pointerIndex3 = (action & MotionEventWrapper.ACTION_POINTER_INDEX_MASK) >> 8;
                    int pointerId = MotionEventWrapper.getPointerId(ev, pointerIndex3);
                    float x4 = MotionEventWrapper.getX(ev, pointerIndex3);
                    float y4 = MotionEventWrapper.getY(ev, pointerIndex3);
                    if (pointerId == this.firstPointerId) {
                        this.initialFirstFinger.setEndX(x4);
                        this.initialFirstFinger.setEndY(y4);
                        this.firstPointerId = -1;
                        this.isMultiTouchReleaseBegin = true;
                    } else if (pointerId == this.secondPointerId) {
                        this.initialSecondFinger.setEndX(x4);
                        this.initialSecondFinger.setEndY(y4);
                        this.secondPointerId = -1;
                        this.isMultiTouchReleaseBegin = true;
                    }
                }
                return true;
        }
    }
}
