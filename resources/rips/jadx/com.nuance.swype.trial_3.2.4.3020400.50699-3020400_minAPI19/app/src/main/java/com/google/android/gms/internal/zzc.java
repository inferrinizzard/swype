package com.google.android.gms.internal;

import android.os.Process;
import com.google.android.gms.internal.zzb;
import java.util.concurrent.BlockingQueue;

/* loaded from: classes.dex */
public final class zzc extends Thread {
    private static final boolean DEBUG = zzs.DEBUG;
    private final BlockingQueue<zzk<?>> zzg;
    private final BlockingQueue<zzk<?>> zzh;
    private final zzb zzi;
    private final zzn zzj;
    volatile boolean zzk;

    public zzc(BlockingQueue<zzk<?>> blockingQueue, BlockingQueue<zzk<?>> blockingQueue2, zzb zzbVar, zzn zznVar) {
        super("VolleyCacheDispatcher");
        this.zzk = false;
        this.zzg = blockingQueue;
        this.zzh = blockingQueue2;
        this.zzi = zzbVar;
        this.zzj = zznVar;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public final void run() {
        if (DEBUG) {
            zzs.zza("start new dispatcher", new Object[0]);
        }
        Process.setThreadPriority(10);
        this.zzi.initialize();
        while (true) {
            try {
                final zzk<?> take = this.zzg.take();
                take.zzc("cache-queue-take");
                zzb.zza zza = this.zzi.zza(take.zzae);
                if (zza == null) {
                    take.zzc("cache-miss");
                    this.zzh.put(take);
                } else {
                    if (zza.zzd < System.currentTimeMillis()) {
                        take.zzc("cache-hit-expired");
                        take.zzao = zza;
                        this.zzh.put(take);
                    } else {
                        take.zzc("cache-hit");
                        zzm<?> zza2 = take.zza(new zzi(zza.data, zza.zzf));
                        take.zzc("cache-hit-parsed");
                        if (zza.zze < System.currentTimeMillis()) {
                            take.zzc("cache-hit-refresh-needed");
                            take.zzao = zza;
                            zza2.zzbh = true;
                            this.zzj.zza(take, zza2, new Runnable() { // from class: com.google.android.gms.internal.zzc.1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    try {
                                        zzc.this.zzh.put(take);
                                    } catch (InterruptedException e) {
                                    }
                                }
                            });
                        } else {
                            this.zzj.zza(take, zza2);
                        }
                    }
                }
            } catch (InterruptedException e) {
                if (this.zzk) {
                    return;
                }
            }
        }
    }
}
