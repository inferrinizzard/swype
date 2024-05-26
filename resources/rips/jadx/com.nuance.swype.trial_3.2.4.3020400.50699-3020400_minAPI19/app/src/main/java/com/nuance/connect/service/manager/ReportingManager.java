package com.nuance.connect.service.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Pair;
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
import com.nuance.connect.service.security.RequestKey;
import com.nuance.connect.sqlite.ReportingDataSource;
import com.nuance.connect.util.Alarm;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import com.nuance.connect.util.TimeConversion;
import com.nuance.connect.util.VersionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.json.JSONArray;
import org.json.JSONException;

/* loaded from: classes.dex */
public class ReportingManager extends AbstractCommandManager {
    public static final String COMMAND_AGGREGATE = "aggregate";
    public static final String COMMAND_FAMILY = "report";
    public static final String COMMAND_INDIVIDUAL = "individual";
    public static final String COMMAND_TRACKING = "tracking";
    public static final int COMMAND_VERSION = 5;
    public static final String MANAGER_NAME = "report";
    public static final String METRICS_ALLOWED_PREF = "REPORTING_METRICS_ALLOWED";
    public static final String REPORTING_EXPIRATION = "REPORTING_EXPIRATION";
    public static final String REPORTING_GENERIC = "REPORTING_GENERIC";
    private static final String REPORTING_LAST_RUN = "REPORTING_LAST_RUN";
    public static final String REPORTING_LIMIT = "REPORTING_LIMIT";
    public static final String REPORTING_TIMER = "REPORTING_TIMER";
    private static final int TRACKING_INTERVAL = 60;
    private final Property.BooleanValueListener booleanPropertyListener;
    private ReportingDataSource dataSource;
    private Property.ValueListener<Integer> intListener;
    private long lastReportingTime;
    private HashSet<String> metricsAllowed;
    private int reportingDataExpriation;
    private int reportingInterval;
    private long trackingNextRun;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ReportingManager.class.getSimpleName());
    private static final int[] MESSAGES_HANDLED = {InternalMessages.MESSAGE_SEND_REPORTING_NOW.ordinal(), InternalMessages.MESSAGE_CLIENT_GET_ALLOWED_REPORTING_METRICS.ordinal()};

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SendDataTransaction extends AbstractTransaction {
        private int individualCount = 0;
        private int transmittedCount = 0;
        private long lastAggregateTime = 0;
        private long lastIndividualTime = 0;
        private Queue<Command> commandQueue = new LinkedBlockingQueue();
        private final AbstractTransaction.AbstractResponseCallback trackingResponseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.ReportingManager.SendDataTransaction.1
            @Override // com.nuance.connect.comm.ResponseCallback
            public void onResponse(Response response) {
                ReportingManager.log.d("onResponse - tracking");
                SendDataTransaction.this.commandQueue.poll();
                if (response.parameters.containsKey(MessageAPI.METRICS) && (response.parameters.get(MessageAPI.METRICS) instanceof JSONArray)) {
                    JSONArray jSONArray = (JSONArray) response.parameters.get(MessageAPI.METRICS);
                    ReportingManager.this.metricsAllowed = new HashSet();
                    for (int i = 0; i < jSONArray.length(); i++) {
                        try {
                            ReportingManager.this.metricsAllowed.add(jSONArray.getString(i));
                        } catch (JSONException e) {
                            ReportingManager.log.e("Error found while parsing tracking responses for reporting: " + e.getMessage());
                        }
                    }
                } else {
                    ReportingManager.this.metricsAllowed = null;
                }
                ReportingManager.this.sendAllowedMetricsToHost();
                ReportingManager.this.savePreferences();
                ReportingManager.this.trackingNextRun = TimeConversion.convertSecondsToTimeStamp(60L);
                ReportingManager.log.d("Tracking next run set to: [", Long.valueOf(ReportingManager.this.trackingNextRun), "][", Long.valueOf(TimeConversion.getCurrentTime()), "]");
                SendDataTransaction.this.sendReports();
                ReportingManager.log.d("onResponse - tracking - complete");
            }
        };
        private final AbstractTransaction.AbstractResponseCallback responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.ReportingManager.SendDataTransaction.2
            @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
            public void onFailure(Command command) {
                SendDataTransaction.this.commandQueue.poll();
                SendDataTransaction.this.handleFailure(command);
            }

            @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
            public void onIOExceptionResponse(Command command) {
                SendDataTransaction.this.commandQueue.poll();
                SendDataTransaction.this.handleFailure(command);
            }

            @Override // com.nuance.connect.comm.ResponseCallback
            public void onResponse(Response response) {
                Command command = (Command) SendDataTransaction.this.commandQueue.poll();
                ReportingManager.log.d("onResponse");
                if (command != null && "aggregate".equals(command.command)) {
                    ReportingManager.log.d("onResponse - aggregate");
                    JSONArray jSONArray = (JSONArray) command.parameters.get(MessageAPI.AGGREGATE_DATA);
                    SendDataTransaction.access$912(SendDataTransaction.this, jSONArray == null ? 0 : jSONArray.length());
                    ReportingManager.this.dataSource.clearAggregate(SendDataTransaction.this.lastAggregateTime);
                } else if (command != null && ReportingManager.COMMAND_INDIVIDUAL.equals(command.command)) {
                    ReportingManager.log.d("onResponse - individual");
                    SendDataTransaction.access$912(SendDataTransaction.this, SendDataTransaction.this.individualCount);
                    ReportingManager.this.dataSource.clearIndividual(SendDataTransaction.this.lastIndividualTime);
                }
                if (SendDataTransaction.this.commandQueue.peek() == null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Strings.DEFAULT_KEY, SendDataTransaction.this.transmittedCount);
                    ReportingManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_REPORTING_TRANSMISSION_SUCCESS, bundle);
                }
            }
        };

        SendDataTransaction() {
            if (TimeConversion.getCurrentTime() >= ReportingManager.this.trackingNextRun) {
                this.commandQueue.add(ReportingManager.this.createCommand(ReportingManager.COMMAND_TRACKING, Command.REQUEST_TYPE.BACKGROUND, this.trackingResponseCallback));
            }
        }

        static /* synthetic */ int access$912(SendDataTransaction sendDataTransaction, int i) {
            int i2 = sendDataTransaction.transmittedCount + i;
            sendDataTransaction.transmittedCount = i2;
            return i2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleFailure(Command command) {
            ReportingManager.log.d("ReportingManager.handleFailure");
            Bundle bundle = new Bundle();
            bundle.putInt(Strings.DEFAULT_KEY, 5);
            ReportingManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_REPORTING_TRANSMISSION_FAILURE, bundle);
            if (command != null && "aggregate".equals(command.command)) {
                ReportingManager.this.dataSource.clearAggregate(this.lastAggregateTime);
            } else {
                if (command == null || !ReportingManager.COMMAND_INDIVIDUAL.equals(command.command)) {
                    return;
                }
                ReportingManager.this.dataSource.clearIndividual(this.lastIndividualTime);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendReports() {
            if (ReportingManager.this.dataSource == null) {
                ReportingManager.log.e("dataSource is not available");
                ReportingManager.this.setLastRunToNow();
                ReportingManager.this.determineUsageNextRun();
                return;
            }
            String[] strArr = ReportingManager.this.metricsAllowed != null ? (String[]) ReportingManager.this.metricsAllowed.toArray(new String[ReportingManager.this.metricsAllowed.size()]) : null;
            this.lastAggregateTime = System.currentTimeMillis();
            JSONArray aggregatePoints = ReportingManager.this.dataSource.getAggregatePoints(strArr, this.lastAggregateTime);
            if (aggregatePoints != null && aggregatePoints.length() > 0) {
                ReportingManager.log.d("has aggregate data points to send");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(MessageAPI.AGGREGATE_DATA, aggregatePoints);
                Command createCommand = ReportingManager.this.createCommand("aggregate", Command.REQUEST_TYPE.BACKGROUND, hashMap, this.responseCallback);
                createCommand.handleIOExceptionInConnector = false;
                this.commandQueue.add(createCommand);
            }
            this.lastIndividualTime = ReportingManager.this.dataSource.getLastIndividualPoint();
            Pair<Integer, String> individualPointsFile = ReportingManager.this.dataSource.getIndividualPointsFile(strArr, this.lastIndividualTime, RequestKey.getKey(), ReportingManager.this.client.getSessionId());
            if (individualPointsFile != null) {
                ReportingManager.log.d("has individual data points to send");
                Command createCommand2 = ReportingManager.this.createCommand(ReportingManager.COMMAND_INDIVIDUAL, Command.REQUEST_TYPE.BACKGROUND, this.responseCallback);
                createCommand2.sendFileLocation = (String) individualPointsFile.second;
                createCommand2.handleIOExceptionInConnector = false;
                this.commandQueue.add(createCommand2);
                this.individualCount = ((Integer) individualPointsFile.first).intValue();
                ReportingManager.log.d("individualCount: ", Integer.valueOf(this.individualCount), " file location: ", createCommand2.sendFileLocation);
            }
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
            return SendDataTransaction.class.getSimpleName();
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command getNextCommand() {
            return this.commandQueue.peek();
        }

        @Override // com.nuance.connect.comm.Transaction
        public int getPriority() {
            return 0;
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command.REQUEST_TYPE getRequestType() {
            return Command.REQUEST_TYPE.BACKGROUND;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onEndProcessing() {
            ReportingManager.this.finishTransaction(getName());
            ReportingManager.this.setLastRunToNow();
            ReportingManager.this.determineUsageNextRun();
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
            this.commandQueue.clear();
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean wifiOnly() {
            return true;
        }
    }

    public ReportingManager(ConnectClient connectClient) {
        super(connectClient);
        this.reportingInterval = 345600;
        this.reportingDataExpriation = 7776000;
        this.trackingNextRun = 0L;
        this.intListener = new Property.IntegerValueListener() { // from class: com.nuance.connect.service.manager.ReportingManager.1
            @Override // com.nuance.connect.internal.Property.ValueListener
            public void onValueChanged(Property<Integer> property) {
                if (property.getKey().equals(ConnectConfiguration.ConfigProperty.REPORTING_FREQUENCY.name())) {
                    ReportingManager.this.reportingInterval = property.getValue().intValue();
                    ReportingManager.this.determineUsageNextRun();
                }
            }
        };
        this.booleanPropertyListener = new Property.BooleanValueListener() { // from class: com.nuance.connect.service.manager.ReportingManager.2
            @Override // com.nuance.connect.internal.Property.ValueListener
            public void onValueChanged(Property<Boolean> property) {
                ConnectConfiguration.ConfigProperty.REPORTING_ALLOWED.name().equals(property.getKey());
            }
        };
        this.version = 5;
        this.commandFamily = "report";
        this.messages = MESSAGES_HANDLED;
        this.validCommands.addCommand("aggregate", new int[]{1});
        this.validCommands.addCommand(COMMAND_INDIVIDUAL, new int[]{1});
        this.validCommands.addCommand(COMMAND_TRACKING, new int[]{1});
        this.client.addListener(ConnectConfiguration.ConfigProperty.REPORTING_ALLOWED, this.booleanPropertyListener);
        this.client.addListener(ConnectConfiguration.ConfigProperty.REPORTING_FREQUENCY, this.intListener);
    }

    private void cleanStoredReports(long j) {
        if (this.dataSource == null) {
            log.d("cleanStoredReports() dataSource is null");
        } else {
            this.dataSource.clearAggregate(j);
            this.dataSource.clearIndividual(j);
        }
    }

    private void clearStoredAggregate() {
        if (this.dataSource != null) {
            this.dataSource.clearAggregate(TimeConversion.getCurrentTime());
        }
    }

    private void clearStoredIndividual() {
        if (this.dataSource != null) {
            this.dataSource.clearIndividual(TimeConversion.getCurrentTime());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void determineUsageNextRun() {
        long convertSecondsToTimeStamp = TimeConversion.convertSecondsToTimeStamp(this.reportingInterval, this.client.getDataStore().readLong(REPORTING_LAST_RUN, 0L));
        Alarm build = this.client.getAlarmBuilder(ReportingManager.class, REPORTING_GENERIC).triggerTime(convertSecondsToTimeStamp).build();
        build.cancel();
        if (convertSecondsToTimeStamp < System.currentTimeMillis()) {
            log.v("determineUsageNextRun running now");
            sendReportTracking(true);
        } else {
            log.v("determineUsageNextRun " + build);
            build.set();
        }
    }

    private void loadPreferences() {
        String readString = this.client.getDataStore().readString(METRICS_ALLOWED_PREF, null);
        if (readString != null) {
            this.metricsAllowed = new HashSet<>(Arrays.asList(readString.split(",")));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void savePreferences() {
        if (this.metricsAllowed == null || this.metricsAllowed.isEmpty()) {
            this.client.getDataStore().saveString(METRICS_ALLOWED_PREF, null);
        } else {
            this.client.getDataStore().saveString(METRICS_ALLOWED_PREF, StringUtils.implode(this.metricsAllowed, ","));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAllowedMetricsToHost() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Strings.DEFAULT_KEY, this.metricsAllowed);
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_SET_ALLOWED_REPORTING_METRICS, bundle);
    }

    private void sendReportTracking(boolean z) {
        boolean z2 = true;
        if (this.dataSource == null) {
            log.d("data source is null. Exitting...");
            return;
        }
        if (z) {
            log.d("sendReportTracking hasAggregate=", Boolean.valueOf(this.dataSource.hasAggregate()));
            log.d("sendReportTracking hasIndividual=", Boolean.valueOf(this.dataSource.hasIndividual()));
            if (!this.dataSource.hasAggregate() && !this.dataSource.hasIndividual()) {
                setLastRunToNow();
                determineUsageNextRun();
                z2 = false;
            }
        }
        log.d("sendReportTracking(", Boolean.valueOf(z), "): ", Boolean.valueOf(z2));
        if (z2) {
            startTransaction(new SendDataTransaction());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLastRunToNow() {
        this.client.getDataStore().saveLong(REPORTING_LAST_RUN, TimeConversion.getCurrentTime());
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.CommandManager
    public void alarmNotification(String str, Bundle bundle) {
        log.v("alarmNotification type=" + str);
        if (str != null) {
            sendReportTracking(true);
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void deregister() {
        super.deregister();
        clearStoredAggregate();
        clearStoredIndividual();
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public String[] getDependencies() {
        ArrayList arrayList = new ArrayList();
        for (ManagerService managerService : ManagerService.REPORTING.values()) {
            arrayList.add(managerService.getName());
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void init() {
        if (this.dataSource == null) {
            initializeData();
        }
    }

    protected void initializeData() {
        this.dataSource = new ReportingDataSource(this.client);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_SEND_REPORTING_NOW:
                sendReportingNow();
                return true;
            case MESSAGE_CLIENT_GET_ALLOWED_REPORTING_METRICS:
                sendAllowedMetricsToHost();
                return true;
            default:
                return false;
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2, boolean z) {
        if (VersionUtils.isDataCleanupRequiredOnUpgrade(version, version2)) {
            this.metricsAllowed = null;
            removePreference(METRICS_ALLOWED_PREF);
        }
    }

    protected void sendReportingNow() {
        this.client.getDataStore().saveLong(REPORTING_LAST_RUN, 0L);
        this.client.getAlarmBuilder(ReportingManager.class, REPORTING_GENERIC).build().cancel();
        determineUsageNextRun();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void start() {
        log.d("start");
        if (this.dataSource == null) {
            initializeData();
        }
        cleanStoredReports(TimeConversion.getCurrentTime() - this.reportingDataExpriation);
        this.managerStartState = AbstractCommandManager.ManagerState.STARTING;
        this.trackingNextRun = 0L;
        loadPreferences();
        sendAllowedMetricsToHost();
        managerStartComplete();
        determineUsageNextRun();
    }
}
