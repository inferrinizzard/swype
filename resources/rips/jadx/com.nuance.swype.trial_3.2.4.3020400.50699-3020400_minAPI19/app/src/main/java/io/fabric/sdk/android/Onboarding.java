package io.fabric.sdk.android;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.DeliveryMechanism;
import io.fabric.sdk.android.services.network.DefaultHttpRequestFactory;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import io.fabric.sdk.android.services.settings.AppRequestData;
import io.fabric.sdk.android.services.settings.AppSettingsData;
import io.fabric.sdk.android.services.settings.CreateAppSpiCall;
import io.fabric.sdk.android.services.settings.IconRequest;
import io.fabric.sdk.android.services.settings.Settings;
import io.fabric.sdk.android.services.settings.SettingsData;
import io.fabric.sdk.android.services.settings.UpdateAppSpiCall;
import java.util.Collection;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class Onboarding extends Kit<Boolean> {
    private String applicationLabel;
    private String installerPackageName;
    private final Collection<Kit> kits;
    private PackageInfo packageInfo;
    private PackageManager packageManager;
    private String packageName;
    private final HttpRequestFactory requestFactory = new DefaultHttpRequestFactory();
    private String targetAndroidSdkVersion;
    private String versionCode;
    private String versionName;

    public Onboarding(Collection<Kit> kits) {
        this.kits = kits;
    }

    @Override // io.fabric.sdk.android.Kit
    public final String getVersion() {
        return "1.2.0.37";
    }

    @Override // io.fabric.sdk.android.Kit
    public final String getIdentifier() {
        return "io.fabric.sdk.android:fabric";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.fabric.sdk.android.Kit
    public final boolean onPreExecute() {
        boolean z = false;
        try {
            this.installerPackageName = this.idManager.getInstallerPackageName();
            this.packageManager = this.context.getPackageManager();
            this.packageName = this.context.getPackageName();
            this.packageInfo = this.packageManager.getPackageInfo(this.packageName, 0);
            this.versionCode = Integer.toString(this.packageInfo.versionCode);
            this.versionName = this.packageInfo.versionName == null ? "0.0" : this.packageInfo.versionName;
            this.applicationLabel = this.packageManager.getApplicationLabel(this.context.getApplicationInfo()).toString();
            this.targetAndroidSdkVersion = Integer.toString(this.context.getApplicationInfo().targetSdkVersion);
            z = true;
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Fabric.getLogger().e("Fabric", "Failed init", e);
            return z;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // io.fabric.sdk.android.Kit
    public Boolean doInBackground() {
        String iconHash = CommonUtils.getAppIconHashOrNull(this.context);
        boolean appConfigured = false;
        SettingsData settingsData = null;
        try {
            Settings.LazyHolder.access$100().initialize(this, this.idManager, this.requestFactory, this.versionCode, this.versionName, getOverridenSpiEndpoint()).loadSettingsData();
            settingsData = Settings.LazyHolder.access$100().awaitSettingsData();
        } catch (Exception e) {
            Fabric.getLogger().e("Fabric", "Error dealing with settings", e);
        }
        if (settingsData != null) {
            try {
                AppSettingsData appSettingsData = settingsData.appData;
                Collection<Kit> collection = this.kits;
                if (!"new".equals(appSettingsData.status)) {
                    if ("configured".equals(appSettingsData.status)) {
                        appConfigured = Settings.LazyHolder.access$100().loadSettingsSkippingCache();
                    } else {
                        if (appSettingsData.updateRequired) {
                            Fabric.getLogger();
                            new UpdateAppSpiCall(this, getOverridenSpiEndpoint(), appSettingsData.url, this.requestFactory).invoke(buildAppRequest(IconRequest.build(this.context, iconHash), collection));
                        }
                        appConfigured = true;
                    }
                } else {
                    if (new CreateAppSpiCall(this, getOverridenSpiEndpoint(), appSettingsData.url, this.requestFactory).invoke(buildAppRequest(IconRequest.build(this.context, iconHash), collection))) {
                        appConfigured = Settings.LazyHolder.access$100().loadSettingsSkippingCache();
                    } else {
                        Fabric.getLogger().e("Fabric", "Failed to create app with Crashlytics service.", null);
                        appConfigured = false;
                    }
                }
            } catch (Exception e2) {
                Fabric.getLogger().e("Fabric", "Error performing auto configuration.", e2);
            }
        }
        return Boolean.valueOf(appConfigured);
    }

    private AppRequestData buildAppRequest(IconRequest iconRequest, Collection<Kit> sdkKits) {
        Context context = this.context;
        new ApiKey();
        String apiKey = ApiKey.getValue(context);
        String buildId = CommonUtils.resolveBuildId(context);
        String instanceId = CommonUtils.createInstanceIdFrom(buildId);
        int source = DeliveryMechanism.determineFrom(this.installerPackageName).id;
        String appIdentifier = this.idManager.appIdentifier;
        return new AppRequestData(apiKey, appIdentifier, this.versionName, this.versionCode, instanceId, this.applicationLabel, source, this.targetAndroidSdkVersion, "0", iconRequest, sdkKits);
    }

    private String getOverridenSpiEndpoint() {
        return CommonUtils.getStringsFileValue(this.context, "com.crashlytics.ApiEndpoint");
    }
}
