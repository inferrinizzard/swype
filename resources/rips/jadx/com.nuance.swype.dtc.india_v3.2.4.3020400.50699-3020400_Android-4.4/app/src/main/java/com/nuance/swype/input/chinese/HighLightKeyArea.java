package com.nuance.swype.input.chinese;

/* loaded from: classes.dex */
public enum HighLightKeyArea {
    NONE(0),
    VOWEL_LABLE(1),
    VOWEL_ALTLABLE(2),
    VOWEL_LEFTALTLABLE(4),
    CONSONANT_LABLE(8),
    CONSONANT_ALTLABLE(16),
    CONSONANT_LEFTALTLABLE(32);

    private final int value;

    HighLightKeyArea(int value) {
        this.value = value;
    }

    public final int getValue() {
        return this.value;
    }
}
