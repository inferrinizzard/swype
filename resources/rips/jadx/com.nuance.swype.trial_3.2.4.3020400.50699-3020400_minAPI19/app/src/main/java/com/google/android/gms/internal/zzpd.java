package com.google.android.gms.internal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public final class zzpd {
    private static void zza(byte[] bArr, int i, long j, long j2, long[] jArr) {
        long zzb = zzb(bArr, i);
        long zzb2 = zzb(bArr, i + 8);
        long zzb3 = zzb(bArr, i + 16);
        long zzb4 = zzb(bArr, i + 24);
        long j3 = zzb + j;
        long j4 = zzb2 + j3 + zzb3;
        long rotateRight = Long.rotateRight(j4, 23) + Long.rotateRight(j2 + j3 + zzb4, 51);
        jArr[0] = j4 + zzb4;
        jArr[1] = j3 + rotateRight;
    }

    private static long zzb(byte[] bArr, int i) {
        ByteBuffer wrap = ByteBuffer.wrap(bArr, i, 8);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        return wrap.getLong();
    }

    private static long zzc(long j, long j2) {
        long j3 = (j2 ^ j) * (-4132994306676758123L);
        long j4 = ((j3 ^ (j3 >>> 47)) ^ j) * (-4132994306676758123L);
        return (j4 ^ (j4 >>> 47)) * (-4132994306676758123L);
    }

    public static long zzm(byte[] bArr) {
        long j;
        long rotateRight;
        long zzc;
        if (bArr.length <= 32) {
            int length = bArr.length & (-8);
            int length2 = bArr.length & 7;
            long length3 = (bArr.length * (-4132994306676758123L)) ^ (-1397348546323613475L);
            int i = 0;
            while (i < length) {
                long zzb = zzb(bArr, i) * (-4132994306676758123L);
                i += 8;
                length3 = (-4132994306676758123L) * (length3 ^ ((zzb ^ (zzb >>> 47)) * (-4132994306676758123L)));
            }
            if (length2 != 0) {
                long j2 = 0;
                for (int i2 = 0; i2 < Math.min(length2, 8); i2++) {
                    j2 |= (bArr[length + i2] & 255) << (i2 * 8);
                }
                length3 = (length3 ^ j2) * (-4132994306676758123L);
            }
            long j3 = (length3 ^ (length3 >>> 47)) * (-4132994306676758123L);
            zzc = j3 ^ (j3 >>> 47);
        } else if (bArr.length <= 64) {
            int length4 = bArr.length;
            long zzb2 = zzb(bArr, 24);
            long zzb3 = zzb(bArr, 0) + ((length4 + zzb(bArr, length4 - 16)) * (-6505348102511208375L));
            long rotateRight2 = Long.rotateRight(zzb3 + zzb2, 52);
            long rotateRight3 = Long.rotateRight(zzb3, 37);
            long zzb4 = zzb3 + zzb(bArr, 8);
            long rotateRight4 = rotateRight3 + Long.rotateRight(zzb4, 7);
            long zzb5 = zzb4 + zzb(bArr, 16);
            long j4 = zzb2 + zzb5;
            long rotateRight5 = Long.rotateRight(zzb5, 31) + rotateRight2 + rotateRight4;
            long zzb6 = zzb(bArr, 16) + zzb(bArr, length4 - 32);
            long zzb7 = zzb(bArr, length4 - 8);
            long rotateRight6 = Long.rotateRight(zzb6 + zzb7, 52);
            long rotateRight7 = Long.rotateRight(zzb6, 37);
            long zzb8 = zzb6 + zzb(bArr, length4 - 24);
            long rotateRight8 = rotateRight7 + Long.rotateRight(zzb8, 7);
            long zzb9 = zzb(bArr, length4 - 16) + zzb8;
            long rotateRight9 = ((Long.rotateRight(zzb9, 31) + rotateRight6 + rotateRight8 + j4) * (-4288712594273399085L)) + ((zzb9 + zzb7 + rotateRight5) * (-6505348102511208375L));
            long j5 = ((rotateRight9 ^ (rotateRight9 >>> 47)) * (-6505348102511208375L)) + rotateRight5;
            zzc = (j5 ^ (j5 >>> 47)) * (-4288712594273399085L);
        } else {
            int length5 = bArr.length;
            long zzb10 = zzb(bArr, 0);
            long zzb11 = zzb(bArr, length5 - 16) ^ (-8261664234251669945L);
            long zzb12 = zzb(bArr, length5 - 56) ^ (-6505348102511208375L);
            long[] jArr = new long[2];
            long[] jArr2 = new long[2];
            zza(bArr, length5 - 64, length5, zzb11, jArr);
            zza(bArr, length5 - 32, length5 * (-8261664234251669945L), -6505348102511208375L, jArr2);
            long j6 = jArr[1];
            long j7 = zzb12 + ((j6 ^ (j6 >>> 47)) * (-8261664234251669945L));
            long rotateRight10 = (-8261664234251669945L) * Long.rotateRight(j7 + zzb10, 39);
            long rotateRight11 = Long.rotateRight(zzb11, 33) * (-8261664234251669945L);
            int i3 = (length5 - 1) & (-64);
            int i4 = 0;
            long j8 = rotateRight10;
            while (true) {
                long rotateRight12 = Long.rotateRight(j8 + rotateRight11 + jArr[0] + zzb(bArr, i4 + 16), 37) * (-8261664234251669945L);
                long rotateRight13 = Long.rotateRight(jArr[1] + rotateRight11 + zzb(bArr, i4 + 48), 42) * (-8261664234251669945L);
                j = rotateRight12 ^ jArr2[1];
                rotateRight11 = rotateRight13 ^ jArr[0];
                rotateRight = Long.rotateRight(j7 ^ jArr2[0], 33);
                zza(bArr, i4, jArr[1] * (-8261664234251669945L), jArr2[0] + j, jArr);
                zza(bArr, i4 + 32, rotateRight + jArr2[1], rotateRight11, jArr2);
                i4 += 64;
                i3 -= 64;
                if (i3 == 0) {
                    break;
                }
                j7 = j;
                j8 = rotateRight;
            }
            zzc = zzc(zzc(jArr[0], jArr2[0]) + (((rotateRight11 >>> 47) ^ rotateRight11) * (-8261664234251669945L)) + j, zzc(jArr[1], jArr2[1]) + rotateRight);
        }
        long zzc2 = zzc(zzc + (bArr.length >= 9 ? zzb(bArr, bArr.length - 8) : -6505348102511208375L), bArr.length >= 8 ? zzb(bArr, 0) : -6505348102511208375L);
        return (zzc2 == 0 || zzc2 == 1) ? zzc2 - 2 : zzc2;
    }
}
