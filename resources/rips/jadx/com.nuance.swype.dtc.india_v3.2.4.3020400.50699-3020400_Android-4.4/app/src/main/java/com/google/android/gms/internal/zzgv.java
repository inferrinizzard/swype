package com.google.android.gms.internal;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import com.google.ads.mediation.MediationAdapter;
import com.google.ads.mediation.MediationBannerAdapter;
import com.google.ads.mediation.MediationInterstitialAdapter;
import com.google.ads.mediation.MediationServerParameters;
import com.google.ads.mediation.NetworkExtras;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.internal.zzgk;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzgv<NETWORK_EXTRAS extends NetworkExtras, SERVER_PARAMETERS extends MediationServerParameters> extends zzgk.zza {
    private final MediationAdapter<NETWORK_EXTRAS, SERVER_PARAMETERS> zzbpo;
    private final NETWORK_EXTRAS zzbpp;

    public zzgv(MediationAdapter<NETWORK_EXTRAS, SERVER_PARAMETERS> mediationAdapter, NETWORK_EXTRAS network_extras) {
        this.zzbpo = mediationAdapter;
        this.zzbpp = network_extras;
    }

    private SERVER_PARAMETERS zzb$3285dd0(String str) throws RemoteException {
        HashMap hashMap;
        try {
            if (str != null) {
                JSONObject jSONObject = new JSONObject(str);
                hashMap = new HashMap(jSONObject.length());
                Iterator<String> keys = jSONObject.keys();
                while (keys.hasNext()) {
                    String next = keys.next();
                    hashMap.put(next, jSONObject.getString(next));
                }
            } else {
                hashMap = new HashMap(0);
            }
            Class<SERVER_PARAMETERS> serverParametersType = this.zzbpo.getServerParametersType();
            if (serverParametersType == null) {
                return null;
            }
            SERVER_PARAMETERS newInstance = serverParametersType.newInstance();
            newInstance.load(hashMap);
            return newInstance;
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not get MediationServerParameters.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void destroy() throws RemoteException {
        try {
            this.zzbpo.destroy();
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not destroy adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final Bundle getInterstitialAdapterInfo() {
        return new Bundle();
    }

    @Override // com.google.android.gms.internal.zzgk
    public final com.google.android.gms.dynamic.zzd getView() throws RemoteException {
        if (!(this.zzbpo instanceof MediationBannerAdapter)) {
            String valueOf = String.valueOf(this.zzbpo.getClass().getCanonicalName());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a MediationBannerAdapter: ".concat(valueOf) : new String("MediationAdapter is not a MediationBannerAdapter: "));
            throw new RemoteException();
        }
        try {
            return com.google.android.gms.dynamic.zze.zzac(((MediationBannerAdapter) this.zzbpo).getBannerView());
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not get banner view from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final boolean isInitialized() {
        return true;
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void pause() throws RemoteException {
        throw new RemoteException();
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void resume() throws RemoteException {
        throw new RemoteException();
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void showInterstitial() throws RemoteException {
        if (!(this.zzbpo instanceof MediationInterstitialAdapter)) {
            String valueOf = String.valueOf(this.zzbpo.getClass().getCanonicalName());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a MediationInterstitialAdapter: ".concat(valueOf) : new String("MediationAdapter is not a MediationInterstitialAdapter: "));
            throw new RemoteException();
        }
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Showing interstitial from adapter.");
        try {
            ((MediationInterstitialAdapter) this.zzbpo).showInterstitial();
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not show interstitial from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void showVideo() {
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(AdRequestParcel adRequestParcel, String str, String str2) {
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(com.google.android.gms.dynamic.zzd zzdVar, AdRequestParcel adRequestParcel, String str, com.google.android.gms.ads.internal.reward.mediation.client.zza zzaVar, String str2) throws RemoteException {
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(com.google.android.gms.dynamic.zzd zzdVar, AdRequestParcel adRequestParcel, String str, zzgl zzglVar) throws RemoteException {
        zza(zzdVar, adRequestParcel, str, (String) null, zzglVar);
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(com.google.android.gms.dynamic.zzd zzdVar, AdRequestParcel adRequestParcel, String str, String str2, zzgl zzglVar) throws RemoteException {
        if (!(this.zzbpo instanceof MediationInterstitialAdapter)) {
            String valueOf = String.valueOf(this.zzbpo.getClass().getCanonicalName());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a MediationInterstitialAdapter: ".concat(valueOf) : new String("MediationAdapter is not a MediationInterstitialAdapter: "));
            throw new RemoteException();
        }
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Requesting interstitial ad from adapter.");
        try {
            MediationInterstitialAdapter mediationInterstitialAdapter = (MediationInterstitialAdapter) this.zzbpo;
            zzgw zzgwVar = new zzgw(zzglVar);
            Activity activity = (Activity) com.google.android.gms.dynamic.zze.zzad(zzdVar);
            int i = adRequestParcel.zzatq;
            mediationInterstitialAdapter.requestInterstitialAd(zzgwVar, activity, zzb$3285dd0(str), zzgx.zzp(adRequestParcel), this.zzbpp);
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not request interstitial ad from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(com.google.android.gms.dynamic.zzd zzdVar, AdRequestParcel adRequestParcel, String str, String str2, zzgl zzglVar, NativeAdOptionsParcel nativeAdOptionsParcel, List<String> list) {
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(com.google.android.gms.dynamic.zzd zzdVar, AdSizeParcel adSizeParcel, AdRequestParcel adRequestParcel, String str, zzgl zzglVar) throws RemoteException {
        zza(zzdVar, adSizeParcel, adRequestParcel, str, null, zzglVar);
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zza(com.google.android.gms.dynamic.zzd zzdVar, AdSizeParcel adSizeParcel, AdRequestParcel adRequestParcel, String str, String str2, zzgl zzglVar) throws RemoteException {
        if (!(this.zzbpo instanceof MediationBannerAdapter)) {
            String valueOf = String.valueOf(this.zzbpo.getClass().getCanonicalName());
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(valueOf.length() != 0 ? "MediationAdapter is not a MediationBannerAdapter: ".concat(valueOf) : new String("MediationAdapter is not a MediationBannerAdapter: "));
            throw new RemoteException();
        }
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Requesting banner ad from adapter.");
        try {
            MediationBannerAdapter mediationBannerAdapter = (MediationBannerAdapter) this.zzbpo;
            zzgw zzgwVar = new zzgw(zzglVar);
            Activity activity = (Activity) com.google.android.gms.dynamic.zze.zzad(zzdVar);
            int i = adRequestParcel.zzatq;
            mediationBannerAdapter.requestBannerAd(zzgwVar, activity, zzb$3285dd0(str), zzgx.zzc(adSizeParcel), zzgx.zzp(adRequestParcel), this.zzbpp);
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not request banner ad from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zzc(AdRequestParcel adRequestParcel, String str) {
    }

    @Override // com.google.android.gms.internal.zzgk
    public final void zzj(com.google.android.gms.dynamic.zzd zzdVar) throws RemoteException {
    }

    @Override // com.google.android.gms.internal.zzgk
    public final zzgn zzmo() {
        return null;
    }

    @Override // com.google.android.gms.internal.zzgk
    public final zzgo zzmp() {
        return null;
    }

    @Override // com.google.android.gms.internal.zzgk
    public final Bundle zzmq() {
        return new Bundle();
    }

    @Override // com.google.android.gms.internal.zzgk
    public final Bundle zzmr() {
        return new Bundle();
    }
}
