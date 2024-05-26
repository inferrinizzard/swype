package com.google.android.gms.clearcut;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.playlog.internal.PlayLoggerContext;

/* loaded from: classes.dex */
public final class zzd implements Parcelable.Creator<LogEventParcelable> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(LogEventParcelable logEventParcelable, Parcel parcel, int i) {
        int zzah = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 20293);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, logEventParcelable.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$377a007(parcel, 2, logEventParcelable.qu, i);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$52910762(parcel, 3, logEventParcelable.qv);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$529435fb(parcel, 4, logEventParcelable.qw);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$41b439c0(parcel, 5, logEventParcelable.qx);
        com.google.android.gms.common.internal.safeparcel.zzb.zza$529435fb(parcel, 6, logEventParcelable.qy);
        byte[][] bArr = logEventParcelable.qz;
        if (bArr != null) {
            int zzah2 = com.google.android.gms.common.internal.safeparcel.zzb.zzah(parcel, 7);
            parcel.writeInt(bArr.length);
            for (byte[] bArr2 : bArr) {
                parcel.writeByteArray(bArr2);
            }
            com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah2);
        }
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 8, logEventParcelable.qA);
        com.google.android.gms.common.internal.safeparcel.zzb.zzai(parcel, zzah);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ LogEventParcelable[] newArray(int i) {
        return new LogEventParcelable[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ LogEventParcelable createFromParcel(Parcel parcel) {
        int zzcm = com.google.android.gms.common.internal.safeparcel.zza.zzcm(parcel);
        int i = 0;
        PlayLoggerContext playLoggerContext = null;
        byte[] bArr = null;
        int[] iArr = null;
        String[] strArr = null;
        int[] iArr2 = null;
        byte[][] bArr2 = null;
        boolean z = true;
        while (parcel.dataPosition() < zzcm) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, readInt);
                    break;
                case 2:
                    playLoggerContext = (PlayLoggerContext) com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt, PlayLoggerContext.CREATOR);
                    break;
                case 3:
                    bArr = com.google.android.gms.common.internal.safeparcel.zza.zzt(parcel, readInt);
                    break;
                case 4:
                    iArr = com.google.android.gms.common.internal.safeparcel.zza.zzw(parcel, readInt);
                    break;
                case 5:
                    strArr = com.google.android.gms.common.internal.safeparcel.zza.zzac(parcel, readInt);
                    break;
                case 6:
                    iArr2 = com.google.android.gms.common.internal.safeparcel.zza.zzw(parcel, readInt);
                    break;
                case 7:
                    int zza = com.google.android.gms.common.internal.safeparcel.zza.zza(parcel, readInt);
                    int dataPosition = parcel.dataPosition();
                    if (zza == 0) {
                        bArr2 = null;
                        break;
                    } else {
                        int readInt2 = parcel.readInt();
                        bArr2 = new byte[readInt2];
                        for (int i2 = 0; i2 < readInt2; i2++) {
                            bArr2[i2] = parcel.createByteArray();
                        }
                        parcel.setDataPosition(dataPosition + zza);
                        break;
                    }
                case 8:
                    z = com.google.android.gms.common.internal.safeparcel.zza.zzc(parcel, readInt);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, readInt);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcm) {
            throw new zza.C0046zza(new StringBuilder(37).append("Overread allowed size end=").append(zzcm).toString(), parcel);
        }
        return new LogEventParcelable(i, playLoggerContext, bArr, iArr, strArr, iArr2, bArr2, z);
    }
}
