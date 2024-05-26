package com.nuance.connect.service.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import com.nuance.connect.comm.AbstractTransaction;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.comm.Response;
import com.nuance.connect.comm.Transaction;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.internal.common.ManagerService;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.service.manager.interfaces.LanguageListener;
import com.nuance.connect.service.manager.interfaces.MessageProcessor;
import com.nuance.connect.service.manager.interfaces.SubManager;
import com.nuance.connect.sqlite.CategoryDatabase;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.EncryptUtils;
import com.nuance.connect.util.FileUtils;
import com.nuance.connect.util.InstallMetadata;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import com.nuance.connect.util.VersionUtils;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class CategoryManager extends AbstractCommandManager {
    private static final String CATEGORY_ENABLED_PREF = "categoryEnabled";
    private static final String CATEGORY_INSTALLER_PREF = "CATEGORY_INSTALLER_DATA";
    private static final String CATEGORY_LAST_CORES_PREF;
    private static final String CATEGORY_LAST_LANGUAGES_PREF;
    private static final String CATEGORY_LAST_LOCALE_PREF;
    private static final String CATEGORY_LIST_LAST_FETCHED = "CategoryRecieved";
    static final int CATEGORY_LIST_TYPE_ALL = 0;
    public static final String COMMAND_ACK = "ack";
    public static final String COMMAND_FAMILY;
    public static final String COMMAND_GET = "get";
    public static final String COMMAND_LIST = "list";
    public static final String COMMAND_STATUS = "status";
    public static final String COMMAND_SUBSCRIBE = "subscribe";
    public static final String COMMAND_UNSUBSCRIBE = "unsubscribe";
    public static final int COMMAND_VERSION = 9;
    public static final String DOWNLOAD_LIST_STATE;
    public static final int KOREAN_KEYBOARD_ID = 18;
    public static final String MANAGER_NAME;
    private static final int[] MESSAGES_HANDLED;
    public static final String SUBMANAGER_DOWNLOAD_LIST_STATE;
    private static final int SUBSCRIBE_PRIORITY = 10;
    private static final String TABLE_PREFIX = "TABLE_TYPE_";
    private final Property.BooleanValueListener booleanListener;
    final CategoryDatabase categoryDatabase;

    @SuppressLint({"UseSparseArrays"})
    private final Map<Integer, CategoryListState> categoryListState;
    final Set<Integer> coresInUse;
    String currentCountry;
    int[] currentLanguageCodes;
    Locale currentLocale;
    private final LanguageListener languageListener;
    private final Logger.Log log;
    private Property.StringValueListener stringListener;

    @SuppressLint({"UseSparseArrays"})
    private final HashMap<Integer, SubManager> subManagerTypeLookup;
    private final HashMap<SubManagerDefinition, SubManager> submanagers;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CategoryListState {
        final int categoryCount;
        final ListUpdateState dirty;
        final AbstractCommandManager.DownloadState state;

        CategoryListState() {
            this.state = AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_UNKNOWN;
            this.dirty = ListUpdateState.CLEAN;
            this.categoryCount = 0;
        }

        CategoryListState(AbstractCommandManager.DownloadState downloadState, ListUpdateState listUpdateState, int i) {
            this.state = downloadState;
            this.dirty = listUpdateState;
            this.categoryCount = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class CategoryListTransaction extends AbstractTransaction {
        private String country;
        private final HashSet<Integer> interestingCategories = new HashSet<>();
        private final Locale locale;
        private volatile boolean rejected;
        private final String taskAcknowledgement;

        CategoryListTransaction(String str, List<Integer> list, Locale locale, String str2) {
            this.locale = locale == null ? CategoryManager.this.client.getCurrentLocale() : locale;
            this.country = str2;
            this.taskAcknowledgement = str;
            Iterator<Integer> it = list.iterator();
            while (it.hasNext()) {
                int intValue = it.next().intValue();
                if (intValue == 0) {
                    for (SubManager subManager : CategoryManager.this.submanagers.values()) {
                        if (subManager.isEnabled()) {
                            this.interestingCategories.addAll(subManager.getTypesSupported());
                        }
                    }
                } else {
                    this.interestingCategories.add(Integer.valueOf(intValue));
                }
            }
            Iterator<Integer> it2 = this.interestingCategories.iterator();
            while (it2.hasNext()) {
                Integer next = it2.next();
                CategoryListState categoryListState = (CategoryListState) CategoryManager.this.categoryListState.get(next);
                CategoryManager.this.categoryListState.put(next, new CategoryListState(categoryListState == null ? AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_UNKNOWN : categoryListState.state, ListUpdateState.UPDATING, categoryListState == null ? 0 : categoryListState.categoryCount));
            }
            getList(this.interestingCategories);
        }

        private void getList(Set<Integer> set) {
            HashMap<String, Object> hashMap = new HashMap<>();
            if (this.country == null) {
                this.country = "";
            }
            CategoryManager.this.log.d("getList", " locale:", this.locale.toString(), " country:", this.country);
            JSONArray jSONArray = new JSONArray();
            Iterator<Integer> it = set.iterator();
            while (it.hasNext()) {
                jSONArray.put(it.next().intValue());
            }
            hashMap.put(MessageAPI.CATEGORY_TYPES, jSONArray);
            hashMap.put(MessageAPI.LOCALE, this.locale.toString());
            hashMap.put(MessageAPI.COUNTRY, this.country);
            Command createCommand = CategoryManager.this.createCommand("list", Command.REQUEST_TYPE.CRITICAL, hashMap);
            createCommand.allowDuplicateOfCommand = false;
            createCommand.responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.CategoryManager.CategoryListTransaction.1
                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    CategoryListTransaction.this.listResponse(response);
                }
            };
            this.currentCommand = createCommand;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void listResponse(Response response) {
            if (1 == response.status) {
                CategoryManager.this.processListResponse(response, this.interestingCategories);
                CategoryManager.this.client.taskCompletedAcknowledgement(this.taskAcknowledgement);
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
            return "CategoryListCommand";
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
            CategoryManager.this.finishTransaction(getName());
            if (this.rejected) {
                return;
            }
            CategoryManager.this.checkRefreshCategoryLists();
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onTransactionRejected(int i) {
            this.rejected = true;
            super.onTransactionRejected(i);
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class CategoryStatusTransaction extends AbstractTransaction {
        CategoryStatusTransaction() {
            getStatus();
        }

        private void getStatus() {
            Command createCommand = CategoryManager.this.createCommand("status", Command.REQUEST_TYPE.CRITICAL, new HashMap<>());
            createCommand.allowDuplicateOfCommand = false;
            createCommand.responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.CategoryManager.CategoryStatusTransaction.1
                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    CategoryStatusTransaction.this.statusResponse(response);
                }
            };
            this.currentCommand = createCommand;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void statusResponse(Response response) {
            if (1 == response.status) {
                if (response.parameters.containsKey(MessageAPI.CATEGORY_LIST)) {
                    CategoryManager.this.log.d("status available for category.");
                    JSONArray jSONArray = (JSONArray) response.parameters.get(MessageAPI.CATEGORY_LIST);
                    if (jSONArray != null) {
                        HashSet<SubManager> hashSet = new HashSet();
                        if (jSONArray.length() > 0) {
                            for (int i = 0; i < jSONArray.length(); i++) {
                                try {
                                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                                    String string = jSONObject.getString(MessageAPI.ID);
                                    String string2 = jSONObject.getString(MessageAPI.URL);
                                    String string3 = jSONObject.getString(MessageAPI.CHECKSUM);
                                    if (CategoryManager.this.categoryDatabase.hasCategory(string)) {
                                        CategoryManager.this.categoryDatabase.setProp(string, CategoryDatabase.LAST_UPDATE_AVAILABLE, System.currentTimeMillis());
                                        CategoryManager.this.categoryDatabase.setProp(string, "URL", string2);
                                        CategoryManager.this.categoryDatabase.setProp(string, "CHECKSUM", string3);
                                        hashSet.add(CategoryManager.this.subManagerTypeLookup.get(Integer.valueOf(CategoryManager.this.getTypeForID(string))));
                                    } else {
                                        CategoryManager.this.log.e("Category is not available for status update (", string, ")");
                                    }
                                } catch (JSONException e) {
                                    CategoryManager.this.log.d("Could not parse JSON response: ", e.getMessage());
                                }
                            }
                        }
                        for (SubManager subManager : hashSet) {
                            if (subManager != null) {
                                subManager.onDataUpdated();
                            }
                        }
                    }
                }
                ((CategorySubmanagerChineseAddon) CategoryManager.this.getSubManager(SubManagerDefinition.SUBMANAGER_CHINESE_DATABASES)).sendChineseAddonDictionaries();
                CategoryManager.this.client.taskCompletedAcknowledgement(Strings.TASK_CDB_AVAILABLE);
            }
            CategoryManager.this.processNextCategory();
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
            return "CategoryStatusTransaction";
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
            CategoryManager.this.finishTransaction(getName());
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static abstract class CategorySubscribeDownloadTransaction extends AbstractTransaction {
        public static final int DOWNLOAD_FAILED_HTTP = 0;
        public static final int FAILURE_DISK_FULL = 6;
        public static final int FAILURE_UNKNOWN = 7;
        private volatile boolean canceled;
        protected final String catDb;
        private final CategoryManager categoryManager;
        private String downloadFilePath;
        private volatile String failureMessage;
        private volatile boolean rolledBack;
        private String sentChecksum;
        private final int type;
        private String url;
        private final HashMap<String, String> props = new HashMap<>();
        private volatile boolean downloadAfterSubscribe = true;
        private volatile int failureType = -1;
        private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, "CategorySubscribeDownloadTransaction");

        /* JADX INFO: Access modifiers changed from: package-private */
        public CategorySubscribeDownloadTransaction(String str, CategoryManager categoryManager) {
            this.catDb = str;
            this.categoryManager = categoryManager;
            this.props.putAll(categoryManager.categoryDatabase.getProps(str));
            if (this.props.get(CategoryDatabase.UNSUBSCRIBE_PENDING) != null) {
                categoryManager.categoryDatabase.removeProp(this.catDb, CategoryDatabase.UNSUBSCRIBE_PENDING);
                this.props.remove(CategoryDatabase.UNSUBSCRIBE_PENDING);
            }
            this.type = categoryManager.getTypeForID(str);
            this.downloadFilePath = this.props.get(Strings.MAP_KEY_FILE_LOCATION);
            this.log.d("CategorySubscribeDownloadTransaction ", getName());
            subscribe();
        }

        private void ack() {
            this.log.d("ack ", getName());
            if (this.canceled) {
                rollback();
                return;
            }
            Command createCommand = this.categoryManager.createCommand("ack", getRequestType());
            createCommand.parameters.put(MessageAPI.ID, this.catDb);
            createCommand.handleIOExceptionInConnector = !getRequestType().equals(Command.REQUEST_TYPE.USER);
            createCommand.responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction.3
                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onIOExceptionResponse(Command command) {
                    CategorySubscribeDownloadTransaction.this.log.d("onIOExceptionResponse()");
                    CategorySubscribeDownloadTransaction.this.failureType = 0;
                    CategorySubscribeDownloadTransaction.this.failureMessage = "Unable to complete download due to IOException.";
                    CategorySubscribeDownloadTransaction.this.rollback();
                    super.onIOExceptionResponse(command);
                }

                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    CategorySubscribeDownloadTransaction.this.ackResponse(response);
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public boolean onRetry(Command command, int i, int i2, String str) {
                    boolean onRetry = super.onRetry(command, i, i2, str);
                    if (!CategorySubscribeDownloadTransaction.this.getRequestType().equals(Command.REQUEST_TYPE.USER)) {
                        return onRetry;
                    }
                    CategorySubscribeDownloadTransaction.this.failureType = 0;
                    CategorySubscribeDownloadTransaction.this.failureMessage = "Unable to complete download. Fast failing user requested transaction.";
                    CategorySubscribeDownloadTransaction.this.rollback();
                    return false;
                }
            };
            this.currentCommand = createCommand;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void ackResponse(Response response) {
            if (response.status == 1) {
                this.props.put(Strings.MAP_KEY_STEP, Integer.toString(5));
                this.currentCommand = null;
            } else {
                this.failureType = 0;
                this.failureMessage = "Server returned an unexpected response.";
                rollback();
            }
        }

        private void deleteDownloadFile() {
            File file = new File(this.downloadFilePath);
            if (!file.delete() || file.exists()) {
                this.log.w("Error removing temporary file: ", this.downloadFilePath, " exists: ", Boolean.valueOf(file.exists()));
            }
            this.downloadFilePath = null;
            this.categoryManager.categoryDatabase.removeProp(this.catDb, Strings.MAP_KEY_FILE_LOCATION);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void downloadResponse(Response response) {
            this.log.d("downloadResponse ", getName());
            if (this.canceled) {
                this.log.d("downloadResponse canceled");
                rollback();
                return;
            }
            String md5 = EncryptUtils.md5(response.file);
            this.log.d("downloadResponse ", getName(), ": checksum verification: [", md5, "] to [", this.sentChecksum, "]");
            this.log.d("downloadResponse ", getName(), ": file ", response.file.getAbsolutePath());
            if (this.sentChecksum.equals(md5)) {
                this.log.d("downloadResponse ", getName(), ": checksum passed");
                this.props.put(Strings.MAP_KEY_STEP, Integer.toString(4));
                this.props.put(Strings.MAP_KEY_FILE_LOCATION, response.file.getAbsolutePath());
                this.props.put(CategoryDatabase.LAST_UPDATE_FETCHED, this.props.get(CategoryDatabase.LAST_UPDATE_AVAILABLE));
                ack();
                return;
            }
            this.log.d("downloadResponse ", getName(), ": checksum failed");
            this.failureType = 0;
            this.failureMessage = "Checksum does not match expected checksum";
            deleteDownloadFile();
            rollback();
        }

        public static String getTransactionName(String str, int i) {
            return "CategorySubscribeDownloadTransaction-" + i + XMLResultsHandler.SEP_HYPHEN + str;
        }

        private void subscribe() {
            this.log.d("starting ", getName());
            onTransactionStarted();
            JSONArray jSONArray = new JSONArray();
            jSONArray.put(this.catDb);
            Command createCommand = this.categoryManager.createCommand(CategoryManager.COMMAND_SUBSCRIBE, getRequestType());
            createCommand.parameters.put(MessageAPI.CATEGORY_LIST, jSONArray);
            createCommand.responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction.1
                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    CategorySubscribeDownloadTransaction.this.subscribeResponse(response);
                }
            };
            this.currentCommand = createCommand;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void subscribeResponse(Response response) {
            this.log.d("processSubscribeResponse(): ", Integer.valueOf(response.status));
            if (1 == response.status && response.parameters.get(MessageAPI.CATEGORY_LIST) != null && (response.parameters.get(MessageAPI.CATEGORY_LIST) instanceof JSONArray)) {
                JSONArray jSONArray = (JSONArray) response.parameters.get(MessageAPI.CATEGORY_LIST);
                this.log.d("completed subscribe for: ", getName(), ". Now downloading all required");
                try {
                    JSONObject jSONObject = jSONArray.getJSONObject(0);
                    if (!this.catDb.equals(jSONObject.getString(MessageAPI.ID))) {
                        throw new Exception("Category ID does not match!");
                    }
                    this.url = jSONObject.getString(MessageAPI.URL);
                    this.sentChecksum = jSONObject.getString(MessageAPI.CHECKSUM);
                    this.props.put("URL", this.url);
                    this.props.put("CHECKSUM", this.sentChecksum);
                    this.props.put(CategoryDatabase.SUBSCRIBED, Boolean.toString(true));
                    this.categoryManager.categoryDatabase.setProp(this.catDb, CategoryDatabase.SUBSCRIBED, true);
                } catch (JSONException e) {
                    this.log.e("JSON parse exception attempting to get category: ", e.getMessage());
                    this.failureType = 0;
                    this.failureMessage = e.getMessage();
                    rollback();
                } catch (Exception e2) {
                    this.log.e("Exception attempting to get a category: ", e2.getMessage());
                    this.failureType = 7;
                    this.failureMessage = e2.getMessage();
                    rollback();
                }
            }
            if (1 != response.status || this.canceled) {
                if (this.canceled) {
                    rollback();
                    return;
                }
                this.failureType = 0;
                this.failureMessage = "Server returned an unexpected response.";
                rollback();
                return;
            }
            if (this.downloadAfterSubscribe) {
                download();
                return;
            }
            this.props.put(Strings.MAP_KEY_STEP, Integer.toString(7));
            this.props.put(CategoryDatabase.LAST_UPDATE_FETCHED, this.props.get(CategoryDatabase.LAST_UPDATE_AVAILABLE));
            this.currentCommand = null;
        }

        private boolean unsubscribeOnCancel() {
            return this.downloadAfterSubscribe;
        }

        @Override // com.nuance.connect.comm.Transaction
        public void cancel() {
            this.log.d("canceling: ", getName());
            this.canceled = true;
            if (this.currentCommand == null || this.currentCommand.command.equals(CategoryManager.COMMAND_UNSUBSCRIBE)) {
                return;
            }
            this.currentCommand.canceled = true;
        }

        @Override // com.nuance.connect.comm.Transaction
        public String createDownloadFile(Context context) {
            if (this.downloadFilePath == null) {
                try {
                    this.downloadFilePath = File.createTempFile("temp", ".download", this.categoryManager.client.getCacheDir()).getAbsolutePath();
                    this.props.put(Strings.MAP_KEY_FILE_LOCATION, this.downloadFilePath);
                    this.categoryManager.categoryDatabase.setProp(this.catDb, Strings.MAP_KEY_FILE_LOCATION, this.downloadFilePath);
                    this.props.put(Strings.MAP_KEY_FILE_LOCATION, this.downloadFilePath);
                } catch (IOException e) {
                    this.downloadFilePath = null;
                }
            }
            return this.downloadFilePath;
        }

        void download() {
            if (this.canceled) {
                rollback();
                return;
            }
            this.props.put(Strings.MAP_KEY_STEP, Integer.toString(2));
            if (!Boolean.parseBoolean(this.props.get(CategoryDatabase.SUBSCRIBED)) && this.url != null && this.sentChecksum != null) {
                this.failureType = 6;
                this.failureMessage = "Unable to complete download due to disk full error.";
                this.log.e("Not Subscribed or missing data");
                rollback();
                return;
            }
            this.props.put(Strings.MAP_KEY_STEP, Integer.toString(3));
            Command createCommand = this.categoryManager.createCommand("download", getRequestType());
            createCommand.thirdPartyURL = this.url;
            createCommand.method = "GET";
            createCommand.hasBody = false;
            createCommand.handleIOExceptionInConnector = !getRequestType().equals(Command.REQUEST_TYPE.USER);
            createCommand.notifyDownloadStatus = true;
            createCommand.allowDuplicateOfCommand = true;
            createCommand.responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction.2
                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onDownloadStatusResponse(Command command, int i, int i2) {
                    if (i < 0) {
                        CategorySubscribeDownloadTransaction.this.failureType = 6;
                        CategorySubscribeDownloadTransaction.this.failureMessage = "Unable to complete download due to disk full error.";
                        CategorySubscribeDownloadTransaction.this.rollback();
                    } else if (CategorySubscribeDownloadTransaction.this.categoryManager.validCommands.isCommandFor("download", command)) {
                        CategorySubscribeDownloadTransaction.this.onDownloadStatus((int) (100.0f * (i / i2)));
                    }
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onFileResponse(Response response) {
                    CategorySubscribeDownloadTransaction.this.downloadResponse(response);
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onIOExceptionResponse(Command command) {
                    CategorySubscribeDownloadTransaction.this.failureType = 0;
                    CategorySubscribeDownloadTransaction.this.failureMessage = "Unable to complete download due to IOException.";
                    CategorySubscribeDownloadTransaction.this.rollback();
                    super.onIOExceptionResponse(command);
                }

                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    CategorySubscribeDownloadTransaction.this.log.e("Error");
                    throw new UnsupportedOperationException("Shouldn't get here!");
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public boolean onRetry(Command command, int i, int i2, String str) {
                    boolean onRetry = super.onRetry(command, i, i2, str);
                    if (!CategorySubscribeDownloadTransaction.this.getRequestType().equals(Command.REQUEST_TYPE.USER)) {
                        return onRetry;
                    }
                    CategorySubscribeDownloadTransaction.this.failureType = 0;
                    CategorySubscribeDownloadTransaction.this.failureMessage = "Unable to complete download. Fast failing user requested transaction.";
                    CategorySubscribeDownloadTransaction.this.rollback();
                    return false;
                }
            };
            this.currentCommand = createCommand;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public String getDownloadFile() {
            return this.downloadFilePath;
        }

        @Override // com.nuance.connect.comm.Transaction
        public String getName() {
            return getTransactionName(this.catDb, getType());
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command getNextCommand() {
            if (this.currentCommand != null && this.currentCommand.canceled) {
                this.currentCommand = null;
            }
            return this.currentCommand;
        }

        @Override // com.nuance.connect.comm.Transaction
        public int getPriority() {
            return Command.REQUEST_TYPE.BACKGROUND == getRequestType() ? 0 : 10;
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command.REQUEST_TYPE getRequestType() {
            return Command.REQUEST_TYPE.CRITICAL;
        }

        int getType() {
            return this.type;
        }

        abstract void onCancelAck();

        abstract void onDownloadStatus(int i);

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onEndProcessing() {
            this.log.d("onEndProcessing: ", getName());
            this.categoryManager.finishTransaction(getName());
            if (!this.canceled && !this.rolledBack) {
                this.categoryManager.categoryDatabase.setProps(this.catDb, this.props);
                if (this.downloadAfterSubscribe) {
                    onSuccess(this.downloadFilePath);
                }
            } else if (this.canceled) {
                onCancelAck();
                if (unsubscribeOnCancel() && Boolean.parseBoolean(this.props.get(CategoryDatabase.SUBSCRIBED))) {
                    this.categoryManager.categoryDatabase.setProp(this.catDb, CategoryDatabase.UNSUBSCRIBE_PENDING, true);
                }
            } else if (this.rolledBack) {
                onFailedTransaction(this.failureType, this.failureMessage);
            }
            this.categoryManager.client.postMessage(InternalMessages.MESSAGE_COMMAND_CDB_PROCESS_CATEGORIES);
        }

        abstract void onFailedTransaction(int i, String str);

        abstract void onSuccess(String str);

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean onTransactionOfflineQueued() {
            return Command.REQUEST_TYPE.USER != getRequestType();
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onTransactionRejected(int i) {
            this.failureType = i;
            this.failureMessage = "Unable to complete download. Fast failing user requested transaction.";
            rollback();
            super.onTransactionRejected(i);
        }

        abstract void onTransactionStarted();

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
            if (this.canceled && this.downloadFilePath != null && this.downloadFilePath.length() > 0) {
                deleteDownloadFile();
            }
            if (this.canceled && unsubscribeOnCancel() && Boolean.parseBoolean(this.props.get(CategoryDatabase.SUBSCRIBED))) {
                unsubscribe();
            } else {
                this.currentCommand = null;
                this.rolledBack = true;
            }
        }

        void setDownloadAfterSubscribe(boolean z) {
            this.downloadAfterSubscribe = z;
        }

        protected void unsubscribe() {
            this.log.d("unsubscribe()");
            Command createCommand = this.categoryManager.createCommand(CategoryManager.COMMAND_UNSUBSCRIBE, Command.REQUEST_TYPE.CRITICAL);
            createCommand.parameters.put(MessageAPI.ID, this.catDb);
            JSONArray jSONArray = new JSONArray();
            jSONArray.put(this.catDb);
            createCommand.parameters.put(MessageAPI.CATEGORY_LIST, jSONArray);
            createCommand.identifier = this.catDb;
            createCommand.allowDuplicateOfCommand = false;
            createCommand.responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction.4
                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    CategorySubscribeDownloadTransaction.this.unsubscribeResponse(response);
                }
            };
            this.currentCommand = createCommand;
        }

        protected void unsubscribeResponse(Response response) {
            this.log.d("unsubscribeResponse()");
            if (1 == response.status) {
                this.props.put(CategoryDatabase.SUBSCRIBED, Boolean.toString(false));
                this.props.put(CategoryDatabase.LAST_UPDATE_FETCHED, Integer.toString(0));
                this.props.put(Strings.MAP_KEY_STEP, Integer.toString(0));
                this.props.remove(CategoryDatabase.UNSUBSCRIBE_PENDING);
                this.categoryManager.categoryDatabase.setProps(this.catDb, this.props);
                this.categoryManager.categoryDatabase.removeProp(this.catDb, CategoryDatabase.UNSUBSCRIBE_PENDING);
            }
            this.currentCommand = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum ListUpdateState {
        CLEAN,
        NEEDS_UPDATE,
        UPDATING
    }

    /* loaded from: classes.dex */
    public enum SubManagerDefinition {
        SUBMANAGER_LIVING_LANGUAGE(false, new int[]{1, 3}),
        SUBMANAGER_CHINESE_DATABASES(false, new int[]{2}),
        SUBMANAGER_UPDATES(false, new int[]{5}),
        SUBMANAGER_CATALOG(false, new int[]{6}),
        SUBMANAGER_CUSTOM_CONFIG(true, new int[]{7});

        static final HashMap<String, SubManagerDefinition> map = new HashMap<>();
        static SubManagerDefinition[] values;
        final boolean enabledByDefault;
        final int[] types;

        static {
            for (SubManagerDefinition subManagerDefinition : values()) {
                map.put(subManagerDefinition.name(), subManagerDefinition);
            }
            values = values();
        }

        SubManagerDefinition(boolean z, int[] iArr) {
            this.enabledByDefault = z;
            this.types = iArr;
        }

        static SubManagerDefinition from(int i) {
            for (SubManagerDefinition subManagerDefinition : values) {
                for (int i2 : subManagerDefinition.types) {
                    if (i2 == i) {
                        return subManagerDefinition;
                    }
                }
            }
            return null;
        }

        static SubManagerDefinition from(String str) {
            return map.get(str);
        }

        final boolean getEnabledByDefault() {
            return this.enabledByDefault;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class UnsubscribeTransaction extends AbstractTransaction {
        final String id;

        /* JADX INFO: Access modifiers changed from: package-private */
        public UnsubscribeTransaction(String str) {
            this.id = str;
            unsubscribe();
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
            return CategoryManager.this.getUnsubscribeTransactionName(this.id);
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
            CategoryManager.this.finishTransaction(getName());
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
            this.currentCommand = null;
        }

        protected void unsubscribe() {
            Command createCommand = CategoryManager.this.createCommand(CategoryManager.COMMAND_UNSUBSCRIBE, Command.REQUEST_TYPE.CRITICAL);
            createCommand.parameters.put(MessageAPI.ID, this.id);
            JSONArray jSONArray = new JSONArray();
            jSONArray.put(this.id);
            createCommand.parameters.put(MessageAPI.CATEGORY_LIST, jSONArray);
            createCommand.identifier = this.id;
            createCommand.allowDuplicateOfCommand = false;
            createCommand.responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.CategoryManager.UnsubscribeTransaction.1
                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    UnsubscribeTransaction.this.unsubscribeResponse(response);
                }
            };
            this.currentCommand = createCommand;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void unsubscribeResponse(Response response) {
            CategoryManager.this.log.d("unsubscribeResponse()");
            if (1 == response.status) {
                String str = response.identifier;
                if (CategoryManager.this.categoryDatabase.getBoolProp(str, CategoryDatabase.UNSUBSCRIBE_PENDING)) {
                    CategoryManager.this.categoryDatabase.setProp(str, CategoryDatabase.SUBSCRIBED, false);
                    CategoryManager.this.categoryDatabase.setProp(str, CategoryDatabase.LAST_UPDATE_FETCHED, 0);
                    CategoryManager.this.categoryDatabase.setStep(str, 0);
                    CategoryManager.this.categoryDatabase.removeProp(str, CategoryDatabase.UNSUBSCRIBE_PENDING);
                }
            }
            this.currentCommand = null;
        }
    }

    static {
        String name = ManagerService.CATEGORY.getName();
        COMMAND_FAMILY = name;
        MANAGER_NAME = name;
        CATEGORY_LAST_LOCALE_PREF = MANAGER_NAME + "_LAST_LOCALE";
        CATEGORY_LAST_LANGUAGES_PREF = MANAGER_NAME + "_LAST_LANGUAGES";
        CATEGORY_LAST_CORES_PREF = MANAGER_NAME + "_LAST_CORES";
        DOWNLOAD_LIST_STATE = MANAGER_NAME + "_DOWNLOAD_LIST_STATE";
        SUBMANAGER_DOWNLOAD_LIST_STATE = MANAGER_NAME + "_SUBMANAGER_DOWNLOAD_LIST_STATE";
        MESSAGES_HANDLED = new int[]{InternalMessages.MESSAGE_COMMAND_CDB_LIST_UPDATE.ordinal(), InternalMessages.MESSAGE_COMMAND_CDB_AVAILABLE.ordinal(), InternalMessages.MESSAGE_COMMAND_CDB_PROCESS_CATEGORIES.ordinal(), InternalMessages.MESSAGE_CLIENT_SET_CATEGORY_HOTWORD_STATUS.ordinal(), InternalMessages.MESSAGE_CLIENT_CATEGORY_DB_SUBSCRIBE.ordinal(), InternalMessages.MESSAGE_CLIENT_CATEGORY_DB_UNSUBSCRIBE.ordinal(), InternalMessages.MESSAGE_CLIENT_CATEGORY_DB_CANCEL.ordinal(), InternalMessages.MESSAGE_CLIENT_SET_CHINESE_CAT_DB_STATUS.ordinal(), InternalMessages.MESSAGE_CLIENT_CATEGORY_DB_SUBSCRIBE_OR_DOWNLOAD.ordinal(), InternalMessages.MESSAGE_CLIENT_CATEGORY_DB_SUBSCRIBE_NO_DOWNLOAD.ordinal(), InternalMessages.MESSAGE_CLIENT_CATEGORY_DB_SUBSCRIBE_OR_DOWNLOAD_LIST.ordinal(), InternalMessages.MESSAGE_CLIENT_CATEGORY_DB_SUBSCRIBE_NO_DOWNLOAD_LIST.ordinal(), InternalMessages.MESSAGE_CLIENT_CATEGORY_INSTALL_COMPLETE.ordinal(), InternalMessages.MESSAGE_CLIENT_CATALOG_CANCEL.ordinal(), InternalMessages.MESSAGE_CLIENT_SET_CATALOG_STATUS.ordinal()};
    }

    public CategoryManager(ConnectClient connectClient) {
        super(connectClient);
        this.booleanListener = new Property.BooleanValueListener() { // from class: com.nuance.connect.service.manager.CategoryManager.1
            @Override // com.nuance.connect.internal.Property.ValueListener
            public void onValueChanged(Property<Boolean> property) {
                if (property.getKey().equals(ConnectConfiguration.ConfigProperty.PLATFORM_UPDATE_ENABLED.name())) {
                    CategoryManager.this.log.d("PLATFORM_UPDATE_ENABLED ", property.getValue());
                    CategoryManager.this.updateSubManagerStatus(SubManagerDefinition.SUBMANAGER_UPDATES, property.getValue().booleanValue());
                }
            }
        };
        this.stringListener = new Property.StringValueListener() { // from class: com.nuance.connect.service.manager.CategoryManager.2
            @Override // com.nuance.connect.internal.Property.ValueListener
            public void onValueChanged(Property<String> property) {
                if (property.getKey().equals(ConnectConfiguration.ConfigProperty.LOCATION_GEO_IP_COUNTRY.name())) {
                    CategoryManager.this.log.d("LOCATION_GEO_IP_COUNTRY ", property.getValue());
                    CategoryManager.this.currentCountry = property.getValue();
                }
            }
        };
        this.coresInUse = new HashSet();
        this.log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
        this.languageListener = new LanguageListener() { // from class: com.nuance.connect.service.manager.CategoryManager.3
            @Override // com.nuance.connect.service.manager.interfaces.LanguageListener
            public void onLanguageUpdate(int[] iArr) {
                boolean z = false;
                CategoryManager.this.log.d("CategoryManager.onLanguageUpdate(", Arrays.toString(iArr), ")");
                HashSet hashSet = new HashSet(CategoryManager.this.coresInUse);
                CategoryManager.this.coresInUse.clear();
                if (iArr.length == 1 && CategoryManager.this.currentLanguageCodes.length == 1 && iArr[0] != CategoryManager.this.currentLanguageCodes[0]) {
                    int coreForLanguage = CategoryManager.this.client.getCoreForLanguage(iArr[0]);
                    if (coreForLanguage >= 0) {
                        CategoryManager.this.coresInUse.add(Integer.valueOf(coreForLanguage));
                    }
                    z = true;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (int i : CategoryManager.this.currentLanguageCodes) {
                        arrayList.add(Integer.valueOf(i));
                    }
                    for (int i2 : iArr) {
                        int coreForLanguage2 = CategoryManager.this.client.getCoreForLanguage(i2);
                        if (coreForLanguage2 >= 0) {
                            CategoryManager.this.coresInUse.add(Integer.valueOf(coreForLanguage2));
                        }
                        if (!arrayList.contains(Integer.valueOf(i2))) {
                            z = true;
                        }
                    }
                }
                CategoryManager.this.currentLanguageCodes = iArr;
                if (hashSet.equals(CategoryManager.this.coresInUse) ? z : true) {
                    CategoryManager.this.savePreferences();
                    Iterator it = CategoryManager.this.submanagers.values().iterator();
                    while (it.hasNext()) {
                        ((SubManager) it.next()).languageUpdated(iArr, CategoryManager.this.coresInUse);
                    }
                }
            }

            @Override // com.nuance.connect.service.manager.interfaces.LanguageListener
            public void onLocaleUpdate(Locale locale) {
                CategoryManager.this.log.d("CategoryManager.onLocaleUpdate(", locale, ")");
                if (CategoryManager.this.currentLocale != locale) {
                    CategoryManager.this.currentLocale = locale;
                    CategoryManager.this.savePreferences();
                    Iterator it = CategoryManager.this.submanagers.values().iterator();
                    while (it.hasNext()) {
                        ((SubManager) it.next()).localeUpdated(locale);
                    }
                }
            }
        };
        this.submanagers = new HashMap<>();
        this.categoryListState = new ConcurrentHashMap();
        this.subManagerTypeLookup = new HashMap<>();
        this.version = 9;
        this.commandFamily = COMMAND_FAMILY;
        this.messages = MESSAGES_HANDLED;
        this.validCommands.addCommand("list", COMMAND_RESPONSE_SUCCESS);
        this.validCommands.addCommand(COMMAND_SUBSCRIBE, COMMAND_RESPONSE_SUCCESS);
        this.validCommands.addCommand(COMMAND_UNSUBSCRIBE, COMMAND_RESPONSE_SUCCESS);
        this.validCommands.addCommand("status", COMMAND_RESPONSE_SUCCESS);
        this.validCommands.addCommand("get", COMMAND_RESPONSE_SUCCESS);
        this.validCommands.addCommand("ack", COMMAND_RESPONSE_SUCCESS);
        this.validCommands.addCommand("download", COMMAND_RESPONSE_SUCCESS);
        this.categoryDatabase = CategoryDatabase.from(this.client);
        for (SubManagerDefinition subManagerDefinition : SubManagerDefinition.values()) {
            getSubManager(subManagerDefinition);
        }
    }

    private void cancelDownload(String str, boolean z) {
        if (!this.categoryDatabase.hasCategory(str)) {
            this.log.e("Category is not available for cancelDownload (" + str + ")");
            return;
        }
        boolean boolProp = this.categoryDatabase.getBoolProp(str, CategoryDatabase.SUBSCRIBED);
        this.log.d("cancelDownload(" + str + ") subscribed=", Boolean.valueOf(boolProp), ", preinstalled=", Boolean.valueOf(z));
        if (isTransactionActiveForId(str)) {
            this.log.d("cancelDownload transaction active: " + str);
            Transaction activeTransactionForId = getActiveTransactionForId(str);
            if (activeTransactionForId != null) {
                activeTransactionForId.cancel();
                return;
            }
            return;
        }
        if (z) {
            this.categoryDatabase.setProp(str, CategoryDatabase.USER_INITIATED, true);
            this.categoryDatabase.setStep(str, 7);
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATEGORY_DOWNLOAD_CANCEL_ACK, str);
        } else if (boolProp) {
            this.log.d("cancel unsubscribing...");
            unsubscribe(str);
        } else if (this.categoryDatabase.getStep(str) == 1) {
            this.log.d("cancel resetting state");
            this.categoryDatabase.setStep(str, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkRefreshCategoryLists() {
        ArrayList arrayList = new ArrayList();
        for (SubManager subManager : this.submanagers.values()) {
            for (Integer num : subManager.getTypesSupported()) {
                CategoryListState categoryListState = this.categoryListState.get(num);
                if (subManager.isEnabled() && categoryListState != null && categoryListState.dirty != ListUpdateState.UPDATING && (categoryListState.state == AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_UNKNOWN || categoryListState.dirty == ListUpdateState.NEEDS_UPDATE || subManager.categoriesManagedCount() != categoryListState.categoryCount)) {
                    arrayList.add(num);
                }
            }
        }
        this.log.d("checkRefreshCategoryLists list=", arrayList);
        if (arrayList.isEmpty()) {
            return;
        }
        this.log.d("Requesting updated list for the following categories: ", arrayList.toString());
        requestCategoryList((String) null, arrayList);
    }

    private static int[] concat(int[] iArr, int[] iArr2) {
        int[] copyOf = Arrays.copyOf(iArr, iArr.length + iArr2.length);
        System.arraycopy(iArr2, 0, copyOf, iArr.length, iArr2.length);
        return copyOf;
    }

    private Transaction getActiveTransactionForId(String str) {
        return super.getActiveTransaction(CategorySubscribeDownloadTransaction.getTransactionName(str, getTypeForID(str)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SubManager getSubManager(SubManagerDefinition subManagerDefinition) {
        SubManager categorySubmanagerCustomConfigs;
        if (subManagerDefinition == null) {
            return null;
        }
        synchronized (this.submanagers) {
            if (this.submanagers.containsKey(subManagerDefinition)) {
                return this.submanagers.get(subManagerDefinition);
            }
            switch (subManagerDefinition) {
                case SUBMANAGER_LIVING_LANGUAGE:
                    categorySubmanagerCustomConfigs = new CategorySubmanagerLivingLanguage(this, this.client, subManagerDefinition.getEnabledByDefault());
                    break;
                case SUBMANAGER_CHINESE_DATABASES:
                    categorySubmanagerCustomConfigs = new CategorySubmanagerChineseAddon(this, this.client, subManagerDefinition.getEnabledByDefault());
                    break;
                case SUBMANAGER_UPDATES:
                    categorySubmanagerCustomConfigs = new CategorySubmanagerPlatformUpdate(this, this.client, subManagerDefinition.getEnabledByDefault());
                    break;
                case SUBMANAGER_CATALOG:
                    categorySubmanagerCustomConfigs = new CategorySubmanagerCatalog(this, this.client, subManagerDefinition.getEnabledByDefault());
                    break;
                case SUBMANAGER_CUSTOM_CONFIG:
                    categorySubmanagerCustomConfigs = new CategorySubmanagerCustomConfigs(this, this.client, subManagerDefinition.getEnabledByDefault());
                    break;
                default:
                    categorySubmanagerCustomConfigs = null;
                    break;
            }
            if (categorySubmanagerCustomConfigs != null) {
                this.submanagers.put(subManagerDefinition, categorySubmanagerCustomConfigs);
                Iterator<Integer> it = categorySubmanagerCustomConfigs.getTypesSupported().iterator();
                while (it.hasNext()) {
                    this.subManagerTypeLookup.put(it.next(), categorySubmanagerCustomConfigs);
                }
            }
            return categorySubmanagerCustomConfigs;
        }
    }

    private SubManager getSubManagerForCategory(String str) {
        int typeForID = getTypeForID(str);
        for (SubManager subManager : this.submanagers.values()) {
            if (subManager.getTypesSupported().contains(Integer.valueOf(typeForID))) {
                return subManager;
            }
        }
        this.log.w("Unknown submanager for category: ", str);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getUnsubscribeTransactionName(String str) {
        return "UnsubscribeTransaction-" + str;
    }

    private void installComplete(String str, int i) {
        this.log.d("installComplete(", str, ") status(", Integer.valueOf(i), ")");
        if (!this.categoryDatabase.hasCategory(str)) {
            this.log.d("installComplete() - category list is not available (", str, ")");
            return;
        }
        this.log.d("Removed downloaded file = ", Boolean.valueOf(FileUtils.deleteFile(this.categoryDatabase.getProp(str, Strings.MAP_KEY_FILE_LOCATION))));
        if (i == Integer.MIN_VALUE) {
            this.categoryDatabase.setStep(str, 7);
        } else {
            this.log.e("install failed.  Unwinding... ", str);
            this.categoryDatabase.setStep(str, 0);
            unsubscribe(str);
        }
        processNextCategory();
    }

    private boolean isSubscribing(String str) {
        for (Map.Entry<String, Transaction> entry : this.activeTransactions.entrySet()) {
            if ((entry.getValue() instanceof CategorySubscribeDownloadTransaction) && entry.getKey().contains(str)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTransactionActiveForId(String str) {
        return super.isTransactionActive(CategorySubscribeDownloadTransaction.getTransactionName(str, getTypeForID(str)));
    }

    private boolean isUnsubscribeTransactionActiveForId(String str) {
        return super.isTransactionActive(getUnsubscribeTransactionName(str));
    }

    private void loadCategoryState(PersistentDataStore persistentDataStore) {
        int categoriesManagedCount;
        String readString = persistentDataStore.readString(SUBMANAGER_DOWNLOAD_LIST_STATE, "");
        this.log.d("loadCategoryState()", readString);
        if (readString.length() > 0) {
            for (String str : readString.split("\\|")) {
                String[] split = str.split(":");
                if (split.length >= 3) {
                    try {
                        int parseInt = Integer.parseInt(split[0]);
                        if (split.length > 3) {
                            categoriesManagedCount = Integer.parseInt(split[3]);
                        } else {
                            SubManager subManager = getSubManager(SubManagerDefinition.from(parseInt));
                            categoriesManagedCount = subManager != null ? subManager.categoriesManagedCount() : 0;
                        }
                        this.categoryListState.put(Integer.valueOf(parseInt), new CategoryListState(AbstractCommandManager.DownloadState.from(split[1]), Boolean.valueOf(split[2]).booleanValue() ? ListUpdateState.NEEDS_UPDATE : ListUpdateState.CLEAN, categoriesManagedCount));
                    } catch (NumberFormatException e) {
                        this.log.e((Object) "Error converting value ", (Throwable) e);
                    }
                }
            }
        } else if (this.managerDownloadListState.equals(AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_AVAILABLE)) {
            this.log.d("migrate()");
            Iterator<String> it = this.categoryDatabase.allCategoryIDs().iterator();
            while (it.hasNext()) {
                int typeForID = getTypeForID(it.next());
                if (!this.categoryListState.containsKey(Integer.valueOf(typeForID))) {
                    this.categoryListState.put(Integer.valueOf(typeForID), new CategoryListState());
                }
            }
            saveCategoryState();
        }
        Iterator<SubManager> it2 = this.submanagers.values().iterator();
        while (it2.hasNext()) {
            for (Integer num : it2.next().getTypesSupported()) {
                if (!this.categoryListState.containsKey(num)) {
                    this.categoryListState.put(num, new CategoryListState());
                }
            }
        }
    }

    private void loadPreferences() {
        PersistentDataStore dataStore = this.client.getDataStore();
        this.managerDownloadListState = AbstractCommandManager.DownloadState.from(dataStore.readString(DOWNLOAD_LIST_STATE, AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_UNKNOWN.toString()));
        loadCategoryState(dataStore);
        if (dataStore.exists(CATEGORY_INSTALLER_PREF)) {
            this.log.d("migrating... metadata");
            for (Map.Entry<String, HashMap<String, String>> entry : new InstallMetadata(this.client.getDataManager(), CATEGORY_INSTALLER_PREF).getAllMetadata().entrySet()) {
                this.log.d("migrating categoryId=", entry.getKey());
                try {
                    HashMap<String, String> value = entry.getValue();
                    String remove = value.remove(MessageAPI.TYPE);
                    value.remove("STARTED");
                    value.remove(MessageAPI.ID);
                    int intValue = Integer.decode(remove).intValue();
                    String tableForType = this.categoryDatabase.getTableForType(intValue);
                    Iterator<Map.Entry<String, String>> it = value.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, String> next = it.next();
                        SubManagerDefinition[] values = SubManagerDefinition.values();
                        int length = values.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                break;
                            }
                            SubManager subManager = getSubManager(values[i]);
                            if (subManager == null || !subManager.getTypesSupported().contains(Integer.valueOf(intValue))) {
                                i++;
                            } else if (!this.categoryDatabase.containsProperty(tableForType, next.getKey())) {
                                this.log.e("Unknown or deprecated property: " + next.getKey(), "; value: " + next.getValue());
                                it.remove();
                            }
                        }
                    }
                    this.log.d("adding categoryId=", entry.getKey(), " properties=", value);
                    this.categoryDatabase.addCategory(entry.getKey(), tableForType, value);
                } catch (NumberFormatException e) {
                    this.log.e("Could not migrate");
                }
            }
            dataStore.delete(CATEGORY_INSTALLER_PREF);
            this.log.d("migrating... done");
        }
        for (String str : dataStore.readString(CATEGORY_LAST_CORES_PREF, "").split(",")) {
            try {
                this.coresInUse.add(Integer.valueOf(Integer.parseInt(str)));
            } catch (NumberFormatException e2) {
            }
        }
        this.log.d("cores in use: ", this.coresInUse.toString());
        String[] split = dataStore.readString(CATEGORY_LAST_LANGUAGES_PREF, "").split(",");
        ArrayList arrayList = new ArrayList();
        for (String str2 : split) {
            try {
                arrayList.add(Integer.valueOf(str2));
            } catch (Exception e3) {
            }
        }
        this.currentLanguageCodes = new int[arrayList.size()];
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            this.currentLanguageCodes[i2] = ((Integer) arrayList.get(i2)).intValue();
        }
        String readString = dataStore.readString(CATEGORY_LAST_LOCALE_PREF, String.valueOf(this.client.getCurrentLocale()));
        if (readString.length() >= 2) {
            this.currentLocale = new Locale(readString);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:111:0x02ac  */
    /* JADX WARN: Removed duplicated region for block: B:129:0x0315  */
    /* JADX WARN: Removed duplicated region for block: B:132:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void processListResponse(com.nuance.connect.comm.Response r20, java.util.Set<java.lang.Integer> r21) {
        /*
            Method dump skipped, instructions count: 816
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.service.manager.CategoryManager.processListResponse(com.nuance.connect.comm.Response, java.util.Set):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processNextCategory() {
        this.log.d("processNextCategory() - state: [", this.managerDownloadListState, "]");
        if (this.managerDownloadListState == AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_NONE || this.categoryDatabase.isEmpty()) {
            this.log.d("processNextCategory() - none exist. done.");
            return;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        for (String str : this.categoryDatabase.listFromSteps(Arrays.asList(1))) {
            if (this.categoryDatabase.getBoolProp(str, CategoryDatabase.USER_INITIATED) && this.categoryDatabase.getBoolProp(str, CategoryDatabase.SUBSCRIBED) && !this.categoryDatabase.isInstalled(str)) {
                arrayList.add(str);
            }
        }
        if (!arrayList.isEmpty()) {
            subscribeList(arrayList);
        }
        for (Map.Entry<String, Map<String, String>> entry : this.categoryDatabase.allWithProperty(CategoryDatabase.UNSUBSCRIBE_PENDING).entrySet()) {
            if (Boolean.TRUE.toString().equals(entry.getValue().get(CategoryDatabase.UNSUBSCRIBE_PENDING))) {
                unsubscribe(entry.getKey());
            }
        }
        this.log.d("processNextCategory() -- completed ");
    }

    private void saveCategoryState() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, CategoryListState> entry : this.categoryListState.entrySet()) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append(entry.getKey().toString()).append(":").append(entry.getValue().state.name()).append(":").append(String.valueOf(entry.getValue().dirty != ListUpdateState.CLEAN)).append(":").append(entry.getValue().categoryCount);
        }
        this.log.d("saveCategoryState() ", sb.toString());
        this.client.getDataStore().saveString(SUBMANAGER_DOWNLOAD_LIST_STATE, sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void savePreferences() {
        PersistentDataStore dataStore = this.client.getDataStore();
        dataStore.saveString(DOWNLOAD_LIST_STATE, this.managerDownloadListState.toString());
        dataStore.saveString(CATEGORY_LAST_CORES_PREF, StringUtils.genericListToString(this.coresInUse, ","));
        dataStore.saveString(CATEGORY_LAST_LANGUAGES_PREF, StringUtils.implode(this.currentLanguageCodes, ","));
        if (this.currentLocale != null) {
            dataStore.saveString(CATEGORY_LAST_LOCALE_PREF, this.currentLocale.toString());
        }
        saveCategoryState();
    }

    private void subscribeNoDownloadList(List<String> list) {
        List<String> listFromSteps = this.categoryDatabase.listFromSteps(Arrays.asList(1));
        for (String str : list) {
            if (listFromSteps.contains(str) && isSubscribing(str)) {
                this.log.d("subscribeList - Category is already attempting a subscribe (", str, ")");
            } else if (isTransactionActive(str)) {
                this.log.d("subscribeList - already active transaction(", str, ")");
            } else {
                SubManager subManagerForCategory = getSubManagerForCategory(str);
                if (subManagerForCategory != null) {
                    CategorySubscribeDownloadTransaction categorySubscribeDownloadTransaction = (CategorySubscribeDownloadTransaction) subManagerForCategory.createSubscribeTransaction(str);
                    categorySubscribeDownloadTransaction.setDownloadAfterSubscribe(false);
                    startTransaction(categorySubscribeDownloadTransaction);
                }
            }
        }
    }

    private void unsubscribe(String str) {
        this.log.d("unsubscribe(", str, ")");
        if (!this.categoryDatabase.hasCategory(str)) {
            this.log.e("Category is not available for unsubscribe (", str, ")");
            return;
        }
        boolean boolProp = this.categoryDatabase.getBoolProp(str, CategoryDatabase.SUBSCRIBED);
        this.categoryDatabase.setProp(str, CategoryDatabase.SUBSCRIBED, false);
        this.categoryDatabase.setProp(str, CategoryDatabase.LAST_UPDATE_FETCHED, 0);
        this.categoryDatabase.setStep(str, 0);
        if (isTransactionActiveForId(str)) {
            Transaction activeTransactionForId = getActiveTransactionForId(str);
            if (activeTransactionForId != null) {
                activeTransactionForId.cancel();
                return;
            }
            return;
        }
        if (isUnsubscribeTransactionActiveForId(str)) {
            this.log.d("Already unsubscribing...");
            return;
        }
        if (!boolProp) {
            this.log.d("Already unsubscribed to: ", str);
            this.categoryDatabase.removeProp(str, CategoryDatabase.UNSUBSCRIBE_PENDING);
            return;
        }
        SubManager subManagerForCategory = getSubManagerForCategory(str);
        if (subManagerForCategory == null || subManagerForCategory.unsubscribe(str)) {
            return;
        }
        startTransaction(new UnsubscribeTransaction(str));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSubManagerStatus(SubManagerDefinition subManagerDefinition, boolean z) {
        SubManager subManager = this.submanagers.get(subManagerDefinition);
        boolean z2 = z == (!subManager.isEnabled());
        subManager.setEnabled(z);
        if (z2) {
            if (z) {
                this.client.idleSnooze();
                checkRefreshCategoryLists();
            }
            this.client.postMessage(InternalMessages.MESSAGE_RECALCULATE_POLLING);
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.CommandManager
    public void alarmNotification(String str, Bundle bundle) {
        Iterator<SubManager> it = this.submanagers.values().iterator();
        while (it.hasNext()) {
            it.next().alarmNotification(str, bundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancelActiveTransactions(int i) {
        for (Map.Entry<String, Transaction> entry : this.activeTransactions.entrySet()) {
            if (entry.getValue() instanceof CategorySubscribeDownloadTransaction) {
                CategorySubscribeDownloadTransaction categorySubscribeDownloadTransaction = (CategorySubscribeDownloadTransaction) entry.getValue();
                if (categorySubscribeDownloadTransaction.getType() == i) {
                    categorySubscribeDownloadTransaction.cancel();
                }
            }
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void destroy() {
        super.destroy();
        savePreferences();
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public String[] getDependencies() {
        ArrayList arrayList = new ArrayList();
        for (ManagerService managerService : ManagerService.CATEGORY.values()) {
            arrayList.add(managerService.getName());
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCommandManager.DownloadState getDownloadListState() {
        return this.managerDownloadListState;
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public int getManagerPollInterval() {
        int intValue = this.client.getInteger(ConnectConfiguration.ConfigProperty.DEFAULT_POLLING_INTERVAL_NO_FEATURES).intValue();
        Iterator<SubManager> it = this.submanagers.values().iterator();
        while (true) {
            int i = intValue;
            if (!it.hasNext()) {
                return i;
            }
            SubManager next = it.next();
            intValue = next.getManagerPollInterval() < i ? next.getManagerPollInterval() : i;
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.MessageProcessor
    public int[] getMessageIDs() {
        int[] iArr = MESSAGES_HANDLED;
        Iterator<SubManager> it = this.submanagers.values().iterator();
        while (true) {
            int[] iArr2 = iArr;
            if (!it.hasNext()) {
                return iArr2;
            }
            iArr = concat(iArr2, ((MessageProcessor) it.next()).getMessageIDs());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getTypeForID(String str) {
        return this.categoryDatabase.getTypeForTable(this.categoryDatabase.getCategoryType(str));
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void init() {
        loadPreferences();
        this.client.addLanguageListener(this.languageListener);
        Iterator<SubManager> it = this.submanagers.values().iterator();
        while (it.hasNext()) {
            it.next().init();
        }
        this.client.addListener(ConnectConfiguration.ConfigProperty.PLATFORM_UPDATE_ENABLED, this.booleanListener);
        this.client.addListener(ConnectConfiguration.ConfigProperty.POSSIBLE_UPGRADE, this.booleanListener);
        this.client.addListener(ConnectConfiguration.ConfigProperty.LOCATION_GEO_IP_COUNTRY, this.stringListener);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        Iterator<SubManager> it = this.submanagers.values().iterator();
        while (it.hasNext()) {
            if (((MessageProcessor) it.next()).onHandleMessage(message)) {
                return true;
            }
        }
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_COMMAND_CDB_PROCESS_CATEGORIES:
                processNextCategory();
                return true;
            case MESSAGE_CLIENT_CATEGORY_DB_SUBSCRIBE:
                String string = message.getData().getString(Strings.DEFAULT_KEY);
                if (this.categoryDatabase.hasCategory(string)) {
                    this.categoryDatabase.setProp(string, CategoryDatabase.USER_INITIATED, true);
                }
                subscribe(string);
                return true;
            case MESSAGE_CLIENT_CATEGORY_DB_SUBSCRIBE_OR_DOWNLOAD:
            case MESSAGE_CLIENT_CATEGORY_DB_SUBSCRIBE_OR_DOWNLOAD_LIST:
                String string2 = message.getData().getString(Strings.DEFAULT_KEY);
                if (string2 == null || string2.length() <= 0) {
                    this.log.e("Error subscribing to empty or null list");
                    return true;
                }
                try {
                    String[] split = string2.split(",");
                    ArrayList<String> arrayList = new ArrayList<>();
                    ArrayList<String> arrayList2 = new ArrayList<>();
                    for (String str : split) {
                        if (this.categoryDatabase.hasCategory(str)) {
                            this.categoryDatabase.setProp(str, CategoryDatabase.USER_INITIATED, true);
                            if (this.categoryDatabase.getBoolProp(str, CategoryDatabase.SUBSCRIBED)) {
                                arrayList2.add(str);
                            } else {
                                arrayList.add(str);
                            }
                        }
                    }
                    if (arrayList2.size() > 0) {
                        subscribeList(arrayList2);
                    }
                    if (arrayList.size() <= 0) {
                        return true;
                    }
                    subscribeList(arrayList);
                    return true;
                } catch (NullPointerException e) {
                    this.log.e("Error splitting list of categories to subscribe to.");
                    return true;
                }
            case MESSAGE_CLIENT_CATEGORY_DB_SUBSCRIBE_NO_DOWNLOAD:
            case MESSAGE_CLIENT_CATEGORY_DB_SUBSCRIBE_NO_DOWNLOAD_LIST:
                String string3 = message.getData().getString(Strings.DEFAULT_KEY);
                if (string3 == null || string3.length() <= 0) {
                    this.log.e("Error subscribing to empty or null list");
                    return true;
                }
                try {
                    String[] split2 = string3.split(",");
                    ArrayList arrayList3 = new ArrayList();
                    for (String str2 : split2) {
                        if (this.categoryDatabase.hasCategory(str2)) {
                            this.categoryDatabase.setProp(str2, CategoryDatabase.USER_INITIATED, true);
                        }
                        if (!this.categoryDatabase.getBoolProp(str2, CategoryDatabase.SUBSCRIBED)) {
                            arrayList3.add(str2);
                        }
                    }
                    if (arrayList3.size() <= 0) {
                        return true;
                    }
                    subscribeNoDownloadList(arrayList3);
                    return true;
                } catch (NullPointerException e2) {
                    this.log.e("Error splitting list of categories to subscribe to as its null.");
                    return true;
                }
            case MESSAGE_CLIENT_CATEGORY_DB_UNSUBSCRIBE:
                unsubscribe(message.getData().getString(Strings.DEFAULT_KEY));
                return true;
            case MESSAGE_CLIENT_CATEGORY_DB_CANCEL:
                cancelDownload(message.getData().getString(Strings.DEFAULT_KEY), message.arg1 == 7);
                return true;
            case MESSAGE_CLIENT_CATALOG_CANCEL:
                cancelDownload(message.getData().getString(Strings.DEFAULT_KEY), message.arg1 == 7);
                return true;
            case MESSAGE_CLIENT_SET_CATEGORY_HOTWORD_STATUS:
                boolean z = message.getData().getBoolean(Strings.DEFAULT_KEY);
                this.log.d("MESSAGE_CLIENT_SET_CATEGORY_HOTWORD_STATUS status: ", Boolean.valueOf(z));
                updateSubManagerStatus(SubManagerDefinition.SUBMANAGER_LIVING_LANGUAGE, z);
                return true;
            case MESSAGE_COMMAND_CDB_LIST_UPDATE:
                String string4 = message.getData().getString(Strings.ACKNOWLEDGEMENT);
                int i = message.getData().getInt(Strings.CATEGORY_TYPE, 0);
                if (!this.subManagerTypeLookup.containsKey(Integer.valueOf(i))) {
                    return true;
                }
                if (this.subManagerTypeLookup.get(Integer.valueOf(i)).isEnabled()) {
                    requestCategoryList(string4, i);
                    return true;
                }
                this.categoryListState.put(Integer.valueOf(i), new CategoryListState(AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_AVAILABLE, ListUpdateState.NEEDS_UPDATE, this.subManagerTypeLookup.get(Integer.valueOf(i)).categoriesManagedCount()));
                return true;
            case MESSAGE_COMMAND_CDB_AVAILABLE:
                if (this.managerStartState == AbstractCommandManager.ManagerState.STARTED) {
                    startTransaction(new CategoryStatusTransaction());
                    return true;
                }
                this.client.postMessageDelayed(InternalMessages.MESSAGE_COMMAND_CDB_AVAILABLE, 5000L);
                return true;
            case MESSAGE_CLIENT_CATEGORY_INSTALL_COMPLETE:
                installComplete(message.getData().getString(Strings.DEFAULT_KEY), message.arg1);
                return true;
            case MESSAGE_CLIENT_SET_CHINESE_CAT_DB_STATUS:
                updateSubManagerStatus(SubManagerDefinition.SUBMANAGER_CHINESE_DATABASES, message.getData().getBoolean(Strings.DEFAULT_KEY));
                return true;
            case MESSAGE_CLIENT_SET_CATALOG_STATUS:
                updateSubManagerStatus(SubManagerDefinition.SUBMANAGER_CATALOG, message.getData().getBoolean(Strings.DEFAULT_KEY));
                return true;
            default:
                return false;
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.comm.ResponseCallback
    public void onResponse(Response response) {
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2, boolean z) {
        if (VersionUtils.isDataCleanupRequiredOnUpgrade(version, version2) && this.categoryDatabase.isEmpty() && AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_AVAILABLE.equals(this.managerDownloadListState)) {
            this.managerDownloadListState = AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_UNKNOWN;
        }
        Iterator<SubManager> it = this.submanagers.values().iterator();
        while (it.hasNext()) {
            it.next().onUpgrade(version, version2);
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void postStart() {
        this.log.d("postStart()");
        processNextCategory();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestCategoryList(String str, int i) {
        requestCategoryList(str, Arrays.asList(Integer.valueOf(i)));
    }

    void requestCategoryList(String str, List<Integer> list) {
        if (startTransactionWithAck(new CategoryListTransaction(str, list, this.currentLocale, this.currentCountry))) {
            return;
        }
        if (list == null || list.contains(0)) {
            list = new ArrayList<>();
            for (SubManager subManager : this.submanagers.values()) {
                if (subManager.isEnabled()) {
                    list.addAll(subManager.getTypesSupported());
                }
            }
        }
        this.log.d("requestCategoryList in progress; types: ", list);
        for (Integer num : list) {
            CategoryListState categoryListState = this.categoryListState.get(num);
            if (categoryListState == null) {
                this.categoryListState.put(num, new CategoryListState());
            } else {
                this.categoryListState.put(num, new CategoryListState(categoryListState.state, ListUpdateState.NEEDS_UPDATE, categoryListState.categoryCount));
            }
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void start() {
        this.managerStartState = AbstractCommandManager.ManagerState.STARTING;
        if (this.managerDownloadListState == AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_UNKNOWN || this.managerDownloadListState == AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_NONE) {
            this.log.d("start() ", this.managerDownloadListState);
            requestCategoryList((String) null, 0);
        } else {
            this.log.d("check to see if the lists need to be refreshed");
            checkRefreshCategoryLists();
        }
        Iterator<SubManager> it = this.submanagers.values().iterator();
        while (it.hasNext()) {
            it.next().start();
        }
        managerStartComplete();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void subscribe(String str) {
        SubManager subManagerForCategory = getSubManagerForCategory(str);
        if (subManagerForCategory != null) {
            startTransaction(subManagerForCategory.createSubscribeTransaction(str));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void subscribeList(ArrayList<String> arrayList) {
        this.log.d("subscribeList: ", StringUtils.implode(arrayList, ","));
        List<String> listFromSteps = this.categoryDatabase.listFromSteps(Arrays.asList(1));
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (!this.categoryDatabase.hasCategory(next)) {
                this.log.d("subscribeList - Category is not available for subscriptions (", next, ")");
            } else if (listFromSteps.contains(next) && isSubscribing(next)) {
                this.log.d("subscribeList - Category is already attempting a subscribe (", next, ")");
            } else if (isTransactionActive(next)) {
                this.log.d("subscribeList - already active transaction(", next, ")");
            } else {
                SubManager subManagerForCategory = getSubManagerForCategory(next);
                if (subManagerForCategory != null) {
                    startTransaction(subManagerForCategory.createSubscribeTransaction(next));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unsubscribeAll() {
        unsubscribeAll(1);
        unsubscribeAll(3);
    }

    void unsubscribeAll(int i) {
        for (String str : this.categoryDatabase.allCategoryIDs(this.categoryDatabase.getTableForType(i))) {
            if (this.categoryDatabase.getBoolProp(str, CategoryDatabase.SUBSCRIBED)) {
                this.log.d("unsubscribeAll type-", Integer.valueOf(i), "; id-", str);
                unsubscribe(str);
            }
        }
    }
}
