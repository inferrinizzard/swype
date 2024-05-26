package com.localytics.android;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.localytics.android.Localytics;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class DatapointHelper {
    private static final String INVALID_ANDROID_ID = "9774d56d682e549c";
    private static final String LEGACY_DEVICE_ID_FILE = "/localytics/device_id";
    private static final Class<?>[] STRING_CLASS_ARRAY = {String.class};

    /* loaded from: classes.dex */
    static class AdvertisingInfo {
        public String id;
        public boolean limitAdTracking;

        public AdvertisingInfo(String id, boolean limitAdTracking) {
            this.id = id;
            this.limitAdTracking = limitAdTracking;
        }
    }

    private DatapointHelper() {
        throw new UnsupportedOperationException("This class is non-instantiable");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getApiLevel() {
        try {
            return Integer.parseInt((String) Build.VERSION.class.getField("SDK").get(null));
        } catch (Exception e) {
            Localytics.Log.w("Caught exception", e);
            try {
                return Build.VERSION.class.getField("SDK_INT").getInt(null);
            } catch (Exception ignore) {
                Localytics.Log.w("Caught exception", ignore);
                return 3;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getAndroidIdHashOrNull(Context appContext) {
        String androidId = getAndroidIdOrNull(appContext);
        if (androidId == null) {
            return null;
        }
        return getSha256_buggy(androidId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0067  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.String getAndroidIdOrNull(android.content.Context r12) {
        /*
            java.io.File r4 = new java.io.File
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.io.File r9 = r12.getFilesDir()
            java.lang.StringBuilder r8 = r8.append(r9)
            java.lang.String r9 = "/localytics/device_id"
            java.lang.StringBuilder r8 = r8.append(r9)
            java.lang.String r8 = r8.toString()
            r4.<init>(r8)
            boolean r8 = r4.exists()
            if (r8 == 0) goto L5a
            long r8 = r4.length()
            r10 = 0
            int r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r8 <= 0) goto L5a
            r6 = 0
            r8 = 100
            char[] r1 = new char[r8]     // Catch: java.io.FileNotFoundException -> L4e java.lang.Throwable -> L76
            java.io.BufferedReader r7 = new java.io.BufferedReader     // Catch: java.io.FileNotFoundException -> L4e java.lang.Throwable -> L76
            java.io.FileReader r8 = new java.io.FileReader     // Catch: java.io.FileNotFoundException -> L4e java.lang.Throwable -> L76
            r8.<init>(r4)     // Catch: java.io.FileNotFoundException -> L4e java.lang.Throwable -> L76
            r9 = 128(0x80, float:1.8E-43)
            r7.<init>(r8, r9)     // Catch: java.io.FileNotFoundException -> L4e java.lang.Throwable -> L76
            int r5 = r7.read(r1)     // Catch: java.lang.Throwable -> L8a java.io.FileNotFoundException -> L8d
            r8 = 0
            java.lang.String r2 = java.lang.String.copyValueOf(r1, r8, r5)     // Catch: java.lang.Throwable -> L8a java.io.FileNotFoundException -> L8d
            r7.close()     // Catch: java.lang.Throwable -> L8a java.io.FileNotFoundException -> L8d
            r7.close()     // Catch: java.io.IOException -> L87
        L4d:
            return r2
        L4e:
            r3 = move-exception
        L4f:
            java.lang.String r8 = "Caught exception"
            com.localytics.android.Localytics.Log.w(r8, r3)     // Catch: java.lang.Throwable -> L76
            if (r6 == 0) goto L5a
            r6.close()     // Catch: java.io.IOException -> L7d
        L5a:
            android.content.ContentResolver r8 = r12.getContentResolver()
            java.lang.String r9 = "android_id"
            java.lang.String r0 = android.provider.Settings.Secure.getString(r8, r9)
            if (r0 == 0) goto L74
            java.lang.String r8 = r0.toLowerCase()
            java.lang.String r9 = "9774d56d682e549c"
            boolean r8 = r8.equals(r9)
            if (r8 == 0) goto L85
        L74:
            r2 = 0
            goto L4d
        L76:
            r8 = move-exception
        L77:
            if (r6 == 0) goto L7c
            r6.close()     // Catch: java.io.IOException -> L7d
        L7c:
            throw r8     // Catch: java.io.IOException -> L7d
        L7d:
            r3 = move-exception
        L7e:
            java.lang.String r8 = "Caught exception"
            com.localytics.android.Localytics.Log.w(r8, r3)
            goto L5a
        L85:
            r2 = r0
            goto L4d
        L87:
            r3 = move-exception
            r6 = r7
            goto L7e
        L8a:
            r8 = move-exception
            r6 = r7
            goto L77
        L8d:
            r3 = move-exception
            r6 = r7
            goto L4f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.localytics.android.DatapointHelper.getAndroidIdOrNull(android.content.Context):java.lang.String");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getSha256_buggy(String string) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(string.getBytes("UTF-8"));
            return new BigInteger(1, digest).toString(16);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AdvertisingInfo getAdvertisingInfo(Context appContext) {
        AdvertisingIdClient.Info advertisingIdInfo;
        try {
            if (PlayServicesUtils.isAdvertisingAvailable() && (advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(appContext)) != null) {
                String advertisingId = advertisingIdInfo.getId();
                boolean isLimit = advertisingIdInfo.isLimitAdTrackingEnabled();
                if (TextUtils.isEmpty(advertisingId)) {
                    advertisingId = null;
                }
                return new AdvertisingInfo(advertisingId, isLimit);
            }
        } catch (Exception e) {
            Localytics.Log.w("Failed to get advertising info", e);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getSerialNumberHashOrNull() {
        return getSha256_buggy(Build.SERIAL);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getNetworkType(TelephonyManager telephonyManager, Context appContext) {
        try {
            if (appContext.getPackageManager().checkPermission("android.permission.ACCESS_NETWORK_STATE", appContext.getPackageName()) == 0) {
                NetworkInfo wifiInfo = ((ConnectivityManager) appContext.getSystemService("connectivity")).getNetworkInfo(1);
                if (wifiInfo != null && wifiInfo.isConnectedOrConnecting()) {
                    return "wifi";
                }
            } else {
                Localytics.Log.w("Application does not have one more more of the following permissions: ACCESS_WIFI_STATE. Determining Wi-Fi connectivity is unavailable");
            }
        } catch (NullPointerException e) {
            Localytics.Log.w("NullPointerException in getNetworkType()", e);
        } catch (SecurityException e2) {
            Localytics.Log.w("Application does not have the permission ACCESS_NETWORK_STATE. Determining Wi-Fi connectivity is unavailable", e2);
        }
        return "android_network_type_" + telephonyManager.getNetworkType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getFBAttribution(Context appContext) {
        String facebookAttribution = null;
        ContentResolver contentResolver = appContext.getContentResolver();
        Uri uri = Uri.parse("content://com.facebook.katana.provider.AttributionIdProvider");
        String[] projection = {"aid"};
        Cursor cursor = null;
        try {
            try {
                cursor = contentResolver.query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    facebookAttribution = cursor.getString(cursor.getColumnIndex("aid"));
                }
            } catch (Exception e) {
                Localytics.Log.w("Error reading FB attribution", e);
                if (cursor != null) {
                    cursor.close();
                }
            }
            return facebookAttribution;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getAppVersion(Context appContext) {
        PackageManager pm = appContext.getPackageManager();
        try {
            String versionName = pm.getPackageInfo(appContext.getPackageName(), 0).versionName;
            if (versionName == null) {
                Localytics.Log.w("versionName was null--is a versionName attribute set in the Android Manifest?");
                return "unknown";
            }
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getTelephonyDeviceIdOrNull(Context appContext) {
        if (!Boolean.valueOf(appContext.getPackageManager().hasSystemFeature("android.hardware.telephony")).booleanValue()) {
            Localytics.Log.i("Device does not have telephony; cannot read telephony id");
            return null;
        }
        if (appContext.getPackageManager().checkPermission("android.permission.READ_PHONE_STATE", appContext.getPackageName()) == 0) {
            String id = ((TelephonyManager) appContext.getSystemService("phone")).getDeviceId();
            return id;
        }
        Localytics.Log.w("Application does not have permission READ_PHONE_STATE; determining device id is not possible.  Please consider requesting READ_PHONE_STATE in the AndroidManifest");
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getLocalyticsAppKeyOrNull(Context context) {
        if (context == null) {
            Localytics.Log.w("Context passed to getLocalyticsAppKeyOrNull() is NULL. Please pass a valid context.");
            return null;
        }
        try {
            Context appContext = context.getApplicationContext();
            ApplicationInfo applicationInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), 128);
            if (applicationInfo.metaData != null) {
                Object metaData = applicationInfo.metaData.get("LOCALYTICS_APP_KEY");
                if (metaData instanceof String) {
                    return (String) metaData;
                }
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getLocalyticsNotificationIcon(Context appContext) {
        try {
            ApplicationInfo applicationInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), 0);
            if (applicationInfo.icon != 0) {
                int appIcon = applicationInfo.icon;
                if (isValidResourceId(appContext, appIcon)) {
                    return appIcon;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return android.R.drawable.sym_def_app_icon;
    }

    static boolean isValidResourceId(Context appContext, int resourceId) {
        try {
            appContext.getResources().getResourceName(resourceId);
            return true;
        } catch (Resources.NotFoundException e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getLocalyticsRollupKeyOrNull(Context appContext) {
        try {
            ApplicationInfo applicationInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), 128);
            if (applicationInfo.metaData == null) {
                return null;
            }
            Object metaData = applicationInfo.metaData.get("LOCALYTICS_ROLLUP_KEY");
            if (!(metaData instanceof String)) {
                return null;
            }
            String rollupKey = (String) metaData;
            return rollupKey;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int isLocationPermissionGranted(Context appContext) {
        return ContextCompat.checkSelfPermission(appContext, "android.permission.ACCESS_FINE_LOCATION") == 0 ? 1 : 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String isWifiEnabled(Context context) {
        String str;
        try {
            if (context.getPackageManager().checkPermission("android.permission.ACCESS_WIFI_STATE", context.getPackageName()) == 0) {
                WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
                if (wifiManager != null) {
                    str = wifiManager.isWifiEnabled() ? "Yes" : "No";
                } else {
                    str = "Wifi Manager is Null";
                }
            } else {
                Localytics.Log.w("Application does not have one more more of the following permissions: ACCESS_WIFI_STATE. Determining Wi-Fi connectivity is unavailable");
                str = "Permissions Not Granted";
            }
            return str;
        } catch (Exception e) {
            Localytics.Log.w("NullPointerException in isWifiEnabled()", e);
            return "Permissions Not Granted";
        }
    }
}
