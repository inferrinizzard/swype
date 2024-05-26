package com.flurry.sdk;

import android.content.Context;
import java.io.File;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class hl {
    private static final String b = hl.class.getSimpleName();
    boolean a;
    private final hm c;
    private final File d;
    private String e;

    public hl() {
        this(jr.a().a);
    }

    public hl(Context context) {
        this.c = new hm();
        this.d = context.getFileStreamPath(".flurryinstallreceiver.");
        kf.a(3, b, "Referrer file name if it exists:  " + this.d);
    }

    public final synchronized Map<String, List<String>> a() {
        c();
        return hm.a(this.e);
    }

    public final synchronized String b() {
        c();
        return this.e;
    }

    public final synchronized void a(String str) {
        this.a = true;
        b(str);
        lq.a(this.d, this.e);
    }

    private void b(String str) {
        if (str != null) {
            this.e = str;
        }
    }

    private void c() {
        if (!this.a) {
            this.a = true;
            kf.a(4, b, "Loading referrer info from file: " + this.d.getAbsolutePath());
            String c = lq.c(this.d);
            kf.a(b, "Referrer file contents: " + c);
            b(c);
        }
    }
}
