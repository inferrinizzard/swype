package com.nuance.swype.input;

import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class LangSwitchAction {
    public static final int SELECTED_FROM_GESTURE = 5;
    public static final int SELECTED_FROM_IMPLICIT = 6;
    public static final int SELECTED_FROM_KEYBOARD = 2;
    public static final int SELECTED_FROM_LOCALE = 3;
    public static final int SELECTED_FROM_QUICK_TOGGLE = 4;
    public static final int SELECTED_FROM_SETTINGS = 1;
    public static final int SELECTED_FROM_UNSPECIFIED = 0;
    protected static final LogManager.Log log = LogManager.getLog("LangSwitchAction");
    private boolean isImplicitToggle;
    private InputMethods.Language lang;
    private int languageSwitchSource;
    private InputMethods.Language returnLang;

    public LangSwitchAction(InputMethods.Language from, InputMethods.Language next, boolean isImplicitToggle, int source) {
        this.languageSwitchSource = 0;
        this.returnLang = from;
        this.lang = next;
        this.isImplicitToggle = isImplicitToggle;
        this.languageSwitchSource = source;
    }

    public LangSwitchAction(InputMethods.Language next, int source) {
        this.languageSwitchSource = 0;
        this.lang = next;
        this.languageSwitchSource = source;
    }

    public String toString() {
        return this.returnLang != null ? this.returnLang.mEnglishName + " -> " + this.lang.mEnglishName + "; implicit: " + this.isImplicitToggle : this.lang.mEnglishName;
    }

    public void execute(IME ime) {
        Connect.from(ime).setCurrentLanguage(this.lang, this.languageSwitchSource);
        ime.mWantToast = true;
        InputMethods im = InputMethods.from(ime);
        if (this.returnLang != null) {
            im.setCurrentLanguage(this.lang.getLanguageId(), this.returnLang.getLanguageId(), this.isImplicitToggle);
        } else {
            im.setCurrentLanguage(this.lang.getLanguageId());
        }
        ime.switchInputView(false);
    }
}
