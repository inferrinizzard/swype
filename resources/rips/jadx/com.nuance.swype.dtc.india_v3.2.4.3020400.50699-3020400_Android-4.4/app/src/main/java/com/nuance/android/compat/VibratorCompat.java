package com.nuance.android.compat;

import android.os.Vibrator;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class VibratorCompat {
    private static final Method Vibrator_hasVibrator = CompatUtil.getMethod((Class<?>) Vibrator.class, "hasVibrator", (Class<?>[]) new Class[0]);

    private VibratorCompat() {
    }

    public static boolean hasVibrator(Vibrator vib) {
        if (Vibrator_hasVibrator != null) {
            return ((Boolean) CompatUtil.invoke(Vibrator_hasVibrator, vib, new Object[0])).booleanValue();
        }
        return true;
    }
}
