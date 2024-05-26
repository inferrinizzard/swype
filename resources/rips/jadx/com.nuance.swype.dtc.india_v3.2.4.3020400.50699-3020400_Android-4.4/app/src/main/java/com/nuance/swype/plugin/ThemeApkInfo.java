package com.nuance.swype.plugin;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import java.io.File;

/* loaded from: classes.dex */
public final class ThemeApkInfo {
    private String apkAppName;
    String apkName;
    String apkPackageName;
    String apkPath;
    public Resources apkResources;
    private int apkVersionCode;
    private String apkVersionName;
    String dexDir;
    public ThemeMetaData themeMetaData;

    public final int hashCode() {
        return (((this.apkPackageName == null ? 0 : this.apkPackageName.hashCode()) + (((this.apkAppName == null ? 0 : this.apkAppName.hashCode()) + 31) * 31)) * 31) + (this.apkVersionName != null ? this.apkVersionName.hashCode() : 0);
    }

    public final boolean equals(Object paramObject) {
        if (this == paramObject) {
            return true;
        }
        if (paramObject != null && getClass() == paramObject.getClass()) {
            ThemeApkInfo inApkInfo = (ThemeApkInfo) paramObject;
            if (this.apkAppName == null) {
                if (inApkInfo.apkAppName != null) {
                    return false;
                }
            } else if (!this.apkAppName.equals(inApkInfo.apkAppName)) {
                return false;
            }
            if (this.apkPackageName == null) {
                if (inApkInfo.apkPackageName != null) {
                    return false;
                }
            } else if (!this.apkPackageName.equals(inApkInfo.apkPackageName)) {
                return false;
            }
            if (this.apkVersionName == null) {
                if (inApkInfo.apkVersionName != null) {
                    return false;
                }
            } else if (!this.apkVersionName.equals(inApkInfo.apkVersionName)) {
                return false;
            }
            return this.apkVersionCode == inApkInfo.apkVersionCode;
        }
        return false;
    }

    public static ThemeApkInfo fromStaticApkFile(Context mainContext, String paramApkPath) {
        File apkFile = new File(paramApkPath);
        if (!apkFile.exists() || !paramApkPath.toLowerCase().endsWith(".apk")) {
            return null;
        }
        try {
            ThemeApkInfo themeApkInfo = new ThemeApkInfo();
            PackageInfo mInfo = mainContext.getPackageManager().getPackageArchiveInfo(paramApkPath, 1);
            themeApkInfo.apkVersionName = mInfo.versionName;
            themeApkInfo.apkVersionCode = mInfo.versionCode;
            ApplicationInfo appInfo = mInfo.applicationInfo;
            appInfo.sourceDir = paramApkPath;
            appInfo.publicSourceDir = paramApkPath;
            Resources apkResources = mainContext.getPackageManager().getResourcesForApplication(appInfo);
            File dexOutputDir = mainContext.getDir("dex", 0);
            themeApkInfo.dexDir = dexOutputDir.getAbsolutePath();
            themeApkInfo.apkPath = apkFile.getParent();
            themeApkInfo.apkName = apkFile.getName();
            themeApkInfo.apkResources = apkResources;
            if (appInfo.labelRes != 0) {
                themeApkInfo.apkAppName = apkResources.getText(appInfo.labelRes).toString();
            } else {
                themeApkInfo.apkAppName = themeApkInfo.apkName.substring(0, themeApkInfo.apkName.lastIndexOf("."));
            }
            themeApkInfo.apkPackageName = appInfo.packageName;
            themeApkInfo.themeMetaData = readThemeMetaDataFromFile(themeApkInfo.apkResources, themeApkInfo.apkPackageName);
            return themeApkInfo;
        } catch (Exception localException) {
            Log.e("ThemeApkInfo", "ApkInfo: " + localException.toString());
            return null;
        }
    }

    private static ThemeMetaData readThemeMetaDataFromFile(Resources themeApkRes, String packageName) {
        int resid = themeApkRes.getIdentifier("theme_metadata", "xml", packageName);
        XmlResourceParser localXmlResourceParser = null;
        try {
            localXmlResourceParser = themeApkRes.getXml(resid);
            ThemeMetaData themeMetaData = new ThemeMetaData(localXmlResourceParser);
            if (localXmlResourceParser == null) {
                return themeMetaData;
            }
            localXmlResourceParser.close();
            return themeMetaData;
        } catch (Exception e) {
            if (localXmlResourceParser == null) {
                return null;
            }
            localXmlResourceParser.close();
            return null;
        } catch (Throwable th) {
            if (localXmlResourceParser != null) {
                localXmlResourceParser.close();
            }
            throw th;
        }
    }
}
