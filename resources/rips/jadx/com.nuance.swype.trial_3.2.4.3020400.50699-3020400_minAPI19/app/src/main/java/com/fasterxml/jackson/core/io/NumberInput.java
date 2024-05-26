package com.fasterxml.jackson.core.io;

/* loaded from: classes.dex */
public final class NumberInput {
    static final String MIN_LONG_STR_NO_SIGN = "-9223372036854775808".substring(1);
    static final String MAX_LONG_STR = "9223372036854775807";

    public static int parseInt(char[] digitChars, int offset, int len) {
        int num = digitChars[offset] - '0';
        int len2 = len + offset;
        int offset2 = offset + 1;
        if (offset2 < len2) {
            int num2 = (num * 10) + (digitChars[offset2] - '0');
            int offset3 = offset2 + 1;
            if (offset3 < len2) {
                int num3 = (num2 * 10) + (digitChars[offset3] - '0');
                int offset4 = offset3 + 1;
                if (offset4 < len2) {
                    int num4 = (num3 * 10) + (digitChars[offset4] - '0');
                    int offset5 = offset4 + 1;
                    if (offset5 < len2) {
                        int num5 = (num4 * 10) + (digitChars[offset5] - '0');
                        int offset6 = offset5 + 1;
                        if (offset6 < len2) {
                            int num6 = (num5 * 10) + (digitChars[offset6] - '0');
                            int offset7 = offset6 + 1;
                            if (offset7 < len2) {
                                int num7 = (num6 * 10) + (digitChars[offset7] - '0');
                                int offset8 = offset7 + 1;
                                if (offset8 < len2) {
                                    int num8 = (num7 * 10) + (digitChars[offset8] - '0');
                                    if (offset8 + 1 < len2) {
                                        return (num8 * 10) + (digitChars[r4] - '0');
                                    }
                                    return num8;
                                }
                                return num7;
                            }
                            return num6;
                        }
                        return num5;
                    }
                    return num4;
                }
                return num3;
            }
            return num2;
        }
        return num;
    }

    public static long parseLong(char[] digitChars, int offset, int len) {
        int len1 = len - 9;
        return (parseInt(digitChars, offset, len1) * 1000000000) + parseInt(digitChars, offset + len1, 9);
    }

    public static boolean inLongRange(char[] digitChars, int offset, int len, boolean negative) {
        String cmpStr = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
        int cmpLen = cmpStr.length();
        if (len < cmpLen) {
            return true;
        }
        if (len > cmpLen) {
            return false;
        }
        for (int i = 0; i < cmpLen; i++) {
            int diff = digitChars[offset + i] - cmpStr.charAt(i);
            if (diff != 0) {
                return diff < 0;
            }
        }
        return true;
    }

    public static double parseDouble(String numStr) throws NumberFormatException {
        if ("2.2250738585072012e-308".equals(numStr)) {
            return Double.MIN_VALUE;
        }
        return Double.parseDouble(numStr);
    }
}
