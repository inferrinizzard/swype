package com.crashlytics.android.beta;

import android.content.Context;
import android.content.pm.PackageManager;
import com.nuance.connect.common.Strings;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.cache.ValueLoader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* loaded from: classes.dex */
public final class DeviceTokenLoader implements ValueLoader<String> {
    @Override // io.fabric.sdk.android.services.cache.ValueLoader
    public final /* bridge */ /* synthetic */ String load(Context x0) throws Exception {
        return load2(x0);
    }

    /* renamed from: load, reason: avoid collision after fix types in other method */
    private static String load2(Context context) throws Exception {
        long start = System.nanoTime();
        String token = "";
        ZipInputStream zis = null;
        try {
            try {
                ZipInputStream zis2 = new ZipInputStream(new FileInputStream(context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir));
                try {
                    token = determineDeviceToken(zis2);
                    try {
                        zis2.close();
                        zis = zis2;
                    } catch (IOException e) {
                        Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Failed to close the APK file", e);
                        zis = zis2;
                    }
                } catch (PackageManager.NameNotFoundException e2) {
                    e = e2;
                    zis = zis2;
                    Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Failed to find this app in the PackageManager", e);
                    if (zis != null) {
                        try {
                            zis.close();
                        } catch (IOException e3) {
                            Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Failed to close the APK file", e3);
                        }
                    }
                    double millis = (System.nanoTime() - start) / 1000000.0d;
                    Fabric.getLogger();
                    new StringBuilder("Beta device token load took ").append(millis).append("ms");
                    return token;
                } catch (FileNotFoundException e4) {
                    e = e4;
                    zis = zis2;
                    Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Failed to find the APK file", e);
                    if (zis != null) {
                        try {
                            zis.close();
                        } catch (IOException e5) {
                            Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Failed to close the APK file", e5);
                        }
                    }
                    double millis2 = (System.nanoTime() - start) / 1000000.0d;
                    Fabric.getLogger();
                    new StringBuilder("Beta device token load took ").append(millis2).append("ms");
                    return token;
                } catch (IOException e6) {
                    e = e6;
                    zis = zis2;
                    Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Failed to read the APK file", e);
                    if (zis != null) {
                        try {
                            zis.close();
                        } catch (IOException e7) {
                            Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Failed to close the APK file", e7);
                        }
                    }
                    double millis22 = (System.nanoTime() - start) / 1000000.0d;
                    Fabric.getLogger();
                    new StringBuilder("Beta device token load took ").append(millis22).append("ms");
                    return token;
                } catch (Throwable th) {
                    th = th;
                    zis = zis2;
                    if (zis != null) {
                        try {
                            zis.close();
                        } catch (IOException e8) {
                            Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Failed to close the APK file", e8);
                        }
                    }
                    throw th;
                }
            } catch (PackageManager.NameNotFoundException e9) {
                e = e9;
            } catch (FileNotFoundException e10) {
                e = e10;
            } catch (IOException e11) {
                e = e11;
            }
            double millis222 = (System.nanoTime() - start) / 1000000.0d;
            Fabric.getLogger();
            new StringBuilder("Beta device token load took ").append(millis222).append("ms");
            return token;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private static String determineDeviceToken(ZipInputStream zis) throws IOException {
        String name;
        do {
            ZipEntry entry = zis.getNextEntry();
            if (entry == null) {
                return "";
            }
            name = entry.getName();
        } while (!name.startsWith("assets/com.crashlytics.android.beta/dirfactor-device-token="));
        return name.substring(59, name.length() - 1);
    }
}
