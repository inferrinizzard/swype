package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;

/* loaded from: classes.dex */
public abstract class zzm {
    private static final Object yL = new Object();
    private static zzm yM;

    public static zzm zzce(Context context) {
        synchronized (yL) {
            if (yM == null) {
                yM = new zzn(context.getApplicationContext());
            }
        }
        return yM;
    }

    public abstract boolean zza(ComponentName componentName, ServiceConnection serviceConnection, String str);

    public abstract boolean zza(String str, String str2, ServiceConnection serviceConnection, String str3);

    public abstract void zzb$3185ab25(String str, String str2, ServiceConnection serviceConnection);

    public abstract void zzb$9b3168c(ComponentName componentName, ServiceConnection serviceConnection);
}
