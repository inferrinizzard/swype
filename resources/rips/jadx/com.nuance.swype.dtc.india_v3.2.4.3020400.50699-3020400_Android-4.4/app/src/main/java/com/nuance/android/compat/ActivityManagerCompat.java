package com.nuance.android.compat;

import android.app.ActivityManager;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class ActivityManagerCompat {
    private static final Method ActivityManager_isRunningInTestHarness = CompatUtil.getMethod((Class<?>) ActivityManager.class, "isRunningInTestHarness", (Class<?>[]) new Class[0]);

    private ActivityManagerCompat() {
    }

    private static boolean invokeIsRunningInTestHarness() {
        return ((Boolean) CompatUtil.invoke(ActivityManager_isRunningInTestHarness, null, new Object[0])).booleanValue();
    }

    public static boolean isRunningInTestHarness() {
        return ActivityManager_isRunningInTestHarness != null && invokeIsRunningInTestHarness();
    }

    public static boolean isUserAMonkey() {
        try {
            return ActivityManager.isUserAMonkey();
        } catch (RuntimeException e) {
            return true;
        }
    }
}
