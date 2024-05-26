package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.ResolveAccountRequest;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public final class zzh implements Parcelable.Creator<SignInRequest> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(SignInRequest signInRequest, Parcel parcel, int i) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, signInRequest.mVersionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 2, signInRequest.aud, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ SignInRequest[] newArray(int i) {
        return new SignInRequest[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ SignInRequest createFromParcel(Parcel parcel) {
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        int i = 0;
        ResolveAccountRequest resolveAccountRequest = null;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    resolveAccountRequest = (ResolveAccountRequest) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, ResolveAccountRequest.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new SignInRequest(i, resolveAccountRequest);
    }
}
