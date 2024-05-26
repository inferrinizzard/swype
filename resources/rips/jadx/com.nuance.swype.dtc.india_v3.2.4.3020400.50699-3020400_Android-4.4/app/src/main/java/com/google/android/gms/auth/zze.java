package com.google.android.gms.auth;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class zze implements Parcelable.Creator<TokenData> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza$6d52043c(TokenData tokenData, Parcel parcel) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, tokenData.mVersionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 2, tokenData.co);
        Long l = tokenData.cp;
        if (l != null) {
            com.google.android.gms.common.internal.safeparcel.zzb.zzb(parcel, 3, 8);
            parcel.writeLong(l.longValue());
        }
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 4, tokenData.cq);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 5, tokenData.cr);
        com.google.android.gms.common.internal.safeparcel.zzb.zzb$62107c48(parcel, 6, tokenData.cs);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ TokenData[] newArray(int i) {
        return new TokenData[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ TokenData createFromParcel(Parcel parcel) {
        boolean z = false;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        ArrayList<String> arrayList = null;
        boolean z2 = false;
        Long l = null;
        String str = null;
        int i = 0;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    str = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 3:
                    int zza = com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt);
                    if (zza != 0) {
                        com.google.android.gms.common.internal.safeparcel.zza.zza$ae3cd4b(parcel, zza, 8);
                        l = Long.valueOf(parcel.readLong());
                        break;
                    } else {
                        l = null;
                        break;
                    }
                case 4:
                    z2 = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 5:
                    z = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 6:
                    arrayList = com.google.android.gms.common.internal.safeparcel.zza.zzae(parcel, readInt);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new TokenData(i, str, l, z2, z, arrayList);
    }
}
