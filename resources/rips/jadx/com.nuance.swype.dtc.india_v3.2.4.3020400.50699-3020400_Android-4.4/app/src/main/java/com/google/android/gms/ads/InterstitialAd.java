package com.google.android.gms.ads;

import android.content.Context;
import com.google.android.gms.ads.internal.client.zzaf;
import com.google.android.gms.ads.purchase.InAppPurchaseListener;
import com.google.android.gms.ads.purchase.PlayStorePurchaseListener;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

/* loaded from: classes.dex */
public final class InterstitialAd {
    private final zzaf zzaij;

    public InterstitialAd(Context context) {
        this.zzaij = new zzaf(context);
    }

    public final AdListener getAdListener() {
        return this.zzaij.getAdListener();
    }

    public final String getAdUnitId() {
        return this.zzaij.getAdUnitId();
    }

    public final InAppPurchaseListener getInAppPurchaseListener() {
        return this.zzaij.getInAppPurchaseListener();
    }

    public final String getMediationAdapterClassName() {
        return this.zzaij.getMediationAdapterClassName();
    }

    public final boolean isLoaded() {
        return this.zzaij.isLoaded();
    }

    public final boolean isLoading() {
        return this.zzaij.isLoading();
    }

    public final void loadAd(AdRequest adRequest) {
        this.zzaij.zza(adRequest.zzdc());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void setAdListener(AdListener adListener) {
        this.zzaij.setAdListener(adListener);
        if (adListener != 0 && (adListener instanceof com.google.android.gms.ads.internal.client.zza)) {
            this.zzaij.zza((com.google.android.gms.ads.internal.client.zza) adListener);
        } else if (adListener == 0) {
            this.zzaij.zza((com.google.android.gms.ads.internal.client.zza) null);
        }
    }

    public final void setAdUnitId(String str) {
        this.zzaij.setAdUnitId(str);
    }

    public final void setInAppPurchaseListener(InAppPurchaseListener inAppPurchaseListener) {
        this.zzaij.setInAppPurchaseListener(inAppPurchaseListener);
    }

    public final void setPlayStorePurchaseParams(PlayStorePurchaseListener playStorePurchaseListener, String str) {
        this.zzaij.setPlayStorePurchaseParams(playStorePurchaseListener, str);
    }

    public final void setRewardedVideoAdListener(RewardedVideoAdListener rewardedVideoAdListener) {
        this.zzaij.setRewardedVideoAdListener(rewardedVideoAdListener);
    }

    public final void show() {
        this.zzaij.show();
    }

    public final void zzd(boolean z) {
        this.zzaij.zzd(z);
    }
}
