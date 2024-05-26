package com.google.android.gms.ads.internal.request;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class zzf implements Parcelable.Creator<AdRequestInfoParcel> {
    @Override // android.os.Parcelable.Creator
    /* renamed from: zzao, reason: merged with bridge method [inline-methods] */
    public AdRequestInfoParcel[] newArray(int i) {
        return new AdRequestInfoParcel[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: zzk, reason: merged with bridge method [inline-methods] */
    public AdRequestInfoParcel createFromParcel(Parcel parcel) {
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        int i = 0;
        Bundle bundle = null;
        AdRequestParcel adRequestParcel = null;
        AdSizeParcel adSizeParcel = null;
        String str = null;
        ApplicationInfo applicationInfo = null;
        PackageInfo packageInfo = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        VersionInfoParcel versionInfoParcel = null;
        Bundle bundle2 = null;
        int i2 = 0;
        ArrayList<String> arrayList = null;
        Bundle bundle3 = null;
        boolean z = false;
        Messenger messenger = null;
        int i3 = 0;
        int i4 = 0;
        float f = 0.0f;
        String str5 = null;
        long j = 0;
        String str6 = null;
        ArrayList<String> arrayList2 = null;
        String str7 = null;
        NativeAdOptionsParcel nativeAdOptionsParcel = null;
        ArrayList<String> arrayList3 = null;
        long j2 = 0;
        CapabilityParcel capabilityParcel = null;
        String str8 = null;
        float f2 = 0.0f;
        boolean z2 = false;
        int i5 = 0;
        int i6 = 0;
        boolean z3 = false;
        boolean z4 = false;
        String str9 = null;
        String str10 = null;
        boolean z5 = false;
        int i7 = 0;
        Bundle bundle4 = null;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    bundle = com.google.android.gms.common.internal.safeparcel.zza.zzs(parcel, readInt);
                    break;
                case 3:
                    adRequestParcel = (AdRequestParcel) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, AdRequestParcel.CREATOR);
                    break;
                case 4:
                    adSizeParcel = (AdSizeParcel) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, AdSizeParcel.CREATOR);
                    break;
                case 5:
                    str = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 6:
                    applicationInfo = (ApplicationInfo) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, ApplicationInfo.CREATOR);
                    break;
                case 7:
                    packageInfo = (PackageInfo) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, PackageInfo.CREATOR);
                    break;
                case 8:
                    str2 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 9:
                    str3 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 10:
                    str4 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 11:
                    versionInfoParcel = (VersionInfoParcel) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, VersionInfoParcel.CREATOR);
                    break;
                case 12:
                    bundle2 = com.google.android.gms.common.internal.safeparcel.zza.zzs(parcel, readInt);
                    break;
                case 13:
                    i2 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 14:
                    arrayList = com.google.android.gms.common.internal.safeparcel.zza.zzae(parcel, readInt);
                    break;
                case 15:
                    bundle3 = com.google.android.gms.common.internal.safeparcel.zza.zzs(parcel, readInt);
                    break;
                case 16:
                    z = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 17:
                    messenger = (Messenger) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, Messenger.CREATOR);
                    break;
                case 18:
                    i3 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 19:
                    i4 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 20:
                    f = com.google.android.gms.common.internal.safeparcel.zza.zzl(parcel, readInt);
                    break;
                case 21:
                    str5 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 22:
                case 23:
                case 24:
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
                case 25:
                    j = com.google.android.gms.common.internal.safeparcel.zza.zzi(parcel, readInt);
                    break;
                case 26:
                    str6 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 27:
                    arrayList2 = com.google.android.gms.common.internal.safeparcel.zza.zzae(parcel, readInt);
                    break;
                case 28:
                    str7 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 29:
                    nativeAdOptionsParcel = (NativeAdOptionsParcel) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, NativeAdOptionsParcel.CREATOR);
                    break;
                case 30:
                    arrayList3 = com.google.android.gms.common.internal.safeparcel.zza.zzae(parcel, readInt);
                    break;
                case 31:
                    j2 = com.google.android.gms.common.internal.safeparcel.zza.zzi(parcel, readInt);
                    break;
                case 32:
                    capabilityParcel = (CapabilityParcel) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, CapabilityParcel.CREATOR);
                    break;
                case 33:
                    str8 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 34:
                    f2 = com.google.android.gms.common.internal.safeparcel.zza.zzl(parcel, readInt);
                    break;
                case 35:
                    i5 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 36:
                    i6 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 37:
                    z3 = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 38:
                    z4 = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 39:
                    str9 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 40:
                    z2 = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 41:
                    str10 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 42:
                    z5 = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 43:
                    i7 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 44:
                    bundle4 = com.google.android.gms.common.internal.safeparcel.zza.zzs(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new AdRequestInfoParcel(i, bundle, adRequestParcel, adSizeParcel, str, applicationInfo, packageInfo, str2, str3, str4, versionInfoParcel, bundle2, i2, arrayList, bundle3, z, messenger, i3, i4, f, str5, j, str6, arrayList2, str7, nativeAdOptionsParcel, arrayList3, j2, capabilityParcel, str8, f2, z2, i5, i6, z3, z4, str9, str10, z5, i7, bundle4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(AdRequestInfoParcel adRequestInfoParcel, Parcel parcel, int i) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, adRequestInfoParcel.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$f7bef55(parcel, 2, adRequestInfoParcel.zzcaq);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 3, adRequestInfoParcel.zzcar, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 4, adRequestInfoParcel.zzapa, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 5, adRequestInfoParcel.zzaou);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 6, adRequestInfoParcel.applicationInfo, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 7, adRequestInfoParcel.zzcas, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 8, adRequestInfoParcel.zzcat);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 9, adRequestInfoParcel.zzcau);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 10, adRequestInfoParcel.zzcav);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 11, adRequestInfoParcel.zzaow, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$f7bef55(parcel, 12, adRequestInfoParcel.zzcaw);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 13, adRequestInfoParcel.zzcax);
        com.google.android.gms.common.internal.safeparcel.zzb.zzb$62107c48(parcel, 14, adRequestInfoParcel.zzaps);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$f7bef55(parcel, 15, adRequestInfoParcel.zzcay);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 16, adRequestInfoParcel.zzcaz);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 17, adRequestInfoParcel.zzcba, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 18, adRequestInfoParcel.zzcbb);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 19, adRequestInfoParcel.zzcbc);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 20, adRequestInfoParcel.zzcbd);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 21, adRequestInfoParcel.zzcbe);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 25, adRequestInfoParcel.zzcbf);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 26, adRequestInfoParcel.zzcbg);
        com.google.android.gms.common.internal.safeparcel.zzb.zzb$62107c48(parcel, 27, adRequestInfoParcel.zzcbh);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 28, adRequestInfoParcel.zzaot);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 29, adRequestInfoParcel.zzapo, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zzb$62107c48(parcel, 30, adRequestInfoParcel.zzcbi);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 31, adRequestInfoParcel.zzcbj);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 32, adRequestInfoParcel.zzcbk, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 33, adRequestInfoParcel.zzcbl);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 34, adRequestInfoParcel.zzcbm);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 35, adRequestInfoParcel.zzcbn);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 36, adRequestInfoParcel.zzcbo);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 37, adRequestInfoParcel.zzcbp);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 38, adRequestInfoParcel.zzcbq);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 39, adRequestInfoParcel.zzcbr);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 40, adRequestInfoParcel.zzcbs);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 41, adRequestInfoParcel.zzcbt);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 42, adRequestInfoParcel.zzbnq);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 43, adRequestInfoParcel.zzcbu);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$f7bef55(parcel, 44, adRequestInfoParcel.zzcbv);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }
}
