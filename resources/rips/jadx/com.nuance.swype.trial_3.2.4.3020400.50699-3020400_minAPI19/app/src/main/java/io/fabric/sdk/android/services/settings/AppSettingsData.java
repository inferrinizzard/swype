package io.fabric.sdk.android.services.settings;

/* loaded from: classes.dex */
public final class AppSettingsData {
    public final AppIconSettingsData icon;
    public final String identifier;
    public final String reportsUrl;
    public final String status;
    public final boolean updateRequired;
    public final String url;

    public AppSettingsData(String identifier, String status, String url, String reportsUrl, boolean updateRequired, AppIconSettingsData icon) {
        this.identifier = identifier;
        this.status = status;
        this.url = url;
        this.reportsUrl = reportsUrl;
        this.updateRequired = updateRequired;
        this.icon = icon;
    }
}
