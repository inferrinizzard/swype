package com.flurry.sdk;

/* loaded from: classes.dex */
public final class lm {
    private static long a = 100;
    private static lm b = null;
    private final ln c = new ln();

    public static synchronized lm a() {
        lm lmVar;
        synchronized (lm.class) {
            if (b == null) {
                b = new lm();
            }
            lmVar = b;
        }
        return lmVar;
    }

    public lm() {
        this.c.a = a;
        this.c.b = true;
    }

    public final synchronized void a(ka<ll> kaVar) {
        kb.a().a("com.flurry.android.sdk.TickEvent", kaVar);
        if (kb.a().b("com.flurry.android.sdk.TickEvent") > 0) {
            this.c.a();
        }
    }

    public final synchronized void b(ka<ll> kaVar) {
        kb.a().b("com.flurry.android.sdk.TickEvent", kaVar);
        if (kb.a().b("com.flurry.android.sdk.TickEvent") == 0) {
            this.c.b();
        }
    }
}
