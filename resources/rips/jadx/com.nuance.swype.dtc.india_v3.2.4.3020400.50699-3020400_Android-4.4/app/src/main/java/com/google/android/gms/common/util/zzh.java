package com.google.android.gms.common.util;

import android.os.SystemClock;

/* loaded from: classes.dex */
public final class zzh implements zze {
    private static zzh AW;

    public static synchronized zze zzavm() {
        zzh zzhVar;
        synchronized (zzh.class) {
            if (AW == null) {
                AW = new zzh();
            }
            zzhVar = AW;
        }
        return zzhVar;
    }

    @Override // com.google.android.gms.common.util.zze
    public final long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override // com.google.android.gms.common.util.zze
    public final long elapsedRealtime() {
        return SystemClock.elapsedRealtime();
    }

    @Override // com.google.android.gms.common.util.zze
    public final long nanoTime() {
        return System.nanoTime();
    }
}
