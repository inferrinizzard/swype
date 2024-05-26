package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.internal.zzrp;

/* loaded from: classes.dex */
public final class zzz {
    private static String ze;
    private static int zf;
    private static Object zzamr = new Object();
    private static boolean zzbyu;

    public static int zzcg(Context context) {
        Bundle bundle;
        synchronized (zzamr) {
            if (!zzbyu) {
                zzbyu = true;
                try {
                    bundle = zzrp.zzcq(context).getApplicationInfo(context.getPackageName(), 128).metaData;
                } catch (PackageManager.NameNotFoundException e) {
                    Log.wtf("MetadataValueReader", "This should never happen.", e);
                }
                if (bundle != null) {
                    ze = bundle.getString("com.google.app.id");
                    zf = bundle.getInt("com.google.android.gms.version");
                }
            }
        }
        return zf;
    }
}
