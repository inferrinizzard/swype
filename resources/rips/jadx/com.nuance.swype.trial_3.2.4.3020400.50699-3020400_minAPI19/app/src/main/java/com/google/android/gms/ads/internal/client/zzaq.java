package com.google.android.gms.ads.internal.client;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public class zzaq implements Parcelable.Creator<VideoOptionsParcel> {
    @Override // android.os.Parcelable.Creator
    /* renamed from: zzv, reason: merged with bridge method [inline-methods] */
    public VideoOptionsParcel[] newArray(int i) {
        return new VideoOptionsParcel[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: zzf, reason: merged with bridge method [inline-methods] */
    public VideoOptionsParcel createFromParcel(Parcel parcel) {
        boolean z = false;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        int i = 0;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
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
        return new VideoOptionsParcel(i, z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza$5d05cac5(VideoOptionsParcel videoOptionsParcel, Parcel parcel) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, videoOptionsParcel.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, videoOptionsParcel.zzaxm);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }
}
