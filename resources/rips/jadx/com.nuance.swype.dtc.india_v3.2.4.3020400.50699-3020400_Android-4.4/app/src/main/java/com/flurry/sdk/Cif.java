package com.flurry.sdk;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.text.TextUtils;
import bolts.MeasurementEvent;
import com.flurry.sdk.id;
import com.flurry.sdk.im;
import com.flurry.sdk.kl;
import com.flurry.sdk.kn;
import com.flurry.sdk.lj;
import com.nuance.swype.input.IME;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* renamed from: com.flurry.sdk.if, reason: invalid class name */
/* loaded from: classes.dex */
public class Cif implements lj.a {
    private static final String e = Cif.class.getSimpleName();
    private static String f = "https://proton.flurry.com/sdk/v1/config";
    private jy<id> i;
    private jy<List<im>> j;
    private boolean n;
    private String o;
    private boolean p;
    private boolean q;
    private long s;
    private boolean t;
    private hs u;
    private boolean v;
    public final Runnable a = new lw() { // from class: com.flurry.sdk.if.1
        @Override // com.flurry.sdk.lw
        public final void a() {
            Cif.this.f();
        }
    };
    public final ka<jf> b = new ka<jf>() { // from class: com.flurry.sdk.if.4
        @Override // com.flurry.sdk.ka
        public final /* bridge */ /* synthetic */ void a(jf jfVar) {
            Cif.this.f();
        }
    };
    public final ka<jg> c = new ka<jg>() { // from class: com.flurry.sdk.if.5
        @Override // com.flurry.sdk.ka
        public final /* bridge */ /* synthetic */ void a(jg jgVar) {
            Cif.this.f();
        }
    };
    public final ka<jj> d = new ka<jj>() { // from class: com.flurry.sdk.if.6
        @Override // com.flurry.sdk.ka
        public final /* bridge */ /* synthetic */ void a(jj jjVar) {
            if (jjVar.a) {
                Cif.this.f();
            }
        }
    };
    private final kj<hr> g = new kj<>("proton config request", new ir());
    private final kj<hs> h = new kj<>("proton config response", new is());
    private final ie k = new ie();
    private final jw<String, hv> l = new jw<>();
    private final List<im> m = new ArrayList();
    private long r = IME.RETRY_DELAY_IN_MILLIS;

    static /* synthetic */ boolean h(Cif cif) {
        cif.v = true;
        return true;
    }

    public Cif() {
        this.p = true;
        li a = li.a();
        this.n = ((Boolean) a.a("ProtonEnabled")).booleanValue();
        a.a("ProtonEnabled", (lj.a) this);
        kf.a(4, e, "initSettings, protonEnabled = " + this.n);
        this.o = (String) a.a("ProtonConfigUrl");
        a.a("ProtonConfigUrl", (lj.a) this);
        kf.a(4, e, "initSettings, protonConfigUrl = " + this.o);
        this.p = ((Boolean) a.a("analyticsEnabled")).booleanValue();
        a.a("analyticsEnabled", (lj.a) this);
        kf.a(4, e, "initSettings, AnalyticsEnabled = " + this.p);
        kb.a().a("com.flurry.android.sdk.IdProviderFinishedEvent", this.b);
        kb.a().a("com.flurry.android.sdk.IdProviderUpdatedAdvertisingId", this.c);
        kb.a().a("com.flurry.android.sdk.NetworkStateEvent", this.d);
        this.i = new jy<>(jr.a().a.getFileStreamPath(".yflurryprotonconfig." + Long.toString(lr.i(jr.a().d), 16)), ".yflurryprotonconfig.", 1, new lc<id>() { // from class: com.flurry.sdk.if.7
            @Override // com.flurry.sdk.lc
            public final kz<id> a$1f969724() {
                return new id.a();
            }
        });
        this.j = new jy<>(jr.a().a.getFileStreamPath(".yflurryprotonreport." + Long.toString(lr.i(jr.a().d), 16)), ".yflurryprotonreport.", 1, new lc<List<im>>() { // from class: com.flurry.sdk.if.8
            @Override // com.flurry.sdk.lc
            public final kz<List<im>> a$1f969724() {
                return new ky(new im.a());
            }
        });
        jr.a().b(new lw() { // from class: com.flurry.sdk.if.9
            @Override // com.flurry.sdk.lw
            public final void a() {
                Cif.this.i();
            }
        });
        jr.a().b(new lw() { // from class: com.flurry.sdk.if.10
            @Override // com.flurry.sdk.lw
            public final void a() {
                Cif.this.k();
            }
        });
    }

    @Override // com.flurry.sdk.lj.a
    public final void a(String str, Object obj) {
        char c = 65535;
        switch (str.hashCode()) {
            case -1720015653:
                if (str.equals("analyticsEnabled")) {
                    c = 2;
                    break;
                }
                break;
            case 640941243:
                if (str.equals("ProtonEnabled")) {
                    c = 0;
                    break;
                }
                break;
            case 1591403975:
                if (str.equals("ProtonConfigUrl")) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this.n = ((Boolean) obj).booleanValue();
                kf.a(4, e, "onSettingUpdate, protonEnabled = " + this.n);
                return;
            case 1:
                this.o = (String) obj;
                kf.a(4, e, "onSettingUpdate, protonConfigUrl = " + this.o);
                return;
            case 2:
                this.p = ((Boolean) obj).booleanValue();
                kf.a(4, e, "onSettingUpdate, AnalyticsEnabled = " + this.p);
                return;
            default:
                kf.a(6, e, "onSettingUpdate internal error!");
                return;
        }
    }

    public final synchronized void a() {
        if (this.n) {
            lr.b();
            jd.a();
            ih.a = jd.d();
            this.v = false;
            f();
        }
    }

    public final synchronized void b() {
        if (this.n) {
            lr.b();
            jd.a();
            b(jd.d());
            j();
        }
    }

    public final synchronized void a(long j) {
        if (this.n) {
            lr.b();
            b(j);
            b("flurry.session_end", (Map<String, String>) null);
            jr.a().b(new lw() { // from class: com.flurry.sdk.if.11
                @Override // com.flurry.sdk.lw
                public final void a() {
                    Cif.this.l();
                }
            });
        }
    }

    public final synchronized void c() {
        if (this.n) {
            lr.b();
            j();
        }
    }

    public final synchronized void a(String str, Map<String, String> map) {
        if (this.n) {
            lr.b();
            b(str, map);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void e() {
        if (this.n) {
            lr.b();
            SharedPreferences sharedPreferences = jr.a().a.getSharedPreferences("FLURRY_SHARED_PREFERENCES", 0);
            if (sharedPreferences.getBoolean("com.flurry.android.flurryAppInstall", true)) {
                b("flurry.app_install", (Map<String, String>) null);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean("com.flurry.android.flurryAppInstall", false);
                edit.apply();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r4v1, types: [byte[], RequestObjectType] */
    public synchronized void f() {
        if (this.n) {
            lr.b();
            if (this.q && je.a().c()) {
                final long currentTimeMillis = System.currentTimeMillis();
                final boolean z = !je.a().d();
                if (this.u != null) {
                    if (this.t != z) {
                        kf.a(3, e, "Limit ad tracking value has changed, purging");
                        this.u = null;
                    } else if (System.currentTimeMillis() < this.s + (this.u.b * 1000)) {
                        kf.a(3, e, "Cached Proton config valid, no need to refresh");
                        if (!this.v) {
                            this.v = true;
                            b("flurry.session_start", (Map<String, String>) null);
                        }
                    } else if (System.currentTimeMillis() >= this.s + (this.u.c * 1000)) {
                        kf.a(3, e, "Cached Proton config expired, purging");
                        this.u = null;
                        this.l.a();
                    }
                }
                jp.a().a(this);
                kf.a(3, e, "Requesting proton config");
                ?? g = g();
                if (g != 0) {
                    kl klVar = new kl();
                    klVar.f = TextUtils.isEmpty(this.o) ? f : this.o;
                    klVar.w = 5000;
                    klVar.g = kn.a.kPost;
                    klVar.a("Content-Type", "application/x-flurry;version=2");
                    klVar.a("Accept", "application/x-flurry;version=2");
                    klVar.a("FM-Checksum", Integer.toString(kj.a(g)));
                    klVar.c = new kv();
                    klVar.d = new kv();
                    klVar.b = g;
                    klVar.a = new kl.a<byte[], byte[]>() { // from class: com.flurry.sdk.if.2
                        @Override // com.flurry.sdk.kl.a
                        public final /* synthetic */ void a(kl<byte[], byte[]> klVar2, byte[] bArr) {
                            long j;
                            hs hsVar;
                            final byte[] bArr2 = bArr;
                            int i = klVar2.p;
                            kf.a(3, Cif.e, "Proton config request: HTTP status code is:" + i);
                            if (i == 400 || i == 406 || i == 412 || i == 415) {
                                Cif.this.r = IME.RETRY_DELAY_IN_MILLIS;
                                return;
                            }
                            if (klVar2.c() && bArr2 != null) {
                                jr.a().b(new lw() { // from class: com.flurry.sdk.if.2.1
                                    @Override // com.flurry.sdk.lw
                                    public final void a() {
                                        Cif.this.a(currentTimeMillis, z, bArr2);
                                    }
                                });
                                try {
                                    hsVar = (hs) Cif.this.h.b(bArr2);
                                } catch (Exception e2) {
                                    kf.a(5, Cif.e, "Failed to decode proton config response: " + e2);
                                    hsVar = null;
                                }
                                r1 = Cif.b(hsVar) ? hsVar : null;
                                if (r1 != null) {
                                    Cif.this.r = IME.RETRY_DELAY_IN_MILLIS;
                                    Cif.this.s = currentTimeMillis;
                                    Cif.this.t = z;
                                    Cif.this.u = r1;
                                    Cif.this.h();
                                    if (!Cif.this.v) {
                                        Cif.h(Cif.this);
                                        Cif.this.b("flurry.session_start", (Map<String, String>) null);
                                    }
                                    Cif.this.e();
                                }
                            }
                            if (r1 == null) {
                                long j2 = Cif.this.r << 1;
                                if (i == 429) {
                                    List<String> a = klVar2.a("Retry-After");
                                    if (!a.isEmpty()) {
                                        String str = a.get(0);
                                        kf.a(3, Cif.e, "Server returned retry time: " + str);
                                        try {
                                            j = Long.parseLong(str) * 1000;
                                        } catch (NumberFormatException e3) {
                                            kf.a(3, Cif.e, "Server returned nonsensical retry time");
                                        }
                                        Cif.this.r = j;
                                        kf.a(3, Cif.e, "Proton config request failed, backing off: " + Cif.this.r + "ms");
                                        jr.a().a(Cif.this.a, Cif.this.r);
                                    }
                                }
                                j = j2;
                                Cif.this.r = j;
                                kf.a(3, Cif.e, "Proton config request failed, backing off: " + Cif.this.r + "ms");
                                jr.a().a(Cif.this.a, Cif.this.r);
                            }
                        }
                    };
                    jp.a().a((Object) this, (Cif) klVar);
                }
            }
        }
    }

    private byte[] g() {
        try {
            hr hrVar = new hr();
            hrVar.a = jr.a().d;
            hrVar.b = lo.a(jr.a().a);
            hrVar.c = lo.b(jr.a().a);
            hrVar.d = js.a();
            hrVar.e = 3;
            jn.a();
            hrVar.f = jn.c();
            hrVar.g = !je.a().d();
            hrVar.h = new hu();
            hrVar.h.a = new ho();
            hrVar.h.a.a = Build.MODEL;
            hrVar.h.a.b = Build.BRAND;
            hrVar.h.a.c = Build.ID;
            hrVar.h.a.d = Build.DEVICE;
            hrVar.h.a.e = Build.PRODUCT;
            hrVar.h.a.f = Build.VERSION.RELEASE;
            hrVar.i = new ArrayList();
            for (Map.Entry entry : Collections.unmodifiableMap(je.a().a).entrySet()) {
                ht htVar = new ht();
                htVar.a = ((jm) entry.getKey()).c;
                if (((jm) entry.getKey()).d) {
                    htVar.b = new String((byte[]) entry.getValue());
                } else {
                    htVar.b = lr.b((byte[]) entry.getValue());
                }
                hrVar.i.add(htVar);
            }
            Location e2 = ji.a().e();
            if (e2 != null) {
                hrVar.j = new hy();
                hrVar.j.a = new hx();
                hrVar.j.a.a = lr.a(e2.getLatitude());
                hrVar.j.a.b = lr.a(e2.getLongitude());
                hrVar.j.a.c = (float) lr.a(e2.getAccuracy());
            }
            String str = (String) li.a().a("UserId");
            if (!str.equals("")) {
                hrVar.k = new ib();
                hrVar.k.a = str;
            }
            kj<hr> kjVar = this.g;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            kjVar.d.a(byteArrayOutputStream, hrVar);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            kf.a(3, kj.a, "Encoding " + kjVar.c + ": " + new String(byteArray));
            kx kxVar = new kx(new kv());
            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            kxVar.a(byteArrayOutputStream2, byteArray);
            byte[] byteArray2 = byteArrayOutputStream2.toByteArray();
            kj.c(byteArray2);
            return byteArray2;
        } catch (Exception e3) {
            kf.a(5, e, "Proton config request failed with exception: " + e3);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean b(hs hsVar) {
        boolean z;
        boolean z2;
        if (hsVar == null) {
            return false;
        }
        hq hqVar = hsVar.e;
        if (hqVar != null && hqVar.a != null) {
            for (int i = 0; i < hqVar.a.size(); i++) {
                hp hpVar = hqVar.a.get(i);
                if (hpVar != null) {
                    if (!hpVar.b.equals("") && hpVar.a != -1 && !hpVar.e.equals("")) {
                        List<hv> list = hpVar.c;
                        if (list != null) {
                            for (hv hvVar : list) {
                                if (hvVar.a.equals("")) {
                                    kf.a(3, e, "An event is missing a name");
                                    z2 = false;
                                    break;
                                }
                                if ((hvVar instanceof hw) && ((hw) hvVar).c.equals("")) {
                                    kf.a(3, e, "An event trigger is missing a param name");
                                    z2 = false;
                                    break;
                                }
                            }
                        }
                        z2 = true;
                        if (!z2) {
                        }
                    }
                    kf.a(3, e, "A callback template is missing required values");
                    z = false;
                    break;
                }
            }
        }
        z = true;
        if (z && (hsVar.e == null || hsVar.e.e == null || !hsVar.e.e.equals(""))) {
            return true;
        }
        kf.a(3, e, "Config response is missing required values.");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void h() {
        List<hp> list;
        List<hv> list2;
        if (this.u != null) {
            kf.a(5, e, "Processing config response");
            il.a(this.u.e.c);
            il.b(this.u.e.d * 1000);
            in a = in.a();
            String str = this.u.e.e;
            if (str != null && !str.endsWith(".do")) {
                kf.a(5, in.b, "overriding analytics agent report URL without an endpoint, are you sure?");
            }
            a.a = str;
            if (this.n) {
                li.a().a("analyticsEnabled", Boolean.valueOf(this.u.f.b));
            }
            this.l.a();
            hq hqVar = this.u.e;
            if (hqVar != null && (list = hqVar.a) != null) {
                for (hp hpVar : list) {
                    if (hpVar != null && (list2 = hpVar.c) != null) {
                        for (hv hvVar : list2) {
                            if (hvVar != null && !TextUtils.isEmpty(hvVar.a)) {
                                hvVar.b = hpVar;
                                this.l.a((jw<String, hv>) hvVar.a, (String) hvVar);
                            }
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void b(String str, Map<String, String> map) {
        iq iqVar;
        boolean z;
        kf.a(3, e, "Event triggered: " + str);
        if (!this.p) {
            kf.e(e, "Analytics and pulse have been disabled.");
        } else if (this.u == null) {
            kf.a(3, e, "Config response is empty. No events to fire.");
        } else {
            lr.b();
            if (!TextUtils.isEmpty(str)) {
                List<hv> a = this.l.a(str);
                if (a == null) {
                    kf.a(3, e, "No events to fire. Returning.");
                } else if (a.size() == 0) {
                    kf.a(3, e, "No events to fire. Returning.");
                } else {
                    long currentTimeMillis = System.currentTimeMillis();
                    boolean z2 = map != null;
                    char c = 65535;
                    switch (str.hashCode()) {
                        case 645204782:
                            if (str.equals("flurry.session_end")) {
                                c = 1;
                                break;
                            }
                            break;
                        case 1371447545:
                            if (str.equals("flurry.app_install")) {
                                c = 2;
                                break;
                            }
                            break;
                        case 1579613685:
                            if (str.equals("flurry.session_start")) {
                                c = 0;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            iqVar = iq.SESSION_START;
                            break;
                        case 1:
                            iqVar = iq.SESSION_END;
                            break;
                        case 2:
                            iqVar = iq.INSTALL;
                            break;
                        default:
                            iqVar = iq.APPLICATION_EVENT;
                            break;
                    }
                    HashMap hashMap = new HashMap();
                    for (hv hvVar : a) {
                        boolean z3 = false;
                        if (hvVar instanceof hw) {
                            kf.a(4, e, "Event contains triggers.");
                            String[] strArr = ((hw) hvVar).d;
                            if (strArr == null) {
                                kf.a(4, e, "Template does not contain trigger values. Firing.");
                                z3 = true;
                            } else if (strArr.length == 0) {
                                kf.a(4, e, "Template does not contain trigger values. Firing.");
                                z3 = true;
                            } else if (map == null) {
                                kf.a(4, e, "Publisher has not passed in params list. Not firing.");
                            }
                            String str2 = map.get(((hw) hvVar).c);
                            if (str2 == null) {
                                kf.a(4, e, "Publisher params has no value associated with proton key. Not firing.");
                            } else {
                                int i = 0;
                                while (true) {
                                    if (i >= strArr.length) {
                                        z = z3;
                                    } else if (!strArr[i].equals(str2)) {
                                        i++;
                                    } else {
                                        z = true;
                                    }
                                }
                                if (!z) {
                                    kf.a(4, e, "Publisher params list does not match proton param values. Not firing.");
                                } else {
                                    kf.a(4, e, "Publisher params match proton values. Firing.");
                                }
                            }
                        }
                        hp hpVar = hvVar.b;
                        if (hpVar == null) {
                            kf.a(3, e, "Template is empty. Not firing current event.");
                        } else {
                            kf.a(3, e, "Creating callback report for partner: " + hpVar.b);
                            HashMap hashMap2 = new HashMap();
                            hashMap2.put(MeasurementEvent.MEASUREMENT_EVENT_NAME_KEY, str);
                            hashMap2.put("event_time_millis", String.valueOf(currentTimeMillis));
                            String a2 = this.k.a(hpVar.e, hashMap2);
                            String str3 = null;
                            if (hpVar.f != null) {
                                str3 = this.k.a(hpVar.f, hashMap2);
                            }
                            hashMap.put(Long.valueOf(hpVar.a), new ii(hpVar.b, hpVar.a, a2, System.currentTimeMillis() + 259200000, this.u.e.b, hpVar.g, hpVar.d, hpVar.j, hpVar.i, hpVar.h, str3));
                        }
                    }
                    if (hashMap.size() != 0) {
                        jd.a();
                        long d = jd.d();
                        jd.a();
                        im imVar = new im(str, z2, d, jd.g(), iqVar, hashMap);
                        if ("flurry.session_end".equals(str)) {
                            kf.a(3, e, "Storing Pulse callbacks for event: " + str);
                            this.m.add(imVar);
                        } else {
                            kf.a(3, e, "Firing Pulse callbacks for event: " + str);
                            il.a().a(imVar);
                        }
                    }
                }
            }
        }
    }

    private synchronized void b(long j) {
        Iterator<im> it = this.m.iterator();
        while (it.hasNext()) {
            if (j == it.next().a) {
                it.remove();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void i() {
        hs hsVar;
        id a = this.i.a();
        if (a != null) {
            try {
                hsVar = this.h.b(a.c);
            } catch (Exception e2) {
                kf.a(5, e, "Failed to decode saved proton config response: " + e2);
                this.i.b();
                hsVar = null;
            }
            if (!b(hsVar)) {
                hsVar = null;
            }
            if (hsVar != null) {
                kf.a(4, e, "Loaded saved proton config response");
                this.r = IME.RETRY_DELAY_IN_MILLIS;
                this.s = a.a;
                this.t = a.b;
                this.u = hsVar;
                h();
            }
        }
        this.q = true;
        jr.a().b(new lw() { // from class: com.flurry.sdk.if.3
            @Override // com.flurry.sdk.lw
            public final void a() {
                Cif.this.f();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void a(long j, boolean z, byte[] bArr) {
        if (bArr != null) {
            kf.a(4, e, "Saving proton config response");
            id idVar = new id();
            idVar.a = j;
            idVar.b = z;
            idVar.c = bArr;
            this.i.a(idVar);
        }
    }

    private synchronized void j() {
        if (!this.p) {
            kf.e(e, "Analytics disabled, not sending pulse reports.");
        } else {
            kf.a(4, e, "Sending " + this.m.size() + " queued reports.");
            for (im imVar : this.m) {
                kf.a(3, e, "Firing Pulse callbacks for event: " + imVar.c);
                il.a().a(imVar);
            }
            m();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void k() {
        kf.a(4, e, "Loading queued report data.");
        List<im> a = this.j.a();
        if (a != null) {
            this.m.addAll(a);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void l() {
        kf.a(4, e, "Saving queued report data.");
        this.j.a(this.m);
    }

    private synchronized void m() {
        this.m.clear();
        this.j.b();
    }
}
