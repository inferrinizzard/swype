package com.google.android.gms.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.zzd;

/* loaded from: classes.dex */
public class zzf {
    private static zzf rx;
    public final Context mContext;

    private zzf(Context context) {
        this.mContext = context.getApplicationContext();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static zzd.zza zza(PackageInfo packageInfo, zzd.zza... zzaVarArr) {
        if (packageInfo.signatures == null) {
            return null;
        }
        if (packageInfo.signatures.length != 1) {
            Log.w("GoogleSignatureVerifier", "Package has more than one signature.");
            return null;
        }
        zzd.zzb zzbVar = new zzd.zzb(packageInfo.signatures[0].toByteArray());
        for (int i = 0; i < zzaVarArr.length; i++) {
            if (zzaVarArr[i].equals(zzbVar)) {
                return zzaVarArr[i];
            }
        }
        return null;
    }

    public static boolean zza(PackageInfo packageInfo, boolean z) {
        if (packageInfo != null && packageInfo.signatures != null) {
            if ((z ? zza(packageInfo, zzd.C0053zzd.ro) : zza(packageInfo, zzd.C0053zzd.ro[0])) != null) {
                return true;
            }
        }
        return false;
    }

    public static zzf zzbz(Context context) {
        zzab.zzy(context);
        synchronized (zzf.class) {
            if (rx == null) {
                zzd.zzbq(context);
                rx = new zzf(context);
            }
        }
        return rx;
    }
}
