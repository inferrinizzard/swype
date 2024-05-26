package com.flurry.sdk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/* loaded from: classes.dex */
public final class lo {
    private static final String a = lo.class.getSimpleName();

    private static PackageInfo d(Context context) {
        if (context == null) {
            return null;
        }
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 20815);
        } catch (PackageManager.NameNotFoundException e) {
            kf.a(a, "Cannot find package info for package: " + context.getPackageName());
            return null;
        }
    }

    public static String a(Context context) {
        PackageInfo d = d(context);
        return (d == null || d.packageName == null) ? "" : d.packageName;
    }

    public static String b(Context context) {
        PackageInfo d = d(context);
        return (d == null || d.versionName == null) ? "" : d.versionName;
    }
}
