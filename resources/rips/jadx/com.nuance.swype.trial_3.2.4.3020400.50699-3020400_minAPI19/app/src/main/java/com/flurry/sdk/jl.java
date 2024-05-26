package com.flurry.sdk;

import android.telephony.TelephonyManager;

/* loaded from: classes.dex */
public class jl {
    private static jl a;
    private static final String b = jl.class.getSimpleName();

    public static synchronized jl a() {
        jl jlVar;
        synchronized (jl.class) {
            if (a == null) {
                a = new jl();
            }
            jlVar = a;
        }
        return jlVar;
    }

    private jl() {
    }

    public static String c() {
        TelephonyManager telephonyManager = (TelephonyManager) jr.a().a.getSystemService("phone");
        if (telephonyManager == null) {
            return null;
        }
        return telephonyManager.getNetworkOperatorName();
    }

    public static String d() {
        TelephonyManager telephonyManager = (TelephonyManager) jr.a().a.getSystemService("phone");
        if (telephonyManager == null) {
            return null;
        }
        return telephonyManager.getNetworkOperator();
    }
}
