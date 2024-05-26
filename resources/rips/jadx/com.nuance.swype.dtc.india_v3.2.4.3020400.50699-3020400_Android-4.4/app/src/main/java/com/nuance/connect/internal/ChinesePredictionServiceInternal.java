package com.nuance.connect.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import com.a.a.c;
import com.a.a.j;
import com.nuance.connect.api.ChinesePredictionService;
import com.nuance.connect.api.ConfigService;
import com.nuance.connect.api.ConnectException;
import com.nuance.connect.comm.AbstractTransaction;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.CommandQueue;
import com.nuance.connect.comm.MessageSendingBus;
import com.nuance.connect.comm.PersistantConnectionConfig;
import com.nuance.connect.comm.Response;
import com.nuance.connect.comm.ResponseCallback;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.Integers;
import com.nuance.connect.host.service.BuildSettings;
import com.nuance.connect.host.service.HostInterface;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.APIHandlers;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.proto.Prediction;
import com.nuance.connect.sqlite.ChinesePredictionDataSource;
import com.nuance.connect.system.Connectivity;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public class ChinesePredictionServiceInternal extends AbstractService implements ChinesePredictionService {
    private static final int CCPS_VERSION = 1;
    private static final String COMMAND_FAMILY = "CHINESEPREDICTION";
    private static final int COMPRESSION_THRESHOLD_VOID = -1;
    private static final int DEFAULT_NUM_PREDICTIONS = 10;
    private static final int DISCONNECT_PENDING_TIMER = 100;
    private static final String LOGGING_URL_NAME = "logging";
    private static final InternalMessages[] MESSAGE_IDS = new InternalMessages[0];
    private static final int PENDING_LOG_THRESHOLD = 10;
    private static final String PREDICTION_URL_NAME = "predict";
    private final String ccpsVersion;
    private ConnectServiceManagerInternal connectService;
    private Connectivity connectivity;
    private int currentSessionCharacterSetId;
    private int currentSessionLanguage;
    private String customerId;
    private final ChinesePredictionDataSource dataStore;
    private int idleTimeout;
    private PersistantConnectionConfig loggingConnectionConfig;
    private int pendingLogsSize;
    private PersistantConnectionConfig persistantConnectionConfig;
    private final CommandQueue queue;
    private String xt9Version;
    private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM);
    private final ConcurrentCallbackSet<ChinesePredictionService.ChinesePredictionCallback> predictionCallbacks = new ConcurrentCallbackSet<>();
    private final Map<String, ChinesePredictionTransaction> activePredictions = new HashMap();
    private int predictionResults = 10;
    private final int[] lock = new int[0];
    private final AtomicBoolean sessionActive = new AtomicBoolean(false);
    private boolean useable = false;
    private Handler shutdownHandler = new Handler(Looper.getMainLooper());
    private String deviceId = null;
    private Runnable transmitOrShutdownRunnable = new Runnable() { // from class: com.nuance.connect.internal.ChinesePredictionServiceInternal.1
        @Override // java.lang.Runnable
        public void run() {
            ChinesePredictionServiceInternal.this.transmitOrShutdown();
        }
    };
    private Property.ValueListener<String> connectivityStringListener = new Property.StringValueListener() { // from class: com.nuance.connect.internal.ChinesePredictionServiceInternal.2
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<String> property) {
            ChinesePredictionServiceInternal.this.log.d("connectivityStringListener.onValueChanged() name=", property.getKey(), " value=", property.getValue());
            if (property.getKey().equals(UserSettings.FOREGROUND_DATA_STATE)) {
                if (ChinesePredictionServiceInternal.this.connectivity != null) {
                    ChinesePredictionServiceInternal.this.connectivity.setForegroundConfiguration(property.getValue());
                }
            } else {
                if (!property.getKey().equals(UserSettings.BACKGROUND_DATA_STATE) || ChinesePredictionServiceInternal.this.connectivity == null) {
                    return;
                }
                ChinesePredictionServiceInternal.this.connectivity.setBackgroundConfiguration(property.getValue());
            }
        }
    };
    private ConnectHandler handler = new ConnectHandler() { // from class: com.nuance.connect.internal.ChinesePredictionServiceInternal.3
        @Override // com.nuance.connect.internal.ConnectHandler
        public String getHandlerName() {
            return APIHandlers.CHINESE_PREDICTION_HANDLER;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public int[] getMessageIDs() {
            int[] iArr = new int[ChinesePredictionServiceInternal.MESSAGE_IDS.length];
            for (int i = 0; i < ChinesePredictionServiceInternal.MESSAGE_IDS.length; i++) {
                iArr[i] = ChinesePredictionServiceInternal.MESSAGE_IDS[i].ordinal();
            }
            return iArr;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void handleMessage(Handler handler, Message message) {
            int[] iArr = AnonymousClass5.$SwitchMap$com$nuance$connect$internal$common$InternalMessages;
            InternalMessages.fromInt(message.what).ordinal();
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void onPostUpgrade() {
        }
    };
    private MessageSendingBus messageBus = new MessageSendingBus() { // from class: com.nuance.connect.internal.ChinesePredictionServiceInternal.4
        @Override // com.nuance.connect.comm.MessageSendingBus
        public Context getContext() {
            ChinesePredictionServiceInternal.this.log.d("[ChinesePredictionServiceInternal]", "getContext()");
            return ChinesePredictionServiceInternal.this.connectService.getContext();
        }

        @Override // com.nuance.connect.comm.MessageSendingBus
        public String getDeviceId() {
            ChinesePredictionServiceInternal.this.log.d("[ChinesePredictionServiceInternal]", "getDeviceId()");
            ConfigService configService = (ConfigService) ChinesePredictionServiceInternal.this.connectService.getFeatureService(ConnectFeature.CONFIG);
            if (configService != null) {
                return configService.getDeviceId();
            }
            return null;
        }

        @Override // com.nuance.connect.comm.MessageSendingBus
        public String getDeviceRegisterCommand() {
            return null;
        }

        @Override // com.nuance.connect.comm.MessageSendingBus
        public String getSessionCreateCommand() {
            return null;
        }

        @Override // com.nuance.connect.comm.MessageSendingBus
        public String getSessionId() {
            ChinesePredictionServiceInternal.this.log.d("[ChinesePredictionServiceInternal]", "getSessionId()");
            ConfigService configService = (ConfigService) ChinesePredictionServiceInternal.this.connectService.getFeatureService(ConnectFeature.CONFIG);
            if (configService != null) {
                return configService.getDeviceId();
            }
            return null;
        }

        @Override // com.nuance.connect.comm.MessageSendingBus
        public boolean isLicensed() {
            return ChinesePredictionServiceInternal.this.connectService.isLicensed();
        }
    };

    /* renamed from: com.nuance.connect.internal.ChinesePredictionServiceInternal$5, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$com$nuance$connect$internal$common$InternalMessages = new int[InternalMessages.values().length];
    }

    /* loaded from: classes.dex */
    static class ChinesePredictionImpl implements ChinesePredictionService.ChinesePrediction {
        private int[] attributes;
        private String fullSpell;
        private String phrase;
        private String predictionId;
        private String spell;

        ChinesePredictionImpl(String str, String str2, String str3, String str4, int[] iArr) {
            this.predictionId = str;
            this.phrase = str2;
            this.spell = str3;
            this.fullSpell = str4;
            this.attributes = iArr;
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        public int[] getAttributes() {
            return this.attributes;
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        public String getFullSpell() {
            return this.fullSpell;
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        public String getPhrase() {
            return this.phrase;
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        public String getPredictionId() {
            return this.predictionId;
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        public String getSpell() {
            return this.spell;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ChinesePredictionLoggingTransaction extends AbstractTransaction {
        private static final int PRIORITY = 10;
        protected final PersistantConnectionConfig connectionConfig;
        protected int fromRowId;
        private ResponseCallback responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.internal.ChinesePredictionServiceInternal.ChinesePredictionLoggingTransaction.1
            @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
            public void onCancel(Command command) {
                ChinesePredictionLoggingTransaction.this.cleanupFailedTransaction();
                ChinesePredictionLoggingTransaction.this.currentCommand = null;
            }

            @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
            public void onFailure(Command command) {
                ChinesePredictionLoggingTransaction.this.cleanupFailedTransaction();
                ChinesePredictionLoggingTransaction.this.currentCommand = null;
            }

            @Override // com.nuance.connect.comm.ResponseCallback
            public void onResponse(Response response) {
                switch (response.status) {
                    case 1:
                        ChinesePredictionServiceInternal.this.dataStore.deletePredictionsFrom(ChinesePredictionLoggingTransaction.this.fromRowId);
                        ChinesePredictionServiceInternal.this.pendingLogsSize = ChinesePredictionServiceInternal.this.dataStore.getPendingLogSize();
                        break;
                    default:
                        ChinesePredictionLoggingTransaction.this.cleanupFailedTransaction();
                        break;
                }
                ChinesePredictionLoggingTransaction.this.currentCommand = null;
            }
        };

        public ChinesePredictionLoggingTransaction(PersistantConnectionConfig persistantConnectionConfig) {
            this.connectionConfig = persistantConnectionConfig;
            requestLogging();
        }

        private void requestLogging() {
            ChinesePredictionServiceInternal.this.log.d("ChinesePredictionLoggingTransaction  - requestLogging()");
            ChinesePredictionDataSource.ChinesePredictionResultDataReturn predictions = ChinesePredictionServiceInternal.this.dataStore.getPredictions(ChinesePredictionServiceInternal.this.deviceId);
            this.fromRowId = predictions.lastRowId;
            if (predictions == null || predictions.loggingRequest == null) {
                return;
            }
            Command command = new Command();
            command.commandFamily = ChinesePredictionServiceInternal.COMMAND_FAMILY;
            command.command = ChinesePredictionServiceInternal.LOGGING_URL_NAME;
            command.version = 1;
            command.thirdPartyURL = this.connectionConfig.getURL();
            command.parameters = new HashMap<>();
            command.handleIOExceptionInConnector = false;
            command.hasBody = true;
            command.requireSession = false;
            command.responseCallback = this.responseCallback;
            command.requestType = Command.REQUEST_TYPE.CRITICAL;
            command.allowDuplicateOfCommand = true;
            command.dataSource = Command.DATA_SOURCE.PBUFF;
            command.dataResponse = Command.DATA_SOURCE.PBUFF;
            command.retryCount = 0;
            command.bufferData = predictions.loggingRequest.toByteArray();
            this.currentCommand = command;
        }

        @Override // com.nuance.connect.comm.Transaction
        public void cancel() {
            ChinesePredictionServiceInternal.this.log.d("ChinesePredictionLoggingTransaction - cancel()");
            if (this.currentCommand != null) {
                this.currentCommand.canceled = true;
            }
        }

        protected void cleanupFailedTransaction() {
            ChinesePredictionServiceInternal.this.log.d("cleaning up failed transaction...");
        }

        @Override // com.nuance.connect.comm.Transaction
        public String createDownloadFile(Context context) {
            ChinesePredictionServiceInternal.this.log.d("ChinesePredictionLoggingTransaction - createDownloadFile()");
            return null;
        }

        @Override // com.nuance.connect.comm.Transaction
        public String getName() {
            return "ChinesePredictionLoggingTransaction-DISTINCT";
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command getNextCommand() {
            return this.currentCommand;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public PersistantConnectionConfig getPersistantConfig() {
            return this.connectionConfig;
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
            this.currentCommand = null;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean onTransactionOfflineQueued() {
            return false;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onTransactionRejected(int i) {
            ChinesePredictionServiceInternal.this.log.d("ChinesePredictionLoggingTransaction - onTransactionRejected reason=", Integer.valueOf(i));
            super.onTransactionRejected(i);
            cleanupFailedTransaction();
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean requiresPersistantConnection() {
            return false;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
            ChinesePredictionServiceInternal.this.log.d("ChinesePredictionLoggingTransaction - rollback()");
            cleanupFailedTransaction();
            this.currentCommand = null;
        }
    }

    /* loaded from: classes.dex */
    static class ChinesePredictionResultImpl implements ChinesePredictionService.ChinesePredictionResult {
        private String predictionId;
        private ArrayList<ChinesePredictionService.ChinesePrediction> predictionList = new ArrayList<>();
        private Long predictionTimeMicro;
        private ChinesePredictionService.ChinesePredictionRequest request;

        ChinesePredictionResultImpl(String str, ChinesePredictionService.ChinesePredictionRequest chinesePredictionRequest) {
            this.predictionId = str;
            this.request = chinesePredictionRequest;
        }

        public void addPrediction(ChinesePredictionService.ChinesePrediction chinesePrediction) {
            this.predictionList.add(chinesePrediction);
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        public int getPredictionCount() {
            return this.predictionList.size();
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        public String getPredictionId() {
            return this.predictionId;
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        public ChinesePredictionService.ChinesePredictionRequest getPredictionRequest() {
            return this.request;
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        public ChinesePredictionService.ChinesePrediction[] getPredictions() {
            return (ChinesePredictionService.ChinesePrediction[]) this.predictionList.toArray(new ChinesePredictionService.ChinesePrediction[this.predictionList.size()]);
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        public long predictionTime() {
            return this.predictionTimeMicro.longValue();
        }

        public void setPredictionTime(long j) {
            this.predictionTimeMicro = Long.valueOf(j);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ChinesePredictionTransaction extends AbstractTransaction {
        private static final int PRIORITY = 10;
        private final String applicationName;
        protected final int characterSetId;
        protected final PersistantConnectionConfig connectionConfig;
        protected byte[] coreData;
        protected final String deviceId;
        protected final int languageId;
        private ChinesePredictionService.ChinesePredictionRequest predictionRequest;
        protected final String predictionRequestId;
        private int predictionRequestMaxCount;
        protected final Prediction.PredictionRequestV1 predictionRequestProtoBuffer;
        private ChinesePredictionResultImpl predictionResult;
        protected long predictionTimeStartMicro;
        protected long predictionTimeEndMicro = 0;
        protected long predictionCloudStartMicro = 0;
        protected long predictionCloudEndMicro = 0;
        protected long predictionSpent = 0;
        protected long predictionCloudSpent = 0;
        Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
        private StringBuilder predictionStringBuilder = new StringBuilder();
        private ResponseCallback responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.internal.ChinesePredictionServiceInternal.ChinesePredictionTransaction.1
            @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
            public void onCancel(Command command) {
                ChinesePredictionTransaction.this.log.d("onCancel(", ChinesePredictionTransaction.this.predictionRequestId, ")");
                ChinesePredictionServiceInternal.this.sendPredictionCancel(ChinesePredictionTransaction.this.predictionRequestId);
                ChinesePredictionTransaction.this.currentCommand = null;
            }

            @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
            public void onFailure(Command command) {
                ChinesePredictionServiceInternal.this.sendPredictionFailure(ChinesePredictionTransaction.this.predictionRequestId, 0, "There was a failure when attempting the prediction", ChinesePredictionTransaction.this.applicationName, ChinesePredictionServiceInternal.this.connectService.getUserSettings().getLocationCountry());
                ChinesePredictionTransaction.this.currentCommand = null;
            }

            @Override // com.nuance.connect.comm.ResponseCallback
            public void onResponse(Response response) {
                Prediction.PredictionResponseV1 predictionResponseV1;
                ChinesePredictionTransaction.this.log.d("onResponse() id=", ChinesePredictionTransaction.this.predictionRequestId, " onResponse ", " status=", Integer.valueOf(response.status));
                ChinesePredictionTransaction.this.predictionCloudEndMicro = SystemClock.elapsedRealtime();
                if (ChinesePredictionTransaction.this.currentCommand != null && ChinesePredictionTransaction.this.currentCommand.canceled) {
                    onCancel(ChinesePredictionTransaction.this.currentCommand);
                    return;
                }
                ChinesePredictionTransaction.this.currentCommand = null;
                switch (response.status) {
                    case 1:
                        try {
                            predictionResponseV1 = Prediction.PredictionResponseV1.parseFrom(response.responseArray);
                        } catch (j e) {
                            ChinesePredictionTransaction.this.log.d("PredictionTransaction id=", ChinesePredictionTransaction.this.predictionRequestId, " onResponse ", " error processing buffer: ", e.getMessage());
                            predictionResponseV1 = null;
                        }
                        if (predictionResponseV1 == null) {
                            ChinesePredictionServiceInternal.this.sendPredictionFailure(ChinesePredictionTransaction.this.predictionRequestId, 0, "There was a problem parsing the response from the server", ChinesePredictionTransaction.this.applicationName, ChinesePredictionServiceInternal.this.connectService.getUserSettings().getLocationCountry());
                            return;
                        }
                        ChinesePredictionTransaction.this.predictionResult = new ChinesePredictionResultImpl(ChinesePredictionTransaction.this.predictionRequestId, ChinesePredictionTransaction.this.predictionRequest);
                        int predictionResultCount = predictionResponseV1.getPredictionResultCount();
                        for (int i = 0; i < predictionResultCount && i < ChinesePredictionTransaction.this.predictionRequestMaxCount; i++) {
                            Prediction.PredictionResultV1 predictionResult = predictionResponseV1.getPredictionResult(i);
                            int[] iArr = new int[predictionResult.getAttributeCount()];
                            ChinesePredictionTransaction.this.predictionStringBuilder.setLength(0);
                            for (int i2 = 0; i2 < predictionResult.getPhraseCount(); i2++) {
                                ChinesePredictionTransaction.this.predictionStringBuilder.append((char) predictionResult.getPhrase(i2));
                            }
                            String sb = ChinesePredictionTransaction.this.predictionStringBuilder.toString();
                            ChinesePredictionTransaction.this.predictionStringBuilder.setLength(0);
                            for (int i3 = 0; i3 < predictionResult.getSpellCount(); i3++) {
                                ChinesePredictionTransaction.this.predictionStringBuilder.append((char) predictionResult.getSpell(i3));
                            }
                            String sb2 = ChinesePredictionTransaction.this.predictionStringBuilder.toString();
                            ChinesePredictionTransaction.this.predictionStringBuilder.setLength(0);
                            for (int i4 = 0; i4 < predictionResult.getFullSpellCount(); i4++) {
                                ChinesePredictionTransaction.this.predictionStringBuilder.append((char) predictionResult.getFullSpell(i4));
                            }
                            String sb3 = ChinesePredictionTransaction.this.predictionStringBuilder.toString();
                            ChinesePredictionTransaction.this.predictionStringBuilder.setLength(0);
                            for (int i5 = 0; i5 < predictionResult.getAttributeCount(); i5++) {
                                iArr[i5] = (char) predictionResult.getAttribute(i5);
                            }
                            ChinesePredictionImpl chinesePredictionImpl = new ChinesePredictionImpl(ChinesePredictionTransaction.this.predictionRequestId, sb, sb2, sb3, iArr);
                            ChinesePredictionTransaction.this.log.d("PredictionTransaction Prediction id=", ChinesePredictionTransaction.this.predictionRequestId, " string=", sb, " spell=", sb2);
                            ChinesePredictionTransaction.this.predictionResult.addPrediction(chinesePredictionImpl);
                        }
                        ChinesePredictionTransaction.this.calculateEndTime();
                        ChinesePredictionTransaction.this.predictionResult.setPredictionTime(ChinesePredictionTransaction.this.predictionSpent);
                        ChinesePredictionTransaction.this.log.d("PredictionTransaction Result id=", ChinesePredictionTransaction.this.predictionRequestId, " totalPredictionTime=", Long.valueOf(ChinesePredictionTransaction.this.predictionSpent), " cloudTime=", Long.valueOf(ChinesePredictionTransaction.this.predictionCloudSpent), " cloudCoreTime=", Integer.valueOf(predictionResponseV1.getCoreTime()), " cloudTimeSpent=", Integer.valueOf(predictionResponseV1.getTimeSpent()));
                        ChinesePredictionServiceInternal.this.sendPredictionResponse(ChinesePredictionTransaction.this.predictionResult, ChinesePredictionTransaction.this.predictionSpent, ChinesePredictionTransaction.this.predictionCloudSpent, ChinesePredictionTransaction.this.applicationName, ChinesePredictionServiceInternal.this.connectService.getUserSettings().getLocationCountry());
                        return;
                    default:
                        ChinesePredictionServiceInternal.this.sendPredictionFailure(ChinesePredictionTransaction.this.predictionRequestId, 0, "There was a network failure (" + response.status + ") when attempting the prediction", ChinesePredictionTransaction.this.applicationName, ChinesePredictionServiceInternal.this.connectService.getUserSettings().getLocationCountry());
                        return;
                }
            }

            @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
            public boolean onRetry(Command command, int i, int i2, String str) {
                ChinesePredictionTransaction.this.calculateEndTime();
                ChinesePredictionServiceInternal.this.sendPredictionFailure(ChinesePredictionTransaction.this.predictionRequestId, 0, "There was an error (" + i2 + ") when attempting the prediction. message: " + str, ChinesePredictionTransaction.this.applicationName, ChinesePredictionServiceInternal.this.connectService.getUserSettings().getLocationCountry());
                ChinesePredictionTransaction.this.currentCommand = null;
                return false;
            }
        };

        public ChinesePredictionTransaction(String str, int i, int i2, String str2, byte[] bArr, PersistantConnectionConfig persistantConnectionConfig, int i3, String str3) {
            this.predictionTimeStartMicro = 0L;
            this.predictionRequestId = str;
            this.languageId = i;
            this.deviceId = str2;
            this.characterSetId = i2;
            this.coreData = bArr;
            this.connectionConfig = persistantConnectionConfig;
            this.predictionTimeStartMicro = SystemClock.elapsedRealtime();
            this.predictionRequestMaxCount = i3;
            this.applicationName = str3;
            this.predictionRequest = new PredictionRequestImpl(this.predictionRequestId, this.languageId, this.characterSetId, bArr);
            this.predictionRequestProtoBuffer = Prediction.PredictionRequestV1.newBuilder().setDeviceID(this.deviceId).setNumPredictionsRequired(this.predictionRequestMaxCount).setPredictionData(c.a(this.coreData)).setRequestTimestamp(System.currentTimeMillis()).setTransactionID(this.predictionRequestId).setXt9Version(ChinesePredictionServiceInternal.this.xt9Version).build();
            this.log.d("PredictionTransaction id=", this.predictionRequestId, " - ", " deviceId=", this.deviceId, " numPredictions=", Integer.valueOf(this.predictionRequestProtoBuffer.getNumPredictionsRequired()), " requestTimestamp=", Long.valueOf(this.predictionRequestProtoBuffer.getRequestTimestamp()), " transactionId=", this.predictionRequestProtoBuffer.getTransactionID(), " data=", this.predictionRequestProtoBuffer.getPredictionData());
            requestPrediction();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void calculateEndTime() {
            if (this.predictionCloudEndMicro == 0) {
                this.predictionCloudEndMicro = SystemClock.elapsedRealtime();
            }
            if (this.predictionTimeEndMicro == 0) {
                this.predictionTimeEndMicro = SystemClock.elapsedRealtime();
            }
            this.predictionSpent = this.predictionTimeEndMicro - this.predictionTimeStartMicro;
            this.predictionCloudSpent = this.predictionCloudEndMicro - this.predictionCloudStartMicro;
        }

        private void requestPrediction() {
            this.log.d("PredictionTransaction id=", this.predictionRequestId, " - requestPrediction()");
            Command command = new Command();
            command.commandFamily = ChinesePredictionServiceInternal.COMMAND_FAMILY;
            command.command = "PREDICTION";
            command.version = 1;
            command.parameters = new HashMap<>();
            command.handleIOExceptionInConnector = false;
            command.hasBody = true;
            command.requireSession = false;
            command.responseCallback = this.responseCallback;
            command.requestType = Command.REQUEST_TYPE.USER;
            command.allowDuplicateOfCommand = true;
            command.dataSource = Command.DATA_SOURCE.PBUFF;
            command.dataResponse = Command.DATA_SOURCE.PBUFF;
            command.retryCount = 0;
            command.bufferData = this.predictionRequestProtoBuffer.toByteArray();
            if (ChinesePredictionServiceInternal.this.sessionActive.get()) {
                this.currentCommand = command;
            } else {
                this.log.d("PredictionTransaction id=", this.predictionRequestId, " - requestPrediction() failing because session is not active");
            }
        }

        @Override // com.nuance.connect.comm.Transaction
        public void cancel() {
            this.log.d("PredictionTransaction id=", this.predictionRequestId, " - cancel()");
            if (this.currentCommand != null) {
                this.currentCommand.canceled = true;
            }
        }

        @Override // com.nuance.connect.comm.Transaction
        public String createDownloadFile(Context context) {
            this.log.d("PredictionTransaction id=", this.predictionRequestId, " - createDownloadFile()");
            return null;
        }

        @Override // com.nuance.connect.comm.Transaction
        public String getName() {
            return "CCP-" + this.predictionRequestId;
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command getNextCommand() {
            if (!ChinesePredictionServiceInternal.this.sessionActive.get()) {
                return null;
            }
            if (this.predictionCloudStartMicro == 0) {
                this.predictionCloudStartMicro = SystemClock.elapsedRealtime();
            }
            return this.currentCommand;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public PersistantConnectionConfig getPersistantConfig() {
            return this.connectionConfig;
        }

        @Override // com.nuance.connect.comm.Transaction
        public int getPriority() {
            return 10;
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command.REQUEST_TYPE getRequestType() {
            return Command.REQUEST_TYPE.USER;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onEndProcessing() {
            this.currentCommand = null;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean onTransactionOfflineQueued() {
            return false;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onTransactionRejected(int i) {
            this.log.d("PredictionTransaction id=", this.predictionRequestId, " - onTransactionRejected reason=", Integer.valueOf(i));
            super.onTransactionRejected(i);
            ChinesePredictionServiceInternal.this.sendPredictionFailure(this.predictionRequestId, 8, "Unable to handle prediction request as connection is not currently active.", this.applicationName, ChinesePredictionServiceInternal.this.connectService.getUserSettings().getLocationCountry());
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean requiresPersistantConnection() {
            return true;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean requiresSessionId() {
            return false;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
            this.log.d("PredictionTransaction id=", this.predictionRequestId, " - rollback()");
            this.currentCommand = null;
            ChinesePredictionServiceInternal.this.sendPredictionFailure(this.predictionRequestId, 5, "", this.applicationName, ChinesePredictionServiceInternal.this.connectService.getUserSettings().getLocationCountry());
        }
    }

    /* loaded from: classes.dex */
    private class LoggingUrl {
        public String customerId;

        LoggingUrl(String str) {
            this.customerId = str;
        }

        public String toString() {
            String cCPServerUrl = ((BuildSettings) ChinesePredictionServiceInternal.this.connectService.getHostService(HostInterface.HostService.HOST_BUILD_SETTINGS)).getCCPServerUrl();
            StringBuilder sb = new StringBuilder();
            sb.append(cCPServerUrl);
            if (!cCPServerUrl.endsWith("/")) {
                sb.append("/");
            }
            sb.append(1);
            sb.append("/");
            sb.append(ChinesePredictionServiceInternal.LOGGING_URL_NAME);
            sb.append("/");
            sb.append(this.customerId);
            return sb.toString();
        }
    }

    /* loaded from: classes.dex */
    static class PredictionRequestImpl implements ChinesePredictionService.ChinesePredictionRequest {
        private int characterSetId;
        private int languageId;
        private byte[] predictionData;
        private String predictionId;

        PredictionRequestImpl(String str, int i, int i2, byte[] bArr) {
            this.predictionId = str;
            this.languageId = i;
            this.characterSetId = i2;
            this.predictionData = bArr;
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionRequest
        public int getCharacterSetId() {
            return this.characterSetId;
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionRequest
        public int getLanguageId() {
            return this.languageId;
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionRequest
        public byte[] getPredictionData() {
            return this.predictionData;
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionRequest
        public String getPredictionId() {
            return this.predictionId;
        }
    }

    /* loaded from: classes.dex */
    public class PredictionUrl {
        public int charsetId;
        public String customerId;
        public int languageId;

        PredictionUrl(String str, int i, int i2) {
            this.customerId = str;
            this.languageId = i;
            this.charsetId = i2;
        }

        public String toString() {
            String cCPServerUrl = ((BuildSettings) ChinesePredictionServiceInternal.this.connectService.getHostService(HostInterface.HostService.HOST_BUILD_SETTINGS)).getCCPServerUrl();
            StringBuilder sb = new StringBuilder();
            sb.append(cCPServerUrl);
            if (!cCPServerUrl.endsWith("/")) {
                sb.append("/");
            }
            sb.append(1);
            sb.append("/");
            sb.append(ChinesePredictionServiceInternal.PREDICTION_URL_NAME);
            sb.append("/");
            sb.append(this.customerId);
            sb.append("/");
            sb.append(this.languageId);
            sb.append("/");
            sb.append(this.charsetId);
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChinesePredictionServiceInternal(ConnectServiceManagerInternal connectServiceManagerInternal) {
        this.pendingLogsSize = Integers.STATUS_SUCCESS;
        this.connectService = connectServiceManagerInternal;
        this.connectivity = new Connectivity(connectServiceManagerInternal.getContext());
        BuildSettings buildSettings = (BuildSettings) this.connectService.getHostService(HostInterface.HostService.HOST_BUILD_SETTINGS);
        UserSettings userSettings = this.connectService.getUserSettings();
        userSettings.registerUserSettingsListener(UserSettings.BACKGROUND_DATA_STATE, this.connectivityStringListener);
        userSettings.registerUserSettingsListener(UserSettings.FOREGROUND_DATA_STATE, this.connectivityStringListener);
        this.customerId = buildSettings.getOemId();
        this.ccpsVersion = parseSDKVersion(buildSettings);
        this.connectivity.setStableCellularTime(0);
        this.connectivity.setStableWifiTime(0);
        this.dataStore = new ChinesePredictionDataSource(connectServiceManagerInternal.getContext());
        this.pendingLogsSize = this.dataStore.getPendingLogSize();
        this.xt9Version = buildSettings.getCoreVersions().get(3);
        determineUsable();
        this.queue = new CommandQueue(this.messageBus, new CommandQueue.CommandQueueErrorCallbackDefault(), this.connectivity, this.connectService.getAppSettings().getMinimumSSLProtocol(), null, 0);
        this.queue.start();
    }

    private void determineUsable() {
        this.useable = true;
    }

    private String parseSDKVersion(BuildSettings buildSettings) {
        int i;
        int i2 = 0;
        String[] split = buildSettings.getVersion().split("\\.");
        if (split.length > 0) {
            try {
                i = Integer.parseInt(split[0]);
            } catch (NumberFormatException e) {
                i = 0;
            }
        } else {
            i = 1;
        }
        if (split.length > 1) {
            try {
                i2 = Integer.parseInt(split[1]);
            } catch (NumberFormatException e2) {
            }
        }
        return i + "." + i2;
    }

    private void removeActivePrediction(String str) {
        this.activePredictions.remove(str);
        this.pendingLogsSize++;
    }

    private void sendLoggingToServer() {
        this.log.d("sendLoggingToServer");
        this.queue.sendTransaction(new ChinesePredictionLoggingTransaction(this.loggingConnectionConfig));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendPredictionCancel(String str) {
        this.log.d("sendPredictionCancel(", str, ")");
        for (ChinesePredictionService.ChinesePredictionCallback chinesePredictionCallback : (ChinesePredictionService.ChinesePredictionCallback[]) this.predictionCallbacks.toArray(new ChinesePredictionService.ChinesePredictionCallback[0])) {
            chinesePredictionCallback.onPredictionCancel(str);
        }
        this.dataStore.insertPrediction(str, this.ccpsVersion, 2, -1L, -1L, null, null);
        this.pendingLogsSize++;
        removeActivePrediction(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendPredictionFailure(String str, int i, String str2, String str3, String str4) {
        this.log.d("sendPredictionFailure(", str, ", ", Integer.valueOf(i), ", ", str2, ")");
        for (ChinesePredictionService.ChinesePredictionCallback chinesePredictionCallback : (ChinesePredictionService.ChinesePredictionCallback[]) this.predictionCallbacks.toArray(new ChinesePredictionService.ChinesePredictionCallback[0])) {
            chinesePredictionCallback.onPredictionFailure(str, i, str2);
        }
        this.dataStore.insertPrediction(str, this.ccpsVersion, 1, -1L, -1L, str3, str4);
        this.pendingLogsSize++;
        removeActivePrediction(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendPredictionResponse(ChinesePredictionService.ChinesePredictionResult chinesePredictionResult, long j, long j2, String str, String str2) {
        this.log.d("sendPredictionResponse(", chinesePredictionResult.getPredictionId(), ", ", Long.valueOf(j), ", ", Long.valueOf(j2), ")");
        this.dataStore.insertPrediction(chinesePredictionResult.getPredictionId(), this.ccpsVersion, 0, j, j2, str, str2);
        removeActivePrediction(chinesePredictionResult.getPredictionId());
        this.pendingLogsSize++;
        try {
            for (ChinesePredictionService.ChinesePredictionCallback chinesePredictionCallback : (ChinesePredictionService.ChinesePredictionCallback[]) this.predictionCallbacks.toArray(new ChinesePredictionService.ChinesePredictionCallback[0])) {
                chinesePredictionCallback.onPredictionResult(chinesePredictionResult);
            }
        } catch (Exception e) {
            this.log.w("The client has thrown an exception to us: ", e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendPredictionStatus(int i, String str) {
        for (ChinesePredictionService.ChinesePredictionCallback chinesePredictionCallback : (ChinesePredictionService.ChinesePredictionCallback[]) this.predictionCallbacks.toArray(new ChinesePredictionService.ChinesePredictionCallback[0])) {
            chinesePredictionCallback.onPredictionStatus(i, str);
        }
    }

    private void setupConnectionQueue(boolean z) {
        if (z) {
            this.queue.createPersistantConnection(this.persistantConnectionConfig, z);
        }
    }

    private void shutdownHandler() {
        this.shutdownHandler.removeCallbacks(this.transmitOrShutdownRunnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transmitOrShutdown() {
        this.log.d("transmitOrShutdown processing now size=", Integer.valueOf(this.pendingLogsSize));
        if (this.pendingLogsSize >= 10) {
            sendLoggingToServer();
        } else {
            shutdownHandler();
        }
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public void cancelPrediction(String str) {
        this.log.d("cancelPrediction() predictionId=", str);
        if (this.queue == null || !this.activePredictions.containsKey(str)) {
            return;
        }
        this.activePredictions.get(str).cancel();
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public void endSession() {
        this.log.d("endSession()");
        synchronized (this.lock) {
            if (this.sessionActive.get()) {
                this.sessionActive.set(false);
                if (this.useable) {
                    this.shutdownHandler.removeCallbacks(this.transmitOrShutdownRunnable);
                    this.shutdownHandler.postDelayed(this.transmitOrShutdownRunnable, 100L);
                    this.currentSessionLanguage = Integers.STATUS_SUCCESS;
                    this.currentSessionCharacterSetId = Integers.STATUS_SUCCESS;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectFeature[] getDependencies() {
        return ConnectFeature.CHINESE_PREDICTION.values();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectHandler[] getHandlers() {
        return new ConnectHandler[]{this.handler};
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public int getIdleTimeout() {
        return this.idleTimeout;
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public int getPredictionResults() {
        return this.predictionResults;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public String getServiceName() {
        return ConnectFeature.CHINESE_PREDICTION.name();
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public boolean isSessionActive() {
        return this.sessionActive.get();
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public void logResultSelection(String str) throws ConnectException {
        this.log.d("logResultSelection() predictionId=", str);
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public void logResultSelection(String str, ChinesePredictionService.ChinesePrediction chinesePrediction) throws ConnectException {
        this.log.d("logResultSelection() predictionId=", str, " prediction=", chinesePrediction.toString());
        if (!this.useable) {
            throw new ConnectException(109);
        }
        this.dataStore.logPredictionResult(str, 3, chinesePrediction.getPhrase(), chinesePrediction.getSpell(), chinesePrediction.getFullSpell(), chinesePrediction.getAttributes());
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public void logResultSelection(String str, String str2, String str3) throws ConnectException {
        this.log.d("logResultSelection() predictionId=", str, " phrase=", str2, " spelling=", str3);
        if (!this.useable) {
            throw new ConnectException(109);
        }
        this.dataStore.logPredictionResult(str, 3, str2, str3, null, null);
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public void logResultSelection(String str, String str2, String str3, String str4, int[] iArr) throws ConnectException {
        this.log.d("logResultSelection() predictionId=", str, " phrase=", str2, " spelling=", str3, " fullSpell=", str4);
        if (!this.useable) {
            throw new ConnectException(109);
        }
        this.dataStore.logPredictionResult(str, 3, str2, str3, str4, iArr);
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public void registerCallback(ChinesePredictionService.ChinesePredictionCallback chinesePredictionCallback) {
        this.predictionCallbacks.clear();
        this.predictionCallbacks.add(chinesePredictionCallback);
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public String requestPrediction(byte[] bArr) throws ConnectException {
        return requestPrediction(bArr, null);
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public String requestPrediction(byte[] bArr, String str) throws ConnectException {
        this.log.d("requestPrediction() sessionActive=", Boolean.valueOf(this.sessionActive.get()));
        if (!this.sessionActive.get()) {
            throw new ConnectException(109);
        }
        if (!this.useable) {
            throw new ConnectException(109);
        }
        String uuid = UUID.randomUUID().toString();
        ChinesePredictionTransaction chinesePredictionTransaction = new ChinesePredictionTransaction(uuid, this.currentSessionLanguage, this.currentSessionCharacterSetId, this.deviceId, bArr, this.persistantConnectionConfig, getPredictionResults(), str);
        this.activePredictions.put(uuid, chinesePredictionTransaction);
        this.queue.sendTransaction(chinesePredictionTransaction);
        return uuid;
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public void setIdleTimeout(int i) throws ConnectException {
        if (i < 0) {
            throw new ConnectException(112, "Idle timeout cannot be less then 0");
        }
        if (i > 60) {
            throw new ConnectException(112, "Idle timeout cannot be greater then 60");
        }
        this.idleTimeout = i;
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public void setPredictionResults(int i) throws ConnectException {
        if (i <= 0) {
            throw new ConnectException(112, "prediction results cannot be less then 1");
        }
        if (i > 10) {
            throw new ConnectException(112, "prediction results cannot be greater then 10");
        }
        this.predictionResults = i;
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public void startSession(int i, int i2) throws ConnectException {
        this.log.d("startSession(", Integer.valueOf(i), ", ", Integer.valueOf(i2), ")");
        synchronized (this.lock) {
            this.shutdownHandler.removeCallbacks(this.transmitOrShutdownRunnable);
            if (!this.useable) {
                throw new ConnectException(109);
            }
            if (this.sessionActive.get()) {
                throw new ConnectException(109, "Session already started.");
            }
            if (this.deviceId == null) {
                this.deviceId = this.messageBus.getDeviceId();
            }
            if (this.deviceId == null || this.deviceId.length() == 0) {
                this.oemLog.w("Device is not registered, unable to process prediction request");
                throw new ConnectException(109, "Device must be registered before attempting to use " + getServiceName());
            }
            PredictionUrl predictionUrl = new PredictionUrl(this.customerId, i, i2);
            this.log.d("Starting Session url=", predictionUrl.toString());
            this.persistantConnectionConfig = PersistantConnectionConfig.create(predictionUrl.toString(), this.idleTimeout, -1);
            this.loggingConnectionConfig = PersistantConnectionConfig.create(new LoggingUrl(this.customerId).toString(), this.idleTimeout, -1);
            setupConnectionQueue(true);
            this.currentSessionLanguage = i;
            this.currentSessionCharacterSetId = i2;
            this.sessionActive.set(true);
        }
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public void unregisterCallback(ChinesePredictionService.ChinesePredictionCallback chinesePredictionCallback) {
        this.predictionCallbacks.remove(chinesePredictionCallback);
    }

    @Override // com.nuance.connect.api.ChinesePredictionService
    public void unregisterCallbacks() {
        this.predictionCallbacks.clear();
    }
}
