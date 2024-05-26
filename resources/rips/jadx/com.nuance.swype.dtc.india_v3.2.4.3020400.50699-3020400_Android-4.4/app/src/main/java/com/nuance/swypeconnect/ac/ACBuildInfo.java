package com.nuance.swypeconnect.ac;

import com.nuance.connect.host.service.BuildSettings;

/* loaded from: classes.dex */
public final class ACBuildInfo {
    public static final int LOCATION_LEVEL_ALL = 2;
    public static final int LOCATION_LEVEL_NETWORK = 1;
    public static final int LOCATION_LEVEL_OFF = 0;
    private BuildSettings buildSettings;

    /* JADX INFO: Access modifiers changed from: protected */
    public ACBuildInfo(ACManager aCManager) {
        this.buildSettings = aCManager.getBuildSettings();
    }

    public final String getBuildDate() {
        return this.buildSettings.getBuildTimestamp();
    }

    public final String getBuildExpiration() {
        return this.buildSettings.getBuildExpiration();
    }

    public final int getLocationLevel() {
        return this.buildSettings.getLocationLevel();
    }

    public final int getLogLevel() {
        return this.buildSettings.getLogLevel();
    }

    public final String getOemId() {
        return this.buildSettings.getOemId();
    }

    public final String getURL() {
        return this.buildSettings.getConnectUrl();
    }

    public final String getVersion() {
        return this.buildSettings.getVersion();
    }

    public final boolean isDeveloperLogEnabled() {
        return this.buildSettings.isDeveloperLogEnabled();
    }
}
