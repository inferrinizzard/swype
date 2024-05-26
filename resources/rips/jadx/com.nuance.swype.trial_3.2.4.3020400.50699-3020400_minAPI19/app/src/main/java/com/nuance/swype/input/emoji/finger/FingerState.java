package com.nuance.swype.input.emoji.finger;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.nuance.swype.input.R;
import com.nuance.swype.input.emoji.finger.FingerInfo;
import com.nuance.swype.util.Callback;
import com.nuance.swype.util.LogManager;
import java.text.NumberFormat;

/* loaded from: classes.dex */
public class FingerState implements FingerInfo {
    private static final boolean ENABLE_LOG = false;
    protected static final LogManager.Log log = LogManager.getLog("FingerState");
    private static NumberFormat nf;
    private Fingerable currentObject;
    private Fingerable downObject;
    private boolean isCaptured;
    private FingerStateListener listener;
    private final Params params;
    private int pointerId;
    private State singlePressedState;
    private State unpressedState;
    private VelocityTracker velocityTracker;
    private float xDown;
    private float xPos;
    private float xVel;
    private float yDown;
    private float yPos;
    private float yVel;
    private Callback<? extends Runnable> timer = Callback.create$afe0100(new Runnable() { // from class: com.nuance.swype.input.emoji.finger.FingerState.1
        @Override // java.lang.Runnable
        public void run() {
            FingerState.this.onTimeout();
        }
    }, 0);
    private State idleState = new State() { // from class: com.nuance.swype.input.emoji.finger.FingerState.2
        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public String getName() {
            return "[idle]";
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        protected FingerInfo.PressState getPressState() {
            return FingerInfo.PressState.UNPRESSED;
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onEnter(State last) {
            FingerState.this.timer.stop();
            FingerState.this.isCaptured = false;
            FingerState.this.downObject = null;
            FingerState.this.currentObject = null;
            FingerState.this.xDown = FingerState.this.yDown = 0.0f;
            FingerState.this.xPos = FingerState.this.xDown;
            FingerState.this.yPos = FingerState.this.yDown;
            FingerState.this.xVel = FingerState.this.yVel = 0.0f;
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onCancel() {
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onEscape() {
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onDown(Fingerable objectUnderCursor, MotionEvent me) {
            int pointerIndex = me.getAction() >> 8;
            FingerState.this.xDown = me.getX(pointerIndex);
            FingerState.this.yDown = me.getY(pointerIndex);
            FingerState.this.xPos = FingerState.this.xDown;
            FingerState.this.yPos = FingerState.this.yDown;
            FingerState.this.xVel = FingerState.this.yVel = 0.0f;
            FingerState.this.downObject = objectUnderCursor;
            FingerState.this.currentObject = objectUnderCursor;
            slog("onDown(): x: " + FingerState.this.xPos + "; y: " + FingerState.this.yPos);
            if (objectUnderCursor != null) {
                FingerState.this.changeState(FingerState.this.singlePressedState);
                if (FingerState.this.listener != null) {
                    FingerState.this.listener.onFingerPress(FingerState.this.downObject, FingerState.this);
                    return;
                }
                return;
            }
            slog("onDown(): no item under finger");
            FingerState.this.changeState(FingerState.this.unpressedState);
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onMove(Fingerable itemUnderFinger, float x, float y, boolean isHistorical) {
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onUp(Fingerable item, MotionEvent me) {
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public boolean isActive() {
            return false;
        }
    };
    private State state = this.idleState;
    private State shortHoldState = new PressedState() { // from class: com.nuance.swype.input.emoji.finger.FingerState.3
        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public String getName() {
            return "[short-hold]";
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        protected FingerInfo.PressState getPressState() {
            return FingerInfo.PressState.SHORT;
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onEnter(State last) {
            FingerState.this.timer.restart(FingerState.this.params.holdTimeout);
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onExit(State next) {
            FingerState.this.timer.stop();
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.ActiveState, com.nuance.swype.input.emoji.finger.FingerState.State
        public void onUp(Fingerable itemUnderFinger, MotionEvent me) {
            if (FingerState.this.listener != null) {
                FingerState.this.listener.onFingerRelease(FingerState.this.downObject, FingerState.this, false);
            }
            FingerState.this.changeState(FingerState.this.idleState);
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onTimeout() {
            slog("onTimeout()");
            FingerState.this.changeState(FingerState.this.longHoldState);
            if (FingerState.this.listener != null) {
                FingerState.this.listener.onFingerPress(FingerState.this.downObject, FingerState.this);
            }
        }
    };
    private State longHoldState = new PressedState() { // from class: com.nuance.swype.input.emoji.finger.FingerState.4
        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public String getName() {
            return "[long-hold]";
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        protected FingerInfo.PressState getPressState() {
            return FingerInfo.PressState.LONG;
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.ActiveState, com.nuance.swype.input.emoji.finger.FingerState.State
        public void onUp(Fingerable itemUnderFinger, MotionEvent me) {
            if (FingerState.this.listener != null) {
                FingerState.this.listener.onFingerRelease(FingerState.this.downObject, FingerState.this, false);
            }
            FingerState.this.changeState(FingerState.this.idleState);
        }
    };
    private State doublePressedState = new PressedState() { // from class: com.nuance.swype.input.emoji.finger.FingerState.5
        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public String getName() {
            return "[double-pressed]";
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        protected FingerInfo.PressState getPressState() {
            return FingerInfo.PressState.DOUBLE;
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.ActiveState, com.nuance.swype.input.emoji.finger.FingerState.State
        public void onUp(Fingerable itemUnderFinger, MotionEvent me) {
            if (FingerState.this.listener != null) {
                FingerState.this.listener.onFingerRelease(FingerState.this.downObject, FingerState.this, false);
            }
            FingerState.this.changeState(FingerState.this.idleState);
        }
    };
    private State doublePressPendingState = new State() { // from class: com.nuance.swype.input.emoji.finger.FingerState.6
        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public String getName() {
            return "[double-press-pending]";
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        protected FingerInfo.PressState getPressState() {
            return FingerInfo.PressState.UNPRESSED;
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onEnter(State last) {
            FingerState.this.timer.restart(FingerState.this.params.doublePressTimeout);
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onExit(State next) {
            FingerState.this.timer.stop();
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public boolean isDoublePressPending() {
            return true;
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onDown(Fingerable itemUnderFinger, MotionEvent me) {
            if (FingerState.this.downObject.equals(itemUnderFinger)) {
                FingerState.this.changeState(FingerState.this.doublePressedState);
                if (FingerState.this.listener != null) {
                    FingerState.this.listener.onFingerPress(FingerState.this.downObject, FingerState.this);
                    return;
                }
                return;
            }
            slog("onDown(): down in new item (cancel and go to down state)");
            onCancel();
            FingerState.this.onPointerDown(itemUnderFinger, me);
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onMove(Fingerable itemUnderFinger, float x, float y, boolean isHistorical) {
            slog("onFingerMove(): unexpected in current state");
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onUp(Fingerable itemUnderFinger, MotionEvent me) {
            slog("onUp(): unexpected in current state");
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onTimeout() {
            FingerState.this.changeState(FingerState.this.singlePressedState);
            if (FingerState.this.listener != null) {
                FingerState.this.listener.onFingerRelease(FingerState.this.downObject, FingerState.this, false);
            }
            FingerState.this.changeState(FingerState.this.idleState);
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public boolean isActive() {
            return false;
        }
    };

    static {
        NumberFormat numberFormat = NumberFormat.getInstance();
        nf = numberFormat;
        numberFormat.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        nf.setGroupingUsed(false);
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerInfo
    public boolean isDown() {
        return this.state.isDown();
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerInfo
    public FingerInfo.PressState getPressState() {
        return this.state.getPressState();
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerInfo
    public float getPosX() {
        return this.xPos;
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerInfo
    public float getPosY() {
        return this.yPos;
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerInfo
    public float getVelX() {
        return this.xVel;
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerInfo
    public float getVelY() {
        return this.yVel;
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerInfo
    public float getDownX() {
        return this.xDown;
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerInfo
    public float getDownY() {
        return this.yDown;
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerInfo
    public float getOffsetX() {
        return this.xPos - this.xDown;
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerInfo
    public float getOffsetY() {
        return this.yPos - this.yDown;
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerInfo
    public int getPointerId() {
        return this.pointerId;
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerInfo
    public String getDesc() {
        return "ps: " + getPressState() + "; id: " + getPointerId();
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerInfo
    public String getMoveDesc() {
        return "x: " + nf.format(getPosX()) + "; y: " + nf.format(getPosY()) + "; xv: " + nf.format(getVelX()) + "; yv: " + nf.format(getVelY());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMovementData(float x, float y, boolean isHistorical) {
        this.xPos = x;
        this.yPos = y;
        if (this.velocityTracker != null && !isHistorical) {
            this.velocityTracker.computeCurrentVelocity(1000);
            this.xVel = this.velocityTracker.getXVelocity(this.pointerId);
            this.yVel = this.velocityTracker.getYVelocity(this.pointerId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMovementData(MotionEvent me, boolean isHistorical) {
        int pointerIndex = me.getAction() >> 8;
        updateMovementData(me.getX(pointerIndex), me.getY(pointerIndex), isHistorical);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public abstract class State {
        private State() {
        }

        public void onEnter(State last) {
        }

        public void onExit(State last) {
        }

        protected FingerInfo.PressState getPressState() {
            return FingerInfo.PressState.UNPRESSED;
        }

        protected boolean isDown() {
            return false;
        }

        public boolean isDoublePressPending() {
            return false;
        }

        public void onDown(Fingerable itemUnderFinger, MotionEvent me) {
            slog("onDown(): unhandled");
        }

        public void onCapture() {
            slog("onCapture(): unhandled");
        }

        public void onMove(Fingerable itemUnderFinger, float x, float y, boolean isHistorical) {
            slog("onMove(): unhandled");
        }

        public void onUp(Fingerable item, MotionEvent me) {
            slog("onUp(): unhandled");
        }

        public void onCancel() {
            if (FingerState.this.listener != null) {
                FingerState.this.listener.onFingerCancel(FingerState.this.downObject, FingerState.this);
            }
            FingerState.this.changeState(FingerState.this.idleState);
        }

        public void onEscape() {
            if (FingerState.this.downObject != null && FingerState.this.listener != null) {
                FingerState.this.listener.onFingerRelease(FingerState.this.downObject, FingerState.this, true);
            }
            FingerState.this.changeState(FingerState.this.unpressedState);
        }

        public void onTimeout() {
            slog("onTimeout(): unhandled");
        }

        protected void slog(Object msg) {
        }

        public String getName() {
            return getClass().getSimpleName();
        }

        public boolean isActive() {
            return true;
        }
    }

    /* loaded from: classes.dex */
    private abstract class ActiveState extends State {
        private ActiveState() {
            super();
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        protected boolean isDown() {
            return true;
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onMove(Fingerable objectUnderFinger, float x, float y, boolean isHistorical) {
            FingerState.this.updateMovementData(x, y, isHistorical);
            boolean notifyMove = true;
            if (FingerState.this.currentObject == null || !FingerState.this.currentObject.equals(objectUnderFinger)) {
                slog("onMove(): old: " + FingerState.this.currentObject + "; new: " + objectUnderFinger + "; captured: " + FingerState.this.isCaptured);
                FingerState.this.currentObject = objectUnderFinger;
                if (!FingerState.this.isCaptured) {
                    slog("onMove(): escape current object");
                    if (objectUnderFinger != null) {
                        slog("onMove(): new object");
                        FingerState.this.downObject = FingerState.this.currentObject = objectUnderFinger;
                        FingerState.this.changeState(FingerState.this.singlePressedState);
                        if (FingerState.this.listener != null) {
                            FingerState.this.listener.onFingerPress(FingerState.this.downObject, FingerState.this);
                        }
                        notifyMove = false;
                    }
                }
            }
            if (notifyMove && FingerState.this.listener != null) {
                FingerState.this.listener.onFingerMove(FingerState.this.downObject, FingerState.this);
            }
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onUp(Fingerable itemUnderFinger, MotionEvent me) {
            FingerState.this.updateMovementData(me, false);
            if (FingerState.this.listener != null) {
                FingerState.this.listener.onFingerRelease(FingerState.this.downObject, FingerState.this, false);
            }
            FingerState.this.changeState(FingerState.this.idleState);
        }
    }

    /* loaded from: classes.dex */
    private abstract class PressedState extends ActiveState {
        private PressedState() {
            super();
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onCapture() {
            if (!FingerState.this.isCaptured) {
                slog("onCapture()");
                FingerState.this.isCaptured = true;
            }
        }
    }

    /* loaded from: classes.dex */
    private class SinglePressedState extends PressedState {
        private SinglePressedState() {
            super();
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public String getName() {
            return "[pressed]";
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        protected FingerInfo.PressState getPressState() {
            return FingerInfo.PressState.PRESSED;
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onEnter(State last) {
            if (FingerState.this.downObject.isPressHoldSupported()) {
                FingerState.this.timer.restart(FingerState.this.params.holdTimeout);
            }
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onExit(State next) {
            FingerState.this.timer.stop();
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.ActiveState, com.nuance.swype.input.emoji.finger.FingerState.State
        public void onUp(Fingerable itemUnderFinger, MotionEvent me) {
            FingerState.this.updateMovementData(me, false);
            if (FingerState.this.downObject.isDoubleTapSupported()) {
                FingerState.this.changeState(FingerState.this.doublePressPendingState);
                return;
            }
            if (FingerState.this.listener != null) {
                FingerState.this.listener.onFingerRelease(FingerState.this.downObject, FingerState.this, false);
            }
            FingerState.this.changeState(FingerState.this.idleState);
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onTimeout() {
            slog("onTimeout()");
            FingerState.this.changeState(FingerState.this.shortHoldState);
            if (FingerState.this.listener != null) {
                FingerState.this.listener.onFingerPress(FingerState.this.downObject, FingerState.this);
            }
        }
    }

    /* loaded from: classes.dex */
    private class UnpressedState extends ActiveState {
        private UnpressedState() {
            super();
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public String getName() {
            return "[unpressed]";
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        protected FingerInfo.PressState getPressState() {
            return FingerInfo.PressState.UNPRESSED;
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onEnter(State last) {
            FingerState.this.timer.stop();
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onExit(State next) {
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerState.State
        public void onEscape() {
        }
    }

    public FingerState(FingerStateListener listener, int pointerId, Params params) {
        this.singlePressedState = new SinglePressedState();
        this.unpressedState = new UnpressedState();
        this.params = params;
        this.pointerId = pointerId;
        setListener(listener);
    }

    protected static String getName(State state) {
        return state != null ? state.getName() : "[*]";
    }

    public final void setListener(FingerStateListener listener) {
        this.listener = listener;
    }

    public Fingerable getDownObject() {
        return this.downObject;
    }

    private void discardVelocityTracker() {
        if (this.velocityTracker != null) {
            this.velocityTracker.recycle();
            this.velocityTracker = null;
        }
    }

    private void updateVelocityTracker(MotionEvent me) {
        if (this.params.isTrackVelocityEnabled) {
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.velocityTracker.addMovement(me);
        }
    }

    public void onPointerDown(Fingerable itemUnderFinger, MotionEvent me) {
        dlog("onPointerDown()");
        if (itemUnderFinger == null) {
            dlog("onDown(): no item associated with down event");
        }
        discardVelocityTracker();
        updateVelocityTracker(me);
        this.state.onDown(itemUnderFinger, me);
    }

    public void capture() {
        dlog("capture()");
        this.state.onCapture();
    }

    public void onVelocityData(MotionEvent me) {
        updateVelocityTracker(me);
    }

    public void onPointerMove(Fingerable itemUnderFinger, float x, float y, boolean isHistorical) {
        this.state.onMove(itemUnderFinger, x, y, isHistorical);
    }

    public void onPointerCancel() {
        dlog("onPointerCancel()");
        this.state.onCancel();
        discardVelocityTracker();
    }

    public void onPointerRelease(Fingerable itemUnderFinger, MotionEvent me) {
        dlog("onPointerRelease()");
        updateVelocityTracker(me);
        this.state.onUp(itemUnderFinger, me);
        discardVelocityTracker();
    }

    public boolean isDoublePressPending() {
        return this.state.isDoublePressPending();
    }

    protected void dlog(Object msg) {
    }

    protected void changeState(State next) {
        dlog("changeState():" + getName(this.state) + "->" + getName(next));
        if (this.state != null) {
            this.state.onExit(next);
        }
        State old = this.state;
        this.state = next;
        if (this.state != null) {
            this.state.onEnter(old);
        }
    }

    protected void onTimeout() {
        this.state.onTimeout();
    }

    public boolean isActive() {
        return this.state.isActive();
    }

    public boolean isCaptured() {
        return this.isCaptured;
    }

    /* loaded from: classes.dex */
    public static class Params {
        public final int doublePressTimeout;
        public final int holdTimeout;
        public final boolean isTrackVelocityEnabled;
        int moveThreshold;
        public final int repeatTimeout;

        /* loaded from: classes.dex */
        public static class Builder {
            private int doublePressTimeout;
            private int holdTimeout;
            private boolean isTrackVelocityEnabled;
            private int moveThreshold;
            private int repeatTimeout;

            public Builder(Params params) {
                this.doublePressTimeout = params.doublePressTimeout;
                this.holdTimeout = params.holdTimeout;
                this.repeatTimeout = params.repeatTimeout;
                this.isTrackVelocityEnabled = params.isTrackVelocityEnabled;
                this.moveThreshold = params.moveThreshold;
            }

            public Params build() {
                return new Params(this.doublePressTimeout, this.holdTimeout, this.repeatTimeout, this.isTrackVelocityEnabled, this.moveThreshold);
            }

            public Builder setDoublePressTimeout(int val) {
                this.doublePressTimeout = val;
                return this;
            }

            public Builder setHoldTimeout(int val) {
                this.holdTimeout = val;
                return this;
            }

            public Builder setRepeatTimeout(int val) {
                this.repeatTimeout = val;
                return this;
            }

            public Builder setTrackVelocity(boolean val) {
                this.isTrackVelocityEnabled = val;
                return this;
            }
        }

        protected Params(int doublePressTimeout, int holdTimeout, int repeatTimeout, boolean isTrackVelocityEnabled, int moveThreshold) {
            this.doublePressTimeout = doublePressTimeout;
            this.holdTimeout = holdTimeout;
            this.repeatTimeout = repeatTimeout;
            this.isTrackVelocityEnabled = isTrackVelocityEnabled;
            this.moveThreshold = moveThreshold;
        }

        public static Params createDefault(Context context) {
            int moveThreshold = ViewConfiguration.get(context).getScaledTouchSlop();
            return new Params(R.styleable.ThemeTemplate_symKeyboardHwr123, 500, 250, true, moveThreshold);
        }
    }
}
