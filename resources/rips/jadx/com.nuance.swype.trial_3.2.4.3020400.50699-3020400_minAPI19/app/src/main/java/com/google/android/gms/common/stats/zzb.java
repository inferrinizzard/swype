package com.google.android.gms.common.stats;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Process;
import android.util.Log;
import com.google.android.gms.common.stats.zzc;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public final class zzb {
    private static zzb Aj;
    private static Integer Ap;
    private static final Object yL = new Object();
    private final List<String> Ak;
    private final List<String> Al;
    private final List<String> Am;
    private final List<String> An;
    private zze Ao;
    private zze Aq;

    private zzb() {
        if (getLogLevel() == zzd.LOG_LEVEL_OFF) {
            this.Ak = Collections.EMPTY_LIST;
            this.Al = Collections.EMPTY_LIST;
            this.Am = Collections.EMPTY_LIST;
            this.An = Collections.EMPTY_LIST;
            return;
        }
        String str = zzc.zza.Au.get();
        this.Ak = str == null ? Collections.EMPTY_LIST : Arrays.asList(str.split(","));
        String str2 = zzc.zza.Av.get();
        this.Al = str2 == null ? Collections.EMPTY_LIST : Arrays.asList(str2.split(","));
        String str3 = zzc.zza.Aw.get();
        this.Am = str3 == null ? Collections.EMPTY_LIST : Arrays.asList(str3.split(","));
        String str4 = zzc.zza.Ax.get();
        this.An = str4 == null ? Collections.EMPTY_LIST : Arrays.asList(str4.split(","));
        this.Ao = new zze(zzc.zza.Ay.get().longValue());
        this.Aq = new zze(zzc.zza.Ay.get().longValue());
    }

    private static int getLogLevel() {
        if (Ap == null) {
            try {
                Ap = Integer.valueOf(zzd.LOG_LEVEL_OFF);
            } catch (SecurityException e) {
                Ap = Integer.valueOf(zzd.LOG_LEVEL_OFF);
            }
        }
        return Ap.intValue();
    }

    @SuppressLint({"UntrackedBindService"})
    public static void zza(Context context, ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
        zzb(serviceConnection);
    }

    public static zzb zzaux() {
        synchronized (yL) {
            if (Aj == null) {
                Aj = new zzb();
            }
        }
        return Aj;
    }

    public static String zzb(ServiceConnection serviceConnection) {
        return String.valueOf((Process.myPid() << 32) | System.identityHashCode(serviceConnection));
    }

    public final boolean zza$31a3108d(Context context, Intent intent, ServiceConnection serviceConnection) {
        context.getClass().getName();
        return zza$58d5677d(context, intent, serviceConnection, 1);
    }

    @SuppressLint({"UntrackedBindService"})
    public static boolean zza$58d5677d(Context context, Intent intent, ServiceConnection serviceConnection, int i) {
        boolean z = false;
        ComponentName component = intent.getComponent();
        if (component == null ? false : com.google.android.gms.common.util.zzd.zzq(context, component.getPackageName())) {
            Log.w("ConnectionTracker", "Attempted to bind to a service in a STOPPED package.");
        } else {
            z = context.bindService(intent, serviceConnection, i);
            if (z) {
                zzb(serviceConnection);
            }
        }
        return z;
    }
}
