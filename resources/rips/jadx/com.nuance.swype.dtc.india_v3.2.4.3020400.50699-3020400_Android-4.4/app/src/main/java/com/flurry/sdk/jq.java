package com.flurry.sdk;

import android.content.Context;
import android.os.SystemClock;
import com.flurry.sdk.le;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class jq {
    static final String a = jq.class.getSimpleName();
    WeakReference<ld> b;
    private String i;
    private String j;
    private Map<String, String> k;
    private final ka<le> g = new ka<le>() { // from class: com.flurry.sdk.jq.1
        @Override // com.flurry.sdk.ka
        public final /* synthetic */ void a(le leVar) {
            le leVar2 = leVar;
            if (jq.this.b == null || leVar2.b == jq.this.b.get()) {
                switch (AnonymousClass4.a[leVar2.c - 1]) {
                    case 1:
                        final jq jqVar = jq.this;
                        ld ldVar = leVar2.b;
                        Context context = leVar2.a.get();
                        jqVar.b = new WeakReference<>(ldVar);
                        jqVar.c = System.currentTimeMillis();
                        jqVar.d = SystemClock.elapsedRealtime();
                        if (ldVar == null || context == null) {
                            kf.a(3, jq.a, "Flurry session id cannot be created.");
                        } else {
                            kf.a(3, jq.a, "Flurry session id started:" + jqVar.c);
                            le leVar3 = new le();
                            leVar3.a = new WeakReference<>(context);
                            leVar3.b = ldVar;
                            leVar3.c = le.a.b;
                            leVar3.b();
                        }
                        jr.a().b(new lw() { // from class: com.flurry.sdk.jq.3
                            @Override // com.flurry.sdk.lw
                            public final void a() {
                                ji.a().c();
                            }
                        });
                        return;
                    case 2:
                        jq jqVar2 = jq.this;
                        leVar2.a.get();
                        jqVar2.a();
                        return;
                    case 3:
                        jq jqVar3 = jq.this;
                        leVar2.a.get();
                        jqVar3.e = SystemClock.elapsedRealtime() - jqVar3.d;
                        return;
                    case 4:
                        kb.a().b("com.flurry.android.sdk.FlurrySessionEvent", jq.this.g);
                        jq.b();
                        return;
                    default:
                        return;
                }
            }
        }
    };
    public volatile long c = 0;
    public volatile long d = 0;
    public volatile long e = -1;
    public volatile long f = 0;
    private volatile long h = 0;

    /* renamed from: com.flurry.sdk.jq$4, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] a = new int[le.a.a().length];

        static {
            try {
                a[le.a.a - 1] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                a[le.a.c - 1] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                a[le.a.d - 1] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                a[le.a.e - 1] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public jq() {
        kb.a().a("com.flurry.android.sdk.FlurrySessionEvent", this.g);
        this.k = new LinkedHashMap<String, String>() { // from class: com.flurry.sdk.jq.2
            @Override // java.util.LinkedHashMap
            protected final boolean removeEldestEntry(Map.Entry<String, String> entry) {
                return size() > 10;
            }
        };
    }

    public final synchronized void a() {
        long j = lf.a().a;
        if (j > 0) {
            this.f = (System.currentTimeMillis() - j) + this.f;
        }
    }

    public static void b() {
    }

    public final synchronized long c() {
        long elapsedRealtime = SystemClock.elapsedRealtime() - this.d;
        if (elapsedRealtime <= this.h) {
            elapsedRealtime = this.h + 1;
            this.h = elapsedRealtime;
        }
        this.h = elapsedRealtime;
        return this.h;
    }

    public final synchronized String d() {
        return this.i;
    }

    public final synchronized String e() {
        return this.j;
    }

    public final synchronized void a(String str, String str2) {
        this.k.put(str, str2);
    }

    public final synchronized Map<String, String> f() {
        return this.k;
    }
}
