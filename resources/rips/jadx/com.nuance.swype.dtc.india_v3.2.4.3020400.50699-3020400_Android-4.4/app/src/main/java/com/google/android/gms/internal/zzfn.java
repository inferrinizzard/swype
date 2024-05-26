package com.google.android.gms.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.client.VideoOptionsParcel;
import com.google.android.gms.ads.internal.client.zzu;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzfn extends zzu.zza {
    private String zzaln;
    private zzfh zzbkq;
    private com.google.android.gms.ads.internal.zzl zzbkv;
    private zzfj zzblc;
    private zzhs zzbld;
    private String zzble;

    public zzfn(Context context, String str, zzgj zzgjVar, VersionInfoParcel versionInfoParcel, com.google.android.gms.ads.internal.zzd zzdVar) {
        this(str, new zzfh(context, zzgjVar, versionInfoParcel, zzdVar));
    }

    private void zzlw() {
        if (this.zzbkv == null || this.zzbld == null) {
            return;
        }
        this.zzbkv.zza(this.zzbld, this.zzble);
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void destroy() throws RemoteException {
        if (this.zzbkv != null) {
            this.zzbkv.destroy();
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final String getMediationAdapterClassName() throws RemoteException {
        if (this.zzbkv != null) {
            return this.zzbkv.getMediationAdapterClassName();
        }
        return null;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final boolean isLoading() throws RemoteException {
        return this.zzbkv != null && this.zzbkv.isLoading();
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final boolean isReady() throws RemoteException {
        return this.zzbkv != null && this.zzbkv.isReady();
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void pause() throws RemoteException {
        if (this.zzbkv != null) {
            this.zzbkv.pause();
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void resume() throws RemoteException {
        if (this.zzbkv != null) {
            this.zzbkv.resume();
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void setManualImpressionsEnabled(boolean z) throws RemoteException {
        abort();
        if (this.zzbkv != null) {
            this.zzbkv.setManualImpressionsEnabled(z);
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void setUserId(String str) {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void showInterstitial() throws RemoteException {
        if (this.zzbkv != null) {
            this.zzbkv.showInterstitial();
        } else {
            zzkd.zzcx("Interstitial ad must be loaded before showInterstitial().");
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void stopLoading() throws RemoteException {
        if (this.zzbkv != null) {
            this.zzbkv.stopLoading();
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void zza(AdSizeParcel adSizeParcel) throws RemoteException {
        if (this.zzbkv != null) {
            this.zzbkv.zza(adSizeParcel);
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void zza(VideoOptionsParcel videoOptionsParcel) {
        throw new IllegalStateException("getVideoController not implemented for interstitials");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void zza(com.google.android.gms.ads.internal.client.zzp zzpVar) throws RemoteException {
        this.zzblc.zzbkk = zzpVar;
        if (this.zzbkv != null) {
            this.zzblc.zzc(this.zzbkv);
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void zza(com.google.android.gms.ads.internal.client.zzq zzqVar) throws RemoteException {
        this.zzblc.zzalf = zzqVar;
        if (this.zzbkv != null) {
            this.zzblc.zzc(this.zzbkv);
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void zza(com.google.android.gms.ads.internal.client.zzw zzwVar) throws RemoteException {
        this.zzblc.zzbkh = zzwVar;
        if (this.zzbkv != null) {
            this.zzblc.zzc(this.zzbkv);
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void zza(com.google.android.gms.ads.internal.client.zzy zzyVar) throws RemoteException {
        abort();
        if (this.zzbkv != null) {
            this.zzbkv.zza(zzyVar);
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void zza(com.google.android.gms.ads.internal.reward.client.zzd zzdVar) {
        this.zzblc.zzbkl = zzdVar;
        if (this.zzbkv != null) {
            this.zzblc.zzc(this.zzbkv);
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void zza(zzdo zzdoVar) throws RemoteException {
        this.zzblc.zzbkj = zzdoVar;
        if (this.zzbkv != null) {
            this.zzblc.zzc(this.zzbkv);
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void zza(zzho zzhoVar) throws RemoteException {
        this.zzblc.zzbki = zzhoVar;
        if (this.zzbkv != null) {
            this.zzblc.zzc(this.zzbkv);
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void zza(zzhs zzhsVar, String str) throws RemoteException {
        this.zzbld = zzhsVar;
        this.zzble = str;
        zzlw();
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final com.google.android.gms.dynamic.zzd zzdm() throws RemoteException {
        if (this.zzbkv != null) {
            return this.zzbkv.zzdm();
        }
        return null;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final AdSizeParcel zzdn() throws RemoteException {
        if (this.zzbkv != null) {
            return this.zzbkv.zzdn();
        }
        return null;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final void zzdp() throws RemoteException {
        if (this.zzbkv != null) {
            this.zzbkv.zzdp();
        } else {
            zzkd.zzcx("Interstitial ad must be loaded before pingManualTrackingUrl().");
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public final com.google.android.gms.ads.internal.client.zzab zzdq() {
        throw new IllegalStateException("getVideoController not implemented for interstitials");
    }

    private zzfn(String str, zzfh zzfhVar) {
        this.zzaln = str;
        this.zzbkq = zzfhVar;
        this.zzblc = new zzfj();
        zzfk zzgb = com.google.android.gms.ads.internal.zzu.zzgb();
        if (zzgb.zzbkq == null) {
            zzgb.zzbkq = new zzfh(zzfhVar.mContext.getApplicationContext(), zzfhVar.zzajz, zzfhVar.zzalo, zzfhVar.zzajv);
            if (zzgb.zzbkq != null) {
                SharedPreferences sharedPreferences = zzgb.zzbkq.mContext.getApplicationContext().getSharedPreferences("com.google.android.gms.ads.internal.interstitial.InterstitialAdPool", 0);
                while (zzgb.zzbkp.size() > 0) {
                    zzfl remove = zzgb.zzbkp.remove();
                    zzfm zzfmVar = zzgb.zzbko.get(remove);
                    zzfk.zza("Flushing interstitial queue for %s.", remove);
                    while (zzfmVar.zzbkr.size() > 0) {
                        zzfmVar.zzm(null).zzbkv.zzeu();
                    }
                    zzgb.zzbko.remove(remove);
                }
                HashMap hashMap = new HashMap();
                for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
                    try {
                        if (!entry.getKey().equals("PoolKeys")) {
                            zzfo zzfoVar = new zzfo((String) entry.getValue());
                            zzfl zzflVar = new zzfl(zzfoVar.zzanc, zzfoVar.zzaln, zzfoVar.zzbkt);
                            if (!zzgb.zzbko.containsKey(zzflVar)) {
                                zzgb.zzbko.put(zzflVar, new zzfm(zzfoVar.zzanc, zzfoVar.zzaln, zzfoVar.zzbkt));
                                hashMap.put(zzflVar.toString(), zzflVar);
                                zzfk.zza("Restored interstitial queue for %s.", zzflVar);
                            }
                        }
                    } catch (IOException | ClassCastException e) {
                        zzkd.zzd("Malformed preferences value for InterstitialAdPool.", e);
                    }
                }
                for (String str2 : zzfk.zzbe(sharedPreferences.getString("PoolKeys", ""))) {
                    zzfl zzflVar2 = (zzfl) hashMap.get(str2);
                    if (zzgb.zzbko.containsKey(zzflVar2)) {
                        zzgb.zzbkp.add(zzflVar2);
                    }
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:65:0x01ad  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x01d3  */
    @Override // com.google.android.gms.ads.internal.client.zzu
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean zzb(com.google.android.gms.ads.internal.client.AdRequestParcel r13) throws android.os.RemoteException {
        /*
            Method dump skipped, instructions count: 481
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzfn.zzb(com.google.android.gms.ads.internal.client.AdRequestParcel):boolean");
    }

    private void abort() {
        if (this.zzbkv != null) {
            return;
        }
        zzfh zzfhVar = this.zzbkq;
        this.zzbkv = new com.google.android.gms.ads.internal.zzl(zzfhVar.mContext, new AdSizeParcel(), this.zzaln, zzfhVar.zzajz, zzfhVar.zzalo, zzfhVar.zzajv);
        this.zzblc.zzc(this.zzbkv);
        zzlw();
    }
}
