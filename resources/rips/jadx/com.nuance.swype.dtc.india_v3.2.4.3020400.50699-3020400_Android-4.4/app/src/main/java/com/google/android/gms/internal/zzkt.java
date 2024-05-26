package com.google.android.gms.internal;

import android.os.StrictMode;
import java.util.concurrent.Callable;

@zzin
/* loaded from: classes.dex */
public final class zzkt {
    public static <T> T zzb(Callable<T> callable) {
        T t;
        StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        try {
            try {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(threadPolicy).permitDiskReads().permitDiskWrites().build());
                t = callable.call();
            } catch (Throwable th) {
                zzkd.zzb("Unexpected exception.", th);
                com.google.android.gms.ads.internal.zzu.zzft().zzb(th, true);
                StrictMode.setThreadPolicy(threadPolicy);
                t = null;
            }
            return t;
        } finally {
            StrictMode.setThreadPolicy(threadPolicy);
        }
    }
}
