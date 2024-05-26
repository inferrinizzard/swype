package com.nuance.connect.service.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import com.nuance.connect.comm.AbstractTransaction;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.comm.Response;
import com.nuance.connect.comm.SimpleTransaction;
import com.nuance.connect.comm.Transaction;
import com.nuance.connect.comm.TransactionException;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.internal.common.ManagerService;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.util.BuildProps;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import com.nuance.connect.util.UUIDFactory;
import com.nuance.connect.util.VersionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class DeviceManager extends AbstractCommandManager {
    public static final String COMMAND_CONFIG = "getConfig";
    public static final String COMMAND_FAMILY;
    public static final String COMMAND_GET_SWYPER_ID = "getSwyperId";
    public static final String COMMAND_REGISTER = "register";
    public static final String COMMAND_UPDATE = "update";
    public static final String COMMAND_VALIDATE = "validate";
    public static final int COMMAND_VERSION = 9;
    public static final String DEVICE_PREFERENCE = "DEVICE_ID";
    private static final String DEVICE_PROPERTIES_KEY_PREFIX = "DEVICE_PROPERTIES_";
    public static final String MANAGER_NAME;
    private static final InternalMessages[] MESSAGES_HANDLED;
    public static final List<String> REGISTRATION_COMMANDS;
    public static final String SWYPER_ID_PREFERENCE = "SWYPER_ID";
    private static final Logger.Log log;
    private volatile String deviceId;
    private final HashMap<String, String> deviceProperties;
    private boolean isUpgrade;
    private volatile boolean pendingPropertyUpdate;
    private volatile boolean runningPropertyUpdate;
    private volatile String swyperId;

    /* loaded from: classes.dex */
    public class DeviceRegistrationTransaction extends AbstractTransaction {
        private static final String DEVICE_TYPE = "Android";
        private final Map<String, String> buildProperties;
        private String sessionId;
        private boolean success;
        private String swyperId;
        private String transactionId;
        private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
        private String deviceId = UUIDFactory.generateUUID(System.currentTimeMillis()).toString();

        DeviceRegistrationTransaction(Map<String, String> map) {
            this.buildProperties = map;
            registerDevice();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void processRegisterDeviceAckResponse(Response response) {
            this.log.d("processRegisterDeviceAckResponse(", Integer.valueOf(response.status), ")");
            switch (response.status) {
                case 1:
                    this.sessionId = response.sessionId;
                    this.success = true;
                    this.currentCommand = null;
                    return;
                case 2:
                default:
                    reset();
                    throw new TransactionException("Register was unsuccessful. Server response status: " + response.status);
                case 3:
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void processRegisterDeviceResponse(Response response) {
            switch (response.status) {
                case 1:
                    this.deviceId = response.deviceId;
                    this.transactionId = response.transactionId;
                    this.swyperId = (String) response.parameters.get(MessageAPI.SWYPER_ID);
                    this.log.d("processRegisterDeviceResponse success. deviceId: ", this.deviceId, " transactionId: ", this.transactionId, " swyperId: ", this.swyperId);
                    registerDeviceAck();
                    return;
                case 2:
                default:
                    reset();
                    throw new TransactionException("Register was unsuccessful. Server response status: " + response.status);
                case 3:
                    return;
            }
        }

        private void registerDevice() {
            this.log.d("Begin device registration for deviceId ", this.deviceId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(MessageAPI.DEVICE_ID, this.deviceId);
            hashMap.put(MessageAPI.DEVICE_TYPE, DEVICE_TYPE);
            hashMap.put(MessageAPI.DEVICE_PROPERTIES, this.buildProperties);
            this.currentCommand = DeviceManager.this.createCommand("register", getRequestType(), hashMap);
            this.currentCommand.requireSession = false;
            this.currentCommand.requireDevice = false;
            this.currentCommand.needDevice = false;
            this.currentCommand.allowDuplicateOfCommand = false;
            this.currentCommand.responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.DeviceManager.DeviceRegistrationTransaction.1
                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onFailure(Command command) {
                    DeviceRegistrationTransaction.this.log.d("onFailure(", command.command, ")");
                    super.onFailure(command);
                }

                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    DeviceRegistrationTransaction.this.processRegisterDeviceResponse(response);
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public boolean onRetry(Command command, int i, int i2, String str) {
                    DeviceRegistrationTransaction.this.log.d("onRetry(", command.command, ", ", Integer.valueOf(i), ", ", Integer.valueOf(i2), ", ", str, ")");
                    return true;
                }
            };
        }

        private void reset() {
            this.deviceId = UUIDFactory.generateUUID(System.currentTimeMillis()).toString();
            this.transactionId = null;
            this.swyperId = null;
            this.sessionId = null;
            this.success = false;
            registerDevice();
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean allowDuplicates() {
            return false;
        }

        @Override // com.nuance.connect.comm.Transaction
        public void cancel() {
        }

        @Override // com.nuance.connect.comm.Transaction
        public String createDownloadFile(Context context) {
            return null;
        }

        @Override // com.nuance.connect.comm.Transaction
        public String getName() {
            return DeviceManager.COMMAND_FAMILY + "/register";
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command getNextCommand() {
            return this.currentCommand;
        }

        @Override // com.nuance.connect.comm.Transaction
        public int getPriority() {
            return 10;
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command.REQUEST_TYPE getRequestType() {
            return Command.REQUEST_TYPE.CRITICAL;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onEndProcessing() {
            if (this.success) {
                DeviceManager.this.deviceRegistrationComplete(this.deviceId, this.swyperId, this.sessionId);
            } else {
                this.log.d("Device registration failed! Retrying in ", Long.valueOf(DeviceManager.this.calcMessageSendDelay()), " milliseconds.");
                DeviceManager.this.client.postMessageDelayed(InternalMessages.MESSAGE_COMMAND_DEVICE_REGISTER, DeviceManager.this.calcMessageSendDelay());
            }
            DeviceManager.this.finishTransaction(getName());
            super.onEndProcessing();
        }

        public void registerDeviceAck() {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(MessageAPI.DEVICE_ID, this.deviceId);
            this.currentCommand = DeviceManager.this.createCommand("ack", getRequestType(), hashMap);
            this.currentCommand.transactionId = this.transactionId;
            this.currentCommand.requireSession = false;
            this.currentCommand.needDevice = false;
            this.currentCommand.requireDevice = false;
            this.currentCommand.allowDuplicateOfCommand = false;
            this.currentCommand.responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.DeviceManager.DeviceRegistrationTransaction.2
                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onFailure(Command command) {
                    DeviceRegistrationTransaction.this.log.d("onFailure(", command.command, ")");
                    super.onFailure(command);
                }

                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    DeviceRegistrationTransaction.this.processRegisterDeviceAckResponse(response);
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public boolean onRetry(Command command, int i, int i2, String str) {
                    DeviceRegistrationTransaction.this.log.d("onRetry(", command.command, ")");
                    return true;
                }
            };
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean requiresDeviceId() {
            return false;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean requiresSessionId() {
            return false;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
            this.currentCommand = null;
            this.success = false;
        }
    }

    static {
        String name = ManagerService.DEVICE.getName();
        COMMAND_FAMILY = name;
        MANAGER_NAME = name;
        REGISTRATION_COMMANDS = Arrays.asList("register", "ack");
        MESSAGES_HANDLED = new InternalMessages[]{InternalMessages.MESSAGE_COMMAND_DEVICE_REGISTER, InternalMessages.MESSAGE_COMMAND_DEVICE_UPDATE, InternalMessages.MESSAGE_COMMAND_REQUEST_TIMERS, InternalMessages.MESSAGE_COMMAND_DEVICE_CONFIG, InternalMessages.MESSAGE_CLIENT_SEND_DEVICE_PROPERTIES};
        log = Logger.getLog(Logger.LoggerType.DEVELOPER, DeviceManager.class.getSimpleName());
    }

    public DeviceManager(ConnectClient connectClient) {
        super(connectClient);
        this.runningPropertyUpdate = false;
        this.pendingPropertyUpdate = false;
        this.deviceProperties = new HashMap<>();
        this.version = 9;
        this.commandFamily = COMMAND_FAMILY;
        setMessagesHandled(MESSAGES_HANDLED);
        this.validCommands.addCommand("register", new int[]{1, 4, 15});
        this.validCommands.addCommand("validate", new int[]{1, 4});
        this.validCommands.addCommand("ack", new int[]{1, 0, 6, 15});
        this.validCommands.addCommand(COMMAND_UPDATE, new int[]{1, 4, 0});
        this.validCommands.addCommand(COMMAND_GET_SWYPER_ID, new int[]{1});
        this.validCommands.addCommand(COMMAND_CONFIG, new int[]{1});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deviceRegistrationComplete(String str, String str2, String str3) {
        log.d("Device registration complete! DeviceId[", str, "] swyperId[", str2, "]");
        synchronized (this) {
            this.deviceId = str;
            this.swyperId = str2;
            savePreferences();
        }
        this.client.removeMessages(InternalMessages.MESSAGE_COMMAND_DEVICE_REGISTER);
        if (str3 == null || str3.length() <= 0) {
            this.client.resetSession(null);
        } else {
            log.d("processRegisterDeviceAckResponse() sessionId:" + str3);
            this.client.resetSession(str3);
        }
        requestConfiguration();
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_SET_SWYPER_ID, str2);
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_SET_DEVICE_ID, str);
        managerStartComplete();
        updateDeviceRegistration(false);
    }

    private boolean isSame(String str, String str2) {
        if (str == null && str2 == null) {
            return true;
        }
        return str != null && str.equals(str2);
    }

    private void loadPreferences() {
        synchronized (this) {
            this.deviceId = this.client.getDataStore().readString(DEVICE_PREFERENCE, null);
            this.swyperId = this.client.getDataStore().readString(SWYPER_ID_PREFERENCE, null);
        }
        synchronized (this.deviceProperties) {
            this.deviceProperties.clear();
            this.deviceProperties.putAll(retrieveSerializedProperties());
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:105:0x019c, code lost:            r14.client.setProperty(com.nuance.connect.service.configuration.ConnectConfiguration.ConfigProperty.POLL_INTERVAL_PLATFORM_UPDATE, java.lang.Integer.valueOf(r9.getInt(r0)));     */
    /* JADX WARN: Code restructure failed: missing block: B:111:0x00ca, code lost:            com.nuance.connect.service.manager.DeviceManager.log.w("Server returned an unknown config ", r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x00c7, code lost:            switch(r2) {            case 0: goto L167;            case 1: goto L157;            case 2: goto L159;            case 3: goto L153;            case 4: goto L155;            case 5: goto L169;            case 6: goto L151;            default: goto L203;        };     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x01b0, code lost:            r14.client.setProperty(com.nuance.connect.service.configuration.ConnectConfiguration.ConfigProperty.MANUAL_REFRESH_THROTTLE, java.lang.Integer.valueOf(r9.getInt(r0)));     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x0174, code lost:            r14.client.setProperty(com.nuance.connect.service.configuration.ConnectConfiguration.ConfigProperty.POLL_INTERVAL_LANGUAGE_DOWNLOAD, java.lang.Integer.valueOf(r9.getInt(r0)));     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x0188, code lost:            r14.client.setProperty(com.nuance.connect.service.configuration.ConnectConfiguration.ConfigProperty.POLL_INTERVAL_CATALOG, java.lang.Integer.valueOf(r9.getInt(r0)));     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x014c, code lost:            r14.client.setProperty(com.nuance.connect.service.configuration.ConnectConfiguration.ConfigProperty.POLL_INTERVAL_CHINESE_DATABASE, java.lang.Integer.valueOf(r9.getInt(r0)));     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x0160, code lost:            r14.client.setProperty(com.nuance.connect.service.configuration.ConnectConfiguration.ConfigProperty.POLL_INTERVAL_LIVING_LANGUAGE, java.lang.Integer.valueOf(r9.getInt(r0)));     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x0138, code lost:            r14.client.setProperty(com.nuance.connect.service.configuration.ConnectConfiguration.ConfigProperty.POLL_INTERVAL_SYNC, java.lang.Integer.valueOf(r9.getInt(r0)));     */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0095 A[Catch: NumberFormatException -> 0x0071, JSONException -> 0x00d3, TRY_LEAVE, TryCatch #8 {JSONException -> 0x00d3, blocks: (B:13:0x0037, B:16:0x0041, B:142:0x006a, B:19:0x0076, B:54:0x007f, B:57:0x0087, B:58:0x008f, B:60:0x0095, B:64:0x00c0, B:65:0x00c4, B:66:0x00c7, B:111:0x00ca, B:113:0x00eb, B:116:0x00f6, B:119:0x0101, B:122:0x010c, B:125:0x0117, B:128:0x0122, B:131:0x012d, B:136:0x00d7, B:138:0x01c4, B:22:0x01cd, B:51:0x01d6, B:25:0x01de, B:48:0x01e7, B:28:0x01ef, B:45:0x01f8, B:31:0x0200, B:42:0x0209, B:34:0x0211, B:37:0x021a), top: B:12:0x0037 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void processRequestConfigurationResponse(com.nuance.connect.comm.Response r15) {
        /*
            Method dump skipped, instructions count: 886
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.service.manager.DeviceManager.processRequestConfigurationResponse(com.nuance.connect.comm.Response):void");
    }

    private void processRequestSwyperIdResponse(Response response) {
        if (response.status == 1) {
            synchronized (this) {
                this.swyperId = (String) response.parameters.get(MessageAPI.SWYPER_ID);
                savePreferences();
                this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_SET_SWYPER_ID, this.swyperId);
                this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_SET_DEVICE_ID, this.deviceId);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void processUpdateDeviceRegistrationResponse(Response response) {
        HashMap<String, String> hashMap;
        if (preProcessResponse(response, InternalMessages.MESSAGE_COMMAND_DEVICE_UPDATE.ordinal())) {
            return;
        }
        switch (response.status) {
            case 0:
                if (this.validCommands.getName(COMMAND_UPDATE).equals(this.lastCommand) && this.lastCommandRetryCount <= 3) {
                    this.client.postMessageDelayed(InternalMessages.MESSAGE_COMMAND_DEVICE_UPDATE, calcMessageSendDelay());
                    break;
                }
                break;
            case 1:
                HashMap hashMap2 = (HashMap) response.initialCommand.parameters.get(MessageAPI.DEVICE_PROPERTIES);
                synchronized (this.deviceProperties) {
                    if (hashMap2 != null) {
                        for (Map.Entry entry : hashMap2.entrySet()) {
                            this.deviceProperties.put(entry.getKey(), entry.getValue());
                        }
                    }
                    hashMap = new HashMap<>(this.deviceProperties);
                }
                saveSerializedProperties(hashMap);
                if (this.isUpgrade) {
                    this.client.setProperty(ConnectConfiguration.ConfigProperty.POSSIBLE_UPGRADE, Boolean.TRUE);
                    managerStartComplete();
                    this.isUpgrade = false;
                    break;
                }
                break;
            case 4:
                registerDevice();
                break;
        }
        synchronized (this) {
            this.runningPropertyUpdate = false;
        }
        if (this.pendingPropertyUpdate) {
            this.pendingPropertyUpdate = false;
            this.client.removeMessages(InternalMessages.MESSAGE_COMMAND_DEVICE_UPDATE);
            this.client.postMessageDelayed(InternalMessages.MESSAGE_COMMAND_DEVICE_UPDATE, 5000L);
        }
    }

    private void processValidateDeviceRegistrationResponse(Response response) {
        log.w("Device validation is currently unsupported.");
    }

    private synchronized void resetDevice() {
        synchronized (this) {
            this.deviceId = null;
            this.swyperId = null;
            savePreferences();
        }
        removePreferences(new String[]{DEVICE_PREFERENCE, SWYPER_ID_PREFERENCE});
        Transaction activeTransaction = getActiveTransaction(COMMAND_FAMILY + "/update");
        if (activeTransaction != null) {
            activeTransaction.cancel();
        }
    }

    private HashMap<String, String> retrieveSerializedProperties() {
        HashMap<String, String> hashMap = (HashMap) this.client.getDataStore().readObject(DEVICE_PROPERTIES_KEY_PREFIX);
        return hashMap == null ? new HashMap<>() : hashMap;
    }

    private synchronized void savePreferences() {
        if (this.swyperId != null) {
            this.client.getDataStore().saveString(SWYPER_ID_PREFERENCE, this.swyperId);
        } else {
            removePreference(SWYPER_ID_PREFERENCE);
        }
        if (this.deviceId != null) {
            this.client.getDataStore().saveString(DEVICE_PREFERENCE, this.deviceId);
        } else {
            removePreference(DEVICE_PREFERENCE);
        }
    }

    private void saveSerializedProperties(HashMap<String, String> hashMap) {
        this.client.getDataStore().saveObject(DEVICE_PROPERTIES_KEY_PREFIX, hashMap);
    }

    private boolean updateDeviceRegistration(boolean z) {
        boolean z2;
        BuildProps buildProps = this.client.getBuildProps();
        String[] compareDeviceProperties = buildProps.compareDeviceProperties(this.deviceProperties, null);
        HashMap<String, String> deviceProperties = buildProps.getDeviceProperties();
        final HashMap hashMap = new HashMap();
        if (compareDeviceProperties == null || compareDeviceProperties.length == 0) {
            return false;
        }
        int i = 0;
        boolean z3 = false;
        while (i < compareDeviceProperties.length) {
            String str = compareDeviceProperties[i];
            String str2 = deviceProperties.get(compareDeviceProperties[i]);
            if (deviceProperties.containsKey(str) && str2 != null && !str2.equals("")) {
                hashMap.put(str, str2);
                log.d("Updated property detected " + str + ":" + str2);
                if (str.equals(BuildProps.BuildProperty.SDK_VERSION.getKey()) && !isSame(str2, this.deviceProperties.get(str))) {
                    log.d("SDK_VERSION has updated. new SDK_VERSION: ", str2, " old SDK_VERSION: ", this.deviceProperties.get(str));
                    z2 = true;
                } else if (str.equals(BuildProps.BuildProperty.CUSTOMER_STRING.getKey()) && !isSame(str2, this.deviceProperties.get(str))) {
                    log.d("CUSTOMER_STRING has updated. new CUSTOMER_STRING: ", str2, " old CUSTOMER_STRING: ", this.deviceProperties.get(str));
                    z2 = true;
                } else if (str.equals(BuildProps.BuildProperty.APPLICATION_ID.getKey()) && !isSame(str2, this.deviceProperties.get(str))) {
                    log.d("APPLICATION_ID has updated. new APPLICATION_ID: ", str2, " old APPLICATION_ID: ", this.deviceProperties.get(str));
                    z2 = true;
                } else if ((str.equals(BuildProps.BuildProperty.CORE_VERSION_ALPHA.getKey()) || str.equals(BuildProps.BuildProperty.CORE_VERSION_CHINESE.getKey()) || str.equals(BuildProps.BuildProperty.CORE_VERSION_JAPANESE.getKey()) || str.equals(BuildProps.BuildProperty.CORE_VERSION_KOREAN.getKey())) && !isSame(str2, this.deviceProperties.get(str))) {
                    log.d(str, " has updated. new: ", str2, " old: ", this.deviceProperties.get(str));
                    z2 = true;
                }
                i++;
                z3 = z2;
            }
            z2 = z3;
            i++;
            z3 = z2;
        }
        if (!hashMap.isEmpty() && (z || !Collections.disjoint(buildProps.getCriticalProperties(), hashMap.keySet()))) {
            synchronized (this) {
                if (this.runningPropertyUpdate) {
                    this.pendingPropertyUpdate = true;
                    return false;
                }
                this.runningPropertyUpdate = true;
                HashMap<String, Object> hashMap2 = new HashMap<>();
                hashMap2.put(MessageAPI.DEVICE_PROPERTIES, hashMap);
                Command createCommand = createCommand(COMMAND_UPDATE, z3 ? Command.REQUEST_TYPE.CRITICAL : Command.REQUEST_TYPE.BACKGROUND, hashMap2);
                createCommand.requireSession = false;
                createCommand.requireDevice = true;
                createCommand.allowDuplicateOfCommand = false;
                startTransaction(new SimpleTransaction(createCommand) { // from class: com.nuance.connect.service.manager.DeviceManager.1
                    @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
                    public void onEndProcessing() {
                        DeviceManager.this.finishTransaction(getName());
                        DeviceManager.this.client.setProperty(ConnectConfiguration.ConfigProperty.DEVICE_PROPERTIES_UPDATED, StringUtils.listToString(hashMap.keySet(), ","));
                    }
                });
            }
        }
        return z3;
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.CommandManager
    public void alarmNotification(String str, Bundle bundle) {
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void deregister() {
        log.d("deregister()");
        super.deregister();
        resetDevice();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void destroy() {
        super.destroy();
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public String[] getDependencies() {
        ArrayList arrayList = new ArrayList();
        for (ManagerService managerService : ManagerService.DEVICE.values()) {
            arrayList.add(managerService.getName());
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public synchronized String getDeviceId() {
        return this.deviceId;
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void init() {
        loadPreferences();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        log.d("DeviceManager.onHandleMessage(" + message.what + ")");
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_COMMAND_DEVICE_REGISTER:
                log.d("onHandleMessage(MESSAGE_COMMAND_DEVICE_REGISTER)");
                registerDevice();
                return true;
            case MESSAGE_COMMAND_DEVICE_UPDATE:
                log.d("onHandleMessage(MESSAGE_COMMAND_DEVICE_UPDATE)");
                if (getDeviceId() == null) {
                    return true;
                }
                updateDeviceRegistration(message.getData().getBoolean(Strings.DEFAULT_KEY));
                return true;
            case MESSAGE_COMMAND_DEVICE_CONFIG:
                log.d("onHandleMessage(MESSAGE_COMMAND_DEVICE_CONFIG)");
                requestConfiguration();
                return true;
            case MESSAGE_CLIENT_SEND_DEVICE_PROPERTIES:
                log.d("onHandleMessage(MESSAGE_CLIENT_SEND_DEVICE_PROPERTIES)");
                if (getDeviceId() == null) {
                    return true;
                }
                updateDeviceRegistration(false);
                return true;
            default:
                return false;
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.comm.ResponseCallback
    public void onResponse(Response response) {
        if (this.validCommands.getName(COMMAND_UPDATE).equals(response.command)) {
            processUpdateDeviceRegistrationResponse(response);
            return;
        }
        if (this.validCommands.getName("validate").equals(response.command)) {
            processValidateDeviceRegistrationResponse(response);
            return;
        }
        if (this.validCommands.getName(COMMAND_CONFIG).equals(response.command)) {
            processRequestConfigurationResponse(response);
        } else if (this.validCommands.getName(COMMAND_GET_SWYPER_ID).equals(response.command)) {
            processRequestSwyperIdResponse(response);
        } else {
            log.e("Server returned unknown command: " + response.command);
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2, boolean z) {
        if (VersionUtils.isDataCleanupRequiredOnUpgrade(version, version2)) {
            log.d("Upgrading from version ", version.toString());
            if (getDeviceId() != null && !StringUtils.isValidUUID(this.deviceId)) {
                log.w("Device id is invalid. Resetting device.");
                this.client.invalidateDevice();
            }
            if (this.swyperId != null && !StringUtils.isValidSwyperId(this.swyperId)) {
                synchronized (this) {
                    this.swyperId = null;
                    removePreference(SWYPER_ID_PREFERENCE);
                }
            }
        }
        requestConfiguration();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void postStart() {
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void rebind() {
        log.d("DeviceManager.rebind()");
        if (!AbstractCommandManager.ManagerState.STARTED.equals(getManagerStartState()) || this.swyperId == null) {
            return;
        }
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_SET_SWYPER_ID, this.swyperId);
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_SET_DEVICE_ID, this.deviceId);
    }

    public void registerDevice() {
        if (getManagerStartState().equals(AbstractCommandManager.ManagerState.DISABLED)) {
            log.w("Attempting to register device before DeviceManager has started");
            return;
        }
        synchronized (this) {
            if (this.deviceId != null) {
                log.w("Attempting to register device when it is already registered.  If this is intentional, please de-register before registering.");
            } else if (getActiveTransaction(COMMAND_FAMILY + "/register") != null) {
                log.w("Attempting to register device when command is already being processed");
            } else {
                HashMap<String, String> deviceProperties = this.client.getBuildProps().getDeviceProperties();
                synchronized (this.deviceProperties) {
                    this.deviceProperties.clear();
                    this.deviceProperties.putAll(deviceProperties);
                }
                saveSerializedProperties(deviceProperties);
                startTransaction(new DeviceRegistrationTransaction(deviceProperties));
            }
        }
    }

    public void requestConfiguration() {
        Command createCommand = createCommand(COMMAND_CONFIG, Command.REQUEST_TYPE.CRITICAL);
        createCommand.allowDuplicateOfCommand = false;
        createCommand.requireSession = true;
        sendCommand(createCommand);
    }

    public void requestSwyperId() {
        Command createCommand = createCommand(COMMAND_GET_SWYPER_ID, Command.REQUEST_TYPE.CRITICAL);
        createCommand.requireSession = false;
        createCommand.requireDevice = true;
        sendCommand(createCommand);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void start() {
        log.i("start()");
        this.managerStartState = AbstractCommandManager.ManagerState.STARTING;
        if (getDeviceId() == null) {
            registerDevice();
            return;
        }
        this.isUpgrade = updateDeviceRegistration(false);
        synchronized (this) {
            if (this.swyperId == null) {
                requestSwyperId();
            } else if (this.swyperId != null) {
                this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_SET_SWYPER_ID, this.swyperId);
            }
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_SET_DEVICE_ID, this.deviceId);
        }
        if (this.isUpgrade) {
            return;
        }
        managerStartComplete();
    }
}
