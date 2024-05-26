package com.nuance.swypeconnect.ac;

import android.content.Context;
import android.text.format.Time;
import android.util.SparseArray;
import android.util.TimeFormatException;
import com.nuance.connect.api.AccountService;
import com.nuance.connect.api.AddonDictionariesService;
import com.nuance.connect.api.CatalogService;
import com.nuance.connect.api.ChinesePredictionService;
import com.nuance.connect.api.ConfigService;
import com.nuance.connect.api.ConnectServiceManager;
import com.nuance.connect.api.ConnectionCallback;
import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.api.LanguageService;
import com.nuance.connect.api.PlatformUpdateService;
import com.nuance.connect.api.ReportingService;
import com.nuance.connect.api.SyncService;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.Strings;
import com.nuance.connect.host.service.BuildSettings;
import com.nuance.connect.host.service.HostInterface;
import com.nuance.connect.internal.ConnectServiceManagerInternal;
import com.nuance.connect.internal.Property;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.system.NetworkState;
import com.nuance.connect.util.EncryptUtils;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.PermissionUtils;
import com.nuance.connect.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.json.JSONArray;

/* loaded from: classes.dex */
public final class ACManager {
    public static final int BUILD_STATUS_LICENSED = 15;
    public static final int BUILD_STATUS_UNLICENSED = 14;
    public static final String CATALOG_SERVICE = "CATALOG_SERVICE";
    public static final int CONNECTION_STATUS_CLOUD_CONFIG_UPDATED = 22;
    public static final int CONNECTION_STATUS_DELAYED = 3;
    public static final int CONNECTION_STATUS_INITIALIZATION_COMPLETE = 13;
    public static final int CONNECTION_STATUS_OK = 1;
    public static final int CONNECTION_STATUS_REFRESH_COMPLETED = 10;
    public static final int CONNECTION_STATUS_REFRESH_DELAYED = 12;
    public static final int CONNECTION_STATUS_REFRESH_FAILED = 16;
    public static final int CONNECTION_STATUS_REFRESH_PENDING = 11;
    public static final int CONNECTION_STATUS_STALLED = 2;
    public static final int CONNECTION_STATUS_STALLED_DOWNLOAD = 8;
    public static final int CONNECTION_STATUS_STALLED_HTTP_ERROR = 7;
    public static final int CONNECTION_STATUS_STALLED_JSON_PARSE = 5;
    public static final int CONNECTION_STATUS_STALLED_PROTOCOL_EXCEPTION = 6;
    public static final int CONNECTION_STATUS_STALLED_SSL = 4;
    public static final int CONNECTION_STATUS_STALLED_UNKNOWN_RESPONSE = 9;
    public static final int CONNECTION_STATUS_TOS_DOCUMENT_UPDATED = 18;
    public static final int CONNECTION_STATUS_UPGRADE_DETECTED = 17;
    public static final int CONNECTION_STATUS_USAGE_DOCUMENT_UPDATED = 19;
    public static final int CONNECTION_TYPE_BACKGROUND = 2;
    public static final int CONNECTION_TYPE_FOREGROUND = 1;
    public static final int CONNECT_TYPE_ALL = 3;
    public static final int CORE_ALPHA = 1;
    public static final int CORE_CHINESE = 3;
    public static final int CORE_JAPANESE = 4;
    public static final int CORE_KOREAN = 2;
    public static final int DEVICE_LOCATION_CHANGE = 21;
    public static final String DEVICE_SERVICE = "DEVICE";
    private String applicationId;
    private ACBuildConfig buildConfig;
    private ACBuildInfo buildInfo;
    private ConnectBuildSettings buildSettings;
    private ConfigService configService;
    private ACConfiguration configuration;
    private ConnectServiceManager connect;
    private final Map<ACConnectionCallback, ConnectionCallback> connectionCallbacks;
    protected Context context;
    private String coreVersionAlpha;
    private String coreVersionChinese;
    private String coreVersionJapanese;
    private String coreVersionKorean;
    private ACDLMConnector dlmConnector;
    private boolean isInitialized;
    private volatile boolean isShutdown;
    private volatile boolean isStarted;
    private ACLanguage language;
    private ACLegalDocuments legaldocs;
    private final Property.StringValueListener locationChangedListener;
    private final Logger.Log log;
    private final String[] selectedServices;
    private final ConcurrentHashMap<String, ACService> services;
    protected ArrayList<String> servicesToInitialize;
    private final SimpleLock startLock;
    private PersistentDataStore store;
    public static final String LANGUAGE_SERVICE = "LANGUAGE";

    @Deprecated
    public static final String LANGUAGE_COMPAT_SERVICE = "LANGUAGE_COMPAT";
    public static final String LIVING_LANGUAGE_SERVICE = "LIVING_LANGUAGE";
    public static final String CHINESE_DICTIONARY_SERVICE = "CHINESE_DICTIONARY_SERVICE";
    public static final String ACCOUNT_SERVICE = "ACCOUNT";
    public static final String SCANNER_SERVICE = "SCANNER_SERVICE";
    public static final String DLM_SYNC_SERVICE = "DLM_SYNC_SERVICE";
    public static final String REPORTING_SERVICE = "REPORTING_SERVICE";
    public static final String PACKAGE_UPDATER_SERVICE = "PACKAGE_UPDATER_SERVICE";
    public static final String CHINESE_CLOUD_PREDICTION = "CHINESE_CLOUD_PREDICTION";
    private static final String[] DEFAULT_SERVICES = {LANGUAGE_SERVICE, LANGUAGE_COMPAT_SERVICE, LIVING_LANGUAGE_SERVICE, CHINESE_DICTIONARY_SERVICE, ACCOUNT_SERVICE, SCANNER_SERVICE, DLM_SYNC_SERVICE, REPORTING_SERVICE, PACKAGE_UPDATER_SERVICE, CHINESE_CLOUD_PREDICTION};

    /* loaded from: classes.dex */
    static class ACConfig {
        public boolean livingLanguageEnabled = true;
        public boolean chineseCategoryEnabled = true;

        ACConfig() {
        }
    }

    /* loaded from: classes.dex */
    public interface ACConnectionCallback {
        void connected(int i, int i2);

        void connectionStatus(int i, String str);

        void disconnected(int i);
    }

    /* loaded from: classes.dex */
    private static class ACHostInterface implements HostInterface {
        private BuildSettings buildSettings;

        ACHostInterface(BuildSettings buildSettings) {
            this.buildSettings = buildSettings;
        }

        @Override // com.nuance.connect.host.service.HostInterface
        public Object getHostService(HostInterface.HostService hostService) {
            if (HostInterface.HostService.HOST_BUILD_SETTINGS.equals(hostService)) {
                return this.buildSettings;
            }
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class ACInitializeInfo {
        private final String applicationId;
        private final Context context;

        public ACInitializeInfo(Context context, String str) {
            this.context = context;
            this.applicationId = str;
        }

        public String legacySecretKey() {
            return null;
        }

        public String[] services() {
            return ACManager.DEFAULT_SERVICES;
        }
    }

    /* loaded from: classes.dex */
    private static class ACInitializeInfoImpl extends ACInitializeInfo {
        final String[] enabledServices;

        public ACInitializeInfoImpl(Context context, String str, String[] strArr) {
            super(context, str);
            if (strArr != null) {
                this.enabledServices = (String[]) Arrays.copyOf(strArr, strArr.length);
            } else {
                this.enabledServices = new String[0];
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACManager.ACInitializeInfo
        public String[] services() {
            return this.enabledServices;
        }
    }

    /* loaded from: classes.dex */
    public static class ACLocation {
        private final String country;

        private ACLocation(String str) {
            this.country = str;
        }

        public String getISOCountry() {
            return this.country;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ConnectBuildSettings implements BuildSettings {
        private final ACConfig acConfig;
        private final String applicationId;
        private final ACBuildConfig buildConfig;
        private boolean locationServiceEnabled;
        private final SparseArray<String> properties = new SparseArray<>();
        private final SparseArray<String> coreVersions = new SparseArray<>();

        public ConnectBuildSettings(ACConfig aCConfig, ACBuildConfig aCBuildConfig, String str) {
            this.buildConfig = aCBuildConfig;
            this.acConfig = aCConfig;
            this.applicationId = str;
            this.locationServiceEnabled = aCBuildConfig.locationServiceEnabled;
            this.properties.put(BuildSettings.Property.OEM_ID.ordinal(), aCBuildConfig.oemId);
            this.properties.put(BuildSettings.Property.ANALYTICS_LEVEL.ordinal(), "0");
            this.properties.put(BuildSettings.Property.LOCATION_LEVEL.ordinal(), aCBuildConfig.locationLevel);
            this.properties.put(BuildSettings.Property.LOCATION_DISTANCE.ordinal(), String.valueOf(aCBuildConfig.locationDistance));
            this.properties.put(BuildSettings.Property.LOCATION_DELAY.ordinal(), String.valueOf(aCBuildConfig.locationDelay));
            this.properties.put(BuildSettings.Property.VERSION.ordinal(), aCBuildConfig.version);
            this.properties.put(BuildSettings.Property.APPLICATION_ID.ordinal(), str);
            this.properties.put(BuildSettings.Property.BUILD_TIMESTAMP.ordinal(), aCBuildConfig.buildTimestamp);
            this.properties.put(BuildSettings.Property.APP_KEY_STARTS.ordinal(), aCBuildConfig.appKeyStarts);
            this.properties.put(BuildSettings.Property.BUILD_EXPIRATION.ordinal(), aCBuildConfig.buildExpiration);
            this.properties.put(BuildSettings.Property.DEFAULT_DELAY.ordinal(), String.valueOf(aCBuildConfig.defaultDelay));
            this.properties.put(BuildSettings.Property.COLLECT_USER_PROPERTIES.ordinal(), String.valueOf(aCBuildConfig.collectUserProperties));
            this.properties.put(BuildSettings.Property.LIVING_LANGUAGE_MAX_EVENTS.ordinal(), String.valueOf(aCBuildConfig.livingLanguageMaxEvents));
            this.properties.put(BuildSettings.Property.MINIMUM_REFRESH_INTERVAL.ordinal(), String.valueOf(aCBuildConfig.minimumRefreshInterval));
            this.properties.put(BuildSettings.Property.ANONYMOUS_BUILD.ordinal(), String.valueOf(aCBuildConfig.anonymousBuild));
            this.properties.put(BuildSettings.Property.BUILD_PROPERTIES_FILTER_PRE_TOS.ordinal(), aCBuildConfig.buildPropertiesFilterPreTos);
            this.properties.put(BuildSettings.Property.BUILD_PROPERTIES_FILTER_BLOCK.ordinal(), aCBuildConfig.buildPropertiesFilterBlock);
            this.properties.put(BuildSettings.Property.PLATFORM_UPDATE_ENABLED.ordinal(), String.valueOf(aCBuildConfig.packageUpdateEnabled));
            this.properties.put(BuildSettings.Property.LIVING_LANGUAGE_ENABLED.ordinal(), String.valueOf(aCConfig.livingLanguageEnabled));
            this.properties.put(BuildSettings.Property.CCP_SERVER_URL.ordinal(), aCBuildConfig.ccpServerUrl);
            this.properties.put(BuildSettings.Property.LBS_SERVER_URL.ordinal(), aCBuildConfig.locationServiceServerUrl);
            this.properties.put(BuildSettings.Property.LBS_ENABLED.ordinal(), String.valueOf(aCBuildConfig.locationServiceEnabled));
            this.properties.put(BuildSettings.Property.LBS_LOOKUP_INTERVAL.ordinal(), String.valueOf(aCBuildConfig.locationServiceLookupInterval));
            this.properties.put(BuildSettings.Property.REQUIRED_LEGAL_DOCUMENTS.ordinal(), aCBuildConfig.requiredLegalDocuments);
            this.properties.put(BuildSettings.Property.CUSTOM_DATAPOINT_SIZES.ordinal(), aCBuildConfig.customDatapointSizes);
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public boolean collectUserProperties() {
            return this.buildConfig.collectUserProperties;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public int getAnalyticsLevel() {
            return Integer.decode(this.properties.get(BuildSettings.Property.ANALYTICS_LEVEL.ordinal())).intValue();
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getAppKeyStarts() {
            return this.properties.get(BuildSettings.Property.APP_KEY_STARTS.ordinal());
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getApplicationId() {
            return this.applicationId;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getBackgroundNetworkDefault() {
            return new NetworkState.NetworkConfiguration(true, false, false).toString();
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getBuildExpiration() {
            return this.properties.get(BuildSettings.Property.BUILD_EXPIRATION.ordinal());
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getBuildPropertiesFilterBlock() {
            return this.buildConfig.buildPropertiesFilterBlock;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getBuildPropertiesFilterPreTos() {
            return this.buildConfig.buildPropertiesFilterPreTos;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getBuildTimestamp() {
            return this.properties.get(BuildSettings.Property.BUILD_TIMESTAMP.ordinal());
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getCCPServerUrl() {
            return this.buildConfig.ccpServerUrl;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getConnectUrl() {
            return this.buildConfig.url;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public SparseArray<String> getCoreVersions() {
            return this.coreVersions;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getCustomDatapointSizes() {
            return this.buildConfig.customDatapointSizes;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public int getDefaultDelay() {
            return this.buildConfig.defaultDelay;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public boolean getDlmSyncDefault() {
            return false;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getForegroundNetworkDefault() {
            return new NetworkState.NetworkConfiguration(true, false, false).toString();
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getLegacySecretKey() {
            return this.properties.get(BuildSettings.Property.LEGACY_SECRET_KEY.ordinal());
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public int getLivingLanguageMaxEventsDefault() {
            return this.buildConfig.livingLanguageMaxEvents;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public int getLocationDelay() {
            return this.buildConfig.locationDelay;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public int getLocationDistance() {
            return this.buildConfig.locationDistance;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public int getLocationLevel() {
            return Integer.decode(this.properties.get(BuildSettings.Property.LOCATION_LEVEL.ordinal())).intValue();
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public int getLocationServiceLookupInterval() {
            return this.buildConfig.locationServiceLookupInterval;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getLocationServiceServerUrl() {
            return this.buildConfig.locationServiceServerUrl;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public int getLogLevel() {
            try {
                return Integer.decode(this.buildConfig.defaultLogLevel).intValue();
            } catch (NumberFormatException e) {
                return 6;
            }
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public int getMinimumRefreshIntervalDefault() {
            return this.buildConfig.minimumRefreshInterval;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getOemId() {
            return this.properties.get(BuildSettings.Property.OEM_ID.ordinal());
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getRequiredLegalDocuments() {
            return this.buildConfig.requiredLegalDocuments;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public boolean getUdbAnonymousUpload() {
            return false;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public boolean getUpdateLivingLanguageByDefault() {
            return false;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public String getVersion() {
            return this.properties.get(BuildSettings.Property.VERSION.ordinal());
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public boolean isAnonymousBuild() {
            return this.buildConfig.anonymousBuild;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public boolean isChineseCategoryEnabled() {
            return this.acConfig.chineseCategoryEnabled;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public boolean isDeveloperLogEnabled() {
            return Boolean.valueOf(this.buildConfig.developerLogEnabled).booleanValue();
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public boolean isDlmSyncEnabled() {
            return false;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public boolean isLivingLanguageEnabled() {
            return this.acConfig.livingLanguageEnabled;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public boolean isLocationServiceEnabled() {
            return this.locationServiceEnabled;
        }

        @Override // com.nuance.connect.host.service.BuildSettings
        public boolean isPlatformUpdateEnabled() {
            return this.buildConfig.packageUpdateEnabled;
        }

        void setCoreVersions(String str, String str2, String str3, String str4) {
            this.coreVersions.put(1, str);
            this.coreVersions.put(3, str2);
            this.coreVersions.put(4, str3);
            this.coreVersions.put(2, str4);
        }

        public void setLegacySecretKey(String str) {
            this.properties.put(BuildSettings.Property.LEGACY_SECRET_KEY.ordinal(), str);
        }

        public void setLocationServiceEnabled(boolean z) {
            this.locationServiceEnabled = this.buildConfig.locationServiceEnabled && z;
            this.properties.put(BuildSettings.Property.LBS_ENABLED.ordinal(), String.valueOf(this.locationServiceEnabled));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SimpleLock {
        private volatile boolean locked;

        public synchronized void lock() {
            while (this.locked) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
            this.locked = true;
        }

        public synchronized void unlock() {
            this.locked = false;
            notifyAll();
        }
    }

    @Deprecated
    public ACManager(Context context, String str) {
        this(context, str, DEFAULT_SERVICES);
    }

    @Deprecated
    public ACManager(Context context, String str, String[] strArr) {
        this(new ACInitializeInfoImpl(context, str, strArr));
    }

    public ACManager(ACInitializeInfo aCInitializeInfo) {
        this.log = Logger.getLog(Logger.LoggerType.OEM);
        this.services = new ConcurrentHashMap<>();
        this.startLock = new SimpleLock();
        this.connectionCallbacks = new HashMap();
        this.locationChangedListener = new Property.StringValueListener() { // from class: com.nuance.swypeconnect.ac.ACManager.1
            @Override // com.nuance.connect.internal.Property.ValueListener
            public void onValueChanged(Property<String> property) {
                Set keySet;
                ACManager.this.log.d("locationChangedListener.locationChangedListener() name=", property.getKey(), " value=", property.getValue());
                if (ACManager.this.isConnectStarted() && property.getKey().equals("LOCATION_COUNTRY")) {
                    synchronized (ACManager.this.connectionCallbacks) {
                        keySet = ACManager.this.connectionCallbacks.keySet();
                    }
                    Iterator it = keySet.iterator();
                    while (it.hasNext()) {
                        ((ACConnectionCallback) it.next()).connectionStatus(21, "Device location has updated");
                    }
                }
            }
        };
        this.servicesToInitialize = null;
        this.context = aCInitializeInfo.context;
        EncryptUtils.defaultSecretKey(this.context);
        this.applicationId = aCInitializeInfo.applicationId;
        this.buildConfig = new ACBuildConfig();
        this.buildSettings = new ConnectBuildSettings(new ACConfig(), this.buildConfig, this.applicationId);
        if (aCInitializeInfo.services() != null) {
            this.selectedServices = (String[]) aCInitializeInfo.services().clone();
        } else {
            this.selectedServices = new String[0];
        }
        this.buildSettings.setLegacySecretKey(aCInitializeInfo.legacySecretKey());
        this.connect = ConnectServiceManagerInternal.make(this.context, new ACHostInterface(this.buildSettings));
        this.store = this.connect.getDataStore();
        this.connect.getUserSettings().registerUserSettingsListener("LOCATION_COUNTRY", this.locationChangedListener);
        Logger.getLog(Logger.LoggerType.CUSTOMER).i("SC-SDK Version: " + this.buildSettings.getVersion() + " (" + this.buildSettings.getBuildTimestamp() + ")");
        this.configService = (ConfigService) this.connect.getFeatureService(ConnectFeature.CONFIG);
        ACDeviceService aCDeviceService = new ACDeviceService(this, this.configService);
        this.services.put(aCDeviceService.getName(), aCDeviceService);
        this.dlmConnector = new ACDLMConnector((DLMConnector) this.connect.getFeatureService(ConnectFeature.DLM), this.store, this);
        this.configuration = new ACConfiguration(this, this.store);
        this.legaldocs = ACLegalDocumentsFactory.getACLegalDocuments(this.buildConfig.legalDocumentsClass, getConnect());
    }

    private String[] checkRequestedServices(String[] strArr) {
        ArrayList arrayList = new ArrayList();
        if (strArr == null) {
            strArr = new String[0];
        }
        for (String str : strArr) {
            if (isRequestedServiceAllowed(str)) {
                arrayList.add(str);
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    private void configureACSDK(String[] strArr) {
        HashSet hashSet = new HashSet(Arrays.asList(checkRequestedServices(strArr)));
        if (hashSet.contains(LANGUAGE_SERVICE) || hashSet.contains(LANGUAGE_COMPAT_SERVICE)) {
            this.log.v("Loading service: ", LANGUAGE_SERVICE);
            Object aCLanguageDownloadService = ACLanguageDownloadServiceFactory.getACLanguageDownloadService(this.buildConfig.languageDownloadServiceClass, (LanguageService) this.connect.getFeatureService(ConnectFeature.LANGUAGE), this.store, this);
            this.services.put(((ACService) aCLanguageDownloadService).getName(), (ACService) aCLanguageDownloadService);
            if (hashSet.contains(LANGUAGE_COMPAT_SERVICE)) {
                this.log.v("Loading service: ", LANGUAGE_COMPAT_SERVICE);
                ACLanguageDownloadCompatService aCLanguageDownloadCompatService = new ACLanguageDownloadCompatService(this, (ACLanguageDownloadServiceBase) aCLanguageDownloadService);
                this.services.put(aCLanguageDownloadCompatService.getName(), aCLanguageDownloadCompatService);
            }
        }
        if (hashSet.contains(CHINESE_DICTIONARY_SERVICE)) {
            this.log.v("Loading service: ", CHINESE_DICTIONARY_SERVICE);
            ACChineseDictionaryDownloadService aCChineseDictionaryDownloadService = new ACChineseDictionaryDownloadService((AddonDictionariesService) this.connect.getFeatureService(ConnectFeature.ADDON_DICTIONARIES), this.store, this);
            this.services.put(aCChineseDictionaryDownloadService.getName(), aCChineseDictionaryDownloadService);
        }
        if (hashSet.contains(LIVING_LANGUAGE_SERVICE)) {
            this.log.v("Loading service: ", LIVING_LANGUAGE_SERVICE);
            ACLivingLanguageService aCLivingLanguageService = new ACLivingLanguageService(this.connect, this.store);
            this.services.put(aCLivingLanguageService.getName(), aCLivingLanguageService);
        }
        if (hashSet.contains(ACCOUNT_SERVICE)) {
            this.log.v("Loading service: ", ACCOUNT_SERVICE);
            ACAccountService aCAccountService = new ACAccountService((AccountService) this.connect.getFeatureService(ConnectFeature.ACCOUNT));
            this.services.put(aCAccountService.getName(), aCAccountService);
        }
        if (hashSet.contains(SCANNER_SERVICE)) {
            this.log.v("Loading service: ", SCANNER_SERVICE);
            ACScannerService aCScannerService = new ACScannerService(this.context, (DLMConnector) this.connect.getFeatureService(ConnectFeature.DLM), this.store, this);
            this.services.put(aCScannerService.getName(), aCScannerService);
        }
        if (hashSet.contains(DLM_SYNC_SERVICE)) {
            this.log.v("Loading service: ", DLM_SYNC_SERVICE);
            ACDLMSyncService aCDLMSyncService = new ACDLMSyncService(this, (SyncService) this.connect.getFeatureService(ConnectFeature.SYNC), (AccountService) this.connect.getFeatureService(ConnectFeature.ACCOUNT));
            this.services.put(aCDLMSyncService.getName(), aCDLMSyncService);
        }
        if (hashSet.contains(REPORTING_SERVICE)) {
            this.log.v("Loading service: ", REPORTING_SERVICE);
            ACReportingService aCReportingService = new ACReportingService(this, (ReportingService) this.connect.getFeatureService(ConnectFeature.REPORTING), this.buildConfig.reportingLogHelperClass, this.store);
            this.services.put(aCReportingService.getName(), aCReportingService);
        }
        if (hashSet.contains(PACKAGE_UPDATER_SERVICE)) {
            this.log.v("Loading service: ", PACKAGE_UPDATER_SERVICE);
            ACPlatformUpdateService aCPlatformUpdateService = new ACPlatformUpdateService((PlatformUpdateService) this.connect.getFeatureService(ConnectFeature.UPDATE));
            this.services.put(aCPlatformUpdateService.getName(), aCPlatformUpdateService);
        }
        if (hashSet.contains(CHINESE_CLOUD_PREDICTION)) {
            this.log.v("Loading service: ", CHINESE_CLOUD_PREDICTION);
            Object aCChinesePredictionService = ACChinesePredictionServiceFactory.getACChinesePredictionService(this.buildConfig.ccpServiceClass, (ChinesePredictionService) this.connect.getFeatureService(ConnectFeature.CHINESE_PREDICTION), this.store, this);
            this.services.put(((ACService) aCChinesePredictionService).getName(), (ACService) aCChinesePredictionService);
        }
        if (hashSet.contains(CATALOG_SERVICE)) {
            this.log.v("Loading service: ", CATALOG_SERVICE);
            Object aCCatalogService = ACCatalogServiceFactory.getACCatalogService(this.buildConfig.catalogServiceClass, (CatalogService) this.connect.getFeatureService(ConnectFeature.CATALOG), this.store, this);
            this.services.put(((ACService) aCCatalogService).getName(), (ACService) aCCatalogService);
        }
    }

    private String getConnectClass() throws IllegalStateException {
        try {
            Class.forName(Strings.CONNECT_CLIENT_CLASS_NAME);
            return Strings.CONNECT_CLIENT_CLASS_NAME;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean isConnectClientClassExist() {
        try {
            getConnectClass();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private boolean isRequestedServiceAllowed(String str) {
        if (PACKAGE_UPDATER_SERVICE.equals(str) && !this.buildConfig.packageUpdateEnabled) {
            this.log.d("PACKAGE_UPDATER_SERVICE is not enabled in this build.");
            return false;
        }
        if (!REPORTING_SERVICE.equals(str) || this.buildConfig.reportingServiceEnabled) {
            return true;
        }
        this.log.d("REPORTING_SERVICE is not enabled in this build.");
        return false;
    }

    private void setupInitialization() {
        synchronized (this) {
            this.servicesToInitialize = new ArrayList<>();
        }
        for (Map.Entry<String, ACService> entry : this.services.entrySet()) {
            if (entry.getValue().requireInitialization()) {
                synchronized (this) {
                    this.servicesToInitialize.add(entry.getValue().getName());
                }
            }
        }
        if (this.dlmConnector.requireInitialization()) {
            synchronized (this) {
                this.servicesToInitialize.add(this.dlmConnector.getName());
            }
        }
    }

    private boolean validApplicationId(String str) {
        if (str == null) {
            return false;
        }
        return this.buildSettings.getAppKeyStarts().length() == 0 || str.startsWith(this.buildSettings.getAppKeyStarts());
    }

    protected final String convertJSONTasksToServiceTasks(String str) {
        ArrayList arrayList = new ArrayList();
        try {
            JSONArray jSONArray = new JSONArray(str);
            for (int i = 0; i < jSONArray.length(); i++) {
                String string = jSONArray.getString(i);
                if (Strings.TASK_CDB_AVAILABLE.equals(string)) {
                    arrayList.add(CHINESE_DICTIONARY_SERVICE);
                    arrayList.add(LIVING_LANGUAGE_SERVICE);
                } else if (Strings.TASK_CDB_LIST_UPDATE.equals(string)) {
                    arrayList.add(CHINESE_DICTIONARY_SERVICE);
                    arrayList.add(LIVING_LANGUAGE_SERVICE);
                } else if (Strings.TASK_LANGUAGE_LIST_UPDATED.equals(string)) {
                    arrayList.add(LANGUAGE_SERVICE);
                }
            }
        } catch (Exception e) {
        }
        return arrayList.size() == 0 ? "" : StringUtils.implode(arrayList, ",");
    }

    public final ACBuildInfo getBuildInfo() {
        if (this.isShutdown) {
            return null;
        }
        if (this.buildInfo == null) {
            this.buildInfo = new ACBuildInfo(this);
        }
        return this.buildInfo;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final BuildSettings getBuildSettings() {
        return this.buildSettings;
    }

    public final ACConfiguration getConfiguration() {
        return this.configuration;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ConnectServiceManager getConnect() {
        return this.connect;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Context getContext() {
        return this.context;
    }

    public final String getCoreVersion(int i) throws ACException {
        String str;
        switch (i) {
            case 1:
                str = this.coreVersionAlpha;
                break;
            case 2:
                str = this.coreVersionKorean;
                break;
            case 3:
                str = this.coreVersionChinese;
                break;
            case 4:
                str = this.coreVersionJapanese;
                break;
            default:
                throw new ACException(123, "You must specify a proper core type");
        }
        if (str == null) {
            throw new ACException(123, "The core version was not set for the specified core");
        }
        return str;
    }

    public final ACDLMConnector getDLMConnector() {
        if (this.isShutdown) {
            return null;
        }
        return this.dlmConnector;
    }

    public final ACLanguage getLanguageSettings() {
        if (this.isShutdown) {
            return null;
        }
        if (this.language == null) {
            this.language = new ACLanguage(this);
        }
        return this.language;
    }

    public final ACLegalDocuments getLegalDocuments() {
        if (this.isShutdown) {
            return null;
        }
        return this.legaldocs;
    }

    public final ACLocation getLocation() throws ACException {
        if (!isConnectStarted()) {
            throw new ACException(105, "start() must be called first");
        }
        if (!this.buildSettings.isLocationServiceEnabled()) {
            throw new ACException(128, "Location services are disabled in this build. Please contact Nuance if you wish to use location services.");
        }
        String locationCountry = this.connect.getUserSettings().getLocationCountry();
        if (locationCountry != null) {
            return new ACLocation(locationCountry);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ACReportingService getReportingService() {
        try {
            return (ACReportingService) getService(REPORTING_SERVICE);
        } catch (ACException e) {
            this.log.d("Could not get the reporting service");
            return null;
        }
    }

    public final ACService getService(String str) throws ACException {
        if (!this.isStarted) {
            this.log.d("getService called before calling start.");
            throw new ACException(105, "Please call start().");
        }
        if (this.isShutdown) {
            this.log.d("getService called after shutdown.");
            throw new ACException(119, "SDK has been shutdown. Please create a new instance with a clean object.");
        }
        if (!this.connect.isLicensed()) {
            throw new ACException(129, "This build is no longer licensed for use.");
        }
        if (!this.buildSettings.isLocationServiceEnabled() && CATALOG_SERVICE.equals(str)) {
            if (this.buildConfig.locationServiceEnabled) {
                throw new ACException(109, "Catalog service requires the location service which is disabled.");
            }
            throw new ACException(101, "Catalog service is not allowed in this build.  It requires the location service which is disabled for this build.");
        }
        if (!this.services.containsKey(str)) {
            this.log.d("Service not available. ", str);
            throw new ACException(101, str + " is not available.");
        }
        ACService aCService = this.services.get(str);
        if (aCService.requiresDocument(4) && !this.legaldocs.userHasAcceptedDocumentByType(4)) {
            this.log.w("A service has been requested that requires DATA_OPT_IN, but DATA_OPT_IN not accepted.");
            throw new ACException(126, "Please Accept the Data Opt-in before requesting " + aCService.getName() + ".");
        }
        if (aCService.requiresDocument(1) && !this.legaldocs.userHasAcceptedDocumentByType(1)) {
            this.log.w("A service has been requested that requires TOS, but TOS not accepted.");
            throw new ACException(104, "Please Accept the TOS before requesting " + aCService.getName() + ".");
        }
        if (aCService.dependenciesMet()) {
            return this.services.get(str);
        }
        this.log.w("All required dependencies are not met for " + aCService.getName() + ".");
        throw new ACException(132, "All required dependencies are not met for " + aCService.getName() + ".");
    }

    public final boolean isConnectInitialized() {
        return this.isInitialized;
    }

    public final boolean isConnectStarted() {
        return this.isStarted;
    }

    public final void refresh() {
        if (this.isStarted && !this.isShutdown && this.isInitialized) {
            this.connect.refresh(false);
        }
    }

    public final void registerConnectionCallback(final ACConnectionCallback aCConnectionCallback) {
        ConnectionCallback connectionCallback = new ConnectionCallback() { // from class: com.nuance.swypeconnect.ac.ACManager.2
            @Override // com.nuance.connect.api.ConnectionCallback
            public void onConnected(int i, int i2) {
                aCConnectionCallback.connected(i, i2);
            }

            @Override // com.nuance.connect.api.ConnectionCallback
            public void onConnectionStatus(int i, String str) {
                if (i == 10) {
                    str = ACManager.this.convertJSONTasksToServiceTasks(str);
                }
                aCConnectionCallback.connectionStatus(i, str);
            }

            @Override // com.nuance.connect.api.ConnectionCallback
            public void onDisconnected(int i) {
                aCConnectionCallback.disconnected(i);
            }
        };
        synchronized (this.connectionCallbacks) {
            this.connectionCallbacks.put(aCConnectionCallback, connectionCallback);
        }
        this.connect.registerConnectionCallback(connectionCallback);
    }

    public final void retryConnection() {
        if (!this.isStarted || this.isShutdown) {
            return;
        }
        this.connect.retryConnection();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void serviceInitialized(String str) {
        this.log.d("ServiceInitialized(", str, ")");
        if (this.servicesToInitialize != null) {
            if (this.servicesToInitialize.contains(str)) {
                this.servicesToInitialize.remove(str);
            }
            if (this.servicesToInitialize.size() == 0) {
                if (this.configService.getInitializationTimestamp().equals(Long.MIN_VALUE)) {
                    this.configService.setInitialVersion(this.buildSettings.getVersion());
                    this.configService.setCurrentVersion(this.buildSettings.getVersion());
                    this.configService.setInitializationTimestamp(Long.valueOf(System.currentTimeMillis()));
                }
                this.isInitialized = true;
                this.connect.notifyConnectionStatus(13, "");
                this.servicesToInitialize = null;
            } else {
                this.log.d("remaining uninitialized services: ", this.servicesToInitialize.toString());
            }
        }
    }

    public final void setCoreVersions(String str, String str2, String str3, String str4) throws ACException {
        if (this.isStarted) {
            throw new ACException(109, "setCoreVersions() cannot be called after start.");
        }
        if (str != null && !Pattern.matches("\\d+\\.\\d+", str)) {
            throw new ACException(123, "Alpha does not match major.minor version pattern with " + str);
        }
        if (str != null) {
            this.coreVersionAlpha = str;
            this.dlmConnector.setDLMSize(1, 2097152, 0);
        } else {
            this.coreVersionAlpha = null;
        }
        if (str4 != null && !Pattern.matches("\\d+\\.\\d+", str4)) {
            throw new ACException(123, "Korean does not match major.minor version pattern with " + str4);
        }
        if (str4 != null) {
            this.coreVersionKorean = str4;
            this.dlmConnector.setDLMSize(2, 2097152, 0);
        } else {
            this.coreVersionKorean = null;
        }
        if (str2 != null && !Pattern.matches("\\d+\\.\\d+", str2)) {
            throw new ACException(123, "Chinese does not match major.minor version pattern with " + str2);
        }
        if (str2 != null) {
            this.coreVersionChinese = str2;
        } else {
            this.coreVersionChinese = null;
        }
        if (str3 != null && !Pattern.matches("\\d+\\.\\d+", str3)) {
            throw new ACException(123, "Japanese does not match major.minor version pattern with " + str3);
        }
        if (str3 != null) {
            this.coreVersionJapanese = str3;
        } else {
            this.coreVersionJapanese = null;
        }
        this.buildSettings.setCoreVersions(this.coreVersionAlpha, this.coreVersionChinese, this.coreVersionJapanese, this.coreVersionKorean);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setLocationServiceAllowed(boolean z) throws ACException {
        if (!this.buildConfig.locationServiceEnabled) {
            throw new ACException(109, "Location services are disabled in this build. Please contact Nuance if you wish to use location services.");
        }
        if (isConnectStarted()) {
            throw new ACException(120, "must be configured before calling start()");
        }
        this.buildSettings.setLocationServiceEnabled(z);
    }

    final void shutDownServices() {
        Iterator<Map.Entry<String, ACService>> it = this.services.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().shutdown();
        }
        this.dlmConnector.shutdown();
    }

    public final void shutdown() {
        this.startLock.lock();
        try {
            if (this.isStarted) {
                this.log.d("Shutdown ACManager");
                this.isStarted = false;
                this.isShutdown = true;
                this.connect.stop();
                shutDownServices();
            } else {
                this.log.d("ACManager not started, shutdown ignored");
            }
            this.log.d("Shutdown Complete");
        } finally {
            this.startLock.unlock();
        }
    }

    public final void start() throws ACException {
        this.startLock.lock();
        try {
            if (this.isStarted) {
                throw new ACException(118, "SDK Already Started");
            }
            if (!isConnectClientClassExist()) {
                throw new ACException(101, "Connect Service is not available. Please verify your SDK setup.");
            }
            if (!validApplicationId(this.applicationId)) {
                this.log.e("Invalid application key.");
                throw new ACException(107, "The application key is not valid.");
            }
            if (this.buildSettings.getBuildExpiration().length() > 0 && this.buildSettings.getBuildTimestamp().length() > 0) {
                this.log.v("Verifying build has not expired.");
                try {
                    String replace = this.buildSettings.getBuildTimestamp().replace(".", "T");
                    Time time = new Time();
                    time.setToNow();
                    Time time2 = new Time();
                    time2.parse(replace);
                    time2.monthDay += Integer.parseInt(this.buildSettings.getBuildExpiration());
                    time2.normalize(false);
                    Logger.getLog(Logger.LoggerType.DEVELOPER).d("now=", time, " expires=", time2, " expired=", Boolean.valueOf(time.after(time2)));
                    if (time.after(time2)) {
                        this.log.e("The SDK trial has expired on ", time2.format2445());
                        throw new ACException(117, "The SDK Trial has expired.");
                    }
                    this.log.w("This SDK trial expires on ", time2.format2445());
                } catch (TimeFormatException e) {
                    this.log.e("The SDK trial has expired.");
                    throw new ACException(117, "The SDK Trial has expired.");
                } catch (NumberFormatException e2) {
                    this.log.e("The SDK trial has expired.");
                    throw new ACException(117, "The SDK Trial has expired.");
                }
            }
            if (this.coreVersionAlpha == null && this.coreVersionChinese == null && this.coreVersionJapanese == null && this.coreVersionKorean == null) {
                throw new ACException(106, "You must specify core versions to start SC-SDK.");
            }
            if (!PermissionUtils.checkPermission(this.context, "android.permission.INTERNET") || !PermissionUtils.checkPermission(this.context, "android.permission.ACCESS_NETWORK_STATE") || !PermissionUtils.checkPermission(this.context, "android.permission.ACCESS_WIFI_STATE")) {
                throw new ACException(100, "No network permissions were enabled. Please verify the Android manifest file.");
            }
            if (this.configService.getCurrentVersion() != null && !this.buildSettings.getVersion().equals(this.configService.getCurrentVersion())) {
                this.log.i("This is an update version from ", this.configService.getCurrentVersion(), " to ", this.buildSettings.getVersion());
                this.configService.setCurrentVersion(this.buildSettings.getVersion());
                this.connect.onUpgrade(this.buildSettings.getVersion(), this.configService.getCurrentVersion());
            }
            this.isShutdown = false;
            this.isStarted = true;
            configureACSDK(this.selectedServices);
            setupInitialization();
            this.connect.start();
            this.connect.sendConnectionStatus();
            startServices();
            this.log.d("ACManager Started");
        } finally {
            this.startLock.unlock();
        }
    }

    final void startServices() {
        Iterator<Map.Entry<String, ACService>> it = this.services.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().start();
        }
        this.dlmConnector.start();
    }

    public final void unregisterConnectionCallback(ACConnectionCallback aCConnectionCallback) {
        ConnectionCallback remove;
        synchronized (this.connectionCallbacks) {
            remove = this.connectionCallbacks.remove(aCConnectionCallback);
        }
        if (remove != null) {
            this.connect.unregisterConnectionCallback(remove);
        }
    }
}
