package com.flurry.sdk;

import android.app.Activity;
import android.content.Context;
import com.flurry.sdk.ju;
import com.flurry.sdk.le;
import com.flurry.sdk.lj;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public class lf implements lj.a {
    private static lf b;
    private static final String c = lf.class.getSimpleName();
    public long a;
    private long g;
    private ld h;
    private final Map<Context, ld> d = new WeakHashMap();
    private final lg e = new lg();
    private final Object f = new Object();
    private ka<lh> i = new ka<lh>() { // from class: com.flurry.sdk.lf.1
        @Override // com.flurry.sdk.ka
        public final /* bridge */ /* synthetic */ void a(lh lhVar) {
            lf.this.h();
        }
    };
    private ka<ju> j = new ka<ju>() { // from class: com.flurry.sdk.lf.2
        @Override // com.flurry.sdk.ka
        public final /* synthetic */ void a(ju juVar) {
            ju juVar2 = juVar;
            Activity activity = juVar2.a.get();
            if (activity == null) {
                kf.a(lf.c, "Activity has been destroyed, can't pass ActivityLifecycleEvent to adobject.");
                return;
            }
            switch (AnonymousClass5.a[juVar2.b.ordinal()]) {
                case 1:
                    kf.a(3, lf.c, "Automatic onStartSession for context:" + juVar2.a);
                    lf.this.e(activity);
                    return;
                case 2:
                    kf.a(3, lf.c, "Automatic onEndSession for context:" + juVar2.a);
                    lf.this.d(activity);
                    return;
                case 3:
                    kf.a(3, lf.c, "Automatic onEndSession (destroyed) for context:" + juVar2.a);
                    lf.this.d(activity);
                    return;
                default:
                    return;
            }
        }
    };

    public static synchronized lf a() {
        lf lfVar;
        synchronized (lf.class) {
            if (b == null) {
                b = new lf();
            }
            lfVar = b;
        }
        return lfVar;
    }

    /* renamed from: com.flurry.sdk.lf$5, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] a = new int[ju.a.values().length];

        static {
            try {
                a[ju.a.kStarted.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                a[ju.a.kStopped.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                a[ju.a.kDestroyed.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private lf() {
        li a = li.a();
        this.a = 0L;
        this.g = ((Long) a.a("ContinueSessionMillis")).longValue();
        a.a("ContinueSessionMillis", (lj.a) this);
        kf.a(4, c, "initSettings, ContinueSessionMillis = " + this.g);
        kb.a().a("com.flurry.android.sdk.ActivityLifecycleEvent", this.j);
        kb.a().a("com.flurry.android.sdk.FlurrySessionTimerEvent", this.i);
    }

    private synchronized int g() {
        return this.d.size();
    }

    public final ld c() {
        ld ldVar;
        synchronized (this.f) {
            ldVar = this.h;
        }
        return ldVar;
    }

    public final synchronized void a(Context context) {
        if ((context instanceof Activity) && jv.a().c()) {
            kf.a(3, c, "bootstrap for context:" + context);
            e(context);
        }
    }

    public final synchronized void b(Context context) {
        if (!jv.a().c() || !(context instanceof Activity)) {
            kf.a(3, c, "Manual onStartSession for context:" + context);
            e(context);
        }
    }

    public final synchronized void c(Context context) {
        if (!jv.a().c() || !(context instanceof Activity)) {
            kf.a(3, c, "Manual onEndSession for context:" + context);
            d(context);
        }
    }

    public final synchronized void e() {
        for (Map.Entry<Context, ld> entry : this.d.entrySet()) {
            le leVar = new le();
            leVar.a = new WeakReference<>(entry.getKey());
            leVar.b = entry.getValue();
            leVar.c = le.a.d;
            jd.a();
            leVar.d = jd.d();
            leVar.b();
        }
        this.d.clear();
        jr.a().b(new lw() { // from class: com.flurry.sdk.lf.3
            @Override // com.flurry.sdk.lw
            public final void a() {
                lf.this.h();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void e(Context context) {
        if (this.d.get(context) != null) {
            if (jv.a().c()) {
                kf.a(3, c, "Session already started with context:" + context);
            } else {
                kf.e(c, "Session already started with context:" + context);
            }
        } else {
            this.e.a();
            ld c2 = c();
            if (c2 == null) {
                c2 = new ld();
                kf.e(c, "Flurry session started for context:" + context);
                le leVar = new le();
                leVar.a = new WeakReference<>(context);
                leVar.b = c2;
                leVar.c = le.a.a;
                leVar.b();
            }
            this.d.put(context, c2);
            synchronized (this.f) {
                this.h = c2;
            }
            kf.e(c, "Flurry session resumed for context:" + context);
            le leVar2 = new le();
            leVar2.a = new WeakReference<>(context);
            leVar2.b = c2;
            leVar2.c = le.a.c;
            leVar2.b();
            this.a = 0L;
        }
    }

    final synchronized void d(Context context) {
        ld remove = this.d.remove(context);
        if (remove == null) {
            if (jv.a().c()) {
                kf.a(3, c, "Session cannot be ended, session not found for context:" + context);
            } else {
                kf.e(c, "Session cannot be ended, session not found for context:" + context);
            }
        } else {
            kf.e(c, "Flurry session paused for context:" + context);
            le leVar = new le();
            leVar.a = new WeakReference<>(context);
            leVar.b = remove;
            jd.a();
            leVar.d = jd.d();
            leVar.c = le.a.d;
            leVar.b();
            if (g() == 0) {
                this.e.a(this.g);
                this.a = System.currentTimeMillis();
            } else {
                this.a = 0L;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void h() {
        int g = g();
        if (g > 0) {
            kf.a(5, c, "Session cannot be finalized, sessionContextCount:" + g);
        } else {
            final ld c2 = c();
            if (c2 == null) {
                kf.a(5, c, "Session cannot be finalized, current session not found");
            } else {
                kf.e(c, "Flurry session ended");
                le leVar = new le();
                leVar.b = c2;
                leVar.c = le.a.e;
                jd.a();
                leVar.d = jd.d();
                leVar.b();
                jr.a().b(new lw() { // from class: com.flurry.sdk.lf.4
                    @Override // com.flurry.sdk.lw
                    public final void a() {
                        lf.a(lf.this, c2);
                    }
                });
            }
        }
    }

    @Override // com.flurry.sdk.lj.a
    public final void a(String str, Object obj) {
        if (str.equals("ContinueSessionMillis")) {
            this.g = ((Long) obj).longValue();
            kf.a(4, c, "onSettingUpdate, ContinueSessionMillis = " + this.g);
        } else {
            kf.a(6, c, "onSettingUpdate internal error!");
        }
    }

    static /* synthetic */ void a(lf lfVar, ld ldVar) {
        synchronized (lfVar.f) {
            if (lfVar.h == ldVar) {
                lfVar.h = null;
            }
        }
    }
}
