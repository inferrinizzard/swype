package com.nuance.swype.input.chinese;

import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardSwitcher;
import com.nuance.swype.input.XT9Keyboard;

/* loaded from: classes.dex */
public class ChineseKeyboardSwitcher extends KeyboardSwitcher {
    public ChineseKeyboardSwitcher(IME ime, XT9CoreInput xt9coreInput) {
        super(ime, xt9coreInput);
    }

    public boolean isMixedInput() {
        String mode = this.ime.mCurrentInputLanguage.getCurrentInputMode().mInputMode;
        return InputMethods.CHINESE_INPUT_MODE_PINYIN_QWERTY.equals(mode) || InputMethods.CHINESE_INPUT_MODE_ZHUYIN_QWERTY.equals(mode) || InputMethods.CHINESE_INPUT_MODE_DOUBLEPINYIN.equals(mode);
    }

    @Override // com.nuance.swype.input.KeyboardSwitcher
    public XT9Keyboard createKeyboardForTextInput(KeyboardEx.KeyboardLayerType keyboardLayer, InputFieldInfo inputFieldInfo, InputMethods.InputMode inputMode) {
        XT9Keyboard keyboard = super.createKeyboardForTextInput(keyboardLayer, inputFieldInfo, inputMode);
        if ((this.mInputView instanceof ChineseHandWritingInputView) && isAlphabetMode()) {
            ((ChineseHandWritingInputView) this.mInputView).showHWFrameAndCharacterList();
            this.ime.refreshLanguageOnSpaceKey(keyboard, this.mInputView);
        }
        if (isKeypadInput() || this.mInputView.isModeStroke()) {
            this.mInputView.setPressDownPreviewEnabled(false);
        }
        return keyboard;
    }
}
