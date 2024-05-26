package com.google.android.gms.internal;

import com.google.android.gms.internal.zzae;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public final class zzba extends zzbp {
    private static volatile String zzagy = null;
    private static final Object zzafc = new Object();

    public zzba(zzax zzaxVar, String str, String str2, zzae.zza zzaVar, int i) {
        super(zzaxVar, str, str2, zzaVar, i, 29);
    }

    @Override // com.google.android.gms.internal.zzbp
    protected final void zzcu() throws IllegalAccessException, InvocationTargetException {
        this.zzaha.zzdp = "E";
        if (zzagy == null) {
            synchronized (zzafc) {
                if (zzagy == null) {
                    zzagy = (String) this.zzahh.invoke(null, this.zzaey.getContext());
                }
            }
        }
        synchronized (this.zzaha) {
            this.zzaha.zzdp = zzaj.zza(zzagy.getBytes(), true);
        }
    }
}
