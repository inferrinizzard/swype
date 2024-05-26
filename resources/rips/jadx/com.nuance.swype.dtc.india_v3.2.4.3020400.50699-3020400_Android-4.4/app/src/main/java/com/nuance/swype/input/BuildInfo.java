package com.nuance.swype.input;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.format.Time;
import android.util.TimeFormatException;
import com.nuance.android.compat.SharedPreferencesEditorCompat;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.common.Document;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class BuildInfo {
    public static final boolean BUILD_SAVE_AUDIOBUF_ONSDCARD = false;
    public static final String DEFAULT_SWIB = "00000000-0000-0000-0000-000000000000";
    public static final String DRAGON_SPEECH_VERSION = "2.0";
    private static final long FIRST_USE_TIME_DEFAULT_MILLISECONDS = 0;
    private static final String FIRST_USE_TIME_IN_MILLISECONDS = "buildinfo_first_use_time_in_milliseconds";
    public static final int INDIVIDUAL_LOGGING_DEFAULT = -99;
    public static final int INDIVIDUAL_LOGGING_NONE = 0;
    public static final int INDIVIDUAL_LOGGING_RND = 1;
    private static final String USAGEDATA_KEY_FLURRY_DEBUG_QA = "J49PC559NF5CP4K2JRJQ";
    private static final String USAGEDATA_KEY_FLURRY_PROD = "XDSKSFTWH34NTWT3RWG8";
    private static final LogManager.Log log = LogManager.getLog("BuildInfo");
    private final int accountNotificationInDays;
    private final String appkey;
    private final Time buildDate;
    private final BuildType buildType;
    private final String buildVersion;
    private final int connectStatus;
    private final String coreLibName;
    private final boolean downloadableThemesEnabled;
    private final long firstUseTimeInMilliseconds;
    private final String googlePlayPublicKey;
    private final HandwritingType handwritingType;
    private boolean hasExpired;
    private final boolean isChineseCoreEnabled;
    private final boolean isJapaneseCoreEnabled;
    private final boolean isKoreanCoreEnabled;
    private boolean isUdbBuild;
    private final int maxCellularConnections;
    private final int maxWifiConnections;
    private final int refreshInterval;
    private final String robWorkspace;
    private final int statisticsLevel;
    private final String swib;
    private final long trialPeriod;
    private final String twitterAPIKey;
    private final String twitterAPISecret;
    private final int usageDataProvider;
    private final int usageLevel;

    /* loaded from: classes.dex */
    public enum BuildType {
        PRODUCTION(Strings.BUILD_TYPE_PRODUCTION),
        DEVELOPMENT(Strings.BUILD_TYPE_DEV),
        DEMO(Strings.BUILD_TYPE_DEMO),
        GOOGLEPLAY("google_play"),
        GOOGLEPLAY_TRIAL("google_play_trial"),
        GOOGLEPLAY_CHINA("google_play_china"),
        AMAZONSTORE("amazon_store"),
        AMAZONSTORE_TRIAL("amazon_store_trial"),
        BEMOBI("bemobi");

        private final String name;

        BuildType(String name) {
            this.name = name;
        }

        public static BuildType parseFromConfig(String name) {
            for (BuildType b : values()) {
                if (b.name.equals(name)) {
                    return b;
                }
            }
            return PRODUCTION;
        }

        public final boolean isTrialBuild() {
            return this == DEMO || this == GOOGLEPLAY_TRIAL || this == GOOGLEPLAY_CHINA || this == AMAZONSTORE_TRIAL;
        }

        public final boolean isGoogleTrialBuild() {
            return this == GOOGLEPLAY_TRIAL;
        }

        public final boolean isLicensingOn() {
            return this == PRODUCTION || this == BEMOBI;
        }

        public final boolean isShowEulaOn() {
            return this == DEMO;
        }

        public final boolean isDevBuild() {
            return this == DEVELOPMENT;
        }

        public final boolean isDistributedOnGooglePlay() {
            return this == GOOGLEPLAY || this == GOOGLEPLAY_TRIAL;
        }

        public final boolean isDTCbuild() {
            return this == GOOGLEPLAY || this == GOOGLEPLAY_TRIAL || this == AMAZONSTORE || this == AMAZONSTORE_TRIAL || this == BEMOBI || this == GOOGLEPLAY_CHINA;
        }

        public final boolean isAmazonBuild() {
            return this == AMAZONSTORE || this == AMAZONSTORE_TRIAL;
        }

        public final boolean isGooglePlayChina() {
            return this == GOOGLEPLAY_CHINA;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.name;
        }
    }

    /* loaded from: classes.dex */
    public enum HandwritingType {
        NONE(0),
        CJK_ONLY(1),
        FULL_SUPPORT(2);

        private final int value;

        HandwritingType(int value) {
            this.value = value;
        }

        public static HandwritingType parseFromConfig(int value) {
            for (HandwritingType h : values()) {
                if (h.value == value) {
                    return h;
                }
            }
            return NONE;
        }
    }

    public static BuildInfo from(Context context) {
        return IMEApplication.from(context).getBuildInfo();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BuildInfo(Context context, SharedPreferences sp) {
        Resources res = context.getResources();
        this.buildVersion = res.getString(R.string.build_version);
        BuildType myBuildType = BuildType.parseFromConfig(res.getString(R.string.build_type));
        this.handwritingType = HandwritingType.parseFromConfig(res.getInteger(R.integer.handwriting_type));
        this.trialPeriod = res.getInteger(R.integer.trial_period);
        this.connectStatus = res.getInteger(R.integer.connect_status);
        this.statisticsLevel = res.getInteger(R.integer.analytics_level);
        this.usageLevel = res.getInteger(R.integer.rnd_level);
        this.usageDataProvider = res.getInteger(R.integer.usage_data_provider);
        this.isUdbBuild = res.getBoolean(R.bool.udb_anonymous_upload);
        this.buildDate = new Time();
        this.appkey = res.getString(R.string.appkey) + Document.ID_SEPARATOR + myBuildType.toString();
        this.googlePlayPublicKey = res.getString(R.string.play_store_app_key);
        this.twitterAPIKey = res.getString(R.string.api_key_twitter);
        this.twitterAPISecret = res.getString(R.string.api_secret_twitter);
        this.isChineseCoreEnabled = res.getBoolean(R.bool.chinese_core_enabled);
        this.isKoreanCoreEnabled = res.getBoolean(R.bool.korean_core_enabled);
        this.isJapaneseCoreEnabled = res.getBoolean(R.bool.japanese_core_enabled);
        this.refreshInterval = res.getInteger(R.integer.refresh_interval);
        this.maxWifiConnections = res.getInteger(R.integer.max_wifi_connections);
        this.maxCellularConnections = res.getInteger(R.integer.max_cellular_connections);
        this.accountNotificationInDays = res.getInteger(R.integer.account_notifications_days);
        String timestamp = res.getString(R.string.build_timestamp);
        if ("yyyymmdd.hhmmss".equals(timestamp)) {
            myBuildType = BuildType.DEVELOPMENT;
        } else {
            String rfc2445Format = timestamp.replace(".", "T");
            try {
                this.buildDate.parse(rfc2445Format);
            } catch (TimeFormatException e) {
                myBuildType = BuildType.DEVELOPMENT;
            }
        }
        this.buildType = myBuildType;
        String swib = DEFAULT_SWIB;
        BuildSettings settings = BuildSettings.getBuildSettings(context);
        if (settings != null && settings.getSWIB() != null) {
            swib = settings.getSWIB();
        }
        this.swib = swib;
        long firstUse = sp == null ? FIRST_USE_TIME_DEFAULT_MILLISECONDS : sp.getLong(FIRST_USE_TIME_IN_MILLISECONDS, FIRST_USE_TIME_DEFAULT_MILLISECONDS);
        if (firstUse == FIRST_USE_TIME_DEFAULT_MILLISECONDS) {
            Time currentTime = new Time();
            currentTime.setToNow();
            firstUse = currentTime.toMillis(false);
            if (sp != null) {
                SharedPreferencesEditorCompat.apply(sp.edit().putLong(FIRST_USE_TIME_IN_MILLISECONDS, firstUse));
            }
        }
        this.firstUseTimeInMilliseconds = firstUse;
        this.coreLibName = res.getString(R.string.corelib_name);
        this.downloadableThemesEnabled = res.getBoolean(R.bool.enable_downloadable_themes);
        this.robWorkspace = res.getString(R.string.rob_workspace);
    }

    public void updateExpirationPeriod() {
        if (isTrialBuild()) {
            Time currentTime = new Time();
            currentTime.setToNow();
            long currentTimeInSeconds = currentTime.toMillis(false) / 1000;
            long comparisonTimeInSeconds = ((this.buildType == BuildType.GOOGLEPLAY_TRIAL || this.buildType == BuildType.AMAZONSTORE_TRIAL || this.buildType == BuildType.GOOGLEPLAY_CHINA) ? this.firstUseTimeInMilliseconds : this.buildDate.toMillis(false)) / 1000;
            long trialDayInSeconds = this.trialPeriod * 24 * 60 * 60;
            this.hasExpired = currentTimeInSeconds < comparisonTimeInSeconds || currentTimeInSeconds - comparisonTimeInSeconds > trialDayInSeconds;
            log.d("currentTimeInSeconds: ", Long.valueOf(currentTimeInSeconds));
            log.d("comparisonTimeInSeconds: ", Long.valueOf(comparisonTimeInSeconds));
            log.d("currentTimeInSeconds - comparisonTimeInSeconds: ", Long.valueOf(currentTimeInSeconds - comparisonTimeInSeconds));
            log.d("trialDayInSeconds: ", Long.valueOf(trialDayInSeconds));
            return;
        }
        this.hasExpired = false;
    }

    public long getBuildDate() {
        return this.buildDate != null ? this.buildDate.toMillis(false) : FIRST_USE_TIME_DEFAULT_MILLISECONDS;
    }

    public String getBuildDateStr() {
        return this.buildDate != null ? this.buildDate.format2445() : "";
    }

    public String getBuildVersion() {
        return this.buildVersion;
    }

    public String getCoreLibName() {
        return this.coreLibName;
    }

    public BuildType getBuildType() {
        return this.buildType;
    }

    public HandwritingType getHandwritingType() {
        return this.handwritingType;
    }

    public boolean isTrialPeriodExpired() {
        return this.hasExpired;
    }

    public boolean isTrialBuild() {
        return this.buildType.isTrialBuild();
    }

    public boolean isLicensingOn() {
        return this.buildType.isLicensingOn();
    }

    public boolean isShowEulaOn() {
        return this.buildType.isShowEulaOn();
    }

    public boolean isDevBuild() {
        return this.buildType.isDevBuild();
    }

    public boolean isGooglePlayChina() {
        return this.buildType.isGooglePlayChina();
    }

    public boolean isDistributedOnGooglePlay() {
        return this.buildType.isDistributedOnGooglePlay();
    }

    public String getGooglePlayPublicKey() {
        return this.googlePlayPublicKey;
    }

    public String getTwitterAPIKey() {
        return this.twitterAPIKey;
    }

    public String getTwitterAPISecret() {
        return this.twitterAPISecret;
    }

    public boolean isAmazonBuild() {
        return this.buildType.isAmazonBuild();
    }

    public String getUsageDataFlurryKey() {
        return USAGEDATA_KEY_FLURRY_PROD;
    }

    public boolean isGoogleTrialBuild() {
        return this.buildType.isGoogleTrialBuild();
    }

    public boolean isDTCbuild() {
        return this.buildType.isDTCbuild();
    }

    public boolean isExpireDialogRequired() {
        return isTrialBuild();
    }

    public int getPaidVersionUrl() {
        return this.buildType == BuildType.AMAZONSTORE_TRIAL ? R.string.url_swype_amazon_store_paid_version : R.string.url_android_market_dtc_details;
    }

    public String getSwib() {
        return this.swib;
    }

    public int getConnectStatus() {
        return this.connectStatus;
    }

    public int getStatisticsLevel() {
        return this.statisticsLevel;
    }

    public boolean isConnectEnabled() {
        return getConnectStatus() > 0;
    }

    public boolean isUsageTrackingBuild() {
        return this.isUdbBuild || this.statisticsLevel > 0 || this.usageLevel != 0;
    }

    public boolean isUsageLogging() {
        return this.usageLevel != 0;
    }

    public String getAppKey() {
        return this.appkey;
    }

    public boolean isChineseCoreEnabled() {
        return this.isChineseCoreEnabled;
    }

    public boolean isKoreanCoreEnabled() {
        return this.isKoreanCoreEnabled;
    }

    public boolean isJapaneseCoreEnabled() {
        return this.isJapaneseCoreEnabled;
    }

    public int getRefreshInterval() {
        return this.refreshInterval;
    }

    public int getMaxConnectionsWifi() {
        return this.maxWifiConnections;
    }

    public int getMaxConnectionsCellular() {
        return this.maxCellularConnections;
    }

    public int getAccountNotificationInDays() {
        return this.accountNotificationInDays;
    }

    public boolean isDownloadableThemesEnabled() {
        return this.downloadableThemesEnabled;
    }

    public String getRobWorkspace() {
        return this.robWorkspace;
    }

    public int getUsageDataProvider() {
        return this.usageDataProvider;
    }
}
