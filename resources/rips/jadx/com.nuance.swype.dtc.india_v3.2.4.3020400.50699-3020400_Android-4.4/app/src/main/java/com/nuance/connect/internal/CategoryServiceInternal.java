package com.nuance.connect.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.nuance.connect.api.AddonDictionariesService;
import com.nuance.connect.api.CatalogService;
import com.nuance.connect.api.LivingLanguageService;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.common.ActionFilterStrings;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.Integers;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.APIHandlers;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.sqlite.CategoryDatabase;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.HandlerThread;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.MapMarshal;
import com.nuance.connect.util.StringUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class CategoryServiceInternal extends AbstractService implements AddonDictionariesService, CatalogService, LivingLanguageService {
    private static final String CATALOG_LABELS_PREF = "CATEGORY_SERVICE_CATALOG_LABELS";
    private static final int SEND_DELAY = 15000;
    private static final int SEND_DELAY_LIMIT = 3000;
    private static final int SEND_DELAY_ONE_SECOND = 1000;
    private static final int SEND_DELAY_SHORT = 10;
    private boolean catalogListReceived;
    private boolean catalogStatus;
    private final ConnectServiceManagerInternal connectService;
    private final CategoryDatabase database;
    private boolean dictionaryListReceived;
    private final CategoryHandlerThread handlerThread;
    private boolean livingLanguageStatus;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, "CategoryService");
    private static final int[] MESSAGE_IDS = {InternalMessages.MESSAGE_HOST_CATEGORY_DOWNLOAD_CANCEL_ACK.ordinal(), InternalMessages.MESSAGE_HOST_CATEGORY_DOWNLOAD_FAILED.ordinal(), InternalMessages.MESSAGE_HOST_ON_DICTIONARIES_UPDATED.ordinal(), InternalMessages.MESSAGE_HOST_DICTIONARY_INSTALL_READY.ordinal(), InternalMessages.MESSAGE_HOST_DICTIONARY_DOWNLOAD_PROGRESS.ordinal(), InternalMessages.MESSAGE_HOST_DICTIONARY_UNINSTALL.ordinal(), InternalMessages.MESSAGE_HOST_GET_CHINESE_CAT_DB_STATUS.ordinal(), InternalMessages.MESSAGE_HOST_GET_LIVING_LANGUAGE_STATUS.ordinal(), InternalMessages.MESSAGE_HOST_ADD_LIVING_LANGUAGE_INFO.ordinal(), InternalMessages.MESSAGE_HOST_UPDATE_LIVING_LANGUAGE_INFO.ordinal(), InternalMessages.MESSAGE_HOST_REMOVE_LIVING_LANGUAGE_INFO.ordinal(), InternalMessages.MESSAGE_HOST_NOTIFY_LIVING_LANGUAGE_UPDATE_STATUS.ordinal(), InternalMessages.MESSAGE_HOST_GET_LIVING_LANGUAGE_MAX_EVENTS.ordinal(), InternalMessages.MESSAGE_HOST_CATALOG_DOWNLOAD_PROGRESS.ordinal(), InternalMessages.MESSAGE_HOST_CATALOG_INSTALL_READY.ordinal(), InternalMessages.MESSAGE_HOST_GET_CATALOG_STATUS.ordinal(), InternalMessages.MESSAGE_HOST_ON_CATALOGS_CHANGED.ordinal(), InternalMessages.MESSAGE_HOST_CATALOG_LOCATION_CHANGED.ordinal()};
    private final Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM);
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean livingLanguageUDAStatus = true;
    private boolean livingLanguageHotwordsStatus = true;
    private boolean livingLanguageAvailable = true;
    private final ConcurrentCallbackSet<LivingLanguageService.Callback> livingLanguageCallbacks = new ConcurrentCallbackSet<>();
    private final Runnable sendHotwordsStatus = new Runnable() { // from class: com.nuance.connect.internal.CategoryServiceInternal.1
        @Override // java.lang.Runnable
        public void run() {
            CategoryServiceInternal.this.connectService.getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_CATEGORY_HOTWORD_STATUS, Boolean.valueOf(CategoryServiceInternal.this.livingLanguageStatus));
        }
    };
    private final Runnable sendMaxLimit = new Runnable() { // from class: com.nuance.connect.internal.CategoryServiceInternal.2
        @Override // java.lang.Runnable
        public void run() {
            CategoryServiceInternal.this.connectService.getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_LIVING_LANGUAGE_MAX_EVENTS, Integer.valueOf(CategoryServiceInternal.this.connectService.getUserSettings().getMaxNumberOfEvents()));
        }
    };
    private final Map<String, AddonDictionaryImpl> availableDownloadDictionaries = new ConcurrentHashMap();
    private final ConcurrentCallbackSet<AddonDictionariesService.AddonDictionaryListCallback> dictionaryListCallbacks = new ConcurrentCallbackSet<>();
    private final Map<String, AddonDictionariesService.AddonDictionaryDownloadCallback> dictionaryDownloadCallbacks = new HashMap();
    private final Map<String, AddonDictionariesService.AddonDictionaryDownloadCallback> redownloadCallbacks = new HashMap();
    private int changedCatalogFlagCount = 1;
    private final ReadWriteLock catalogLock = new ReentrantReadWriteLock();
    private final Set<String> catalogAllPurchasedSkuSet = new HashSet();
    private final Set<String> catalogAvailableSkuSet = new HashSet();
    private final Set<String> catalogSKUsChanged = new HashSet();
    private final Map<String, CatalogService.CatalogItem.Type> catalogSkuForPurchaseMap = new HashMap();
    private final Map<String, CatalogService.CatalogItem.Type> catalogAvailableLabelMap = new HashMap();
    private final ConcurrentCallbackSet<CatalogService.CatalogCallback> catalogListCallbacks = new ConcurrentCallbackSet<>();
    private final Map<String, CatalogService.CatalogItemDownloadCallback> catalogDownloadCallbacks = new HashMap();
    private final Map<String, CatalogService.CatalogItemDownloadCallback> catalogRedownloadCallbacks = new HashMap();
    private final Map<String, String> catalogSkuPriceList = new HashMap();
    private final Set<String> catalogPurchasedSkuList = new HashSet();
    private final Map<String, String> catalogLabelList = new LinkedHashMap();
    private final List<String> catalogSKUListAvailableCheck = new ArrayList();
    private final ConcurrentHashMap<String, CatalogItemImpl> lazyLoadCatalogItems = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CatalogItemImpl> skuToCatalogItems = new ConcurrentHashMap<>();
    private final Runnable sendCatalogStatus = new Runnable() { // from class: com.nuance.connect.internal.CategoryServiceInternal.3
        @Override // java.lang.Runnable
        public void run() {
            CategoryServiceInternal.log.d("sendCatalogStatus=", Boolean.valueOf(CategoryServiceInternal.this.catalogStatus));
            CategoryServiceInternal.this.connectService.getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_SET_CATALOG_STATUS, Boolean.valueOf(CategoryServiceInternal.this.catalogStatus));
        }
    };
    private final Runnable sendCatalogItemsChangedCallback = new Runnable() { // from class: com.nuance.connect.internal.CategoryServiceInternal.4
        @Override // java.lang.Runnable
        public void run() {
            for (CatalogService.CatalogCallback catalogCallback : (CatalogService.CatalogCallback[]) CategoryServiceInternal.this.catalogListCallbacks.toArray(new CatalogService.CatalogCallback[0])) {
                catalogCallback.catalogItemListChanged();
            }
        }
    };
    private final Runnable sendCatalogPurchasedSKUs = new Runnable() { // from class: com.nuance.connect.internal.CategoryServiceInternal.5
        @Override // java.lang.Runnable
        public void run() {
            synchronized (CategoryServiceInternal.this.catalogPurchasedSkuList) {
                if (CategoryServiceInternal.this.catalogPurchasedSkuList.size() > 0) {
                    CategoryServiceInternal.this.catalogPurchasedSkuList.remove("");
                    CategoryServiceInternal.this.catalogPurchasedSkuList.remove(null);
                    String implode = StringUtils.implode(CategoryServiceInternal.this.catalogPurchasedSkuList, ",");
                    CategoryServiceInternal.log.d("sendCatalogPurchasedSKUs=", implode);
                    CategoryServiceInternal.this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_CATALOG_PURCHASED_SKUS, implode);
                }
            }
        }
    };
    private final Runnable sendSkuListChanged = new Runnable() { // from class: com.nuance.connect.internal.CategoryServiceInternal.6
        @Override // java.lang.Runnable
        public void run() {
            final HashSet hashSet = new HashSet();
            for (Map.Entry entry : CategoryServiceInternal.this.skuToCatalogItems.entrySet()) {
                if (((CatalogItemImpl) entry.getValue()).isPendingPrice()) {
                    hashSet.add(entry.getKey());
                }
            }
            CategoryServiceInternal.this.mHandler.post(new Runnable() { // from class: com.nuance.connect.internal.CategoryServiceInternal.6.1
                @Override // java.lang.Runnable
                public void run() {
                    for (CatalogService.CatalogCallback catalogCallback : (CatalogService.CatalogCallback[]) CategoryServiceInternal.this.catalogListCallbacks.toArray(new CatalogService.CatalogCallback[0])) {
                        catalogCallback.catalogSKUListChanged(new ArrayList(hashSet));
                    }
                }
            });
        }
    };
    private final Runnable processCatalogSKUListAvailable = new Runnable() { // from class: com.nuance.connect.internal.CategoryServiceInternal.7
        @Override // java.lang.Runnable
        public void run() {
            List list;
            CategoryServiceInternal.log.d("processCatalogSKUListAvailable");
            final ArrayList arrayList = new ArrayList();
            final ArrayList arrayList2 = new ArrayList();
            synchronized (CategoryServiceInternal.this.catalogSKUListAvailableCheck) {
                list = CategoryServiceInternal.this.catalogSKUListAvailableCheck;
                CategoryServiceInternal.this.catalogSKUListAvailableCheck.clear();
            }
            String currentDeviceCountry = CategoryServiceInternal.this.getCurrentDeviceCountry();
            ArrayList arrayList3 = new ArrayList();
            Map categoryIdsForSKUs = CategoryServiceInternal.this.getCategoryIdsForSKUs();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                CatalogItemImpl catalogItem = CategoryServiceInternal.this.getCatalogItem(CategoryServiceInternal.this.getCategoryIdForSKU(categoryIdsForSKUs, (String) it.next()));
                if (catalogItem != null) {
                    if (!catalogItem.isAvailable(currentDeviceCountry)) {
                        arrayList.add(catalogItem.getSKU());
                    } else if (!catalogItem.isLocationRestricted()) {
                        arrayList2.add(catalogItem.getSKU());
                    } else if (catalogItem.isAllowedInCountry(currentDeviceCountry)) {
                        arrayList2.add(catalogItem.getSKU());
                    } else {
                        arrayList.add(catalogItem.getSKU());
                    }
                }
            }
            if (!arrayList3.isEmpty()) {
                synchronized (CategoryServiceInternal.this.catalogSKUListAvailableCheck) {
                    CategoryServiceInternal.this.catalogSKUListAvailableCheck.addAll(arrayList3);
                }
            }
            if (arrayList.isEmpty() && arrayList2.isEmpty()) {
                return;
            }
            CategoryServiceInternal.this.mHandler.post(new Runnable() { // from class: com.nuance.connect.internal.CategoryServiceInternal.7.1
                @Override // java.lang.Runnable
                public void run() {
                    for (CatalogService.CatalogCallback catalogCallback : (CatalogService.CatalogCallback[]) CategoryServiceInternal.this.catalogListCallbacks.toArray(new CatalogService.CatalogCallback[0])) {
                        if (arrayList.size() > 0) {
                            catalogCallback.skuListUnavailable(arrayList);
                        }
                        if (arrayList2.size() > 0) {
                            catalogCallback.skuListAvailable(arrayList2);
                        }
                    }
                }
            });
        }
    };
    private final Property.BooleanValueListener listener = new Property.BooleanValueListener() { // from class: com.nuance.connect.internal.CategoryServiceInternal.8
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<Boolean> property) {
            if (!property.getKey().equals(UserSettings.USER_ALLOW_DATA_COLLECTION) || property.getValue().booleanValue()) {
                return;
            }
            CategoryServiceInternal.this.setLivingLanguageStatus(false);
        }
    };
    private final ConnectHandler handler = new ConnectHandler() { // from class: com.nuance.connect.internal.CategoryServiceInternal.9
        @Override // com.nuance.connect.internal.ConnectHandler
        public String getHandlerName() {
            return APIHandlers.CATEGORY_HANDLER;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public int[] getMessageIDs() {
            return (int[]) CategoryServiceInternal.MESSAGE_IDS.clone();
        }

        /* JADX WARN: Removed duplicated region for block: B:111:0x01e9  */
        /* JADX WARN: Removed duplicated region for block: B:113:? A[RETURN, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:254:0x0669  */
        /* JADX WARN: Removed duplicated region for block: B:256:? A[RETURN, SYNTHETIC] */
        @Override // com.nuance.connect.internal.ConnectHandler
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void handleMessage(android.os.Handler r13, android.os.Message r14) {
            /*
                Method dump skipped, instructions count: 1774
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.internal.CategoryServiceInternal.AnonymousClass9.handleMessage(android.os.Handler, android.os.Message):void");
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void onPostUpgrade() {
        }
    };

    /* loaded from: classes.dex */
    public static class AddonDictionaryImpl implements AddonDictionariesService.AddonDictionary {
        private final String category;
        private final String categoryTranslated;
        private final String dictionary;
        private final int id;
        private final int language;
        private final String name;
        private final String nameTranslated;
        private final int rank;
        private String revertStatus;
        private String status;

        public AddonDictionaryImpl(String str, int i, String str2, String str3, int i2, int i3, String str4, String str5) {
            this.id = i;
            this.name = str2;
            this.category = str3;
            this.language = i2;
            this.dictionary = str;
            this.rank = i3;
            this.nameTranslated = str4;
            this.categoryTranslated = str5;
        }

        String diffLog(AddonDictionaryImpl addonDictionaryImpl) {
            StringBuilder sb = new StringBuilder();
            if (!String.valueOf(this.name).equals(String.valueOf(addonDictionaryImpl.name))) {
                sb.append("; Name(").append(String.valueOf(this.name)).append(" => ").append(String.valueOf(addonDictionaryImpl.name)).append(") ");
            }
            if (!String.valueOf(this.category).equals(String.valueOf(addonDictionaryImpl.category))) {
                sb.append("; Category(").append(String.valueOf(this.category)).append(" => ").append(String.valueOf(addonDictionaryImpl.category)).append(") ");
            }
            if (!String.valueOf(this.id).equals(String.valueOf(addonDictionaryImpl.id))) {
                sb.append("; ID(").append(String.valueOf(this.id)).append(" => ").append(String.valueOf(addonDictionaryImpl.id)).append(") ");
            }
            if (!String.valueOf(this.language).equals(String.valueOf(addonDictionaryImpl.language))) {
                sb.append("; Language(").append(String.valueOf(this.language)).append(" => ").append(String.valueOf(addonDictionaryImpl.language)).append(") ");
            }
            if (!String.valueOf(this.dictionary).equals(String.valueOf(addonDictionaryImpl.dictionary))) {
                sb.append("; Dictionary(").append(String.valueOf(this.dictionary)).append(" => ").append(String.valueOf(addonDictionaryImpl.dictionary)).append(") ");
            }
            if (!String.valueOf(this.status).equals(String.valueOf(addonDictionaryImpl.status))) {
                sb.append("; Status(").append(String.valueOf(this.status)).append(" => ").append(String.valueOf(addonDictionaryImpl.status)).append(") ");
            }
            if (!String.valueOf(this.categoryTranslated).equals(String.valueOf(addonDictionaryImpl.categoryTranslated))) {
                sb.append("; categoryTranslated(").append(String.valueOf(this.categoryTranslated)).append(" => ").append(String.valueOf(addonDictionaryImpl.categoryTranslated)).append(") ");
            }
            if (!String.valueOf(this.nameTranslated).equals(String.valueOf(addonDictionaryImpl.nameTranslated))) {
                sb.append("; nameTranslated(").append(String.valueOf(this.nameTranslated)).append(" => ").append(String.valueOf(addonDictionaryImpl.nameTranslated)).append(") ");
            }
            if (sb.length() > 0) {
                sb.insert(0, this.id).insert(0, "ID: ");
            }
            return sb.toString();
        }

        void download() {
            this.revertStatus = this.status;
            this.status = Strings.STATUS_DOWNLOADING;
        }

        @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionary
        public String getCategory() {
            return this.category;
        }

        @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionary
        public String getCategoryTranslated() {
            return this.categoryTranslated;
        }

        @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionary
        public String getDictionary() {
            return this.dictionary;
        }

        @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionary
        public int getId() {
            return this.id;
        }

        @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionary
        public int getLanguage() {
            return this.language;
        }

        @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionary
        public String getName() {
            return this.name;
        }

        @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionary
        public String getNameTranslated() {
            return this.nameTranslated;
        }

        @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionary
        public int getRank() {
            return this.rank;
        }

        public String getStatus() {
            return this.status;
        }

        @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionary
        public boolean hasUpdate() {
            return Strings.STATUS_INSTALLED_WITH_UPDATE.equals(this.status);
        }

        @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionary
        public boolean isInstalled() {
            return Strings.STATUS_INSTALLED.equals(this.status) || Strings.STATUS_INSTALLED_WITH_UPDATE.equals(this.status);
        }

        @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionary
        public boolean isSubscribed() {
            return !Strings.STATUS_AVAILABLE.equals(this.status);
        }

        void revert() {
            if (this.revertStatus != null) {
                this.status = this.revertStatus;
            } else {
                this.status = Strings.STATUS_AVAILABLE;
            }
        }

        public void setStatus(String str) {
            if (str != null && str.equals(Strings.STATUS_UNINSTALL_PENDING)) {
                this.revertStatus = null;
            }
            this.status = str;
        }

        boolean wasInstalled() {
            return this.revertStatus != null && (Strings.STATUS_INSTALLED.equals(this.revertStatus) || Strings.STATUS_INSTALLED_WITH_UPDATE.equals(this.revertStatus));
        }
    }

    /* loaded from: classes.dex */
    public static class CatalogItemBundleImpl extends CatalogItemImpl implements CatalogService.CatalogItemBundle {
        final HashMap<String, CatalogItemImpl> catalogBundleItems;
        final List<CatalogService.CatalogItem.Type> catalogBundleTypes;
        final List<String> catalogBundledCategoryIds;
        final List<String> catalogBundledSKUs;

        public CatalogItemBundleImpl(String str, String str2, String str3, Map<String, Integer> map, String str4, String str5, String str6, int i, String str7, List<String> list, Calendar calendar, Calendar calendar2, Calendar calendar3, Calendar calendar4, Calendar calendar5, String str8, Map<String, String> map2, List<String> list2, List<String> list3, String str9, String str10) {
            super(str, str2, str3, map, str4, str5, str6, i, str7, list, calendar, calendar2, calendar3, calendar4, calendar5, str8, map2, list2, list3, CatalogService.CatalogItem.Type.BUNDLE);
            this.catalogBundledCategoryIds = new ArrayList();
            this.catalogBundledSKUs = new ArrayList();
            this.catalogBundleItems = new HashMap<>();
            this.catalogBundleTypes = new ArrayList();
            if (str9 != null && str9.length() > 0) {
                String[] split = str9.split(",");
                if (split.length > 0) {
                    this.catalogBundledCategoryIds.addAll(Arrays.asList(split));
                }
            }
            if (str10 != null && str10.length() > 0) {
                String[] split2 = str10.split(",");
                if (split2.length > 0) {
                    this.catalogBundledSKUs.addAll(Arrays.asList(split2));
                }
            }
            this.subscribable = false;
        }

        public void addBundledItem(CatalogItemImpl catalogItemImpl) {
            if (getBundledCatalogItemSKUList().contains(catalogItemImpl.getSKU())) {
                this.catalogBundleItems.put(catalogItemImpl.getSKU(), catalogItemImpl);
                if (this.catalogBundleTypes.contains(catalogItemImpl.getType())) {
                    return;
                }
                this.catalogBundleTypes.add(catalogItemImpl.getType());
            }
        }

        @Override // com.nuance.connect.internal.CategoryServiceInternal.CatalogItemImpl
        public Set<String> getAllAvailableSKUs(String str) {
            HashSet hashSet = new HashSet();
            Iterator<Map.Entry<String, CatalogItemImpl>> it = this.catalogBundleItems.entrySet().iterator();
            while (it.hasNext()) {
                hashSet.addAll(it.next().getValue().getAllAvailableSKUs(str));
            }
            if (isAvailable(str)) {
                hashSet.add(getSKU());
            }
            return hashSet;
        }

        public Set<String> getAllChildrenSKUs() {
            HashSet hashSet = new HashSet();
            for (Map.Entry<String, CatalogItemImpl> entry : this.catalogBundleItems.entrySet()) {
                if (entry.getValue().getType() == CatalogService.CatalogItem.Type.BUNDLE) {
                    hashSet.addAll(((CatalogItemBundleImpl) entry.getValue()).getAllChildrenSKUs());
                } else {
                    hashSet.add(entry.getKey());
                }
            }
            return hashSet;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItemBundle
        public List<CatalogService.CatalogItem> getBundledBaseCatalogItemList() {
            return new ArrayList(this.catalogBundleItems.values());
        }

        public List<String> getBundledCatalogItemCategoryIdList() {
            return Collections.unmodifiableList(this.catalogBundledCategoryIds);
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItemBundle
        public List<String> getBundledCatalogItemSKUList() {
            return Collections.unmodifiableList(this.catalogBundledSKUs);
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItemBundle
        public List<CatalogService.CatalogItem.Type> getBundledCatalogItemTypes() {
            return Collections.unmodifiableList(this.catalogBundleTypes);
        }

        @Override // com.nuance.connect.internal.CategoryServiceInternal.CatalogItemImpl
        protected boolean isDifferent(CatalogItemImpl catalogItemImpl) {
            boolean z = super.isDifferent(catalogItemImpl) || !(catalogItemImpl instanceof CatalogItemBundleImpl);
            if (!z) {
                if (!this.catalogBundledCategoryIds.containsAll(((CatalogItemBundleImpl) catalogItemImpl).catalogBundledCategoryIds) || !((CatalogItemBundleImpl) catalogItemImpl).catalogBundledCategoryIds.containsAll(this.catalogBundledCategoryIds)) {
                    return true;
                }
                if (!this.catalogBundledSKUs.containsAll(((CatalogItemBundleImpl) catalogItemImpl).catalogBundledSKUs) || !((CatalogItemBundleImpl) catalogItemImpl).catalogBundledSKUs.containsAll(this.catalogBundledSKUs)) {
                    return true;
                }
            }
            return z;
        }

        public void resetBundledItems() {
            this.catalogBundleItems.clear();
        }

        @Override // com.nuance.connect.internal.CategoryServiceInternal.CatalogItemImpl
        public void resetPurchased() {
            super.resetPurchased();
            for (CatalogService.CatalogItem catalogItem : getBundledBaseCatalogItemList()) {
                if (catalogItem != null) {
                    ((CatalogItemImpl) catalogItem).resetPurchased();
                }
            }
        }

        @Override // com.nuance.connect.internal.CategoryServiceInternal.CatalogItemImpl
        public void setPurchased() {
            super.setPurchased();
            for (CatalogService.CatalogItem catalogItem : getBundledBaseCatalogItemList()) {
                if (catalogItem != null) {
                    ((CatalogItemImpl) catalogItem).setPurchased();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class CatalogItemImpl implements CatalogService.CatalogItem {
        private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, CatalogItemImpl.class.getSimpleName());
        Calendar added;
        Calendar availableEnd;
        Calendar availableStart;
        String catalogItemUUID;
        String descriptionLong;
        String descriptionShort;
        Calendar fulfillDate;
        boolean installed;
        CatalogService.CatalogItem.Type itemType;
        String locale;
        String name;
        List<String> previewURLList;
        String price;
        int purchaseFlag;
        boolean purchased;
        Set<String> regionsExcluded;
        Set<String> regionsIncluded;
        String revertStatus;
        String sku;
        String status;
        String thumbnailURL;
        String title;
        Calendar updatedDate;
        final Map<String, Integer> categoryWeightList = new LinkedHashMap();
        boolean subscribable = true;

        CatalogItemImpl(String str, String str2, String str3, Map<String, Integer> map, String str4, String str5, String str6, int i, String str7, List<String> list, Calendar calendar, Calendar calendar2, Calendar calendar3, Calendar calendar4, Calendar calendar5, String str8, Map<String, String> map2, List<String> list2, List<String> list3) {
            setup(str, str2, str3, map, str4, str5, str6, i, str7, list, calendar, calendar2, calendar3, calendar4, calendar5, str8, map2, list2, list3, CatalogService.CatalogItem.Type.KEYBOARD);
        }

        CatalogItemImpl(String str, String str2, String str3, Map<String, Integer> map, String str4, String str5, String str6, int i, String str7, List<String> list, Calendar calendar, Calendar calendar2, Calendar calendar3, Calendar calendar4, Calendar calendar5, String str8, Map<String, String> map2, List<String> list2, List<String> list3, CatalogService.CatalogItem.Type type) {
            setup(str, str2, str3, map, str4, str5, str6, i, str7, list, calendar, calendar2, calendar3, calendar4, calendar5, str8, map2, list2, list3, type);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean availableNow() {
            boolean z = true;
            long currentTimeMillis = System.currentTimeMillis();
            if (getDateAvailableStart() != null && getDateAvailableStart().getTimeInMillis() > currentTimeMillis) {
                z = false;
            }
            if (getDateAvailableEnd() == null || currentTimeMillis <= getDateAvailableEnd().getTimeInMillis()) {
                return z;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean inCategory(String str) {
            return this.categoryWeightList.keySet().contains(str);
        }

        private boolean isFulfillable() {
            return getDateFulfillEnd() == null || System.currentTimeMillis() <= getDateFulfillEnd().getTimeInMillis();
        }

        void download() {
            this.revertStatus = this.status;
            this.status = Strings.STATUS_DOWNLOADING;
        }

        public Set<String> getAllAvailableSKUs(String str) {
            HashSet hashSet = new HashSet();
            if (isAvailable(str)) {
                hashSet.add(getSKU());
            }
            return hashSet;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public String getCanonicalName() {
            return this.name;
        }

        public String getCategoryId() {
            return this.catalogItemUUID;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public List<String> getCategoryList() {
            return new ArrayList(this.categoryWeightList.keySet());
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public Map<String, Integer> getCategoryWeight() {
            return this.categoryWeightList;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public Calendar getDateAdded() {
            if (this.added != null) {
                return (Calendar) this.added.clone();
            }
            return null;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public Calendar getDateAvailableEnd() {
            if (this.availableEnd != null) {
                return (Calendar) this.availableEnd.clone();
            }
            return null;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public Calendar getDateAvailableStart() {
            if (this.availableStart != null) {
                return (Calendar) this.availableStart.clone();
            }
            return null;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public Calendar getDateFulfillEnd() {
            if (this.fulfillDate != null) {
                return (Calendar) this.fulfillDate.clone();
            }
            return null;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public String getDescriptionLong() {
            return this.descriptionLong;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public String getDescriptionShort() {
            return this.descriptionShort;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public List<String> getPreviewURLList() {
            return Collections.unmodifiableList(this.previewURLList);
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public String getPrice() {
            return this.price;
        }

        public List<String> getRegionsExcluded() {
            return Collections.unmodifiableList(new ArrayList(this.regionsExcluded));
        }

        public List<String> getRegionsIncluded() {
            return Collections.unmodifiableList(new ArrayList(this.regionsIncluded));
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public String getSKU() {
            return this.sku;
        }

        public String getStatus() {
            return this.status;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public String getThumbnailURL() {
            return this.thumbnailURL;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public String getTitle() {
            return this.title;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public CatalogService.CatalogItem.Type getType() {
            return this.itemType;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public Calendar getUpdatedDate() {
            if (this.updatedDate != null) {
                return (Calendar) this.updatedDate.clone();
            }
            return null;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public int getWeight(String str) {
            if (str != null && this.categoryWeightList.containsKey(str)) {
                return this.categoryWeightList.get(str).intValue();
            }
            return -1;
        }

        public boolean hasUpdate() {
            return Strings.STATUS_INSTALLED_WITH_UPDATE.equals(this.status);
        }

        public boolean isAllowedInCountry(String str) {
            if (isLocationRestricted()) {
                return (getRegionsIncluded() == null || getRegionsIncluded().isEmpty()) ? getRegionsExcluded() == null || getRegionsExcluded().isEmpty() || !getRegionsExcluded().contains(str) : getRegionsIncluded().contains(str);
            }
            return true;
        }

        boolean isAvailable(String str) {
            return isFulfillable() && (isPurchasable() || ((isFree() && availableNow()) || isInstalled() || isPurchased())) && isAllowedInCountry(str);
        }

        boolean isDifferent(CatalogItemImpl catalogItemImpl) {
            if (!StringUtils.nullSafeEquals(catalogItemImpl.getSKU(), getSKU())) {
                log.d("Item sku isDifferent this: ", getSKU(), " other: ", catalogItemImpl.getSKU());
                return true;
            }
            if (!catalogItemImpl.getCategoryList().containsAll(getCategoryList()) || !getCategoryList().containsAll(catalogItemImpl.getCategoryList())) {
                log.d("Item categoryList isDifferent this: ", getCategoryList().toString(), " other: ", catalogItemImpl.getCategoryList().toString());
                return true;
            }
            if (!StringUtils.nullSafeEquals(catalogItemImpl.getTitle(), getTitle())) {
                log.d("Item title isDifferent this: ", getTitle(), " other: ", catalogItemImpl.getTitle());
                return true;
            }
            if (!StringUtils.nullSafeEquals(catalogItemImpl.getDescriptionShort(), getDescriptionShort())) {
                log.d("Item short description isDifferent this: ", getDescriptionShort(), " other: ", catalogItemImpl.getDescriptionShort());
                return true;
            }
            if (!StringUtils.nullSafeEquals(catalogItemImpl.getDescriptionLong(), getDescriptionLong())) {
                log.d("Item long description isDifferent this: ", getDescriptionLong(), " other: ", catalogItemImpl.getDescriptionLong());
                return true;
            }
            if (catalogItemImpl.purchaseFlag != this.purchaseFlag) {
                log.d("Item isPurchasable isDifferent this: ", Boolean.valueOf(isPurchasable()), " other: ", Boolean.valueOf(catalogItemImpl.isPurchasable()));
                return true;
            }
            if (catalogItemImpl.isFree() != isFree()) {
                log.d("Item isFree isDifferent this: ", Boolean.valueOf(isFree()), " other: ", Boolean.valueOf(catalogItemImpl.isFree()));
                return true;
            }
            if (!StringUtils.nullSafeEquals(catalogItemImpl.getPrice(), getPrice())) {
                log.d("Item price isDifferent this: ", getPrice(), " other: ", catalogItemImpl.getPrice());
                return true;
            }
            if (catalogItemImpl.isPurchased() != isPurchased()) {
                log.d("Item isPurchased isDifferent this: ", Boolean.valueOf(isPurchased()), " other: ", Boolean.valueOf(catalogItemImpl.isPurchased()));
                return true;
            }
            if (catalogItemImpl.isInstalled() != isInstalled()) {
                log.d("Item isInstalled isDifferent this: ", Boolean.valueOf(isInstalled()), " other: ", Boolean.valueOf(catalogItemImpl.isInstalled()));
                return true;
            }
            if (catalogItemImpl.isUpdateAvailable() != isUpdateAvailable()) {
                log.d("Item isUpdateAvailable isDifferent this: ", Boolean.valueOf(isUpdateAvailable()), " other: ", Boolean.valueOf(catalogItemImpl.isUpdateAvailable()));
                return true;
            }
            if (!StringUtils.nullSafeEquals(catalogItemImpl.getThumbnailURL(), getThumbnailURL())) {
                log.d("Item getThumbnailURL isDifferent this: ", getThumbnailURL(), " other: ", catalogItemImpl.getThumbnailURL());
                return true;
            }
            if (!catalogItemImpl.getPreviewURLList().containsAll(getPreviewURLList()) || !getPreviewURLList().containsAll(catalogItemImpl.getPreviewURLList())) {
                log.d("Item getPreviewURLList() isDifferent this: ", getPreviewURLList(), " other: ", catalogItemImpl.getPreviewURLList());
                return true;
            }
            if ((catalogItemImpl.getDateAdded() == null && getDateAdded() != null) || (catalogItemImpl.getDateAdded() != null && !catalogItemImpl.getDateAdded().equals(getDateAdded()))) {
                log.d("Item getDateAdded() isDifferent this: ", getDateAdded(), " other: ", catalogItemImpl.getDateAdded());
                return true;
            }
            if ((catalogItemImpl.getDateAvailableStart() == null && getDateAvailableStart() != null) || (catalogItemImpl.getDateAvailableStart() != null && !catalogItemImpl.getDateAvailableStart().equals(getDateAvailableStart()))) {
                log.d("Item getDateAvailableStart() isDifferent this: ", getDateAvailableStart(), " other: ", catalogItemImpl.getDateAvailableStart());
                return true;
            }
            if ((catalogItemImpl.getDateAvailableEnd() == null && getDateAvailableEnd() != null) || (catalogItemImpl.getDateAvailableEnd() != null && !catalogItemImpl.getDateAvailableEnd().equals(getDateAvailableEnd()))) {
                log.d("Item getDateAvailableEnd() isDifferent this: ", getDateAvailableEnd(), " other: ", catalogItemImpl.getDateAvailableEnd());
                return true;
            }
            if ((catalogItemImpl.getUpdatedDate() == null && getUpdatedDate() != null) || (catalogItemImpl.getUpdatedDate() != null && !catalogItemImpl.getUpdatedDate().equals(getUpdatedDate()))) {
                log.d("Item getUpdatedDate() isDifferent this: ", getUpdatedDate(), " other: ", catalogItemImpl.getUpdatedDate());
                return true;
            }
            if (!StringUtils.nullSafeEquals(catalogItemImpl.getStatus(), getStatus())) {
                log.d("Item getStatus isDifferent this: ", getStatus(), " other: ", catalogItemImpl.getStatus());
                return true;
            }
            if (!StringUtils.nullSafeEquals(catalogItemImpl.getCanonicalName(), getCanonicalName())) {
                log.d("Item getCanonicalName() isDifferent this: ", getCanonicalName(), " other: ", catalogItemImpl.getCanonicalName());
                return true;
            }
            if (!this.regionsExcluded.equals(catalogItemImpl.regionsExcluded)) {
                log.d("Item regionsExcluded isDifferent this: ", this.regionsExcluded.toString(), " other: ", catalogItemImpl.getRegionsExcluded().toString());
                return true;
            }
            if (this.regionsIncluded.equals(catalogItemImpl.regionsIncluded)) {
                return false;
            }
            log.d("Item regionsIncluded isDifferent this: ", this.regionsIncluded.toString(), " other: ", catalogItemImpl.regionsIncluded.toString());
            return true;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public boolean isDownloadable() {
            boolean z = false;
            if (!getType().equals(CatalogService.CatalogItem.Type.BUNDLE) && isFulfillable()) {
                if (isInstalled() || isPurchased()) {
                    z = true;
                } else if (availableNow() && isFree()) {
                    z = true;
                }
            }
            log.d("isDownloadable sku:" + getSKU() + " downloadable=" + z);
            return z;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public boolean isFree() {
            return CatalogService.CatalogItem.Purchasable.FREE.equals(this.purchaseFlag);
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public boolean isInstalled() {
            return Strings.STATUS_INSTALLED.equals(this.status) || Strings.STATUS_INSTALLED_WITH_UPDATE.equals(this.status) || this.installed;
        }

        public boolean isLocationRestricted() {
            return ((getRegionsExcluded() == null || getRegionsExcluded().isEmpty()) && (getRegionsIncluded() == null || getRegionsIncluded().isEmpty())) ? false : true;
        }

        public boolean isPendingPrice() {
            if (CatalogService.CatalogItem.Purchasable.PURCHASABLE.equals(this.purchaseFlag) && availableNow()) {
                return this.price == null || this.price.isEmpty();
            }
            return false;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public boolean isPurchasable() {
            return (isPurchased() || CatalogService.CatalogItem.Purchasable.FREE.equals(this.purchaseFlag) || CatalogService.CatalogItem.Purchasable.UNPURCHASABLE.equals(this.purchaseFlag) || !isFulfillable() || !availableNow() || this.price == null) ? false : true;
        }

        public boolean isPurchaseLookupAble() {
            return (isPurchased() || CatalogService.CatalogItem.Purchasable.FREE.equals(this.purchaseFlag) || CatalogService.CatalogItem.Purchasable.UNPURCHASABLE.equals(this.purchaseFlag) || !isFulfillable()) ? false : true;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public boolean isPurchased() {
            return this.purchased;
        }

        public boolean isSubscribable() {
            return this.subscribable;
        }

        public boolean isSubscribed() {
            return !Strings.STATUS_AVAILABLE.equals(this.status);
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        public boolean isUpdateAvailable() {
            return Strings.STATUS_INSTALLED_WITH_UPDATE.equals(this.status);
        }

        public boolean priceUpdateAllowed() {
            return (CatalogService.CatalogItem.Purchasable.FREE.equals(this.purchaseFlag) || CatalogService.CatalogItem.Purchasable.UNPURCHASABLE.equals(this.purchaseFlag) || !availableNow()) ? false : true;
        }

        public void resetInstalled() {
            this.installed = false;
        }

        public void resetPrice() {
            this.price = null;
        }

        public void resetPurchased() {
            this.purchased = false;
        }

        void revert() {
            if (this.revertStatus != null) {
                this.status = this.revertStatus;
            } else {
                this.status = Strings.STATUS_AVAILABLE;
            }
        }

        public void setInstalled() {
            this.installed = true;
        }

        public void setPrice(String str) {
            this.price = str;
        }

        public void setPurchased() {
            this.purchased = true;
        }

        public void setStatus(String str) {
            if (str != null && str.equals(Strings.STATUS_UNINSTALL_PENDING)) {
                this.revertStatus = null;
            }
            this.status = str;
        }

        void setup(String str, String str2, String str3, Map<String, Integer> map, String str4, String str5, String str6, int i, String str7, List<String> list, Calendar calendar, Calendar calendar2, Calendar calendar3, Calendar calendar4, Calendar calendar5, String str8, Map<String, String> map2, List<String> list2, List<String> list3, CatalogService.CatalogItem.Type type) {
            this.catalogItemUUID = str;
            this.name = str2;
            this.sku = str3;
            this.title = str4;
            this.descriptionShort = str5;
            this.descriptionLong = str6;
            this.purchaseFlag = i;
            this.thumbnailURL = str7;
            if (list == null) {
                list = Collections.emptyList();
            }
            this.previewURLList = list;
            this.added = calendar;
            this.availableStart = calendar2;
            this.availableEnd = calendar3;
            this.updatedDate = calendar5;
            this.fulfillDate = calendar4;
            this.locale = str8;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (map2.containsKey(entry.getKey())) {
                    this.categoryWeightList.put(map2.get(entry.getKey()), entry.getValue());
                } else {
                    log.e("Translation not found for label ", entry.getKey(), " for sku ", this.sku);
                }
            }
            this.regionsIncluded = new HashSet(list2);
            this.regionsExcluded = new HashSet(list3);
            Collections.sort(this.previewURLList);
            this.itemType = type;
        }

        boolean wasInstalled() {
            return this.revertStatus != null && (Strings.STATUS_INSTALLED.equals(this.revertStatus) || Strings.STATUS_INSTALLED_WITH_UPDATE.equals(this.revertStatus));
        }
    }

    /* loaded from: classes.dex */
    private enum CategoryEvents {
        ON_DICTIONARIES_UPDATED,
        ON_CATALOGS_CHANGED,
        ON_CATALOG_PRICES_SET,
        ON_CATALOG_PRICES_RESET,
        UNKNOWN;

        private static CategoryEvents[] values = null;

        public static CategoryEvents fromInt(int i) {
            if (values == null) {
                values = values();
            }
            return (values.length <= i || i < 0) ? UNKNOWN : values[i];
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CategoryHandlerThread extends HandlerThread {
        private final WeakReference<CategoryServiceInternal> parent;

        CategoryHandlerThread(CategoryServiceInternal categoryServiceInternal) {
            this.parent = new WeakReference<>(categoryServiceInternal);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.nuance.connect.util.HandlerThread
        public Message handleMessage(Message message) {
            CategoryServiceInternal categoryServiceInternal = this.parent.get();
            if (categoryServiceInternal == null) {
                return null;
            }
            switch (CategoryEvents.fromInt(message.what)) {
                case ON_DICTIONARIES_UPDATED:
                    Logger.getTrace().enterMethod("onDictionariesUpdated");
                    categoryServiceInternal.onDictionariesUpdated();
                    Logger.getTrace().exitMethod("onDictionariesUpdated");
                    return null;
                case ON_CATALOGS_CHANGED:
                    Logger.getTrace().enterMethod("onCatalogsChanged");
                    categoryServiceInternal.onCatalogsChanged();
                    Logger.getTrace().exitMethod("onCatalogsChanged");
                    return null;
                case ON_CATALOG_PRICES_SET:
                    Logger.getTrace().enterMethod("onCatalogsPricesSet");
                    categoryServiceInternal.onCatalogsPricesSet((Map) message.obj);
                    Logger.getTrace().exitMethod("onCatalogsPricesSet");
                    return null;
                case ON_CATALOG_PRICES_RESET:
                    Logger.getTrace().enterMethod("onCatalogPricesReset");
                    categoryServiceInternal.onCatalogPricesReset();
                    Logger.getTrace().exitMethod("onCatalogPricesReset");
                    return null;
                case UNKNOWN:
                    CategoryServiceInternal.log.w("unknown events: " + message.what);
                    return null;
                default:
                    return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CategoryServiceInternal(ConnectServiceManagerInternal connectServiceManagerInternal) {
        this.livingLanguageStatus = true;
        this.catalogStatus = false;
        this.connectService = connectServiceManagerInternal;
        this.livingLanguageStatus = this.connectService.getUserSettings().isLivingLanguageEnabled();
        this.catalogStatus = this.connectService.getUserSettings().isCatalogEnabled();
        synchronized (this.catalogPurchasedSkuList) {
            this.catalogPurchasedSkuList.addAll(this.connectService.getUserSettings().getCatalogPurchasedSKUList());
        }
        String readString = connectServiceManagerInternal.getDataStore().readString(CATALOG_LABELS_PREF, "");
        try {
            this.catalogLabelList.putAll(MapMarshal.toStringMap(readString));
        } catch (Exception e) {
            log.e("Could not process the catalog labels list: ", readString);
        }
        this.connectService.registerUserSettingsListener(UserSettings.USER_ALLOW_DATA_COLLECTION, this.listener);
        this.database = CategoryDatabase.from(connectServiceManagerInternal.getContext());
        this.handlerThread = new CategoryHandlerThread(this);
        this.handlerThread.start();
        if (!this.catalogStatus || this.catalogLabelList.isEmpty()) {
            return;
        }
        this.handlerThread.process(CategoryEvents.ON_CATALOGS_CHANGED.ordinal(), null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CatalogItemImpl getCatalogItem(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }
        if (this.lazyLoadCatalogItems.containsKey(str)) {
            return this.lazyLoadCatalogItems.get(str);
        }
        HashMap hashMap = new HashMap();
        synchronized (this.catalogLabelList) {
            hashMap.putAll(this.catalogLabelList);
        }
        return getCatalogItem(str, hashMap);
    }

    private CatalogItemImpl getCatalogItem(String str, Map<String, String> map) {
        Map<String, String> props = this.database.getProps(str);
        Lock readLock = this.catalogLock.readLock();
        readLock.lock();
        try {
            return loadCatalogItemFromProperties(str, props, map, this.catalogAllPurchasedSkuSet, this.catalogSkuForPurchaseMap, this.catalogSKUsChanged);
        } finally {
            readLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getCategoryIdForSKU(Map<String, Map<String, String>> map, String str) {
        if (str != null && !"".equals(str)) {
            for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
                if (str.equals(entry.getValue().get(MessageAPI.SKU))) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getCategoryIdFromSKU(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }
        CatalogItemImpl catalogItemImpl = this.skuToCatalogItems.get(str);
        return catalogItemImpl != null ? catalogItemImpl.getCategoryId() : getCategoryIdForSKU(getCategoryIdsForSKUs(), str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Map<String, Map<String, String>> getCategoryIdsForSKUs() {
        return this.database.allWithProperty(MessageAPI.SKU, Arrays.asList(this.database.getTableForType(6)));
    }

    private List<String> getCategoryListFromKeySet(Set<String> set) {
        ArrayList arrayList = new ArrayList();
        synchronized (this.catalogLabelList) {
            for (String str : this.catalogLabelList.values()) {
                if (set.contains(str)) {
                    arrayList.add(str);
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getCurrentDeviceCountry() {
        return this.connectService.getUserSettings().getLocationCountry();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getSKUFromCategoryId(String str) {
        return this.database.getProp(str, MessageAPI.SKU);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCategoryLivingLanguage(int i) {
        return i == 1 || i == 3;
    }

    private CatalogItemImpl loadCatalogItemFromProperties(String str, Map<String, String> map, Map<String, String> map2, Set<String> set, Map<String, CatalogService.CatalogItem.Type> map3, Set<String> set2) {
        long j;
        long j2;
        CatalogItemImpl catalogItemImpl;
        if (map == null || map.isEmpty()) {
            log.e("empty catalog item: ", str);
            return null;
        }
        CatalogItemImpl catalogItemImpl2 = this.lazyLoadCatalogItems.get(str);
        if (catalogItemImpl2 != null) {
            if (!catalogItemImpl2.isPendingPrice()) {
                return catalogItemImpl2;
            }
            set2.add(catalogItemImpl2.getSKU());
            return catalogItemImpl2;
        }
        try {
            try {
                CatalogService.CatalogItem.Type valueOf = CatalogService.CatalogItem.Type.valueOf(map.get(Strings.PROP_TYPE));
                Map<String, Integer> stringIntegerMap = MapMarshal.toStringIntegerMap(map.get(MessageAPI.LABELS));
                List<String> stringToList = stringToList(map.get(MessageAPI.MEDIA_URLS));
                Calendar dateToEPOC = setDateToEPOC(map.get(MessageAPI.CREATION_TIMESTAMP));
                Calendar dateToEPOC2 = setDateToEPOC(map.get(MessageAPI.START));
                Calendar dateToEPOC3 = setDateToEPOC(map.get(MessageAPI.END));
                Calendar dateToEPOC4 = setDateToEPOC(map.get(MessageAPI.FULFILL_UNTIL));
                Calendar dateToEPOC5 = setDateToEPOC("");
                List<String> stringToList2 = stringToList(map.get(MessageAPI.COUNTRIES_INCLUDED));
                List<String> stringToList3 = stringToList(map.get(MessageAPI.COUNTRIES_EXCLUDED));
                int i = Integers.STATUS_SUCCESS;
                try {
                    i = Integer.parseInt(map.get(Strings.MAP_KEY_STEP));
                } catch (NumberFormatException e) {
                    log.e("Could not parse step");
                }
                String str2 = Strings.STATUS_AVAILABLE;
                if (i < 7 && i > 0) {
                    str2 = Strings.STATUS_DOWNLOADING;
                } else if (i == 7) {
                    long j3 = Long.MIN_VALUE;
                    try {
                        j3 = Long.parseLong(map.get(CategoryDatabase.LAST_UPDATE_FETCHED));
                        j = j3;
                        j2 = Long.parseLong(map.get(CategoryDatabase.LAST_UPDATE_AVAILABLE));
                    } catch (NumberFormatException e2) {
                        log.e("Could not read times.");
                        j = j3;
                        j2 = Long.MIN_VALUE;
                    }
                    if (j < j2) {
                        this.oemLog.d("Catalog item has update -- key: [" + str + "] lastFetched: [" + j + "] lastAvailable: [" + j2 + "]");
                        str2 = Strings.STATUS_INSTALLED_WITH_UPDATE;
                    } else {
                        str2 = Strings.STATUS_INSTALLED;
                    }
                } else if (i == 8) {
                    str2 = Strings.STATUS_CANCELED;
                } else if (i == 10) {
                    str2 = Strings.STATUS_UNINSTALL_PENDING;
                }
                String str3 = (map.containsKey(CategoryDatabase.UNSUBSCRIBE_PENDING) && Boolean.TRUE.toString().equals(map.get(CategoryDatabase.UNSUBSCRIBE_PENDING))) ? Strings.STATUS_UNINSTALL_PENDING : str2;
                switch (valueOf) {
                    case BUNDLE:
                        CatalogItemBundleImpl catalogItemBundleImpl = new CatalogItemBundleImpl(str, map.get(MessageAPI.NAME), map.get(MessageAPI.SKU), stringIntegerMap, map.get(MessageAPI.TITLE), map.get(MessageAPI.SHORT_DESCRIPTION), map.get(MessageAPI.DESCRIPTION), Integer.parseInt(map.get(MessageAPI.PURCHASABLE)), map.get(MessageAPI.THUMBNAIL_URL), stringToList, dateToEPOC, dateToEPOC2, dateToEPOC3, dateToEPOC4, dateToEPOC5, map.get(MessageAPI.LOCALE), map2, stringToList2, stringToList3, map.get(Strings.PROP_BUNDLED_THEMES_CDB), map.get(Strings.PROP_BUNDLED_THEMES_SKUS));
                        catalogItemBundleImpl.setStatus(str3);
                        CatalogItemBundleImpl catalogItemBundleImpl2 = catalogItemBundleImpl;
                        if (catalogItemBundleImpl2.getBundledCatalogItemCategoryIdList() != null) {
                            log.d("bundle.getBundledCatalogItemCategoryIdList()", catalogItemBundleImpl2.getBundledCatalogItemCategoryIdList());
                            for (String str4 : catalogItemBundleImpl2.getBundledCatalogItemCategoryIdList()) {
                                if (!this.lazyLoadCatalogItems.containsKey(str4)) {
                                    loadCatalogItemFromProperties(str4, this.database.getProps(str4), map2, set, map3, set2);
                                }
                                catalogItemBundleImpl2.addBundledItem(this.lazyLoadCatalogItems.get(str4));
                            }
                            catalogItemImpl = catalogItemBundleImpl;
                            break;
                        } else {
                            catalogItemImpl = catalogItemBundleImpl;
                            break;
                        }
                    case KEYBOARD:
                        CatalogItemImpl catalogItemImpl3 = new CatalogItemImpl(str, map.get(MessageAPI.NAME), map.get(MessageAPI.SKU), stringIntegerMap, map.get(MessageAPI.TITLE), map.get(MessageAPI.SHORT_DESCRIPTION), map.get(MessageAPI.DESCRIPTION), Integer.parseInt(map.get(MessageAPI.PURCHASABLE)), map.get(MessageAPI.THUMBNAIL_URL), stringToList, dateToEPOC, dateToEPOC2, dateToEPOC3, dateToEPOC4, dateToEPOC5, map.get(MessageAPI.LOCALE), map2, stringToList2, stringToList3);
                        catalogItemImpl3.setStatus(str3);
                        catalogItemImpl = catalogItemImpl3;
                        break;
                    default:
                        log.w("Unsupported catalog type: ", valueOf);
                        catalogItemImpl = catalogItemImpl2;
                        break;
                }
                if (catalogItemImpl != null) {
                    synchronized (this.catalogSkuPriceList) {
                        if (this.catalogSkuPriceList.containsKey(catalogItemImpl.getSKU())) {
                            catalogItemImpl.setPrice(this.catalogSkuPriceList.get(catalogItemImpl.getSKU()));
                        }
                    }
                    synchronized (this.catalogPurchasedSkuList) {
                        if (this.catalogPurchasedSkuList.contains(catalogItemImpl.getSKU())) {
                            catalogItemImpl.setPurchased();
                        }
                    }
                    if (catalogItemImpl.isPurchased()) {
                        if (catalogItemImpl.getType() == CatalogService.CatalogItem.Type.BUNDLE) {
                            set.addAll(((CatalogItemBundleImpl) catalogItemImpl).getAllChildrenSKUs());
                        } else {
                            set.add(catalogItemImpl.getSKU());
                        }
                    } else if (set.contains(catalogItemImpl.getSKU())) {
                        catalogItemImpl.setPurchased();
                    }
                    if (!catalogItemImpl.isFree() && !catalogItemImpl.isPurchased() && catalogItemImpl.isPurchaseLookupAble() && catalogItemImpl.availableNow()) {
                        map3.put(catalogItemImpl.getSKU(), catalogItemImpl.getType());
                    }
                    if (catalogItemImpl.isPendingPrice()) {
                        set2.add(catalogItemImpl.getSKU());
                    }
                    this.lazyLoadCatalogItems.put(str, catalogItemImpl);
                    this.skuToCatalogItems.put(catalogItemImpl.getSKU(), catalogItemImpl);
                }
                return catalogItemImpl;
            } catch (Exception e3) {
                log.e("Error Processing Catalog Item: " + e3.getMessage());
                return null;
            }
        } catch (NumberFormatException e4) {
            log.e("Error Processing Catalog Item Number: " + e4.getMessage());
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCatalogCallbackOfSkusChanged() {
        this.mHandler.removeCallbacks(this.sendSkuListChanged);
        this.mHandler.postDelayed(this.sendSkuListChanged, 1000L);
    }

    private void notifyCatalogCallbackofInstalledUpdates(final List<CatalogService.CatalogItem> list) {
        this.oemLog.e("CatalogItem notifyCatalogCallbackofInstalledUpdates called");
        this.mHandler.post(new Runnable() { // from class: com.nuance.connect.internal.CategoryServiceInternal.10
            @Override // java.lang.Runnable
            public void run() {
                for (CatalogService.CatalogCallback catalogCallback : (CatalogService.CatalogCallback[]) CategoryServiceInternal.this.catalogListCallbacks.toArray(new CatalogService.CatalogCallback[0])) {
                    catalogCallback.catalogItemInstalledUpdates(list);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCatalogCallbackofItemsChanged() {
        this.mHandler.removeCallbacks(this.sendCatalogItemsChangedCallback);
        this.mHandler.postDelayed(this.sendCatalogItemsChangedCallback, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCatalogPricesReset() {
        boolean z;
        synchronized (this.catalogSkuPriceList) {
            z = this.catalogSkuPriceList.size() > 0;
        }
        log.d("resetCatalogItemPrice()");
        this.catalogLock.writeLock().lock();
        try {
            Iterator<CatalogItemImpl> it = this.lazyLoadCatalogItems.values().iterator();
            while (it.hasNext()) {
                it.next().resetPrice();
            }
            rebuildSkuAvailableList();
            this.catalogLock.writeLock().unlock();
            synchronized (this.catalogSkuPriceList) {
                this.catalogSkuPriceList.clear();
            }
            if (z) {
                notifyCatalogCallbackOfSkusChanged();
                notifyCatalogCallbackofItemsChanged();
            }
        } catch (Throwable th) {
            this.catalogLock.writeLock().unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00cb  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x00f2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onCatalogsChanged() {
        /*
            Method dump skipped, instructions count: 456
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.internal.CategoryServiceInternal.onCatalogsChanged():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCatalogsPricesSet(Map<String, String> map) {
        HashMap hashMap;
        boolean z;
        if (map == null) {
            map = Collections.emptyMap();
        }
        synchronized (this.catalogSkuPriceList) {
            hashMap = new HashMap(this.catalogSkuPriceList);
        }
        boolean z2 = false;
        this.catalogLock.writeLock().lock();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                log.d("changing price for sku: ", entry.getKey(), " to: ", entry.getValue());
                String value = entry.getValue();
                CatalogItemImpl catalogItemImpl = entry.getKey() != null ? this.skuToCatalogItems.get(entry.getKey()) : null;
                if (value == null || value.length() == 0) {
                    hashMap.remove(entry.getKey());
                    if (catalogItemImpl != null) {
                        catalogItemImpl.resetPrice();
                        z2 = true;
                    }
                } else {
                    hashMap.put(entry.getKey(), value);
                    if (catalogItemImpl != null) {
                        catalogItemImpl.setPrice(value);
                        z = true;
                        z2 = z;
                    }
                }
                z = z2;
                z2 = z;
            }
            rebuildSkuAvailableList();
            this.catalogLock.writeLock().unlock();
            synchronized (this.catalogSkuPriceList) {
                this.catalogSkuPriceList.clear();
                this.catalogSkuPriceList.putAll(hashMap);
            }
            if (z2) {
                notifyCatalogCallbackOfSkusChanged();
                notifyCatalogCallbackofItemsChanged();
            }
        } catch (Throwable th) {
            this.catalogLock.writeLock().unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDictionariesUpdated() {
        String str;
        long j;
        long j2;
        boolean z;
        Set<String> allCategoryIDs = this.database.allCategoryIDs(this.database.getTableForType(2));
        log.d("onDictionariesUpdated count=", Integer.valueOf(allCategoryIDs.size()));
        for (String str2 : allCategoryIDs) {
            Map<String, String> props = this.database.getProps(str2);
            if (props != null && !props.isEmpty()) {
                try {
                    String str3 = props.get(MessageAPI.RANK);
                    int parseInt = str3 != null ? Integer.parseInt(str3) : 0;
                    int i = Integers.STATUS_SUCCESS;
                    try {
                        i = Integer.parseInt(props.get(Strings.MAP_KEY_STEP));
                    } catch (NumberFormatException e) {
                        log.e("Could not parse step");
                    }
                    if (i < 7 && i > 0) {
                        str = Strings.STATUS_DOWNLOADING;
                    } else if (i == 7) {
                        long j3 = Long.MIN_VALUE;
                        try {
                            j3 = Long.parseLong(props.get(CategoryDatabase.LAST_UPDATE_FETCHED));
                            j = j3;
                            j2 = Long.parseLong(props.get(CategoryDatabase.LAST_UPDATE_AVAILABLE));
                        } catch (NumberFormatException e2) {
                            log.e("Could not read times.");
                            j = j3;
                            j2 = Long.MIN_VALUE;
                        }
                        if (j < j2) {
                            this.oemLog.d("Chinese dictionary has update -- key: [" + str2 + "] lastFetched: [" + j + "] lastAvailable: [" + j2 + "]");
                            str = Strings.STATUS_INSTALLED_WITH_UPDATE;
                        } else {
                            str = Strings.STATUS_INSTALLED;
                        }
                    } else {
                        str = Strings.STATUS_AVAILABLE;
                    }
                    AddonDictionaryImpl addonDictionaryImpl = new AddonDictionaryImpl(str2, Integer.parseInt(props.get(MessageAPI.CATEGORY_ID)), props.get(MessageAPI.NAME), props.get(MessageAPI.DESCRIPTION), Integer.parseInt(props.get(MessageAPI.LANGUAGE_ID)), parseInt, props.get(MessageAPI.NAME_TRANSLATED), props.get(MessageAPI.DESCRIPTION_TRANSLATED));
                    addonDictionaryImpl.setStatus(str);
                    if (this.availableDownloadDictionaries.containsKey(str2)) {
                        String status = this.availableDownloadDictionaries.get(str2).getStatus();
                        z = status.equals(Strings.STATUS_CANCELED) && this.redownloadCallbacks.containsKey(str2);
                        if (status.equals(Strings.STATUS_UNINSTALL_PENDING) && !addonDictionaryImpl.getStatus().equals(Strings.STATUS_AVAILABLE)) {
                            z = true;
                        }
                    } else {
                        z = false;
                    }
                    if (addonDictionaryImpl.getCategory() == null || "".equals(addonDictionaryImpl.getCategory()) || addonDictionaryImpl.getName() == null || "".equals(addonDictionaryImpl.getName())) {
                        log.d("Chinese Dictionary being filtered (", addonDictionaryImpl.getDictionary(), ")");
                        z = true;
                    }
                    if (!z) {
                        this.availableDownloadDictionaries.put(str2, addonDictionaryImpl);
                    }
                } catch (NumberFormatException e3) {
                    log.e("Error Processing Chinese Dictionaries: " + e3.getMessage());
                }
            }
        }
        this.dictionaryListReceived = true;
        notifyDictionariesofStatus();
        this.connectService.dispatchAction(ActionFilterStrings.ACTION_DATA_AVAILABLE, ActionFilterStrings.TYPE_CATEGORY_DATA);
    }

    private void rebuildSkuAvailableList() {
        this.catalogLock.writeLock().lock();
        try {
            this.catalogAvailableSkuSet.clear();
            this.catalogAvailableLabelMap.clear();
            String currentDeviceCountry = getCurrentDeviceCountry();
            for (CatalogItemImpl catalogItemImpl : this.lazyLoadCatalogItems.values()) {
                if (catalogItemImpl.isAvailable(currentDeviceCountry)) {
                    this.catalogAvailableSkuSet.add(catalogItemImpl.getSKU());
                    Iterator<String> it = catalogItemImpl.getCategoryList().iterator();
                    while (it.hasNext()) {
                        this.catalogAvailableLabelMap.put(it.next(), catalogItemImpl.getType());
                    }
                }
            }
        } finally {
            this.catalogLock.writeLock().unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveDeviceCountry(String str, long j) {
        this.connectService.getUserSettings().setLocationCountry(str);
        this.connectService.getUserSettings().setLocationCountryTimestamp(j);
    }

    private void savePurchasedSkuList() {
        this.connectService.getUserSettings().setCatalogPurchasedSKUList(this.catalogPurchasedSkuList);
    }

    private Calendar setDateToEPOC(String str) {
        Calendar calendar = Calendar.getInstance();
        if (str == null || str.length() == 0 || str.contains("null") || str.equals("0")) {
            return null;
        }
        try {
            calendar.setTimeInMillis(Long.parseLong(str) * 1000);
            return calendar;
        } catch (Exception e) {
            log.e("Error Creating creating calendar", str, " error: ", e.getMessage());
            return calendar;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLivingLanguageStatus(boolean z) {
        log.d("setLivingLanguageStatus(", Boolean.valueOf(z), ")");
        if (!livingLanguageAvailable()) {
            z = false;
        }
        this.livingLanguageStatus = z;
        this.livingLanguageUDAStatus = z;
        this.livingLanguageHotwordsStatus = z;
        this.connectService.getUserSettings().setLivingLanguageEnabled(z);
        this.mHandler.removeCallbacks(this.sendHotwordsStatus);
        this.mHandler.postDelayed(this.sendHotwordsStatus, 15000L);
    }

    private List<String> stringToList(String str) {
        return str == null ? new ArrayList() : Arrays.asList(str.split(","));
    }

    private void subscribe(String str) {
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_CATEGORY_DB_SUBSCRIBE_OR_DOWNLOAD, str, 0, 0);
    }

    private void subscribeNoDownload(String str) {
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_CATEGORY_DB_SUBSCRIBE_NO_DOWNLOAD, str, 0, 0);
    }

    private void subscribeNoDownload(List<String> list) {
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_CATEGORY_DB_SUBSCRIBE_NO_DOWNLOAD_LIST, StringUtils.implode(list, ","), 0, 0);
    }

    private void unsubscribe(String str) {
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_CATEGORY_DB_UNSUBSCRIBE, str, 0, 0);
    }

    @Override // com.nuance.connect.api.AddonDictionariesService
    public void cancelDownload(String str) {
        if (!this.availableDownloadDictionaries.containsKey(str)) {
            this.oemLog.e("cannot cancel dict: ", str);
            return;
        }
        synchronized (this.redownloadCallbacks) {
            this.redownloadCallbacks.remove(str);
        }
        this.availableDownloadDictionaries.get(str).setStatus(Strings.STATUS_CANCELED);
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_CATEGORY_DB_CANCEL, str, this.availableDownloadDictionaries.get(str).wasInstalled() ? 7 : 0, 0);
        this.oemLog.d("canceling unsubscribing dict=", str);
    }

    @Override // com.nuance.connect.api.CatalogService
    public void cancelDownloadCatalogItem(String str) {
        log.d("CategoryServiceInternal.cancelDownloadCatalogItem(", str, ")");
        if (str == null || "".equals(str)) {
            this.oemLog.e("CatalogItem cannot cancel catalog download for empty sku");
            return;
        }
        synchronized (this.catalogDownloadCallbacks) {
            if (this.catalogDownloadCallbacks.containsKey(str)) {
                synchronized (this.catalogRedownloadCallbacks) {
                    if (this.catalogRedownloadCallbacks.containsKey(str)) {
                        this.catalogRedownloadCallbacks.remove(str);
                    }
                }
                String categoryIdFromSKU = getCategoryIdFromSKU(str);
                if (categoryIdFromSKU == null || "".equals(categoryIdFromSKU)) {
                    this.oemLog.e("CatalogItem cannot cancel catalog download for empty ID");
                } else {
                    this.database.setProp(categoryIdFromSKU, Strings.MAP_KEY_STEP, 8);
                    this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_CATALOG_CANCEL, categoryIdFromSKU, 0, 0);
                    this.oemLog.d("CatalogItem canceling sku=", str);
                }
            } else {
                this.oemLog.e("CatalogItem cannot cancel catalog download for sku not being downloaded: ", str);
            }
        }
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public void cancelLivingDownloads() {
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_CATEGORY_LIVING_LANGUAGE_CANCEL);
    }

    @Override // com.nuance.connect.api.CatalogService
    public void disableCatalog() {
        this.connectService.getUserSettings().setCatalogEnabled(false);
        this.catalogStatus = false;
        this.mHandler.removeCallbacks(this.sendCatalogStatus);
        this.mHandler.postDelayed(this.sendCatalogStatus, this.changedCatalogFlagCount * 3000);
        this.changedCatalogFlagCount *= 3;
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public void disableLivingLanguage() {
        setLivingLanguageStatus(false);
    }

    @Override // com.nuance.connect.api.CatalogService
    public void downloadCatalogItem(String str, CatalogService.CatalogItemDownloadCallback catalogItemDownloadCallback) {
        log.d("CategoryServiceInternal.downloadCatalogItem(", str, ")");
        if (str == null || "".equals(str)) {
            this.oemLog.e("CatalogItem cannot download catalog for empty sku");
            return;
        }
        synchronized (this.catalogDownloadCallbacks) {
            if (this.catalogDownloadCallbacks.containsKey(str)) {
                this.oemLog.e("CatalogItem cannot download SKU already downloading: ", str);
            } else {
                String categoryIdFromSKU = getCategoryIdFromSKU(str);
                if (categoryIdFromSKU == null || "".equals(categoryIdFromSKU)) {
                    this.oemLog.e("CatalogItem cannot download catalog for empty ID");
                } else {
                    CatalogItemImpl catalogItem = getCatalogItem(categoryIdFromSKU);
                    String status = catalogItem.getStatus();
                    this.oemLog.d("install CatalogItem dict=", str, " status=", status, " type=", catalogItem.getType(), " downloadable=", Boolean.valueOf(catalogItem.isDownloadable()), " catId=", catalogItem.getCategoryId());
                    if (CatalogService.CatalogItem.Type.BUNDLE.equals(catalogItem.getType())) {
                        catalogItemDownloadCallback.downloadStopped(9);
                    } else if (!catalogItem.isDownloadable()) {
                        catalogItemDownloadCallback.downloadStopped(9);
                    } else if (status.equals(Strings.STATUS_AVAILABLE) || !status.equals(Strings.STATUS_CANCELED)) {
                        synchronized (this.catalogDownloadCallbacks) {
                            this.catalogDownloadCallbacks.put(str, catalogItemDownloadCallback);
                        }
                        catalogItem.download();
                        subscribe(catalogItem.getCategoryId());
                    } else {
                        synchronized (this.catalogRedownloadCallbacks) {
                            this.catalogRedownloadCallbacks.put(str, catalogItemDownloadCallback);
                        }
                    }
                }
            }
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public void enableCatalog() {
        this.connectService.getUserSettings().setCatalogEnabled(true);
        this.catalogStatus = true;
        this.mHandler.removeCallbacks(this.sendCatalogStatus);
        this.mHandler.postDelayed(this.sendCatalogStatus, this.changedCatalogFlagCount * 3000);
        this.changedCatalogFlagCount *= 3;
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public void enableLivingLanguage() {
        setLivingLanguageStatus(true);
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public void forcePendingLivingToForeground() {
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_CATEGORY_LIVING_LANGUAGE_FOREGROUND);
    }

    @Override // com.nuance.connect.api.AddonDictionariesService
    public List<AddonDictionariesService.AddonDictionary> getAvailableDictionaries() {
        return new ArrayList(this.availableDownloadDictionaries.values());
    }

    @Override // com.nuance.connect.api.CatalogService
    public List<CatalogService.CatalogItem> getCatalogItemByCategory(String str) {
        ArrayList arrayList = new ArrayList();
        Lock readLock = this.catalogLock.readLock();
        readLock.lock();
        try {
            Map<String, Map<String, String>> categoryIdsForSKUs = getCategoryIdsForSKUs();
            for (String str2 : this.catalogAvailableSkuSet) {
                String categoryIdForSKU = getCategoryIdForSKU(categoryIdsForSKUs, str2);
                if (categoryIdForSKU == null) {
                    log.d("Could not find SKU: ", str2);
                } else {
                    CatalogItemImpl catalogItem = getCatalogItem(categoryIdForSKU);
                    if (catalogItem != null && catalogItem.inCategory(str)) {
                        arrayList.add(catalogItem);
                    }
                }
            }
            return arrayList;
        } finally {
            readLock.unlock();
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public List<String> getCatalogItemCategories() {
        this.catalogLock.writeLock().lock();
        try {
            HashSet hashSet = new HashSet(this.catalogAvailableLabelMap.keySet());
            this.catalogLock.writeLock().unlock();
            return getCategoryListFromKeySet(hashSet);
        } catch (Throwable th) {
            this.catalogLock.writeLock().unlock();
            throw th;
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public List<String> getCatalogItemCategoriesByType(List<CatalogService.CatalogItem.Type> list) {
        ArrayList arrayList = new ArrayList();
        this.catalogLock.writeLock().lock();
        try {
            for (Map.Entry<String, CatalogService.CatalogItem.Type> entry : this.catalogAvailableLabelMap.entrySet()) {
                if (list.contains(entry.getValue())) {
                    arrayList.add(entry.getKey());
                }
            }
            return arrayList;
        } finally {
            this.catalogLock.writeLock().unlock();
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public List<CatalogService.CatalogItem> getCatalogItems() {
        HashMap hashMap = new HashMap();
        Lock readLock = this.catalogLock.readLock();
        readLock.lock();
        try {
            for (Map.Entry<String, CatalogItemImpl> entry : this.skuToCatalogItems.entrySet()) {
                if (this.catalogAvailableSkuSet.contains(entry.getKey())) {
                    log.d("SKU: ", entry.getKey(), " is available.");
                    hashMap.put(entry.getKey(), entry.getValue());
                } else {
                    log.d("SKU: ", entry.getKey(), " is NOT available.");
                }
            }
            readLock.unlock();
            return new ArrayList(hashMap.values());
        } catch (Throwable th) {
            readLock.unlock();
            throw th;
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public List<CatalogService.CatalogItem> getCatalogItemsByType(List<CatalogService.CatalogItem.Type> list) {
        ArrayList arrayList = new ArrayList();
        Lock readLock = this.catalogLock.readLock();
        readLock.lock();
        try {
            Iterator<String> it = this.catalogAvailableSkuSet.iterator();
            while (it.hasNext()) {
                CatalogItemImpl catalogItemImpl = this.skuToCatalogItems.get(it.next());
                if (catalogItemImpl != null && list.contains(catalogItemImpl.getType())) {
                    arrayList.add(catalogItemImpl);
                }
            }
            return arrayList;
        } finally {
            readLock.unlock();
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public String getCategoryKeyForCategoryName(String str) {
        String str2;
        if (str == null) {
            return null;
        }
        synchronized (this.catalogLabelList) {
            Iterator<Map.Entry<String, String>> it = this.catalogLabelList.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    str2 = null;
                    break;
                }
                Map.Entry<String, String> next = it.next();
                if (str.equals(next.getValue())) {
                    str2 = next.getKey();
                    break;
                }
            }
        }
        return str2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectFeature[] getDependencies() {
        return ConnectFeature.CATEGORY.values();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectHandler[] getHandlers() {
        return new ConnectHandler[]{this.handler};
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public int getMaxNumberOfLivingEvents() {
        return this.connectService.getUserSettings().getMaxNumberOfEvents();
    }

    @Override // com.nuance.connect.api.CatalogService
    public List<String> getSKUList() {
        ArrayList arrayList = new ArrayList();
        log.d("CategoryServiceInternal.getSKUList()");
        Lock readLock = this.catalogLock.readLock();
        readLock.lock();
        try {
            arrayList.addAll(this.catalogAvailableSkuSet);
            return arrayList;
        } finally {
            readLock.unlock();
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public List<String> getSKUListForPurchase() {
        ArrayList arrayList = new ArrayList();
        log.d("CategoryServiceInternal.getSKUListForPurchase()");
        this.catalogLock.readLock().lock();
        try {
            arrayList.addAll(this.catalogSkuForPurchaseMap.keySet());
            return arrayList;
        } finally {
            this.catalogLock.readLock().unlock();
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public List<String> getSKUListForPurchaseByType(List<CatalogService.CatalogItem.Type> list) {
        this.catalogLock.readLock().lock();
        try {
            log.d("catalogSkuForPurchaseMap: ", this.catalogSkuForPurchaseMap);
            if (list == null) {
                log.e("CategoryServiceInternal.getSKUListForPurchaseByType() given null list");
                return Collections.emptyList();
            }
            ArrayList arrayList = new ArrayList();
            this.catalogLock.readLock().lock();
            try {
                for (Map.Entry<String, CatalogService.CatalogItem.Type> entry : this.catalogSkuForPurchaseMap.entrySet()) {
                    if (list.contains(entry.getValue())) {
                        arrayList.add(entry.getKey());
                    }
                }
                return arrayList;
            } finally {
            }
        } finally {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public String getServiceName() {
        return ConnectFeature.CATEGORY.name();
    }

    @Override // com.nuance.connect.api.AddonDictionariesService
    public void installDictionary(String str, AddonDictionariesService.AddonDictionaryDownloadCallback addonDictionaryDownloadCallback) {
        if (!this.availableDownloadDictionaries.containsKey(str)) {
            this.oemLog.e("cannot install dict: ", str);
            return;
        }
        String status = this.availableDownloadDictionaries.get(str).getStatus();
        this.oemLog.d("installDictionary dict=", str, " status=", status);
        if (!status.equals(Strings.STATUS_AVAILABLE) && status.equals(Strings.STATUS_CANCELED)) {
            synchronized (this.redownloadCallbacks) {
                this.redownloadCallbacks.put(str, addonDictionaryDownloadCallback);
            }
        } else {
            synchronized (this.dictionaryDownloadCallbacks) {
                this.dictionaryDownloadCallbacks.put(str, addonDictionaryDownloadCallback);
            }
            this.availableDownloadDictionaries.get(str).download();
            subscribe(str);
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public void installedCatalogItem(String str) {
        log.d("installedCatalogItem sku=", str);
        String categoryIdFromSKU = getCategoryIdFromSKU(str);
        this.database.setProp(categoryIdFromSKU, Strings.MAP_KEY_STEP, 7);
        CatalogItemImpl catalogItem = getCatalogItem(categoryIdFromSKU);
        if (catalogItem != null) {
            catalogItem.setStatus(Strings.STATUS_INSTALLED);
            if (catalogItem.isSubscribable() && !catalogItem.isSubscribed()) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(catalogItem.getCategoryId());
                subscribeNoDownload(arrayList);
            }
        }
        notifyCatalogCallbackofItemsChanged();
    }

    @Override // com.nuance.connect.api.CatalogService
    public boolean isCatalogEnabled() {
        return this.catalogStatus;
    }

    @Override // com.nuance.connect.api.CatalogService
    public boolean isCatalogListAvailable() {
        return this.catalogListReceived;
    }

    @Override // com.nuance.connect.api.AddonDictionariesService
    public boolean isDictionaryListAvailable() {
        return this.dictionaryListReceived;
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public boolean isHotWordsEnabled() {
        return this.livingLanguageHotwordsStatus;
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public boolean isLivingLanguageEnabled() {
        return this.livingLanguageStatus;
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public boolean isUDAEnabled() {
        return this.livingLanguageUDAStatus;
    }

    public boolean livingLanguageAllowed() {
        return livingLanguageAvailable() && isLivingLanguageEnabled();
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public boolean livingLanguageAvailable() {
        return this.livingLanguageAvailable;
    }

    @Override // com.nuance.connect.api.AddonDictionariesService
    public void markDictionaryInstalled(String str) {
        if (!this.availableDownloadDictionaries.containsKey(str)) {
            this.oemLog.e("dictionary is not available to mark as installed.  Dict: ", str);
            return;
        }
        AddonDictionaryImpl addonDictionaryImpl = this.availableDownloadDictionaries.get(str);
        if (addonDictionaryImpl.isSubscribed()) {
            return;
        }
        addonDictionaryImpl.setStatus(Strings.STATUS_INSTALLED);
        subscribeNoDownload(str);
    }

    @Override // com.nuance.connect.api.AddonDictionariesService
    public void notifyDictionariesofStatus() {
        for (AddonDictionariesService.AddonDictionaryListCallback addonDictionaryListCallback : (AddonDictionariesService.AddonDictionaryListCallback[]) this.dictionaryListCallbacks.toArray(new AddonDictionariesService.AddonDictionaryListCallback[0])) {
            addonDictionaryListCallback.listUpdated();
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public void registerCatalogCallback(CatalogService.CatalogCallback catalogCallback) {
        this.catalogListCallbacks.add(catalogCallback);
    }

    @Override // com.nuance.connect.api.AddonDictionariesService
    public void registerDictionaryListCallback(AddonDictionariesService.AddonDictionaryListCallback addonDictionaryListCallback) {
        if (this.dictionaryListCallbacks.add(addonDictionaryListCallback)) {
            return;
        }
        log.d("registerDictionaryListCallback callback already added");
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public void registerLivingCallback(LivingLanguageService.Callback callback) {
        this.livingLanguageCallbacks.add(callback);
    }

    @Override // com.nuance.connect.api.AddonDictionariesService
    public void removeDictionary(String str) {
        if (!this.availableDownloadDictionaries.containsKey(str)) {
            this.oemLog.e("cannot install dict: ", str);
        } else if (!this.availableDownloadDictionaries.get(str).isInstalled()) {
            this.oemLog.e("dictionary is not installed cannot uninstall.  Dict: ", str);
        } else {
            this.availableDownloadDictionaries.get(str).setStatus(Strings.STATUS_UNINSTALL_PENDING);
            unsubscribe(str);
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public void resetCatalogItemPrice() {
        this.handlerThread.process(CategoryEvents.ON_CATALOG_PRICES_RESET.ordinal(), null);
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0073  */
    /* JADX WARN: Removed duplicated region for block: B:25:? A[RETURN, SYNTHETIC] */
    @Override // com.nuance.connect.api.CatalogService
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void resetPurchasedSKU() {
        /*
            r5 = this;
            r2 = 1
            r1 = 0
            java.util.Set<java.lang.String> r4 = r5.catalogPurchasedSkuList
            monitor-enter(r4)
            java.util.Set<java.lang.String> r0 = r5.catalogPurchasedSkuList     // Catch: java.lang.Throwable -> L52
            boolean r0 = r0.isEmpty()     // Catch: java.lang.Throwable -> L52
            if (r0 != 0) goto L50
            r3 = r2
        Le:
            java.util.Set<java.lang.String> r0 = r5.catalogPurchasedSkuList     // Catch: java.lang.Throwable -> L52
            r0.clear()     // Catch: java.lang.Throwable -> L52
            r5.savePurchasedSkuList()     // Catch: java.lang.Throwable -> L52
            monitor-exit(r4)     // Catch: java.lang.Throwable -> L52
            java.util.concurrent.locks.ReadWriteLock r0 = r5.catalogLock
            java.util.concurrent.locks.Lock r0 = r0.writeLock()
            r0.lock()
            java.util.Set<java.lang.String> r0 = r5.catalogAllPurchasedSkuSet     // Catch: java.lang.Throwable -> L45
            r0.clear()     // Catch: java.lang.Throwable -> L45
            java.util.concurrent.ConcurrentHashMap<java.lang.String, com.nuance.connect.internal.CategoryServiceInternal$CatalogItemImpl> r0 = r5.lazyLoadCatalogItems     // Catch: java.lang.Throwable -> L45
            java.util.Set r0 = r0.entrySet()     // Catch: java.lang.Throwable -> L45
            java.util.Iterator r4 = r0.iterator()     // Catch: java.lang.Throwable -> L45
        L2f:
            boolean r0 = r4.hasNext()     // Catch: java.lang.Throwable -> L45
            if (r0 == 0) goto L55
            java.lang.Object r0 = r4.next()     // Catch: java.lang.Throwable -> L45
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch: java.lang.Throwable -> L45
            java.lang.Object r0 = r0.getValue()     // Catch: java.lang.Throwable -> L45
            com.nuance.connect.internal.CategoryServiceInternal$CatalogItemImpl r0 = (com.nuance.connect.internal.CategoryServiceInternal.CatalogItemImpl) r0     // Catch: java.lang.Throwable -> L45
            r0.resetPurchased()     // Catch: java.lang.Throwable -> L45
            goto L2f
        L45:
            r0 = move-exception
            java.util.concurrent.locks.ReadWriteLock r1 = r5.catalogLock
            java.util.concurrent.locks.Lock r1 = r1.writeLock()
            r1.unlock()
            throw r0
        L50:
            r3 = r1
            goto Le
        L52:
            r0 = move-exception
            monitor-exit(r4)     // Catch: java.lang.Throwable -> L52
            throw r0
        L55:
            if (r3 != 0) goto L5f
            java.util.Set<java.lang.String> r0 = r5.catalogAllPurchasedSkuSet     // Catch: java.lang.Throwable -> L45
            boolean r0 = r0.isEmpty()     // Catch: java.lang.Throwable -> L45
            if (r0 != 0) goto L7d
        L5f:
            r0 = r2
        L60:
            java.util.concurrent.locks.ReadWriteLock r1 = r5.catalogLock
            java.util.concurrent.locks.Lock r1 = r1.writeLock()
            r1.unlock()
            com.nuance.connect.util.Logger$Log r1 = com.nuance.connect.internal.CategoryServiceInternal.log
            java.lang.String r2 = "resetPurchasedSKU()"
            r1.d(r2)
            if (r0 == 0) goto L7c
            r5.rebuildSkuAvailableList()
            r5.notifyCatalogCallbackOfSkusChanged()
            r5.notifyCatalogCallbackofItemsChanged()
        L7c:
            return
        L7d:
            r0 = r1
            goto L60
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.internal.CategoryServiceInternal.resetPurchasedSKU():void");
    }

    @Override // com.nuance.connect.api.CatalogService
    public void setCatalogItemPrices(Map<String, String> map) {
        this.handlerThread.removeMessages(CategoryEvents.ON_CATALOG_PRICES_SET.ordinal());
        this.handlerThread.process(CategoryEvents.ON_CATALOG_PRICES_SET.ordinal(), map);
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public void setLivingLanguageAvailable(boolean z) {
        this.livingLanguageAvailable = z;
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public void setLivingLanguageStatus(boolean z, boolean z2) {
        if (z || z2) {
            this.livingLanguageStatus = true;
            this.livingLanguageUDAStatus = z;
            this.livingLanguageHotwordsStatus = z2;
        } else {
            this.livingLanguageStatus = false;
            this.livingLanguageUDAStatus = false;
            this.livingLanguageHotwordsStatus = false;
        }
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public void setMaxNumberOfLivingEvents(int i) {
        this.connectService.getUserSettings().setMaxNumberOfEvents(i);
        this.mHandler.removeCallbacks(this.sendMaxLimit);
        this.mHandler.postDelayed(this.sendMaxLimit, 3000L);
    }

    @Override // com.nuance.connect.api.CatalogService
    public void setPurchasedSKU(String str) {
        if (str != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(str);
            setPurchasedSKUList(arrayList);
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public void setPurchasedSKUList(List<String> list) {
        boolean z;
        if (list == null) {
            list = Collections.emptyList();
        }
        synchronized (this.catalogPurchasedSkuList) {
            z = (this.catalogPurchasedSkuList.containsAll(list) && list.containsAll(this.catalogPurchasedSkuList)) ? false : true;
            this.catalogPurchasedSkuList.addAll(list);
        }
        if (z) {
            String currentDeviceCountry = getCurrentDeviceCountry();
            this.catalogLock.writeLock().lock();
            try {
                for (String str : list) {
                    CatalogItemImpl catalogItemImpl = this.skuToCatalogItems.get(str);
                    if (catalogItemImpl != null) {
                        catalogItemImpl.setPurchased();
                        this.catalogAllPurchasedSkuSet.add(str);
                        this.catalogAllPurchasedSkuSet.addAll(catalogItemImpl.getAllAvailableSKUs(currentDeviceCountry));
                    }
                }
                this.catalogLock.writeLock().unlock();
                rebuildSkuAvailableList();
                savePurchasedSkuList();
                notifyCatalogCallbackofItemsChanged();
                notifyCatalogCallbackOfSkusChanged();
                this.handlerThread.processDelayed(this.sendCatalogPurchasedSKUs, 1000L, true);
            } catch (Throwable th) {
                this.catalogLock.writeLock().unlock();
                throw th;
            }
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public void skuListAvailable(List<String> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        log.d("CategoryServiceInternal.skuListAvailable() - " + list.toString());
        synchronized (this.catalogSKUListAvailableCheck) {
            this.catalogSKUListAvailableCheck.addAll(list);
            this.handlerThread.processDelayed(this.processCatalogSKUListAvailable, 10L, true);
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public void uninstallCatalogItem(String str) {
        log.d("CategoryServiceInternal.uninstallCatalogItem(", str, ")");
        if (str == null || "".equals(str)) {
            this.oemLog.e("uninstallCatalogItem has empty sku");
            return;
        }
        String categoryIdFromSKU = getCategoryIdFromSKU(str);
        if (categoryIdFromSKU == null || "".equals(categoryIdFromSKU)) {
            this.oemLog.e("uninstallCatalogItem for empty ID");
        } else {
            unsubscribe(categoryIdFromSKU);
            this.database.setProp(getCategoryIdFromSKU(str), Strings.MAP_KEY_STEP, 10);
        }
    }

    @Override // com.nuance.connect.api.CatalogService
    public void unregisterCatalogCallback(CatalogService.CatalogCallback catalogCallback) {
        this.catalogListCallbacks.remove(catalogCallback);
    }

    @Override // com.nuance.connect.api.CatalogService
    public void unregisterCatalogCallbacks() {
        this.catalogListCallbacks.clear();
    }

    @Override // com.nuance.connect.api.AddonDictionariesService
    public void unregisterDictionaryListCallback(AddonDictionariesService.AddonDictionaryListCallback addonDictionaryListCallback) {
        if (this.dictionaryListCallbacks.remove(addonDictionaryListCallback)) {
            return;
        }
        log.d("registerDictionaryListCallback callback does not exist");
    }

    @Override // com.nuance.connect.api.AddonDictionariesService
    public void unregisterDictionaryListCallbacks() {
        this.dictionaryListCallbacks.clear();
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public void unregisterLivingCallback(LivingLanguageService.Callback callback) {
        this.livingLanguageCallbacks.remove(callback);
    }

    @Override // com.nuance.connect.api.LivingLanguageService
    public void unregisterLivingCallbacks() {
        this.livingLanguageCallbacks.clear();
    }
}
