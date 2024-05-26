package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.ads.internal.request.AdRequestInfoParcel;
import com.nuance.swype.input.IME;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@zzin
/* loaded from: classes.dex */
public final class zzgg implements zzfy {
    private final Context mContext;
    private final zzgj zzajz;
    private final boolean zzarl;
    private final boolean zzawn;
    private final zzga zzboe;
    private final AdRequestInfoParcel zzbot;
    final long zzbou;
    final long zzbov;
    final Object zzail = new Object();
    boolean zzbox = false;
    final Map<zzky<zzge>, zzgd> zzboy = new HashMap();
    private List<zzge> zzboz = new ArrayList();
    private final int zzbow = 2;

    public zzgg(Context context, AdRequestInfoParcel adRequestInfoParcel, zzgj zzgjVar, zzga zzgaVar, boolean z, boolean z2, long j, long j2) {
        this.mContext = context;
        this.zzbot = adRequestInfoParcel;
        this.zzajz = zzgjVar;
        this.zzboe = zzgaVar;
        this.zzarl = z;
        this.zzawn = z2;
        this.zzbou = j;
        this.zzbov = j2;
    }

    private void zza(final zzky<zzge> zzkyVar) {
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzgg.2
            @Override // java.lang.Runnable
            public final void run() {
                for (zzky<zzge> zzkyVar2 : zzgg.this.zzboy.keySet()) {
                    if (zzkyVar2 != zzkyVar) {
                        zzgg.this.zzboy.get(zzkyVar2).cancel();
                    }
                }
            }
        });
    }

    private zzge zze(List<zzky<zzge>> list) {
        synchronized (this.zzail) {
            if (this.zzbox) {
                return new zzge(-1);
            }
            for (zzky<zzge> zzkyVar : list) {
                try {
                    zzge zzgeVar = zzkyVar.get();
                    this.zzboz.add(zzgeVar);
                    if (zzgeVar != null && zzgeVar.zzbom == 0) {
                        zza(zzkyVar);
                        return zzgeVar;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    zzkd.zzd("Exception while processing an adapter; continuing with other adapters", e);
                }
            }
            zza(null);
            return new zzge(1);
        }
    }

    private zzge zzf(List<zzky<zzge>> list) {
        zzge zzgeVar;
        zzge zzgeVar2;
        zzky<zzge> zzkyVar;
        int i;
        zzgm zzgmVar;
        synchronized (this.zzail) {
            if (this.zzbox) {
                return new zzge(-1);
            }
            int i2 = -1;
            zzky<zzge> zzkyVar2 = null;
            zzge zzgeVar3 = null;
            long j = this.zzboe.zzbnw != -1 ? this.zzboe.zzbnw : IME.RETRY_DELAY_IN_MILLIS;
            long j2 = j;
            for (zzky<zzge> zzkyVar3 : list) {
                long currentTimeMillis = com.google.android.gms.ads.internal.zzu.zzfu().currentTimeMillis();
                if (j2 == 0) {
                    try {
                        try {
                        } catch (RemoteException | InterruptedException | ExecutionException | TimeoutException e) {
                            zzkd.zzd("Exception while processing an adapter; continuing with other adapters", e);
                            j2 = Math.max(j2 - (com.google.android.gms.ads.internal.zzu.zzfu().currentTimeMillis() - currentTimeMillis), 0L);
                        }
                        if (zzkyVar3.isDone()) {
                            zzgeVar = zzkyVar3.get();
                            this.zzboz.add(zzgeVar);
                            if (zzgeVar != null || zzgeVar.zzbom != 0 || (zzgmVar = zzgeVar.zzbor) == null || zzgmVar.zzmm() <= i2) {
                                zzgeVar2 = zzgeVar3;
                                zzkyVar = zzkyVar2;
                                i = i2;
                            } else {
                                i = zzgmVar.zzmm();
                                zzge zzgeVar4 = zzgeVar;
                                zzkyVar = zzkyVar3;
                                zzgeVar2 = zzgeVar4;
                            }
                            j2 = Math.max(j2 - (com.google.android.gms.ads.internal.zzu.zzfu().currentTimeMillis() - currentTimeMillis), 0L);
                            zzkyVar2 = zzkyVar;
                            i2 = i;
                            zzgeVar3 = zzgeVar2;
                        }
                    } catch (Throwable th) {
                        Math.max(j2 - (com.google.android.gms.ads.internal.zzu.zzfu().currentTimeMillis() - currentTimeMillis), 0L);
                        throw th;
                    }
                }
                zzgeVar = zzkyVar3.get(j2, TimeUnit.MILLISECONDS);
                this.zzboz.add(zzgeVar);
                if (zzgeVar != null) {
                }
                zzgeVar2 = zzgeVar3;
                zzkyVar = zzkyVar2;
                i = i2;
                j2 = Math.max(j2 - (com.google.android.gms.ads.internal.zzu.zzfu().currentTimeMillis() - currentTimeMillis), 0L);
                zzkyVar2 = zzkyVar;
                i2 = i;
                zzgeVar3 = zzgeVar2;
            }
            zza(zzkyVar2);
            return zzgeVar3 == null ? new zzge(1) : zzgeVar3;
        }
    }

    @Override // com.google.android.gms.internal.zzfy
    public final void cancel() {
        synchronized (this.zzail) {
            this.zzbox = true;
            Iterator<zzgd> it = this.zzboy.values().iterator();
            while (it.hasNext()) {
                it.next().cancel();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzfy
    public final zzge zzd(List<zzfz> list) {
        zzkd.zzcv("Starting mediation.");
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        ArrayList arrayList = new ArrayList();
        for (zzfz zzfzVar : list) {
            String valueOf = String.valueOf(zzfzVar.zzbmv);
            zzkd.zzcw(valueOf.length() != 0 ? "Trying mediation network: ".concat(valueOf) : new String("Trying mediation network: "));
            Iterator<String> it = zzfzVar.zzbmw.iterator();
            while (it.hasNext()) {
                final zzgd zzgdVar = new zzgd(this.mContext, it.next(), this.zzajz, this.zzboe, zzfzVar, this.zzbot.zzcar, this.zzbot.zzapa, this.zzbot.zzaow, this.zzarl, this.zzawn, this.zzbot.zzapo, this.zzbot.zzaps);
                zzky<zzge> zza = zzkg.zza(newCachedThreadPool, new Callable<zzge>() { // from class: com.google.android.gms.internal.zzgg.1
                    /* JADX INFO: Access modifiers changed from: private */
                    @Override // java.util.concurrent.Callable
                    /* renamed from: zzmn, reason: merged with bridge method [inline-methods] */
                    public zzge call() throws Exception {
                        synchronized (zzgg.this.zzail) {
                            if (zzgg.this.zzbox) {
                                return null;
                            }
                            return zzgdVar.zza(zzgg.this.zzbou, zzgg.this.zzbov);
                        }
                    }
                });
                this.zzboy.put(zza, zzgdVar);
                arrayList.add(zza);
            }
        }
        switch (this.zzbow) {
            case 2:
                return zzf(arrayList);
            default:
                return zze(arrayList);
        }
    }

    @Override // com.google.android.gms.internal.zzfy
    public final List<zzge> zzmg() {
        return this.zzboz;
    }
}
