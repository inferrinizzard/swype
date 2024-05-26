package com.nuance.swype.util;

/* loaded from: classes.dex */
public final class StringUtils {
    public static boolean isApkCompletePath(String sku) {
        return sku != null && sku.endsWith(".apk") && sku.contains("/");
    }
}
