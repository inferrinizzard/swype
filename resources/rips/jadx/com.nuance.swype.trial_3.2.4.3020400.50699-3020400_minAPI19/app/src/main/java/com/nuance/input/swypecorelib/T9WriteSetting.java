package com.nuance.input.swypecorelib;

/* loaded from: classes.dex */
public abstract class T9WriteSetting {
    public static final int ANCHORED_SUPPORT_LINES = 3;
    public static final int BASE_AND_HELP_LINE = 0;
    public static final int BASE_AND_TOP_LINE = 1;
    public static final int BOX = 2;
    public static final int LEFT_TO_RIGHT = 0;
    public static final int MCR_MODE = 1;
    public static final int NONE = 0;
    public static final int ON_TOP_WRITING = 2;
    public static final int RIGHT_TO_LEFT = 1;
    public static final int SCR_MODE = 0;
    public static final int SUPPORT_LINES = 1;
    public static final int UCR_MODE = 2;
    public static final int UNKNOWN_WRITING = 3;
    private int mHeight;
    public int mHwrTemplateDatabaseID;
    private int mInputGuide;
    private int mJniCategoryMask;
    public int mLanguageDatabaseID;
    private int mRecognitionMode;
    private int mWidth;
    private int mWritingDirection;
    T9WriteCategory mCategory = new T9WriteCategory();
    private int mSupportLineSet = 0;
    private int mHelpline = 0;
    private int mBaseline = 0;
    private int mTopline = 0;
    public int mRecognizeDelay = 500;

    /* JADX INFO: Access modifiers changed from: protected */
    public T9WriteSetting() {
        setWritingDirection(0);
        setWidth(0);
        setHeight(0);
    }

    public void setRecognizerDelay(int delayMS) {
        this.mRecognizeDelay = delayMS;
    }

    public int getRecognizerDelay() {
        return this.mRecognizeDelay;
    }

    public final void setWidth(int w) {
        this.mWidth = w;
    }

    public final void setHeight(int h) {
        this.mHeight = h;
    }

    public final void setInputGuide(int inputGuide) {
        this.mInputGuide = inputGuide;
    }

    public final void setSupportLineSet(int supportLineSet) {
        this.mSupportLineSet = supportLineSet;
    }

    public final void setBaseline(int b) {
        this.mBaseline = b;
    }

    public final void setHelpline(int h) {
        this.mHelpline = h;
    }

    public final void setTopline(int t) {
        this.mTopline = t;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getInputGuide() {
        return this.mInputGuide;
    }

    public int getSupportLineSet() {
        return this.mSupportLineSet;
    }

    public int getBaseline() {
        return this.mBaseline;
    }

    public int getHelpline() {
        return this.mHelpline;
    }

    public int getTopline() {
        return this.mTopline;
    }

    public int getRecognitionMode() {
        return this.mRecognitionMode;
    }

    public void setRecognitionMode(int mode) {
        this.mRecognitionMode = mode;
    }

    public void setWritingDirection(int direction) {
        this.mWritingDirection = direction;
    }

    public void addNumberCategory() {
        this.mCategory.addNumber();
        applyJniCategoryChange();
    }

    public void addSymbolCategory() {
        this.mCategory.addSymbol();
        applyJniCategoryChange();
    }

    public void addOnlySymbolCategory() {
        this.mCategory.clear();
        this.mCategory.addSymbol();
        applyJniCategoryChange();
    }

    public void addNumberOnlyCategory() {
        this.mCategory.addOnlyNumber();
        applyJniCategoryChange();
    }

    public void addPunctuationCategory() {
        this.mCategory.addPunctuation();
        applyJniCategoryChange();
    }

    public void addDigitsAndSymbolsOnlyCategory() {
        this.mCategory.clear();
        this.mCategory.addSymbol();
        this.mCategory.addNumber();
        applyJniCategoryChange();
    }

    public void addPhoneNumberOnlyCategory() {
        this.mCategory.addOnlyPhoneNumber();
        applyJniCategoryChange();
    }

    public void addEmailOnlyCategory() {
        this.mCategory.clear();
        this.mCategory.addOnlyEmail();
        applyJniCategoryChange();
    }

    public void addUrlOnlyCategory() {
        this.mCategory.clear();
        this.mCategory.addOnlyUrl();
        applyJniCategoryChange();
    }

    public void addOnlyTextCategory() {
        this.mCategory.clear();
        addTextCategory();
    }

    public void addTextCategory() {
        this.mCategory.addText();
        applyJniCategoryChange();
    }

    public void addTextOnlyCategory() {
        this.mCategory.clear();
        this.mCategory.addText();
        applyJniCategoryChange();
    }

    public void addLatinLetterCategory() {
        this.mCategory.addLatinLetter();
        applyJniCategoryChange();
    }

    public void addOnlyLatinLetterCategory() {
        this.mCategory.addOnyLatinLetter();
        applyJniCategoryChange();
    }

    public void addGestureCategory() {
        this.mCategory.addGesture();
        applyJniCategoryChange();
    }

    public void clearCategory() {
        this.mCategory.clear();
    }

    private void applyJniCategoryChange() {
        this.mJniCategoryMask = this.mCategory.getCategory();
    }
}
