package com.google.android.gms.auth.api.credentials.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public final class zzh implements Parcelable.Creator<DeleteRequest> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(DeleteRequest deleteRequest, Parcel parcel, int i) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 1, deleteRequest.di, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1000, deleteRequest.mVersionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ DeleteRequest[] newArray(int i) {
        return new DeleteRequest[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ DeleteRequest createFromParcel(Parcel parcel) {
        int zzcm = zza.zzcm(parcel);
        int i = 0;
        Credential credential = null;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    credential = (Credential) zza.zza(parcel, readInt, Credential.CREATOR);
                    break;
                case 1000:
                    i = zza.zzg(parcel, readInt);
                    break;
                default:
                    zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new DeleteRequest(i, credential);
    }
}
