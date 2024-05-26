package com.nuance.swype.input.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.bumptech.glide.Glide;
import com.nuance.android.compat.ConfigurationCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.connect.ConnectedStatus;
import com.nuance.swype.connect.SDKUpdateManager;
import com.nuance.swype.inapp.CatalogManager;
import com.nuance.swype.inapp.InstalledList;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.DatabaseConfig;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.PlatformUtil;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.settings.ThemesFragment;
import com.nuance.swype.input.store.ThemeActionBarManager;
import com.nuance.swype.input.store.ThemeFragmentController;
import com.nuance.swype.input.store.ThemeTabHostManager;
import com.nuance.swype.service.impl.AccountUtil;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.storage.ThemeData;
import com.nuance.swype.util.storage.ThemeItemSeed;
import com.nuance.swype.widget.CustomDrawerAdapter;
import com.nuance.swype.widget.DrawerItem;
import com.nuance.swypeconnect.ac.ACException;
import com.nuance.swypeconnect.ac.ACPlatformUpdateService;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class SettingsV11 extends FragmentActivity {
    private static final int DELAY_OPEN_DRAWER_AUTOMATICALLY = 10000;
    private static final int REQUEST_ADDING_GOOGLE_ACCOUNT = 100;
    public static final int REQUEST_CODE_TOS = 200;
    private static final String STATE_CURRENT_FRAGMENT = "current_fragment";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String STATE_SELECTED_SETTING_FRAGMENT = "selected_setting_fragment";
    private static final String SWYPE_SETTINGS_PERMISSION_REQUESTED = "swype_settings_permission_requested";
    public static final String TAB_BUNDLE_VIEW = "Bundle View Tab";
    public static final String TAB_CATEGORY_VIEW = "Category View Tab";
    public static final String TAB_GET_THEMES = "Get Themes Tab";
    public static final String TAB_MY_THEMES = "My Themes Tab";
    private static final String THEME_FRAGMENT_BUNDLE_SKU = "theme_fragment_bundle_sku";
    private static final String THEME_FRAGMENT_CATEGORY = "theme_fragment_category";
    private static final String THEME_FRAGMENT_TYPE = "theme_fragment_type";
    private static final String THEME_TAB_POSITION = "theme_tab_position";
    private static final String TOS_CURRENTLY_SHOWN = "tos_currently_shown";
    private static boolean isTablet_720DP;
    private static boolean sRunning;
    private ThemeActionBarManager actionbarManager;
    private CustomDrawerAdapter adapter;
    private Fragment currentFragment;
    private List<DrawerItem> dataList;
    private boolean isActivityResultReturned;
    private boolean isEnterMainStore;
    private boolean isPaused;
    private AlertDialog mAlertDialog;
    private HackyDrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mDrawerTitles;
    private ActionBarDrawerToggle mDrawerToggle;
    private NetworkStateReceiver mNetworkStateReceiver;
    private UsageData.Screen mPreviousScreen;
    private CharSequence mTitle;
    private KeyUpListener onKeyUpListener;
    private Fragment selectedSettingFragment;
    private ThemeTabHostManager tabHostManager;
    private boolean tosCurrentlyShown;
    private ACPlatformUpdateService.ACPlatformUpdateCallback updateCallback;
    private static final LogManager.Log log = LogManager.getLog("SettingsV11");
    private static int drawerItemsDefaultPosition = -1;
    public static Integer INSTANCE_COUNT = 0;
    private boolean shouldBackToStore = true;
    private int mCurrentSelectedPosition = -1;
    private boolean isPermissionRequested = false;
    private final Handler.Callback SettingsCallback = new Handler.Callback() { // from class: com.nuance.swype.input.settings.SettingsV11.8
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 10000:
                    SettingsV11.this.mDrawerLayout.openDrawer(8388611);
                    UserPreferences.from(SettingsV11.this.getApplicationContext()).setDrawerAutomaticallyOpenedOnce(true);
                    return true;
                default:
                    return false;
            }
        }
    };
    private final Handler mSettingsHandler = WeakReferenceHandler.create(this.SettingsCallback);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum DrawerItems {
        Drawer_Store,
        Drawer_Settings,
        Drawer_Themes,
        Drawer_Chinese_Settings,
        Drawer_My_Words,
        Drawer_Language_Category,
        Drawer_Divider,
        Drawer_Gestures,
        Drawer_Help,
        Drawer_Updates
    }

    /* loaded from: classes.dex */
    public interface KeyUpListener {
        boolean onKeyUp(int i, KeyEvent keyEvent);
    }

    public static boolean isRunning() {
        return sRunning;
    }

    private static void setRunning(boolean bValue) {
        sRunning = bValue;
    }

    /* loaded from: classes.dex */
    public class NetworkStateReceiver extends BroadcastReceiver {
        public NetworkStateReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (SettingsV11.this.isStoreShowing()) {
                SettingsV11.this.showNetworkFailureDialog();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    @SuppressLint({"NewApi"})
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        synchronized (INSTANCE_COUNT) {
            INSTANCE_COUNT = Integer.valueOf(INSTANCE_COUNT.intValue() + 1);
        }
        if (icicle != null) {
            this.tosCurrentlyShown = icicle.getBoolean(TOS_CURRENTLY_SHOWN, false);
            this.isPermissionRequested = icicle.getBoolean(SWYPE_SETTINGS_PERMISSION_REQUESTED, false);
            this.currentFragment = getSupportFragmentManager().getFragment(icicle, STATE_CURRENT_FRAGMENT);
            this.selectedSettingFragment = getSupportFragmentManager().getFragment(icicle, STATE_SELECTED_SETTING_FRAGMENT);
        }
        setRunning(true);
        setContentView(R.layout.settings_main);
        replaceDecorViewWithDrawer();
        this.mTitle = getTitle();
        this.mDrawerTitles = getResources().getStringArray(R.array.settings_drawer_text_array);
        this.mDrawerLayout = (HackyDrawerLayout) findViewById(R.id.drawer_layout);
        this.mDrawerList = (ListView) findViewById(R.id.list);
        this.dataList = new ArrayList();
        setDataList();
        ViewGroup header = (ViewGroup) getThemedLayoutInflater(getApplicationContext()).inflate(R.layout.settings_drawer_header, (ViewGroup) this.mDrawerList, false);
        this.mDrawerList.addHeaderView(header, null, false);
        this.adapter = new CustomDrawerAdapter(getApplicationContext(), R.layout.custom_drawer_item, this.dataList);
        this.mDrawerList.setAdapter((ListAdapter) this.adapter);
        this.mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.mDrawerToggle = new ActionBarDrawerToggle(this, this.mDrawerLayout, R.drawable.icon_settings_menu, R.string.drawer_open, R.string.drawer_close) { // from class: com.nuance.swype.input.settings.SettingsV11.1
            @Override // android.support.v4.app.ActionBarDrawerToggle, android.support.v4.widget.DrawerLayout.DrawerListener
            public void onDrawerClosed(View view) {
                if (SettingsV11.this.mPreviousScreen != null) {
                    UsageData.recordScreenVisited(SettingsV11.this.mPreviousScreen);
                }
                SettingsV11.this.getActionBar().setTitle(SettingsV11.this.mTitle);
                SettingsV11.log.d("ActionBarDrawerToggle...onDrawerClosed...getActionBar().setTitle: ", SettingsV11.this.mTitle);
                SettingsV11.this.invalidateOptionsMenu();
            }

            @Override // android.support.v4.app.ActionBarDrawerToggle, android.support.v4.widget.DrawerLayout.DrawerListener
            public void onDrawerOpened(View drawerView) {
                SettingsV11.this.mPreviousScreen = UsageData.getActiveScreen();
                UsageData.recordScreenVisited(UsageData.Screen.SETTINGS_DRAWER);
                SettingsV11.this.invalidateOptionsMenu();
            }
        };
        this.mDrawerToggle.syncState();
        this.mDrawerLayout.setDrawerListener(this.mDrawerToggle);
        this.actionbarManager = new ThemeActionBarManager(this);
        this.actionbarManager.restoreActionBar();
        this.mCurrentSelectedPosition = -1;
        if (!InputMethods.isLocaleChanged()) {
            String previousSavedLocale = AppPreferences.from(getApplicationContext()).getString(InputMethods.SAVED_LOCALE_KEY, null);
            if (!getApplicationContext().getResources().getConfiguration().locale.toString().equals(previousSavedLocale)) {
                InputMethods.setIsLocaleChanged(true);
            }
        }
        IMEApplication.from(getApplicationContext()).getConnect();
        createTabHostManager(icicle);
        setCatalogManagerActivity(this);
        Configuration config = getResources().getConfiguration();
        int swDp = ConfigurationCompat.getSmallestScreenWidthDp(config);
        if (swDp != 0) {
            isTablet_720DP = swDp >= 720;
        } else {
            isTablet_720DP = (config.screenLayout & 4) != 0;
        }
        DatabaseConfig.refreshDatabaseConfig(this, BuildInfo.from(this).getBuildDate());
        this.mNetworkStateReceiver = new NetworkStateReceiver();
    }

    private void setCatalogManagerActivity(SettingsV11 activity) {
        CatalogManager mCatalogManager = IMEApplication.from(getApplicationContext()).getCatalogManager();
        if (mCatalogManager == null) {
            return;
        }
        mCatalogManager.mActivity = new WeakReference<>(activity);
    }

    private void createTabHostManager(Bundle icicle) {
        Object obj;
        if (isTabHostSupported()) {
            this.tabHostManager = new ThemeTabHostManager(getApplicationContext(), this);
        }
        Bundle data = getIntent().getExtras();
        if (icicle == null) {
            if (data != null) {
                String fragmentName = data.getString(":android:show_fragment");
                log.d("createTabHostManager...fragmentName...", fragmentName);
                if (fragmentName != null) {
                    if (fragmentName.equals(ThemesFragment.class.getName())) {
                        selectFragmentItem(null, DrawerItems.Drawer_Themes.ordinal(), null);
                        this.shouldBackToStore = ThemeManager.isDownloadableThemesEnabled();
                        return;
                    }
                    if (fragmentName.equals(ThemesCategoryFragment.class.getName())) {
                        selectFragmentItem(null, DrawerItems.Drawer_Store.ordinal(), null);
                        this.shouldBackToStore = true;
                        return;
                    }
                    Class<?> cls = null;
                    try {
                        cls = Class.forName(fragmentName);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    Object obj2 = null;
                    if (cls != null) {
                        try {
                            obj2 = cls.newInstance();
                        } catch (IllegalAccessException | InstantiationException e2) {
                            e2.printStackTrace();
                            obj = null;
                        }
                    }
                    obj = obj2;
                    if (this.tabHostManager != null) {
                        this.tabHostManager.hideTabHost();
                    }
                    Fragment frag = (Fragment) obj;
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, frag).commit();
                    setCurrentFragment(frag);
                    CharSequence title = getFragmentTitle(frag);
                    setTitle(title);
                    this.mCurrentSelectedPosition = calculateSelectedPosition(title);
                    this.shouldBackToStore = false;
                    return;
                }
                return;
            }
            PlatformUtil util = IMEApplication.from(getApplicationContext()).getPlatformUtil();
            if (ThemeManager.isDownloadableThemesEnabled()) {
                Integer position = Integer.valueOf(util.checkForDataConnection() ? DrawerItems.Drawer_Store.ordinal() : DrawerItems.Drawer_Themes.ordinal());
                selectFragmentItem(null, position.intValue(), null);
            } else {
                selectFragmentItem(null, DrawerItems.Drawer_Settings.ordinal(), null);
            }
            if (!UserPreferences.from(getApplicationContext()).isDrawerAutomaticallyOpenedOnce()) {
                this.mSettingsHandler.sendEmptyMessageDelayed(10000, 50L);
            }
            this.shouldBackToStore = ThemeManager.isDownloadableThemesEnabled();
            return;
        }
        if (data != null) {
            this.shouldBackToStore = ThemeManager.isDownloadableThemesEnabled();
            String fragmentName2 = data.getString(":android:show_fragment");
            if (fragmentName2 != null) {
                if (fragmentName2.equals(ThemesFragment.class.getName())) {
                    this.shouldBackToStore = ThemeManager.isDownloadableThemesEnabled();
                } else {
                    this.shouldBackToStore = fragmentName2.equals(ThemesCategoryFragment.class.getName());
                }
            }
            selectFragmentItemByRecord(icicle);
            return;
        }
        if (this.tabHostManager == null) {
            this.shouldBackToStore = ThemeManager.isDownloadableThemesEnabled();
            selectFragmentItemByRecord(icicle);
            return;
        }
        int fragmentType = icicle.getInt(THEME_FRAGMENT_TYPE);
        if (fragmentType == -1) {
            this.shouldBackToStore = ThemeManager.isDownloadableThemesEnabled();
            selectFragmentItemByRecord(icicle);
            return;
        }
        if (fragmentType == ThemesFragment.FRAGMENT_SOURCE.MY_THEMES.ordinal() || fragmentType == ThemesFragment.FRAGMENT_SOURCE.BUY_THEMES.ordinal()) {
            selectFragmentItemByRecord(icicle);
        } else if (fragmentType == ThemesFragment.FRAGMENT_SOURCE.VIEW_ALL_THEMES.ordinal()) {
            if (!this.tabHostManager.isDownloadableThemesDataAvailable()) {
                this.mCurrentSelectedPosition = 0;
                selectFragmentItem(null, DrawerItems.Drawer_Store.ordinal(), null);
            } else {
                this.mCurrentSelectedPosition = icicle.getInt(STATE_SELECTED_POSITION);
                ThemeFragmentController.newInstance(getApplicationContext(), this).ShowViewAllTabFragments(icicle.getString(THEME_FRAGMENT_CATEGORY), this);
            }
        } else if (fragmentType == ThemesFragment.FRAGMENT_SOURCE.BUNDLE_THEMES.ordinal()) {
            if (!this.tabHostManager.isDownloadableThemesDataAvailable()) {
                this.mCurrentSelectedPosition = 0;
                selectFragmentItem(null, DrawerItems.Drawer_Store.ordinal(), null);
            } else {
                this.mCurrentSelectedPosition = icicle.getInt(STATE_SELECTED_POSITION);
                ThemeFragmentController.newInstance(getApplicationContext(), this).ShowBundleFragments(icicle.getString(THEME_FRAGMENT_CATEGORY), icicle.getString(THEME_FRAGMENT_BUNDLE_SKU), ThemesFragment.FRAGMENT_SOURCE.FRAGMENT_NONE.ordinal(), this);
            }
        }
        this.shouldBackToStore = ThemeManager.isDownloadableThemesEnabled();
    }

    private boolean isTabHostSupported() {
        if (ThemeManager.isDownloadableThemesEnabled() || getEmbeddedThemesCount() > 1) {
            return true;
        }
        return false;
    }

    private int getEmbeddedThemesCount() {
        return IMEApplication.from(getApplicationContext()).getThemeManager().getCategoryThemes(getApplicationContext(), ThemeManager.STR_NO_CATEGORY_ID).size();
    }

    public boolean showNetworkFailureDialog() {
        if (IMEApplication.from(getApplicationContext()).getPlatformUtil().checkForDataConnection()) {
            return false;
        }
        if (this.mAlertDialog != null && this.mAlertDialog.isShowing()) {
            this.mAlertDialog.dismiss();
            this.mAlertDialog = null;
        }
        this.mAlertDialog = new AlertDialog.Builder(this).setTitle(R.string.no_network_available).setMessage(R.string.no_network_try_again_msg).setNegativeButton(R.string.dismiss_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.SettingsV11.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                SettingsV11.this.showMyThemesTab();
            }
        }).create();
        this.mAlertDialog.show();
        return true;
    }

    public boolean showGoogleAccountMissingDialog() {
        if (!AccountUtil.isGoogleAccountMissing()) {
            return false;
        }
        if (this.mAlertDialog != null && this.mAlertDialog.isShowing()) {
            this.mAlertDialog.dismiss();
            this.mAlertDialog = null;
        }
        this.mAlertDialog = new AlertDialog.Builder(this).setTitle(R.string.theme_upgrade_google_play_service_title).setMessage(R.string.google_sign_in_msg).setPositiveButton(R.string.google_sign_in_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.SettingsV11.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent("android.settings.ADD_ACCOUNT_SETTINGS");
                intent.putExtra("account_types", new String[]{"com.google"});
                SettingsV11.this.startActivityForResult(intent, 100);
            }
        }).create();
        this.mAlertDialog.show();
        return true;
    }

    public boolean showGoogleAccountLoginFailedDialog() {
        if (AccountUtil.isGoogleAccountSignedIn()) {
            return false;
        }
        if (this.mAlertDialog != null && this.mAlertDialog.isShowing()) {
            this.mAlertDialog.dismiss();
            this.mAlertDialog = null;
        }
        this.mAlertDialog = new AlertDialog.Builder(this).setTitle(R.string.theme_upgrade_google_play_service_title).setMessage(R.string.google_sign_in_msg).setNegativeButton(R.string.dismiss_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.SettingsV11.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                SettingsV11.this.mAlertDialog.dismiss();
            }
        }).create();
        this.mAlertDialog.show();
        return true;
    }

    public boolean showNotAvailableDialogForGoogleTrial(Context ctx) {
        if (!BuildInfo.from(getApplicationContext()).isGoogleTrialBuild()) {
            return false;
        }
        if (this.mAlertDialog != null && this.mAlertDialog.isShowing()) {
            this.mAlertDialog.dismiss();
            this.mAlertDialog = null;
        }
        final String appStoreUrl = ctx.getString(R.string.url_android_market_dtc_details);
        this.mAlertDialog = new AlertDialog.Builder(this).setIcon(R.drawable.swype_logo).setTitle(R.string.swype_label).setCancelable(false).setMessage(R.string.theme_buy_swype_desc).setPositiveButton(R.string.license_buy_now, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.SettingsV11.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent marketIntent = new Intent("android.intent.action.VIEW", Uri.parse(appStoreUrl));
                marketIntent.addFlags(268435456);
                ThemeManager.recordThemePreviewData("yes", SettingsV11.this.getApplicationContext());
                SettingsV11.this.startActivity(marketIntent);
            }
        }).setNegativeButton(R.string.label_back, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.SettingsV11.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                SettingsV11.this.showMyThemesTab();
                ThemeManager.recordThemePreviewData("no", SettingsV11.this.getApplicationContext());
            }
        }).create();
        this.mAlertDialog.show();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showMyThemesTab() {
        this.mCurrentSelectedPosition = DrawerItems.Drawer_Themes.ordinal();
        selectFragmentItem(null, this.mCurrentSelectedPosition, null);
        if (this.tabHostManager != null && this.tabHostManager.isTabHostShown() && this.actionbarManager != null && !this.actionbarManager.isActionBarShowing()) {
            this.actionbarManager.showActionBar();
        }
    }

    private void showGetThemesTab() {
        this.mCurrentSelectedPosition = DrawerItems.Drawer_Store.ordinal();
        selectFragmentItem(null, this.mCurrentSelectedPosition, null);
    }

    private void selectFragmentItemByRecord(Bundle icicle) {
        this.mCurrentSelectedPosition = icicle.getInt(STATE_SELECTED_POSITION);
        selectFragmentItem(null, this.mCurrentSelectedPosition, icicle);
        if (!UserPreferences.from(getApplicationContext()).isDrawerAutomaticallyOpenedOnce()) {
            this.mSettingsHandler.sendEmptyMessageDelayed(10000, 50L);
        }
    }

    @SuppressLint({"InflateParams"})
    private void replaceDecorViewWithDrawer() {
        DrawerLayout drawer = (DrawerLayout) ((LayoutInflater) getSystemService("layout_inflater")).inflate(R.layout.settings_decor, (ViewGroup) null);
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        ((LinearLayout) drawer.findViewById(R.id.drawer_content)).addView(child, 0);
        decor.addView(drawer);
    }

    private int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId <= 0) {
            return 0;
        }
        int result = getResources().getDimensionPixelSize(resourceId);
        return result;
    }

    public ThemeTabHostManager getTabHostManager() {
        return this.tabHostManager;
    }

    public ThemeActionBarManager getActionbarManager() {
        return this.actionbarManager;
    }

    @Override // android.app.Activity
    public void setTitle(CharSequence title) {
        log.d("setTitle...", title);
        if (getActionBar() != null) {
            getActionBar().setTitle(title);
        }
        if (!title.equals(this.mTitle)) {
            this.mTitle = title;
            if (getString(R.string.view_all_content).equals(title)) {
                this.mDrawerToggle.setDrawerIndicatorEnabled(false);
                log.d("setTitle...disable drawer indicator...");
            } else {
                this.mDrawerToggle.setDrawerIndicatorEnabled(true);
                log.d("setTitle...enable drawer indicator...");
            }
            this.mDrawerToggle.syncState();
        }
    }

    public void setActivityResulted(boolean result) {
        this.isActivityResultReturned = result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity
    public void onResumeFragments() {
        log.d("onResumeFragments...currentFragment: ", this.currentFragment);
        super.onResumeFragments();
        if (!this.isActivityResultReturned && this.currentFragment != null && (this.currentFragment instanceof ThemeFragmentController.BundleThemeFragment)) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, this.currentFragment);
            ft.commit();
            this.isActivityResultReturned = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        UsageData.startSession(getApplicationContext());
        IMEApplication.from(getApplicationContext()).clearKeyboardCache();
        final CatalogManager catalogManager = IMEApplication.from(getApplicationContext()).getCatalogManager();
        if (ThemeManager.isDownloadableThemesEnabled()) {
            if (catalogManager.mInstalledList == null) {
                catalogManager.mInstalledList = new InstalledList(catalogManager.mContext);
            }
            catalogManager.readLastCheckedSkuList();
            if (catalogManager.connection == null) {
                catalogManager.connection = new ConnectedStatus(catalogManager.mContext) { // from class: com.nuance.swype.inapp.CatalogManager.3
                    public AnonymousClass3(Context ctx) {
                        super(ctx);
                    }

                    @Override // com.nuance.swype.connect.ConnectedStatus
                    public final void onConnectionChanged(boolean isConnected) {
                        if (isConnected) {
                            CatalogManager.log.d("onConnectionChanged...isConnected: ", Boolean.valueOf(isConnected));
                            ThemeManager themeManager = ThemeManager.from(CatalogManager.this.mContext);
                            themeManager.getThemeDataManager();
                            HashMap<String, List<ThemeItemSeed>> cache = ThemeData.getCache();
                            if (cache.isEmpty() || !CatalogManager.this.isPurchaseInfoFetched) {
                                CatalogManager.this.setUpService();
                                themeManager.refetchPurchaseInfoFromGoogleStore(CatalogManager.this.mContext);
                                return;
                            }
                            List<String> reInstallingSkus = new ArrayList<>();
                            for (String category : cache.keySet()) {
                                List<ThemeItemSeed> seeds = cache.get(category);
                                CatalogManager.log.d("onConnectionChanged...category: ", category, "...seeds: ", seeds);
                                for (ThemeItemSeed seed : seeds) {
                                    if (seed.isInstalling && !reInstallingSkus.contains(seed.sku)) {
                                        ThemeManager.SwypeTheme theme = themeManager.getSwypeTheme(seed.categoryKey, seed.sku);
                                        if (theme instanceof ThemeManager.ConnectDownloadableThemeWrapper) {
                                            try {
                                                reInstallingSkus.add(seed.sku);
                                                CatalogManager.this.downloadTheme(0, (ThemeManager.ConnectDownloadableThemeWrapper) theme);
                                            } catch (ACException e) {
                                                CatalogManager.log.e("onConnectionChanged...e: " + e.toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override // com.nuance.swype.connect.ConnectedStatus
                    public final void onInitialized() {
                        super.onInitialized();
                        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.nuance.swype.inapp.CatalogManager.3.1
                            AnonymousClass1() {
                            }

                            @Override // java.lang.Runnable
                            public final void run() {
                                CatalogManager.access$702$5c4c8ca3(CatalogManager.this);
                                CatalogManager.log.d("connect initialized.");
                                ThemeManager themeManager = ThemeManager.from(CatalogManager.this.mContext);
                                themeManager.getThemeDataManager();
                                ThemeData.getCache().clear();
                                if (IMEApplication.from(CatalogManager.this.mContext).getPlatformUtil().checkForDataConnection()) {
                                    CatalogManager.this.setUpService();
                                    themeManager.refetchPurchaseInfoFromGoogleStore(CatalogManager.this.mContext);
                                }
                            }
                        });
                    }

                    /* renamed from: com.nuance.swype.inapp.CatalogManager$3$1 */
                    /* loaded from: classes.dex */
                    final class AnonymousClass1 implements Runnable {
                        AnonymousClass1() {
                        }

                        @Override // java.lang.Runnable
                        public final void run() {
                            CatalogManager.access$702$5c4c8ca3(CatalogManager.this);
                            CatalogManager.log.d("connect initialized.");
                            ThemeManager themeManager = ThemeManager.from(CatalogManager.this.mContext);
                            themeManager.getThemeDataManager();
                            ThemeData.getCache().clear();
                            if (IMEApplication.from(CatalogManager.this.mContext).getPlatformUtil().checkForDataConnection()) {
                                CatalogManager.this.setUpService();
                                themeManager.refetchPurchaseInfoFromGoogleStore(CatalogManager.this.mContext);
                            }
                        }
                    }
                };
                catalogManager.connection.register();
            }
        }
        if (ThemeManager.isDownloadableThemesEnabled()) {
            IMEApplication.from(getApplicationContext()).getCatalogManager().refetchPurchaseInfoFromGoolgeStore();
        }
        updateDrawerList();
        this.isPaused = false;
        setCatalogManagerActivity(this);
        if (this.updateCallback == null && !SDKUpdateManager.from(this).isAvailable()) {
            this.updateCallback = new ACPlatformUpdateService.ACPlatformUpdateCallback() { // from class: com.nuance.swype.input.settings.SettingsV11.7
                @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateCallback
                public void updateAvailable() {
                }
            };
            SDKUpdateManager.from(this).registerCallback(this.updateCallback);
        }
        try {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(this.mNetworkStateReceiver, filter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        this.isPaused = true;
        setCatalogManagerActivity(null);
        this.mSettingsHandler.removeMessages(10000);
        if (this.updateCallback != null) {
            SDKUpdateManager.from(this).unregisterCallback(this.updateCallback);
            this.updateCallback = null;
        }
        super.onPause();
        try {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            log.e(Log.getStackTraceString(e));
        }
        unregisterReceiver(this.mNetworkStateReceiver);
        UsageData.endSession(getApplicationContext());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        if (!isChangingConfigurations()) {
            log.d("onStop: recording exit, isChangingConfigurations=" + isChangingConfigurations());
            UsageData.exitedSettings();
            IMEApplication.from(this).updateCustomDimensions();
        }
    }

    @Override // android.app.Activity
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.mDrawerToggle.syncState();
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        log.d("onOptionsItemSelected...item: ", item);
        return (this.tabHostManager != null && this.tabHostManager.isTabHostShown() && this.tabHostManager.onBackPressed()) || this.mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void setOnKeyUpListener(KeyUpListener listener) {
        this.onKeyUpListener = listener;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return (this.onKeyUpListener != null && this.onKeyUpListener.onKeyUp(keyCode, event)) || super.onKeyUp(keyCode, event);
    }

    @Override // android.app.Activity
    protected void onUserLeaveHint() {
        log.d("onUserLeaveHint");
        if (this.tabHostManager != null) {
            this.tabHostManager.onUserLeaveHint();
        }
        super.onUserLeaveHint();
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        this.isPaused = true;
        this.isEnterMainStore = false;
        log.d("onBackPressed...");
        if (this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
            log.d("onBackPressed...close drawer.");
            this.mDrawerLayout.closeDrawer(this.mDrawerList);
            return;
        }
        if (this.currentFragment != null) {
            if (this.currentFragment == this.selectedSettingFragment && this.shouldBackToStore && this.tabHostManager != null) {
                selectFragmentItem(null, this.tabHostManager.getCurrentTab() == 1 ? DrawerItems.Drawer_Store.ordinal() : DrawerItems.Drawer_Themes.ordinal(), null);
                return;
            } else if (!this.shouldBackToStore) {
                super.onBackPressed();
                return;
            }
        } else if (!this.shouldBackToStore) {
            super.onBackPressed();
            return;
        }
        if (this.tabHostManager != null && this.tabHostManager.onBackPressed()) {
            this.mCurrentSelectedPosition = 0;
            invalidateOptionsMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.tabHostManager != null && this.tabHostManager.isTabHostShown() && this.tabHostManager.isMainTabActive()) {
            this.mCurrentSelectedPosition = calculateSelectedPosition(this.mTitle);
        }
        outState.putInt(STATE_SELECTED_POSITION, this.mCurrentSelectedPosition);
        outState.putBoolean(TOS_CURRENTLY_SHOWN, this.tosCurrentlyShown);
        outState.putBoolean(SWYPE_SETTINGS_PERMISSION_REQUESTED, this.isPermissionRequested);
        if (this.currentFragment != null) {
            getSupportFragmentManager().putFragment(outState, STATE_CURRENT_FRAGMENT, this.currentFragment);
        }
        if (this.selectedSettingFragment != null) {
            getSupportFragmentManager().putFragment(outState, STATE_SELECTED_SETTING_FRAGMENT, this.selectedSettingFragment);
        }
        if (this.tabHostManager != null) {
            log.d("current fragment type:", Integer.valueOf(this.tabHostManager.getCurrentFragmentInfo().getType()), " category:", this.tabHostManager.getCurrentFragmentInfo().getCategory());
            outState.putInt(THEME_FRAGMENT_TYPE, this.tabHostManager.getCurrentFragmentInfo().getType());
            outState.putString(THEME_FRAGMENT_CATEGORY, this.tabHostManager.getCurrentFragmentInfo().getCategory());
            outState.putString(THEME_FRAGMENT_BUNDLE_SKU, this.tabHostManager.getCurrentFragmentInfo().getBundleSku());
            outState.putInt(THEME_TAB_POSITION, this.tabHostManager.getCurrentTab());
        }
    }

    public boolean isCurrentThemeStoreShowing() {
        return !this.isPaused && this.isEnterMainStore;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isStoreShowing() {
        return (this.tabHostManager != null && this.tabHostManager.isStoreActive()) || (this.currentFragment != null && (this.currentFragment instanceof ThemeFragmentController.BundleThemeFragment));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        synchronized (INSTANCE_COUNT) {
            INSTANCE_COUNT = Integer.valueOf(INSTANCE_COUNT.intValue() - 1);
        }
        setRunning(false);
        IMEApplication.from(getApplicationContext()).getCatalogManager().setCatalogCallBackListener(null, UsageData.DownloadLocation.GET_THEMES);
        if (this.tabHostManager != null) {
            this.tabHostManager.destroy();
        }
        this.tabHostManager = null;
        this.mDrawerLayout.setDrawerListener(null);
        this.mDrawerToggle = null;
        this.mDrawerLayout = null;
        this.actionbarManager.release();
        ThemeFragmentController.destroyInstance();
        Glide.get(getApplicationContext()).clearMemory();
        log.d("onDestroy...");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DrawerItemClickListener implements AdapterView.OnItemClickListener {
        private DrawerItemClickListener() {
        }

        /* JADX WARN: Type inference failed for: r1v2, types: [android.widget.Adapter] */
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SettingsV11.log.d("onItemClick, position: ", Integer.valueOf(position));
            SettingsV11.this.mPreviousScreen = null;
            DrawerItem item = (DrawerItem) parent.getAdapter().getItem(position);
            SettingsV11.this.selectFragmentItem(item, SettingsV11.this.drawerIndexToArrayIndex(position - 1), null);
            SettingsV11.this.shouldBackToStore = ThemeManager.isDownloadableThemesEnabled();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectFragmentItem(DrawerItem item, int position, Bundle icicle) {
        String text;
        log.d("selectFragmentItem...", item, "...position: ", Integer.valueOf(position));
        if (item == null && position >= this.mDrawerTitles.length) {
            log.e("selectFragmentItem... selected fragment index is out of range.");
            return;
        }
        Fragment fragment = null;
        if (item == null) {
            text = this.mDrawerTitles[position];
        } else {
            text = item.ItemName;
        }
        log.d("selectFragmentItem...text: ", text);
        if (this.tabHostManager != null) {
            this.tabHostManager.getCurrentFragmentInfo().initialize(-1, null, null);
        }
        if (getString(R.string.store).equals(text)) {
            if (this.tabHostManager != null) {
                this.tabHostManager.themesStartPointTime = System.currentTimeMillis();
                this.tabHostManager.showTabHost();
                this.tabHostManager.showMainTabsList(1, ThemeTabHostManager.REFRESH_SOURCE.INIT);
                this.isEnterMainStore = true;
            }
        } else if (getString(R.string.pref_connect_settings).equals(text)) {
            if (this.tabHostManager != null) {
                this.tabHostManager.hideTabHost();
            }
            if (icicle == null) {
                fragment = new SettingsPrefsFragment();
            }
        } else if (getString(R.string.pref_menu_themes).equals(text)) {
            if (this.tabHostManager != null) {
                this.tabHostManager.themesStartPointTime = System.currentTimeMillis();
                this.tabHostManager.showTabHost();
                this.tabHostManager.showMainTabsList(0, ThemeTabHostManager.REFRESH_SOURCE.INIT);
                this.isEnterMainStore = true;
            }
        } else if (getString(R.string.settings_swype_chinese).equals(text)) {
            if (this.tabHostManager != null) {
                this.tabHostManager.hideTabHost();
            }
            if (icicle == null) {
                fragment = new ChinesePrefsFragment();
            }
        } else if (getString(R.string.pref_my_words_title).equals(text)) {
            if (this.tabHostManager != null) {
                this.tabHostManager.hideTabHost();
            }
            if (icicle == null) {
                fragment = new MyWordsPrefsFragment();
            }
        } else if (getString(R.string.language_category_title).equals(text)) {
            if (this.tabHostManager != null) {
                this.tabHostManager.hideTabHost();
            }
            if (icicle == null) {
                fragment = new LanguageOptionsFragment();
            }
        } else if (getString(R.string.pref_menu_gestures).equals(text)) {
            if (this.tabHostManager != null) {
                this.tabHostManager.hideTabHost();
            }
            if (icicle == null) {
                fragment = new GesturesFragment();
            }
        } else if (getString(R.string.pref_help_title).equals(text)) {
            if (this.tabHostManager != null) {
                this.tabHostManager.hideTabHost();
            }
            if (icicle == null) {
                fragment = new HelpFragment();
            }
        } else if (getString(R.string.updates_title).equals(text)) {
            if (this.tabHostManager != null) {
                this.tabHostManager.hideTabHost();
            }
            if (icicle == null) {
                fragment = new UpdatesFragment();
            }
        } else {
            log.e("selectFragmentItem: item text not matched: " + text);
        }
        if (!getString(R.string.pref_menu_themes).equals(text) && !getString(R.string.store).equals(text)) {
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                setCurrentFragment(fragment);
                this.selectedSettingFragment = this.currentFragment;
            }
        } else {
            if (this.tabHostManager == null && fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
            setCurrentFragment(null);
        }
        if (item != null) {
            this.mDrawerList.setItemChecked(position, true);
            this.mCurrentSelectedPosition = calculateSelectedPosition(item.ItemName);
        } else {
            this.mCurrentSelectedPosition = position;
        }
        setTitle(text);
        this.mDrawerLayout.closeDrawer(this.mDrawerList);
        invalidateOptionsMenu();
    }

    private int calculateSelectedPosition(CharSequence fragmentName) {
        if (fragmentName == null) {
            return -1;
        }
        for (int i = 0; i < this.mDrawerTitles.length; i++) {
            if (fragmentName.equals(this.mDrawerTitles[i])) {
                int selectedPosition = i;
                return selectedPosition;
            }
        }
        return -1;
    }

    private CharSequence getFragmentTitle(Fragment fragment) {
        CharSequence title = this.mTitle;
        if (fragment instanceof SettingsPrefsFragment) {
            CharSequence title2 = getString(R.string.pref_connect_settings);
            return title2;
        }
        if (fragment instanceof ChinesePrefsFragment) {
            CharSequence title3 = getString(R.string.settings_swype_chinese);
            return title3;
        }
        if (fragment instanceof MyWordsPrefsFragment) {
            CharSequence title4 = getString(R.string.pref_my_words_title);
            return title4;
        }
        if (fragment instanceof LanguageOptionsFragment) {
            CharSequence title5 = getString(R.string.language_category_title);
            return title5;
        }
        if (fragment instanceof GesturesFragment) {
            CharSequence title6 = getString(R.string.pref_menu_gestures);
            return title6;
        }
        if (fragment instanceof HelpFragment) {
            CharSequence title7 = getString(R.string.pref_help_title);
            return title7;
        }
        if (fragment instanceof UpdatesFragment) {
            CharSequence title8 = getString(R.string.updates_title);
            return title8;
        }
        return title;
    }

    public void setCurrentFragment(Fragment fg) {
        if (fg == null) {
            this.selectedSettingFragment = null;
            if (this.currentFragment != null) {
                log.d("setCurrentFragment: removing fragment " + this.currentFragment);
                getSupportFragmentManager().beginTransaction().remove(this.currentFragment).commitAllowingStateLoss();
            }
        }
        this.currentFragment = fg;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CatalogManager mCatalogManager;
        switch (requestCode) {
            case 100:
                break;
            case 200:
                if (resultCode == -1) {
                    if (ConnectLegal.from(this).isTosAccepted() && (mCatalogManager = IMEApplication.from(this).getCatalogManager()) != null) {
                        mCatalogManager.setUpService();
                        this.mCurrentSelectedPosition = DrawerItems.Drawer_Store.ordinal();
                        selectFragmentItem(null, this.mCurrentSelectedPosition, null);
                    }
                } else {
                    this.tabHostManager.highlightTitle(ThemeTabHostManager.MAIN_TABS_ID.MY_THEMES.ordinal());
                }
                this.tosCurrentlyShown = false;
                return;
            case PopupDialogThemeActivity.REQUEST_PURCHASE /* 10001 */:
                if (resultCode == -1) {
                    if (this.currentFragment != null && (this.currentFragment instanceof ThemesFragment)) {
                        this.currentFragment.onActivityResult(requestCode, resultCode, data);
                        return;
                    }
                    return;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
        log.d("REQUEST_ADDING_GOOGLE_ACCOUNT... resultCode:", Integer.valueOf(resultCode));
        IMEApplication.from(this).resetThemeManagedData();
        if (ThemeManager.isDownloadableThemesEnabled()) {
            IMEApplication.from(this).getCatalogManager().refetchPurchaseInfoFromGoolgeStore();
        }
        if (this.currentFragment != null && (this.currentFragment instanceof ThemeFragmentController.BundleThemeFragment)) {
            getActionbarManager().showActionBar();
            showGetThemesTab();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getCurrentFragmentSource() {
        if (this.tabHostManager != null) {
            return this.tabHostManager.getCurrentFragmentSource();
        }
        log.d("getCurrentFragmentSource: tabHostManager null, returning empty string");
        return "";
    }

    private LayoutInflater getThemedLayoutInflater(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return IMEApplication.from(context).getThemedLayoutInflater(inflater);
    }

    public void updateDrawerList() {
        this.dataList = new ArrayList();
        setDataList();
        this.adapter = new CustomDrawerAdapter(getApplicationContext(), R.layout.custom_drawer_item, this.dataList);
        this.mDrawerList.setAdapter((ListAdapter) this.adapter);
        this.mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    public void switchToDefaultScreen() {
        if (drawerItemsDefaultPosition >= 0 && drawerItemsDefaultPosition < this.adapter.getCount()) {
            selectFragmentItem(this.adapter.getItem(drawerItemsDefaultPosition), drawerItemsDefaultPosition, null);
        }
    }

    private void setDataList() {
        boolean isChinese = InputMethods.from(this).getCurrentInputLanguage().isChineseLanguage();
        int defaultIndex = -1;
        this.dataList.clear();
        for (String mDrawerTitle : this.mDrawerTitles) {
            if (getString(R.string.store).equals(mDrawerTitle)) {
                if (ThemeManager.isDownloadableThemesEnabled()) {
                    this.dataList.add(new DrawerItem(mDrawerTitle, R.drawable.icon_settings_store));
                    defaultIndex = DrawerItems.Drawer_Store.ordinal();
                }
            } else if (getString(R.string.pref_connect_settings).equals(mDrawerTitle)) {
                this.dataList.add(new DrawerItem(mDrawerTitle, R.drawable.icon_settings_settings));
                defaultIndex = DrawerItems.Drawer_Settings.ordinal();
            } else if (getString(R.string.pref_menu_themes).equals(mDrawerTitle)) {
                if (isTabHostSupported()) {
                    this.dataList.add(new DrawerItem(mDrawerTitle, R.drawable.icon_settings_themes));
                    defaultIndex = DrawerItems.Drawer_Themes.ordinal();
                }
            } else if (getString(R.string.settings_swype_chinese).equals(mDrawerTitle)) {
                if (isChinese) {
                    this.dataList.add(new DrawerItem(mDrawerTitle, R.drawable.icon_settings_chinese));
                    defaultIndex = DrawerItems.Drawer_Chinese_Settings.ordinal();
                }
            } else if (getString(R.string.pref_my_words_title).equals(mDrawerTitle)) {
                this.dataList.add(new DrawerItem(mDrawerTitle, R.drawable.icon_settings_mywords));
                defaultIndex = DrawerItems.Drawer_My_Words.ordinal();
            } else if (getString(R.string.language_category_title).equals(mDrawerTitle)) {
                this.dataList.add(new DrawerItem(mDrawerTitle, R.drawable.icon_settings_languages));
                defaultIndex = DrawerItems.Drawer_Language_Category.ordinal();
            } else if (getString(R.string.drawer_divider_text).equals(mDrawerTitle)) {
                this.dataList.add(new DrawerItem(mDrawerTitle, R.drawable.icon_settings_languages));
                defaultIndex = DrawerItems.Drawer_Divider.ordinal();
            } else if (getString(R.string.pref_menu_gestures).equals(mDrawerTitle)) {
                this.dataList.add(new DrawerItem(mDrawerTitle, R.drawable.icon_settings_gestures));
                defaultIndex = DrawerItems.Drawer_Gestures.ordinal();
            } else if (getString(R.string.pref_help_title).equals(mDrawerTitle)) {
                this.dataList.add(new DrawerItem(mDrawerTitle, R.drawable.icon_settings_about));
                defaultIndex = DrawerItems.Drawer_Help.ordinal();
            } else if (getString(R.string.updates_title).equals(mDrawerTitle)) {
                BuildInfo buildInfo = BuildInfo.from(this);
                if (Connect.from(this).isStarted() && ((buildInfo == null || !buildInfo.isDTCbuild()) && SDKUpdateManager.from(this).isAvailable())) {
                    this.dataList.add(new DrawerItem(mDrawerTitle, R.drawable.icon_settings_whatsnew));
                    defaultIndex = DrawerItems.Drawer_Updates.ordinal();
                }
            }
            if (this.dataList.size() == 1) {
                drawerItemsDefaultPosition = defaultIndex;
            }
        }
    }

    public void setTosCurrentlyShown(boolean shown) {
        this.tosCurrentlyShown = shown;
    }

    public boolean isTosCurrentlyShown() {
        return this.tosCurrentlyShown;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int drawerIndexToArrayIndex(int drawerIndex) {
        int index = drawerIndex;
        if (drawerIndex < 0) {
            log.e("drawerIndexToArrayIndex...error drawer index: " + drawerIndex);
            index = 0;
        }
        boolean isChinese = InputMethods.from(this).getCurrentInputLanguage().isChineseLanguage();
        if (ThemeManager.isDownloadableThemesEnabled()) {
            if (index >= DrawerItems.Drawer_Chinese_Settings.ordinal() && !isChinese) {
                index++;
            }
        } else {
            index++;
            if (index >= DrawerItems.Drawer_Chinese_Settings.ordinal() && !isChinese) {
                index++;
            }
        }
        if (index >= this.mDrawerTitles.length) {
            log.e("drawerIndexToArrayIndex...drawerIndex: " + drawerIndex + "...error array index: " + index);
            int index2 = DrawerItems.Drawer_Store.ordinal();
            return index2;
        }
        return index;
    }

    public static boolean isTablet720DP() {
        return isTablet_720DP;
    }
}
