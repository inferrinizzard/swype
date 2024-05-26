package com.nuance.swype.input.ads;

import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.util.LogManager;
import java.util.Calendar;

/* loaded from: classes.dex */
public class BillboardSessionTracker implements BillboardDisplayStrategy {
    private static final LogManager.Log log = LogManager.getLog("BillboardSessionTracker");
    private KeyboardEx.KeyboardDockMode mCurrentDockMode;
    private int mCurrentOrientation;
    private int mDayOfYear;
    private boolean mIsHandwriting;
    private boolean mIsNetworkConnected;
    private float mKeyboardHeight;
    private int mMaxNumberOfTimes;
    private int mNumberOfTimesShown;
    private boolean mPaused;
    private long mSessionCount;
    private int mStart;
    private int mStepSize;

    public BillboardSessionTracker(int start, int stepSize, int maxNumberOfTimes) {
        setStart(start);
        setStepSize(stepSize);
        setMaxNumberOfTimes(maxNumberOfTimes);
        log.d("Setting up with: ", Integer.valueOf(this.mStart), ", ", Integer.valueOf(this.mStepSize), ", ", Integer.valueOf(this.mMaxNumberOfTimes));
        resetSessionCount();
    }

    public void updateStart(int start) {
        setStart(start);
        resetSessionCount();
        resetNumberOfTimesShown();
    }

    public void updateStepSize(int stepSize) {
        setStepSize(stepSize);
        resetSessionCount();
        resetNumberOfTimesShown();
    }

    public void updateMaxNumberOfTimes(int maxNumberOfTimes) {
        setMaxNumberOfTimes(maxNumberOfTimes);
        resetSessionCount();
        resetNumberOfTimesShown();
    }

    public void requestIncrement() {
        if (isNewDay()) {
            log.d("Resetting the counter");
            resetSessionCount();
            this.mNumberOfTimesShown = 0;
        }
        if (canIncrementCounter()) {
            if (this.mSessionCount < 0 || this.mSessionCount >= 9223372036854775806L) {
                resetSessionCount();
            }
            this.mSessionCount++;
            log.d("Incremented session count. New count is: ", Long.valueOf(this.mSessionCount));
        }
        if (!this.mIsNetworkConnected && isInSlot()) {
            log.d("Triggering pause since we something to display, but cannot at this time");
            pause();
        }
    }

    private boolean isNewDay() {
        int dayOfYear = Calendar.getInstance().get(6);
        if (dayOfYear == this.mDayOfYear) {
            return false;
        }
        this.mDayOfYear = dayOfYear;
        log.d("New day.");
        return true;
    }

    private boolean canIncrementCounter() {
        return (this.mPaused || !isDockingModeAllowed() || this.mIsHandwriting || ((double) this.mKeyboardHeight) >= 1.2d || this.mCurrentOrientation == 2) ? false : true;
    }

    private boolean isDockingModeAllowed() {
        return this.mCurrentDockMode == KeyboardEx.KeyboardDockMode.DOCK_FULL || this.mCurrentDockMode == KeyboardEx.KeyboardDockMode.DOCK_SPLIT;
    }

    private void resetSessionCount() {
        this.mSessionCount = 0L;
    }

    private void resetNumberOfTimesShown() {
        this.mNumberOfTimesShown = 0;
    }

    private void pause() {
        log.d("Tracker is paused");
        this.mPaused = true;
    }

    public void resume() {
        log.d("Tracker resumes");
        this.mPaused = false;
    }

    @Override // com.nuance.swype.input.ads.BillboardDisplayStrategy
    public boolean canShowBillboard() {
        log.d("canShowBillboard, current state: ");
        log.d("\t number of times shown: ", Integer.valueOf(this.mNumberOfTimesShown), ". max number of times: ", Integer.valueOf(this.mMaxNumberOfTimes));
        log.d("\t session count: ", Long.valueOf(this.mSessionCount));
        LogManager.Log log2 = log;
        Object[] objArr = new Object[2];
        objArr[0] = "\t session count == start? ";
        objArr[1] = Boolean.valueOf(this.mSessionCount == ((long) this.mStart));
        log2.d(objArr);
        log.d("\t step match? ", Boolean.valueOf(isInSlot()));
        log.d("\t orientation: ", Integer.valueOf(this.mCurrentOrientation));
        log.d("\t dock mode: ", this.mCurrentDockMode, " allowed? ", Boolean.valueOf(isDockingModeAllowed()));
        log.d("\t connected to network: ", Boolean.valueOf(this.mIsNetworkConnected));
        log.d("\t height: ", Float.valueOf(this.mKeyboardHeight));
        if (this.mCurrentOrientation == 2 || !isDockingModeAllowed() || this.mIsHandwriting || !this.mIsNetworkConnected || this.mKeyboardHeight >= 1.2d) {
            log.d("Cannot show billboard");
            return false;
        }
        if (isInSlot()) {
            log.d("Billboard can be shown");
            return true;
        }
        log.d("Billboard cannot be shown");
        return false;
    }

    private boolean isInSlot() {
        return this.mNumberOfTimesShown < this.mMaxNumberOfTimes && (this.mSessionCount == ((long) this.mStart) || (this.mSessionCount > ((long) this.mStart) && (this.mSessionCount - ((long) this.mStart)) % ((long) this.mStepSize) == 0));
    }

    @Override // com.nuance.swype.input.ads.BillboardDisplayStrategy
    public void wasShown() {
        log.d("Registering that the billboard was shown successfully");
        this.mNumberOfTimesShown++;
        resume();
    }

    public void setScreenOrientation(int orientation) {
        this.mCurrentOrientation = orientation;
    }

    public void setKeyboardMode(KeyboardEx.KeyboardDockMode dockMode) {
        this.mCurrentDockMode = dockMode;
    }

    public void setNetworkConnected(boolean isNetworkConnected) {
        log.d("Network connected: ", Boolean.valueOf(isNetworkConnected));
        this.mIsNetworkConnected = isNetworkConnected;
    }

    public void setKeyboardHeight(float keyboardHeight) {
        log.d("Keyboard height is: ", Float.valueOf(keyboardHeight));
        this.mKeyboardHeight = keyboardHeight;
    }

    public void setHandwriting(boolean handwriting) {
        this.mIsHandwriting = handwriting;
    }

    private void setStart(int start) {
        if (start <= 0) {
            start = 1;
        }
        this.mStart = start;
    }

    private void setStepSize(int stepSize) {
        if (stepSize <= 0) {
            stepSize = 1;
        }
        this.mStepSize = stepSize;
    }

    private void setMaxNumberOfTimes(int maxNumberOfTimes) {
        if (maxNumberOfTimes <= 0) {
            maxNumberOfTimes = 1;
        }
        this.mMaxNumberOfTimes = maxNumberOfTimes;
    }
}
