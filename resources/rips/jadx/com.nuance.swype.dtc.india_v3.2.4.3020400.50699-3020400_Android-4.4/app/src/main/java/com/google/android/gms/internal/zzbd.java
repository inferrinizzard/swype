package com.google.android.gms.internal;

import com.google.android.gms.internal.zzae;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public final class zzbd extends zzbp {
    private static volatile String zzcq = null;
    private static final Object zzafc = new Object();

    public zzbd(zzax zzaxVar, String str, String str2, zzae.zza zzaVar, int i) {
        super(zzaxVar, str, str2, zzaVar, i, 34);
    }

    @Override // com.google.android.gms.internal.zzbp
    protected final void zzcu() throws IllegalAccessException, InvocationTargetException {
        this.zzaha.zzdt = "E";
        if (zzcq == null) {
            synchronized (zzafc) {
                if (zzcq == null) {
                    zzcq = (String) this.zzahh.invoke(null, this.zzaey.getContext());
                }
            }
        }
        synchronized (this.zzaha) {
            this.zzaha.zzdt = zzcq;
        }
    }
}
