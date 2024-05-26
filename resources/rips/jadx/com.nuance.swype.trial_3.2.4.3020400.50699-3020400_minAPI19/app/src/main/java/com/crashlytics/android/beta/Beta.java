package com.crashlytics.android.beta;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.nuance.connect.common.Strings;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.cache.MemoryValueCache;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.DeviceIdentifierProvider;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.common.SystemCurrentTimeProvider;
import io.fabric.sdk.android.services.network.DefaultHttpRequestFactory;
import io.fabric.sdk.android.services.persistence.PreferenceStoreImpl;
import io.fabric.sdk.android.services.settings.BetaSettingsData;
import io.fabric.sdk.android.services.settings.Settings;
import io.fabric.sdk.android.services.settings.SettingsData;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/* loaded from: classes.dex */
public class Beta extends Kit<Boolean> implements DeviceIdentifierProvider {
    private final MemoryValueCache<String> deviceTokenCache = new MemoryValueCache<>();
    private final DeviceTokenLoader deviceTokenLoader = new DeviceTokenLoader();

    @Override // io.fabric.sdk.android.Kit
    public final String getIdentifier() {
        return "com.crashlytics.sdk.android:beta";
    }

    @Override // io.fabric.sdk.android.Kit
    public final String getVersion() {
        return "1.1.2.37";
    }

    private String getBetaDeviceToken(Context context, String installerPackageName) {
        boolean equals;
        if (Build.VERSION.SDK_INT < 11) {
            equals = installerPackageName == null;
        } else {
            equals = "io.crash.air".equals(installerPackageName);
        }
        if (equals) {
            Fabric.getLogger();
            try {
                String cachedToken = this.deviceTokenCache.get(context, this.deviceTokenLoader);
                if ("".equals(cachedToken)) {
                    return null;
                }
                return cachedToken;
            } catch (Exception e) {
                Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Failed to load the Beta device token", e);
                return null;
            }
        }
        Fabric.getLogger();
        return null;
    }

    private static BuildProperties loadBuildProperties(Context context) {
        InputStream buildPropsStream = null;
        BuildProperties buildProps = null;
        try {
            try {
                buildPropsStream = context.getAssets().open("crashlytics-build.properties");
                if (buildPropsStream != null) {
                    Properties properties = new Properties();
                    properties.load(buildPropsStream);
                    BuildProperties buildProps2 = new BuildProperties(properties.getProperty("version_code"), properties.getProperty("version_name"), properties.getProperty("build_id"), properties.getProperty("package_name"));
                    try {
                        Fabric.getLogger();
                        new StringBuilder().append(buildProps2.packageName).append(" build properties: ").append(buildProps2.versionName).append(" (").append(buildProps2.versionCode).append(") - ").append(buildProps2.buildId);
                        buildProps = buildProps2;
                    } catch (Exception e) {
                        e = e;
                        buildProps = buildProps2;
                        Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Error reading Beta build properties", e);
                        if (buildPropsStream != null) {
                            try {
                                buildPropsStream.close();
                            } catch (IOException e2) {
                                Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Error closing Beta build properties asset", e2);
                            }
                        }
                        return buildProps;
                    } catch (Throwable th) {
                        th = th;
                        if (buildPropsStream != null) {
                            try {
                                buildPropsStream.close();
                            } catch (IOException e3) {
                                Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Error closing Beta build properties asset", e3);
                            }
                        }
                        throw th;
                    }
                }
                if (buildPropsStream != null) {
                    try {
                        buildPropsStream.close();
                    } catch (IOException e4) {
                        Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Error closing Beta build properties asset", e4);
                    }
                }
            } catch (Exception e5) {
                e = e5;
            }
            return buildProps;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // io.fabric.sdk.android.services.common.DeviceIdentifierProvider
    public final Map<IdManager.DeviceIdentifierType, String> getDeviceIdentifiers() {
        String installerPackageName = this.idManager.getInstallerPackageName();
        String betaDeviceToken = getBetaDeviceToken(this.context, installerPackageName);
        Map<IdManager.DeviceIdentifierType, String> ids = new HashMap<>();
        if (!TextUtils.isEmpty(betaDeviceToken)) {
            ids.put(IdManager.DeviceIdentifierType.FONT_TOKEN, betaDeviceToken);
        }
        return ids;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.fabric.sdk.android.Kit
    public final /* bridge */ /* synthetic */ Boolean doInBackground() {
        Settings settings;
        BetaSettingsData betaSettingsData;
        boolean z = false;
        Fabric.getLogger();
        Context context = this.context;
        IdManager idManager = this.idManager;
        if (TextUtils.isEmpty(getBetaDeviceToken(context, idManager.getInstallerPackageName()))) {
            Fabric.getLogger();
            return false;
        }
        Fabric.getLogger();
        settings = Settings.LazyHolder.INSTANCE;
        SettingsData awaitSettingsData = settings.awaitSettingsData();
        if (awaitSettingsData != null) {
            betaSettingsData = awaitSettingsData.betaSettingsData;
        } else {
            betaSettingsData = null;
        }
        BuildProperties loadBuildProperties = loadBuildProperties(context);
        if (betaSettingsData != null && !TextUtils.isEmpty(betaSettingsData.updateUrl) && loadBuildProperties != null) {
            z = true;
        }
        if (z) {
            CheckForUpdatesController checkForUpdatesController = new CheckForUpdatesController(context, this, idManager, betaSettingsData, loadBuildProperties, new PreferenceStoreImpl(Fabric.getKit(Beta.class)), new SystemCurrentTimeProvider(), new DefaultHttpRequestFactory(Fabric.getLogger()));
            long currentTimeMillis = checkForUpdatesController.currentTimeProvider.getCurrentTimeMillis();
            long j = checkForUpdatesController.betaSettings.updateSuspendDurationSeconds * 1000;
            Fabric.getLogger();
            long j2 = checkForUpdatesController.preferenceStore.get().getLong("last_update_check", 0L);
            Fabric.getLogger();
            long j3 = j + j2;
            Fabric.getLogger();
            new StringBuilder("Check for updates current time: ").append(currentTimeMillis).append(", next check time: ").append(j3);
            if (currentTimeMillis >= j3) {
                try {
                    Fabric.getLogger();
                    new ApiKey();
                    String value = ApiKey.getValue(checkForUpdatesController.context);
                    new CheckForUpdatesRequest(checkForUpdatesController.beta, CommonUtils.getStringsFileValue(checkForUpdatesController.beta.context, "com.crashlytics.ApiEndpoint"), checkForUpdatesController.betaSettings.updateUrl, checkForUpdatesController.httpRequestFactory, new CheckForUpdatesResponseTransform()).invoke(value, checkForUpdatesController.idManager.createIdHeaderValue(value, checkForUpdatesController.buildProps.packageName), checkForUpdatesController.buildProps);
                } finally {
                    checkForUpdatesController.preferenceStore.edit().putLong("last_update_check", currentTimeMillis).commit();
                }
            } else {
                Fabric.getLogger();
            }
        }
        return true;
    }
}
