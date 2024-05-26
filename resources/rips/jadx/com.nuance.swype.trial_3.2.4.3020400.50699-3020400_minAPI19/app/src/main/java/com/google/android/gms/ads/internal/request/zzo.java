package com.google.android.gms.ads.internal.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public class zzo implements Parcelable.Creator<StringParcel> {
    @Override // android.os.Parcelable.Creator
    /* renamed from: zzat, reason: merged with bridge method [inline-methods] */
    public StringParcel[] newArray(int i) {
        return new StringParcel[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: zzp, reason: merged with bridge method [inline-methods] */
    public StringParcel createFromParcel(Parcel parcel) {
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        int i = 0;
        String str = null;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
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
        return new StringParcel(i, str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza$7244a929(StringParcel stringParcel, Parcel parcel) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, stringParcel.mVersionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 2, stringParcel.zzbem);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }
}
