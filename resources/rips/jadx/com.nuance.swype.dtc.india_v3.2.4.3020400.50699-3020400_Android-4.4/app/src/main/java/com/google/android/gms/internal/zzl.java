package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public final class zzl {
    private AtomicInteger zzax;
    final Map<String, Queue<zzk<?>>> zzay;
    final Set<zzk<?>> zzaz;
    final PriorityBlockingQueue<zzk<?>> zzba;
    private final PriorityBlockingQueue<zzk<?>> zzbb;
    private zzg[] zzbc;
    private zzc zzbd;
    List<Object> zzbe;
    private final zzb zzi;
    private final zzn zzj;
    private final zzf zzy;

    private zzl(zzb zzbVar, zzf zzfVar) {
        this(zzbVar, zzfVar, new zze(new Handler(Looper.getMainLooper())));
    }

    public zzl(zzb zzbVar, zzf zzfVar, byte b) {
        this(zzbVar, zzfVar);
    }

    private zzl(zzb zzbVar, zzf zzfVar, zzn zznVar) {
        this.zzax = new AtomicInteger();
        this.zzay = new HashMap();
        this.zzaz = new HashSet();
        this.zzba = new PriorityBlockingQueue<>();
        this.zzbb = new PriorityBlockingQueue<>();
        this.zzbe = new ArrayList();
        this.zzi = zzbVar;
        this.zzy = zzfVar;
        this.zzbc = new zzg[4];
        this.zzj = zznVar;
    }

    public final void start() {
        if (this.zzbd != null) {
            zzc zzcVar = this.zzbd;
            zzcVar.zzk = true;
            zzcVar.interrupt();
        }
        for (int i = 0; i < this.zzbc.length; i++) {
            if (this.zzbc[i] != null) {
                zzg zzgVar = this.zzbc[i];
                zzgVar.zzk = true;
                zzgVar.interrupt();
            }
        }
        this.zzbd = new zzc(this.zzba, this.zzbb, this.zzi, this.zzj);
        this.zzbd.start();
        for (int i2 = 0; i2 < this.zzbc.length; i2++) {
            zzg zzgVar2 = new zzg(this.zzbb, this.zzy, this.zzi, this.zzj);
            this.zzbc[i2] = zzgVar2;
            zzgVar2.start();
        }
    }

    public final <T> zzk<T> zze(zzk<T> zzkVar) {
        zzkVar.zzai = this;
        synchronized (this.zzaz) {
            this.zzaz.add(zzkVar);
        }
        zzkVar.zzah = Integer.valueOf(this.zzax.incrementAndGet());
        zzkVar.zzc("add-to-queue");
        if (zzkVar.zzaj) {
            synchronized (this.zzay) {
                String str = zzkVar.zzae;
                if (this.zzay.containsKey(str)) {
                    Queue<zzk<?>> queue = this.zzay.get(str);
                    if (queue == null) {
                        queue = new LinkedList<>();
                    }
                    queue.add(zzkVar);
                    this.zzay.put(str, queue);
                    if (zzs.DEBUG) {
                        zzs.zza("Request for cacheKey=%s is in flight, putting on hold.", str);
                    }
                } else {
                    this.zzay.put(str, null);
                    this.zzba.add(zzkVar);
                }
            }
        } else {
            this.zzbb.add(zzkVar);
        }
        return zzkVar;
    }
}
