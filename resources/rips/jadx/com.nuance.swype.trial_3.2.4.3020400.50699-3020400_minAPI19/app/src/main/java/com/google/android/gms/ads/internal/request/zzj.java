package com.google.android.gms.ads.internal.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public class zzj implements Parcelable.Creator<CapabilityParcel> {
    @Override // android.os.Parcelable.Creator
    /* renamed from: zzar, reason: merged with bridge method [inline-methods] */
    public CapabilityParcel[] newArray(int i) {
        return new CapabilityParcel[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: zzn, reason: merged with bridge method [inline-methods] */
    public CapabilityParcel createFromParcel(Parcel parcel) {
        boolean z = false;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        boolean z2 = false;
        boolean z3 = false;
        int i = 0;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    z3 = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 3:
                    z2 = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 4:
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
        return new CapabilityParcel(i, z3, z2, z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza$364ade50(CapabilityParcel capabilityParcel, Parcel parcel) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, capabilityParcel.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, capabilityParcel.zzccw);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 3, capabilityParcel.zzccx);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 4, capabilityParcel.zzccy);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }
}
