package com.flurry.sdk;

import com.flurry.sdk.kp;
import com.nuance.swype.input.IME;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public abstract class kq<ReportInfo extends kp> {
    private static final String a = kq.class.getSimpleName();
    public static long b = IME.RETRY_DELAY_IN_MILLIS;
    public boolean c;
    public long d;
    private final jy<List<ReportInfo>> f;
    private int h;
    private final int e = Integer.MAX_VALUE;
    private final List<ReportInfo> g = new ArrayList();
    private final Runnable i = new lw() { // from class: com.flurry.sdk.kq.1
        @Override // com.flurry.sdk.lw
        public final void a() {
            kq.this.b();
        }
    };
    private final ka<jj> j = new ka<jj>() { // from class: com.flurry.sdk.kq.2
        @Override // com.flurry.sdk.ka
        public final /* bridge */ /* synthetic */ void a(jj jjVar) {
            if (jjVar.a) {
                kq.this.b();
            }
        }
    };

    public abstract jy<List<ReportInfo>> a();

    public abstract void a(ReportInfo reportinfo);

    public kq() {
        kb.a().a("com.flurry.android.sdk.NetworkStateEvent", this.j);
        this.f = a();
        this.d = b;
        this.h = -1;
        jr.a().b(new lw() { // from class: com.flurry.sdk.kq.3
            @Override // com.flurry.sdk.lw
            public final void a() {
                kq.this.b(kq.this.g);
                kq.this.b();
            }
        });
    }

    public final synchronized void b(ReportInfo reportinfo) {
        if (reportinfo != null) {
            this.g.add(reportinfo);
            jr.a().b(new lw() { // from class: com.flurry.sdk.kq.5
                @Override // com.flurry.sdk.lw
                public final void a() {
                    kq.this.b();
                }
            });
        }
    }

    public final synchronized void c(ReportInfo reportinfo) {
        reportinfo.o = true;
        jr.a().b(new lw() { // from class: com.flurry.sdk.kq.6
            @Override // com.flurry.sdk.lw
            public final void a() {
                kq.this.e();
            }
        });
    }

    public final synchronized void d(ReportInfo reportinfo) {
        reportinfo.a_();
        jr.a().b(new lw() { // from class: com.flurry.sdk.kq.7
            @Override // com.flurry.sdk.lw
            public final void a() {
                kq.this.e();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void b() {
        if (!this.c) {
            if (this.h >= 0) {
                kf.a(3, a, "Transmit is in progress");
            } else {
                g();
                if (this.g.isEmpty()) {
                    this.d = b;
                    this.h = -1;
                } else {
                    this.h = 0;
                    jr.a().b(new lw() { // from class: com.flurry.sdk.kq.8
                        @Override // com.flurry.sdk.lw
                        public final void a() {
                            kq.this.e();
                        }
                    });
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:11:0x002b A[Catch: all -> 0x003f, TRY_LEAVE, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x000d, B:7:0x0017, B:11:0x002b, B:15:0x003b, B:20:0x0031), top: B:2:0x0001 }] */
    /* JADX WARN: Removed duplicated region for block: B:15:0x003b A[Catch: all -> 0x003f, TRY_LEAVE, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x000d, B:7:0x0017, B:11:0x002b, B:15:0x003b, B:20:0x0031), top: B:2:0x0001 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized void e() {
        /*
            r4 = this;
            monitor-enter(r4)
            com.flurry.sdk.lr.b()     // Catch: java.lang.Throwable -> L3f
            r1 = 0
            com.flurry.sdk.jk r0 = com.flurry.sdk.jk.a()     // Catch: java.lang.Throwable -> L3f
            boolean r0 = r0.b     // Catch: java.lang.Throwable -> L3f
            if (r0 == 0) goto L30
        Ld:
            int r0 = r4.h     // Catch: java.lang.Throwable -> L3f
            java.util.List<ReportInfo extends com.flurry.sdk.kp> r2 = r4.g     // Catch: java.lang.Throwable -> L3f
            int r2 = r2.size()     // Catch: java.lang.Throwable -> L3f
            if (r0 >= r2) goto L39
            java.util.List<ReportInfo extends com.flurry.sdk.kp> r0 = r4.g     // Catch: java.lang.Throwable -> L3f
            int r2 = r4.h     // Catch: java.lang.Throwable -> L3f
            int r3 = r2 + 1
            r4.h = r3     // Catch: java.lang.Throwable -> L3f
            java.lang.Object r0 = r0.get(r2)     // Catch: java.lang.Throwable -> L3f
            com.flurry.sdk.kp r0 = (com.flurry.sdk.kp) r0     // Catch: java.lang.Throwable -> L3f
            boolean r2 = r0.o     // Catch: java.lang.Throwable -> L3f
            if (r2 != 0) goto Ld
        L29:
            if (r0 != 0) goto L3b
            r4.f()     // Catch: java.lang.Throwable -> L3f
        L2e:
            monitor-exit(r4)
            return
        L30:
            r0 = 3
            java.lang.String r2 = com.flurry.sdk.kq.a     // Catch: java.lang.Throwable -> L3f
            java.lang.String r3 = "Network is not available, aborting transmission"
            com.flurry.sdk.kf.a(r0, r2, r3)     // Catch: java.lang.Throwable -> L3f
        L39:
            r0 = r1
            goto L29
        L3b:
            r4.a(r0)     // Catch: java.lang.Throwable -> L3f
            goto L2e
        L3f:
            r0 = move-exception
            monitor-exit(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.flurry.sdk.kq.e():void");
    }

    private synchronized void f() {
        g();
        a(this.g);
        if (this.c) {
            kf.a(3, a, "Reporter paused");
            this.d = b;
        } else if (this.g.isEmpty()) {
            kf.a(3, a, "All reports sent successfully");
            this.d = b;
        } else {
            this.d <<= 1;
            kf.a(3, a, "One or more reports failed to send, backing off: " + this.d + "ms");
            jr.a().a(this.i, this.d);
        }
        this.h = -1;
    }

    public synchronized void b(List<ReportInfo> list) {
        lr.b();
        List<ReportInfo> a2 = this.f.a();
        if (a2 != null) {
            list.addAll(a2);
        }
    }

    public synchronized void a(List<ReportInfo> list) {
        lr.b();
        this.f.a(new ArrayList(list));
    }

    private synchronized void g() {
        Iterator<ReportInfo> it = this.g.iterator();
        while (it.hasNext()) {
            ReportInfo next = it.next();
            if (next.o) {
                kf.a(3, a, "Url transmitted - " + next.q + " Attempts: " + next.p);
                it.remove();
            } else if (next.p > next.s) {
                kf.a(3, a, "Exceeded max no of attempts - " + next.q + " Attempts: " + next.p);
                it.remove();
            } else if (System.currentTimeMillis() > next.n && next.p > 0) {
                kf.a(3, a, "Expired: Time expired - " + next.q + " Attempts: " + next.p);
                it.remove();
            }
        }
    }
}
