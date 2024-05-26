package com.flurry.sdk;

import com.flurry.sdk.ks;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class kr {
    public final String b;
    public kt d;
    public Set<String> c = new HashSet();
    public String e = "defaultDataKey_";
    private ka<jj> a = new ka<jj>() { // from class: com.flurry.sdk.kr.1
        @Override // com.flurry.sdk.ka
        public final /* synthetic */ void a(jj jjVar) {
            jj jjVar2 = jjVar;
            kf.a(4, kr.this.b, "onNetworkStateChanged : isNetworkEnable = " + jjVar2.a);
            if (!jjVar2.a) {
                return;
            }
            kr.this.b();
        }
    };

    /* loaded from: classes.dex */
    public interface a {
    }

    public abstract void a(byte[] bArr, String str, String str2);

    public kr(final String str, String str2) {
        this.b = str2;
        kb.a().a("com.flurry.android.sdk.NetworkStateEvent", this.a);
        jr.a().b(new lw() { // from class: com.flurry.sdk.kr.2
            @Override // com.flurry.sdk.lw
            public final void a() {
                kr.this.d = new kt(str);
            }
        });
    }

    public final void b() {
        jr.a().b(new lw() { // from class: com.flurry.sdk.kr.4
            final /* synthetic */ a a = null;

            @Override // com.flurry.sdk.lw
            public final void a() {
                final kr krVar = kr.this;
                if (!jk.a().b) {
                    kf.a(5, krVar.b, "Reports were not sent! No Internet connection!");
                    return;
                }
                ArrayList<String> arrayList = new ArrayList(krVar.d.c.keySet());
                if (arrayList.isEmpty()) {
                    kf.a(4, krVar.b, "No more reports to send.");
                    return;
                }
                for (String str : arrayList) {
                    if (!krVar.c()) {
                        return;
                    }
                    List<String> a2 = krVar.d.a(str);
                    kf.a(4, krVar.b, "Number of not sent blocks = " + a2.size());
                    int i = 0;
                    while (true) {
                        int i2 = i;
                        if (i2 < a2.size()) {
                            String str2 = a2.get(i2);
                            if (!krVar.c.contains(str2)) {
                                if (krVar.c()) {
                                    ks ksVar = (ks) new jy(jr.a().a.getFileStreamPath(ks.a(str2)), ".yflurrydatasenderblock.", 1, new lc<ks>() { // from class: com.flurry.sdk.kr.6
                                        @Override // com.flurry.sdk.lc
                                        public final kz<ks> a$1f969724() {
                                            return new ks.a();
                                        }
                                    }).a();
                                    if (ksVar == null) {
                                        kf.a(6, krVar.b, "Internal ERROR! Cannot read!");
                                        krVar.d.a(str2, str);
                                    } else {
                                        byte[] bArr = ksVar.b;
                                        if (bArr == null || bArr.length == 0) {
                                            kf.a(6, krVar.b, "Internal ERROR! Report is empty!");
                                            krVar.d.a(str2, str);
                                        } else {
                                            kf.a(5, krVar.b, "Reading block info " + str2);
                                            krVar.c.add(str2);
                                            krVar.a(bArr, str2, str);
                                        }
                                    }
                                }
                            }
                            i = i2 + 1;
                        }
                    }
                }
            }
        });
    }

    public void a(final String str, final String str2, int i) {
        jr.a().b(new lw() { // from class: com.flurry.sdk.kr.7
            @Override // com.flurry.sdk.lw
            public final void a() {
                if (!kr.this.d.a(str, str2)) {
                    kf.a(6, kr.this.b, "Internal error. Block wasn't deleted with id = " + str);
                }
                if (!kr.this.c.remove(str)) {
                    kf.a(6, kr.this.b, "Internal error. Block with id = " + str + " was not in progress state");
                }
            }
        });
    }

    final boolean c() {
        return this.c.size() <= 5;
    }
}
