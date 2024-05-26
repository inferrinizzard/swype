package com.flurry.sdk;

/* loaded from: classes.dex */
public class mb {
    private static final String a = mb.class.getSimpleName();
    private static boolean b;

    public static synchronized void a() {
        synchronized (mb.class) {
            if (!b) {
                kh.a((Class<? extends ki>) jd.class);
                try {
                    kh.a((Class<? extends ki>) hk.class);
                } catch (NoClassDefFoundError e) {
                    kf.a(3, a, "Analytics module not available");
                }
                try {
                    kh.a((Class<? extends ki>) lz.class);
                } catch (NoClassDefFoundError e2) {
                    kf.a(3, a, "Crash module not available");
                }
                try {
                    kh.a((Class<? extends ki>) Class.forName("com.flurry.sdk.i"));
                } catch (ClassNotFoundException | NoClassDefFoundError e3) {
                    kf.a(3, a, "Ads module not available");
                }
                b = true;
            }
        }
    }
}
