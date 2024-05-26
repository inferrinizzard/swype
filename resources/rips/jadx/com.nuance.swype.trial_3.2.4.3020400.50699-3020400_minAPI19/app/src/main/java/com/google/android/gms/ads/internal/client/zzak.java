package com.google.android.gms.ads.internal.client;

import android.os.RemoteException;
import com.google.android.gms.ads.internal.client.zzu;
import com.google.android.gms.internal.zzdo;
import com.google.android.gms.internal.zzho;
import com.google.android.gms.internal.zzhs;

/* loaded from: classes.dex */
public class zzak extends zzu.zza {
    private zzq zzalf;

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void destroy() {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public String getMediationAdapterClassName() {
        return null;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public boolean isLoading() {
        return false;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public boolean isReady() {
        return false;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void pause() {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void resume() {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void setManualImpressionsEnabled(boolean z) {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void setUserId(String str) {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void showInterstitial() {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void stopLoading() {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(AdSizeParcel adSizeParcel) {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(VideoOptionsParcel videoOptionsParcel) {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzp zzpVar) {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzq zzqVar) {
        this.zzalf = zzqVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzw zzwVar) {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzy zzyVar) {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(com.google.android.gms.ads.internal.reward.client.zzd zzdVar) {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzdo zzdoVar) {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzho zzhoVar) {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzhs zzhsVar, String str) {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public boolean zzb(AdRequestParcel adRequestParcel) {
        com.google.android.gms.ads.internal.util.client.zzb.e("This app is using a lightweight version of the Google Mobile Ads SDK that requires the latest Google Play services to be installed, but Google Play services is either missing or out of date.");
        com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.ads.internal.client.zzak.1
            @Override // java.lang.Runnable
            public final void run() {
                if (zzak.this.zzalf != null) {
                    try {
                        zzak.this.zzalf.onAdFailedToLoad(1);
                    } catch (RemoteException e) {
                        com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not notify onAdFailedToLoad event.", e);
                    }
                }
            }
        });
        return false;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public com.google.android.gms.dynamic.zzd zzdm() {
        return null;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public AdSizeParcel zzdn() {
        return null;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zzdp() {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public zzab zzdq() {
        return null;
    }
}
