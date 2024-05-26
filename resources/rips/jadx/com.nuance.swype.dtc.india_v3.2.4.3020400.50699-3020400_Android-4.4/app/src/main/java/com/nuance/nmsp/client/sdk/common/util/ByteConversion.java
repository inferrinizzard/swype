package com.nuance.nmsp.client.sdk.common.util;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;

/* loaded from: classes.dex */
public class ByteConversion {
    private static final LogFactory.Log a = LogFactory.getLog(ByteConversion.class);

    public static int bytesToInt(byte[] bArr, int i) {
        int bytesToShort = (bytesToShort(bArr, i) & 65535) | ((bytesToShort(bArr, i + 2) & 65535) << 16);
        if (a.isTraceEnabled()) {
            a.trace("Converted [" + ((int) bArr[i]) + ", " + ((int) bArr[i + 1]) + ", " + ((int) bArr[i + 2]) + ", " + ((int) bArr[i + 3]) + "] to int " + bytesToShort);
        }
        return bytesToShort;
    }

    public static short bytesToShort(byte[] bArr, int i) {
        short s = (short) ((bArr[i] & 255) | ((bArr[i + 1] & 255) << 8));
        if (a.isTraceEnabled()) {
            a.trace("Converted [" + ((int) bArr[i]) + ", " + ((int) bArr[i + 1]) + "] to short " + ((int) s));
        }
        return s;
    }

    public static void intToBytes(int i, byte[] bArr, int i2) {
        shortToBytes((short) i, bArr, i2);
        shortToBytes((short) (i >> 16), bArr, i2 + 2);
        if (a.isTraceEnabled()) {
            a.trace("Converted int " + i + " to [" + ((int) bArr[i2]) + ", " + ((int) bArr[i2 + 1]) + ", " + ((int) bArr[i2 + 2]) + ", " + ((int) bArr[i2 + 3]) + "]");
        }
    }

    public static void shortToBytes(short s, byte[] bArr, int i) {
        bArr[i] = (byte) s;
        bArr[i + 1] = (byte) (s >> 8);
        if (a.isTraceEnabled()) {
            a.trace("Converted short " + ((int) s) + " to [" + ((int) bArr[i]) + ", " + ((int) bArr[i + 1]) + "]");
        }
    }
}
