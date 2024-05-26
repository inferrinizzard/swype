package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
final class zzaps implements Cloneable {
    private zzapq<?, ?> bjD;
    List<zzapx> bjE = new ArrayList();
    private Object value;

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: aD, reason: merged with bridge method [inline-methods] */
    public zzaps clone() {
        int i = 0;
        zzaps zzapsVar = new zzaps();
        try {
            zzapsVar.bjD = this.bjD;
            if (this.bjE == null) {
                zzapsVar.bjE = null;
            } else {
                zzapsVar.bjE.addAll(this.bjE);
            }
            if (this.value != null) {
                if (this.value instanceof zzapv) {
                    zzapsVar.value = (zzapv) ((zzapv) this.value).clone();
                } else if (this.value instanceof byte[]) {
                    zzapsVar.value = ((byte[]) this.value).clone();
                } else if (this.value instanceof byte[][]) {
                    byte[][] bArr = (byte[][]) this.value;
                    byte[][] bArr2 = new byte[bArr.length];
                    zzapsVar.value = bArr2;
                    for (int i2 = 0; i2 < bArr.length; i2++) {
                        bArr2[i2] = (byte[]) bArr[i2].clone();
                    }
                } else if (this.value instanceof boolean[]) {
                    zzapsVar.value = ((boolean[]) this.value).clone();
                } else if (this.value instanceof int[]) {
                    zzapsVar.value = ((int[]) this.value).clone();
                } else if (this.value instanceof long[]) {
                    zzapsVar.value = ((long[]) this.value).clone();
                } else if (this.value instanceof float[]) {
                    zzapsVar.value = ((float[]) this.value).clone();
                } else if (this.value instanceof double[]) {
                    zzapsVar.value = ((double[]) this.value).clone();
                } else if (this.value instanceof zzapv[]) {
                    zzapv[] zzapvVarArr = (zzapv[]) this.value;
                    zzapv[] zzapvVarArr2 = new zzapv[zzapvVarArr.length];
                    zzapsVar.value = zzapvVarArr2;
                    while (true) {
                        int i3 = i;
                        if (i3 >= zzapvVarArr.length) {
                            break;
                        }
                        zzapvVarArr2[i3] = (zzapv) zzapvVarArr[i3].clone();
                        i = i3 + 1;
                    }
                }
            }
            return zzapsVar;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzaps)) {
            return false;
        }
        zzaps zzapsVar = (zzaps) obj;
        if (this.value != null && zzapsVar.value != null) {
            if (this.bjD == zzapsVar.bjD) {
                return !this.bjD.baj.isArray() ? this.value.equals(zzapsVar.value) : this.value instanceof byte[] ? Arrays.equals((byte[]) this.value, (byte[]) zzapsVar.value) : this.value instanceof int[] ? Arrays.equals((int[]) this.value, (int[]) zzapsVar.value) : this.value instanceof long[] ? Arrays.equals((long[]) this.value, (long[]) zzapsVar.value) : this.value instanceof float[] ? Arrays.equals((float[]) this.value, (float[]) zzapsVar.value) : this.value instanceof double[] ? Arrays.equals((double[]) this.value, (double[]) zzapsVar.value) : this.value instanceof boolean[] ? Arrays.equals((boolean[]) this.value, (boolean[]) zzapsVar.value) : Arrays.deepEquals((Object[]) this.value, (Object[]) zzapsVar.value);
            }
            return false;
        }
        if (this.bjE != null && zzapsVar.bjE != null) {
            return this.bjE.equals(zzapsVar.bjE);
        }
        try {
            return Arrays.equals(toByteArray(), zzapsVar.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public final int hashCode() {
        try {
            return Arrays.hashCode(toByteArray()) + 527;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int zzx() {
        int i = 0;
        if (this.value != null) {
            return this.bjD.zzcp(this.value);
        }
        Iterator<zzapx> it = this.bjE.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            zzapx next = it.next();
            i = next.apf.length + zzapo.zzagc(next.tag) + 0 + i2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void zza(zzapo zzapoVar) throws IOException {
        if (this.value != null) {
            this.bjD.zza(this.value, zzapoVar);
            return;
        }
        for (zzapx zzapxVar : this.bjE) {
            zzapoVar.zzagb(zzapxVar.tag);
            zzapoVar.zzbh(zzapxVar.apf);
        }
    }

    private byte[] toByteArray() throws IOException {
        byte[] bArr = new byte[zzx()];
        zza(zzapo.zzc$715daad5(bArr, bArr.length));
        return bArr;
    }
}
