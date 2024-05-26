package com.google.android.gms.ads.internal;

import android.os.Handler;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkh;
import com.nuance.connect.util.TimeConversion;
import java.lang.ref.WeakReference;

@zzin
/* loaded from: classes.dex */
public class zzr {
    private final zza zzanb;
    private AdRequestParcel zzanc;
    private boolean zzand;
    private boolean zzane;
    private long zzanf;
    private final Runnable zzw;

    /* loaded from: classes.dex */
    public static class zza {
        private final Handler mHandler;

        public zza(Handler handler) {
            this.mHandler = handler;
        }

        public boolean postDelayed(Runnable runnable, long j) {
            return this.mHandler.postDelayed(runnable, j);
        }

        public void removeCallbacks(Runnable runnable) {
            this.mHandler.removeCallbacks(runnable);
        }
    }

    public zzr(com.google.android.gms.ads.internal.zza zzaVar) {
        this(zzaVar, new zza(zzkh.zzclc));
    }

    private zzr(com.google.android.gms.ads.internal.zza zzaVar, zza zzaVar2) {
        this.zzand = false;
        this.zzane = false;
        this.zzanf = 0L;
        this.zzanb = zzaVar2;
        final WeakReference weakReference = new WeakReference(zzaVar);
        this.zzw = new Runnable() { // from class: com.google.android.gms.ads.internal.zzr.1
            @Override // java.lang.Runnable
            public final void run() {
                zzr.zza$da7ce75(zzr.this);
                com.google.android.gms.ads.internal.zza zzaVar3 = (com.google.android.gms.ads.internal.zza) weakReference.get();
                if (zzaVar3 != null) {
                    zzaVar3.zzd(zzr.this.zzanc);
                }
            }
        };
    }

    static /* synthetic */ boolean zza$da7ce75(zzr zzrVar) {
        zzrVar.zzand = false;
        return false;
    }

    public void cancel() {
        this.zzand = false;
        this.zzanb.removeCallbacks(this.zzw);
    }

    public void pause() {
        this.zzane = true;
        if (this.zzand) {
            this.zzanb.removeCallbacks(this.zzw);
        }
    }

    public void resume() {
        this.zzane = false;
        if (this.zzand) {
            this.zzand = false;
            zza(this.zzanc, this.zzanf);
        }
    }

    public void zza(AdRequestParcel adRequestParcel, long j) {
        if (this.zzand) {
            zzkd.zzcx("An ad refresh is already scheduled.");
            return;
        }
        this.zzanc = adRequestParcel;
        this.zzand = true;
        this.zzanf = j;
        if (this.zzane) {
            return;
        }
        zzkd.zzcw(new StringBuilder(65).append("Scheduling ad refresh ").append(j).append(" milliseconds from now.").toString());
        this.zzanb.postDelayed(this.zzw, j);
    }

    public boolean zzfc() {
        return this.zzand;
    }

    public void zzg(AdRequestParcel adRequestParcel) {
        zza(adRequestParcel, TimeConversion.MILLIS_IN_MINUTE);
    }
}
