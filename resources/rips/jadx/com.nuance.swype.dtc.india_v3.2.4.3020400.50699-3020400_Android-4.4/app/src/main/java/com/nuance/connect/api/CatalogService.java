package com.nuance.connect.api;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public interface CatalogService {

    /* loaded from: classes.dex */
    public interface CatalogCallback {
        void catalogItemInstalledUpdates(List<CatalogItem> list);

        void catalogItemListChanged();

        void catalogSKUListChanged(List<String> list);

        void skuListAvailable(List<String> list);

        void skuListUnavailable(List<String> list);
    }

    /* loaded from: classes.dex */
    public interface CatalogItem {

        /* loaded from: classes.dex */
        public enum Purchasable {
            UNPURCHASABLE(0),
            PURCHASABLE(1),
            FREE(2);

            int value;

            Purchasable(int i) {
                this.value = i;
            }

            public final boolean equals(int i) {
                return this.value == i;
            }
        }

        /* loaded from: classes.dex */
        public enum Type {
            KEYBOARD,
            BUNDLE
        }

        String getCanonicalName();

        List<String> getCategoryList();

        Map<String, Integer> getCategoryWeight();

        Calendar getDateAdded();

        Calendar getDateAvailableEnd();

        Calendar getDateAvailableStart();

        Calendar getDateFulfillEnd();

        String getDescriptionLong();

        String getDescriptionShort();

        List<String> getPreviewURLList();

        String getPrice();

        String getSKU();

        String getThumbnailURL();

        String getTitle();

        Type getType();

        Calendar getUpdatedDate();

        int getWeight(String str);

        boolean isDownloadable();

        boolean isFree();

        boolean isInstalled();

        boolean isPurchasable();

        boolean isPurchased();

        boolean isUpdateAvailable();
    }

    /* loaded from: classes.dex */
    public interface CatalogItemBundle extends CatalogItem {
        List<CatalogItem> getBundledBaseCatalogItemList();

        List<String> getBundledCatalogItemSKUList();

        List<CatalogItem.Type> getBundledCatalogItemTypes();
    }

    /* loaded from: classes.dex */
    public interface CatalogItemDownloadCallback {
        boolean downloadComplete(File file);

        void downloadFailed(int i);

        void downloadPercentage(int i);

        void downloadStarted();

        void downloadStopped(int i);
    }

    void cancelDownloadCatalogItem(String str);

    void disableCatalog();

    void downloadCatalogItem(String str, CatalogItemDownloadCallback catalogItemDownloadCallback);

    void enableCatalog();

    List<CatalogItem> getCatalogItemByCategory(String str);

    List<String> getCatalogItemCategories();

    List<String> getCatalogItemCategoriesByType(List<CatalogItem.Type> list);

    List<CatalogItem> getCatalogItems();

    List<CatalogItem> getCatalogItemsByType(List<CatalogItem.Type> list);

    String getCategoryKeyForCategoryName(String str);

    List<String> getSKUList();

    List<String> getSKUListForPurchase();

    List<String> getSKUListForPurchaseByType(List<CatalogItem.Type> list);

    void installedCatalogItem(String str);

    boolean isCatalogEnabled();

    boolean isCatalogListAvailable();

    void registerCatalogCallback(CatalogCallback catalogCallback);

    void resetCatalogItemPrice();

    void resetPurchasedSKU();

    void setCatalogItemPrices(Map<String, String> map);

    void setPurchasedSKU(String str);

    void setPurchasedSKUList(List<String> list);

    void skuListAvailable(List<String> list);

    void uninstallCatalogItem(String str);

    void unregisterCatalogCallback(CatalogCallback catalogCallback);

    void unregisterCatalogCallbacks();
}
