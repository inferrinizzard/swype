package com.nuance.nmsp.client.util.internal.dictationresult.util;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import java.io.UnsupportedEncodingException;

/* loaded from: classes.dex */
public class Util {
    private static final LogFactory.Log a = LogFactory.getLog(Util.class);

    public static int bytesToInt(byte[] bArr, int i) {
        return (bytesToUShort(bArr, i) & 65535) | ((bytesToUShort(bArr, i + 2) & 65535) << 16);
    }

    public static long bytesToUInt(byte[] bArr, int i) {
        return (((bArr[i + 3] & 255) << 24) | ((bArr[i + 2] & 255) << 16) | ((bArr[i + 1] & 255) << 8) | (bArr[i] & 255)) & 4294967295L;
    }

    public static int bytesToUShort(byte[] bArr, int i) {
        return (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8);
    }

    public static int computeStrLen(byte[] bArr, int i) {
        if (a.isTraceEnabled()) {
            a.trace("Computing string length");
        }
        int i2 = i;
        while (bArr[i2] != 0) {
            i2++;
        }
        int i3 = i2 - i;
        if (a.isTraceEnabled()) {
            a.trace("String length: [" + i3 + "]");
        }
        return i3;
    }

    public static String constructByteEncodedString(byte[] bArr, int i, int i2, String str) {
        try {
            return new String(bArr, i, i2, str);
        } catch (UnsupportedEncodingException e) {
            if (a.isWarnEnabled()) {
                a.warn(str + " character encoding is not available in your VM. Using the default one.");
            }
            return new String(bArr, i, i2);
        }
    }

    public static String trimWhiteSpace(String str) {
        int i = 0;
        int length = str.length();
        char[] charArray = str.toCharArray();
        while (i < length && charArray[i] == ' ') {
            i++;
        }
        while (i < length && charArray[length - 1] == ' ') {
            length--;
        }
        return str.substring(i, length);
    }
}
