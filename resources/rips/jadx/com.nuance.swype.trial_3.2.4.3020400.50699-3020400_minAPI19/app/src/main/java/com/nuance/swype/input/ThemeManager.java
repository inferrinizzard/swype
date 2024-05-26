package com.nuance.swype.input;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import com.nuance.connect.api.CatalogService;
import com.nuance.connect.internal.common.Document;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.inapp.CatalogManager;
import com.nuance.swype.inapp.CategoryItem;
import com.nuance.swype.inapp.InstalledList;
import com.nuance.swype.inapp.ThemePurchaser;
import com.nuance.swype.inapp.util.IabHelper;
import com.nuance.swype.inapp.util.IabResult;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.settings.ThemesCategory;
import com.nuance.swype.input.udb.NewWordsBucketFactory;
import com.nuance.swype.measure.UsecaseStopwatch;
import com.nuance.swype.measure.Usecases;
import com.nuance.swype.service.impl.AccountUtil;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.StringUtils;
import com.nuance.swype.util.storage.ThemeData;
import com.nuance.swype.util.storage.ThemeDataManager;
import com.nuance.swype.util.storage.ThemeItemSeed;
import com.nuance.swypeconnect.ac.ACCatalogService;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/* loaded from: classes.dex */
public class ThemeManager {
    private static final String CONFIG_PROPERTIES = "config.properties";
    public static final String MY_THEMES = "my_themes";
    public static final String NO_PRICE = "  ";
    private static final String PROP_KEY_MENU = "themes.prefs.menu";
    public static final String STR_NO_CATEGORY_ID = "no_category_id";
    public static final String STR_NO_CATEGORY_ID_DLT = "no_category_id_DLT";
    public static final String SWYPE_CLASSIC_CATEGORY_ID = "swype_classics";
    public static final String THEME_MLS_DELIMETER = ",";
    private static boolean isDownloadableThemesEnabled;
    public static boolean isThemesLocked;
    private static NotifyObserverDataChanged observer;
    private SwypeTheme currentTheme;
    SwypeTheme defaultTheme;
    private volatile ThemePurchaser mThemePurchaser;
    private List<String> menuOrder;
    protected ThemesCategory themeCategory;
    private Context themeManagerContext;
    protected static final LogManager.Log log = LogManager.getLog("ThemeManager");
    private static boolean isInAppServiceExisting = true;
    private static boolean isInAppApiSupported = true;
    private static final int[] mls_hotwords = {R.string.msl_hotwords_1, R.string.msl_hotwords_2};
    private List<SwypeTheme> myEntries = new ArrayList();
    private Set<String> mEmbeddedSkuList = new HashSet();
    private Map<String, SwypeTheme> themeMap = new LinkedHashMap();
    private Map<String, LinkedHashMap<String, SwypeTheme>> themeCategoryMap = new LinkedHashMap();
    private String currentCategoryId = STR_NO_CATEGORY_ID;
    private ArrayList<CategoryItem> categoryItemList = new ArrayList<>();
    private SwypeThemeWeightComparator swypeThemeWeightComparator = new SwypeThemeWeightComparator();
    private final ThemeDataManager dataManager = ThemeDataManager.getInstance();

    /* loaded from: classes.dex */
    public interface NotifyObserverDataChanged {
        boolean onDataChanged();
    }

    /* loaded from: classes.dex */
    public interface OnThemePreviewDialogListener {
        void onThemePreivewDialogClosed();
    }

    public static ThemeManager from(Context context) {
        return IMEApplication.from(context.getApplicationContext()).getThemeManager();
    }

    /* loaded from: classes.dex */
    public static class SwypeTheme {
        private static final String DEFAULT_WORD_LIST_FILE_NAME = "wordlist.txt";
        private final String currentCategoryLabel;
        private final String displayName;
        private final int keyboardPreviewId;
        protected String mSku;
        private final int nameResId;
        private final int previewResId;
        private int resId;
        private final List<String> skuList;
        protected final int weight;

        /* loaded from: classes.dex */
        public enum THEME_SOURCE {
            EMBEDDED,
            CONNECT
        }

        protected SwypeTheme(int resId, int nameResId, String sku, String name, int previewResId, int keyboardPreviewId, String currentCategoryLabel, List<String> skuList, int weight) {
            this.resId = resId;
            this.nameResId = nameResId;
            this.mSku = sku;
            this.displayName = name;
            this.previewResId = previewResId;
            this.keyboardPreviewId = keyboardPreviewId;
            this.currentCategoryLabel = currentCategoryLabel;
            this.skuList = skuList;
            this.weight = weight;
        }

        public List<String> getSkuList() {
            return this.skuList;
        }

        public int getResId() {
            return this.resId;
        }

        public String getSku() {
            return this.mSku;
        }

        private String getDisplayName() {
            return this.displayName;
        }

        public String getDisplayName(Resources res) {
            return (this.nameResId == 0 || res == null) ? getDisplayName() : res.getString(this.nameResId);
        }

        public int getPreviewResId() {
            return this.previewResId;
        }

        public int getKeyboardPreviewResId() {
            return this.keyboardPreviewId;
        }

        public String getCurrentCategoryLabel() {
            return this.currentCategoryLabel;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof SwypeTheme)) {
                return false;
            }
            SwypeTheme other = (SwypeTheme) obj;
            return this.mSku.equals(other.mSku);
        }

        public int hashCode() {
            return this.mSku.hashCode();
        }

        public boolean hasWordList() {
            return true;
        }

        public String getWordListFileName() {
            return DEFAULT_WORD_LIST_FILE_NAME;
        }

        public THEME_SOURCE getSource() {
            return THEME_SOURCE.EMBEDDED;
        }

        public boolean isConnectTheme() {
            return getSource() == THEME_SOURCE.CONNECT;
        }

        public int getStatusIcon(Context ctx) {
            return 0;
        }
    }

    /* loaded from: classes.dex */
    public static class ConnectDownloadableThemeWrapper extends SwypeTheme {
        private ThemeItemSeed mTheme;

        public String getPrice() {
            return this.mTheme.price;
        }

        public ThemeItemSeed getThemeItemSeed() {
            return this.mTheme;
        }

        @Override // com.nuance.swype.input.ThemeManager.SwypeTheme
        public String getSku() {
            return this.mTheme.sku;
        }

        @Override // com.nuance.swype.input.ThemeManager.SwypeTheme
        public String getDisplayName(Resources res) {
            return this.mTheme.themeName;
        }

        public String getThumbnailUrl() {
            return this.mTheme.thumbnailUrl;
        }

        public int getType() {
            return this.mTheme.type;
        }

        public String getPreviewImageUrl() {
            return this.mTheme.previewUrl;
        }

        @Override // com.nuance.swype.input.ThemeManager.SwypeTheme
        public SwypeTheme.THEME_SOURCE getSource() {
            return SwypeTheme.THEME_SOURCE.CONNECT;
        }

        @Override // com.nuance.swype.input.ThemeManager.SwypeTheme
        public int getStatusIcon(Context ctx) {
            CatalogManager catalogManager = IMEApplication.from(ctx).getCatalogManager();
            ThemeManager.log.d(" theme sku:", this.mTheme.sku, " purchased:", Boolean.valueOf(this.mTheme.isPurchased));
            InstalledList installedList = catalogManager.mInstalledList;
            Map<String, Boolean> purchasedList = catalogManager.purchasedMap;
            String str = this.mTheme.sku;
            if ((installedList.mInstalledMap.containsKey(str) ? ((Boolean) installedList.mInstalledMap.get(str)).booleanValue() : false) && this.mTheme.isInstalled) {
                ThemeManager.log.d(" theme sku:", this.mTheme.sku, " is purchased or free and installed");
                return R.drawable.settings_theme_check;
            }
            if ((this.mTheme.isPurchased && !this.mTheme.isFree) || (purchasedList != null && purchasedList.containsKey(this.mTheme.sku))) {
                ThemeManager.log.d(" theme sku:", this.mTheme.sku, " is purchased but not installed");
                this.mTheme.isPurchased = true;
                return R.drawable.settings_theme_download;
            }
            ThemeManager.log.d(" theme sku:", this.mTheme.sku, " display text free or price");
            return 0;
        }

        public String getStatusText(Context ctx) {
            Resources res = ctx.getResources();
            if (!this.mTheme.isPurchasable) {
                if (this.mTheme.isFree) {
                    return res.getString(R.string.free);
                }
                return ThemeManager.NO_PRICE;
            }
            String price = this.mTheme.price;
            if (price == null) {
                CatalogManager.PriceMapLocalStorageHelper priceMapLocalStorageHelper = CatalogManager.PriceMapLocalStorageHelper.getInstance(ctx);
                return priceMapLocalStorageHelper.mPriceMap.get(this.mTheme.sku);
            }
            return price;
        }

        public void purchase(Activity activity, int requestCode, IabHelper.OnIabPurchaseFinishedListener listener, Context ctx) {
            ThemeManager.from(ctx).getThemePurchaser(ctx).purchase(activity, getSku(), requestCode, listener);
        }

        public ConnectDownloadableThemeWrapper(ThemeItemSeed theme, String category) {
            super(0, 0, theme.sku, theme.themeName, R.drawable.thumbnail_test, R.drawable.theme_preview_test, theme.themeCategoryLabel, theme.skuList, theme.weight);
            this.mTheme = theme;
        }
    }

    public static boolean isDownloadableThemesEnabled() {
        return isDownloadableThemesEnabled;
    }

    public static void setIsDownloadableThemesEnabled(boolean isDownloadableThemesEnabled2) {
        isDownloadableThemesEnabled = isDownloadableThemesEnabled2;
    }

    public static boolean isInAppServiceExisting() {
        return isInAppServiceExisting;
    }

    public static void setIsInAppServiceExisting(boolean isInAppServiceExisting2) {
        isInAppServiceExisting = isInAppServiceExisting2;
    }

    public static boolean isInAppApiSupported() {
        return isInAppApiSupported;
    }

    public static void setIsInAppApiSupported(boolean isInAppApiSupported2) {
        isInAppApiSupported = isInAppApiSupported2;
    }

    protected ThemeManager(Context context) {
        this.themeManagerContext = context;
    }

    public void clearCache() {
        this.categoryItemList.clear();
        ThemeData.getCache().clear();
        synchronized (this.themeCategoryMap) {
            this.themeCategoryMap.clear();
            this.themeCategoryMap.put(STR_NO_CATEGORY_ID, (LinkedHashMap) this.themeMap);
        }
    }

    public void initializeInAppPurchase(Context context) {
        if (isDownloadableThemesEnabled()) {
            if (this.mThemePurchaser == null) {
                this.mThemePurchaser = new ThemePurchaser(context);
            }
            if (isInAppServiceExisting() && isInAppApiSupported()) {
                fetchThemeDetailsFromStore(context);
            }
        }
    }

    public ThemePurchaser getThemePurchaser(Context ctx) {
        if (this.mThemePurchaser == null) {
            this.mThemePurchaser = new ThemePurchaser(ctx);
        }
        return this.mThemePurchaser;
    }

    public static boolean isBillingServiceAvailabeOnDevice(Context context) {
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        return !context.getPackageManager().queryIntentServices(serviceIntent, 0).isEmpty();
    }

    private void fetchThemeDetailsFromStore(Context context) {
        UsecaseStopwatch.getInstance().start(Usecases.GET_PRICE);
        List<String> skus = getDownloadableSkus();
        log.d("requested skus count:", Integer.valueOf(skus.size()));
        if (skus.isEmpty()) {
            CatalogManager catalogManager = IMEApplication.from(context).getCatalogManager();
            if (catalogManager != null) {
                catalogManager.triggerShowingFreeCategories();
                return;
            }
            return;
        }
        this.mThemePurchaser.queryItems(skus, new IabHelper.OnIabSetupFinishedListener() { // from class: com.nuance.swype.input.ThemeManager.1
            @Override // com.nuance.swype.inapp.util.IabHelper.OnIabSetupFinishedListener
            public void onIabSetupFinished(IabResult result) {
                UsecaseStopwatch.getInstance().stop(Usecases.GET_PRICE);
                UsecaseStopwatch.getInstance();
                Usecases.GET_PRICE.mStartTime = SystemClock.elapsedRealtime();
                if (result.isSuccess()) {
                    ThemeManager.notifyIapDataChanges();
                    AccountUtil.setIsGoogleAccountSignedIn(true);
                } else {
                    result.mMessage.equals("inapp_not_supported");
                    ThemeManager.log.d("fetchThemeDetailsFromStore failure.");
                }
            }
        });
    }

    private static void logSkuList(List<String> skus) {
        log.d("Getting details for the following skus from Google Play: ");
        for (String sku : skus) {
            log.d(sku);
        }
    }

    public static void setIapObserver(NotifyObserverDataChanged obj) {
        observer = obj;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void notifyIapDataChanges() {
        if (observer != null) {
            observer.onDataChanged();
        }
    }

    private List<String> getDownloadableSkus() {
        List<String> skuList = new ArrayList<>();
        skuList.clear();
        CatalogManager catalogManager = IMEApplication.from(this.themeManagerContext).getCatalogManager();
        if (catalogManager != null) {
            UsecaseStopwatch.getInstance().start(Usecases.GET_SKUS_FOR_DOWNLOAD);
            List<String> skus = catalogManager.getSKUforPurchase();
            if (skus != null) {
                skuList.addAll(skus);
                log.d("getDownloadableSkus");
                logSkuList(skus);
            }
            UsecaseStopwatch.getInstance().stop(Usecases.GET_SKUS_FOR_DOWNLOAD);
        }
        return skuList;
    }

    public int getSwypeThemeCount() {
        if (this.themeMap != null) {
            return this.themeMap.size();
        }
        return 0;
    }

    public ThemeDataManager getThemeDataManager() {
        return this.dataManager;
    }

    public ArrayList<CategoryItem> getThemeCategoryItemList(Context ctx) {
        if (this.categoryItemList.size() == 0) {
            syncToCategoryItemList(ctx);
        }
        return this.categoryItemList;
    }

    private void syncToCategoryItemList(Context ctx) {
        if (!isDownloadableThemesEnabled()) {
            this.categoryItemList.add(new CategoryItem("Swype", STR_NO_CATEGORY_ID, new ArrayList(this.themeCategoryMap.get(STR_NO_CATEGORY_ID).values())));
        } else {
            LinkedHashMap<String, SwypeTheme> embeddedMap = this.themeCategoryMap.get(STR_NO_CATEGORY_ID);
            this.categoryItemList.clear();
            ACCatalogService cs = IMEApplication.from(ctx).getConnect().getCatalogService();
            for (String category : cs.getCatalogItemCategories()) {
                String categoryKey = cs.getCategoryKeyForCategoryName(category);
                LinkedHashMap<String, SwypeTheme> themesMap = this.themeCategoryMap.get(categoryKey);
                if (themesMap != null) {
                    List<SwypeTheme> themes = new ArrayList<>();
                    List<SwypeTheme> classicThemes = new ArrayList<>();
                    if (SWYPE_CLASSIC_CATEGORY_ID.equals(categoryKey)) {
                        themes.addAll(themesMap.values());
                        for (SwypeTheme theme : themes) {
                            log.d("swype classic theme sku:", theme.getSku());
                            if (!embeddedMap.keySet().contains(theme.getSku())) {
                                classicThemes.add(theme);
                            }
                        }
                        if (classicThemes.isEmpty()) {
                            log.d("all embedded themes existed locally");
                        } else {
                            themes = classicThemes;
                        }
                    } else {
                        for (SwypeTheme theme2 : themesMap.values()) {
                            if (theme2.isConnectTheme()) {
                                ThemeItemSeed seed = ((ConnectDownloadableThemeWrapper) theme2).getThemeItemSeed();
                                if (!AccountUtil.isGoogleAccountMissing() && AccountUtil.isGoogleAccountSignedIn()) {
                                    if (!seed.isFree && !seed.isPurchasable) {
                                        if (!(seed.price == null || seed.price.isEmpty())) {
                                        }
                                    }
                                    themes.add(theme2);
                                } else {
                                    themes.add(theme2);
                                }
                            }
                        }
                    }
                    if (!themes.isEmpty()) {
                        Collections.sort(themes, this.swypeThemeWeightComparator);
                        this.categoryItemList.add(new CategoryItem(category, categoryKey, themes));
                    }
                }
            }
        }
        notifyIapDataChanges();
        log.d("categoryItemList size:", Integer.valueOf(this.categoryItemList.size()));
    }

    public boolean addConnectedThemesToMap() {
        boolean z;
        synchronized (this.themeCategoryMap) {
            HashMap<String, List<ThemeItemSeed>> cache = ThemeData.getCache();
            int totalCount = 0;
            for (String category : this.themeCategoryMap.keySet()) {
                if (!category.equals(STR_NO_CATEGORY_ID)) {
                    totalCount += this.themeCategoryMap.get(category).values().size();
                }
            }
            syncPurchasedOrInstalledList();
            if (InputMethods.isLocaleChanged() || ThemeData.getTotalCacheCount() != totalCount) {
                log.d("addConnectedThemesToMap re-cache");
                CatalogManager catalogManager = IMEApplication.from(this.themeManagerContext).getCatalogManager();
                if (catalogManager != null) {
                    for (String categoryKey : cache.keySet()) {
                        List<SwypeTheme> themes = catalogManager.getThemesForCategory(categoryKey);
                        if (!themes.isEmpty()) {
                            log.d("add category:", categoryKey);
                            LinkedHashMap<String, SwypeTheme> connectThemesMap = new LinkedHashMap<>();
                            for (SwypeTheme theme : themes) {
                                connectThemesMap.put(theme.getSku(), theme);
                            }
                            this.themeCategoryMap.put(categoryKey, connectThemesMap);
                        }
                    }
                }
                syncToCategoryItemList(this.themeManagerContext);
                z = ThemeData.getTotalCacheCount() != totalCount;
            } else {
                z = false;
            }
        }
        return z;
    }

    private void syncPurchasedOrInstalledList() {
        Map<String, Boolean> purchasedMap;
        CatalogManager catalogManager = IMEApplication.from(this.themeManagerContext).getCatalogManager();
        if (catalogManager == null || (purchasedMap = catalogManager.purchasedMap) == null) {
            return;
        }
        ACCatalogService cs = IMEApplication.from(this.themeManagerContext).getConnect().getCatalogService();
        for (String category : cs.getCatalogItemCategories()) {
            String categoryKey = cs.getCategoryKeyForCategoryName(category);
            for (SwypeTheme theme : catalogManager.getThemesForCategory(categoryKey)) {
                ThemeItemSeed seed = catalogManager.getThemeSeed(categoryKey, theme.getSku());
                if (seed != null && !seed.isFree && !seed.isInstalling && !seed.isInstalled) {
                    updateThemeStatus(theme.getSku(), CatalogManager.ThemeStatusChange.THEME_NOSTATE);
                }
            }
        }
        for (String sku : purchasedMap.keySet()) {
            log.d("syncPurchasedListOrInstalledList theme sku:", sku, " is purchased");
            updateThemeStatus(sku, CatalogManager.ThemeStatusChange.THEME_PURCHASED);
            ThemeData.update(sku, CatalogManager.ThemeStatusChange.THEME_PURCHASED);
        }
        InstalledList installedList = catalogManager.mInstalledList;
        if (installedList != null) {
            for (String installedSku : installedList.mInstalledMap.keySet()) {
                log.d("syncPurchasedListOrInstalledList theme sku:", installedSku, " is installed");
                updateThemeStatus(installedSku, CatalogManager.ThemeStatusChange.THEME_INSTALLED);
                ThemeData.update(installedSku, CatalogManager.ThemeStatusChange.THEME_INSTALLED);
            }
        }
    }

    public List<SwypeTheme> getBundleThemes(String category, String bundleSku) {
        List<SwypeTheme> bundle;
        SwypeTheme subTheme;
        synchronized (this.themeCategoryMap) {
            LinkedHashMap<String, SwypeTheme> themeMap = this.themeCategoryMap.get(category);
            bundle = new ArrayList<>();
            if (themeMap != null) {
                SwypeTheme bundleTheme = themeMap.get(bundleSku);
                List<String> categoryList = ((ConnectDownloadableThemeWrapper) bundleTheme).getThemeItemSeed().themeCategories;
                if (categoryList == null) {
                    categoryList = new ArrayList<>();
                    categoryList.add(bundleTheme.getCurrentCategoryLabel());
                }
                List<String> skuList = bundleTheme.getSkuList();
                ACCatalogService cs = IMEApplication.from(this.themeManagerContext).getConnect().getCatalogService();
                for (String sku : skuList) {
                    Iterator<String> it = categoryList.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            String item = it.next();
                            String key = cs.getCategoryKeyForCategoryName(item);
                            LinkedHashMap<String, SwypeTheme> themes = this.themeCategoryMap.get(key);
                            if (themes != null) {
                                SwypeTheme subTheme2 = themes.get(sku);
                                if (subTheme2 != null) {
                                    bundle.add(subTheme2);
                                    break;
                                }
                                LinkedHashMap<String, SwypeTheme> themes2 = this.themeCategoryMap.get(STR_NO_CATEGORY_ID_DLT);
                                if (themes2 != null && (subTheme = themes2.get(sku)) != null) {
                                    bundle.add(subTheme);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return bundle;
    }

    public void setCurrentCategoryId(String id) {
        this.currentCategoryId = id;
    }

    public Map<String, LinkedHashMap<String, SwypeTheme>> getCategoryThemes(Context ctx) {
        Map<String, LinkedHashMap<String, SwypeTheme>> map;
        synchronized (this.themeCategoryMap) {
            map = this.themeCategoryMap;
        }
        return map;
    }

    public List<SwypeTheme> getCategoryThemes(Context ctx, String categoryId) {
        List<SwypeTheme> entries;
        synchronized (this.themeCategoryMap) {
            if (categoryId == null) {
                categoryId = this.currentCategoryId;
            }
            if (categoryId.equals(MY_THEMES)) {
                HashMap<String, List<ThemeItemSeed>> cache = ThemeData.getCache();
                CatalogManager catalogManager = IMEApplication.from(ctx).getCatalogManager();
                if (cache.isEmpty() && !catalogManager.hasStoredSkuList()) {
                    categoryId = STR_NO_CATEGORY_ID;
                } else {
                    if (hasNewEntriesAddedToMyThemes()) {
                        this.myEntries.clear();
                        ACCatalogService cs = IMEApplication.from(ctx).getConnect().getCatalogService();
                        if (cs != null) {
                            for (String category : cs.getCatalogItemCategories()) {
                                String categoryKey = cs.getCategoryKeyForCategoryName(category);
                                addOwnedThemeToMyTheme(categoryKey, this.myEntries);
                            }
                        }
                        addOwnedThemeToMyTheme(STR_NO_CATEGORY_ID_DLT, this.myEntries);
                        List<SwypeTheme> embeddedEntries = new ArrayList<>(this.themeCategoryMap.get(STR_NO_CATEGORY_ID).values());
                        this.myEntries.addAll(embeddedEntries);
                    }
                    entries = this.myEntries;
                }
            }
            if (!this.categoryItemList.isEmpty()) {
                Iterator<CategoryItem> it = this.categoryItemList.iterator();
                while (it.hasNext()) {
                    CategoryItem item = it.next();
                    if (item.categoryId.equals(categoryId)) {
                        entries = item.themes;
                        break;
                    }
                }
            }
            entries = new ArrayList<>();
            if (this.themeCategoryMap.get(categoryId) != null) {
                entries.addAll(this.themeCategoryMap.get(categoryId).values());
            }
            Collections.sort(entries, this.swypeThemeWeightComparator);
        }
        return entries;
    }

    private void addOwnedThemeToMyTheme(String categoryKey, List<SwypeTheme> myTheme) {
        if (categoryKey != null && !categoryKey.isEmpty() && myTheme != null && this.themeCategoryMap.get(categoryKey) != null) {
            for (SwypeTheme item : this.themeCategoryMap.get(categoryKey).values()) {
                if (item.isConnectTheme()) {
                    ThemeItemSeed seed = ((ConnectDownloadableThemeWrapper) item).getThemeItemSeed();
                    if (seed.isPurchased || seed.isInstalled) {
                        log.d("purchased or free downloaded themes sku:", item.getSku(), " categoryid:", item.getCurrentCategoryLabel());
                        if (!seed.isPurchased || seed.type != CatalogService.CatalogItem.Type.BUNDLE.ordinal()) {
                            boolean hasAdded = false;
                            Iterator<SwypeTheme> it = myTheme.iterator();
                            while (true) {
                                if (it.hasNext()) {
                                    if (it.next().getSku().equals(item.getSku())) {
                                        hasAdded = true;
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            if (!hasAdded && !isDownloadableThemeInEmbeddedList(item.getSku())) {
                                myTheme.add(item);
                            }
                        }
                    }
                }
            }
        }
    }

    private void setEmbeddedThemeList(List<String> embeddedSkuList) {
        this.mEmbeddedSkuList.clear();
        this.mEmbeddedSkuList.addAll(embeddedSkuList);
    }

    private boolean isDownloadableThemeInEmbeddedList(String sku) {
        return this.mEmbeddedSkuList.contains(sku);
    }

    private boolean hasNewEntriesAddedToMyThemes() {
        synchronized (this.themeCategoryMap) {
            if (InputMethods.isLocaleChanged()) {
                return true;
            }
            int counter = 0;
            HashMap<String, List<ThemeItemSeed>> cache = ThemeData.getCache();
            List<String> skus = new ArrayList<>();
            for (String category : cache.keySet()) {
                if (this.themeCategoryMap.get(category) != null) {
                    for (SwypeTheme item : this.themeCategoryMap.get(category).values()) {
                        if (item.isConnectTheme()) {
                            ThemeItemSeed seed = ((ConnectDownloadableThemeWrapper) item).getThemeItemSeed();
                            if (seed.isPurchased || seed.isInstalled) {
                                log.d("hasNewEntriesAddedToMyThemes purchased or free downloaded themes sku:", item.getSku(), " categoryid:", item.getCurrentCategoryLabel());
                                if (!skus.contains(seed.sku)) {
                                    skus.add(seed.sku);
                                    counter++;
                                }
                            }
                        }
                    }
                }
            }
            return this.myEntries.size() != counter + this.themeCategoryMap.get(STR_NO_CATEGORY_ID).values().size();
        }
    }

    public void updateThemeStatus(String categoryKey, String sku, CatalogManager.ThemeStatusChange type) {
        synchronized (this.themeCategoryMap) {
            if (this.themeCategoryMap.get(categoryKey) != null) {
                for (SwypeTheme item : this.themeCategoryMap.get(categoryKey).values()) {
                    if (item.getSku().equals(sku)) {
                        if (item.isConnectTheme()) {
                            ThemeItemSeed themeSeed = ((ConnectDownloadableThemeWrapper) item).getThemeItemSeed();
                            if (type == CatalogManager.ThemeStatusChange.THEME_INSTALLED) {
                                themeSeed.setInstalled(true);
                            } else if (type != CatalogManager.ThemeStatusChange.THEME_PURCHASED) {
                                if (type == CatalogManager.ThemeStatusChange.THEME_INSTALL_CANCELED) {
                                    themeSeed.setInstalled(false);
                                    themeSeed.setInstalling(false);
                                } else if (type == CatalogManager.ThemeStatusChange.THEME_UNINSTALLED) {
                                    themeSeed.setInstalled(false);
                                    themeSeed.setInstalling(false);
                                } else if (type == CatalogManager.ThemeStatusChange.THEME_NOSTATE) {
                                    themeSeed.setInstalled(false);
                                    themeSeed.setInstalling(false);
                                    themeSeed.isPurchasable = !themeSeed.isFree;
                                }
                            } else {
                                themeSeed.isPurchased = true;
                            }
                        }
                        log.d("updateThemeStatus sku:", sku, " type:", Integer.valueOf(type.ordinal()));
                    }
                }
            }
        }
    }

    public void updateBundleThemeStatus(String categoryKey, String bundleSku, CatalogManager.ThemeStatusChange type) {
        synchronized (this.themeCategoryMap) {
            SwypeTheme bundleTheme = this.themeCategoryMap.get(categoryKey).get(bundleSku);
            List<String> skuList = bundleTheme.getSkuList();
            List<String> categoryList = ((ConnectDownloadableThemeWrapper) bundleTheme).getThemeItemSeed().themeCategories;
            if (categoryList == null) {
                categoryList = new ArrayList<>();
                categoryList.add(bundleTheme.getCurrentCategoryLabel());
            }
            ACCatalogService cs = IMEApplication.from(this.themeManagerContext).getConnect().getCatalogService();
            if (skuList != null) {
                for (String sku : skuList) {
                    for (String item : categoryList) {
                        String key = cs.getCategoryKeyForCategoryName(item);
                        ConnectDownloadableThemeWrapper theme = (ConnectDownloadableThemeWrapper) this.themeCategoryMap.get(key).get(sku);
                        if (theme != null) {
                            if (type == CatalogManager.ThemeStatusChange.THEME_PURCHASED && !theme.getThemeItemSeed().isFree && !theme.getThemeItemSeed().isPurchased) {
                                theme.getThemeItemSeed().isPurchased = true;
                            }
                            if (type == CatalogManager.ThemeStatusChange.THEME_INSTALLED && sku != null && theme.getSku().equals(sku)) {
                                theme.getThemeItemSeed().setInstalled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateThemeStatus(String sku, CatalogManager.ThemeStatusChange type) {
        synchronized (this.themeCategoryMap) {
            for (String category : this.themeCategoryMap.keySet()) {
                for (SwypeTheme item : this.themeCategoryMap.get(category).values()) {
                    if (item.getSku().equals(sku) && item.isConnectTheme()) {
                        if (type == CatalogManager.ThemeStatusChange.THEME_INSTALLED) {
                            ((ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalled(true);
                        } else if (type != CatalogManager.ThemeStatusChange.THEME_PURCHASED) {
                            if (type == CatalogManager.ThemeStatusChange.THEME_INSTALL_CANCELED) {
                                ((ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalled(false);
                                ((ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalling(false);
                            } else if (type == CatalogManager.ThemeStatusChange.THEME_UNINSTALLED) {
                                ((ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalled(false);
                                ((ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalling(false);
                                if (((ConnectDownloadableThemeWrapper) item).getThemeItemSeed().isFree) {
                                    this.myEntries.remove(item);
                                }
                            }
                        } else {
                            ((ConnectDownloadableThemeWrapper) item).getThemeItemSeed().isPurchased = true;
                        }
                    }
                }
            }
        }
    }

    public boolean hasFreeThemes() {
        for (String category : this.themeCategoryMap.keySet()) {
            for (SwypeTheme item : this.themeCategoryMap.get(category).values()) {
                if (item.getSource() == SwypeTheme.THEME_SOURCE.CONNECT && ((ConnectDownloadableThemeWrapper) item).getThemeItemSeed().isFree) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasPricedThemes() {
        for (String category : this.themeCategoryMap.keySet()) {
            for (SwypeTheme item : this.themeCategoryMap.get(category).values()) {
                if (item.isConnectTheme()) {
                    ThemeItemSeed seed = ((ConnectDownloadableThemeWrapper) item).getThemeItemSeed();
                    if (seed.price != null && !seed.price.equals(NO_PRICE)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasNoPriceThemes() {
        for (String category : this.themeCategoryMap.keySet()) {
            for (SwypeTheme item : this.themeCategoryMap.get(category).values()) {
                if (item.isConnectTheme()) {
                    ThemeItemSeed seed = ((ConnectDownloadableThemeWrapper) item).getThemeItemSeed();
                    if (seed.price != null && seed.price.equals(NO_PRICE)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setCurrentThemesCategory(ThemesCategory themesCategory) {
    }

    public List<SwypeTheme> getSwypeThemes() {
        return new ArrayList(this.themeMap.values());
    }

    public List<String> getBundleOfTheme(String themeId) {
        List<String> bundle = new ArrayList<>();
        if (themeId != null) {
            for (String category : this.themeCategoryMap.keySet()) {
                for (SwypeTheme theme : this.themeCategoryMap.get(category).values()) {
                    if (theme.isConnectTheme()) {
                        ThemeItemSeed seed = ((ConnectDownloadableThemeWrapper) theme).getThemeItemSeed();
                        if (seed.type == CatalogService.CatalogItem.Type.BUNDLE.ordinal() && seed.skuList.contains(themeId)) {
                            bundle.add(theme.getSku());
                        }
                    }
                }
            }
        }
        return bundle;
    }

    public SwypeTheme getSwypeTheme(String category, String sku) {
        SwypeTheme theme;
        if (this.themeCategoryMap == null) {
            return null;
        }
        synchronized (this.themeCategoryMap) {
            if (this.themeCategoryMap.get(category) == null && sku != null) {
                theme = getSwypeTheme(sku);
            } else {
                Iterator<SwypeTheme> it = this.themeCategoryMap.get(category).values().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        theme = null;
                        break;
                    }
                    theme = it.next();
                    if (theme.getSku().equals(sku)) {
                        break;
                    }
                }
            }
        }
        return theme;
    }

    public SwypeTheme getSwypeTheme(String sku) {
        if (this.currentTheme != null && this.currentTheme.getSku().equals(sku)) {
            log.d("find current theme, sku:", this.currentTheme.getSku());
            return this.currentTheme;
        }
        if (sku != null && sku.contains("/")) {
            SwypeTheme downloadedTheme = getDownloadedSwypeTheme(sku);
            if (downloadedTheme != null) {
                log.d("returning downloaded theme");
                this.currentTheme = downloadedTheme;
                return this.currentTheme;
            }
        } else {
            synchronized (this.themeCategoryMap) {
                for (String item : this.themeCategoryMap.keySet()) {
                    for (SwypeTheme theme : this.themeCategoryMap.get(item).values()) {
                        if (theme.getSku().equals(sku)) {
                            this.currentTheme = theme;
                            log.d("find macthed theme, sku:", this.currentTheme.getSku());
                            return this.currentTheme;
                        }
                    }
                }
            }
        }
        if (this.defaultTheme == null) {
            List<SwypeTheme> list = getSwypeThemes();
            if (!list.isEmpty()) {
                this.defaultTheme = list.get(0);
                UserPreferences.from(this.themeManagerContext).setCurrentThemeId(this.defaultTheme.getSku());
            } else {
                log.d("default theme is null");
            }
        }
        this.currentTheme = this.defaultTheme;
        LogManager.Log log2 = log;
        Object[] objArr = new Object[2];
        objArr[0] = "getting default theme sku:";
        objArr[1] = this.currentTheme != null ? this.currentTheme.getSku() : "";
        log2.d(objArr);
        return this.currentTheme;
    }

    private SwypeTheme getDownloadedSwypeTheme(String sku) {
        if (IMEApplication.from(this.themeManagerContext) == null || !sku.contains("/")) {
            return null;
        }
        return new SwypeTheme(R.style.Swype, 0, sku, sku, R.drawable.thumbnail_test, R.drawable.theme_preview_test, STR_NO_CATEGORY_ID, null, 0);
    }

    protected static void scanSideloadedThemes(Context context, Map<String, SwypeTheme> map) {
        File[] dirFiles;
        if (ContextCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && Environment.getExternalStorageState() != null) {
            File dir = new File(Environment.getExternalStorageDirectory() + "/Download//themes/");
            if (dir.exists() && (dirFiles = dir.listFiles(new FilenameFilter() { // from class: com.nuance.swype.input.ThemeManager.2
                @Override // java.io.FilenameFilter
                public final boolean accept(File dir2, String name) {
                    return name.toLowerCase(Locale.US).endsWith(".apk");
                }
            })) != null && dirFiles.length > 0) {
                for (File file : dirFiles) {
                    String apkName = file.getName();
                    if (!map.containsKey(apkName)) {
                        map.put(apkName, new SwypeTheme(R.style.Swype, 0, apkName, apkName, R.drawable.thumbnail_test, R.drawable.theme_preview_test, STR_NO_CATEGORY_ID, null, 0));
                    }
                }
            }
        }
    }

    protected static void addThemes(Context context, int rid, Map<String, SwypeTheme> map) {
        TypedArray a = context.getResources().obtainTypedArray(rid);
        int[] resIds = new int[a.length()];
        for (int i = 0; i < resIds.length; i++) {
            resIds[i] = a.getResourceId(i, 0);
        }
        a.recycle();
        int[] attrs = {R.attr.themeProductId, R.attr.themeName, R.attr.themeLogoPreview, R.attr.themeKeyboardPreview, R.attr.themeIsLocked, R.attr.themeCategoryId};
        for (int i2 = 0; i2 < resIds.length; i2++) {
            TypedArray a2 = context.obtainStyledAttributes(null, attrs, 0, resIds[i2]);
            String key = a2.getString(0);
            int nameResId = a2.getResourceId(1, 0);
            int previewResId = a2.getResourceId(2, 0);
            int keyboardPreviewResId = a2.getResourceId(3, 0);
            if (a2.getString(5) == null) {
                map.put(key, new SwypeTheme(resIds[i2], nameResId, key, null, previewResId, keyboardPreviewResId, STR_NO_CATEGORY_ID, null, 0));
            }
            a2.recycle();
        }
    }

    public static ThemeManager createInstance(Context context) {
        Map<String, SwypeTheme> map = new HashMap<>();
        addThemes(context, R.array.theme_resids, map);
        UserPreferences usrPrefs = UserPreferences.from(context);
        String currThemeId = usrPrefs.getCurrentThemeId();
        Map<String, SwypeTheme> oemMap = new HashMap<>();
        addThemes(context, R.array.theme_resids_oem, oemMap);
        map.putAll(oemMap);
        Map<String, LinkedHashMap<String, SwypeTheme>> themeCategoryMap = new LinkedHashMap<>();
        themeCategoryMap.put(STR_NO_CATEGORY_ID, new LinkedHashMap<>());
        Properties props = new Properties();
        InputStream in = null;
        try {
            try {
                in = context.getAssets().open(CONFIG_PROPERTIES);
                if (in != null) {
                    props.load(in);
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            } catch (IOException ex) {
                log.e("Failed to load config properties", ex);
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e2) {
                    }
                }
            }
            String menu = props.getProperty(PROP_KEY_MENU);
            if (menu == null) {
                Iterator<String> it = map.keySet().iterator();
                if (it.hasNext()) {
                    menu = it.next();
                }
                log.e("Missing theme list!");
            }
            List<String> menuOrder = new ArrayList<>();
            for (String key : oemMap.keySet()) {
                menuOrder.add(key);
            }
            for (String menuKey : menu.toString().split(",")) {
                log.d("menu key:", menuKey);
                if (menuKey.contains(XMLResultsHandler.SEP_HYPHEN)) {
                    String temKey = menuKey.replace(XMLResultsHandler.SEP_HYPHEN, Document.ID_SEPARATOR);
                    menuOrder.add(temKey);
                } else {
                    menuOrder.add(menuKey);
                }
            }
            SwypeTheme currentTheme = null;
            if (currThemeId != null) {
                SwypeTheme currentTheme2 = map.get(currThemeId);
                currentTheme = currentTheme2;
            }
            SwypeTheme defaultTheme = null;
            if (currentTheme != null) {
                defaultTheme = currentTheme;
                usrPrefs.setCurrentThemeId(defaultTheme.getSku());
                themeCategoryMap.get(STR_NO_CATEGORY_ID).put(currThemeId, currentTheme);
                Resources res = context.getResources();
                for (String menuKey2 : menuOrder) {
                    SwypeTheme theme = map.get(menuKey2);
                    if (theme != null) {
                        if (!theme.getDisplayName(res).equals(currentTheme.getDisplayName(res))) {
                            themeCategoryMap.get(STR_NO_CATEGORY_ID).put(menuKey2, theme);
                        }
                    } else {
                        log.d("Theme not found: " + menuKey2);
                    }
                }
            } else {
                if (!map.isEmpty() && menuOrder.size() > 0 && currThemeId == null && (defaultTheme = map.get(menuOrder.get(0))) != null) {
                    usrPrefs.setCurrentThemeId(defaultTheme.getSku());
                }
                for (String menuKey3 : menuOrder) {
                    SwypeTheme theme2 = map.get(menuKey3);
                    if (theme2 != null) {
                        themeCategoryMap.get(STR_NO_CATEGORY_ID).put(menuKey3, theme2);
                    } else {
                        log.d("Theme not found: " + menuKey3);
                    }
                }
            }
            ThemeManager themeManager = new ThemeManager(context);
            themeManager.themeCategoryMap = themeCategoryMap;
            themeManager.themeMap = themeCategoryMap.get(STR_NO_CATEGORY_ID);
            themeManager.menuOrder = menuOrder;
            themeManager.defaultTheme = defaultTheme;
            ThemeData.getCache().clear();
            themeManager.setEmbeddedThemeList(menuOrder);
            return themeManager;
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e3) {
                }
            }
            throw th;
        }
    }

    public void updateThemesOrderIfOem(SwypeTheme currentTheme, Resources res) {
        if (!isDownloadableThemesEnabled() && currentTheme != null && this.themeCategoryMap != null) {
            LinkedHashMap<String, SwypeTheme> newMap = new LinkedHashMap<>();
            newMap.put(currentTheme.mSku, currentTheme);
            Map<String, SwypeTheme> map = this.themeCategoryMap.get(STR_NO_CATEGORY_ID);
            for (String menuKey : this.menuOrder) {
                SwypeTheme theme = map.get(menuKey);
                if (theme != null) {
                    if (!theme.getDisplayName(res).equals(currentTheme.getDisplayName(res))) {
                        newMap.put(menuKey, theme);
                    }
                } else {
                    log.d("Theme not found: " + menuKey);
                }
            }
            this.themeCategoryMap.clear();
            this.themeCategoryMap.put(STR_NO_CATEGORY_ID, newMap);
            this.themeMap = this.themeCategoryMap.get(STR_NO_CATEGORY_ID);
        }
    }

    public static ThemeManager createThemeManager(Context ctx) {
        setIsDownloadableThemesEnabled(BuildInfo.from(ctx).isDownloadableThemesEnabled());
        isThemesLocked = AppPreferences.from(ctx).getDefaultBoolean(R.bool.enable_lock_themes);
        return createInstance(ctx);
    }

    public void refetchPurchaseInfoFromGoogleStore(final Context ctx) {
        if (isDownloadableThemesEnabled()) {
            if (isBillingServiceAvailabeOnDevice(ctx)) {
                setIsInAppServiceExisting(true);
            } else {
                setIsInAppServiceExisting(false);
            }
            if (this.mThemePurchaser == null) {
                this.mThemePurchaser = new ThemePurchaser(ctx);
            }
            this.mThemePurchaser.setupInAppBillingService(new IabHelper.OnIabSetupFinishedListener() { // from class: com.nuance.swype.input.ThemeManager.3
                @Override // com.nuance.swype.inapp.util.IabHelper.OnIabSetupFinishedListener
                public void onIabSetupFinished(IabResult result) {
                    if (result.isSuccess()) {
                        ThemeManager.setIsInAppApiSupported(true);
                        AccountUtil.setGoogleAccountMissing(ThemeManager.this.themeManagerContext, false);
                    } else if (!result.isSuccess() && (result.mMessage.equals(IabHelper.getResponseDetailedDesc("inapp_not_supported", result.mResponse)) || result.mMessage.equals(IabHelper.getResponseDetailedDesc("Billing_service_unavailable", result.mResponse)))) {
                        ThemeManager.setIsInAppApiSupported(false);
                        if (result.mMessage.equals(IabHelper.getResponseDetailedDesc("inapp_not_supported", result.mResponse)) && result.mResponse == 3) {
                            try {
                                if (ThemeManager.this.themeManagerContext.getResources().getInteger(R.integer.google_play_versioncode_min) > ThemeManager.this.themeManagerContext.getPackageManager().getPackageInfo("com.android.vending", 0).versionCode) {
                                    ThemeManager.log.d("Google Play should be updated");
                                } else {
                                    ThemeManager.log.d("Login Google Play using an valid account");
                                    AccountUtil.setGoogleAccountMissing(ThemeManager.this.themeManagerContext, true);
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                ThemeManager.log.e("Google Play is not available:" + e.getMessage());
                            }
                        }
                    }
                    if (ThemeManager.isInAppApiSupported() && !AccountUtil.isGoogleAccountMissing()) {
                        ThemeManager.this.mThemePurchaser.cleanup(result);
                        ThemeManager.this.initializeInAppPurchase(ctx);
                        if (!ThemeManager.isInAppApiSupported()) {
                            UserPreferences.from(ctx).getGoogleServiceUpgradeDialogShown();
                            return;
                        }
                        return;
                    }
                    ThemeManager.log.d("google store is not available,show free categories.");
                    CatalogManager catalogManager = IMEApplication.from(ctx).getCatalogManager();
                    if (catalogManager != null) {
                        if (AccountUtil.isGoogleAccountMissing()) {
                            if (catalogManager.getCatalogService() != null && !ThemeManager.this.hasNoPriceThemes()) {
                                catalogManager.getCatalogService().resetCatalogItemPrice();
                            }
                            catalogManager.triggerShowingThemesWithoutPrices();
                            return;
                        }
                        catalogManager.triggerShowingFreeCategories();
                    }
                }
            });
        }
    }

    public static Dialog createUpgradeGooglePlayDialog(Context ctx) {
        return new AlertDialog.Builder(ctx).setTitle(R.string.theme_upgrade_google_play_service_title).setMessage(R.string.theme_upgrade_google_play_service_desc).setNegativeButton(R.string.dismiss_button, (DialogInterface.OnClickListener) null).create();
    }

    public static Dialog createGoogleAccountLoginFailedDialog(Context ctx) {
        return new AlertDialog.Builder(ctx).setTitle(R.string.theme_upgrade_google_play_service_title).setMessage(R.string.login_failed).setNegativeButton(R.string.dismiss_button, (DialogInterface.OnClickListener) null).create();
    }

    public static boolean isIapFullySupported() {
        return isDownloadableThemesEnabled() && isInAppApiSupported() && isInAppServiceExisting();
    }

    public Dialog showNotAvailableDialogForGoogleTrial(final Context context, final OnThemePreviewDialogListener listener) {
        final String appStoreUrl = context.getString(R.string.url_android_market_dtc_details);
        return new AlertDialog.Builder(context).setIcon(R.drawable.swype_logo).setTitle(R.string.swype_label).setMessage(R.string.theme_buy_swype_desc).setPositiveButton(R.string.license_buy_now, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.ThemeManager.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent marketIntent = new Intent("android.intent.action.VIEW", Uri.parse(appStoreUrl));
                marketIntent.addFlags(268435456);
                ThemeManager.recordThemePreviewData("yes", context);
                listener.onThemePreivewDialogClosed();
                context.startActivity(marketIntent);
            }
        }).setNegativeButton(R.string.label_back, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.ThemeManager.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                ThemeManager.recordThemePreviewData("no", context);
                listener.onThemePreivewDialogClosed();
                dialog.dismiss();
            }
        }).create();
    }

    public static void recordThemePreviewData(String info, Context context) {
        Map<String, String> values = new HashMap<>();
        values.put("Trial Conversion", info);
        UsageData.recordEvent(UsageData.Event.THEME_PREVIEW_TRIAL_CONVERSION, values);
    }

    public void onThemeChanged(String fromThemeSku, String toThemeSku, Context context) {
        log.d("Theme changed from: " + fromThemeSku + ", to: " + toThemeSku);
        IME ime = IMEApplication.from(context).getIME();
        if (ime != null) {
            ime.getKeyboardBackgroundManager().setReloadRequiredFromResources(true);
        }
        recordThemeChanged(fromThemeSku, toThemeSku, context);
    }

    public void recordThemeChanged(String fromThemeSku, String toThemeSku, Context context) {
        String[] themePath;
        if (StringUtils.isApkCompletePath(fromThemeSku) && (themePath = fromThemeSku.split("/")) != null && themePath.length > 2) {
            fromThemeSku = themePath[themePath.length - 2];
        }
        Map<String, String> values = new HashMap<>();
        values.put("Theme Changed", "From: " + fromThemeSku + " to: " + toThemeSku);
        values.put("Theme Type", "From: " + getThemePaidType(getSwypeTheme(fromThemeSku)) + " to: " + getThemePaidType(getSwypeTheme(toThemeSku)));
        UsageData.recordEvent(UsageData.Event.STORE_THEME_CHANGED, values);
    }

    private String getThemePaidType(SwypeTheme theme) {
        if (theme.getSource() != SwypeTheme.THEME_SOURCE.CONNECT || ((ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().isFree) {
            return "free";
        }
        return "paid";
    }

    public void importMlsHotwords(Context ctx) {
        InputMethods.Language lan = InputMethods.from(ctx).getCurrentInputLanguage();
        if (lan != null && !lan.isCJK() && IMEApplication.from(ctx).getIME() != null && !UserPreferences.from(ctx).getMlsHotWordsImported()) {
            importMls(ctx, -1);
            UserPreferences.from(ctx).setMlsHotWordsImported();
        }
    }

    public void importMls(Context ctx, int leftNum) {
        IMEApplication imeApp = IMEApplication.from(ctx);
        NewWordsBucketFactory.NewWordsBucket bucket = imeApp.getNewWordsBucketFactory().getMlsThemeWordsBucketInstance();
        Set<String> names = new HashSet<>();
        Resources resource = ctx.getResources();
        for (int i = 0; i < mls_hotwords.length; i++) {
            String[] nameArray = resource.getString(mls_hotwords[i]).split(":");
            for (int j = 0; j < nameArray.length; j++) {
                if (!TextUtils.isEmpty(nameArray[j])) {
                    names.add(nameArray[j]);
                }
            }
        }
        bucket.add(names);
        if (leftNum > 0) {
            while (bucket.size() > leftNum) {
                bucket.remove();
            }
        }
        log.d("import MLS hot words:", names.toString());
        imeApp.notifyNewWordsForScanning(bucket);
    }

    public void setThemesCategory(ThemesCategory themeCategory) {
        this.themeCategory = themeCategory;
    }

    public void onPurchaseFinished(int resultCode, String sku, String categoryId) {
        if (this.themeCategory != null) {
            this.themeCategory.onPurchaseFinished(resultCode, sku, categoryId);
        }
    }

    public static void setUpdateNotificationAllowed(boolean allow) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SwypeThemeWeightComparator implements Comparator<SwypeTheme> {
        Collator collator;

        private SwypeThemeWeightComparator() {
            this.collator = Collator.getInstance();
        }

        @Override // java.util.Comparator
        public int compare(SwypeTheme lhs, SwypeTheme rhs) {
            return rhs.weight == lhs.weight ? this.collator.compare(lhs.getDisplayName(ThemeManager.this.themeManagerContext.getResources()), rhs.getDisplayName(ThemeManager.this.themeManagerContext.getResources())) : rhs.weight - lhs.weight;
        }
    }

    public void setThemeManagerContext(Context context) {
        this.themeManagerContext = context;
    }
}
