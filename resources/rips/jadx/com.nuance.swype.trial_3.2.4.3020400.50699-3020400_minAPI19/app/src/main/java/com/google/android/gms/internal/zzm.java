package com.google.android.gms.internal;

import com.google.android.gms.internal.zzb;

/* loaded from: classes.dex */
public final class zzm<T> {
    public final T result;
    public final zzb.zza zzbf;
    public final zzr zzbg;
    public boolean zzbh;

    /* loaded from: classes.dex */
    public interface zza {
        void zze(zzr zzrVar);
    }

    /* loaded from: classes.dex */
    public interface zzb<T> {
        void zzb(T t);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzm(zzr zzrVar) {
        this.zzbh = false;
        this.result = null;
        this.zzbf = null;
        this.zzbg = zzrVar;
    }

    private zzm(T t, zzb.zza zzaVar) {
        this.zzbh = false;
        this.result = t;
        this.zzbf = zzaVar;
        this.zzbg = null;
    }

    public static <T> zzm<T> zza(T t, zzb.zza zzaVar) {
        return new zzm<>(t, zzaVar);
    }
}
