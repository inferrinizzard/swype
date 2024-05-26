package com.google.android.gms.internal;

import android.content.Context;
import android.text.TextUtils;
import java.math.BigInteger;
import java.util.Locale;

@zzin
/* loaded from: classes.dex */
public final class zzkb {
    private static final Object zzamr = new Object();
    private static String zzckj;

    public static String zzsy() {
        String str;
        synchronized (zzamr) {
            str = zzckj;
        }
        return str;
    }

    public static String zza(Context context, String str, String str2) {
        String str3;
        synchronized (zzamr) {
            if (zzckj == null && !TextUtils.isEmpty(str)) {
                try {
                    ClassLoader classLoader = context.createPackageContext(str2, 3).getClassLoader();
                    Class<?> cls = Class.forName("com.google.ads.mediation.MediationAdapter", false, classLoader);
                    BigInteger bigInteger = new BigInteger(new byte[1]);
                    String[] split = str.split(",");
                    BigInteger bigInteger2 = bigInteger;
                    for (int i = 0; i < split.length; i++) {
                        com.google.android.gms.ads.internal.zzu.zzfq();
                        if (zzkh.zza(classLoader, cls, split[i])) {
                            bigInteger2 = bigInteger2.setBit(i);
                        }
                    }
                    zzckj = String.format(Locale.US, "%X", bigInteger2);
                } catch (Throwable th) {
                    zzckj = "err";
                }
            }
            str3 = zzckj;
        }
        return str3;
    }
}
