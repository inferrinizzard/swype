package com.nuance.connect.host.service;

import android.util.SparseArray;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.UserSettings;

/* loaded from: classes.dex */
public interface BuildSettings {

    /* loaded from: classes.dex */
    public enum Property {
        OEM_ID("OEM_ID"),
        APPLICATION_ID("APPLICATION_ID"),
        CUSTOMER_STRING("CUSTOMER_STRING"),
        VERSION("VERSION"),
        ANALYTICS_LEVEL("ANALYTICS_LEVEL"),
        DLM_SYNC_ALLOWED("DLM_SYNC_ALLOWED"),
        DLM_SYNC_DEFAULT("DLM_SYNC_DEFAULT"),
        USE_CELLULAR_DATA_DEFAULT("USE_CELLULAR_DATA_DEFAULT"),
        LOCATION_LEVEL("LOCATION_LEVEL"),
        LOCATION_DISTANCE("LOCATION_DISTANCE"),
        LOCATION_DELAY("LOCATION_DELAY"),
        SHOW_STARTUP_TIP("SHOW_STARTUP_TIP"),
        SHOW_STARTUP_ACCOUNT_REGISTRATION(""),
        UDB_ANONYMOUS_UPLOAD("UDB_ANONYMOUS_UPLOAD"),
        LIVING_LANGUAGE_ENABLED("LIVING_LANGUAGE_ENABLED"),
        CORE_VERSION("CORE_VERSION"),
        LANGUAGE_CORE_MAP("LANGUAGE_CORE_MAP"),
        BUILD_TIMESTAMP("BUILD_TIMESTAMP"),
        APP_KEY_STARTS("APP_KEY_STARTS"),
        BUILD_EXPIRATION("BUILD_EXPIRATION"),
        DEFAULT_DELAY("DEFAULT_DELAY"),
        COLLECT_USER_PROPERTIES(Strings.COLLECT_USER_PROPERTIES),
        ANONYMOUS_BUILD("ANONYMOUS_BUILD"),
        LIVING_LANGUAGE_MAX_EVENTS(UserSettings.LIVING_LANGUAGE_MAX_EVENTS),
        MINIMUM_REFRESH_INTERVAL("MINIMUM_REFRESH_INTERVAL"),
        BUILD_PROPERTIES_FILTER_BLOCK("BUILD_PROPERTIES_FILTER_BLOCK"),
        BUILD_PROPERTIES_FILTER_PRE_TOS("BUILD_PROPERTIES_FILTER_PRE_TOS"),
        PLATFORM_UPDATE_ENABLED("PLATFORM_UPDATE_ENABLED"),
        CCP_SERVER_URL("CCP_SERVER_URL"),
        LBS_SERVER_URL("LBS_SERVER_URL"),
        LBS_ENABLED("LBS_ENABLED"),
        LBS_LOOKUP_INTERVAL("LBS_LOOKUP_INTERVAL"),
        REQUIRED_LEGAL_DOCUMENTS("REQUIRED_LEGAL_DOCUMENTS"),
        CUSTOM_DATAPOINT_SIZES("CUSTOM_DATAPOINT_SIZES"),
        LEGACY_SECRET_KEY("LEGACY_SECRET_KEY");

        private String key;

        Property(String str) {
            this.key = str;
        }

        public final String getKey() {
            return this.key;
        }
    }

    boolean collectUserProperties();

    int getAnalyticsLevel();

    String getAppKeyStarts();

    String getApplicationId();

    String getBackgroundNetworkDefault();

    String getBuildExpiration();

    String getBuildPropertiesFilterBlock();

    String getBuildPropertiesFilterPreTos();

    String getBuildTimestamp();

    String getCCPServerUrl();

    String getConnectUrl();

    SparseArray<String> getCoreVersions();

    String getCustomDatapointSizes();

    int getDefaultDelay();

    boolean getDlmSyncDefault();

    String getForegroundNetworkDefault();

    String getLegacySecretKey();

    int getLivingLanguageMaxEventsDefault();

    int getLocationDelay();

    int getLocationDistance();

    int getLocationLevel();

    int getLocationServiceLookupInterval();

    String getLocationServiceServerUrl();

    int getLogLevel();

    int getMinimumRefreshIntervalDefault();

    String getOemId();

    String getRequiredLegalDocuments();

    boolean getUdbAnonymousUpload();

    boolean getUpdateLivingLanguageByDefault();

    String getVersion();

    boolean isAnonymousBuild();

    boolean isChineseCategoryEnabled();

    boolean isDeveloperLogEnabled();

    boolean isDlmSyncEnabled();

    boolean isLivingLanguageEnabled();

    boolean isLocationServiceEnabled();

    boolean isPlatformUpdateEnabled();
}
