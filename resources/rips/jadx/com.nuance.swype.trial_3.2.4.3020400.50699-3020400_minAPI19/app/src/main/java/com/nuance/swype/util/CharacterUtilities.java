package com.nuance.swype.util;

import android.content.Context;
import android.text.TextUtils;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class CharacterUtilities {
    private static final int[] thaiCombiningMarks = {3633, 3636, 3637, 3638, 3639, 3640, 3641, 3642, 3655, 3656, 3657, 3658, 3659, 3660, 3661, 3662};
    private final String compounders;
    private final char[] diacriticMark;
    private final char[] embeddedPunct;
    private final String joiners;
    public final char[] leftPunct;
    public int maxSmileyLength;
    private final String punctuation;
    private final String separators;
    private final String[] smileyDefinition;
    private final String terminals;
    public final String unicase;

    public static CharacterUtilities from(Context context) {
        return IMEApplication.from(context).getCharacterUtilities();
    }

    public CharacterUtilities(Context context) {
        this.maxSmileyLength = 0;
        this.separators = context.getString(R.string.word_separators);
        this.terminals = context.getString(R.string.terminal_punctuations);
        this.punctuation = context.getString(R.string.punctuation_and_symbols);
        this.compounders = context.getString(R.string.word_compounders);
        this.joiners = context.getString(R.string.string_joiners);
        this.unicase = context.getString(R.string.unicase_letters);
        this.diacriticMark = context.getString(R.string.diacritic_mark).toCharArray();
        this.leftPunct = context.getString(R.string.left_punctuation).toCharArray();
        this.embeddedPunct = context.getString(R.string.embedded_punct).toCharArray();
        this.smileyDefinition = context.getResources().getStringArray(R.array.smiley_definition);
        for (String str : this.smileyDefinition) {
            int len = str.length();
            if (len > this.maxSmileyLength) {
                this.maxSmileyLength = len;
            }
        }
    }

    public final boolean isWordSeparator(int code) {
        return this.separators.indexOf(code) != -1;
    }

    public final boolean isTerminalPunctuation(int code) {
        return this.terminals.indexOf(code) != -1;
    }

    public final boolean isPunctuationOrSymbol(int code) {
        return this.punctuation.indexOf(code) != -1 || isEmbeddedPunct((char) code);
    }

    public final boolean isWordCompounder(int code) {
        return this.compounders.indexOf(code) != -1;
    }

    public final boolean isDiacriticMark(char c) {
        return Arrays.binarySearch(this.diacriticMark, c) >= 0;
    }

    public final boolean isEmbeddedPunct(char c) {
        return Arrays.binarySearch(this.embeddedPunct, c) >= 0;
    }

    public static boolean isWhiteSpace(char c) {
        return Character.isWhitespace(c) || c == '\n' || c == 160;
    }

    public static boolean isControl(char c) {
        return (c < ' ' && !isWhiteSpace(c)) || c == 127;
    }

    public static boolean isUpperCaseLetter(char c) {
        return c <= 'Z' && c >= 'A';
    }

    public final boolean isWordAcceptingSymbol(int code) {
        return isWhiteSpace((char) code) || isTerminalPunctuation(code);
    }

    public final boolean isWordBoundary(CharSequence cs) {
        return TextUtils.isEmpty(cs) || isWordBoundary(cs.charAt(0));
    }

    public final boolean isWordBoundary(char c) {
        return isWhiteSpace(c) || isWordSeparator(c) || (isWordCompounder(c) && c != '\'');
    }

    public static boolean isDigit(int code) {
        return 48 <= code && code <= 57;
    }

    public static boolean isValidChineseChar(int iChar) {
        return (iChar >= 12288 && iChar <= 40869) || (iChar >= 57344 && iChar <= 61105) || ((iChar >= 62368 && iChar <= 63469) || ((iChar >= 61264 && iChar <= 65131) || ((iChar >= 65281 && iChar <= 65374) || (iChar >= 65504 && iChar <= 65509))));
    }

    public static boolean isValidCJChar(int iChar) {
        return (iChar >= 12288 && iChar < 12592) || (iChar > 12687 && iChar <= 40869) || ((iChar >= 57344 && iChar <= 61105) || ((iChar >= 62368 && iChar <= 63469) || ((iChar >= 61264 && iChar <= 65131) || ((iChar >= 65281 && iChar <= 65374) || ((iChar >= 11904 && iChar <= 12031) || ((iChar >= 19968 && iChar <= 40959) || ((iChar >= 63744 && iChar <= 64255) || ((iChar >= 65504 && iChar <= 65103) || ((iChar >= 65072 && iChar <= 65509) || ((iChar >= 131072 && iChar <= 173791) || (iChar >= 194560 && iChar <= 195103)))))))))));
    }

    public static boolean isValidAlphabeticChar(int iChar) {
        return !isValidChineseChar(iChar);
    }

    public static boolean endsWithSurrogatePair(CharSequence text) {
        int index;
        if (text == null || text.length() - 2 < 0) {
            return false;
        }
        if (text == null) {
            throw new NullPointerException("text == null");
        }
        int length = text.length();
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException();
        }
        int i = index + 1;
        char charAt = text.charAt(index);
        if (i == length) {
            return false;
        }
        return Character.isSurrogatePair(charAt, text.charAt(i));
    }

    public static boolean isThaiCombiningMark(CharSequence label) {
        if (label == null || label.length() != 1) {
            return false;
        }
        int codePoint = Character.codePointAt(label, 0);
        return Arrays.binarySearch(thaiCombiningMarks, codePoint) >= 0;
    }

    public final boolean isSmiley(String text) {
        int len = text.length();
        if (len < 2) {
            return false;
        }
        for (String str : this.smileyDefinition) {
            int idx = text.lastIndexOf(str);
            if (idx != -1 && str.length() + idx == len) {
                return true;
            }
        }
        return false;
    }
}
