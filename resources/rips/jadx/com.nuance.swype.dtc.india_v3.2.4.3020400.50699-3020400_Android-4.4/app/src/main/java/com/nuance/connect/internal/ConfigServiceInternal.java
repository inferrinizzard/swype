package com.nuance.connect.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import com.nuance.connect.api.ConfigService;
import com.nuance.connect.common.ActionFilterStrings;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.Strings;
import com.nuance.connect.host.service.BuildSettings;
import com.nuance.connect.host.service.HostInterface;
import com.nuance.connect.internal.AppSettings;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.APIHandlers;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.system.NetworkState;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class ConfigServiceInternal extends AbstractService implements ConfigService {
    private static final InternalMessages[] MESSAGE_IDS = {InternalMessages.MESSAGE_HOST_GET_OEM_ID, InternalMessages.MESSAGE_HOST_GET_LANGUAGE_INFO, InternalMessages.MESSAGE_HOST_GET_CORE_VERSIONS, InternalMessages.MESSAGE_HOST_GET_SDK_VERSION, InternalMessages.MESSAGE_HOST_GET_FOREGROUND_DATA, InternalMessages.MESSAGE_HOST_GET_BACKGROUND_DATA, InternalMessages.MESSAGE_HOST_GET_APPLICATION_ID, InternalMessages.MESSAGE_HOST_GET_CUSTOMER_STRING, InternalMessages.MESSAGE_HOST_SET_SWYPER_ID, InternalMessages.MESSAGE_HOST_SET_DEVICE_ID, InternalMessages.MESSAGE_HOST_GET_CONNECTION_LIMIT, InternalMessages.MESSAGE_HOST_GET_NETWORK_IDLE_TIMEOUT, InternalMessages.MESSAGE_HOST_GET_REPORTING_ALLOWED, InternalMessages.MESSAGE_HOST_GET_BUILD_PROPERTIES_FILTER, InternalMessages.MESSAGE_HOST_GET_PLATFORM_UPDATE_ENABLED, InternalMessages.MESSAGE_HOST_GET_CUSTOMER_SETTINGS, InternalMessages.MESSAGE_HOST_CONFIG_UPDATED};
    private int[] activeLanguages;
    private Locale activeLocale;
    private ConnectServiceManagerInternal connectService;
    private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    Property.IntegerValueListener refreshListener = new Property.IntegerValueListener() { // from class: com.nuance.connect.internal.ConfigServiceInternal.1
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<Integer> property) {
            ConfigServiceInternal.this.sendPollingFrequency();
        }
    };
    Property.IntegerValueListener concurrentConnectionLimit = new Property.IntegerValueListener() { // from class: com.nuance.connect.internal.ConfigServiceInternal.2
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<Integer> property) {
            ConfigServiceInternal.this.sendConnectionConcurrentLimit();
        }
    };
    Property.IntegerValueListener downloadIdleTimeout = new Property.IntegerValueListener() { // from class: com.nuance.connect.internal.ConfigServiceInternal.3
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<Integer> property) {
            ConfigServiceInternal.this.sendDownloadIdleTimeout();
        }
    };
    Property.StringValueListener foregroundDataListener = new Property.StringValueListener() { // from class: com.nuance.connect.internal.ConfigServiceInternal.4
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<String> property) {
            if (property != null) {
                ConfigServiceInternal.this.sendForegroundDataState();
                ConfigServiceInternal.this.connectService.onConnectivityChanged();
            }
        }
    };
    Property.StringValueListener backgroundDataListener = new Property.StringValueListener() { // from class: com.nuance.connect.internal.ConfigServiceInternal.5
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<String> property) {
            if (property != null) {
                ConfigServiceInternal.this.sendBackgroundDataState();
                ConfigServiceInternal.this.connectService.onConnectivityChanged();
            }
        }
    };
    private ConnectHandler handler = new ConnectHandler() { // from class: com.nuance.connect.internal.ConfigServiceInternal.6
        @Override // com.nuance.connect.internal.ConnectHandler
        public String getHandlerName() {
            return APIHandlers.SWYPE_CONFIGURATION_HANDLER;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public int[] getMessageIDs() {
            int[] iArr = new int[ConfigServiceInternal.MESSAGE_IDS.length];
            for (int i = 0; i < ConfigServiceInternal.MESSAGE_IDS.length; i++) {
                iArr[i] = ConfigServiceInternal.MESSAGE_IDS[i].ordinal();
            }
            return iArr;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void handleMessage(Handler handler, Message message) {
            ClientBinder binder = ConfigServiceInternal.this.connectService.getBinder();
            switch (AnonymousClass7.$SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.fromInt(message.what).ordinal()]) {
                case 1:
                    ConfigServiceInternal.this.log.d("MESSAGE_HOST_GET_OEM_ID");
                    binder.sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_OEM_ID, ConfigServiceInternal.this.getProperty(BuildSettings.Property.OEM_ID));
                    return;
                case 2:
                    ConfigServiceInternal.this.log.d("MESSAGE_HOST_GET_LANGUAGE_INFO");
                    ConfigServiceInternal.this.sendLanguageInfo();
                    ConfigServiceInternal.this.sendLocaleInfo();
                    return;
                case 3:
                    Bundle bundle = new Bundle();
                    SparseArray<String> coreVersions = ((BuildSettings) ConfigServiceInternal.this.connectService.getHostService(HostInterface.HostService.HOST_BUILD_SETTINGS)).getCoreVersions();
                    bundle.putString(Strings.PROP_CORE_ALPHA, coreVersions.get(1));
                    bundle.putString(Strings.PROP_CORE_CHINESE, coreVersions.get(3));
                    bundle.putString(Strings.PROP_CORE_JAPANESE, coreVersions.get(4));
                    bundle.putString(Strings.PROP_CORE_KOREAN, coreVersions.get(2));
                    binder.sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_CORE_VERSIONS, bundle);
                    return;
                case 4:
                    binder.sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_SDK_VERSION, ConfigServiceInternal.this.getProperty(BuildSettings.Property.VERSION));
                    return;
                case 5:
                    binder.sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_APPLICATION_ID, ((BuildSettings) ConfigServiceInternal.this.connectService.getHostService(HostInterface.HostService.HOST_BUILD_SETTINGS)).getApplicationId());
                    return;
                case 6:
                    binder.sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_CUSTOMER_STRING, ConfigServiceInternal.this.connectService.getAppSettings().getCustomerString());
                    return;
                case 7:
                    String string = message.getData().getString(Strings.DEFAULT_KEY);
                    if (ConfigServiceInternal.this.getSwyperId() == null || !ConfigServiceInternal.this.getSwyperId().equals(string)) {
                        ConfigServiceInternal.this.setSwyperId(string);
                        ConfigServiceInternal.this.connectService.dispatchAction(ActionFilterStrings.ACTION_DATA_AVAILABLE, ActionFilterStrings.TYPE_SWYPER_ID);
                        return;
                    }
                    return;
                case 8:
                    String string2 = message.getData().getString(Strings.DEFAULT_KEY);
                    if (ConfigServiceInternal.this.getDeviceId() == null || !ConfigServiceInternal.this.getDeviceId().equals(string2)) {
                        ConfigServiceInternal.this.setDeviceId(string2);
                        ConfigServiceInternal.this.connectService.dispatchAction(ActionFilterStrings.ACTION_DATA_AVAILABLE, ActionFilterStrings.TYPE_DEVICE_ID);
                        return;
                    }
                    return;
                case 9:
                    ConfigServiceInternal.this.sendConnectionConcurrentLimit();
                    return;
                case 10:
                    ConfigServiceInternal.this.sendDownloadIdleTimeout();
                    return;
                case 11:
                    ConfigServiceInternal.this.sendForegroundDataState();
                    return;
                case 12:
                    ConfigServiceInternal.this.sendBackgroundDataState();
                    return;
                case 13:
                    binder.sendConnectMessage(InternalMessages.MESSAGE_CLIENT_SET_SDK_VERSION, ConfigServiceInternal.this.getProperty(BuildSettings.Property.COLLECT_USER_PROPERTIES));
                    return;
                case 14:
                    ConfigServiceInternal.this.sendReportingAllowed();
                    return;
                case 15:
                    ConfigServiceInternal.this.log.d("MESSAGE_HOST_GET_BUILD_PROPERTIES_FILTER");
                    Bundle bundle2 = new Bundle();
                    BuildSettings buildSettings = (BuildSettings) ConfigServiceInternal.this.connectService.getHostService(HostInterface.HostService.HOST_BUILD_SETTINGS);
                    bundle2.putString(Strings.MESSAGE_BUNDLE_KEY_BUILD_PROPERTIES_FILTER_BLOCK, buildSettings.getBuildPropertiesFilterBlock());
                    bundle2.putString(Strings.MESSAGE_BUNDLE_KEY_BUILD_PROPERTIES_FILTER_PRE_TOS, buildSettings.getBuildPropertiesFilterPreTos());
                    binder.sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_BUILD_PROPERTIES_FILTER, bundle2);
                    return;
                case 16:
                    ConfigServiceInternal.this.log.d("MESSAGE_HOST_GET_PLATFORM_UPDATE_ENABLED");
                    binder.sendConnectMessage(InternalMessages.MESSAGE_CLIENT_SET_PLATFORM_UPDATE_ENABLED, Boolean.valueOf(((BuildSettings) ConfigServiceInternal.this.connectService.getHostService(HostInterface.HostService.HOST_BUILD_SETTINGS)).isPlatformUpdateEnabled()));
                    return;
                case 17:
                    ConfigServiceInternal.this.connectService.getAppSettings().sendCustomProperties();
                    return;
                case 18:
                    ConfigServiceInternal.this.connectService.notifyConnectionStatus(22, "A new or updated configuration is available.");
                    return;
                default:
                    return;
            }
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void onPostUpgrade() {
        }
    };

    /* renamed from: com.nuance.connect.internal.ConfigServiceInternal$7, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$com$nuance$connect$internal$common$InternalMessages = new int[InternalMessages.values().length];

        static {
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_OEM_ID.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_LANGUAGE_INFO.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_CORE_VERSIONS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_SDK_VERSION.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_APPLICATION_ID.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_CUSTOMER_STRING.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_SET_SWYPER_ID.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_SET_DEVICE_ID.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_CONNECTION_LIMIT.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_NETWORK_IDLE_TIMEOUT.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_FOREGROUND_DATA.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_BACKGROUND_DATA.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_COLLECT_USER_PROPERTIES.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_REPORTING_ALLOWED.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_BUILD_PROPERTIES_FILTER.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_PLATFORM_UPDATE_ENABLED.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_CUSTOMER_SETTINGS.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_CONFIG_UPDATED.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
        }
    }

    public ConfigServiceInternal(ConnectServiceManagerInternal connectServiceManagerInternal) {
        this.connectService = connectServiceManagerInternal;
        UserSettings userSettings = this.connectService.getUserSettings();
        AppSettings appSettings = connectServiceManagerInternal.getAppSettings();
        appSettings.registerSettingsListener(AppSettings.Key.CONFIGURATION_REFRESH_INTERVAL, this.refreshListener);
        userSettings.registerUserSettingsListener(UserSettings.BACKGROUND_DATA_STATE, this.backgroundDataListener);
        userSettings.registerUserSettingsListener(UserSettings.FOREGROUND_DATA_STATE, this.foregroundDataListener);
        appSettings.registerSettingsListener(AppSettings.Key.CONFIGURATION_CONNECTION_CONCURRENT_LIMIT, this.concurrentConnectionLimit);
        appSettings.registerSettingsListener(AppSettings.Key.DOWNLOAD_IDLE_TIMEOUT, this.downloadIdleTimeout);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getProperty(BuildSettings.Property property) {
        BuildSettings buildSettings = (BuildSettings) this.connectService.getHostService(HostInterface.HostService.HOST_BUILD_SETTINGS);
        if (buildSettings != null && property != null) {
            if (property.equals(BuildSettings.Property.VERSION)) {
                return buildSettings.getVersion();
            }
            if (property.equals(BuildSettings.Property.OEM_ID)) {
                return buildSettings.getOemId();
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendBackgroundDataState() {
        this.log.d("ConfigService.sendBackgroundDataState()");
        ClientBinder binder = this.connectService.getBinder();
        NetworkState.NetworkConfiguration backgroundNetworkState = this.connectService.getUserSettings().getBackgroundNetworkState();
        if (backgroundNetworkState == null || !binder.isBound()) {
            return;
        }
        binder.sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_BACKGROUND_DATA, backgroundNetworkState.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendConnectionConcurrentLimit() {
        this.log.d("ConfigService.sendConnectionConcurrentLimit()");
        ClientBinder binder = this.connectService.getBinder();
        int connectionConcurrentLimit = this.connectService.getAppSettings().getConnectionConcurrentLimit();
        if (connectionConcurrentLimit >= 0) {
            binder.sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_CONNECTION_LIMIT, Integer.valueOf(connectionConcurrentLimit));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendDownloadIdleTimeout() {
        this.log.d("ConfigService.sendDownloadIdleTimeout()");
        int downloadIdleTimeout = this.connectService.getAppSettings().getDownloadIdleTimeout();
        if (downloadIdleTimeout >= 10 || downloadIdleTimeout == -1) {
            this.connectService.getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_NETWORK_IDLE_TIMEOUT, Integer.valueOf(downloadIdleTimeout));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendForegroundDataState() {
        this.log.d("ConfigService.sendForegroundDataState()");
        ClientBinder binder = this.connectService.getBinder();
        NetworkState.NetworkConfiguration foregroundNetworkState = this.connectService.getUserSettings().getForegroundNetworkState();
        if (foregroundNetworkState == null || !binder.isBound()) {
            return;
        }
        binder.sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_FOREGROUND_DATA, foregroundNetworkState.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendPollingFrequency() {
        ClientBinder binder = this.connectService.getBinder();
        int refreshInterval = this.connectService.getAppSettings().getRefreshInterval();
        if (refreshInterval >= 0) {
            binder.sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_POLLING_FREQUENCY, Integer.valueOf(refreshInterval));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendReportingAllowed() {
        this.log.d("ConfigService.sendReportingAllowed");
        this.connectService.getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_REPORTING_ALLOWED, Boolean.valueOf(this.connectService.getAppSettings().isReportingAllowed()));
    }

    @Override // com.nuance.connect.api.ConfigService
    public void deleteConfigurationProperty(String str) throws Exception {
        this.connectService.getAppSettings().deleteConfigurationProperty(str);
    }

    @Override // com.nuance.connect.api.ConfigService
    public int[] getActiveLanguages() {
        return this.activeLanguages != null ? (int[]) this.activeLanguages.clone() : new int[0];
    }

    @Override // com.nuance.connect.api.ConfigService
    public Locale getActiveLocale() {
        return this.activeLocale;
    }

    @Override // com.nuance.connect.api.ConfigService
    public NetworkState.NetworkConfiguration getBackgroundNetworkState() {
        return this.connectService.getUserSettings().getBackgroundNetworkState();
    }

    @Override // com.nuance.connect.api.ConfigService
    public String getConfigurationProperty(String str) throws Exception {
        return this.connectService.getAppSettings().getConfigurationProperty(str);
    }

    @Override // com.nuance.connect.api.ConfigService
    public int getConnectionConcurrentLimit() {
        return this.connectService.getAppSettings().getConnectionConcurrentLimit();
    }

    @Override // com.nuance.connect.api.ConfigService
    public String getCurrentVersion() {
        return this.connectService.getAppSettings().getCurrentVersion();
    }

    @Override // com.nuance.connect.api.ConfigService
    public String getCustomExternalConfiguration(String str) throws Exception {
        String readString = this.connectService.getDataStore().readString(Strings.EXTERNAL_CONFIGS_PREFIX + str, null);
        if (readString != null) {
            return readString;
        }
        throw new Exception();
    }

    @Override // com.nuance.connect.api.ConfigService
    public String[] getCustomExternalConfigurationKeys() {
        List<String> stringToList = StringUtils.stringToList(this.connectService.getDataStore().readString("EXT_CONF_ALL_KEYS", null), ",");
        return (String[]) stringToList.toArray(new String[stringToList.size()]);
    }

    @Override // com.nuance.connect.api.ConfigService
    public String getCustomerString() {
        return this.connectService.getAppSettings().getCustomerString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectFeature[] getDependencies() {
        return ConnectFeature.CONFIG.values();
    }

    @Override // com.nuance.connect.api.ConfigService
    public String getDeviceId() {
        return this.connectService.getAppSettings().getDeviceId();
    }

    @Override // com.nuance.connect.api.ConfigService
    public int getDownloadIdleTimeout() {
        return this.connectService.getAppSettings().getDownloadIdleTimeout();
    }

    @Override // com.nuance.connect.api.ConfigService
    public NetworkState.NetworkConfiguration getForegroundNetworkState() {
        return this.connectService.getUserSettings().getForegroundNetworkState();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectHandler[] getHandlers() {
        return new ConnectHandler[]{this.handler};
    }

    @Override // com.nuance.connect.api.ConfigService
    public String getInitialVersion() {
        return this.connectService.getAppSettings().getInitialVersion();
    }

    @Override // com.nuance.connect.api.ConfigService
    public Long getInitializationTimestamp() {
        return this.connectService.getAppSettings().getInitializationTimestamp();
    }

    @Override // com.nuance.connect.api.ConfigService
    public int getMinimumRefreshInterval() {
        return this.connectService.getMinimumRefreshInterval();
    }

    @Override // com.nuance.connect.api.ConfigService
    public int getRefreshInterval() {
        return this.connectService.getAppSettings().getRefreshInterval();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public String getServiceName() {
        return ConnectFeature.CONFIG.name();
    }

    @Override // com.nuance.connect.api.ConfigService
    public String getSwyperId() {
        return this.connectService.getAppSettings().getSwyperId();
    }

    @Override // com.nuance.connect.api.ConfigService
    public boolean getUsageDataState() {
        return this.connectService.getUserSettings().isDataCollectionAccepted();
    }

    public void sendLanguageInfo() {
        this.log.d("ConfigService.sendLanguageInfo()");
        ClientBinder binder = this.connectService.getBinder();
        Bundle bundle = new Bundle();
        bundle.putIntArray(Strings.DEFAULT_KEY, this.activeLanguages);
        binder.sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_UPDATE_CURRENT_LANGUAGE, bundle);
    }

    protected void sendLocaleInfo() {
        ClientBinder binder = this.connectService.getBinder();
        if (this.activeLocale != null) {
            binder.sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_UPDATE_CURRENT_LOCALE, this.activeLocale.toString());
        }
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setActiveLanguages(int[] iArr) {
        if (Arrays.equals(iArr, this.activeLanguages)) {
            return;
        }
        this.activeLanguages = iArr;
        sendLanguageInfo();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setActiveLocale(Locale locale) {
        this.log.d("setActiveLocale()");
        this.activeLocale = locale;
        sendLocaleInfo();
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setBackgroundNetworkState(NetworkState.NetworkConfiguration networkConfiguration) {
        this.connectService.getUserSettings().setBackgroundNetworkState(networkConfiguration);
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setConfigurationProperty(String str, String str2, boolean z) {
        this.connectService.getAppSettings().setConfigurationProperty(str, str2, z);
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setConnectionConcurrentLimit(int i) {
        this.connectService.getAppSettings().setConnectionConcurrentLimit(i);
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setCurrentVersion(String str) {
        this.connectService.getAppSettings().setCurrentVersion(str);
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setCustomerString(String str) {
        this.connectService.getAppSettings().setCustomerString(str);
    }

    protected void setDeviceId(String str) {
        this.connectService.getAppSettings().setDeviceId(str);
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setDownloadIdleTimeout(int i) {
        this.connectService.getAppSettings().setDownloadIdleTimeout(i);
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setForegroundNetworkState(NetworkState.NetworkConfiguration networkConfiguration) {
        this.connectService.getUserSettings().setForegroundNetworkState(networkConfiguration);
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setInitialVersion(String str) {
        this.connectService.getAppSettings().setInitialVersion(str);
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setInitializationTimestamp(Long l) {
        this.connectService.getAppSettings().setInitializationTimestamp(l);
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setLogLevel(int i) {
        this.connectService.getAppSettings().setLogLevel(i);
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setMinimumSSLProtocol(String str) {
        this.connectService.getAppSettings().setMinimumSSLProtocol(str);
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setRefreshInterval(int i) {
        this.connectService.getAppSettings().setRefreshInterval(i);
    }

    protected void setSwyperId(String str) {
        this.connectService.getAppSettings().setSwyperId(str);
    }

    @Override // com.nuance.connect.api.ConfigService
    public void setUsageDataState(boolean z) {
        this.connectService.getUserSettings().setDataCollectionAccepted(z);
    }
}
