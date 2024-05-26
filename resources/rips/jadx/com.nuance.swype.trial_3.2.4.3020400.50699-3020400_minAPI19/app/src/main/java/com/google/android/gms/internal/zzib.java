package com.google.android.gms.internal;

import android.content.Context;
import android.os.SystemClock;
import com.google.android.gms.ads.internal.request.AdResponseParcel;
import com.google.android.gms.internal.zzic;
import com.google.android.gms.internal.zzju;

@zzin
/* loaded from: classes.dex */
public abstract class zzib extends zzkc {
    protected final Context mContext;
    protected final Object zzail;
    protected final zzic.zza zzbxq;
    protected final zzju.zza zzbxr;
    protected AdResponseParcel zzbxs;
    protected final Object zzbxu;

    /* loaded from: classes.dex */
    protected static final class zza extends Exception {
        final int zzbyi;

        public zza(String str, int i) {
            super(str);
            this.zzbyi = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public zzib(Context context, zzju.zza zzaVar, zzic.zza zzaVar2) {
        super(true);
        this.zzail = new Object();
        this.zzbxu = new Object();
        this.mContext = context;
        this.zzbxr = zzaVar;
        this.zzbxs = zzaVar.zzciq;
        this.zzbxq = zzaVar2;
    }

    @Override // com.google.android.gms.internal.zzkc
    public void onStop() {
    }

    protected abstract zzju zzak(int i);

    protected abstract void zzh(long j) throws zza;

    protected final void zzm(zzju zzjuVar) {
        this.zzbxq.zzb(zzjuVar);
    }

    @Override // com.google.android.gms.internal.zzkc
    public void zzew() {
        synchronized (this.zzail) {
            zzkd.zzcv("AdRendererBackgroundTask started.");
            int i = this.zzbxr.errorCode;
            try {
                zzh(SystemClock.elapsedRealtime());
            } catch (zza e) {
                int i2 = e.zzbyi;
                if (i2 == 3 || i2 == -1) {
                    zzkd.zzcw(e.getMessage());
                } else {
                    zzkd.zzcx(e.getMessage());
                }
                if (this.zzbxs == null) {
                    this.zzbxs = new AdResponseParcel(i2);
                } else {
                    this.zzbxs = new AdResponseParcel(i2, this.zzbxs.zzbns);
                }
                zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzib.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        zzib.this.onStop();
                    }
                });
                i = i2;
            }
            final zzju zzak = zzak(i);
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzib.2
                @Override // java.lang.Runnable
                public final void run() {
                    synchronized (zzib.this.zzail) {
                        zzib.this.zzm(zzak);
                    }
                }
            });
        }
    }
}
