package io.fabric.sdk.android.services.common;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Debug;
import android.os.StatFs;
import android.provider.Settings;
import android.text.TextUtils;
import com.facebook.internal.ServerProtocol;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import io.fabric.sdk.android.Fabric;
import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public final class CommonUtils {
    private static Boolean clsTrace = null;
    private static final char[] HEX_VALUES = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static long totalRamInBytes = -1;
    private static Boolean loggingEnabled = null;
    public static final Comparator<File> FILE_MODIFIED_COMPARATOR = new Comparator<File>() { // from class: io.fabric.sdk.android.services.common.CommonUtils.1
        @Override // java.util.Comparator
        public final /* bridge */ /* synthetic */ int compare(File file, File file2) {
            return (int) (file.lastModified() - file2.lastModified());
        }
    };

    public static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences("com.crashlytics.prefs", 0);
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0033, code lost:            r5 = r4[1];     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static java.lang.String extractFieldFromSystemFile(java.io.File r10, java.lang.String r11) {
        /*
            r8 = 1
            r5 = 0
            boolean r6 = r10.exists()
            if (r6 == 0) goto L3c
            r0 = 0
            java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch: java.lang.Exception -> L3d java.lang.Throwable -> L5f
            java.io.FileReader r6 = new java.io.FileReader     // Catch: java.lang.Exception -> L3d java.lang.Throwable -> L5f
            r6.<init>(r10)     // Catch: java.lang.Exception -> L3d java.lang.Throwable -> L5f
            r7 = 1024(0x400, float:1.435E-42)
            r1.<init>(r6, r7)     // Catch: java.lang.Exception -> L3d java.lang.Throwable -> L5f
        L15:
            java.lang.String r3 = r1.readLine()     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L6a
            if (r3 == 0) goto L36
            java.lang.String r6 = "\\s*:\\s*"
            java.util.regex.Pattern r6 = java.util.regex.Pattern.compile(r6)     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L6a
            r7 = 2
            java.lang.String[] r4 = r6.split(r3, r7)     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L6a
            int r6 = r4.length     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L6a
            if (r6 <= r8) goto L15
            r6 = 0
            r6 = r4[r6]     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L6a
            boolean r6 = r6.equals(r11)     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L6a
            if (r6 == 0) goto L15
            r6 = 1
            r5 = r4[r6]     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L6a
        L36:
            java.lang.String r6 = "Failed to close system file reader."
            closeOrLog(r1, r6)
        L3c:
            return r5
        L3d:
            r2 = move-exception
        L3e:
            io.fabric.sdk.android.Logger r6 = io.fabric.sdk.android.Fabric.getLogger()     // Catch: java.lang.Throwable -> L5f
            java.lang.String r7 = "Fabric"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L5f
            java.lang.String r9 = "Error parsing "
            r8.<init>(r9)     // Catch: java.lang.Throwable -> L5f
            java.lang.StringBuilder r8 = r8.append(r10)     // Catch: java.lang.Throwable -> L5f
            java.lang.String r8 = r8.toString()     // Catch: java.lang.Throwable -> L5f
            r6.e(r7, r8, r2)     // Catch: java.lang.Throwable -> L5f
            java.lang.String r6 = "Failed to close system file reader."
            closeOrLog(r0, r6)
            goto L3c
        L5f:
            r6 = move-exception
        L60:
            java.lang.String r7 = "Failed to close system file reader."
            closeOrLog(r0, r7)
            throw r6
        L67:
            r6 = move-exception
            r0 = r1
            goto L60
        L6a:
            r2 = move-exception
            r0 = r1
            goto L3e
        */
        throw new UnsupportedOperationException("Method not decompiled: io.fabric.sdk.android.services.common.CommonUtils.extractFieldFromSystemFile(java.io.File, java.lang.String):java.lang.String");
    }

    public static int getCpuArchitectureInt() {
        return Architecture.getValue().ordinal();
    }

    /* loaded from: classes.dex */
    enum Architecture {
        X86_32,
        X86_64,
        ARM_UNKNOWN,
        PPC,
        PPC64,
        ARMV6,
        ARMV7,
        UNKNOWN,
        ARMV7S,
        ARM64;

        private static final Map<String, Architecture> matcher;

        static {
            HashMap hashMap = new HashMap(4);
            matcher = hashMap;
            hashMap.put("armeabi-v7a", ARMV7);
            matcher.put("armeabi", ARMV6);
            matcher.put("x86", X86_32);
        }

        static Architecture getValue() {
            String arch = Build.CPU_ABI;
            if (TextUtils.isEmpty(arch)) {
                Fabric.getLogger();
                return UNKNOWN;
            }
            Architecture value = matcher.get(arch.toLowerCase(Locale.US));
            if (value == null) {
                return UNKNOWN;
            }
            return value;
        }
    }

    public static synchronized long getTotalRamInBytes() {
        long j;
        synchronized (CommonUtils.class) {
            if (totalRamInBytes == -1) {
                long bytes = 0;
                String result = extractFieldFromSystemFile(new File("/proc/meminfo"), "MemTotal");
                if (!TextUtils.isEmpty(result)) {
                    String result2 = result.toUpperCase(Locale.US);
                    try {
                        if (result2.endsWith("KB")) {
                            bytes = convertMemInfoToBytes(result2, "KB", 1024);
                        } else if (result2.endsWith("MB")) {
                            bytes = convertMemInfoToBytes(result2, "MB", 1048576);
                        } else if (result2.endsWith("GB")) {
                            bytes = convertMemInfoToBytes(result2, "GB", 1073741824);
                        } else {
                            Fabric.getLogger();
                        }
                    } catch (NumberFormatException e) {
                        Fabric.getLogger().e("Fabric", "Unexpected meminfo format while computing RAM: " + result2, e);
                    }
                }
                totalRamInBytes = bytes;
            }
            j = totalRamInBytes;
        }
        return j;
    }

    private static long convertMemInfoToBytes(String memInfo, String notation, int notationMultiplier) {
        return Long.parseLong(memInfo.split(notation)[0].trim()) * notationMultiplier;
    }

    public static ActivityManager.RunningAppProcessInfo getAppProcessInfo(String packageName, Context context) {
        List<ActivityManager.RunningAppProcessInfo> processes = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
        if (processes == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo info : processes) {
            if (info.processName.equals(packageName)) {
                return info;
            }
        }
        return null;
    }

    public static String streamToString(InputStream is) throws IOException {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String sha1(String source) {
        return hash(source.getBytes(), "SHA-1");
    }

    private static String hash$4e276518(InputStream source) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[1024];
            while (true) {
                int length = source.read(buffer);
                if (length != -1) {
                    digest.update(buffer, 0, length);
                } else {
                    return hexify(digest.digest());
                }
            }
        } catch (Exception e) {
            Fabric.getLogger().e("Fabric", "Could not calculate hash for app icon.", e);
            return "";
        }
    }

    private static String hash(byte[] bytes, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(bytes);
            return hexify(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            Fabric.getLogger().e("Fabric", "Could not create hashing algorithm: " + algorithm + ", returning empty string.", e);
            return "";
        }
    }

    public static String createInstanceIdFrom(String... sliceIds) {
        List<String> sliceIdList = new ArrayList<>();
        for (int i$ = 0; i$ <= 0; i$++) {
            String id = sliceIds[0];
            if (id != null) {
                sliceIdList.add(id.replace(XMLResultsHandler.SEP_HYPHEN, "").toLowerCase(Locale.US));
            }
        }
        Collections.sort(sliceIdList);
        StringBuilder sb = new StringBuilder();
        Iterator i$2 = sliceIdList.iterator();
        while (i$2.hasNext()) {
            sb.append(i$2.next());
        }
        String concatValue = sb.toString();
        if (concatValue.length() > 0) {
            return sha1(concatValue);
        }
        return null;
    }

    public static long calculateFreeRamInBytes(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ((ActivityManager) context.getSystemService("activity")).getMemoryInfo(mi);
        return mi.availMem;
    }

    public static long calculateUsedDiskSpaceInBytes(String path) {
        StatFs statFs = new StatFs(path);
        long blockSizeBytes = statFs.getBlockSize();
        long totalSpaceBytes = blockSizeBytes * statFs.getBlockCount();
        long availableSpaceBytes = blockSizeBytes * statFs.getAvailableBlocks();
        return totalSpaceBytes - availableSpaceBytes;
    }

    public static float getBatteryLevel(Context context) {
        IntentFilter ifilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        Intent battery = context.registerReceiver(null, ifilter);
        int level = battery.getIntExtra("level", -1);
        int scale = battery.getIntExtra("scale", -1);
        return level / scale;
    }

    public static boolean getProximitySensorEnabled(Context context) {
        return (isEmulator(context) || ((SensorManager) context.getSystemService("sensor")).getDefaultSensor(8) == null) ? false : true;
    }

    public static void logControlled$5ffc00fd(Context context) {
        if (isClsTrace(context)) {
            Fabric.getLogger();
        }
    }

    public static void logControlledError$43da9ce8(Context context, String msg) {
        if (isClsTrace(context)) {
            Fabric.getLogger().e("Fabric", msg);
        }
    }

    public static void logControlled$3aaf2084$3f52113a(Context context, String msg) {
        if (isClsTrace(context)) {
            Fabric.getLogger().log$6ef37c42("Fabric", msg);
        }
    }

    public static boolean isClsTrace(Context context) {
        if (clsTrace == null) {
            clsTrace = Boolean.valueOf(getBooleanResourceValue(context, "com.crashlytics.Trace", false));
        }
        return clsTrace.booleanValue();
    }

    public static boolean getBooleanResourceValue(Context context, String key, boolean defaultValue) {
        Resources resources;
        if (context != null && (resources = context.getResources()) != null) {
            int id = getResourcesIdentifier(context, key, "bool");
            if (id > 0) {
                return resources.getBoolean(id);
            }
            int id2 = getResourcesIdentifier(context, key, "string");
            if (id2 > 0) {
                return Boolean.parseBoolean(context.getString(id2));
            }
            return defaultValue;
        }
        return defaultValue;
    }

    public static int getResourcesIdentifier(Context context, String key, String resourceType) {
        String packageName;
        Resources resources = context.getResources();
        int i = context.getApplicationContext().getApplicationInfo().icon;
        if (i > 0) {
            packageName = context.getResources().getResourcePackageName(i);
        } else {
            packageName = context.getPackageName();
        }
        return resources.getIdentifier(key, resourceType, packageName);
    }

    public static boolean isEmulator(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), "android_id");
        return ServerProtocol.DIALOG_PARAM_SDK_VERSION.equals(Build.PRODUCT) || "google_sdk".equals(Build.PRODUCT) || androidId == null;
    }

    public static boolean isRooted(Context context) {
        boolean isEmulator = isEmulator(context);
        String buildTags = Build.TAGS;
        if ((!isEmulator && buildTags != null && buildTags.contains("test-keys")) || new File("/system/app/Superuser.apk").exists()) {
            return true;
        }
        File file = new File("/system/xbin/su");
        return !isEmulator && file.exists();
    }

    public static int getDeviceState(Context context) {
        int deviceState = 0;
        if (isEmulator(context)) {
            deviceState = 1;
        }
        if (isRooted(context)) {
            deviceState |= 2;
        }
        if (Debug.isDebuggerConnected() || Debug.waitingForDebugger()) {
            return deviceState | 4;
        }
        return deviceState;
    }

    public static int getBatteryVelocity(Context context, boolean powerConnected) {
        float batterLevel = getBatteryLevel(context);
        if (!powerConnected) {
            return 1;
        }
        if (powerConnected && batterLevel >= 99.0d) {
            return 3;
        }
        if (powerConnected && batterLevel < 99.0d) {
            return 2;
        }
        return 0;
    }

    @SuppressLint({"GetInstance"})
    public static Cipher createCipher$4ef6f629(String key) throws InvalidKeyException {
        if (key.length() < 32) {
            throw new InvalidKeyException("Key must be at least 32 bytes.");
        }
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), 0, 32, "AES/ECB/PKCS7Padding");
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(1, secretKey);
            return cipher;
        } catch (GeneralSecurityException e) {
            Fabric.getLogger().e("Fabric", "Could not create Cipher for AES/ECB/PKCS7Padding - should never happen.", e);
            throw new RuntimeException(e);
        }
    }

    public static String hexify(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 255;
            hexChars[i * 2] = HEX_VALUES[v >>> 4];
            hexChars[(i * 2) + 1] = HEX_VALUES[v & 15];
        }
        return new String(hexChars);
    }

    public static boolean isAppDebuggable(Context context) {
        return (context.getApplicationInfo().flags & 2) != 0;
    }

    public static String getStringsFileValue(Context context, String key) {
        int id = getResourcesIdentifier(context, key, "string");
        return id > 0 ? context.getString(id) : "";
    }

    public static void closeOrLog(Closeable c, String message) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                Fabric.getLogger().e("Fabric", message, e);
            }
        }
    }

    public static void flushOrLog(Flushable f, String message) {
        if (f != null) {
            try {
                f.flush();
            } catch (IOException e) {
                Fabric.getLogger().e("Fabric", message, e);
            }
        }
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static void copyStream(InputStream is, OutputStream os, byte[] buffer) throws IOException {
        while (true) {
            int count = is.read(buffer);
            if (count != -1) {
                os.write(buffer, 0, count);
            } else {
                return;
            }
        }
    }

    public static String getAppIconHashOrNull(Context context) {
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(getAppIconResourceId(context));
            String sha1 = hash$4e276518(is);
            if (isNullOrEmpty(sha1)) {
                sha1 = null;
            }
            return sha1;
        } catch (Exception e) {
            Fabric.getLogger().e("Fabric", "Could not calculate hash for app icon.", e);
            return null;
        } finally {
            closeOrLog(is, "Failed to close icon input stream.");
        }
    }

    public static int getAppIconResourceId(Context context) {
        return context.getApplicationContext().getApplicationInfo().icon;
    }

    public static String resolveBuildId(Context context) {
        int id = getResourcesIdentifier(context, "io.fabric.android.build_id", "string");
        if (id == 0) {
            id = getResourcesIdentifier(context, "com.crashlytics.android.build_id", "string");
        }
        if (id == 0) {
            return null;
        }
        String buildId = context.getResources().getString(id);
        Fabric.getLogger();
        return buildId;
    }

    public static boolean canTryConnection(Context context) {
        if (!(context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == 0)) {
            return true;
        }
        NetworkInfo activeNetwork = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
