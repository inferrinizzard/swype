package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.internal.zzju;

@zzin
/* loaded from: classes.dex */
public final class zzjg extends zzkc implements zzjh, zzjk {
    final Context mContext;
    final String zzboc;
    private final zzju.zza zzbxr;
    private final zzjm zzchm;
    private final zzjk zzchn;
    final String zzcho;
    private final String zzchp;
    private int zzchq = 0;
    private int zzbyi = 3;
    private final Object zzail = new Object();

    public zzjg(Context context, String str, String str2, String str3, zzju.zza zzaVar, zzjm zzjmVar, zzjk zzjkVar) {
        this.mContext = context;
        this.zzboc = str;
        this.zzcho = str2;
        this.zzchp = str3;
        this.zzbxr = zzaVar;
        this.zzchm = zzjmVar;
        this.zzchn = zzjkVar;
    }

    private boolean zzf(long j) {
        long elapsedRealtime = 20000 - (com.google.android.gms.ads.internal.zzu.zzfu().elapsedRealtime() - j);
        if (elapsedRealtime <= 0) {
            return false;
        }
        try {
            this.zzail.wait(elapsedRealtime);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override // com.google.android.gms.internal.zzkc
    public final void onStop() {
    }

    final void zza(AdRequestParcel adRequestParcel, zzgk zzgkVar) {
        try {
            if ("com.google.ads.mediation.admob.AdMobAdapter".equals(this.zzboc)) {
                zzgkVar.zza(adRequestParcel, this.zzcho, this.zzchp);
            } else {
                zzgkVar.zzc(adRequestParcel, this.zzcho);
            }
        } catch (RemoteException e) {
            zzkd.zzd("Fail to load ad from adapter.", e);
            zza$505cff1c(0);
        }
    }

    @Override // com.google.android.gms.internal.zzjk
    public final void zza$505cff1c(int i) {
        synchronized (this.zzail) {
            this.zzchq = 2;
            this.zzbyi = i;
            this.zzail.notify();
        }
    }

    @Override // com.google.android.gms.internal.zzjh
    public final void zzaw$13462e() {
        zza$505cff1c(0);
    }

    @Override // com.google.android.gms.internal.zzjk
    public final void zzcg(String str) {
        synchronized (this.zzail) {
            this.zzchq = 1;
            this.zzail.notify();
        }
    }

    @Override // com.google.android.gms.internal.zzkc
    public final void zzew() {
        if (this.zzchm == null || this.zzchm.zzcib == null || this.zzchm.zzbog == null) {
            return;
        }
        final zzjj zzjjVar = this.zzchm.zzcib;
        zzjjVar.zzchn = this;
        zzjjVar.zzchu = this;
        final AdRequestParcel adRequestParcel = this.zzbxr.zzcip.zzcar;
        final zzgk zzgkVar = this.zzchm.zzbog;
        try {
            if (zzgkVar.isInitialized()) {
                com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzjg.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        zzjg.this.zza(adRequestParcel, zzgkVar);
                    }
                });
            } else {
                com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzjg.2
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            zzgkVar.zza(com.google.android.gms.dynamic.zze.zzac(zzjg.this.mContext), adRequestParcel, (String) null, zzjjVar, zzjg.this.zzcho);
                        } catch (RemoteException e) {
                            String valueOf = String.valueOf(zzjg.this.zzboc);
                            zzkd.zzd(valueOf.length() != 0 ? "Fail to initialize adapter ".concat(valueOf) : new String("Fail to initialize adapter "), e);
                            zzjg.this.zza$505cff1c(0);
                        }
                    }
                });
            }
        } catch (RemoteException e) {
            zzkd.zzd("Fail to check if adapter is initialized.", e);
            zza$505cff1c(0);
        }
        long elapsedRealtime = com.google.android.gms.ads.internal.zzu.zzfu().elapsedRealtime();
        while (true) {
            synchronized (this.zzail) {
                if (this.zzchq == 0) {
                    if (!zzf(elapsedRealtime)) {
                    }
                }
            }
        }
        zzjjVar.zzchn = null;
        zzjjVar.zzchu = null;
        if (this.zzchq == 1) {
            this.zzchn.zzcg(this.zzboc);
        } else {
            this.zzchn.zza$505cff1c(this.zzbyi);
        }
    }

    @Override // com.google.android.gms.internal.zzjh
    public final void zzrs() {
        zza(this.zzbxr.zzcip.zzcar, this.zzchm.zzbog);
    }
}
