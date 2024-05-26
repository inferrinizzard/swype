package com.nuance.input.swypecorelib;

import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class T9WriteCategory {
    private static final int CATEGORY_DIGIT = 8;
    private static final int CATEGORY_EMAIL = 256;
    private static final int CATEGORY_GESTURE = 64;
    private static final int CATEGORY_LATIN = 4;
    private static final int CATEGORY_PHONE = 128;
    private static final int CATEGORY_PUNCTUATION = 16;
    private static final int CATEGORY_SYMBOL = 32;
    private static final int CATEGORY_TEXT = 2;
    private static final int CATEGORY_URL = 512;
    public static final int MULTITOUCH_HORIZONTAL_SWIPE_LEFT_UNICODE_VALUE = 8656;
    public static final int MULTITOUCH_HORIZONTAL_SWIPE_RIGHT_UNICODE_VALUE = 8658;
    public static final int MULTITOUCH_VERTICAL_SWIPE_DOWN_UNICODE_VALUE = 8659;
    public static final int MULTITOUCH_VERTICAL_SWIPE_UP_UNICODE_VALUE = 8657;
    private int categories;

    public int getCategory() {
        return this.categories;
    }

    public void clear() {
        remove(-1);
    }

    public void addText() {
        this.categories |= 2;
    }

    public void addGesture() {
        this.categories |= 64;
    }

    public void addLatinLetter() {
        remove(R.styleable.ThemeTemplate_chineseFeedbackStroke4);
        this.categories |= 4;
    }

    public void addOnyLatinLetter() {
        clear();
        this.categories |= 4;
    }

    public void addNumber() {
        this.categories |= 8;
    }

    public void addOnlyNumber() {
        clear();
        this.categories |= 8;
    }

    public void addOnlyPhoneNumber() {
        clear();
        this.categories |= 128;
    }

    public void addSymbol() {
        remove(R.styleable.ThemeTemplate_chineseFeedbackStroke4);
        this.categories |= 32;
    }

    public void addPunctuation() {
        remove(R.styleable.ThemeTemplate_chineseFeedbackStroke4);
        this.categories |= 16;
    }

    public void addOnlySymbol() {
        clear();
        this.categories |= 32;
    }

    public void addOnlyEmail() {
        remove(-1);
        this.categories |= 256;
    }

    public void addOnlyUrl() {
        clear();
        this.categories |= 512;
    }

    private void remove(int cat) {
        this.categories &= cat ^ (-1);
    }

    public static boolean isMultiTouchGesturesCharacter(int value) {
        return value == 8656 || value == 8658 || value == 8657 || value == 8659;
    }

    public static boolean isInstantGestureCharacter(char ch) {
        return ch == '\b' || Character.isWhitespace(ch);
    }
}
