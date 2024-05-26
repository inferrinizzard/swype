package com.google.android.gms.playlog.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

/* loaded from: classes.dex */
public final class zza implements Parcelable.Creator<PlayLoggerContext> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza$495264e0(PlayLoggerContext playLoggerContext, Parcel parcel) {
        int zzah = zzb.zzah(parcel, 20293);
        zzb.zzc(parcel, 1, playLoggerContext.versionCode);
        zzb.zza$2cfb68bf(parcel, 2, playLoggerContext.packageName);
        zzb.zzc(parcel, 3, playLoggerContext.arq);
        zzb.zzc(parcel, 4, playLoggerContext.arr);
        zzb.zza$2cfb68bf(parcel, 5, playLoggerContext.ars);
        zzb.zza$2cfb68bf(parcel, 6, playLoggerContext.art);
        zzb.zza(parcel, 7, playLoggerContext.aru);
        zzb.zza$2cfb68bf(parcel, 8, playLoggerContext.arv);
        zzb.zza(parcel, 9, playLoggerContext.arw);
        zzb.zzc(parcel, 10, playLoggerContext.arx);
        zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ PlayLoggerContext[] newArray(int i) {
        return new PlayLoggerContext[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ PlayLoggerContext createFromParcel(Parcel parcel) {
        String str = null;
        int i = 0;
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        boolean z = true;
        boolean z2 = false;
        String str2 = null;
        String str3 = null;
        int i2 = 0;
        int i3 = 0;
        String str4 = null;
        int i4 = 0;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i4 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    str4 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 3:
                    i3 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 4:
                    i2 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 5:
                    str3 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 6:
                    str2 = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 7:
                    z = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 8:
                    str = com.google.android.gms.common.internal.safeparcel.zza.zzq(parcel, readInt);
                    break;
                case 9:
                    z2 = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                case 10:
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
        return new PlayLoggerContext(i4, str4, i3, i2, str3, str2, z, str, z2, i);
    }
}
