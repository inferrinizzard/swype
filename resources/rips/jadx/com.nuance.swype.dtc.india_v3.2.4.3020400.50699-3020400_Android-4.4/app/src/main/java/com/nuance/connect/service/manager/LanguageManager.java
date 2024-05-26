package com.nuance.connect.service.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.SparseIntArray;
import com.nuance.connect.comm.AbstractTransaction;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.comm.Response;
import com.nuance.connect.comm.Transaction;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.Document;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.internal.common.ManagerService;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.util.EncryptUtils;
import com.nuance.connect.util.FileUtils;
import com.nuance.connect.util.InstallMetadata;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import com.nuance.connect.util.TimeConversion;
import com.nuance.connect.util.VersionUtils;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class LanguageManager extends AbstractCommandManager {
    public static final String COMMAND_DOWNLOAD_LANGUAGE_PACK = "downloadLangPack";
    public static final String COMMAND_FAMILY;
    public static final int COMMAND_VERSION = 9;
    public static final int DOWNLOAD_LIST_STATE_AVAILABLE = 1;
    public static final int DOWNLOAD_LIST_STATE_NONE = 2;
    public static final String DOWNLOAD_LIST_STATE_PREF;
    public static final int DOWNLOAD_LIST_STATE_UNKNOWN = 0;
    public static final int KOREAN_LL_ID = 2066;
    public static final String LANGUAGE_INSTALLER_PREF = "LANGUAGE_INSTALLER_DATA";
    public static final String LANGUAGE_PREINSTALL_LIST_PREF = "LANGUAGE_PREINSTALL_LIST";
    public static final String LANGUAGE_SUPPORTED_LIST_PREF = "LANGUAGE_SUPPORTED_LIST";
    public static final String LANGUAGE_UPGRADE_PREF = "LANGUAGE_UPGRADE_DATA";
    public static final String MANAGER_NAME;
    private static final InternalMessages[] MESSAGES_HANDLED;
    public static final String USER_LANGUAGES_PREF = "LANGUAGES_DL";
    private final Property.BooleanValueListener booleanListener;
    private int downloadListState;
    private final InstallMetadata languageInstallMetadata;
    private final SparseIntArray languagesToCores;
    private final Logger.Log log;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class LanguageDownloadTransaction extends AbstractTransaction {
        private static final int FREESPACE_MIN_SIZE = 102400;
        private volatile boolean canceled;
        private String downloadFilePath;
        private volatile boolean failed;
        private volatile int failureType = 0;
        private final String lang;
        private final HashMap<String, String> properties;
        private String transactionId;
        private String url;

        public LanguageDownloadTransaction(String str) {
            LanguageManager.this.log.d("LanguageDownloadTransaction language: ", str);
            this.lang = str;
            if (!LanguageManager.this.languageInstallMetadata.hasPackage(this.lang)) {
                LanguageManager.this.log.e("Language is not available for download! (", this.lang, ")");
                this.properties = null;
                this.currentCommand = null;
                return;
            }
            this.properties = LanguageManager.this.languageInstallMetadata.getProps(this.lang);
            this.url = this.properties.get("URL");
            if (this.url == null || this.url.equals("")) {
                LanguageManager.this.log.e("unable to download language pack, no URL found for pack ", this.lang);
                this.currentCommand = null;
                return;
            }
            LanguageManager.this.languageInstallMetadata.beginTransaction();
            try {
                this.downloadFilePath = this.properties.get(Strings.MAP_KEY_FILE_LOCATION);
                if (LanguageManager.this.languageInstallMetadata.isInstalling(this.lang)) {
                    int intValue = Integer.decode(this.properties.get(Strings.MAP_KEY_STEP)).intValue();
                    String str2 = this.properties.get(Strings.MAP_KEY_FILE_LOCATION);
                    if (!(intValue == 5 && str2 == null) && (str2 == null || new File(str2).exists())) {
                        this.properties.put(Strings.MAP_KEY_STEP, MessageAPI.DELAYED_FROM);
                        downloadLanguagePack();
                    } else {
                        this.currentCommand = null;
                    }
                } else {
                    downloadLanguagePack();
                }
                new Bundle().putString(Strings.DEFAULT_KEY, this.lang);
                LanguageManager.this.languageInstallMetadata.setUnsavedProp(this.lang, Strings.PROP_DOWNLOAD_PERCENT, 0);
            } finally {
                LanguageManager.this.languageInstallMetadata.commitTransaction();
            }
        }

        private void downloadLanguagePack() {
            if (this.canceled) {
                LanguageManager.this.log.d("ackLanguagePack canceled: ", getName());
                rollback();
                return;
            }
            LanguageManager.this.log.d("LanguageDownloadTransaction ", getName(), " download now");
            LanguageManager.this.languageInstallMetadata.setUnsavedProps(this.lang, this.properties);
            this.currentCommand = LanguageManager.this.createCommand(LanguageManager.COMMAND_DOWNLOAD_LANGUAGE_PACK, Command.REQUEST_TYPE.USER, new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.LanguageManager.LanguageDownloadTransaction.1
                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onDownloadStatusResponse(Command command, int i, int i2) {
                    if (LanguageDownloadTransaction.this.canceled) {
                        LanguageManager.this.log.d("ackLanguagePack canceled: ", LanguageDownloadTransaction.this.getName());
                        LanguageDownloadTransaction.this.rollback();
                    } else {
                        if (i >= 0) {
                            int max = (int) ((i / Math.max(i2, 1.0f)) * 100.0f);
                            LanguageManager.this.languageInstallMetadata.setUnsavedProp(LanguageDownloadTransaction.this.lang, Strings.PROP_DOWNLOAD_PERCENT, max);
                            LanguageManager.this.sendLanguageDownloadProgress(LanguageDownloadTransaction.this.lang, max);
                            return;
                        }
                        LanguageManager.this.log.e(command.parameters.get(MessageAPI.LANGUAGE_ID) + " failed to install due to insufficient storage.");
                        LanguageDownloadTransaction.this.failed = true;
                        LanguageDownloadTransaction.this.failureType = 6;
                        LanguageDownloadTransaction.this.rollback();
                    }
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onFileResponse(Response response) {
                    LanguageManager.this.log.d("LanguageDownloadTransaction ", LanguageDownloadTransaction.this.getName(), " onFileResponse");
                    if (LanguageDownloadTransaction.this.canceled) {
                        LanguageManager.this.log.d("ackLanguagePack canceled: ", LanguageDownloadTransaction.this.getName());
                        LanguageDownloadTransaction.this.rollback();
                        return;
                    }
                    LanguageDownloadTransaction.this.downloadFilePath = null;
                    String str = (String) LanguageDownloadTransaction.this.properties.get("CHECKSUM");
                    String md5 = EncryptUtils.md5(response.file);
                    LanguageManager.this.log.d("LanguageDownloadTransaction ", LanguageDownloadTransaction.this.getName(), " checksum verification: [", md5, "] to [", str, "]");
                    LanguageManager.this.log.d("LanguageDownloadTransaction ", LanguageDownloadTransaction.this.getName(), ": file ", response.file.getAbsolutePath());
                    if (str.equals(md5)) {
                        LanguageManager.this.languageInstallMetadata.setStep(LanguageDownloadTransaction.this.lang, 4);
                        LanguageManager.this.languageInstallMetadata.setProp(LanguageDownloadTransaction.this.lang, Strings.MAP_KEY_FILE_LOCATION, response.file.getAbsolutePath());
                        LanguageDownloadTransaction.this.currentCommand = null;
                    } else {
                        LanguageManager.this.log.d("LanguageDownloadTransaction ", LanguageDownloadTransaction.this.getName(), ": failed to verify the language pack, rollback will occur");
                        LanguageDownloadTransaction.this.currentCommand = null;
                        LanguageDownloadTransaction.this.failed = true;
                        LanguageDownloadTransaction.this.rollback();
                    }
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onIOExceptionResponse(Command command) {
                    LanguageManager.this.log.d("LanguageDownloadTransaction ", LanguageDownloadTransaction.this.getName(), ": ioException", command.thirdPartyURL);
                    int intProp = LanguageManager.this.languageInstallMetadata.getIntProp(LanguageDownloadTransaction.this.lang, Strings.PROP_DOWNLOAD_PERCENT);
                    LanguageDownloadTransaction.this.properties.put(Strings.MAP_KEY_FILE_LOCATION, LanguageDownloadTransaction.this.downloadFilePath);
                    LanguageDownloadTransaction.this.properties.put(Strings.MAP_KEY_STEP, "0");
                    LanguageManager.this.languageInstallMetadata.setProps(LanguageDownloadTransaction.this.lang, LanguageDownloadTransaction.this.properties);
                    LanguageDownloadTransaction.this.currentCommand = null;
                    LanguageDownloadTransaction.this.failed = true;
                    if (intProp <= 0 || intProp >= 100 || LanguageDownloadTransaction.this.downloadFilePath == null) {
                        return;
                    }
                    File file = new File(LanguageDownloadTransaction.this.downloadFilePath);
                    LanguageManager.this.log.d("No more space to complete download.");
                    if (file.getFreeSpace() < 102400) {
                        LanguageDownloadTransaction.this.failureType = 6;
                    }
                }

                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    LanguageManager.this.log.e("Error");
                    throw new UnsupportedOperationException("Shouldn't get here!");
                }
            });
            this.currentCommand.thirdPartyURL = this.url;
            this.currentCommand.method = "GET";
            this.currentCommand.hasBody = false;
            this.currentCommand.handleIOExceptionInConnector = false;
            this.currentCommand.notifyDownloadStatus = true;
            this.currentCommand.allowDuplicateOfCommand = true;
            this.currentCommand.parameters.put(MessageAPI.LANGUAGE_ID, this.lang);
            this.currentCommand.parameters.put(MessageAPI.TRANSACTION_ID, this.transactionId);
        }

        private void verifyWithHost() {
            this.properties.put(Strings.MAP_KEY_STEP, MessageAPI.DEVICE_ID);
            Bundle bundle = new Bundle();
            bundle.putString(Strings.DEFAULT_KEY, this.lang);
            bundle.putString(Strings.MESSAGE_BUNDLE_FILEPATH, this.properties.get(Strings.MAP_KEY_FILE_LOCATION));
            LanguageManager.this.languageInstallMetadata.setProps(this.lang, this.properties);
            if (this.properties.get(Strings.MAP_KEY_FILE_LOCATION) != null && new File(this.properties.get(Strings.MAP_KEY_FILE_LOCATION)).exists()) {
                LanguageManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_LANGUAGE_INSTALL_READY, bundle);
            } else {
                LanguageManager.this.log.w("Something went wrong, file does not exist for ", this.lang, " file: ", this.properties.get(Strings.MAP_KEY_FILE_LOCATION));
                LanguageManager.this.notifyOfFailedInstall(this.lang, this.failureType);
            }
        }

        @Override // com.nuance.connect.comm.Transaction
        public void cancel() {
            LanguageManager.this.log.d("LanguageDownloadTransaction ", getName(), ": cancel called");
            this.canceled = true;
            if (this.currentCommand != null) {
                this.currentCommand.canceled = true;
            } else {
                rollback();
            }
        }

        @Override // com.nuance.connect.comm.Transaction
        public String createDownloadFile(Context context) {
            if (this.downloadFilePath == null) {
                try {
                    this.downloadFilePath = File.createTempFile("temp", ".download", LanguageManager.this.client.getCacheDir()).getAbsolutePath();
                    this.properties.put(Strings.MAP_KEY_FILE_LOCATION, this.downloadFilePath);
                } catch (IOException e) {
                    this.downloadFilePath = null;
                }
            }
            LanguageManager.this.log.d("temp file for lang: ", this.lang, this.downloadFilePath);
            return this.downloadFilePath;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public String getDownloadFile() {
            return this.downloadFilePath;
        }

        @Override // com.nuance.connect.comm.Transaction
        public String getName() {
            return LanguageManager.getTransactionName(this.lang);
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
            return 10;
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command.REQUEST_TYPE getRequestType() {
            return Command.REQUEST_TYPE.USER;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onEndProcessing() {
            LanguageManager.this.finishTransaction(getName());
            if (this.canceled) {
                rollback();
                LanguageManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_LANGUAGE_DOWNLOAD_CANCEL_ACK, this.lang);
            } else if (this.failed) {
                LanguageManager.this.notifyOfFailedInstall(this.lang, this.failureType);
            } else {
                verifyWithHost();
            }
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public boolean onTransactionOfflineQueued() {
            return false;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onTransactionRejected(int i) {
            LanguageManager.this.log.d("onTransactionRejected(", Integer.valueOf(i), ") ", this.lang);
            this.failed = true;
            this.failureType = i;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
            FileUtils.deleteFile(LanguageManager.this.languageInstallMetadata.getProp(this.lang, Strings.MAP_KEY_FILE_LOCATION));
            LanguageManager.this.languageInstallMetadata.removeProp(this.lang, Strings.MAP_KEY_FILE_LOCATION);
            LanguageManager.this.languageInstallMetadata.uninstallPackage(this.lang);
            this.currentCommand = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class LanguageListTransaction extends AbstractTransaction {
        private final String taskAcknowledgement;

        LanguageListTransaction(String str) {
            this.taskAcknowledgement = str;
            getList();
        }

        private void getList() {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(MessageAPI.ALL, Boolean.TRUE);
            Command createCommand = LanguageManager.this.createCommand("list", Command.REQUEST_TYPE.CRITICAL, hashMap);
            createCommand.responseCallback = new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.LanguageManager.LanguageListTransaction.1
                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    LanguageListTransaction.this.listResponse(response);
                }
            };
            this.currentCommand = createCommand;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void listResponse(Response response) {
            boolean z;
            this.currentCommand = null;
            if (1 == response.status) {
                if (response.parameters.containsKey(MessageAPI.LANGUAGE_LIST)) {
                    LanguageManager.this.log.d("Processing languages available for download.");
                    JSONArray jSONArray = (JSONArray) response.parameters.get(MessageAPI.LANGUAGE_LIST);
                    if (jSONArray != null) {
                        if (jSONArray.length() > 0) {
                            LanguageManager.this.languageInstallMetadata.beginTransaction();
                            for (int i = 0; i < jSONArray.length(); i++) {
                                try {
                                    try {
                                        JSONObject jSONObject = jSONArray.getJSONObject(i);
                                        String string = jSONObject.getString(MessageAPI.LANGUAGE);
                                        HashMap hashMap = new HashMap();
                                        hashMap.put(Strings.PROP_VERSION, jSONObject.getString(MessageAPI.VERSION));
                                        String string2 = jSONObject.getString(MessageAPI.URL);
                                        hashMap.put("CHECKSUM", jSONObject.getString(MessageAPI.CHECKSUM));
                                        hashMap.put("URL", string2);
                                        if (jSONObject.has(MessageAPI.CORE_VERSION_ALPHA)) {
                                            hashMap.put(Strings.PROP_CORE, MessageAPI.CORE_VERSION_ALPHA);
                                            hashMap.put(Strings.PROP_CORE_VERSION, jSONObject.getString(MessageAPI.CORE_VERSION_ALPHA));
                                            z = false;
                                        } else if (jSONObject.has(MessageAPI.CORE_VERSION_CHINESE)) {
                                            hashMap.put(Strings.PROP_CORE, MessageAPI.CORE_VERSION_CHINESE);
                                            hashMap.put(Strings.PROP_CORE_VERSION, jSONObject.getString(MessageAPI.CORE_VERSION_CHINESE));
                                            z = false;
                                        } else if (jSONObject.has(MessageAPI.CORE_VERSION_JAPANESE)) {
                                            hashMap.put(Strings.PROP_CORE, MessageAPI.CORE_VERSION_JAPANESE);
                                            hashMap.put(Strings.PROP_CORE_VERSION, jSONObject.getString(MessageAPI.CORE_VERSION_JAPANESE));
                                            z = false;
                                        } else if (jSONObject.has(MessageAPI.CORE_VERSION_KOREAN)) {
                                            hashMap.put(Strings.PROP_CORE, MessageAPI.CORE_VERSION_KOREAN);
                                            hashMap.put(Strings.PROP_CORE_VERSION, jSONObject.getString(MessageAPI.CORE_VERSION_KOREAN));
                                            z = false;
                                        } else {
                                            LanguageManager.this.log.e("Core version not supplied for language: " + string);
                                            z = true;
                                        }
                                        hashMap.put(MessageAPI.COUNTRY, jSONObject.getString(MessageAPI.COUNTRY));
                                        hashMap.put(MessageAPI.NAME_TRANSLATED, jSONObject.getString(MessageAPI.NAME_TRANSLATED));
                                        JSONArray jSONArray2 = jSONObject.getJSONArray(MessageAPI.LANGUAGE_IDS);
                                        int[] iArr = new int[jSONArray2.length()];
                                        if (iArr.length > 0) {
                                            for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                                                iArr[i2] = jSONArray2.getInt(i2);
                                            }
                                            Arrays.sort(iArr);
                                        } else {
                                            LanguageManager.this.log.e("Could not process language Ids for " + string);
                                            z = true;
                                        }
                                        if (!z) {
                                            LanguageManager.this.languageInstallMetadata.addPackage(string);
                                            HashMap<String, String> props = LanguageManager.this.languageInstallMetadata.getProps(string);
                                            props.putAll(hashMap);
                                            LanguageManager.this.languageInstallMetadata.setProps(string, props);
                                            LanguageManager.this.languageInstallMetadata.setProp(string, MessageAPI.LANGUAGE_IDS, iArr);
                                        }
                                    } catch (JSONException e) {
                                        LanguageManager.this.log.e("Failure processing JSON object: " + e.getMessage() + " list: " + i);
                                    }
                                } catch (Throwable th) {
                                    LanguageManager.this.languageInstallMetadata.commitTransaction();
                                    throw th;
                                }
                            }
                            LanguageManager.this.languageInstallMetadata.commitTransaction();
                            LanguageManager.this.downloadListState = 1;
                            LanguageManager.this.client.getDataStore().saveInt(LanguageManager.DOWNLOAD_LIST_STATE_PREF, LanguageManager.this.downloadListState);
                        } else {
                            LanguageManager.this.downloadListState = 2;
                            LanguageManager.this.client.getDataStore().saveInt(LanguageManager.DOWNLOAD_LIST_STATE_PREF, LanguageManager.this.downloadListState);
                        }
                    }
                }
                LanguageManager.this.sendDownloadLanguagesStatus(true);
                LanguageManager.this.client.taskCompletedAcknowledgement(this.taskAcknowledgement);
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
            return "LanguageListTransaction";
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
            LanguageManager.this.finishTransaction(getName());
            LanguageManager.this.managerStartComplete();
            LanguageManager.this.client.notifyLanguageListeners();
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
        }
    }

    static {
        String name = ManagerService.LANGUAGE.getName();
        COMMAND_FAMILY = name;
        MANAGER_NAME = name;
        DOWNLOAD_LIST_STATE_PREF = MANAGER_NAME + "_DOWNLOAD_LIST_STATE";
        MESSAGES_HANDLED = new InternalMessages[]{InternalMessages.MESSAGE_CLIENT_LANGUAGE_INSTALL, InternalMessages.MESSAGE_CLIENT_LANGUAGE_INSTALLED, InternalMessages.MESSAGE_CLIENT_LANGUAGE_UNINSTALL, InternalMessages.MESSAGE_CLIENT_LANGUAGE_DOWNLOAD_CANCEL, InternalMessages.MESSAGE_COMMAND_LANGUAGE_LIST_UPDATE, InternalMessages.MESSAGE_CLIENT_INSTALLED_LANGUAGES};
    }

    public LanguageManager(ConnectClient connectClient) {
        super(connectClient);
        this.downloadListState = 0;
        this.log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
        this.booleanListener = new Property.BooleanValueListener() { // from class: com.nuance.connect.service.manager.LanguageManager.1
            @Override // com.nuance.connect.internal.Property.ValueListener
            public void onValueChanged(Property<Boolean> property) {
                if (property.getKey().equals(ConnectConfiguration.ConfigProperty.POSSIBLE_UPGRADE.name()) && property.getValue().equals(Boolean.TRUE)) {
                    LanguageManager.this.markListDirty();
                }
            }
        };
        this.languagesToCores = new SparseIntArray();
        this.version = 9;
        this.commandFamily = COMMAND_FAMILY;
        setMessagesHandled(MESSAGES_HANDLED);
        this.validCommands.addCommand("list", new int[]{1});
        this.validCommands.addCommand("get", new int[]{1});
        this.validCommands.addCommand(COMMAND_DOWNLOAD_LANGUAGE_PACK, new int[]{1});
        this.validCommands.addCommand("ack", new int[]{1, 0});
        this.validCommands.addCommand("status", new int[]{1, 0});
        this.languageInstallMetadata = new InstallMetadata(this.client.getDataManager(), LANGUAGE_INSTALLER_PREF);
    }

    private void cleanUpFromFailedInstall(String str) {
        if (this.languageInstallMetadata.hasPackage(str)) {
            FileUtils.deleteFile(this.languageInstallMetadata.getProp(str, Strings.MAP_KEY_FILE_LOCATION));
            this.languageInstallMetadata.uninstallPackage(str);
            finishTransaction(getTransactionName(str));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getTransactionName(String str) {
        return LanguageDownloadTransaction.class.getName() + Document.ID_SEPARATOR + (str == null ? "" : str.split(XMLResultsHandler.SEP_HYPHEN)[0]);
    }

    private void loadPreferences() {
        this.downloadListState = this.client.getDataStore().readInt(DOWNLOAD_LIST_STATE_PREF, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void markListDirty() {
        this.downloadListState = 0;
        this.client.getDataStore().saveInt(DOWNLOAD_LIST_STATE_PREF, this.downloadListState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyOfFailedInstall(String str, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt(Strings.DEFAULT_KEY, i);
        bundle.putString(Strings.PROP_LANGUAGE, str);
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_LANGUAGE_DOWNLOAD_FAILED, bundle);
    }

    private void saveInstalledLanguageList() {
        this.client.getDataStore().saveString(USER_LANGUAGES_PREF, this.languageInstallMetadata.getInstalledPackageList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendDownloadLanguagesStatus(boolean z) {
        if ((z || this.managerStartState.equals(AbstractCommandManager.ManagerState.STARTED)) && this.languageInstallMetadata != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Strings.DEFAULT_KEY, this.languageInstallMetadata.getAllMetadata());
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_SET_LANGUAGES_STATUS, bundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendLanguageDownloadProgress(String str, int i) {
        if (this.languageInstallMetadata != null) {
            Bundle bundle = new Bundle();
            bundle.putString(Strings.PROP_LANGUAGE, str);
            bundle.putString(Strings.PROP_DOWNLOAD_PERCENT, String.valueOf(i));
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_LANGUAGE_DOWNLOAD_PROGRESS, bundle);
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.CommandManager
    public void alarmNotification(String str, Bundle bundle) {
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void destroy() {
        this.languageInstallMetadata.saveMetadata();
        saveInstalledLanguageList();
        super.destroy();
    }

    public void finishLanguageInstall(String str, String str2) {
        this.log.d("finishLanguageInstall(Lang: ", str, ", Version: ", str2, ")");
        finishTransaction(getTransactionName(str));
        HashMap<String, String> props = this.languageInstallMetadata.getProps(str);
        props.put(Strings.MAP_KEY_STEP, MessageAPI.MESSAGE);
        props.put(Strings.PROP_INSTALLEDVERSION, str2);
        props.remove(Strings.MAP_KEY_TRANSACTION_ID);
        props.remove(Strings.MAP_KEY_FILE_LOCATION);
        props.remove(Strings.PROP_INSTALL_TIME);
        this.languageInstallMetadata.setProps(str, props);
        sendDownloadLanguagesStatus(false);
        saveInstalledLanguageList();
        if (this.languageInstallMetadata.hasMoreInstalls() || this.languageInstallMetadata.hasMoreRemoves()) {
            return;
        }
        this.client.postMessage(InternalMessages.MESSAGE_COMMAND_DEVICE_UPDATE);
    }

    public int getCoreForLanguage(int i) {
        boolean z;
        synchronized (this.languagesToCores) {
            if (this.languagesToCores.get(i) != 0) {
                return this.languagesToCores.get(i);
            }
            Iterator<String> it = this.languageInstallMetadata.allPackages().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                String next = it.next();
                int[] intArrayProp = this.languageInstallMetadata.getIntArrayProp(next, MessageAPI.LANGUAGE_IDS);
                if (intArrayProp != null) {
                    int length = intArrayProp.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 < length) {
                            int i3 = intArrayProp[i2];
                            if (i3 != i) {
                                if (i == 2066 && (i3 & 255) == (i & 255)) {
                                    z = true;
                                    break;
                                }
                                i2++;
                            } else {
                                z = true;
                                break;
                            }
                        } else {
                            z = false;
                            break;
                        }
                    }
                    if (z) {
                        String prop = this.languageInstallMetadata.getProp(next, Strings.PROP_CORE);
                        if (prop != null) {
                            int i4 = prop.equals(MessageAPI.CORE_VERSION_ALPHA) ? 1 : prop.equals(MessageAPI.CORE_VERSION_CHINESE) ? 3 : prop.equals(MessageAPI.CORE_VERSION_JAPANESE) ? 4 : prop.equals(MessageAPI.CORE_VERSION_KOREAN) ? 2 : 0;
                            if (i4 != 0) {
                                synchronized (this.languagesToCores) {
                                    this.languagesToCores.put(i, i4);
                                }
                                return i4;
                            }
                        }
                    }
                }
            }
            return 0;
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public String[] getDependencies() {
        ArrayList arrayList = new ArrayList();
        for (ManagerService managerService : ManagerService.LANGUAGE.values()) {
            arrayList.add(managerService.getName());
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public List<String> getInstalledLanguages() {
        List<String> listFromSteps = this.languageInstallMetadata.listFromSteps(Arrays.asList(7));
        if (listFromSteps.size() != 0) {
            return listFromSteps;
        }
        String readString = this.client.getDataStore().readString(LANGUAGE_UPGRADE_PREF, "");
        return readString.length() > 0 ? Arrays.asList(readString.split(",")) : listFromSteps;
    }

    public void getLanguagePack(String str) {
        this.log.d("getLanguagePack(lang: ", str, ")");
        if (!this.languageInstallMetadata.hasPackage(str)) {
            this.log.e("Language is not available for download! (" + str + ")");
        } else if (isTransactionActive(getTransactionName(str))) {
            this.log.d("getLanguagePack attempted to download already downloading language: ", str);
        } else {
            startTransaction(new LanguageDownloadTransaction(str));
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public int getManagerPollInterval() {
        return this.client.getInteger(ConnectConfiguration.ConfigProperty.POLL_INTERVAL_LANGUAGE_DOWNLOAD).intValue();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void init() {
        loadPreferences();
        this.client.addListener(ConnectConfiguration.ConfigProperty.POSSIBLE_UPGRADE, this.booleanListener);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        boolean z = false;
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_COMMAND_LANGUAGE_LIST_UPDATE:
                this.log.d("LanguageManager.onHandleMessage(MESSAGE_COMMAND_LANGUAGE_LIST_UPDATE)");
                requestLanguagesList(message.getData().getString(Strings.ACKNOWLEDGEMENT));
                break;
            case MESSAGE_CLIENT_LANGUAGE_INSTALL:
                this.log.d("LanguageManager.onHandleMessage(MESSAGE_CLIENT_LANGUAGE_INSTALL)");
                String string = message.getData().getString(Strings.DEFAULT_KEY);
                this.log.d("Message: MESSAGE_CLIENT_LANGUAGE_INSTALL -- language: ", string);
                getLanguagePack(string);
                break;
            case MESSAGE_CLIENT_LANGUAGE_INSTALLED:
                this.log.d("LanguageManager.onHandleMessage(MESSAGE_CLIENT_LANGUAGE_INSTALLED) msg.arg1=" + message.arg1);
                String string2 = message.getData().getString(Strings.DEFAULT_KEY);
                if (message.arg1 == Integer.MIN_VALUE) {
                    finishLanguageInstall(string2, String.valueOf(message.arg2));
                } else if (message.arg1 == 0) {
                    this.log.e(string2 + " failed to install.");
                    cleanUpFromFailedInstall(string2);
                } else if (message.arg1 == 1) {
                    this.log.e(string2 + " failed to install.");
                    unwind();
                }
                return true;
            case MESSAGE_CLIENT_LANGUAGE_UNINSTALL:
                this.log.d("LanguageManager.onHandleMessage(MESSAGE_CLIENT_LANGUAGE_UNINSTALL)");
                uninstallLanguage(message.getData().getString(Strings.DEFAULT_KEY));
                break;
            case MESSAGE_CLIENT_LANGUAGE_DOWNLOAD_CANCEL:
                this.log.d("LanguageManager.onHandleMessage(MESSAGE_CLIENT_LANGUAGE_DOWNLOAD_CANCEL)");
                String string3 = message.getData().getString(Strings.DEFAULT_KEY);
                Transaction activeTransaction = getActiveTransaction(getTransactionName(string3));
                if (activeTransaction != null) {
                    activeTransaction.cancel();
                } else {
                    this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_LANGUAGE_DOWNLOAD_CANCEL_ACK, string3);
                }
                return true;
            case MESSAGE_CLIENT_INSTALLED_LANGUAGES:
                this.log.d("MESSAGE_CLIENT_INSTALLED_LANGUAGES");
                String string4 = message.getData().getString(Strings.DEFAULT_KEY);
                String string5 = message.getData().getString(Strings.PROP_LANGUAGE);
                String readString = this.client.getDataStore().readString(LANGUAGE_PREINSTALL_LIST_PREF, "");
                String readString2 = this.client.getDataStore().readString(LANGUAGE_SUPPORTED_LIST_PREF, "");
                if (string4 != null && !string4.equals(readString)) {
                    ArrayList arrayList = new ArrayList(Arrays.asList(string4.split(",")));
                    TreeSet treeSet = new TreeSet(Arrays.asList(readString.split(",")));
                    treeSet.remove("");
                    if (!treeSet.containsAll(arrayList)) {
                        Iterator it = arrayList.iterator();
                        while (it.hasNext()) {
                            treeSet.add((String) it.next());
                        }
                        String implode = StringUtils.implode(treeSet, ",");
                        this.client.getDataStore().saveString(LANGUAGE_PREINSTALL_LIST_PREF, implode);
                        readString = implode;
                        z = true;
                    }
                }
                if (string5 != null && !string5.equals(readString2)) {
                    ArrayList arrayList2 = new ArrayList(Arrays.asList(string5.split(",")));
                    TreeSet treeSet2 = new TreeSet(Arrays.asList(readString2.split(",")));
                    treeSet2.remove("");
                    if (!treeSet2.containsAll(arrayList2)) {
                        Iterator it2 = arrayList2.iterator();
                        while (it2.hasNext()) {
                            treeSet2.add((String) it2.next());
                        }
                        String implode2 = StringUtils.implode(treeSet2, ",");
                        this.client.getDataStore().saveString(LANGUAGE_SUPPORTED_LIST_PREF, implode2);
                        readString2 = implode2;
                        z = true;
                    }
                }
                if (z) {
                    this.client.postMessageDelayed(InternalMessages.MESSAGE_COMMAND_DEVICE_UPDATE, TimeConversion.MILLIS_IN_MINUTE);
                    this.log.d("List of preinstalled languages updated.  New existing list: ", readString, "; New supported list: ", readString2);
                    break;
                }
                break;
            default:
                return false;
        }
        return true;
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2, boolean z) {
        if (z) {
            this.log.d("onUpgrade() OEM ID has changed, deleting language list to be retrieved again.");
            this.languageInstallMetadata.clear();
        }
        markListDirty();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void rebind() {
        this.log.d("LanguageManager.rebind()");
        if (AbstractCommandManager.ManagerState.STARTED.equals(getManagerStartState())) {
            sendDownloadLanguagesStatus(true);
        }
    }

    public void requestLanguagesList(String str) {
        startTransaction(new LanguageListTransaction(str));
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void start() {
        this.managerStartState = AbstractCommandManager.ManagerState.STARTING;
        if (this.downloadListState != 0) {
            sendDownloadLanguagesStatus(true);
        }
        if (this.downloadListState == 0) {
            requestLanguagesList(null);
        } else {
            managerStartComplete();
        }
    }

    public void uninstallLanguage(String str) {
        this.log.d("uninstallLanguage(", str, ")");
        this.languageInstallMetadata.uninstallPackage(str);
        sendDownloadLanguagesStatus(false);
    }

    public void uninstallLanguages(String str) {
        this.log.d("uninstallLanguages(", str, ")");
        for (String str2 : str.split(",")) {
            this.languageInstallMetadata.uninstallPackage(str2);
        }
        sendDownloadLanguagesStatus(false);
    }

    public void unwind() {
        Iterator<String> it = this.languageInstallMetadata.listFromSteps(Arrays.asList(1, 2, 3, 4, 5)).iterator();
        while (it.hasNext()) {
            cleanUpFromFailedInstall(it.next());
        }
        sendDownloadLanguagesStatus(false);
    }
}
