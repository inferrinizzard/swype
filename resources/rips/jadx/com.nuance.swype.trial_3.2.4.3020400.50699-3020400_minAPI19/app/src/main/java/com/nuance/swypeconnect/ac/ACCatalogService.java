package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.CatalogService;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public interface ACCatalogService {
    public static final int REASON_BUNDLE_NOT_DOWNLOADABLE = 9;
    public static final int REASON_FAILED_DISK_FULL = 6;
    public static final int REASON_FAILED_HTTP = 0;
    public static final int REASON_FAILED_MAX_RETRY = 1;
    public static final int REASON_NETWORK_TIMEOUT = 4;
    public static final int REASON_USER_CANCELED = 3;

    /* loaded from: classes.dex */
    public interface ACCatalogCallback {
        void catalogItemInstalledUpdates(List<ACCatalogItem> list);

        void catalogItemListChanged();

        void catalogSKUListChanged(List<String> list);

        void skuListAvailable(List<String> list);

        void skuListUnavailable(List<String> list);
    }

    /* loaded from: classes.dex */
    public interface ACCatalogItem extends CatalogService.CatalogItem {
        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        String getCanonicalName();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        List<String> getCategoryList();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        Map<String, Integer> getCategoryWeight();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        Calendar getDateAdded();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        Calendar getDateAvailableEnd();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        Calendar getDateAvailableStart();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        Calendar getDateFulfillEnd();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        String getDescriptionLong();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        String getDescriptionShort();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        List<String> getPreviewURLList();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        String getPrice();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        String getSKU();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        String getThumbnailURL();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        String getTitle();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        CatalogService.CatalogItem.Type getType();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        Calendar getUpdatedDate();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        int getWeight(String str);

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        boolean isDownloadable();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        boolean isFree();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        boolean isInstalled();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        boolean isPurchasable();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        boolean isPurchased();

        @Override // com.nuance.connect.api.CatalogService.CatalogItem
        boolean isUpdateAvailable();
    }

    /* loaded from: classes.dex */
    public interface ACCatalogItemBundle extends CatalogService.CatalogItemBundle {
        List<ACCatalogItem> getBundledCatalogItemList();

        @Override // com.nuance.connect.api.CatalogService.CatalogItemBundle
        List<String> getBundledCatalogItemSKUList();

        @Override // com.nuance.connect.api.CatalogService.CatalogItemBundle
        List<CatalogService.CatalogItem.Type> getBundledCatalogItemTypes();
    }

    /* loaded from: classes.dex */
    public interface ACCatalogItemDownloadCallback {
        boolean downloadComplete(File file);

        void downloadFailed(int i);

        void downloadPercentage(int i);

        void downloadStarted();

        void downloadStopped(int i);
    }

    void cancelDownloadCatalogItem(String str) throws ACException;

    void disableCatalog();

    void downloadCatalogItem(String str, ACCatalogItemDownloadCallback aCCatalogItemDownloadCallback) throws ACException;

    void enableCatalog();

    List<ACCatalogItem> getCatalogItemByCategory(String str);

    List<String> getCatalogItemCategories();

    List<String> getCatalogItemCategoriesByType(List<CatalogService.CatalogItem.Type> list);

    List<ACCatalogItem> getCatalogItems();

    List<ACCatalogItem> getCatalogItemsByType(List<CatalogService.CatalogItem.Type> list);

    String getCategoryKeyForCategoryName(String str);

    List<String> getSKUList();

    List<String> getSKUListForPurchase();

    List<String> getSKUListForPurchaseByType(List<CatalogService.CatalogItem.Type> list);

    void installedCatalogItem(String str) throws ACException;

    boolean isCatalogListAvailable();

    boolean isEnabled();

    void registerCallback(ACCatalogCallback aCCatalogCallback);

    void resetCatalogItemPrice();

    void resetPurchasedSKU();

    void setCatalogItemPrice(String str, String str2);

    void setCatalogItemPrices(Map<String, String> map);

    void setPurchasedSKU(String str);

    void setPurchasedSKUList(List<String> list);

    void skuAvailable(String str);

    void skuListAvailable(List<String> list);

    void uninstallCatalogItem(String str) throws ACException;

    void unregisterCallback(ACCatalogCallback aCCatalogCallback);

    void unregisterCallbacks();
}
