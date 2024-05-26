package com.nuance.connect.service.manager;

import android.os.Bundle;
import android.os.Message;
import android.util.Pair;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.Response;
import com.nuance.connect.comm.ResponseCallback;
import com.nuance.connect.comm.Transaction;
import com.nuance.connect.internal.GenericProperty;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.interfaces.CommandManager;
import com.nuance.connect.service.manager.interfaces.Manager;
import com.nuance.connect.service.manager.interfaces.MessageProcessor;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.TimeConversion;
import com.nuance.connect.util.VersionUtils;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public abstract class AbstractCommandManager implements CommandManager, Manager, MessageProcessor {
    public static final int ANTI_SPAM_DELAY = 5;
    public static final String COMMAND_ACK = "ack";
    public static final String COMMAND_DOWNLOAD = "download";
    public static final String COMMAND_GET = "get";
    public static final String COMMAND_LIST = "list";
    static final int[] COMMAND_RESPONSE_SUCCESS = {1};
    public static final String COMMAND_STATUS = "status";
    public static final int MAX_RETRIES_BEFORE_FAILURE = 3;
    protected ConnectClient client;
    protected String commandFamily;
    protected int dependentCount;
    protected String lastCommand;
    protected int lastCommandRetryCount;
    protected int[] messages;
    protected int version;
    protected ManagerState managerStartState = ManagerState.DISABLED;
    protected DownloadState managerDownloadListState = DownloadState.DOWNLOAD_LIST_STATE_UNKNOWN;
    private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final GenericProperty.BooleanProperty idleProperty = new GenericProperty.BooleanProperty(getClass().getSimpleName(), false);
    protected final Map<String, Transaction> activeTransactions = new ConcurrentHashMap();
    protected ValidCommands validCommands = new ValidCommands();

    /* loaded from: classes.dex */
    public enum DownloadState {
        DOWNLOAD_LIST_STATE_UNKNOWN,
        DOWNLOAD_LIST_STATE_AVAILABLE,
        DOWNLOAD_LIST_STATE_NONE;

        public static DownloadState from(String str) {
            if (str != null) {
                try {
                    return valueOf(str);
                } catch (IllegalArgumentException e) {
                    Logger.getLog(Logger.LoggerType.DEVELOPER, DownloadState.class.getSimpleName()).e("DownloadState name incorrect: " + str);
                }
            }
            return DOWNLOAD_LIST_STATE_UNKNOWN;
        }
    }

    /* loaded from: classes.dex */
    public enum ManagerState {
        DISABLED,
        STARTING,
        STARTED
    }

    /* loaded from: classes.dex */
    public static class ValidCommands {
        private HashMap<String, int[]> commands = new HashMap<>();

        public void addCommand(String str, int[] iArr) {
            this.commands.put(str, iArr);
        }

        public String getName(String str) {
            return this.commands.containsKey(str) ? str : "";
        }

        public int[] getResponses(String str) {
            return !this.commands.containsKey(str) ? new int[0] : this.commands.get(str);
        }

        public boolean hasName(String str) {
            return this.commands.containsKey(str);
        }

        public boolean isCommandFor(String str, Command command) {
            return hasName(str) && str.equals(command.command);
        }

        public boolean isExpectedResponse(String str, int i) {
            for (int i2 : getResponses(str)) {
                if (i2 == i) {
                    return true;
                }
            }
            return false;
        }

        public boolean isResponseFor(String str, Response response) {
            return hasName(str) && str.equals(response.command);
        }
    }

    public AbstractCommandManager(ConnectClient connectClient) {
        this.client = connectClient;
    }

    @Override // com.nuance.connect.service.manager.interfaces.CommandManager
    public abstract void alarmNotification(String str, Bundle bundle);

    /* JADX INFO: Access modifiers changed from: protected */
    public long calcDefaultMilliDelay() {
        return TimeConversion.convertSecondsToMillis(this.client.getInteger(ConnectConfiguration.ConfigProperty.DEFAULT_DELAY).intValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public long calcMessageSendDelay() {
        return (long) (Math.pow(2.0d, this.lastCommandRetryCount) * TimeConversion.convertSecondsToMillis(5L));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Command createCommand(String str, Command.REQUEST_TYPE request_type) {
        return createCommand(str, request_type, new HashMap<>());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Command createCommand(String str, Command.REQUEST_TYPE request_type, ResponseCallback responseCallback) {
        return createCommand(str, request_type, new HashMap<>(), responseCallback);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Command createCommand(String str, Command.REQUEST_TYPE request_type, HashMap<String, Object> hashMap) {
        return createCommand(str, request_type, hashMap, this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Command createCommand(String str, Command.REQUEST_TYPE request_type, HashMap<String, Object> hashMap, ResponseCallback responseCallback) {
        if (hashMap == null) {
            return createCommand(str, request_type);
        }
        if (!this.validCommands.hasName(str)) {
            return new Command();
        }
        Command command = new Command();
        command.commandFamily = this.commandFamily;
        command.version = this.version;
        command.command = str;
        command.parameters = hashMap;
        command.responseCallback = responseCallback;
        command.requestType = request_type;
        return command;
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void deregister() {
        this.managerStartState = ManagerState.DISABLED;
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void destroy() {
        this.managerStartState = ManagerState.DISABLED;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void finishTransaction(String str) {
        this.activeTransactions.remove(str);
        if (this.activeTransactions.isEmpty()) {
            this.idleProperty.setValue((Boolean) true, Property.Source.DEFAULT);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Transaction getActiveTransaction(String str) {
        for (Map.Entry<String, Transaction> entry : this.activeTransactions.entrySet()) {
            if (entry.getKey().equals(str)) {
                return entry.getValue();
            }
        }
        this.log.v("Transaction not found for ID: ", str);
        return null;
    }

    @Override // com.nuance.connect.service.manager.interfaces.CommandManager
    public String getCommandFamily() {
        return this.commandFamily;
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
        return this.commandFamily;
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public int getManagerPollInterval() {
        return this.client.getInteger(ConnectConfiguration.ConfigProperty.DEFAULT_POLLING_INTERVAL_NO_FEATURES).intValue();
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public ManagerState getManagerStartState() {
        return this.managerStartState;
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public int[] getMessageIDs() {
        return (int[]) this.messages.clone();
    }

    @Override // com.nuance.connect.service.manager.interfaces.CommandManager
    public List<Pair<String, String>> getRealTimeSubscriptions() {
        return null;
    }

    @Override // com.nuance.connect.service.manager.interfaces.CommandManager
    public int getVersion() {
        return this.version;
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void incrementDependentCount() {
        this.dependentCount++;
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public abstract void init();

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isTransactionActive(String str) {
        Iterator<Map.Entry<String, Transaction>> it = this.activeTransactions.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getKey().equals(str)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void managerStartComplete() {
        this.log.i("managerStartComplete()");
        this.managerStartState = ManagerState.STARTED;
        this.client.managerStartComplete(getManagerName());
        this.idleProperty.setValue((Boolean) true, Property.Source.DEFAULT);
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onCancel(Command command) {
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onDownloadStatusResponse(Command command, int i, int i2) {
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onFailure(Command command) {
        this.log.d("Abstract.onFailure(" + command + ")");
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onFileResponse(Response response) {
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        return false;
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onIOExceptionResponse(Command command) {
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onResponse(Response response) {
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public boolean onRetry(Command command, int i, int i2, String str) {
        return true;
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

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean preProcessResponse(Response response, int i) {
        preProcessResponse(response.command, response.status, i);
        return false;
    }

    protected boolean preProcessResponse(String str, int i, int i2) {
        if (!this.validCommands.hasName(str) || this.validCommands.isExpectedResponse(str, i) || this.lastCommandRetryCount > 3) {
            return false;
        }
        this.client.postMessageDelayed(InternalMessages.fromInt(i2), calcMessageSendDelay());
        this.log.i("Response from server is not an expected response. " + this.commandFamily + XMLResultsHandler.SEP_SPACE + str + " Response Status: " + i);
        return true;
    }

    protected void printActiveTransactions() {
        this.log.v(getClass().getSimpleName(), "; printActiveTransactions");
        for (Map.Entry<String, Transaction> entry : this.activeTransactions.entrySet()) {
            this.log.v("    Name=", entry.getKey(), ", Value=", entry.getValue());
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void rebind() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removePreference(String str) {
        this.client.getDataStore().delete(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removePreferences(String[] strArr) {
        for (String str : strArr) {
            this.client.getDataStore().delete(str);
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public void restart() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sendCommand(Command command) {
        this.log.d(getClass().getSimpleName() + ".sendCommand() - " + command.commandFamily + "/" + command.version + "/" + command.command);
        if (this.managerStartState.equals(ManagerState.DISABLED)) {
            this.log.w("sendCommand() attempting to send command when " + getClass().getName() + " has not completed starting. Attempted command:" + command.commandFamily + "/" + command.version + "/" + command.command);
        } else {
            setLastCommand(command.command);
            this.client.sendCommand(command);
        }
    }

    protected void setLastCommand(String str) {
        if (str.equals(this.lastCommand)) {
            this.lastCommandRetryCount++;
        } else {
            this.lastCommandRetryCount = 0;
        }
        this.lastCommand = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setMessagesHandled(InternalMessages[] internalMessagesArr) {
        this.messages = new int[internalMessagesArr.length];
        for (int i = 0; i < internalMessagesArr.length; i++) {
            this.messages[i] = internalMessagesArr[i].ordinal();
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public abstract void start();

    /* JADX INFO: Access modifiers changed from: protected */
    public void startTransaction(Transaction transaction) {
        startTransactionWithAck(transaction);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean startTransactionWithAck(Transaction transaction) {
        if (transaction == null) {
            this.log.e("Transaction null!");
            return false;
        }
        if (transaction.allowDuplicates()) {
            throw new UnsupportedOperationException("Not supported yet");
        }
        if (transaction.getName() == null) {
            throw new NullPointerException("Transactions must have a name.");
        }
        if (!transaction.allowDuplicates() && isTransactionActive(transaction.getName())) {
            this.log.d("transaction is already in progress: " + transaction.getName());
            return false;
        }
        this.activeTransactions.put(transaction.getName(), transaction);
        this.idleProperty.setValue((Boolean) false, Property.Source.DEFAULT);
        this.client.sendTransaction(transaction);
        return true;
    }
}
