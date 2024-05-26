package com.google.android.gms.internal;

import android.content.SharedPreferences;
import java.util.concurrent.Callable;

@zzin
/* loaded from: classes.dex */
public final class zzdb {
    final Object zzail = new Object();
    boolean zzamt = false;
    SharedPreferences zzaxu = null;

    public final <T> T zzd(final zzcy<T> zzcyVar) {
        synchronized (this.zzail) {
            if (this.zzamt) {
                return (T) zzkt.zzb(new Callable<T>() { // from class: com.google.android.gms.internal.zzdb.1
                    @Override // java.util.concurrent.Callable
                    public final T call() {
                        return (T) zzcyVar.zza(zzdb.this.zzaxu);
                    }
                });
            }
            return zzcyVar.zzaxq;
        }
    }
}
