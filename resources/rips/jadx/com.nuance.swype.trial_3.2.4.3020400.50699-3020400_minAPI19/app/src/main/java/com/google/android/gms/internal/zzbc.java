package com.google.android.gms.internal;

import com.google.android.gms.internal.zzae;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public final class zzbc extends zzbp {
    private static volatile Long zzcr = null;
    private static final Object zzafc = new Object();

    public zzbc(zzax zzaxVar, String str, String str2, zzae.zza zzaVar, int i) {
        super(zzaxVar, str, str2, zzaVar, i, 35);
    }

    @Override // com.google.android.gms.internal.zzbp
    protected final void zzcu() throws IllegalAccessException, InvocationTargetException {
        this.zzaha.zzdu = -1L;
        if (zzcr == null) {
            synchronized (zzafc) {
                if (zzcr == null) {
                    zzcr = (Long) this.zzahh.invoke(null, this.zzaey.getContext());
                }
            }
        }
        synchronized (this.zzaha) {
            this.zzaha.zzdu = zzcr;
        }
    }
}
