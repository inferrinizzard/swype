package com.google.android.gms.ads.internal.purchase;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

/* loaded from: classes.dex */
public class zza implements Parcelable.Creator<GInAppPurchaseManagerInfoParcel> {
    @Override // android.os.Parcelable.Creator
    /* renamed from: zzah, reason: merged with bridge method [inline-methods] */
    public GInAppPurchaseManagerInfoParcel[] newArray(int i) {
        return new GInAppPurchaseManagerInfoParcel[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: zzj, reason: merged with bridge method [inline-methods] */
    public GInAppPurchaseManagerInfoParcel createFromParcel(Parcel parcel) {
        IBinder iBinder = null;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        int i = 0;
        IBinder iBinder2 = null;
        IBinder iBinder3 = null;
        IBinder iBinder4 = null;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
                case 3:
                    iBinder4 = com.google.android.gms.common.internal.safeparcel.zza.zzr(parcel, readInt);
                    break;
                case 4:
                    iBinder3 = com.google.android.gms.common.internal.safeparcel.zza.zzr(parcel, readInt);
                    break;
                case 5:
                    iBinder2 = com.google.android.gms.common.internal.safeparcel.zza.zzr(parcel, readInt);
                    break;
                case 6:
                    iBinder = com.google.android.gms.common.internal.safeparcel.zza.zzr(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new GInAppPurchaseManagerInfoParcel(i, iBinder4, iBinder3, iBinder2, iBinder);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza$629388ef(GInAppPurchaseManagerInfoParcel gInAppPurchaseManagerInfoParcel, Parcel parcel) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, gInAppPurchaseManagerInfoParcel.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$cdac282(parcel, 3, com.google.android.gms.dynamic.zze.zzac(gInAppPurchaseManagerInfoParcel.zzapt).asBinder());
        com.google.android.gms.common.internal.safeparcel.zzb.zza$cdac282(parcel, 4, com.google.android.gms.dynamic.zze.zzac(gInAppPurchaseManagerInfoParcel.zzbwm).asBinder());
        com.google.android.gms.common.internal.safeparcel.zzb.zza$cdac282(parcel, 5, com.google.android.gms.dynamic.zze.zzac(gInAppPurchaseManagerInfoParcel.zzbwn).asBinder());
        com.google.android.gms.common.internal.safeparcel.zzb.zza$cdac282(parcel, 6, com.google.android.gms.dynamic.zze.zzac(gInAppPurchaseManagerInfoParcel.zzbwo).asBinder());
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }
}
