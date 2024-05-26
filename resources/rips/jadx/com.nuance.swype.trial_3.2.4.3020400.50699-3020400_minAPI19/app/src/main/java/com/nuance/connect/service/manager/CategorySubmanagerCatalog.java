package com.nuance.connect.service.manager;

import android.os.Bundle;
import android.os.Message;
import android.util.Pair;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.comm.Response;
import com.nuance.connect.comm.Transaction;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.service.manager.CategoryManager;
import com.nuance.connect.service.manager.interfaces.MessageProcessor;
import com.nuance.connect.service.manager.interfaces.SubManager;
import com.nuance.connect.sqlite.CategoryDatabase;
import com.nuance.connect.util.FileUtils;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.MapMarshal;
import com.nuance.connect.util.StringUtils;
import com.nuance.connect.util.VersionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class CategorySubmanagerCatalog implements MessageProcessor, SubManager {
    private static final Set<String> catalogDeviceProperties;
    private final CategoryDatabase categoryDatabase;
    private final ConnectClient client;
    private boolean enabled;
    private String lastGeoIPCountry;
    private final CategoryManager parent;
    private boolean sendConfigRequest;
    private static final List<Integer> typesSupported = Collections.unmodifiableList(Arrays.asList(6));
    private static final int[] MESSAGES_HANDLED = {InternalMessages.MESSAGE_CLIENT_CATALOG_PURCHASED_SKUS.ordinal(), InternalMessages.MESSAGE_COMMAND_CATALOG_LIST_REFRESH.ordinal()};
    private static final String CATALOG_WEIGHT_LIST_PREF = CategoryManager.MANAGER_NAME + "CatalogWeightList";
    private static final String CATALOG_ENABLED_PREF = CategoryManager.MANAGER_NAME + "CatalogEnabled";
    private static final String LOCALE_FOR_CATALOG = CategoryManager.MANAGER_NAME + "LocaleForCatalog";
    private AbstractCommandManager.ManagerState managerState = AbstractCommandManager.ManagerState.DISABLED;
    private long lastGeoIPTimeEpoc = Long.MIN_VALUE;
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM);
    private final List<String> categoriesManaged = new CopyOnWriteArrayList();
    private int minWaitBetweenCatalogListRefresh = 3600000;
    private final Property.StringValueListener stringListener = new Property.StringValueListener() { // from class: com.nuance.connect.service.manager.CategorySubmanagerCatalog.1
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<String> property) {
            if (!property.getKey().equals(ConnectConfiguration.ConfigProperty.LOCATION_GEO_IP_COUNTRY.name())) {
                if (property.getKey().equals(ConnectConfiguration.ConfigProperty.DEVICE_PROPERTIES_UPDATED.name())) {
                    List<String> stringToList = StringUtils.stringToList(property.getValue(), ",");
                    CategorySubmanagerCatalog.this.log.d("Detected device properties update: ", property.getValue());
                    if (Collections.disjoint(stringToList, CategorySubmanagerCatalog.catalogDeviceProperties)) {
                        return;
                    }
                    CategorySubmanagerCatalog.this.log.d("Important catalog device properties have updated. enabled: ", Boolean.valueOf(CategorySubmanagerCatalog.this.enabled));
                    CategorySubmanagerCatalog.this.postRefreshMessage(0L);
                    return;
                }
                return;
            }
            Pair<String, Long> lastGeoIP = CategorySubmanagerCatalog.this.client.getLastGeoIP();
            if (lastGeoIP != null) {
                Bundle bundle = new Bundle();
                bundle.putString("LOCATION_COUNTRY", (String) lastGeoIP.first);
                bundle.putLong(Strings.LOCATION_TIME_EPOC, ((Long) lastGeoIP.second).longValue());
                CategorySubmanagerCatalog.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATALOG_LOCATION_CHANGED, bundle);
                if (!((String) lastGeoIP.first).equals(CategorySubmanagerCatalog.this.lastGeoIPCountry)) {
                    CategorySubmanagerCatalog.this.postRefreshMessage(((Long) lastGeoIP.second).longValue() - CategorySubmanagerCatalog.this.lastGeoIPTimeEpoc < ((long) CategorySubmanagerCatalog.this.minWaitBetweenCatalogListRefresh) ? CategorySubmanagerCatalog.this.minWaitBetweenCatalogListRefresh : ((Long) lastGeoIP.second).longValue() - CategorySubmanagerCatalog.this.lastGeoIPTimeEpoc);
                }
                CategorySubmanagerCatalog.this.lastGeoIPCountry = (String) lastGeoIP.first;
                CategorySubmanagerCatalog.this.lastGeoIPTimeEpoc = ((Long) lastGeoIP.second).longValue();
            }
        }
    };
    private final Property.IntegerValueListener integerListener = new Property.IntegerValueListener() { // from class: com.nuance.connect.service.manager.CategorySubmanagerCatalog.2
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<Integer> property) {
            if (property.getKey().equals(ConnectConfiguration.ConfigProperty.CATALOG_LIST_REFRESH_MINIMUM_WAIT.name())) {
                CategorySubmanagerCatalog.this.minWaitBetweenCatalogListRefresh = property.getValue().intValue();
                CategorySubmanagerCatalog.this.log.d("Minimum wait for location change: ", Integer.valueOf(CategorySubmanagerCatalog.this.minWaitBetweenCatalogListRefresh));
            }
        }
    };

    /* loaded from: classes.dex */
    public enum Catalog_Type {
        KEYBOARD,
        BUNDLE
    }

    static {
        HashSet hashSet = new HashSet();
        hashSet.add("CUSTOMER_PAYMENT_PROCESSOR");
        hashSet.add("CUSTOMER_THEME_ENGINE");
        catalogDeviceProperties = Collections.unmodifiableSet(hashSet);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CategorySubmanagerCatalog(CategoryManager categoryManager, ConnectClient connectClient, boolean z) {
        this.parent = categoryManager;
        this.client = connectClient;
        this.categoryDatabase = categoryManager.categoryDatabase;
        this.categoriesManaged.clear();
        Iterator<Integer> it = typesSupported.iterator();
        while (it.hasNext()) {
            this.categoriesManaged.addAll(this.categoryDatabase.allCategoryIDs(this.categoryDatabase.getTableForType(it.next().intValue())));
        }
        connectClient.addListener(ConnectConfiguration.ConfigProperty.LOCATION_GEO_IP_COUNTRY, this.stringListener);
        connectClient.addListener(ConnectConfiguration.ConfigProperty.DEVICE_PROPERTIES_UPDATED, this.stringListener);
        connectClient.addListener(ConnectConfiguration.ConfigProperty.CATALOG_LIST_REFRESH_MINIMUM_WAIT, this.integerListener);
        this.enabled = connectClient.getDataStore().readBoolean(CATALOG_ENABLED_PREF, z);
        if (this.enabled) {
            this.sendConfigRequest = true;
        }
    }

    private void catalogCatDbReset() {
        this.log.v("catalogCatDbReset() enabled: " + this.enabled);
        if (this.enabled) {
            List asList = Arrays.asList(1, 2, 3, 4, 5);
            for (String str : this.categoriesManaged) {
                int step = this.categoryDatabase.getStep(str);
                if (asList.contains(Integer.valueOf(step)) && step <= 5) {
                    this.categoryDatabase.setStep(str, 1);
                    if (step < 5) {
                        FileUtils.deleteFile(this.categoryDatabase.getProp(str, Strings.MAP_KEY_FILE_LOCATION));
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void postRefreshMessage(long j) {
        if (this.client.getHandler().hasMessages(InternalMessages.MESSAGE_COMMAND_CATALOG_LIST_REFRESH.ordinal())) {
            this.client.getHandler().removeMessages(InternalMessages.MESSAGE_COMMAND_CATALOG_LIST_REFRESH.ordinal());
        }
        if (j > 0) {
            this.client.postMessageDelayed(InternalMessages.MESSAGE_COMMAND_CATALOG_LIST_REFRESH, j);
        } else {
            this.client.postMessage(InternalMessages.MESSAGE_COMMAND_CATALOG_LIST_REFRESH);
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
        final String prop = this.categoryDatabase.getProp(str, MessageAPI.SKU);
        return new CategoryManager.CategorySubscribeDownloadTransaction(str, this.parent) { // from class: com.nuance.connect.service.manager.CategorySubmanagerCatalog.4
            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction, com.nuance.connect.comm.Transaction
            public Command.REQUEST_TYPE getRequestType() {
                return Command.REQUEST_TYPE.USER;
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onCancelAck() {
                CategorySubmanagerCatalog.this.parent.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATEGORY_DOWNLOAD_CANCEL_ACK, this.catDb);
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onDownloadStatus(int i) {
                Bundle bundle = new Bundle();
                bundle.putInt("download", i);
                bundle.putInt(Strings.MESSAGE_BUNDLE_TOTAL, 100);
                bundle.putString(Strings.MESSAGE_BUNDLE_DICTIONARY, this.catDb);
                bundle.putInt(Strings.CATEGORY_TYPE, 6);
                bundle.putString(Strings.MESSAGE_BUNDLE_CATALOG_SKU, prop);
                CategorySubmanagerCatalog.this.parent.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATALOG_DOWNLOAD_PROGRESS, bundle);
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onFailedTransaction(int i, String str2) {
                Bundle bundle = new Bundle();
                bundle.putInt(Strings.DEFAULT_KEY, 0);
                bundle.putString(Strings.PROP_CATEGORY_ID, this.catDb);
                CategorySubmanagerCatalog.this.parent.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATEGORY_DOWNLOAD_FAILED, bundle);
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onSuccess(String str2) {
                CategorySubmanagerCatalog.this.install(this.catDb);
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onTransactionStarted() {
            }
        };
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public int getManagerPollInterval() {
        return (this.enabled ? this.client.getInteger(ConnectConfiguration.ConfigProperty.POLL_INTERVAL_CATALOG) : this.client.getInteger(ConnectConfiguration.ConfigProperty.DEFAULT_POLLING_INTERVAL_NO_FEATURES)).intValue();
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public int[] getMessageIDs() {
        return (int[]) MESSAGES_HANDLED.clone();
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public String getName() {
        return CategoryManager.SubManagerDefinition.SUBMANAGER_CATALOG.name();
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
        if (this.parent.getTypeForID(str) != 6) {
            return false;
        }
        String prop = this.categoryDatabase.getProp(str, Strings.MAP_KEY_FILE_LOCATION);
        String prop2 = this.categoryDatabase.getProp(str, MessageAPI.SKU);
        this.log.d("  Catalog Item - " + prop);
        Bundle bundle = new Bundle();
        bundle.putString(Strings.DEFAULT_KEY, str);
        bundle.putString(Strings.MESSAGE_BUNDLE_FILEPATH, prop);
        bundle.putString(Strings.MESSAGE_BUNDLE_CATALOG_SKU, prop2);
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATALOG_INSTALL_READY, bundle);
        return true;
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isSupported(int i) {
        return typesSupported.contains(Integer.valueOf(i));
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void languageUpdated(int[] iArr, Set<Integer> set) {
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void localeUpdated(Locale locale) {
        if (locale == null) {
            return;
        }
        String readString = this.client.getDataStore().readString(LOCALE_FOR_CATALOG, null);
        String locale2 = locale.toString();
        if (this.enabled && !locale2.equals(readString)) {
            this.log.d("Catalog Pending Fetch of Items with new locale of:" + locale2);
            this.parent.requestCategoryList((String) null, 6);
        }
        this.client.getDataStore().saveString(LOCALE_FOR_CATALOG, locale2);
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void onDataUpdated() {
        this.log.v("onDataUpdated()");
        this.categoriesManaged.clear();
        Iterator<Integer> it = typesSupported.iterator();
        while (it.hasNext()) {
            this.categoriesManaged.addAll(this.categoryDatabase.allCategoryIDs(this.categoryDatabase.getTableForType(it.next().intValue())));
        }
        HashMap hashMap = new HashMap();
        for (String str : this.categoriesManaged) {
            try {
                String prop = this.categoryDatabase.getProp(str, MessageAPI.SKU);
                if (prop != null) {
                    if (hashMap.containsKey(prop)) {
                        ((ArrayList) hashMap.get(prop)).add(str);
                    } else {
                        hashMap.put(prop, new ArrayList(Arrays.asList(str)));
                    }
                }
            } catch (NumberFormatException e) {
            }
        }
        for (String str2 : this.categoriesManaged) {
            String prop2 = this.categoryDatabase.getProp(str2, MessageAPI.BUNDLED_THEMES);
            boolean z = (prop2 == null || prop2.length() == 0) ? false : true;
            HashMap hashMap2 = new HashMap();
            if (z) {
                hashMap2.put(Strings.PROP_TYPE, Catalog_Type.BUNDLE.toString());
                HashSet hashSet = new HashSet();
                String[] split = prop2.split(",");
                if (split != null && split.length > 0) {
                    for (String str3 : split) {
                        List list = (List) hashMap.get(str3);
                        if (list != null) {
                            hashSet.addAll(list);
                        }
                    }
                }
                if (hashSet.size() > 0) {
                    hashMap2.put(Strings.PROP_BUNDLED_THEMES_CDB, StringUtils.implode(hashSet, ","));
                }
                hashMap2.put(Strings.PROP_BUNDLED_THEMES_SKUS, prop2);
            } else {
                hashMap2.put(Strings.PROP_TYPE, Catalog_Type.KEYBOARD.toString());
            }
            this.categoryDatabase.setProps(str2, hashMap2);
        }
        if (this.managerState == AbstractCommandManager.ManagerState.STARTED && this.categoriesManaged.size() > 0 && this.parent.getDownloadListState().equals(AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_AVAILABLE)) {
            catalogCatDbReset();
            sendCatalogItemsChanged(null);
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_CLIENT_CATALOG_PURCHASED_SKUS:
                this.log.d("MESSAGE_CLIENT_CATALOG_PURCHASED_SKUS ");
                String string = message.getData().getString(Strings.DEFAULT_KEY);
                if (string == null) {
                    string = "";
                }
                String string2 = this.client.getConfiguration().getString(ConnectConfiguration.ConfigProperty.CATALOG_SKU_LIST);
                this.log.d("new=[", string, "], old=[", string2, "]");
                ArrayList arrayList = new ArrayList(StringUtils.stringToList(string, ","));
                arrayList.removeAll(StringUtils.stringToList(string2, ","));
                if (!arrayList.isEmpty()) {
                    this.log.d("New purchased skus: ", arrayList);
                    Iterator<String> it = this.categoriesManaged.iterator();
                    while (it.hasNext()) {
                        arrayList.remove(this.categoryDatabase.getProp(it.next(), MessageAPI.SKU));
                    }
                    if (!arrayList.isEmpty()) {
                        this.log.d("Unknown skus: ", arrayList, "; fetching list...");
                        postRefreshMessage(3000L);
                    }
                }
                this.client.setProperty(ConnectConfiguration.ConfigProperty.CATALOG_SKU_LIST, string);
                this.client.postMessageDelayed(InternalMessages.MESSAGE_COMMAND_DEVICE_UPDATE, 1000L);
                return true;
            case MESSAGE_COMMAND_CATALOG_LIST_REFRESH:
                this.log.d("MESSAGE_COMMAND_CATALOG_LIST_REFRESH");
                if (this.enabled) {
                    this.parent.requestCategoryList((String) null, 6);
                }
                return true;
            default:
                return false;
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2) {
        if (VersionUtils.isDataCleanupRequiredOnUpgrade(version, version2) && this.enabled && this.client.getDataStore().readString(CATALOG_WEIGHT_LIST_PREF, "") != null) {
            try {
                new JSONObject(this.client.getDataStore().readString(CATALOG_WEIGHT_LIST_PREF, ""));
            } catch (JSONException e) {
                this.parent.requestCategoryList((String) null, 6);
            }
        }
        sendCatalogItemsChanged(null);
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public Map<String, String> parseJsonListResponse(JSONObject jSONObject) {
        this.log.d("parseJsonListResponse() -- TYPE_CATALOG");
        this.log.d("-- " + jSONObject.toString());
        HashMap hashMap = new HashMap();
        try {
            hashMap.put(MessageAPI.NAME, jSONObject.getString(MessageAPI.NAME));
            hashMap.put(MessageAPI.LOCALE, jSONObject.getString(MessageAPI.LOCALE));
            hashMap.put(MessageAPI.TITLE, jSONObject.getString(MessageAPI.TITLE));
            hashMap.put(MessageAPI.DESCRIPTION, jSONObject.getString(MessageAPI.DESCRIPTION));
            hashMap.put(MessageAPI.SHORT_DESCRIPTION, jSONObject.getString(MessageAPI.SHORT_DESCRIPTION));
            hashMap.put(MessageAPI.THUMBNAIL_URL, jSONObject.getString(MessageAPI.THUMBNAIL_URL));
            hashMap.put(MessageAPI.PURCHASABLE, jSONObject.getString(MessageAPI.PURCHASABLE));
            hashMap.put(MessageAPI.CREATION_TIMESTAMP, jSONObject.getString(MessageAPI.CREATION_TIMESTAMP));
            if (jSONObject.has(MessageAPI.START)) {
                hashMap.put(MessageAPI.START, jSONObject.getString(MessageAPI.START));
            }
            if (jSONObject.has(MessageAPI.END)) {
                hashMap.put(MessageAPI.END, jSONObject.getString(MessageAPI.END));
            }
            if (jSONObject.has(MessageAPI.FULFILL_UNTIL)) {
                hashMap.put(MessageAPI.FULFILL_UNTIL, jSONObject.getString(MessageAPI.FULFILL_UNTIL));
            }
            hashMap.put(MessageAPI.SKU, jSONObject.getString(MessageAPI.SKU));
            if (!jSONObject.has(MessageAPI.BUNDLED_THEMES) || jSONObject.getJSONArray(MessageAPI.BUNDLED_THEMES) == null) {
                hashMap.put(Strings.PROP_TYPE, Catalog_Type.KEYBOARD.toString());
            } else {
                JSONArray jSONArray = jSONObject.getJSONArray(MessageAPI.BUNDLED_THEMES);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < jSONArray.length(); i++) {
                    sb.append(jSONArray.getString(i));
                    sb.append(",");
                }
                hashMap.put(MessageAPI.BUNDLED_THEMES, sb.toString());
                hashMap.put(Strings.PROP_TYPE, Catalog_Type.BUNDLE.toString());
            }
            if (jSONObject.has(MessageAPI.LABELS)) {
                this.log.d("getLabel(): " + jSONObject.getString(MessageAPI.LABELS));
                JSONObject jSONObject2 = new JSONObject(jSONObject.getString(MessageAPI.LABELS));
                HashMap hashMap2 = new HashMap();
                this.log.d("getLabel(): " + jSONObject2.toString());
                Iterator<String> keys = jSONObject2.keys();
                while (keys.hasNext()) {
                    String next = keys.next();
                    hashMap2.put(next, jSONObject2.getString(next));
                }
                hashMap.put(MessageAPI.LABELS, MapMarshal.toString(hashMap2));
            }
            if (jSONObject.has(MessageAPI.MEDIA_URLS) && jSONObject.getJSONArray(MessageAPI.MEDIA_URLS) != null) {
                JSONArray jSONArray2 = jSONObject.getJSONArray(MessageAPI.MEDIA_URLS);
                StringBuilder sb2 = new StringBuilder();
                for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                    sb2.append(jSONArray2.getString(i2));
                    sb2.append(",");
                }
                hashMap.put(MessageAPI.MEDIA_URLS, sb2.toString());
            }
            if (jSONObject.has(MessageAPI.COUNTRIES_INCLUDED) && jSONObject.getJSONArray(MessageAPI.COUNTRIES_INCLUDED) != null) {
                JSONArray jSONArray3 = jSONObject.getJSONArray(MessageAPI.COUNTRIES_INCLUDED);
                StringBuilder sb3 = new StringBuilder();
                for (int i3 = 0; i3 < jSONArray3.length(); i3++) {
                    sb3.append(jSONArray3.getString(i3));
                    sb3.append(",");
                }
                hashMap.put(MessageAPI.COUNTRIES_INCLUDED, sb3.toString());
            }
            if (jSONObject.has(MessageAPI.COUNTRIES_EXCLUDED) && jSONObject.getJSONArray(MessageAPI.COUNTRIES_EXCLUDED) != null) {
                JSONArray jSONArray4 = jSONObject.getJSONArray(MessageAPI.COUNTRIES_EXCLUDED);
                StringBuilder sb4 = new StringBuilder();
                for (int i4 = 0; i4 < jSONArray4.length(); i4++) {
                    sb4.append(jSONArray4.getString(i4));
                    sb4.append(",");
                }
                hashMap.put(MessageAPI.COUNTRIES_EXCLUDED, sb4.toString());
            }
            return hashMap;
        } catch (Exception e) {
            this.log.e("Error in processing catalog item with error " + e.getMessage());
            return null;
        }
    }

    synchronized void processNextCategory(int i) {
    }

    void sendCatalogItemsChanged(List<String> list) {
        this.log.v("sendCatalogItemsChanged() enabled: " + this.enabled);
        Bundle bundle = new Bundle();
        bundle.putString(Strings.MESSAGE_BUNDLE_CATALOG_LIST, StringUtils.implode(list, ","));
        bundle.putString(Strings.MESSAGE_BUNDLE_CATALOG_CATEGORY_LIST, this.client.getDataStore().readString(CATALOG_WEIGHT_LIST_PREF, ""));
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ON_CATALOGS_CHANGED, bundle);
    }

    void sendIfCatalogDictionary(String str) {
        if (this.categoriesManaged.contains(str)) {
            sendCatalogItemsChanged(Arrays.asList(str));
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void setEnabled(boolean z) {
        if (z == this.enabled) {
            return;
        }
        this.enabled = z;
        this.client.getDataStore().saveBoolean(CATALOG_ENABLED_PREF, z);
        this.log.d("Updated ", getName(), " status to ", Boolean.valueOf(z));
        if (z && this.managerState == AbstractCommandManager.ManagerState.STARTED && this.categoriesManaged.size() > 0) {
            catalogCatDbReset();
            sendCatalogItemsChanged(null);
        }
    }

    public void setLabelList(JSONArray jSONArray) {
        this.log.d("setLabelList: ", jSONArray.toString());
        try {
            this.client.getDataStore().saveString(CATALOG_WEIGHT_LIST_PREF, MapMarshal.toString(MapMarshal.toStringMapFromJSON(jSONArray)));
        } catch (Exception e) {
            this.log.e("Error parsing label list from server, unable to provide categories for catalogs with list: " + jSONArray.toString());
        }
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void start() {
        Pair<String, Long> lastGeoIP = this.client.getLastGeoIP();
        if (lastGeoIP != null) {
            this.lastGeoIPCountry = (String) lastGeoIP.first;
            this.lastGeoIPTimeEpoc = ((Long) lastGeoIP.second).longValue();
        }
        if (this.sendConfigRequest) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_CATALOG_STATUS);
        }
        if (this.categoriesManaged.size() > 0 && this.parent.getDownloadListState().equals(AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_AVAILABLE)) {
            catalogCatDbReset();
        }
        this.managerState = AbstractCommandManager.ManagerState.STARTED;
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public boolean unsubscribe(String str) {
        this.log.d("unsubscribe(", str, ")");
        if (!typesSupported.contains(Integer.valueOf(this.parent.getTypeForID(str)))) {
            return false;
        }
        CategoryManager categoryManager = this.parent;
        categoryManager.getClass();
        this.parent.startTransaction(new CategoryManager.UnsubscribeTransaction(categoryManager, str) { // from class: com.nuance.connect.service.manager.CategorySubmanagerCatalog.3
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(str);
                categoryManager.getClass();
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.UnsubscribeTransaction
            protected void unsubscribeResponse(Response response) {
                CategorySubmanagerCatalog.this.log.d("unsubscribeResponse()");
                super.unsubscribeResponse(response);
                if (1 == response.status) {
                    CategorySubmanagerCatalog.this.sendIfCatalogDictionary(this.id);
                }
            }
        });
        return true;
    }
}
