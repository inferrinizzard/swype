package io.fabric.sdk.android.services.settings;

import io.fabric.sdk.android.Kit;
import java.util.Collection;

/* loaded from: classes.dex */
public final class AppRequestData {
    public final String apiKey;
    public final String appId;
    public final String buildVersion;
    public final String builtSdkVersion;
    public final String displayVersion;
    public final IconRequest icon;
    public final String instanceIdentifier;
    public final String minSdkVersion;
    public final String name;
    public final Collection<Kit> sdkKits;
    public final int source;

    public AppRequestData(String apiKey, String appId, String displayVersion, String buildVersion, String instanceIdentifier, String name, int source, String minSdkVersion, String builtSdkVersion, IconRequest icon, Collection<Kit> sdkKits) {
        this.apiKey = apiKey;
        this.appId = appId;
        this.displayVersion = displayVersion;
        this.buildVersion = buildVersion;
        this.instanceIdentifier = instanceIdentifier;
        this.name = name;
        this.source = source;
        this.minSdkVersion = minSdkVersion;
        this.builtSdkVersion = builtSdkVersion;
        this.icon = icon;
        this.sdkKits = sdkKits;
    }
}
