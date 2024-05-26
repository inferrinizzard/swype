package com.google.android.gms.ads.internal.reward.client;

import com.google.android.gms.ads.internal.reward.client.zzd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class zzg extends zzd.zza {
    private final RewardedVideoAdListener zzfh;

    public zzg(RewardedVideoAdListener rewardedVideoAdListener) {
        this.zzfh = rewardedVideoAdListener;
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzd
    public void onRewardedVideoAdClosed() {
        if (this.zzfh != null) {
            this.zzfh.onRewardedVideoAdClosed();
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzd
    public void onRewardedVideoAdFailedToLoad(int i) {
        if (this.zzfh != null) {
            this.zzfh.onRewardedVideoAdFailedToLoad(i);
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzd
    public void onRewardedVideoAdLeftApplication() {
        if (this.zzfh != null) {
            this.zzfh.onRewardedVideoAdLeftApplication();
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzd
    public void onRewardedVideoAdLoaded() {
        if (this.zzfh != null) {
            this.zzfh.onRewardedVideoAdLoaded();
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzd
    public void onRewardedVideoAdOpened() {
        if (this.zzfh != null) {
            this.zzfh.onRewardedVideoAdOpened();
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzd
    public void onRewardedVideoStarted() {
        if (this.zzfh != null) {
            this.zzfh.onRewardedVideoStarted();
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzd
    public void zza(zza zzaVar) {
        if (this.zzfh != null) {
            this.zzfh.onRewarded(new zze(zzaVar));
        }
    }
}
