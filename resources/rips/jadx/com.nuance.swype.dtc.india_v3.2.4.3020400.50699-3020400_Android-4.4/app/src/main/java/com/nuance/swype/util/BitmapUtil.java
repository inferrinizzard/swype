package com.nuance.swype.util;

import android.graphics.Bitmap;

/* loaded from: classes.dex */
public final class BitmapUtil {
    public static Bitmap createDeviceOptimizedBitmap(int width, int height, boolean isLowEndDevice) {
        Bitmap.Config config = isLowEndDevice ? Bitmap.Config.RGB_565 : Bitmap.Config.ARGB_8888;
        return Bitmap.createBitmap(width, height, config);
    }
}
