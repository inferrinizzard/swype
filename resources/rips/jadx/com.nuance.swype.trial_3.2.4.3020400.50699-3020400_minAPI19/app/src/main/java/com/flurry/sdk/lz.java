package com.flurry.sdk;

import android.content.Context;
import com.flurry.sdk.lj;
import java.lang.Thread;

/* loaded from: classes.dex */
public class lz implements ki, lj.a, Thread.UncaughtExceptionHandler {
    private static final String a = lz.class.getSimpleName();
    private boolean b;

    @Override // com.flurry.sdk.ki
    public final void a(Context context) {
        li a2 = li.a();
        this.b = ((Boolean) a2.a("CaptureUncaughtExceptions")).booleanValue();
        a2.a("CaptureUncaughtExceptions", (lj.a) this);
        kf.a(4, a, "initSettings, CrashReportingEnabled = " + this.b);
        ma a3 = ma.a();
        synchronized (a3.b) {
            a3.b.put(this, null);
        }
    }

    @Override // com.flurry.sdk.lj.a
    public final void a(String str, Object obj) {
        if (str.equals("CaptureUncaughtExceptions")) {
            this.b = ((Boolean) obj).booleanValue();
            kf.a(4, a, "onSettingUpdate, CrashReportingEnabled = " + this.b);
        } else {
            kf.a(6, a, "onSettingUpdate internal error!");
        }
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        th.printStackTrace();
        if (this.b) {
            String str = "";
            StackTraceElement[] stackTrace = th.getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                StringBuilder sb = new StringBuilder();
                if (th.getMessage() != null) {
                    sb.append(" (" + th.getMessage() + ")\n");
                }
                str = sb.toString();
            } else if (th.getMessage() != null) {
                str = th.getMessage();
            }
            hk.a();
            hk.a("uncaught", str, th);
        }
        lf.a().e();
        ji.a().d();
    }
}
