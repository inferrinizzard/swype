package com.google.android.gms.signin.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public final class zzf implements Parcelable.Creator<RecordConsentRequest> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(RecordConsentRequest recordConsentRequest, Parcel parcel, int i) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, recordConsentRequest.mVersionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 2, recordConsentRequest.aL, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2d7953c6(parcel, 3, recordConsentRequest.aua, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 4, recordConsentRequest.dR);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ RecordConsentRequest[] newArray(int i) {
        return new RecordConsentRequest[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ RecordConsentRequest createFromParcel(Parcel parcel) {
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        Scope[] scopeArr = null;
        Account account = null;
        int i = 0;
        String str = null;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    account = (Account) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, Account.CREATOR);
                    break;
                case 3:
                    scopeArr = (Scope[]) com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt, Scope.CREATOR);
                    break;
                case 4:
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
        return new RecordConsentRequest(i, account, scopeArr, str);
    }
}
