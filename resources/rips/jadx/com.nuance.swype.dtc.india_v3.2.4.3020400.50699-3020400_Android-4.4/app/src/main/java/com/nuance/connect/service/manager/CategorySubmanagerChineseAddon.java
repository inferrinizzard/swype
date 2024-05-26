package com.nuance.connect.service.manager;

import android.os.Bundle;
import android.os.Message;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.comm.Response;
import com.nuance.connect.comm.Transaction;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.service.manager.CategoryManager;
import com.nuance.connect.service.manager.interfaces.MessageProcessor;
import com.nuance.connect.service.manager.interfaces.SubManager;
import com.nuance.connect.sqlite.CategoryDatabase;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.VersionUtils;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class CategorySubmanagerChineseAddon implements MessageProcessor, SubManager {
    private final CategoryDatabase categoryDatabase;
    private final ConnectClient client;
    private boolean enabled;
    private final CategoryManager parent;
    private boolean sendConfigRequest;
    private static final List<Integer> typesSupported = Collections.unmodifiableList(Arrays.asList(2));
    private static final int[] MESSAGES_HANDLED = new int[0];
    private static final String CHINESE_DICTIONARIES_ENABLED_PREF = CategoryManager.MANAGER_NAME + "ChineseDictionariesEnabled";
    private AbstractCommandManager.ManagerState managerState = AbstractCommandManager.ManagerState.DISABLED;
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM);
    private final List<String> categoriesManaged = new CopyOnWriteArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public CategorySubmanagerChineseAddon(CategoryManager categoryManager, ConnectClient connectClient, boolean z) {
        this.parent = categoryManager;
        this.client = connectClient;
        this.categoryDatabase = categoryManager.categoryDatabase;
        onDataUpdated();
        Boolean readBoolean = connectClient.getDataStore().readBoolean(CHINESE_DICTIONARIES_ENABLED_PREF, (Boolean) null);
        if (readBoolean == null) {
            this.sendConfigRequest = true;
        } else {
            this.enabled = readBoolean.booleanValue();
        }
    }

    private void chineseCatDbReset() {
        this.log.v("chineseCatDbReset() enabled: " + this.enabled);
        if (this.enabled) {
            List asList = Arrays.asList(1, 2, 3, 4, 5);
            for (String str : this.categoriesManaged) {
                int step = this.categoryDatabase.getStep(str);
                if (asList.contains(Integer.valueOf(step))) {
                    String prop = this.categoryDatabase.getProp(str, Strings.MAP_KEY_FILE_LOCATION);
                    boolean boolProp = this.categoryDatabase.getBoolProp(str, CategoryDatabase.SUBSCRIBED);
                    if (step >= 5) {
                        File file = prop != null ? new File(prop) : null;
                        if (file != null && file.exists()) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Strings.DEFAULT_KEY, str);
                            bundle.putString(Strings.MESSAGE_BUNDLE_FILEPATH, prop);
                            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_DICTIONARY_INSTALL_READY, bundle);
                        }
                        this.categoryDatabase.setStep(str, 1);
                    } else if (boolProp) {
                        this.categoryDatabase.setStep(str, 1);
                    } else {
                        this.categoryDatabase.setStep(str, 0);
                        this.parent.subscribe(str);
                    }
                }
            }
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void alarmNotification(String str, Bundle bundle) {
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public int categoriesManagedCount() {
        return this.categoriesManaged.size();
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public Transaction createSubscribeTransaction(String str) {
        return new CategoryManager.CategorySubscribeDownloadTransaction(str, this.parent) { // from class: com.nuance.connect.service.manager.CategorySubmanagerChineseAddon.2
            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction, com.nuance.connect.comm.Transaction
            public Command.REQUEST_TYPE getRequestType() {
                return Command.REQUEST_TYPE.USER;
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onCancelAck() {
                CategorySubmanagerChineseAddon.this.parent.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATEGORY_DOWNLOAD_CANCEL_ACK, this.catDb);
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onDownloadStatus(int i) {
                Bundle bundle = new Bundle();
                bundle.putInt("download", i);
                bundle.putInt(Strings.MESSAGE_BUNDLE_TOTAL, 100);
                bundle.putString(Strings.MESSAGE_BUNDLE_DICTIONARY, this.catDb);
                bundle.putInt(Strings.CATEGORY_TYPE, 2);
                CategorySubmanagerChineseAddon.this.parent.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_DICTIONARY_DOWNLOAD_PROGRESS, bundle);
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onFailedTransaction(int i, String str2) {
                Bundle bundle = new Bundle();
                bundle.putInt(Strings.DEFAULT_KEY, 0);
                bundle.putString(Strings.PROP_CATEGORY_ID, this.catDb);
                CategorySubmanagerChineseAddon.this.parent.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATEGORY_DOWNLOAD_FAILED, bundle);
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onSuccess(String str2) {
                CategorySubmanagerChineseAddon.this.install(this.catDb);
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onTransactionStarted() {
            }
        };
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public int getManagerPollInterval() {
        return (this.enabled ? this.client.getInteger(ConnectConfiguration.ConfigProperty.POLL_INTERVAL_CHINESE_DATABASE) : this.client.getInteger(ConnectConfiguration.ConfigProperty.DEFAULT_POLLING_INTERVAL_NO_FEATURES)).intValue();
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public int[] getMessageIDs() {
        return (int[]) MESSAGES_HANDLED.clone();
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public String getName() {
        return CategoryManager.SubManagerDefinition.SUBMANAGER_CHINESE_DATABASES.name();
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public List<Integer> getTypesSupported() {
        return typesSupported;
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void init() {
        this.log.v("init()");
    }

    boolean install(String str) {
        if (this.parent.getTypeForID(str) != 2) {
            return false;
        }
        String prop = this.categoryDatabase.getProp(str, Strings.MAP_KEY_FILE_LOCATION);
        this.log.d("  Chinese Addon Dictionary - " + prop);
        Bundle bundle = new Bundle();
        bundle.putString(Strings.DEFAULT_KEY, str);
        bundle.putString(Strings.MESSAGE_BUNDLE_FILEPATH, prop);
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_DICTIONARY_INSTALL_READY, bundle);
        return true;
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void languageUpdated(int[] iArr, Set<Integer> set) {
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void localeUpdated(Locale locale) {
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void onDataUpdated() {
        this.log.v("onDataUpdated()");
        this.categoriesManaged.clear();
        Iterator<Integer> it = typesSupported.iterator();
        while (it.hasNext()) {
            this.categoriesManaged.addAll(this.categoryDatabase.allCategoryIDs(this.categoryDatabase.getTableForType(it.next().intValue())));
        }
        if (this.managerState == AbstractCommandManager.ManagerState.STARTED && this.parent.getDownloadListState().equals(AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_AVAILABLE)) {
            chineseCatDbReset();
            sendChineseAddonDictionaries();
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        int i = message.what;
        return false;
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2) {
        if (version == null || version2 == null || version.getMajor() >= 5 || version2.getMajor() < 5) {
            return;
        }
        this.parent.requestCategoryList((String) null, 2);
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public Map<String, String> parseJsonListResponse(JSONObject jSONObject) {
        HashMap hashMap = new HashMap();
        this.log.d("parseJsonListResponse() -- TYPE_CHINESE_ADDON_DICTIONARY");
        this.log.d("-- " + jSONObject.toString());
        try {
            hashMap.put(MessageAPI.NAME, jSONObject.getString(MessageAPI.NAME));
            hashMap.put(MessageAPI.LANGUAGE_ID, jSONObject.getString(MessageAPI.LANGUAGE_ID));
            hashMap.put(MessageAPI.LOCALE, jSONObject.getString(MessageAPI.LOCALE));
            hashMap.put(MessageAPI.DESCRIPTION, jSONObject.getString(MessageAPI.DESCRIPTION));
            hashMap.put(MessageAPI.RANK, jSONObject.getString(MessageAPI.RANK));
            hashMap.put(MessageAPI.NAME_TRANSLATED, jSONObject.getString(MessageAPI.NAME_TRANSLATED));
            hashMap.put(MessageAPI.DESCRIPTION_TRANSLATED, jSONObject.getString(MessageAPI.DESCRIPTION_TRANSLATED));
            return hashMap;
        } catch (JSONException e) {
            this.log.e("Failure processing JSON object: ", e.getMessage());
            return null;
        }
    }

    synchronized void processNextCategory(int i) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendChineseAddonDictionaries() {
        this.log.v("sendChineseAddonDictionaries() enabled: " + this.enabled);
        if (this.enabled) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ON_DICTIONARIES_UPDATED);
        }
    }

    void sendIfChineseAddonDictionary(String str) {
        if (this.categoriesManaged.contains(str)) {
            sendChineseAddonDictionaries();
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void setEnabled(boolean z) {
        if (z == this.enabled) {
            return;
        }
        this.enabled = z;
        this.client.getDataStore().saveBoolean(CHINESE_DICTIONARIES_ENABLED_PREF, z);
        this.log.d("Updated ", getName(), " status to ", Boolean.valueOf(z));
        if (z && this.managerState == AbstractCommandManager.ManagerState.STARTED && this.parent.getDownloadListState().equals(AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_AVAILABLE)) {
            chineseCatDbReset();
            sendChineseAddonDictionaries();
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void start() {
        if (this.sendConfigRequest) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_CHINESE_CAT_DB_STATUS);
        }
        if (this.parent.getDownloadListState().equals(AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_AVAILABLE)) {
            chineseCatDbReset();
            sendChineseAddonDictionaries();
        }
        this.managerState = AbstractCommandManager.ManagerState.STARTED;
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public boolean unsubscribe(String str) {
        Logger.getTrace().enterMethod("unsubscribe_CategorySubmanagerChineseAddon");
        this.log.d("unsubscribe(", str, ")");
        if (!typesSupported.contains(Integer.valueOf(this.parent.getTypeForID(str)))) {
            Logger.getTrace().exitMethod("unsubscribe_CategorySubmanagerChineseAddon");
            return false;
        }
        sendChineseAddonDictionaries();
        CategoryManager categoryManager = this.parent;
        categoryManager.getClass();
        this.parent.startTransaction(new CategoryManager.UnsubscribeTransaction(categoryManager, str) { // from class: com.nuance.connect.service.manager.CategorySubmanagerChineseAddon.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(str);
                categoryManager.getClass();
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.UnsubscribeTransaction
            protected void unsubscribeResponse(Response response) {
                CategorySubmanagerChineseAddon.this.log.d("unsubscribeResponse()");
                super.unsubscribeResponse(response);
                if (1 == response.status) {
                    CategorySubmanagerChineseAddon.this.sendIfChineseAddonDictionary(this.id);
                }
            }
        });
        Logger.getTrace().exitMethod("unsubscribe_CategorySubmanagerChineseAddon");
        return true;
    }
}
