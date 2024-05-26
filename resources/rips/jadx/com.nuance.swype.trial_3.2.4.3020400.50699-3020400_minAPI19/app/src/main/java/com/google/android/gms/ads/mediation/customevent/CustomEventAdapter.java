package com.google.android.gms.ads.mediation.customevent;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.MediationBannerAdapter;
import com.google.android.gms.ads.mediation.MediationBannerListener;
import com.google.android.gms.ads.mediation.MediationInterstitialAdapter;
import com.google.android.gms.ads.mediation.MediationInterstitialListener;
import com.google.android.gms.ads.mediation.MediationNativeAdapter;
import com.google.android.gms.ads.mediation.MediationNativeListener;
import com.google.android.gms.ads.mediation.NativeAdMapper;
import com.google.android.gms.ads.mediation.NativeMediationAdRequest;
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter;
import com.google.android.gms.common.annotation.KeepName;

@KeepName
/* loaded from: classes.dex */
public final class CustomEventAdapter implements MediationBannerAdapter, MediationInterstitialAdapter, MediationNativeAdapter {
    CustomEventBanner zzcqr;
    CustomEventInterstitial zzcqs;
    CustomEventNative zzcqt;
    private View zzfu;

    /* loaded from: classes.dex */
    static final class zza implements CustomEventBannerListener {
        private final CustomEventAdapter zzcqu;
        private final MediationBannerListener zzfm;

        public zza(CustomEventAdapter customEventAdapter, MediationBannerListener mediationBannerListener) {
            this.zzcqu = customEventAdapter;
            this.zzfm = mediationBannerListener;
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdClicked() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdClicked.");
            this.zzfm.onAdClicked(this.zzcqu);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdClosed() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdClosed.");
            this.zzfm.onAdClosed(this.zzcqu);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdFailedToLoad(int i) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdFailedToLoad.");
            this.zzfm.onAdFailedToLoad(this.zzcqu, i);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdLeftApplication() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdLeftApplication.");
            this.zzfm.onAdLeftApplication(this.zzcqu);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener
        public final void onAdLoaded(View view) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdLoaded.");
            this.zzcqu.zzfu = view;
            this.zzfm.onAdLoaded(this.zzcqu);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdOpened() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdOpened.");
            this.zzfm.onAdOpened(this.zzcqu);
        }
    }

    /* loaded from: classes.dex */
    class zzb implements CustomEventInterstitialListener {
        private final CustomEventAdapter zzcqu;
        private final MediationInterstitialListener zzfn;

        public zzb(CustomEventAdapter customEventAdapter, MediationInterstitialListener mediationInterstitialListener) {
            this.zzcqu = customEventAdapter;
            this.zzfn = mediationInterstitialListener;
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdClicked() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdClicked.");
            this.zzfn.onAdClicked(this.zzcqu);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdClosed() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdClosed.");
            this.zzfn.onAdClosed(this.zzcqu);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdFailedToLoad(int i) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onFailedToReceiveAd.");
            this.zzfn.onAdFailedToLoad(this.zzcqu, i);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdLeftApplication() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdLeftApplication.");
            this.zzfn.onAdLeftApplication(this.zzcqu);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener
        public final void onAdLoaded() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onReceivedAd.");
            this.zzfn.onAdLoaded(CustomEventAdapter.this);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdOpened() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdOpened.");
            this.zzfn.onAdOpened(this.zzcqu);
        }
    }

    /* loaded from: classes.dex */
    static class zzc implements CustomEventNativeListener {
        private final CustomEventAdapter zzcqu;
        private final MediationNativeListener zzfo;

        public zzc(CustomEventAdapter customEventAdapter, MediationNativeListener mediationNativeListener) {
            this.zzcqu = customEventAdapter;
            this.zzfo = mediationNativeListener;
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdClicked() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdClicked.");
            this.zzfo.onAdClicked(this.zzcqu);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdClosed() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdClosed.");
            this.zzfo.onAdClosed(this.zzcqu);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdFailedToLoad(int i) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdFailedToLoad.");
            this.zzfo.onAdFailedToLoad(this.zzcqu, i);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener
        public final void onAdImpression() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdImpression.");
            this.zzfo.onAdImpression(this.zzcqu);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdLeftApplication() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdLeftApplication.");
            this.zzfo.onAdLeftApplication(this.zzcqu);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener
        public final void onAdLoaded(NativeAdMapper nativeAdMapper) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdLoaded.");
            this.zzfo.onAdLoaded(this.zzcqu, nativeAdMapper);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public final void onAdOpened() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onAdOpened.");
            this.zzfo.onAdOpened(this.zzcqu);
        }
    }

    private static <T> T zzj(String str) {
        try {
            return (T) Class.forName(str).newInstance();
        } catch (Throwable th) {
            String valueOf = String.valueOf(th.getMessage());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(new StringBuilder(String.valueOf(str).length() + 46 + String.valueOf(valueOf).length()).append("Could not instantiate custom event adapter: ").append(str).append(". ").append(valueOf).toString());
            return null;
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationBannerAdapter
    public final View getBannerView() {
        return this.zzfu;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdapter
    public final void onDestroy() {
        if (this.zzcqr != null) {
            this.zzcqr.onDestroy();
        }
        if (this.zzcqs != null) {
            this.zzcqs.onDestroy();
        }
        if (this.zzcqt != null) {
            this.zzcqt.onDestroy();
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdapter
    public final void onPause() {
        if (this.zzcqr != null) {
            this.zzcqr.onPause();
        }
        if (this.zzcqs != null) {
            this.zzcqs.onPause();
        }
        if (this.zzcqt != null) {
            this.zzcqt.onPause();
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdapter
    public final void onResume() {
        if (this.zzcqr != null) {
            this.zzcqr.onResume();
        }
        if (this.zzcqs != null) {
            this.zzcqs.onResume();
        }
        if (this.zzcqt != null) {
            this.zzcqt.onResume();
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationBannerAdapter
    public final void requestBannerAd(Context context, MediationBannerListener mediationBannerListener, Bundle bundle, AdSize adSize, MediationAdRequest mediationAdRequest, Bundle bundle2) {
        this.zzcqr = (CustomEventBanner) zzj(bundle.getString("class_name"));
        if (this.zzcqr == null) {
            mediationBannerListener.onAdFailedToLoad(this, 0);
        } else {
            this.zzcqr.requestBannerAd(context, new zza(this, mediationBannerListener), bundle.getString(MediationRewardedVideoAdAdapter.CUSTOM_EVENT_SERVER_PARAMETER_FIELD), adSize, mediationAdRequest, bundle2 == null ? null : bundle2.getBundle(bundle.getString("class_name")));
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationNativeAdapter
    public final void requestNativeAd(Context context, MediationNativeListener mediationNativeListener, Bundle bundle, NativeMediationAdRequest nativeMediationAdRequest, Bundle bundle2) {
        this.zzcqt = (CustomEventNative) zzj(bundle.getString("class_name"));
        if (this.zzcqt == null) {
            mediationNativeListener.onAdFailedToLoad(this, 0);
        } else {
            this.zzcqt.requestNativeAd(context, new zzc(this, mediationNativeListener), bundle.getString(MediationRewardedVideoAdAdapter.CUSTOM_EVENT_SERVER_PARAMETER_FIELD), nativeMediationAdRequest, bundle2 == null ? null : bundle2.getBundle(bundle.getString("class_name")));
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialAdapter
    public final void showInterstitial() {
        this.zzcqs.showInterstitial();
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialAdapter
    public final void requestInterstitialAd(Context context, MediationInterstitialListener mediationInterstitialListener, Bundle bundle, MediationAdRequest mediationAdRequest, Bundle bundle2) {
        this.zzcqs = (CustomEventInterstitial) zzj(bundle.getString("class_name"));
        if (this.zzcqs == null) {
            mediationInterstitialListener.onAdFailedToLoad(this, 0);
        } else {
            this.zzcqs.requestInterstitialAd(context, new zzb(this, mediationInterstitialListener), bundle.getString(MediationRewardedVideoAdAdapter.CUSTOM_EVENT_SERVER_PARAMETER_FIELD), mediationAdRequest, bundle2 == null ? null : bundle2.getBundle(bundle.getString("class_name")));
        }
    }
}
