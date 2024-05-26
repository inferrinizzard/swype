package com.flurry.sdk;

/* loaded from: classes.dex */
public final class jh {
    public static jh a;

    public static synchronized jh a() {
        jh jhVar;
        synchronized (jh.class) {
            if (a == null) {
                a = new jh();
            }
            jhVar = a;
        }
        return jhVar;
    }

    private jh() {
    }
}
