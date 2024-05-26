package com.flurry.sdk;

import android.os.Build;
import android.text.TextUtils;
import com.flurry.sdk.lj;

/* loaded from: classes.dex */
public class jn implements lj.a {
    private static jn a;
    private static final String b = jn.class.getSimpleName();
    private String c;
    private String d;

    public static synchronized jn a() {
        jn jnVar;
        synchronized (jn.class) {
            if (a == null) {
                a = new jn();
            }
            jnVar = a;
        }
        return jnVar;
    }

    private jn() {
        li a2 = li.a();
        this.c = (String) a2.a("VersionName");
        a2.a("VersionName", (lj.a) this);
        kf.a(4, b, "initSettings, VersionName = " + this.c);
    }

    public static String c() {
        return Build.VERSION.RELEASE;
    }

    public static String d() {
        return Build.DEVICE;
    }

    public final synchronized String i() {
        String str;
        if (!TextUtils.isEmpty(this.c)) {
            str = this.c;
        } else if (!TextUtils.isEmpty(this.d)) {
            str = this.d;
        } else {
            this.d = j();
            str = this.d;
        }
        return str;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0019, code lost:            r0 = com.facebook.internal.AnalyticsEvents.PARAMETER_DIALOG_OUTCOME_VALUE_UNKNOWN;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static java.lang.String j() {
        /*
            com.flurry.sdk.jr r0 = com.flurry.sdk.jr.a()     // Catch: java.lang.Throwable -> L25
            android.content.Context r0 = r0.a     // Catch: java.lang.Throwable -> L25
            android.content.pm.PackageManager r1 = r0.getPackageManager()     // Catch: java.lang.Throwable -> L25
            java.lang.String r0 = r0.getPackageName()     // Catch: java.lang.Throwable -> L25
            r2 = 0
            android.content.pm.PackageInfo r0 = r1.getPackageInfo(r0, r2)     // Catch: java.lang.Throwable -> L25
            java.lang.String r1 = r0.versionName     // Catch: java.lang.Throwable -> L25
            if (r1 == 0) goto L1a
            java.lang.String r0 = r0.versionName     // Catch: java.lang.Throwable -> L25
        L19:
            return r0
        L1a:
            int r1 = r0.versionCode     // Catch: java.lang.Throwable -> L25
            if (r1 == 0) goto L2f
            int r0 = r0.versionCode     // Catch: java.lang.Throwable -> L25
            java.lang.String r0 = java.lang.Integer.toString(r0)     // Catch: java.lang.Throwable -> L25
            goto L19
        L25:
            r0 = move-exception
            r1 = 6
            java.lang.String r2 = com.flurry.sdk.jn.b
            java.lang.String r3 = ""
            com.flurry.sdk.kf.a(r1, r2, r3, r0)
        L2f:
            java.lang.String r0 = "Unknown"
            goto L19
        */
        throw new UnsupportedOperationException("Method not decompiled: com.flurry.sdk.jn.j():java.lang.String");
    }

    @Override // com.flurry.sdk.lj.a
    public final void a(String str, Object obj) {
        if (str.equals("VersionName")) {
            this.c = (String) obj;
            kf.a(4, b, "onSettingUpdate, VersionName = " + this.c);
        } else {
            kf.a(6, b, "onSettingUpdate internal error!");
        }
    }
}
