package com.nuance.connect.service.manager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.util.SparseIntArray;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.comm.Response;
import com.nuance.connect.comm.Transaction;
import com.nuance.connect.common.Integers;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.service.manager.CategoryManager;
import com.nuance.connect.service.manager.interfaces.MessageProcessor;
import com.nuance.connect.service.manager.interfaces.SubManager;
import com.nuance.connect.sqlite.CategoryDatabase;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.FileUtils;
import com.nuance.connect.util.IntegerUtils;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import com.nuance.connect.util.VersionUtils;
import com.nuance.swype.input.IME;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"UseSparseArrays"})
/* loaded from: classes.dex */
public class CategorySubmanagerLivingLanguage implements MessageProcessor, SubManager {
    private static final String LIVINGLANGUAGE_CURRENTCOUNT_PREF = "LIVINGLANGUAGE_CURRENTCOUNT_PREF";
    private static final String LIVINGLANGUAGE_LAST_LANGUAGE_LIST = "LIVINGLANGUAGE_LAST_LANGUAGE_LIST";
    private static final String LIVINGLANGUAGE_LAST_LOCALE_COUNTRY = "LIVINGLANGUAGE_LAST_LOCALE_COUNTRY";
    private static final String LIVINGLANGUAGE_LAST_PROCESSED_PREF = "LIVINGLANGUAGE_LAST_PROCESSED_PREF";
    private static final String LIVINGLANGUAGE_LAST_UPDATED_PREF = "LIVINGLANGUAGE_LAST_UPDATED_PREF";
    private static final String LIVINGLANGUAGE_MAX_EVENTS_PREF = "LIVINGLANGUAGE_MAX_EVENTS_PREF";
    private static final int PROCESS_DELAY = 10000;
    private final CategoryDatabase categoryDatabase;
    private final ConnectClient client;
    private volatile boolean enabled;
    private String lastCountryCode;
    private int[] lastLanguageCodes;
    private long lastProcessed;
    private long lastUpdated;
    private final CategoryManager parent;
    private static final List<Integer> typesSupported = Collections.unmodifiableList(Arrays.asList(1, 3));
    private static final String LIVING_LANGUAGE_ENABLED_PREF = CategoryManager.MANAGER_NAME + "LivingLanguageEnabled";
    private static final int[] MESSAGES_HANDLED = {InternalMessages.MESSAGE_COMMAND_PROCESS_LIVING_LANGUAGE.ordinal(), InternalMessages.MESSAGE_CLIENT_CATEGORY_LIVING_LANGUAGE_REFRESH.ordinal(), InternalMessages.MESSAGE_CLIENT_CATEGORY_LIVING_LANGUAGE_CANCEL.ordinal(), InternalMessages.MESSAGE_CLIENT_CATEGORY_LIVING_LANGUAGE_FOREGROUND.ordinal(), InternalMessages.MESSAGE_CLIENT_SET_LIVING_LANGUAGE_MAX_EVENTS.ordinal(), InternalMessages.MESSAGE_CLIENT_PROCESS_CATEGORY_EVENTS_ACK.ordinal(), InternalMessages.MESSAGE_CLIENT_PROCESS_CATEGORY_DELETE_CATEGORY_ACK.ordinal(), InternalMessages.MESSAGE_CLIENT_CATEGORY_INSTALL.ordinal()};
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM);
    private final List<String> categoriesManaged = new CopyOnWriteArrayList();
    private int currentEventCount = 0;
    private int maxEvents = -1;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CategorySubmanagerLivingLanguage(CategoryManager categoryManager, ConnectClient connectClient, boolean z) {
        this.parent = categoryManager;
        this.client = connectClient;
        this.categoryDatabase = categoryManager.categoryDatabase;
        Iterator<Integer> it = typesSupported.iterator();
        while (it.hasNext()) {
            this.categoriesManaged.addAll(this.categoryDatabase.allCategoryIDs(this.categoryDatabase.getTableForType(it.next().intValue())));
        }
        this.enabled = connectClient.getDataStore().readBoolean(LIVING_LANGUAGE_ENABLED_PREF, z);
    }

    private void determinePurgeRequired(int i) {
        if (this.enabled) {
            int i2 = i + this.currentEventCount;
            int i3 = this.maxEvents;
            if (i2 > i3 && i3 != -1) {
                HashMap hashMap = new HashMap();
                for (Integer num : typesSupported) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(this.categoryDatabase.getTableForType(num.intValue()));
                    for (Map.Entry<String, Map<String, String>> entry : this.categoryDatabase.allWithProperty(CategoryDatabase.SUBSCRIBED, arrayList).entrySet()) {
                        if (Boolean.parseBoolean(entry.getValue().get(CategoryDatabase.SUBSCRIBED)) && this.categoryDatabase.getIntProp(entry.getKey(), Strings.CATEGORY_COUNT) > 0) {
                            hashMap.put(Integer.valueOf(this.categoryDatabase.getIntProp(entry.getKey(), CategoryDatabase.LAST_USED_AT)), entry.getKey());
                        }
                    }
                }
                Iterator it = new TreeSet(hashMap.keySet()).iterator();
                int i4 = i2;
                while (it.hasNext()) {
                    String str = (String) hashMap.get((Integer) it.next());
                    i4 -= this.categoryDatabase.getIntProp(str, Strings.CATEGORY_COUNT);
                    removeLivingLanguage(str);
                    if (i4 <= i3) {
                        break;
                    }
                }
            }
            savePreferences();
        }
    }

    private String findVariantLanguageCategory(int i, String str, int i2) {
        int parseInt;
        this.log.d("findVariantLanguageCategory(", str, ", ", Integer.valueOf(i2), ") for keyboardId 0x", Integer.toHexString(i));
        if (this.parent.getDownloadListState() == AbstractCommandManager.DownloadState.DOWNLOAD_LIST_STATE_NONE || this.categoryDatabase.isEmpty()) {
            this.log.d("findVariantLanguageCategory() - none exist. done.");
            return null;
        }
        if (!this.enabled) {
            this.log.d("findVariantLanguageCategory() - living language not enabled");
            return null;
        }
        HashMap hashMap = new HashMap();
        hashMap.put(MessageAPI.CATEGORY_ID, Integer.toString(i2));
        hashMap.put(MessageAPI.LANGUAGE_ID, Integer.toString(i));
        hashMap.put(MessageAPI.COUNTRY_LIST, null);
        String str2 = null;
        for (Map.Entry<String, Map<String, String>> entry : this.categoryDatabase.allWithPropertyMap(hashMap, Arrays.asList(this.categoryDatabase.getTableForType(3)), true).entrySet()) {
            String key = entry.getKey();
            String str3 = entry.getValue().get(MessageAPI.COUNTRY_LIST);
            String[] split = str3 != null ? str3.split(",") : null;
            if (split != null) {
                for (String str4 : split) {
                    if (str != null && str.contains(str4)) {
                        this.log.d("Found exact match database for variant: ", str, " category: ", key);
                        return key;
                    }
                }
            } else {
                if (str2 == null) {
                    this.log.d("Setting default category: ", key);
                } else {
                    key = str2;
                }
                str2 = key;
            }
        }
        hashMap.clear();
        hashMap.put(MessageAPI.CATEGORY_ID, Integer.toString(i2));
        hashMap.put(MessageAPI.LANGUAGE_ID, null);
        for (Map.Entry<String, Map<String, String>> entry2 : this.categoryDatabase.allWithPropertyMap(hashMap, Arrays.asList(this.categoryDatabase.getTableForType(1)), false).entrySet()) {
            String key2 = entry2.getKey();
            try {
                parseInt = Integer.parseInt(entry2.getValue().get(MessageAPI.LANGUAGE_ID));
            } catch (NumberFormatException e) {
            }
            if (parseInt == i) {
                this.log.d("Found TYPE_KEYBOARD_LANGUAGE_ONLY category: ", key2);
            } else if ((parseInt & 255) == 18 && (parseInt & 255) == (i & 255)) {
                this.log.d("Found Korean database with partial keyboard match 0x", Integer.toHexString(parseInt & 255), " : ", key2);
            }
            return key2;
        }
        return str2;
    }

    private SparseIntArray getUniqueCategoryIds() {
        SparseIntArray sparseIntArray = new SparseIntArray();
        for (Integer num : typesSupported) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.categoryDatabase.getTableForType(num.intValue()));
            Iterator<Map.Entry<String, Map<String, String>>> it = this.categoryDatabase.allWithProperty(MessageAPI.CATEGORY_ID, arrayList).entrySet().iterator();
            while (it.hasNext()) {
                try {
                    int parseInt = Integer.parseInt(it.next().getValue().get(MessageAPI.CATEGORY_ID));
                    if (sparseIntArray.indexOfKey(parseInt) < 0) {
                        sparseIntArray.put(parseInt, num.intValue());
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return sparseIntArray;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean install(String str) {
        int typeForID = this.parent.getTypeForID(str);
        int intProp = this.categoryDatabase.getIntProp(str, Strings.CATEGORY_COUNT);
        int intProp2 = this.categoryDatabase.getIntProp(str, CategoryDatabase.CATEGORY_COUNT_OLD);
        this.log.d("CategorySubmanagerLivingLanguage.install(", str, ") Count: [", Integer.valueOf(intProp), "]");
        if (!typesSupported.contains(Integer.valueOf(typeForID))) {
            return false;
        }
        if (intProp2 > 0) {
            this.log.d("CategorySubmanagerLivingLanguage.install(", str, ") -- this is an update, removing old count: ", Integer.valueOf(intProp2));
            this.currentEventCount -= intProp2;
            savePreferences();
        }
        determinePurgeRequired(intProp);
        this.currentEventCount += intProp;
        savePreferences();
        installCategory(str);
        return true;
    }

    private void installCategory(String str) {
        this.log.d("installCategory(", str, ")");
        if (!this.categoryDatabase.hasCategory(str)) {
            this.log.d("installCategory() - category list is not available (", str, ")");
            return;
        }
        int typeForID = this.parent.getTypeForID(str);
        if (this.categoryDatabase.getStep(str) != 5) {
            this.log.d("installCategory() - category list is not ready for install (", str, ")");
        } else if (!isSupported(typeForID)) {
            this.log.e("installCategory() - category type is not installable client-side (", str, ") type (", Integer.valueOf(typeForID), ")");
            return;
        }
        String prop = this.categoryDatabase.getProp(str, Strings.MAP_KEY_FILE_LOCATION);
        int coreForLanguage = this.client.getCoreForLanguage(this.categoryDatabase.getIntProp(str, MessageAPI.LANGUAGE_ID));
        this.categoryDatabase.setStep(str, 7);
        if (prop != null) {
            this.log.d("installCategory() - sending dlm events now: ", prop, " core: ", Integer.valueOf(coreForLanguage));
            Bundle bundle = new Bundle();
            bundle.putString(Strings.DLM_EVENT_FILE, prop);
            bundle.putInt(Strings.DLM_EVENT_CORE, coreForLanguage);
            bundle.putString(Strings.IDENTIFIER, str);
            bundle.putInt(Strings.DLM_EVENT_ACK, InternalMessages.MESSAGE_CLIENT_PROCESS_CATEGORY_EVENTS_ACK.ordinal());
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_PROCESS_DLM_EVENTS, bundle);
        }
    }

    private synchronized void processNextCategory(int i) {
        long j;
        long j2;
        this.log.d("LL.processNextCategory() - languageId: [", Integer.valueOf(i), "]");
        if (this.enabled) {
            if (this.parent.coresInUse.isEmpty()) {
                this.log.d("Current core not yet set.  Delay processing.");
            } else {
                SparseIntArray uniqueCategoryIds = getUniqueCategoryIds();
                ArrayList<String> arrayList = new ArrayList<>();
                ArrayList<String> arrayList2 = new ArrayList<>();
                for (int i2 = 0; i2 < uniqueCategoryIds.size(); i2++) {
                    String findVariantLanguageCategory = typesSupported.contains(Integer.valueOf(uniqueCategoryIds.valueAt(i2))) ? findVariantLanguageCategory(i, this.parent.currentLocale != null ? this.parent.currentLocale.getCountry() : null, uniqueCategoryIds.keyAt(i2)) : null;
                    if (findVariantLanguageCategory != null) {
                        Map<String, String> props = this.categoryDatabase.getProps(findVariantLanguageCategory);
                        if (props == null) {
                            this.log.e("Did not find category: " + findVariantLanguageCategory);
                        } else {
                            boolean parseBoolean = Boolean.parseBoolean(props.get(CategoryDatabase.SUBSCRIBED));
                            int i3 = Integers.STATUS_SUCCESS;
                            try {
                                i3 = Integer.parseInt(props.get(MessageAPI.LANGUAGE_ID));
                            } catch (NumberFormatException e) {
                            }
                            int typeForID = this.parent.getTypeForID(findVariantLanguageCategory);
                            boolean parseBoolean2 = Boolean.parseBoolean(props.get(CategoryDatabase.DELETE_CATEGORY));
                            if (this.parent.coresInUse.contains(Integer.valueOf(this.client.getCoreForLanguage(i3))) && parseBoolean2) {
                                sendDeleteCategoryToHost(findVariantLanguageCategory);
                                this.categoryDatabase.removeProp(findVariantLanguageCategory, CategoryDatabase.DELETE_CATEGORY);
                            } else {
                                if (parseBoolean) {
                                    long j3 = Long.MIN_VALUE;
                                    try {
                                        j3 = Long.parseLong(props.get(CategoryDatabase.LAST_UPDATE_FETCHED));
                                        j = Long.parseLong(props.get(CategoryDatabase.LAST_UPDATE_AVAILABLE));
                                        j2 = j3;
                                    } catch (NumberFormatException e2) {
                                        this.log.e("format error with fetched times");
                                        j = Long.MIN_VALUE;
                                        j2 = j3;
                                    }
                                    this.log.d("getNextCategory() -- key: [", findVariantLanguageCategory, "] lastFetched: [", Long.valueOf(j2), "] ", "lastAvailable: [", Long.valueOf(j), "]");
                                    if (j2 < j) {
                                        arrayList.add(findVariantLanguageCategory);
                                    }
                                } else if (this.enabled && !parseBoolean && typesSupported.contains(Integer.valueOf(typeForID))) {
                                    this.categoryDatabase.setProp(findVariantLanguageCategory, CategoryDatabase.FIRST_TIME_DOWNLOADED, true);
                                    arrayList2.add(findVariantLanguageCategory);
                                }
                                this.categoryDatabase.setProp(findVariantLanguageCategory, CategoryDatabase.LAST_USED_AT, System.currentTimeMillis());
                            }
                        }
                    }
                }
                if (arrayList2.size() > 0) {
                    this.parent.subscribeList(arrayList2);
                }
                if (arrayList.size() > 0 || arrayList2.size() > 0) {
                    this.parent.subscribeList(arrayList);
                    sendLivingLanguageUpdateStatus(true);
                }
            }
        }
    }

    private void removeLivingLanguage(String str) {
        this.log.d("removeLivingLanguage: ", str);
        int max = Math.max(0, this.categoryDatabase.getIntProp(str, Strings.CATEGORY_COUNT));
        this.categoryDatabase.setStep(str, 0);
        this.categoryDatabase.setProp(str, CategoryDatabase.LL_DELETE_CLEAR, true);
        this.categoryDatabase.removeProp(str, Strings.CATEGORY_COUNT);
        this.currentEventCount -= max;
        savePreferences();
        sendDeleteCategoryToHost(str);
    }

    private void resetCategoryDownloadState(String str) {
        this.log.d("resetCategoryDownloadState(", str, ")");
        this.categoryDatabase.setStep(str, 0);
        this.log.d("resetCategoryDownloadState(", str, ") -- ", Boolean.valueOf(FileUtils.deleteFile(this.categoryDatabase.getProp(str, Strings.MAP_KEY_FILE_LOCATION))));
        this.categoryDatabase.removeProp(str, Strings.MAP_KEY_FILE_LOCATION);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendDeleteCategoryToHost(String str) {
        if (this.categoriesManaged.contains(str)) {
            Bundle bundle = new Bundle();
            int intProp = this.categoryDatabase.getIntProp(str, MessageAPI.LANGUAGE_ID);
            int intProp2 = this.categoryDatabase.getIntProp(str, MessageAPI.CATEGORY_ID);
            bundle.putInt(Strings.DLM_EVENT_CORE, this.client.getCoreForLanguage(intProp));
            bundle.putInt(Strings.DLM_DELETE_CATEGORY, intProp2);
            bundle.putInt(Strings.DLM_DELETE_LANGUAGE, intProp);
            bundle.putString(Strings.IDENTIFIER, str);
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_PROCESS_DLM_DELETE_CATEGORY, bundle);
        }
    }

    private void sendLivingLanguageUpdateStatus(boolean z) {
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_NOTIFY_LIVING_LANGUAGE_UPDATE_STATUS, Boolean.valueOf(z));
    }

    private void sendUninstallInfo(String str) {
        Logger.getTrace().enterMethod("sendUninstallInfo");
        if (this.categoriesManaged.contains(str)) {
            Map<String, String> props = this.categoryDatabase.getProps(str);
            if (props == null) {
                this.log.e("Could not find category: " + str);
                return;
            }
            int typeForID = this.parent.getTypeForID(str);
            Bundle bundle = new Bundle();
            bundle.putInt(Strings.CATEGORY_TYPE, typeForID);
            bundle.putString(Strings.CATEGORY_UUID, str);
            try {
                bundle.putInt(Strings.CATEGORY_ID, Integer.parseInt(props.get(MessageAPI.CATEGORY_ID)));
                bundle.putInt(Strings.CATEGORY_LANGUAGE_ID, Integer.parseInt(props.get(MessageAPI.LANGUAGE_ID)));
            } catch (NumberFormatException e) {
            }
            bundle.putInt(Strings.CATEGORY_TYPE, typeForID);
            bundle.putString(Strings.CATEGORY_LOCALE, props.get(MessageAPI.LOCALE));
            bundle.putString(Strings.CATEGORY_COUNTRY, props.get(MessageAPI.COUNTRY_LIST));
            this.categoryDatabase.setProp(str, CategoryDatabase.FIRST_TIME_DOWNLOADED, true);
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_REMOVE_LIVING_LANGUAGE_INFO, bundle);
        }
        Logger.getTrace().enterMethod("sendUninstallInfo");
    }

    private boolean shouldProcessCategories() {
        boolean z = true;
        String country = this.parent.currentLocale != null ? this.parent.currentLocale.getCountry() : null;
        if ((country != null || this.lastCountryCode == null) && ((country == null || country.equals(this.lastCountryCode)) && IntegerUtils.arrayCompare(this.parent.currentLanguageCodes, this.lastLanguageCodes) && this.lastProcessed >= this.lastUpdated)) {
            z = false;
        }
        this.log.d("LL.shouldProcessCategories() -- " + z);
        return z;
    }

    private void updateCategoryDeleteAck(boolean z, int i, String str) {
        this.log.d("updateCategoryDeleteAck() - status: [", Boolean.valueOf(z), "] dlmCategory: [", Integer.valueOf(i), "] category: [", str, "]");
        if (!this.categoryDatabase.hasCategory(str)) {
            this.log.d("updateCategoryDeleteAck() - category list is not available (", str, ")");
            return;
        }
        if (this.categoryDatabase.getStep(str) == 0 && this.categoryDatabase.getBoolProp(str, CategoryDatabase.LL_DELETE_CLEAR)) {
            this.log.d("updateCategoryDeleteAck() - LL Clear occurring for: ", str);
            this.categoryDatabase.removeProp(str, CategoryDatabase.LL_DELETE_CLEAR);
            sendUninstallInfo(str);
            this.categoryDatabase.removeProp(str, Strings.MAP_KEY_FILE_LOCATION);
            this.categoryDatabase.setStep(str, 0);
        }
    }

    private void updateCategoryEventAck(boolean z, int i, String str, int i2) {
        this.log.d("updateCategoryEventAck() - status: [", Boolean.valueOf(z), "] core: [", Integer.valueOf(i), "] category: [", str, "]");
        if (!this.categoryDatabase.hasCategory(str)) {
            this.log.d("updateCategoryEventAck() - category list is not available (", str, ")");
            return;
        }
        if (this.categoryDatabase.getStep(str) != 7) {
            this.log.d("updateCategoryEventAck() - category list is not ready for install (", str, ")");
            resetCategoryDownloadState(str);
            return;
        }
        if (z) {
            FileUtils.deleteFile(this.categoryDatabase.getProp(str, Strings.MAP_KEY_FILE_LOCATION));
            sendInstallInfo(str, i2);
            this.categoryDatabase.removeProp(str, Strings.MAP_KEY_FILE_LOCATION);
            this.categoryDatabase.setStep(str, 0);
            return;
        }
        this.categoryDatabase.setStep(str, 5);
        Bundle bundle = new Bundle();
        bundle.putString(Strings.DEFAULT_KEY, str);
        Message obtainMessage = this.client.getHandler().obtainMessage(InternalMessages.MESSAGE_CLIENT_CATEGORY_INSTALL.ordinal());
        obtainMessage.setData(bundle);
        this.client.postMessageDelayed(obtainMessage, this.parent.calcDefaultMilliDelay());
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
        return new CategoryManager.CategorySubscribeDownloadTransaction(str, this.parent) { // from class: com.nuance.connect.service.manager.CategorySubmanagerLivingLanguage.2
            int typeId;

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction, com.nuance.connect.comm.Transaction
            public Command.REQUEST_TYPE getRequestType() {
                return Command.REQUEST_TYPE.BACKGROUND;
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onCancelAck() {
                if (CategorySubmanagerLivingLanguage.this.enabled) {
                    CategorySubmanagerLivingLanguage.this.categoryDatabase.setStep(this.catDb, 7);
                    CategorySubmanagerLivingLanguage.this.categoryDatabase.setProp(this.catDb, CategoryDatabase.LAST_UPDATE_FETCHED, System.currentTimeMillis());
                }
                CategorySubmanagerLivingLanguage.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_CATEGORY_DOWNLOAD_CANCEL_ACK, this.catDb);
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onDownloadStatus(int i) {
                if (!CategorySubmanagerLivingLanguage.this.isSupported(this.typeId)) {
                    CategorySubmanagerLivingLanguage.this.log.w("Not a living language type: ", Integer.valueOf(this.typeId), "; for category: ", this.catDb);
                    return;
                }
                Map<String, String> props = CategorySubmanagerLivingLanguage.this.categoryDatabase.getProps(this.catDb);
                if (props == null) {
                    CategorySubmanagerLivingLanguage.this.log.e("Did not find category: " + this.catDb);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt("download", i);
                bundle.putInt(Strings.MESSAGE_BUNDLE_TOTAL, 100);
                bundle.putString(Strings.MESSAGE_BUNDLE_DICTIONARY, this.catDb);
                bundle.putInt(Strings.CATEGORY_TYPE, this.typeId);
                try {
                    bundle.putInt(Strings.CATEGORY_ID, Integer.parseInt(props.get(MessageAPI.CATEGORY_ID)));
                    bundle.putInt(Strings.CATEGORY_LANGUAGE_ID, Integer.parseInt(props.get(MessageAPI.LANGUAGE_ID)));
                } catch (NumberFormatException e) {
                }
                bundle.putInt(Strings.CATEGORY_TYPE, this.typeId);
                bundle.putString(Strings.CATEGORY_LOCALE, props.get(MessageAPI.LOCALE));
                bundle.putString(Strings.CATEGORY_COUNTRY, props.get(MessageAPI.COUNTRY_LIST));
                CategorySubmanagerLivingLanguage.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_DICTIONARY_DOWNLOAD_PROGRESS, bundle);
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onFailedTransaction(int i, String str2) {
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onSuccess(String str2) {
                try {
                    int numberOfLines = FileUtils.getNumberOfLines(new File(str2));
                    String prop = CategorySubmanagerLivingLanguage.this.categoryDatabase.getProp(this.catDb, Strings.CATEGORY_COUNT);
                    int intValue = prop == null ? 0 : Integer.decode(prop).intValue();
                    if (intValue > 0) {
                        CategorySubmanagerLivingLanguage.this.categoryDatabase.setProp(this.catDb, CategoryDatabase.CATEGORY_COUNT_OLD, Integer.toString(intValue));
                    }
                    CategorySubmanagerLivingLanguage.this.categoryDatabase.setProp(this.catDb, Strings.CATEGORY_COUNT, Integer.toString(numberOfLines - 1));
                    CategorySubmanagerLivingLanguage.this.install(this.catDb);
                } catch (IOException e) {
                    CategorySubmanagerLivingLanguage.this.log.d("Error attempting to install a Living Language Database: ", e.getMessage());
                } catch (NumberFormatException e2) {
                    CategorySubmanagerLivingLanguage.this.log.d("Error attempting to install a Living Language Database: ", e2.getMessage());
                }
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.CategorySubscribeDownloadTransaction
            void onTransactionStarted() {
                this.typeId = CategorySubmanagerLivingLanguage.this.parent.getTypeForID(this.catDb);
                if (CategorySubmanagerLivingLanguage.this.isSupported(this.typeId)) {
                    return;
                }
                CategorySubmanagerLivingLanguage.this.log.w("Not a living language type: ", Integer.valueOf(this.typeId), "; for category: ", this.catDb);
            }
        };
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public int getManagerPollInterval() {
        return (this.enabled ? this.client.getInteger(ConnectConfiguration.ConfigProperty.POLL_INTERVAL_LIVING_LANGUAGE) : this.client.getInteger(ConnectConfiguration.ConfigProperty.DEFAULT_POLLING_INTERVAL_NO_FEATURES)).intValue();
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public int[] getMessageIDs() {
        return (int[]) MESSAGES_HANDLED.clone();
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public String getName() {
        return CategoryManager.SubManagerDefinition.SUBMANAGER_LIVING_LANGUAGE.name();
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public List<Integer> getTypesSupported() {
        return typesSupported;
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void init() {
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_GET_LIVING_LANGUAGE_MAX_EVENTS);
        loadPreferences();
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public boolean isEnabled() {
        return this.enabled;
    }

    boolean isSupported(int i) {
        return typesSupported.contains(Integer.valueOf(i));
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void languageUpdated(int[] iArr, Set<Integer> set) {
        processNextCategory();
    }

    void loadPreferences() {
        PersistentDataStore dataStore = this.client.getDataStore();
        this.currentEventCount = dataStore.readInt(LIVINGLANGUAGE_CURRENTCOUNT_PREF, 0);
        this.maxEvents = dataStore.readInt(LIVINGLANGUAGE_MAX_EVENTS_PREF, -1);
        this.lastUpdated = dataStore.readLong(LIVINGLANGUAGE_LAST_UPDATED_PREF, Long.MIN_VALUE);
        this.lastProcessed = dataStore.readLong(LIVINGLANGUAGE_LAST_PROCESSED_PREF, Long.MIN_VALUE);
        this.lastUpdated = this.lastUpdated <= System.currentTimeMillis() ? this.lastUpdated : System.currentTimeMillis();
        this.lastProcessed = this.lastProcessed <= System.currentTimeMillis() ? this.lastProcessed : System.currentTimeMillis();
        this.lastCountryCode = dataStore.readString(LIVINGLANGUAGE_LAST_LOCALE_COUNTRY, null);
        this.lastLanguageCodes = StringUtils.safeStringToIntArray(dataStore.readString(LIVINGLANGUAGE_LAST_LANGUAGE_LIST, ""));
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void localeUpdated(Locale locale) {
        processNextCategory();
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void onDataUpdated() {
        this.log.v("onDataUpdated()");
        this.categoriesManaged.clear();
        Iterator<Integer> it = typesSupported.iterator();
        while (it.hasNext()) {
            this.categoriesManaged.addAll(this.categoryDatabase.allCategoryIDs(this.categoryDatabase.getTableForType(it.next().intValue())));
        }
        this.lastUpdated = System.currentTimeMillis();
        savePreferences();
        processNextCategory();
    }

    @Override // com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_COMMAND_PROCESS_LIVING_LANGUAGE:
                this.log.d("MESSAGE_COMMAND_PROCESS_LIVING_LANGUAGE ");
                if (this.parent.currentLanguageCodes != null) {
                    if (shouldProcessCategories()) {
                        for (int i : this.parent.currentLanguageCodes) {
                            processNextCategory(i);
                        }
                        determinePurgeRequired(0);
                        this.lastProcessed = System.currentTimeMillis();
                        this.lastLanguageCodes = this.parent.currentLanguageCodes;
                        this.lastCountryCode = this.parent.currentCountry;
                        savePreferences();
                    } else {
                        this.log.d("processNextCategory() no changes requiring processing");
                    }
                }
                return true;
            case MESSAGE_CLIENT_CATEGORY_LIVING_LANGUAGE_REFRESH:
                this.log.d("MESSAGE_CLIENT_CATEGORY_LIVING_LANGUAGE_REFRESH");
                int i2 = message.getData().getInt(Strings.DEFAULT_KEY);
                for (String str : this.categoriesManaged) {
                    if (i2 == this.client.getCoreForLanguage(this.categoryDatabase.getIntProp(str, MessageAPI.LANGUAGE_ID))) {
                        this.categoryDatabase.removeProp(str, CategoryDatabase.LAST_UPDATE_FETCHED);
                    }
                }
                this.lastProcessed = Long.MIN_VALUE;
                processNextCategory();
                return true;
            case MESSAGE_CLIENT_CATEGORY_LIVING_LANGUAGE_CANCEL:
                this.log.d("MESSAGE_CLIENT_CATEGORY_LIVING_LANGUAGE_CANCEL");
                this.parent.cancelActiveTransactions(1);
                this.parent.cancelActiveTransactions(3);
                return true;
            case MESSAGE_CLIENT_SET_LIVING_LANGUAGE_MAX_EVENTS:
                int i3 = message.getData().getInt(Strings.DEFAULT_KEY);
                this.log.d("MASSAGE_CLIENT_SET_LIVING_LANGUAGE_MAX_EVENTS events: ", Integer.valueOf(i3));
                this.maxEvents = i3;
                savePreferences();
                determinePurgeRequired(0);
                return true;
            case MESSAGE_CLIENT_PROCESS_CATEGORY_EVENTS_ACK:
                updateCategoryEventAck(message.getData().getBoolean(Strings.DEFAULT_KEY), message.getData().getInt(Strings.DLM_EVENT_CORE), message.getData().getString(Strings.IDENTIFIER), message.getData().getInt(Strings.DLM_EVENT_COUNT));
                return true;
            case MESSAGE_CLIENT_PROCESS_CATEGORY_DELETE_CATEGORY_ACK:
                updateCategoryDeleteAck(message.getData().getBoolean(Strings.DEFAULT_KEY), message.getData().getInt(Strings.DLM_DELETE_CATEGORY), message.getData().getString(Strings.IDENTIFIER));
                return true;
            case MESSAGE_CLIENT_CATEGORY_INSTALL:
                installCategory(message.getData().getString(Strings.DEFAULT_KEY));
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
        HashMap hashMap = new HashMap();
        try {
            switch (jSONObject.getInt(MessageAPI.TYPE)) {
                case 1:
                    this.log.d("parseJsonListResponse() -- TYPE_KEYBOARD_LANGUAGE_ONLY");
                    hashMap.put(MessageAPI.LANGUAGE_ID, jSONObject.getString(MessageAPI.LANGUAGE_ID));
                    return hashMap;
                case 2:
                default:
                    this.log.e("Unsupported type: " + jSONObject.getInt(MessageAPI.TYPE));
                    return hashMap;
                case 3:
                    this.log.d("parseJsonListResponse() -- TYPE_KEYBOARD_PLUS_LANGUAGE_VARIANT");
                    this.log.d("-- " + jSONObject.toString());
                    hashMap.put(MessageAPI.LANGUAGE_ID, jSONObject.getString(MessageAPI.LANGUAGE_ID));
                    if (jSONObject.has(MessageAPI.LOCALE)) {
                        hashMap.put(MessageAPI.LOCALE, jSONObject.getString(MessageAPI.LOCALE));
                    }
                    if (!jSONObject.has(MessageAPI.COUNTRY_LIST) || jSONObject.getJSONArray(MessageAPI.COUNTRY_LIST) == null) {
                        return hashMap;
                    }
                    JSONArray jSONArray = jSONObject.getJSONArray(MessageAPI.COUNTRY_LIST);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < jSONArray.length(); i++) {
                        sb.append(jSONArray.getString(i));
                        sb.append(",");
                    }
                    hashMap.put(MessageAPI.COUNTRY_LIST, sb.toString());
                    return hashMap;
            }
        } catch (JSONException e) {
            this.log.e("Failure processing JSON object: ", e.getMessage());
            return null;
        }
    }

    synchronized void processNextCategory() {
        this.client.removeMessages(InternalMessages.MESSAGE_COMMAND_PROCESS_LIVING_LANGUAGE);
        this.client.postMessageDelayed(InternalMessages.MESSAGE_COMMAND_PROCESS_LIVING_LANGUAGE, IME.RETRY_DELAY_IN_MILLIS);
    }

    void savePreferences() {
        this.log.d("savePreferences() called");
        PersistentDataStore dataStore = this.client.getDataStore();
        dataStore.saveInt(LIVINGLANGUAGE_CURRENTCOUNT_PREF, this.currentEventCount);
        dataStore.saveInt(LIVINGLANGUAGE_MAX_EVENTS_PREF, this.maxEvents);
        dataStore.saveLong(LIVINGLANGUAGE_LAST_UPDATED_PREF, this.lastUpdated);
        dataStore.saveLong(LIVINGLANGUAGE_LAST_PROCESSED_PREF, this.lastProcessed);
        dataStore.saveString(LIVINGLANGUAGE_LAST_LANGUAGE_LIST, StringUtils.implode(this.lastLanguageCodes, ","));
        dataStore.saveString(LIVINGLANGUAGE_LAST_LOCALE_COUNTRY, this.lastCountryCode);
    }

    void sendInstallInfo(String str, int i) {
        Logger.getTrace().enterMethod("sendInstallInfo");
        if (this.categoriesManaged.contains(str)) {
            Map<String, String> props = this.categoryDatabase.getProps(str);
            if (props == null) {
                this.log.e("Could not find category: " + str);
                return;
            }
            int typeForID = this.parent.getTypeForID(str);
            Bundle bundle = new Bundle();
            bundle.putInt(Strings.CATEGORY_TYPE, typeForID);
            bundle.putInt(Strings.CATEGORY_COUNT, i);
            bundle.putString(Strings.CATEGORY_UUID, str);
            try {
                bundle.putInt(Strings.CATEGORY_ID, Integer.parseInt(props.get(MessageAPI.CATEGORY_ID)));
                bundle.putInt(Strings.CATEGORY_LANGUAGE_ID, Integer.parseInt(props.get(MessageAPI.LANGUAGE_ID)));
            } catch (NumberFormatException e) {
            }
            bundle.putInt(Strings.CATEGORY_TYPE, typeForID);
            bundle.putString(Strings.CATEGORY_LOCALE, props.get(MessageAPI.LOCALE));
            bundle.putString(Strings.CATEGORY_COUNTRY, props.get(MessageAPI.COUNTRY_LIST));
            if (this.categoryDatabase.getBoolProp(str, CategoryDatabase.FIRST_TIME_DOWNLOADED)) {
                this.categoryDatabase.removeProp(str, CategoryDatabase.FIRST_TIME_DOWNLOADED);
                this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ADD_LIVING_LANGUAGE_INFO, bundle);
            } else {
                this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_UPDATE_LIVING_LANGUAGE_INFO, bundle);
            }
        }
        Logger.getTrace().exitMethod("sendInstallInfo");
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void setEnabled(boolean z) {
        this.log.d("Updating ", getName(), " old status ", Boolean.valueOf(this.enabled), " new status: ", Boolean.valueOf(z));
        if (z == this.enabled) {
            return;
        }
        this.enabled = z;
        this.client.getDataStore().saveBoolean(LIVING_LANGUAGE_ENABLED_PREF, z);
        this.log.d("Updated ", getName(), " status to ", Boolean.valueOf(z));
        if (z) {
            this.oemLog.v("Enabling Living language");
            this.lastProcessed = Long.MIN_VALUE;
            savePreferences();
            processNextCategory();
            return;
        }
        this.oemLog.v("Disabling Living language");
        for (String str : this.categoriesManaged) {
            if (this.categoryDatabase.getBoolProp(str, CategoryDatabase.SUBSCRIBED)) {
                removeLivingLanguage(str);
            }
        }
        this.parent.unsubscribeAll();
        this.parent.cancelActiveTransactions(1);
        this.parent.cancelActiveTransactions(3);
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public void start() {
    }

    @Override // com.nuance.connect.service.manager.interfaces.SubManager
    public boolean unsubscribe(String str) {
        this.log.d("unsubscribe(", str, ")");
        if (!isSupported(this.parent.getTypeForID(str))) {
            return false;
        }
        if (this.parent.coresInUse.contains(Integer.valueOf(this.client.getCoreForLanguage(Integer.parseInt(this.categoryDatabase.getProp(str, MessageAPI.LANGUAGE_ID)))))) {
            sendDeleteCategoryToHost(str);
        } else {
            this.categoryDatabase.setProp(str, CategoryDatabase.DELETE_CATEGORY, true);
        }
        CategoryManager categoryManager = this.parent;
        categoryManager.getClass();
        this.parent.startTransaction(new CategoryManager.UnsubscribeTransaction(categoryManager, str) { // from class: com.nuance.connect.service.manager.CategorySubmanagerLivingLanguage.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(str);
                categoryManager.getClass();
            }

            @Override // com.nuance.connect.service.manager.CategoryManager.UnsubscribeTransaction
            protected void unsubscribeResponse(Response response) {
                CategorySubmanagerLivingLanguage.this.log.d("unsubscribeResponse()");
                super.unsubscribeResponse(response);
                if (1 == response.status) {
                    CategorySubmanagerLivingLanguage.this.sendDeleteCategoryToHost(this.id);
                }
            }
        });
        return true;
    }
}
