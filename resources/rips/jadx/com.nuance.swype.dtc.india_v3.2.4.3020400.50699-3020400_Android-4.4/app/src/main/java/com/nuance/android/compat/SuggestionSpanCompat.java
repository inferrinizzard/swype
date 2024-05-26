package com.nuance.android.compat;

import android.content.Context;
import android.text.ParcelableSpan;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class SuggestionSpanCompat {
    private static final Class<?>[] ARGS_SuggestionSpan;
    public static final Class<?> CLASS_SuggestionSpan;
    private static final Constructor<?> CONSTRUCTOR_SuggestionSpan;
    public static final Integer FLAG_VALUE_AUTO_CORRECTION;
    private static final Method SuggestionSpan_getFlags;

    private SuggestionSpanCompat() {
    }

    static {
        Class<?> cls = CompatUtil.getClass("android.text.style.SuggestionSpan");
        CLASS_SuggestionSpan = cls;
        SuggestionSpan_getFlags = CompatUtil.getMethod(cls, "getFlags", (Class<?>[]) new Class[0]);
        ARGS_SuggestionSpan = new Class[]{Context.class, String[].class, Integer.TYPE};
        CONSTRUCTOR_SuggestionSpan = CompatUtil.getConstructor(CLASS_SuggestionSpan, ARGS_SuggestionSpan);
        FLAG_VALUE_AUTO_CORRECTION = (Integer) CompatUtil.getStaticFieldValue(CLASS_SuggestionSpan, "FLAG_AUTO_CORRECTION");
    }

    public static ParcelableSpan createSpan(Context context, int underlineColor, int underlineThickness) {
        if (CLASS_SuggestionSpan != null) {
            Object[] args = {context, new String[0], FLAG_VALUE_AUTO_CORRECTION};
            Object ss = CompatUtil.newInstance(CONSTRUCTOR_SuggestionSpan, args);
            if (ss != null) {
                CompatUtil.setFieldIntIgnoreAccess(ss, "mAutoCorrectionUnderlineColor", underlineColor);
                CompatUtil.setFieldFloatIgnoreAccess(ss, "mAutoCorrectionUnderlineThickness", underlineThickness);
                return (ParcelableSpan) ss;
            }
        }
        return null;
    }

    public static int getFlags(Object ss) {
        if (SuggestionSpan_getFlags != null) {
            return ((Integer) CompatUtil.invoke(SuggestionSpan_getFlags, ss, new Object[0])).intValue();
        }
        return 0;
    }

    public static boolean isAutoCorrectionSpan(Object ss) {
        return (getFlags(ss) & FLAG_VALUE_AUTO_CORRECTION.intValue()) == FLAG_VALUE_AUTO_CORRECTION.intValue();
    }
}
