package com.nuance.connect.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.api.LivingLanguageService;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.Strings;
import com.nuance.connect.host.service.HostInterface;
import com.nuance.connect.host.service.HostSystemData;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.APIHandlers;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.sqlite.DlmEventsDataSource;
import com.nuance.connect.util.EncryptUtils;
import com.nuance.connect.util.FileUtils;
import com.nuance.connect.util.InstallMetadata;
import com.nuance.connect.util.Logger;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.IME;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DLMConnectorInternal extends AbstractService implements DLMConnector {
    private static final int BATCH_SIZE = 1000;
    private static final String BUCKET_CATEGORY = "categoryId";
    private static final String BUCKET_CONTEXT1 = "context1";
    private static final String BUCKET_CONTEXT2 = "context2";
    private static final String BUCKET_CONTEXT3 = "context3";
    private static final String BUCKET_DUPLICATES = "removeDuplicates";
    private static final String BUCKET_SCAN_ACTION = "scanAction";
    private static final String BUCKET_USER_ACTION = "userAction";
    private static final int DLM_CATEGORY_USER = 0;
    public static final String DLM_EVENT_QUEUE_PREF = "DLM_EVENT_QUEUE_DATA";
    public static final int DLM_SIZE_LARGE = 3145728;
    public static final int DLM_SIZE_MIN = 102400;
    public static final int DLM_SIZE_NORMAL = 2097152;
    public static final int DLM_SIZE_SMALL = 1048576;
    public static final int DLM_SIZE_TINY = 204800;
    private static final String DLM_TYPE = "DLM_TYPE";
    private static final int DLM_TYPE_DELETE_CORE = 2;
    private static final int DLM_TYPE_RECORDSET = 1;
    private static final int DLM_TYPE_SCANNER_SET = 3;
    private static final int DLM_TYPE_SCANNER_SET_BATCH = 4;
    private static final int SCAN_BATCH_SIZE = 50;
    private static final int WRITE_DELAY = 2000;
    private boolean accountEnabled;
    private BackupEventsFileQueue backupEvents;
    private ConnectServiceManagerInternal connectService;
    private String currentAppName;
    private int currentInputType;
    private String currentLocale;
    private String currentLocation;
    private boolean dataAccepted;
    private final DlmEventsDataSource dataSource;
    private InstallMetadata dlmEventProcessingQueue;
    private volatile DlmEventBatchProcessRunnable eventsBatchRunnable;
    private DLMConnector.EventNotificationCallback genericEventNotification;
    private boolean performingBackup;
    private volatile boolean processingScanningSet;
    private boolean syncEnabled;
    private static final Integer[] SUPPORTED_CORES = {1, 2};
    private static final InternalMessages[] MESSAGE_IDS = {InternalMessages.MESSAGE_HOST_PROCESS_DLM_EVENTS, InternalMessages.MESSAGE_HOST_PROCESS_DLM_DELETE_CATEGORY};
    private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM, getClass().getSimpleName());
    private int backupCore = 0;
    private final SparseArray<DLMConnector.DlmEventCallback> eventCallbacks = new SparseArray<>();
    private final List<ParentScannerBucket> buckets = new ArrayList();
    private final SparseArray<DLMConnector.DLMInformation> dlmInfoList = new SparseArray<>();
    private boolean allDlmYielding = false;
    private SparseBooleanArray dlmYielding = new SparseBooleanArray();
    private final Comparator<String> timestampPackageComparator = new Comparator<String>() { // from class: com.nuance.connect.internal.DLMConnectorInternal.1
        @Override // java.util.Comparator
        public int compare(String str, String str2) {
            return Long.compare(DLMConnectorInternal.this.dlmEventProcessingQueue.getLongProp(str, Strings.DLM_EVENT_TIMESTAMP), DLMConnectorInternal.this.dlmEventProcessingQueue.getLongProp(str2, Strings.DLM_EVENT_TIMESTAMP));
        }
    };
    Property.StringValueListener stringListener = new Property.StringValueListener() { // from class: com.nuance.connect.internal.DLMConnectorInternal.2
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<String> property) {
            if (property.getKey().equals("LOCATION_COUNTRY")) {
                DLMConnectorInternal.this.processLocationUpate(property.getValue());
            }
        }
    };
    Property.BooleanValueListener booleanListener = new Property.BooleanValueListener() { // from class: com.nuance.connect.internal.DLMConnectorInternal.3
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<Boolean> property) {
            if (property.getKey().equals(UserSettings.USER_DLM_SYNC_ENABLED)) {
                DLMConnectorInternal.this.syncEnabled = property.getValue().booleanValue();
            } else if (property.getKey().equals(UserSettings.USER_DLM_SYNC_ACCOUNT_ENABLED)) {
                DLMConnectorInternal.this.accountEnabled = property.getValue().booleanValue();
            } else if (property.getKey().equals(UserSettings.USER_ALLOW_DATA_COLLECTION)) {
                DLMConnectorInternal.this.dataAccepted = property.getValue().booleanValue();
            }
            DLMConnectorInternal.this.oemLog.d("Dlm sync property change: ", property.getKey(), "(", property.getValue(), ")");
            DLMConnectorInternal.this.oemLog.d("syncEnabled: ", Boolean.valueOf(DLMConnectorInternal.this.syncEnabled), "; accountEnabled: ", Boolean.valueOf(DLMConnectorInternal.this.accountEnabled), "; dataAccepted:", Boolean.valueOf(DLMConnectorInternal.this.dataAccepted), "; syncAllowed:", Boolean.valueOf(DLMConnectorInternal.this.dlmSyncAllowed()));
        }
    };
    private final Handler.Callback dlmCallback = new Handler.Callback() { // from class: com.nuance.connect.internal.DLMConnectorInternal.4
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            DLMConnectorInternal.this.log.d("handleMessage what=", Integer.valueOf(message.what));
            switch (message.what) {
                case 1:
                    DLMConnectorInternal.this.processDlmEvents(message.arg1, (String) message.obj);
                    return true;
                case 2:
                    DLMConnectorInternal.this.processDlmDelete(message.arg1, (String) message.obj);
                    return true;
                case 3:
                    DLMConnectorInternal.this.processDlmScannerSet(message.arg1, (String) message.obj);
                    return true;
                case 4:
                    DLMConnectorInternal.this.processScanBatch((ScanningSetBatchInfo) message.obj);
                    return true;
                default:
                    DLMConnectorInternal.this.log.e("Unknown message: ", Integer.valueOf(message.what));
                    return false;
            }
        }
    };
    private ConnectHandler handler = new ConnectHandler() { // from class: com.nuance.connect.internal.DLMConnectorInternal.6
        @Override // com.nuance.connect.internal.ConnectHandler
        public String getHandlerName() {
            return APIHandlers.DLM_HANDLER;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public int[] getMessageIDs() {
            int[] iArr = new int[DLMConnectorInternal.MESSAGE_IDS.length];
            for (int i = 0; i < DLMConnectorInternal.MESSAGE_IDS.length; i++) {
                iArr[i] = DLMConnectorInternal.MESSAGE_IDS[i].ordinal();
            }
            return iArr;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void handleMessage(Handler handler, Message message) {
            switch (AnonymousClass8.$SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.fromInt(message.what).ordinal()]) {
                case 1:
                    DLMConnectorInternal.this.log.v("MESSAGE_HOST_PROCESS_DLM_EVENTS");
                    Bundle data = message.getData();
                    String string = data.getString(Strings.DLM_EVENT_FILE);
                    int i = data.getInt(Strings.DLM_EVENT_CORE);
                    int i2 = data.getInt(Strings.DLM_EVENT_ACK, InternalMessages.MESSAGE_CLIENT_PROCESS_DLM_EVENTS_ACK.ordinal());
                    int i3 = data.getInt(Strings.DLM_EVENT_COUNT);
                    DLMConnectorInternal.this.recordDLMRecordset(i, string, i2, data.getString(Strings.IDENTIFIER));
                    DLMConnectorInternal.this.processDLMCore(i);
                    if (i2 == InternalMessages.MESSAGE_CLIENT_PROCESS_DLM_EVENTS_ACK.ordinal()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(Strings.DLM_EVENT_COUNT, i3);
                        bundle.putInt(Strings.DLM_EVENT_CORE, i);
                        Message obtainMessage = handler.obtainMessage(InternalMessages.MESSAGE_DLM_RECEIVED_EVENTS.ordinal());
                        obtainMessage.setData(bundle);
                        handler.sendMessage(obtainMessage);
                        return;
                    }
                    return;
                case 2:
                    DLMConnectorInternal.this.log.v("MESSAGE_HOST_PROCESS_DLM_DELETE_CATEGORY");
                    Bundle data2 = message.getData();
                    int i4 = data2.getInt(Strings.DLM_DELETE_CATEGORY);
                    int i5 = data2.getInt(Strings.DLM_EVENT_CORE);
                    int i6 = data2.getInt(Strings.DLM_DELETE_LANGUAGE);
                    String string2 = data2.getString(Strings.IDENTIFIER);
                    DLMConnectorInternal.this.recordDLMDeleteCategory(i5, i6, i4, data2.getInt(Strings.DLM_EVENT_ACK, InternalMessages.MESSAGE_CLIENT_PROCESS_CATEGORY_DELETE_CATEGORY_ACK.ordinal()), string2);
                    DLMConnectorInternal.this.processDLMCore(i5);
                    return;
                default:
                    return;
            }
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void onPostUpgrade() {
        }
    };
    private final Handler dlmHandler = new Handler(Looper.getMainLooper(), this.dlmCallback);

    /* renamed from: com.nuance.connect.internal.DLMConnectorInternal$8, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass8 {
        static final /* synthetic */ int[] $SwitchMap$com$nuance$connect$internal$common$InternalMessages = new int[InternalMessages.values().length];

        static {
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_PROCESS_DLM_EVENTS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_PROCESS_DLM_DELETE_CATEGORY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BackupEventsFileQueue {
        private final StatusCallback callback;
        private volatile boolean complete;
        private final int coreId;
        private final File file;
        private final FileOutputStream fos;
        private Handler handler;
        private Looper looper;
        private final BufferedOutputStream output;
        private final LinkedBlockingQueue<String> eventsBuffer = new LinkedBlockingQueue<>();
        private final HandlerThread handlerThread = new HandlerThread("dlm_backup_task");
        private final AtomicInteger count = new AtomicInteger();
        private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, BackupEventsFileQueue.class.getSimpleName());
        Runnable processRunnable = new Runnable() { // from class: com.nuance.connect.internal.DLMConnectorInternal.BackupEventsFileQueue.1
            @Override // java.lang.Runnable
            public void run() {
                BackupEventsFileQueue.this.processEvents();
            }
        };

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public interface StatusCallback {
            void onComplete(String str, int i);
        }

        public BackupEventsFileQueue(File file, StatusCallback statusCallback, int i) throws FileNotFoundException {
            this.log.v("constructor ", file, " coreId: ", Integer.valueOf(i));
            this.file = file;
            this.fos = new FileOutputStream(file);
            this.output = new BufferedOutputStream(this.fos);
            this.callback = statusCallback;
            this.coreId = i;
            initHandler();
        }

        private void initHandler() {
            this.log.v("initHandler()");
            this.handlerThread.start();
            this.looper = this.handlerThread.getLooper();
            this.handler = new Handler(this.looper);
        }

        private void onFinished() {
            this.log.v("onFinished()");
            try {
                this.output.flush();
                this.output.close();
                this.fos.close();
                this.log.v("size: " + this.file.length());
            } catch (IOException e) {
            }
            this.callback.onComplete(this.file.getAbsolutePath(), this.count.get());
            destroyHandler();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void processEvents() {
            byte[] bArr;
            this.log.v("processEvents()");
            ArrayList arrayList = new ArrayList();
            this.eventsBuffer.drainTo(arrayList);
            byte[] bArr2 = new byte[0];
            try {
                bArr = "\n".getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                bArr = bArr2;
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                try {
                    this.output.write(((String) it.next()).getBytes("UTF-8"));
                    this.output.write(bArr);
                } catch (UnsupportedEncodingException e2) {
                    this.log.e((Object) "UnsupportedEncodingException ", (Throwable) e2);
                } catch (IOException e3) {
                    this.log.e((Object) "IOException ", (Throwable) e3);
                }
            }
            if (this.complete && this.eventsBuffer.size() == 0) {
                onFinished();
            }
        }

        public void addEvent(String str) {
            this.log.v("addEvent()");
            this.eventsBuffer.add(str);
            int size = this.eventsBuffer.size();
            this.count.incrementAndGet();
            if (size > DLMConnectorInternal.BATCH_SIZE) {
                this.handler.post(this.processRunnable);
            }
        }

        public void backupComplete() {
            this.log.v("backupComplete()");
            this.complete = true;
            this.handler.post(this.processRunnable);
        }

        public void destroyHandler() {
            this.log.v("destroyHandler()");
            this.handlerThread.quit();
            this.handler = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DlmEventBatchProcessRunnable implements Runnable {
        private static final int BATCH_SIZE = 50;
        private final int ackMessage;
        private final DLMConnector.EventNotificationCallback callback;
        private final int coreId;
        private final DLMConnector.DlmEventCallback dlmEventCallback;
        private final FileUtils.CountingIterator<String> eventIterator;
        private final String fileLocation;
        private final String identifier;
        private final DLMConnectorInternal parent;
        private boolean status = true;
        private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());

        DlmEventBatchProcessRunnable(String str, FileUtils.CountingIterator<String> countingIterator, String str2, int i, int i2, DLMConnector.EventNotificationCallback eventNotificationCallback, DLMConnector.DlmEventCallback dlmEventCallback, DLMConnectorInternal dLMConnectorInternal) {
            this.fileLocation = str;
            this.eventIterator = countingIterator;
            this.identifier = str2;
            this.coreId = i;
            this.ackMessage = i2;
            this.callback = eventNotificationCallback;
            this.dlmEventCallback = dlmEventCallback;
            this.parent = dLMConnectorInternal;
        }

        public boolean getStatus() {
            return this.status;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.log.d("run() core: ", Integer.valueOf(this.coreId), " identifier: ", this.identifier, " count: ", Integer.valueOf(this.eventIterator.getCount()));
            int i = 0;
            while (true) {
                int i2 = i + 1;
                if (i >= 50 || !this.eventIterator.hasNext()) {
                    break;
                }
                this.status = this.dlmEventCallback.processEvent(this.eventIterator.next()) ? this.status : false;
                i = i2;
            }
            if (this.eventIterator.hasNext()) {
                this.parent.processBatch(this);
                return;
            }
            if (this.callback != null) {
                this.log.d("run() complete: ", Integer.valueOf(this.eventIterator.getCount()));
                this.callback.onEventProcessComplete(0, this.eventIterator.getCount());
            }
            this.parent.finishBatch(this);
            this.parent.dlmEventProcessingQueue.deletePackage(this.fileLocation);
            this.parent.dlmEventProcessingQueue.saveMetadata();
            this.parent.deleteFile(this.fileLocation);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ParentScannerBucket implements DLMConnector.ScannerBucket {
        private final String baseFileName;
        private final Set<DLMConnector.ScannerBucket> childScanners = new HashSet();
        private final int coreId;
        private final String data;
        private File outputFile;
        private final String outputFolder;
        private OutputStream outputStream;
        private final boolean sentenceBased;
        private final int wordQuality;

        ParentScannerBucket(String str, int i, int i2, boolean z) {
            this.data = str;
            this.coreId = i;
            this.wordQuality = i2;
            this.sentenceBased = z;
            this.outputFolder = DLMConnectorInternal.this.connectService.getAppFilesFolder();
            this.baseFileName = "scanner-" + i + XMLResultsHandler.SEP_HYPHEN + i2 + XMLResultsHandler.SEP_HYPHEN + z;
        }

        private void createOutputFile() {
            try {
                this.outputFile = File.createTempFile(this.baseFileName, ".bucket", new File(this.outputFolder));
                this.outputStream = new BufferedOutputStream(new FileOutputStream(this.outputFile, true));
            } catch (IOException e) {
                DLMConnectorInternal.this.log.e("Could not create an output stream: ", e.getMessage());
                this.outputFile = null;
                this.outputStream = null;
            }
        }

        @Override // com.nuance.connect.api.DLMConnector.ScannerBucket
        public synchronized void close() {
            DLMConnectorInternal.this.log.d("ParentScannerBucket.close(), children count: ", Integer.valueOf(this.childScanners.size()));
            if (this.outputStream != null) {
                try {
                    this.outputStream.flush();
                    this.outputStream.close();
                } catch (IOException e) {
                    DLMConnectorInternal.this.log.e("Failed to close scan: ", e.getMessage());
                }
                DLMConnectorInternal.this.recordDLMScannerSet(this.data, this.coreId, this.outputFile.getAbsolutePath(), this.wordQuality, this.sentenceBased);
                DLMConnectorInternal.this.processDLMCore(this.coreId);
            }
            this.outputStream = null;
            this.outputFile = null;
        }

        void done(DLMConnector.ScannerBucket scannerBucket) {
            this.childScanners.remove(scannerBucket);
            if (this.childScanners.isEmpty()) {
                close();
            }
        }

        DLMConnector.ScannerBucket getChildBucket() {
            ScannerBucketWrapper scannerBucketWrapper = new ScannerBucketWrapper(this);
            this.childScanners.add(scannerBucketWrapper);
            return scannerBucketWrapper;
        }

        boolean isSame(String str, int i, int i2, boolean z) {
            return ((this.data == null && str == null) || !(this.data == null || str == null || !this.data.equals(str))) && this.coreId == i && this.wordQuality == i2 && this.sentenceBased == z;
        }

        @Override // com.nuance.connect.api.DLMConnector.ScannerBucket
        public synchronized void scan(String str) {
            DLMConnectorInternal.this.log.d("scan buffer=", String.valueOf(str));
            if (this.outputStream == null) {
                createOutputFile();
            }
            if (this.outputStream != null) {
                try {
                    this.outputStream.write(str.getBytes("UTF-8"));
                    this.outputStream.write("\n".getBytes("UTF-8"));
                } catch (IOException e) {
                    DLMConnectorInternal.this.log.e("Failed to log scan: ", e.getMessage());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ScannerBucketWrapper implements DLMConnector.ScannerBucket {
        private final ParentScannerBucket bucket;
        private boolean done = false;

        ScannerBucketWrapper(ParentScannerBucket parentScannerBucket) {
            this.bucket = parentScannerBucket;
        }

        @Override // com.nuance.connect.api.DLMConnector.ScannerBucket
        public void close() {
            this.done = true;
            this.bucket.done(this);
        }

        @Override // com.nuance.connect.api.DLMConnector.ScannerBucket
        public void scan(String str) {
            if (this.done) {
                return;
            }
            this.bucket.scan(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ScanningSetBatchInfo {
        final int categoryId;
        final DLMConnector.DlmEventCallback coreSpecificEventCallback;
        final FileUtils.CountingIterator<String> eventIterator;
        final String fileLocation;
        final boolean sentenceBased;
        final int wordQuality;

        private ScanningSetBatchInfo(FileUtils.CountingIterator<String> countingIterator, String str, int i, int i2, boolean z, DLMConnector.DlmEventCallback dlmEventCallback) {
            this.eventIterator = countingIterator;
            this.fileLocation = str;
            this.categoryId = i;
            this.wordQuality = i2;
            this.sentenceBased = z;
            this.coreSpecificEventCallback = dlmEventCallback;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DLMConnectorInternal(ConnectServiceManagerInternal connectServiceManagerInternal) {
        this.connectService = connectServiceManagerInternal;
        this.dataSource = new DlmEventsDataSource(connectServiceManagerInternal.getContext());
        this.dlmEventProcessingQueue = new InstallMetadata(connectServiceManagerInternal.getDataManager(), DLM_EVENT_QUEUE_PREF);
        for (String str : this.dlmEventProcessingQueue.allPackages()) {
            if (this.dlmEventProcessingQueue.getStep(str) == 1) {
                this.dlmEventProcessingQueue.setStep(str, 0);
            }
        }
        UserSettings userSettings = connectServiceManagerInternal.getUserSettings();
        userSettings.registerUserSettingsListener(UserSettings.USER_DLM_SYNC_ENABLED, this.booleanListener);
        this.syncEnabled = userSettings.isDlmSyncEnabled();
        userSettings.registerUserSettingsListener(UserSettings.USER_DLM_SYNC_ACCOUNT_ENABLED, this.booleanListener);
        this.accountEnabled = userSettings.isDlmSyncAccountEnabled();
        userSettings.registerUserSettingsListener(UserSettings.USER_ALLOW_DATA_COLLECTION, this.booleanListener);
        this.dataAccepted = userSettings.isDataCollectionAccepted();
        userSettings.registerUserSettingsListener("LOCATION_COUNTRY", this.stringListener);
        processLocationUpate(userSettings.getLocationCountry());
    }

    private boolean canProcess(int i) {
        this.log.d("canProcess(", Integer.valueOf(i), ") allDlmYielding: ", Boolean.valueOf(this.allDlmYielding), " dlmYielding.get(coreId, false) ", Boolean.valueOf(this.dlmYielding.get(i, false)), " canProcess: ", Boolean.valueOf((this.allDlmYielding || this.dlmYielding.get(i, false)) ? false : true));
        return (this.allDlmYielding || this.dlmYielding.get(i, false)) ? false : true;
    }

    private void createEvent(Bundle bundle, boolean z) {
        if (dlmSyncAllowed()) {
            String string = bundle.getString(Strings.DLM_EVENT_DATA);
            String string2 = bundle.getString(Strings.DLM_EVENT_APPNAME);
            int i = bundle.getInt(Strings.DLM_EVENT_CORE);
            long j = bundle.getLong(Strings.DLM_EVENT_TIMESTAMP);
            String string3 = bundle.getString(Strings.DLM_EVENT_LOCATION);
            String string4 = bundle.getString(Strings.DLM_EVENT_LOCALE);
            int i2 = bundle.getInt(Strings.DLM_EVENT_INPUT_TYPE);
            if (!Arrays.asList(SUPPORTED_CORES).contains(Integer.valueOf(i))) {
                this.log.e("core not supported: ", Integer.valueOf(i));
                return;
            }
            this.dataSource.insertEvent(string, i, j, string2, string3, string4, i2);
            if (z) {
                this.dataSource.insertHighPriorityEvent(string, i, j);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteFile(String str) {
        if (new File(str).delete()) {
            return;
        }
        this.log.e("dlm service unable to delete file.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean dlmSyncAllowed() {
        return this.syncEnabled && this.accountEnabled && this.dataAccepted;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishBatch(DlmEventBatchProcessRunnable dlmEventBatchProcessRunnable) {
        this.log.d("finishBatch()");
        if (dlmEventBatchProcessRunnable == this.eventsBatchRunnable) {
            this.eventsBatchRunnable = null;
            if (dlmEventBatchProcessRunnable.identifier != null) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(Strings.DEFAULT_KEY, dlmEventBatchProcessRunnable.getStatus());
                bundle.putInt(Strings.DLM_EVENT_CORE, dlmEventBatchProcessRunnable.coreId);
                bundle.putInt(Strings.DLM_EVENT_COUNT, dlmEventBatchProcessRunnable.eventIterator.getCount());
                bundle.putString(Strings.IDENTIFIER, dlmEventBatchProcessRunnable.identifier);
                this.connectService.getBinder().sendConnectMessage(InternalMessages.fromInt(dlmEventBatchProcessRunnable.ackMessage), bundle);
            }
        }
        for (int i = 0; i < this.eventCallbacks.size(); i++) {
            processDLMCore(this.eventCallbacks.keyAt(i));
        }
    }

    private File getBackupFile() throws IOException {
        File createTempFile = File.createTempFile("backup_", ".dlm", new File(this.connectService.getAppFilesFolder()));
        createTempFile.deleteOnExit();
        return createTempFile;
    }

    private boolean getBoolValueFromBucketData(String str, String str2) {
        if (str == null || str2 == null || str2.length() <= 0) {
            return false;
        }
        try {
            Matcher matcher = Pattern.compile(str2 + "\"(\\w*\\=*)\"").matcher(str);
            if (matcher.find()) {
                return Boolean.parseBoolean(matcher.group(1));
            }
            return false;
        } catch (IllegalStateException e) {
            this.log.e((Object) "getBoolValueFromBucketData IllegalStateException", (Throwable) e);
            return false;
        }
    }

    private int getIntValueFromBucketData(String str, String str2) {
        if (str == null || str2 == null || str2.length() <= 0) {
            return 0;
        }
        try {
            Matcher matcher = Pattern.compile(str2 + "\"(\\d+)\"").matcher(str);
            if (matcher.find()) {
                return Integer.decode(matcher.group(1)).intValue();
            }
            return 0;
        } catch (IllegalStateException e) {
            this.log.e((Object) "getIntValueFromBucketData IllegalStateException", (Throwable) e);
            return 0;
        } catch (NumberFormatException e2) {
            this.log.e((Object) "getIntValueFromBucketData NumberFormatException", (Throwable) e2);
            return 0;
        }
    }

    private String getStringValueFromBucketData(String str, String str2) {
        if (str != null && str2 != null && str2.length() > 0) {
            try {
                Matcher matcher = Pattern.compile(str2 + "\"(\\w*\\=*)\"").matcher(str);
                if (matcher.find()) {
                    return EncryptUtils.decodeString(matcher.group(1));
                }
                return null;
            } catch (IllegalStateException e) {
                this.log.e((Object) "getStringValueFromBucketData IllegalStateException", (Throwable) e);
            }
        }
        return "";
    }

    private void onDlmEvent(int i, String str, long j) {
        if (dlmSyncAllowed()) {
            if (str == null || str.length() == 0) {
                this.log.d("DLM event is empty or null.");
                return;
            }
            if (this.performingBackup) {
                this.backupEvents.addEvent(str);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString(Strings.DLM_EVENT_DATA, str);
            bundle.putLong(Strings.DLM_EVENT_TIMESTAMP, j);
            bundle.putInt(Strings.DLM_EVENT_CORE, i);
            bundle.putString(Strings.DLM_EVENT_APPNAME, this.currentAppName);
            bundle.putString(Strings.DLM_EVENT_LOCALE, this.currentLocale);
            bundle.putString(Strings.DLM_EVENT_LOCATION, this.currentLocation);
            bundle.putInt(Strings.DLM_EVENT_INPUT_TYPE, this.currentInputType);
            createEvent(bundle, false);
        }
    }

    private void onHighPriorityDlmEvent(int i, String str, long j) {
        if (dlmSyncAllowed()) {
            if (this.performingBackup) {
                this.backupEvents.addEvent(str);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString(Strings.DLM_EVENT_DATA, str);
            bundle.putString(Strings.DLM_EVENT_APPNAME, this.currentAppName);
            bundle.putLong(Strings.DLM_EVENT_TIMESTAMP, j);
            bundle.putInt(Strings.DLM_EVENT_CORE, i);
            createEvent(bundle, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processBatch(DlmEventBatchProcessRunnable dlmEventBatchProcessRunnable) {
        this.log.d("processBatch()");
        if (this.eventsBatchRunnable == null || this.eventsBatchRunnable == dlmEventBatchProcessRunnable) {
            this.eventsBatchRunnable = dlmEventBatchProcessRunnable;
            if (canProcess(dlmEventBatchProcessRunnable.coreId)) {
                this.dlmHandler.post(dlmEventBatchProcessRunnable);
            } else {
                this.dlmHandler.postDelayed(dlmEventBatchProcessRunnable, IME.NEXT_SCAN_IN_MILLIS);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processDlmDelete(int i, String str) {
        if (this.eventCallbacks.get(i) == null || !this.dlmEventProcessingQueue.hasPackage(str)) {
            return;
        }
        this.dlmEventProcessingQueue.setStep(str, 1);
        int intProp = this.dlmEventProcessingQueue.getIntProp(str, Strings.DLM_DELETE_CATEGORY);
        int intProp2 = this.dlmEventProcessingQueue.getIntProp(str, Strings.DLM_DELETE_LANGUAGE);
        String prop = this.dlmEventProcessingQueue.getProp(str, Strings.IDENTIFIER);
        int intProp3 = this.dlmEventProcessingQueue.getIntProp(str, Strings.DLM_EVENT_ACK);
        this.dlmEventProcessingQueue.deletePackage(str);
        this.dlmEventProcessingQueue.saveMetadata();
        if (intProp2 == Integer.MIN_VALUE) {
            this.eventCallbacks.get(i).processDlmDelete(intProp);
            return;
        }
        boolean processDlmDelete = this.eventCallbacks.get(i).processDlmDelete(intProp, intProp2);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Strings.DEFAULT_KEY, processDlmDelete);
        bundle.putString(Strings.IDENTIFIER, prop);
        bundle.putInt(Strings.DLM_DELETE_CATEGORY, intProp);
        bundle.putInt(Strings.DLM_EVENT_CORE, i);
        bundle.putInt(Strings.DLM_DELETE_LANGUAGE, intProp2);
        this.connectService.getBinder().sendConnectMessage(InternalMessages.fromInt(intProp3), bundle);
    }

    private void processDlmEvents(int i, String str, DLMConnector.EventNotificationCallback eventNotificationCallback) {
        this.log.d("DLMConnectorInternal.processDlmEvents(", Integer.valueOf(i), ", ", str, ")");
        if (this.eventsBatchRunnable != null) {
            this.log.d("Another batch of events is currently being processed.  Ignoring until it is complete.");
            return;
        }
        DLMConnector.DlmEventCallback dlmEventCallback = this.eventCallbacks.get(i);
        if (dlmEventCallback == null || !this.dlmEventProcessingQueue.hasPackage(str)) {
            return;
        }
        int intProp = this.dlmEventProcessingQueue.getIntProp(str, Strings.DLM_EVENT_CORE);
        String prop = this.dlmEventProcessingQueue.getProp(str, Strings.IDENTIFIER);
        int intProp2 = this.dlmEventProcessingQueue.getIntProp(str, Strings.DLM_EVENT_ACK);
        if (intProp != i) {
            this.log.d("DLMConnectorInternal.processDlmEvents processing events for core=", Integer.valueOf(i), " of file=", str, " but categories don't match");
            return;
        }
        this.dlmEventProcessingQueue.setStep(str, 1);
        FileUtils.CountingIterator<String> streamFile = FileUtils.streamFile(str, false);
        if (streamFile != null) {
            processBatch(new DlmEventBatchProcessRunnable(str, streamFile, prop, i, intProp2, eventNotificationCallback != null ? eventNotificationCallback : this.genericEventNotification, dlmEventCallback, this));
            return;
        }
        this.log.d("processing events for file=", str, " but no events so cleaning it out");
        this.dlmEventProcessingQueue.deletePackage(str);
        this.dlmEventProcessingQueue.saveMetadata();
        deleteFile(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processDlmScannerSet(int i, String str) {
        this.log.d("DLMConnectorInternal.processDlmScannerSet(", Integer.valueOf(i), ", ", str, ") callbacks=", Integer.valueOf(this.eventCallbacks.size()));
        if (this.processingScanningSet) {
            this.log.d("DLMConnectorInternal.processDlmScannerSet.  Ignoring until it is complete.");
            return;
        }
        DLMConnector.DlmEventCallback dlmEventCallback = this.eventCallbacks.get(i);
        if (dlmEventCallback == null || !this.dlmEventProcessingQueue.hasPackage(str)) {
            return;
        }
        if (this.dlmEventProcessingQueue.getIntProp(str, Strings.DLM_EVENT_CORE) != i) {
            this.log.d("DLMConnectorInternal.processDlmScannerSet processing events for core=", Integer.valueOf(i), " of file=", str, " but categories don't match");
            return;
        }
        this.dlmEventProcessingQueue.setStep(str, 1);
        String prop = this.dlmEventProcessingQueue.getProp(str, Strings.DLM_EVENT_DATA);
        final FileUtils.CountingIterator<String> streamFile = FileUtils.streamFile(str, false);
        FileUtils.CountingIterator<String> countingIterator = (streamFile == null || !getBoolValueFromBucketData(prop, BUCKET_DUPLICATES)) ? streamFile : new FileUtils.CountingIterator<String>() { // from class: com.nuance.connect.internal.DLMConnectorInternal.7
            final Set<String> items = new HashSet();
            String next = null;

            @Override // com.nuance.connect.util.FileUtils.CountingIterator
            public int getCount() {
                return this.items.size();
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                this.next = next();
                return this.next != null;
            }

            @Override // com.nuance.connect.util.FileUtils.CountingIterator, java.util.Iterator
            public String next() {
                String str2;
                if (this.next != null) {
                    String str3 = this.next;
                    this.next = null;
                    return str3;
                }
                if (!streamFile.hasNext()) {
                    return null;
                }
                do {
                    str2 = (String) streamFile.next();
                    if (!this.items.contains(str2)) {
                        break;
                    }
                } while (streamFile.hasNext());
                if (this.items.contains(str2)) {
                    return null;
                }
                this.items.add(str2);
                return str2;
            }

            @Override // java.util.Iterator
            public void remove() {
            }
        };
        if (countingIterator == null) {
            this.log.d("processing events for file=", str, " but no events so cleaning it out");
            this.dlmEventProcessingQueue.deletePackage(str);
            this.dlmEventProcessingQueue.saveMetadata();
            deleteFile(str);
            return;
        }
        this.processingScanningSet = true;
        dlmEventCallback.scanBegin(getStringValueFromBucketData(prop, BUCKET_CONTEXT1), getStringValueFromBucketData(prop, BUCKET_CONTEXT2), getStringValueFromBucketData(prop, BUCKET_CONTEXT3), getIntValueFromBucketData(prop, BUCKET_USER_ACTION), getIntValueFromBucketData(prop, BUCKET_SCAN_ACTION));
        ScanningSetBatchInfo scanningSetBatchInfo = new ScanningSetBatchInfo(countingIterator, str, getIntValueFromBucketData(prop, BUCKET_CATEGORY), this.dlmEventProcessingQueue.getIntProp(str, Strings.DLM_SCAN_WORD_QUALITY), this.dlmEventProcessingQueue.getBoolProp(str, Strings.DLM_SCAN_SENTENCE_BASED), dlmEventCallback);
        Message obtainMessage = this.dlmHandler.obtainMessage(4);
        obtainMessage.obj = scanningSetBatchInfo;
        this.dlmHandler.sendMessageDelayed(obtainMessage, IME.NEXT_SCAN_IN_MILLIS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processLocationUpate(String str) {
        String str2 = "";
        if (str != null) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("CC", str);
                str2 = jSONObject.toString();
            } catch (JSONException e) {
            } catch (Exception e2) {
            }
        }
        this.log.d("processLocationUpate() - ", str2);
        this.currentLocation = str2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processScanBatch(ScanningSetBatchInfo scanningSetBatchInfo) {
        if (scanningSetBatchInfo == null) {
            this.log.e("processScanBatch info is null");
            this.processingScanningSet = false;
            for (int i = 0; i < this.eventCallbacks.size(); i++) {
                processDLMCore(this.eventCallbacks.keyAt(i));
            }
            return;
        }
        int i2 = 0;
        while (scanningSetBatchInfo.eventIterator.hasNext()) {
            int i3 = i2 + 1;
            if (i2 >= 50) {
                break;
            }
            char[] charArray = scanningSetBatchInfo.eventIterator.next().toCharArray();
            if (scanningSetBatchInfo.categoryId == 0) {
                scanningSetBatchInfo.coreSpecificEventCallback.scan(charArray, charArray.length, scanningSetBatchInfo.wordQuality, scanningSetBatchInfo.sentenceBased);
                i2 = i3;
            } else {
                scanningSetBatchInfo.coreSpecificEventCallback.scanCategory(scanningSetBatchInfo.categoryId, charArray, charArray.length, scanningSetBatchInfo.wordQuality, scanningSetBatchInfo.sentenceBased);
                i2 = i3;
            }
        }
        if (scanningSetBatchInfo.eventIterator.hasNext()) {
            Message obtainMessage = this.dlmHandler.obtainMessage(4);
            obtainMessage.obj = scanningSetBatchInfo;
            this.dlmHandler.sendMessageDelayed(obtainMessage, IME.NEXT_SCAN_IN_MILLIS);
            return;
        }
        this.log.d("scan complete");
        scanningSetBatchInfo.coreSpecificEventCallback.scanEnd();
        this.dlmEventProcessingQueue.deletePackage(scanningSetBatchInfo.fileLocation);
        this.dlmEventProcessingQueue.saveMetadata();
        deleteFile(scanningSetBatchInfo.fileLocation);
        this.processingScanningSet = false;
        for (int i4 = 0; i4 < this.eventCallbacks.size(); i4++) {
            processDLMCore(this.eventCallbacks.keyAt(i4));
        }
    }

    private String serializeBucketContext(String str, String str2, String str3, int i, int i2, int i3, boolean z) {
        StringBuffer stringBuffer = new StringBuffer();
        if (str != null) {
            stringBuffer.append("context1\"").append(EncryptUtils.encodeString(str).trim()).append("\"");
        }
        if (str2 != null) {
            stringBuffer.append("context2\"").append(EncryptUtils.encodeString(str2).trim()).append("\"");
        }
        if (str3 != null) {
            stringBuffer.append("context3\"").append(EncryptUtils.encodeString(str3).trim()).append("\"");
        }
        stringBuffer.append("userAction\"").append(i).append("\"");
        stringBuffer.append("scanAction\"").append(i2).append("\"");
        stringBuffer.append("categoryId\"").append(i3).append("\"");
        stringBuffer.append("removeDuplicates\"").append(z).append("\"");
        return stringBuffer.toString();
    }

    private void updateDlmManagement(int i) {
        if (isSupportedCore(i)) {
            if (this.dlmInfoList.get(i).getSize() < 1048576) {
                ((LivingLanguageService) this.connectService.getFeatureService(ConnectFeature.LIVING_LANGUAGE)).setLivingLanguageAvailable(false);
            } else {
                ((LivingLanguageService) this.connectService.getFeatureService(ConnectFeature.LIVING_LANGUAGE)).setLivingLanguageAvailable(true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean backupDlm(int i) {
        if (this.eventCallbacks.get(i) == null) {
            return false;
        }
        onBeginBackup(i);
        boolean backupDlm = this.eventCallbacks.get(i).backupDlm();
        onEndBackup();
        return backupDlm;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectFeature[] getDependencies() {
        return ConnectFeature.DLM.values();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectHandler[] getHandlers() {
        return new ConnectHandler[]{this.handler};
    }

    @Override // com.nuance.connect.api.DLMConnector
    public DLMConnector.ScannerBucket getScannerBucket(String str, String str2, String str3, int i, int i2, int i3, int i4, boolean z, int i5, boolean z2) {
        String serializeBucketContext = serializeBucketContext(str, str2, str3, i, i2, i5, z2);
        for (ParentScannerBucket parentScannerBucket : this.buckets) {
            if (parentScannerBucket.isSame(serializeBucketContext, i3, i4, z)) {
                return parentScannerBucket.getChildBucket();
            }
        }
        ParentScannerBucket parentScannerBucket2 = new ParentScannerBucket(serializeBucketContext, i3, i4, z);
        this.buckets.add(parentScannerBucket2);
        return parentScannerBucket2.getChildBucket();
    }

    @Override // com.nuance.connect.api.DLMConnector
    public DLMConnector.ScannerBucket getScannerBucket(String str, String str2, String str3, int i, int i2, int[] iArr, int i3, boolean z, int i4, boolean z2) {
        DLMConnector.ScannerBucketSet scannerBucketSet = new DLMConnector.ScannerBucketSet();
        for (int i5 : iArr) {
            scannerBucketSet.addBucket(getScannerBucket(str, str2, str3, i, i2, i5, i3, z, i4, z2));
        }
        return scannerBucketSet;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public String getServiceName() {
        return ConnectFeature.DLM.name();
    }

    @Override // com.nuance.connect.api.DLMConnector
    public boolean isSupportedCore(int i) {
        return Arrays.asList(SUPPORTED_CORES).contains(Integer.valueOf(i));
    }

    @Override // com.nuance.connect.api.DLMConnector
    public void onBeginBackup(int i) {
        int i2 = 0;
        boolean z = true;
        try {
            this.backupEvents = new BackupEventsFileQueue(getBackupFile(), new BackupEventsFileQueue.StatusCallback() { // from class: com.nuance.connect.internal.DLMConnectorInternal.5
                @Override // com.nuance.connect.internal.DLMConnectorInternal.BackupEventsFileQueue.StatusCallback
                public void onComplete(String str, int i3) {
                    DLMConnectorInternal.this.log.d("Backup complete count: ", Integer.valueOf(i3));
                    Bundle bundle = new Bundle();
                    bundle.putString(Strings.DEFAULT_KEY, str);
                    bundle.putInt(Strings.DLM_EVENT_CORE, DLMConnectorInternal.this.backupCore);
                    DLMConnectorInternal.this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_DLM_BACKUP_EVENTS, bundle);
                    DLMConnectorInternal.this.connectService.getIncomingHandler().post(new Runnable() { // from class: com.nuance.connect.internal.DLMConnectorInternal.5.1
                        @Override // java.lang.Runnable
                        public void run() {
                            DLMConnectorInternal.this.log.v("(DLMConnectorInternal.run() cleanup backupEvents");
                            DLMConnectorInternal.this.performingBackup = false;
                            DLMConnectorInternal.this.backupEvents = null;
                            DLMConnectorInternal.this.backupCore = 0;
                        }
                    });
                }
            }, i);
            i2 = i;
        } catch (FileNotFoundException e) {
            this.log.e((Object) "Unable to backup, unable to create BackupEventsFileQueue.", (Throwable) e);
            z = false;
        } catch (IOException e2) {
            this.log.e((Object) "Unable to backup, unable to create BackupEventsFileQueue.", (Throwable) e2);
            z = false;
        }
        this.backupCore = i2;
        this.performingBackup = z;
    }

    @Override // com.nuance.connect.api.DLMConnector
    public void onDlmEvent(int i, String str, long j, boolean z) {
        this.log.d("onDlmEvent()");
        if (z) {
            onHighPriorityDlmEvent(i, str, j);
        } else {
            onDlmEvent(i, str, j);
        }
    }

    @Override // com.nuance.connect.api.DLMConnector
    public void onEndBackup() {
        this.log.d("DLMHandler.onEndBackup() backupCore=", Integer.valueOf(this.backupCore));
        if (this.backupEvents != null) {
            this.backupEvents.backupComplete();
        }
    }

    @Override // com.nuance.connect.api.DLMConnector
    public void onReset(int i, boolean z) {
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_CATEGORY_LIVING_LANGUAGE_REFRESH, Integer.valueOf(i));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void processDLMCore(int i) {
        this.log.d("processDLMCore(", Integer.valueOf(i), ") allDlmYielding: ", Boolean.valueOf(this.allDlmYielding), " dlmYielding: ", Boolean.valueOf(this.dlmYielding.get(i, false)));
        if (canProcess(i)) {
            List<String> listFromPropEquals = this.dlmEventProcessingQueue.listFromPropEquals(Strings.DLM_EVENT_CORE, i);
            this.log.d("pendingInstalls size: " + listFromPropEquals.size());
            Collections.sort(listFromPropEquals, this.timestampPackageComparator);
            for (String str : listFromPropEquals) {
                if (!this.dlmEventProcessingQueue.isInstalling(str)) {
                    Message obtainMessage = this.dlmHandler.obtainMessage(this.dlmEventProcessingQueue.getIntProp(str, DLM_TYPE));
                    obtainMessage.arg1 = i;
                    obtainMessage.obj = str;
                    this.dlmHandler.sendMessage(obtainMessage);
                    return;
                }
            }
        }
    }

    void processDlmEvents(int i, String str) {
        this.log.d("DLMConnectorInternal.processDlmEvents(", Integer.valueOf(i), ", ", str, ")");
        processDlmEvents(i, str, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean recordDLMDeleteCategory(int i) {
        this.log.d("DLMConnectorInternal.recordDLMDeleteCategory(", Integer.valueOf(i), ")");
        StringBuilder sb = new StringBuilder();
        sb.append("DEL|").append(i);
        this.dlmEventProcessingQueue.addPackage(sb.toString());
        this.dlmEventProcessingQueue.setProp(sb.toString(), DLM_TYPE, 2);
        this.dlmEventProcessingQueue.setProp(sb.toString(), Strings.DLM_EVENT_CORE, i);
        this.dlmEventProcessingQueue.setProp(sb.toString(), Strings.DLM_EVENT_TIMESTAMP, System.currentTimeMillis());
        this.dlmEventProcessingQueue.setStep(sb.toString(), 0);
        this.dlmEventProcessingQueue.saveMetadata();
        return true;
    }

    boolean recordDLMDeleteCategory(int i, int i2, int i3, int i4, String str) {
        this.log.d("DLMConnectorInternal.recordDLMDeleteCategory(", Integer.valueOf(i), ", ", Integer.valueOf(i2), ", ", Integer.valueOf(i3), ", ", Integer.valueOf(i4), ", ", str, ")");
        StringBuilder sb = new StringBuilder();
        sb.append("DEL|").append(i).append("|").append(i2).append("|").append(i3);
        this.dlmEventProcessingQueue.addPackage(sb.toString());
        this.dlmEventProcessingQueue.setProp(sb.toString(), DLM_TYPE, 2);
        this.dlmEventProcessingQueue.setProp(sb.toString(), Strings.DLM_DELETE_CATEGORY, i3);
        this.dlmEventProcessingQueue.setProp(sb.toString(), Strings.DLM_EVENT_CORE, i);
        this.dlmEventProcessingQueue.setProp(sb.toString(), Strings.DLM_DELETE_LANGUAGE, i2);
        this.dlmEventProcessingQueue.setProp(sb.toString(), Strings.IDENTIFIER, str);
        this.dlmEventProcessingQueue.setProp(sb.toString(), Strings.DLM_EVENT_ACK, i4);
        this.dlmEventProcessingQueue.setProp(sb.toString(), Strings.DLM_EVENT_TIMESTAMP, System.currentTimeMillis());
        this.dlmEventProcessingQueue.setStep(sb.toString(), 0);
        this.dlmEventProcessingQueue.saveMetadata();
        return true;
    }

    boolean recordDLMRecordset(int i, String str, int i2, String str2) {
        this.log.d("DLMConnectorInternal.recordDLMRecordset(", Integer.valueOf(i), ", ", str, ", ", Integer.valueOf(i2), ", ", str2, ")");
        if (str2 != null) {
            for (String str3 : this.dlmEventProcessingQueue.listFromPropEquals(Strings.IDENTIFIER, str2)) {
                this.log.d("DLMConnectorInternal.recordDLMRecordset--replacing staged events for: ", str2);
                this.dlmEventProcessingQueue.deletePackage(str3);
                deleteFile(str3);
            }
        }
        this.dlmEventProcessingQueue.addPackage(str);
        this.dlmEventProcessingQueue.setProp(str, DLM_TYPE, 1);
        this.dlmEventProcessingQueue.setProp(str, Strings.DLM_EVENT_CORE, i);
        this.dlmEventProcessingQueue.setProp(str, Strings.IDENTIFIER, str2);
        this.dlmEventProcessingQueue.setProp(str, Strings.DLM_EVENT_FILE, str);
        this.dlmEventProcessingQueue.setProp(str, Strings.DLM_EVENT_ACK, i2);
        this.dlmEventProcessingQueue.setProp(str, Strings.DLM_EVENT_TIMESTAMP, System.currentTimeMillis());
        this.dlmEventProcessingQueue.setStep(str, 0);
        this.dlmEventProcessingQueue.saveMetadata();
        return true;
    }

    boolean recordDLMScannerSet(String str, int i, String str2, int i2, boolean z) {
        this.log.d("DLMConnectorInternal.recordDLMScannerSet(", str, ", ", Integer.valueOf(i), ", ", str2, ", ", Integer.valueOf(i2), ", ", Boolean.valueOf(z), ")");
        try {
            this.dlmEventProcessingQueue.beginTransaction();
            this.dlmEventProcessingQueue.addPackage(str2);
            this.dlmEventProcessingQueue.setProp(str2, DLM_TYPE, 3);
            this.dlmEventProcessingQueue.setProp(str2, Strings.DLM_EVENT_DATA, str);
            this.dlmEventProcessingQueue.setProp(str2, Strings.DLM_EVENT_CORE, i);
            this.dlmEventProcessingQueue.setProp(str2, Strings.DLM_EVENT_FILE, str2);
            this.dlmEventProcessingQueue.setProp(str2, Strings.DLM_SCAN_WORD_QUALITY, i2);
            this.dlmEventProcessingQueue.setProp(str2, Strings.DLM_SCAN_SENTENCE_BASED, z);
            this.dlmEventProcessingQueue.setStep(str2, 0);
            this.dlmEventProcessingQueue.commitTransaction();
            return true;
        } catch (Throwable th) {
            this.dlmEventProcessingQueue.commitTransaction();
            throw th;
        }
    }

    @Override // com.nuance.connect.api.DLMConnector
    public void registerDlmCallback(int i, DLMConnector.DlmEventCallback dlmEventCallback) {
        this.eventCallbacks.put(i, dlmEventCallback);
        processDLMCore(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void restoreDlm(int i, String str, DLMConnector.EventNotificationCallback eventNotificationCallback) {
        this.dlmEventProcessingQueue.addPackage(str);
        this.dlmEventProcessingQueue.setProp(str, Strings.DLM_EVENT_CORE, i);
        this.dlmEventProcessingQueue.setProp(str, Strings.DLM_EVENT_TIMESTAMP, System.currentTimeMillis());
        this.dlmEventProcessingQueue.setProp(str, DLM_TYPE, 1);
        processDlmEvents(i, str, eventNotificationCallback);
    }

    @Override // com.nuance.connect.api.DLMConnector
    public void resume() {
        this.allDlmYielding = false;
        for (int i = 0; i < this.eventCallbacks.size(); i++) {
            processDLMCore(this.eventCallbacks.keyAt(i));
        }
    }

    @Override // com.nuance.connect.api.DLMConnector
    public void resume(int i) {
        this.dlmYielding.append(i, Boolean.FALSE.booleanValue());
        processDLMCore(i);
    }

    @Override // com.nuance.connect.api.DLMConnector
    public void setDLMInfo(DLMConnector.DLMInformation dLMInformation) {
        this.log.d("setDLMInfo(", Integer.valueOf(dLMInformation.getCoreId()), ",", Integer.valueOf(dLMInformation.getSize()), ",", Integer.valueOf(dLMInformation.getReserved()), ")");
        this.dlmInfoList.remove(dLMInformation.getCoreId());
        this.dlmInfoList.put(dLMInformation.getCoreId(), dLMInformation);
        updateDlmManagement(dLMInformation.getCoreId());
    }

    public void setEventNotificationCallback(DLMConnector.EventNotificationCallback eventNotificationCallback) {
        this.genericEventNotification = eventNotificationCallback;
    }

    @Override // com.nuance.connect.api.DLMConnector
    public void unregisterDlmCallback(int i) {
        this.eventCallbacks.remove(i);
    }

    public void update() {
        this.currentAppName = ((HostSystemData) this.connectService.getHostService(HostInterface.HostService.HOST_SYSTEM_DATA)).getCurrentApplicationName();
        this.currentInputType = ((HostSystemData) this.connectService.getHostService(HostInterface.HostService.HOST_SYSTEM_DATA)).getCurrentFieldInfo();
        this.currentLocale = Locale.getDefault().toString();
    }

    @Override // com.nuance.connect.api.DLMConnector
    public void yield() {
        this.log.d("yield()");
        this.allDlmYielding = true;
    }

    @Override // com.nuance.connect.api.DLMConnector
    public void yield(int i) {
        this.log.d("yield(", Integer.valueOf(i), ")");
        this.dlmYielding.append(i, Boolean.TRUE.booleanValue());
    }
}
