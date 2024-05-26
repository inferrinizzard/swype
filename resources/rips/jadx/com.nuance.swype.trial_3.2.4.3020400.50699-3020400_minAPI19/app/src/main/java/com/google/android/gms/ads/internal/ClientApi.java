package com.google.android.gms.ads.internal;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.Keep;
import android.widget.FrameLayout;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.client.zzx;
import com.google.android.gms.ads.internal.client.zzz;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.internal.zzdt;
import com.google.android.gms.internal.zzgj;
import com.google.android.gms.internal.zzhi;
import com.google.android.gms.internal.zzhp;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzje;

@Keep
@DynamiteApi
@zzin
/* loaded from: classes.dex */
public class ClientApi extends zzx.zza {
    @Override // com.google.android.gms.ads.internal.client.zzx
    public com.google.android.gms.ads.internal.client.zzs createAdLoaderBuilder(com.google.android.gms.dynamic.zzd zzdVar, String str, zzgj zzgjVar, int i) {
        return new zzk((Context) com.google.android.gms.dynamic.zze.zzad(zzdVar), str, zzgjVar, new VersionInfoParcel(com.google.android.gms.common.internal.zze.xM, i, true), zzd.zzek());
    }

    @Override // com.google.android.gms.ads.internal.client.zzx
    public zzhi createAdOverlay(com.google.android.gms.dynamic.zzd zzdVar) {
        return new com.google.android.gms.ads.internal.overlay.zzd((Activity) com.google.android.gms.dynamic.zze.zzad(zzdVar));
    }

    @Override // com.google.android.gms.ads.internal.client.zzx
    public com.google.android.gms.ads.internal.client.zzu createBannerAdManager(com.google.android.gms.dynamic.zzd zzdVar, AdSizeParcel adSizeParcel, String str, zzgj zzgjVar, int i) throws RemoteException {
        return new zzf((Context) com.google.android.gms.dynamic.zze.zzad(zzdVar), adSizeParcel, str, zzgjVar, new VersionInfoParcel(com.google.android.gms.common.internal.zze.xM, i, true), zzd.zzek());
    }

    @Override // com.google.android.gms.ads.internal.client.zzx
    public zzhp createInAppPurchaseManager(com.google.android.gms.dynamic.zzd zzdVar) {
        return new com.google.android.gms.ads.internal.purchase.zze((Activity) com.google.android.gms.dynamic.zze.zzad(zzdVar));
    }

    @Override // com.google.android.gms.ads.internal.client.zzx
    public zzdt createNativeAdViewDelegate(com.google.android.gms.dynamic.zzd zzdVar, com.google.android.gms.dynamic.zzd zzdVar2) {
        return new com.google.android.gms.ads.internal.formats.zzk((FrameLayout) com.google.android.gms.dynamic.zze.zzad(zzdVar), (FrameLayout) com.google.android.gms.dynamic.zze.zzad(zzdVar2));
    }

    @Override // com.google.android.gms.ads.internal.client.zzx
    public com.google.android.gms.ads.internal.reward.client.zzb createRewardedVideoAd(com.google.android.gms.dynamic.zzd zzdVar, zzgj zzgjVar, int i) {
        return new zzje((Context) com.google.android.gms.dynamic.zze.zzad(zzdVar), zzd.zzek(), zzgjVar, new VersionInfoParcel(com.google.android.gms.common.internal.zze.xM, i, true));
    }

    @Override // com.google.android.gms.ads.internal.client.zzx
    public com.google.android.gms.ads.internal.client.zzu createSearchAdManager(com.google.android.gms.dynamic.zzd zzdVar, AdSizeParcel adSizeParcel, String str, int i) throws RemoteException {
        return new zzt((Context) com.google.android.gms.dynamic.zze.zzad(zzdVar), adSizeParcel, str, new VersionInfoParcel(com.google.android.gms.common.internal.zze.xM, i, true));
    }

    @Override // com.google.android.gms.ads.internal.client.zzx
    public zzz getMobileAdsSettingsManager(com.google.android.gms.dynamic.zzd zzdVar) {
        return null;
    }

    @Override // com.google.android.gms.ads.internal.client.zzx
    public zzz getMobileAdsSettingsManagerWithClientJarVersion(com.google.android.gms.dynamic.zzd zzdVar, int i) {
        return zzo.zza((Context) com.google.android.gms.dynamic.zze.zzad(zzdVar), new VersionInfoParcel(com.google.android.gms.common.internal.zze.xM, i, true));
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0043, code lost:            if (((java.lang.Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(com.google.android.gms.internal.zzdc.zzbaf)).booleanValue() != false) goto L9;     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x002f, code lost:            if (((java.lang.Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(com.google.android.gms.internal.zzdc.zzbae)).booleanValue() == false) goto L6;     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x0045, code lost:            r1 = true;     */
    @Override // com.google.android.gms.ads.internal.client.zzx
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.google.android.gms.ads.internal.client.zzu createInterstitialAdManager(com.google.android.gms.dynamic.zzd r14, com.google.android.gms.ads.internal.client.AdSizeParcel r15, java.lang.String r16, com.google.android.gms.internal.zzgj r17, int r18) throws android.os.RemoteException {
        /*
            r13 = this;
            java.lang.Object r2 = com.google.android.gms.dynamic.zze.zzad(r14)
            android.content.Context r2 = (android.content.Context) r2
            com.google.android.gms.internal.zzdc.initialize(r2)
            com.google.android.gms.ads.internal.util.client.VersionInfoParcel r5 = new com.google.android.gms.ads.internal.util.client.VersionInfoParcel
            r1 = 9452000(0x9039e0, float:1.3245073E-38)
            r3 = 1
            r0 = r18
            r5.<init>(r1, r0, r3)
            java.lang.String r1 = "reward_mb"
            java.lang.String r3 = r15.zzaur
            boolean r3 = r1.equals(r3)
            if (r3 != 0) goto L31
            com.google.android.gms.internal.zzcy<java.lang.Boolean> r1 = com.google.android.gms.internal.zzdc.zzbae
            com.google.android.gms.internal.zzdb r4 = com.google.android.gms.ads.internal.zzu.zzfz()
            java.lang.Object r1 = r4.zzd(r1)
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 != 0) goto L45
        L31:
            if (r3 == 0) goto L56
            com.google.android.gms.internal.zzcy<java.lang.Boolean> r1 = com.google.android.gms.internal.zzdc.zzbaf
            com.google.android.gms.internal.zzdb r3 = com.google.android.gms.ads.internal.zzu.zzfz()
            java.lang.Object r1 = r3.zzd(r1)
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L56
        L45:
            r1 = 1
        L46:
            if (r1 == 0) goto L58
            com.google.android.gms.internal.zzfn r1 = new com.google.android.gms.internal.zzfn
            com.google.android.gms.ads.internal.zzd r6 = com.google.android.gms.ads.internal.zzd.zzek()
            r3 = r16
            r4 = r17
            r1.<init>(r2, r3, r4, r5, r6)
        L55:
            return r1
        L56:
            r1 = 0
            goto L46
        L58:
            com.google.android.gms.ads.internal.zzl r6 = new com.google.android.gms.ads.internal.zzl
            com.google.android.gms.ads.internal.zzd r12 = com.google.android.gms.ads.internal.zzd.zzek()
            r7 = r2
            r8 = r15
            r9 = r16
            r10 = r17
            r11 = r5
            r6.<init>(r7, r8, r9, r10, r11, r12)
            r1 = r6
            goto L55
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.ads.internal.ClientApi.createInterstitialAdManager(com.google.android.gms.dynamic.zzd, com.google.android.gms.ads.internal.client.AdSizeParcel, java.lang.String, com.google.android.gms.internal.zzgj, int):com.google.android.gms.ads.internal.client.zzu");
    }
}
