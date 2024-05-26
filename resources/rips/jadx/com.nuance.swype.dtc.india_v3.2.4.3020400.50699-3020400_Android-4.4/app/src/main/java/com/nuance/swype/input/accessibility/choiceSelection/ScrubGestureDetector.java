package com.nuance.swype.input.accessibility.choiceSelection;

import android.graphics.Point;

/* loaded from: classes.dex */
public class ScrubGestureDetector {
    private static final int CHANGE_THRESHOLD = 4;
    private static final String TAG = "ScrubGesture";
    private int circleCount;
    Direction currentDirection;
    private int currentXSign;
    private int currentYSign;
    private boolean isFirstMove;
    private int max;
    private int min;
    private boolean newCircleComplete;
    private float previousX;
    private int previousXSign;
    private float previousY;
    private int previousYSign;
    private int xSignChangeCount;
    private int ySignChangeCount;
    private int[] xSignChangePattern = {1, 0, 0, 1};
    private int[] ySignChangePattern = {1, 1, 0, 0};

    /* loaded from: classes.dex */
    public enum Direction {
        UNDEF,
        CLOCKWISE,
        ANTI_CLOCKWISE
    }

    public void reset(Point point, int min, int max, int initialCircleCount) {
        this.xSignChangeCount = 0;
        this.ySignChangeCount = 0;
        this.previousX = point.x;
        this.previousY = point.y;
        this.previousXSign = 0;
        this.previousYSign = 0;
        this.currentXSign = 0;
        this.currentYSign = 0;
        this.isFirstMove = true;
        this.currentDirection = Direction.UNDEF;
        this.circleCount = initialCircleCount;
        this.min = min;
        this.max = max;
        this.newCircleComplete = false;
    }

    public int trackSignChange(Point point) {
        float x = point.x;
        float y = point.y;
        float diffX = x - this.previousX;
        float diffY = y - this.previousY;
        if (Math.abs(diffX) < 4.0f && Math.abs(diffY) < 4.0f) {
            return this.circleCount;
        }
        this.currentXSign = diffX > 0.0f ? 1 : 0;
        this.currentYSign = diffY <= 0.0f ? 0 : 1;
        if (this.isFirstMove) {
            this.isFirstMove = false;
            this.previousXSign = this.currentXSign;
            this.previousYSign = this.currentYSign;
        } else {
            if (Math.abs(diffX) > 4.0f && this.currentXSign != this.previousXSign) {
                this.previousXSign = this.currentXSign;
                this.xSignChangeCount++;
            }
            if (Math.abs(diffY) > 4.0f && this.currentYSign != this.previousYSign) {
                this.previousYSign = this.currentYSign;
                this.ySignChangeCount++;
            }
            computeDirection();
        }
        this.previousX = x;
        this.previousY = y;
        this.newCircleComplete = isCircleComplete();
        if (this.newCircleComplete) {
            this.ySignChangeCount = 0;
            this.xSignChangeCount = 0;
            if (this.currentDirection == Direction.CLOCKWISE) {
                if (this.circleCount < this.max) {
                    this.circleCount++;
                }
            } else if (this.circleCount > this.min) {
                this.circleCount--;
            }
        }
        if (this.currentDirection == Direction.CLOCKWISE && this.circleCount == this.min + 1) {
            this.circleCount++;
        } else if (this.currentDirection == Direction.ANTI_CLOCKWISE && this.circleCount == this.max - 1) {
            this.circleCount--;
        }
        return this.circleCount;
    }

    private boolean isCircleComplete() {
        return this.xSignChangeCount + this.ySignChangeCount > 2;
    }

    public boolean hasNewCircleComplete() {
        return this.newCircleComplete;
    }

    public boolean shouldLockInScrubMode() {
        return this.xSignChangeCount + this.ySignChangeCount > 1;
    }

    private void computeDirection() {
        int matchPointIndex = 0;
        int patternLength = this.xSignChangePattern.length;
        int i = 0;
        while (true) {
            if (i < patternLength) {
                if (this.previousXSign != this.xSignChangePattern[i] || this.previousYSign != this.ySignChangePattern[i]) {
                    i++;
                } else {
                    matchPointIndex = i;
                    break;
                }
            } else {
                break;
            }
        }
        int clockwiseCheckPointIndex = (matchPointIndex + 1) % patternLength;
        int anticlockwiseCheckPointIndex = matchPointIndex - 1;
        if (anticlockwiseCheckPointIndex < 0) {
            anticlockwiseCheckPointIndex = patternLength - 1;
        }
        if (this.currentXSign == this.xSignChangePattern[clockwiseCheckPointIndex] && this.currentYSign == this.ySignChangePattern[clockwiseCheckPointIndex]) {
            this.currentDirection = Direction.CLOCKWISE;
        } else if (this.currentXSign == this.xSignChangePattern[anticlockwiseCheckPointIndex] && this.currentYSign == this.ySignChangePattern[anticlockwiseCheckPointIndex]) {
            this.currentDirection = Direction.ANTI_CLOCKWISE;
        }
    }

    public Direction getDirection() {
        return this.currentDirection;
    }

    public int getCircleCount() {
        return this.circleCount;
    }

    public void resetCircleCount(int newCount) {
        this.circleCount = newCount;
    }
}
