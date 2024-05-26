package com.nuance.swype.inapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import com.nuance.android.compat.SharedPreferencesEditorCompat;
import com.nuance.connect.api.CatalogService;
import com.nuance.swype.connect.ConnectedStatus;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.input.ThemeWordListManager;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.settings.SettingsDispatch;
import com.nuance.swype.input.settings.SettingsV11;
import com.nuance.swype.input.store.ThemeTabHostManager;
import com.nuance.swype.measure.UsecaseStopwatch;
import com.nuance.swype.measure.Usecases;
import com.nuance.swype.service.impl.AccountUtil;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.storage.ThemeData;
import com.nuance.swype.util.storage.ThemeItemSeed;
import com.nuance.swypeconnect.ac.ACCatalogService;
import com.nuance.swypeconnect.ac.ACException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/* loaded from: classes.dex */
public final class CatalogManager {
    private OnCatalogCallBackListener callbackListener;
    public ConnectedStatus connection;
    private UsageData.DownloadLocation downloadLocation;
    private boolean isPurchaseInfoFetched;
    public WeakReference<SettingsV11> mActivity;
    public Context mContext;
    public InstalledList mInstalledList;
    public boolean mServiceEnabled;
    public Map<String, Boolean> purchasedMap;
    protected static final LogManager.Log log = LogManager.getLog("CatalogManager");
    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    public static final Executor mSerialExecutor = new SerialExecutor(0);
    private AsyncTask<Void, Void, List<ACCatalogService.ACCatalogItem>> catalogChangedTask = null;
    private AsyncTask<Void, Void, List<ACCatalogService.ACCatalogItem>> skuChangedTask = null;
    ACCatalogService.ACCatalogCallback mCatalogServiceCallback = new ACCatalogService.ACCatalogCallback() { // from class: com.nuance.swype.inapp.CatalogManager.1
        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogCallback
        public final void catalogItemListChanged() {
            CatalogManager.log.d("Catalog item changed");
            if (!BuildInfo.from(CatalogManager.this.mContext).isGoogleTrialBuild()) {
                if (CatalogManager.this.catalogChangedTask != null) {
                    CatalogManager.this.catalogChangedTask.cancel(true);
                }
                CatalogManager.this.catalogChangedTask = new AsyncTask<Void, Void, List<ACCatalogService.ACCatalogItem>>() { // from class: com.nuance.swype.inapp.CatalogManager.1.1
                    @Override // android.os.AsyncTask
                    protected final /* bridge */ /* synthetic */ List<ACCatalogService.ACCatalogItem> doInBackground(Void[] voidArr) {
                        return doInBackground$68cf9880();
                    }

                    @Override // android.os.AsyncTask
                    protected final /* bridge */ /* synthetic */ void onPostExecute(List<ACCatalogService.ACCatalogItem> list) {
                        ThemeTabHostManager tabHostManager;
                        ThemeTabHostManager tabHostManager2;
                        List<ACCatalogService.ACCatalogItem> list2 = list;
                        if (list2 != null) {
                            ThemeManager from = ThemeManager.from(CatalogManager.this.mContext);
                            boolean addConnectedThemesToMap = from.addConnectedThemesToMap();
                            SettingsV11 access$400 = CatalogManager.access$400(CatalogManager.this);
                            if (ThemeManager.isDownloadableThemesEnabled()) {
                                from.getThemeDataManager();
                                if (!ThemeData.isCacheEmpty() && (!ThemeManager.isInAppApiSupported() || !ThemeManager.isInAppServiceExisting())) {
                                    if (access$400 != null && access$400.isCurrentThemeStoreShowing() && (tabHostManager2 = access$400.getTabHostManager()) != null) {
                                        if (!addConnectedThemesToMap) {
                                            if (CatalogManager.this.callbackListener != null) {
                                                CatalogManager.log.d("call back refresh free themes");
                                                CatalogManager.this.callbackListener.onCatalogItemListChanged();
                                                return;
                                            }
                                            return;
                                        }
                                        tabHostManager2.showMainTabsList(tabHostManager2.isCurrentTabBuyThemes() ? 1 : 0, ThemeTabHostManager.REFRESH_SOURCE.CATALOG_CALLBACK);
                                        return;
                                    }
                                    return;
                                }
                            }
                            if (CatalogManager.access$600(CatalogManager.this, list2) || (list2.size() == 0 && CatalogManager.this.getCatalogService().getSKUListForPurchase() != null && CatalogManager.this.getCatalogService().getSKUListForPurchase().size() > 0)) {
                                CatalogManager.access$702$5c4c8ca3(CatalogManager.this);
                                CatalogManager.this.fetchPurchaseInfoFromGoogleStore();
                            }
                            if (access$400 == null || (tabHostManager = access$400.getTabHostManager()) == null || !ThemeManager.isDownloadableThemesEnabled() || !ThemeManager.isInAppApiSupported() || !ThemeManager.isInAppServiceExisting()) {
                                return;
                            }
                            from.getThemeDataManager();
                            if (!ThemeData.isCacheEmpty()) {
                                if ((CatalogManager.this.purchasedMap == null && !addConnectedThemesToMap) || !access$400.isCurrentThemeStoreShowing()) {
                                    return;
                                }
                                CatalogManager.log.d("call back refresh all themes");
                                if (!addConnectedThemesToMap) {
                                    return;
                                }
                                tabHostManager.showMainTabsList(tabHostManager.isCurrentTabBuyThemes() ? 1 : 0, ThemeTabHostManager.REFRESH_SOURCE.CATALOG_CALLBACK);
                            }
                        }
                    }

                    private List<ACCatalogService.ACCatalogItem> doInBackground$68cf9880() {
                        List<ACCatalogService.ACCatalogItem> items = CatalogManager.this.getCatalogService().getCatalogItems();
                        ThemeManager.from(CatalogManager.this.mContext).getThemeDataManager();
                        if (!ThemeData.isCacheEmpty()) {
                            if (ThemeData.isItemDeleted()) {
                                ThemeData.getCache().clear();
                            } else {
                                ThemeData.resetItemDeleteStatus();
                            }
                        }
                        boolean updateSuccessful = false;
                        try {
                            CatalogManager.this.updateThemesListFromConnect(items);
                            updateSuccessful = true;
                        } catch (Exception e) {
                            CatalogManager.log.e("Themes update failed");
                        }
                        if (!updateSuccessful) {
                            return null;
                        }
                        CatalogManager.log.d("New catalog item list size is: ", Integer.valueOf(items.size()));
                        List<String> categories = CatalogManager.this.getCatalogService().getCatalogItemCategories();
                        for (int i = 0; i < categories.size(); i++) {
                            CatalogManager.log.d("categories:", Integer.valueOf(i), ":", categories.get(i));
                        }
                        CatalogManager.log.d("call back onCatalogItemListChanged");
                        return items;
                    }
                };
                CatalogManager.this.catalogChangedTask.executeOnExecutor(CatalogManager.mSerialExecutor, null);
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogCallback
        public final void catalogSKUListChanged(List<String> strings) {
            CatalogManager.log.d("Catalog SKU list changed");
            if (strings != null) {
                CatalogManager.log.d("Changed SKUs: ", strings);
            }
            if (CatalogManager.this.skuChangedTask != null) {
                CatalogManager.this.skuChangedTask.cancel(true);
            }
            CatalogManager.this.skuChangedTask = new AsyncTask<Void, Void, List<ACCatalogService.ACCatalogItem>>() { // from class: com.nuance.swype.inapp.CatalogManager.1.2
                @Override // android.os.AsyncTask
                protected final /* bridge */ /* synthetic */ void onPostExecute(List<ACCatalogService.ACCatalogItem> list) {
                    List<ACCatalogService.ACCatalogItem> list2 = list;
                    if (list2 == null || !CatalogManager.access$600(CatalogManager.this, list2) || !AccountUtil.isGoogleAccountMissing()) {
                        return;
                    }
                    CatalogManager.this.triggerShowingThemesWithoutPrices();
                }

                @Override // android.os.AsyncTask
                protected final /* bridge */ /* synthetic */ List<ACCatalogService.ACCatalogItem> doInBackground(Void[] voidArr) {
                    List<ACCatalogService.ACCatalogItem> catalogItems = CatalogManager.this.getCatalogService().getCatalogItems();
                    CatalogManager.logItems("New items received in catalogSKUListChanged(): ", catalogItems);
                    CatalogManager.log.d("Listing categories in catalogSKUListChanged()");
                    Iterator<String> it = CatalogManager.this.getCatalogService().getCatalogItemCategories().iterator();
                    while (it.hasNext()) {
                        CatalogManager.log.d("catalog service category:", it.next());
                    }
                    return catalogItems;
                }
            };
            CatalogManager.this.skuChangedTask.executeOnExecutor(CatalogManager.mSerialExecutor, null);
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogCallback
        public final void catalogItemInstalledUpdates(List<ACCatalogService.ACCatalogItem> acCatalogItems) {
            CatalogManager.log.d("Catalog catalogItemInstalledUpdates changed");
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogCallback
        public final void skuListAvailable(List<String> arg0) {
            CatalogManager.logSkus$7dc49990();
        }

        @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogCallback
        public final void skuListUnavailable(List<String> arg0) {
            CatalogManager.logSkus$7dc49990();
        }
    };

    /* loaded from: classes.dex */
    public interface OnCatalogCallBackListener {
        void onCatalogItemListChanged();

        void onCatalogItemStatusChanged(int i, ThemeManager.ConnectDownloadableThemeWrapper connectDownloadableThemeWrapper, String str, String str2, ThemeStatusChange themeStatusChange);
    }

    /* loaded from: classes.dex */
    public enum ThemeStatusChange {
        THEME_NOSTATE,
        THEME_PURCHASED,
        THEME_INSTALLING,
        THEME_INSTALLED,
        THEME_UNINSTALLED,
        THEME_INSTALL_CANCELED
    }

    static /* synthetic */ boolean access$702$5c4c8ca3(CatalogManager x0) {
        x0.isPurchaseInfoFetched = false;
        return false;
    }

    /* loaded from: classes.dex */
    private static class SerialExecutor implements Executor {
        Runnable mActive;
        final ArrayDeque<Runnable> mTasks;

        private SerialExecutor() {
            this.mTasks = new ArrayDeque<>();
        }

        /* synthetic */ SerialExecutor(byte b) {
            this();
        }

        @Override // java.util.concurrent.Executor
        public final synchronized void execute(final Runnable r) {
            this.mTasks.offer(new Runnable() { // from class: com.nuance.swype.inapp.CatalogManager.SerialExecutor.1
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        r.run();
                    } finally {
                        SerialExecutor.this.scheduleNext();
                    }
                }
            });
            if (this.mActive == null) {
                scheduleNext();
            }
        }

        protected final synchronized void scheduleNext() {
            Runnable poll = this.mTasks.poll();
            this.mActive = poll;
            if (poll != null) {
                AsyncTask.THREAD_POOL_EXECUTOR.execute(this.mActive);
            }
        }
    }

    public final void refreshOfflineCatalogItemsWhenNetworkOff() {
        setUpService();
        if (getCatalogService() == null) {
            log.e("Cannot retrieve ACCatalogService");
            return;
        }
        List<ACCatalogService.ACCatalogItem> items = getCatalogService().getCatalogItems();
        if (items != null) {
            updateThemesListFromConnect(items);
            log.d("refreshOfflineCatalogItemsWhenNetworkOff New catalog item list size is: ", Integer.valueOf(items.size()));
        }
        List<String> categories = getCatalogService().getCatalogItemCategories();
        for (int i = 0; i < categories.size(); i++) {
            log.d("categories:", Integer.valueOf(i), ":", categories.get(i));
        }
        log.d("refreshOfflineCatalogItemsWhenNetworkOff refreshOfflineCatalogItemsWhenNetworkOff");
        ThemeManager.from(this.mContext).addConnectedThemesToMap();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fetchPurchaseInfoFromGoogleStore() {
        if (!this.isPurchaseInfoFetched) {
            UsecaseStopwatch.getInstance().stop(Usecases.ENABLE_CATALOG_SERVICE);
            setUpService();
            ThemeManager.from(this.mContext).refetchPurchaseInfoFromGoogleStore(this.mContext);
        }
    }

    public final void refetchPurchaseInfoFromGoolgeStore() {
        this.isPurchaseInfoFetched = false;
        this.purchasedMap = null;
        fetchPurchaseInfoFromGoogleStore();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void updateThemesListFromConnect(List<ACCatalogService.ACCatalogItem> items) {
        if (items != null) {
            try {
                ThemeManager from = ThemeManager.from(this.mContext);
                ACCatalogService catalogService = getCatalogService();
                for (ACCatalogService.ACCatalogItem aCCatalogItem : items) {
                    for (String str : aCCatalogItem.getCategoryList()) {
                        if (aCCatalogItem.getType() == CatalogService.CatalogItem.Type.BUNDLE && (aCCatalogItem instanceof ACCatalogService.ACCatalogItemBundle)) {
                            List<ACCatalogService.ACCatalogItem> bundledCatalogItemList = ((ACCatalogService.ACCatalogItemBundle) aCCatalogItem).getBundledCatalogItemList();
                            String categoryKeyForCategoryName = catalogService.getCategoryKeyForCategoryName(str);
                            if (categoryKeyForCategoryName == null) {
                                throw new Exception("Localized category name not found");
                            }
                            log.d("add seed to cache, category key:", categoryKeyForCategoryName);
                            ThemeItemSeed themeItemSeed = new ThemeItemSeed(str, null, bundledCatalogItemList, aCCatalogItem, categoryKeyForCategoryName);
                            from.getThemeDataManager();
                            ThemeData.syncToCache(themeItemSeed);
                            boolean z = themeItemSeed.isPurchased;
                            for (ACCatalogService.ACCatalogItem aCCatalogItem2 : bundledCatalogItemList) {
                                List<String> categoryList = aCCatalogItem2.getCategoryList();
                                if (categoryList != null) {
                                    String str2 = ThemeManager.STR_NO_CATEGORY_ID_DLT;
                                    if (categoryList.contains(str)) {
                                        str2 = catalogService.getCategoryKeyForCategoryName(str);
                                    }
                                    ThemeItemSeed themeItemSeed2 = new ThemeItemSeed(str, aCCatalogItem, null, aCCatalogItem2, str2);
                                    if (z && this.purchasedMap != null) {
                                        synchronized (this.purchasedMap) {
                                            this.purchasedMap.put(themeItemSeed2.sku, true);
                                        }
                                    }
                                    log.d("in bundle, theme sku:", themeItemSeed2.sku, " purchased:", Boolean.valueOf(themeItemSeed2.isPurchased), " category:", str2);
                                    from.getThemeDataManager();
                                    ThemeData.syncToCache(themeItemSeed2);
                                }
                            }
                        } else {
                            ThemeItemSeed themeItemSeed3 = new ThemeItemSeed(str, null, null, aCCatalogItem, catalogService.getCategoryKeyForCategoryName(str));
                            log.d("not in bundle, theme sku:", themeItemSeed3.sku, " purchased:", Boolean.valueOf(themeItemSeed3.isPurchased), " categroy:", str);
                            from.getThemeDataManager();
                            ThemeData.syncToCache(themeItemSeed3);
                        }
                        log.d("add seed to cache, category label:", str, " sku:", aCCatalogItem.getSKU());
                    }
                }
                logItems("Items added", items);
            } catch (Exception e) {
                log.e("Offline Themes update failed");
            }
        }
    }

    public final void setCatalogCallBackListener(OnCatalogCallBackListener listener, UsageData.DownloadLocation location) {
        log.d("setCatalogCallBackListener...listener: ", listener);
        this.callbackListener = listener;
        this.downloadLocation = location;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logItems(String s, List<ACCatalogService.ACCatalogItem> items) {
        log.d(s);
        for (ACCatalogService.ACCatalogItem item : items) {
            log.d("\t", "Added: ", item.getTitle(), " sku: ", item.getSKU(), "...category list: ", item.getCategoryList());
        }
    }

    public CatalogManager(Context context) {
        this.mContext = context;
    }

    public final ACCatalogService getCatalogService() {
        return IMEApplication.from(this.mContext).getConnect().getCatalogService();
    }

    public final void triggerShowingFreeCategories() {
        setUpService();
        ACCatalogService service = getCatalogService();
        if (service != null && ThemeManager.from(this.mContext).hasPricedThemes()) {
            IMEApplication.from(this.mContext).resetThemeManagedData();
            service.resetCatalogItemPrice();
            service.resetPurchasedSKU();
        }
    }

    public final void triggerShowingThemesWithoutPrices() {
        setUpService();
        ACCatalogService service = getCatalogService();
        if (service != null) {
            if (ThemeManager.from(this.mContext).hasPricedThemes()) {
                IMEApplication.from(this.mContext).resetThemeManagedData();
                service.resetCatalogItemPrice();
                service.resetPurchasedSKU();
            }
            Set<String> allSkus = new HashSet<>();
            allSkus.addAll(getSKUforPurchase());
            PriceMap priceMap = new PriceMap();
            log.d("triggerShowingThemesWithoutPrice... allSkus.size:", Integer.valueOf(allSkus.size()));
            for (String id : allSkus) {
                priceMap.addSkuWithPrice(id, ThemeManager.NO_PRICE);
            }
            if (!priceMap.mPriceMap.isEmpty()) {
                sendPurchaseInfoToConnect(priceMap);
            }
            this.isPurchaseInfoFetched = true;
        }
    }

    public static void logSkus$7dc49990() {
    }

    public final void cancelDownloadTheme(int index, String category, String sku) throws ACException {
        log.d("cancelDownloadTheme...theme: ", sku);
        ThemeItemSeed seed = getThemeSeed(category, sku);
        if (seed != null) {
            seed.setInstalling(false);
            seed.setInstalled(false);
            ThemeManager.from(this.mContext).updateThemeStatus(sku, ThemeStatusChange.THEME_INSTALL_CANCELED);
        }
        if (this.callbackListener != null) {
            this.callbackListener.onCatalogItemStatusChanged(index, null, seed.categoryKey, sku, ThemeStatusChange.THEME_INSTALL_CANCELED);
        }
        getCatalogService().cancelDownloadCatalogItem(sku);
    }

    public final void uninstallTheme(int themeViewIndex, String categoryKey, String sku) throws ACException {
        log.d("uninstallTheme...theme: ", sku);
        ThemeManager themeManager = ThemeManager.from(this.mContext);
        ACCatalogService catalogService = getCatalogService();
        catalogService.uninstallCatalogItem(sku);
        String installPath$7157d249 = installPath$7157d249(sku);
        log.d("uninstallDownloadedFile...delete themefile: ", installPath$7157d249);
        if (fileExists(installPath$7157d249)) {
            deleteFile(installPath$7157d249);
            String parent = new File(installPath$7157d249).getParent();
            log.d("Deleting theme background files in: " + parent);
            deleteFile(new File(parent, "background.png").getAbsolutePath());
            deleteFile(new File(parent, "background-land.png").getAbsolutePath());
            String[] split = installPath$7157d249.split("/");
            if (split.length > 1) {
                String replace = installPath$7157d249.replace("/" + split[split.length - 1], "");
                log.d("uninstallDownloadedFile...delete themeFolder: ", replace);
                if (fileExists(replace)) {
                    deleteFile(replace);
                }
            }
            log.d("uninstallDownloadedFile...done.");
        }
        ThemeItemSeed seed = getThemeSeed(categoryKey, sku);
        if (seed != null) {
            seed.setInstalling(false);
            seed.setInstalled(false);
            InstalledList installedList = this.mInstalledList;
            installedList.mSharedPreferences.edit().remove(seed.sku).commit();
            installedList.loadAll();
            themeManager.updateThemeStatus(sku, ThemeStatusChange.THEME_UNINSTALLED);
            List<String> categoryList = seed.themeCategories;
            if (categoryList != null) {
                for (String category : categoryList) {
                    String key = catalogService.getCategoryKeyForCategoryName(category);
                    ThemeItemSeed itemSeed = getThemeSeed(key, sku);
                    if (itemSeed != null) {
                        itemSeed.setInstalling(false);
                        itemSeed.setInstalled(false);
                        themeManager.updateThemeStatus(sku, ThemeStatusChange.THEME_UNINSTALLED);
                    }
                }
            }
            if (this.callbackListener != null) {
                this.callbackListener.onCatalogItemStatusChanged(themeViewIndex, null, categoryKey, sku, ThemeStatusChange.THEME_UNINSTALLED);
            }
        }
    }

    public final void downloadTheme(final int index, final ThemeManager.ConnectDownloadableThemeWrapper theme) throws ACException {
        final String sku = theme.getSku();
        if (!showNoSpaceNotificationIfShortStorage()) {
            log.d("Downloading theme with sku: ", sku);
            ThemeItemSeed seed = getThemeSeed(theme.getThemeItemSeed().categoryKey, sku);
            if (seed != null) {
                seed.setInstalling(true);
                seed.installingPercentage = 0;
            }
            ThemeManager.from(this.mContext).getThemeDataManager();
            ThemeData.update(sku, ThemeStatusChange.THEME_INSTALLING);
            if (this.callbackListener != null) {
                this.callbackListener.onCatalogItemStatusChanged(index, theme, seed.categoryKey, sku, ThemeStatusChange.THEME_INSTALLING);
            }
            getCatalogService().downloadCatalogItem(sku, new ACCatalogService.ACCatalogItemDownloadCallback() { // from class: com.nuance.swype.inapp.CatalogManager.4
                @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItemDownloadCallback
                public final void downloadStarted() {
                    CatalogManager.log.d("Starting download for: ", sku);
                    ThemeItemSeed seed2 = CatalogManager.this.getThemeSeed(theme.getThemeItemSeed().categoryKey, sku);
                    if (seed2 != null) {
                        seed2.setInstalling(true);
                        seed2.installingPercentage = 0;
                        if (CatalogManager.this.callbackListener != null) {
                            CatalogManager.this.callbackListener.onCatalogItemStatusChanged(index, theme, seed2.categoryKey, sku, ThemeStatusChange.THEME_INSTALLING);
                            return;
                        }
                        return;
                    }
                    CatalogManager.log.e("Seed data not found when downloading.");
                }

                @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItemDownloadCallback
                public final void downloadPercentage(int i) {
                    CatalogManager.log.d("Download %", Integer.valueOf(i));
                    if (CatalogManager.this.showNoSpaceNotificationIfShortStorage()) {
                        try {
                            CatalogManager.this.cancelDownloadTheme(index, theme.getThemeItemSeed().categoryKey, sku);
                            return;
                        } catch (ACException e) {
                            CatalogManager.log.e("downloadPercentage...cancelDownloadTheme exception: ", e);
                            return;
                        }
                    }
                    ThemeItemSeed seed2 = CatalogManager.this.getThemeSeed(theme.getThemeItemSeed().categoryKey, sku);
                    if (seed2 == null) {
                        return;
                    }
                    seed2.installingPercentage = i;
                    if (CatalogManager.this.callbackListener != null) {
                        CatalogManager.this.callbackListener.onCatalogItemStatusChanged(index, theme, seed2.categoryKey, sku, ThemeStatusChange.THEME_INSTALLING);
                    }
                }

                @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItemDownloadCallback
                public final boolean downloadComplete(File file) {
                    CatalogManager.log.d("Download complete. File at: ", file.getAbsolutePath());
                    CatalogManager catalogManager = CatalogManager.this;
                    String str = theme.getThemeItemSeed().categoryKey;
                    if (!catalogManager.installDownloadedFile$14e1ec69(sku, file.getAbsolutePath())) {
                        if (CatalogManager.this.callbackListener != null) {
                            ThemeItemSeed seed2 = CatalogManager.this.getThemeSeed(theme.getThemeItemSeed().categoryKey, sku);
                            ThemeManager.from(CatalogManager.this.mContext).getThemeDataManager();
                            ThemeData.update(sku, ThemeStatusChange.THEME_INSTALLING);
                            CatalogManager.this.callbackListener.onCatalogItemStatusChanged(index, theme, seed2.categoryKey, sku, ThemeStatusChange.THEME_INSTALLING);
                        }
                        UsageData.recordDownloadEvent(sku, CatalogManager.this.downloadLocation, UsageData.DownloadResult.CANCELED);
                        return false;
                    }
                    CatalogManager.log.d("downloadComplete..installDownloadedFile....STATUS_SUCCESS.");
                    CatalogManager catalogManager2 = CatalogManager.this;
                    String str2 = theme.getThemeItemSeed().categoryKey;
                    String themeFile = catalogManager2.installPath$7157d249(sku);
                    CatalogManager.this.addWordListFromApk(sku, themeFile);
                    List<String> categoryList = theme.getThemeItemSeed().themeCategories;
                    if (categoryList == null) {
                        categoryList = new ArrayList<>();
                        categoryList.add(theme.getThemeItemSeed().themeCategoryLabel);
                    }
                    if (categoryList != null) {
                        for (String category : categoryList) {
                            String categoryKey = CatalogManager.this.getCatalogService().getCategoryKeyForCategoryName(category);
                            ThemeItemSeed seed3 = CatalogManager.this.getThemeSeed(categoryKey, sku);
                            if (seed3 != null) {
                                seed3.installingPercentage = 100;
                                seed3.setInstalling(false);
                                seed3.setInstalled(true);
                                InstalledList installedList = CatalogManager.this.mInstalledList;
                                SharedPreferencesEditorCompat.apply(installedList.mSharedPreferences.edit().putBoolean(seed3.sku, true));
                                installedList.loadAll();
                                ThemeManager.from(CatalogManager.this.mContext).updateThemeStatus(sku, ThemeStatusChange.THEME_INSTALLED);
                            }
                            if (CatalogManager.this.callbackListener != null) {
                                CatalogManager.this.callbackListener.onCatalogItemStatusChanged(index, theme, categoryKey, sku, ThemeStatusChange.THEME_INSTALLED);
                            }
                        }
                    }
                    if (CatalogManager.this.callbackListener != null) {
                        String key = CatalogManager.this.getCatalogService().getCategoryKeyForCategoryName(categoryList.get(0));
                        CatalogManager.this.callbackListener.onCatalogItemStatusChanged(index, theme, key, sku, ThemeStatusChange.THEME_INSTALLED);
                    }
                    UsageData.recordDownloadEvent(sku, CatalogManager.this.downloadLocation, UsageData.DownloadResult.COMPLETED);
                    return true;
                }

                @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItemDownloadCallback
                public final void downloadStopped(int i) {
                    CatalogManager.log.d("Download stopped. Reason: ", Integer.valueOf(i));
                    ThemeItemSeed seed2 = CatalogManager.this.getThemeSeed(theme.getThemeItemSeed().categoryKey, sku);
                    if (seed2 != null) {
                        seed2.setInstalling(false);
                        ThemeManager.from(CatalogManager.this.mContext).getThemeDataManager();
                        ThemeData.update(sku, ThemeStatusChange.THEME_INSTALL_CANCELED);
                        if (CatalogManager.this.callbackListener != null) {
                            CatalogManager.this.callbackListener.onCatalogItemStatusChanged(index, theme, seed2.categoryKey, sku, ThemeStatusChange.THEME_INSTALL_CANCELED);
                        }
                    }
                    if (theme.getType() != CatalogService.CatalogItem.Type.BUNDLE.ordinal()) {
                        if (!CatalogManager.this.showNoSpaceNotificationIfShortStorage() && i != 6) {
                            if (i == 3) {
                                UsageData.recordDownloadEvent(sku, CatalogManager.this.downloadLocation, UsageData.DownloadResult.CANCELED);
                                return;
                            } else {
                                UsageData.recordDownloadEvent(sku, CatalogManager.this.downloadLocation, UsageData.DownloadResult.NETWORK);
                                return;
                            }
                        }
                        UsageData.recordDownloadEvent(sku, CatalogManager.this.downloadLocation, UsageData.DownloadResult.NO_SPACE);
                    }
                }

                @Override // com.nuance.swypeconnect.ac.ACCatalogService.ACCatalogItemDownloadCallback
                public final void downloadFailed(int i) {
                    CatalogManager.log.d("Download failed: ", Integer.valueOf(i));
                    ThemeItemSeed seed2 = CatalogManager.this.getThemeSeed(theme.getThemeItemSeed().categoryKey, sku);
                    if (seed2 != null) {
                        seed2.setInstalling(true);
                        seed2.installingPercentage = 0;
                    }
                    ThemeManager.from(CatalogManager.this.mContext).getThemeDataManager();
                    ThemeData.update(sku, ThemeStatusChange.THEME_INSTALLING);
                    if (CatalogManager.this.callbackListener != null && seed2 != null) {
                        CatalogManager.this.callbackListener.onCatalogItemStatusChanged(index, theme, seed2.categoryKey, sku, ThemeStatusChange.THEME_INSTALLING);
                    }
                }
            });
            return;
        }
        UsageData.recordDownloadEvent(sku, this.downloadLocation, UsageData.DownloadResult.NO_SPACE);
    }

    protected final boolean installDownloadedFile$14e1ec69(String sku, String sourceFile) {
        String finalFileName = installPath$7157d249(sku);
        if (fileExists(finalFileName)) {
            deleteFile(finalFileName);
        }
        File source = new File(sourceFile);
        File destination = new File(finalFileName);
        log.d("installDownloadedFile...copy ", sourceFile, "  to destination: ", destination.toString());
        try {
            copyFile(source, destination);
            log.d("installDownloadedFile...succeed.");
            return true;
        } catch (IOException e) {
            log.d("installDownloadedFile...fail...e: ", e);
            return false;
        }
    }

    private static void copyFile(File source, File destination) throws IOException {
        log.d("copyFile...");
        InputStream ins = new FileInputStream(source);
        BufferedInputStream bis = new BufferedInputStream(ins, 20480);
        log.d("copyFile...bis: ", bis);
        FileOutputStream os = new FileOutputStream(destination);
        OutputStream bos = new BufferedOutputStream(os, 20480);
        log.d("copyFile...bos: ", bos);
        byte[] fileDataBuffer = new byte[20480];
        while (true) {
            int bytesRead = bis.read(fileDataBuffer);
            if (bytesRead != -1) {
                bos.write(fileDataBuffer, 0, bytesRead);
            } else {
                bos.close();
                bis.close();
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String installPath$7157d249(String sku) {
        String installPath = IMEApplication.from(this.mContext).getNewThemeApkPath(sku);
        String filePath = new File(installPath).getParent();
        if (!fileExists(filePath)) {
            new File(filePath).mkdirs();
        }
        log.d("install path: ", installPath);
        return installPath;
    }

    private static boolean deleteFile(String fileName) {
        log.d("Deleting file: " + fileName);
        return new File(fileName).delete();
    }

    private static boolean fileExists(String name) {
        try {
            boolean isThere = new File(name).exists();
            return isThere;
        } catch (SecurityException ex) {
            log.e("Security exception in file.exists()", ex);
            return false;
        }
    }

    public final void addWordListFromApk(String sku, String apkFilePath) {
        log.d("Adding word list from apk for sku: ", sku, " file: ", apkFilePath);
        new ThemeWordListManager.WordListAdderAsyncTask(this.mContext, sku, apkFilePath).execute(new Void[0]);
    }

    /* loaded from: classes.dex */
    public static class PriceMap {
        Map<String, String> mPriceMap = new ConcurrentHashMap();
        Map<String, Boolean> mPurchasedMap = new ConcurrentHashMap();

        public final void addSkuWithPrice(String sku, String price) {
            this.mPriceMap.put(sku, price);
        }

        public final String toString() {
            StringBuffer buffer = new StringBuffer();
            for (String sku : this.mPriceMap.keySet()) {
                buffer.append(String.format("(%s, %s) ", sku, this.mPriceMap.get(sku)));
            }
            return buffer.toString();
        }

        public final ArrayList<String> getPurchasedList() {
            ArrayList<String> purchasedSkuList = new ArrayList<>();
            for (String sku : this.mPurchasedMap.keySet()) {
                if (this.mPurchasedMap.get(sku).booleanValue()) {
                    purchasedSkuList.add(sku);
                }
            }
            return purchasedSkuList;
        }
    }

    /* loaded from: classes.dex */
    public static class PriceMapLocalStorageHelper {
        private static PriceMapLocalStorageHelper sInstance;
        private Context mContext;
        public ConcurrentHashMap<String, String> mPriceMap = new ConcurrentHashMap<>();

        public static PriceMapLocalStorageHelper getInstance(Context context) {
            if (sInstance == null) {
                sInstance = new PriceMapLocalStorageHelper(context.getApplicationContext());
            }
            return sInstance;
        }

        private PriceMapLocalStorageHelper(Context context) {
            this.mContext = context;
            new Thread(new Runnable() { // from class: com.nuance.swype.inapp.CatalogManager.PriceMapLocalStorageHelper.1
                @Override // java.lang.Runnable
                public final void run() {
                    synchronized (PriceMapLocalStorageHelper.this) {
                        PriceMapLocalStorageHelper.access$1600(PriceMapLocalStorageHelper.this);
                    }
                }
            }).start();
        }

        static void logMessage(String message, ConcurrentHashMap<String, String> priceMap) {
            CatalogManager.log.d(message);
            for (String sku : priceMap.keySet()) {
                CatalogManager.log.d("\t", sku, ": ", priceMap.get(sku));
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:43:0x0182 A[Catch: IOException -> 0x0186, TRY_LEAVE, TryCatch #8 {IOException -> 0x0186, blocks: (B:49:0x017d, B:43:0x0182), top: B:48:0x017d }] */
        /* JADX WARN: Removed duplicated region for block: B:48:0x017d A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        static /* synthetic */ void access$1600(com.nuance.swype.inapp.CatalogManager.PriceMapLocalStorageHelper r9) {
            /*
                Method dump skipped, instructions count: 455
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.inapp.CatalogManager.PriceMapLocalStorageHelper.access$1600(com.nuance.swype.inapp.CatalogManager$PriceMapLocalStorageHelper):void");
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:40:0x00c2 A[Catch: IOException -> 0x00c6, TRY_LEAVE, TryCatch #6 {IOException -> 0x00c6, blocks: (B:46:0x00bd, B:40:0x00c2), top: B:45:0x00bd }] */
        /* JADX WARN: Removed duplicated region for block: B:45:0x00bd A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Type inference failed for: r1v0 */
        /* JADX WARN: Type inference failed for: r1v1 */
        /* JADX WARN: Type inference failed for: r1v10, types: [java.io.ObjectOutputStream] */
        /* JADX WARN: Type inference failed for: r1v12 */
        /* JADX WARN: Type inference failed for: r1v14 */
        /* JADX WARN: Type inference failed for: r1v15 */
        /* JADX WARN: Type inference failed for: r1v16 */
        /* JADX WARN: Type inference failed for: r1v17, types: [java.io.ObjectOutputStream] */
        /* JADX WARN: Type inference failed for: r1v18, types: [com.nuance.swype.util.LogManager$Log] */
        /* JADX WARN: Type inference failed for: r1v19 */
        /* JADX WARN: Type inference failed for: r1v2 */
        /* JADX WARN: Type inference failed for: r1v4, types: [java.io.ObjectOutputStream] */
        /* JADX WARN: Type inference failed for: r1v5, types: [com.nuance.swype.util.LogManager$Log] */
        /* JADX WARN: Type inference failed for: r1v6, types: [java.io.ObjectOutputStream] */
        /* JADX WARN: Type inference failed for: r3v0 */
        /* JADX WARN: Type inference failed for: r3v1 */
        /* JADX WARN: Type inference failed for: r3v10 */
        /* JADX WARN: Type inference failed for: r3v12, types: [java.io.OutputStream, java.io.FileOutputStream] */
        /* JADX WARN: Type inference failed for: r3v13, types: [java.lang.Object[]] */
        /* JADX WARN: Type inference failed for: r3v3, types: [java.io.FileOutputStream] */
        /* JADX WARN: Type inference failed for: r3v4, types: [java.lang.Object[]] */
        /* JADX WARN: Type inference failed for: r3v5, types: [java.io.FileOutputStream] */
        /* JADX WARN: Type inference failed for: r3v7 */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        static /* synthetic */ void access$1700(com.nuance.swype.inapp.CatalogManager.PriceMapLocalStorageHelper r9) {
            /*
                Method dump skipped, instructions count: 242
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.inapp.CatalogManager.PriceMapLocalStorageHelper.access$1700(com.nuance.swype.inapp.CatalogManager$PriceMapLocalStorageHelper):void");
        }
    }

    public final List<ThemeManager.SwypeTheme> getThemesForCategory(String categoryKey) {
        List<ThemeItemSeed> themesList = new ArrayList<>();
        ThemeManager.from(this.mContext).getThemeDataManager();
        HashMap<String, List<ThemeItemSeed>> cache = ThemeData.getCache();
        for (String categoryId : cache.keySet()) {
            themesList.addAll(cache.get(categoryId));
        }
        List<ThemeManager.SwypeTheme> themes = new ArrayList<>();
        for (ThemeItemSeed item : themesList) {
            log.d("item categoryKey label:", item.themeCategoryLabel, " item sku:", item.sku);
            if (item.categoryKey != null && item.categoryKey.equals(categoryKey)) {
                ThemeManager.ConnectDownloadableThemeWrapper theme = new ThemeManager.ConnectDownloadableThemeWrapper(item, categoryKey);
                log.d("theme categoryKey label:", theme.getThemeItemSeed().themeCategoryLabel, " theme sku:", theme.getSku(), " theme name:", theme.getThemeItemSeed().themeName);
                themes.add(theme);
            }
        }
        return themes;
    }

    public final ThemeItemSeed getThemeSeed(String category, String sku) {
        ThemeManager.from(this.mContext).getThemeDataManager();
        ThemeItemSeed seed = ThemeData.getThemeSeed(sku, category);
        if (seed == null) {
            ThemeManager.from(this.mContext).getThemeDataManager();
            seed = ThemeData.getThemeSeed(sku, null);
        }
        log.d("getThemeSeed...sku: ", sku, " category key:", category);
        return seed;
    }

    public final List<String> getSKUforPurchase() {
        log.d("Getting SKUs for purchase");
        if (!this.mServiceEnabled) {
            log.d("Service not enabled");
            return null;
        }
        return getCatalogService().getSKUListForPurchase();
    }

    public final List<String> getAllSKUs() {
        log.d("Getting all SKUs");
        if (!this.mServiceEnabled) {
            log.d("Service not enabled");
            return null;
        }
        return getCatalogService().getSKUList();
    }

    public final Set<String> readLastCheckedSkuList() {
        return UserPreferences.from(this.mContext).getLastCheckedSkuList(Collections.EMPTY_SET);
    }

    public final boolean hasStoredSkuList() {
        Set<String> set = UserPreferences.from(this.mContext).getLastCheckedSkuList(Collections.EMPTY_SET);
        return (set == null || set.isEmpty()) ? false : true;
    }

    public final boolean showNoSpaceNotificationIfShortStorage() {
        log.d("showNoSpaceNotificationIfShortStorage...free space: ", Long.valueOf(this.mContext.getFilesDir().getFreeSpace()));
        if (this.mContext.getFilesDir().getFreeSpace() >= 18874368) {
            return false;
        }
        log.d("sendOutOfSpaceNotification() ", " sending...");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.mContext);
        Intent intent = new Intent(this.mContext, (Class<?>) SettingsDispatch.class);
        intent.putExtra("fromNotification", true);
        PendingIntent activity = PendingIntent.getActivity(this.mContext, 0, intent, 134217728);
        Resources resources = this.mContext.getResources();
        NotificationCompat.Builder smallIcon = builder.setContentTitle(resources.getString(R.string.notification_default_title)).setContentText(resources.getString(R.string.error_out_of_disc_space)).setSmallIcon(R.drawable.swype_icon);
        smallIcon.mContentIntent = activity;
        smallIcon.setAutoCancel$7abcb88d();
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
        if (notificationManager != null) {
            notificationManager.notify(287211, builder.build());
        }
        return true;
    }

    public final void setUpService() {
        if (!this.mServiceEnabled) {
            ACCatalogService catalogService = getCatalogService();
            if (catalogService != null) {
                log.d("Registering for Catalog service callback");
                catalogService.registerCallback(this.mCatalogServiceCallback);
                getCatalogService().enableCatalog();
                log.d("Service enabled");
                this.mServiceEnabled = true;
                return;
            }
            log.e("Catalog service cannot be registered.");
        }
    }

    public final void sendPurchaseInfoToConnect(PriceMap priceMap) {
        ACCatalogService catalogService;
        final PriceMapLocalStorageHelper priceMapLocalStorageHelper = PriceMapLocalStorageHelper.getInstance(this.mContext);
        priceMapLocalStorageHelper.mPriceMap.putAll(priceMap.mPriceMap);
        PriceMapLocalStorageHelper.logMessage("new price map is: ", priceMapLocalStorageHelper.mPriceMap);
        new Thread(new Runnable() { // from class: com.nuance.swype.inapp.CatalogManager.PriceMapLocalStorageHelper.2
            @Override // java.lang.Runnable
            public final void run() {
                synchronized (PriceMapLocalStorageHelper.this) {
                    PriceMapLocalStorageHelper.access$1700(PriceMapLocalStorageHelper.this);
                }
            }
        }).start();
        ACCatalogService catalogService2 = getCatalogService();
        if (catalogService2 == null) {
            this.isPurchaseInfoFetched = false;
        } else {
            Map<String, String> map = priceMap.mPriceMap;
            this.purchasedMap = priceMap.mPurchasedMap;
            if (!map.isEmpty()) {
                log.d("Sending price list to Connect: ", map);
                catalogService2.setCatalogItemPrices(map);
                this.isPurchaseInfoFetched = true;
            }
        }
        ArrayList<String> purchasedList = priceMap.getPurchasedList();
        if (!this.mServiceEnabled || (catalogService = getCatalogService()) == null) {
            return;
        }
        if (!purchasedList.isEmpty()) {
            catalogService.setPurchasedSKUList(purchasedList);
        } else {
            catalogService.resetPurchasedSKU();
        }
    }

    public final void setPurchased(String category, String sku) {
        if (this.mServiceEnabled) {
            log.e("Notifying connect of purchase for sku " + sku);
            ThemeItemSeed oldSeed = getThemeSeed(category, sku);
            getCatalogService().setPurchasedSKU(sku);
            oldSeed.isPurchased = true;
            ThemeManager.from(this.mContext).getThemeDataManager();
            ThemeData.update(oldSeed);
            if (this.purchasedMap != null) {
                synchronized (this.purchasedMap) {
                    List<String> purchasedSkuList = new ArrayList<>();
                    purchasedSkuList.add(sku);
                    if (oldSeed.skuList != null) {
                        for (String s : oldSeed.skuList) {
                            purchasedSkuList.add(s);
                        }
                    }
                    for (String purchasedSku : purchasedSkuList) {
                        this.purchasedMap.put(purchasedSku, true);
                    }
                }
            }
        }
    }

    static /* synthetic */ SettingsV11 access$400(CatalogManager x0) {
        if (x0.mActivity != null) {
            return x0.mActivity.get();
        }
        return null;
    }

    static /* synthetic */ boolean access$600(CatalogManager x0, List x1) {
        log.d("Checking if theme updates are available");
        HashSet hashSet = new HashSet();
        Iterator it = x1.iterator();
        while (it.hasNext()) {
            hashSet.add(((ACCatalogService.ACCatalogItem) it.next()).getSKU());
        }
        Set<String> readLastCheckedSkuList = x0.readLastCheckedSkuList();
        HashSet hashSet2 = new HashSet();
        hashSet2.addAll(hashSet);
        hashSet2.removeAll(readLastCheckedSkuList);
        boolean z = hashSet2.size() > 0;
        if (z && x0.isPurchaseInfoFetched) {
            UserPreferences.from(x0.mContext).setLastCheckedSkuList(new HashSet(hashSet));
            if (!AccountUtil.isGoogleAccountMissing() && AccountUtil.isGoogleAccountSignedIn()) {
                IMEApplication.from(x0.mContext).getAppPreferences().setNewThemeAvailableInStore(true);
                NotificationCompat.Builder autoCancel$7abcb88d = new NotificationCompat.Builder(x0.mContext).setSmallIcon(R.drawable.swype_icon).setContentTitle(x0.mContext.getResources().getString(R.string.notification_default_title)).setContentText(x0.mContext.getResources().getString(R.string.checkout_new_themes)).setAutoCancel$7abcb88d();
                Intent intent = new Intent(x0.mContext, (Class<?>) SettingsDispatch.class);
                intent.putExtra("fromNotification", true);
                autoCancel$7abcb88d.mContentIntent = PendingIntent.getActivity(x0.mContext, 0, intent, 134217728);
                ((NotificationManager) x0.mContext.getSystemService("notification")).notify(287210, autoCancel$7abcb88d.build());
            }
        }
        return z;
    }
}
