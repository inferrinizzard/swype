package com.google.android.gms.internal;

/* loaded from: classes.dex */
public final class zzaoq {
    final byte[] bhE = new byte[256];
    int bhF;
    int bhG;

    public zzaoq(byte[] bArr) {
        for (int i = 0; i < 256; i++) {
            this.bhE[i] = (byte) i;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < 256; i3++) {
            i2 = (i2 + this.bhE[i3] + bArr[i3 % bArr.length]) & 255;
            byte b = this.bhE[i3];
            this.bhE[i3] = this.bhE[i2];
            this.bhE[i2] = b;
        }
        this.bhF = 0;
        this.bhG = 0;
    }

    public final void zzax(byte[] bArr) {
        int i = this.bhF;
        int i2 = this.bhG;
        for (int i3 = 0; i3 < 256; i3++) {
            i = (i + 1) & 255;
            i2 = (i2 + this.bhE[i]) & 255;
            byte b = this.bhE[i];
            this.bhE[i] = this.bhE[i2];
            this.bhE[i2] = b;
            bArr[i3] = (byte) (bArr[i3] ^ this.bhE[(this.bhE[i] + this.bhE[i2]) & 255]);
        }
        this.bhF = i;
        this.bhG = i2;
    }
}
