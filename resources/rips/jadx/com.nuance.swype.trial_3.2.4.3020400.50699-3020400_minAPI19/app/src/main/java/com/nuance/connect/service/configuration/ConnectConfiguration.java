package com.nuance.connect.service.configuration;

import android.webkit.URLUtil;
import com.facebook.internal.ServerProtocol;
import com.nuance.connect.common.ServiceInitializationConfig;
import com.nuance.connect.internal.GenericProperty;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.PropertyStore;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.manager.SessionManager;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACBuildConfigRuntime;
import com.nuance.swypeconnect.ac.ACReportingService;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class ConnectConfiguration {
    private static final String ASSET_DATA_FILES_FOLDER = "";
    private static final String CONFIGURATION_FILE = "connect.dat";
    private static final String CONFIGURATION_FILE_ON_APK = "connect.dat";
    public static final String PROPERTY_DEFAULT_DELAY = "DEFAULT_DELAY";
    public static final String PROPERTY_DLM_ADD_WORD_FREQUENCY = "DLM_ADD_WORD_FREQUENCY";
    public static final String PROPERTY_DLM_SYNC_FREQUENCY = "DLM_SYNC_FREQUENCY";
    public static final String PROPERTY_KEY_ACCOUNT = "KEY_ACCOUNT";
    public static final String PROPERTY_KEY_BUILD = "KEY_BUILD";
    public static final String PROPERTY_KEY_DEVICE = "KEY_DEVICE";
    public static final String PROPERTY_MQTT_ENABLED = "MQTT_ENABLED";
    public static final String PROPERTY_MQTT_HOSTS = "MQTT_HOSTS";
    public static final String PROPERTY_MQTT_KEEPALIVE = "MQTT_KEEP_ALIVE";
    public static final String PROPERTY_POLLING_FREQUENCY = "POLLING_FREQUENCY";
    public static final String PROPERTY_REPORTING_AGGREGATE_FREQUENCY = "AGGREGATE_FREQUENCY";
    public static final String PROPERTY_REPORTING_STATISTICS_FREQUENCY = "REPORTING_FREQUENCY";
    public static final String PROPERTY_RESEARCH_UDB_UPLOAD_FREQUENCY = "UDB_UPLOAD_FREQUENCY";
    public static final int TYPE_BOOLEAN = 4;
    public static final int TYPE_INT = 3;
    public static final int TYPE_OBJECT = 1;
    public static final int TYPE_STRING = 2;
    public static final int TYPE_UNKNOWN = 0;
    public static final int VERIFY_NEG_ONE = 5;
    public static final int VERIFY_NON_NEGATIVE = 4;
    public static final int VERIFY_POSITIVE = 3;
    public static final int VERIFY_TYPE = 1;
    public static final int VERIFY_UNKNOWN = 0;
    public static final int VERIFY_URL = 2;
    private ConnectClient context;
    private static final List<String> ALLOWED_OEM_IDS = Arrays.asList("39124", ACBuildConfigRuntime.OEM_ID);
    private static final long DEFAULT_POLL_INTERVAL = TimeUnit.DAYS.toSeconds(7);
    private static final long DEFAULT_MANUAL_THROTTLE = TimeUnit.HOURS.toSeconds(4);
    private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final PropertyStore propertyStore = new PropertyStore();
    private Property.Verifier<String> stringVerifier = new Property.Verifier<String>() { // from class: com.nuance.connect.service.configuration.ConnectConfiguration.1
        @Override // com.nuance.connect.internal.Property.Verifier
        public boolean verify(Object obj, Property.Source source, Property<String> property) {
            return obj != null && (obj instanceof String) && ((String) obj).length() > 0;
        }
    };
    private Property.Verifier<String> urlVerifier = new Property.Verifier<String>() { // from class: com.nuance.connect.service.configuration.ConnectConfiguration.2
        @Override // com.nuance.connect.internal.Property.Verifier
        public boolean verify(Object obj, Property.Source source, Property<String> property) {
            return obj != null && (obj instanceof String) && URLUtil.isNetworkUrl((String) obj);
        }
    };
    private Property.Verifier<Integer> integerVerifier = new Property.Verifier<Integer>() { // from class: com.nuance.connect.service.configuration.ConnectConfiguration.3
        @Override // com.nuance.connect.internal.Property.Verifier
        public boolean verify(Object obj, Property.Source source, Property<Integer> property) {
            Integer num;
            if (obj instanceof String) {
                try {
                    num = Integer.valueOf(Integer.parseInt((String) obj));
                } catch (NumberFormatException e) {
                    num = null;
                }
            } else {
                num = obj instanceof Integer ? (Integer) obj : null;
            }
            if (num != null) {
                switch (property.getVerification()) {
                    case 1:
                        return num.intValue() >= 0;
                    case 3:
                        return num.intValue() > 0;
                    case 4:
                        return num.intValue() >= 0;
                    case 5:
                        return num.intValue() >= -1;
                }
            }
            return false;
        }
    };
    private Property.Verifier<Boolean> booleanVerifier = new Property.Verifier<Boolean>() { // from class: com.nuance.connect.service.configuration.ConnectConfiguration.4
        @Override // com.nuance.connect.internal.Property.Verifier
        public boolean verify(Object obj, Property.Source source, Property<Boolean> property) {
            if (obj instanceof Boolean) {
                return true;
            }
            String lowerCase = String.valueOf(obj).toLowerCase(Locale.US);
            return lowerCase.equals(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE) || lowerCase.equals(ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED) || lowerCase.equals("1") || lowerCase.equals("0");
        }
    };
    private Property.Verifier<Long> longVerifier = new Property.Verifier<Long>() { // from class: com.nuance.connect.service.configuration.ConnectConfiguration.5
        @Override // com.nuance.connect.internal.Property.Verifier
        public boolean verify(Object obj, Property.Source source, Property<Long> property) {
            Long l;
            if (obj instanceof String) {
                try {
                    l = Long.valueOf(Long.parseLong((String) obj));
                } catch (NumberFormatException e) {
                    l = null;
                }
            } else {
                l = obj instanceof Long ? (Long) obj : null;
            }
            if (l != null) {
                switch (property.getVerification()) {
                    case 1:
                        return l.longValue() >= 0;
                    case 3:
                        return l.longValue() > 0;
                    case 4:
                        return l.longValue() >= 0;
                    case 5:
                        return l.longValue() >= -1;
                }
            }
            return false;
        }
    };

    /* loaded from: classes.dex */
    public enum ConfigProperty {
        URL,
        DEFAULT_DELAY,
        REPORTING_FREQUENCY,
        REPORTING_TIME_LIMIT,
        REPORTING_MAX_RESULTS,
        AGGREGATE_FREQUENCY,
        REPORTING_AGGREGATE_TIME_LIMIT,
        POLLING_FREQUENCY,
        CUSTOMER_POLLING_FREQUENCY,
        DEFAULT_POLLING_INTERVAL_NO_FEATURES,
        POLL_INTERVAL_SYNC,
        POLL_INTERVAL_CHINESE_DATABASE,
        POLL_INTERVAL_LIVING_LANGUAGE,
        POLL_INTERVAL_LANGUAGE_DOWNLOAD,
        POLL_INTERVAL_CATALOG,
        POLL_INTERVAL_PLATFORM_UPDATE,
        MANUAL_REFRESH_THROTTLE,
        QUEUE_RESUME_TIMESTAMP,
        LOGGING,
        DEVELOPER_LOG_ENABLED,
        DEFAULT_LOADTIME,
        UDB_UPLOAD_FREQUENCY,
        CONNECTION_LIMIT,
        NETWORK_TIMEOUT,
        INITIAL_LOADTIME,
        FOREGROUND_CONFIGURATION,
        BACKGROUND_CONFIGURATION,
        STABLE_CELLULAR_CONNECTION_THRESHOLD,
        STABLE_WIFI_CONNECTION_THRESHOLD,
        HTTP_ANALYTICS_TIME,
        HTTP_LIMIT,
        MQTT_ENABLED,
        MQTT_HOSTS,
        MQTT_KEEP_ALIVE,
        MQTT_RECONNECT_WIFI,
        MQTT_RECONNECT_CELLULAR,
        KEY_DEVICE,
        KEY_BUILD,
        KEY_ACCOUNT,
        DLM_SYNC_FREQUENCY,
        DLM_ADD_WORD_FREQUENCY,
        ACCOUNT_ACCOUNT_EMAIL,
        CATEGORY_FREQUENCY,
        MESSAGE_EXPERATION,
        LANG_AUTO_LIST,
        CORE_VERSION_ALPHA,
        CORE_VERSION_KOREAN,
        CORE_VERSION_CHINESE,
        CORE_VERSION_JAPANESE,
        POSSIBLE_UPGRADE,
        ACCOUNT_ID,
        SESSION_ID,
        DEVICE_ID,
        SWIB,
        OEM_ID,
        APPLICATION_ID,
        CUSTOMER_STRING,
        BUILD_TYPE,
        SDK_VERSION,
        REPORTING_ALLOWED,
        PLATFORM_UPDATE_ENABLED,
        DOCUMENT_REVISIONS,
        TOS_ACCEPTED,
        MINIMUM_SSL_PROTOCOL,
        COLLECT_USER_PROPERTIES,
        ANONYMOUS_BUILD,
        BUILD_PROPERTIES_FILTER_BLOCK,
        BUILD_PROPERTIES_FILTER_PRE_TOS,
        LOCATION_GEO_IP_COUNTRY,
        LICENSING_DEFAULT_DELAY,
        LICENSING_LICENSED_BUILD,
        LICENSING_SERVER_DELAY,
        LICENSING_LAST_CHECKIN,
        LOCATION_SERVICE_SERVER_URL,
        LOCATION_SERVICE_ENABLED,
        LOCATION_SERVICE_LOOKUP_INTERVAL,
        CATALOG_LIST_REFRESH_MINIMUM_WAIT,
        CATALOG_SKU_LIST,
        FEATURES_LAST_USED,
        DEVICE_PROPERTIES_UPDATED
    }

    public ConnectConfiguration(ConnectClient connectClient) {
        this.context = connectClient;
        setup();
    }

    private Map<String, String> createFromDigest(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        try {
            return createFromString(new String(bArr, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            this.log.e((Object) "createFromDigest failed; UnsupportedEncodingException", (Throwable) e);
            return null;
        }
    }

    private byte[] getAPKFileContents(String str) {
        if (!ALLOWED_OEM_IDS.contains(getString(ConfigProperty.OEM_ID))) {
            this.log.d("getAPIKFileContents...quitting");
            return null;
        }
        try {
            InputStream open = this.context.getAssets().open(str);
            try {
                byte[] readBytes = readBytes(open, open.available());
                open.close();
                return readBytes;
            } catch (Throwable th) {
                open.close();
                throw th;
            }
        } catch (IOException e) {
            return null;
        }
    }

    private String getConfigurationFilenameOnFilesystem() {
        return this.context.getApplicationInfo().dataDir + "/connect.dat";
    }

    private boolean isValidConfiguration(byte[] bArr) {
        Map<String, String> createFromDigest;
        if (bArr == null || (createFromDigest = createFromDigest(bArr)) == null || !isValidConfigurationProperties(createFromDigest)) {
            return false;
        }
        setConfigurationProperties(createFromDigest);
        return true;
    }

    private static byte[] readBytes(InputStream inputStream, int i) throws IOException {
        byte[] bArr = new byte[i];
        new DataInputStream(inputStream).readFully(bArr);
        return bArr;
    }

    private void setConfigurationProperties(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Property<?> property = this.propertyStore.getProperty(entry.getKey());
            if (property != null) {
                try {
                    property.set(entry.getValue(), Property.Source.CONNECT_DAT);
                } catch (Exception e) {
                }
            }
        }
    }

    private void setup() {
        PersistentDataStore dataStore = this.context.getDataStore();
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.OEM_ID.name(), null, dataStore));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.APPLICATION_ID.name(), null, dataStore));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.CUSTOMER_STRING.name(), null, dataStore));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.BUILD_TYPE.name(), null, dataStore));
        this.propertyStore.setProperty(new GenericProperty.BooleanProperty(ConfigProperty.REPORTING_ALLOWED.name(), Boolean.FALSE, dataStore));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.DOCUMENT_REVISIONS.name(), null, dataStore));
        this.propertyStore.setProperty(new GenericProperty.BooleanProperty(ConfigProperty.TOS_ACCEPTED.name(), Boolean.FALSE, dataStore));
        this.propertyStore.setProperty(new GenericProperty.BooleanProperty(ConfigProperty.PLATFORM_UPDATE_ENABLED.name(), Boolean.FALSE, dataStore));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.URL.name(), "https://api.swypeconnect.com/", dataStore, 2, 0, this.urlVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.DEFAULT_DELAY.name(), Integer.valueOf((int) TimeUnit.MINUTES.toSeconds(5L)), dataStore, 3, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.DEFAULT_POLLING_INTERVAL_NO_FEATURES.name(), Integer.valueOf(SessionManager.DEFAULT_POLLING_INTERVAL_NO_FEATURES), dataStore, 5, 2, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.POLLING_FREQUENCY.name(), Integer.valueOf(SessionManager.DEFAULT_POLLING_INTERVAL_NO_FEATURES), dataStore, 5, 2, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.CUSTOMER_POLLING_FREQUENCY.name(), -1, dataStore, 5, 4, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.POLL_INTERVAL_SYNC.name(), Integer.valueOf((int) DEFAULT_POLL_INTERVAL), dataStore, 3, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.POLL_INTERVAL_CHINESE_DATABASE.name(), Integer.valueOf((int) DEFAULT_POLL_INTERVAL), dataStore, 3, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.POLL_INTERVAL_LIVING_LANGUAGE.name(), Integer.valueOf((int) DEFAULT_POLL_INTERVAL), dataStore, 3, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.POLL_INTERVAL_LANGUAGE_DOWNLOAD.name(), Integer.valueOf((int) DEFAULT_POLL_INTERVAL), dataStore, 3, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.POLL_INTERVAL_CATALOG.name(), Integer.valueOf((int) DEFAULT_POLL_INTERVAL), dataStore, 3, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.POLL_INTERVAL_PLATFORM_UPDATE.name(), Integer.valueOf((int) DEFAULT_POLL_INTERVAL), dataStore, 3, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.MANUAL_REFRESH_THROTTLE.name(), Integer.valueOf((int) DEFAULT_MANUAL_THROTTLE), dataStore, 3, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.LongProperty(ConfigProperty.QUEUE_RESUME_TIMESTAMP.name(), -1L, dataStore, 5, 0, this.longVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.DEFAULT_LOADTIME.name(), Integer.valueOf((int) TimeUnit.HOURS.toSeconds(1L)), dataStore, 1, 2, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.LOGGING.name(), 6, dataStore, 4, 6, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.BooleanProperty(ConfigProperty.DEVELOPER_LOG_ENABLED.name(), Boolean.FALSE, dataStore, 1, 2, this.booleanVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.REPORTING_FREQUENCY.name(), Integer.valueOf((int) TimeUnit.DAYS.toSeconds(8L)), dataStore, 3, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.REPORTING_TIME_LIMIT.name(), Integer.valueOf((int) TimeUnit.DAYS.toDays(90L)), dataStore, 3, 2, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.REPORTING_MAX_RESULTS.name(), 50000, dataStore, 3, 2, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.AGGREGATE_FREQUENCY.name(), Integer.valueOf((int) TimeUnit.DAYS.toSeconds(8L)), dataStore, 3, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.REPORTING_AGGREGATE_TIME_LIMIT.name(), Integer.valueOf((int) TimeUnit.DAYS.toDays(90L)), dataStore, 3, 2, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.UDB_UPLOAD_FREQUENCY.name(), Integer.valueOf((int) TimeUnit.DAYS.toSeconds(25L)), dataStore, 5, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.STABLE_CELLULAR_CONNECTION_THRESHOLD.name(), Integer.valueOf((int) TimeUnit.MINUTES.toMillis(5L)), dataStore, 5, 2, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.STABLE_WIFI_CONNECTION_THRESHOLD.name(), Integer.valueOf((int) TimeUnit.SECONDS.toMillis(5L)), dataStore, 5, 2, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.HTTP_ANALYTICS_TIME.name(), 12, dataStore, 5, 2, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.HTTP_LIMIT.name(), 10485760, dataStore, 5, 2, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.DLM_SYNC_FREQUENCY.name(), 0, dataStore, 5, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.DLM_ADD_WORD_FREQUENCY.name(), Integer.valueOf(ACReportingService.REASON_DATA_POINT_IDENTIFIER_NOT_FOUND), dataStore, 5, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.KEY_DEVICE.name(), "", dataStore, 1, 3, this.stringVerifier));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.KEY_BUILD.name(), "", dataStore, 1, 3, this.stringVerifier));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.KEY_ACCOUNT.name(), "", dataStore, 1, 3, this.stringVerifier));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.ACCOUNT_ACCOUNT_EMAIL.name(), "", dataStore, 1, 2, this.stringVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.CATEGORY_FREQUENCY.name(), Integer.valueOf((int) TimeUnit.HOURS.toSeconds(2L)), dataStore, 3, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.LANG_AUTO_LIST.name(), Integer.valueOf((int) TimeUnit.SECONDS.toSeconds(30L)), dataStore, 5, 7, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.LICENSING_DEFAULT_DELAY.name(), Integer.valueOf((int) TimeUnit.DAYS.toSeconds(1L)), dataStore, 3, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.LICENSING_SERVER_DELAY.name(), 0, dataStore, 5, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.LongProperty(ConfigProperty.LICENSING_LAST_CHECKIN.name(), null, dataStore));
        this.propertyStore.setProperty(new GenericProperty.BooleanProperty(ConfigProperty.LICENSING_LICENSED_BUILD.name(), Boolean.TRUE, dataStore, 1, 0, this.booleanVerifier));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.BUILD_PROPERTIES_FILTER_BLOCK.name(), null, dataStore, 1, 0, this.stringVerifier));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.BUILD_PROPERTIES_FILTER_PRE_TOS.name(), null, dataStore, 1, 0, this.stringVerifier));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.LOCATION_SERVICE_SERVER_URL.name(), "https://lbs.swypeconnect.com/", dataStore, 2, 3, this.urlVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.LOCATION_SERVICE_LOOKUP_INTERVAL.name(), Integer.valueOf((int) DEFAULT_POLL_INTERVAL), dataStore, 3, 3, this.integerVerifier));
        this.propertyStore.setProperty(new GenericProperty.BooleanProperty(ConfigProperty.LOCATION_SERVICE_ENABLED.name(), Boolean.TRUE, dataStore, 1, 3, this.booleanVerifier));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.LOCATION_GEO_IP_COUNTRY.name(), null, dataStore, 1, 0, this.stringVerifier));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.CATALOG_SKU_LIST.name(), null, dataStore, 1, 0, this.stringVerifier));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.FEATURES_LAST_USED.name(), null, dataStore, 1, 0, this.stringVerifier));
        this.propertyStore.setProperty(new GenericProperty.StringProperty(ConfigProperty.DEVICE_PROPERTIES_UPDATED.name(), null, null, 1, 0, this.stringVerifier));
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(ConfigProperty.CATALOG_LIST_REFRESH_MINIMUM_WAIT.name(), Integer.valueOf((int) TimeUnit.HOURS.toMillis(1L)), dataStore, 3, 3, this.integerVerifier));
        loadOverrides();
    }

    private boolean validateProperty(String str, String str2, Property.Source source) {
        Property<?> property = this.propertyStore.getProperty(str);
        boolean verify = property != null ? property.verify(str2, source) : false;
        if (!verify) {
            this.log.e("Validation failed while loading override values for ", str, " : ", str2);
        }
        return verify;
    }

    public void addListener(String str, Property.ValueListener<?> valueListener) {
        this.propertyStore.addListener(str, valueListener);
    }

    public final Map<String, String> createFromString(String str) {
        try {
            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(str.getBytes("UTF-8")));
            return new HashMap(properties);
        } catch (IOException e) {
            this.log.e((Object) "createFromString failed: ", (Throwable) e);
            return null;
        }
    }

    public void destroy() {
        this.context = null;
    }

    public Boolean getBoolean(ConfigProperty configProperty) {
        return this.propertyStore.getBoolean(configProperty.name());
    }

    public final byte[] getConfigurationDigest(boolean z) {
        return !z ? getConfigurationFromFileSystem(null) : getAPKFileContents("connect.dat");
    }

    public byte[] getConfigurationFromFileSystem(String str) {
        if (str == null) {
            str = getConfigurationFilenameOnFilesystem();
            if (!ALLOWED_OEM_IDS.contains(getString(ConfigProperty.OEM_ID))) {
                return null;
            }
        }
        File file = new File(str);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                byte[] readBytes = readBytes(fileInputStream, (int) file.length());
                fileInputStream.close();
                return readBytes;
            } catch (Throwable th) {
                fileInputStream.close();
                throw th;
            }
        } catch (IOException e) {
            return null;
        }
    }

    public Integer getInteger(ConfigProperty configProperty) {
        return this.propertyStore.getInteger(configProperty.name());
    }

    public Long getLong(ConfigProperty configProperty) {
        return this.propertyStore.getLong(configProperty.name());
    }

    public String getString(ConfigProperty configProperty) {
        return this.propertyStore.getString(configProperty.name());
    }

    protected final boolean isValidConfigurationProperties(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!validateProperty(entry.getKey(), entry.getValue(), Property.Source.CONNECT_DAT)) {
                return false;
            }
        }
        return true;
    }

    public void loadOverrides() {
        if (isValidConfiguration(getConfigurationDigest(false))) {
            return;
        }
        isValidConfiguration(getConfigurationDigest(true));
    }

    public void overrideFromServer(String str, int i) {
        Property<?> property = this.propertyStore.getProperty(str);
        if (property != null) {
            try {
                property.set(Integer.valueOf(i), Property.Source.SERVER);
            } catch (Exception e) {
                this.log.e((Object) "Error overriding configration value ", (Throwable) e);
            }
        }
    }

    public void overrideFromServer(String str, String str2) {
        Property<?> property = this.propertyStore.getProperty(str);
        if (property != null) {
            try {
                property.set(str2, Property.Source.SERVER);
            } catch (Exception e) {
                this.log.e((Object) "Error overriding configration value ", (Throwable) e);
            }
        }
    }

    public void overrideFromServer(String str, boolean z) {
        Property<?> property = this.propertyStore.getProperty(str);
        if (property != null) {
            try {
                property.set(Boolean.valueOf(z), Property.Source.SERVER);
            } catch (Exception e) {
                this.log.e((Object) "Error overriding configration value ", (Throwable) e);
            }
        }
    }

    public void setProperty(ConfigProperty configProperty, Boolean bool) {
        this.propertyStore.setProperty(new GenericProperty.BooleanProperty(configProperty.name(), bool));
    }

    public void setProperty(ConfigProperty configProperty, Integer num) {
        this.propertyStore.setProperty(new GenericProperty.IntegerProperty(configProperty.name(), num));
    }

    public void setProperty(ConfigProperty configProperty, Long l) {
        this.propertyStore.setProperty(new GenericProperty.LongProperty(configProperty.name(), l));
    }

    public void setProperty(ConfigProperty configProperty, String str) {
        this.propertyStore.setProperty(new GenericProperty.StringProperty(configProperty.name(), str));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String str : this.propertyStore.keySet()) {
            sb.append(str).append('=').append(this.propertyStore.getProperty(str).toString()).append('\n');
        }
        return sb.toString();
    }

    public void updateFromInitializationConfig(ServiceInitializationConfig serviceInitializationConfig) {
        if (validateProperty(ConfigProperty.URL.name(), serviceInitializationConfig.getApiServerUrl(), Property.Source.BUILD)) {
            setProperty(ConfigProperty.URL, serviceInitializationConfig.getApiServerUrl());
        }
        if (validateProperty(ConfigProperty.LOGGING.name(), String.valueOf(serviceInitializationConfig.getLogLevel()), Property.Source.OEM_RUNTIME)) {
            setProperty(ConfigProperty.LOGGING, Integer.valueOf(serviceInitializationConfig.getLogLevel()));
        }
        if (validateProperty(ConfigProperty.LOCATION_SERVICE_SERVER_URL.name(), serviceInitializationConfig.getLocationServerUrl(), Property.Source.BUILD)) {
            setProperty(ConfigProperty.LOCATION_SERVICE_SERVER_URL, serviceInitializationConfig.getLocationServerUrl());
        }
        if (validateProperty(ConfigProperty.LOCATION_SERVICE_LOOKUP_INTERVAL.name(), String.valueOf(serviceInitializationConfig.getLocationServiceLookupInterval()), Property.Source.BUILD)) {
            setProperty(ConfigProperty.LOCATION_SERVICE_LOOKUP_INTERVAL, Integer.valueOf(serviceInitializationConfig.getLocationServiceLookupInterval()));
        }
        if (validateProperty(ConfigProperty.LOCATION_SERVICE_ENABLED.name(), String.valueOf(serviceInitializationConfig.isLocationServiceEnabled()), Property.Source.BUILD)) {
            setProperty(ConfigProperty.LOCATION_SERVICE_ENABLED, Boolean.valueOf(serviceInitializationConfig.isLocationServiceEnabled()));
        }
        if (validateProperty(ConfigProperty.DEVELOPER_LOG_ENABLED.name(), String.valueOf(serviceInitializationConfig.isDeveloperLogEnabled()), Property.Source.BUILD)) {
            setProperty(ConfigProperty.DEVELOPER_LOG_ENABLED, Boolean.valueOf(serviceInitializationConfig.isDeveloperLogEnabled()));
        }
        if (validateProperty(ConfigProperty.DEFAULT_DELAY.name(), String.valueOf(serviceInitializationConfig.getDefaultDelay()), Property.Source.BUILD)) {
            setProperty(ConfigProperty.DEFAULT_DELAY, Integer.valueOf(serviceInitializationConfig.getDefaultDelay()));
        }
    }

    public boolean updateFromString(String str) {
        Map<String, String> createFromString = createFromString(str);
        if (createFromString == null || !isValidConfigurationProperties(createFromString)) {
            return false;
        }
        for (Map.Entry<String, String> entry : createFromString.entrySet()) {
            Property<?> property = this.propertyStore.getProperty(entry.getKey());
            if (property != null) {
                property.set(entry.getValue(), Property.Source.BUILD);
            }
        }
        return true;
    }
}
