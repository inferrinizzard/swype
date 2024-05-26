package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.text.TextUtils;
import com.google.android.gms.ads.internal.overlay.AdLauncherIntentInfoParcel;
import com.google.android.gms.ads.internal.overlay.AdOverlayInfoParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.MediationInterstitialAdapter;
import com.google.android.gms.ads.mediation.MediationInterstitialListener;
import com.google.android.gms.internal.zzdq;

@zzin
/* loaded from: classes.dex */
public final class zzgy implements MediationInterstitialAdapter {
    private Uri mUri;
    private Activity zzbpu;
    private zzdq zzbpv;
    private MediationInterstitialListener zzbpw;

    @Override // com.google.android.gms.ads.mediation.MediationAdapter
    public final void onDestroy() {
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Destroying AdMobCustomTabsAdapter adapter.");
        try {
            this.zzbpv.zzd(this.zzbpu);
        } catch (Exception e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzb("Exception while unbinding from CustomTabsService.", e);
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdapter
    public final void onPause() {
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Pausing AdMobCustomTabsAdapter adapter.");
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdapter
    public final void onResume() {
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Resuming AdMobCustomTabsAdapter adapter.");
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialAdapter
    public final void requestInterstitialAd(Context context, MediationInterstitialListener mediationInterstitialListener, Bundle bundle, MediationAdRequest mediationAdRequest, Bundle bundle2) {
        this.zzbpw = mediationInterstitialListener;
        if (this.zzbpw == null) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("Listener not set for mediation. Returning.");
            return;
        }
        if (!(context instanceof Activity)) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("AdMobCustomTabs can only work with Activity context. Bailing out.");
            this.zzbpw.onAdFailedToLoad(this, 0);
            return;
        }
        if (!zzdq.zzo(context)) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("Default browser does not support custom tabs. Bailing out.");
            this.zzbpw.onAdFailedToLoad(this, 0);
            return;
        }
        String string = bundle.getString("tab_url");
        if (TextUtils.isEmpty(string)) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("The tab_url retrieved from mediation metadata is empty. Bailing out.");
            this.zzbpw.onAdFailedToLoad(this, 0);
            return;
        }
        this.zzbpu = (Activity) context;
        this.mUri = Uri.parse(string);
        this.zzbpv = new zzdq();
        this.zzbpv.zzber = new zzdq.zza() { // from class: com.google.android.gms.internal.zzgy.1
            @Override // com.google.android.gms.internal.zzdq.zza
            public final void zzkn() {
                com.google.android.gms.ads.internal.util.client.zzb.zzcv("Hinting CustomTabsService for the load of the new url.");
            }

            @Override // com.google.android.gms.internal.zzdq.zza
            public final void zzko() {
                com.google.android.gms.ads.internal.util.client.zzb.zzcv("Disconnecting from CustomTabs service.");
            }
        };
        this.zzbpv.zze(this.zzbpu);
        this.zzbpw.onAdLoaded(this);
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialAdapter
    public final void showInterstitial() {
        CustomTabsIntent build = new CustomTabsIntent.Builder(this.zzbpv.zzkl()).build();
        build.intent.setData(this.mUri);
        final AdOverlayInfoParcel adOverlayInfoParcel = new AdOverlayInfoParcel(new AdLauncherIntentInfoParcel(build.intent), null, new com.google.android.gms.ads.internal.overlay.zzg() { // from class: com.google.android.gms.internal.zzgy.2
            @Override // com.google.android.gms.ads.internal.overlay.zzg
            public final void onPause() {
                com.google.android.gms.ads.internal.util.client.zzb.zzcv("AdMobCustomTabsAdapter overlay is paused.");
            }

            @Override // com.google.android.gms.ads.internal.overlay.zzg
            public final void onResume() {
                com.google.android.gms.ads.internal.util.client.zzb.zzcv("AdMobCustomTabsAdapter overlay is resumed.");
            }

            @Override // com.google.android.gms.ads.internal.overlay.zzg
            public final void zzdx() {
                com.google.android.gms.ads.internal.util.client.zzb.zzcv("AdMobCustomTabsAdapter overlay is closed.");
                zzgy.this.zzbpw.onAdClosed(zzgy.this);
                zzgy.this.zzbpv.zzd(zzgy.this.zzbpu);
            }

            @Override // com.google.android.gms.ads.internal.overlay.zzg
            public final void zzdy() {
                com.google.android.gms.ads.internal.util.client.zzb.zzcv("Opening AdMobCustomTabsAdapter overlay.");
                zzgy.this.zzbpw.onAdOpened(zzgy.this);
            }
        }, null, new VersionInfoParcel(0, 0, false));
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzgy.3
            @Override // java.lang.Runnable
            public final void run() {
                com.google.android.gms.ads.internal.zzu.zzfo().zza(zzgy.this.zzbpu, adOverlayInfoParcel);
            }
        });
        com.google.android.gms.ads.internal.zzu.zzft().zzcjz = false;
    }
}
