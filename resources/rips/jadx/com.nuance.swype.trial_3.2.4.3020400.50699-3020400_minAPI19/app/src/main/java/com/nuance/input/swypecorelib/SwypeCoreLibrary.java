package com.nuance.input.swypecorelib;

import android.content.Context;
import android.util.Log;
import com.nuance.input.swypecorelib.usagedata.SessionDataCollector;
import com.nuance.input.swypecorelib.usagedata.SessionDataDefaultCollector;
import java.io.File;
import java.util.EnumMap;
import java.util.EnumSet;
import jp.co.omronsoft.openwnn.JAJP.OpenWnnEngineJAJP;

/* loaded from: classes.dex */
public class SwypeCoreLibrary {
    public static final String COMPRESSED_FILE_EXTENSION = ".mp3";
    private static final String JP_WNN_ENGINE_JAJP_DIC = "writableJAJP.dic";
    public static final int RUNNING_STATE_BACKGROUND_UI_HIDDEN = 1;
    private static final String TAG = "SwypeCoreLibrary";
    private static SwypeCoreLibrary swypecorelibInstance;
    private XT9CoreAlphaInput alphaCoreInputInstance;
    private SessionDataCollector alphaSessionDataCollector;
    private final Context appContext;
    private XT9CoreChineseInput chineseCoreInputInstance;
    private final EnumSet<Feature> enabled;
    private XT9CoreJapaneseInput japaneseCoreInputInstance;
    private XT9CoreKoreanInput koreanCoreInputInstance;
    private int runningState = 1;
    private final EnumMap<Core, String> versions;
    public static int RUNNING_STATE_FOREGROUND_UI = 0;
    public static int RUNNING_STATE_BACKGROUND_HIBERNATE = 2;

    /* loaded from: classes.dex */
    public enum Core {
        XT9,
        XT9BuildID,
        Trace,
        WriteAlpha,
        WriteChinese
    }

    /* loaded from: classes.dex */
    public enum Feature {
        Trace,
        ChineseInput,
        WriteAlpha,
        WriteChinese,
        WriteJapanese
    }

    private static native byte[] config_getLanguages(Context context);

    private static native String getBuildInfo();

    private static native String getCoreVersions();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native boolean isJapaneseEnabled();

    static native void nativeEvictLdbCache(int i);

    static native void nativeGetAllLdbCacheIds(int[] iArr);

    static native int nativeGetLdbCacheSize();

    static native int nativeGetMostRecentUsedLdbIdCached();

    private static native void nativeSetRunningState(int i);

    private static native void refresh_dbconfig(String str);

    private static native void setApplicationContext(Context context, String str);

    public static SwypeCoreLibrary getInstance(Context appContext, String coreLibName, String nativeLibraryPath) {
        if (swypecorelibInstance == null) {
            boolean loadNativeLib = loadNativeLibrary(appContext, coreLibName, nativeLibraryPath);
            try {
                swypecorelibInstance = new SwypeCoreLibrary(appContext);
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "loadNativeLibrary() error: " + nativeLibraryPath);
            }
            if (!loadNativeLib) {
                Log.e(TAG, "loadNativeLibrary() error: " + nativeLibraryPath);
            }
        }
        return swypecorelibInstance;
    }

    private static boolean loadNativeLibrary(Context appContext, String coreLibName, String nativeLibPath) {
        if (!loadNativeLibraryInternal(appContext, coreLibName, nativeLibPath)) {
            return false;
        }
        setNativeApplicationContext(appContext, appContext.getFilesDir().getPath());
        return true;
    }

    private static boolean tryLoad(File libFile) {
        String path = libFile.getPath();
        if (libFile.exists()) {
            try {
                System.load(path);
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e);
            } catch (UnsatisfiedLinkError ex) {
                new StringBuilder("tryLoad(): failed: ").append(ex);
            }
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x006c  */
    /* JADX WARN: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static boolean loadNativeLibraryInternal(android.content.Context r9, java.lang.String r10, java.lang.String r11) {
        /*
            r5 = 1
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            java.lang.String r7 = "#loadNativeLibrary() libName: "
            r6.<init>(r7)
            java.lang.StringBuilder r6 = r6.append(r10)
            java.lang.String r7 = " path: "
            java.lang.StringBuilder r6 = r6.append(r7)
            r6.append(r11)
            java.lang.String r2 = java.lang.System.mapLibraryName(r10)
            java.io.File r3 = new java.io.File
            java.io.File r6 = new java.io.File
            android.content.pm.ApplicationInfo r7 = r9.getApplicationInfo()
            java.lang.String r7 = r7.dataDir
            java.lang.String r8 = "lib"
            r6.<init>(r7, r8)
            r3.<init>(r6, r2)
            boolean r6 = tryLoad(r3)
            if (r6 == 0) goto L35
        L34:
            return r5
        L35:
            java.io.File r4 = new java.io.File
            r4.<init>(r11, r2)
            boolean r6 = r4.equals(r3)
            if (r6 != 0) goto L46
            boolean r6 = tryLoad(r4)
            if (r6 != 0) goto L34
        L46:
            java.lang.System.loadLibrary(r10)     // Catch: java.lang.UnsatisfiedLinkError -> L4a java.lang.Exception -> L84
            goto L34
        L4a:
            r1 = move-exception
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            java.lang.String r7 = "loadNativeLibraryInternal(): failed: "
            r6.<init>(r7)
            r6.append(r1)
        L56:
            java.io.File r6 = new java.io.File
            java.io.File r7 = new java.io.File
            android.content.pm.ApplicationInfo r8 = r9.getApplicationInfo()
            java.lang.String r8 = r8.nativeLibraryDir
            r7.<init>(r8)
            r6.<init>(r7, r2)
            boolean r6 = tryLoad(r6)
            if (r6 != 0) goto L34
            java.lang.String r5 = "SwypeCoreLibrary"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            java.lang.String r7 = "Could not load native library "
            r6.<init>(r7)
            java.lang.StringBuilder r6 = r6.append(r10)
            java.lang.String r6 = r6.toString()
            android.util.Log.e(r5, r6)
            r5 = 0
            goto L34
        L84:
            r0 = move-exception
            java.lang.String r6 = "SwypeCoreLibrary"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            java.lang.String r8 = "Exception: "
            r7.<init>(r8)
            java.lang.StringBuilder r7 = r7.append(r0)
            java.lang.String r7 = r7.toString()
            android.util.Log.e(r6, r7)
            goto L56
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.input.swypecorelib.SwypeCoreLibrary.loadNativeLibraryInternal(android.content.Context, java.lang.String, java.lang.String):boolean");
    }

    private SwypeCoreLibrary(Context appContext) {
        this.appContext = appContext;
        String buildInfo = getBuildInfo();
        String coreVersions = getCoreVersions();
        this.enabled = parseBuildInfo(buildInfo);
        this.versions = parseCoreVersions(coreVersions);
    }

    private static void setNativeApplicationContext(Context context, String filePath) {
        setApplicationContext(context, filePath);
    }

    public XT9CoreAlphaInput getAlphaCoreInstance() {
        if (this.alphaCoreInputInstance == null) {
            this.alphaSessionDataCollector = new SessionDataCollector(getXT9CoreVersion());
            this.alphaCoreInputInstance = new XT9CoreAlphaInput(this.alphaSessionDataCollector);
        }
        return this.alphaCoreInputInstance;
    }

    public XT9CoreChineseInput getChineseCoreInstance() {
        if (this.chineseCoreInputInstance == null) {
            this.chineseCoreInputInstance = new XT9CoreChineseInput(new SessionDataDefaultCollector());
        }
        return this.chineseCoreInputInstance;
    }

    public XT9CoreKoreanInput getKoreanCoreInstance(XT9CoreAlphaInput alphaInput) {
        if (this.koreanCoreInputInstance == null) {
            this.koreanCoreInputInstance = new XT9CoreKoreanInput(alphaInput, new SessionDataDefaultCollector());
        }
        return this.koreanCoreInputInstance;
    }

    public XT9CoreJapaneseInput getJapaneseCoreInstance(XT9CoreAlphaInput alphaInput) {
        if (this.japaneseCoreInputInstance == null) {
            this.japaneseCoreInputInstance = new XT9CoreJapaneseInput(alphaInput, new SessionDataDefaultCollector());
        }
        return this.japaneseCoreInputInstance;
    }

    public boolean isAlphaInputHasContext() {
        return this.alphaCoreInputInstance != null && this.alphaCoreInputInstance.hasInputContext();
    }

    public boolean isKoreanInputHasContext() {
        return this.koreanCoreInputInstance != null && this.koreanCoreInputInstance.hasInputContext();
    }

    public boolean isJapaneseInputHasContext() {
        return this.japaneseCoreInputInstance != null && this.japaneseCoreInputInstance.hasInputContext();
    }

    public boolean isChineseInputHasContext() {
        return this.chineseCoreInputInstance != null && this.chineseCoreInputInstance.hasInputContext();
    }

    /* loaded from: classes.dex */
    private static class T9WriteAlphaSingletonHolder {
        private static final T9WriteAlpha T9WRITE_ALPHA_INSTANCE = new T9WriteAlpha();

        private T9WriteAlphaSingletonHolder() {
        }
    }

    /* loaded from: classes.dex */
    private static class T9WriteChineseSingletonHolder {
        private static final T9WriteChinese T9WRITE_CHINESE_INSTANCE = new T9WriteChinese();

        private T9WriteChineseSingletonHolder() {
        }
    }

    /* loaded from: classes.dex */
    private static class T9WriteKoreanSingletonHolder {
        private static final T9WriteKorean T9WRITIE_KOREAN_INSTANCE = new T9WriteKorean();

        private T9WriteKoreanSingletonHolder() {
        }
    }

    /* loaded from: classes.dex */
    private static class T9WriteJapaneseSingletonHolder {
        private static final T9WriteJapanese T9WRITE_JAPANESE_INSTANCE = new T9WriteJapanese();

        private T9WriteJapaneseSingletonHolder() {
        }
    }

    public T9WriteAlpha getT9WriteAlphaInstance() {
        return T9WriteAlphaSingletonHolder.T9WRITE_ALPHA_INSTANCE;
    }

    public T9WriteChinese getT9WriteChineseInstance() {
        return T9WriteChineseSingletonHolder.T9WRITE_CHINESE_INSTANCE;
    }

    public T9WriteKorean getT9WriteKoreanInstance() {
        return T9WriteKoreanSingletonHolder.T9WRITIE_KOREAN_INSTANCE;
    }

    public T9WriteJapanese getT9WriteJapaneseInstance() {
        return T9WriteJapaneseSingletonHolder.T9WRITE_JAPANESE_INSTANCE;
    }

    public OpenWnnEngineJAJP createOpenWnnEngineJAJP(String coreLibName, String nativeLibraryPath) {
        String libName = System.mapLibraryName(coreLibName);
        String libpath = libName;
        if (nativeLibraryPath != null) {
            try {
                File nativeLibFile = new File(nativeLibraryPath, libName);
                if (nativeLibFile.exists()) {
                    libpath = nativeLibFile.getPath();
                }
            } catch (UnsatisfiedLinkError e) {
            }
        }
        String syspath = "/system/lib/" + libName;
        String dicpath = this.appContext.getApplicationInfo().dataDir + "/writableJAJP.dic";
        return new OpenWnnEngineJAJP(libpath, syspath, dicpath);
    }

    public boolean isTraceBuildEnabled() {
        return isEnabled(Feature.Trace);
    }

    public boolean isChineseInputBuildEnabled() {
        return isEnabled(Feature.ChineseInput);
    }

    public boolean isWriteAlphaBuildEnabled() {
        return isEnabled(Feature.WriteAlpha);
    }

    public boolean isWriteChineseBuildEnabled() {
        return isEnabled(Feature.WriteChinese);
    }

    public boolean isWriteJapaneseBuildEnabled() {
        return isEnabled(Feature.WriteJapanese);
    }

    public String getXT9CoreVersion() {
        return getVersion(Core.XT9);
    }

    public String getXT9CoreBuildId() {
        return getVersion(Core.XT9BuildID);
    }

    public String getT9TraceVersion() {
        return getVersion(Core.Trace);
    }

    public String getT9WriteAlphaCoreVersion() {
        return getVersion(Core.WriteAlpha);
    }

    public String getT9WriteChineseCoreVersion() {
        return getVersion(Core.WriteChinese);
    }

    private EnumSet<Feature> parseBuildInfo(String buildInfo) {
        EnumSet<Feature> features = EnumSet.noneOf(Feature.class);
        if (buildInfo != null) {
            if (buildInfo.contains("trace:")) {
                features.add(Feature.Trace);
            }
            if (buildInfo.contains("write_alpha:")) {
                features.add(Feature.WriteAlpha);
            }
            if (buildInfo.contains("chinese_input:")) {
                features.add(Feature.ChineseInput);
            }
            if (buildInfo.contains("write_chinese:")) {
                features.add(Feature.WriteChinese);
            }
        }
        return features;
    }

    private EnumMap<Core, String> parseCoreVersions(String coreVersions) {
        EnumMap<Core, String> map = new EnumMap<>((Class<Core>) Core.class);
        if (coreVersions != null) {
            for (String version : coreVersions.split(";")) {
                int index = version.indexOf(61);
                String coreName = version.substring(0, index);
                String coreVersion = version.substring(index + 1);
                if (coreName.equals("xt9core_version")) {
                    map.put((EnumMap<Core, String>) Core.XT9, (Core) coreVersion);
                } else if (coreName.equals("xt9core_build_id")) {
                    map.put((EnumMap<Core, String>) Core.XT9BuildID, (Core) coreVersion);
                } else if (coreName.equals("t9trace_version")) {
                    map.put((EnumMap<Core, String>) Core.Trace, (Core) coreVersion);
                } else if (coreName.equals("t9write_alpha_version")) {
                    map.put((EnumMap<Core, String>) Core.WriteAlpha, (Core) coreVersion);
                } else if (coreName.equals("t9write_chinese_version")) {
                    map.put((EnumMap<Core, String>) Core.WriteChinese, (Core) coreVersion);
                }
            }
        }
        return map;
    }

    public String getVersion(Core core) {
        return this.versions.get(core);
    }

    public boolean isEnabled(Feature feature) {
        return this.enabled.contains(feature);
    }

    public void refreshDatabaseConfigFile(String configFile) {
        refresh_dbconfig(configFile);
    }

    public static byte[] getSpeechKey(Context context) {
        return config_getLanguages(context);
    }

    public int getRunningState() {
        return this.runningState;
    }

    public void setRunningState(int runningState) {
        this.runningState = runningState;
        nativeSetRunningState(runningState);
    }
}
