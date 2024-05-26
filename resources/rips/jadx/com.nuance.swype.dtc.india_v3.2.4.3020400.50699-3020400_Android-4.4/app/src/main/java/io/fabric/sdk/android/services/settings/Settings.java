package io.fabric.sdk.android.services.settings;

import android.content.Context;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.CurrentTimeProvider;
import io.fabric.sdk.android.services.common.DeliveryMechanism;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.common.SystemCurrentTimeProvider;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public final class Settings {
    private boolean initialized;
    private SettingsController settingsController;
    private final AtomicReference<SettingsData> settingsData;
    private final CountDownLatch settingsDataLatch;

    /* loaded from: classes.dex */
    public interface SettingsAccess<T> {
        T usingSettings(SettingsData settingsData);
    }

    /* synthetic */ Settings(byte b) {
        this();
    }

    /* loaded from: classes.dex */
    public static class LazyHolder {
        private static final Settings INSTANCE = new Settings((byte) 0);

        public static /* synthetic */ Settings access$100() {
            return INSTANCE;
        }
    }

    private Settings() {
        this.settingsData = new AtomicReference<>();
        this.settingsDataLatch = new CountDownLatch(1);
        this.initialized = false;
    }

    public final synchronized Settings initialize(Kit kit, IdManager idManager, HttpRequestFactory httpRequestFactory, String versionCode, String versionName, String urlEndpoint) {
        Settings settings;
        if (this.initialized) {
            settings = this;
        } else {
            if (this.settingsController == null) {
                Context context = kit.context;
                String appIdentifier = idManager.appIdentifier;
                new ApiKey();
                String apiKey = ApiKey.getValue(context);
                String installerPackageName = idManager.getInstallerPackageName();
                CurrentTimeProvider currentTimeProvider = new SystemCurrentTimeProvider();
                SettingsJsonTransform settingsJsonTransform = new DefaultSettingsJsonTransform();
                CachedSettingsIo cachedSettingsIo = new DefaultCachedSettingsIo(kit);
                String iconHash = CommonUtils.getAppIconHashOrNull(context);
                String settingsUrl = String.format(Locale.US, "https://settings.crashlytics.com/spi/v2/platforms/android/apps/%s/settings", appIdentifier);
                SettingsSpiCall settingsSpiCall = new DefaultSettingsSpiCall(kit, urlEndpoint, settingsUrl, httpRequestFactory);
                String deviceId = idManager.createIdHeaderValue(apiKey, appIdentifier);
                String instanceId = CommonUtils.createInstanceIdFrom(CommonUtils.resolveBuildId(context));
                int deliveryMechanismId = DeliveryMechanism.determineFrom(installerPackageName).id;
                SettingsRequest settingsRequest = new SettingsRequest(apiKey, deviceId, instanceId, versionName, versionCode, deliveryMechanismId, iconHash);
                this.settingsController = new DefaultSettingsController(kit, settingsRequest, currentTimeProvider, settingsJsonTransform, cachedSettingsIo, settingsSpiCall);
            }
            this.initialized = true;
            settings = this;
        }
        return settings;
    }

    public final <T> T withSettings(SettingsAccess<T> access, T defaultValue) {
        SettingsData settingsData = this.settingsData.get();
        if (settingsData == null) {
            return defaultValue;
        }
        T defaultValue2 = access.usingSettings(settingsData);
        return defaultValue2;
    }

    public final SettingsData awaitSettingsData() {
        try {
            this.settingsDataLatch.await();
            return this.settingsData.get();
        } catch (InterruptedException e) {
            Fabric.getLogger().e("Fabric", "Interrupted while waiting for settings data.");
            return null;
        }
    }

    public final synchronized boolean loadSettingsData() {
        SettingsData settingsData;
        settingsData = this.settingsController.loadSettingsData();
        setSettingsData(settingsData);
        return settingsData != null;
    }

    public final synchronized boolean loadSettingsSkippingCache() {
        SettingsData settingsData;
        settingsData = this.settingsController.loadSettingsData(SettingsCacheBehavior.SKIP_CACHE_LOOKUP);
        setSettingsData(settingsData);
        if (settingsData == null) {
            Fabric.getLogger().e("Fabric", "Failed to force reload of settings from Crashlytics.", null);
        }
        return settingsData != null;
    }

    private void setSettingsData(SettingsData settingsData) {
        this.settingsData.set(settingsData);
        this.settingsDataLatch.countDown();
    }
}
