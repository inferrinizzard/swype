package com.google.android.gms.internal;

import java.io.IOException;

/* loaded from: classes.dex */
public abstract class zzapv {
    protected volatile int bjG = -1;

    @Override // 
    /* renamed from: aB, reason: merged with bridge method [inline-methods] */
    public zzapv clone() throws CloneNotSupportedException {
        return (zzapv) super.clone();
    }

    public final int aL() {
        if (this.bjG < 0) {
            aM();
        }
        return this.bjG;
    }

    public final int aM() {
        int zzx = zzx();
        this.bjG = zzx;
        return zzx;
    }

    public String toString() {
        return zzapw.zzg(this);
    }

    public void zza(zzapo zzapoVar) throws IOException {
    }

    public abstract zzapv zzb(zzapn zzapnVar) throws IOException;

    protected int zzx() {
        return 0;
    }

    public static final byte[] zzf(zzapv zzapvVar) {
        byte[] bArr = new byte[zzapvVar.aM()];
        try {
            zzapo zzc$715daad5 = zzapo.zzc$715daad5(bArr, bArr.length);
            zzapvVar.zza(zzc$715daad5);
            if (zzc$715daad5.bjw.remaining() != 0) {
                throw new IllegalStateException("Did not write as much data as expected.");
            }
            return bArr;
        } catch (IOException e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }

    public static <T extends zzapv> T zzb$16844d7a(T t, byte[] bArr, int i) throws zzapu {
        try {
            zzapn zzapnVar = new zzapn(bArr, i);
            t.zzb(zzapnVar);
            zzapnVar.zzafo(0);
            return t;
        } catch (zzapu e) {
            throw e;
        } catch (IOException e2) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
        }
    }
}
