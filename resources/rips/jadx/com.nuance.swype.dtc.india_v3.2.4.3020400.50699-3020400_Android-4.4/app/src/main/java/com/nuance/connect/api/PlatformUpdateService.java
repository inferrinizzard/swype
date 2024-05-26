package com.nuance.connect.api;

import java.io.File;

/* loaded from: classes.dex */
public interface PlatformUpdateService {

    /* loaded from: classes.dex */
    public interface DownloadCallback {
        void downloadComplete(File file);

        void downloadFailed(int i);

        void downloadPercentage(int i);

        void downloadStarted();

        void downloadStopped(int i);
    }

    /* loaded from: classes.dex */
    public interface UpdateCallback {
        void updateAvailable();
    }

    void cancelDownload();

    void downloadUpdate(DownloadCallback downloadCallback);

    boolean isAvailable();

    void registerCallback(UpdateCallback updateCallback);

    void unregisterCallback(DownloadCallback downloadCallback);

    void unregisterCallback(UpdateCallback updateCallback);

    void unregisterCallbacks();
}
