package com.flurry.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import com.flurry.android.FlurryEventRecordStatus;
import com.flurry.sdk.iy;
import com.flurry.sdk.ks;
import com.flurry.sdk.le;
import com.flurry.sdk.lj;
import com.nuance.connect.internal.common.Document;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public class ja implements lj.a {
    static final String a = ja.class.getSimpleName();
    static int b = 100;
    static int c = 10;
    static int d = 1000;
    static int e = 160000;
    static int f = 50;
    WeakReference<ld> g;
    File h;
    jy<List<iy>> i;
    public boolean j;
    boolean k;
    String l;
    byte m;
    Long n;
    private long u;
    private final AtomicInteger q = new AtomicInteger(0);
    private final AtomicInteger r = new AtomicInteger(0);
    private final AtomicInteger s = new AtomicInteger(0);
    private final ka<le> t = new ka<le>() { // from class: com.flurry.sdk.ja.1
        @Override // com.flurry.sdk.ka
        public final /* synthetic */ void a(le leVar) {
            le leVar2 = leVar;
            if (ja.this.g == null || leVar2.b == ja.this.g.get()) {
                switch (AnonymousClass8.a[leVar2.c - 1]) {
                    case 1:
                        final ja jaVar = ja.this;
                        ld ldVar = leVar2.b;
                        Context context = leVar2.a.get();
                        jaVar.g = new WeakReference<>(ldVar);
                        li a2 = li.a();
                        jaVar.k = ((Boolean) a2.a("LogEvents")).booleanValue();
                        a2.a("LogEvents", (lj.a) jaVar);
                        kf.a(4, ja.a, "initSettings, LogEvents = " + jaVar.k);
                        jaVar.l = (String) a2.a("UserId");
                        a2.a("UserId", (lj.a) jaVar);
                        kf.a(4, ja.a, "initSettings, UserId = " + jaVar.l);
                        jaVar.m = ((Byte) a2.a("Gender")).byteValue();
                        a2.a("Gender", (lj.a) jaVar);
                        kf.a(4, ja.a, "initSettings, Gender = " + ((int) jaVar.m));
                        jaVar.n = (Long) a2.a("Age");
                        a2.a("Age", (lj.a) jaVar);
                        kf.a(4, ja.a, "initSettings, BirthDate = " + jaVar.n);
                        jaVar.o = ((Boolean) a2.a("analyticsEnabled")).booleanValue();
                        a2.a("analyticsEnabled", (lj.a) jaVar);
                        kf.a(4, ja.a, "initSettings, AnalyticsEnabled = " + jaVar.o);
                        jaVar.h = context.getFileStreamPath(".flurryagent." + Integer.toString(jr.a().d.hashCode(), 16));
                        jaVar.i = new jy<>(context.getFileStreamPath(".yflurryreport." + Long.toString(lr.i(jr.a().d), 16)), ".yflurryreport.", 1, new lc<List<iy>>() { // from class: com.flurry.sdk.ja.10
                            @Override // com.flurry.sdk.lc
                            public final kz<List<iy>> a$1f969724() {
                                return new ky(new iy.a());
                            }
                        });
                        jaVar.a(context);
                        jaVar.a(true);
                        if (hk.a().a != null) {
                            jr.a().b(new lw() { // from class: com.flurry.sdk.ja.11
                                @Override // com.flurry.sdk.lw
                                public final void a() {
                                    hk.a().a.a();
                                }
                            });
                        }
                        jr.a().b(new lw() { // from class: com.flurry.sdk.ja.12
                            @Override // com.flurry.sdk.lw
                            public final void a() {
                                ja.this.e();
                            }
                        });
                        jr.a().b(new lw() { // from class: com.flurry.sdk.ja.13
                            @Override // com.flurry.sdk.lw
                            public final void a() {
                                ja.d(ja.this);
                            }
                        });
                        if (je.a().c()) {
                            jr.a().b(new lw() { // from class: com.flurry.sdk.ja.14
                                @Override // com.flurry.sdk.lw
                                public final void a() {
                                    ja jaVar2 = ja.this;
                                    jd.a();
                                    jaVar2.a(true, jd.d());
                                }
                            });
                            return;
                        } else {
                            kb.a().a("com.flurry.android.sdk.IdProviderFinishedEvent", jaVar.p);
                            return;
                        }
                    case 2:
                        ja jaVar2 = ja.this;
                        leVar2.a.get();
                        jaVar2.a();
                        return;
                    case 3:
                        ja jaVar3 = ja.this;
                        leVar2.a.get();
                        jaVar3.b();
                        return;
                    case 4:
                        kb.a().b("com.flurry.android.sdk.FlurrySessionEvent", ja.this.t);
                        ja.this.a(leVar2.d);
                        return;
                    default:
                        return;
                }
            }
        }
    };
    private int v = -1;
    private final List<iy> w = new ArrayList();
    private final Map<String, List<String>> x = new HashMap();
    private final Map<String, String> y = new HashMap();
    private final Map<String, iu> z = new HashMap();
    private final List<iv> A = new ArrayList();
    private boolean B = true;
    private int C = 0;
    private final List<it> D = new ArrayList();
    private int E = 0;
    private int F = 0;
    boolean o = true;
    private final hl G = new hl();
    final ka<jf> p = new ka<jf>() { // from class: com.flurry.sdk.ja.9
        @Override // com.flurry.sdk.ka
        public final /* synthetic */ void a(jf jfVar) {
            jr.a().b(new lw() { // from class: com.flurry.sdk.ja.9.1
                @Override // com.flurry.sdk.lw
                public final void a() {
                    ja jaVar = ja.this;
                    jd.a();
                    jaVar.a(true, jd.d());
                }
            });
        }
    };

    /* renamed from: com.flurry.sdk.ja$8, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass8 {
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

    public ja() {
        kb.a().a("com.flurry.android.sdk.FlurrySessionEvent", this.t);
    }

    public final synchronized void a() {
        this.v = lp.e();
        if (hk.a().c != null) {
            jr.a().b(new lw() { // from class: com.flurry.sdk.ja.15
                @Override // com.flurry.sdk.lw
                public final void a() {
                    final ih ihVar = hk.a().c;
                    ihVar.c = false;
                    jr.a().b(new lw() { // from class: com.flurry.sdk.kq.4
                        public AnonymousClass4() {
                        }

                        @Override // com.flurry.sdk.lw
                        public final void a() {
                            kq.this.b();
                        }
                    });
                }
            });
        }
        if (this.o && hk.a().a != null) {
            jr.a().b(new lw() { // from class: com.flurry.sdk.ja.16
                @Override // com.flurry.sdk.lw
                public final void a() {
                    hk.a().a.b();
                }
            });
        }
    }

    public final synchronized void b() {
        a(false);
        jd.a();
        final long d2 = jd.d();
        jd.a();
        final long f2 = jd.f();
        jd.a();
        final long j = 0;
        jq i = jd.i();
        if (i != null) {
            j = i.f;
        }
        jd.a();
        final int i2 = jd.h().e;
        jd.a();
        b(jd.f());
        if (this.o && hk.a().a != null) {
            jr.a().b(new lw() { // from class: com.flurry.sdk.ja.2
                @Override // com.flurry.sdk.lw
                public final void a() {
                    hk.a().a.a(d2);
                }
            });
        }
        jr.a().b(new lw() { // from class: com.flurry.sdk.ja.3
            @Override // com.flurry.sdk.lw
            public final void a() {
                ja.this.f();
            }
        });
        if (je.a().c()) {
            jr.a().b(new lw() { // from class: com.flurry.sdk.ja.4
                @Override // com.flurry.sdk.lw
                public final void a() {
                    iy a2 = ja.this.a(d2, f2, j, i2);
                    ja.this.w.clear();
                    ja.this.w.add(a2);
                    ja.this.d();
                }
            });
        }
    }

    public final synchronized void a(final long j) {
        kb.a().a(this.p);
        jr.a().b(new lw() { // from class: com.flurry.sdk.ja.5
            @Override // com.flurry.sdk.lw
            public final void a() {
                if (ja.this.o && hk.a().a != null) {
                    hk.a().a.c();
                }
                if (hk.a().c != null) {
                    jr.a().b(new lw() { // from class: com.flurry.sdk.ja.5.1
                        @Override // com.flurry.sdk.lw
                        public final void a() {
                            hk.a().c.c = true;
                        }
                    });
                }
            }
        });
        if (je.a().c()) {
            jr.a().b(new lw() { // from class: com.flurry.sdk.ja.6
                @Override // com.flurry.sdk.lw
                public final void a() {
                    ja.this.a(false, j);
                }
            });
        }
        li.a().b("Gender", this);
        li.a().b("UserId", this);
        li.a().b("Age", this);
        li.a().b("LogEvents", this);
    }

    @Override // com.flurry.sdk.lj.a
    public final void a(String str, Object obj) {
        char c2 = 65535;
        switch (str.hashCode()) {
            case -1752163738:
                if (str.equals("UserId")) {
                    c2 = 1;
                    break;
                }
                break;
            case -1720015653:
                if (str.equals("analyticsEnabled")) {
                    c2 = 4;
                    break;
                }
                break;
            case -738063011:
                if (str.equals("LogEvents")) {
                    c2 = 0;
                    break;
                }
                break;
            case 65759:
                if (str.equals("Age")) {
                    c2 = 3;
                    break;
                }
                break;
            case 2129321697:
                if (str.equals("Gender")) {
                    c2 = 2;
                    break;
                }
                break;
        }
        switch (c2) {
            case 0:
                this.k = ((Boolean) obj).booleanValue();
                kf.a(4, a, "onSettingUpdate, LogEvents = " + this.k);
                return;
            case 1:
                this.l = (String) obj;
                kf.a(4, a, "onSettingUpdate, UserId = " + this.l);
                return;
            case 2:
                this.m = ((Byte) obj).byteValue();
                kf.a(4, a, "onSettingUpdate, Gender = " + ((int) this.m));
                return;
            case 3:
                this.n = (Long) obj;
                kf.a(4, a, "onSettingUpdate, Birthdate = " + this.n);
                return;
            case 4:
                this.o = ((Boolean) obj).booleanValue();
                kf.a(4, a, "onSettingUpdate, AnalyticsEnabled = " + this.o);
                return;
            default:
                kf.a(6, a, "onSettingUpdate internal error!");
                return;
        }
    }

    final void a(Context context) {
        Bundle extras;
        if ((context instanceof Activity) && (extras = ((Activity) context).getIntent().getExtras()) != null) {
            kf.a(3, a, "Launch Options Bundle is present " + extras.toString());
            for (String str : extras.keySet()) {
                if (str != null) {
                    Object obj = extras.get(str);
                    String obj2 = obj != null ? obj.toString() : "null";
                    this.x.put(str, new ArrayList(Arrays.asList(obj2)));
                    kf.a(3, a, "Launch options Key: " + str + ". Its value: " + obj2);
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x0138  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0156  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x01df  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x01da  */
    @android.annotation.TargetApi(18)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    final void a(boolean r11) {
        /*
            Method dump skipped, instructions count: 497
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.flurry.sdk.ja.a(boolean):void");
    }

    private synchronized void b(long j) {
        for (iv ivVar : this.A) {
            if (ivVar.b && !ivVar.c) {
                ivVar.c = true;
                ivVar.d = j - ivVar.g;
                kf.a(3, "FlurryAgent", "Ended event '" + ivVar.a + "' (" + ivVar.g + ") after " + ivVar.d + "ms");
            }
        }
    }

    final synchronized iy a(long j, long j2, long j3, int i) {
        iy iyVar;
        iz izVar = new iz();
        izVar.a = jn.a().i();
        izVar.b = j;
        izVar.c = j2;
        izVar.d = j3;
        izVar.e = this.y;
        jd.a();
        jq i2 = jd.i();
        izVar.f = i2 != null ? i2.d() : null;
        jd.a();
        jq i3 = jd.i();
        izVar.g = i3 != null ? i3.e() : null;
        jd.a();
        jq i4 = jd.i();
        izVar.h = i4 != null ? i4.f() : null;
        jh.a();
        izVar.i = Locale.getDefault().getLanguage() + Document.ID_SEPARATOR + Locale.getDefault().getCountry();
        jh.a();
        izVar.j = TimeZone.getDefault().getID();
        izVar.k = i;
        izVar.l = this.v != -1 ? this.v : lp.e();
        izVar.m = this.l == null ? "" : this.l;
        izVar.n = ji.a().e();
        izVar.o = this.F;
        izVar.p = this.m;
        izVar.q = this.n;
        izVar.r = this.z;
        izVar.s = this.A;
        izVar.t = this.B;
        izVar.v = this.D;
        izVar.u = this.E;
        try {
            iyVar = new iy(izVar);
        } catch (IOException e2) {
            kf.a(5, a, "Error creating analytics session report: " + e2);
            iyVar = null;
        }
        if (iyVar == null) {
            kf.e(a, "New session report wasn't created");
        }
        return iyVar;
    }

    public final synchronized FlurryEventRecordStatus a$7a1fda5(String str, Map<String, String> map) {
        FlurryEventRecordStatus flurryEventRecordStatus;
        FlurryEventRecordStatus flurryEventRecordStatus2 = FlurryEventRecordStatus.kFlurryEventRecorded;
        if (!this.o) {
            flurryEventRecordStatus = FlurryEventRecordStatus.kFlurryEventAnalyticsDisabled;
            kf.e(a, "Analytics has been disabled, not logging event.");
        } else {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            jd.a();
            long e2 = elapsedRealtime - jd.e();
            final String b2 = lr.b(str);
            if (b2.length() == 0) {
                flurryEventRecordStatus = FlurryEventRecordStatus.kFlurryEventFailed;
            } else {
                iu iuVar = this.z.get(b2);
                if (iuVar == null) {
                    if (this.z.size() < b) {
                        iu iuVar2 = new iu();
                        iuVar2.a = 1;
                        this.z.put(b2, iuVar2);
                        kf.e(a, "Event count started: " + b2);
                        flurryEventRecordStatus = flurryEventRecordStatus2;
                    } else {
                        kf.e(a, "Too many different events. Event not counted: " + b2);
                        flurryEventRecordStatus = FlurryEventRecordStatus.kFlurryEventUniqueCountExceeded;
                    }
                } else {
                    iuVar.a++;
                    kf.e(a, "Event count incremented: " + b2);
                    flurryEventRecordStatus = FlurryEventRecordStatus.kFlurryEventRecorded;
                }
                if (this.k && this.A.size() < d && this.C < e) {
                    final Map<String, String> emptyMap = map == null ? Collections.emptyMap() : map;
                    if (emptyMap.size() > c) {
                        kf.e(a, "MaxEventParams exceeded: " + emptyMap.size());
                        flurryEventRecordStatus = FlurryEventRecordStatus.kFlurryEventParamsCountExceeded;
                    } else {
                        iv ivVar = new iv(this.q.incrementAndGet(), b2, emptyMap, e2);
                        if (ivVar.b().length + this.C <= e) {
                            this.A.add(ivVar);
                            this.C = ivVar.b().length + this.C;
                            flurryEventRecordStatus = FlurryEventRecordStatus.kFlurryEventRecorded;
                            if (this.o && hk.a().a != null) {
                                jr.a().b(new Runnable() { // from class: com.flurry.sdk.ja.7
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        hk.a().a.a(b2, emptyMap);
                                    }
                                });
                            }
                        } else {
                            this.C = e;
                            this.B = false;
                            kf.e(a, "Event Log size exceeded. No more event details logged.");
                            flurryEventRecordStatus = FlurryEventRecordStatus.kFlurryEventLogCountExceeded;
                        }
                    }
                } else {
                    this.B = false;
                }
            }
        }
        return flurryEventRecordStatus;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0059  */
    /* JADX WARN: Removed duplicated region for block: B:7:0x001e A[Catch: all -> 0x009c, TRY_LEAVE, TryCatch #0 {, blocks: (B:26:0x0004, B:5:0x000e, B:7:0x001e, B:13:0x005c, B:15:0x0064, B:17:0x0070, B:20:0x007b, B:12:0x00a3), top: B:25:0x0004 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final synchronized void a(java.lang.String r10, java.lang.String r11, java.lang.String r12, java.lang.Throwable r13) {
        /*
            r9 = this;
            r0 = 0
            monitor-enter(r9)
            if (r10 == 0) goto L57
            java.lang.String r1 = "uncaught"
            boolean r1 = r1.equals(r10)     // Catch: java.lang.Throwable -> L9c
            if (r1 == 0) goto L57
            r1 = 1
        Le:
            int r2 = r9.E     // Catch: java.lang.Throwable -> L9c
            int r2 = r2 + 1
            r9.E = r2     // Catch: java.lang.Throwable -> L9c
            java.util.List<com.flurry.sdk.it> r2 = r9.D     // Catch: java.lang.Throwable -> L9c
            int r2 = r2.size()     // Catch: java.lang.Throwable -> L9c
            int r3 = com.flurry.sdk.ja.f     // Catch: java.lang.Throwable -> L9c
            if (r2 >= r3) goto L59
            long r0 = java.lang.System.currentTimeMillis()     // Catch: java.lang.Throwable -> L9c
            java.lang.Long r2 = java.lang.Long.valueOf(r0)     // Catch: java.lang.Throwable -> L9c
            com.flurry.sdk.it r0 = new com.flurry.sdk.it     // Catch: java.lang.Throwable -> L9c
            java.util.concurrent.atomic.AtomicInteger r1 = r9.r     // Catch: java.lang.Throwable -> L9c
            int r1 = r1.incrementAndGet()     // Catch: java.lang.Throwable -> L9c
            long r2 = r2.longValue()     // Catch: java.lang.Throwable -> L9c
            r4 = r10
            r5 = r11
            r6 = r12
            r7 = r13
            r0.<init>(r1, r2, r4, r5, r6, r7)     // Catch: java.lang.Throwable -> L9c
            java.util.List<com.flurry.sdk.it> r1 = r9.D     // Catch: java.lang.Throwable -> L9c
            r1.add(r0)     // Catch: java.lang.Throwable -> L9c
            java.lang.String r1 = com.flurry.sdk.ja.a     // Catch: java.lang.Throwable -> L9c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L9c
            java.lang.String r3 = "Error logged: "
            r2.<init>(r3)     // Catch: java.lang.Throwable -> L9c
            java.lang.String r0 = r0.a     // Catch: java.lang.Throwable -> L9c
            java.lang.StringBuilder r0 = r2.append(r0)     // Catch: java.lang.Throwable -> L9c
            java.lang.String r0 = r0.toString()     // Catch: java.lang.Throwable -> L9c
            com.flurry.sdk.kf.e(r1, r0)     // Catch: java.lang.Throwable -> L9c
        L55:
            monitor-exit(r9)
            return
        L57:
            r1 = r0
            goto Le
        L59:
            if (r1 == 0) goto La3
            r8 = r0
        L5c:
            java.util.List<com.flurry.sdk.it> r0 = r9.D     // Catch: java.lang.Throwable -> L9c
            int r0 = r0.size()     // Catch: java.lang.Throwable -> L9c
            if (r8 >= r0) goto L55
            java.util.List<com.flurry.sdk.it> r0 = r9.D     // Catch: java.lang.Throwable -> L9c
            java.lang.Object r0 = r0.get(r8)     // Catch: java.lang.Throwable -> L9c
            com.flurry.sdk.it r0 = (com.flurry.sdk.it) r0     // Catch: java.lang.Throwable -> L9c
            java.lang.String r1 = r0.a     // Catch: java.lang.Throwable -> L9c
            if (r1 == 0) goto L9f
            java.lang.String r1 = "uncaught"
            java.lang.String r0 = r0.a     // Catch: java.lang.Throwable -> L9c
            boolean r0 = r1.equals(r0)     // Catch: java.lang.Throwable -> L9c
            if (r0 != 0) goto L9f
            long r0 = java.lang.System.currentTimeMillis()     // Catch: java.lang.Throwable -> L9c
            java.lang.Long r2 = java.lang.Long.valueOf(r0)     // Catch: java.lang.Throwable -> L9c
            com.flurry.sdk.it r0 = new com.flurry.sdk.it     // Catch: java.lang.Throwable -> L9c
            java.util.concurrent.atomic.AtomicInteger r1 = r9.r     // Catch: java.lang.Throwable -> L9c
            int r1 = r1.incrementAndGet()     // Catch: java.lang.Throwable -> L9c
            long r2 = r2.longValue()     // Catch: java.lang.Throwable -> L9c
            r4 = r10
            r5 = r11
            r6 = r12
            r7 = r13
            r0.<init>(r1, r2, r4, r5, r6, r7)     // Catch: java.lang.Throwable -> L9c
            java.util.List<com.flurry.sdk.it> r1 = r9.D     // Catch: java.lang.Throwable -> L9c
            r1.set(r8, r0)     // Catch: java.lang.Throwable -> L9c
            goto L55
        L9c:
            r0 = move-exception
            monitor-exit(r9)
            throw r0
        L9f:
            int r0 = r8 + 1
            r8 = r0
            goto L5c
        La3:
            java.lang.String r0 = com.flurry.sdk.ja.a     // Catch: java.lang.Throwable -> L9c
            java.lang.String r1 = "Max errors logged. No more errors logged."
            com.flurry.sdk.kf.e(r0, r1)     // Catch: java.lang.Throwable -> L9c
            goto L55
        */
        throw new UnsupportedOperationException("Method not decompiled: com.flurry.sdk.ja.a(java.lang.String, java.lang.String, java.lang.String, java.lang.Throwable):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void a(boolean z, long j) {
        final byte[] bArr;
        if (!this.o) {
            kf.a(3, a, "Analytics disabled, not sending agent report.");
        } else if (z || !this.w.isEmpty()) {
            kf.a(3, a, "generating agent report");
            try {
                bArr = new iw(jr.a().d, jn.a().i(), this.j, je.a().d(), this.u, j, this.w, Collections.unmodifiableMap(je.a().a), this.G.a(), this.x, jt.a().c(), System.currentTimeMillis()).a;
            } catch (Exception e2) {
                kf.e(a, "Exception while generating report: " + e2);
                bArr = null;
            }
            if (bArr == null) {
                kf.e(a, "Error generating report");
            } else {
                kf.a(3, a, "generated report of size " + bArr.length + " with " + this.w.size() + " reports.");
                final ix ixVar = hk.a().b;
                final String sb = new StringBuilder().append(js.a()).toString();
                final String str = jr.a().d;
                if (bArr == null || bArr.length == 0) {
                    kf.a(6, ixVar.b, "Report that has to be sent is EMPTY or NULL");
                } else {
                    jr.a().b(new lw() { // from class: com.flurry.sdk.kr.3
                        final /* synthetic */ byte[] a;
                        final /* synthetic */ String b;
                        final /* synthetic */ String c;

                        public AnonymousClass3(final byte[] bArr2, final String str2, final String sb2) {
                            r2 = bArr2;
                            r3 = str2;
                            r4 = sb2;
                        }

                        @Override // com.flurry.sdk.lw
                        public final void a() {
                            kr krVar = kr.this;
                            byte[] bArr2 = r2;
                            String str2 = krVar.e + r3 + Document.ID_SEPARATOR + r4;
                            ks ksVar = new ks(bArr2);
                            String str3 = ksVar.a;
                            new jy(jr.a().a.getFileStreamPath(ks.a(str3)), ".yflurrydatasenderblock.", 1, new lc<ks>() { // from class: com.flurry.sdk.kr.5
                                AnonymousClass5() {
                                }

                                @Override // com.flurry.sdk.lc
                                public final kz<ks> a$1f969724() {
                                    return new ks.a();
                                }
                            }).a(ksVar);
                            kf.a(5, krVar.b, "Saving Block File " + str3 + " at " + jr.a().a.getFileStreamPath(ks.a(str3)));
                            krVar.d.a(ksVar, str2);
                        }
                    });
                    ixVar.b();
                }
            }
            this.w.clear();
            this.i.b();
        }
    }

    public final synchronized void d() {
        kf.a(4, a, "Saving persistent agent data.");
        this.i.a(this.w);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void e() {
        kf.a(4, a, "Loading persistent session report data.");
        List<iy> a2 = this.i.a();
        if (a2 != null) {
            this.w.addAll(a2);
        } else if (this.h.exists()) {
            kf.a(4, a, "Legacy persistent agent data found, converting.");
            jb a3 = hn.a(this.h);
            if (a3 != null) {
                boolean z = a3.a;
                long j = a3.b;
                if (j <= 0) {
                    jd.a();
                    j = jd.d();
                }
                this.j = z;
                this.u = j;
                f();
                List unmodifiableList = Collections.unmodifiableList(a3.c);
                if (unmodifiableList != null) {
                    this.w.addAll(unmodifiableList);
                }
            }
            this.h.delete();
            d();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void f() {
        SharedPreferences.Editor edit = jr.a().a.getSharedPreferences("FLURRY_SHARED_PREFERENCES", 0).edit();
        edit.putBoolean("com.flurry.sdk.previous_successful_report", this.j);
        edit.putLong("com.flurry.sdk.initial_run_time", this.u);
        edit.commit();
    }

    static /* synthetic */ void d(ja jaVar) {
        SharedPreferences sharedPreferences = jr.a().a.getSharedPreferences("FLURRY_SHARED_PREFERENCES", 0);
        jaVar.j = sharedPreferences.getBoolean("com.flurry.sdk.previous_successful_report", false);
        jd.a();
        jaVar.u = sharedPreferences.getLong("com.flurry.sdk.initial_run_time", jd.d());
    }
}
