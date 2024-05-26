package com.google.android.gms.ads.internal.overlay;

import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkh;

/* JADX INFO: Access modifiers changed from: package-private */
@zzin
/* loaded from: classes.dex */
public final class zzy implements Runnable {
    boolean mCancelled = false;
    private zzk zzbwf;

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzy(zzk zzkVar) {
        this.zzbwf = zzkVar;
    }

    public final void zzpk() {
        zzkh.zzclc.postDelayed(this, 250L);
    }

    @Override // java.lang.Runnable
    public final void run() {
        if (this.mCancelled) {
            return;
        }
        zzk zzkVar = this.zzbwf;
        if (zzkVar.zzbty != null) {
            long currentPosition = zzkVar.zzbty.getCurrentPosition();
            if (zzkVar.zzbuc != currentPosition && currentPosition > 0) {
                if (zzkVar.zzor()) {
                    zzkVar.zzbtw.removeView(zzkVar.zzbub);
                }
                zzkVar.zza("timeupdate", "time", String.valueOf(((float) currentPosition) / 1000.0f));
                zzkVar.zzbuc = currentPosition;
            }
        }
        zzpk();
    }
}
