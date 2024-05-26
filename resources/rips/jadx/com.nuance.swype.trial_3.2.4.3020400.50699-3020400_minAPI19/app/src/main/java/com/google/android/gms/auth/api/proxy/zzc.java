package com.google.android.gms.auth.api.proxy;

import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public final class zzc implements Parcelable.Creator<ProxyResponse> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(ProxyResponse proxyResponse, Parcel parcel, int i) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, proxyResponse.googlePlayServicesStatusCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 2, proxyResponse.recoveryAction, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 3, proxyResponse.statusCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$f7bef55(parcel, 4, proxyResponse.dq);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$52910762(parcel, 5, proxyResponse.body);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1000, proxyResponse.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ ProxyResponse[] newArray(int i) {
        return new ProxyResponse[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ ProxyResponse createFromParcel(Parcel parcel) {
        byte[] bArr = null;
        int i = 0;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        Bundle bundle = null;
        PendingIntent pendingIntent = null;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i2 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    pendingIntent = (PendingIntent) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, PendingIntent.CREATOR);
                    break;
                case 3:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 4:
                    bundle = com.google.android.gms.common.internal.safeparcel.zza.zzs(parcel, readInt);
                    break;
                case 5:
                    bArr = com.google.android.gms.common.internal.safeparcel.zza.zzt(parcel, readInt);
                    break;
                case 1000:
                    i3 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new ProxyResponse(i3, i2, pendingIntent, i, bundle, bArr);
    }
}
