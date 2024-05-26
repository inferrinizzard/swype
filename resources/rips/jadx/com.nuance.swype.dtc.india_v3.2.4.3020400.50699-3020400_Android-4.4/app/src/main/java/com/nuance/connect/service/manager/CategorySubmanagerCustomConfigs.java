package com.nuance.connect.service.manager;

import android.os.Bundle;
import android.os.Message;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.Transaction;
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
public class CategorySubmanagerCustomConfigs implements MessageProcessor, SubManager {
    private final CategoryDatabase categoryDatabase;
    private final ConnectClient client;
    private CategoryManager.CategorySubscribeDownloadTransaction downloadTransaction;
    private volatile boolean enabled;
    private final CategoryManager parent;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, CategorySubmanagerCustomConfigs.class.getSimpleName());
    private static final int[] MESSAGES_HANDLED = new int[0];
    private final List<Integer> typesSupported = Collections.unmodifiableList(Arrays.asList(7));
    private final List<String> categoriesManaged = new CopyOnWriteArrayList();

    /* renamed from: com.nuance.connect.service.manager.CategorySubmanagerCustomConfigs$2, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$nuance$connect$internal$common$InternalMessages = new int[InternalMessages.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CategorySubmanagerCustomConfigs(CategoryManager categoryManager, ConnectClient connectClient, boolean z) {
        this.enabled = true;
        this.parent = categoryManager;
        this.client = connectClient;
        this.categoryDatabase = categoryManager.categoryDatabase;
        Iterator<Integer> it = this.typesSupported.iterator();
        while (it.hasNext()) {
            this.categoriesManaged.addAll(this.categoryDatabase.allCategoryIDs(this.categoryDatabase.getTableForType(it.next().intValue())));
        }
        this.enabled = z;
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
        this.downloadTransaction = new CategoryManager.CategorySubscribeDownloadTransaction(str, this.parent) { // from class: com.nuance.connect.service.manager.CategorySubmanagerCustomConfigs.1
            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction, com.nuance.connect.comm.Transaction
            public Command.REQUEST_TYPE getRequestType() {
                return Command.REQUEST_TYPE.CRITICAL;
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onCancelAck() {
                CategorySubmanagerCustomConfigs.this.downloadTransaction = null;
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onDownloadStatus(int i) {
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onFailedTransaction(int i, String str2) {
                CategorySubmanagerCustomConfigs.this.downloadTransaction = null;
            }

            /* JADX WARN: Removed duplicated region for block: B:11:0x0033  */
            /* JADX WARN: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            void onSuccess(java.lang.String r10) {
                /*
                    Method dump skipped, instructions count: 328
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.service.manager.CategorySubmanagerCustomConfigs.AnonymousClass1.onSuccess(java.lang.String):void");
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onTransactionStarted() {
            }
        };
        return this.downloadTransaction;
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public int getManagerPollInterval() {
        return this.parent.client.getInteger(ConnectConfiguration.ConfigProperty.DEFAULT_POLLING_INTERVAL_NO_FEATURES).intValue();
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public int[] getMessageIDs() {
        return (int[]) MESSAGES_HANDLED.clone();
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public String getName() {
        return CategoryManager.SubManagerDefinition.SUBMANAGER_CUSTOM_CONFIG.name();
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
            long longProp = this.categoryDatabase.getLongProp(next, CategoryDatabase.LAST_UPDATE_FETCHED);
            long longProp2 = this.categoryDatabase.getLongProp(next, CategoryDatabase.LAST_UPDATE_AVAILABLE);
            if (longProp2 > longProp) {
                log.d("Custom Cloud Config update available: ", next, " lastFetched: ", Long.valueOf(longProp), " latestAvailable: ", Long.valueOf(longProp2));
                if ((this.parent.getManagerStartState().equals(AbstractCommandManager.ManagerState.STARTED) || this.parent.getManagerStartState().equals(AbstractCommandManager.ManagerState.STARTING)) && this.parent.getDownloadListState().equals(AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_AVAILABLE)) {
                    this.parent.subscribe(next);
                }
            }
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        int[] iArr = AnonymousClass2.$SwitchMap$com$nuance$connect$internal$common$InternalMessages;
        InternalMessages.fromInt(message.what).ordinal();
        return false;
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2) {
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public Map<String, String> parseJsonListResponse(JSONObject jSONObject) {
        log.d("processListResponse() -- TYPE_CUSTOM_CONFIG");
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
        if (this.categoriesManaged.size() > 0 && this.parent.getManagerStartState().equals(AbstractCommandManager.ManagerState.STARTED) && this.parent.getDownloadListState().equals(AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_AVAILABLE)) {
            Iterator<String> it = this.categoriesManaged.iterator();
            if (it.hasNext()) {
                String next = it.next();
                long longProp = this.categoryDatabase.getLongProp(next, CategoryDatabase.LAST_UPDATE_FETCHED);
                long longProp2 = this.categoryDatabase.getLongProp(next, CategoryDatabase.LAST_UPDATE_AVAILABLE);
                if (longProp2 > longProp) {
                    log.d("Custom Cloud Config update available: ", next, " lastFetched: ", Long.valueOf(longProp), " latestAvailable: ", Long.valueOf(longProp2));
                    if ((this.parent.getManagerStartState().equals(AbstractCommandManager.ManagerState.STARTED) || this.parent.getManagerStartState().equals(AbstractCommandManager.ManagerState.STARTING)) && this.parent.getDownloadListState().equals(AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_AVAILABLE)) {
                        this.parent.subscribe(next);
                    }
                }
            }
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public boolean unsubscribe(String str) {
        log.d("unsubscribe(", str, ")");
        return false;
    }
}
