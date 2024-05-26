package com.nuance.connect.api;

import java.io.File;
import java.util.List;

/* loaded from: classes.dex */
public interface LanguageService {

    /* loaded from: classes.dex */
    public interface DownloadCallback {
        boolean downloadComplete(File file);

        void downloadFailed(int i);

        void downloadPercentage(int i);

        void downloadStarted();

        void downloadStopped(int i);

        int getVersion();
    }

    /* loaded from: classes.dex */
    public static class LdbInfo {
        final String countryCode;
        final String displayName;
        final String flavor;
        final int version;
        final int xt9LangId;
        final int[] xt9LangIds;

        public LdbInfo(int[] iArr, String str, String str2, String str3, int i) {
            this.xt9LangIds = iArr == null ? new int[0] : iArr;
            this.xt9LangId = this.xt9LangIds.length > 0 ? this.xt9LangIds[0] : 0;
            this.version = i;
            this.flavor = str;
            this.displayName = str2;
            this.countryCode = str3;
        }

        public String getCountryCode() {
            return this.countryCode;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String getFlavor() {
            return this.flavor;
        }

        public int getVersion() {
            return this.version;
        }

        public int getXT9LanguageId() {
            return this.xt9LangId;
        }

        public int[] getXT9LanguageIds() {
            return this.xt9LangIds;
        }
    }

    /* loaded from: classes.dex */
    public interface ListCallback {
        void languageListUpdate();
    }

    void addSupportedLanguage(int i, boolean z);

    void cancelDownload(int i) throws ConnectException;

    void cancelDownload(int i, String str) throws ConnectException;

    void downloadLanguage(int i, DownloadCallback downloadCallback) throws ConnectException;

    void downloadLanguage(int i, String str, DownloadCallback downloadCallback) throws ConnectException;

    List<LdbInfo> getDownloadLdbList();

    int getPrimaryLanguageId(int i);

    boolean isLanguageListAvailable();

    void languageUninstalled(int i) throws ConnectException;

    void languageUninstalled(int i, String str) throws ConnectException;

    void notifyCallbacksOfStatus();

    void registerCallback(ListCallback listCallback);

    void unregisterCallback(ListCallback listCallback);

    void unregisterCallbacks();
}
