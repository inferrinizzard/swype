package com.nuance.nmsp.client.sdk.common.util;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;

/* loaded from: classes.dex */
public final class Checker {
    private static final LogFactory.Log a = LogFactory.getLog(Checker.class);

    private static void a(String str) {
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException(str);
        a.error(str);
        throw illegalArgumentException;
    }

    public static void checkArgForCondition(String str, String str2, boolean z) {
        if (!z) {
            throw new IllegalArgumentException(str + " must be " + str2);
        }
    }

    public static Object checkArgForNull(String str, Object obj) {
        if (obj == null) {
            a(str + " must not be null");
        }
        return obj;
    }

    public static void checkArgsForNull(String str, Object... objArr) {
        for (Object obj : objArr) {
            if (obj != null) {
                return;
            }
        }
        a("At least one of " + str + " must not be null or empty");
    }

    public static void checkState(Object obj, boolean z) {
        checkState(obj, z, null);
    }

    public static void checkState(Object obj, boolean z, String str) {
        if (z) {
            return;
        }
        String shortClassName = obj instanceof String ? (String) obj : obj != null ? ClassUtils.getShortClassName(obj.getClass()) : null;
        IllegalStateException illegalStateException = shortClassName == null ? str == null ? new IllegalStateException() : new IllegalStateException(str) : str == null ? new IllegalStateException(shortClassName + " is in an invalid state") : new IllegalStateException(shortClassName + " is in an invalid state: " + str);
        a.error("State check failed", illegalStateException);
        throw illegalStateException;
    }

    public static String checkStringArgForEmpty(String str, String str2) {
        if (str2 != null && str2.length() == 0) {
            a(str + " could be null but must not be empty if non-null");
        }
        return str2;
    }

    public static String checkStringArgForNullOrEmpty(String str, String str2) {
        if (str2 == null || str2.length() == 0) {
            a(str + " must not be null or empty");
        }
        return str2;
    }

    public static void checkStringArgsForAllNullOrEmpty(String str, String... strArr) {
        for (String str2 : strArr) {
            if (str2 != null && str2.length() > 0) {
                return;
            }
        }
        a("At least one of " + str + " must not be null or empty");
    }
}
