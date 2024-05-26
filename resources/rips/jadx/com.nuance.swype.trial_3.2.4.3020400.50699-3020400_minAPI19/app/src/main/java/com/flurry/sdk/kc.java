package com.flurry.sdk;

import java.util.Comparator;

/* loaded from: classes.dex */
public class kc implements Comparator<Runnable> {
    private static final String a = kc.class.getSimpleName();

    @Override // java.util.Comparator
    public /* synthetic */ int compare(Runnable runnable, Runnable runnable2) {
        int a2 = a(runnable);
        int a3 = a(runnable2);
        if (a2 < a3) {
            return -1;
        }
        if (a2 > a3) {
            return 1;
        }
        return 0;
    }

    private static int a(Runnable runnable) {
        int i;
        if (runnable == null) {
            return Integer.MAX_VALUE;
        }
        if (runnable instanceof kd) {
            lx lxVar = (lx) ((kd) runnable).a();
            if (lxVar == null) {
                i = Integer.MAX_VALUE;
            } else {
                i = lxVar.w;
            }
            return i;
        }
        if (!(runnable instanceof lx)) {
            kf.a(6, a, "Unknown runnable class: " + runnable.getClass().getName());
            return Integer.MAX_VALUE;
        }
        return ((lx) runnable).w;
    }
}
