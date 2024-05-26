package com.nuance.swype.input.accessibility.statemachine;

import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.view.accessibility.AccessibilityEvent;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.R;
import com.nuance.swype.input.accessibility.AccessibilityInfo;
import com.nuance.swype.input.accessibility.AccessibilityLabel;
import com.nuance.swype.input.accessibility.AccessibilityNotification;
import com.nuance.swype.input.accessibility.KeyboardModel;
import com.nuance.swype.input.accessibility.SoundResources;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class ExplorationState extends KeyboardAccessibilityState {
    private static final int ACCESSIBILITY_LONG_PRESS_TIMEOUT_FACTOR = 10;
    private static final int ACCESSIBILITY_SHORT_PRESS_TIMEOUT_FACTOR = 3;
    protected static final int MSG_SPEAK_AFTER_DELAY = 1;
    private static ExplorationState instance;
    protected static final LogManager.Log log = LogManager.getLog("ExplorationState");
    private AccessibilityInfo accessibilityInfo;
    private final Handler handler = new Handler() { // from class: com.nuance.swype.input.accessibility.statemachine.ExplorationState.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (ExplorationState.this.mCurrentAccessibilityKey != null) {
                        ExplorationState.this.mLastAccessibilityKey = null;
                        AccessibilityNotification.getInstance().speak(ExplorationState.this.getCurrentView().getContext());
                    }
                    ExplorationState.this.mFirstKeyDelayCompleted = true;
                    ExplorationState.this.validInputConfirmed = true;
                    return;
                default:
                    return;
            }
        }
    };
    private boolean isExploreByTouchOn;
    private boolean isExploring;
    private boolean isFirstTime;
    private int keyboardTopExitThreshold;
    private double lastSpeed;
    private boolean mFirstKeyDelayCompleted;
    private KeyboardEx.Key mLastAccessibilityKey;
    private long mLastEventTime;
    private KeyboardEx.Key mLastKeyUsedForClick;
    private Point mLastPoint;
    private int minimumMoveTime;
    private int scrubGestureBeingValue;
    private int tapHoldDuration;
    private double thresholdSpeed;
    private boolean validInputConfirmed;

    public static KeyboardAccessibilityState getInstance() {
        if (instance == null) {
            instance = new ExplorationState();
        }
        return instance;
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void handleActionDown(KeyboardViewEx.Pointer pointer, KeyboardEx.Key key) {
        this.handler.removeMessages(1);
        this.validInputConfirmed = true;
        this.isExploring = false;
        KeyboardViewEx currentView = getCurrentView();
        this.isFirstTime = true;
        this.mLastPoint = null;
        this.mFirstKeyDelayCompleted = false;
        if (currentView != null) {
            IMEApplication.from(currentView.getContext()).getIME().closeLanguageList();
            Point point = pointer.getCurrentLocation();
            this.mCurrentAccessibilityKey = key;
            if (point.x < this.hover_exit_adjustment_width_offset_low || point.x > currentView.getWidth() - this.hover_exit_adjustment_width_offset_low) {
                playSoundPlayedOnKeyboardExit();
            }
            KeyboardViewEx.Pointer initialPointer = currentView.getFrozenPointerState(pointer);
            setKeyboardModel(initialPointer);
        }
    }

    private void setKeyboardModel(KeyboardViewEx.Pointer pointer) {
        KeyboardModel keyboardModel = KeyboardModel.getInstance();
        keyboardModel.setKeyboardContext(getCurrentView());
        keyboardModel.setPointer(pointer);
        keyboardModel.setCurrentKey(this.mCurrentAccessibilityKey);
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void handleActionMove(KeyboardViewEx.Pointer pointer, KeyboardEx.Key key) {
        KeyboardViewEx currentView = getCurrentView();
        Point currentPoint = pointer.getCurrentLocation();
        if (currentPoint.y < this.keyboardTopExitThreshold || currentPoint.y > currentView.getHeight() || currentPoint.x < this.hover_exit_adjustment_width_offset_low || currentPoint.x > currentView.getWidth() - this.hover_exit_adjustment_width_offset_low) {
            playSoundPlayedOnKeyboardExit();
        } else {
            resetPlaySoundPlayedOnKeyboardExit();
        }
        if ((currentView instanceof InputView) && ((InputView) currentView).getWordCandidateListContainer() != null) {
            this.scrubGestureBeingValue = -((InputView) currentView).getWordCandidateListContainer().getHeight();
        }
        log.d("ExplorationState currentpoint y:", Integer.valueOf(currentPoint.y), " scrubGestureBeingValue:", Integer.valueOf(this.scrubGestureBeingValue));
        if (currentPoint.y < this.scrubGestureBeingValue) {
            log.d("ExplorationState on top of WCL");
            changeState(WordSelectionState.getInstance());
            this.handler.removeMessages(1);
            return;
        }
        this.mCurrentAccessibilityKey = key;
        this.isExploring = true;
        if (this.mCurrentAccessibilityKey != null && (this.mLastAccessibilityKey == null || this.mCurrentAccessibilityKey != this.mLastAccessibilityKey)) {
            long time = pointer.getCurrentTime();
            this.isTooFast = isTooFast(currentPoint, time);
            if (!this.isTooFast) {
                if (this.isExploreByTouchOn && shouldSpeakForPassword()) {
                    AccessibilityNotification.getInstance().speak(currentView.getContext());
                }
                this.mLastAccessibilityKey = this.mCurrentAccessibilityKey;
                this.mLastPoint.x = currentPoint.x;
                this.mLastPoint.y = currentPoint.y;
                this.mLastEventTime = time;
            } else if (this.mCurrentAccessibilityKey != null && SoundResources.getInstance() != null && (this.mLastKeyUsedForClick == null || this.mLastKeyUsedForClick != this.mCurrentAccessibilityKey)) {
                SoundResources.getInstance().play(2);
                this.mLastKeyUsedForClick = this.mCurrentAccessibilityKey;
            }
        }
        KeyboardViewEx.Pointer currentPointer = currentView.getFrozenPointerState(pointer);
        setKeyboardModel(currentPointer);
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void handleActionUp(KeyboardViewEx.Pointer pointer, KeyboardEx.Key key) {
        this.handler.removeMessages(1);
        this.isExploring = false;
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void handleActionCancel(KeyboardViewEx.Pointer pointer) {
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void populateEventData(AccessibilityEvent event) {
        AccessibilityLabel accessibilityLabel = this.accessibilityInfo.getAccessibilityLabel();
        if (accessibilityLabel != null) {
            KeyboardEx.KeyboardLayerType layer = getKeyboardLayer();
            if (this.mCurrentAccessibilityKey != null) {
                CharSequence label = accessibilityLabel.getAccessibilityLabel(this.mCurrentAccessibilityKey, getCurrentView().getShiftState(), layer, this.isFirstTime);
                if (this.mCurrentAccessibilityKey != this.mLastAccessibilityKey) {
                    interruptTalkbackIfRequired(this.accessibilityInfo);
                    setAccessibilityEventText(event, label);
                }
                this.isFirstTime = false;
            }
        }
    }

    private boolean isTooFast(Point point, long currentTime) {
        if (point == null) {
            return false;
        }
        if (this.mLastPoint == null) {
            this.mLastPoint = new Point();
            this.mLastPoint.x = point.x;
            this.mLastPoint.y = point.y;
            return false;
        }
        double diffX = point.x - this.mLastPoint.x;
        double diffY = point.y - this.mLastPoint.y;
        double distance = Math.sqrt((diffX * diffX) + (diffY * diffY));
        double time = currentTime - this.mLastEventTime;
        double speed = distance / time;
        boolean fast = Math.abs(speed - this.lastSpeed) > this.thresholdSpeed;
        this.lastSpeed = speed;
        if (time < this.minimumMoveTime) {
            fast = true;
        }
        if (fast) {
            this.hover_exit_adjustment_width_offset = this.hover_exit_adjustment_width_offset_high;
            return fast;
        }
        this.hover_exit_adjustment_width_offset = this.hover_exit_adjustment_width_offset_low;
        return fast;
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void onEnter() {
        KeyboardViewEx currentView = getCurrentView();
        resetPlaySoundPlayedOnKeyboardExit();
        this.accessibilityInfo = IMEApplication.from(currentView.getContext()).getAppPreferences().getAccessibilityInfo();
        this.isExploreByTouchOn = IMEApplication.from(currentView.getContext()).getIME().isAccessibilitySupportEnabled();
        this.keyboardTopExitThreshold = currentView.getResources().getDimensionPixelSize(R.dimen.keyboard_top_exit_threshold);
        this.scrubGestureBeingValue = currentView.getResources().getDimensionPixelSize(R.dimen.scrub_gesture_being_value);
        double thresholdSpeedFactor = currentView.getResources().getInteger(R.integer.accessibility_explore_speed_threshold_factor);
        this.thresholdSpeed = currentView.getResources().getInteger(R.integer.accessibility_explore_speed_threshold) / thresholdSpeedFactor;
        this.minimumMoveTime = currentView.getResources().getInteger(R.integer.accessibility_explore_minimum_move_time);
        this.lastSpeed = currentView.getResources().getInteger(R.integer.accessibility_explore_max_speed);
        this.tapHoldDuration = getCurrentView().getContext().getResources().getInteger(R.integer.first_key_delay_timeout);
        this.hover_exit_adjustment_width_offset_low = getCurrentView().getContext().getResources().getDimensionPixelSize(R.dimen.hover_exit_adjustment_width_offset_exploration_state_low);
        this.hover_exit_adjustment_width_offset_high = getCurrentView().getContext().getResources().getDimensionPixelSize(R.dimen.hover_exit_adjustment_width_offset_exploration_state_high);
        this.hover_exit_adjustment_width_offset = this.hover_exit_adjustment_width_offset_low;
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void onExit() {
    }

    public boolean confirmValidInput() {
        return this.isExploring || this.validInputConfirmed;
    }
}
