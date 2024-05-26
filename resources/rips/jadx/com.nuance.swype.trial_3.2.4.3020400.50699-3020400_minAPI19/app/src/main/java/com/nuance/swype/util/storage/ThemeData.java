package com.nuance.swype.util.storage;

import com.nuance.connect.api.CatalogService;
import com.nuance.swype.inapp.CatalogManager;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.service.impl.AccountUtil;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public final class ThemeData {
    private static final LogManager.Log log = LogManager.getLog("ThemeData");
    private static final HashMap<String, List<ThemeItemSeed>> cache = new HashMap<>();

    public static boolean update(String sku, CatalogManager.ThemeStatusChange type) {
        boolean flag = false;
        for (String category : cache.keySet()) {
            for (ThemeItemSeed seed : cache.get(category)) {
                if (seed.sku.equals(sku)) {
                    if (type == CatalogManager.ThemeStatusChange.THEME_INSTALLED) {
                        seed.setInstalled(true);
                    } else if (type != CatalogManager.ThemeStatusChange.THEME_PURCHASED) {
                        if (type == CatalogManager.ThemeStatusChange.THEME_INSTALL_CANCELED) {
                            seed.setInstalled(false);
                            seed.setInstalling(false);
                        } else if (type == CatalogManager.ThemeStatusChange.THEME_UNINSTALLED) {
                            seed.setInstalled(false);
                            seed.setInstalling(false);
                        } else if (type == CatalogManager.ThemeStatusChange.THEME_INSTALLING) {
                            seed.setInstalling(true);
                        }
                    } else {
                        seed.isPurchased = true;
                    }
                    log.d("update sku:", sku, " status:", Integer.valueOf(type.ordinal()));
                    flag = true;
                }
            }
        }
        return flag;
    }

    public static boolean update(ThemeItemSeed seed) {
        boolean flag = false;
        String key = seed.categoryKey;
        try {
            for (ThemeItemSeed item : cache.get(key)) {
                if (item.sku.equals(seed.sku)) {
                    item.setInstalled(seed.isInstalled);
                    item.isPurchased = seed.isPurchased;
                    item.setInstalling(seed.isInstalling);
                    flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static HashMap<String, List<ThemeItemSeed>> getCache() {
        return cache;
    }

    public static int getTotalCacheCount() {
        int count = 0;
        for (String category : cache.keySet()) {
            if (cache.get(category) != null) {
                count += cache.get(category).size();
            }
        }
        return count;
    }

    public static ThemeItemSeed getThemeSeed(String sku, String categoryId) {
        if (cache.isEmpty() || sku == null) {
            return null;
        }
        if (categoryId == null) {
            for (String category : cache.keySet()) {
                for (ThemeItemSeed seed : cache.get(category)) {
                    if (seed.sku.equals(sku)) {
                        return seed;
                    }
                }
            }
        } else {
            List<ThemeItemSeed> seeds = cache.get(categoryId);
            if (seeds != null) {
                for (ThemeItemSeed seed2 : seeds) {
                    if (seed2.sku.equals(sku)) {
                        return seed2;
                    }
                }
            } else {
                log.e(categoryId + " not found");
            }
        }
        return null;
    }

    public static boolean isCacheEmpty() {
        return cache.isEmpty();
    }

    public static boolean isItemDeleted() {
        if (cache == null) {
            return false;
        }
        for (String category : cache.keySet()) {
            for (ThemeItemSeed seed : cache.get(category)) {
                if (seed != null && !seed.isExisted) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void resetItemDeleteStatus() {
        if (cache != null) {
            for (String category : cache.keySet()) {
                for (ThemeItemSeed seed : cache.get(category)) {
                    if (seed != null) {
                        seed.isExisted = false;
                    }
                }
            }
        }
    }

    public static boolean syncToCache(ThemeItemSeed seed) {
        List<String> list;
        seed.isExisted = true;
        String categoryKey = seed.categoryKey;
        List<ThemeItemSeed> seeds = cache.get(categoryKey);
        if (seeds != null) {
            for (ThemeItemSeed item : new ArrayList(seeds)) {
                if (item != null && item.sku.equals(seed.sku) && item.categoryKey.equals(categoryKey)) {
                    if (ThemeManager.NO_PRICE.equals(seed.price) && !AccountUtil.isGoogleAccountMissing() && AccountUtil.isGoogleAccountSignedIn()) {
                        seeds.remove(seed);
                        if (!seeds.isEmpty()) {
                            return false;
                        }
                        cache.remove(seeds);
                        return false;
                    }
                    item.themeCategoryLabel = seed.themeCategoryLabel;
                    item.themeName = seed.themeName;
                    item.setThemeCategories(seed.themeCategories);
                    if (item.type == CatalogService.CatalogItem.Type.BUNDLE.ordinal() && (list = seed.skuList) != null) {
                        item.skuList = new ArrayList(list);
                    }
                    item.isPurchased = seed.isPurchased;
                    if (seed.price != null) {
                        item.price = seed.price;
                    }
                    item.isExisted = true;
                    return false;
                }
            }
        }
        if (ThemeManager.NO_PRICE.equals(seed.price) && !AccountUtil.isGoogleAccountMissing() && AccountUtil.isGoogleAccountSignedIn()) {
            return false;
        }
        try {
            if (seeds != null) {
                seeds.add(seed);
            } else {
                List<ThemeItemSeed> seeds2 = new ArrayList<>();
                try {
                    seeds2.add(seed);
                    cache.put(categoryKey, seeds2);
                    log.d("put sku into cache:", seed.sku, " item purchased:", Boolean.valueOf(seed.isPurchased));
                } catch (Exception e) {
                    e = e;
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        } catch (Exception e2) {
            e = e2;
        }
    }
}
