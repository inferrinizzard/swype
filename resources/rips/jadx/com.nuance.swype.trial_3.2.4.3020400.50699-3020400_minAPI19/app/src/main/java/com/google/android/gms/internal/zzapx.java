package com.google.android.gms.internal;

import java.util.Arrays;

/* loaded from: classes.dex */
final class zzapx {
    final byte[] apf;
    final int tag;

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzapx(int i, byte[] bArr) {
        this.tag = i;
        this.apf = bArr;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzapx)) {
            return false;
        }
        zzapx zzapxVar = (zzapx) obj;
        return this.tag == zzapxVar.tag && Arrays.equals(this.apf, zzapxVar.apf);
    }

    public final int hashCode() {
        return ((this.tag + 527) * 31) + Arrays.hashCode(this.apf);
    }
}
