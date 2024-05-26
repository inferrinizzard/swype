package com.google.android.gms.internal;

/* loaded from: classes.dex */
public final class zzapr implements Cloneable {
    static final zzaps bjz = new zzaps();
    private boolean bjA;
    int[] bjB;
    zzaps[] bjC;
    int mSize;

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzapr() {
        this(10);
    }

    private zzapr(int i) {
        this.bjA = false;
        int idealIntArraySize = idealIntArraySize(i);
        this.bjB = new int[idealIntArraySize];
        this.bjC = new zzaps[idealIntArraySize];
        this.mSize = 0;
    }

    public final int hashCode() {
        int i = 17;
        for (int i2 = 0; i2 < this.mSize; i2++) {
            i = (((i * 31) + this.bjB[i2]) * 31) + this.bjC[i2].hashCode();
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int zzagh(int i) {
        int i2 = 0;
        int i3 = this.mSize - 1;
        while (i2 <= i3) {
            int i4 = (i2 + i3) >>> 1;
            int i5 = this.bjB[i4];
            if (i5 < i) {
                i2 = i4 + 1;
            } else {
                if (i5 <= i) {
                    return i4;
                }
                i3 = i4 - 1;
            }
        }
        return i2 ^ (-1);
    }

    public final boolean isEmpty() {
        return this.mSize == 0;
    }

    public final boolean equals(Object obj) {
        boolean z;
        boolean z2;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzapr)) {
            return false;
        }
        zzapr zzaprVar = (zzapr) obj;
        if (this.mSize != zzaprVar.mSize) {
            return false;
        }
        int[] iArr = this.bjB;
        int[] iArr2 = zzaprVar.bjB;
        int i = this.mSize;
        int i2 = 0;
        while (true) {
            if (i2 >= i) {
                z = true;
                break;
            }
            if (iArr[i2] != iArr2[i2]) {
                z = false;
                break;
            }
            i2++;
        }
        if (z) {
            zzaps[] zzapsVarArr = this.bjC;
            zzaps[] zzapsVarArr2 = zzaprVar.bjC;
            int i3 = this.mSize;
            int i4 = 0;
            while (true) {
                if (i4 >= i3) {
                    z2 = true;
                    break;
                }
                if (!zzapsVarArr[i4].equals(zzapsVarArr2[i4])) {
                    z2 = false;
                    break;
                }
                i4++;
            }
            if (z2) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int idealIntArraySize(int i) {
        int i2 = i * 4;
        int i3 = 4;
        while (true) {
            if (i3 >= 32) {
                break;
            }
            if (i2 <= (1 << i3) - 12) {
                i2 = (1 << i3) - 12;
                break;
            }
            i3++;
        }
        return i2 / 4;
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        int i = this.mSize;
        zzapr zzaprVar = new zzapr(i);
        System.arraycopy(this.bjB, 0, zzaprVar.bjB, 0, i);
        for (int i2 = 0; i2 < i; i2++) {
            if (this.bjC[i2] != null) {
                zzaprVar.bjC[i2] = (zzaps) this.bjC[i2].clone();
            }
        }
        zzaprVar.mSize = i;
        return zzaprVar;
    }
}
