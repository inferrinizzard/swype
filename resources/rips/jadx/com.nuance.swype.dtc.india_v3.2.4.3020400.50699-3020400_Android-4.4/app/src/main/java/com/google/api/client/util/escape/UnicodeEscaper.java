package com.google.api.client.util.escape;

/* loaded from: classes.dex */
public abstract class UnicodeEscaper extends Escaper {
    protected abstract char[] escape(int i);

    protected abstract int nextEscapeIndex(CharSequence charSequence, int i, int i2);

    /* JADX INFO: Access modifiers changed from: protected */
    public final String escapeSlow(String str, int i) {
        int length = str.length();
        char[] charBufferFromThreadLocal = Platform.charBufferFromThreadLocal();
        int i2 = 0;
        int i3 = 0;
        while (i < length) {
            if (i < length) {
                int i4 = i + 1;
                char charAt = str.charAt(i);
                int i5 = charAt;
                if (charAt >= 55296) {
                    i5 = charAt;
                    if (charAt <= 57343) {
                        if (charAt <= 56319) {
                            if (i4 == length) {
                                i5 = -charAt;
                            } else {
                                char charAt2 = str.charAt(i4);
                                if (Character.isLowSurrogate(charAt2)) {
                                    i5 = Character.toCodePoint(charAt, charAt2);
                                } else {
                                    throw new IllegalArgumentException("Expected low surrogate but got char '" + charAt2 + "' with value " + ((int) charAt2) + " at index " + i4);
                                }
                            }
                        } else {
                            throw new IllegalArgumentException("Unexpected low surrogate character '" + charAt + "' with value " + ((int) charAt) + " at index " + (i4 - 1));
                        }
                    }
                }
                if (i5 < 0) {
                    throw new IllegalArgumentException("Trailing high surrogate at end of input");
                }
                char[] escape = escape(i5);
                int i6 = i + (Character.isSupplementaryCodePoint(i5) ? 2 : 1);
                if (escape != null) {
                    int i7 = i - i3;
                    int length2 = i2 + i7 + escape.length;
                    if (charBufferFromThreadLocal.length < length2) {
                        charBufferFromThreadLocal = growBuffer(charBufferFromThreadLocal, i2, ((length2 + length) - i) + 32);
                    }
                    if (i7 > 0) {
                        str.getChars(i3, i, charBufferFromThreadLocal, i2);
                        i2 += i7;
                    }
                    if (escape.length > 0) {
                        System.arraycopy(escape, 0, charBufferFromThreadLocal, i2, escape.length);
                        i2 += escape.length;
                    }
                    i3 = i6;
                }
                i = nextEscapeIndex(str, i6, length);
            } else {
                throw new IndexOutOfBoundsException("Index exceeds specified range");
            }
        }
        int i8 = length - i3;
        if (i8 > 0) {
            int i9 = i2 + i8;
            if (charBufferFromThreadLocal.length < i9) {
                charBufferFromThreadLocal = growBuffer(charBufferFromThreadLocal, i2, i9);
            }
            str.getChars(i3, length, charBufferFromThreadLocal, i2);
            i2 = i9;
        }
        return new String(charBufferFromThreadLocal, 0, i2);
    }

    private static char[] growBuffer(char[] dest, int index, int size) {
        char[] copy = new char[size];
        if (index > 0) {
            System.arraycopy(dest, 0, copy, 0, index);
        }
        return copy;
    }
}
