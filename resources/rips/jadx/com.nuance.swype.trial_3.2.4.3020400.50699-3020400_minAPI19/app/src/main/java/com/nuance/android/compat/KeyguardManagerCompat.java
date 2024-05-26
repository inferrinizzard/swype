package com.nuance.android.compat;

import android.app.KeyguardManager;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class KeyguardManagerCompat {
    private static final Method KeyguardManager_isKeyguardLocked = CompatUtil.getMethod((Class<?>) KeyguardManager.class, "isKeyguardLocked", (Class<?>[]) new Class[0]);
    private static final Method KeyguardManager_isKeyguardSecure = CompatUtil.getMethod((Class<?>) KeyguardManager.class, "isKeyguardSecure", (Class<?>[]) new Class[0]);

    private KeyguardManagerCompat() {
    }

    public static boolean isKeyguardLocked(KeyguardManager kgm) {
        if (KeyguardManager_isKeyguardLocked != null) {
            return ((Boolean) CompatUtil.invoke(KeyguardManager_isKeyguardLocked, kgm, new Object[0])).booleanValue();
        }
        return false;
    }

    public static boolean isKeyguardSecure(KeyguardManager kgm) {
        if (KeyguardManager_isKeyguardSecure != null) {
            return ((Boolean) CompatUtil.invoke(KeyguardManager_isKeyguardSecure, kgm, new Object[0])).booleanValue();
        }
        return false;
    }
}
