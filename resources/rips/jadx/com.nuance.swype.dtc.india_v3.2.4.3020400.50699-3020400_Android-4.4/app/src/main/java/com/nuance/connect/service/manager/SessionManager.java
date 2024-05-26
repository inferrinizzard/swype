package com.nuance.connect.service.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import com.nuance.connect.comm.AbstractTransaction;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.comm.Response;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.internal.common.ManagerService;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.Alarm;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import com.nuance.connect.util.TimeConversion;
import com.nuance.connect.util.VersionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;

/* loaded from: classes.dex */
public class SessionManager extends AbstractCommandManager {
    public static final String COMMAND_CREATE = "create";
    public static final String COMMAND_FAMILY;
    public static final String COMMAND_INVALIDATE = "invalidate";
    public static final String COMMAND_POLL = "poll";
    public static final String COMMAND_VALIDATE = "validate";
    public static final int COMMAND_VERSION = 2;
    public static final int DEFAULT_POLLING_INTERVAL_NO_FEATURES;
    public static final String MANAGER_NAME;
    private static final InternalMessages[] MESSAGES_HANDLED;
    private static final int POLLING_INTERVAL_NOT_SET = -1;
    private static final String SESSION_ID_KEY = "SESSION_ID";
    private static final String SESSION_LAST_POLL = "SESSION_LAST_POLL";
    private volatile int customerPollingInterval;
    private Property.ValueListener<Integer> intListener;
    private long lastPollTime;
    private Logger.Log log;
    private volatile PollMode pollMode;
    private volatile int pollingInterval;
    private volatile String sessionId;
    private int unlicensedPollingInterval;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum PollMode {
        AUTOMATIC,
        MANUAL
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PollTransaction extends AbstractTransaction {
        private boolean failure;
        private boolean userInitiated;

        PollTransaction(boolean z) {
            this.userInitiated = z;
            this.currentCommand = SessionManager.this.createCommand(SessionManager.COMMAND_POLL, getRequestType());
            this.currentCommand.allowDuplicateOfCommand = false;
            this.currentCommand.handleIOExceptionInConnector = z ? false : true;
            this.currentCommand.responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.SessionManager.PollTransaction.1
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super();
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onFailure(Command command) {
                    SessionManager.this.log.d(PollTransaction.this.getName() + " onFailure()");
                    PollTransaction.this.failure = true;
                    super.onFailure(command);
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onIOExceptionResponse(Command command) {
                    SessionManager.this.log.d(PollTransaction.this.getName() + " onIOExceptionResponse()");
                    PollTransaction.this.failure = true;
                    super.onIOExceptionResponse(command);
                }

                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    PollTransaction.this.processPollResponse(response);
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public boolean onRetry(Command command, int i, int i2, String str) {
                    if (PollTransaction.this.failure) {
                        return false;
                    }
                    return super.onRetry(command, i, i2, str);
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void processPollResponse(Response response) {
            if (response.status == 1 && response.parameters.containsKey(MessageAPI.TASKS) && (response.parameters.get(MessageAPI.TASKS) instanceof JSONArray)) {
                try {
                    SessionManager.this.client.performTasks((JSONArray) response.parameters.get(MessageAPI.TASKS));
                } catch (Exception e) {
                    SessionManager.this.log.d("Exception processing poll responses");
                }
            }
            if (!this.failure) {
                SessionManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_POLL_COMPLETE, Boolean.TRUE);
            }
            if (!SessionManager.this.client.isLicensed()) {
                SessionManager.this.log.d("Poll succeeded, build is still unlicensed.");
                SessionManager.this.client.setProperty(ConnectConfiguration.ConfigProperty.LICENSING_LAST_CHECKIN, Long.valueOf(System.currentTimeMillis()));
            }
            this.currentCommand = null;
        }

        private void sendFailMessage() {
            SessionManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_POLL_COMPLETE, Boolean.FALSE);
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
            return "POLL_" + (this.userInitiated ? "USER" : "SYSTEM");
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command getNextCommand() {
            return this.currentCommand;
        }

        @Override // com.nuance.connect.comm.Transaction
        public int getPriority() {
            return this.userInitiated ? 10 : 0;
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command.REQUEST_TYPE getRequestType() {
            return this.userInitiated ? Command.REQUEST_TYPE.USER : Command.REQUEST_TYPE.CRITICAL;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onEndProcessing() {
            SessionManager.this.log.d("onEndProcessing()");
            super.onEndProcessing();
            SessionManager.this.finishTransaction(getName());
            if (this.failure) {
                sendFailMessage();
            }
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean onTransactionOfflineQueued() {
            return Command.REQUEST_TYPE.USER != getRequestType();
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onTransactionRejected(int i) {
            this.failure = true;
            super.onTransactionRejected(i);
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
            SessionManager.this.log.d("rollback()");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SessionCreateTransaction extends AbstractTransaction {
        private static final String TRANSACTION_NAME = "session/create";
        private String sessionId;

        private SessionCreateTransaction() {
            SessionManager.this.log.d("SessionCreateTransaction()");
            this.currentCommand = SessionManager.this.createCommand(SessionManager.COMMAND_CREATE, Command.REQUEST_TYPE.CRITICAL);
            this.currentCommand.requireSession = false;
            this.currentCommand.requireDevice = true;
            this.currentCommand.allowDuplicateOfCommand = false;
            this.currentCommand.responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.SessionManager.SessionCreateTransaction.1
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super();
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onFailure(Command command) {
                    SessionManager.this.log.d(SessionCreateTransaction.this.getName() + " onFailure()");
                    super.onFailure(command);
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onIOExceptionResponse(Command command) {
                    SessionManager.this.log.d(SessionCreateTransaction.this.getName() + " onIOExceptionResponse()");
                    super.onIOExceptionResponse(command);
                }

                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    SessionCreateTransaction.this.processCreateSessionResponse(response);
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void processCreateSessionResponse(Response response) {
            switch (response.status) {
                case 1:
                    this.sessionId = response.sessionId;
                    break;
            }
            this.currentCommand = null;
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
            return TRANSACTION_NAME;
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
            if (this.sessionId != null) {
                SessionManager.this.log.d("session created. sessionId: ", this.sessionId);
                SessionManager.this.resetSession(this.sessionId);
                if (SessionManager.this.managerStartState.equals(AbstractCommandManager.ManagerState.STARTING)) {
                    SessionManager.this.poll(false, false);
                    SessionManager.this.managerStartComplete();
                }
            }
            SessionManager.this.finishTransaction(getName());
            super.onEndProcessing();
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean requiresDeviceId() {
            return true;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean requiresSessionId() {
            return false;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
            this.sessionId = null;
        }
    }

    static {
        String name = ManagerService.SESSION.getName();
        COMMAND_FAMILY = name;
        MANAGER_NAME = name;
        DEFAULT_POLLING_INTERVAL_NO_FEATURES = (int) TimeUnit.DAYS.toSeconds(90L);
        MESSAGES_HANDLED = new InternalMessages[]{InternalMessages.MESSAGE_COMMAND_SESSION_CREATE, InternalMessages.MESSAGE_COMMAND_SESSION_POLL, InternalMessages.MESSAGE_CLIENT_MANUAL_POLL};
    }

    public SessionManager(ConnectClient connectClient) {
        super(connectClient);
        this.pollingInterval = DEFAULT_POLLING_INTERVAL_NO_FEATURES;
        this.customerPollingInterval = -1;
        this.pollMode = PollMode.AUTOMATIC;
        this.intListener = new Property.IntegerValueListener() { // from class: com.nuance.connect.service.manager.SessionManager.1
            @Override // com.nuance.connect.internal.Property.ValueListener
            public void onValueChanged(Property<Integer> property) {
                if (property.getKey().equals(ConnectConfiguration.ConfigProperty.POLLING_FREQUENCY.name())) {
                    SessionManager.this.pollingInterval = property.getValue().intValue();
                    SessionManager.this.log.d("pollingChangeListener.pollingInterval(" + property + ")");
                } else if (property.getKey().equals(ConnectConfiguration.ConfigProperty.CUSTOMER_POLLING_FREQUENCY.name())) {
                    SessionManager.this.customerPollingInterval = property.getValue().intValue();
                    SessionManager.this.log.d("pollingChangeListener.customerPollingInterval(" + property + ")");
                    if (SessionManager.this.customerPollingInterval == 0) {
                        SessionManager.this.pollMode = PollMode.MANUAL;
                        SessionManager.this.determineNextPoll();
                    }
                } else if (property.getKey().equals(ConnectConfiguration.ConfigProperty.LICENSING_SERVER_DELAY.name())) {
                    SessionManager.this.loadUnlicensedPollInterval();
                }
                if (SessionManager.this.pollMode == PollMode.AUTOMATIC && SessionManager.this.managerStartState.equals(AbstractCommandManager.ManagerState.STARTED)) {
                    SessionManager.this.poll(false, false);
                }
            }
        };
        this.log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
        this.version = 2;
        this.commandFamily = COMMAND_FAMILY;
        setMessagesHandled(MESSAGES_HANDLED);
        this.validCommands.addCommand(COMMAND_CREATE, new int[]{1, 17});
        this.validCommands.addCommand("validate", new int[]{1, 0});
        this.validCommands.addCommand(COMMAND_INVALIDATE, new int[]{1, 0});
        this.validCommands.addCommand(COMMAND_POLL, new int[]{1, 0});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void determineNextPoll() {
        long convertSecondsToTimeStamp = TimeConversion.convertSecondsToTimeStamp(getPollInterval(), this.lastPollTime);
        if (convertSecondsToTimeStamp <= System.currentTimeMillis()) {
            convertSecondsToTimeStamp = TimeConversion.convertSecondsToTimeStamp(getPollInterval());
        }
        Alarm build = this.client.getAlarmBuilder(SessionManager.class, COMMAND_POLL).triggerTime(convertSecondsToTimeStamp).build();
        build.cancel();
        if (this.pollMode != PollMode.AUTOMATIC || getPollInterval() <= 0) {
            this.log.v("polling manually; not scheduling next poll");
        } else {
            build.set();
            this.log.v("determineNextPoll " + build.toString());
        }
    }

    private int getPollInterval() {
        return !this.client.isLicensed() ? this.unlicensedPollingInterval : this.customerPollingInterval > this.pollingInterval ? this.customerPollingInterval : this.pollingInterval;
    }

    private void invalidateSession() {
        if (getSessionId() == null) {
            return;
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(MessageAPI.SESSION_ID, getSessionId());
        Command createCommand = createCommand(COMMAND_INVALIDATE, Command.REQUEST_TYPE.CRITICAL, hashMap);
        createCommand.requireSession = true;
        createCommand.requireDevice = true;
        sendCommand(createCommand);
    }

    private synchronized void loadPreferences() {
        PersistentDataStore dataStore = this.client.getDataStore();
        this.sessionId = dataStore.readString(SESSION_ID_KEY, null);
        this.lastPollTime = dataStore.readLong(SESSION_LAST_POLL, System.currentTimeMillis());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadUnlicensedPollInterval() {
        Integer integer = this.client.getInteger(ConnectConfiguration.ConfigProperty.LICENSING_SERVER_DELAY);
        if (integer.intValue() >= -1) {
            if (integer.intValue() == 0) {
                this.unlicensedPollingInterval = this.client.getInteger(ConnectConfiguration.ConfigProperty.LICENSING_DEFAULT_DELAY).intValue();
            } else if (integer.intValue() > 0) {
                this.unlicensedPollingInterval = integer.intValue();
            } else if (integer.intValue() == -1) {
                this.unlicensedPollingInterval = 0;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void poll(boolean z, boolean z2) {
        this.log.d("poll(" + z + ", " + z2 + ")");
        this.log.d("last poll: ", TimeConversion.prettyDateFormat(this.lastPollTime));
        this.log.d("next poll: ", TimeConversion.prettyDateFormat(TimeConversion.convertSecondsToTimeStamp(getPollInterval(), this.lastPollTime)));
        if (getSessionId() != null && (System.currentTimeMillis() >= TimeConversion.convertSecondsToTimeStamp(getPollInterval(), this.lastPollTime) || ((z2 && System.currentTimeMillis() >= TimeConversion.convertSecondsToTimeStamp(this.client.getInteger(ConnectConfiguration.ConfigProperty.MANUAL_REFRESH_THROTTLE).intValue(), this.lastPollTime)) || z))) {
            this.client.postMessage(InternalMessages.MESSAGE_COMMAND_FORCE_GEO_IP_LOOKUP);
            this.client.postMessage(InternalMessages.MESSAGE_COMMAND_DEVICE_UPDATE, (Object) true);
            startTransaction(new PollTransaction(z2));
            this.lastPollTime = System.currentTimeMillis();
            savePreferences();
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_POLL_REQUESTED);
        } else if (z2) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Strings.DEFAULT_KEY, Boolean.FALSE.booleanValue());
            bundle.putLong("DEFAULT_DELAY", TimeConversion.convertSecondsToTimeStamp(getPollInterval(), this.lastPollTime));
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_POLL_COMPLETE, bundle);
        }
        determineNextPoll();
    }

    private void processInvalidateSessionResponse(Response response) {
        if (response.status == 1) {
            resetSession(null);
        } else if (response.status == 0) {
            invalidateSession();
        }
    }

    private synchronized void savePreferences() {
        PersistentDataStore dataStore = this.client.getDataStore();
        dataStore.saveString(SESSION_ID_KEY, this.sessionId);
        dataStore.saveLong(SESSION_LAST_POLL, this.lastPollTime);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.CommandManager
    public void alarmNotification(String str, Bundle bundle) {
        if (str.equals(COMMAND_POLL)) {
            poll(false, false);
        }
    }

    public void createSession() {
        if (getSessionId() != null) {
            this.log.w("session already exists, ignoring createSession()");
        } else if (isTransactionActive("session/create")) {
            this.log.w("session/create transaction already being processed");
        } else {
            startTransaction(new SessionCreateTransaction());
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void deregister() {
        super.deregister();
        synchronized (this) {
            this.sessionId = null;
            savePreferences();
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void destroy() {
        super.destroy();
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public String[] getDependencies() {
        ArrayList arrayList = new ArrayList();
        for (ManagerService managerService : ManagerService.SESSION.values()) {
            arrayList.add(managerService.getName());
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public synchronized String getSessionId() {
        return this.sessionId;
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void init() {
        loadPreferences();
        this.client.addListener(ConnectConfiguration.ConfigProperty.POLLING_FREQUENCY, this.intListener);
        this.client.addListener(ConnectConfiguration.ConfigProperty.CUSTOMER_POLLING_FREQUENCY, this.intListener);
        this.client.addListener(ConnectConfiguration.ConfigProperty.LICENSING_SERVER_DELAY, this.intListener);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_COMMAND_SESSION_CREATE:
                this.log.d("MESSAGE_COMMAND_SESSION_CREATE");
                createSession();
                return true;
            case MESSAGE_COMMAND_SESSION_POLL:
                this.log.d("MESSAGE_COMMAND_SESSION_POLL");
                poll(message.getData().getBoolean(Strings.DEFAULT_KEY), message.getData().getBoolean(Strings.ACKNOWLEDGEMENT, false));
                return true;
            case MESSAGE_CLIENT_MANUAL_POLL:
                this.log.d("MESSAGE_CLIENT_MANUAL_POLL");
                poll(false, message.getData().getBoolean(Strings.ACKNOWLEDGEMENT, false));
                return true;
            default:
                return false;
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.comm.ResponseCallback
    public void onResponse(Response response) {
        if (this.validCommands.isResponseFor(COMMAND_INVALIDATE, response)) {
            processInvalidateSessionResponse(response);
        }
        savePreferences();
    }

    public void onUnlicensed() {
        this.lastPollTime = System.currentTimeMillis();
        savePreferences();
        loadUnlicensedPollInterval();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2, boolean z) {
        if (!VersionUtils.isDataCleanupRequiredOnUpgrade(version, version2) || StringUtils.isValidUUID(this.sessionId)) {
            poll(true, false);
            return;
        }
        synchronized (this) {
            this.sessionId = null;
            savePreferences();
        }
    }

    public synchronized void resetSession(String str) {
        this.log.d("set sessionId to: ", str);
        this.sessionId = str;
        savePreferences();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void start() {
        this.managerStartState = AbstractCommandManager.ManagerState.STARTING;
        if (getSessionId() == null) {
            createSession();
            return;
        }
        if (this.pollMode == PollMode.AUTOMATIC) {
            poll(false, false);
        }
        managerStartComplete();
    }
}
