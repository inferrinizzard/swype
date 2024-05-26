package com.google.android.gms.ads.internal.util.client;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public class zzd implements Parcelable.Creator<VersionInfoParcel> {
    @Override // android.os.Parcelable.Creator
    /* renamed from: zzba, reason: merged with bridge method [inline-methods] */
    public VersionInfoParcel[] newArray(int i) {
        return new VersionInfoParcel[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: zzs, reason: merged with bridge method [inline-methods] */
    public VersionInfoParcel createFromParcel(Parcel parcel) {
        boolean z = false;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        String str = null;
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i3 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    str = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 3:
                    i2 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 4:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 5:
                    z = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new VersionInfoParcel(i3, str, i2, i, z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza$1ad68b2d(VersionInfoParcel versionInfoParcel, Parcel parcel) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, versionInfoParcel.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 2, versionInfoParcel.zzcs);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 3, versionInfoParcel.zzcnk);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 4, versionInfoParcel.zzcnl);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 5, versionInfoParcel.zzcnm);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }
}
