package com.google.android.gms.ads.internal.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.FrameLayout;
import com.facebook.internal.NativeProtocol;
import com.google.android.gms.ads.internal.client.zzx;
import com.google.android.gms.internal.zzdt;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.internal.zzgj;
import com.google.android.gms.internal.zzhh;
import com.google.android.gms.internal.zzhi;
import com.google.android.gms.internal.zzhp;
import com.google.android.gms.internal.zzhu;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class zzl {
    private final Object zzail = new Object();
    private zzx zzauz;
    private final zze zzava;
    private final zzd zzavb;
    private final zzai zzavc;
    private final zzef zzavd;
    private final com.google.android.gms.ads.internal.reward.client.zzf zzave;
    private final zzhu zzavf;
    private final zzhh zzavg;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public abstract class zza<T> {
        private zza() {
        }

        /* synthetic */ zza(zzl zzlVar, byte b) {
            this();
        }

        protected abstract T zzb(zzx zzxVar) throws RemoteException;

        protected abstract T zzin();

        protected final T zziu() {
            zzx zzil = zzl.this.zzil();
            if (zzil == null) {
                com.google.android.gms.ads.internal.util.client.zzb.zzcx("ClientApi class cannot be loaded.");
                return null;
            }
            try {
                return zzb(zzil);
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Cannot invoke local loader using ClientApi class", e);
                return null;
            }
        }
    }

    public zzl(zze zzeVar, zzd zzdVar, zzai zzaiVar, zzef zzefVar, com.google.android.gms.ads.internal.reward.client.zzf zzfVar, zzhu zzhuVar, zzhh zzhhVar) {
        this.zzava = zzeVar;
        this.zzavb = zzdVar;
        this.zzavc = zzaiVar;
        this.zzavd = zzefVar;
        this.zzave = zzfVar;
        this.zzavf = zzhuVar;
        this.zzavg = zzhhVar;
    }

    private static <T> T zza(Context context, boolean z, zza<T> zzaVar) {
        if (!z && !zzm.zziw().zzar(context)) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Google Play Services is not available");
            z = true;
        }
        if (z) {
            T zziu = zzaVar.zziu();
            return zziu == null ? zzaVar.zzin() : zziu;
        }
        T zzin = zzaVar.zzin();
        return zzin == null ? zzaVar.zziu() : zzin;
    }

    private static boolean zza(Activity activity, String str) {
        Intent intent = activity.getIntent();
        if (intent.hasExtra(str)) {
            return intent.getBooleanExtra(str, false);
        }
        com.google.android.gms.ads.internal.util.client.zzb.e("useClientJar flag not found in activity intent extras.");
        return false;
    }

    private static zzx zzik() {
        zzx asInterface;
        try {
            Object newInstance = zzl.class.getClassLoader().loadClass("com.google.android.gms.ads.internal.ClientApi").newInstance();
            if (newInstance instanceof IBinder) {
                asInterface = zzx.zza.asInterface((IBinder) newInstance);
            } else {
                com.google.android.gms.ads.internal.util.client.zzb.zzcx("ClientApi class is not an instance of IBinder");
                asInterface = null;
            }
            return asInterface;
        } catch (Exception e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to instantiate ClientApi class.", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public zzx zzil() {
        zzx zzxVar;
        synchronized (this.zzail) {
            if (this.zzauz == null) {
                this.zzauz = zzik();
            }
            zzxVar = this.zzauz;
        }
        return zzxVar;
    }

    public zzu zza(final Context context, final AdSizeParcel adSizeParcel, final String str) {
        return (zzu) zza(context, false, (zza) new zza<zzu>() { // from class: com.google.android.gms.ads.internal.client.zzl.2
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(zzl.this, (byte) 0);
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzu zzin() {
                zzu zza2 = zzl.this.zzava.zza(context, adSizeParcel, str, null, 3);
                if (zza2 != null) {
                    return zza2;
                }
                zzl.zza$69ae8221(context, "search");
                return new zzak();
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzu zzb(zzx zzxVar) throws RemoteException {
                return zzxVar.createSearchAdManager(com.google.android.gms.dynamic.zze.zzac(context), adSizeParcel, str, com.google.android.gms.common.internal.zze.xM);
            }
        });
    }

    public zzu zza(final Context context, final AdSizeParcel adSizeParcel, final String str, final zzgj zzgjVar) {
        return (zzu) zza(context, false, (zza) new zza<zzu>() { // from class: com.google.android.gms.ads.internal.client.zzl.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(zzl.this, (byte) 0);
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzu zzin() {
                zzu zza2 = zzl.this.zzava.zza(context, adSizeParcel, str, zzgjVar, 1);
                if (zza2 != null) {
                    return zza2;
                }
                zzl.zza$69ae8221(context, "banner");
                return new zzak();
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzu zzb(zzx zzxVar) throws RemoteException {
                return zzxVar.createBannerAdManager(com.google.android.gms.dynamic.zze.zzac(context), adSizeParcel, str, zzgjVar, com.google.android.gms.common.internal.zze.xM);
            }
        });
    }

    public com.google.android.gms.ads.internal.reward.client.zzb zza(final Context context, final zzgj zzgjVar) {
        return (com.google.android.gms.ads.internal.reward.client.zzb) zza(context, false, (zza) new zza<com.google.android.gms.ads.internal.reward.client.zzb>() { // from class: com.google.android.gms.ads.internal.client.zzl.7
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(zzl.this, (byte) 0);
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ com.google.android.gms.ads.internal.reward.client.zzb zzin() {
                com.google.android.gms.ads.internal.reward.client.zzb zzb = zzl.this.zzave.zzb(context, zzgjVar);
                if (zzb != null) {
                    return zzb;
                }
                zzl.zza$69ae8221(context, "rewarded_video");
                return new zzan();
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ com.google.android.gms.ads.internal.reward.client.zzb zzb(zzx zzxVar) throws RemoteException {
                return zzxVar.createRewardedVideoAd(com.google.android.gms.dynamic.zze.zzac(context), zzgjVar, com.google.android.gms.common.internal.zze.xM);
            }
        });
    }

    public zzdt zza(final Context context, final FrameLayout frameLayout, final FrameLayout frameLayout2) {
        return (zzdt) zza(context, false, (zza) new zza<zzdt>() { // from class: com.google.android.gms.ads.internal.client.zzl.6
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(zzl.this, (byte) 0);
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzdt zzin() {
                zzdt zzb = zzl.this.zzavd.zzb(context, frameLayout, frameLayout2);
                if (zzb != null) {
                    return zzb;
                }
                zzl.zza$69ae8221(context, "native_ad_view_delegate");
                return new zzam();
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzdt zzb(zzx zzxVar) throws RemoteException {
                return zzxVar.createNativeAdViewDelegate(com.google.android.gms.dynamic.zze.zzac(frameLayout), com.google.android.gms.dynamic.zze.zzac(frameLayout2));
            }
        });
    }

    public zzs zzb(final Context context, final String str, final zzgj zzgjVar) {
        return (zzs) zza(context, false, (zza) new zza<zzs>() { // from class: com.google.android.gms.ads.internal.client.zzl.4
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(zzl.this, (byte) 0);
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzs zzin() {
                zzs zza2 = zzl.this.zzavb.zza(context, str, zzgjVar);
                if (zza2 != null) {
                    return zza2;
                }
                zzl.zza$69ae8221(context, "native_ad");
                return new zzaj();
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzs zzb(zzx zzxVar) throws RemoteException {
                return zzxVar.createAdLoaderBuilder(com.google.android.gms.dynamic.zze.zzac(context), str, zzgjVar, com.google.android.gms.common.internal.zze.xM);
            }
        });
    }

    public zzu zzb(final Context context, final AdSizeParcel adSizeParcel, final String str, final zzgj zzgjVar) {
        return (zzu) zza(context, false, (zza) new zza<zzu>() { // from class: com.google.android.gms.ads.internal.client.zzl.3
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(zzl.this, (byte) 0);
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzu zzin() {
                zzu zza2 = zzl.this.zzava.zza(context, adSizeParcel, str, zzgjVar, 2);
                if (zza2 != null) {
                    return zza2;
                }
                zzl.zza$69ae8221(context, "interstitial");
                return new zzak();
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzu zzb(zzx zzxVar) throws RemoteException {
                return zzxVar.createInterstitialAdManager(com.google.android.gms.dynamic.zze.zzac(context), adSizeParcel, str, zzgjVar, com.google.android.gms.common.internal.zze.xM);
            }
        });
    }

    public zzhp zzb(final Activity activity) {
        return (zzhp) zza(activity, zza(activity, "com.google.android.gms.ads.internal.purchase.useClientJar"), new zza<zzhp>() { // from class: com.google.android.gms.ads.internal.client.zzl.8
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(zzl.this, (byte) 0);
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzhp zzin() {
                zzhp zzg = zzl.this.zzavf.zzg(activity);
                if (zzg != null) {
                    return zzg;
                }
                zzl.zza$69ae8221(activity, "iap");
                return null;
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzhp zzb(zzx zzxVar) throws RemoteException {
                return zzxVar.createInAppPurchaseManager(com.google.android.gms.dynamic.zze.zzac(activity));
            }
        });
    }

    public zzhi zzc(final Activity activity) {
        return (zzhi) zza(activity, zza(activity, "com.google.android.gms.ads.internal.overlay.useClientJar"), new zza<zzhi>() { // from class: com.google.android.gms.ads.internal.client.zzl.9
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(zzl.this, (byte) 0);
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzhi zzin() {
                zzhi zzf = zzl.this.zzavg.zzf(activity);
                if (zzf != null) {
                    return zzf;
                }
                zzl.zza$69ae8221(activity, "ad_overlay");
                return null;
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzhi zzb(zzx zzxVar) throws RemoteException {
                return zzxVar.createAdOverlay(com.google.android.gms.dynamic.zze.zzac(activity));
            }
        });
    }

    public zzz zzl(final Context context) {
        return (zzz) zza(context, false, (zza) new zza<zzz>() { // from class: com.google.android.gms.ads.internal.client.zzl.5
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(zzl.this, (byte) 0);
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzz zzin() {
                zzz zzm = zzl.this.zzavc.zzm(context);
                if (zzm != null) {
                    return zzm;
                }
                zzl.zza$69ae8221(context, "mobile_ads_settings");
                return new zzal();
            }

            @Override // com.google.android.gms.ads.internal.client.zzl.zza
            public final /* synthetic */ zzz zzb(zzx zzxVar) throws RemoteException {
                return zzxVar.getMobileAdsSettingsManagerWithClientJarVersion(com.google.android.gms.dynamic.zze.zzac(context), com.google.android.gms.common.internal.zze.xM);
            }
        });
    }

    static /* synthetic */ void zza$69ae8221(Context context, String str) {
        Bundle bundle = new Bundle();
        bundle.putString(NativeProtocol.WEB_DIALOG_ACTION, "no_ads_fallback");
        bundle.putString("flow", str);
        zzm.zziw().zza(context, (String) null, "gmob-apps", bundle, true);
    }
}
