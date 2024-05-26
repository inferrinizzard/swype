package com.nuance.swypeconnect.ac;

import android.annotation.SuppressLint;
import com.facebook.internal.AnalyticsEvents;
import com.nuance.connect.api.CatalogService;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACCatalogService;
import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class ACCatalogServiceBase extends ACService implements ACCatalogService {
    private CatalogService catalogService;
    private ACManager manager;
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.OEM);
    private final Logger.Log customer = Logger.getLog(Logger.LoggerType.CUSTOMER);
    private final ConcurrentCallbackSet<ACCatalogService.ACCatalogCallback> listCallbacks = new ConcurrentCallbackSet<>();
    private final CatalogService.CatalogCallback CatalogCallbackAdapter = new CatalogService.CatalogCallback() { // from class: com.nuance.swypeconnect.ac.ACCatalogServiceBase.1
        @Override // com.nuance.connect.api.CatalogService.CatalogCallback
        public void catalogItemInstalledUpdates(List<CatalogService.CatalogItem> list) {
            ACCatalogServiceBase.this.log.d("CatalogCallbackAdapter.catalogItemInstalledUpdates() called");
            ArrayList arrayList = new ArrayList();
            Iterator<CatalogService.CatalogItem> it = list.iterator();
            while (it.hasNext()) {
                arrayList.add(new ACCatalogItemImpl(it.next()));
            }
            for (ACCatalogService.ACCatalogCallback aCCatalogCallback : (ACCatalogService.ACCatalogCallback[]) ACCatalogServiceBase.this.listCallbacks.toArray(new ACCatalogService.ACCatalogCallback[0])) {
                aCCatalogCallback.catalogItemInstalledUpdates(arrayList);
            }
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogCallback
        public void catalogItemListChanged() {
            ACCatalogServiceBase.this.log.d("CatalogCallbackAdapter.catalogItemListChanged() called");
            for (ACCatalogService.ACCatalogCallback aCCatalogCallback : (ACCatalogService.ACCatalogCallback[]) ACCatalogServiceBase.this.listCallbacks.toArray(new ACCatalogService.ACCatalogCallback[0])) {
                aCCatalogCallback.catalogItemListChanged();
            }
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogCallback
        public void catalogSKUListChanged(List<String> list) {
            ACCatalogServiceBase.this.log.d("CatalogCallbackAdapter.catalogSKUListChanged() called");
            for (ACCatalogService.ACCatalogCallback aCCatalogCallback : (ACCatalogService.ACCatalogCallback[]) ACCatalogServiceBase.this.listCallbacks.toArray(new ACCatalogService.ACCatalogCallback[0])) {
                aCCatalogCallback.catalogSKUListChanged(list);
            }
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogCallback
        public void skuListAvailable(List<String> list) {
            for (ACCatalogService.ACCatalogCallback aCCatalogCallback : (ACCatalogService.ACCatalogCallback[]) ACCatalogServiceBase.this.listCallbacks.toArray(new ACCatalogService.ACCatalogCallback[0])) {
                aCCatalogCallback.skuListAvailable(list);
            }
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogCallback
        public void skuListUnavailable(List<String> list) {
            for (ACCatalogService.ACCatalogCallback aCCatalogCallback : (ACCatalogService.ACCatalogCallback[]) ACCatalogServiceBase.this.listCallbacks.toArray(new ACCatalogService.ACCatalogCallback[0])) {
                aCCatalogCallback.skuListUnavailable(list);
            }
        }
    };

    /* loaded from: classes.dex */
    static class ACCatalogItemBundleImpl extends ACCatalogItemImpl implements ACCatalogService.ACCatalogItemBundle {
        private final CatalogService.CatalogItemBundle itemBundle;

        ACCatalogItemBundleImpl(CatalogService.CatalogItemBundle catalogItemBundle) {
            super(catalogItemBundle);
            this.itemBundle = catalogItemBundle;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItemBundle
        public List<CatalogService.CatalogItem> getBundledBaseCatalogItemList() {
            return this.itemBundle.getBundledBaseCatalogItemList();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItemBundle
        public List<ACCatalogService.ACCatalogItem> getBundledCatalogItemList() {
            List<CatalogService.CatalogItem> bundledBaseCatalogItemList = getBundledBaseCatalogItemList();
            ArrayList arrayList = new ArrayList();
            for (CatalogService.CatalogItem catalogItem : bundledBaseCatalogItemList) {
                switch (catalogItem.getType()) {
                    case KEYBOARD:
                        arrayList.add(new ACCatalogItemImpl(catalogItem));
                        break;
                    case BUNDLE:
                        arrayList.add(new ACCatalogItemBundleImpl((CatalogService.CatalogItemBundle) catalogItem));
                        break;
                }
            }
            return arrayList;
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItemBundle, com.nuance.connect.api.CatalogService.CatalogItemBundle
        public List<String> getBundledCatalogItemSKUList() {
            return this.itemBundle.getBundledCatalogItemSKUList();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItemBundle, com.nuance.connect.api.CatalogService.CatalogItemBundle
        public List<CatalogService.CatalogItem.Type> getBundledCatalogItemTypes() {
            return this.itemBundle.getBundledCatalogItemTypes();
        }
    }

    /* loaded from: classes.dex */
    private static class ACCatalogItemComparator implements Comparator<ACCatalogService.ACCatalogItem> {
        private String category;
        private final Collator collator = Collator.getInstance();

        ACCatalogItemComparator() {
        }

        @Override // java.util.Comparator
        @SuppressLint({"DefaultLocale"})
        public int compare(ACCatalogService.ACCatalogItem aCCatalogItem, ACCatalogService.ACCatalogItem aCCatalogItem2) {
            if (this.category == null || this.category.length() == 0) {
                return this.collator.compare(aCCatalogItem.getTitle() != null ? aCCatalogItem.getTitle().toUpperCase() : "", aCCatalogItem2.getTitle() != null ? aCCatalogItem2.getTitle().toUpperCase() : "");
            }
            if (aCCatalogItem.getWeight(this.category) < aCCatalogItem2.getWeight(this.category)) {
                return 1;
            }
            if (aCCatalogItem2.getWeight(this.category) < aCCatalogItem.getWeight(this.category)) {
                return -1;
            }
            return this.collator.compare(aCCatalogItem.getTitle() != null ? aCCatalogItem.getTitle().toUpperCase() : "", aCCatalogItem2.getTitle() != null ? aCCatalogItem2.getTitle().toUpperCase() : "");
        }

        public void setCategory(String str) {
            this.category = str;
        }
    }

    /* loaded from: classes.dex */
    static class ACCatalogItemImpl implements ACCatalogService.ACCatalogItem {
        private final CatalogService.CatalogItem item;

        ACCatalogItemImpl(CatalogService.CatalogItem catalogItem) {
            this.item = catalogItem;
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public String getCanonicalName() {
            return this.item.getCanonicalName();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public List<String> getCategoryList() {
            return this.item.getCategoryList();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public Map<String, Integer> getCategoryWeight() {
            return this.item.getCategoryWeight();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public Calendar getDateAdded() {
            return this.item.getDateAdded();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public Calendar getDateAvailableEnd() {
            return this.item.getDateAvailableEnd();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public Calendar getDateAvailableStart() {
            return this.item.getDateAvailableStart();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public Calendar getDateFulfillEnd() {
            return this.item.getDateFulfillEnd();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public String getDescriptionLong() {
            return this.item.getDescriptionLong();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public String getDescriptionShort() {
            return this.item.getDescriptionShort();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public List<String> getPreviewURLList() {
            return this.item.getPreviewURLList();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public String getPrice() {
            return this.item.getPrice();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public String getSKU() {
            return this.item.getSKU();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public String getThumbnailURL() {
            return this.item.getThumbnailURL();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public String getTitle() {
            return this.item.getTitle();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public CatalogService.CatalogItem.Type getType() {
            return this.item.getType();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public Calendar getUpdatedDate() {
            return this.item.getUpdatedDate();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public int getWeight(String str) {
            return this.item.getWeight(str);
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public boolean isDownloadable() {
            return this.item.isDownloadable();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public boolean isFree() {
            return this.item.isFree();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public boolean isInstalled() {
            return this.item.isInstalled();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public boolean isPurchasable() {
            return this.item.isPurchasable();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public boolean isPurchased() {
            return this.item.isPurchased();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItem, com.nuance.connect.api.CatalogService.CatalogItem
        public boolean isUpdateAvailable() {
            return this.item.isUpdateAvailable();
        }
    }

    /* loaded from: classes.dex */
    private final class CatalogDownloadCallbackAdapter implements CatalogService.CatalogItemDownloadCallback {
        private final ACCatalogService.ACCatalogItemDownloadCallback callback;
        private final String sku;
        private boolean notifiedStart = false;
        private boolean notifiedPercentDone = false;

        public CatalogDownloadCallbackAdapter(String str, ACCatalogService.ACCatalogItemDownloadCallback aCCatalogItemDownloadCallback) {
            this.sku = str;
            this.callback = aCCatalogItemDownloadCallback;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItemDownloadCallback
        public final boolean downloadComplete(File file) {
            if (ACCatalogServiceBase.this.isShutdown) {
                return false;
            }
            if (!this.notifiedStart) {
                downloadStarted();
            }
            if (!this.notifiedPercentDone) {
                downloadPercentage(100);
            }
            ACCatalogServiceBase.this.manager.getConnect().updateFeatureLastUsed(FeaturesLastUsed.Feature.DLT, System.currentTimeMillis());
            boolean downloadComplete = this.callback.downloadComplete(file);
            ACCatalogServiceBase.this.customer.d("Catalog Item download Complete. sku:", this.sku, " Status: ", downloadComplete ? "Success" : AnalyticsEvents.PARAMETER_DIALOG_OUTCOME_VALUE_FAILED);
            if (downloadComplete) {
                ACCatalogServiceBase.this.catalogService.installedCatalogItem(this.sku);
            }
            return downloadComplete;
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItemDownloadCallback
        public final void downloadFailed(int i) {
            if (ACCatalogServiceBase.this.isShutdown) {
                return;
            }
            ACCatalogServiceBase.this.customer.d("Catalog Item Download failed. sku:", this.sku, " reason: ", Integer.valueOf(i));
            this.callback.downloadFailed(i);
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItemDownloadCallback
        public final void downloadPercentage(int i) {
            if (ACCatalogServiceBase.this.isShutdown) {
                return;
            }
            if (!this.notifiedStart) {
                downloadStarted();
            }
            if (i == 100) {
                this.notifiedPercentDone = true;
            }
            this.callback.downloadPercentage(i);
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItemDownloadCallback
        public final void downloadStarted() {
            if (ACCatalogServiceBase.this.isShutdown) {
                return;
            }
            ACCatalogServiceBase.this.customer.d("Catalog Item Download sku:", this.sku);
            this.notifiedStart = true;
            this.callback.downloadStarted();
        }

        @Override // com.nuance.connect.api.CatalogService.CatalogItemDownloadCallback
        public final void downloadStopped(int i) {
            if (ACCatalogServiceBase.this.isShutdown) {
                return;
            }
            ACCatalogServiceBase.this.customer.d("Catalog Item Download stopped. sku:", this.sku, " reason: ", Integer.valueOf(i));
            this.callback.downloadStopped(i);
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void cancelDownloadCatalogItem(String str) throws ACException {
        if (!isEnabled()) {
            throw new ACException(135, "Catalog Service is not enabled");
        }
        if (str == null) {
            throw new ACException(122, "SKU cannot be null");
        }
        if (!this.catalogService.getSKUList().contains(str)) {
            throw new ACException(122, "SKU not available for download");
        }
        this.catalogService.cancelDownloadCatalogItem(str);
    }

    protected List<CatalogService.CatalogItem.Type> convertACTypeToType(List<CatalogService.CatalogItem.Type> list) {
        ArrayList arrayList = new ArrayList();
        Iterator<CatalogService.CatalogItem.Type> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(CatalogService.CatalogItem.Type.valueOf(it.next().toString()));
        }
        return arrayList;
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void disableCatalog() {
        this.catalogService.disableCatalog();
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void downloadCatalogItem(String str, ACCatalogService.ACCatalogItemDownloadCallback aCCatalogItemDownloadCallback) throws ACException {
        if (!isEnabled()) {
            throw new ACException(135, "Catalog Service is not enabled");
        }
        if (str == null) {
            throw new ACException(122, "SKU cannot be null");
        }
        if (!this.catalogService.getSKUList().contains(str)) {
            throw new ACException(122, "SKU not available for download");
        }
        this.log.d("downloadCatalogItem sku=", str, " callback=", aCCatalogItemDownloadCallback);
        this.customer.d("downloadCatalogItem requested for sku: ", str);
        this.catalogService.downloadCatalogItem(str, new CatalogDownloadCallbackAdapter(str, aCCatalogItemDownloadCallback));
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void enableCatalog() {
        this.catalogService.enableCatalog();
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public List<ACCatalogService.ACCatalogItem> getCatalogItemByCategory(String str) {
        List<CatalogService.CatalogItem> catalogItemByCategory = this.catalogService.getCatalogItemByCategory(str);
        ArrayList arrayList = new ArrayList();
        if (!isEnabled()) {
            return arrayList;
        }
        for (CatalogService.CatalogItem catalogItem : catalogItemByCategory) {
            switch (catalogItem.getType()) {
                case KEYBOARD:
                    arrayList.add(new ACCatalogItemImpl(catalogItem));
                    break;
                case BUNDLE:
                    arrayList.add(new ACCatalogItemBundleImpl((CatalogService.CatalogItemBundle) catalogItem));
                    break;
            }
        }
        ACCatalogItemComparator aCCatalogItemComparator = new ACCatalogItemComparator();
        aCCatalogItemComparator.setCategory(str);
        Collections.sort(arrayList, aCCatalogItemComparator);
        return arrayList;
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public List<String> getCatalogItemCategories() {
        return isEnabled() ? this.catalogService.getCatalogItemCategories() : new ArrayList();
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public List<String> getCatalogItemCategoriesByType(List<CatalogService.CatalogItem.Type> list) {
        return isEnabled() ? this.catalogService.getCatalogItemCategoriesByType(convertACTypeToType(list)) : new ArrayList();
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public List<ACCatalogService.ACCatalogItem> getCatalogItems() {
        List<CatalogService.CatalogItem> catalogItems = this.catalogService.getCatalogItems();
        ArrayList arrayList = new ArrayList();
        if (!isEnabled()) {
            return arrayList;
        }
        for (CatalogService.CatalogItem catalogItem : catalogItems) {
            switch (catalogItem.getType()) {
                case KEYBOARD:
                    arrayList.add(new ACCatalogItemImpl(catalogItem));
                    break;
                case BUNDLE:
                    arrayList.add(new ACCatalogItemBundleImpl((CatalogService.CatalogItemBundle) catalogItem));
                    break;
            }
        }
        Collections.sort(arrayList, new ACCatalogItemComparator());
        return arrayList;
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public List<ACCatalogService.ACCatalogItem> getCatalogItemsByType(List<CatalogService.CatalogItem.Type> list) {
        List<CatalogService.CatalogItem> catalogItemsByType = this.catalogService.getCatalogItemsByType(convertACTypeToType(list));
        ArrayList arrayList = new ArrayList();
        if (!isEnabled()) {
            return arrayList;
        }
        for (CatalogService.CatalogItem catalogItem : catalogItemsByType) {
            switch (catalogItem.getType()) {
                case KEYBOARD:
                    arrayList.add(new ACCatalogItemImpl(catalogItem));
                    break;
                case BUNDLE:
                    arrayList.add(new ACCatalogItemBundleImpl((CatalogService.CatalogItemBundle) catalogItem));
                    break;
            }
        }
        Collections.sort(arrayList, new ACCatalogItemComparator());
        return arrayList;
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public String getCategoryKeyForCategoryName(String str) {
        return this.catalogService.getCategoryKeyForCategoryName(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public String getName() {
        return ACManager.CATALOG_SERVICE;
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public List<String> getSKUList() {
        return isEnabled() ? this.catalogService.getSKUList() : new ArrayList();
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public List<String> getSKUListForPurchase() {
        return isEnabled() ? this.catalogService.getSKUListForPurchase() : new ArrayList();
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public List<String> getSKUListForPurchaseByType(List<CatalogService.CatalogItem.Type> list) {
        return isEnabled() ? this.catalogService.getSKUListForPurchaseByType(convertACTypeToType(list)) : new ArrayList();
    }

    public void init(CatalogService catalogService, PersistentDataStore persistentDataStore, ACManager aCManager) {
        this.log.d("ACCatalogServiceBase init()");
        catalogService.registerCatalogCallback(this.CatalogCallbackAdapter);
        this.catalogService = catalogService;
        this.manager = aCManager;
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void installedCatalogItem(String str) throws ACException {
        if (!isEnabled()) {
            throw new ACException(135, "Catalog Service is not enabled");
        }
        if (str == null) {
            throw new ACException(122, "SKU cannot be null");
        }
        this.catalogService.installedCatalogItem(str);
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public boolean isCatalogListAvailable() {
        return this.catalogService.isCatalogListAvailable();
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public boolean isEnabled() {
        return this.catalogService.isCatalogEnabled();
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void registerCallback(ACCatalogService.ACCatalogCallback aCCatalogCallback) {
        this.listCallbacks.add(aCCatalogCallback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public boolean requireInitialization() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swypeconnect.ac.ACService
    public boolean requiresDocument(int i) {
        return i == 1;
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void resetCatalogItemPrice() {
        if (isEnabled()) {
            this.catalogService.resetCatalogItemPrice();
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void resetPurchasedSKU() {
        if (isEnabled()) {
            this.catalogService.resetPurchasedSKU();
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void setCatalogItemPrice(String str, String str2) {
        if (isEnabled()) {
            HashMap hashMap = new HashMap();
            hashMap.put(str, str2);
            this.catalogService.setCatalogItemPrices(hashMap);
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void setCatalogItemPrices(Map<String, String> map) {
        if (isEnabled()) {
            this.catalogService.setCatalogItemPrices(map);
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void setPurchasedSKU(String str) {
        if (isEnabled()) {
            this.catalogService.setPurchasedSKU(str);
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void setPurchasedSKUList(List<String> list) {
        if (isEnabled()) {
            this.catalogService.setPurchasedSKUList(list);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public void shutdown() {
        this.isShutdown = true;
        unregisterCallbacks();
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void skuAvailable(String str) {
        this.catalogService.skuListAvailable(Arrays.asList(str));
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void skuListAvailable(List<String> list) {
        this.catalogService.skuListAvailable(list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public void start() {
        this.isShutdown = false;
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void uninstallCatalogItem(String str) throws ACException {
        if (!isEnabled()) {
            throw new ACException(135, "Catalog Service is not enabled");
        }
        if (str == null) {
            throw new ACException(122, "SKU cannot be null");
        }
        this.catalogService.uninstallCatalogItem(str);
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void unregisterCallback(ACCatalogService.ACCatalogCallback aCCatalogCallback) {
        this.listCallbacks.remove(aCCatalogCallback);
    }

    @Override // com.nuance.swypeconnect.ac.ACCatalogService
    public void unregisterCallbacks() {
        this.listCallbacks.clear();
    }
}
