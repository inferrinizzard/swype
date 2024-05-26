package com.google.android.gms.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.google.android.gms.common.zzf;
import com.google.android.gms.internal.zzrp;

/* loaded from: classes.dex */
public final class zzy {
    public static boolean zze(Context context, int i) {
        if (!zzrp.zzcq(context).zzg(i, "com.google.android.gms")) {
            return false;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.google.android.gms", 64);
            zzf zzbz = zzf.zzbz(context);
            context.getPackageManager();
            if (packageInfo == null) {
                return false;
            }
            if (zzf.zza(packageInfo, false)) {
                return true;
            }
            if (!zzf.zza(packageInfo, true)) {
                return false;
            }
            if (com.google.android.gms.common.zze.zzbu(zzbz.mContext)) {
                return true;
            }
            Log.w("GoogleSignatureVerifier", "Test-keys aren't accepted on this build.");
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.isLoggable("UidVerifier", 3);
            return false;
        }
    }
}
