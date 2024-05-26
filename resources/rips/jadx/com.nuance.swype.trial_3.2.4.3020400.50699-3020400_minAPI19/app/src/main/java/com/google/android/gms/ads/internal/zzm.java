package com.google.android.gms.ads.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public class zzm implements Parcelable.Creator<InterstitialAdParameterParcel> {
    @Override // android.os.Parcelable.Creator
    /* renamed from: zzi, reason: merged with bridge method [inline-methods] */
    public InterstitialAdParameterParcel[] newArray(int i) {
        return new InterstitialAdParameterParcel[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: zzb, reason: merged with bridge method [inline-methods] */
    public InterstitialAdParameterParcel createFromParcel(Parcel parcel) {
        int i = 0;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        String str = null;
        float f = 0.0f;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        int i2 = 0;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i2 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    z3 = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 3:
                    z2 = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 4:
                    str = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 5:
                    z = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 6:
                    f = com.google.android.gms.common.internal.safeparcel.zza.zzl(parcel, readInt);
                    break;
                case 7:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new InterstitialAdParameterParcel(i2, z3, z2, str, z, f, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza$2350bab8(InterstitialAdParameterParcel interstitialAdParameterParcel, Parcel parcel) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, interstitialAdParameterParcel.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, interstitialAdParameterParcel.zzame);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 3, interstitialAdParameterParcel.zzamf);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 4, interstitialAdParameterParcel.zzamg);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 5, interstitialAdParameterParcel.zzamh);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 6, interstitialAdParameterParcel.zzami);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 7, interstitialAdParameterParcel.zzamj);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }
}
