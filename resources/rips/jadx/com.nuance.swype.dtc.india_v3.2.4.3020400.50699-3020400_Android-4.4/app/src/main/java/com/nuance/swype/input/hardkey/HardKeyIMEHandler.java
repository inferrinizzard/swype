package com.nuance.swype.input.hardkey;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.IMEHandler;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.chinese.ChineseInputView;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class HardKeyIMEHandler extends IMEHandler {
    protected static final LogManager.Log log = LogManager.getLog("HardKeyIMEHandler");
    private HardKeyboardManager mHardKeyboardManager;

    public HardKeyIMEHandler(IME ime) {
        this.mIme = ime;
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onDestroy() {
        this.mHardKeyboardManager = null;
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onInitializeInterface() {
    }

    @Override // com.nuance.swype.input.IMEHandler
    public View onCreateInputView() {
        if (this.mIme.mInputFieldInfo == null) {
            this.mIme.mInputFieldInfo = new InputFieldInfo(this.mIme);
            return null;
        }
        return null;
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onFinishInput() {
        if (this.mIme != null) {
            this.mIme.setImeInUse(false);
            this.mIme.setCandidatesViewShown(false);
        }
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void handleXT9LanguageCyclingKey() {
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onCreate() {
    }

    @Override // com.nuance.swype.input.IMEHandler
    public View onCreateCandidatesView() {
        return null;
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onFinishCandidatesView(boolean finishingInput) {
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onFinishInputView(boolean finishingInput) {
        if (this.mIme != null) {
            this.mIme.setImeInUse(false);
        }
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        if (this.mHardKeyboardManager == null) {
            this.mHardKeyboardManager = new HardKeyboardManager(this.mIme);
        }
        if (this.mIme != null && this.mIme.getCurrentInputView() != null && this.mIme.mCurrentInputLanguage != null) {
            this.mIme.getAppSpecificBehavior().onStartInputView(attribute, restarting);
            if (isValidInputField()) {
                if (this.mIme.mInputFieldInfo == null) {
                    this.mIme.mInputFieldInfo = new InputFieldInfo(this.mIme);
                }
                if (this.mIme.mInputMethods == null) {
                    this.mIme.mInputMethods = this.mIme.getInputMethods();
                }
                this.mIme.setImeInUse(true);
                this.mIme.onStartInputView(attribute, restarting);
                this.mIme.updateFullscreenMode();
                return;
            }
            this.mIme.setImeInUse(false);
        }
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void cyclingKeyboardInput() {
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void toggleFullScreenHwr() {
    }

    public void setIME(IME ime) {
        this.mIme = ime;
    }

    private void resendKeyEvent(int keyCode, KeyEvent event) {
        Handler handler;
        if (this.mIme.getCurrentInputView() != null && (handler = this.mIme.getHandler()) != null) {
            handler.removeMessages(117);
            handler.sendEmptyMessageDelayed(117, 50L);
            handler.removeMessages(116);
            handler.sendMessageDelayed(handler.obtainMessage(116, keyCode, 0, event), 300L);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mIme == null || this.mIme.mInputMethods == null || this.mIme.mInputFieldInfo == null || this.mIme.mCurrentInputLanguage == null || !isValidInputField()) {
            return false;
        }
        InputView inputview = this.mIme.getCurrentInputView();
        InputMethods mInputMethods = this.mIme.getInputMethods();
        if (inputview == null) {
            if (isLanguageSupportedByHardKeyboard()) {
                onStartInput(this.mIme.getCurrentInputEditorInfo(), true);
                if (((HardKeyIMEHandler) IMEApplication.from(this.mIme).getHardKeyIMEHandlerInstance()).isValidInputField()) {
                    resendKeyEvent(keyCode, event);
                    return true;
                }
            }
            return false;
        }
        if (!mInputMethods.isInputLanguageCurrent(this.mIme.mCurrentInputLanguage.getLanguageId()) && isLanguageSupportedByHardKeyboard()) {
            this.mIme.switchInputView(false);
            resendKeyEvent(keyCode, event);
            return true;
        }
        if (inputview.isMiniKeyboardMode()) {
            inputview.switchKeyboardDockMode(KeyboardEx.KeyboardDockMode.DOCK_FULL);
            this.mIme.setModeForInputContainerView();
            if (inputview instanceof ChineseInputView) {
                ((ChineseInputView) inputview).closeGridCandidatesView();
                ((ChineseInputView) inputview).handleHardKeyResize();
            }
        }
        if (inputview.isPopupKeyboardShowing()) {
            return inputview.miniKeyboardOnKeyDown(keyCode, event);
        }
        if (this.mIme.isLangPopupMenuShowing()) {
            if (keyCode == 20 || keyCode == 19 || keyCode == 66 || (!HardKeyboardManager.isAltPressed(event) && keyCode == 62)) {
                return true;
            }
            if (this.mHardKeyboardManager != null && !this.mHardKeyboardManager.mHardLangPopupMenuShownOnce) {
                this.mIme.dismissLangPopupMenu();
            }
        }
        if ((keyCode == 158 || (keyCode >= 144 && keyCode <= 153)) && !HardKeyboardManager.isNumLockOn(event)) {
            return false;
        }
        inputview.useKDBHardkey(true);
        return onHardKeyEvent(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        InputView inputview;
        if (this.mIme == null || this.mIme.mInputMethods == null || this.mIme.mInputFieldInfo == null || this.mIme.mCurrentInputLanguage == null || !isValidInputField() || (inputview = this.mIme.getCurrentInputView()) == null || !isLanguageSupportedByHardKeyboard() || !isInputModeSupportedByHardKeyboard()) {
            return false;
        }
        if (inputview.isPopupKeyboardShowing()) {
            if (keyCode == 21 || keyCode == 22 || keyCode == 19 || keyCode == 20 || keyCode == 66 || keyCode == 62) {
                return inputview.miniKeyboardOnKeyUp(keyCode, event);
            }
            return false;
        }
        if (this.mIme.isLangPopupMenuShowing()) {
            if (this.mHardKeyboardManager != null && this.mHardKeyboardManager.mHardLangPopupMenuShownOnce && keyCode == 62) {
                this.mHardKeyboardManager.mHardLangPopupMenuShownOnce = false;
                return true;
            }
            if (keyCode == 20 || keyCode == 19 || keyCode == 66 || !(this.mHardKeyboardManager == null || this.mHardKeyboardManager.mHardLangPopupMenuShownOnce || HardKeyboardManager.isAltPressed(event) || keyCode != 62)) {
                return this.mIme.onHardKeyLangPopupMenu(keyCode, event);
            }
            return false;
        }
        if ((keyCode != 59 && keyCode != 60) || !(inputview instanceof ChineseInputView)) {
            return false;
        }
        inputview.handleHardkeyShiftKey(Shift.ShiftState.OFF);
        return false;
    }

    private boolean onHardKeyEvent(int keyCode, KeyEvent event) {
        if (this.mIme.isInputViewShown() && event.getAction() == 0) {
            this.mIme.setHardKeyboardAttached(true);
        }
        if (this.mHardKeyboardManager == null) {
            this.mHardKeyboardManager = new HardKeyboardManager(this.mIme);
            this.mIme.getCurrentInputView().useKDBHardkey(false);
            return false;
        }
        if (!isValidInputField()) {
            return false;
        }
        boolean onKeyEvent = this.mHardKeyboardManager.onKeyEvent(keyCode, event);
        this.mIme.getCurrentInputView().useKDBHardkey(false);
        return onKeyEvent;
    }

    public boolean isLanguageSupportedByHardKeyboard() {
        if (this.mIme == null || this.mIme.mCurrentInputLanguage == null) {
            return false;
        }
        return this.mIme.mCurrentInputLanguage.isLatinLanguage() || this.mIme.mCurrentInputLanguage.isCJK();
    }

    public boolean isInputModeSupportedByHardKeyboard() {
        if (this.mIme == null || this.mIme.mCurrentInputLanguage == null) {
            return false;
        }
        String mode = this.mIme.mCurrentInputLanguage.getCurrentInputMode().mInputMode;
        return (mode.equalsIgnoreCase(InputMethods.CHINESE_INPUT_MODE_STROKE) || mode.equalsIgnoreCase(InputMethods.CHINESE_INPUT_MODE_HANDWRITING_FULL_SCREEN) || mode.equalsIgnoreCase(InputMethods.CHINESE_INPUT_MODE_HANDWRITING_HALF_SCREEN) || mode.equalsIgnoreCase(InputMethods.HANDWRITING_INPUT_MODE)) ? false : true;
    }

    public boolean isValidInputField() {
        EditorInfo editorInfo = this.mIme.getCurrentInputEditorInfo();
        if (this.mIme.getAppSpecificBehavior().shouldTreatEditTextAsInvalidField()) {
            return false;
        }
        return ((this.mIme.getAppSpecificBehavior().shouldSkipInvalidFieldIdEditor() && editorInfo.fieldId == -1) || editorInfo == null || editorInfo.inputType == 0) ? false : true;
    }
}
