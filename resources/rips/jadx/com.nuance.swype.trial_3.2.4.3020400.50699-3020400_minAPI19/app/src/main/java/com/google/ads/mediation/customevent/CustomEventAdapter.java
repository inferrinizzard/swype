package com.google.ads.mediation.customevent;

import android.app.Activity;
import android.view.View;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.MediationBannerAdapter;
import com.google.ads.mediation.MediationBannerListener;
import com.google.ads.mediation.MediationInterstitialAdapter;
import com.google.ads.mediation.MediationInterstitialListener;
import com.google.android.gms.ads.mediation.customevent.CustomEventExtras;
import com.google.android.gms.common.annotation.KeepName;

@KeepName
/* loaded from: classes.dex */
public final class CustomEventAdapter implements MediationBannerAdapter<CustomEventExtras, CustomEventServerParameters>, MediationInterstitialAdapter<CustomEventExtras, CustomEventServerParameters> {
    private View zzfu;
    CustomEventBanner zzfv;
    CustomEventInterstitial zzfw;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class zza implements CustomEventBannerListener {
        private final CustomEventAdapter zzfx;
        private final MediationBannerListener zzfy;

        public zza(CustomEventAdapter customEventAdapter, MediationBannerListener mediationBannerListener) {
            this.zzfx = customEventAdapter;
            this.zzfy = mediationBannerListener;
        }

        @Override // com.google.ads.mediation.customevent.CustomEventBannerListener
        public final void onClick() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onFailedToReceiveAd.");
            this.zzfy.onClick(this.zzfx);
        }

        @Override // com.google.ads.mediation.customevent.CustomEventListener
        public final void onDismissScreen() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onFailedToReceiveAd.");
            this.zzfy.onDismissScreen(this.zzfx);
        }

        @Override // com.google.ads.mediation.customevent.CustomEventListener
        public final void onFailedToReceiveAd() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onFailedToReceiveAd.");
            this.zzfy.onFailedToReceiveAd(this.zzfx, AdRequest.ErrorCode.NO_FILL);
        }

        @Override // com.google.ads.mediation.customevent.CustomEventListener
        public final void onLeaveApplication() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onFailedToReceiveAd.");
            this.zzfy.onLeaveApplication(this.zzfx);
        }

        @Override // com.google.ads.mediation.customevent.CustomEventListener
        public final void onPresentScreen() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onFailedToReceiveAd.");
            this.zzfy.onPresentScreen(this.zzfx);
        }

        @Override // com.google.ads.mediation.customevent.CustomEventBannerListener
        public final void onReceivedAd(View view) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onReceivedAd.");
            this.zzfx.zzfu = view;
            this.zzfy.onReceivedAd(this.zzfx);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class zzb implements CustomEventInterstitialListener {
        private final CustomEventAdapter zzfx;
        private final MediationInterstitialListener zzfz;

        public zzb(CustomEventAdapter customEventAdapter, MediationInterstitialListener mediationInterstitialListener) {
            this.zzfx = customEventAdapter;
            this.zzfz = mediationInterstitialListener;
        }

        @Override // com.google.ads.mediation.customevent.CustomEventListener
        public final void onDismissScreen() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onDismissScreen.");
            this.zzfz.onDismissScreen(this.zzfx);
        }

        @Override // com.google.ads.mediation.customevent.CustomEventListener
        public final void onFailedToReceiveAd() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onFailedToReceiveAd.");
            this.zzfz.onFailedToReceiveAd(this.zzfx, AdRequest.ErrorCode.NO_FILL);
        }

        @Override // com.google.ads.mediation.customevent.CustomEventListener
        public final void onLeaveApplication() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onLeaveApplication.");
            this.zzfz.onLeaveApplication(this.zzfx);
        }

        @Override // com.google.ads.mediation.customevent.CustomEventListener
        public final void onPresentScreen() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onPresentScreen.");
            this.zzfz.onPresentScreen(this.zzfx);
        }

        @Override // com.google.ads.mediation.customevent.CustomEventInterstitialListener
        public final void onReceivedAd() {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Custom event adapter called onReceivedAd.");
            this.zzfz.onReceivedAd(CustomEventAdapter.this);
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

    @Override // com.google.ads.mediation.MediationAdapter
    public final void destroy() {
        if (this.zzfv != null) {
            this.zzfv.destroy();
        }
        if (this.zzfw != null) {
            this.zzfw.destroy();
        }
    }

    @Override // com.google.ads.mediation.MediationAdapter
    public final Class<CustomEventExtras> getAdditionalParametersType() {
        return CustomEventExtras.class;
    }

    @Override // com.google.ads.mediation.MediationBannerAdapter
    public final View getBannerView() {
        return this.zzfu;
    }

    @Override // com.google.ads.mediation.MediationAdapter
    public final Class<CustomEventServerParameters> getServerParametersType() {
        return CustomEventServerParameters.class;
    }

    @Override // com.google.ads.mediation.MediationBannerAdapter
    public final void requestBannerAd(MediationBannerListener mediationBannerListener, Activity activity, CustomEventServerParameters customEventServerParameters, AdSize adSize, MediationAdRequest mediationAdRequest, CustomEventExtras customEventExtras) {
        this.zzfv = (CustomEventBanner) zzj(customEventServerParameters.className);
        if (this.zzfv == null) {
            mediationBannerListener.onFailedToReceiveAd(this, AdRequest.ErrorCode.INTERNAL_ERROR);
        } else {
            this.zzfv.requestBannerAd(new zza(this, mediationBannerListener), activity, customEventServerParameters.label, customEventServerParameters.parameter, adSize, mediationAdRequest, customEventExtras == null ? null : customEventExtras.getExtra(customEventServerParameters.label));
        }
    }

    @Override // com.google.ads.mediation.MediationInterstitialAdapter
    public final void showInterstitial() {
        this.zzfw.showInterstitial();
    }

    @Override // com.google.ads.mediation.MediationInterstitialAdapter
    public final void requestInterstitialAd(MediationInterstitialListener mediationInterstitialListener, Activity activity, CustomEventServerParameters customEventServerParameters, MediationAdRequest mediationAdRequest, CustomEventExtras customEventExtras) {
        this.zzfw = (CustomEventInterstitial) zzj(customEventServerParameters.className);
        if (this.zzfw == null) {
            mediationInterstitialListener.onFailedToReceiveAd(this, AdRequest.ErrorCode.INTERNAL_ERROR);
        } else {
            this.zzfw.requestInterstitialAd(new zzb(this, mediationInterstitialListener), activity, customEventServerParameters.label, customEventServerParameters.parameter, mediationAdRequest, customEventExtras == null ? null : customEventExtras.getExtra(customEventServerParameters.label));
        }
    }
}
