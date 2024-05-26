package com.google.android.gms.internal;

import com.google.android.gms.internal.zzae;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public final class zzbj extends zzbp {
    private static volatile Long zzahb = null;
    private static final Object zzafc = new Object();

    public zzbj(zzax zzaxVar, String str, String str2, zzae.zza zzaVar, int i) {
        super(zzaxVar, str, str2, zzaVar, i, 22);
    }

    @Override // com.google.android.gms.internal.zzbp
    protected final void zzcu() throws IllegalAccessException, InvocationTargetException {
        if (zzahb == null) {
            synchronized (zzafc) {
                if (zzahb == null) {
                    zzahb = (Long) this.zzahh.invoke(null, new Object[0]);
                }
            }
        }
        synchronized (this.zzaha) {
            this.zzaha.zzdm = zzahb;
        }
    }
}
