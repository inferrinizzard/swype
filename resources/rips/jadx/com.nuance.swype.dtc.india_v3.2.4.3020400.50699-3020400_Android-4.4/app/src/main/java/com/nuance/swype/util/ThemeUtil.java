package com.nuance.swype.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
public final class ThemeUtil {
    public static int getAppThemeId(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getPackageInfo(context.getPackageName(), 0).applicationInfo.theme;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public static boolean getBool(TypedArray a) {
        try {
            return a.getBoolean(0, false);
        } finally {
            a.recycle();
        }
    }

    public static int getColor(TypedArray a) {
        try {
            return a.getColor(0, -16777216);
        } finally {
            a.recycle();
        }
    }

    public static ColorStateList getColorStateList(TypedArray a) {
        try {
            return a.getColorStateList(0);
        } finally {
            a.recycle();
        }
    }

    public static int getResId(TypedArray a, int defValue) {
        try {
            return a.getResourceId(0, defValue);
        } finally {
            a.recycle();
        }
    }

    public static float getDimen(TypedArray a) {
        try {
            return a.getDimension(0, 0.0f);
        } finally {
            a.recycle();
        }
    }

    public static Drawable getDrawable(TypedArray a) {
        try {
            return a.getDrawable(0);
        } finally {
            a.recycle();
        }
    }
}
