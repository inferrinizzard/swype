package com.nuance.swype.input.accessibility.statemachine;

import com.nuance.swype.input.KeyboardViewEx;

/* loaded from: classes.dex */
public class AccessibilityStateManager {
    private static AccessibilityStateManager sInstance;
    private KeyboardAccessibilityState currentState;
    private KeyboardViewEx currentView;

    public static AccessibilityStateManager getInstance() {
        if (sInstance == null) {
            sInstance = new AccessibilityStateManager();
        }
        return sInstance;
    }

    public KeyboardAccessibilityState getKeyboardAccessibilityState() {
        return this.currentState;
    }

    private void setKeyboardAccessibilityState(KeyboardAccessibilityState state) {
        this.currentState = state;
    }

    public void changeAccessibilityState(KeyboardAccessibilityState state) {
        KeyboardAccessibilityState currentState = getKeyboardAccessibilityState();
        if (currentState != null) {
            currentState.onExit();
        }
        setKeyboardAccessibilityState(state);
        if (state != null) {
            state.setCurrentView(this.currentView);
            state.onEnter();
        }
    }

    public void setCurrentView(KeyboardViewEx view) {
        this.currentView = view;
    }
}
