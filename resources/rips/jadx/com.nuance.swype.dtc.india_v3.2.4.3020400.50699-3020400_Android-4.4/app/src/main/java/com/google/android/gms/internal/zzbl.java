package com.google.android.gms.internal;

import com.google.android.gms.internal.zzae;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public final class zzbl extends zzbp {
    private static volatile String zzct = null;
    private static final Object zzafc = new Object();

    public zzbl(zzax zzaxVar, String str, String str2, zzae.zza zzaVar, int i) {
        super(zzaxVar, str, str2, zzaVar, i, 1);
    }

    @Override // com.google.android.gms.internal.zzbp
    protected final void zzcu() throws IllegalAccessException, InvocationTargetException {
        this.zzaha.zzct = "E";
        if (zzct == null) {
            synchronized (zzafc) {
                if (zzct == null) {
                    zzct = (String) this.zzahh.invoke(null, new Object[0]);
                }
            }
        }
        synchronized (this.zzaha) {
            this.zzaha.zzct = zzct;
        }
    }
}
