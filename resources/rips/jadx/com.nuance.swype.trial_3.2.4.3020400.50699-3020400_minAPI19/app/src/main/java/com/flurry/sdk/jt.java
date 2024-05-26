package com.flurry.sdk;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class jt {
    private static jt a;
    private static final String b = jt.class.getSimpleName();
    private static final HashMap<String, Map<String, String>> c = new HashMap<>();

    public static synchronized jt a() {
        jt jtVar;
        synchronized (jt.class) {
            if (a == null) {
                a = new jt();
            }
            jtVar = a;
        }
        return jtVar;
    }

    public final synchronized HashMap<String, Map<String, String>> c() {
        HashMap<String, Map<String, String>> hashMap;
        synchronized (c) {
            hashMap = new HashMap<>(c);
        }
        return hashMap;
    }
}
