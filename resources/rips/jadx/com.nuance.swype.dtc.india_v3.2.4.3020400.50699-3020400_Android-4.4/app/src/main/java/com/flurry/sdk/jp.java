package com.flurry.sdk;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public final class jp extends ke<kn> {
    private static jp a = null;

    public static synchronized jp a() {
        jp jpVar;
        synchronized (jp.class) {
            if (a == null) {
                a = new jp();
            }
            jpVar = a;
        }
        return jpVar;
    }

    protected jp() {
        super(jp.class.getName(), TimeUnit.MILLISECONDS, new PriorityBlockingQueue(11, new kc()));
    }
}
