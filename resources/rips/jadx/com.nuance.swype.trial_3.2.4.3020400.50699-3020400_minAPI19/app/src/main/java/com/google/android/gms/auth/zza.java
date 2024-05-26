package com.google.android.gms.auth;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public final class zza implements Parcelable.Creator<AccountChangeEvent> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza$119e69c0(AccountChangeEvent accountChangeEvent, Parcel parcel) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, accountChangeEvent.mVersion);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, accountChangeEvent.ca);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 3, accountChangeEvent.cb);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 4, accountChangeEvent.cc);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 5, accountChangeEvent.cd);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 6, accountChangeEvent.ce);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ AccountChangeEvent[] newArray(int i) {
        return new AccountChangeEvent[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ AccountChangeEvent createFromParcel(Parcel parcel) {
        String str = null;
        int i = 0;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        long j = 0;
        int i2 = 0;
        String str2 = null;
        int i3 = 0;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i3 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    j = com.google.android.gms.common.internal.safeparcel.zza.zzi(parcel, readInt);
                    break;
                case 3:
                    str2 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 4:
                    i2 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 5:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 6:
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
        return new AccountChangeEvent(i3, j, str2, i2, i, str);
    }
}
