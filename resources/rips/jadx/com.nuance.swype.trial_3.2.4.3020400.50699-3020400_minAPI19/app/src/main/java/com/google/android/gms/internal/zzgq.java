package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.ads.mediation.MediationAdapter;
import com.google.android.gms.ads.mediation.MediationBannerAdapter;
import com.google.android.gms.ads.mediation.MediationInterstitialAdapter;
import com.google.android.gms.ads.mediation.MediationNativeAdapter;
import com.google.android.gms.ads.mediation.NativeAdMapper;
import com.google.android.gms.ads.mediation.NativeAppInstallAdMapper;
import com.google.android.gms.ads.mediation.NativeContentAdMapper;
import com.google.android.gms.ads.mediation.OnContextChangedListener;
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter;
import com.google.android.gms.internal.zzgk;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzgq extends zzgk.zza {
    private final MediationAdapter zzbpi;
    private zzgr zzbpj;

    public zzgq(MediationAdapter mediationAdapter) {
        this.zzbpi = mediationAdapter;
    }

    private Bundle zza(String str, int i, String str2) throws RemoteException {
        String valueOf = String.valueOf(str);
        com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "Server parameters: ".concat(valueOf) : new String("Server parameters: "));
        try {
            Bundle bundle = new Bundle();
            if (str != null) {
                JSONObject jSONObject = new JSONObject(str);
                Bundle bundle2 = new Bundle();
                Iterator<String> keys = jSONObject.keys();
                while (keys.hasNext()) {
                    String next = keys.next();
                    bundle2.putString(next, jSONObject.getString(next));
                }
                bundle = bundle2;
            }
            if (this.zzbpi instanceof AdMobAdapter) {
                bundle.putString("adJson", str2);
                bundle.putInt("tagForChildDirectedTreatment", i);
            }
            return bundle;
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not get Server Parameters Bundle.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void destroy() throws RemoteException {
        try {
            this.zzbpi.onDestroy();
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not destroy adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final Bundle getInterstitialAdapterInfo() {
        if (this.zzbpi instanceof zzlt) {
            return ((zzlt) this.zzbpi).getInterstitialAdapterInfo();
        }
        String valueOf = String.valueOf(this.zzbpi.getClass().getCanonicalName());
        com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a v2 MediationInterstitialAdapter: ".concat(valueOf) : new String("MediationAdapter is not a v2 MediationInterstitialAdapter: "));
        return new Bundle();
    }

    @Override // com.google.android.gms.internal.zzgk
    public final com.google.android.gms.dynamic.zzd getView() throws RemoteException {
        if (!(this.zzbpi instanceof MediationBannerAdapter)) {
            String valueOf = String.valueOf(this.zzbpi.getClass().getCanonicalName());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a MediationBannerAdapter: ".concat(valueOf) : new String("MediationAdapter is not a MediationBannerAdapter: "));
            throw new RemoteException();
        }
        try {
            return com.google.android.gms.dynamic.zze.zzac(((MediationBannerAdapter) this.zzbpi).getBannerView());
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not get banner view from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final boolean isInitialized() throws RemoteException {
        if (!(this.zzbpi instanceof MediationRewardedVideoAdAdapter)) {
            String valueOf = String.valueOf(this.zzbpi.getClass().getCanonicalName());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a MediationRewardedVideoAdAdapter: ".concat(valueOf) : new String("MediationAdapter is not a MediationRewardedVideoAdAdapter: "));
            throw new RemoteException();
        }
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Check if adapter is initialized.");
        try {
            return ((MediationRewardedVideoAdAdapter) this.zzbpi).isInitialized();
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not check if adapter is initialized.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void pause() throws RemoteException {
        try {
            this.zzbpi.onPause();
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not pause adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void resume() throws RemoteException {
        try {
            this.zzbpi.onResume();
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not resume adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void showInterstitial() throws RemoteException {
        if (!(this.zzbpi instanceof MediationInterstitialAdapter)) {
            String valueOf = String.valueOf(this.zzbpi.getClass().getCanonicalName());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a MediationInterstitialAdapter: ".concat(valueOf) : new String("MediationAdapter is not a MediationInterstitialAdapter: "));
            throw new RemoteException();
        }
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Showing interstitial from adapter.");
        try {
            ((MediationInterstitialAdapter) this.zzbpi).showInterstitial();
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not show interstitial from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void showVideo() throws RemoteException {
        if (!(this.zzbpi instanceof MediationRewardedVideoAdAdapter)) {
            String valueOf = String.valueOf(this.zzbpi.getClass().getCanonicalName());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a MediationRewardedVideoAdAdapter: ".concat(valueOf) : new String("MediationAdapter is not a MediationRewardedVideoAdAdapter: "));
            throw new RemoteException();
        }
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Show rewarded video ad from adapter.");
        try {
            ((MediationRewardedVideoAdAdapter) this.zzbpi).showVideo();
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not show rewarded video ad from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(AdRequestParcel adRequestParcel, String str, String str2) throws RemoteException {
        if (!(this.zzbpi instanceof MediationRewardedVideoAdAdapter)) {
            String valueOf = String.valueOf(this.zzbpi.getClass().getCanonicalName());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a MediationRewardedVideoAdAdapter: ".concat(valueOf) : new String("MediationAdapter is not a MediationRewardedVideoAdAdapter: "));
            throw new RemoteException();
        }
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Requesting rewarded video ad from adapter.");
        try {
            MediationRewardedVideoAdAdapter mediationRewardedVideoAdAdapter = (MediationRewardedVideoAdAdapter) this.zzbpi;
            mediationRewardedVideoAdAdapter.loadAd(new zzgp(adRequestParcel.zzatm == -1 ? null : new Date(adRequestParcel.zzatm), adRequestParcel.zzatn, adRequestParcel.zzato != null ? new HashSet(adRequestParcel.zzato) : null, adRequestParcel.zzatu, adRequestParcel.zzatp, adRequestParcel.zzatq, adRequestParcel.zzaub), zza(str, adRequestParcel.zzatq, str2), adRequestParcel.zzatw != null ? adRequestParcel.zzatw.getBundle(mediationRewardedVideoAdAdapter.getClass().getName()) : null);
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not load rewarded video ad from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(com.google.android.gms.dynamic.zzd zzdVar, AdRequestParcel adRequestParcel, String str, com.google.android.gms.ads.internal.reward.mediation.client.zza zzaVar, String str2) throws RemoteException {
        if (!(this.zzbpi instanceof MediationRewardedVideoAdAdapter)) {
            String valueOf = String.valueOf(this.zzbpi.getClass().getCanonicalName());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a MediationRewardedVideoAdAdapter: ".concat(valueOf) : new String("MediationAdapter is not a MediationRewardedVideoAdAdapter: "));
            throw new RemoteException();
        }
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Initialize rewarded video adapter.");
        try {
            MediationRewardedVideoAdAdapter mediationRewardedVideoAdAdapter = (MediationRewardedVideoAdAdapter) this.zzbpi;
            mediationRewardedVideoAdAdapter.initialize((Context) com.google.android.gms.dynamic.zze.zzad(zzdVar), new zzgp(adRequestParcel.zzatm == -1 ? null : new Date(adRequestParcel.zzatm), adRequestParcel.zzatn, adRequestParcel.zzato != null ? new HashSet(adRequestParcel.zzato) : null, adRequestParcel.zzatu, adRequestParcel.zzatp, adRequestParcel.zzatq, adRequestParcel.zzaub), str, new com.google.android.gms.ads.internal.reward.mediation.client.zzb(zzaVar), zza(str2, adRequestParcel.zzatq, (String) null), adRequestParcel.zzatw != null ? adRequestParcel.zzatw.getBundle(mediationRewardedVideoAdAdapter.getClass().getName()) : null);
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not initialize rewarded video adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(com.google.android.gms.dynamic.zzd zzdVar, AdRequestParcel adRequestParcel, String str, zzgl zzglVar) throws RemoteException {
        zza(zzdVar, adRequestParcel, str, (String) null, zzglVar);
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(com.google.android.gms.dynamic.zzd zzdVar, AdRequestParcel adRequestParcel, String str, String str2, zzgl zzglVar) throws RemoteException {
        if (!(this.zzbpi instanceof MediationInterstitialAdapter)) {
            String valueOf = String.valueOf(this.zzbpi.getClass().getCanonicalName());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a MediationInterstitialAdapter: ".concat(valueOf) : new String("MediationAdapter is not a MediationInterstitialAdapter: "));
            throw new RemoteException();
        }
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Requesting interstitial ad from adapter.");
        try {
            MediationInterstitialAdapter mediationInterstitialAdapter = (MediationInterstitialAdapter) this.zzbpi;
            mediationInterstitialAdapter.requestInterstitialAd((Context) com.google.android.gms.dynamic.zze.zzad(zzdVar), new zzgr(zzglVar), zza(str, adRequestParcel.zzatq, str2), new zzgp(adRequestParcel.zzatm == -1 ? null : new Date(adRequestParcel.zzatm), adRequestParcel.zzatn, adRequestParcel.zzato != null ? new HashSet(adRequestParcel.zzato) : null, adRequestParcel.zzatu, adRequestParcel.zzatp, adRequestParcel.zzatq, adRequestParcel.zzaub), adRequestParcel.zzatw != null ? adRequestParcel.zzatw.getBundle(mediationInterstitialAdapter.getClass().getName()) : null);
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not request interstitial ad from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(com.google.android.gms.dynamic.zzd zzdVar, AdRequestParcel adRequestParcel, String str, String str2, zzgl zzglVar, NativeAdOptionsParcel nativeAdOptionsParcel, List<String> list) throws RemoteException {
        if (!(this.zzbpi instanceof MediationNativeAdapter)) {
            String valueOf = String.valueOf(this.zzbpi.getClass().getCanonicalName());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a MediationNativeAdapter: ".concat(valueOf) : new String("MediationAdapter is not a MediationNativeAdapter: "));
            throw new RemoteException();
        }
        try {
            MediationNativeAdapter mediationNativeAdapter = (MediationNativeAdapter) this.zzbpi;
            zzgu zzguVar = new zzgu(adRequestParcel.zzatm == -1 ? null : new Date(adRequestParcel.zzatm), adRequestParcel.zzatn, adRequestParcel.zzato != null ? new HashSet(adRequestParcel.zzato) : null, adRequestParcel.zzatu, adRequestParcel.zzatp, adRequestParcel.zzatq, nativeAdOptionsParcel, list, adRequestParcel.zzaub);
            Bundle bundle = adRequestParcel.zzatw != null ? adRequestParcel.zzatw.getBundle(mediationNativeAdapter.getClass().getName()) : null;
            this.zzbpj = new zzgr(zzglVar);
            mediationNativeAdapter.requestNativeAd((Context) com.google.android.gms.dynamic.zze.zzad(zzdVar), this.zzbpj, zza(str, adRequestParcel.zzatq, str2), zzguVar, bundle);
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not request native ad from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(com.google.android.gms.dynamic.zzd zzdVar, AdSizeParcel adSizeParcel, AdRequestParcel adRequestParcel, String str, zzgl zzglVar) throws RemoteException {
        zza(zzdVar, adSizeParcel, adRequestParcel, str, null, zzglVar);
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(com.google.android.gms.dynamic.zzd zzdVar, AdSizeParcel adSizeParcel, AdRequestParcel adRequestParcel, String str, String str2, zzgl zzglVar) throws RemoteException {
        if (!(this.zzbpi instanceof MediationBannerAdapter)) {
            String valueOf = String.valueOf(this.zzbpi.getClass().getCanonicalName());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a MediationBannerAdapter: ".concat(valueOf) : new String("MediationAdapter is not a MediationBannerAdapter: "));
            throw new RemoteException();
        }
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Requesting banner ad from adapter.");
        try {
            MediationBannerAdapter mediationBannerAdapter = (MediationBannerAdapter) this.zzbpi;
            mediationBannerAdapter.requestBannerAd((Context) com.google.android.gms.dynamic.zze.zzad(zzdVar), new zzgr(zzglVar), zza(str, adRequestParcel.zzatq, str2), com.google.android.gms.ads.zza.zza(adSizeParcel.width, adSizeParcel.height, adSizeParcel.zzaur), new zzgp(adRequestParcel.zzatm == -1 ? null : new Date(adRequestParcel.zzatm), adRequestParcel.zzatn, adRequestParcel.zzato != null ? new HashSet(adRequestParcel.zzato) : null, adRequestParcel.zzatu, adRequestParcel.zzatp, adRequestParcel.zzatq, adRequestParcel.zzaub), adRequestParcel.zzatw != null ? adRequestParcel.zzatw.getBundle(mediationBannerAdapter.getClass().getName()) : null);
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not request banner ad from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zzc(AdRequestParcel adRequestParcel, String str) throws RemoteException {
        zza(adRequestParcel, str, (String) null);
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zzj(com.google.android.gms.dynamic.zzd zzdVar) throws RemoteException {
        try {
            ((OnContextChangedListener) this.zzbpi).onContextChanged((Context) com.google.android.gms.dynamic.zze.zzad(zzdVar));
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zza("Could not inform adapter of changed context", th);
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final Bundle zzmq() {
        if (this.zzbpi instanceof zzls) {
            return ((zzls) this.zzbpi).zzmq();
        }
        String valueOf = String.valueOf(this.zzbpi.getClass().getCanonicalName());
        com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a v2 MediationBannerAdapter: ".concat(valueOf) : new String("MediationAdapter is not a v2 MediationBannerAdapter: "));
        return new Bundle();
    }

    @Override // com.google.android.gms.internal.zzgk
    public final Bundle zzmr() {
        return new Bundle();
    }

    @Override // com.google.android.gms.internal.zzgk
    public final zzgn zzmo() {
        NativeAdMapper nativeAdMapper = this.zzbpj.zzbpl;
        if (nativeAdMapper instanceof NativeAppInstallAdMapper) {
            return new zzgs((NativeAppInstallAdMapper) nativeAdMapper);
        }
        return null;
    }

    @Override // com.google.android.gms.internal.zzgk
    public final zzgo zzmp() {
        NativeAdMapper nativeAdMapper = this.zzbpj.zzbpl;
        if (nativeAdMapper instanceof NativeContentAdMapper) {
            return new zzgt((NativeContentAdMapper) nativeAdMapper);
        }
        return null;
    }
}
