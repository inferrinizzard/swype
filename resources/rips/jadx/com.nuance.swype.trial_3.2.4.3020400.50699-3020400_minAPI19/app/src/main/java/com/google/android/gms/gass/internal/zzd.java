package com.google.android.gms.gass.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.internal.zzapv;

/* loaded from: classes.dex */
public final class zzd implements Parcelable.Creator<GassResponseParcel> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza$765441b7(GassResponseParcel gassResponseParcel, Parcel parcel) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, gassResponseParcel.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$52910762(parcel, 2, gassResponseParcel.YY != null ? gassResponseParcel.YY : zzapv.zzf(gassResponseParcel.YX));
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ GassResponseParcel[] newArray(int i) {
        return new GassResponseParcel[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ GassResponseParcel createFromParcel(Parcel parcel) {
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        int i = 0;
        byte[] bArr = null;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    bArr = com.google.android.gms.common.internal.safeparcel.zza.zzt(parcel, readInt);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new GassResponseParcel(i, bArr);
    }
}
