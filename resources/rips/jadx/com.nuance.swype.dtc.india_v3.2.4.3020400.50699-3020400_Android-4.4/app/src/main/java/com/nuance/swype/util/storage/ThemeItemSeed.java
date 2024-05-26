package com.nuance.swype.util.storage;

import com.nuance.connect.api.CatalogService;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACCatalogService;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ThemeItemSeed {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final LogManager.Log log;
    public String bundleDesc;
    private String bundleSku;
    public String categoryKey;
    public int installingPercentage = 0;
    boolean isExisted;
    public boolean isFree;
    public boolean isInstalled;
    public boolean isInstalling;
    public boolean isPurchasable;
    public boolean isPurchased;
    public String previewUrl;
    public String price;
    public String sku;
    public List<String> skuList;
    private int source;
    public List<String> themeCategories;
    public String themeCategoryLabel;
    public String themeName;
    public String thumbnailUrl;
    public int type;
    public final int weight;

    static {
        $assertionsDisabled = !ThemeItemSeed.class.desiredAssertionStatus();
        log = LogManager.getLog("ThemeItemSeed");
    }

    public ThemeItemSeed(String category, ACCatalogService.ACCatalogItem bundle, List<ACCatalogService.ACCatalogItem> bundleItems, ACCatalogService.ACCatalogItem item, String categoryKeyValue) {
        this.weight = item.getWeight(category);
        log.d(item.getSKU(), ".getWeight(", category, "): ", Integer.valueOf(this.weight));
        this.previewUrl = item.getPreviewURLList().get(0);
        this.price = item.getPrice();
        if (bundle != null && bundle.isPurchased()) {
            this.isPurchased = bundle.isPurchased();
            this.bundleSku = bundle.getSKU();
        } else {
            this.isPurchased = item.isPurchased();
        }
        if (bundle != null) {
            this.bundleSku = bundle.getSKU();
        }
        if (bundleItems != null) {
            this.skuList = new ArrayList();
            for (ACCatalogService.ACCatalogItem bundleItem : bundleItems) {
                this.skuList.add(bundleItem.getSKU());
            }
        }
        this.themeCategoryLabel = category;
        this.themeName = item.getTitle();
        this.sku = item.getSKU();
        this.thumbnailUrl = item.getThumbnailURL();
        this.source = ThemeManager.SwypeTheme.THEME_SOURCE.CONNECT.ordinal();
        this.type = item.getType().ordinal();
        this.bundleDesc = item.getType() == CatalogService.CatalogItem.Type.BUNDLE ? item.getDescriptionLong() : XMLResultsHandler.SEP_SPACE;
        this.isFree = item.isFree();
        setInstalled(item.isInstalled());
        this.isPurchasable = item.isPurchasable();
        setThemeCategories(item.getCategoryList());
        this.categoryKey = categoryKeyValue;
        log.d("catalog ThemeItemSeed bundle list: ", this.skuList);
        log.d("catalog ThemeItemSeed bundle sku: ", this.bundleSku);
        log.d("catalog ThemeItemSeed sku:", item.getSKU(), " categories:", item.getCategoryList());
        log.d("catalog ThemeItemSeed sku:", item.getSKU(), " category label:", category, " themeName:", item.getTitle(), " purchased:", Boolean.valueOf(item.isPurchased()), " price:", item.getPrice(), " type:", item.getType());
        log.d("catalog isFree:", Boolean.valueOf(item.isFree()), " purchasable:", Boolean.valueOf(item.isPurchasable()));
    }

    public final void setThemeCategories(List<String> categories) {
        if (categories != null && !categories.isEmpty()) {
            this.themeCategories = new ArrayList(categories);
        }
    }

    public final void setInstalled(boolean install) {
        if (install) {
            this.isInstalling = false;
        }
        this.isInstalled = install;
    }

    public final void setInstalling(boolean installing) {
        if (installing) {
            this.isInstalled = false;
        }
        this.isInstalling = installing;
    }

    public boolean equals(Object o) {
        if (!o.getClass().getName().equals(getClass().getName())) {
            return false;
        }
        ThemeItemSeed obj = (ThemeItemSeed) o;
        return this.source == obj.source && this.type == obj.type && this.sku.equals(obj.sku) && this.categoryKey.equals(obj.categoryKey) && this.isPurchased == obj.isPurchased && this.isPurchasable == obj.isPurchasable && this.isInstalled == obj.isInstalled && this.themeName.equals(obj.themeName) && this.thumbnailUrl.equals(obj.thumbnailUrl) && this.previewUrl.equals(obj.previewUrl);
    }

    public int hashCode() {
        if ($assertionsDisabled) {
            return 1;
        }
        throw new AssertionError("hashCode not designed");
    }
}
