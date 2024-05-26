package com.google.android.gms.common.internal.safeparcel;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class zza {

    /* renamed from: com.google.android.gms.common.internal.safeparcel.zza$zza, reason: collision with other inner class name */
    /* loaded from: classes.dex */
    public static class C0046zza extends RuntimeException {
        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public C0046zza(java.lang.String r5, android.os.Parcel r6) {
            /*
                r4 = this;
                int r0 = r6.dataPosition()
                int r1 = r6.dataSize()
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                java.lang.String r3 = java.lang.String.valueOf(r5)
                int r3 = r3.length()
                int r3 = r3 + 41
                r2.<init>(r3)
                java.lang.StringBuilder r2 = r2.append(r5)
                java.lang.String r3 = " Parcel: pos="
                java.lang.StringBuilder r2 = r2.append(r3)
                java.lang.StringBuilder r0 = r2.append(r0)
                java.lang.String r2 = " size="
                java.lang.StringBuilder r0 = r0.append(r2)
                java.lang.StringBuilder r0 = r0.append(r1)
                java.lang.String r0 = r0.toString()
                r4.<init>(r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.common.internal.safeparcel.zza.C0046zza.<init>(java.lang.String, android.os.Parcel):void");
        }
    }

    public static int zza(Parcel parcel, int i) {
        return (i & (-65536)) != -65536 ? (i >> 16) & 65535 : parcel.readInt();
    }

    public static <T extends Parcelable> T zza(Parcel parcel, int i, Parcelable.Creator<T> creator) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        T createFromParcel = creator.createFromParcel(parcel);
        parcel.setDataPosition(zza + dataPosition);
        return createFromParcel;
    }

    private static void zza(Parcel parcel, int i, int i2) {
        int zza = zza(parcel, i);
        if (zza != i2) {
            String valueOf = String.valueOf(Integer.toHexString(zza));
            throw new C0046zza(new StringBuilder(String.valueOf(valueOf).length() + 46).append("Expected size ").append(i2).append(" got ").append(zza).append(" (0x").append(valueOf).append(")").toString(), parcel);
        }
    }

    public static void zza$ae3cd4b(Parcel parcel, int i, int i2) {
        if (i != i2) {
            String valueOf = String.valueOf(Integer.toHexString(i));
            throw new C0046zza(new StringBuilder(String.valueOf(valueOf).length() + 46).append("Expected size ").append(i2).append(" got ").append(i).append(" (0x").append(valueOf).append(")").toString(), parcel);
        }
    }

    public static String[] zzac(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        String[] createStringArray = parcel.createStringArray();
        parcel.setDataPosition(zza + dataPosition);
        return createStringArray;
    }

    public static ArrayList<String> zzae(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        ArrayList<String> createStringArrayList = parcel.createStringArrayList();
        parcel.setDataPosition(zza + dataPosition);
        return createStringArrayList;
    }

    public static void zzb(Parcel parcel, int i) {
        parcel.setDataPosition(zza(parcel, i) + parcel.dataPosition());
    }

    public static <T> T[] zzb(Parcel parcel, int i, Parcelable.Creator<T> creator) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        T[] tArr = (T[]) parcel.createTypedArray(creator);
        parcel.setDataPosition(zza + dataPosition);
        return tArr;
    }

    public static <T> ArrayList<T> zzc(Parcel parcel, int i, Parcelable.Creator<T> creator) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        ArrayList<T> createTypedArrayList = parcel.createTypedArrayList(creator);
        parcel.setDataPosition(zza + dataPosition);
        return createTypedArrayList;
    }

    public static boolean zzc(Parcel parcel, int i) {
        zza(parcel, i, 4);
        return parcel.readInt() != 0;
    }

    public static int zzg(Parcel parcel, int i) {
        zza(parcel, i, 4);
        return parcel.readInt();
    }

    public static Integer zzh(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        if (zza == 0) {
            return null;
        }
        zza$ae3cd4b(parcel, zza, 4);
        return Integer.valueOf(parcel.readInt());
    }

    public static long zzi(Parcel parcel, int i) {
        zza(parcel, i, 8);
        return parcel.readLong();
    }

    public static float zzl(Parcel parcel, int i) {
        zza(parcel, i, 4);
        return parcel.readFloat();
    }

    public static String zzq(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        String readString = parcel.readString();
        parcel.setDataPosition(zza + dataPosition);
        return readString;
    }

    public static IBinder zzr(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        IBinder readStrongBinder = parcel.readStrongBinder();
        parcel.setDataPosition(zza + dataPosition);
        return readStrongBinder;
    }

    public static Bundle zzs(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        Bundle readBundle = parcel.readBundle();
        parcel.setDataPosition(zza + dataPosition);
        return readBundle;
    }

    public static byte[] zzt(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        byte[] createByteArray = parcel.createByteArray();
        parcel.setDataPosition(zza + dataPosition);
        return createByteArray;
    }

    public static int[] zzw(Parcel parcel, int i) {
        int zza = zza(parcel, i);
        int dataPosition = parcel.dataPosition();
        if (zza == 0) {
            return null;
        }
        int[] createIntArray = parcel.createIntArray();
        parcel.setDataPosition(zza + dataPosition);
        return createIntArray;
    }

    public static int zzcm(Parcel parcel) {
        int readInt = parcel.readInt();
        int zza = zza(parcel, readInt);
        int dataPosition = parcel.dataPosition();
        if ((65535 & readInt) != 20293) {
            String valueOf = String.valueOf(Integer.toHexString(readInt));
            throw new C0046zza(valueOf.length() != 0 ? "Expected object header. Got 0x".concat(valueOf) : new String("Expected object header. Got 0x"), parcel);
        }
        int i = dataPosition + zza;
        if (i < dataPosition || i > parcel.dataSize()) {
            throw new C0046zza(new StringBuilder(54).append("Size read is invalid start=").append(dataPosition).append(" end=").append(i).toString(), parcel);
        }
        return i;
    }
}
