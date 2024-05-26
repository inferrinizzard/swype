package com.nuance.android.compat;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class ViewCompat {
    private static final Method View_setScrollX = CompatUtil.getMethod((Class<?>) View.class, "setScrollX", (Class<?>[]) new Class[]{Integer.TYPE});
    private static final Method View_setScrollY = CompatUtil.getMethod((Class<?>) View.class, "setScrollY", (Class<?>[]) new Class[]{Integer.TYPE});
    private static final Method View_setLayerType = CompatUtil.getMethod((Class<?>) View.class, "setLayerType", (Class<?>[]) new Class[]{Integer.TYPE, Paint.class});
    private static final Method View_getLayerType = CompatUtil.getMethod((Class<?>) View.class, "getLayerType", (Class<?>[]) new Class[0]);
    private static final Method View_setLayerPaint = CompatUtil.getMethod((Class<?>) View.class, "setLayerPaint", (Class<?>[]) new Class[]{Paint.class});
    private static final Method View_setTranslationX = CompatUtil.getMethod((Class<?>) View.class, "setTranslationX", (Class<?>[]) new Class[]{Float.TYPE});
    private static final Method View_setTranslationY = CompatUtil.getMethod((Class<?>) View.class, "setTranslationY", (Class<?>[]) new Class[]{Float.TYPE});
    public static final Integer FLAG_VALUE_LAYER_TYPE_HARDWARE = (Integer) CompatUtil.getStaticFieldValue((Class<?>) View.class, "LAYER_TYPE_HARDWARE");
    public static final Integer FLAG_VALUE_LAYER_TYPE_SOFTWARE = (Integer) CompatUtil.getStaticFieldValue((Class<?>) View.class, "LAYER_TYPE_SOFTWARE");
    public static final Integer FLAG_VALUE_LAYER_TYPE_NONE = (Integer) CompatUtil.getStaticFieldValue((Class<?>) View.class, "LAYER_TYPE_NONE");
    public static final Method View_isHardwareAccelerated = CompatUtil.getMethod((Class<?>) View.class, "isHardwareAccelerated", (Class<?>[]) new Class[0]);
    private static final Method View_setBackground = CompatUtil.getMethod((Class<?>) View.class, "setBackground", (Class<?>[]) new Class[]{Drawable.class});
    private static final Method View_setBackgroundDrawable = CompatUtil.getMethod((Class<?>) View.class, "setBackgroundDrawable", (Class<?>[]) new Class[]{Drawable.class});

    private ViewCompat() {
    }

    public static boolean supportsSetLayerPaint() {
        return View_setLayerPaint != null;
    }

    public static boolean supports2dTranslation() {
        return (View_setTranslationX == null || View_setTranslationY == null) ? false : true;
    }

    public static void setTranslation(View view, float dx, float dy) {
        if (View_setTranslationX != null) {
            CompatUtil.invoke(View_setTranslationX, view, Float.valueOf(dx));
            CompatUtil.invoke(View_setTranslationY, view, Float.valueOf(dy));
        }
    }

    public static void setLayerPaint(View view, Paint paint) {
        if (View_setLayerPaint != null) {
            CompatUtil.invoke(View_setLayerPaint, view, paint);
        }
    }

    public static void enableHardwareLayer(View view) {
        enableHardwareLayer(view, null);
    }

    public static void enableHardwareLayer(View view, Paint paint) {
        if (View_setLayerType != null) {
            CompatUtil.invoke(View_setLayerType, view, FLAG_VALUE_LAYER_TYPE_HARDWARE, null);
        }
    }

    public static void removeLayer(View view) {
        if (View_setLayerType != null) {
            CompatUtil.invoke(View_setLayerType, view, FLAG_VALUE_LAYER_TYPE_NONE, null);
        }
    }

    public static void disableHardwareAccel(View view) {
        if (View_setLayerType != null) {
            CompatUtil.invoke(View_setLayerType, view, FLAG_VALUE_LAYER_TYPE_SOFTWARE, null);
        }
    }

    public static boolean isBackedByLayer(View view) {
        if (View_getLayerType == null) {
            return false;
        }
        Integer layerType = (Integer) CompatUtil.invoke(View_getLayerType, view, new Object[0]);
        return !FLAG_VALUE_LAYER_TYPE_NONE.equals(layerType);
    }

    public static void setScrollX(View view, int value) {
        if (View_setScrollX != null) {
            CompatUtil.invoke(View_setScrollX, view, Integer.valueOf(value));
        } else {
            view.scrollTo(value, view.getScrollY());
        }
    }

    public static void setScrollY(View view, int value) {
        if (View_setScrollY != null) {
            CompatUtil.invoke(View_setScrollY, view, Integer.valueOf(value));
        } else {
            view.scrollTo(view.getScrollX(), value);
        }
    }

    public static boolean isHardwareAccelerated(View view) {
        if (View_isHardwareAccelerated != null) {
            return ((Boolean) CompatUtil.invoke(View_isHardwareAccelerated, view, new Object[0])).booleanValue();
        }
        return false;
    }

    public static void setBackground(View view, Drawable drawable) {
        if (View_setBackground != null) {
            CompatUtil.invoke(View_setBackground, view, drawable);
        } else {
            CompatUtil.invoke(View_setBackgroundDrawable, view, drawable);
        }
    }
}
