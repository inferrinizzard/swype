package com.nuance.android.compat;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.Region;
import android.inputmethodservice.InputMethodService;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class InputMethodServiceCompat {
    private static final Field Insets_touchableRegion = CompatUtil.getDeclaredField(InputMethodService.Insets.class, "touchableRegion");
    private static final Method InputMethodService_enableHardwareAcceleration = CompatUtil.getMethod((Class<?>) InputMethodService.class, "enableHardwareAcceleration", (Class<?>[]) new Class[0]);

    private InputMethodServiceCompat() {
    }

    public static Region getTouchableRegion(InputMethodService.Insets insets) {
        try {
            return (Region) Insets_touchableRegion.get(insets);
        } catch (IllegalAccessException iae) {
            throw new IllegalStateException(iae);
        }
    }

    public static boolean isTouchableRegionSupported() {
        return Insets_touchableRegion != null;
    }

    @SuppressLint({"InlinedApi"})
    public static boolean setTouchableRegion(InputMethodService.Insets insets, Rect rect) {
        Region region;
        if (Insets_touchableRegion == null || (region = getTouchableRegion(insets)) == null) {
            return false;
        }
        region.set(rect);
        insets.touchableInsets = 3;
        return true;
    }

    public static boolean enableHardwareAcceleration(InputMethodService ims) {
        if (InputMethodService_enableHardwareAcceleration != null) {
            return ((Boolean) CompatUtil.invoke(InputMethodService_enableHardwareAcceleration, ims, new Object[0])).booleanValue();
        }
        return false;
    }
}
