package com.nuance.connect.util;

import android.content.Context;
import android.os.Build;

/* loaded from: classes.dex */
public class PermissionUtils {
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String READ_PROFILE = "android.permission.READ_PROFILE";
    public static final String READ_SOCIAL_STREAM = "android.permission.READ_SOCIAL_STREAM";
    public static final String READ_USER_DICTIONARY = "android.permission.READ_USER_DICTIONARY";
    public static final int VERSION_FROYO = 8;
    public static final int VERSION_GB = 9;
    public static final int VERSION_HC = 11;
    public static final int VERSION_ICS = 14;
    public static final int VERSION_JB = 16;

    public static boolean checkPermission(Context context, int i, String str) {
        return i >= Integer.valueOf(Build.VERSION.SDK_INT).intValue() || context.getApplicationContext().checkCallingOrSelfPermission(str) == 0;
    }

    public static boolean checkPermission(Context context, int i, String str, String str2) {
        return i >= Integer.valueOf(Build.VERSION.SDK_INT).intValue() ? context.getApplicationContext().checkCallingOrSelfPermission(str2) == 0 : context.getApplicationContext().checkCallingOrSelfPermission(str) == 0;
    }

    public static boolean checkPermission(Context context, String str) {
        return context.getApplicationContext().checkCallingOrSelfPermission(str) == 0;
    }
}
