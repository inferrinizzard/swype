package com.nuance.android.compat;

import android.graphics.drawable.Drawable;
import android.preference.Preference;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class PreferenceCompat {
    private static final Method Preference_setIcon_Drawable = CompatUtil.getMethod((Class<?>) Preference.class, "setIcon", (Class<?>[]) new Class[]{Drawable.class});
    private static final Method Preference_setIcon_int = CompatUtil.getMethod((Class<?>) Preference.class, "setIcon", (Class<?>[]) new Class[]{Integer.TYPE});

    private PreferenceCompat() {
    }

    public static void setIcon(Preference preference, Drawable icon) {
        if (Preference_setIcon_Drawable != null) {
            CompatUtil.invoke(Preference_setIcon_Drawable, preference, icon);
        }
    }

    public static void setIcon(Preference preference, int iconResId) {
        if (Preference_setIcon_int != null) {
            CompatUtil.invoke(Preference_setIcon_int, preference, Integer.valueOf(iconResId));
        }
    }
}
