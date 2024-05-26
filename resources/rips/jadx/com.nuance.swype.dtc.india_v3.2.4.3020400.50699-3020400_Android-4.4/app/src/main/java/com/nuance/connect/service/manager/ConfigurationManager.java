package com.nuance.connect.service.manager;

import android.os.Message;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.common.ServiceInitializationConfig;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.GenericProperty;
import com.nuance.connect.internal.common.Document;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.internal.common.ManagerService;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.service.manager.interfaces.Manager;
import com.nuance.connect.service.manager.interfaces.MessageProcessor;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.VersionUtils;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class ConfigurationManager implements Manager, MessageProcessor {
    private static final String PROP_CORE_VERSION = "CORE_VERSION";
    private ConnectClient client;
    private int dependentCount;
    private static final String PROP_BUILD_PROPERTIES_FILTER = "BUILD_PROPERTIES_FILTER";
    private static final List<String> REQUIRED_PROPS = Arrays.asList(ConnectConfiguration.ConfigProperty.FOREGROUND_CONFIGURATION.name(), ConnectConfiguration.ConfigProperty.BACKGROUND_CONFIGURATION.name(), ConnectConfiguration.ConfigProperty.OEM_ID.name(), ConnectConfiguration.ConfigProperty.SDK_VERSION.name(), ConnectConfiguration.ConfigProperty.APPLICATION_ID.name(), ConnectConfiguration.ConfigProperty.CUSTOMER_STRING.name(), ConnectConfiguration.ConfigProperty.ANONYMOUS_BUILD.name(), ConnectConfiguration.ConfigProperty.REPORTING_ALLOWED.name(), PROP_BUILD_PROPERTIES_FILTER);
    public static final String MANAGER_NAME = ManagerService.CONFIG.getName();
    private static final InternalMessages[] MESSAGES_HANDLED = {InternalMessages.MESSAGE_CLIENT_UPDATE_CURRENT_LANGUAGE, InternalMessages.MESSAGE_CLIENT_SET_BUILD_TYPE, InternalMessages.MESSAGE_CLIENT_SET_OEM_ID, InternalMessages.MESSAGE_CLIENT_SET_CORE_VERSIONS, InternalMessages.MESSAGE_CLIENT_SET_SDK_VERSION, InternalMessages.MESSAGE_CLIENT_SET_APPLICATION_ID, InternalMessages.MESSAGE_CLIENT_SET_CUSTOMER_STRING, InternalMessages.MESSAGE_CLIENT_UPDATE_CURRENT_LOCALE, InternalMessages.MESSAGE_CLIENT_SET_CONNECTION_LIMIT, InternalMessages.MESSAGE_CLIENT_SET_NETWORK_IDLE_TIMEOUT, InternalMessages.MESSAGE_CLIENT_SET_BACKGROUND_DATA, InternalMessages.MESSAGE_CLIENT_SET_FOREGROUND_DATA, InternalMessages.MESSAGE_CLIENT_SET_POLLING_FREQUENCY, InternalMessages.MESSAGE_HOST_GET_COLLECT_USER_PROPERTIES, InternalMessages.MESSAGE_CLIENT_SET_ANONYMOUS_BUILD, InternalMessages.MESSAGE_CLIENT_SET_REPORTING_ALLOWED, InternalMessages.MESSAGE_CLIENT_SET_BUILD_PROPERTIES_FILTER, InternalMessages.MESSAGE_CLIENT_SET_PLATFORM_UPDATE_ENABLED, InternalMessages.MESSAGE_CLIENT_SET_CUSTOM_CONFIGURATION, InternalMessages.MESSAGE_CLIENT_UPDATE_FEATURE_USED_LAST};
    private AbstractCommandManager.ManagerState state = AbstractCommandManager.ManagerState.DISABLED;
    private final HashMap<String, Boolean> properties = new HashMap<>();
    private GenericProperty.BooleanProperty idleProperty = new GenericProperty.BooleanProperty(AbstractCommandManager.class.getSimpleName(), true);
    private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());

    public ConfigurationManager(ConnectClient connectClient) {
        this.client = connectClient;
    }

    private boolean isPropertyReceived(String str) {
        return this.properties.keySet().contains(str);
    }

    private void managerStartComplete() {
        this.log.i("managerStartComplete()");
        this.state = AbstractCommandManager.ManagerState.STARTED;
        this.client.managerStartComplete(MANAGER_NAME);
    }

    private void preloadProperty(String str) {
        ConnectConfiguration.ConfigProperty valueOf = ConnectConfiguration.ConfigProperty.valueOf(str);
        if (valueOf == null || this.client.getString(valueOf) == null) {
            return;
        }
        propertyReceived(str);
    }

    private void propertyReceived(String str) {
        this.properties.put(str, true);
        ArrayList arrayList = new ArrayList(REQUIRED_PROPS);
        arrayList.removeAll(this.properties.keySet());
        this.log.i("property: " + str + " left: " + arrayList.size() + "; " + arrayList);
        if (REQUIRED_PROPS != null && this.properties.keySet().containsAll(REQUIRED_PROPS) && getManagerStartState().equals(AbstractCommandManager.ManagerState.STARTING)) {
            managerStartComplete();
        }
    }

    private void sendPropertyRequests() {
        if (!isPropertyReceived(ConnectConfiguration.ConfigProperty.BACKGROUND_CONFIGURATION.name())) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_BACKGROUND_DATA);
        }
        if (!isPropertyReceived(ConnectConfiguration.ConfigProperty.FOREGROUND_CONFIGURATION.name())) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_FOREGROUND_DATA);
        }
        if (!isPropertyReceived(PROP_CORE_VERSION)) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_CORE_VERSIONS);
        }
        if (!isPropertyReceived(ConnectConfiguration.ConfigProperty.SDK_VERSION.name())) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_SDK_VERSION);
        }
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_LANGUAGE_INFO);
        if (!isPropertyReceived(ConnectConfiguration.ConfigProperty.CONNECTION_LIMIT.name())) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_CONNECTION_LIMIT);
        }
        if (!isPropertyReceived(ConnectConfiguration.ConfigProperty.APPLICATION_ID.name())) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_APPLICATION_ID);
        }
        if (!isPropertyReceived(ConnectConfiguration.ConfigProperty.CUSTOMER_STRING.name())) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_CUSTOMER_STRING);
        }
        if (!isPropertyReceived(ConnectConfiguration.ConfigProperty.NETWORK_TIMEOUT.name())) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_NETWORK_IDLE_TIMEOUT);
        }
        if (!isPropertyReceived(ConnectConfiguration.ConfigProperty.COLLECT_USER_PROPERTIES.name())) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_COLLECT_USER_PROPERTIES);
        }
        if (!isPropertyReceived(ConnectConfiguration.ConfigProperty.ANONYMOUS_BUILD.name())) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_ANONYMOUS_BUILD);
        }
        if (!isPropertyReceived(PROP_BUILD_PROPERTIES_FILTER)) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_BUILD_PROPERTIES_FILTER);
        }
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_CUSTOMER_SETTINGS);
        if (!isPropertyReceived(ConnectConfiguration.ConfigProperty.REPORTING_ALLOWED.name())) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_REPORTING_ALLOWED);
        }
        if (isPropertyReceived(ConnectConfiguration.ConfigProperty.PLATFORM_UPDATE_ENABLED.name())) {
            return;
        }
        this.log.d(ConnectConfiguration.ConfigProperty.PLATFORM_UPDATE_ENABLED.name(), " has not been received, sending request");
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_PLATFORM_UPDATE_ENABLED);
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void deregister() {
        this.state = AbstractCommandManager.ManagerState.DISABLED;
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void destroy() {
        this.client = null;
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public String[] getDependencies() {
        ArrayList arrayList = new ArrayList();
        for (ManagerService managerService : ManagerService.CONFIG.values()) {
            arrayList.add(managerService.getName());
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public int getDependentCount() {
        return this.dependentCount;
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public GenericProperty.BooleanProperty getIdleProperty() {
        return this.idleProperty;
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public String getManagerName() {
        return MANAGER_NAME;
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public int getManagerPollInterval() {
        return this.client.getInteger(ConnectConfiguration.ConfigProperty.DEFAULT_POLLING_INTERVAL_NO_FEATURES).intValue();
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public AbstractCommandManager.ManagerState getManagerStartState() {
        return this.state;
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public int[] getMessageIDs() {
        int[] iArr = new int[MESSAGES_HANDLED.length];
        for (int i = 0; i < MESSAGES_HANDLED.length; i++) {
            iArr[i] = MESSAGES_HANDLED[i].ordinal();
        }
        return iArr;
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void incrementDependentCount() {
        this.dependentCount++;
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void init() {
        ServiceInitializationConfig serviceInitializationConfig = this.client.getServiceInitializationConfig();
        if (serviceInitializationConfig != null) {
            preloadProperty(ConnectConfiguration.ConfigProperty.OEM_ID.name());
            if (!isPropertyReceived(ConnectConfiguration.ConfigProperty.OEM_ID.name()) && serviceInitializationConfig.getOemId() != null && serviceInitializationConfig.getOemId().length() > 0) {
                this.client.setProperty(ConnectConfiguration.ConfigProperty.OEM_ID, serviceInitializationConfig.getOemId());
                propertyReceived(ConnectConfiguration.ConfigProperty.OEM_ID.name());
            }
            if (serviceInitializationConfig.getVersion() != null) {
                this.client.setProperty(ConnectConfiguration.ConfigProperty.SDK_VERSION, serviceInitializationConfig.getVersion());
                propertyReceived(ConnectConfiguration.ConfigProperty.SDK_VERSION.name());
            }
            this.client.setProperty(ConnectConfiguration.ConfigProperty.PLATFORM_UPDATE_ENABLED, Boolean.valueOf(serviceInitializationConfig.isPlatformUpdateEnabled()));
            propertyReceived(ConnectConfiguration.ConfigProperty.PLATFORM_UPDATE_ENABLED.name());
            this.client.setProperty(ConnectConfiguration.ConfigProperty.REPORTING_ALLOWED, Boolean.valueOf(serviceInitializationConfig.isReportingAllowed()));
            propertyReceived(ConnectConfiguration.ConfigProperty.REPORTING_ALLOWED.name());
            if (serviceInitializationConfig.getApplicationId() != null) {
                this.client.setProperty(ConnectConfiguration.ConfigProperty.APPLICATION_ID, serviceInitializationConfig.getApplicationId());
                propertyReceived(ConnectConfiguration.ConfigProperty.APPLICATION_ID.name());
            }
            this.client.setProperty(ConnectConfiguration.ConfigProperty.CUSTOMER_STRING, serviceInitializationConfig.getCustomerString());
            propertyReceived(ConnectConfiguration.ConfigProperty.CUSTOMER_STRING.name());
            if (serviceInitializationConfig.getForeGroundData() != null) {
                this.client.setProperty(ConnectConfiguration.ConfigProperty.FOREGROUND_CONFIGURATION, serviceInitializationConfig.getForeGroundData());
                propertyReceived(ConnectConfiguration.ConfigProperty.FOREGROUND_CONFIGURATION.name());
            }
            if (serviceInitializationConfig.getForeGroundData() != null) {
                this.client.setProperty(ConnectConfiguration.ConfigProperty.BACKGROUND_CONFIGURATION, serviceInitializationConfig.getBackgroundData());
                propertyReceived(ConnectConfiguration.ConfigProperty.BACKGROUND_CONFIGURATION.name());
            }
            this.client.setProperty(ConnectConfiguration.ConfigProperty.ANONYMOUS_BUILD, Boolean.valueOf(serviceInitializationConfig.isAnonymousBuild()));
            propertyReceived(ConnectConfiguration.ConfigProperty.ANONYMOUS_BUILD.name());
            this.client.setProperty(ConnectConfiguration.ConfigProperty.BUILD_PROPERTIES_FILTER_BLOCK, serviceInitializationConfig.getBuildPropertiesFilter());
            this.client.setProperty(ConnectConfiguration.ConfigProperty.BUILD_PROPERTIES_FILTER_PRE_TOS, serviceInitializationConfig.getBuildPropertiesPreTosFilter());
            propertyReceived(PROP_BUILD_PROPERTIES_FILTER);
            this.client.setProperty(ConnectConfiguration.ConfigProperty.CORE_VERSION_ALPHA, serviceInitializationConfig.getCoreVersionAlpha());
            this.client.setProperty(ConnectConfiguration.ConfigProperty.CORE_VERSION_CHINESE, serviceInitializationConfig.getCoreVersionChinese());
            this.client.setProperty(ConnectConfiguration.ConfigProperty.CORE_VERSION_JAPANESE, serviceInitializationConfig.getCoreVersionJapanese());
            this.client.setProperty(ConnectConfiguration.ConfigProperty.CORE_VERSION_KOREAN, serviceInitializationConfig.getCoreVersionKorean());
            propertyReceived(PROP_CORE_VERSION);
            this.client.setProperty(ConnectConfiguration.ConfigProperty.CONNECTION_LIMIT, Integer.valueOf(serviceInitializationConfig.getConnectionLimit()));
            propertyReceived(ConnectConfiguration.ConfigProperty.CONNECTION_LIMIT.name());
            this.client.setProperty(ConnectConfiguration.ConfigProperty.NETWORK_TIMEOUT, Integer.valueOf(serviceInitializationConfig.getNetworkTimeout()));
            propertyReceived(ConnectConfiguration.ConfigProperty.NETWORK_TIMEOUT.name());
            this.client.setProperty(ConnectConfiguration.ConfigProperty.COLLECT_USER_PROPERTIES, Boolean.valueOf(serviceInitializationConfig.isCollectUserProperties()));
            propertyReceived(ConnectConfiguration.ConfigProperty.COLLECT_USER_PROPERTIES.name());
            this.client.setProperty(ConnectConfiguration.ConfigProperty.MINIMUM_SSL_PROTOCOL, serviceInitializationConfig.getMinimumSSLProtocol());
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_CLIENT_UPDATE_CURRENT_LANGUAGE:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_UPDATE_CURRENT_LANGUAGE)");
                int[] intArray = message.getData().getIntArray(Strings.DEFAULT_KEY);
                this.log.d("onHandleMessage(MESSAGE_CLIENT_UPDATE_CURRENT_LANGUAGE) Language: [" + Arrays.toString(intArray) + "]");
                if (intArray != null) {
                    this.client.setCurrentLanguageInfo(intArray);
                }
                return true;
            case MESSAGE_CLIENT_UPDATE_CURRENT_LOCALE:
                String string = message.getData().getString(Strings.DEFAULT_KEY);
                this.log.d("onHandleMessage(MESSAGE_CLIENT_UPDATE_CURRENT_LOCALE) Locale: [" + string + "]");
                if (string != null && string.length() != 0) {
                    String[] split = string.split(Document.ID_SEPARATOR);
                    Locale locale = Locale.getDefault();
                    if (split.length == 1) {
                        locale = new Locale(split[0]);
                    } else if (split.length == 2) {
                        locale = new Locale(split[0], split[1]);
                    } else if (split.length == 3) {
                        locale = new Locale(split[0], split[1], split[2]);
                    }
                    this.client.setCurrentLocaleInfo(locale);
                }
                return true;
            case MESSAGE_CLIENT_SET_CONNECTION_LIMIT:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_CONNECTION_LIMIT)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.CONNECTION_LIMIT, Integer.valueOf(message.getData().getInt(Strings.DEFAULT_KEY)));
                return true;
            case MESSAGE_CLIENT_SET_BUILD_TYPE:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_BUILD_TYPE)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.BUILD_TYPE, message.getData().getString(Strings.DEFAULT_KEY));
                propertyReceived(ConnectConfiguration.ConfigProperty.BUILD_TYPE.name());
                return true;
            case MESSAGE_CLIENT_SET_OEM_ID:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_OEM_ID)");
                String string2 = message.getData().getString(Strings.DEFAULT_KEY);
                if (string2 == null || string2.length() == 0) {
                    this.log.e("onHandleMessage(MESSAGE_CLIENT_SET_OEM_ID) invalid OEM");
                    this.client.sendMessageToHostDelayed(InternalMessages.MESSAGE_HOST_GET_OEM_ID, ConnectClient.DEFAULT_MESSAGE_DELAY);
                } else {
                    this.client.setProperty(ConnectConfiguration.ConfigProperty.OEM_ID, string2);
                    propertyReceived(ConnectConfiguration.ConfigProperty.OEM_ID.name());
                    sendPropertyRequests();
                }
                return true;
            case MESSAGE_CLIENT_SET_CORE_VERSIONS:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_CORE_VERSIONS) ", message.getData().getString(Strings.PROP_CORE_ALPHA), XMLResultsHandler.SEP_SPACE, message.getData().getString(Strings.PROP_CORE_CHINESE));
                this.client.setProperty(ConnectConfiguration.ConfigProperty.CORE_VERSION_ALPHA, message.getData().getString(Strings.PROP_CORE_ALPHA));
                this.client.setProperty(ConnectConfiguration.ConfigProperty.CORE_VERSION_CHINESE, message.getData().getString(Strings.PROP_CORE_CHINESE));
                this.client.setProperty(ConnectConfiguration.ConfigProperty.CORE_VERSION_JAPANESE, message.getData().getString(Strings.PROP_CORE_JAPANESE));
                this.client.setProperty(ConnectConfiguration.ConfigProperty.CORE_VERSION_KOREAN, message.getData().getString(Strings.PROP_CORE_KOREAN));
                return true;
            case MESSAGE_CLIENT_SET_APPLICATION_ID:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_APPLICATION_ID)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.APPLICATION_ID, message.getData().getString(Strings.DEFAULT_KEY));
                propertyReceived(ConnectConfiguration.ConfigProperty.APPLICATION_ID.name());
                return true;
            case MESSAGE_CLIENT_SET_SDK_VERSION:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_SDK_VERSION)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.SDK_VERSION, message.getData().getString(Strings.DEFAULT_KEY));
                propertyReceived(ConnectConfiguration.ConfigProperty.SDK_VERSION.name());
                return true;
            case MESSAGE_CLIENT_SET_CUSTOMER_STRING:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_CUSTOMER_STRING)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.CUSTOMER_STRING, message.getData().getString(Strings.DEFAULT_KEY));
                propertyReceived(ConnectConfiguration.ConfigProperty.CUSTOMER_STRING.name());
                return true;
            case MESSAGE_CLIENT_SET_NETWORK_IDLE_TIMEOUT:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_NETWORK_IDLE_TIMEOUT)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.NETWORK_TIMEOUT, Integer.valueOf(message.getData().getInt(Strings.DEFAULT_KEY)));
                return true;
            case MESSAGE_CLIENT_SET_BACKGROUND_DATA:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_BACKGROUND_DATA)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.BACKGROUND_CONFIGURATION, message.getData().getString(Strings.DEFAULT_KEY));
                propertyReceived(ConnectConfiguration.ConfigProperty.BACKGROUND_CONFIGURATION.name());
                return true;
            case MESSAGE_CLIENT_SET_FOREGROUND_DATA:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_FOREGROUND_DATA)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.FOREGROUND_CONFIGURATION, message.getData().getString(Strings.DEFAULT_KEY));
                propertyReceived(ConnectConfiguration.ConfigProperty.FOREGROUND_CONFIGURATION.name());
                return true;
            case MESSAGE_CLIENT_SET_POLLING_FREQUENCY:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_POLLING_FREQUENCY)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.CUSTOMER_POLLING_FREQUENCY, Integer.valueOf(message.getData().getInt(Strings.DEFAULT_KEY)));
                return true;
            case MESSAGE_HOST_GET_COLLECT_USER_PROPERTIES:
                this.log.d("onHandleMessage(MESSAGE_HOST_GET_COLLECT_USER_PROPERTIES)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.COLLECT_USER_PROPERTIES, Boolean.valueOf(message.getData().getBoolean(Strings.DEFAULT_KEY)));
                return true;
            case MESSAGE_CLIENT_SET_ANONYMOUS_BUILD:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_ANONYMOUS_BUILD)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.ANONYMOUS_BUILD, Boolean.valueOf(message.getData().getBoolean(Strings.DEFAULT_KEY)));
                propertyReceived(ConnectConfiguration.ConfigProperty.ANONYMOUS_BUILD.name());
                return true;
            case MESSAGE_CLIENT_SET_BUILD_PROPERTIES_FILTER:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_BUILD_PROPERTIES_FILTER)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.BUILD_PROPERTIES_FILTER_BLOCK, message.getData().getString(Strings.MESSAGE_BUNDLE_KEY_BUILD_PROPERTIES_FILTER_BLOCK));
                this.client.setProperty(ConnectConfiguration.ConfigProperty.BUILD_PROPERTIES_FILTER_PRE_TOS, message.getData().getString(Strings.MESSAGE_BUNDLE_KEY_BUILD_PROPERTIES_FILTER_PRE_TOS));
                propertyReceived(PROP_BUILD_PROPERTIES_FILTER);
                return true;
            case MESSAGE_CLIENT_SET_REPORTING_ALLOWED:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_REPORTING_ALLOWED)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.REPORTING_ALLOWED, Boolean.valueOf(message.getData().getBoolean(Strings.DEFAULT_KEY)));
                propertyReceived(ConnectConfiguration.ConfigProperty.REPORTING_ALLOWED.name());
                return true;
            case MESSAGE_CLIENT_SET_PLATFORM_UPDATE_ENABLED:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_PLATFORM_UPDATE_ENABLED)");
                this.client.setProperty(ConnectConfiguration.ConfigProperty.PLATFORM_UPDATE_ENABLED, Boolean.valueOf(message.getData().getBoolean(Strings.DEFAULT_KEY)));
                propertyReceived(ConnectConfiguration.ConfigProperty.PLATFORM_UPDATE_ENABLED.name());
                return true;
            case MESSAGE_CLIENT_SET_CUSTOM_CONFIGURATION:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_SET_CUSTOM_CONFIGURATION)");
                String string3 = message.getData().getString(Strings.DEFAULT_KEY);
                this.client.getBuildProps().setCustomProperty(string3, message.getData().getString(string3), message.getData().getBoolean(Strings.IS_CRITICAL));
                this.client.idleSnooze();
                this.client.removeMessages(InternalMessages.MESSAGE_CLIENT_SEND_DEVICE_PROPERTIES);
                this.client.postMessageDelayed(InternalMessages.MESSAGE_CLIENT_SEND_DEVICE_PROPERTIES, 1000L);
                return true;
            case MESSAGE_CLIENT_UPDATE_FEATURE_USED_LAST:
                this.log.d("onHandleMessage(MESSAGE_CLIENT_UPDATE_FEATURE_USED_LAST)");
                String string4 = message.getData().getString(Strings.DEFAULT_KEY);
                FeaturesLastUsed featuresLastUsed = new FeaturesLastUsed(this.client.getConfiguration().getString(ConnectConfiguration.ConfigProperty.FEATURES_LAST_USED));
                featuresLastUsed.updateWithString(string4);
                this.client.getConfiguration().setProperty(ConnectConfiguration.ConfigProperty.FEATURES_LAST_USED, featuresLastUsed.toString());
                return true;
            default:
                return false;
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2, boolean z) {
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void postInit() {
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void postStart() {
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void rebind() {
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void restart() {
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void start() {
        this.log.i("start()");
        this.state = AbstractCommandManager.ManagerState.STARTING;
        if (isPropertyReceived(ConnectConfiguration.ConfigProperty.OEM_ID.name())) {
            sendPropertyRequests();
        } else {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_OEM_ID);
        }
        if (this.properties.keySet().containsAll(REQUIRED_PROPS)) {
            managerStartComplete();
        }
    }
}
