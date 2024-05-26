package com.google.android.gms.internal;

import java.io.UnsupportedEncodingException;

@zzin
/* loaded from: classes.dex */
public final class zzcr {
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int zzac(String str) {
        byte[] bytes;
        int i;
        int i2 = 0;
        try {
            bytes = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            bytes = str.getBytes();
        }
        int length = bytes.length;
        int i3 = (length & (-4)) + 0;
        int i4 = 0;
        int i5 = 0;
        while (i4 < i3) {
            int i6 = ((bytes[i4] & 255) | ((bytes[i4 + 1] & 255) << 8) | ((bytes[i4 + 2] & 255) << 16) | (bytes[i4 + 3] << 24)) * (-862048943);
            int i7 = i5 ^ (((i6 >>> 17) | (i6 << 15)) * 461845907);
            i4 += 4;
            i5 = (-430675100) + (((i7 >>> 19) | (i7 << 13)) * 5);
        }
        switch (length & 3) {
            case 1:
                int i8 = ((bytes[i3] & 255) | i2) * (-862048943);
                i = (((i8 >>> 17) | (i8 << 15)) * 461845907) ^ i5;
                break;
            case 2:
                i2 |= (bytes[i3 + 1] & 255) << 8;
                int i82 = ((bytes[i3] & 255) | i2) * (-862048943);
                i = (((i82 >>> 17) | (i82 << 15)) * 461845907) ^ i5;
                break;
            case 3:
                i2 = (bytes[i3 + 2] & 255) << 16;
                i2 |= (bytes[i3 + 1] & 255) << 8;
                int i822 = ((bytes[i3] & 255) | i2) * (-862048943);
                i = (((i822 >>> 17) | (i822 << 15)) * 461845907) ^ i5;
                break;
            default:
                i = i5;
                break;
        }
        int i9 = i ^ length;
        int i10 = (i9 ^ (i9 >>> 16)) * (-2048144789);
        int i11 = (i10 ^ (i10 >>> 13)) * (-1028477387);
        return i11 ^ (i11 >>> 16);
    }

    /* JADX WARN: Code restructure failed: missing block: B:44:0x007a, code lost:            if (((r9 >= 65382 && r9 <= 65437) || (r9 >= 65441 && r9 <= 65500)) != false) goto L50;     */
    /* JADX WARN: Removed duplicated region for block: B:52:0x007f  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x00a1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.String[] zzad(java.lang.String r13) {
        /*
            Method dump skipped, instructions count: 232
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzcr.zzad(java.lang.String):java.lang.String[]");
    }
}
