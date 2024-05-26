package com.google.android.gms.internal;

import com.google.android.gms.internal.zzae;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public final class zzaz extends zzbp {
    public zzaz(zzax zzaxVar, String str, String str2, zzae.zza zzaVar, int i) {
        super(zzaxVar, str, str2, zzaVar, i, 3);
    }

    @Override // com.google.android.gms.internal.zzbp
    protected final void zzcu() throws IllegalAccessException, InvocationTargetException {
        synchronized (this.zzaha) {
            this.zzaha.zzcu = -1L;
            this.zzaha.zzcu = (Long) this.zzahh.invoke(null, this.zzaey.getContext());
        }
    }
}
