package com.google.android.gms.internal;

import com.google.android.gms.internal.zzae;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public final class zzbg extends zzbp {
    private long startTime;

    public zzbg(zzax zzaxVar, String str, String str2, zzae.zza zzaVar, long j, int i) {
        super(zzaxVar, str, str2, zzaVar, i, 25);
        this.startTime = j;
    }

    @Override // com.google.android.gms.internal.zzbp
    protected final void zzcu() throws IllegalAccessException, InvocationTargetException {
        long longValue = ((Long) this.zzahh.invoke(null, new Object[0])).longValue();
        synchronized (this.zzaha) {
            this.zzaha.zzek = Long.valueOf(longValue);
            if (this.startTime != 0) {
                this.zzaha.zzdi = Long.valueOf(longValue - this.startTime);
                this.zzaha.zzdn = Long.valueOf(this.startTime);
            }
        }
    }
}
