package com.google.android.gms.internal;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@zzin
/* loaded from: classes.dex */
public class zzkv<T> implements zzky<T> {
    private boolean zzbox;
    private T zzcnn;
    private boolean zzcno;
    private final Object zzail = new Object();
    final zzkz zzcnp = new zzkz();

    private boolean zzty() {
        return this.zzcno;
    }

    @Override // java.util.concurrent.Future
    public boolean cancel(boolean z) {
        boolean z2 = false;
        if (z) {
            synchronized (this.zzail) {
                if (!zzty()) {
                    this.zzbox = true;
                    this.zzcno = true;
                    this.zzail.notifyAll();
                    this.zzcnp.zztz();
                    z2 = true;
                }
            }
        }
        return z2;
    }

    @Override // java.util.concurrent.Future
    public T get() throws CancellationException, ExecutionException, InterruptedException {
        T t;
        synchronized (this.zzail) {
            if (!zzty()) {
                try {
                    this.zzail.wait();
                } catch (InterruptedException e) {
                    throw e;
                }
            }
            if (this.zzbox) {
                throw new CancellationException("CallbackFuture was cancelled.");
            }
            t = this.zzcnn;
        }
        return t;
    }

    @Override // java.util.concurrent.Future
    public T get(long j, TimeUnit timeUnit) throws CancellationException, ExecutionException, InterruptedException, TimeoutException {
        T t;
        synchronized (this.zzail) {
            if (!zzty()) {
                try {
                    long millis = timeUnit.toMillis(j);
                    if (millis != 0) {
                        this.zzail.wait(millis);
                    }
                } catch (InterruptedException e) {
                    throw e;
                }
            }
            if (!this.zzcno) {
                throw new TimeoutException("CallbackFuture timed out.");
            }
            if (this.zzbox) {
                throw new CancellationException("CallbackFuture was cancelled.");
            }
            t = this.zzcnn;
        }
        return t;
    }

    @Override // java.util.concurrent.Future
    public boolean isCancelled() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzbox;
        }
        return z;
    }

    @Override // java.util.concurrent.Future
    public boolean isDone() {
        boolean zzty;
        synchronized (this.zzail) {
            zzty = zzty();
        }
        return zzty;
    }

    @Override // com.google.android.gms.internal.zzky
    public final void zzc(Runnable runnable) {
        this.zzcnp.zzc(runnable);
    }

    public final void zzh(T t) {
        synchronized (this.zzail) {
            if (this.zzbox) {
                return;
            }
            if (zzty()) {
                com.google.android.gms.ads.internal.zzu.zzft().zzb((Throwable) new IllegalStateException("Provided CallbackFuture with multiple values."), true);
                return;
            }
            this.zzcno = true;
            this.zzcnn = t;
            this.zzail.notifyAll();
            this.zzcnp.zztz();
        }
    }
}
