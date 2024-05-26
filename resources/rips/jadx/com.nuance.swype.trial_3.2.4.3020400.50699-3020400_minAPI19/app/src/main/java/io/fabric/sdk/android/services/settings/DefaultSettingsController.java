package io.fabric.sdk.android.services.settings;

import android.content.SharedPreferences;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.CurrentTimeProvider;
import io.fabric.sdk.android.services.persistence.PreferenceStore;
import io.fabric.sdk.android.services.persistence.PreferenceStoreImpl;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class DefaultSettingsController implements SettingsController {
    private final CachedSettingsIo cachedSettingsIo;
    private final CurrentTimeProvider currentTimeProvider;
    private final Kit kit;
    private final PreferenceStore preferenceStore;
    private final SettingsJsonTransform settingsJsonTransform;
    private final SettingsRequest settingsRequest;
    private final SettingsSpiCall settingsSpiCall;

    public DefaultSettingsController(Kit kit, SettingsRequest settingsRequest, CurrentTimeProvider currentTimeProvider, SettingsJsonTransform settingsJsonTransform, CachedSettingsIo cachedSettingsIo, SettingsSpiCall settingsSpiCall) {
        this.kit = kit;
        this.settingsRequest = settingsRequest;
        this.currentTimeProvider = currentTimeProvider;
        this.settingsJsonTransform = settingsJsonTransform;
        this.cachedSettingsIo = cachedSettingsIo;
        this.settingsSpiCall = settingsSpiCall;
        this.preferenceStore = new PreferenceStoreImpl(this.kit);
    }

    @Override // io.fabric.sdk.android.services.settings.SettingsController
    public final SettingsData loadSettingsData() {
        return loadSettingsData(SettingsCacheBehavior.USE_CACHE);
    }

    @Override // io.fabric.sdk.android.services.settings.SettingsController
    public final SettingsData loadSettingsData(SettingsCacheBehavior cacheBehavior) {
        JSONObject settingsJson;
        SettingsData toReturn = null;
        try {
            if (!Fabric.isDebuggable()) {
                if (!(!this.preferenceStore.get().getString("existing_instance_identifier", "").equals(getBuildInstanceIdentifierFromContext()))) {
                    toReturn = getCachedSettingsData(cacheBehavior);
                }
            }
            if (toReturn == null && (settingsJson = this.settingsSpiCall.invoke(this.settingsRequest)) != null) {
                toReturn = this.settingsJsonTransform.buildFromJson(this.currentTimeProvider, settingsJson);
                this.cachedSettingsIo.writeCachedSettings(toReturn.expiresAtMillis, settingsJson);
                logSettings(settingsJson, "Loaded settings: ");
                String buildInstanceIdentifierFromContext = getBuildInstanceIdentifierFromContext();
                SharedPreferences.Editor edit = this.preferenceStore.edit();
                edit.putString("existing_instance_identifier", buildInstanceIdentifierFromContext);
                this.preferenceStore.save(edit);
            }
            if (toReturn == null) {
                return getCachedSettingsData(SettingsCacheBehavior.IGNORE_CACHE_EXPIRATION);
            }
            return toReturn;
        } catch (Exception e) {
            Fabric.getLogger().e("Fabric", "Unknown error while loading Crashlytics settings. Crashes will be cached until settings can be retrieved.", e);
            return null;
        }
    }

    private SettingsData getCachedSettingsData(SettingsCacheBehavior cacheBehavior) {
        SettingsData toReturn = null;
        try {
            if (!SettingsCacheBehavior.SKIP_CACHE_LOOKUP.equals(cacheBehavior)) {
                JSONObject settingsJson = this.cachedSettingsIo.readCachedSettings();
                if (settingsJson != null) {
                    SettingsData settingsData = this.settingsJsonTransform.buildFromJson(this.currentTimeProvider, settingsJson);
                    logSettings(settingsJson, "Loaded cached settings: ");
                    long currentTimeMillis = this.currentTimeProvider.getCurrentTimeMillis();
                    if (!SettingsCacheBehavior.IGNORE_CACHE_EXPIRATION.equals(cacheBehavior)) {
                        if (settingsData.expiresAtMillis < currentTimeMillis) {
                            Fabric.getLogger();
                        }
                    }
                    toReturn = settingsData;
                    Fabric.getLogger();
                } else {
                    Fabric.getLogger();
                }
            }
        } catch (Exception e) {
            Fabric.getLogger().e("Fabric", "Failed to get cached settings", e);
        }
        return toReturn;
    }

    private void logSettings(JSONObject json, String message) throws JSONException {
        if (!CommonUtils.isClsTrace(this.kit.context)) {
            json = this.settingsJsonTransform.sanitizeTraceInfo(json);
        }
        Fabric.getLogger();
        new StringBuilder().append(message).append(json.toString());
    }

    private String getBuildInstanceIdentifierFromContext() {
        return CommonUtils.createInstanceIdFrom(CommonUtils.resolveBuildId(this.kit.context));
    }
}
