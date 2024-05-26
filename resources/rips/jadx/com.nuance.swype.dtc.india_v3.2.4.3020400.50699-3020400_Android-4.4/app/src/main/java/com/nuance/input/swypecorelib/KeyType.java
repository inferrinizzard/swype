package com.nuance.input.swypecorelib;

import android.util.SparseArray;

/* loaded from: classes.dex */
public enum KeyType {
    INVALID(0),
    LETTER(1),
    PUNCTUATION(2),
    NUMBER(3),
    STRING(4),
    FUNCTION(5),
    SMARTPUNCT(6),
    UNKNOWN(7);

    private static final SparseArray<KeyType> keyTypeMap;
    private final int value;

    static {
        KeyType[] values = values();
        keyTypeMap = new SparseArray<>(values.length);
        for (KeyType keyType : values) {
            keyTypeMap.append(keyType.value, keyType);
        }
    }

    public static KeyType from(int value) {
        return keyTypeMap.get(value);
    }

    KeyType(int value) {
        this.value = value;
    }

    public final int value() {
        return this.value;
    }

    public final boolean isFunctionKey() {
        return this == FUNCTION;
    }

    public final boolean isNumber() {
        return this == NUMBER;
    }

    public final boolean isLetter() {
        return this == LETTER;
    }

    public final boolean isString() {
        return this == STRING;
    }

    public final boolean isPunctuation() {
        return this == PUNCTUATION;
    }

    public final boolean isSmartPunctuation() {
        return this == SMARTPUNCT;
    }
}
