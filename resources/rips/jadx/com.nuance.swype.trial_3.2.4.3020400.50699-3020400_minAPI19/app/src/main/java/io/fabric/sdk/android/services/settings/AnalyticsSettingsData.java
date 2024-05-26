package io.fabric.sdk.android.services.settings;

/* loaded from: classes.dex */
public final class AnalyticsSettingsData {
    public final String analyticsURL;
    public final int flushIntervalSeconds;
    public final int maxByteSizePerFile;
    public final int maxFileCountPerSend;
    public final int maxPendingSendFileCount;

    public AnalyticsSettingsData(String analyticsURL, int flushIntervalSeconds, int maxByteSizePerFile, int maxFileCountPerSend, int maxPendingSendFileCount) {
        this.analyticsURL = analyticsURL;
        this.flushIntervalSeconds = flushIntervalSeconds;
        this.maxByteSizePerFile = maxByteSizePerFile;
        this.maxFileCountPerSend = maxFileCountPerSend;
        this.maxPendingSendFileCount = maxPendingSendFileCount;
    }
}
