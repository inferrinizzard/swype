package com.nuance.android.compat;

import android.content.res.Configuration;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public class ConfigurationCompat {
    private static final String swDp = "smallestScreenWidthDp";
    private static final Field Configuration_smallestScreenWidthDp = CompatUtil.getDeclaredField(Configuration.class, swDp);

    private ConfigurationCompat() {
    }

    public static int getSmallestScreenWidthDp(Configuration con) {
        if (Configuration_smallestScreenWidthDp != null) {
            try {
                return Configuration_smallestScreenWidthDp.getInt(con);
            } catch (IllegalAccessException e) {
            }
        }
        return 0;
    }
}
