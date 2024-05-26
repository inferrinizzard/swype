package com.nuance.input.swypecorelib;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class T9WriteJapaneseSettings extends T9WriteCJKSetting {
    public static final int MAX_RESULT_CHARACTERS = 20;
    public int mBaseline;
    public int mHeight;
    public int mHelpline;
    public int mInputGuide;
    public int mRecognitionMode;
    public int mSupportLineSet;
    public int mTopline;
    public int mWidth;
    public final List<T9WriteJapaneseCategory> mCategories = new ArrayList();
    public int mRecognizeDelay = 750;

    T9WriteJapaneseSettings() {
        addJapaneseCategory();
    }

    public final void addJapaneseCategory() {
        clearCategory();
        addCategory(T9WriteJapaneseCategory.DECUMA_JIS);
        addCategory(T9WriteJapaneseCategory.DECUMA_HIRAGANA);
        addCategory(T9WriteJapaneseCategory.DECUMA_KATAKANA);
        addCategory(T9WriteJapaneseCategory.DECUMA_HIRAGANASMALL);
        addCategory(T9WriteJapaneseCategory.DECUMA_KATAKANASMALL);
        addCategory(T9WriteJapaneseCategory.DECUMA_JIS_LEVEL_1);
        addCategory(T9WriteJapaneseCategory.DECUMA_JIS_LEVEL_2);
        addCategory(T9WriteJapaneseCategory.DECUMA_JIS_LEVEL_3);
        addCategory(T9WriteJapaneseCategory.DECUMA_JIS_LEVEL_4);
        addCategory(T9WriteJapaneseCategory.DECUMA_GESTURES);
    }

    public void addMixSymbols() {
        clearCategory();
        addCategory(T9WriteJapaneseCategory.DECUMA_PUNCTUATION);
        addCategory(T9WriteJapaneseCategory.DECUMA_DIGIT);
        addCategory(T9WriteJapaneseCategory.DECUMA_GESTURES);
        addCategory(T9WriteJapaneseCategory.DECUMA_SYMBOL);
    }

    public void addCategory(T9WriteJapaneseCategory cat) {
        if (this.mCategories.indexOf(cat) == -1) {
            this.mCategories.add(cat);
        }
    }

    public void addDigitOnly() {
        clearCategory();
        addCategory(T9WriteJapaneseCategory.DECUMA_DIGIT);
        addCategory(T9WriteJapaneseCategory.DECUMA_GESTURES);
    }

    public void addAlphaCategory() {
        clearCategory();
        addCategory(T9WriteJapaneseCategory.DECUMA_ALPHA);
        addCategory(T9WriteJapaneseCategory.DECUMA_GESTURES);
    }

    public void removeCategory(T9WriteJapaneseCategory cat) {
        int location = this.mCategories.indexOf(cat);
        if (location != -1) {
            this.mCategories.remove(location);
        }
    }

    @Override // com.nuance.input.swypecorelib.T9WriteSetting
    public void clearCategory() {
        this.mCategories.clear();
    }
}
