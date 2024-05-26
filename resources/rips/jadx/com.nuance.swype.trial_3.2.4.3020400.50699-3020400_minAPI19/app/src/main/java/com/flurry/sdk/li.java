package com.flurry.sdk;

import android.location.Criteria;
import android.location.Location;
import com.nuance.swype.input.IME;

/* loaded from: classes.dex */
public final class li extends lj {
    public static final Integer a = 226;
    public static final Integer b = 6;
    public static final Integer c = 3;
    public static final Integer d = 1;
    public static final String e = null;
    public static final Boolean f = true;
    public static final Boolean g = true;
    public static final String h = null;
    public static final Boolean i = true;
    public static final Criteria j = null;
    public static final Location k = null;
    public static final Long l = Long.valueOf(IME.RETRY_DELAY_IN_MILLIS);
    public static final Boolean m = true;
    public static final Long n = null;
    public static final Byte o = (byte) -1;
    public static final Boolean p = false;
    public static final String q = null;
    public static final Boolean r = true;
    private static li s;

    public static synchronized li a() {
        li liVar;
        synchronized (li.class) {
            if (s == null) {
                s = new li();
            }
            liVar = s;
        }
        return liVar;
    }

    private li() {
        a("AgentVersion", a);
        a("ReleaseMajorVersion", b);
        a("ReleaseMinorVersion", c);
        a("ReleasePatchVersion", d);
        a("ReleaseBetaVersion", "");
        a("VersionName", e);
        a("CaptureUncaughtExceptions", f);
        a("UseHttps", g);
        a("ReportUrl", h);
        a("ReportLocation", i);
        a("ExplicitLocation", k);
        a("ContinueSessionMillis", l);
        a("LogEvents", m);
        a("Age", n);
        a("Gender", o);
        a("UserId", "");
        a("ProtonEnabled", p);
        a("ProtonConfigUrl", q);
        a("analyticsEnabled", r);
    }
}
