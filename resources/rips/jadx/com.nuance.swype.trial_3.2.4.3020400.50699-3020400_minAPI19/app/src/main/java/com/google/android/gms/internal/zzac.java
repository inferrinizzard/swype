package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import java.io.File;

/* loaded from: classes.dex */
public final class zzac {
    public static zzl zza$575a9856(Context context) {
        File file = new File(context.getCacheDir(), "volley");
        String str = "volley/0";
        try {
            String packageName = context.getPackageName();
            str = new StringBuilder(String.valueOf(packageName).length() + 12).append(packageName).append("/").append(context.getPackageManager().getPackageInfo(packageName, 0).versionCode).toString();
        } catch (PackageManager.NameNotFoundException e) {
        }
        zzl zzlVar = new zzl(new zzv(file, (byte) 0), new zzt(Build.VERSION.SDK_INT >= 9 ? new zzz() : new zzw(AndroidHttpClient.newInstance(str))), (byte) 0);
        zzlVar.start();
        return zzlVar;
    }
}
