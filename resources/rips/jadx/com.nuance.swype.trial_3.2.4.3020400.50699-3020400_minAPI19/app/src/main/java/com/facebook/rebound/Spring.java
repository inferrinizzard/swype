package com.facebook.rebound;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

/* loaded from: classes.dex */
public class Spring {
    private static int ID = 0;
    private static final double MAX_DELTA_TIME_SEC = 0.064d;
    private static final double SOLVER_TIMESTEP_SEC = 0.001d;
    private final PhysicsState mCurrentState;
    private double mEndValue;
    private final String mId;
    private boolean mOvershootClampingEnabled;
    private final PhysicsState mPreviousState;
    private SpringConfig mSpringConfig;
    private final BaseSpringSystem mSpringSystem;
    private double mStartValue;
    private final PhysicsState mTempState;
    private boolean mWasAtRest = true;
    private double mRestSpeedThreshold = 0.005d;
    private double mDisplacementFromRestThreshold = 0.005d;
    private CopyOnWriteArraySet<SpringListener> mListeners = new CopyOnWriteArraySet<>();
    private double mTimeAccumulator = 0.0d;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PhysicsState {
        double position;
        double velocity;

        private PhysicsState() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Spring(BaseSpringSystem springSystem) {
        this.mCurrentState = new PhysicsState();
        this.mPreviousState = new PhysicsState();
        this.mTempState = new PhysicsState();
        if (springSystem == null) {
            throw new IllegalArgumentException("Spring cannot be created outside of a BaseSpringSystem");
        }
        this.mSpringSystem = springSystem;
        StringBuilder sb = new StringBuilder("spring:");
        int i = ID;
        ID = i + 1;
        this.mId = sb.append(i).toString();
        setSpringConfig(SpringConfig.defaultConfig);
    }

    public void destroy() {
        this.mListeners.clear();
        this.mSpringSystem.deregisterSpring(this);
    }

    public String getId() {
        return this.mId;
    }

    public Spring setSpringConfig(SpringConfig springConfig) {
        if (springConfig == null) {
            throw new IllegalArgumentException("springConfig is required");
        }
        this.mSpringConfig = springConfig;
        return this;
    }

    public SpringConfig getSpringConfig() {
        return this.mSpringConfig;
    }

    public Spring setCurrentValue(double currentValue) {
        return setCurrentValue(currentValue, true);
    }

    public Spring setCurrentValue(double currentValue, boolean setAtRest) {
        this.mStartValue = currentValue;
        this.mCurrentState.position = currentValue;
        this.mSpringSystem.activateSpring(getId());
        Iterator i$ = this.mListeners.iterator();
        while (i$.hasNext()) {
            i$.next().onSpringUpdate(this);
        }
        if (setAtRest) {
            setAtRest();
        }
        return this;
    }

    public double getStartValue() {
        return this.mStartValue;
    }

    public double getCurrentValue() {
        return this.mCurrentState.position;
    }

    public double getCurrentDisplacementDistance() {
        return getDisplacementDistanceForState(this.mCurrentState);
    }

    private double getDisplacementDistanceForState(PhysicsState state) {
        return Math.abs(this.mEndValue - state.position);
    }

    public Spring setEndValue(double endValue) {
        if (this.mEndValue != endValue || !isAtRest()) {
            this.mStartValue = getCurrentValue();
            this.mEndValue = endValue;
            this.mSpringSystem.activateSpring(getId());
            Iterator i$ = this.mListeners.iterator();
            while (i$.hasNext()) {
                i$.next().onSpringEndStateChange(this);
            }
        }
        return this;
    }

    public double getEndValue() {
        return this.mEndValue;
    }

    public Spring setVelocity(double velocity) {
        if (velocity != this.mCurrentState.velocity) {
            this.mCurrentState.velocity = velocity;
            this.mSpringSystem.activateSpring(getId());
        }
        return this;
    }

    public double getVelocity() {
        return this.mCurrentState.velocity;
    }

    public Spring setRestSpeedThreshold(double restSpeedThreshold) {
        this.mRestSpeedThreshold = restSpeedThreshold;
        return this;
    }

    public double getRestSpeedThreshold() {
        return this.mRestSpeedThreshold;
    }

    public Spring setRestDisplacementThreshold(double displacementFromRestThreshold) {
        this.mDisplacementFromRestThreshold = displacementFromRestThreshold;
        return this;
    }

    public double getRestDisplacementThreshold() {
        return this.mDisplacementFromRestThreshold;
    }

    public Spring setOvershootClampingEnabled(boolean overshootClampingEnabled) {
        this.mOvershootClampingEnabled = overshootClampingEnabled;
        return this;
    }

    public boolean isOvershootClampingEnabled() {
        return this.mOvershootClampingEnabled;
    }

    public boolean isOvershooting() {
        return this.mSpringConfig.tension > 0.0d && ((this.mStartValue < this.mEndValue && getCurrentValue() > this.mEndValue) || (this.mStartValue > this.mEndValue && getCurrentValue() < this.mEndValue));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void advance(double realDeltaTime) {
        boolean isAtRest = isAtRest();
        if (!isAtRest || !this.mWasAtRest) {
            double adjustedDeltaTime = realDeltaTime;
            if (realDeltaTime > MAX_DELTA_TIME_SEC) {
                adjustedDeltaTime = MAX_DELTA_TIME_SEC;
            }
            this.mTimeAccumulator += adjustedDeltaTime;
            double tension = this.mSpringConfig.tension;
            double friction = this.mSpringConfig.friction;
            double position = this.mCurrentState.position;
            double velocity = this.mCurrentState.velocity;
            double tempPosition = this.mTempState.position;
            double tempVelocity = this.mTempState.velocity;
            while (this.mTimeAccumulator >= SOLVER_TIMESTEP_SEC) {
                this.mTimeAccumulator -= SOLVER_TIMESTEP_SEC;
                if (this.mTimeAccumulator < SOLVER_TIMESTEP_SEC) {
                    this.mPreviousState.position = position;
                    this.mPreviousState.velocity = velocity;
                }
                double aVelocity = velocity;
                double aAcceleration = ((this.mEndValue - tempPosition) * tension) - (friction * velocity);
                double tempPosition2 = position + (SOLVER_TIMESTEP_SEC * aVelocity * 0.5d);
                double tempVelocity2 = velocity + (SOLVER_TIMESTEP_SEC * aAcceleration * 0.5d);
                double bAcceleration = ((this.mEndValue - tempPosition2) * tension) - (friction * tempVelocity2);
                double tempPosition3 = position + (SOLVER_TIMESTEP_SEC * tempVelocity2 * 0.5d);
                double tempVelocity3 = velocity + (SOLVER_TIMESTEP_SEC * bAcceleration * 0.5d);
                double cAcceleration = ((this.mEndValue - tempPosition3) * tension) - (friction * tempVelocity3);
                tempPosition = position + (SOLVER_TIMESTEP_SEC * tempVelocity3);
                tempVelocity = velocity + (SOLVER_TIMESTEP_SEC * cAcceleration);
                double dAcceleration = ((this.mEndValue - tempPosition) * tension) - (friction * tempVelocity);
                double dxdt = 0.16666666666666666d * ((2.0d * (tempVelocity2 + tempVelocity3)) + aVelocity + tempVelocity);
                double dvdt = 0.16666666666666666d * ((2.0d * (bAcceleration + cAcceleration)) + aAcceleration + dAcceleration);
                position += SOLVER_TIMESTEP_SEC * dxdt;
                velocity += SOLVER_TIMESTEP_SEC * dvdt;
            }
            this.mTempState.position = tempPosition;
            this.mTempState.velocity = tempVelocity;
            this.mCurrentState.position = position;
            this.mCurrentState.velocity = velocity;
            if (this.mTimeAccumulator > 0.0d) {
                interpolate(this.mTimeAccumulator / SOLVER_TIMESTEP_SEC);
            }
            if (isAtRest() || (this.mOvershootClampingEnabled && isOvershooting())) {
                if (tension > 0.0d) {
                    this.mStartValue = this.mEndValue;
                    this.mCurrentState.position = this.mEndValue;
                } else {
                    this.mEndValue = this.mCurrentState.position;
                    this.mStartValue = this.mEndValue;
                }
                setVelocity(0.0d);
                isAtRest = true;
            }
            boolean notifyActivate = false;
            if (this.mWasAtRest) {
                this.mWasAtRest = false;
                notifyActivate = true;
            }
            boolean notifyAtRest = false;
            if (isAtRest) {
                this.mWasAtRest = true;
                notifyAtRest = true;
            }
            Iterator i$ = this.mListeners.iterator();
            while (i$.hasNext()) {
                SpringListener listener = i$.next();
                if (notifyActivate) {
                    listener.onSpringActivate(this);
                }
                listener.onSpringUpdate(this);
                if (notifyAtRest) {
                    listener.onSpringAtRest(this);
                }
            }
        }
    }

    public boolean systemShouldAdvance() {
        return (isAtRest() && wasAtRest()) ? false : true;
    }

    public boolean wasAtRest() {
        return this.mWasAtRest;
    }

    public boolean isAtRest() {
        return Math.abs(this.mCurrentState.velocity) <= this.mRestSpeedThreshold && (getDisplacementDistanceForState(this.mCurrentState) <= this.mDisplacementFromRestThreshold || this.mSpringConfig.tension == 0.0d);
    }

    public Spring setAtRest() {
        this.mEndValue = this.mCurrentState.position;
        this.mTempState.position = this.mCurrentState.position;
        this.mCurrentState.velocity = 0.0d;
        return this;
    }

    private void interpolate(double alpha) {
        this.mCurrentState.position = (this.mCurrentState.position * alpha) + (this.mPreviousState.position * (1.0d - alpha));
        this.mCurrentState.velocity = (this.mCurrentState.velocity * alpha) + (this.mPreviousState.velocity * (1.0d - alpha));
    }

    public Spring addListener(SpringListener newListener) {
        if (newListener == null) {
            throw new IllegalArgumentException("newListener is required");
        }
        this.mListeners.add(newListener);
        return this;
    }

    public Spring removeListener(SpringListener listenerToRemove) {
        if (listenerToRemove == null) {
            throw new IllegalArgumentException("listenerToRemove is required");
        }
        this.mListeners.remove(listenerToRemove);
        return this;
    }

    public Spring removeAllListeners() {
        this.mListeners.clear();
        return this;
    }

    public boolean currentValueIsApproximately(double value) {
        return Math.abs(getCurrentValue() - value) <= getRestDisplacementThreshold();
    }
}
