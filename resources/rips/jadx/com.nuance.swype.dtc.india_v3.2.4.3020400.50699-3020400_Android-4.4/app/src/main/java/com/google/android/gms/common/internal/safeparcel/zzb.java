package com.google.android.gms.common.internal.safeparcel;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/* loaded from: classes.dex */
public final class zzb {
    public static void zza(Parcel parcel, int i, float f) {
        zzb(parcel, i, 4);
        parcel.writeFloat(f);
    }

    public static void zza(Parcel parcel, int i, long j) {
        zzb(parcel, i, 8);
        parcel.writeLong(j);
    }

    public static void zza(Parcel parcel, int i, boolean z) {
        zzb(parcel, i, 4);
        parcel.writeInt(z ? 1 : 0);
    }

    private static <T extends Parcelable> void zza(Parcel parcel, T t, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(1);
        int dataPosition2 = parcel.dataPosition();
        t.writeToParcel(parcel, i);
        int dataPosition3 = parcel.dataPosition();
        parcel.setDataPosition(dataPosition);
        parcel.writeInt(dataPosition3 - dataPosition2);
        parcel.setDataPosition(dataPosition3);
    }

    public static void zza$1ed7098(Parcel parcel, int i, Integer num) {
        if (num == null) {
            return;
        }
        zzb(parcel, i, 4);
        parcel.writeInt(num.intValue());
    }

    public static void zza$2cfb68bf(Parcel parcel, int i, String str) {
        if (str == null) {
            return;
        }
        int zzah = zzah(parcel, i);
        parcel.writeString(str);
        zzai(parcel, zzah);
    }

    public static <T extends Parcelable> void zza$2d7953c6(Parcel parcel, int i, T[] tArr, int i2) {
        if (tArr == null) {
            return;
        }
        int zzah = zzah(parcel, i);
        parcel.writeInt(tArr.length);
        for (T t : tArr) {
            if (t == null) {
                parcel.writeInt(0);
            } else {
                zza(parcel, t, i2);
            }
        }
        zzai(parcel, zzah);
    }

    public static void zza$377a007(Parcel parcel, int i, Parcelable parcelable, int i2) {
        if (parcelable == null) {
            return;
        }
        int zzah = zzah(parcel, i);
        parcelable.writeToParcel(parcel, i2);
        zzai(parcel, zzah);
    }

    public static void zza$41b439c0(Parcel parcel, int i, String[] strArr) {
        if (strArr == null) {
            return;
        }
        int zzah = zzah(parcel, i);
        parcel.writeStringArray(strArr);
        zzai(parcel, zzah);
    }

    public static void zza$52910762(Parcel parcel, int i, byte[] bArr) {
        if (bArr == null) {
            return;
        }
        int zzah = zzah(parcel, i);
        parcel.writeByteArray(bArr);
        zzai(parcel, zzah);
    }

    public static void zza$529435fb(Parcel parcel, int i, int[] iArr) {
        if (iArr == null) {
            return;
        }
        int zzah = zzah(parcel, i);
        parcel.writeIntArray(iArr);
        zzai(parcel, zzah);
    }

    public static void zza$cdac282(Parcel parcel, int i, IBinder iBinder) {
        if (iBinder == null) {
            return;
        }
        int zzah = zzah(parcel, i);
        parcel.writeStrongBinder(iBinder);
        zzai(parcel, zzah);
    }

    public static void zza$f7bef55(Parcel parcel, int i, Bundle bundle) {
        if (bundle == null) {
            return;
        }
        int zzah = zzah(parcel, i);
        parcel.writeBundle(bundle);
        zzai(parcel, zzah);
    }

    public static int zzah(Parcel parcel, int i) {
        parcel.writeInt((-65536) | i);
        parcel.writeInt(0);
        return parcel.dataPosition();
    }

    public static void zzai(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.setDataPosition(i - 4);
        parcel.writeInt(dataPosition - i);
        parcel.setDataPosition(dataPosition);
    }

    public static void zzb(Parcel parcel, int i, int i2) {
        if (i2 < 65535) {
            parcel.writeInt((i2 << 16) | i);
        } else {
            parcel.writeInt((-65536) | i);
            parcel.writeInt(i2);
        }
    }

    public static void zzb$62107c48(Parcel parcel, int i, List<String> list) {
        if (list == null) {
            return;
        }
        int zzah = zzah(parcel, i);
        parcel.writeStringList(list);
        zzai(parcel, zzah);
    }

    public static void zzc(Parcel parcel, int i, int i2) {
        zzb(parcel, i, 4);
        parcel.writeInt(i2);
    }

    public static <T extends Parcelable> void zzc$62107c48(Parcel parcel, int i, List<T> list) {
        if (list == null) {
            return;
        }
        int zzah = zzah(parcel, i);
        int size = list.size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            T t = list.get(i2);
            if (t == null) {
                parcel.writeInt(0);
            } else {
                zza(parcel, t, 0);
            }
        }
        zzai(parcel, zzah);
    }
}
