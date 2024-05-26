package com.flurry.sdk;

import android.content.Context;
import android.os.Build;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class kh {
    private static final String a = kh.class.getSimpleName();
    private static final Map<Class<? extends ki>, kg> b = new LinkedHashMap();
    private final Map<Class<? extends ki>, ki> c = new LinkedHashMap();

    public static void a(Class<? extends ki> cls) {
        if (cls != null) {
            synchronized (b) {
                b.put(cls, new kg(cls));
            }
        }
    }

    public final synchronized void a(Context context) {
        ArrayList<kg> arrayList;
        if (context == null) {
            kf.a(5, a, "Null context.");
        } else {
            synchronized (b) {
                arrayList = new ArrayList(b.values());
            }
            for (kg kgVar : arrayList) {
                try {
                    if (kgVar.a != null && Build.VERSION.SDK_INT >= kgVar.b) {
                        ki newInstance = kgVar.a.newInstance();
                        newInstance.a(context);
                        this.c.put(kgVar.a, newInstance);
                    }
                } catch (Exception e) {
                    kf.a(5, a, "Flurry Module for class " + kgVar.a + " is not available:", e);
                }
            }
            lf.a().a(context);
            jv.a();
        }
    }

    public final ki b(Class<? extends ki> cls) {
        ki kiVar;
        if (cls == null) {
            return null;
        }
        synchronized (this.c) {
            kiVar = this.c.get(cls);
        }
        if (kiVar == null) {
            throw new IllegalStateException("Module was not registered/initialized. " + cls);
        }
        return kiVar;
    }
}
