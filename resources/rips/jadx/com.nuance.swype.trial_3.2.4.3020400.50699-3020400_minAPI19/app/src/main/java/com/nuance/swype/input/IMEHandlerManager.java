package com.nuance.swype.input;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.Message;
import android.view.Window;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.chinese.ChineseIMEHandler;
import com.nuance.swype.input.japanese.JapaneseIMEHandler;
import com.nuance.swype.input.korean.KoreanIMEHandler;

/* loaded from: classes.dex */
public class IMEHandlerManager {
    private ChineseIMEHandler IMEHandlerChinese;
    private JapaneseIMEHandler IMEHandlerJapanese;
    private KoreanIMEHandler IMEHandlerKorean;
    private IME mIme;

    public IMEHandlerManager(IME ime) {
        this.mIme = ime;
    }

    public void refreshIME(IME ime) {
        this.mIme = ime;
    }

    public IMEHandler getIMEInstance() {
        if (this.mIme.mInputMethods == null) {
            this.mIme.mInputMethods = InputMethods.from(this.mIme);
        }
        if (this.mIme.mInputFieldInfo == null) {
            this.mIme.mInputFieldInfo = new InputFieldInfo(this.mIme);
        }
        if (this.mIme.mCurrentInputLanguage == null) {
            this.mIme.setCurrentInputLanguage();
        }
        this.mIme.mCurrentInputLanguage = this.mIme.mInputMethods.getCurrentInputLanguage();
        if (this.mIme.mCurrentInputLanguage.isChineseLanguage()) {
            if (this.IMEHandlerChinese == null) {
                this.IMEHandlerChinese = new ChineseIMEHandler(this.mIme);
            }
            return this.IMEHandlerChinese;
        }
        if (this.mIme.mCurrentInputLanguage.isJapaneseLanguage()) {
            if (this.IMEHandlerJapanese == null) {
                this.IMEHandlerJapanese = new JapaneseIMEHandler(this.mIme);
            }
            return this.IMEHandlerJapanese;
        }
        if (this.mIme.mCurrentInputLanguage.isKoreanLanguage()) {
            if (this.IMEHandlerKorean == null) {
                this.IMEHandlerKorean = new KoreanIMEHandler(this.mIme);
            }
            return this.IMEHandlerKorean;
        }
        return null;
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 103:
                if (getIMEInstance() != null) {
                    getIMEInstance().cyclingKeyboardInput();
                    return false;
                }
                return false;
            case 107:
                if (getIMEInstance() != null) {
                    getIMEInstance().toggleFullScreenHwr();
                    return false;
                }
                return false;
            default:
                return false;
        }
    }

    public void toggleKeyboard() {
        this.mIme.getHandler().removeMessages(103);
        this.mIme.getHandler().sendEmptyMessageDelayed(103, 5L);
    }

    public void toggleFullScreenHW() {
        this.mIme.getHandler().removeMessages(107);
        this.mIme.getHandler().sendEmptyMessageDelayed(107, 5L);
    }

    public static int getStatusBarHeight(IME ime) {
        int statusBarHeight = 0;
        Rect rectgle = new Rect();
        Dialog dg = ime.getWindow();
        if (dg == null) {
            return 0;
        }
        Window window = dg.getWindow();
        if (window != null) {
            window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
            statusBarHeight = rectgle.top;
        }
        if (rectgle.top == 0 && rectgle.bottom > 0) {
            statusBarHeight = ime.getResources().getDisplayMetrics().heightPixels - rectgle.bottom;
        }
        return statusBarHeight;
    }

    public int statusBarHeight() {
        return getStatusBarHeight(this.mIme);
    }

    public void fastSwitchLanguage(boolean isImplicit) {
        InputView inputView;
        InputMethods im = InputMethods.from(this.mIme);
        LangSwitchAction switchRequest = null;
        InputMethods.Language lang = im.getFastSwitchedOffLanguage();
        if (lang != null) {
            switchRequest = new LangSwitchAction(lang, isImplicit ? 6 : 4);
        } else if (this.mIme.mCurrentInputLanguage.isCJK()) {
            switchRequest = new LangSwitchAction(this.mIme.mCurrentInputLanguage, im.getDefaultAlphabeticInputLanguage(), isImplicit, isImplicit ? 6 : 4);
        }
        if (switchRequest != null && (inputView = this.mIme.getCurrentInputView()) != null) {
            inputView.finishInput();
            inputView.handleClose();
            this.mIme.switchLanguageAsync(switchRequest);
        }
    }

    public void toggleLanguageOrRestore(InputFieldInfo inputFieldInfo, InputView inputView) {
        boolean needSwitch = false;
        if (inputFieldInfo.isPasswordField() || inputFieldInfo.isEmailAddressField() || inputFieldInfo.isURLField()) {
            needSwitch = this.mIme.mCurrentInputLanguage.isCJK();
        } else if (inputFieldInfo.isInputTextClass()) {
            InputMethods im = InputMethods.from(this.mIme);
            needSwitch = im.getFastSwitchedOffLanguage() != null && im.isToggleTemporary();
        }
        if (needSwitch) {
            if (this.mIme != null) {
                this.mIme.getKeyboardBackgroundManager().setReloadRequiredFromResources(true);
            }
            fastSwitchLanguage(true);
        }
    }
}
