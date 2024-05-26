package com.google.android.gms.internal;

import com.google.android.gms.internal.zzae;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public final class zzbk extends zzbp {
    private long zzahc;

    public zzbk(zzax zzaxVar, String str, String str2, zzae.zza zzaVar, int i) {
        super(zzaxVar, str, str2, zzaVar, i, 12);
        this.zzahc = -1L;
    }

    @Override // com.google.android.gms.internal.zzbp
    protected final void zzcu() throws IllegalAccessException, InvocationTargetException {
        this.zzaha.zzdd = -1L;
        if (this.zzahc == -1) {
            this.zzahc = ((Integer) this.zzahh.invoke(null, this.zzaey.getContext())).intValue();
        }
        synchronized (this.zzaha) {
            this.zzaha.zzdd = Long.valueOf(this.zzahc);
        }
    }
}
