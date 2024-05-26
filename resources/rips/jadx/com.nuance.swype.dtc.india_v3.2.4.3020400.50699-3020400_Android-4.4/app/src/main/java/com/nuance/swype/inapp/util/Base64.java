package com.nuance.swype.inapp.util;

import android.util.Log;
import com.nuance.nmsp.client.sdk.common.protocols.ProtocolDefines;
import java.io.UnsupportedEncodingException;

/* loaded from: classes.dex */
public final class Base64 {
    private static final byte[] ALPHABET = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] WEBSAFE_ALPHABET = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
    private static final byte[] DECODABET = {-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, ProtocolDefines.XMODE_PROTOCOL_BB_HANDSHAKE, ProtocolDefines.XMODE_SERVER_SESSION_UUID_SIZE, 17, ProtocolDefines.XMODE_VERSION_VAP, 19, 20, 21, 22, ProtocolDefines.XMODE_VERSION_COP, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, ProtocolDefines.XMODE_VERSION_BCP, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9, -9};
    private static final byte[] WEBSAFE_DECODABET = {-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, ProtocolDefines.XMODE_PROTOCOL_BB_HANDSHAKE, ProtocolDefines.XMODE_SERVER_SESSION_UUID_SIZE, 17, ProtocolDefines.XMODE_VERSION_VAP, 19, 20, 21, 22, ProtocolDefines.XMODE_VERSION_COP, 24, 25, -9, -9, -9, -9, 63, -9, 26, 27, 28, 29, 30, 31, 32, 33, ProtocolDefines.XMODE_VERSION_BCP, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9, -9};

    private static int decode4to3$355785af(byte[] source, byte[] destination, int destOffset, byte[] decodabet) {
        if (source[2] == 61) {
            destination[destOffset] = (byte) ((((decodabet[source[0]] << 24) >>> 6) | ((decodabet[source[1]] << 24) >>> 12)) >>> 16);
            return 1;
        }
        if (source[3] == 61) {
            int outBuff = ((decodabet[source[1]] << 24) >>> 12) | ((decodabet[source[0]] << 24) >>> 6) | ((decodabet[source[2]] << 24) >>> 18);
            destination[destOffset] = (byte) (outBuff >>> 16);
            destination[destOffset + 1] = (byte) (outBuff >>> 8);
            return 2;
        }
        int outBuff2 = ((decodabet[source[1]] << 24) >>> 12) | ((decodabet[source[0]] << 24) >>> 6) | ((decodabet[source[2]] << 24) >>> 18) | ((decodabet[source[3]] << 24) >>> 24);
        destination[destOffset] = (byte) (outBuff2 >> 16);
        destination[destOffset + 1] = (byte) (outBuff2 >> 8);
        destination[destOffset + 2] = (byte) outBuff2;
        return 3;
    }

    public static byte[] decode(String s) throws Base64DecoderException {
        int i;
        int i2;
        try {
            byte[] bytes = s.getBytes("UTF-8");
            int length = bytes.length;
            byte[] bArr = DECODABET;
            byte[] bArr2 = new byte[((length * 3) / 4) + 2];
            int i3 = 0;
            byte[] bArr3 = new byte[4];
            int i4 = 0;
            int i5 = 0;
            while (true) {
                if (i5 >= length) {
                    break;
                }
                byte b = (byte) (bytes[i5 + 0] & Byte.MAX_VALUE);
                byte b2 = bArr[b];
                if (b2 >= -5) {
                    if (b2 < -1) {
                        i = i4;
                        i2 = i3;
                    } else if (b == 61) {
                        int i6 = length - i5;
                        byte b3 = (byte) (bytes[(length - 1) + 0] & Byte.MAX_VALUE);
                        if (i4 == 0 || i4 == 1) {
                            throw new Base64DecoderException("invalid padding byte '=' at byte offset " + i5);
                        }
                        if ((i4 == 3 && i6 > 2) || (i4 == 4 && i6 > 1)) {
                            throw new Base64DecoderException("padding byte '=' falsely signals end of encoded value at offset " + i5);
                        }
                        if (b3 != 61 && b3 != 10) {
                            throw new Base64DecoderException("encoded value has invalid trailing byte");
                        }
                    } else {
                        i = i4 + 1;
                        bArr3[i4] = b;
                        if (i == 4) {
                            i2 = i3 + decode4to3$355785af(bArr3, bArr2, i3, bArr);
                            i = 0;
                        } else {
                            i2 = i3;
                        }
                    }
                    i5++;
                    i3 = i2;
                    i4 = i;
                } else {
                    throw new Base64DecoderException("Bad Base64 input character at " + i5 + ": " + ((int) bytes[i5 + 0]) + "(decimal)");
                }
            }
            if (i4 != 0) {
                if (i4 == 1) {
                    throw new Base64DecoderException("single trailing character at offset " + (length - 1));
                }
                bArr3[i4] = 61;
                i3 += decode4to3$355785af(bArr3, bArr2, i3, bArr);
            }
            byte[] bArr4 = new byte[i3];
            System.arraycopy(bArr2, 0, bArr4, 0, i3);
            return bArr4;
        } catch (UnsupportedEncodingException e) {
            Log.e("Base64", "decode: ", e);
            return null;
        }
    }
}
