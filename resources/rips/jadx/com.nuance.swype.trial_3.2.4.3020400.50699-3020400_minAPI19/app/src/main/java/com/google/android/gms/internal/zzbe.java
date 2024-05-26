package com.google.android.gms.internal;

import com.google.android.gms.internal.zzae;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public final class zzbe extends zzbp {
    public zzbe(zzax zzaxVar, String str, String str2, zzae.zza zzaVar, int i) {
        super(zzaxVar, str, str2, zzaVar, i, 5);
    }

    @Override // com.google.android.gms.internal.zzbp
    protected final void zzcu() throws IllegalAccessException, InvocationTargetException {
        this.zzaha.zzcw = -1L;
        this.zzaha.zzcx = -1L;
        int[] iArr = (int[]) this.zzahh.invoke(null, this.zzaey.getContext());
        synchronized (this.zzaha) {
            this.zzaha.zzcw = Long.valueOf(iArr[0]);
            this.zzaha.zzcx = Long.valueOf(iArr[1]);
        }
    }
}
