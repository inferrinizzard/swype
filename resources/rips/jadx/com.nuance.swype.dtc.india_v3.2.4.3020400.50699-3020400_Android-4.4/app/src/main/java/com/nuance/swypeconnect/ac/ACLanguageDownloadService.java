package com.nuance.swypeconnect.ac;

import java.io.File;
import java.util.List;

/* loaded from: classes.dex */
public interface ACLanguageDownloadService {
    public static final int REASON_FAILED_DISK_FULL = 6;
    public static final int REASON_FAILED_HTTP = 0;
    public static final int REASON_FAILED_MAX_RETRY = 1;
    public static final int REASON_NETWORK_TIMEOUT = 4;
    public static final int REASON_USER_CANCELED = 3;

    /* loaded from: classes.dex */
    public interface ACLanguageDbInfo {
        String getCountryCode();

        String getDisplayName();

        List<ACLdbType> getEnhancedLanguageModels();

        String[] getFlavors();

        ACLdbType[] getTypes();

        int getXt9LanguageId();

        int[] getXt9LanguageIds();

        String installedFlavor();

        ACLdbType installedType();

        boolean isDownloaded();

        boolean isInstalled();

        boolean isNewerVersionAvailable();

        boolean isPreinstalled();
    }

    /* loaded from: classes.dex */
    public interface ACLanguageDownloadFileCallback {
        boolean downloadComplete(File file);

        void downloadFailed(int i);

        void downloadPercentage(int i);

        void downloadStarted();

        void downloadStopped(int i);
    }

    /* loaded from: classes.dex */
    public interface ACLanguageListCallback {
        void onLanguageListUpdate();
    }

    /* loaded from: classes.dex */
    public enum ACLdbType {
        Unspecified,
        Baseline,
        MLM,
        ALM;

        static ACLdbType[] values = values();

        public static ACLdbType fromString(String str) {
            if (str != null) {
                for (ACLdbType aCLdbType : values()) {
                    if (str.equalsIgnoreCase(aCLdbType.name())) {
                        return aCLdbType;
                    }
                }
            }
            return Unspecified;
        }
    }

    void addExistingLanguage(Integer num, String[] strArr);

    void addExistingLanguage(String str);

    void addSupportedLanguage(Integer num);

    void addSupportedLanguages(List<Integer> list);

    void cancelDownload(int i) throws ACException;

    void downloadLanguage(int i, ACLanguageDownloadFileCallback aCLanguageDownloadFileCallback) throws ACException;

    void downloadLanguage(int i, ACLdbType aCLdbType, ACLanguageDownloadFileCallback aCLanguageDownloadFileCallback) throws ACException;

    void downloadLanguageFlavor(int i, String str, ACLanguageDownloadFileCallback aCLanguageDownloadFileCallback) throws ACException;

    ACLanguageDbInfo getDatabase(int i) throws ACException;

    List<ACLanguageDbInfo> getDatabaseList();

    boolean isLanguageListAvailable();

    void languageUninstalled(int i) throws ACException;

    void registerCallback(ACLanguageListCallback aCLanguageListCallback);

    void removeSupportedLanguage(Integer num);

    void unregisterCallback(ACLanguageListCallback aCLanguageListCallback);

    void unregisterCallbacks();
}
