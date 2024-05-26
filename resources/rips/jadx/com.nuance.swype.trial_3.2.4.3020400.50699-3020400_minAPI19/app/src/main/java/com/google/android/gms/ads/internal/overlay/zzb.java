package com.google.android.gms.ads.internal.overlay;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public class zzb implements Parcelable.Creator<AdLauncherIntentInfoParcel> {
    @Override // android.os.Parcelable.Creator
    /* renamed from: zzac, reason: merged with bridge method [inline-methods] */
    public AdLauncherIntentInfoParcel[] newArray(int i) {
        return new AdLauncherIntentInfoParcel[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: zzh, reason: merged with bridge method [inline-methods] */
    public AdLauncherIntentInfoParcel createFromParcel(Parcel parcel) {
        Intent intent = null;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    str7 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 3:
                    str6 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 4:
                    str5 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 5:
                    str4 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 6:
                    str3 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 7:
                    str2 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 8:
                    str = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 9:
                    intent = (Intent) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, Intent.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new AdLauncherIntentInfoParcel(i, str7, str6, str5, str4, str3, str2, str, intent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(AdLauncherIntentInfoParcel adLauncherIntentInfoParcel, Parcel parcel, int i) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, adLauncherIntentInfoParcel.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 2, adLauncherIntentInfoParcel.zzbrn);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 3, adLauncherIntentInfoParcel.url);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 4, adLauncherIntentInfoParcel.mimeType);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 5, adLauncherIntentInfoParcel.packageName);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 6, adLauncherIntentInfoParcel.zzbro);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 7, adLauncherIntentInfoParcel.zzbrp);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 8, adLauncherIntentInfoParcel.zzbrq);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 9, adLauncherIntentInfoParcel.intent, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }
}
