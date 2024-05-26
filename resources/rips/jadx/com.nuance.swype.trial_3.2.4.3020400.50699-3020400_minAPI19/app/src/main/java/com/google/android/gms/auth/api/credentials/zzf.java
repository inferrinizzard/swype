package com.google.android.gms.auth.api.credentials;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class zzf implements Parcelable.Creator<PasswordSpecification> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza$18e098bf(PasswordSpecification passwordSpecification, Parcel parcel) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$2cfb68bf(parcel, 1, passwordSpecification.db);
        com.google.android.gms.common.internal.safeparcel.zzb.zzb$62107c48(parcel, 2, passwordSpecification.dc);
        List<Integer> list = passwordSpecification.dd;
        if (list != null) {
            int zzah2 = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 3);
            int size = list.size();
            parcel.writeInt(size);
            for (int i = 0; i < size; i++) {
                parcel.writeInt(list.get(i).intValue());
            }
            com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah2);
        }
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 4, passwordSpecification.de);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 5, passwordSpecification.df);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1000, passwordSpecification.mVersionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ PasswordSpecification[] newArray(int i) {
        return new PasswordSpecification[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ PasswordSpecification createFromParcel(Parcel parcel) {
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        int i = 0;
        int i2 = 0;
        ArrayList arrayList = null;
        ArrayList<String> arrayList2 = null;
        String str = null;
        int i3 = 0;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    str = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 2:
                    arrayList2 = com.google.android.gms.common.internal.safeparcel.zza.zzae(parcel, readInt);
                    break;
                case 3:
                    int zza = com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt);
                    int dataPosition = parcel.dataPosition();
                    if (zza == 0) {
                        arrayList = null;
                        break;
                    } else {
                        arrayList = new ArrayList();
                        int readInt2 = parcel.readInt();
                        for (int i4 = 0; i4 < readInt2; i4++) {
                            arrayList.add(Integer.valueOf(parcel.readInt()));
                        }
                        parcel.setDataPosition(dataPosition + zza);
                        break;
                    }
                case 4:
                    i2 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 5:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
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
        return new PasswordSpecification(i3, str, arrayList2, arrayList, i2, i);
    }
}
