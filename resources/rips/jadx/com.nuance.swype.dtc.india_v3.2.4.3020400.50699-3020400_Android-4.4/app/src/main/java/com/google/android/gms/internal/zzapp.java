package com.google.android.gms.internal;

import com.google.android.gms.internal.zzapp;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class zzapp<M extends zzapp<M>> extends zzapv {
    protected zzapr bjx;

    @Override // com.google.android.gms.internal.zzapv
    /* renamed from: aA, reason: merged with bridge method [inline-methods] */
    public M clone() throws CloneNotSupportedException {
        M m = (M) super.clone();
        zzapt.zza(this, m);
        return m;
    }

    @Override // com.google.android.gms.internal.zzapv
    /* renamed from: aB */
    public /* synthetic */ zzapv clone() throws CloneNotSupportedException {
        return (zzapp) clone();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.internal.zzapv
    public int zzx() {
        if (this.bjx == null) {
            return 0;
        }
        int i = 0;
        for (int i2 = 0; i2 < this.bjx.mSize; i2++) {
            i += this.bjx.bjC[i2].zzx();
        }
        return i;
    }

    @Override // com.google.android.gms.internal.zzapv
    public void zza(zzapo zzapoVar) throws IOException {
        if (this.bjx == null) {
            return;
        }
        for (int i = 0; i < this.bjx.mSize; i++) {
            this.bjx.bjC[i].zza(zzapoVar);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean zza(zzapn zzapnVar, int i) throws IOException {
        byte[] bArr;
        zzaps zzapsVar;
        int position = zzapnVar.getPosition();
        if (!zzapnVar.zzafp(i)) {
            return false;
        }
        int zzagj = zzapy.zzagj(i);
        int position2 = zzapnVar.getPosition() - position;
        if (position2 == 0) {
            bArr = zzapy.bjO;
        } else {
            bArr = new byte[position2];
            System.arraycopy(zzapnVar.buffer, position + zzapnVar.bjn, bArr, 0, position2);
        }
        zzapx zzapxVar = new zzapx(i, bArr);
        if (this.bjx == null) {
            this.bjx = new zzapr();
            zzapsVar = null;
        } else {
            zzapr zzaprVar = this.bjx;
            int zzagh = zzaprVar.zzagh(zzagj);
            zzapsVar = (zzagh < 0 || zzaprVar.bjC[zzagh] == zzapr.bjz) ? null : zzaprVar.bjC[zzagh];
        }
        if (zzapsVar == null) {
            zzapsVar = new zzaps();
            zzapr zzaprVar2 = this.bjx;
            int zzagh2 = zzaprVar2.zzagh(zzagj);
            if (zzagh2 >= 0) {
                zzaprVar2.bjC[zzagh2] = zzapsVar;
            } else {
                int i2 = zzagh2 ^ (-1);
                if (i2 >= zzaprVar2.mSize || zzaprVar2.bjC[i2] != zzapr.bjz) {
                    if (zzaprVar2.mSize >= zzaprVar2.bjB.length) {
                        int idealIntArraySize = zzapr.idealIntArraySize(zzaprVar2.mSize + 1);
                        int[] iArr = new int[idealIntArraySize];
                        zzaps[] zzapsVarArr = new zzaps[idealIntArraySize];
                        System.arraycopy(zzaprVar2.bjB, 0, iArr, 0, zzaprVar2.bjB.length);
                        System.arraycopy(zzaprVar2.bjC, 0, zzapsVarArr, 0, zzaprVar2.bjC.length);
                        zzaprVar2.bjB = iArr;
                        zzaprVar2.bjC = zzapsVarArr;
                    }
                    if (zzaprVar2.mSize - i2 != 0) {
                        System.arraycopy(zzaprVar2.bjB, i2, zzaprVar2.bjB, i2 + 1, zzaprVar2.mSize - i2);
                        System.arraycopy(zzaprVar2.bjC, i2, zzaprVar2.bjC, i2 + 1, zzaprVar2.mSize - i2);
                    }
                    zzaprVar2.bjB[i2] = zzagj;
                    zzaprVar2.bjC[i2] = zzapsVar;
                    zzaprVar2.mSize++;
                } else {
                    zzaprVar2.bjB[i2] = zzagj;
                    zzaprVar2.bjC[i2] = zzapsVar;
                }
            }
        }
        zzapsVar.bjE.add(zzapxVar);
        return true;
    }
}
