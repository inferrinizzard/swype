package com.google.android.gms.ads.internal.overlay;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.ads.internal.InterstitialAdParameterParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public class zzf implements Parcelable.Creator<AdOverlayInfoParcel> {
    @Override // android.os.Parcelable.Creator
    /* renamed from: zzag, reason: merged with bridge method [inline-methods] */
    public AdOverlayInfoParcel[] newArray(int i) {
        return new AdOverlayInfoParcel[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: zzi, reason: merged with bridge method [inline-methods] */
    public AdOverlayInfoParcel createFromParcel(Parcel parcel) {
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        int i = 0;
        AdLauncherIntentInfoParcel adLauncherIntentInfoParcel = null;
        IBinder iBinder = null;
        IBinder iBinder2 = null;
        IBinder iBinder3 = null;
        IBinder iBinder4 = null;
        String str = null;
        boolean z = false;
        String str2 = null;
        IBinder iBinder5 = null;
        int i2 = 0;
        int i3 = 0;
        String str3 = null;
        VersionInfoParcel versionInfoParcel = null;
        IBinder iBinder6 = null;
        String str4 = null;
        InterstitialAdParameterParcel interstitialAdParameterParcel = null;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    adLauncherIntentInfoParcel = (AdLauncherIntentInfoParcel) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, AdLauncherIntentInfoParcel.CREATOR);
                    break;
                case 3:
                    iBinder = com.google.android.gms.common.internal.safeparcel.zza.zzr(parcel, readInt);
                    break;
                case 4:
                    iBinder2 = com.google.android.gms.common.internal.safeparcel.zza.zzr(parcel, readInt);
                    break;
                case 5:
                    iBinder3 = com.google.android.gms.common.internal.safeparcel.zza.zzr(parcel, readInt);
                    break;
                case 6:
                    iBinder4 = com.google.android.gms.common.internal.safeparcel.zza.zzr(parcel, readInt);
                    break;
                case 7:
                    str = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 8:
                    z = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 9:
                    str2 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 10:
                    iBinder5 = com.google.android.gms.common.internal.safeparcel.zza.zzr(parcel, readInt);
                    break;
                case 11:
                    i2 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 12:
                    i3 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 13:
                    str3 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 14:
                    versionInfoParcel = (VersionInfoParcel) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, VersionInfoParcel.CREATOR);
                    break;
                case 15:
                    iBinder6 = com.google.android.gms.common.internal.safeparcel.zza.zzr(parcel, readInt);
                    break;
                case 16:
                    str4 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 17:
                    interstitialAdParameterParcel = (InterstitialAdParameterParcel) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, InterstitialAdParameterParcel.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new AdOverlayInfoParcel(i, adLauncherIntentInfoParcel, iBinder, iBinder2, iBinder3, iBinder4, str, z, str2, iBinder5, i2, i3, str3, versionInfoParcel, iBinder6, str4, interstitialAdParameterParcel);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(AdOverlayInfoParcel adOverlayInfoParcel, Parcel parcel, int i) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, adOverlayInfoParcel.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 2, adOverlayInfoParcel.zzbtj, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$cdac282(parcel, 3, com.google.android.gms.dynamic.zze.zzac(adOverlayInfoParcel.zzbtk).asBinder());
        com.google.android.gms.common.internal.safeparcel.zzb.zza$cdac282(parcel, 4, com.google.android.gms.dynamic.zze.zzac(adOverlayInfoParcel.zzbtl).asBinder());
        com.google.android.gms.common.internal.safeparcel.zzb.zza$cdac282(parcel, 5, com.google.android.gms.dynamic.zze.zzac(adOverlayInfoParcel.zzbtm).asBinder());
        com.google.android.gms.common.internal.safeparcel.zzb.zza$cdac282(parcel, 6, com.google.android.gms.dynamic.zze.zzac(adOverlayInfoParcel.zzbtn).asBinder());
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 7, adOverlayInfoParcel.zzbto);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 8, adOverlayInfoParcel.zzbtp);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 9, adOverlayInfoParcel.zzbtq);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$cdac282(parcel, 10, com.google.android.gms.dynamic.zze.zzac(adOverlayInfoParcel.zzbtr).asBinder());
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 11, adOverlayInfoParcel.orientation);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 12, adOverlayInfoParcel.zzbts);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 13, adOverlayInfoParcel.url);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 14, adOverlayInfoParcel.zzaow, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$cdac282(parcel, 15, com.google.android.gms.dynamic.zze.zzac(adOverlayInfoParcel.zzbtt).asBinder());
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 16, adOverlayInfoParcel.zzbtu);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 17, adOverlayInfoParcel.zzbtv, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }
}
