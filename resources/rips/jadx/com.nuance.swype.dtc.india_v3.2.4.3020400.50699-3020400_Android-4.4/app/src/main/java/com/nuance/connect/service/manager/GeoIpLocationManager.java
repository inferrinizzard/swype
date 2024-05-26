package com.nuance.connect.service.manager;

import android.os.Bundle;
import android.os.Message;
import android.util.Pair;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.Response;
import com.nuance.connect.comm.SimpleTransaction;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.internal.common.ManagerService;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.util.Alarm;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.VersionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class GeoIpLocationManager extends AbstractCommandManager {
    public static final String ALARM_TYPE_NEXT_LOOKUP = "NEXT_LOOKUP";
    public static final String COMMAND_FAMILY;
    private static final String COMMAND_GEO_IP = "geoIP";
    public static final int COMMAND_VERSION = 1;
    private static final int DEFAULT_LOOKUP_INTERVAL = 604800;
    private static final String HEADER_CLIENT_CUSTOMER = "X_ClientCustomer";
    private static final String HEADER_CLIENT_TYPE = "X_ClientType";
    private static final String HEADER_CLIENT_VERSION = "X_ClientVersion";
    public static final String MANAGER_NAME;
    private static final InternalMessages[] MESSAGES_HANDLED;
    private static final int MIN_LOOKUP_INTERVAL = 3600;
    public static final String PREF_LAST_GEO_LOCATION_TIMESTAMP;
    private final Property.BooleanValueListener booleanValueListener;
    private boolean enabled;
    private final Property.IntegerValueListener integerValueListener;
    private String lastCountry;
    private long lastTimestamp;
    private final Logger.Log log;
    private int lookupInterval;
    private boolean lookupOnStart;
    private String serverUrl;
    private final Property.StringValueListener stringValueListener;

    static {
        String name = ManagerService.LOCATION_BASED_SERVICE.getName();
        COMMAND_FAMILY = name;
        MANAGER_NAME = name;
        PREF_LAST_GEO_LOCATION_TIMESTAMP = COMMAND_FAMILY + "_PREF_LAST_GEO_LOCATION_TIMESTAMP";
        MESSAGES_HANDLED = new InternalMessages[]{InternalMessages.MESSAGE_COMMAND_FORCE_GEO_IP_LOOKUP};
    }

    public GeoIpLocationManager(ConnectClient connectClient) {
        super(connectClient);
        this.booleanValueListener = new Property.BooleanValueListener() { // from class: com.nuance.connect.service.manager.GeoIpLocationManager.1
            @Override // com.nuance.connect.internal.Property.ValueListener
            public void onValueChanged(Property<Boolean> property) {
                if (property.getKey().equals(ConnectConfiguration.ConfigProperty.LOCATION_SERVICE_ENABLED.name())) {
                    GeoIpLocationManager.this.log.d("LOCATION_SERVICE_ENABLED ", property.getValue());
                    GeoIpLocationManager.this.enabled = property.getValue().booleanValue();
                }
            }
        };
        this.integerValueListener = new Property.IntegerValueListener() { // from class: com.nuance.connect.service.manager.GeoIpLocationManager.2
            @Override // com.nuance.connect.internal.Property.ValueListener
            public void onValueChanged(Property<Integer> property) {
                if (property.getKey().equals(ConnectConfiguration.ConfigProperty.LOCATION_SERVICE_LOOKUP_INTERVAL.name())) {
                    GeoIpLocationManager.this.lookupInterval = property.getValue().intValue() > 3600 ? property.getValue().intValue() : 604800;
                }
            }
        };
        this.stringValueListener = new Property.StringValueListener() { // from class: com.nuance.connect.service.manager.GeoIpLocationManager.3
            @Override // com.nuance.connect.internal.Property.ValueListener
            public void onValueChanged(Property<String> property) {
                if (property.getKey().equals(ConnectConfiguration.ConfigProperty.LOCATION_SERVICE_SERVER_URL.name())) {
                    GeoIpLocationManager.this.serverUrl = property.getValue();
                } else if (property.getKey().equals(ConnectConfiguration.ConfigProperty.LOCATION_GEO_IP_COUNTRY.name())) {
                    GeoIpLocationManager.this.lastCountry = property.getValue();
                }
            }
        };
        this.log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
        this.version = 1;
        this.commandFamily = COMMAND_FAMILY;
        setMessagesHandled(MESSAGES_HANDLED);
        this.validCommands.addCommand(COMMAND_GEO_IP, new int[0]);
    }

    private void loadPreferences() {
        this.lastTimestamp = this.client.getDataStore().readLong(PREF_LAST_GEO_LOCATION_TIMESTAMP, Long.MIN_VALUE);
    }

    private void requestGeoLocation(boolean z) {
        this.log.d("requestGeoLocation(", Boolean.valueOf(z), ")");
        if (this.managerStartState != AbstractCommandManager.ManagerState.STARTED && this.managerStartState != AbstractCommandManager.ManagerState.STARTING) {
            this.log.d("Manager has not started, ignoring geolocation request.");
            return;
        }
        if (!this.enabled || (!z && this.lastCountry != null && this.lastTimestamp != Long.MIN_VALUE && System.currentTimeMillis() - this.lastTimestamp <= TimeUnit.SECONDS.toMillis(this.lookupInterval))) {
            if (!this.enabled || this.lastTimestamp == Long.MIN_VALUE || System.currentTimeMillis() - this.lastTimestamp >= TimeUnit.SECONDS.toMillis(this.lookupInterval)) {
                return;
            }
            setNextLookupAlarm(System.currentTimeMillis() - this.lastTimestamp);
            return;
        }
        Command createCommand = createCommand(COMMAND_GEO_IP, Command.REQUEST_TYPE.CRITICAL);
        createCommand.thirdPartyURL = this.serverUrl + createCommand.version + "/" + createCommand.command;
        createCommand.allowDuplicateOfCommand = false;
        createCommand.hasBody = false;
        createCommand.method = "GET";
        createCommand.extraHeaders = new HashMap(3);
        createCommand.extraHeaders.put(HEADER_CLIENT_TYPE, "SC-SDK");
        createCommand.extraHeaders.put(HEADER_CLIENT_CUSTOMER, this.client.getString(ConnectConfiguration.ConfigProperty.OEM_ID));
        createCommand.extraHeaders.put(HEADER_CLIENT_VERSION, this.client.getString(ConnectConfiguration.ConfigProperty.SDK_VERSION));
        createCommand.requireDevice = false;
        createCommand.needDevice = false;
        createCommand.requireSession = false;
        this.log.d("url: ", createCommand.thirdPartyURL);
        startTransaction(new SimpleTransaction(createCommand) { // from class: com.nuance.connect.service.manager.GeoIpLocationManager.4
            private volatile String mLastCountry;
            private volatile long mLastTimestamp;
            private volatile boolean success;

            @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
            public void onEndProcessing() {
                GeoIpLocationManager.this.lastTimestamp = this.mLastTimestamp;
                GeoIpLocationManager.this.client.getDataStore().saveLong(GeoIpLocationManager.PREF_LAST_GEO_LOCATION_TIMESTAMP, GeoIpLocationManager.this.lastTimestamp);
                if (this.success && (GeoIpLocationManager.this.lastCountry == null || !GeoIpLocationManager.this.lastCountry.equals(this.mLastCountry))) {
                    GeoIpLocationManager.this.log.d("Country has changed: ", GeoIpLocationManager.this.lastCountry, " new value: ", this.mLastCountry);
                    GeoIpLocationManager.this.lastCountry = this.mLastCountry;
                    GeoIpLocationManager.this.client.setProperty(ConnectConfiguration.ConfigProperty.LOCATION_GEO_IP_COUNTRY, GeoIpLocationManager.this.lastCountry);
                    GeoIpLocationManager.this.client.postMessage(InternalMessages.MESSAGE_COMMAND_DEVICE_UPDATE);
                }
                GeoIpLocationManager.this.setNextLookupAlarm(TimeUnit.SECONDS.toMillis(GeoIpLocationManager.this.lookupInterval));
                GeoIpLocationManager.this.finishTransaction(getName());
            }

            @Override // com.nuance.connect.comm.SimpleTransaction, com.nuance.connect.comm.ResponseCallback
            public void onResponse(Response response) {
                super.onResponse(response);
                this.success = true;
                this.mLastCountry = (String) response.parameters.get("country");
                GeoIpLocationManager.this.log.d("requestGeoLocationResponse() country: ", this.mLastCountry);
                this.mLastTimestamp = System.currentTimeMillis();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNextLookupAlarm(long j) {
        Alarm build = this.client.getAlarmBuilder(getClass(), ALARM_TYPE_NEXT_LOOKUP).millis((int) j).build();
        build.cancel();
        build.set();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.CommandManager
    public void alarmNotification(String str, Bundle bundle) {
        if (ALARM_TYPE_NEXT_LOOKUP.equals(str)) {
            requestGeoLocation(false);
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public String[] getDependencies() {
        ArrayList arrayList = new ArrayList();
        for (ManagerService managerService : ManagerService.LOCATION_BASED_SERVICE.values()) {
            arrayList.add(managerService.getName());
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public Pair<String, Long> getLatestGeoIPCountry() {
        if (this.lastCountry != null) {
            return new Pair<>(this.lastCountry, Long.valueOf(this.lastTimestamp));
        }
        return null;
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void init() {
        loadPreferences();
        this.client.addListener(ConnectConfiguration.ConfigProperty.LOCATION_SERVICE_ENABLED, this.booleanValueListener);
        this.client.addListener(ConnectConfiguration.ConfigProperty.LOCATION_SERVICE_LOOKUP_INTERVAL, this.integerValueListener);
        this.client.addListener(ConnectConfiguration.ConfigProperty.LOCATION_SERVICE_SERVER_URL, this.stringValueListener);
        this.client.addListener(ConnectConfiguration.ConfigProperty.LOCATION_GEO_IP_COUNTRY, this.stringValueListener);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_COMMAND_FORCE_GEO_IP_LOOKUP:
                this.log.d("MESSAGE_COMMAND_FORCE_GEO_IP_LOOKUP");
                if (getManagerStartState().equals(AbstractCommandManager.ManagerState.DISABLED)) {
                    this.lookupOnStart = true;
                    return true;
                }
                requestGeoLocation(true);
                return true;
            default:
                return false;
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putString("LOCATION_COUNTRY", this.client.getConfiguration().getString(ConnectConfiguration.ConfigProperty.LOCATION_GEO_IP_COUNTRY));
        bundle.putLong(Strings.LOCATION_TIME_EPOC, this.lastTimestamp);
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATALOG_LOCATION_CHANGED, bundle);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void start() {
        this.managerStartState = AbstractCommandManager.ManagerState.STARTING;
        requestGeoLocation(this.lookupOnStart);
        this.lookupOnStart = false;
        managerStartComplete();
    }
}
