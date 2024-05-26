package com.nuance.connect.service.manager;

import android.os.Bundle;
import android.os.Message;
import com.nuance.connect.comm.Command;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class CategorySubmanagerPlatformUpdate implements MessageProcessor, SubManager {
    private final CategoryDatabase categoryDatabase;
    private CategoryManager.CategorySubscribeDownloadTransaction downloadTransaction;
    private volatile boolean enabled;
    private final CategoryManager parent;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, CategorySubmanagerPlatformUpdate.class.getSimpleName());
    private static final int[] MESSAGES_HANDLED = {InternalMessages.MESSAGE_CLIENT_CATEGORY_PLATFORM_UPDATE_DOWNLOAD.ordinal(), InternalMessages.MESSAGE_CLIENT_CATEGORY_PLATFORM_UPDATE_CANCEL.ordinal()};
    private final List<Integer> typesSupported = Collections.unmodifiableList(Arrays.asList(5));
    private final List<String> categoriesManaged = new CopyOnWriteArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public CategorySubmanagerPlatformUpdate(CategoryManager categoryManager, ConnectClient connectClient, boolean z) {
        this.enabled = false;
        this.parent = categoryManager;
        this.categoryDatabase = categoryManager.categoryDatabase;
        this.enabled = connectClient.getBoolean(ConnectConfiguration.ConfigProperty.PLATFORM_UPDATE_ENABLED) != null ? connectClient.getBoolean(ConnectConfiguration.ConfigProperty.PLATFORM_UPDATE_ENABLED).booleanValue() : z;
        Iterator<Integer> it = this.typesSupported.iterator();
        while (it.hasNext()) {
            this.categoriesManaged.addAll(this.categoryDatabase.allCategoryIDs(this.categoryDatabase.getTableForType(it.next().intValue())));
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
        if (this.downloadTransaction != null) {
            return this.downloadTransaction;
        }
        this.downloadTransaction = new CategoryManager.CategorySubscribeDownloadTransaction(str, this.parent) { // from class: com.nuance.connect.service.manager.CategorySubmanagerPlatformUpdate.1
            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction, com.nuance.connect.comm.Transaction
            public Command.REQUEST_TYPE getRequestType() {
                return Command.REQUEST_TYPE.USER;
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onCancelAck() {
                CategorySubmanagerPlatformUpdate.this.downloadTransaction = null;
                CategorySubmanagerPlatformUpdate.this.parent.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_CANCEL_ACK);
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onDownloadStatus(int i) {
                CategorySubmanagerPlatformUpdate.this.parent.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_PROGRESS, Integer.valueOf(i));
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onFailedTransaction(int i, String str2) {
                CategorySubmanagerPlatformUpdate.this.downloadTransaction = null;
                CategorySubmanagerPlatformUpdate.this.parent.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_DOWNLOAD_FAILED, Integer.valueOf(i));
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onSuccess(String str2) {
                CategorySubmanagerPlatformUpdate.this.downloadTransaction = null;
                CategorySubmanagerPlatformUpdate.this.parent.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_INSTALL_READY, str2);
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onTransactionStarted() {
                CategorySubmanagerPlatformUpdate.this.parent.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_PROGRESS, (Object) 0);
            }
        };
        return this.downloadTransaction;
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public int getManagerPollInterval() {
        return (this.enabled ? this.parent.client.getInteger(ConnectConfiguration.ConfigProperty.POLL_INTERVAL_PLATFORM_UPDATE) : this.parent.client.getInteger(ConnectConfiguration.ConfigProperty.DEFAULT_POLLING_INTERVAL_NO_FEATURES)).intValue();
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public int[] getMessageIDs() {
        return (int[]) MESSAGES_HANDLED.clone();
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public String getName() {
        return CategoryManager.SubManagerDefinition.SUBMANAGER_UPDATES.name();
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public List<Integer> getTypesSupported() {
        return this.typesSupported;
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void init() {
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
        log.v("onDataUpdated()");
        this.categoriesManaged.clear();
        Iterator<Integer> it = this.typesSupported.iterator();
        while (it.hasNext()) {
            this.categoriesManaged.addAll(this.categoryDatabase.allCategoryIDs(this.categoryDatabase.getTableForType(it.next().intValue())));
        }
        Iterator<String> it2 = this.categoriesManaged.iterator();
        if (it2.hasNext()) {
            String next = it2.next();
            log.d("Platform Update available: ", next, " lastFetched: ", Long.valueOf(this.categoryDatabase.getLongProp(next, CategoryDatabase.LAST_UPDATE_FETCHED)), " latestAvailable: ", Long.valueOf(this.categoryDatabase.getLongProp(next, CategoryDatabase.LAST_UPDATE_AVAILABLE)));
            if ((this.parent.getManagerStartState().equals(AbstractCommandManager.ManagerState.STARTED) || this.parent.getManagerStartState().equals(AbstractCommandManager.ManagerState.STARTING)) && this.parent.getDownloadListState().equals(AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_AVAILABLE)) {
                this.parent.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_AVAILABLE, next);
            }
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_CLIENT_CATEGORY_PLATFORM_UPDATE_DOWNLOAD:
                String string = message.getData().getString(Strings.DEFAULT_KEY);
                log.d("MESSAGE_CLIENT_CATEGORY_PLATFORM_UPDATE_DOWNLOAD ", string);
                if (string == null) {
                    return true;
                }
                this.parent.subscribe(string);
                return true;
            case MESSAGE_CLIENT_CATEGORY_PLATFORM_UPDATE_CANCEL:
                log.d("MESSAGE_CLIENT_CATEGORY_PLATFORM_UPDATE_CANCEL");
                if (this.downloadTransaction == null) {
                    return true;
                }
                this.downloadTransaction.cancel();
                return true;
            default:
                return false;
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2) {
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public Map<String, String> parseJsonListResponse(JSONObject jSONObject) {
        log.d("processListResponse() -- TYPE_UPDATES");
        return Collections.EMPTY_MAP;
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void setEnabled(boolean z) {
        if (z == this.enabled) {
            return;
        }
        this.enabled = z;
        log.d("Updated ", getName(), " status to ", Boolean.valueOf(z));
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void start() {
        if (this.categoriesManaged.size() <= 0 || !this.parent.getDownloadListState().equals(AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_AVAILABLE)) {
            return;
        }
        this.parent.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_AVAILABLE, this.categoriesManaged.get(0));
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public boolean unsubscribe(String str) {
        log.d("unsubscribe(", str, ")");
        return false;
    }
}
