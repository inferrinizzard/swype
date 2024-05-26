package com.google.android.gms.internal;

import java.io.IOException;

/* loaded from: classes.dex */
public final class zzapy {
    public static final int[] bjH = new int[0];
    public static final long[] bjI = new long[0];
    public static final float[] bjJ = new float[0];
    public static final double[] bjK = new double[0];
    public static final boolean[] bjL = new boolean[0];
    public static final String[] bjM = new String[0];
    public static final byte[][] bjN = new byte[0];
    public static final byte[] bjO = new byte[0];

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int zzagi(int i) {
        return i & 7;
    }

    public static int zzagj(int i) {
        return i >>> 3;
    }

    public static int zzaj(int i, int i2) {
        return (i << 3) | i2;
    }

    public static final int zzc(zzapn zzapnVar, int i) throws IOException {
        int i2 = 1;
        int position = zzapnVar.getPosition();
        zzapnVar.zzafp(i);
        while (zzapnVar.ah() == i) {
            zzapnVar.zzafp(i);
            i2++;
        }
        zzapnVar.zzaft(position);
        return i2;
    }
}
