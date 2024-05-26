package com.flurry.sdk;

import android.widget.Toast;
import com.flurry.sdk.kl;
import com.flurry.sdk.kn;
import com.flurry.sdk.lj;
import com.nuance.swypeconnect.ac.ACReportingService;
import java.util.Arrays;

/* loaded from: classes.dex */
public class ix extends kr implements lj.a {
    private static final String a = ix.class.getSimpleName();
    private static String f = "http://data.flurry.com/aap.do";
    private static String g = "https://data.flurry.com/aap.do";
    private String h;
    private boolean i;

    public ix() {
        this((byte) 0);
    }

    private ix(byte b) {
        super("Analytics", ix.class.getSimpleName());
        this.e = "AnalyticsData_";
        li a2 = li.a();
        this.i = ((Boolean) a2.a("UseHttps")).booleanValue();
        a2.a("UseHttps", (lj.a) this);
        kf.a(4, a, "initSettings, UseHttps = " + this.i);
        String str = (String) a2.a("ReportUrl");
        a2.a("ReportUrl", (lj.a) this);
        b(str);
        kf.a(4, a, "initSettings, ReportUrl = " + str);
        b();
    }

    @Override // com.flurry.sdk.lj.a
    public final void a(String str, Object obj) {
        char c = 65535;
        switch (str.hashCode()) {
            case -239660092:
                if (str.equals("UseHttps")) {
                    c = 0;
                    break;
                }
                break;
            case 1650629499:
                if (str.equals("ReportUrl")) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this.i = ((Boolean) obj).booleanValue();
                kf.a(4, a, "onSettingUpdate, UseHttps = " + this.i);
                return;
            case 1:
                String str2 = (String) obj;
                b(str2);
                kf.a(4, a, "onSettingUpdate, ReportUrl = " + str2);
                return;
            default:
                kf.a(6, a, "onSettingUpdate internal error!");
                return;
        }
    }

    private void b(String str) {
        if (str != null && !str.endsWith(".do")) {
            kf.a(5, a, "overriding analytics agent report URL without an endpoint, are you sure?");
        }
        this.h = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.flurry.sdk.kr
    public final void a(String str, String str2, final int i) {
        jr.a().b(new lw() { // from class: com.flurry.sdk.ix.2
            @Override // com.flurry.sdk.lw
            public final void a() {
                if (i == 200) {
                    hk.a();
                    ja c = hk.c();
                    if (c == null) {
                        return;
                    }
                    c.j = true;
                }
            }
        });
        super.a(str, str2, i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.flurry.sdk.kr
    public final void a(byte[] bArr, final String str, final String str2) {
        String str3;
        if (this.h != null) {
            str3 = this.h;
        } else if (this.i) {
            str3 = g;
        } else {
            str3 = f;
        }
        kf.a(4, a, "FlurryDataSender: start upload data " + Arrays.toString(bArr) + " with id = " + str + " to " + str3);
        kl klVar = new kl();
        klVar.f = str3;
        klVar.w = ACReportingService.MAXIMUM_DATABASE_ENTRIES;
        klVar.g = kn.a.kPost;
        klVar.a("Content-Type", "application/octet-stream");
        klVar.c = new kv();
        klVar.b = bArr;
        klVar.a = new kl.a<byte[], Void>() { // from class: com.flurry.sdk.ix.1
            @Override // com.flurry.sdk.kl.a
            public final /* synthetic */ void a(kl<byte[], Void> klVar2, Void r7) {
                final int i = klVar2.p;
                if (i > 0) {
                    kf.e(ix.a, "Analytics report sent.");
                    kf.a(3, ix.a, "FlurryDataSender: report " + str + " sent. HTTP response: " + i);
                    if (kf.c() <= 3 && kf.d()) {
                        jr.a().a(new Runnable() { // from class: com.flurry.sdk.ix.1.1
                            @Override // java.lang.Runnable
                            public final void run() {
                                Toast.makeText(jr.a().a, "SD HTTP Response Code: " + i, 0).show();
                            }
                        });
                    }
                    ix.this.a(str, str2, i);
                    ix.this.b();
                    return;
                }
                ix.a(ix.this, str);
            }
        };
        jp.a().a((Object) this, (ix) klVar);
    }

    static /* synthetic */ void a(final ix ixVar, final String str) {
        jr.a().b(new lw() { // from class: com.flurry.sdk.kr.8
            final /* synthetic */ String a;

            public AnonymousClass8(final String str2) {
                r2 = str2;
            }

            @Override // com.flurry.sdk.lw
            public final void a() {
                if (!kr.this.c.remove(r2)) {
                    kf.a(6, kr.this.b, "Internal error. Block with id = " + r2 + " was not in progress state");
                }
            }
        });
    }
}
