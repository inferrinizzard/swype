package com.google.android.gms.ads.internal.overlay;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.ads.internal.InterstitialAdParameterParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.internal.zzel;
import com.google.android.gms.internal.zzer;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzlh;

@zzin
/* loaded from: classes.dex */
public final class AdOverlayInfoParcel extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final zzf CREATOR = new zzf();
    public final int orientation;
    public final String url;
    public final int versionCode;
    public final VersionInfoParcel zzaow;
    public final AdLauncherIntentInfoParcel zzbtj;
    public final com.google.android.gms.ads.internal.client.zza zzbtk;
    public final zzg zzbtl;
    public final zzlh zzbtm;
    public final zzel zzbtn;
    public final String zzbto;
    public final boolean zzbtp;
    public final String zzbtq;
    public final zzp zzbtr;
    public final int zzbts;
    public final zzer zzbtt;
    public final String zzbtu;
    public final InterstitialAdParameterParcel zzbtv;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdOverlayInfoParcel(int i, AdLauncherIntentInfoParcel adLauncherIntentInfoParcel, IBinder iBinder, IBinder iBinder2, IBinder iBinder3, IBinder iBinder4, String str, boolean z, String str2, IBinder iBinder5, int i2, int i3, String str3, VersionInfoParcel versionInfoParcel, IBinder iBinder6, String str4, InterstitialAdParameterParcel interstitialAdParameterParcel) {
        this.versionCode = i;
        this.zzbtj = adLauncherIntentInfoParcel;
        this.zzbtk = (com.google.android.gms.ads.internal.client.zza) com.google.android.gms.dynamic.zze.zzad(zzd.zza.zzfc(iBinder));
        this.zzbtl = (zzg) com.google.android.gms.dynamic.zze.zzad(zzd.zza.zzfc(iBinder2));
        this.zzbtm = (zzlh) com.google.android.gms.dynamic.zze.zzad(zzd.zza.zzfc(iBinder3));
        this.zzbtn = (zzel) com.google.android.gms.dynamic.zze.zzad(zzd.zza.zzfc(iBinder4));
        this.zzbto = str;
        this.zzbtp = z;
        this.zzbtq = str2;
        this.zzbtr = (zzp) com.google.android.gms.dynamic.zze.zzad(zzd.zza.zzfc(iBinder5));
        this.orientation = i2;
        this.zzbts = i3;
        this.url = str3;
        this.zzaow = versionInfoParcel;
        this.zzbtt = (zzer) com.google.android.gms.dynamic.zze.zzad(zzd.zza.zzfc(iBinder6));
        this.zzbtu = str4;
        this.zzbtv = interstitialAdParameterParcel;
    }

    public AdOverlayInfoParcel(com.google.android.gms.ads.internal.client.zza zzaVar, zzg zzgVar, zzp zzpVar, zzlh zzlhVar, int i, VersionInfoParcel versionInfoParcel, String str, InterstitialAdParameterParcel interstitialAdParameterParcel) {
        this.versionCode = 4;
        this.zzbtj = null;
        this.zzbtk = zzaVar;
        this.zzbtl = zzgVar;
        this.zzbtm = zzlhVar;
        this.zzbtn = null;
        this.zzbto = null;
        this.zzbtp = false;
        this.zzbtq = null;
        this.zzbtr = zzpVar;
        this.orientation = i;
        this.zzbts = 1;
        this.url = null;
        this.zzaow = versionInfoParcel;
        this.zzbtt = null;
        this.zzbtu = str;
        this.zzbtv = interstitialAdParameterParcel;
    }

    public AdOverlayInfoParcel(com.google.android.gms.ads.internal.client.zza zzaVar, zzg zzgVar, zzp zzpVar, zzlh zzlhVar, boolean z, int i, VersionInfoParcel versionInfoParcel) {
        this.versionCode = 4;
        this.zzbtj = null;
        this.zzbtk = zzaVar;
        this.zzbtl = zzgVar;
        this.zzbtm = zzlhVar;
        this.zzbtn = null;
        this.zzbto = null;
        this.zzbtp = z;
        this.zzbtq = null;
        this.zzbtr = zzpVar;
        this.orientation = i;
        this.zzbts = 2;
        this.url = null;
        this.zzaow = versionInfoParcel;
        this.zzbtt = null;
        this.zzbtu = null;
        this.zzbtv = null;
    }

    public AdOverlayInfoParcel(com.google.android.gms.ads.internal.client.zza zzaVar, zzg zzgVar, zzel zzelVar, zzp zzpVar, zzlh zzlhVar, boolean z, int i, String str, VersionInfoParcel versionInfoParcel, zzer zzerVar) {
        this.versionCode = 4;
        this.zzbtj = null;
        this.zzbtk = zzaVar;
        this.zzbtl = zzgVar;
        this.zzbtm = zzlhVar;
        this.zzbtn = zzelVar;
        this.zzbto = null;
        this.zzbtp = z;
        this.zzbtq = null;
        this.zzbtr = zzpVar;
        this.orientation = i;
        this.zzbts = 3;
        this.url = str;
        this.zzaow = versionInfoParcel;
        this.zzbtt = zzerVar;
        this.zzbtu = null;
        this.zzbtv = null;
    }

    public AdOverlayInfoParcel(com.google.android.gms.ads.internal.client.zza zzaVar, zzg zzgVar, zzel zzelVar, zzp zzpVar, zzlh zzlhVar, boolean z, int i, String str, String str2, VersionInfoParcel versionInfoParcel, zzer zzerVar) {
        this.versionCode = 4;
        this.zzbtj = null;
        this.zzbtk = zzaVar;
        this.zzbtl = zzgVar;
        this.zzbtm = zzlhVar;
        this.zzbtn = zzelVar;
        this.zzbto = str2;
        this.zzbtp = z;
        this.zzbtq = str;
        this.zzbtr = zzpVar;
        this.orientation = i;
        this.zzbts = 3;
        this.url = null;
        this.zzaow = versionInfoParcel;
        this.zzbtt = zzerVar;
        this.zzbtu = null;
        this.zzbtv = null;
    }

    public AdOverlayInfoParcel(AdLauncherIntentInfoParcel adLauncherIntentInfoParcel, com.google.android.gms.ads.internal.client.zza zzaVar, zzg zzgVar, zzp zzpVar, VersionInfoParcel versionInfoParcel) {
        this.versionCode = 4;
        this.zzbtj = adLauncherIntentInfoParcel;
        this.zzbtk = zzaVar;
        this.zzbtl = zzgVar;
        this.zzbtm = null;
        this.zzbtn = null;
        this.zzbto = null;
        this.zzbtp = false;
        this.zzbtq = null;
        this.zzbtr = zzpVar;
        this.orientation = -1;
        this.zzbts = 4;
        this.url = null;
        this.zzaow = versionInfoParcel;
        this.zzbtt = null;
        this.zzbtu = null;
        this.zzbtv = null;
    }

    public static void zza(Intent intent, AdOverlayInfoParcel adOverlayInfoParcel) {
        Bundle bundle = new Bundle(1);
        bundle.putParcelable("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo", adOverlayInfoParcel);
        intent.putExtra("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo", bundle);
    }

    public static AdOverlayInfoParcel zzb(Intent intent) {
        try {
            Bundle bundleExtra = intent.getBundleExtra("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo");
            bundleExtra.setClassLoader(AdOverlayInfoParcel.class.getClassLoader());
            return (AdOverlayInfoParcel) bundleExtra.getParcelable("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo");
        } catch (Exception e) {
            return null;
        }
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        zzf.zza(this, parcel, i);
    }
}
