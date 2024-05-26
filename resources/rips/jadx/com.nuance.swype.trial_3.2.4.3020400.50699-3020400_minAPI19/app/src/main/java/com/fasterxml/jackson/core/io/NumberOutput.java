package com.fasterxml.jackson.core.io;

import com.nuance.connect.comm.MessageAPI;
import com.nuance.nmsp.client.sdk.components.core.internal.calllog.CalllogImpl;

/* loaded from: classes.dex */
public final class NumberOutput {
    static final byte[] FULL_TRIPLETS_B;
    static final String[] sSmallIntStrs;
    static final String[] sSmallIntStrs2;
    private static int MILLION = CalllogImpl.CALLLOG_CHUNK_SIZE_DEFAULT;
    private static int BILLION = 1000000000;
    private static long TEN_BILLION_L = 10000000000L;
    private static long THOUSAND_L = 1000;
    private static long MIN_INT_AS_LONG = -2147483648L;
    private static long MAX_INT_AS_LONG = 2147483647L;
    static final String SMALLEST_LONG = "-9223372036854775808";
    static final char[] LEADING_TRIPLETS = new char[4000];
    static final char[] FULL_TRIPLETS = new char[4000];

    static {
        int ix = 0;
        int i1 = 0;
        while (i1 < 10) {
            char f1 = (char) (i1 + 48);
            char l1 = i1 == 0 ? (char) 0 : f1;
            int i2 = 0;
            while (i2 < 10) {
                char f2 = (char) (i2 + 48);
                char l2 = (i1 == 0 && i2 == 0) ? (char) 0 : f2;
                for (int i3 = 0; i3 < 10; i3++) {
                    char f3 = (char) (i3 + 48);
                    LEADING_TRIPLETS[ix] = l1;
                    LEADING_TRIPLETS[ix + 1] = l2;
                    LEADING_TRIPLETS[ix + 2] = f3;
                    FULL_TRIPLETS[ix] = f1;
                    FULL_TRIPLETS[ix + 1] = f2;
                    FULL_TRIPLETS[ix + 2] = f3;
                    ix += 4;
                }
                i2++;
            }
            i1++;
        }
        FULL_TRIPLETS_B = new byte[4000];
        for (int i = 0; i < 4000; i++) {
            FULL_TRIPLETS_B[i] = (byte) FULL_TRIPLETS[i];
        }
        sSmallIntStrs = new String[]{"0", "1", MessageAPI.DELAYED_FROM, MessageAPI.SESSION_ID, MessageAPI.TRANSACTION_ID, MessageAPI.DEVICE_ID, "6", MessageAPI.MESSAGE, "8", MessageAPI.PROPERTIES_TO_VALIDATE, MessageAPI.CHECKSUM};
        sSmallIntStrs2 = new String[]{"-1", "-2", "-3", "-4", "-5", "-6", "-7", "-8", "-9", "-10"};
    }

    public static int outputInt(int value, char[] buffer, int offset) {
        int offset2;
        int offset3;
        if (value < 0) {
            if (value == Integer.MIN_VALUE) {
                return outputLong(value, buffer, offset);
            }
            buffer[offset] = '-';
            value = -value;
            offset++;
        }
        if (value < MILLION) {
            if (value < 1000) {
                if (value < 10) {
                    buffer[offset] = (char) (value + 48);
                    offset3 = offset + 1;
                } else {
                    offset3 = outputLeadingTriplet(value, buffer, offset);
                }
            } else {
                int thousands = value / 1000;
                offset3 = outputFullTriplet(value - (thousands * 1000), buffer, outputLeadingTriplet(thousands, buffer, offset));
            }
            return offset3;
        }
        boolean hasBillions = value >= BILLION;
        if (hasBillions) {
            value -= BILLION;
            if (value >= BILLION) {
                value -= BILLION;
                buffer[offset] = '2';
                offset++;
            } else {
                buffer[offset] = '1';
                offset++;
            }
        }
        int newValue = value / 1000;
        int ones = value - (newValue * 1000);
        int newValue2 = newValue / 1000;
        int thousands2 = newValue - (newValue2 * 1000);
        if (hasBillions) {
            offset2 = outputFullTriplet(newValue2, buffer, offset);
        } else {
            offset2 = outputLeadingTriplet(newValue2, buffer, offset);
        }
        return outputFullTriplet(ones, buffer, outputFullTriplet(thousands2, buffer, offset2));
    }

    public static int outputInt(int value, byte[] buffer, int offset) {
        int offset2;
        int offset3;
        if (value < 0) {
            if (value == Integer.MIN_VALUE) {
                return outputLong(value, buffer, offset);
            }
            buffer[offset] = 45;
            value = -value;
            offset++;
        }
        if (value < MILLION) {
            if (value < 1000) {
                if (value < 10) {
                    buffer[offset] = (byte) (value + 48);
                    offset3 = offset + 1;
                } else {
                    offset3 = outputLeadingTriplet(value, buffer, offset);
                }
            } else {
                int thousands = value / 1000;
                offset3 = outputFullTriplet(value - (thousands * 1000), buffer, outputLeadingTriplet(thousands, buffer, offset));
            }
            return offset3;
        }
        boolean hasBillions = value >= BILLION;
        if (hasBillions) {
            value -= BILLION;
            if (value >= BILLION) {
                value -= BILLION;
                buffer[offset] = 50;
                offset++;
            } else {
                buffer[offset] = 49;
                offset++;
            }
        }
        int newValue = value / 1000;
        int ones = value - (newValue * 1000);
        int newValue2 = newValue / 1000;
        int thousands2 = newValue - (newValue2 * 1000);
        if (hasBillions) {
            offset2 = outputFullTriplet(newValue2, buffer, offset);
        } else {
            offset2 = outputLeadingTriplet(newValue2, buffer, offset);
        }
        return outputFullTriplet(ones, buffer, outputFullTriplet(thousands2, buffer, offset2));
    }

    public static int outputLong(long value, char[] buffer, int offset) {
        if (value < 0) {
            if (value > MIN_INT_AS_LONG) {
                return outputInt((int) value, buffer, offset);
            }
            if (value == Long.MIN_VALUE) {
                int len = SMALLEST_LONG.length();
                SMALLEST_LONG.getChars(0, len, buffer, offset);
                return offset + len;
            }
            buffer[offset] = '-';
            value = -value;
            offset++;
        } else if (value <= MAX_INT_AS_LONG) {
            return outputInt((int) value, buffer, offset);
        }
        int origOffset = offset;
        int offset2 = offset + calcLongStrLength(value);
        int ptr = offset2;
        while (value > MAX_INT_AS_LONG) {
            ptr -= 3;
            long newValue = value / THOUSAND_L;
            outputFullTriplet((int) (value - (THOUSAND_L * newValue)), buffer, ptr);
            value = newValue;
        }
        int ivalue = (int) value;
        while (ivalue >= 1000) {
            ptr -= 3;
            int newValue2 = ivalue / 1000;
            outputFullTriplet(ivalue - (newValue2 * 1000), buffer, ptr);
            ivalue = newValue2;
        }
        outputLeadingTriplet(ivalue, buffer, origOffset);
        return offset2;
    }

    public static int outputLong(long value, byte[] buffer, int offset) {
        if (value < 0) {
            if (value > MIN_INT_AS_LONG) {
                return outputInt((int) value, buffer, offset);
            }
            if (value == Long.MIN_VALUE) {
                int len = SMALLEST_LONG.length();
                int i = 0;
                int offset2 = offset;
                while (i < len) {
                    buffer[offset2] = (byte) SMALLEST_LONG.charAt(i);
                    i++;
                    offset2++;
                }
                return offset2;
            }
            buffer[offset] = 45;
            value = -value;
            offset++;
        } else if (value <= MAX_INT_AS_LONG) {
            return outputInt((int) value, buffer, offset);
        }
        int origOffset = offset;
        int offset3 = offset + calcLongStrLength(value);
        int ptr = offset3;
        while (value > MAX_INT_AS_LONG) {
            ptr -= 3;
            long newValue = value / THOUSAND_L;
            outputFullTriplet((int) (value - (THOUSAND_L * newValue)), buffer, ptr);
            value = newValue;
        }
        int ivalue = (int) value;
        while (ivalue >= 1000) {
            ptr -= 3;
            int newValue2 = ivalue / 1000;
            outputFullTriplet(ivalue - (newValue2 * 1000), buffer, ptr);
            ivalue = newValue2;
        }
        outputLeadingTriplet(ivalue, buffer, origOffset);
        return offset3;
    }

    private static int outputLeadingTriplet(int triplet, char[] buffer, int offset) {
        int digitOffset = triplet << 2;
        int digitOffset2 = digitOffset + 1;
        char c = LEADING_TRIPLETS[digitOffset];
        if (c != 0) {
            buffer[offset] = c;
            offset++;
        }
        int digitOffset3 = digitOffset2 + 1;
        char c2 = LEADING_TRIPLETS[digitOffset2];
        if (c2 != 0) {
            buffer[offset] = c2;
            offset++;
        }
        int offset2 = offset + 1;
        buffer[offset] = LEADING_TRIPLETS[digitOffset3];
        return offset2;
    }

    private static int outputLeadingTriplet(int triplet, byte[] buffer, int offset) {
        int digitOffset = triplet << 2;
        int digitOffset2 = digitOffset + 1;
        char c = LEADING_TRIPLETS[digitOffset];
        if (c != 0) {
            buffer[offset] = (byte) c;
            offset++;
        }
        int digitOffset3 = digitOffset2 + 1;
        char c2 = LEADING_TRIPLETS[digitOffset2];
        if (c2 != 0) {
            buffer[offset] = (byte) c2;
            offset++;
        }
        int offset2 = offset + 1;
        buffer[offset] = (byte) LEADING_TRIPLETS[digitOffset3];
        return offset2;
    }

    private static int outputFullTriplet(int triplet, char[] buffer, int offset) {
        int digitOffset = triplet << 2;
        int offset2 = offset + 1;
        int digitOffset2 = digitOffset + 1;
        buffer[offset] = FULL_TRIPLETS[digitOffset];
        int offset3 = offset2 + 1;
        buffer[offset2] = FULL_TRIPLETS[digitOffset2];
        int offset4 = offset3 + 1;
        buffer[offset3] = FULL_TRIPLETS[digitOffset2 + 1];
        return offset4;
    }

    private static int outputFullTriplet(int triplet, byte[] buffer, int offset) {
        int digitOffset = triplet << 2;
        int offset2 = offset + 1;
        int digitOffset2 = digitOffset + 1;
        buffer[offset] = FULL_TRIPLETS_B[digitOffset];
        int offset3 = offset2 + 1;
        buffer[offset2] = FULL_TRIPLETS_B[digitOffset2];
        int offset4 = offset3 + 1;
        buffer[offset3] = FULL_TRIPLETS_B[digitOffset2 + 1];
        return offset4;
    }

    private static int calcLongStrLength(long posValue) {
        int len = 10;
        for (long comp = TEN_BILLION_L; posValue >= comp && len != 19; comp = (comp << 3) + (comp << 1)) {
            len++;
        }
        return len;
    }
}
