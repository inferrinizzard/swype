package com.nuance.connect.api;

import java.io.File;
import java.util.List;

/* loaded from: classes.dex */
public interface AddonDictionariesService {

    /* loaded from: classes.dex */
    public interface AddonDictionary {
        String getCategory();

        String getCategoryTranslated();

        String getDictionary();

        int getId();

        int getLanguage();

        String getName();

        String getNameTranslated();

        int getRank();

        boolean hasUpdate();

        boolean isInstalled();

        boolean isSubscribed();
    }

    /* loaded from: classes.dex */
    public interface AddonDictionaryDownloadCallback {
        boolean downloadComplete(File file);

        void downloadFailed(int i);

        void downloadPercentage(int i);

        void downloadStarted();

        void downloadStopped(int i);
    }

    /* loaded from: classes.dex */
    public interface AddonDictionaryListCallback {
        void listUpdated();
    }

    void cancelDownload(String str);

    List<AddonDictionary> getAvailableDictionaries();

    void installDictionary(String str, AddonDictionaryDownloadCallback addonDictionaryDownloadCallback);

    boolean isDictionaryListAvailable();

    void markDictionaryInstalled(String str);

    void notifyDictionariesofStatus();

    void registerDictionaryListCallback(AddonDictionaryListCallback addonDictionaryListCallback);

    void removeDictionary(String str);

    void unregisterDictionaryListCallback(AddonDictionaryListCallback addonDictionaryListCallback);

    void unregisterDictionaryListCallbacks();
}
