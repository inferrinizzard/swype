package com.flurry.sdk;

import com.flurry.sdk.ii;
import com.flurry.sdk.kl;
import com.flurry.sdk.kn;
import com.nuance.swypeconnect.ac.ACReportingService;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class ih extends kq<ii> {
    public static long a;
    private static final String e = ih.class.getSimpleName();

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Type inference failed for: r2v26, types: [byte[], RequestObjectType] */
    @Override // com.flurry.sdk.kq
    public final /* synthetic */ void a(ii iiVar) {
        final ii iiVar2 = iiVar;
        kf.a(3, e, "Sending next pulse report to " + iiVar2.k + " at: " + iiVar2.r);
        jd.a();
        long d = jd.d();
        if (d == 0) {
            d = a;
        }
        jd.a();
        long g = jd.g();
        if (g == 0) {
            g = System.currentTimeMillis() - d;
        }
        final ij ijVar = new ij(iiVar2, d, g, iiVar2.p);
        kl klVar = new kl();
        klVar.f = iiVar2.r;
        klVar.w = ACReportingService.MAXIMUM_DATABASE_ENTRIES;
        if (iiVar2.e.equals(ip.POST)) {
            klVar.c = new kv();
            if (iiVar2.j != null) {
                klVar.b = iiVar2.j.getBytes();
            }
            klVar.g = kn.a.kPost;
        } else {
            klVar.g = kn.a.kGet;
        }
        klVar.h = iiVar2.h * 1000;
        klVar.i = iiVar2.i * 1000;
        klVar.l = true;
        klVar.r = true;
        klVar.s = (iiVar2.h + iiVar2.i) * 1000;
        Map<String, String> map = iiVar2.f;
        if (map != null) {
            for (String str : iiVar2.f.keySet()) {
                klVar.a(str, map.get(str));
            }
        }
        klVar.j = false;
        klVar.a = new kl.a<byte[], String>() { // from class: com.flurry.sdk.ih.2
            @Override // com.flurry.sdk.kl.a
            public final /* synthetic */ void a(kl<byte[], String> klVar2, String str2) {
                String str3 = str2;
                kf.a(3, ih.e, "Pulse report to " + iiVar2.k + " for " + iiVar2.m.c + ", HTTP status code is: " + klVar2.p);
                int i = klVar2.p;
                ij ijVar2 = ijVar;
                int i2 = (int) klVar2.n;
                if (i2 >= 0) {
                    ijVar2.k = i2 + ijVar2.k;
                } else if (ijVar2.k <= 0) {
                    ijVar2.k = 0L;
                }
                ijVar.e = i;
                if (klVar2.c()) {
                    if (i >= 200 && i < 300) {
                        ih.b(ih.this, ijVar, iiVar2);
                        return;
                    } else if (i >= 300 && i < 400) {
                        ih.a(ih.this, ijVar, iiVar2, klVar2);
                        return;
                    } else {
                        kf.a(3, ih.e, iiVar2.m.c + " report failed sending to : " + iiVar2.k);
                        ih.a(ih.this, ijVar, iiVar2, str3);
                        return;
                    }
                }
                Exception exc = klVar2.o;
                if (klVar2.t || (klVar2.o != null && (klVar2.o instanceof SocketTimeoutException))) {
                    if (klVar2.e()) {
                        kf.a(3, ih.e, "Timeout occured when trying to connect to: " + iiVar2.k + ". Exception: " + klVar2.o.getMessage());
                    } else {
                        kf.a(3, ih.e, "Manually managed http request timeout occured for: " + iiVar2.k);
                    }
                    ih.a(ih.this, ijVar, iiVar2);
                    return;
                }
                kf.a(3, ih.e, "Error occured when trying to connect to: " + iiVar2.k + ". Exception: " + exc.getMessage());
                ih.a(ih.this, ijVar, iiVar2, str3);
            }
        };
        jp.a().a((Object) this, (ih) klVar);
    }

    public ih() {
        kq.b = 30000L;
        this.d = kq.b;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.flurry.sdk.kq
    public final jy<List<ii>> a() {
        return new jy<>(jr.a().a.getFileStreamPath(".yflurryanpulsecallbackreporter"), ".yflurryanpulsecallbackreporter", 2, new lc<List<ii>>() { // from class: com.flurry.sdk.ih.1
            @Override // com.flurry.sdk.lc
            public final kz<List<ii>> a$1f969724() {
                return new ky(new ii.a());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.flurry.sdk.kq
    public final synchronized void a(List<ii> list) {
        il.a().d();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.flurry.sdk.kq
    public final synchronized void b(List<ii> list) {
        il.a();
        List<im> e2 = il.e();
        if (e2 != null && e2.size() != 0) {
            kf.a(3, e, "Restoring " + e2.size() + " from report queue.");
            Iterator<im> it = e2.iterator();
            while (it.hasNext()) {
                il.a().b(it.next());
            }
            il.a();
            Iterator<im> it2 = il.c().iterator();
            while (it2.hasNext()) {
                for (ii iiVar : it2.next().a()) {
                    if (!iiVar.l) {
                        kf.a(3, e, "Callback for " + iiVar.m.c + " to " + iiVar.k + " not completed.  Adding to reporter queue.");
                        list.add(iiVar);
                    }
                }
            }
        }
    }

    static /* synthetic */ void a(ih ihVar, ij ijVar, ii iiVar) {
        il.a().b(ijVar);
        ihVar.c((ih) iiVar);
    }

    static /* synthetic */ void a(ih ihVar, ij ijVar, ii iiVar, String str) {
        boolean b = il.a().b(ijVar, str);
        kf.a(3, e, "Failed report retrying: " + b);
        if (b) {
            ihVar.d(iiVar);
        } else {
            ihVar.c((ih) iiVar);
        }
    }

    static /* synthetic */ void b(ih ihVar, ij ijVar, ii iiVar) {
        kf.a(3, e, iiVar.m.c + " report sent successfully to : " + iiVar.k);
        il.a().a(ijVar);
        ihVar.c((ih) iiVar);
    }

    static /* synthetic */ void a(ih ihVar, ij ijVar, ii iiVar, kl klVar) {
        String str = null;
        List<String> a2 = klVar.a("Location");
        if (a2 != null && a2.size() > 0) {
            str = ly.b(a2.get(0), iiVar.q);
        }
        boolean a3 = il.a().a(ijVar, str);
        if (a3) {
            kf.a(3, e, "Received redirect url. Retrying: " + str);
        } else {
            kf.a(3, e, "Received redirect url. Retrying: false");
        }
        if (!a3) {
            ihVar.c((ih) iiVar);
            return;
        }
        iiVar.r = str;
        klVar.f = str;
        if (klVar.q != null && klVar.q.a.containsKey("Location")) {
            klVar.q.b("Location");
        }
        jp.a().a((Object) ihVar, (ih) klVar);
    }
}
