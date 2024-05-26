package com.flurry.sdk;

import android.content.Context;

/* loaded from: classes.dex */
public class hk implements ki {
    private static final String d = hk.class.getSimpleName();
    public Cif a;
    public ix b;
    public ih c;

    public static synchronized hk a() {
        hk hkVar;
        synchronized (hk.class) {
            hkVar = (hk) jr.a().a(hk.class);
        }
        return hkVar;
    }

    @Override // com.flurry.sdk.ki
    public final void a(Context context) {
        ld.a(ja.class);
        this.b = new ix();
        this.a = new Cif();
        this.c = new ih();
        if (!lr.a(context, "android.permission.INTERNET")) {
            kf.b(d, "Application must declare permission: android.permission.INTERNET");
        }
        if (lr.a(context, "android.permission.ACCESS_NETWORK_STATE")) {
            return;
        }
        kf.e(d, "It is highly recommended that the application declare permission: android.permission.ACCESS_NETWORK_STATE");
    }

    public static void a(String str, String str2, Throwable th) {
        ja c = c();
        if (c != null) {
            c.a(str, str2, th.getClass().getName(), th);
        }
    }

    public static ja c() {
        ld c = lf.a().c();
        if (c == null) {
            return null;
        }
        return (ja) c.c(ja.class);
    }
}
