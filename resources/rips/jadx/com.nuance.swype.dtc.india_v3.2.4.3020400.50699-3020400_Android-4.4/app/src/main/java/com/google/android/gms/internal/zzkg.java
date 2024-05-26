package com.google.android.gms.internal;

import android.os.Process;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@zzin
/* loaded from: classes.dex */
public final class zzkg {
    private static final ExecutorService zzcku = Executors.newFixedThreadPool(10, zzcn("Default"));
    private static final ExecutorService zzckv = Executors.newFixedThreadPool(5, zzcn("Loader"));

    public static zzky<Void> zza(int i, final Runnable runnable) {
        return i == 1 ? zza(zzckv, new Callable<Void>() { // from class: com.google.android.gms.internal.zzkg.1
            @Override // java.util.concurrent.Callable
            public final /* synthetic */ Void call() throws Exception {
                runnable.run();
                return null;
            }
        }) : zza(zzcku, new Callable<Void>() { // from class: com.google.android.gms.internal.zzkg.2
            @Override // java.util.concurrent.Callable
            public final /* synthetic */ Void call() throws Exception {
                runnable.run();
                return null;
            }
        });
    }

    public static zzky<Void> zza(Runnable runnable) {
        return zza(0, runnable);
    }

    public static <T> zzky<T> zza(Callable<T> callable) {
        return zza(zzcku, callable);
    }

    private static ThreadFactory zzcn(final String str) {
        return new ThreadFactory() { // from class: com.google.android.gms.internal.zzkg.5
            private final AtomicInteger zzcla = new AtomicInteger(1);

            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                String str2 = str;
                return new Thread(runnable, new StringBuilder(String.valueOf(str2).length() + 23).append("AdWorker(").append(str2).append(") #").append(this.zzcla.getAndIncrement()).toString());
            }
        };
    }

    public static <T> zzky<T> zza(ExecutorService executorService, final Callable<T> callable) {
        final zzkv zzkvVar = new zzkv();
        try {
            final Future<?> submit = executorService.submit(new Runnable() { // from class: com.google.android.gms.internal.zzkg.3
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        Process.setThreadPriority(10);
                        zzkv.this.zzh(callable.call());
                    } catch (Exception e) {
                        com.google.android.gms.ads.internal.zzu.zzft().zzb((Throwable) e, true);
                        zzkv.this.cancel(true);
                    }
                }
            });
            Runnable runnable = new Runnable() { // from class: com.google.android.gms.internal.zzkg.4
                @Override // java.lang.Runnable
                public final void run() {
                    if (zzkv.this.isCancelled()) {
                        submit.cancel(true);
                    }
                }
            };
            zzkz zzkzVar = zzkvVar.zzcnp;
            synchronized (zzkzVar.zzcnx) {
                if (zzkzVar.zzcoa) {
                    zzkz.zzf(runnable);
                } else {
                    zzkzVar.zzcnz.add(runnable);
                }
            }
        } catch (RejectedExecutionException e) {
            zzkd.zzd("Thread execution is rejected.", e);
            zzkvVar.cancel(true);
        }
        return zzkvVar;
    }
}
