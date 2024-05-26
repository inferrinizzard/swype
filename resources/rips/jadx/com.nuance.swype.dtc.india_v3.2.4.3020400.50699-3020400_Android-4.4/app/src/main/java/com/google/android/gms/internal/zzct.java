package com.google.android.gms.internal;

import java.util.PriorityQueue;

@zzin
/* loaded from: classes.dex */
public final class zzct {

    /* loaded from: classes.dex */
    public static class zza {
        final long value;
        final String zzati;
        final int zzatj;

        zza(long j, String str, int i) {
            this.value = j;
            this.zzati = str;
            this.zzatj = i;
        }

        public final boolean equals(Object obj) {
            if (obj == null || !(obj instanceof zza)) {
                return false;
            }
            return ((zza) obj).value == this.value && ((zza) obj).zzatj == this.zzatj;
        }

        public final int hashCode() {
            return (int) this.value;
        }
    }

    private static long zza(long j, int i) {
        if (i == 0) {
            return 1L;
        }
        return i != 1 ? i % 2 == 0 ? zza((j * j) % 1073807359, i / 2) % 1073807359 : ((zza((j * j) % 1073807359, i / 2) % 1073807359) * j) % 1073807359 : j;
    }

    private static String zza(String[] strArr, int i, int i2) {
        if (strArr.length < i + i2) {
            zzkd.e("Unable to construct shingle");
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i3 = i; i3 < (i + i2) - 1; i3++) {
            stringBuffer.append(strArr[i3]);
            stringBuffer.append(' ');
        }
        stringBuffer.append(strArr[(i + i2) - 1]);
        return stringBuffer.toString();
    }

    private static void zza(int i, long j, String str, int i2, PriorityQueue<zza> priorityQueue) {
        zza zzaVar = new zza(j, str, i2);
        if ((priorityQueue.size() != i || (priorityQueue.peek().zzatj <= zzaVar.zzatj && priorityQueue.peek().value <= zzaVar.value)) && !priorityQueue.contains(zzaVar)) {
            priorityQueue.add(zzaVar);
            if (priorityQueue.size() > i) {
                priorityQueue.poll();
            }
        }
    }

    private static long zzb$6cdd695e(String[] strArr, int i) {
        long zzac = (zzcr.zzac(strArr[0]) + 2147483647L) % 1073807359;
        for (int i2 = 1; i2 < i + 0; i2++) {
            zzac = (((zzac * 16785407) % 1073807359) + ((zzcr.zzac(strArr[i2]) + 2147483647L) % 1073807359)) % 1073807359;
        }
        return zzac;
    }

    public static void zza(String[] strArr, int i, int i2, PriorityQueue<zza> priorityQueue) {
        if (strArr.length < i2) {
            zza(i, zzb$6cdd695e(strArr, strArr.length), zza(strArr, 0, strArr.length), strArr.length, priorityQueue);
            return;
        }
        long zzb$6cdd695e = zzb$6cdd695e(strArr, i2);
        zza(i, zzb$6cdd695e, zza(strArr, 0, i2), i2, priorityQueue);
        long zza2 = zza(16785407L, i2 - 1);
        for (int i3 = 1; i3 < (strArr.length - i2) + 1; i3++) {
            zzb$6cdd695e = ((((((zzb$6cdd695e + 1073807359) - ((((zzcr.zzac(strArr[i3 - 1]) + 2147483647L) % 1073807359) * zza2) % 1073807359)) % 1073807359) * 16785407) % 1073807359) + ((zzcr.zzac(strArr[(i3 + i2) - 1]) + 2147483647L) % 1073807359)) % 1073807359;
            zza(i, zzb$6cdd695e, zza(strArr, i3, i2), strArr.length, priorityQueue);
        }
    }
}
