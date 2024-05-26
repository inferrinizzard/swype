package com.google.android.gms.internal;

import com.google.android.gms.internal.zzge;
import com.google.android.gms.internal.zzgl;

@zzin
/* loaded from: classes.dex */
public final class zzgc extends zzgl.zza {
    final Object zzail = new Object();
    zzge.zza zzboa;
    private zzgb zzbob;

    @Override // com.google.android.gms.internal.zzgl
    public final void onAdClicked() {
        synchronized (this.zzail) {
            if (this.zzbob != null) {
                this.zzbob.zzdz();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzgl
    public final void onAdClosed() {
        synchronized (this.zzail) {
            if (this.zzbob != null) {
                this.zzbob.zzea();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzgl
    public final void onAdFailedToLoad(int i) {
        synchronized (this.zzail) {
            if (this.zzboa != null) {
                this.zzboa.zzy(i == 3 ? 1 : 2);
                this.zzboa = null;
            }
        }
    }

    @Override // com.google.android.gms.internal.zzgl
    public final void onAdImpression() {
        synchronized (this.zzail) {
            if (this.zzbob != null) {
                this.zzbob.zzee();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzgl
    public final void onAdLeftApplication() {
        synchronized (this.zzail) {
            if (this.zzbob != null) {
                this.zzbob.zzeb();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzgl
    public final void onAdLoaded() {
        synchronized (this.zzail) {
            if (this.zzboa != null) {
                this.zzboa.zzy(0);
                this.zzboa = null;
            } else {
                if (this.zzbob != null) {
                    this.zzbob.zzed();
                }
            }
        }
    }

    @Override // com.google.android.gms.internal.zzgl
    public final void onAdOpened() {
        synchronized (this.zzail) {
            if (this.zzbob != null) {
                this.zzbob.zzec();
            }
        }
    }

    public final void zza(zzgb zzgbVar) {
        synchronized (this.zzail) {
            this.zzbob = zzgbVar;
        }
    }

    @Override // com.google.android.gms.internal.zzgl
    public final void zza(zzgm zzgmVar) {
        synchronized (this.zzail) {
            if (this.zzboa != null) {
                this.zzboa.zza$37cb6271(zzgmVar);
                this.zzboa = null;
            } else {
                if (this.zzbob != null) {
                    this.zzbob.zzed();
                }
            }
        }
    }
}
