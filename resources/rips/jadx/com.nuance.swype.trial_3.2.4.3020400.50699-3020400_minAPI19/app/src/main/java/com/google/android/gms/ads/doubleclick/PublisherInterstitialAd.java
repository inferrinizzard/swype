package com.google.android.gms.ads.doubleclick;

import android.content.Context;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.Correlator;
import com.google.android.gms.ads.internal.client.zzaf;

/* loaded from: classes.dex */
public final class PublisherInterstitialAd {
    private final zzaf zzaij;

    public PublisherInterstitialAd(Context context) {
        this.zzaij = new zzaf(context, this);
    }

    public final AdListener getAdListener() {
        return this.zzaij.getAdListener();
    }

    public final String getAdUnitId() {
        return this.zzaij.getAdUnitId();
    }

    public final AppEventListener getAppEventListener() {
        return this.zzaij.getAppEventListener();
    }

    public final String getMediationAdapterClassName() {
        return this.zzaij.getMediationAdapterClassName();
    }

    public final OnCustomRenderedAdLoadedListener getOnCustomRenderedAdLoadedListener() {
        return this.zzaij.getOnCustomRenderedAdLoadedListener();
    }

    public final boolean isLoaded() {
        return this.zzaij.isLoaded();
    }

    public final boolean isLoading() {
        return this.zzaij.isLoading();
    }

    public final void loadAd(PublisherAdRequest publisherAdRequest) {
        this.zzaij.zza(publisherAdRequest.zzdc());
    }

    public final void setAdListener(AdListener adListener) {
        this.zzaij.setAdListener(adListener);
    }

    public final void setAdUnitId(String str) {
        this.zzaij.setAdUnitId(str);
    }

    public final void setAppEventListener(AppEventListener appEventListener) {
        this.zzaij.setAppEventListener(appEventListener);
    }

    public final void setCorrelator(Correlator correlator) {
        this.zzaij.setCorrelator(correlator);
    }

    public final void setOnCustomRenderedAdLoadedListener(OnCustomRenderedAdLoadedListener onCustomRenderedAdLoadedListener) {
        this.zzaij.setOnCustomRenderedAdLoadedListener(onCustomRenderedAdLoadedListener);
    }

    public final void show() {
        this.zzaij.show();
    }
}
