package com.google.android.gms.internal;

import android.net.TrafficStats;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import java.util.concurrent.BlockingQueue;

/* loaded from: classes.dex */
public final class zzg extends Thread {
    private final zzb zzi;
    private final zzn zzj;
    volatile boolean zzk;
    private final BlockingQueue<zzk<?>> zzx;
    private final zzf zzy;

    public zzg(BlockingQueue<zzk<?>> blockingQueue, zzf zzfVar, zzb zzbVar, zzn zznVar) {
        super("VolleyNetworkDispatcher");
        this.zzk = false;
        this.zzx = blockingQueue;
        this.zzy = zzfVar;
        this.zzi = zzbVar;
        this.zzj = zznVar;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public final void run() {
        Process.setThreadPriority(10);
        while (true) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            try {
                zzk<?> take = this.zzx.take();
                try {
                    take.zzc("network-queue-take");
                    if (Build.VERSION.SDK_INT >= 14) {
                        TrafficStats.setThreadStatsTag(take.zzaf);
                    }
                    zzi zza = this.zzy.zza(take);
                    take.zzc("network-http-complete");
                    if (zza.zzaa && take.zzal) {
                        take.zzd("not-modified");
                    } else {
                        zzm<?> zza2 = take.zza(zza);
                        take.zzc("network-parse-complete");
                        if (take.zzaj && zza2.zzbf != null) {
                            this.zzi.zza(take.zzae, zza2.zzbf);
                            take.zzc("network-cache-written");
                        }
                        take.zzal = true;
                        this.zzj.zza(take, zza2);
                    }
                } catch (zzr e) {
                    e.zzab = SystemClock.elapsedRealtime() - elapsedRealtime;
                    this.zzj.zza(take, zzk.zzb(e));
                } catch (Exception e2) {
                    zzs.zza(e2, "Unhandled exception %s", e2.toString());
                    zzr zzrVar = new zzr(e2);
                    zzrVar.zzab = SystemClock.elapsedRealtime() - elapsedRealtime;
                    this.zzj.zza(take, zzrVar);
                }
            } catch (InterruptedException e3) {
                if (this.zzk) {
                    return;
                }
            }
        }
    }
}
