package com.nuance.swype.input.japanese;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import com.nuance.input.swypecorelib.XT9CoreJapaneseInput;
import com.nuance.input.swypecorelib.XT9Status;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.IMEHandler;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardInputInflater;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class JapaneseIMEHandler extends IMEHandler {
    protected static final LogManager.Log log = LogManager.getLog("JapaneseIMEHandler");

    public JapaneseIMEHandler(IME ime) {
        this.mIme = ime;
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onDestroy() {
        this.mIme.removeAllPendingMsgs();
        destroyAllInputs(true);
        try {
            this.mIme.unregisterReceiver(this.mIme.mReceiver);
        } catch (IllegalArgumentException ex) {
            log.e(ex.getMessage());
        }
        this.mIme.mInputMethods = null;
    }

    @Override // com.nuance.swype.input.IMEHandler
    public View onCreateInputView() {
        if (this.mIme.mInputFieldInfo == null) {
            this.mIme.mInputFieldInfo = new InputFieldInfo(this.mIme);
        }
        destroyAllInputs(false);
        return null;
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onFinishInput() {
        if (this.mIme != null && this.mIme.keyboardInputInflater != null && !this.mIme.keyboardInputInflater.isEmpty()) {
            this.mIme.getHandler().removeMessages(100);
            InputView inputView = this.mIme.getCurrentInputView();
            if (inputView != null) {
                inputView.finishInput();
                inputView.setOnKeyboardActionListener((IME) null);
            }
        }
    }

    public void destroyAllInputs(boolean destroyCore) {
        finishAllInputView();
        this.mIme.keyboardInputInflater.callAllInputViewToDestroy(destroyCore);
        this.mIme.resetInputView(destroyCore);
        this.mIme.mCurrentInputViewName = KeyboardInputInflater.NO_INPUTVIEW;
    }

    public void finishAllInputView() {
        this.mIme.keyboardInputInflater.callAllInputViewToFinish();
    }

    @Override // com.nuance.swype.input.IMEHandler
    @SuppressLint({"PrivateResource"})
    public void cyclingKeyboardInput() {
        InputMethods.InputMode currentInputMode = this.mIme.mCurrentInputLanguage.getCurrentInputMode();
        InputMethods.Layout currentLayout = currentInputMode.getCurrentLayout();
        InputMethods.Layout nextLayout = currentInputMode.getNextLayout();
        if (nextLayout.mLayoutId == this.mIme.getResources().getInteger(R.integer.symbols_keyboard_id)) {
            nextLayout = currentInputMode.getNextLayout();
        }
        if (!currentLayout.equals(nextLayout)) {
            this.mIme.keyboardInputInflater.getInputView(this.mIme.mCurrentInputViewName).flushCurrentActiveWord();
            this.mIme.switchInputViewAsync();
        }
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void toggleFullScreenHwr() {
        InputView inputView = this.mIme.getCurrentInputView();
        if (inputView != null && (inputView instanceof JapaneseHandWritingInputView)) {
            JapaneseHandWritingInputView hwrInputView = (JapaneseHandWritingInputView) inputView;
            this.mIme.updateInputViewShown();
            hwrInputView.showHandWritingView(true);
        }
    }

    @Override // com.nuance.swype.input.IMEHandler
    public XT9Status checkCompatability() {
        log.d("Japanese checkCompatability()");
        XT9Status status = XT9Status.ET9STATUS_NONE;
        InputMethods im = this.mIme.getInputMethods();
        if (im != null && im.getCurrentInputLanguage().isJapaneseLanguage()) {
            XT9CoreJapaneseInput japaneseInput = IMEApplication.from(this.mIme).getSwypeCoreLibMgr().getXT9CoreJapaneseInputSession();
            japaneseInput.startSession();
            return im.getCurrentInputLanguage().setLanguage(japaneseInput);
        }
        return status;
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
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onStartInput(EditorInfo attribute, boolean restarting) {
    }
}
