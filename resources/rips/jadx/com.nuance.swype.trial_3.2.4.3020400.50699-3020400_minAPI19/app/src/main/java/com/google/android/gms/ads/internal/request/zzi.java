package com.google.android.gms.ads.internal.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class zzi implements Parcelable.Creator<AutoClickProtectionConfigurationParcel> {
    @Override // android.os.Parcelable.Creator
    /* renamed from: zzaq, reason: merged with bridge method [inline-methods] */
    public AutoClickProtectionConfigurationParcel[] newArray(int i) {
        return new AutoClickProtectionConfigurationParcel[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: zzm, reason: merged with bridge method [inline-methods] */
    public AutoClickProtectionConfigurationParcel createFromParcel(Parcel parcel) {
        boolean z = false;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        ArrayList<String> arrayList = null;
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
                    arrayList = com.google.android.gms.common.internal.safeparcel.zza.zzae(parcel, readInt);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new AutoClickProtectionConfigurationParcel(i, z, arrayList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza$392eeb3c(AutoClickProtectionConfigurationParcel autoClickProtectionConfigurationParcel, Parcel parcel) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, autoClickProtectionConfigurationParcel.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, autoClickProtectionConfigurationParcel.zzccu);
        com.google.android.gms.common.internal.safeparcel.zzb.zzb$62107c48(parcel, 3, autoClickProtectionConfigurationParcel.zzccv);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }
}
