package com.nuance.swype.input.accessibility;

import android.view.MotionEvent;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class KeyboardModel {
    private static KeyboardModel instance;
    private AccessibilityLabel accessibilityLabel;
    private KeyboardEx.Key currentKey;
    private KeyboardViewEx keyboardContext;
    private MotionEvent motionEvent;
    private KeyboardViewEx.Pointer pointer;
    private ArrayList<String> wordChoiceList;

    public static KeyboardModel getInstance() {
        if (instance == null) {
            instance = new KeyboardModel();
        }
        return instance;
    }

    public void setPointer(KeyboardViewEx.Pointer pointer) {
        this.pointer = pointer;
    }

    public KeyboardViewEx.Pointer getPointer() {
        return this.pointer;
    }

    public void setMotionEvent(MotionEvent motionEvent) {
        this.motionEvent = motionEvent;
    }

    public MotionEvent getMotionEvent() {
        return this.motionEvent;
    }

    public KeyboardViewEx getKeyboardContext() {
        return this.keyboardContext;
    }

    public void setKeyboardContext(KeyboardViewEx keyboardContext) {
        this.keyboardContext = keyboardContext;
        checkAccessibilityLabelInitialization();
    }

    public KeyboardEx.Key getCurrentKey() {
        return this.currentKey;
    }

    public void setCurrentKey(KeyboardEx.Key key) {
        this.currentKey = key;
    }

    public boolean isInShiftState() {
        return this.keyboardContext.getShiftState() != Shift.ShiftState.OFF;
    }

    public ArrayList<String> getWordChoiceList() {
        return this.wordChoiceList;
    }

    public void setWordChoiceList(ArrayList<String> wordChoiceList) {
        this.wordChoiceList = wordChoiceList;
    }

    void checkAccessibilityLabelInitialization() {
        if (this.keyboardContext != null && this.accessibilityLabel == null) {
            this.accessibilityLabel = new AccessibilityLabel(this.keyboardContext.getContext());
        }
    }

    public AccessibilityLabel getAccessibilityLabel() {
        checkAccessibilityLabelInitialization();
        return this.accessibilityLabel;
    }

    public KeyboardEx.KeyboardLayerType getKeyboardLayer() {
        InputView currentView;
        KeyboardEx.KeyboardLayerType layerType = KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID;
        IME ime = IMEApplication.from(this.keyboardContext.getContext()).getIME();
        if (ime != null && (currentView = ime.getCurrentInputView()) != null) {
            return currentView.getKeyboardLayer();
        }
        return layerType;
    }
}
