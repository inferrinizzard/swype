package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public final class zzad implements Parcelable.Creator<ResolveAccountResponse> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(ResolveAccountResponse resolveAccountResponse, Parcel parcel, int i) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, resolveAccountResponse.mVersionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$cdac282(parcel, 2, resolveAccountResponse.xj);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 3, resolveAccountResponse.rF, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 4, resolveAccountResponse.tL);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 5, resolveAccountResponse.zj);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ ResolveAccountResponse[] newArray(int i) {
        return new ResolveAccountResponse[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ ResolveAccountResponse createFromParcel(Parcel parcel) {
        ConnectionResult connectionResult = null;
        boolean z = false;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        boolean z2 = false;
        IBinder iBinder = null;
        int i = 0;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    iBinder = com.google.android.gms.common.internal.safeparcel.zza.zzr(parcel, readInt);
                    break;
                case 3:
                    connectionResult = (ConnectionResult) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, ConnectionResult.CREATOR);
                    break;
                case 4:
                    z2 = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
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
        return new ResolveAccountResponse(i, iBinder, connectionResult, z2, z);
    }
}
