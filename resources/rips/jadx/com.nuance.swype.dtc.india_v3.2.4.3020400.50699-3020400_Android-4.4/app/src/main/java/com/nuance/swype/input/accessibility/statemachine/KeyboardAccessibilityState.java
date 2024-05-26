package com.nuance.swype.input.accessibility.statemachine;

import android.annotation.SuppressLint;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.accessibility.AccessibilityInfo;
import com.nuance.swype.input.accessibility.KeyboardModel;
import com.nuance.swype.input.accessibility.SoundResources;

/* loaded from: classes.dex */
public abstract class KeyboardAccessibilityState {
    protected KeyboardViewEx currentKeyboardView;
    private boolean soundPlayedOnKeyboardExit;
    protected boolean isTooFast = false;
    protected int hover_exit_adjustment_width_offset = 0;
    protected int hover_exit_adjustment_width_offset_low = 0;
    protected int hover_exit_adjustment_width_offset_high = 0;
    protected KeyboardEx.Key mCurrentAccessibilityKey = null;

    public abstract void handleActionCancel(KeyboardViewEx.Pointer pointer);

    public abstract void handleActionDown(KeyboardViewEx.Pointer pointer, KeyboardEx.Key key);

    public abstract void handleActionMove(KeyboardViewEx.Pointer pointer, KeyboardEx.Key key);

    public abstract void handleActionUp(KeyboardViewEx.Pointer pointer, KeyboardEx.Key key);

    public abstract void onEnter();

    public abstract void onExit();

    public abstract void populateEventData(AccessibilityEvent accessibilityEvent);

    public void changeState(KeyboardAccessibilityState newState) {
        this.hover_exit_adjustment_width_offset = 0;
        this.currentKeyboardView.changeAccessibilityState(newState);
    }

    public void setCurrentView(KeyboardViewEx currentView) {
        this.currentKeyboardView = currentView;
    }

    public KeyboardViewEx getCurrentView() {
        return this.currentKeyboardView;
    }

    public KeyboardAccessibilityState getCurrentState() {
        return this.currentKeyboardView.getKeyboardAccessibilityState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SuppressLint({"NewApi"})
    public void setAccessibilityEventText(AccessibilityEvent event, CharSequence label) {
        if (label != null) {
            if (event.getEventType() == 8192) {
                int labelLength = label.length();
                event.setEnabled(true);
                event.getText().add(label);
                event.setFromIndex(0);
                event.setRemovedCount(0);
                event.setAddedCount(labelLength);
                event.setToIndex(labelLength);
                event.setItemCount(labelLength);
                event.setBeforeText(label);
                return;
            }
            if (event.getEventType() == 8 || event.getEventType() == 16384) {
                event.getText().add(label);
            }
        }
    }

    public boolean isMovingTooFast() {
        return this.isTooFast;
    }

    public int getHoverExitWidthOffset() {
        return this.hover_exit_adjustment_width_offset;
    }

    public void playSoundPlayedOnKeyboardExit() {
        this.currentKeyboardView.dismissSingleAltCharPopup();
        if (this.mCurrentAccessibilityKey != null) {
            this.mCurrentAccessibilityKey.focused = false;
            this.mCurrentAccessibilityKey.pressed = false;
            this.currentKeyboardView.invalidateKey(this.mCurrentAccessibilityKey);
        }
        if (!this.soundPlayedOnKeyboardExit && SoundResources.getInstance() != null) {
            SoundResources.getInstance().play(1);
            this.soundPlayedOnKeyboardExit = true;
        }
    }

    public void resetPlaySoundPlayedOnKeyboardExit() {
        this.soundPlayedOnKeyboardExit = false;
    }

    public KeyboardModel getKeyboardModel() {
        return KeyboardModel.getInstance();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public KeyboardEx.KeyboardLayerType getKeyboardLayer() {
        IME ime;
        InputView currentView;
        KeyboardEx.KeyboardLayerType layerType = KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID;
        KeyboardViewEx currentKeyboardView = getCurrentView();
        if (currentKeyboardView != null && (ime = IMEApplication.from(currentKeyboardView.getContext()).getIME()) != null && (currentView = ime.getCurrentInputView()) != null) {
            return currentView.getKeyboardLayer();
        }
        return layerType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void interruptTalkbackIfRequired(AccessibilityInfo accessibilityInfo) {
        accessibilityInfo.interruptTalkback();
    }

    public boolean shouldSpeakForPassword() {
        return (isInvisiblePasswordField() && Settings.Secure.getString(getCurrentView().getContext().getContentResolver(), "speak_password").equalsIgnoreCase("0")) ? false : true;
    }

    protected boolean isInvisiblePasswordField() {
        return IMEApplication.from(getCurrentView().getContext()).getIME().mInputFieldInfo.isInvisiblePasswordField();
    }
}
