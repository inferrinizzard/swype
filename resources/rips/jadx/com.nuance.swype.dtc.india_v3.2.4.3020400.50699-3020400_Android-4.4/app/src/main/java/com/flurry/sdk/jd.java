package com.flurry.sdk;

import android.content.Context;
import com.flurry.sdk.jk;

/* loaded from: classes.dex */
public class jd implements ki {
    private static final String a = jd.class.getSimpleName();

    public static synchronized jd a() {
        jd jdVar;
        synchronized (jd.class) {
            jdVar = (jd) jr.a().a(jd.class);
        }
        return jdVar;
    }

    @Override // com.flurry.sdk.ki
    public final void a(Context context) {
        ld.a(jq.class);
        kb.a();
        lm.a();
        li.a();
        jt.a();
        jk.a();
        je.a();
        jl.a();
        ji.a();
        je.a();
        jn.a();
        jh.a();
        jp.a();
    }

    public static long d() {
        jq i = i();
        if (i == null) {
            return 0L;
        }
        return i.c;
    }

    public static long e() {
        jq i = i();
        if (i == null) {
            return 0L;
        }
        return i.d;
    }

    public static long f() {
        jq i = i();
        if (i == null) {
            return -1L;
        }
        return i.e;
    }

    public static long g() {
        jq i = i();
        if (i == null) {
            return 0L;
        }
        return i.c();
    }

    public static jk.a h() {
        return jk.a().c();
    }

    public static jq i() {
        ld c = lf.a().c();
        if (c == null) {
            return null;
        }
        return (jq) c.c(jq.class);
    }
}
