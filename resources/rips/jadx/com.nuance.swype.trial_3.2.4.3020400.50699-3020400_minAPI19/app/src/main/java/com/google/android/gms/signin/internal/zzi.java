package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public final class zzi implements Parcelable.Creator<SignInResponse> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(SignInResponse signInResponse, Parcel parcel, int i) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, signInResponse.mVersionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 2, signInResponse.rF, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 3, signInResponse.aue, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ SignInResponse[] newArray(int i) {
        return new SignInResponse[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ SignInResponse createFromParcel(Parcel parcel) {
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        ConnectionResult connectionResult = null;
        int i = 0;
        ResolveAccountResponse resolveAccountResponse = null;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    connectionResult = (ConnectionResult) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, ConnectionResult.CREATOR);
                    break;
                case 3:
                    resolveAccountResponse = (ResolveAccountResponse) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, ResolveAccountResponse.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new SignInResponse(i, connectionResult, resolveAccountResponse);
    }
}
