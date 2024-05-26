package com.nuance.swype.input.keyboard;

import android.content.Context;
import android.os.SystemClock;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import com.nuance.input.swypecorelib.KeyType;
import com.nuance.input.swypecorelib.TouchRequestCallback;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.accessibility.statemachine.AccessibilityStateManager;
import com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState;
import com.nuance.swype.input.accessibility.statemachine.WordSelectionState;
import com.nuance.swype.input.keyboard.TimerHandler;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class KeyboardTouchEventDispatcher implements View.OnHoverListener, View.OnTouchListener, TouchRequestCallback, TimerHandler.Callbacks {
    private final XT9CoreInput coreInput;
    private final MultiTouchFlingGestures gestureDetector;
    private final IME ime;
    private final KeyPressAndHoldTracker keyPressAndHoldTracker;
    private final int longPressTimeout;
    private int shiftGestureOffset;
    private TouchKeyActionHandler touchKeyActionHandler;
    protected static final LogManager.Log log = LogManager.getLog(KeyboardTouchEventDispatcher.class.getSimpleName());
    private static final FlingGestureListener defaultFlingGestureListener = new FlingGestureListener() { // from class: com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.1
        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.FlingGestureListener
        public final boolean onFlingLeft() {
            KeyboardTouchEventDispatcher.log.d("onFlingLeft()");
            return false;
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.FlingGestureListener
        public final boolean onFlingRight() {
            KeyboardTouchEventDispatcher.log.d("onFlingRight()");
            return false;
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.FlingGestureListener
        public final boolean onFlingUp() {
            KeyboardTouchEventDispatcher.log.d("onFlingUp()");
            return false;
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.FlingGestureListener
        public final boolean onFlingDown() {
            KeyboardTouchEventDispatcher.log.d("onFlingDown()");
            return false;
        }
    };
    private int currentKeyIndex = -1;
    private final SparseArray<ResizableFloatArray> xCoordArray = new SparseArray<>();
    private final SparseArray<ResizableFloatArray> yCoordArray = new SparseArray<>();
    private final SparseArray<ResizableIntArray> timersArray = new SparseArray<>();
    private final SparseIntArray keyIndices = new SparseIntArray();
    private boolean isTracing = false;
    private final long startTime = SystemClock.uptimeMillis();
    private final TimerHandler timerHandler = new TimerHandler(this);
    private FlingGestureListener flingGestureListener = defaultFlingGestureListener;

    /* loaded from: classes.dex */
    public interface FlingGestureListener {
        boolean onFlingDown();

        boolean onFlingLeft();

        boolean onFlingRight();

        boolean onFlingUp();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum GestureDirection {
        FLIP_UP,
        FLIP_DOWN,
        FLIP_LEFT,
        FLIP_RIGHT,
        FLIP_NONE
    }

    /* loaded from: classes.dex */
    public interface TouchKeyActionHandler {
        void handleKeyboardShiftState(float f);

        void multiTapTimerTimeOut();

        void multiTapTimerTimeoutActive();

        void touchCanceled(int i, int i2);

        void touchEnded(int i, int i2, KeyType keyType, boolean z, boolean z2, float f, float f2, int i3);

        boolean touchHeld(int i, int i2, KeyType keyType);

        void touchHeldEnded(int i, int i2, KeyType keyType);

        void touchHeldMove(int i, int i2, float[] fArr, float[] fArr2, int[] iArr);

        boolean touchHeldRepeat(int i, int i2, KeyType keyType, int i3);

        void touchHelpRepeatEnded(int i, int i2, KeyType keyType);

        void touchMoved(int i, int i2, float[] fArr, float[] fArr2, int[] iArr, boolean z);

        void touchStarted(int i, int i2, KeyType keyType, float f, float f2, int i3);
    }

    public KeyboardTouchEventDispatcher(Context context, XT9CoreInput coreInput) {
        this.coreInput = coreInput;
        this.longPressTimeout = UserPreferences.from(context).getLongPressDelay();
        this.gestureDetector = new MultiTouchFlingGestures(context);
        this.keyPressAndHoldTracker = new KeyPressAndHoldTracker();
        this.ime = IMEApplication.from(context).getIME();
    }

    public void resisterTouchKeyHandler(TouchKeyActionHandler touchKeyActionHandler) {
        this.coreInput.setTouchRequestCallback(this);
        this.touchKeyActionHandler = touchKeyActionHandler;
    }

    public void unresisterTouchKeyHandler() {
        this.coreInput.setTouchRequestCallback(null);
        this.touchKeyActionHandler = null;
    }

    public void registerFlingGestureHandler(FlingGestureListener flingGestureListener) {
        this.flingGestureListener = flingGestureListener;
    }

    public void wrapTouchEvent(View view) {
        log.d("wrapTouchEvent: ", view);
        view.setOnTouchListener(this);
        clearPendingTimers();
    }

    public void wrapHoverEvent(View view) {
        log.d("wrapHoverEvent: ", view);
        view.setOnHoverListener(this);
        clearPendingTimers();
    }

    public void unwrapTouchEvent(View view) {
        log.d("unwrapTouchEvent: ", view);
        view.setOnTouchListener(null);
    }

    public void unwrapHoverEvent(View view) {
        log.d("upwrapOverEvent: ", view);
        view.setOnHoverListener(null);
    }

    private void clearPendingTimers() {
        this.timerHandler.cancelMultiTapTimerTimeOut();
        this.timerHandler.cancelRepeatKeyTimer();
        this.timerHandler.cancelLongPressKeyTimer();
        this.keyPressAndHoldTracker.endPressHoldTracker(-1);
    }

    private boolean isAccessibilitySupportEnabled() {
        return this.ime.isAccessibilitySupportEnabled();
    }

    private KeyboardAccessibilityState getKeyboardAccessibilityState() {
        return AccessibilityStateManager.getInstance().getKeyboardAccessibilityState();
    }

    private void playKeyFeedback(int keycode) {
        if (this.ime != null) {
            this.ime.vibrate();
            this.ime.playKeyClick(keycode);
        }
    }

    private void onDown(MotionEvent me) {
        int pointerId = me.getPointerId(me.getActionIndex());
        log.d("onDown(", Integer.valueOf(me.getActionMasked()), ",", Integer.valueOf(pointerId), ", ", Integer.valueOf(me.getPointerCount()), ")");
        this.xCoordArray.put(pointerId, new ResizableFloatArray());
        this.yCoordArray.put(pointerId, new ResizableFloatArray());
        this.timersArray.put(pointerId, new ResizableIntArray());
        ResizableFloatArray xcoords = new ResizableFloatArray();
        ResizableFloatArray ycoords = new ResizableFloatArray();
        ResizableIntArray times = new ResizableIntArray();
        collectPoints(me, xcoords, ycoords, times);
        float[] newYcoords = adjustShiftMargin(ycoords);
        this.coreInput.touchStart(pointerId, xcoords.getRawArray(), newYcoords, times.getRawArray());
        playKeyFeedback(0);
    }

    private void collectPoints(MotionEvent me, ResizableFloatArray xcoords, ResizableFloatArray ycoords, ResizableIntArray times) {
        int index = me.getActionIndex();
        int pointerId = me.getPointerId(index);
        xcoords.add(me.getX(index));
        ycoords.add(me.getY(index));
        times.add(normalizeEventTime(me.getEventTime()));
        this.xCoordArray.get(pointerId).add(xcoords);
        this.yCoordArray.get(pointerId).add(ycoords);
        this.timersArray.get(pointerId).add(times);
    }

    private void onUp(MotionEvent me) {
        int pointerId = me.getPointerId(me.getActionIndex());
        log.d("onUp(", Integer.valueOf(me.getActionMasked()), ", ", Integer.valueOf(pointerId), ", ", Integer.valueOf(me.getPointerCount()), ")");
        ResizableFloatArray xcoords = new ResizableFloatArray();
        ResizableFloatArray ycoords = new ResizableFloatArray();
        ResizableIntArray times = new ResizableIntArray();
        collectPoints(me, xcoords, ycoords, times);
        float[] newYcoords = adjustShiftMargin(ycoords);
        if (!this.coreInput.touchEnd(pointerId, xcoords.getRawArray(), newYcoords, times.getRawArray())) {
            this.timerHandler.cancelRepeatKeyTimer();
            this.timerHandler.cancelLongPressKeyTimer();
        }
    }

    private void onMove(MotionEvent me) {
        int historySize = me.getHistorySize();
        int pointerCounter = me.getPointerCount();
        for (int p = 0; p < pointerCounter; p++) {
            int pointerId = me.getPointerId(p);
            log.d("onMove(", Integer.valueOf(pointerId), ")");
            ResizableFloatArray xcoords = new ResizableFloatArray();
            ResizableFloatArray ycoords = new ResizableFloatArray();
            ResizableIntArray times = new ResizableIntArray();
            for (int h = 0; h < historySize; h++) {
                xcoords.add(me.getHistoricalX(p, h));
                ycoords.add(me.getHistoricalY(p, h));
                times.add(normalizeEventTime(me.getHistoricalEventTime(h)));
            }
            xcoords.add(me.getX(p));
            ycoords.add(me.getY(p));
            this.touchKeyActionHandler.handleKeyboardShiftState(me.getY(p));
            times.add(normalizeEventTime(me.getEventTime()));
            float[] newYcoords = adjustShiftMargin(ycoords);
            this.xCoordArray.get(pointerId).add(xcoords);
            this.yCoordArray.get(pointerId).add(ycoords);
            this.timersArray.get(pointerId).add(times);
            this.coreInput.touchMove(pointerId, xcoords.getRawArray(), newYcoords, times.getRawArray());
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View.OnHoverListener
    public boolean onHover(View v, MotionEvent me) {
        if (isAccessibilitySupportEnabled() && me.getPointerId(me.getActionIndex()) == 0) {
            log.d("onHove.view(", Float.valueOf(v.getX()), ", ", Float.valueOf(v.getY()), ", ", Integer.valueOf(v.getWidth()), ", ", Integer.valueOf(v.getHeight()), ")");
            KeyboardAccessibilityState currentState = getKeyboardAccessibilityState().getCurrentState();
            boolean isScrubWordSelectionState = currentState instanceof WordSelectionState;
            switch (me.getActionMasked()) {
                case 4:
                    onMove(me);
                    log.d("onHover.ACTION_OUTSIDE: x = ", Float.valueOf(this.xCoordArray.get(me.getPointerId(me.getActionIndex())).getLast()), " y = ", Float.valueOf(this.yCoordArray.get(me.getPointerId(me.getActionIndex())).getLast()));
                    break;
                case 7:
                    if (!isScrubWordSelectionState && !this.keyPressAndHoldTracker.handleTouchEvent(me)) {
                        onMove(me);
                    }
                    log.d("onHover.ACTION_HOVER_MOVE: x = ", Float.valueOf(this.xCoordArray.get(me.getPointerId(me.getActionIndex())).getLast()), " y = ", Float.valueOf(this.yCoordArray.get(me.getPointerId(me.getActionIndex())).getLast()), " rawY: ", Float.valueOf(me.getRawY()));
                    break;
                case 9:
                    if (!isScrubWordSelectionState) {
                        onDown(me);
                    }
                    log.d("onHover.ACTION_HOVER_ENTER: x = ", Float.valueOf(this.xCoordArray.get(me.getPointerId(me.getActionIndex())).getLast()), " y = ", Float.valueOf(this.yCoordArray.get(me.getPointerId(me.getActionIndex())).getLast()));
                    break;
                case 10:
                    if (!isScrubWordSelectionState && me.getY() > 0.0f) {
                        if (!this.keyPressAndHoldTracker.handleTouchEvent(me)) {
                            onUp(me);
                        }
                    } else {
                        currentState.handleActionUp(null, null);
                    }
                    log.d("onHover.ACTION_HOVER_EXIT: x = ", Float.valueOf(this.xCoordArray.get(me.getPointerId(me.getActionIndex())).getLast()), " y = ", Float.valueOf(this.yCoordArray.get(me.getPointerId(me.getActionIndex())).getLast()));
                    break;
            }
        }
        return false;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent me) {
        if (view.getTouchDelegate() != null && view.getTouchDelegate().onTouchEvent(me)) {
            log.d("onTouch handled by touch delegate");
        } else if (this.keyPressAndHoldTracker.handleTouchEvent(me)) {
            log.d("onTouch handled by press hold");
        } else if (this.gestureDetector.onTouch(view, me)) {
            log.d("onTouch handled by gesture");
        } else {
            switch (me.getActionMasked()) {
                case 0:
                case 5:
                    onDown(me);
                    break;
                case 1:
                case 6:
                    onUp(me);
                    break;
                case 2:
                    onMove(me);
                    break;
                case 3:
                    cancelAllPendingTouches();
                    break;
            }
        }
        return true;
    }

    private void cancelAllPendingTouches() {
        log.d("onCancel() pointer count: ", Integer.valueOf(this.keyIndices.size()));
        for (int p = 0; p < this.keyIndices.size(); p++) {
            int pointerId = this.keyIndices.keyAt(p);
            this.coreInput.touchCancel(pointerId);
            this.touchKeyActionHandler.touchCanceled(pointerId, -1);
            this.keyPressAndHoldTracker.endPressHoldTracker(pointerId);
        }
        this.timerHandler.cancelRepeatKeyTimer();
        this.timerHandler.cancelLongPressKeyTimer();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int normalizeEventTime(long eventTime) {
        return (int) (eventTime - this.startTime);
    }

    @Override // com.nuance.swype.input.keyboard.TimerHandler.Callbacks
    public void longPressKey(int pointerId, int keyIndex, KeyType keyType) {
        if (this.touchKeyActionHandler.touchHeld(pointerId, keyIndex, keyType)) {
            this.coreInput.touchCancel(pointerId);
            this.touchKeyActionHandler.touchCanceled(pointerId, keyIndex);
            this.keyPressAndHoldTracker.startPressHoldTracker(pointerId, keyIndex, keyType, 0);
        }
    }

    @Override // com.nuance.swype.input.keyboard.TimerHandler.Callbacks
    public void repeatedKey(int pointerId, int keyIndex, KeyType keyType, TimerHandler.RepeatKeyRate repeatKeyRate) {
        if (repeatKeyRate.getRepeatCount() < 2) {
            this.coreInput.touchCancel(pointerId);
            this.touchKeyActionHandler.touchCanceled(pointerId, keyIndex);
            this.keyPressAndHoldTracker.startPressHoldTracker(pointerId, keyIndex, keyType, repeatKeyRate.getRepeatCount() + 1);
        }
        if (this.touchKeyActionHandler.touchHeldRepeat(pointerId, keyIndex, keyType, repeatKeyRate.getRepeatCount()) && this.keyPressAndHoldTracker.getPressHoldKey(pointerId) != null) {
            repeatKeyRate.updateRepeatCount();
            this.timerHandler.startRepeatKeyTimer(pointerId, keyIndex, keyType, repeatKeyRate, repeatKeyRate.getRepeatDelay());
        }
    }

    @Override // com.nuance.swype.input.keyboard.TimerHandler.Callbacks
    public void quickPressKeyTimeOut(int pointerId, int keyIndex, KeyType keyType) {
        this.currentKeyIndex = -1;
    }

    @Override // com.nuance.swype.input.keyboard.TimerHandler.Callbacks
    public void multiTapTimerTimeOut() {
        log.d("multiTapTimerTimeOut");
        this.coreInput.multiTapTimeOut();
        this.touchKeyActionHandler.multiTapTimerTimeOut();
    }

    @Override // com.nuance.swype.input.keyboard.TimerHandler.Callbacks
    public void touchTimerTimeOut(int timeout) {
        this.coreInput.touchTimeOut(timeout);
    }

    private boolean processQuickTap(int pointerId, int keyIndex, KeyType keyType) {
        if (this.currentKeyIndex == keyIndex) {
            if (!this.timerHandler.isQuickKeyPressTimerTimeOutActive()) {
                return false;
            }
            this.timerHandler.cancelQuickKeyPressTimerTimeOut();
            this.currentKeyIndex = -1;
            return true;
        }
        this.currentKeyIndex = keyIndex;
        this.timerHandler.startQuickKeyPressTimerTimeOut(pointerId, keyIndex, keyType, ViewConfiguration.getDoubleTapTimeout());
        return false;
    }

    @Override // com.nuance.input.swypecorelib.TouchRequestCallback
    public void touchStarted(boolean mainTouch, int pointerId, KeyType keyType, int keyIndex, char keyCode, boolean canBeTraced) {
        log.d("touchStarted(): pointerId:  ", Integer.valueOf(pointerId), " keyIndex: ", Integer.valueOf(keyIndex));
        Context context = this.ime.getApplicationContext();
        IMEApplication.from(context).setUserTapKey(true);
        this.keyIndices.put(pointerId, keyIndex);
        this.timerHandler.cancelRepeatKeyTimer();
        this.timerHandler.cancelLongPressKeyTimer();
        IMEApplication.from(this.ime);
        this.isTracing = canBeTraced;
        if (mainTouch) {
            if (keyCode == '\b' || KeyboardEx.isArrowKeys(keyCode)) {
                this.timerHandler.startRepeatKeyTimer(pointerId, keyIndex, keyType, new TimerHandler.RepeatKeyRate(context), ViewConfiguration.getKeyRepeatTimeout());
            } else {
                this.timerHandler.startLongPressKeyTimer(pointerId, keyIndex, keyType, this.longPressTimeout);
            }
        }
        this.touchKeyActionHandler.touchStarted(pointerId, keyIndex, keyType, this.xCoordArray.get(pointerId).getLast(), this.yCoordArray.get(pointerId).getLast(), this.timersArray.get(pointerId).getLast());
    }

    @Override // com.nuance.input.swypecorelib.TouchRequestCallback
    public void touchUpdated(boolean mainTouch, int pointerId, KeyType keyType, int keyIndex, char keyCode, boolean canBeTraced) {
        log.d("touchUpdated(): pointerId:  ", Integer.valueOf(pointerId), " keyIndex: ", Integer.valueOf(keyIndex), " canBeTraced: ", Boolean.valueOf(canBeTraced));
        if (canBeTraced || (!this.coreInput.isTraceEnabled() && this.keyIndices.get(pointerId, -1) != keyIndex)) {
            this.timerHandler.cancelRepeatKeyTimer();
            this.timerHandler.cancelLongPressKeyTimer();
            this.timerHandler.cancelQuickKeyPressTimerTimeOut();
        }
        this.keyIndices.put(pointerId, keyIndex);
        this.isTracing = canBeTraced;
        float[] xcoords = this.xCoordArray.get(pointerId).getRawArray();
        float[] ycoords = this.yCoordArray.get(pointerId).getRawArray();
        int[] times = this.timersArray.get(pointerId).getRawArray();
        if (xcoords != null && ycoords != null && times != null) {
            this.touchKeyActionHandler.touchMoved(pointerId, keyIndex, xcoords, ycoords, times, canBeTraced);
        }
    }

    @Override // com.nuance.input.swypecorelib.TouchRequestCallback
    public void touchEnded(boolean mainTouch, int pointerId, KeyType keyType, int keyIndex, char keyCode, boolean canBeTraced) {
        boolean quickPressed = false;
        if (!canBeTraced) {
            quickPressed = processQuickTap(pointerId, keyIndex, keyType);
        }
        this.isTracing = canBeTraced;
        log.d("touchEnded(): pointerId:  ", Integer.valueOf(pointerId), " keyIndex: ", Integer.valueOf(keyIndex));
        this.timerHandler.cancelRepeatKeyTimer();
        this.timerHandler.cancelLongPressKeyTimer();
        this.touchKeyActionHandler.touchEnded(pointerId, keyIndex, keyType, canBeTraced, quickPressed, this.xCoordArray.get(pointerId).getLast(), this.yCoordArray.get(pointerId).getLast(), this.timersArray.get(pointerId).getLast());
        this.keyIndices.put(pointerId, -1);
    }

    @Override // com.nuance.input.swypecorelib.TouchRequestCallback
    public void touchCanceled(boolean mainTouch, int pointerId) {
        this.currentKeyIndex = -1;
        this.timerHandler.cancelRepeatKeyTimer();
        this.timerHandler.cancelLongPressKeyTimer();
        this.timerHandler.cancelQuickKeyPressTimerTimeOut();
    }

    @Override // com.nuance.input.swypecorelib.TouchRequestCallback
    public void keyboardLoaded(int keyboardId, int page) {
        log.d("keyboardLoaded: keyboardId = 0x", Integer.toHexString(keyboardId));
    }

    @Override // com.nuance.input.swypecorelib.TouchRequestCallback
    public void setMultiTapTimerTimeOutRequest(int timerID) {
        log.d("setMultiTapTimerTimeOutRequest timerID = ", Integer.valueOf(timerID));
        this.timerHandler.cancelQuickKeyPressTimerTimeOut();
        this.timerHandler.startMultiTapTimerTimeOut(750);
        this.touchKeyActionHandler.multiTapTimerTimeoutActive();
    }

    @Override // com.nuance.input.swypecorelib.TouchRequestCallback
    public void setTouchTimerTimeOutRequest(int timeOut) {
        this.timerHandler.startTouchTimerTimeOut(timeOut);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class KeyPressAndHoldTracker {
        private SparseArray<PressHoldKey> pressHoldKeys;

        private KeyPressAndHoldTracker() {
            this.pressHoldKeys = new SparseArray<>();
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class PressHoldKey {
            final int keyIndex;
            final KeyType keyType;
            final int repeatCount;

            PressHoldKey(int keyIndex, int repeatCount, KeyType keyType) {
                this.keyIndex = keyIndex;
                this.repeatCount = repeatCount;
                this.keyType = keyType;
            }
        }

        public final void startPressHoldTracker(int pointerId, int keyIndex, KeyType keyType, int repeatCount) {
            this.pressHoldKeys.put(pointerId, new PressHoldKey(keyIndex, repeatCount, keyType));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void endPressHoldTracker(int pointerId) {
            if (pointerId != -1) {
                this.pressHoldKeys.remove(pointerId);
            } else {
                this.pressHoldKeys.clear();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public PressHoldKey getPressHoldKey(int pointerId) {
            return this.pressHoldKeys.get(pointerId, null);
        }

        public final boolean handleTouchEvent(MotionEvent me) {
            int pointerIndex = me.getActionIndex();
            int pointerId = me.getPointerId(pointerIndex);
            PressHoldKey pressHoldKey = getPressHoldKey(pointerId);
            if (pressHoldKey == null) {
                return this.pressHoldKeys.size() > 0;
            }
            switch (me.getActionMasked()) {
                case 1:
                case 6:
                case 10:
                    KeyboardTouchEventDispatcher.this.timerHandler.cancelRepeatKeyTimer();
                    KeyboardTouchEventDispatcher.this.coreInput.touchCancel(pointerId);
                    if (pressHoldKey.repeatCount == 0) {
                        KeyboardTouchEventDispatcher.this.touchKeyActionHandler.touchHeldEnded(pointerId, pressHoldKey.keyIndex, pressHoldKey.keyType);
                    } else {
                        KeyboardTouchEventDispatcher.this.touchKeyActionHandler.touchHelpRepeatEnded(pointerId, pressHoldKey.keyIndex, pressHoldKey.keyType);
                    }
                    endPressHoldTracker(pointerId);
                    break;
                case 2:
                case 7:
                    if (pressHoldKey.repeatCount == 0) {
                        ResizableFloatArray xcoords = new ResizableFloatArray();
                        ResizableFloatArray ycoords = new ResizableFloatArray();
                        ResizableIntArray times = new ResizableIntArray();
                        for (int h = 0; h < me.getHistorySize(); h++) {
                            xcoords.add(me.getHistoricalX(pointerIndex, h));
                            ycoords.add(me.getHistoricalY(pointerIndex, h));
                            times.add(KeyboardTouchEventDispatcher.this.normalizeEventTime(me.getHistoricalEventTime(h)));
                        }
                        xcoords.add(me.getX(pointerIndex));
                        ycoords.add(me.getY(pointerIndex));
                        times.add(KeyboardTouchEventDispatcher.this.normalizeEventTime(me.getEventTime()));
                        ((ResizableFloatArray) KeyboardTouchEventDispatcher.this.xCoordArray.get(pointerId)).add(xcoords);
                        ((ResizableFloatArray) KeyboardTouchEventDispatcher.this.yCoordArray.get(pointerId)).add(ycoords);
                        ((ResizableIntArray) KeyboardTouchEventDispatcher.this.timersArray.get(pointerId)).add(times);
                        KeyboardTouchEventDispatcher.this.touchKeyActionHandler.touchHeldMove(pointerId, pressHoldKey.keyIndex, ((ResizableFloatArray) KeyboardTouchEventDispatcher.this.xCoordArray.get(pointerId)).getRawArray(), ((ResizableFloatArray) KeyboardTouchEventDispatcher.this.yCoordArray.get(pointerId)).getRawArray(), ((ResizableIntArray) KeyboardTouchEventDispatcher.this.timersArray.get(pointerId)).getRawArray());
                        break;
                    }
                    break;
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TouchGesture {
        private GestureDirection gestureDirection;
        private float x1;
        private float x2;
        private float y1;
        private float y2;

        private TouchGesture() {
            this.gestureDirection = GestureDirection.FLIP_NONE;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startPoint(float x, float y) {
            this.x1 = x;
            this.y1 = y;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void currentPoint(float x, float y) {
            this.x2 = x;
            this.y2 = y;
        }
    }

    /* loaded from: classes.dex */
    private class MultiTouchFlingGestures {
        private boolean gestureDetectAndDispatched;
        private final int minFlingVelocity;
        private final SparseArray<TouchGesture> touchGestures;
        private VelocityTracker velocityTracker;

        private MultiTouchFlingGestures(Context context) {
            this.touchGestures = new SparseArray<>();
            ViewConfiguration configuration = ViewConfiguration.get(context);
            this.minFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        }

        boolean onTouch(View view, MotionEvent event) {
            boolean touchGestureDetected = false;
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            int pointerId = event.getPointerId(event.getActionIndex());
            switch (event.getActionMasked()) {
                case 0:
                    this.gestureDetectAndDispatched = false;
                    this.velocityTracker.clear();
                    this.touchGestures.clear();
                    TouchGesture touchGesture = new TouchGesture();
                    touchGesture.startPoint(event.getX(), event.getY());
                    this.touchGestures.put(pointerId, touchGesture);
                    this.velocityTracker.addMovement(event);
                    break;
                case 1:
                    if (this.touchGestures.size() > 1 && !this.gestureDetectAndDispatched) {
                        TouchGesture touchGesture2 = this.touchGestures.get(pointerId);
                        touchGesture2.currentPoint(event.getX(event.getActionIndex()), event.getY(event.getActionIndex()));
                        this.velocityTracker.addMovement(event);
                        touchGestureDetected = detectGesture(view, pointerId, touchGesture2);
                        if (touchGestureDetected) {
                            KeyboardTouchEventDispatcher.this.coreInput.touchCancel(pointerId);
                            KeyboardTouchEventDispatcher.this.touchKeyActionHandler.touchCanceled(pointerId, -1);
                        }
                        this.gestureDetectAndDispatched = checkAndDispatchGesture();
                        break;
                    }
                    break;
                case 2:
                    this.velocityTracker.addMovement(event);
                    break;
                case 5:
                    TouchGesture touchGesture3 = new TouchGesture();
                    touchGesture3.startPoint(event.getX(event.getActionIndex()), event.getY(event.getActionIndex()));
                    this.touchGestures.put(pointerId, touchGesture3);
                    this.velocityTracker.addMovement(event);
                    break;
                case 6:
                    if (!this.gestureDetectAndDispatched) {
                        TouchGesture touchGesture4 = this.touchGestures.get(pointerId);
                        touchGesture4.currentPoint(event.getX(event.getActionIndex()), event.getY(event.getActionIndex()));
                        this.velocityTracker.addMovement(event);
                        touchGestureDetected = detectGesture(view, pointerId, touchGesture4);
                        if (touchGestureDetected) {
                            KeyboardTouchEventDispatcher.this.coreInput.touchCancel(pointerId);
                            KeyboardTouchEventDispatcher.this.touchKeyActionHandler.touchCanceled(pointerId, -1);
                        }
                        this.gestureDetectAndDispatched = checkAndDispatchGesture();
                        break;
                    }
                    break;
            }
            return this.gestureDetectAndDispatched || touchGestureDetected;
        }

        private boolean detectGesture(View view, int pointerId, TouchGesture gesture) {
            this.velocityTracker.computeCurrentVelocity(1000);
            float velocityX = this.velocityTracker.getXVelocity(pointerId);
            float velocityY = this.velocityTracker.getYVelocity(pointerId);
            KeyboardTouchEventDispatcher.log.d("velocityX(", Integer.valueOf(pointerId), ") = ", Float.valueOf(velocityX));
            KeyboardTouchEventDispatcher.log.d("velocityY(", Integer.valueOf(pointerId), ") = ", Float.valueOf(velocityY));
            if ((Math.abs(velocityX) > this.minFlingVelocity || Math.abs(velocityY) > this.minFlingVelocity) && ((Math.abs(velocityX) > this.minFlingVelocity || Math.abs(velocityY) > this.minFlingVelocity) && (Math.abs(gesture.x1 - gesture.x2) > ((int) (view.getWidth() * 0.5d)) || Math.abs(gesture.y1 - gesture.y2) > ((int) (view.getHeight() * 0.6d))))) {
                gesture.gestureDirection = getGestureDirection(gesture.x1, gesture.y1, gesture.x2, gesture.y2);
            }
            KeyboardTouchEventDispatcher.log.d("gestureDirection = ", gesture.gestureDirection);
            return gesture.gestureDirection != GestureDirection.FLIP_NONE;
        }

        private boolean checkAndDispatchGesture() {
            if (this.touchGestures.size() > 1) {
                GestureDirection gestureDirection = GestureDirection.FLIP_NONE;
                int numOfGesturesDetected = 0;
                for (int i = 0; i < this.touchGestures.size(); i++) {
                    TouchGesture gesture = this.touchGestures.get(this.touchGestures.keyAt(i));
                    if (gesture.gestureDirection == GestureDirection.FLIP_NONE || (gestureDirection != GestureDirection.FLIP_NONE && gesture.gestureDirection != gestureDirection)) {
                        gestureDirection = GestureDirection.FLIP_NONE;
                        break;
                    }
                    numOfGesturesDetected++;
                    gestureDirection = gesture.gestureDirection;
                }
                if (numOfGesturesDetected == this.touchGestures.size() && dispatchGesture(gestureDirection)) {
                    for (int i2 = 0; i2 < this.touchGestures.size(); i2++) {
                        KeyboardTouchEventDispatcher.this.coreInput.touchCancel(i2);
                        KeyboardTouchEventDispatcher.this.touchKeyActionHandler.touchCanceled(i2, -1);
                    }
                    return true;
                }
            }
            return false;
        }

        private GestureDirection getGestureDirection(float x1, float y1, float x2, float y2) {
            GestureDirection direction = GestureDirection.FLIP_NONE;
            int angle = (int) Math.ceil(Math.toDegrees(Math.atan2(y1 - y2, x2 - x1)));
            if (angle > 45 && angle <= 135) {
                return GestureDirection.FLIP_UP;
            }
            if ((angle >= 135 && angle < 180) || (angle < -135 && angle > -180)) {
                return GestureDirection.FLIP_LEFT;
            }
            if (angle < -45 && angle >= -135) {
                return GestureDirection.FLIP_DOWN;
            }
            if (angle > -45 && angle <= 45) {
                return GestureDirection.FLIP_RIGHT;
            }
            return direction;
        }

        private boolean dispatchGesture(GestureDirection gestureDirection) {
            switch (gestureDirection) {
                case FLIP_UP:
                    KeyboardTouchEventDispatcher.log.d("fling up");
                    return KeyboardTouchEventDispatcher.this.flingGestureListener.onFlingUp();
                case FLIP_DOWN:
                    KeyboardTouchEventDispatcher.log.d("fling down");
                    return KeyboardTouchEventDispatcher.this.flingGestureListener.onFlingDown();
                case FLIP_LEFT:
                    KeyboardTouchEventDispatcher.log.d("fling left");
                    return KeyboardTouchEventDispatcher.this.flingGestureListener.onFlingLeft();
                case FLIP_RIGHT:
                    KeyboardTouchEventDispatcher.log.d("fling right");
                    return KeyboardTouchEventDispatcher.this.flingGestureListener.onFlingRight();
                default:
                    return false;
            }
        }
    }

    private float[] adjustShiftMargin(ResizableFloatArray ycoords) {
        float[] newYcoords = ycoords.getRawArray();
        for (int i = 0; i < newYcoords.length; i++) {
            newYcoords[i] = newYcoords[i] + this.shiftGestureOffset;
        }
        return newYcoords;
    }

    public void setShiftGestureOffsetValue(int shiftGestureOffset) {
        this.shiftGestureOffset = shiftGestureOffset;
    }

    public boolean isTracing() {
        return this.isTracing;
    }
}
