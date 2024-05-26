package com.google.android.gms.internal;

import com.google.android.gms.ads.internal.reward.mediation.client.RewardItemParcel;
import com.google.android.gms.ads.internal.reward.mediation.client.zza;

@zzin
/* loaded from: classes.dex */
public final class zzjj extends zza.AbstractBinderC0036zza {
    zzjk zzchn;
    zzjh zzchu;
    private zzji zzchv;

    public zzjj(zzji zzjiVar) {
        this.zzchv = zzjiVar;
    }

    @Override // com.google.android.gms.ads.internal.reward.mediation.client.zza
    public final void zza(com.google.android.gms.dynamic.zzd zzdVar, RewardItemParcel rewardItemParcel) {
        if (this.zzchv != null) {
            this.zzchv.zzc(rewardItemParcel);
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.mediation.client.zza
    public final void zzb(com.google.android.gms.dynamic.zzd zzdVar, int i) {
        if (this.zzchu != null) {
            this.zzchu.zzaw$13462e();
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.mediation.client.zza
    public final void zzc(com.google.android.gms.dynamic.zzd zzdVar, int i) {
        if (this.zzchn != null) {
            zzjk zzjkVar = this.zzchn;
            com.google.android.gms.dynamic.zze.zzad(zzdVar).getClass().getName();
            zzjkVar.zza$505cff1c(i);
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.mediation.client.zza
    public final void zzp(com.google.android.gms.dynamic.zzd zzdVar) {
        if (this.zzchu != null) {
            this.zzchu.zzrs();
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.mediation.client.zza
    public final void zzq(com.google.android.gms.dynamic.zzd zzdVar) {
        if (this.zzchn != null) {
            this.zzchn.zzcg(com.google.android.gms.dynamic.zze.zzad(zzdVar).getClass().getName());
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.mediation.client.zza
    public final void zzr(com.google.android.gms.dynamic.zzd zzdVar) {
        if (this.zzchv != null) {
            this.zzchv.onRewardedVideoAdOpened();
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.mediation.client.zza
    public final void zzs(com.google.android.gms.dynamic.zzd zzdVar) {
        if (this.zzchv != null) {
            this.zzchv.onRewardedVideoStarted();
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.mediation.client.zza
    public final void zzt(com.google.android.gms.dynamic.zzd zzdVar) {
        if (this.zzchv != null) {
            this.zzchv.onRewardedVideoAdClosed();
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.mediation.client.zza
    public final void zzu(com.google.android.gms.dynamic.zzd zzdVar) {
        if (this.zzchv != null) {
            this.zzchv.zzrr();
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.mediation.client.zza
    public final void zzv(com.google.android.gms.dynamic.zzd zzdVar) {
        if (this.zzchv != null) {
            this.zzchv.onRewardedVideoAdLeftApplication();
        }
    }
}
