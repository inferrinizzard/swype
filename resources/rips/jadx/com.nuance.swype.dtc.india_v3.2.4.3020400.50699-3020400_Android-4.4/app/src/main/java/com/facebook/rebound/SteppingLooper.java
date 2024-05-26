package com.facebook.rebound;

/* loaded from: classes.dex */
public class SteppingLooper extends SpringLooper {
    private long mLastTime;
    private boolean mStarted;

    @Override // com.facebook.rebound.SpringLooper
    public void start() {
        this.mStarted = true;
        this.mLastTime = 0L;
    }

    public boolean step(long interval) {
        if (this.mSpringSystem == null || !this.mStarted) {
            return false;
        }
        long currentTime = this.mLastTime + interval;
        this.mSpringSystem.loop(currentTime);
        this.mLastTime = currentTime;
        return this.mSpringSystem.getIsIdle();
    }

    @Override // com.facebook.rebound.SpringLooper
    public void stop() {
        this.mStarted = false;
    }
}
