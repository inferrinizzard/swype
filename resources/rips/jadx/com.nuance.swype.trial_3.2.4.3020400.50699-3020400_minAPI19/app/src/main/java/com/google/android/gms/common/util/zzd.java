package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import com.google.android.gms.internal.zzrp;

/* loaded from: classes.dex */
public final class zzd {
    @TargetApi(12)
    public static boolean zzq(Context context, String str) {
        if (!zzs.zzhb(12)) {
            return false;
        }
        "com.google.android.gms".equals(str);
        try {
            return (zzrp.zzcq(context).getApplicationInfo(str, 0).flags & 2097152) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
