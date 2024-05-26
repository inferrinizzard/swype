package com.google.android.gms.auth.api.credentials;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class zza implements Parcelable.Creator<Credential> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(Credential credential, Parcel parcel, int i) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 1, credential.zzbgg);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 2, credential.mName);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 3, credential.cJ, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc$62107c48(parcel, 4, credential.cK);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 5, credential.cL);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 6, credential.cM);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 7, credential.cN);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1000, credential.mVersionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 8, credential.cO);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Credential[] newArray(int i) {
        return new Credential[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Credential createFromParcel(Parcel parcel) {
        String str = null;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        int i = 0;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        ArrayList arrayList = null;
        Uri uri = null;
        String str5 = null;
        String str6 = null;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    str6 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 2:
                    str5 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 3:
                    uri = (Uri) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, Uri.CREATOR);
                    break;
                case 4:
                    arrayList = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt, IdToken.CREATOR);
                    break;
                case 5:
                    str4 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 6:
                    str3 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 7:
                    str2 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 8:
                    str = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 1000:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new Credential(i, str6, str5, uri, arrayList, str4, str3, str2, str);
    }
}
