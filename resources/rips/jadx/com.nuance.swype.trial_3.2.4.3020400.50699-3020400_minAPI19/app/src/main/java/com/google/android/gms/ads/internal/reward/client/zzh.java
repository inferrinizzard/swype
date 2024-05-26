package com.google.android.gms.ads.internal.reward.client;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public class zzh implements Parcelable.Creator<RewardedVideoAdRequestParcel> {
    @Override // android.os.Parcelable.Creator
    /* renamed from: zzav, reason: merged with bridge method [inline-methods] */
    public RewardedVideoAdRequestParcel[] newArray(int i) {
        return new RewardedVideoAdRequestParcel[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: zzq, reason: merged with bridge method [inline-methods] */
    public RewardedVideoAdRequestParcel createFromParcel(Parcel parcel) {
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        AdRequestParcel adRequestParcel = null;
        int i = 0;
        String str = null;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    adRequestParcel = (AdRequestParcel) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, AdRequestParcel.CREATOR);
                    break;
                case 3:
                    str = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new RewardedVideoAdRequestParcel(i, adRequestParcel, str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(RewardedVideoAdRequestParcel rewardedVideoAdRequestParcel, Parcel parcel, int i) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, rewardedVideoAdRequestParcel.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 2, rewardedVideoAdRequestParcel.zzcar, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 3, rewardedVideoAdRequestParcel.zzaou);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }
}
