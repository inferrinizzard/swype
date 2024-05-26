package com.nuance.swype.plugin;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.util.Log;
import com.facebook.internal.AnalyticsEvents;
import dalvik.system.DexClassLoader;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class ThemeApkResourceBroker {
    static ThemeApkResourceBroker sThemeApkResBroker;
    public ThemeResourceAccessor mResourceAccessor;
    ThemeApkInfo mThemeApkInfo;
    public boolean mHasInited = false;
    private final Map<Integer, Object> cacheAttributes = new HashMap();

    public static ThemeApkResourceBroker getInstance() {
        if (sThemeApkResBroker == null) {
            sThemeApkResBroker = new ThemeApkResourceBroker();
        }
        return sThemeApkResBroker;
    }

    @TargetApi(14)
    public final boolean setThemeFromApk(ThemeApkInfo themeApkInfo) {
        Resources themeApkRes = themeApkInfo.apkResources;
        ThemeMetaData apkThemeInfo = themeApkInfo.themeMetaData;
        if (apkThemeInfo == null || apkThemeInfo.themeStyleName == null) {
            return false;
        }
        try {
            Object object = this.cacheAttributes.get(Integer.valueOf(themeApkInfo.hashCode()));
            if (object == null) {
                Class loadClass = new DexClassLoader(themeApkInfo.apkPath + "/" + themeApkInfo.apkName, themeApkInfo.dexDir, null, ClassLoader.getSystemClassLoader()).loadClass(themeApkInfo.apkPackageName + ".R$styleable");
                apkThemeInfo.getClass();
                object = loadClass.getField("SwypeThemeTemplate").get(null);
                this.cacheAttributes.put(Integer.valueOf(themeApkInfo.hashCode()), object);
            }
            int[] attrs = (int[]) object;
            Resources.Theme newTheme = themeApkRes.newTheme();
            int j = themeApkRes.getIdentifier(apkThemeInfo.themeStyleName, AnalyticsEvents.PARAMETER_LIKE_VIEW_STYLE, themeApkInfo.apkPackageName);
            newTheme.applyStyle(j, true);
            if (this.mResourceAccessor != null) {
                this.mResourceAccessor.release();
            }
            this.mThemeApkInfo = themeApkInfo;
            int j2 = themeApkRes.getIdentifier("SwypeThemeDefaults", AnalyticsEvents.PARAMETER_LIKE_VIEW_STYLE, themeApkInfo.apkPackageName);
            this.mResourceAccessor = new ThemeResourceAccessor(newTheme, themeApkRes, attrs, this.mThemeApkInfo.apkPackageName, j2);
            this.mHasInited = true;
            return true;
        } catch (Exception e) {
            Log.e("ThemeApkResourceBroker", "ThemeApkResourceBroker: " + e.toString());
            return false;
        }
    }
}
