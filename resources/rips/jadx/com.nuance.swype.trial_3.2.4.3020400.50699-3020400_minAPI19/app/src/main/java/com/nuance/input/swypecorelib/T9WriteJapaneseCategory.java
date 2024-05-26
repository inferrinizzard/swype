package com.nuance.input.swypecorelib;

/* loaded from: classes.dex */
public class T9WriteJapaneseCategory {
    private static final int DECUMA_CATEGORY_DIGIT = 1101;
    private static final int DECUMA_CATEGORY_GESTURES = 105;
    private final int category;
    private static final int DECUMA_CATEGORY_ANSI = 1002;
    public static final T9WriteJapaneseCategory DECUMA_ALPHA = new T9WriteJapaneseCategory(DECUMA_CATEGORY_ANSI);
    public static final T9WriteJapaneseCategory DECUMA_DIGIT = new T9WriteJapaneseCategory(1101);
    private static final int DECUMA_CATEGORY_PUNCTUATIONS = 1250;
    public static final T9WriteJapaneseCategory DECUMA_PUNCTUATION = new T9WriteJapaneseCategory(DECUMA_CATEGORY_PUNCTUATIONS);
    private static final int DECUMA_CATEGORY_CJK_SYMBOL = 6000;
    public static final T9WriteJapaneseCategory DECUMA_SYMBOL = new T9WriteJapaneseCategory(DECUMA_CATEGORY_CJK_SYMBOL);
    public static final T9WriteJapaneseCategory DECUMA_GESTURES = new T9WriteJapaneseCategory(105);
    private static final int DECUMA_CATEGORY_JIS_LEVEL_1 = 6006;
    public static final T9WriteJapaneseCategory DECUMA_JIS_LEVEL_1 = new T9WriteJapaneseCategory(DECUMA_CATEGORY_JIS_LEVEL_1);
    private static final int DECUMA_CATEGORY_JIS_LEVEL_2 = 6007;
    public static final T9WriteJapaneseCategory DECUMA_JIS_LEVEL_2 = new T9WriteJapaneseCategory(DECUMA_CATEGORY_JIS_LEVEL_2);
    private static final int DECUMA_CATEGORY_JIS_LEVEL_3 = 6008;
    public static final T9WriteJapaneseCategory DECUMA_JIS_LEVEL_3 = new T9WriteJapaneseCategory(DECUMA_CATEGORY_JIS_LEVEL_3);
    private static final int DECUMA_CATEGORY_JIS_LEVEL_4 = 6009;
    public static final T9WriteJapaneseCategory DECUMA_JIS_LEVEL_4 = new T9WriteJapaneseCategory(DECUMA_CATEGORY_JIS_LEVEL_4);
    private static final int DECUMA_CATEGORY_HIRAGANA = 6010;
    public static final T9WriteJapaneseCategory DECUMA_HIRAGANA = new T9WriteJapaneseCategory(DECUMA_CATEGORY_HIRAGANA);
    private static final int DECUMA_CATEGORY_KATAKANA = 6011;
    public static final T9WriteJapaneseCategory DECUMA_KATAKANA = new T9WriteJapaneseCategory(DECUMA_CATEGORY_KATAKANA);
    private static final int DECUMA_CATEGORY_HIRAGANASMALL = 6012;
    public static final T9WriteJapaneseCategory DECUMA_HIRAGANASMALL = new T9WriteJapaneseCategory(DECUMA_CATEGORY_HIRAGANASMALL);
    private static final int DECUMA_CATEGORY_KATAKANASMALL = 6013;
    public static final T9WriteJapaneseCategory DECUMA_KATAKANASMALL = new T9WriteJapaneseCategory(DECUMA_CATEGORY_KATAKANASMALL);
    private static final int DECUMA_CATEGORY_JIS = 6042;
    public static final T9WriteJapaneseCategory DECUMA_JIS = new T9WriteJapaneseCategory(DECUMA_CATEGORY_JIS);

    private T9WriteJapaneseCategory(int category) {
        this.category = category;
    }

    public int get() {
        return this.category;
    }

    public boolean equals(Object o) {
        return (o instanceof T9WriteJapaneseCategory) && get() == ((T9WriteJapaneseCategory) o).get();
    }

    public int hashCode() {
        return this.category;
    }
}
