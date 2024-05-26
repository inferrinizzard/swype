package com.bumptech.glide.load.engine.cache;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;

/* loaded from: classes.dex */
public final class MemorySizeCalculator {
    public final int bitmapPoolSize;
    private final Context context;
    public final int memoryCacheSize;

    /* loaded from: classes.dex */
    interface ScreenDimensions {
        int getHeightPixels();

        int getWidthPixels();
    }

    public MemorySizeCalculator(Context context) {
        this(context, (ActivityManager) context.getSystemService("activity"), new DisplayMetricsScreenDimensions(context.getResources().getDisplayMetrics()));
    }

    private MemorySizeCalculator(Context context, ActivityManager activityManager, ScreenDimensions screenDimensions) {
        this.context = context;
        int maxSize = Math.round((isLowMemoryDevice(activityManager) ? 0.33f : 0.4f) * activityManager.getMemoryClass() * 1024 * 1024);
        int screenSize = screenDimensions.getWidthPixels() * screenDimensions.getHeightPixels() * 4;
        int targetPoolSize = screenSize * 4;
        int targetMemoryCacheSize = screenSize * 2;
        if (targetMemoryCacheSize + targetPoolSize <= maxSize) {
            this.memoryCacheSize = targetMemoryCacheSize;
            this.bitmapPoolSize = targetPoolSize;
        } else {
            int part = Math.round(maxSize / 6.0f);
            this.memoryCacheSize = part * 2;
            this.bitmapPoolSize = part * 4;
        }
        if (Log.isLoggable("MemorySizeCalculator", 3)) {
            new StringBuilder("Calculated memory cache size: ").append(toMb(this.memoryCacheSize)).append(" pool size: ").append(toMb(this.bitmapPoolSize)).append(" memory class limited? ").append(targetMemoryCacheSize + targetPoolSize > maxSize).append(" max size: ").append(toMb(maxSize)).append(" memoryClass: ").append(activityManager.getMemoryClass()).append(" isLowMemoryDevice: ").append(isLowMemoryDevice(activityManager));
        }
    }

    private String toMb(int bytes) {
        return Formatter.formatFileSize(this.context, bytes);
    }

    @TargetApi(19)
    private static boolean isLowMemoryDevice(ActivityManager activityManager) {
        if (Build.VERSION.SDK_INT >= 19) {
            return activityManager.isLowRamDevice();
        }
        return Build.VERSION.SDK_INT < 11;
    }

    /* loaded from: classes.dex */
    private static class DisplayMetricsScreenDimensions implements ScreenDimensions {
        private final DisplayMetrics displayMetrics;

        public DisplayMetricsScreenDimensions(DisplayMetrics displayMetrics) {
            this.displayMetrics = displayMetrics;
        }

        @Override // com.bumptech.glide.load.engine.cache.MemorySizeCalculator.ScreenDimensions
        public final int getWidthPixels() {
            return this.displayMetrics.widthPixels;
        }

        @Override // com.bumptech.glide.load.engine.cache.MemorySizeCalculator.ScreenDimensions
        public final int getHeightPixels() {
            return this.displayMetrics.heightPixels;
        }
    }
}
