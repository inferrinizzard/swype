package com.nuance.swype.connect;

import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class SDKDownloadStatusCallback {
    public static final LogManager.Log log = LogManager.getLog("SDKDownloadStatusCallback");
    private int currentPercent;
    private final String identifier;
    private final int type;

    public SDKDownloadStatusCallback(String identifier, int type) {
        log.d("[SDKDownloadStatusCallback] ", "identifier: [", identifier, "] ", "type:", Integer.valueOf(type));
        this.identifier = identifier;
        this.type = type;
        this.currentPercent = 0;
    }

    public String getIdentifer() {
        return this.identifier;
    }

    public int getType() {
        return this.type;
    }

    public int getPercent() {
        return this.currentPercent;
    }

    public void downloadStarted() {
        log.d("[SDKDownloadStatusCallback] ", "downloadStarted");
    }

    public void downloadPercentage(int percent) {
        log.d("[SDKDownloadStatusCallback] ", "downloadPercentage:", Integer.valueOf(percent));
        this.currentPercent = percent;
    }

    public void downloadStopped(int reasonCode) {
        log.d("[SDKDownloadStatusCallback] ", "downloadStopped:", Integer.valueOf(reasonCode));
    }

    public void downloadFailed(int reasonCode) {
        log.d("[SDKDownloadStatusCallback] ", "downloadFailed:", Integer.valueOf(reasonCode));
    }

    public void downloadComplete() {
        log.d("[SDKDownloadStatusCallback] ", "downloadComplete");
    }

    public void downloadInstalled() {
        log.d("[SDKDownloadStatusCallback] ", "downloadInstalled");
    }
}
