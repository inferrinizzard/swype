package com.nuance.swype.input;

import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.util.LogManager;
import java.util.Iterator;

/* loaded from: classes.dex */
public class KeyboardSwitcher {
    protected static final LogManager.Log log = LogManager.getLog("KeyboardSwitcher");
    public IME ime;
    protected InputMethods.InputMode inputMode;
    private boolean isCycleToCapsLockSupported;
    protected int mImeOptions;
    public InputView mInputView;
    protected InputMethods.Layout mKeyboardLayout;
    protected int mKeyboardLayoutId;
    protected int mLanguageId;
    protected int mLastDisplayWidth;
    private final XT9CoreInput xt9coreinput;
    private KeyboardEx.KeyboardLayerType lastKeyboardLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID;
    KeyboardEx.KeyboardLayerType defaultKeyboardLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT;
    protected KeyboardEx.KeyboardLayerType mKeyboardEntryLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID;
    private Shift.ShiftState shiftState = Shift.ShiftState.OFF;
    private Shift.ShiftState preCycleShiftState = Shift.ShiftState.OFF;

    public KeyboardSwitcher(IME ime, XT9CoreInput xt9coreinput) {
        this.isCycleToCapsLockSupported = true;
        this.ime = ime;
        this.xt9coreinput = xt9coreinput;
        this.isCycleToCapsLockSupported = ime.getResources().getBoolean(R.bool.enable_capslock_via_cycle);
    }

    public void setInputView(InputView inputView) {
        this.mInputView = inputView;
    }

    public XT9Keyboard createKeyboardForTextInput(KeyboardEx.KeyboardLayerType keyboardLayer, InputFieldInfo fieldInfo, InputMethods.InputMode inputMode) {
        this.inputMode = inputMode;
        this.mImeOptions = fieldInfo.getImeOptions();
        this.mKeyboardLayout = inputMode.getCurrentLayout();
        this.mKeyboardLayoutId = this.mKeyboardLayout.mLayoutId;
        XT9Keyboard keyboard = createKeyboard(keyboardLayer, getKeyboardModeId(fieldInfo));
        setKeyboard(keyboard, keyboardLayer);
        keyboard.setImeOptions(this.ime, keyboardLayer, this.mImeOptions, this.ime.getInputFieldInfo());
        return keyboard;
    }

    public void returnToDefaultLayer() {
        if (this.defaultKeyboardLayer != KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID && this.mKeyboardEntryLayer != this.defaultKeyboardLayer) {
            this.mInputView.setKeyboardLayer(this.defaultKeyboardLayer);
        }
    }

    public KeyboardEx.KeyboardLayerType getDefaultLayer() {
        return this.defaultKeyboardLayer;
    }

    public XT9Keyboard createKeyboardForTextInput(InputFieldInfo inputFieldInfo, InputMethods.InputMode inputMode, boolean enableAutoLayerSelect) {
        KeyboardEx.KeyboardLayerType keyboardLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT;
        if (enableAutoLayerSelect) {
            if (inputFieldInfo.isPhoneNumberField()) {
                keyboardLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_PHONE;
            } else if (inputFieldInfo.isNumberPasswordField()) {
                keyboardLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_NUM_PW;
            } else if (inputFieldInfo.isNumericModeField()) {
                keyboardLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_NUM;
            }
        }
        log.d("createKeyboardForTextInput(): method called : keyboardLayer: " + keyboardLayer);
        this.defaultKeyboardLayer = keyboardLayer;
        return createKeyboardForTextInput(keyboardLayer, inputFieldInfo, inputMode);
    }

    public void clearCacheTableKeyboards() {
        KeyboardManager.from(this.ime).evictAll();
    }

    public XT9Keyboard createKeyboardForTextInput(InputFieldInfo inputFieldInfo, InputMethods.InputMode inputMode) {
        return createKeyboardForTextInput(inputFieldInfo, inputMode, true);
    }

    private XT9Keyboard createKeyboard(KeyboardEx.KeyboardLayerType keyboardLayer, int modeId) {
        int resId;
        if (keyboardLayer == null || keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID) {
            log.e("createKeyboard(): illegal value for keyboardLayer: " + keyboardLayer);
            keyboardLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT;
        }
        log.d("createKeyboard(): method called : keyboardLayer: " + keyboardLayer);
        switch (keyboardLayer) {
            case KEYBOARD_PHONE:
                resId = R.xml.kbd_phone;
                break;
            case KEYBOARD_EDIT:
                resId = R.xml.kbd_edit;
                break;
            case KEYBOARD_SYMBOLS:
                int symId = this.ime.getResources().getInteger(R.integer.symbols_keyboard_id);
                resId = this.inputMode.findLayout(symId).mLayoutResID;
                break;
            case KEYBOARD_NUM:
                resId = R.xml.kbd_num;
                break;
            case KEYBOARD_NUM_PW:
                resId = R.xml.kbd_num_password;
                break;
            default:
                resId = this.mKeyboardLayout.mLayoutResID;
                break;
        }
        XT9Keyboard keyboard = KeyboardManager.from(this.ime).setKeyboard(this.xt9coreinput, resId, modeId);
        if (this.mInputView.mCurrentInputLanguage.isHindiLanguage() && !this.mInputView.mCurrentInputLanguage.isBilingualLanguage()) {
            if (keyboardLayer != KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT) {
                this.ime.setTransliterationToggleButtonOff();
            } else {
                this.ime.setTransliterationToggleButtonOn();
            }
        }
        boolean showPreviewKey = keyboardLayer != KeyboardEx.KeyboardLayerType.KEYBOARD_PHONE && UserPreferences.from(this.ime).getShowPressDownPreview();
        this.mInputView.setPressDownPreviewEnabled(showPreviewKey);
        keyboard.updateABCLabel(this.mInputView.mCurrentInputLanguage, keyboardLayer);
        return keyboard;
    }

    public boolean setShiftState(Shift.ShiftState state) {
        return setShiftState(state, false);
    }

    public boolean setShiftState(Shift.ShiftState state, boolean overrideKeyLockableFlag) {
        boolean changed = this.shiftState != state;
        this.shiftState = state;
        for (KeyboardEx.Key key : this.mInputView.getKeyboard().getShiftKeys()) {
            key.on = false;
            key.locked = false;
            if (this.shiftState == Shift.ShiftState.ON) {
                if (key.sticky) {
                    key.on = true;
                }
            } else if (this.shiftState == Shift.ShiftState.LOCKED && (key.lockable || overrideKeyLockableFlag)) {
                key.locked = true;
            }
        }
        return changed;
    }

    public Shift.ShiftState getCurrentShiftState() {
        return this.shiftState;
    }

    public boolean isShifted() {
        return this.shiftState == Shift.ShiftState.LOCKED || this.shiftState == Shift.ShiftState.ON;
    }

    public boolean toggleCapsLock(boolean isDoubleTap) {
        Shift.ShiftState currentShiftState;
        if (getCurrentShiftState() == Shift.ShiftState.LOCKED) {
            currentShiftState = Shift.ShiftState.OFF;
        } else if (isDoubleTap) {
            currentShiftState = this.preCycleShiftState == Shift.ShiftState.LOCKED ? Shift.ShiftState.OFF : Shift.ShiftState.LOCKED;
        } else {
            currentShiftState = Shift.ShiftState.LOCKED;
        }
        setShiftState(currentShiftState);
        return true;
    }

    public void cycleShiftState() {
        boolean hasLock = false;
        if (this.isCycleToCapsLockSupported) {
            Iterator<KeyboardEx.Key> it = this.mInputView.getKeyboard().getShiftKeys().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                } else if (it.next().lockable) {
                    hasLock = true;
                    break;
                }
            }
        }
        this.preCycleShiftState = getCurrentShiftState();
        Shift.ShiftState newShiftState = Shift.ShiftState.OFF;
        switch (this.preCycleShiftState) {
            case OFF:
                newShiftState = Shift.ShiftState.ON;
                break;
            case ON:
                if (!hasLock) {
                    newShiftState = Shift.ShiftState.OFF;
                    break;
                } else {
                    newShiftState = Shift.ShiftState.LOCKED;
                    break;
                }
            case LOCKED:
                newShiftState = Shift.ShiftState.OFF;
                break;
        }
        setShiftState(newShiftState);
    }

    public void updateShiftStateToggle() {
        Shift.ShiftState newShiftState = Shift.ShiftState.OFF;
        switch (getCurrentShiftState()) {
            case OFF:
                newShiftState = Shift.ShiftState.LOCKED;
                break;
            case LOCKED:
                newShiftState = Shift.ShiftState.OFF;
                break;
        }
        setShiftState(newShiftState);
    }

    public void toggleSymbolKeyboard() {
        KeyboardEx.KeyboardLayerType keyboardLayer;
        if (currentKeyboardLayer() == KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS) {
            keyboardLayer = this.lastKeyboardLayer;
        } else {
            keyboardLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS;
        }
        XT9Keyboard current = createKeyboard(keyboardLayer, this.mInputView.getKeyboard().getModeId());
        current.setImeOptions(this.ime, keyboardLayer, this.mImeOptions, this.ime.getInputFieldInfo());
        setShiftState(Shift.ShiftState.OFF);
        setKeyboard(current, keyboardLayer);
    }

    public void toggleLastKeyboard() {
        if (this.lastKeyboardLayer != null) {
            this.mInputView.setKeyboardLayer(this.lastKeyboardLayer);
        }
    }

    public boolean isKeypadInput() {
        return this.mKeyboardLayout != null && (this.mKeyboardLayout.mLayoutId == 1536 || this.mKeyboardLayout.mLayoutId == 1792 || this.mKeyboardLayout.mLayoutId == 1808 || this.mKeyboardLayout.mLayoutId == 1824);
    }

    public boolean isAlphabetMode() {
        return currentKeyboardLayer() == KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT;
    }

    public boolean isEditMode() {
        return currentKeyboardLayer() == KeyboardEx.KeyboardLayerType.KEYBOARD_EDIT;
    }

    public boolean isNumMode() {
        return currentKeyboardLayer() == KeyboardEx.KeyboardLayerType.KEYBOARD_NUM || currentKeyboardLayer() == KeyboardEx.KeyboardLayerType.KEYBOARD_NUM_PW;
    }

    public boolean isSymbolMode() {
        return currentKeyboardLayer() == KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS;
    }

    public boolean isPhoneMode() {
        return currentKeyboardLayer() == KeyboardEx.KeyboardLayerType.KEYBOARD_PHONE;
    }

    public boolean supportsAlphaMode() {
        return (this.mKeyboardEntryLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_PHONE || this.mKeyboardEntryLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS || isNumMode() || isEditMode()) ? false : true;
    }

    public boolean currentLanguageSupportsCapitalization() {
        return this.mInputView.currentLanguageSupportsCapitalization();
    }

    public KeyboardEx.KeyboardLayerType currentKeyboardLayer() {
        return this.mKeyboardEntryLayer;
    }

    public void setKeyboardEntryLayerType(KeyboardEx.KeyboardLayerType type) {
        this.mKeyboardEntryLayer = type;
    }

    public String getCurrentInputMode() {
        return this.inputMode.mInputMode;
    }

    public void resetLayerState() {
        this.mKeyboardEntryLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID;
        this.defaultKeyboardLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT;
    }

    private void setKeyboard(KeyboardEx keyboard, KeyboardEx.KeyboardLayerType layer) {
        if (layer != currentKeyboardLayer()) {
            this.lastKeyboardLayer = this.mKeyboardEntryLayer;
        }
        this.mKeyboardEntryLayer = layer;
        this.mInputView.setKeyboard(keyboard);
        setShiftState(getCurrentShiftState());
        this.ime.refreshLanguageOnSpaceKey(keyboard, this.mInputView);
    }

    private int getKeyboardModeId(InputFieldInfo inputFieldInfo) {
        if (inputFieldInfo.isEmailAddressField()) {
            return 2;
        }
        if (inputFieldInfo.isShortMessageField()) {
            return 4;
        }
        if (inputFieldInfo.isNumericModeField()) {
            return 8;
        }
        return 1;
    }
}
