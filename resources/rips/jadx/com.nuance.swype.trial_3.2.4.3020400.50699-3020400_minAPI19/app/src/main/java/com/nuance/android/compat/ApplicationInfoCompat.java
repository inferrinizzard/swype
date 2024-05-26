package com.nuance.android.compat;

import android.content.pm.ApplicationInfo;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public class ApplicationInfoCompat {
    private static final Field ApplicationInfo_nativeLibraryDir = CompatUtil.getDeclaredField(ApplicationInfo.class, "nativeLibraryDir");

    private ApplicationInfoCompat() {
    }

    public static String getNativeLibraryDir(ApplicationInfo appInfo) {
        if (ApplicationInfo_nativeLibraryDir != null) {
            try {
                return (String) ApplicationInfo_nativeLibraryDir.get(appInfo);
            } catch (IllegalAccessException iae) {
                throw new IllegalStateException(iae);
            }
        }
        return null;
    }
}
