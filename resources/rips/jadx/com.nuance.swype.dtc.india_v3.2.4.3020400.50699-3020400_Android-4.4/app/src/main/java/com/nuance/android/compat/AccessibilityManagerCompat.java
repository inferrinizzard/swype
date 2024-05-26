package com.nuance.android.compat;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityManager;
import java.lang.reflect.Method;
import java.util.List;

/* loaded from: classes.dex */
public class AccessibilityManagerCompat {
    private static final Method AccessibilityManager_isTouchExplorationEnabled = CompatUtil.getMethod((Class<?>) AccessibilityManager.class, "isTouchExplorationEnabled", (Class<?>[]) new Class[0]);
    private static final Method AccessibilityManager_getEnabledAccessibilityServiceList = CompatUtil.getMethod((Class<?>) AccessibilityManager.class, "getEnabledAccessibilityServiceList", (Class<?>[]) new Class[]{Integer.TYPE});

    private AccessibilityManagerCompat() {
    }

    public static boolean isTouchExplorationEnabled(AccessibilityManager am) {
        if (AccessibilityManager_isTouchExplorationEnabled != null) {
            return ((Boolean) CompatUtil.invoke(AccessibilityManager_isTouchExplorationEnabled, am, new Object[0])).booleanValue();
        }
        return false;
    }

    public static boolean isTalkBackEnabled(AccessibilityManager am) {
        List<AccessibilityServiceInfo> spokenServices;
        return (AccessibilityManager_getEnabledAccessibilityServiceList == null || (spokenServices = (List) CompatUtil.invoke(AccessibilityManager_getEnabledAccessibilityServiceList, am, 1)) == null || spokenServices.size() <= 0) ? false : true;
    }
}
