package com.nuance.nmsp.client.sdk.common.util;

/* loaded from: classes.dex */
public final class ClassUtils {
    public static String getShortClassName(Class<?> cls) {
        int i;
        String simpleName = cls.getSimpleName();
        if (simpleName == null) {
            return null;
        }
        if (simpleName.length() > 0) {
            return simpleName;
        }
        String name = cls.getName();
        if (name == null) {
            return null;
        }
        int lastIndexOf = name.lastIndexOf(46);
        return (lastIndexOf < 0 || (i = lastIndexOf + 1) >= name.length()) ? name : name.substring(i);
    }
}
