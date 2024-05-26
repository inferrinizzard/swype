package com.nuance.connect.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Pair;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.CommandQueue;
import com.nuance.connect.comm.MessageSendingBus;
import com.nuance.connect.comm.Response;
import com.nuance.connect.comm.Transaction;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.common.ServiceInitializationConfig;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AccountManager;
import com.nuance.connect.service.manager.DeviceManager;
import com.nuance.connect.service.manager.DlmSyncManager;
import com.nuance.connect.service.manager.GeoIpLocationManager;
import com.nuance.connect.service.manager.LanguageManager;
import com.nuance.connect.service.manager.ManagerRegistry;
import com.nuance.connect.service.manager.ReportingManager;
import com.nuance.connect.service.manager.SessionManager;
import com.nuance.connect.service.manager.interfaces.AccountListener;
import com.nuance.connect.service.manager.interfaces.CommandManager;
import com.nuance.connect.service.manager.interfaces.LanguageListener;
import com.nuance.connect.service.manager.interfaces.Manager;
import com.nuance.connect.service.security.RequestKey;
import com.nuance.connect.store.DataStoreFactory;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.system.Connectivity;
import com.nuance.connect.util.Alarm;
import com.nuance.connect.util.BuildProperties;
import com.nuance.connect.util.BuildProps;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.HandlerThread;
import com.nuance.connect.util.InstallMetadata;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.PrivateBuildProperties;
import com.nuance.connect.util.TimeConversion;
import com.nuance.connect.util.VersionUtils;
import com.nuance.swype.input.IME;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONException;

/* loaded from: classes.dex */
public class ConnectClient extends Service implements MessageSendingBus {
    public static final String ALARM_CLASS = "ALARM_CLASS";
    public static final String ALARM_TYPE = "ALARM_TYPE";
    public static final String CONNECTION_TYPE = "CONNECTION_TYPE";
    public static final int DEFAULT_MESSAGE_DELAY = 1500;
    protected static final int DELAY_NOTIFY_TASKS_COMPLETE = 3000;
    public static final String MAJOR_VERSION = "8";
    private static final int MAXIMUM_SNOOZE_DELAY = 30000;
    private static final int MINIMUM_SNOOZE_DELAY = 10000;
    private static final int PASSED_ALARM_DELAY = 60000;
    private static final int SERVICE_SPINUP_TIME = 10000;
    private int[] activeLanguages;
    private Locale activeLocale;
    private BuildProps buildProperties;
    private boolean clientStartComplete;
    private volatile CommandQueue commandQueue;
    private volatile ConnectConfiguration connectConfiguration;
    private Connectivity connectivity;
    private volatile PersistentDataStore defaultStore;
    private volatile boolean destroyed;
    private HandlerThread handlerThread;
    private volatile ServiceInitializationConfig initializationConfig;
    private volatile boolean initialized;
    private volatile Messenger mHostMessenger;
    protected Handler mainHandler;
    protected Messenger messenger;
    protected MessageHandler msgHandler;
    protected volatile ManagerRegistry registry;
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM, getClass().getSimpleName());
    private final List<Long> triggerTimes = new ArrayList();
    private final Alarm.ExecutionTimeTracker triggerTimeTracker = new Alarm.ExecutionTimeTracker() { // from class: com.nuance.connect.service.ConnectClient.1
        @Override // com.nuance.connect.util.Alarm.ExecutionTimeTracker
        public void addAlarm(long j) {
            ConnectClient.this.addAlarm(j);
        }
    };
    private final HashSet<String> taskToProcess = new HashSet<>();
    private volatile long lastMessageProcessed = Long.MIN_VALUE;
    private long lastIdleSnoozeAt = Long.MIN_VALUE;
    private boolean isLicensed = true;
    private Long nextCheckin = null;
    protected int alarmUniqueId = 1123321232;
    private final ConcurrentCallbackSet<WeakReference<AccountListener>> accountListeners = new ConcurrentCallbackSet<>();
    private final ConcurrentCallbackSet<WeakReference<LanguageListener>> languageListeners = new ConcurrentCallbackSet<>();
    private final AtomicInteger bindCount = new AtomicInteger(0);
    private final Property.ValueListener<String> connectivityStringListener = new Property.StringValueListener() { // from class: com.nuance.connect.service.ConnectClient.2
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<String> property) {
            ConnectClient.this.log.d("connectivityStringListener.onValueChanged() name=", property.getKey(), " value=", property.getValue());
            if (property.getKey().equals(ConnectConfiguration.ConfigProperty.FOREGROUND_CONFIGURATION.name())) {
                ConnectClient.this.connectivity.setForegroundConfiguration(property.getValue());
            } else if (property.getKey().equals(ConnectConfiguration.ConfigProperty.BACKGROUND_CONFIGURATION.name())) {
                ConnectClient.this.connectivity.setBackgroundConfiguration(property.getValue());
            }
        }
    };
    private final Property.ValueListener<Integer> connectivityIntListener = new Property.IntegerValueListener() { // from class: com.nuance.connect.service.ConnectClient.3
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<Integer> property) {
            ConnectClient.this.log.d("connectivityIntListener.onValueChanged() name=", property.getKey(), " value=", property.getValue());
            if (property.getKey().equals(ConnectConfiguration.ConfigProperty.STABLE_CELLULAR_CONNECTION_THRESHOLD.name())) {
                ConnectClient.this.connectivity.setStableCellularTime(property.getValue().intValue());
            } else if (property.getKey().equals(ConnectConfiguration.ConfigProperty.STABLE_WIFI_CONNECTION_THRESHOLD.name())) {
                ConnectClient.this.connectivity.setStableWifiTime(property.getValue().intValue());
            }
        }
    };
    private final Property.ValueListener<String> commandQueueStringListener = new Property.StringValueListener() { // from class: com.nuance.connect.service.ConnectClient.4
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<String> property) {
            ConnectClient.this.log.d("commandQueueStringListener.onValueChanged() name=", property.getKey(), " value=", property.getValue());
            if (property.getKey().equals(ConnectConfiguration.ConfigProperty.URL.name())) {
                ConnectClient.this.commandQueue.setServerURL(property.getValue());
            } else if (property.getKey().equals(ConnectConfiguration.ConfigProperty.MINIMUM_SSL_PROTOCOL.name())) {
                ConnectClient.this.commandQueue.updateMinimumSSLProtocol(property.getValue());
            }
        }
    };
    private final Property.ValueListener<Integer> commandQueueIntListener = new Property.IntegerValueListener() { // from class: com.nuance.connect.service.ConnectClient.5
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<Integer> property) {
            ConnectClient.this.log.d("commandQueueIntListener.onValueChanged() name=", property.getKey(), " value=", property.getValue());
            if (property.getKey().equals(ConnectConfiguration.ConfigProperty.DEFAULT_DELAY.name())) {
                ConnectClient.this.commandQueue.setDefaultDelaySeconds(property.getValue().intValue());
                return;
            }
            if (property.getKey().equals(ConnectConfiguration.ConfigProperty.HTTP_ANALYTICS_TIME.name())) {
                ConnectClient.this.commandQueue.setHTTPAnalyticsTime(property.getValue().intValue());
            } else if (property.getKey().equals(ConnectConfiguration.ConfigProperty.NETWORK_TIMEOUT.name())) {
                ConnectClient.this.commandQueue.setNetworkTimeoutSeconds(property.getValue().intValue());
            } else if (property.getKey().equals(ConnectConfiguration.ConfigProperty.CONNECTION_LIMIT.name())) {
                ConnectClient.this.commandQueue.setConnectionLimit(property.getValue().intValue());
            }
        }
    };
    private final Property.ValueListener<String> oemIdListener = new Property.StringValueListener() { // from class: com.nuance.connect.service.ConnectClient.6
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<String> property) {
            ConnectClient.this.connectConfiguration.loadOverrides();
        }
    };
    private final Property.ValueListener<Integer> intListener = new Property.IntegerValueListener() { // from class: com.nuance.connect.service.ConnectClient.7
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<Integer> property) {
            if (property.getKey().equals(ConnectConfiguration.ConfigProperty.LOGGING.name())) {
                ConnectClient.this.reconfigureLogger(ConnectClient.this.getBoolean(ConnectConfiguration.ConfigProperty.DEVELOPER_LOG_ENABLED).booleanValue(), property.getValue().intValue());
            }
        }
    };
    private final Property.ValueListener<Boolean> boolListener = new Property.BooleanValueListener() { // from class: com.nuance.connect.service.ConnectClient.8
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<Boolean> property) {
            if (property.getKey().equals(ConnectConfiguration.ConfigProperty.DEVELOPER_LOG_ENABLED.name())) {
                ConnectClient.this.reconfigureLogger(property.getValue().booleanValue(), ConnectClient.this.getInteger(ConnectConfiguration.ConfigProperty.LOGGING).intValue());
            } else if (property.getKey().equals(ConnectConfiguration.ConfigProperty.LICENSING_LICENSED_BUILD.name())) {
                ConnectClient.this.isLicensed = property.getValue().booleanValue();
            }
        }
    };
    private final Property.BooleanValueListener idleStatus = new Property.BooleanValueListener() { // from class: com.nuance.connect.service.ConnectClient.9
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<Boolean> property) {
            ConnectClient.this.log.v("Idle status changed: ", property);
            if (property.getValue().booleanValue()) {
                ConnectClient.this.idleSnooze();
            } else {
                ConnectClient.this.getHandler().removeCallbacks(ConnectClient.this.sendStopMessage);
            }
        }
    };
    private final Runnable sendStopMessage = new Runnable() { // from class: com.nuance.connect.service.ConnectClient.10
        @Override // java.lang.Runnable
        public void run() {
            long j;
            int i;
            ConnectClient.this.log.v("sendStopMessage this=" + ConnectClient.this.boolListener);
            synchronized (ConnectClient.this.triggerTimes) {
                long j2 = Long.MAX_VALUE;
                long currentTimeMillis = System.currentTimeMillis();
                boolean z = false;
                while (true) {
                    if (ConnectClient.this.triggerTimes.isEmpty()) {
                        break;
                    }
                    Long l = (Long) Collections.min(ConnectClient.this.triggerTimes);
                    if (currentTimeMillis < l.longValue()) {
                        j2 = l.longValue();
                        break;
                    }
                    z = ConnectClient.this.triggerTimes.remove(l) ? true : z;
                }
                long max = Math.max(j2 - IME.RETRY_DELAY_IN_MILLIS, ConnectClient.this.lastMessageProcessed);
                long j3 = currentTimeMillis + IME.RETRY_DELAY_IN_MILLIS;
                if (z) {
                    ConnectClient.this.log.v("Alarms have passed...waiting for possible intent triggers");
                    i = Math.max(60000, 10000);
                } else if (max < j3) {
                    ConnectClient.this.log.v("Alarm too soon... delaying shutdown. Requested time[", TimeConversion.prettyDateFormat(max), "] min time[", TimeConversion.prettyDateFormat(max), "]");
                    i = 10000;
                } else if ((ConnectClient.this.commandQueue == null || ConnectClient.this.commandQueue.isIdle()) && (ConnectClient.this.registry == null || ConnectClient.this.registry.getIdleProperty().getValue().booleanValue())) {
                    if (ConnectClient.this.commandQueue == null || !ConnectClient.this.commandQueue.hasPendingTransactions()) {
                        ConnectClient.this.log.v("Sending shutdown message... time[", TimeConversion.prettyDateFormat(max), "] now[", TimeConversion.prettyDateFormat(currentTimeMillis), "]");
                        j = max;
                    } else {
                        ConnectClient.this.log.v("Sending shutdown message... restart on reconnection");
                        j = -1;
                    }
                    ConnectClient.this.sendMessageToHost(InternalMessages.MESSAGE_HOST_ON_SERVICE_SHUTDOWN, Long.valueOf(j));
                    i = 10000;
                } else {
                    ConnectClient.this.log.v("Not idle...");
                    i = 10000;
                }
                if (ConnectClient.this.mHostMessenger != null) {
                    ConnectClient.this.getHandler().postDelayed(ConnectClient.this.sendStopMessage, i);
                }
            }
        }
    };
    private final CommandQueue.CommandQueueErrorCallback commandQueueErrorCallback = new CommandQueue.CommandQueueErrorCallback() { // from class: com.nuance.connect.service.ConnectClient.11
        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onAccountInvalidated() {
            ConnectClient.this.invalidAccount();
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onConnectionStatus(int i, String str) {
            Bundle bundle = new Bundle();
            bundle.putInt("status", i);
            bundle.putString("message", str);
            ConnectClient.this.sendMessageToHost(InternalMessages.MESSAGE_HOST_CONNECTION_STATUS, bundle);
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onDeviceInvalidated() {
            ConnectClient.this.invalidateDevice();
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onDeviceNotAvailable() {
            DeviceManager deviceManager = (DeviceManager) ConnectClient.this.registry.getManagerReference(DeviceManager.MANAGER_NAME);
            if (deviceManager != null) {
                deviceManager.registerDevice();
            }
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onQueuePaused(int i, int i2, String str, boolean z) {
            Bundle bundle = new Bundle();
            bundle.putInt(Strings.MESSAGE_BUNDLE_DELAY, i < 0 ? 0 : i);
            bundle.putInt("status", i2);
            bundle.putString("message", str);
            ConnectClient.this.sendMessageToHost(InternalMessages.MESSAGE_HOST_CONNECTION_STATUS, bundle);
            long currentTimeMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(i);
            if (!z || currentTimeMillis <= ConnectClient.this.getLong(ConnectConfiguration.ConfigProperty.QUEUE_RESUME_TIMESTAMP).longValue()) {
                return;
            }
            ConnectClient.this.setProperty(ConnectConfiguration.ConfigProperty.QUEUE_RESUME_TIMESTAMP, Long.valueOf(currentTimeMillis));
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onSessionInvalidated() {
            ConnectClient.this.resetSession(null);
            SessionManager sessionManager = (SessionManager) ConnectClient.this.registry.getManagerReference(SessionManager.MANAGER_NAME);
            if (sessionManager != null) {
                sessionManager.createSession();
            }
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onSessionNotAvailable() {
            ConnectClient.this.postMessage(InternalMessages.MESSAGE_COMMAND_SESSION_CREATE);
            SessionManager sessionManager = (SessionManager) ConnectClient.this.registry.getManagerReference(SessionManager.MANAGER_NAME);
            if (sessionManager != null) {
                sessionManager.createSession();
            }
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onUnlicensed(int i) {
            ConnectClient.this.log.d("onUnlicensed(", Integer.valueOf(i), ")");
            if (i < -1) {
                if (i == Integer.MIN_VALUE) {
                    ConnectClient.this.setProperty(ConnectConfiguration.ConfigProperty.LICENSING_LICENSED_BUILD, Boolean.TRUE);
                    ConnectClient.this.setProperty(ConnectConfiguration.ConfigProperty.LICENSING_SERVER_DELAY, (Integer) 0);
                    ConnectClient.this.setProperty(ConnectConfiguration.ConfigProperty.LICENSING_LAST_CHECKIN, (Long) null);
                    ConnectClient.this.sendMessageToHost(InternalMessages.MESSAGE_HOST_LICENSED_BUILD, Boolean.TRUE);
                    return;
                }
                return;
            }
            ConnectClient.this.sendMessageToHost(InternalMessages.MESSAGE_HOST_LICENSED_BUILD, Boolean.FALSE);
            ConnectClient.this.setProperty(ConnectConfiguration.ConfigProperty.LICENSING_LICENSED_BUILD, Boolean.FALSE);
            ConnectClient.this.setProperty(ConnectConfiguration.ConfigProperty.LICENSING_SERVER_DELAY, Integer.valueOf(i));
            ConnectClient.this.setProperty(ConnectConfiguration.ConfigProperty.LICENSING_LAST_CHECKIN, Long.valueOf(System.currentTimeMillis()));
            ConnectClient.this.calculateNextCheckin();
            SessionManager sessionManager = (SessionManager) ConnectClient.this.registry.getManagerReference(SessionManager.COMMAND_FAMILY);
            if (sessionManager != null) {
                sessionManager.onUnlicensed();
            }
        }
    };
    private final InstallMetadata.MetaDataClient datamgr = new InstallMetadata.MetaDataClient() { // from class: com.nuance.connect.service.ConnectClient.14
        @Override // com.nuance.connect.util.InstallMetadata.MetaDataClient
        public PersistentDataStore getDataStore() {
            return ConnectClient.this.defaultStore;
        }
    };

    /* loaded from: classes.dex */
    protected static class ClientCallback implements Handler.Callback {
        private final WeakReference<ConnectClient> connectRef;

        public ClientCallback(ConnectClient connectClient) {
            this.connectRef = new WeakReference<>(connectClient);
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            ConnectClient connectClient = this.connectRef.get();
            if (connectClient == null || connectClient.destroyed) {
                return true;
            }
            Message obtainMessage = connectClient.msgHandler.obtainMessage(message.what);
            obtainMessage.copyFrom(message);
            connectClient.msgHandler.sendMessage(obtainMessage);
            return true;
        }
    }

    /* loaded from: classes.dex */
    private static class ClientHandlerThread extends HandlerThread {
        private final WeakReference<ConnectClient> clientWeakReference;

        ClientHandlerThread(ConnectClient connectClient) {
            this.clientWeakReference = new WeakReference<>(connectClient);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.nuance.connect.util.HandlerThread
        public Message handleMessage(Message message) {
            ConnectClient connectClient = this.clientWeakReference.get();
            if (connectClient == null) {
                return null;
            }
            connectClient.handleMessage(message);
            return null;
        }

        @Override // com.nuance.connect.util.HandlerThread, java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            ConnectClient connectClient = this.clientWeakReference.get();
            if (connectClient != null) {
                connectClient.cleanUp();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MessageHandler extends Handler {
        private final WeakReference<ConnectClient> connectRef;

        public MessageHandler(ConnectClient connectClient, HandlerThread handlerThread) {
            super(handlerThread.getLooper());
            this.connectRef = new WeakReference<>(connectClient);
        }

        public void handleCommand(Intent intent) {
            ConnectClient connectClient = this.connectRef.get();
            if (connectClient != null) {
                connectClient.handleCommand(intent);
            } else {
                Logger.getLog(Logger.LoggerType.DEVELOPER).d("Could not handle intent: " + intent);
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            ConnectClient connectClient = this.connectRef.get();
            if (connectClient != null) {
                connectClient.handleMessage(message);
            }
        }

        public void stop() {
            this.connectRef.clear();
            for (int i = 0; i <= InternalMessages.values().length; i++) {
                removeMessages(i);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void calculateNextCheckin() {
        this.log.d("calculateNextCheckin()");
        if (this.isLicensed) {
            return;
        }
        int intValue = getInteger(ConnectConfiguration.ConfigProperty.LICENSING_SERVER_DELAY).intValue();
        Long l = getLong(ConnectConfiguration.ConfigProperty.LICENSING_LAST_CHECKIN);
        if (l == null) {
            l = Long.valueOf(System.currentTimeMillis());
            setProperty(ConnectConfiguration.ConfigProperty.LICENSING_LAST_CHECKIN, l);
        }
        this.log.d("calculateNextCheckin() lastCheckin: ", TimeConversion.prettyDateFormat(l.longValue()));
        if (intValue == 0) {
            this.nextCheckin = Long.valueOf(TimeConversion.convertSecondsToTimeStamp(getInteger(ConnectConfiguration.ConfigProperty.LICENSING_DEFAULT_DELAY).intValue(), l.longValue()));
        } else if (intValue > 0) {
            this.nextCheckin = Long.valueOf(TimeConversion.convertSecondsToTimeStamp(intValue, l.longValue()));
        } else if (intValue == -1) {
            this.nextCheckin = Long.MAX_VALUE;
        } else if (intValue == Integer.MIN_VALUE) {
            this.nextCheckin = null;
        }
        if (this.nextCheckin != null) {
            this.log.d("nextCheckin: ", TimeConversion.prettyDateFormat(this.nextCheckin.longValue()));
        }
    }

    private void cancelAlarms() {
        this.log.d("cancelAlarms()");
        getAlarmBuilder(DlmSyncManager.class, DlmSyncManager.DLM_EVENTS_SEND).build().cancel();
        getAlarmBuilder(GeoIpLocationManager.class, GeoIpLocationManager.ALARM_TYPE_NEXT_LOOKUP).build().cancel();
        getAlarmBuilder(ReportingManager.class, ReportingManager.REPORTING_GENERIC).build().cancel();
        getAlarmBuilder(SessionManager.class, SessionManager.COMMAND_POLL).build().cancel();
        getAlarmBuilder(Connectivity.class, "CONNECTIVITY_ALARM_STABLE_CONNECTION").build().cancel();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanUp() {
        this.log.d("cleanUp thread=", Thread.currentThread(), "; obj=", this.boolListener);
        this.msgHandler.stop();
        if (this.commandQueue != null) {
            this.commandQueue.stop();
        }
        if (this.connectConfiguration != null) {
            this.connectConfiguration.destroy();
        }
        this.registry.destroy();
        this.mHostMessenger = null;
        this.handlerThread = null;
        this.log.d("Done cleanUp");
    }

    private Message getMessage(InternalMessages internalMessages, Object obj) {
        Bundle bundle;
        Message obtainMessage = this.mainHandler.obtainMessage(internalMessages.ordinal());
        Bundle bundle2 = new Bundle();
        if (obj instanceof String) {
            bundle2.putString(Strings.DEFAULT_KEY, (String) obj);
            bundle = bundle2;
        } else if (obj instanceof Bundle) {
            bundle = (Bundle) obj;
        } else if (obj instanceof Boolean) {
            bundle2.putBoolean(Strings.DEFAULT_KEY, ((Boolean) obj).booleanValue());
            bundle = bundle2;
        } else if (obj instanceof Integer) {
            bundle2.putInt(Strings.DEFAULT_KEY, ((Integer) obj).intValue());
            bundle = bundle2;
        } else {
            this.log.e("sendMessageToHost(", internalMessages, ", ", obj != null ? obj.getClass().getSimpleName() : null, ") Error attaching Object, unsupported type.");
            bundle = bundle2;
        }
        obtainMessage.setData(bundle);
        return obtainMessage;
    }

    private void recalculatePolling() {
        setProperty(ConnectConfiguration.ConfigProperty.POLLING_FREQUENCY, Integer.valueOf(this.registry.getMinimumPollInterval()));
        postMessage(InternalMessages.MESSAGE_CLIENT_SEND_DEVICE_PROPERTIES);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reconfigureLogger(boolean z, int i) {
        Logger.configure(z, i, Logger.OutputMode.ANDROID_LOG, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessageToHost(Message message) {
        if (this.mHostMessenger != null) {
            try {
                this.mHostMessenger.send(message);
            } catch (RemoteException e) {
                this.mHostMessenger = null;
            }
        }
    }

    private void setup(String str) {
        int i = 0;
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        this.defaultStore = DataStoreFactory.getDataStore(this, "com.nuance.swype.connect.store.FilePreference", str);
        this.connectConfiguration = new ConnectConfiguration(this);
        reconfigureLogger(getBoolean(ConnectConfiguration.ConfigProperty.DEVELOPER_LOG_ENABLED).booleanValue(), getInteger(ConnectConfiguration.ConfigProperty.LOGGING).intValue());
        long longValue = getLong(ConnectConfiguration.ConfigProperty.QUEUE_RESUME_TIMESTAMP).longValue();
        if (longValue != -1) {
            long seconds = TimeUnit.MILLISECONDS.toSeconds(longValue - System.currentTimeMillis());
            if (seconds > 0 && seconds <= 2147483647L) {
                i = (int) seconds;
            }
            if (i == 0) {
                setProperty(ConnectConfiguration.ConfigProperty.QUEUE_RESUME_TIMESTAMP, (Long) (-1L));
            }
        }
        this.commandQueue = new CommandQueue(this, this.commandQueueErrorCallback, this.connectivity, getString(ConnectConfiguration.ConfigProperty.MINIMUM_SSL_PROTOCOL), RequestKey.getKey(), i);
        configureListeners();
        calculateNextCheckin();
        this.commandQueue.start();
        synchronized (this.triggerTimes) {
            this.triggerTimes.clear();
        }
    }

    public void addAlarm(long j) {
        this.log.i("Alarm trigger in: ", Long.valueOf(j - System.currentTimeMillis()));
        synchronized (this.triggerTimes) {
            this.triggerTimes.add(Long.valueOf(j));
        }
    }

    public void addLanguageListener(LanguageListener languageListener) {
        this.languageListeners.add(new WeakReference<>(languageListener));
    }

    public void addListener(ConnectConfiguration.ConfigProperty configProperty, Property.ValueListener<?> valueListener) {
        getConfiguration().addListener(configProperty.name(), valueListener);
    }

    public void addListener(AccountListener accountListener) {
        this.accountListeners.add(new WeakReference<>(accountListener));
    }

    public void addListener(String str, Property.ValueListener<?> valueListener) {
        getConfiguration().addListener(str, valueListener);
    }

    public void alarmNotification(String str, Bundle bundle) {
        this.log.w("Unhandled alarm. type: ", str);
    }

    protected void configureListeners() {
        addListener(ConnectConfiguration.ConfigProperty.OEM_ID.name(), this.oemIdListener);
        addListener(ConnectConfiguration.ConfigProperty.LOGGING.name(), this.intListener);
        addListener(ConnectConfiguration.ConfigProperty.DEVELOPER_LOG_ENABLED.name(), this.boolListener);
        addListener(ConnectConfiguration.ConfigProperty.LICENSING_LICENSED_BUILD.name(), this.boolListener);
        addListener(ConnectConfiguration.ConfigProperty.STABLE_CELLULAR_CONNECTION_THRESHOLD, this.connectivityIntListener);
        addListener(ConnectConfiguration.ConfigProperty.STABLE_WIFI_CONNECTION_THRESHOLD, this.connectivityIntListener);
        addListener(ConnectConfiguration.ConfigProperty.BACKGROUND_CONFIGURATION, this.connectivityStringListener);
        addListener(ConnectConfiguration.ConfigProperty.FOREGROUND_CONFIGURATION, this.connectivityStringListener);
        addListener(ConnectConfiguration.ConfigProperty.DEFAULT_DELAY, this.commandQueueIntListener);
        addListener(ConnectConfiguration.ConfigProperty.NETWORK_TIMEOUT, this.commandQueueIntListener);
        addListener(ConnectConfiguration.ConfigProperty.CONNECTION_LIMIT, this.commandQueueIntListener);
        addListener(ConnectConfiguration.ConfigProperty.HTTP_ANALYTICS_TIME, this.commandQueueIntListener);
        addListener(ConnectConfiguration.ConfigProperty.URL, this.commandQueueStringListener);
        addListener(ConnectConfiguration.ConfigProperty.MINIMUM_SSL_PROTOCOL, this.commandQueueStringListener);
    }

    public Alarm.Builder getAlarmBuilder(Class<?> cls, String str) {
        return new Alarm.Builder(getContext(), cls, getClass(), this.triggerTimeTracker, str);
    }

    public Boolean getBoolean(ConnectConfiguration.ConfigProperty configProperty) {
        return getConfiguration().getBoolean(configProperty);
    }

    public BuildProps getBuildProps() {
        if (this.buildProperties == null) {
            if (getBoolean(ConnectConfiguration.ConfigProperty.ANONYMOUS_BUILD).booleanValue()) {
                this.buildProperties = new PrivateBuildProperties(this);
            } else {
                this.buildProperties = new BuildProperties(this);
            }
        }
        return this.buildProperties;
    }

    public ConnectConfiguration getConfiguration() {
        return this.connectConfiguration;
    }

    public String getConnectVersion() {
        return "8.0." + String.valueOf(InternalMessages.values().length);
    }

    @Override // com.nuance.connect.comm.MessageSendingBus
    public Context getContext() {
        return this;
    }

    public int getCoreForLanguage(int i) {
        LanguageManager languageManager = (LanguageManager) this.registry.getManagerReference(LanguageManager.COMMAND_FAMILY);
        if (languageManager != null) {
            return languageManager.getCoreForLanguage(i);
        }
        return 0;
    }

    public Locale getCurrentLocale() {
        return this.activeLocale != null ? this.activeLocale : Locale.getDefault();
    }

    public InstallMetadata.MetaDataClient getDataManager() {
        return this.datamgr;
    }

    public PersistentDataStore getDataStore() {
        return this.defaultStore;
    }

    @Override // android.content.ContextWrapper, android.content.Context, com.nuance.connect.comm.MessageSendingBus
    public String getDeviceId() {
        DeviceManager deviceManager = (DeviceManager) this.registry.getManagerReference(DeviceManager.COMMAND_FAMILY);
        if (deviceManager != null) {
            return deviceManager.getDeviceId();
        }
        return null;
    }

    @Override // com.nuance.connect.comm.MessageSendingBus
    public String getDeviceRegisterCommand() {
        return DeviceManager.COMMAND_FAMILY + "/register";
    }

    public Handler getHandler() {
        return this.mainHandler;
    }

    public Integer getInteger(ConnectConfiguration.ConfigProperty configProperty) {
        return getConfiguration().getInteger(configProperty);
    }

    public Pair<String, Long> getLastGeoIP() {
        GeoIpLocationManager geoIpLocationManager = (GeoIpLocationManager) this.registry.getManagerReference(GeoIpLocationManager.COMMAND_FAMILY);
        if (geoIpLocationManager != null) {
            return geoIpLocationManager.getLatestGeoIPCountry();
        }
        return null;
    }

    public Long getLong(ConnectConfiguration.ConfigProperty configProperty) {
        return getConfiguration().getLong(configProperty);
    }

    public Long getNextLicenseCheckin() {
        if (isLicensed()) {
            return Long.MAX_VALUE;
        }
        return this.nextCheckin;
    }

    public ServiceInitializationConfig getServiceInitializationConfig() {
        return this.initializationConfig;
    }

    @Override // com.nuance.connect.comm.MessageSendingBus
    public String getSessionCreateCommand() {
        return SessionManager.COMMAND_FAMILY + "/create";
    }

    @Override // com.nuance.connect.comm.MessageSendingBus
    public String getSessionId() {
        SessionManager sessionManager = (SessionManager) this.registry.getManagerReference(SessionManager.COMMAND_FAMILY);
        if (sessionManager != null) {
            return sessionManager.getSessionId();
        }
        return null;
    }

    public String getString(ConnectConfiguration.ConfigProperty configProperty) {
        return getConfiguration().getString(configProperty);
    }

    public String getUsedLastForFeature(FeaturesLastUsed.Feature feature) {
        return new FeaturesLastUsed(getConfiguration().getString(ConnectConfiguration.ConfigProperty.FEATURES_LAST_USED)).getLastUsed(feature);
    }

    protected void handleCommand(Intent intent) {
        Bundle extras;
        if (intent == null || (extras = intent.getExtras()) == null) {
            return;
        }
        this.log.v("handleCommand() alarm class: ", extras.getString("ALARM_CLASS"));
        String string = extras.getString("ALARM_CLASS");
        String string2 = extras.getString("ALARM_TYPE");
        if (string == null || string2 == null) {
            return;
        }
        if (ConnectClient.class.toString().equals(string)) {
            alarmNotification(string2, extras);
            return;
        }
        Manager managerReferenceByClass = this.registry.getManagerReferenceByClass(string);
        if (managerReferenceByClass == null || !(managerReferenceByClass instanceof CommandManager)) {
            this.log.w("Unable to find reference for alarm. ", string);
        } else {
            ((CommandManager) managerReferenceByClass).alarmNotification(string2, extras);
            this.log.d("Alarm handled by: ", managerReferenceByClass.getClass());
        }
    }

    protected void handleMessage(Message message) {
        String str = null;
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_CLIENT_REGISTER_CLIENT:
                this.bindCount.incrementAndGet();
                this.mHostMessenger = message.replyTo;
                if (message.peekData() != null) {
                    message.getData().setClassLoader(ServiceInitializationConfig.class.getClassLoader());
                    String string = message.getData().getString(Strings.SERVICES_LIST);
                    if (string != null && string.length() > 0) {
                        str = string;
                    }
                    this.initializationConfig = (ServiceInitializationConfig) message.getData().getParcelable(Strings.CONFIG_KEY);
                    if (message.getData().getInt(Strings.PROCESS_ID) != Process.myPid()) {
                        this.log.e("The Connect Service cannot be run in its own process.");
                        this.log.e("Please remove the 'android:process' attribute in AndroidManifest.xml for ConnectClient.");
                        this.log.e("See documentation for further details.");
                        throw new RuntimeException("ACSDK cannot be run in its own process");
                    }
                    if (this.initializationConfig != null) {
                        this.log.d(this.initializationConfig.toString());
                        setup(this.initializationConfig.getLegacySecretKey());
                        if (this.connectConfiguration != null) {
                            this.connectConfiguration.updateFromInitializationConfig(this.initializationConfig);
                        }
                    }
                }
                this.log.v("MESSAGE_CLIENT_REGISTER_CLIENT");
                this.oemLog.v("Registering with service");
                if (!isLicensed()) {
                    sendMessageToHost(InternalMessages.MESSAGE_HOST_LICENSED_BUILD, Boolean.FALSE);
                }
                if (str == null) {
                    sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_HANDLERS, getConnectVersion());
                    return;
                }
                if (!registerServices(str)) {
                    sendMessageToHost(InternalMessages.MESSAGE_HOST_INVALID_HANDLERS, getConnectVersion());
                    return;
                }
                if (this.bindCount.get() > 1) {
                    this.registry.rebind();
                }
                if (this.registry.isValid()) {
                    recalculatePolling();
                    startNextManager();
                    return;
                } else {
                    this.log.d("There are no managers registered!!!");
                    postMessageDelayed(message, 1500L);
                    return;
                }
            case MESSAGE_CLIENT_UNREGISTER_CLIENT:
                this.log.v("MESSAGE_CLIENT_UNREGISTER_CLIENT");
                this.mHostMessenger = null;
                return;
            case MESSAGE_HOST_DEREGISTER:
                this.log.v("MESSAGE_HOST_DEREGISTER");
                return;
            case MESSAGE_CLIENT_UPDATE_CONFIGURATION:
                this.log.d("MESSAGE_CLIENT_UPDATE_CONFIGURATION");
                this.oemLog.v("Configuring Connect");
                this.initializationConfig = (ServiceInitializationConfig) message.getData().getParcelable(Strings.DEFAULT_KEY);
                if (this.initializationConfig == null || this.connectConfiguration == null) {
                    return;
                }
                this.log.d(this.initializationConfig.toString());
                this.connectConfiguration.updateFromInitializationConfig(this.initializationConfig);
                return;
            case MESSAGE_RETRY_CONNECTION:
                this.log.d("MESSAGE_RETRY_CONNECTION");
                this.oemLog.v("Retrying Connection");
                if (this.commandQueue != null) {
                    this.commandQueue.retryConnection();
                    return;
                }
                return;
            case MESSAGE_HOST_ON_UPGRADE:
                String string2 = message.getData().getString(Strings.VERSION_FROM);
                String string3 = message.getData().getString(Strings.VERSION_TO);
                String string4 = message.getData().getString(Strings.DEFAULT_KEY);
                VersionUtils.Version version = new VersionUtils.Version(string2);
                VersionUtils.Version version2 = new VersionUtils.Version(string3);
                boolean z = (getString(ConnectConfiguration.ConfigProperty.OEM_ID) == null || getString(ConnectConfiguration.ConfigProperty.OEM_ID).equals(string4)) ? false : true;
                this.log.d("MESSAGE_HOST_ON_UPGRADE from:", string2, " to:", string3, " oemId: ", string4, " oem changed: ", Boolean.valueOf(z));
                this.registry.versionUpdate(version, version2, z);
                recalculatePolling();
                if (z) {
                    setProperty(ConnectConfiguration.ConfigProperty.OEM_ID, string4);
                    return;
                }
                return;
            case MESSAGE_RECALCULATE_POLLING:
                this.log.d("MESSAGE_RECALCULATE_POLLING");
                recalculatePolling();
                return;
            default:
                if (this.registry.handleMessage(message)) {
                    this.lastMessageProcessed = System.currentTimeMillis();
                    return;
                } else {
                    this.log.d("Message is invalid (", InternalMessages.fromInt(message.what).toString(), ")");
                    return;
                }
        }
    }

    public void idleSnooze() {
        idleSnooze(0L);
    }

    public void idleSnooze(long j) {
        long max = Math.max(IME.RETRY_DELAY_IN_MILLIS, 1000 + j) + SystemClock.uptimeMillis();
        if (max > this.lastIdleSnoozeAt) {
            this.lastIdleSnoozeAt = max;
            if (SystemClock.uptimeMillis() + 30000 < this.lastIdleSnoozeAt) {
                this.log.d("Beyond max idle time, setting time for trigger.");
                synchronized (this.triggerTimes) {
                    this.triggerTimes.add(Long.valueOf(System.currentTimeMillis() + j));
                }
                this.lastIdleSnoozeAt = SystemClock.uptimeMillis() + 30000;
            }
            getHandler().removeCallbacks(this.sendStopMessage);
            getHandler().postAtTime(this.sendStopMessage, max);
        }
        this.log.d("idle snoozed for number of seconds: ", Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(this.lastIdleSnoozeAt - SystemClock.uptimeMillis())));
    }

    public void invalidAccount() {
        Iterator<WeakReference<AccountListener>> it = this.accountListeners.iterator();
        while (it.hasNext()) {
            WeakReference<AccountListener> next = it.next();
            AccountListener accountListener = next.get();
            if (accountListener != null) {
                accountListener.onInvalidated();
            } else {
                this.accountListeners.remove(next);
            }
        }
    }

    public void invalidateDevice() {
        this.registry.deregister();
        startNextManager();
    }

    public boolean isAccountLinked() {
        AccountManager accountManager = (AccountManager) this.registry.getManagerReference(AccountManager.MANAGER_NAME);
        if (accountManager != null) {
            return accountManager.isAccountLinked();
        }
        return false;
    }

    @Override // com.nuance.connect.comm.MessageSendingBus
    public boolean isLicensed() {
        return this.isLicensed;
    }

    public void linkedAccount() {
        Iterator<WeakReference<AccountListener>> it = this.accountListeners.iterator();
        while (it.hasNext()) {
            WeakReference<AccountListener> next = it.next();
            AccountListener accountListener = next.get();
            if (accountListener != null) {
                accountListener.onLinked();
            } else {
                this.accountListeners.remove(next);
            }
        }
    }

    public void managerStartComplete(String str) {
        this.log.d("managerStartComplete(", str, ")");
        str.equals(SessionManager.MANAGER_NAME);
        startNextManager();
    }

    public void notifyLanguageListeners() {
        setCurrentLanguageInfo(this.activeLanguages);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        this.log.d("onBind()");
        return this.messenger.getBinder();
    }

    @Override // android.app.Service
    public void onCreate() {
        this.log.d("onCreate obj=", this.boolListener);
        this.destroyed = false;
        this.mainHandler = new Handler(new ClientCallback(this));
        this.handlerThread = new ClientHandlerThread(this);
        this.handlerThread.start();
        this.msgHandler = new MessageHandler(this, this.handlerThread);
        this.messenger = new Messenger(this.msgHandler);
        this.registry = new ManagerRegistry();
        this.registry.getIdleProperty().addListener(this.idleStatus);
        this.connectivity = new Connectivity(getContext());
        super.onCreate();
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.log.d("onDestroy thread=", Thread.currentThread(), "; obj=", this.boolListener);
        this.destroyed = true;
        this.initialized = false;
        this.handlerThread.quit();
        getHandler().removeCallbacks(this.sendStopMessage);
        this.connectivity.destroy();
        super.onDestroy();
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onLowMemory() {
        this.log.d("onLowMemory() called");
        super.onLowMemory();
    }

    @Override // android.app.Service
    public void onRebind(Intent intent) {
        this.log.d("onRebind i=", intent);
        super.onRebind(intent);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        this.log.v("onStartCommand intent=", intent);
        this.msgHandler.handleCommand(intent);
        return 2;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        cancelAlarms();
        boolean onUnbind = super.onUnbind(intent);
        stopSelf();
        this.log.d("onUnbind() return=", Boolean.valueOf(onUnbind));
        return onUnbind;
    }

    public void performTask(String str, boolean z) {
        Bundle bundle = new Bundle();
        if (z) {
            bundle.putString(Strings.ACKNOWLEDGEMENT, str);
        }
        if (str.equals(Strings.TASK_SEND_REPORTING)) {
            postMessage(InternalMessages.MESSAGE_SEND_REPORTING_NOW, bundle);
            return;
        }
        if (str.equals(Strings.TASK_UPDATE_TIMERS)) {
            postMessage(InternalMessages.MESSAGE_COMMAND_REQUEST_TIMERS, bundle);
            return;
        }
        if (str.startsWith(Strings.TASK_DLM_BACKUP)) {
            try {
                bundle.putInt(Strings.DLM_EVENT_CORE, Integer.parseInt(str.substring(11)));
                postMessage(InternalMessages.MESSAGE_COMMAND_DLM_BACKUP_REQUIRED, bundle);
                return;
            } catch (NumberFormatException e) {
                this.log.e(Strings.TASK_DLM_BACKUP, " Could not parse task: ", str);
                return;
            }
        }
        if (str.startsWith(Strings.TASK_DLM_SYNC_AVAILABLE)) {
            try {
                bundle.putInt(Strings.DLM_EVENT_CORE, Integer.parseInt(str.substring(9)));
                postMessage(InternalMessages.MESSAGE_COMMAND_PULL_DLM_EVENTS, bundle);
                return;
            } catch (NumberFormatException e2) {
                this.log.e(Strings.TASK_DLM_SYNC_AVAILABLE, " Could not parse task: ", str);
                return;
            }
        }
        if (str.equals(Strings.TASK_GET_CONFIG)) {
            postMessage(InternalMessages.MESSAGE_COMMAND_DEVICE_CONFIG, bundle);
            return;
        }
        if (str.startsWith(Strings.TASK_CDB_LIST_UPDATE)) {
            int i = 0;
            try {
                i = Integer.parseInt(str.substring(16));
            } catch (NumberFormatException e3) {
            }
            if (i > 0) {
                bundle.putInt(Strings.CATEGORY_TYPE, i);
            }
            postMessage(InternalMessages.MESSAGE_COMMAND_CDB_LIST_UPDATE, bundle);
            return;
        }
        if (str.equals(Strings.TASK_CDB_AVAILABLE)) {
            postMessage(InternalMessages.MESSAGE_COMMAND_CDB_AVAILABLE, bundle);
        } else if (str.equals(Strings.TASK_UPDATE_DOCS)) {
            postMessage(InternalMessages.MESSAGE_COMMAND_UPDATE_DOCS, bundle);
        } else if (str.equals(Strings.TASK_LANGUAGE_LIST_UPDATED)) {
            postMessage(InternalMessages.MESSAGE_COMMAND_LANGUAGE_LIST_UPDATE, bundle);
        }
    }

    public void performTasks(JSONArray jSONArray) {
        ArrayList arrayList = new ArrayList(Arrays.asList(Strings.TASK_CDB_AVAILABLE, Strings.TASK_CDB_LIST_UPDATE, Strings.TASK_LANGUAGE_LIST_UPDATED));
        for (int i = 0; i < jSONArray.length(); i++) {
            try {
                String string = jSONArray.getString(i);
                if (arrayList.contains(string)) {
                    performTask(string, true);
                    this.taskToProcess.add(string);
                } else {
                    performTask(string, false);
                }
            } catch (JSONException e) {
            }
        }
        if (this.taskToProcess.size() == 0) {
            sendMessageToHost(InternalMessages.MESSAGE_HOST_POLL_COMPLETE, Boolean.TRUE);
        }
    }

    public void postMessage(Message message) {
        this.mainHandler.sendMessage(message);
    }

    public void postMessage(InternalMessages internalMessages) {
        postMessage(this.mainHandler.obtainMessage(internalMessages.ordinal()));
    }

    public void postMessage(InternalMessages internalMessages, Bundle bundle) {
        postMessage(getMessage(internalMessages, bundle));
    }

    public void postMessage(InternalMessages internalMessages, Object obj) {
        postMessage(getMessage(internalMessages, obj));
    }

    public void postMessageDelayed(Message message, long j) {
        idleSnooze(j);
        this.mainHandler.sendMessageDelayed(message, j);
    }

    public void postMessageDelayed(InternalMessages internalMessages, long j) {
        postMessageDelayed(this.mainHandler.obtainMessage(internalMessages.ordinal()), j);
    }

    public void postMessageDelayed(InternalMessages internalMessages, Object obj, long j) {
        postMessageDelayed(getMessage(internalMessages, obj), j);
    }

    public void processReceivedServerMessage(String str, String str2, Response response) {
        this.registry.dispatchReceivedMessage(str, str2, response);
    }

    public boolean registerServices(String str) {
        this.log.v("registerServices()");
        this.clientStartComplete = false;
        if (str == null || str.length() == 0) {
            return false;
        }
        if (this.registry.isValid() && !this.registry.isCurrentlyProcessing()) {
            return true;
        }
        this.registry.setup(this, true);
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        while (stringTokenizer.hasMoreTokens()) {
            this.registry.addService(stringTokenizer.nextToken());
        }
        return this.registry.complete();
    }

    public void removeMessages(InternalMessages internalMessages) {
        this.mainHandler.removeMessages(internalMessages.ordinal());
    }

    public void resetSession(String str) {
        SessionManager sessionManager = (SessionManager) this.registry.getManagerReference(SessionManager.COMMAND_FAMILY);
        if (sessionManager != null) {
            sessionManager.resetSession(str);
        }
    }

    public void sendCommand(Command command) {
        this.commandQueue.sendCommand(command);
    }

    public void sendMessageToHost(InternalMessages internalMessages) {
        sendMessageToHost(Message.obtain((Handler) null, internalMessages.ordinal()));
    }

    public void sendMessageToHost(InternalMessages internalMessages, Bundle bundle) {
        sendMessageToHost(internalMessages, (Object) bundle);
    }

    public void sendMessageToHost(InternalMessages internalMessages, Object obj) {
        Bundle bundle;
        Message obtain = Message.obtain((Handler) null, internalMessages.ordinal());
        Bundle bundle2 = new Bundle();
        if (obj instanceof String) {
            bundle2.putString(Strings.DEFAULT_KEY, (String) obj);
            bundle = bundle2;
        } else if (obj instanceof Bundle) {
            bundle = (Bundle) obj;
        } else if (obj instanceof Boolean) {
            bundle2.putBoolean(Strings.DEFAULT_KEY, ((Boolean) obj).booleanValue());
            bundle = bundle2;
        } else if (obj instanceof Integer) {
            bundle2.putInt(Strings.DEFAULT_KEY, ((Integer) obj).intValue());
            bundle = bundle2;
        } else if (obj instanceof Long) {
            bundle2.putLong(Strings.DEFAULT_KEY, ((Long) obj).longValue());
            bundle = bundle2;
        } else {
            this.log.e("sendMessageToHost(", internalMessages, ", ", obj != null ? obj.getClass().getSimpleName() : null, ") Error attaching Object, unsupported type.");
            bundle = bundle2;
        }
        obtain.setData(bundle);
        sendMessageToHost(obtain);
    }

    public void sendMessageToHostDelayed(final Message message, int i) {
        Runnable runnable = new Runnable() { // from class: com.nuance.connect.service.ConnectClient.13
            @Override // java.lang.Runnable
            public void run() {
                ConnectClient.this.sendMessageToHost(message);
            }
        };
        idleSnooze(i);
        getHandler().postDelayed(runnable, i);
    }

    public void sendMessageToHostDelayed(final InternalMessages internalMessages, int i) {
        Runnable runnable = new Runnable() { // from class: com.nuance.connect.service.ConnectClient.12
            @Override // java.lang.Runnable
            public void run() {
                ConnectClient.this.sendMessageToHost(internalMessages);
            }
        };
        idleSnooze(i);
        getHandler().postDelayed(runnable, i);
    }

    public void sendTransaction(Transaction transaction) {
        this.commandQueue.sendTransaction(transaction);
    }

    public synchronized void setCurrentLanguageInfo(int[] iArr) {
        this.activeLanguages = iArr;
        if (iArr != null) {
            Iterator<WeakReference<LanguageListener>> it = this.languageListeners.iterator();
            while (it.hasNext()) {
                WeakReference<LanguageListener> next = it.next();
                LanguageListener languageListener = next.get();
                if (languageListener != null) {
                    languageListener.onLanguageUpdate(iArr);
                } else {
                    this.languageListeners.remove(next);
                }
            }
        }
    }

    public void setCurrentLocaleInfo(Locale locale) {
        if (locale == null || locale.equals(this.activeLocale)) {
            return;
        }
        this.activeLocale = locale;
        Iterator<WeakReference<LanguageListener>> it = this.languageListeners.iterator();
        while (it.hasNext()) {
            WeakReference<LanguageListener> next = it.next();
            LanguageListener languageListener = next.get();
            if (languageListener != null) {
                languageListener.onLocaleUpdate(locale);
            } else {
                this.languageListeners.remove(next);
            }
        }
    }

    public void setProperty(ConnectConfiguration.ConfigProperty configProperty, Boolean bool) {
        getConfiguration().setProperty(configProperty, bool);
    }

    public void setProperty(ConnectConfiguration.ConfigProperty configProperty, Integer num) {
        getConfiguration().setProperty(configProperty, num);
    }

    public void setProperty(ConnectConfiguration.ConfigProperty configProperty, Long l) {
        getConfiguration().setProperty(configProperty, l);
    }

    public void setProperty(ConnectConfiguration.ConfigProperty configProperty, String str) {
        getConfiguration().setProperty(configProperty, str);
    }

    protected void startNextManager() {
        this.log.v("startNextManager()");
        Manager nextPendingManager = this.registry.getNextPendingManager();
        if (nextPendingManager != null) {
            this.log.v("startNextManager() -- ", nextPendingManager.getManagerName());
            nextPendingManager.start();
        } else {
            if (!this.registry.allStarted() || this.clientStartComplete) {
                return;
            }
            this.log.i("All managers started. Notifying host.");
            sendMessageToHost(InternalMessages.MESSAGE_HOST_CLIENT_START_COMPLETE);
            this.registry.postStart();
            this.clientStartComplete = true;
            this.log.i("ConnectClient started");
        }
    }

    public void taskCompletedAcknowledgement(String str) {
        if (str == null || !this.taskToProcess.contains(str)) {
            return;
        }
        this.taskToProcess.remove(str);
        if (this.taskToProcess.size() == 0) {
            sendMessageToHostDelayed(getMessage(InternalMessages.MESSAGE_HOST_POLL_COMPLETE, Boolean.TRUE), 3000);
        }
    }
}
