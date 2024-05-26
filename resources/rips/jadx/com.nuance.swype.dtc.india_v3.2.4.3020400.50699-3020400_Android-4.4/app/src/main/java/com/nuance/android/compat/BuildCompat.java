package com.nuance.android.compat;

import android.os.Build;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public class BuildCompat {
    private static final Field Build_SERIAL = CompatUtil.getDeclaredField(Build.class, "SERIAL");

    private BuildCompat() {
    }

    public static String getSerial() {
        if (Build_SERIAL != null) {
            try {
                return (String) Build_SERIAL.get(null);
            } catch (IllegalAccessException iae) {
                throw new IllegalStateException(iae);
            }
        }
        return "unknown";
    }
}
