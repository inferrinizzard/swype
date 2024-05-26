package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class zzc implements Parcelable.Creator<CheckServerAuthResult> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza$2809d959(CheckServerAuthResult checkServerAuthResult, Parcel parcel) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, checkServerAuthResult.mVersionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, checkServerAuthResult.atY);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc$62107c48(parcel, 3, checkServerAuthResult.atZ);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ CheckServerAuthResult[] newArray(int i) {
        return new CheckServerAuthResult[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ CheckServerAuthResult createFromParcel(Parcel parcel) {
        boolean z = false;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        ArrayList arrayList = null;
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
                case 3:
                    arrayList = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt, Scope.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new CheckServerAuthResult(i, z, arrayList);
    }
}
