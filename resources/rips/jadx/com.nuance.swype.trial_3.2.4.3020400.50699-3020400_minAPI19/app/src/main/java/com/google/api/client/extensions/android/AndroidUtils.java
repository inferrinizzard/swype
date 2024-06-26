package com.google.api.client.extensions.android;

import android.os.Build;
import com.google.api.client.util.Preconditions;

/* loaded from: classes.dex */
public class AndroidUtils {
    public static boolean isMinimumSdkLevel(int minimumSdkLevel) {
        return Build.VERSION.SDK_INT >= minimumSdkLevel;
    }

    public static void checkMinimumSdkLevel(int minimumSdkLevel) {
        Preconditions.checkArgument(isMinimumSdkLevel(minimumSdkLevel), "running on Android SDK level %s but requires minimum %s", Integer.valueOf(Build.VERSION.SDK_INT), Integer.valueOf(minimumSdkLevel));
    }

    private AndroidUtils() {
    }
}
