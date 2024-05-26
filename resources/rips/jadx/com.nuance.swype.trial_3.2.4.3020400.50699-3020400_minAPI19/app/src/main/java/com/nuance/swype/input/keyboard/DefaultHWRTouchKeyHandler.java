package com.nuance.swype.input.keyboard;

import android.text.TextUtils;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.KeyType;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher;

/* loaded from: classes.dex */
public class DefaultHWRTouchKeyHandler implements KeyboardTouchEventDispatcher.TouchKeyActionHandler {
    private final char[] functionKey = new char[1];
    private final IME ime;
    private final InputView inputView;
    private final KeyUIState keyUIState;
    private final XT9CoreInput xt9CoreInput;

    /* loaded from: classes.dex */
    public interface KeyUIState {
        void onPress(int i, int i2);

        void onRelease(int i, int i2);
    }

    public DefaultHWRTouchKeyHandler(IME ime, InputView inputView, XT9CoreInput xt9CoreInput, KeyUIState keyUIState) {
        this.ime = ime;
        this.inputView = inputView;
        this.xt9CoreInput = xt9CoreInput;
        this.keyUIState = keyUIState;
    }

    @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
    public void touchStarted(int pointerId, int keyIndex, KeyType keyType, float x, float y, int eventTime) {
        this.keyUIState.onPress(pointerId, keyIndex);
    }

    @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
    public void touchMoved(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times, boolean canBeTraced) {
    }

    @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
    public void touchEnded(int pointerId, int keyIndex, KeyType keyType, boolean isTraced, boolean quickPressed, float x, float y, int eventTime) {
        this.keyUIState.onRelease(pointerId, keyIndex);
        this.xt9CoreInput.processStoredTouch(-1, this.functionKey);
        KeyboardEx.Key key = this.inputView.getKey(keyIndex);
        Candidates candidates = this.xt9CoreInput.getCandidates(Candidates.Source.TRACE);
        if (keyType.isFunctionKey()) {
            if ((key.codes[0] == 2900 || (((isTraced || !this.inputView.handleKey(key.codes[0], quickPressed, 0)) && !this.inputView.handleGesture(key.codes[0]) && !this.inputView.handleGesture(candidates)) || key.codes[0] == 43575)) && !isTraced) {
                this.ime.onKey(null, key.codes[0], key.codes, key, 0L);
            }
        } else if (!TextUtils.isEmpty(key.text)) {
            this.inputView.onText(key.text, 0L);
        } else {
            this.inputView.handleCharKey(null, key.codes[0], key.codes, 0L);
        }
        this.xt9CoreInput.clearAllKeys();
    }

    @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
    public void touchCanceled(int pointerId, int keyIndex) {
    }

    @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
    public boolean touchHeld(int pointerId, int keyIndex, KeyType keyType) {
        return this.inputView.onShortPress(keyIndex, pointerId);
    }

    @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
    public boolean touchHeldRepeat(int pointerId, int keyIndex, KeyType keyType, int repeatCount) {
        if (keyType.isFunctionKey()) {
            KeyboardEx.Key key = this.inputView.getKey(keyIndex);
            if (key.codes[0] == 8) {
                return this.inputView.handleKey(key.codes[0], false, repeatCount);
            }
            if (KeyboardEx.isArrowKeys(key.codes[0])) {
                this.ime.onKey(null, key.codes[0], null, null, 0L);
                return true;
            }
        }
        return false;
    }

    @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
    public void touchHeldMove(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times) {
        this.inputView.onTouchHeldMoved(pointerId, xcoords, ycoords, times);
    }

    @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
    public void touchHeldEnded(int pointerId, int keyIndex, KeyType keyType) {
        this.inputView.onTouchHeldEnded(pointerId, keyIndex);
        this.keyUIState.onRelease(pointerId, keyIndex);
    }

    @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
    public void touchHelpRepeatEnded(int pointerId, int keyIndex, KeyType keyType) {
        this.keyUIState.onRelease(pointerId, keyIndex);
    }

    @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
    public void multiTapTimerTimeoutActive() {
    }

    @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
    public void multiTapTimerTimeOut() {
    }

    @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
    public void handleKeyboardShiftState(float x) {
    }
}
