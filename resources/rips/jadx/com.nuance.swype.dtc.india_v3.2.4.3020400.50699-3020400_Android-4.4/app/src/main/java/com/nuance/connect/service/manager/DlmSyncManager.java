package com.nuance.connect.service.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import com.nuance.connect.comm.AbstractTransaction;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.comm.Response;
import com.nuance.connect.comm.SimpleTransaction;
import com.nuance.connect.comm.Transaction;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.internal.common.ManagerService;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.service.manager.interfaces.AccountListener;
import com.nuance.connect.service.manager.interfaces.LanguageListener;
import com.nuance.connect.sqlite.DlmEventsDataSource;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.Alarm;
import com.nuance.connect.util.FileUtils;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.TimeConversion;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;

/* loaded from: classes.dex */
public class DlmSyncManager extends AbstractCommandManager {
    private static final String CATEGORY_STATE = "CATEGORY_STATE";
    public static final String COMMAND_BACKUP = "backup";
    public static final String COMMAND_FAMILY;
    private static final String COMMAND_PULL = "pull";
    private static final String COMMAND_PULL_ACK = "pullAck";
    private static final String COMMAND_PUSH = "push";
    private static final String COMMAND_RESTORE_GET = "restoreGet";
    private static final String COMMAND_RESTORE_REQUEST = "restoreRequest";
    public static final int COMMAND_VERSION = 5;
    private static final String DLM_EVENTS_LAST_SENT = "DLM_EVENTS_LAST_SENT";
    public static final String DLM_EVENTS_SEND = "DLM_EVENTS_SEND";
    private static final String DLM_SYNC_ENABLED = "DLM_SYNC_ENABLED";
    private static final String DLM_SYNC_USER_ENABLED = "DLM_SYNC_USER_ENABLED";
    public static final String MANAGER_NAME;
    private static final InternalMessages[] MESSAGES_HANDLED;
    private static final Integer[] supportedCategories;
    private final AccountListener accountListener;
    private final Set<Integer> categoriesInUse;

    @SuppressLint({"UseSparseArrays"})
    private final HashMap<Integer, DlmState> categoryState;
    private DlmEventsDataSource dataSource;
    private volatile boolean dlmEnabled;
    private final Property.ValueListener<Integer> intListener;
    private final LanguageListener languageListener;
    private final Logger.Log log;
    private final Logger.Log oemLog;
    private int sendDlmEventsInterval;
    private Boolean userDlmEnabled;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DlmState {
        private static final String SEPARATOR = ",";
        private boolean backupInProgress;
        private boolean backupRequired;
        private boolean pullInProgress;
        private boolean pullRequested;
        private String pullTransID;
        private boolean restoreInProgress;
        private boolean restoreRequested;
        private String restoreTransID;

        public DlmState(String str) {
            if (str == null) {
                return;
            }
            String[] split = str.split(",");
            try {
                if (split.length >= 4) {
                    this.pullRequested = Boolean.parseBoolean(split[0]);
                    this.restoreRequested = Boolean.parseBoolean(split[1]);
                    this.backupRequired = Boolean.parseBoolean(split[2]);
                    this.pullTransID = split[3];
                }
            } catch (Exception e) {
                Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName()).e("Error loading DlmState: " + str);
                this.pullRequested = false;
                this.restoreRequested = false;
                this.backupRequired = false;
                this.pullTransID = null;
            }
        }

        public void backupComplete() {
            this.backupRequired = false;
            this.backupInProgress = false;
        }

        public void backupRequired() {
            this.backupRequired = true;
        }

        public String getPullTransactionId() {
            return this.pullTransID;
        }

        public String getRestoreTransactionId() {
            return this.restoreTransID;
        }

        public boolean isBackupHappening() {
            return this.backupInProgress;
        }

        public boolean isBackupRequired() {
            return this.backupRequired;
        }

        public boolean isPullHappening() {
            return this.pullInProgress;
        }

        public boolean isPullRequested() {
            return this.pullRequested;
        }

        public boolean isRestoreHappening() {
            return this.restoreInProgress;
        }

        public boolean isRestoreRequested() {
            return this.restoreRequested;
        }

        public void pause() {
            this.pullTransID = null;
            this.restoreTransID = null;
            this.backupInProgress = false;
            this.pullInProgress = false;
            this.restoreInProgress = false;
        }

        public void pullComplete() {
            this.pullRequested = false;
            this.pullTransID = null;
            this.pullInProgress = false;
        }

        public void pullRequested() {
            this.pullRequested = !this.restoreRequested;
            this.pullInProgress = false;
            this.pullTransID = null;
        }

        public void restoreComplete() {
            this.restoreRequested = false;
            this.pullRequested = false;
            this.pullTransID = null;
            this.restoreTransID = null;
            this.restoreInProgress = false;
            this.pullInProgress = true;
        }

        public void restoreRequested() {
            this.restoreRequested = true;
            this.pullRequested = false;
            this.pullTransID = null;
        }

        public void setPullTransactionId(String str) {
            this.pullTransID = str;
            this.pullRequested = false;
        }

        public void setRestoreTransactionId(String str) {
            this.restoreTransID = str;
        }

        public void startBackup() {
            this.backupRequired = true;
            this.backupInProgress = true;
        }

        public void startPull() {
            this.pullRequested = true;
            this.pullInProgress = true;
        }

        public void startRestore() {
            this.restoreRequested = true;
            this.restoreInProgress = true;
            this.pullRequested = false;
            this.pullTransID = null;
            this.pullInProgress = false;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.pullRequested).append(",").append(this.restoreRequested).append(",").append(this.backupRequired).append(",").append(this.pullTransID).append(",");
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PushTransaction extends SimpleTransaction {
        private final int coreId;
        private final int count;
        private final long rowid;

        public PushTransaction(Command command, int i, int i2, long j) {
            super(command);
            this.count = i2;
            this.coreId = i;
            this.rowid = j;
        }

        @Override // com.nuance.connect.comm.SimpleTransaction, com.nuance.connect.comm.Transaction
        public String getName() {
            return super.getName() + this.coreId;
        }

        @Override // com.nuance.connect.comm.SimpleTransaction, com.nuance.connect.comm.ResponseCallback
        public void onResponse(Response response) {
            this.currentCommand = null;
            if (response.status == 1) {
                DlmSyncManager.this.log.d("processPushResponse cat=" + this.coreId);
                DlmSyncManager.this.dataSource.clearEvents(this.coreId, this.rowid);
                DlmSyncManager.this.finishTransaction(getName());
                Bundle bundle = new Bundle();
                bundle.putInt(Strings.DEFAULT_KEY, this.count);
                bundle.putInt(Strings.DLM_EVENT_CORE, this.coreId);
                DlmSyncManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_DLM_PUSH_COMPLETE, bundle);
            }
        }

        @Override // com.nuance.connect.comm.SimpleTransaction, com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
            super.rollback();
            DlmSyncManager.this.finishTransaction(getName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SyncTransaction extends AbstractTransaction {
        private final int coreId;
        private boolean isRestore;

        SyncTransaction(final int i, String str) {
            this.coreId = i;
            if (!DlmSyncManager.this.categoryState.containsKey(Integer.valueOf(i))) {
                DlmSyncManager.this.oemLog.e("SyncTransaction - unknown core=" + i);
                return;
            }
            if (!Arrays.asList(DlmSyncManager.supportedCategories).contains(Integer.valueOf(i))) {
                DlmSyncManager.this.log.e("SyncTransaction - unsupported core=" + i);
                return;
            }
            if (!DlmSyncManager.this.categoriesInUse.contains(Integer.valueOf(i))) {
                DlmSyncManager.this.oemLog.d("SyncTransaction - Ignoring core not in use; core=" + i);
                return;
            }
            if (str == null || !((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(i))).isBackupRequired()) {
                return;
            }
            DlmSyncManager.this.dataSource.clearEvents(i, DlmSyncManager.this.dataSource.getLastRowId());
            ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(i))).startBackup();
            DlmSyncManager.this.log.d("backupFile: ", str);
            final FileUtils.CountingIterator<String> streamFile = FileUtils.streamFile(str, true);
            FileUtils.deleteFile(str);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(MessageAPI.CATEGORY_ID, Integer.valueOf(i));
            hashMap.put(MessageAPI.RECORDS, streamFile);
            this.currentCommand = DlmSyncManager.this.createCommand(DlmSyncManager.COMMAND_BACKUP, getRequestType(), hashMap, new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.DlmSyncManager.SyncTransaction.1
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super();
                }

                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    SyncTransaction.this.currentCommand = null;
                    if (response.status != 1) {
                        rollback();
                        return;
                    }
                    DlmSyncManager.this.log.d("processBackupResponse cat=" + i);
                    ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(i))).backupComplete();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Strings.DEFAULT_KEY, streamFile != null ? streamFile.getCount() : 0);
                    bundle.putInt(Strings.DLM_EVENT_CORE, i);
                    DlmSyncManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_DLM_BACKUP_COMPLETE, bundle);
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback
                public void rollback() {
                    SyncTransaction.this.currentCommand = null;
                }
            });
        }

        private void pull() {
            this.currentCommand = null;
            DlmSyncManager.this.log.d("SyncTransaction.pull() coreId=" + this.coreId);
            if (DlmSyncManager.this.categoryState.containsKey(Integer.valueOf(this.coreId)) && ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(this.coreId))).isPullHappening()) {
                DlmSyncManager.this.log.e("    Pull already in progress. Ignoring.  Illegal state?");
                return;
            }
            ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(this.coreId))).startPull();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(MessageAPI.CATEGORY_ID, Integer.valueOf(this.coreId));
            this.currentCommand = DlmSyncManager.this.createCommand(DlmSyncManager.COMMAND_PULL, Command.REQUEST_TYPE.BACKGROUND, hashMap, new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.DlmSyncManager.SyncTransaction.4
                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    SyncTransaction.this.currentCommand = null;
                    if (response.status != 1) {
                        if (response.status == 25) {
                            DlmSyncManager.this.log.d("Restore required for category=" + SyncTransaction.this.coreId);
                            ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(SyncTransaction.this.coreId))).restoreRequested();
                            SyncTransaction.this.restoreRequest();
                            ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(SyncTransaction.this.coreId))).pullComplete();
                            return;
                        }
                        if (response.status == 26) {
                            ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(SyncTransaction.this.coreId))).pullComplete();
                            DlmSyncManager.this.log.d("Unexpected request. Clearing pull for coreId=" + SyncTransaction.this.coreId);
                            return;
                        }
                        return;
                    }
                    if (!DlmSyncManager.this.categoriesInUse.contains(Integer.valueOf(SyncTransaction.this.coreId))) {
                        DlmSyncManager.this.log.d("Ignoring pull response because the core is not in use");
                        ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(SyncTransaction.this.coreId))).pullComplete();
                        return;
                    }
                    ArrayList arrayList = new ArrayList();
                    JSONArray jSONArray = (JSONArray) response.parameters.get(MessageAPI.RECORDS);
                    DlmSyncManager.this.log.d("pullResponse coreId=" + SyncTransaction.this.coreId + " numEvents=" + jSONArray.length());
                    arrayList.ensureCapacity(jSONArray.length());
                    for (int i = 0; i < jSONArray.length(); i++) {
                        try {
                            arrayList.add(jSONArray.getString(i));
                        } catch (JSONException e) {
                            DlmSyncManager.this.log.e("pullResponse() Issue loading object: " + e.getMessage());
                        }
                    }
                    if (arrayList.isEmpty()) {
                        return;
                    }
                    DlmSyncManager.this.sendReceivedEvents(SyncTransaction.this.coreId, arrayList);
                    ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(SyncTransaction.this.coreId))).setPullTransactionId(response.transactionId);
                    SyncTransaction.this.pullAck();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void pullAck() {
            DlmSyncManager.this.log.d("pullAck() coreId=" + this.coreId);
            DlmState dlmState = (DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(this.coreId));
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(MessageAPI.CATEGORY_ID, Integer.valueOf(this.coreId));
            hashMap.put(MessageAPI.TRANSACTION_ID, dlmState.getPullTransactionId());
            this.currentCommand = DlmSyncManager.this.createCommand(DlmSyncManager.COMMAND_PULL_ACK, Command.REQUEST_TYPE.BACKGROUND, hashMap);
            this.currentCommand.allowDuplicateOfCommand = false;
            this.currentCommand.responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.DlmSyncManager.SyncTransaction.5
                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    SyncTransaction.this.currentCommand = null;
                    ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(SyncTransaction.this.coreId))).pullComplete();
                    if (response.status != 1) {
                        DlmSyncManager.this.log.e("pullAck response failed status=", Integer.valueOf(response.status));
                    }
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void resetRestore() {
            DlmState dlmState = (DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(this.coreId));
            if (dlmState != null) {
                dlmState.restoreComplete();
                dlmState.restoreRequested();
            }
        }

        private void restoreGet() {
            this.currentCommand = null;
            DlmSyncManager.this.log.d("SyncTransaction.restoreGet() core=" + this.coreId);
            if (!DlmSyncManager.this.categoriesInUse.contains(Integer.valueOf(this.coreId))) {
                DlmSyncManager.this.log.d("Ignoring restore because the current core is no longer in use. core=" + this.coreId);
                resetRestore();
            } else {
                if (!((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(this.coreId))).isRestoreRequested()) {
                    DlmSyncManager.this.log.e("The core (" + this.coreId + ") does not have a pending restore request");
                    ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(this.coreId))).restoreComplete();
                    return;
                }
                DlmState dlmState = (DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(this.coreId));
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(MessageAPI.CATEGORY_ID, Integer.valueOf(this.coreId));
                hashMap.put(MessageAPI.TRANSACTION_ID, dlmState.getRestoreTransactionId());
                this.currentCommand = DlmSyncManager.this.createCommand(DlmSyncManager.COMMAND_RESTORE_GET, getRequestType(), hashMap, new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.DlmSyncManager.SyncTransaction.3
                    @Override // com.nuance.connect.comm.ResponseCallback
                    public void onResponse(Response response) {
                        OutputStreamWriter outputStreamWriter;
                        OutputStreamWriter outputStreamWriter2;
                        BufferedWriter bufferedWriter;
                        int i;
                        BufferedWriter bufferedWriter2 = null;
                        SyncTransaction.this.currentCommand = null;
                        if (!DlmSyncManager.this.dlmEnabled) {
                            DlmSyncManager.this.log.d("    DLM Sync Disabled. Ignoring request.");
                            return;
                        }
                        if (response.status != 1) {
                            SyncTransaction.this.resetRestore();
                            return;
                        }
                        DlmSyncManager.this.log.d("restoreGetResponse coreId=" + SyncTransaction.this.coreId);
                        if (!DlmSyncManager.this.categoriesInUse.contains(Integer.valueOf(SyncTransaction.this.coreId))) {
                            DlmSyncManager.this.log.d("Ignoring restore response because the current category has not been used. cat=" + SyncTransaction.this.coreId);
                            SyncTransaction.this.resetRestore();
                            return;
                        }
                        JSONArray jSONArray = (JSONArray) response.parameters.get(MessageAPI.RECORDS);
                        DlmSyncManager.this.log.d("restoreGetResponse numEvents=" + jSONArray.length());
                        try {
                            File createTempFile = File.createTempFile("temp", ".batch", DlmSyncManager.this.client.getCacheDir());
                            try {
                                outputStreamWriter2 = new OutputStreamWriter(new FileOutputStream(createTempFile), "UTF-8");
                                try {
                                    bufferedWriter = new BufferedWriter(outputStreamWriter2);
                                    i = 0;
                                } catch (Throwable th) {
                                    th = th;
                                    outputStreamWriter = outputStreamWriter2;
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                outputStreamWriter = null;
                            }
                            while (true) {
                                try {
                                    int i2 = i;
                                    if (i2 >= jSONArray.length()) {
                                        break;
                                    }
                                    try {
                                        bufferedWriter.write(jSONArray.getString(i2));
                                        bufferedWriter.newLine();
                                    } catch (JSONException e) {
                                        DlmSyncManager.this.log.e((Object) "processRestoreGetResponse() Issue loading object: ", (Throwable) e);
                                    }
                                    i = i2 + 1;
                                } catch (Throwable th3) {
                                    th = th3;
                                    bufferedWriter2 = bufferedWriter;
                                    outputStreamWriter = outputStreamWriter2;
                                }
                                th = th3;
                                bufferedWriter2 = bufferedWriter;
                                outputStreamWriter = outputStreamWriter2;
                                if (bufferedWriter2 != null) {
                                    bufferedWriter2.close();
                                }
                                if (outputStreamWriter != null) {
                                    outputStreamWriter.close();
                                }
                                throw th;
                            }
                            bufferedWriter.close();
                            DlmSyncManager.this.sendRestoreEvents(SyncTransaction.this.coreId, createTempFile.getAbsolutePath());
                            bufferedWriter.close();
                            outputStreamWriter2.close();
                        } catch (IOException e2) {
                            DlmSyncManager.this.log.e((Object) "restoreGetResponse() error creating temp file", (Throwable) e2);
                        }
                        ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(SyncTransaction.this.coreId))).restoreComplete();
                    }

                    @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                    public boolean onRetry(Command command, int i, int i2, String str) {
                        return (i2 == 3 && SyncTransaction.this.retryCount.get() < 3) || super.onRetry(command, i, i2, str);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void restoreRequest() {
            this.currentCommand = null;
            this.isRestore = true;
            DlmSyncManager.this.log.d("SyncTransaction.restoreRequest() cat=" + this.coreId);
            if (!DlmSyncManager.this.categoryState.containsKey(Integer.valueOf(this.coreId))) {
                DlmSyncManager.this.log.e("DLMManager.restoreRequest() unknown category=" + this.coreId);
                return;
            }
            if (((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(this.coreId))).isRestoreHappening()) {
                DlmSyncManager.this.log.d("    Restore already in progress. Ignoring request.");
                DlmSyncManager.this.log.e("    This should be an illegal condition.");
                return;
            }
            ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(this.coreId))).startRestore();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(MessageAPI.CATEGORY_ID, Integer.valueOf(this.coreId));
            this.currentCommand = DlmSyncManager.this.createCommand(DlmSyncManager.COMMAND_RESTORE_REQUEST, getRequestType(), hashMap, new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.DlmSyncManager.SyncTransaction.2
                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    SyncTransaction.this.currentCommand = null;
                    if (!DlmSyncManager.this.dlmEnabled) {
                        DlmSyncManager.this.log.d("    DLM Sync Disabled. Ignoring request.");
                    } else if (response.status == 1) {
                        DlmSyncManager.this.log.d("restoreRequestResponse core=" + SyncTransaction.this.coreId);
                        ((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(SyncTransaction.this.coreId))).setRestoreTransactionId(response.transactionId);
                    }
                }
            });
        }

        @Override // com.nuance.connect.comm.Transaction
        public void cancel() {
            DlmSyncManager.this.log.e("Should not be cancel-able");
            rollback();
        }

        @Override // com.nuance.connect.comm.Transaction
        public String createDownloadFile(Context context) {
            DlmSyncManager.this.log.e("SyncTransaction - unexpectedly called createDownloadFile");
            return null;
        }

        @Override // com.nuance.connect.comm.Transaction
        public String getName() {
            return SyncTransaction.class.getName() + "." + this.coreId;
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command getNextCommand() {
            if (!DlmSyncManager.this.dlmEnabled) {
                DlmSyncManager.this.log.d("DLM is disabled... quitting transaction");
                this.currentCommand = null;
                return null;
            }
            if (this.isRestore) {
                return this.currentCommand;
            }
            if (!DlmSyncManager.this.categoriesInUse.contains(Integer.valueOf(this.coreId))) {
                DlmSyncManager.this.log.d("getNextCommand, the current category is not in use; quitting. cat=" + this.coreId);
                this.currentCommand = null;
            } else if (this.currentCommand == null && DlmSyncManager.this.categoryState.containsKey(Integer.valueOf(this.coreId))) {
                if (((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(this.coreId))).isRestoreHappening()) {
                    restoreGet();
                } else if (((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(this.coreId))).isRestoreRequested()) {
                    restoreRequest();
                } else if (((DlmState) DlmSyncManager.this.categoryState.get(Integer.valueOf(this.coreId))).isPullRequested()) {
                    pull();
                }
            }
            return this.currentCommand;
        }

        @Override // com.nuance.connect.comm.Transaction
        public int getPriority() {
            return 0;
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command.REQUEST_TYPE getRequestType() {
            return Command.REQUEST_TYPE.USER;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onEndProcessing() {
            DlmSyncManager.this.finishTransaction(getName());
            DlmSyncManager.this.savePreferences();
            if (this.isRestore) {
                DlmSyncManager.this.client.postMessageDelayed(InternalMessages.MESSAGE_COMMAND_CHECK_SYNC, TimeUnit.SECONDS.toMillis(15L));
            } else {
                DlmSyncManager.this.checkDlmSync();
            }
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
            this.currentCommand = null;
        }
    }

    static {
        String name = ManagerService.SYNC.getName();
        COMMAND_FAMILY = name;
        MANAGER_NAME = name;
        supportedCategories = new Integer[]{1, 2};
        MESSAGES_HANDLED = new InternalMessages[]{InternalMessages.MESSAGE_COMMAND_PULL_DLM_EVENTS, InternalMessages.MESSAGE_COMMAND_DLM_BACKUP_REQUIRED, InternalMessages.MESSAGE_CLIENT_DLM_RESTORE, InternalMessages.MESSAGE_CLIENT_DLM_BACKUP_EVENTS, InternalMessages.MESSAGE_CLIENT_SET_DLM_STATUS, InternalMessages.MESSAGE_CLIENT_DLM_SYNC_NOW, InternalMessages.MESSAGE_CLIENT_SET_DLM_SYNC_FREQUECY, InternalMessages.MESSAGE_COMMAND_CHECK_SYNC};
    }

    public DlmSyncManager(ConnectClient connectClient) {
        super(connectClient);
        this.sendDlmEventsInterval = 0;
        this.categoriesInUse = new ConcurrentSkipListSet();
        this.userDlmEnabled = null;
        this.intListener = new Property.IntegerValueListener() { // from class: com.nuance.connect.service.manager.DlmSyncManager.1
            @Override // com.nuance.connect.internal.Property.ValueListener
            public void onValueChanged(Property<Integer> property) {
                if (property.getKey().equals(ConnectConfiguration.ConfigProperty.DLM_ADD_WORD_FREQUENCY.name())) {
                    DlmSyncManager.this.log.d("dlmImmediateEventsTimer.setConfiguration(" + property + ")");
                    return;
                }
                if (property.getKey().equals(ConnectConfiguration.ConfigProperty.DLM_SYNC_FREQUENCY.name())) {
                    DlmSyncManager.this.log.d("dlmEventsIntervalChangeListener.setConfiguration(" + property + ")");
                    DlmSyncManager.this.sendDlmEventsInterval = property.getValue().intValue();
                    DlmSyncManager.this.client.getAlarmBuilder(DlmSyncManager.class, DlmSyncManager.DLM_EVENTS_SEND).build().cancel();
                    DlmSyncManager.this.setDlmEventsTimer();
                }
            }
        };
        this.accountListener = new AccountListener() { // from class: com.nuance.connect.service.manager.DlmSyncManager.2
            @Override // com.nuance.connect.service.manager.interfaces.AccountListener
            public void onInvalidated() {
                DlmSyncManager.this.log.d("DLMManager.onInvalidated");
                DlmSyncManager.this.disableDlmSync();
                Iterator it = DlmSyncManager.this.categoryState.entrySet().iterator();
                while (it.hasNext()) {
                    ((DlmState) ((Map.Entry) it.next()).getValue()).pause();
                }
            }

            @Override // com.nuance.connect.service.manager.interfaces.AccountListener
            public void onLinked() {
                DlmSyncManager.this.log.d("DLMManager.onLinked");
                if (DlmSyncManager.this.userDlmEnabled == null || !DlmSyncManager.this.userDlmEnabled.booleanValue()) {
                    return;
                }
                DlmSyncManager.this.enableDlmSync();
            }
        };
        this.languageListener = new LanguageListener() { // from class: com.nuance.connect.service.manager.DlmSyncManager.3
            @Override // com.nuance.connect.service.manager.interfaces.LanguageListener
            public void onLanguageUpdate(int[] iArr) {
                DlmSyncManager.this.categoriesInUse.clear();
                List asList = Arrays.asList(DlmSyncManager.supportedCategories);
                if (iArr != null) {
                    for (int i : iArr) {
                        int coreForLanguage = DlmSyncManager.this.client.getCoreForLanguage(i);
                        if (coreForLanguage >= 0 && asList.contains(Integer.valueOf(coreForLanguage))) {
                            DlmSyncManager.this.categoriesInUse.add(Integer.valueOf(coreForLanguage));
                        }
                    }
                }
                if (DlmSyncManager.this.managerStartState == AbstractCommandManager.ManagerState.STARTING) {
                    DlmSyncManager.this.managerStartComplete();
                }
                if (DlmSyncManager.this.managerStartState == AbstractCommandManager.ManagerState.STARTED) {
                    DlmSyncManager.this.checkDlmSync();
                }
            }

            @Override // com.nuance.connect.service.manager.interfaces.LanguageListener
            public void onLocaleUpdate(Locale locale) {
            }
        };
        this.categoryState = new HashMap<>();
        this.log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
        this.oemLog = Logger.getLog(Logger.LoggerType.OEM, getClass().getSimpleName());
        this.version = 5;
        this.commandFamily = COMMAND_FAMILY;
        setMessagesHandled(MESSAGES_HANDLED);
        int[] iArr = {1};
        this.validCommands.addCommand(COMMAND_BACKUP, iArr);
        this.validCommands.addCommand(COMMAND_RESTORE_REQUEST, iArr);
        this.validCommands.addCommand(COMMAND_RESTORE_GET, iArr);
        this.validCommands.addCommand(COMMAND_PUSH, iArr);
        this.validCommands.addCommand(COMMAND_PULL_ACK, iArr);
        this.validCommands.addCommand(COMMAND_PULL, new int[]{25, 26, 1});
    }

    private void backup(String str, int i) {
        this.log.d("DLMManager.backup()  category=" + i);
        if (!this.categoryState.containsKey(Integer.valueOf(i))) {
            this.log.e("    Backup already for unsupported category!  (Initialization failure?)");
            FileUtils.deleteFile(str);
        } else if (!this.categoryState.containsKey(Integer.valueOf(i)) || !this.categoryState.get(Integer.valueOf(i)).isBackupHappening()) {
            startTransaction(new SyncTransaction(i, str));
        } else {
            this.log.d("    Backup already in progress. Ignoring request.");
            FileUtils.deleteFile(str);
        }
    }

    private void checkBackupRequired() {
        if (this.dataSource.isFull()) {
            this.log.d("checkBackupRequired(): backup is required");
            Iterator<Map.Entry<Integer, DlmState>> it = this.categoryState.entrySet().iterator();
            while (it.hasNext()) {
                it.next().getValue().backupRequired();
            }
            savePreferences();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkDlmSync() {
        if (!this.dlmEnabled) {
            this.log.d("checkDlmSync(): dlmEnabled not enabled");
            return;
        }
        Iterator<Integer> it = this.categoriesInUse.iterator();
        while (it.hasNext()) {
            int intValue = it.next().intValue();
            this.log.d("DLMManager.checkDlmSync() currentCategory=" + intValue);
            if (intValue == 0) {
                return;
            }
            if (!Arrays.asList(supportedCategories).contains(Integer.valueOf(intValue))) {
                this.log.d("category not supported: " + intValue);
                return;
            }
            DlmState dlmState = this.categoryState.get(Integer.valueOf(intValue));
            if (dlmState.isBackupRequired()) {
                Bundle bundle = new Bundle();
                bundle.putInt(Strings.DLM_EVENT_CORE, intValue);
                this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_DLM_BACKUP_REQUEST, bundle);
            } else if (dlmState.isRestoreRequested() || (dlmState.isPullRequested() && !dlmState.isPullHappening())) {
                startTransaction(new SyncTransaction(intValue, null));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disableDlmSync() {
        setEnabled(false);
        for (Map.Entry<Integer, DlmState> entry : this.categoryState.entrySet()) {
            entry.getValue().backupRequired();
            entry.getValue().restoreRequested();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableDlmSync() {
        for (Map.Entry<Integer, DlmState> entry : this.categoryState.entrySet()) {
            entry.getValue().backupRequired();
            entry.getValue().restoreRequested();
        }
        setEnabled(true);
        checkDlmSync();
    }

    @SuppressLint({"UseSparseArrays"})
    private void loadPreferences() {
        PersistentDataStore dataStore = this.client.getDataStore();
        for (Integer num : supportedCategories) {
            int intValue = num.intValue();
            this.categoryState.put(Integer.valueOf(intValue), new DlmState(dataStore.readString(CATEGORY_STATE + intValue, null)));
        }
        this.dlmEnabled = this.client.isAccountLinked() && dataStore.readBoolean(DLM_SYNC_ENABLED, true);
        this.userDlmEnabled = dataStore.readBoolean(DLM_SYNC_USER_ENABLED, (Boolean) null);
    }

    private void openDlmDB() {
        if (this.dataSource == null) {
            this.dataSource = new DlmEventsDataSource(this.client);
        }
    }

    private void push() {
        this.log.d("DLMManager.push()");
        resetDlmEventsTimer();
        int[] eventCategories = this.dataSource.getEventCategories();
        if (eventCategories == null || eventCategories.length == 0) {
            this.log.d("DLMManager.push() found no categories; exiting push");
            return;
        }
        long lastRowId = this.dataSource.getLastRowId();
        for (int i : eventCategories) {
            if (Arrays.asList(supportedCategories).contains(Integer.valueOf(i))) {
                JSONArray events = this.dataSource.getEvents(i, lastRowId);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(MessageAPI.EXTRA, Long.valueOf(lastRowId));
                hashMap.put(MessageAPI.CATEGORY_ID, Integer.valueOf(i));
                hashMap.put(MessageAPI.CHANGE_LOG, events);
                this.log.d("   DLMManager.push() category=" + i + " time=" + lastRowId + " events count=" + events.length(), ", events=", events);
                Command createCommand = createCommand(COMMAND_PUSH, Command.REQUEST_TYPE.BACKGROUND, hashMap);
                createCommand.allowDuplicateOfCommand = false;
                sendTransaction(new PushTransaction(createCommand, i, events.length(), lastRowId));
            } else {
                this.log.e("category not supported: " + i);
                this.dataSource.clearEvents(i, lastRowId);
            }
        }
    }

    private void resetDlmEventsTimer() {
        this.client.getDataStore().saveLong(DLM_EVENTS_LAST_SENT, System.currentTimeMillis());
        setDlmEventsTimer();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void savePreferences() {
        PersistentDataStore dataStore = this.client.getDataStore();
        for (Map.Entry<Integer, DlmState> entry : this.categoryState.entrySet()) {
            dataStore.saveString(CATEGORY_STATE + entry.getKey(), entry.getValue().toString());
        }
        dataStore.saveBoolean(DLM_SYNC_ENABLED, this.dlmEnabled);
        if (this.userDlmEnabled != null) {
            dataStore.saveBoolean(DLM_SYNC_USER_ENABLED, this.userDlmEnabled.booleanValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendReceivedEvents(int i, ArrayList<String> arrayList) {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt(Strings.DLM_EVENT_CORE, i);
            bundle.putInt(Strings.DLM_EVENT_COUNT, arrayList.size());
            bundle.putInt(Strings.DLM_EVENT_ACK, InternalMessages.MESSAGE_CLIENT_PROCESS_DLM_EVENTS_ACK.ordinal());
            String absolutePath = File.createTempFile("temp", ".download", this.client.getCacheDir()).getAbsolutePath();
            FileUtils.persistListToFile(arrayList, absolutePath);
            bundle.putString(Strings.DLM_EVENT_FILE, absolutePath);
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_PROCESS_DLM_EVENTS, bundle);
        } catch (IOException e) {
            this.log.e("Could not send the received events to the host: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendRestoreEvents(int i, String str) {
        Bundle bundle = new Bundle();
        bundle.putString(Strings.DLM_EVENT_FILE, str);
        bundle.putInt(Strings.DLM_EVENT_CORE, i);
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_DLM_RESTORE, bundle);
    }

    private void sendTransaction(Transaction transaction) {
        if (this.dlmEnabled) {
            startTransaction(transaction);
        } else {
            this.log.d("sendTransaction(): dropping transaction: " + transaction.getNextCommand());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDlmEventsTimer() {
        if (!this.dlmEnabled) {
            this.log.d("    DLM Sync Disabled. Ignoring request to set timer.");
            return;
        }
        if (this.sendDlmEventsInterval > 0) {
            long convertSecondsToTimeStamp = TimeConversion.convertSecondsToTimeStamp(this.sendDlmEventsInterval, this.client.getDataStore().readLong(DLM_EVENTS_LAST_SENT, System.currentTimeMillis()));
            if (convertSecondsToTimeStamp <= System.currentTimeMillis()) {
                timerDlmEvents();
                return;
            }
            Alarm build = this.client.getAlarmBuilder(DlmSyncManager.class, DLM_EVENTS_SEND).triggerTime(convertSecondsToTimeStamp).build();
            build.set();
            this.log.v("setDlmEventsTimer() " + build);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x007f, code lost:            if (r0.isBackupRequired() != false) goto L39;     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0081, code lost:            push();     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void timerDlmEvents() {
        /*
            r4 = this;
            com.nuance.connect.util.Logger$Log r1 = r4.log
            java.lang.String r2 = "timerDlmEvents() isFull="
            com.nuance.connect.sqlite.DlmEventsDataSource r0 = r4.dataSource
            if (r0 == 0) goto L27
            com.nuance.connect.sqlite.DlmEventsDataSource r0 = r4.dataSource
            boolean r0 = r0.isFull()
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
        L13:
            r1.v(r2, r0)
            com.nuance.connect.sqlite.DlmEventsDataSource r0 = r4.dataSource
            if (r0 == 0) goto L1e
            boolean r0 = r4.dlmEnabled
            if (r0 != 0) goto L29
        L1e:
            com.nuance.connect.util.Logger$Log r0 = r4.log
            java.lang.String r1 = "    DLM Sync Disabled. Ignoring request."
            r0.d(r1)
        L26:
            return
        L27:
            r0 = 0
            goto L13
        L29:
            java.util.Set<java.lang.Integer> r0 = r4.categoriesInUse
            java.util.Iterator r1 = r0.iterator()
        L2f:
            boolean r0 = r1.hasNext()
            if (r0 == 0) goto L85
            java.lang.Object r0 = r1.next()
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r2 = r0.intValue()
            java.util.HashMap<java.lang.Integer, com.nuance.connect.service.manager.DlmSyncManager$DlmState> r0 = r4.categoryState
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)
            java.lang.Object r0 = r0.get(r3)
            com.nuance.connect.service.manager.DlmSyncManager$DlmState r0 = (com.nuance.connect.service.manager.DlmSyncManager.DlmState) r0
            com.nuance.connect.sqlite.DlmEventsDataSource r3 = r4.dataSource
            boolean r3 = r3.isFull()
            if (r3 != 0) goto L61
            if (r0 == 0) goto L79
            boolean r3 = r0.isBackupRequired()
            if (r3 == 0) goto L79
            boolean r3 = r0.isBackupHappening()
            if (r3 != 0) goto L79
        L61:
            com.nuance.connect.sqlite.DlmEventsDataSource r0 = r4.dataSource
            r0.reset()
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r3 = "DLM_EVENT_CORE"
            r0.putInt(r3, r2)
            com.nuance.connect.service.ConnectClient r2 = r4.client
            com.nuance.connect.internal.common.InternalMessages r3 = com.nuance.connect.internal.common.InternalMessages.MESSAGE_HOST_DLM_BACKUP_REQUEST
            r2.sendMessageToHost(r3, r0)
            goto L2f
        L79:
            if (r0 == 0) goto L2f
            boolean r0 = r0.isBackupRequired()
            if (r0 != 0) goto L2f
            r4.push()
            goto L2f
        L85:
            r4.resetDlmEventsTimer()
            goto L26
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.service.manager.DlmSyncManager.timerDlmEvents():void");
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.CommandManager
    public void alarmNotification(String str, Bundle bundle) {
        if (str == null || !str.equals(DLM_EVENTS_SEND)) {
            return;
        }
        timerDlmEvents();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void deregister() {
        super.deregister();
        disableDlmSync();
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public String[] getDependencies() {
        ArrayList arrayList = new ArrayList();
        for (ManagerService managerService : ManagerService.SYNC.values()) {
            arrayList.add(managerService.getName());
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void init() {
        loadPreferences();
        openDlmDB();
        this.client.addListener(this.accountListener);
        this.client.addLanguageListener(this.languageListener);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.comm.ResponseCallback
    public void onFailure(Command command) {
        this.log.d("DlmManager.onFailure " + command);
        if (command.command.equals(COMMAND_BACKUP) || command.command.equals(COMMAND_PULL) || command.command.equals(COMMAND_PULL_ACK) || command.command.equals(COMMAND_RESTORE_REQUEST) || command.command.equals(COMMAND_RESTORE_GET)) {
            int intValue = ((Integer) command.parameters.get(MessageAPI.CATEGORY_ID)).intValue();
            if (this.categoryState.containsKey(Integer.valueOf(intValue))) {
                this.categoryState.get(Integer.valueOf(intValue)).pause();
            }
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        boolean z = false;
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_COMMAND_PULL_DLM_EVENTS:
                int i = message.getData().getInt(Strings.DLM_EVENT_CORE);
                this.log.v("MESSAGE_COMMAND_PULL_DLM_EVENTS category=" + i);
                if (this.categoryState.containsKey(Integer.valueOf(i))) {
                    this.categoryState.get(Integer.valueOf(i)).pullRequested();
                    this.log.v("    pullRequested = " + this.categoryState.get(Integer.valueOf(i)).isPullRequested());
                }
                checkDlmSync();
                return true;
            case MESSAGE_COMMAND_DLM_BACKUP_REQUIRED:
                int i2 = message.getData().getInt(Strings.DLM_EVENT_CORE);
                this.log.v("MESSAGE_COMMAND_DLM_BACKUP_REQUIRED category=" + i2);
                if (this.categoryState.containsKey(Integer.valueOf(i2))) {
                    this.categoryState.get(Integer.valueOf(i2)).backupRequired();
                    this.log.v("    backupRequired = " + this.categoryState.get(Integer.valueOf(i2)).isBackupRequired());
                }
                checkDlmSync();
                return true;
            case MESSAGE_CLIENT_DLM_RESTORE:
                this.log.v("MESSAGE_CLIENT_DLM_RESTORE");
                int i3 = message.getData().getInt(Strings.DLM_EVENT_CORE);
                if (this.categoryState.containsKey(Integer.valueOf(i3))) {
                    this.categoryState.get(Integer.valueOf(i3)).restoreRequested();
                }
                startTransaction(new SyncTransaction(i3, null));
                return true;
            case MESSAGE_CLIENT_DLM_BACKUP_EVENTS:
                this.log.v("MESSAGE_CLIENT_DLM_BACKUP_EVENTS");
                Bundle data = message.getData();
                backup(data.getString(Strings.DEFAULT_KEY), data.getInt(Strings.DLM_EVENT_CORE));
                return true;
            case MESSAGE_CLIENT_DLM_SYNC_NOW:
                this.log.v("MESSAGE_CLIENT_DLM_SYNC_NOW");
                push();
                return true;
            case MESSAGE_CLIENT_SET_DLM_STATUS:
                Bundle data2 = message.getData();
                if (this.userDlmEnabled == null || this.userDlmEnabled.booleanValue() != data2.getBoolean(Strings.DEFAULT_KEY)) {
                    this.userDlmEnabled = Boolean.valueOf(data2.getBoolean(Strings.DEFAULT_KEY));
                    z = true;
                } else if (this.dlmEnabled != this.userDlmEnabled.booleanValue()) {
                    z = true;
                }
                this.log.v("MESSAGE_CLIENT_SET_DLM_STATUS changed=" + z + ", value=" + this.userDlmEnabled);
                if (!z) {
                    return true;
                }
                if (this.userDlmEnabled.booleanValue()) {
                    enableDlmSync();
                } else {
                    disableDlmSync();
                }
                savePreferences();
                return true;
            case MESSAGE_CLIENT_SET_DLM_SYNC_FREQUECY:
                this.client.setProperty(ConnectConfiguration.ConfigProperty.DLM_SYNC_FREQUENCY, Integer.valueOf(message.getData().getInt(Strings.DEFAULT_KEY)));
                return true;
            case MESSAGE_COMMAND_CHECK_SYNC:
                checkDlmSync();
                return true;
            default:
                return false;
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.comm.ResponseCallback
    public void onResponse(Response response) {
        this.log.e("Unexpected response.  Command=", response.command);
        savePreferences();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.connect.service.manager.AbstractCommandManager
    public void sendCommand(Command command) {
        if (this.dlmEnabled) {
            super.sendCommand(command);
        } else {
            this.log.d("sendCommand(): dropping command: " + command.command);
            onFailure(command);
        }
    }

    public void setEnabled(boolean z) {
        this.log.d("DLMManager.setEnabled(): " + z);
        if (z == this.dlmEnabled) {
            return;
        }
        this.dlmEnabled = z && this.client.isAccountLinked();
        if (this.dlmEnabled) {
            resetDlmEventsTimer();
        } else {
            this.dataSource.reset();
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void start() {
        this.managerStartState = AbstractCommandManager.ManagerState.STARTING;
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_DLM_STATUS);
        this.client.addListener(ConnectConfiguration.ConfigProperty.DLM_ADD_WORD_FREQUENCY, this.intListener);
        this.client.addListener(ConnectConfiguration.ConfigProperty.DLM_SYNC_FREQUENCY, this.intListener);
        managerStartComplete();
    }
}
