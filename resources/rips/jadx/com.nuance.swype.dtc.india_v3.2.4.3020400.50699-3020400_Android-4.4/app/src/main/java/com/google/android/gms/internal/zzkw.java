package com.google.android.gms.internal;

import java.util.concurrent.TimeUnit;

@zzin
/* loaded from: classes.dex */
public final class zzkw<T> implements zzky<T> {
    private final T zzcnn;
    private final zzkz zzcnp = new zzkz();

    public zzkw(T t) {
        this.zzcnn = t;
        this.zzcnp.zztz();
    }

    @Override // java.util.concurrent.Future
    public final boolean cancel(boolean z) {
        return false;
    }

    @Override // java.util.concurrent.Future
    public final T get() {
        return this.zzcnn;
    }

    @Override // java.util.concurrent.Future
    public final T get(long j, TimeUnit timeUnit) {
        return this.zzcnn;
    }

    @Override // java.util.concurrent.Future
    public final boolean isCancelled() {
        return false;
    }

    @Override // java.util.concurrent.Future
    public final boolean isDone() {
        return true;
    }

    @Override // com.google.android.gms.internal.zzky
    public final void zzc(Runnable runnable) {
        this.zzcnp.zzc(runnable);
    }
}
