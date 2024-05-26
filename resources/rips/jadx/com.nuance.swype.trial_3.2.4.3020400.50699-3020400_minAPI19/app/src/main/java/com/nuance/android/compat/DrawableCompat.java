package com.nuance.android.compat;

import android.graphics.drawable.Drawable;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class DrawableCompat {
    private static final Method Display_getAlpha = CompatUtil.getMethod((Class<?>) Drawable.class, "getAlpha", (Class<?>[]) new Class[0]);

    private DrawableCompat() {
    }

    public static int getAlpha(Drawable drawable, int defaultVal) {
        if (Display_getAlpha != null) {
            int defaultVal2 = ((Integer) CompatUtil.invoke(Display_getAlpha, drawable, new Object[0])).intValue();
            return defaultVal2;
        }
        return defaultVal;
    }
}
