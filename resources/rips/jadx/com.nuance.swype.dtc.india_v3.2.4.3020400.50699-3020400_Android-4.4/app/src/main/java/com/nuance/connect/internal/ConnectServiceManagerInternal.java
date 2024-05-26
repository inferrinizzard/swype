package com.nuance.connect.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.SparseArray;
import com.nuance.connect.api.ConfigService;
import com.nuance.connect.api.ConnectServiceManager;
import com.nuance.connect.api.ConnectionCallback;
import com.nuance.connect.api.LocaleCallback;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.common.ServiceInitializationConfig;
import com.nuance.connect.common.Strings;
import com.nuance.connect.host.service.BuildSettings;
import com.nuance.connect.host.service.HostInterface;
import com.nuance.connect.internal.AppSettings;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.store.DataStoreFactory;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.system.NetworkState;
import com.nuance.connect.util.ActionDelegateCallback;
import com.nuance.connect.util.ActionFilter;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.ConnectAction;
import com.nuance.connect.util.InstallMetadata;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import com.nuance.swype.input.IME;
import com.nuance.swypeconnect.ac.ACBuildConfigRuntime;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class ConnectServiceManagerInternal implements ConnectServiceManager {
    private static final int MINIMUM_RESTART = 10000;
    private static final int RETRYCONNECTION_PROTECTION = 1200000;
    private static volatile ConnectServiceManagerInternal instance;
    private String appFilesFolder;
    private final AppSettings appSettings;
    private Context context;
    private NetworkState.NetworkConfiguration currentBackgroundConfiguration;
    private NetworkState.NetworkConfiguration currentForegroundConfiguration;
    private NetworkState currentNetworkState;
    private PersistentDataStore defaultStore;
    protected HostInterface hostInterface;
    private int minimumRefreshInterval;
    private boolean restartOnReconnect;
    private ClientBinderInternal serviceBinder;
    private final UserSettings userSettings;
    private static final String connectivityFilterSpec = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final IntentFilter connectivityFilter = new IntentFilter(connectivityFilterSpec);
    private static final IntentFilter localeFilter = new IntentFilter("android.intent.action.LOCALE_CHANGED");
    private final HashMap<ActionFilter, WeakReference<ActionDelegateCallback>> actionHandlers = new HashMap<>();
    private final HandlerRegistry handlerRegistry = new HandlerRegistry();
    private final ConcurrentHashMap<String, AbstractService> services = new ConcurrentHashMap<>();
    private final IncomingHandler mHandler = new IncomingHandler(this);
    private final Logger.Log devLog = Logger.getLog(Logger.LoggerType.DEVELOPER);
    private final Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM);
    private Handler refreshHandler = new Handler(Looper.getMainLooper());
    private long lastRefresh = Long.MIN_VALUE;
    private long lastRetry = Long.MIN_VALUE;
    private boolean started = false;
    private final ConcurrentCallbackSet<ConnectionCallback> connectionCallbacks = new ConcurrentCallbackSet<>();
    private final ConcurrentCallbackSet<LocaleCallback> localeCallbacks = new ConcurrentCallbackSet<>();
    private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ConnectServiceManagerInternal.class.getSimpleName());
    private boolean isLicensed = true;
    private BroadcastReceiver connectivityReceiver = new BroadcastReceiver() { // from class: com.nuance.connect.internal.ConnectServiceManagerInternal.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ConnectServiceManagerInternal.this.onConnectivityChanged();
        }
    };
    private boolean activeLocaleOverridden = false;
    private final BroadcastReceiver localeReceiver = new BroadcastReceiver() { // from class: com.nuance.connect.internal.ConnectServiceManagerInternal.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ConnectServiceManagerInternal.this.log.d("localeReceiver.onReceive()");
            synchronized (ConnectServiceManagerInternal.this.localeReceiver) {
                if (ConnectServiceManagerInternal.this.activeLocaleOverridden) {
                    ConnectServiceManagerInternal.this.log.d("localeReceiver.onReceive() ignoring");
                } else {
                    ConnectServiceManagerInternal.this.changeActiveLocale(Locale.getDefault());
                }
            }
        }
    };
    private InstallMetadata.MetaDataClient datamgr = new InstallMetadata.MetaDataClient() { // from class: com.nuance.connect.internal.ConnectServiceManagerInternal.3
        @Override // com.nuance.connect.util.InstallMetadata.MetaDataClient
        public PersistentDataStore getDataStore() {
            return ConnectServiceManagerInternal.this.defaultStore;
        }
    };
    private final FeaturesLastUsed featuresLastUsed = new FeaturesLastUsed("");
    private Runnable restart = new Runnable() { // from class: com.nuance.connect.internal.ConnectServiceManagerInternal.4
        @Override // java.lang.Runnable
        public void run() {
            ConnectServiceManagerInternal.this.oemLog.v("Rebinding SDK service now.");
            if (ConnectServiceManagerInternal.this.serviceBinder != null) {
                ConnectServiceManagerInternal.this.serviceBinder.restart();
            }
        }
    };
    private Runnable refresh = new Runnable() { // from class: com.nuance.connect.internal.ConnectServiceManagerInternal.5
        @Override // java.lang.Runnable
        public void run() {
            ConnectServiceManagerInternal.this.refresh(false);
        }
    };
    private Runnable retryConnection = new Runnable() { // from class: com.nuance.connect.internal.ConnectServiceManagerInternal.6
        @Override // java.lang.Runnable
        public void run() {
            ConnectServiceManagerInternal.this.retryConnection();
        }
    };
    private ClientBinderLifecycleCallback lifecycleCallback = new ClientBinderLifecycleCallback() { // from class: com.nuance.connect.internal.ConnectServiceManagerInternal.8
        @Override // com.nuance.connect.internal.ClientBinderLifecycleCallback
        public void onBound() {
            ConnectServiceManagerInternal.this.oemLog.v("SDK Service bound.");
            Bundle bundle = new Bundle();
            bundle.putString(Strings.HANDLER_LIST, ConnectServiceManagerInternal.this.handlerRegistry.getRegisteredHandlers());
            bundle.putString(Strings.SERVICES_LIST, StringUtils.listToString(new ArrayList(ConnectServiceManagerInternal.this.services.keySet()), ","));
            bundle.putInt(Strings.PROCESS_ID, Process.myPid());
            bundle.putParcelable(Strings.CONFIG_KEY, ConnectServiceManagerInternal.this.getConnectConfig());
            ConnectServiceManagerInternal.this.getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_REGISTER_CLIENT, bundle);
        }

        @Override // com.nuance.connect.internal.ClientBinderLifecycleCallback
        public void onUnbound() {
            ConnectServiceManagerInternal.this.oemLog.v("SDK Service unbound.");
            ConnectServiceManagerInternal.this.mHandler.stop();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class IncomingHandler extends Handler {
        private final WeakReference<ConnectServiceManagerInternal> connectRef;

        public IncomingHandler(ConnectServiceManagerInternal connectServiceManagerInternal) {
            super(Looper.getMainLooper());
            this.connectRef = new WeakReference<>(connectServiceManagerInternal);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            ConnectServiceManagerInternal connectServiceManagerInternal = this.connectRef.get();
            if (connectServiceManagerInternal != null) {
                connectServiceManagerInternal.handleMessage(this, message);
            }
        }

        public void stop() {
            for (int i = 0; i <= InternalMessages.values().length; i++) {
                removeMessages(i);
            }
        }
    }

    protected ConnectServiceManagerInternal(Context context, HostInterface hostInterface) {
        this.minimumRefreshInterval = ACBuildConfigRuntime.MINUMUM_REFRESH_INTERVAL;
        this.context = context;
        this.appFilesFolder = context.getFilesDir().getAbsolutePath();
        this.hostInterface = hostInterface;
        instance = this;
        BuildSettings buildSettings = (BuildSettings) hostInterface.getHostService(HostInterface.HostService.HOST_BUILD_SETTINGS);
        this.defaultStore = DataStoreFactory.getDataStore(context, "com.nuance.swype.connect.store.FilePreference", buildSettings.getLegacySecretKey());
        Logger.configure(buildSettings.isDeveloperLogEnabled(), buildSettings.getLogLevel(), Logger.OutputMode.ANDROID_LOG, null);
        this.userSettings = new UserSettings(this.defaultStore, buildSettings.getDlmSyncDefault(), buildSettings.getUpdateLivingLanguageByDefault(), buildSettings.getForegroundNetworkDefault(), buildSettings.getBackgroundNetworkDefault(), buildSettings.getLivingLanguageMaxEventsDefault());
        this.appSettings = new AppSettings(this.defaultStore, this);
        this.minimumRefreshInterval = buildSettings.getMinimumRefreshIntervalDefault();
        addService(new ConfigServiceInternal(this));
        addService(new DocumentServiceInternal(this));
        onConnectivityChanged();
        changeActiveLocale(Locale.getDefault());
        registerAppSettingsListener(AppSettings.Key.OEM_LOG_LEVEL.name(), new Property.IntegerValueListener() { // from class: com.nuance.connect.internal.ConnectServiceManagerInternal.7
            @Override // com.nuance.connect.internal.Property.ValueListener
            public void onValueChanged(Property<Integer> property) {
                if (ConnectServiceManagerInternal.this.getBinder().isBound()) {
                    ConnectServiceManagerInternal.this.sendConnectConfig();
                }
            }
        });
    }

    private void addService(AbstractService abstractService) {
        if (this.services.containsKey(abstractService.getServiceName())) {
            return;
        }
        for (ConnectFeature connectFeature : abstractService.getDependencies()) {
            if (!this.services.containsKey(connectFeature.name())) {
                getFeatureService(connectFeature);
            }
        }
        this.services.put(abstractService.getServiceName(), abstractService);
        registerServiceHandlers(abstractService.getHandlers());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeActiveLocale(Locale locale) {
        ((ConfigServiceInternal) ((ConfigService) getFeatureService(ConnectFeature.CONFIG))).setActiveLocale(locale);
        for (LocaleCallback localeCallback : (LocaleCallback[]) this.localeCallbacks.toArray(new LocaleCallback[0])) {
            localeCallback.onLocaleChange(locale);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ServiceInitializationConfig getConnectConfig() {
        BuildSettings buildSettings = (BuildSettings) this.hostInterface.getHostService(HostInterface.HostService.HOST_BUILD_SETTINGS);
        int min = Math.min(buildSettings.getLogLevel(), this.appSettings.getLogLevel());
        SparseArray<String> coreVersions = buildSettings.getCoreVersions();
        ServiceInitializationConfig.ServiceInitializationConfigBuilder serviceInitializationConfigBuilder = new ServiceInitializationConfig.ServiceInitializationConfigBuilder();
        serviceInitializationConfigBuilder.setOemId(buildSettings.getOemId()).setVersion(buildSettings.getVersion()).setApiServerUrl(buildSettings.getConnectUrl()).setLocationServerUrl(buildSettings.getLocationServiceServerUrl()).setLocationServiceLookupInterval(buildSettings.getLocationServiceLookupInterval()).setLocationServiceEnabled(buildSettings.isLocationServiceEnabled()).setLogLevel(min).setDeveloperLogEnabled(buildSettings.isDeveloperLogEnabled()).setForeGroundData(getUserSettings().getForegroundNetworkState() != null ? getUserSettings().getForegroundNetworkState().toString() : "").setBackgroundData(getUserSettings().getBackgroundNetworkState() != null ? getUserSettings().getBackgroundNetworkState().toString() : "").setApplicationId(buildSettings.getApplicationId()).setCustomerString(getAppSettings().getCustomerString()).setAnonymousBuild(buildSettings.isAnonymousBuild()).setReportingAllowed(getAppSettings().isReportingAllowed()).setBuildPropertiesFilter(buildSettings.getBuildPropertiesFilterBlock()).setBuildPropertiesPreTosFilter(buildSettings.getBuildPropertiesFilterPreTos()).setCoreVersionAlpha(coreVersions.get(1)).setCoreVersionChinese(coreVersions.get(3)).setCoreVersionJapanese(coreVersions.get(4)).setCoreVersionKorean(coreVersions.get(2)).setConnectionLimit(getAppSettings().getConnectionConcurrentLimit()).setNetworkTimeout(getAppSettings().getDownloadIdleTimeout()).setCollectUserProperties(buildSettings.collectUserProperties()).setPlatformUpdateEnabled(buildSettings.isPlatformUpdateEnabled()).setDefaultDelay(buildSettings.getDefaultDelay()).setRequiredLegalDocuments(buildSettings.getRequiredLegalDocuments()).setMinimumSSLProtocol(this.appSettings.getMinimumSSLProtocol()).setLegacySecretKey(buildSettings.getLegacySecretKey());
        return serviceInitializationConfigBuilder.build();
    }

    private AbstractService getService(ConnectFeature connectFeature) {
        return this.services.get(connectFeature.name());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMessage(Handler handler, Message message) {
        if (this.handlerRegistry.tryHandleMessage(handler, message)) {
            return;
        }
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_HOST_GET_HANDLERS:
                getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_HANDLERS, this.handlerRegistry.getRegisteredHandlers());
                return;
            case MESSAGE_HOST_CLIENT_START_COMPLETE:
                this.devLog.v("MESSAGE_HOST_CLIENT_START_COMPLETE");
                ClientBinder binder = getBinder();
                binder.setClientComplete();
                if (binder instanceof ClientBinderInternal) {
                    ((ClientBinderInternal) binder).processQueuedMessages();
                    return;
                }
                return;
            case MESSAGE_HOST_CONNECTION_STATUS:
                if (message.getData() != null) {
                    Bundle data = message.getData();
                    int i = data.getInt("status");
                    String string = data.getString("message");
                    this.oemLog.v("Connection status: ", Integer.valueOf(i), " message: ", string);
                    notifyConnectionStatus(i, string);
                    return;
                }
                return;
            case MESSAGE_HOST_ON_SERVICE_SHUTDOWN:
                synchronized (getBinder().getLock()) {
                    long j = message.getData().getLong(Strings.DEFAULT_KEY, 0L);
                    this.devLog.v("MESSAGE_HOST_ON_SERVICE_SHUTDOWN ", Long.valueOf(j));
                    if (j < 0) {
                        this.devLog.v("Restart on connection restore");
                        if (this.currentNetworkState != null && !this.currentNetworkState.hasConnectivity(this.currentForegroundConfiguration) && !this.currentNetworkState.hasConnectivity(this.currentBackgroundConfiguration)) {
                            this.restartOnReconnect = true;
                            getBinder().pause();
                        }
                    } else {
                        long currentTimeMillis = j - System.currentTimeMillis();
                        long currentTimeMillis2 = System.currentTimeMillis() - getBinder().getLastMessageSent();
                        this.devLog.v("Restart time from now in millis: ", Long.valueOf(currentTimeMillis), " last sent time: ", Long.valueOf(currentTimeMillis2));
                        if (currentTimeMillis <= IME.RETRY_DELAY_IN_MILLIS || currentTimeMillis2 <= IME.RETRY_DELAY_IN_MILLIS) {
                            this.devLog.v("Ignoring shutdown because it's too soon");
                        } else {
                            getBinder().pause();
                            handler.removeCallbacks(this.restart);
                            handler.postDelayed(this.restart, currentTimeMillis);
                        }
                    }
                }
                return;
            case MESSAGE_HOST_POLL_REQUESTED:
                notifyConnectionStatus(11, "");
                return;
            case MESSAGE_HOST_POLL_COMPLETE:
                boolean z = message.getData().getBoolean(Strings.DEFAULT_KEY, false);
                this.lastRefresh = z ? System.currentTimeMillis() : Long.MIN_VALUE;
                if (z || !message.getData().containsKey("DEFAULT_DELAY")) {
                    notifyConnectionStatus(z ? 10 : 16, "");
                    return;
                }
                long seconds = TimeUnit.MILLISECONDS.toSeconds(message.getData().getLong("DEFAULT_DELAY") - System.currentTimeMillis());
                this.oemLog.v("refresh delayed: ", Long.valueOf(seconds), " seconds");
                notifyConnectionStatus(12, "Delayed for " + seconds + " seconds");
                return;
            case MESSAGE_HOST_LICENSED_BUILD:
                this.isLicensed = message.getData().getBoolean(Strings.DEFAULT_KEY);
                this.oemLog.d("Licensing has changed. Is licensed: ", Boolean.valueOf(this.isLicensed));
                if (this.isLicensed) {
                    notifyConnectionStatus(15, "Build has been licensed.");
                    return;
                } else {
                    notifyConnectionStatus(14, "Build is unlicensed.");
                    return;
                }
            case MESSAGE_HOST_GET_ANONYMOUS_BUILD:
                getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_ANONYMOUS_BUILD, Boolean.valueOf(((BuildSettings) this.hostInterface.getHostService(HostInterface.HostService.HOST_BUILD_SETTINGS)).isAnonymousBuild()));
                return;
            default:
                return;
        }
    }

    public static ConnectServiceManagerInternal make(Context context, HostInterface hostInterface) {
        synchronized (ConnectServiceManagerInternal.class) {
            if (instance == null) {
                instance = new ConnectServiceManagerInternal(context, hostInterface);
            }
        }
        return instance;
    }

    private void registerServiceHandlers(ConnectHandler[] connectHandlerArr) {
        for (ConnectHandler connectHandler : connectHandlerArr) {
            if (!this.handlerRegistry.hasHandler(connectHandler.getHandlerName())) {
                this.handlerRegistry.add(connectHandler);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendConnectConfig() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Strings.CONFIG_KEY, getConnectConfig());
        getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_UPDATE_CONFIGURATION, bundle);
    }

    private void sendConnectionStatusNotification(NetworkState networkState, int i, boolean z, boolean z2) {
        int i2 = 2;
        NetworkState.NetworkConfiguration foregroundNetworkState = this.userSettings.getForegroundNetworkState();
        NetworkState.NetworkConfiguration backgroundNetworkState = this.userSettings.getBackgroundNetworkState();
        int i3 = (networkState.hasConnectivity(backgroundNetworkState) && networkState.hasConnectivity(foregroundNetworkState)) ? 3 : (z2 && networkState.hasConnectivity(backgroundNetworkState)) ? 2 : (z && networkState.hasConnectivity(foregroundNetworkState)) ? 1 : 0;
        if (!networkState.hasConnectivity(backgroundNetworkState) && !networkState.hasConnectivity(foregroundNetworkState)) {
            i2 = 3;
        } else if (!z2 || networkState.hasConnectivity(backgroundNetworkState)) {
            i2 = (!z || networkState.hasConnectivity(foregroundNetworkState)) ? 0 : 1;
        }
        for (ConnectionCallback connectionCallback : (ConnectionCallback[]) this.connectionCallbacks.toArray(new ConnectionCallback[0])) {
            if (i3 > 0) {
                connectionCallback.onConnected(i, i3);
            }
            if (i2 > 0) {
                connectionCallback.onDisconnected(i2);
            }
        }
    }

    private void sendCurrentConnectionStatus(ConnectionCallback connectionCallback) {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService("connectivity");
        NetworkState.NetworkConfiguration foregroundNetworkState = this.userSettings.getForegroundNetworkState();
        NetworkState.NetworkConfiguration backgroundNetworkState = this.userSettings.getBackgroundNetworkState();
        int i = (this.currentNetworkState.hasConnectivity(backgroundNetworkState) && this.currentNetworkState.hasConnectivity(foregroundNetworkState)) ? 3 : this.currentNetworkState.hasConnectivity(backgroundNetworkState) ? 2 : this.currentNetworkState.hasConnectivity(foregroundNetworkState) ? 1 : 0;
        int i2 = (this.currentNetworkState.hasConnectivity(backgroundNetworkState) || this.currentNetworkState.hasConnectivity(foregroundNetworkState)) ? !this.currentNetworkState.hasConnectivity(backgroundNetworkState) ? 2 : !this.currentNetworkState.hasConnectivity(foregroundNetworkState) ? 1 : 0 : 3;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        int type = activeNetworkInfo != null ? activeNetworkInfo.getType() : -1;
        if (i > 0) {
            connectionCallback.onConnected(type, i);
        }
        if (i2 > 0) {
            connectionCallback.onDisconnected(i2);
        }
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public Bundle dispatchAction(ConnectAction connectAction) {
        Bundle bundle;
        ActionDelegateCallback actionDelegateCallback;
        Bundle bundle2 = new Bundle();
        Iterator<Map.Entry<ActionFilter, WeakReference<ActionDelegateCallback>>> it = this.actionHandlers.entrySet().iterator();
        while (true) {
            bundle = bundle2;
            if (!it.hasNext()) {
                break;
            }
            Map.Entry<ActionFilter, WeakReference<ActionDelegateCallback>> next = it.next();
            ActionFilter key = next.getKey();
            if (key.matches(connectAction.getFilter()) && (actionDelegateCallback = next.getValue().get()) != null) {
                bundle = actionDelegateCallback.handle(connectAction);
                if (key.isOneShot()) {
                    break;
                }
            }
            bundle2 = bundle;
        }
        return bundle;
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public Bundle dispatchAction(String str, String str2) {
        return dispatchAction(new ConnectAction(new ActionFilter(str, str2), new Bundle()));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getAppFilesFolder() {
        return this.appFilesFolder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppSettings getAppSettings() {
        return this.appSettings;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ClientBinder getBinder() {
        if (this.serviceBinder == null) {
            this.serviceBinder = new ClientBinderInternal(this.context, this, this.mHandler, this.lifecycleCallback);
        }
        return this.serviceBinder;
    }

    public Context getContext() {
        return this.context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public InstallMetadata.MetaDataClient getDataManager() {
        return this.datamgr;
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public PersistentDataStore getDataStore() {
        return this.defaultStore;
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public Object getFeatureService(ConnectFeature connectFeature) throws IllegalArgumentException {
        switch (connectFeature) {
            case LANGUAGE:
                if (!this.services.containsKey(connectFeature.name())) {
                    this.oemLog.d("Loading service: ", ConnectFeature.LANGUAGE.name());
                    addService(new LanguageServiceInternal(this));
                }
                return getService(connectFeature);
            case CONFIG:
                if (!this.services.containsKey(connectFeature.name())) {
                    this.oemLog.d("Loading service: ", ConnectFeature.CONFIG.name());
                    addService(new ConfigServiceInternal(this));
                }
                return getService(connectFeature);
            case REPORTING:
                if (!this.services.containsKey(connectFeature.name())) {
                    this.oemLog.d("Loading service: ", ConnectFeature.REPORTING.name());
                    addService(new ReportingServiceInternal(this));
                }
                return getService(connectFeature);
            case ACCOUNT:
                if (!this.services.containsKey(connectFeature.name())) {
                    this.oemLog.d("Loading service: ", ConnectFeature.ACCOUNT.name());
                    addService(new AccountServiceInternal(this));
                }
                return getService(connectFeature);
            case DLM:
                if (!this.services.containsKey(connectFeature.name())) {
                    this.oemLog.d("Loading service: ", ConnectFeature.DLM.name());
                    addService(new DLMConnectorInternal(this));
                }
                return getService(connectFeature);
            case CATEGORY:
                if (!this.services.containsKey(connectFeature.name())) {
                    this.oemLog.d("Loading service: ", ConnectFeature.CATEGORY.name());
                    addService(new CategoryServiceInternal(this));
                }
                return getService(connectFeature);
            case ADDON_DICTIONARIES:
                return getFeatureService(ConnectFeature.CATEGORY);
            case LIVING_LANGUAGE:
                return getFeatureService(ConnectFeature.CATEGORY);
            case SYNC:
                if (!this.services.containsKey(connectFeature.name())) {
                    this.oemLog.d("Loading service: ", ConnectFeature.SYNC.name());
                    addService(new SyncServiceInternal(this));
                }
                return getService(connectFeature);
            case DOCUMENTS:
                if (!this.services.containsKey(connectFeature.name())) {
                    this.oemLog.d("Loading service: ", ConnectFeature.DOCUMENTS.name());
                    addService(new DocumentServiceInternal(this));
                }
                return getService(connectFeature);
            case UPDATE:
                if (!this.services.containsKey(connectFeature.name())) {
                    this.oemLog.d("Loading service: ", ConnectFeature.UPDATE.name());
                    addService(new PlatformUpdateServiceInternal(this));
                }
                return getService(connectFeature);
            case CHINESE_PREDICTION:
                if (!this.services.containsKey(connectFeature.name())) {
                    this.oemLog.d("Loading service: ", ConnectFeature.CHINESE_PREDICTION.name());
                    addService(new ChinesePredictionServiceInternal(this));
                }
                return getService(connectFeature);
            case CATALOG:
                return getFeatureService(ConnectFeature.CATEGORY);
            default:
                throw new IllegalArgumentException();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Object getHostService(HostInterface.HostService hostService) {
        return this.hostInterface.getHostService(hostService);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Handler getIncomingHandler() {
        return this.mHandler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getMinimumRefreshInterval() {
        return this.minimumRefreshInterval;
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public UserSettings getUserSettings() {
        return this.userSettings;
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public boolean isLicensed() {
        return this.isLicensed;
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void notifyConnectionStatus(int i, String str) {
        for (ConnectionCallback connectionCallback : (ConnectionCallback[]) this.connectionCallbacks.toArray(new ConnectionCallback[0])) {
            connectionCallback.onConnectionStatus(i, str);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onConnectivityChanged() {
        int i;
        NetworkState networkState;
        boolean z = true;
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService("connectivity");
        NetworkState networkState2 = new NetworkState();
        NetworkState.NetworkConfiguration foregroundNetworkState = this.userSettings.getForegroundNetworkState();
        NetworkState.NetworkConfiguration backgroundNetworkState = this.userSettings.getBackgroundNetworkState();
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            i = -1;
            networkState = networkState2;
        } else {
            i = activeNetworkInfo.getType();
            networkState = new NetworkState(activeNetworkInfo, System.currentTimeMillis());
        }
        if (this.currentNetworkState == null || !this.currentNetworkState.isSame(networkState) || this.currentForegroundConfiguration == null || this.currentBackgroundConfiguration == null || !this.currentForegroundConfiguration.toString().equals(foregroundNetworkState.toString()) || !this.currentBackgroundConfiguration.toString().equals(backgroundNetworkState.toString())) {
            boolean z2 = this.currentNetworkState == null || this.currentNetworkState.hasConnectivity(this.currentBackgroundConfiguration) != networkState.hasConnectivity(backgroundNetworkState);
            if (this.currentNetworkState != null && this.currentNetworkState.hasConnectivity(this.currentForegroundConfiguration) == networkState.hasConnectivity(foregroundNetworkState)) {
                z = false;
            }
            this.currentNetworkState = networkState;
            this.currentForegroundConfiguration = foregroundNetworkState;
            this.currentBackgroundConfiguration = backgroundNetworkState;
            if (z2 || z) {
                sendConnectionStatusNotification(networkState, i, z, z2);
            }
            if (this.restartOnReconnect) {
                this.restartOnReconnect = false;
                if (z2 && this.currentNetworkState.hasConnectivity(this.currentBackgroundConfiguration)) {
                    this.restart.run();
                } else if (z && this.currentNetworkState.hasConnectivity(this.currentForegroundConfiguration)) {
                    this.restart.run();
                }
            }
        }
        this.oemLog.d("Connectivity Changed ", "\nBackgroundhasConnectivity: ", Boolean.valueOf(networkState.hasConnectivity(backgroundNetworkState)), "\nForegroundhasConnectivity: ", Boolean.valueOf(networkState.hasConnectivity(foregroundNetworkState)), "\nType: ", Integer.valueOf(i));
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void onUpgrade(String str, String str2) {
        notifyConnectionStatus(17, "Version Upgrade Detected");
        Bundle bundle = new Bundle();
        bundle.putString(Strings.VERSION_FROM, str);
        bundle.putString(Strings.VERSION_TO, str2);
        bundle.putString(Strings.DEFAULT_KEY, ((BuildSettings) this.hostInterface.getHostService(HostInterface.HostService.HOST_BUILD_SETTINGS)).getOemId());
        getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_HOST_ON_UPGRADE, bundle);
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void refresh(boolean z) {
        if (z || this.lastRefresh + (getMinimumRefreshInterval() * 1000) < System.currentTimeMillis()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Strings.DEFAULT_KEY, true);
            bundle.putBoolean(Strings.ACKNOWLEDGEMENT, true);
            getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_MANUAL_POLL, bundle);
            this.lastRefresh = System.currentTimeMillis();
            return;
        }
        int minimumRefreshInterval = (int) ((getMinimumRefreshInterval() * 1000) - (System.currentTimeMillis() - this.lastRefresh));
        this.oemLog.v("refresh delayed: ", Integer.valueOf(minimumRefreshInterval), " milliseconds");
        this.refreshHandler.removeCallbacks(this.refresh);
        notifyConnectionStatus(12, "Delayed for " + minimumRefreshInterval + " milliseconds");
        this.refreshHandler.postDelayed(this.refresh, (long) minimumRefreshInterval);
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void registerActionCallback(ActionDelegateCallback actionDelegateCallback, ActionFilter actionFilter) {
        this.actionHandlers.put(actionFilter, new WeakReference<>(actionDelegateCallback));
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void registerActionCallback(ActionDelegateCallback actionDelegateCallback, List<ActionFilter> list) {
        Iterator<ActionFilter> it = list.iterator();
        while (it.hasNext()) {
            this.actionHandlers.put(it.next(), new WeakReference<>(actionDelegateCallback));
        }
    }

    public void registerAppSettingsListener(String str, Property.ValueListener<?> valueListener) {
        this.appSettings.registerSettingsListener(str, valueListener);
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void registerConnectionCallback(ConnectionCallback connectionCallback) {
        this.connectionCallbacks.add(connectionCallback);
        sendCurrentConnectionStatus(connectionCallback);
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void registerLocaleCallback(LocaleCallback localeCallback) {
        this.localeCallbacks.add(localeCallback);
    }

    public void registerUserSettingsListener(String str, Property.ValueListener<?> valueListener) {
        this.userSettings.registerUserSettingsListener(str, valueListener);
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void retryConnection() {
        if (this.lastRetry + 1200000 < System.currentTimeMillis()) {
            getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_RETRY_CONNECTION, true);
            this.lastRetry = System.currentTimeMillis();
        } else {
            int currentTimeMillis = (int) (1200000 - (System.currentTimeMillis() - this.lastRetry));
            this.oemLog.v("retryConnection() delayed: ", Long.valueOf(this.lastRetry), " milliseconds");
            this.refreshHandler.removeCallbacks(this.retryConnection);
            this.refreshHandler.postDelayed(this.retryConnection, currentTimeMillis);
        }
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void sendConnectionStatus() {
        int i;
        NetworkState networkState;
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService("connectivity");
        NetworkState networkState2 = new NetworkState();
        NetworkState.NetworkConfiguration foregroundNetworkState = this.userSettings.getForegroundNetworkState();
        NetworkState.NetworkConfiguration backgroundNetworkState = this.userSettings.getBackgroundNetworkState();
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            i = -1;
            networkState = networkState2;
        } else {
            i = activeNetworkInfo.getType();
            networkState = (NetworkState.isValidConnection(activeNetworkInfo, foregroundNetworkState) || NetworkState.isValidConnection(activeNetworkInfo, backgroundNetworkState)) ? new NetworkState(activeNetworkInfo, System.currentTimeMillis()) : networkState2;
        }
        sendConnectionStatusNotification(networkState, i, true, true);
    }

    public void sendCustomProperty(String str, String str2, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putString(Strings.DEFAULT_KEY, str);
        bundle.putString(str, str2);
        bundle.putBoolean(Strings.IS_CRITICAL, z);
        getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_CUSTOM_CONFIGURATION, bundle);
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void setActiveLocale(Locale locale) {
        this.log.d("setActiveLocale(", locale, ")");
        synchronized (this.localeReceiver) {
            if (!this.activeLocaleOverridden) {
                try {
                    this.context.unregisterReceiver(this.localeReceiver);
                } catch (Exception e) {
                    this.log.d("Unregistered localeReceiver twice? " + e.getMessage());
                }
            }
            this.activeLocaleOverridden = true;
        }
        changeActiveLocale(locale);
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void setHostInterface(HostInterface hostInterface) {
        if (hostInterface != null) {
            this.hostInterface = hostInterface;
        }
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void start() {
        this.started = true;
        this.context.registerReceiver(this.connectivityReceiver, connectivityFilter);
        synchronized (this.localeReceiver) {
            if (!this.activeLocaleOverridden) {
                this.context.registerReceiver(this.localeReceiver, localeFilter);
            }
        }
        getBinder().start();
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void stop() {
        this.started = false;
        getBinder().stop();
        this.context.unregisterReceiver(this.connectivityReceiver);
        synchronized (this.localeReceiver) {
            if (!this.activeLocaleOverridden) {
                this.context.unregisterReceiver(this.localeReceiver);
            }
        }
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void unregisterActionCallback(ActionDelegateCallback actionDelegateCallback) {
        Iterator<Map.Entry<ActionFilter, WeakReference<ActionDelegateCallback>>> it = this.actionHandlers.entrySet().iterator();
        while (it.hasNext()) {
            ActionDelegateCallback actionDelegateCallback2 = it.next().getValue().get();
            if (actionDelegateCallback2 == null) {
                it.remove();
            } else if (actionDelegateCallback2.equals(actionDelegateCallback)) {
                it.remove();
            }
        }
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void unregisterConnectionCallback(ConnectionCallback connectionCallback) {
        this.connectionCallbacks.remove(connectionCallback);
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void unregisterLocaleCallback(LocaleCallback localeCallback) {
        this.localeCallbacks.remove(localeCallback);
    }

    @Override // com.nuance.connect.api.ConnectServiceManager
    public void updateFeatureLastUsed(FeaturesLastUsed.Feature feature, long j) {
        this.featuresLastUsed.setLastUsed(feature, j);
        getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_UPDATE_FEATURE_USED_LAST, this.featuresLastUsed.toString());
    }
}
