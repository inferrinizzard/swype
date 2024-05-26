package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.ads.internal.request.AdResponseParcel;
import com.google.android.gms.internal.zzic;
import com.google.android.gms.internal.zzju;
import com.nuance.connect.util.TimeConversion;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@zzin
/* loaded from: classes.dex */
public final class zzig extends zzkc {
    private final Object zzail;
    final zzic.zza zzbxq;
    private final zzju.zza zzbxr;
    private final AdResponseParcel zzbxs;
    private final zzii zzbyq;
    private Future<zzju> zzbyr;

    public zzig(Context context, com.google.android.gms.ads.internal.zzq zzqVar, zzju.zza zzaVar, zzas zzasVar, zzic.zza zzaVar2) {
        this(zzaVar, zzaVar2, new zzii(context, zzqVar, new zzkn(context), zzasVar, zzaVar));
    }

    private zzig(zzju.zza zzaVar, zzic.zza zzaVar2, zzii zziiVar) {
        this.zzail = new Object();
        this.zzbxr = zzaVar;
        this.zzbxs = zzaVar.zzciq;
        this.zzbxq = zzaVar2;
        this.zzbyq = zziiVar;
    }

    @Override // com.google.android.gms.internal.zzkc
    public final void onStop() {
        synchronized (this.zzail) {
            if (this.zzbyr != null) {
                this.zzbyr.cancel(true);
            }
        }
    }

    @Override // com.google.android.gms.internal.zzkc
    public final void zzew() {
        final zzju zzjuVar;
        int i = -2;
        try {
            synchronized (this.zzail) {
                this.zzbyr = zzkg.zza(this.zzbyq);
            }
            zzjuVar = this.zzbyr.get(TimeConversion.MILLIS_IN_MINUTE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            i = 0;
            zzjuVar = null;
        } catch (CancellationException e2) {
            i = 0;
            zzjuVar = null;
        } catch (ExecutionException e3) {
            i = 0;
            zzjuVar = null;
        } catch (TimeoutException e4) {
            zzkd.zzcx("Timed out waiting for native ad.");
            i = 2;
            this.zzbyr.cancel(true);
            zzjuVar = null;
        }
        if (zzjuVar == null) {
            zzjuVar = new zzju(this.zzbxr.zzcip.zzcar, null, null, i, null, null, this.zzbxs.orientation, this.zzbxs.zzbns, this.zzbxr.zzcip.zzcau, false, null, null, null, null, null, this.zzbxs.zzcbz, this.zzbxr.zzapa, this.zzbxs.zzcbx, this.zzbxr.zzcik, this.zzbxs.zzccc, this.zzbxs.zzccd, this.zzbxr.zzcie, null, null, null, null, this.zzbxr.zzciq.zzccq, this.zzbxr.zzciq.zzccr, null, null);
        }
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzig.1
            @Override // java.lang.Runnable
            public final void run() {
                zzig.this.zzbxq.zzb(zzjuVar);
            }
        });
    }
}
