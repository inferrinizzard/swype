package com.google.android.gms.auth.api.signin;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class zza implements Parcelable.Creator<GoogleSignInAccount> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(GoogleSignInAccount googleSignInAccount, Parcel parcel, int i) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, googleSignInAccount.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 2, googleSignInAccount.zzbgg);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 3, googleSignInAccount.cY);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 4, googleSignInAccount.dB);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 5, googleSignInAccount.dC);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 6, googleSignInAccount.dD, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 7, googleSignInAccount.dE);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 8, googleSignInAccount.dF);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 9, googleSignInAccount.dG);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc$62107c48(parcel, 10, googleSignInAccount.cs);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 11, googleSignInAccount.dH);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 12, googleSignInAccount.dI);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ GoogleSignInAccount[] newArray(int i) {
        return new GoogleSignInAccount[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ GoogleSignInAccount createFromParcel(Parcel parcel) {
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        Uri uri = null;
        String str5 = null;
        long j = 0;
        String str6 = null;
        ArrayList arrayList = null;
        String str7 = null;
        String str8 = null;
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
                    str2 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 4:
                    str3 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 5:
                    str4 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 6:
                    uri = (Uri) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, Uri.CREATOR);
                    break;
                case 7:
                    str5 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 8:
                    j = com.google.android.gms.common.internal.safeparcel.zza.zzi(parcel, readInt);
                    break;
                case 9:
                    str6 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 10:
                    arrayList = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt, Scope.CREATOR);
                    break;
                case 11:
                    str7 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 12:
                    str8 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new GoogleSignInAccount(i, str, str2, str3, str4, uri, str5, j, str6, arrayList, str7, str8);
    }
}
