package com.nuance.swype.input.store;

import android.annotation.TargetApi;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.inapp.CatalogManager;
import com.nuance.swype.inapp.CategoryItem;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.input.settings.SettingsV11;
import com.nuance.swype.input.settings.ThemesCategoryFragment;
import com.nuance.swype.input.settings.ThemesFragment;
import com.nuance.swype.input.store.ThemeFragmentController;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.widget.directional.DirectionalUtil;
import com.nuance.swypeconnect.ac.ACCatalogService;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@TargetApi(11)
/* loaded from: classes.dex */
public class ThemeTabHostManager implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {
    public static final String MAIN_TABS_TAG = "maintabs";
    private static final int MSG_DELAY_SCROLL_VIEW_TABS = 100000;
    public static final String VIEWALL_TABS_TAG = "viewalltabs";
    private static String currentCategoryTitle;
    private FragmentManager fm;
    private FrameLayout fragmentArea;
    private HorizontalScrollView hScrollView;
    private boolean isDestroyed;
    private final Context mContext;
    private WeakReference<FragmentActivity> mStoreWeak;
    private TabHost mTabHost;
    private ThemeViewPager mViewPager;
    private ThemePageAdapter mainTabPageAdapter;
    private ThemePageAdapter viewAllTabPageAdapter;
    protected static final LogManager.Log log = LogManager.getLog("ThemeTabHostManager");
    private static boolean doNotLogVisitCount = false;
    private static CurrentFragmentInfo currentFragmentInfo = new CurrentFragmentInfo();
    private static final Object lock = new Object();
    public long themesStartPointTime = System.currentTimeMillis();
    private ArrayList<TabHost.TabSpec> mainTabsList = new ArrayList<>();
    private ArrayList<TabHost.TabSpec> viewAllTabsList = new ArrayList<>();
    Handler.Callback handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.store.ThemeTabHostManager.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 100000:
                    int i = msg.arg1;
                    int pos = msg.arg2;
                    int screenWidth = IMEApplication.from(ThemeTabHostManager.this.mContext).getDisplayWidth();
                    int width = ThemeTabHostManager.this.mTabHost.getCurrentTabView().getWidth();
                    int availableTabCount = 3;
                    if (width > 0) {
                        availableTabCount = screenWidth / width;
                    } else {
                        width = screenWidth / 3;
                    }
                    ThemeTabHostManager.log.d("MSG_DELAY_SCROLL_VIEW_TABS...tab width:  ", Integer.valueOf(width), "...availableTabCount: ", Integer.valueOf(availableTabCount));
                    if (DirectionalUtil.isCurrentlyRtl()) {
                        ThemeTabHostManager.this.hScrollView.scrollTo(((ThemeTabHostManager.this.mTabHost.getTabWidget().getChildCount() - 1) - pos) * width, 0);
                        return true;
                    }
                    ThemeTabHostManager.this.hScrollView.scrollTo(pos * width, 0);
                    return true;
                default:
                    return false;
            }
        }
    };
    private final Handler mHandler = WeakReferenceHandler.create(this.handlerCallback);

    /* loaded from: classes.dex */
    public enum MAIN_TABS_ID {
        MY_THEMES,
        GET_THEMES
    }

    /* loaded from: classes.dex */
    public enum REFRESH_SOURCE {
        INIT,
        ON_BACK,
        CATALOG_CALLBACK
    }

    public ThemeTabHostManager(Context context, FragmentActivity activity) {
        this.mContext = context;
        this.mStoreWeak = new WeakReference<>(activity);
        this.fm = activity.getSupportFragmentManager();
        this.mainTabPageAdapter = new ThemePageAdapter(this.fm);
        this.viewAllTabPageAdapter = new ThemePageAdapter(this.fm);
        initialiseTabHost();
    }

    public void destroy() {
        this.isDestroyed = true;
    }

    private void setCurrentTab(int index, REFRESH_SOURCE source) {
        this.mTabHost.setCurrentTab(index);
        if (index == 0) {
            onPageSelected(index);
        }
    }

    public void highlightTitle(int currentPos) {
        log.d("highlightTitle...currentPos: ", Integer.valueOf(currentPos));
        this.mViewPager.setCurrentItem(0);
        this.mTabHost.setCurrentTab(currentPos);
        String title = refreshTabTitleColor(currentPos);
        updateStoreUiProperties();
        recordCategoryBrowseTime(title, System.currentTimeMillis());
    }

    @Override // android.widget.TabHost.OnTabChangeListener
    public void onTabChanged(String tag) {
        if (!this.isDestroyed) {
            if (this.mViewPager == null) {
                initialiseTabHost();
            }
            int pos = this.mTabHost.getCurrentTab();
            if (checkTOS(pos) && !ConnectLegal.from(this.mContext).isTosAccepted()) {
                this.mTabHost.setCurrentTab(MAIN_TABS_ID.MY_THEMES.ordinal());
                pos = MAIN_TABS_ID.MY_THEMES.ordinal();
            }
            if (this.mViewPager.getAdapter() == null || pos < this.mViewPager.getAdapter().getCount()) {
                if (this.mViewPager.getAdapter().equals(this.mainTabPageAdapter) && pos == 1) {
                    SettingsV11 store = (SettingsV11) getStore();
                    if (store.showNotAvailableDialogForGoogleTrial(this.mContext) || store.showNetworkFailureDialog()) {
                        return;
                    }
                }
                int preTabPosition = this.mViewPager.getCurrentItem();
                if (preTabPosition != pos) {
                    this.mViewPager.setCurrentItem(pos, true);
                }
                String title = refreshTabTitleColor(pos);
                log.d("onTabChanged...refreshTabTitle: ", title);
                updateStoreUiProperties();
                recordCategoryBrowseTime(title, System.currentTimeMillis());
                setCatlogCallBackListener(pos);
                if (preTabPosition != pos) {
                    Message msg = new Message();
                    msg.what = 100000;
                    msg.arg1 = preTabPosition;
                    msg.arg2 = pos;
                    if (this.mHandler.hasMessages(100000)) {
                        this.mHandler.removeMessages(100000);
                    }
                    this.mHandler.sendMessageDelayed(msg, 50L);
                }
            }
        }
    }

    private FragmentActivity getStore() {
        if (this.mStoreWeak != null) {
            return this.mStoreWeak.get();
        }
        return null;
    }

    private void recordCategoryBrowseTime(String currentCategory, long time) {
        String categoryKey;
        if (this.mViewPager.getAdapter() != null && !this.mViewPager.getAdapter().equals(this.viewAllTabPageAdapter)) {
            currentCategory = null;
        }
        if (currentCategoryTitle != null) {
            String categoryName = currentCategoryTitle;
            ACCatalogService catalogService = IMEApplication.from(this.mContext).getConnect().getCatalogService();
            if (catalogService != null && (categoryKey = catalogService.getCategoryKeyForCategoryName(currentCategoryTitle)) != null && !categoryKey.isEmpty()) {
                categoryName = categoryKey;
            }
            UsageData.recordCategoryBrowse(categoryName, time - this.themesStartPointTime);
        }
        currentCategoryTitle = currentCategory;
        this.themesStartPointTime = System.currentTimeMillis();
    }

    public void setCatlogCallBackListener(int pos) {
        ThemePageAdapter pageAdapter;
        ComponentCallbacks registeredFragment;
        CatalogManager mCatalogManager;
        if (this.mViewPager != null && (pageAdapter = (ThemePageAdapter) this.mViewPager.getAdapter()) != null && pageAdapter.getCount() > pos) {
            if (pageAdapter.equals(this.viewAllTabPageAdapter)) {
                registeredFragment = pageAdapter.getRegisteredFragmentByCategory(pos);
            } else {
                registeredFragment = pageAdapter.getRegisteredFragment(pos);
            }
            if (registeredFragment != null && (mCatalogManager = IMEApplication.from(this.mContext).getCatalogManager()) != null) {
                mCatalogManager.setUpService();
                if (registeredFragment instanceof ThemeFragmentController.BundleThemeFragment) {
                    mCatalogManager.setCatalogCallBackListener((CatalogManager.OnCatalogCallBackListener) registeredFragment, UsageData.DownloadLocation.BUNDLE);
                    return;
                }
                if (registeredFragment instanceof ThemesFragment) {
                    if (registeredFragment instanceof ThemeFragmentController.MyThemesFragment) {
                        log.d("my themes");
                        ((ThemesFragment) registeredFragment).setCurrentTheme();
                        mCatalogManager.setCatalogCallBackListener((CatalogManager.OnCatalogCallBackListener) registeredFragment, UsageData.DownloadLocation.MY_THEMES);
                        return;
                    }
                    mCatalogManager.setCatalogCallBackListener((CatalogManager.OnCatalogCallBackListener) registeredFragment, UsageData.DownloadLocation.GET_THEMES);
                    return;
                }
                if (registeredFragment instanceof ThemesCategoryFragment) {
                    mCatalogManager.setCatalogCallBackListener((CatalogManager.OnCatalogCallBackListener) registeredFragment, UsageData.DownloadLocation.GET_THEMES);
                }
            }
        }
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        int width;
        Fragment fm;
        log.d("onPageScrolled...arg0: ", Integer.valueOf(arg0), "...arg1: ", Float.valueOf(arg1), "...arg2: ", Integer.valueOf(arg2));
        int pos = this.mViewPager.getCurrentItem();
        int preTabPosition = this.mTabHost.getCurrentTab();
        log.d("onPageScrolled...pos: ", Integer.valueOf(pos), "...preTabPosition: ", Integer.valueOf(preTabPosition));
        if ((pos != preTabPosition) && checkTOS(pos) && !ConnectLegal.from(this.mContext).isTosAccepted()) {
            pos = MAIN_TABS_ID.MY_THEMES.ordinal();
            if (this.mTabHost.getCurrentTab() != pos) {
                this.mTabHost.setCurrentTab(pos);
            }
            if (this.mViewPager.getCurrentItem() != preTabPosition) {
                this.mViewPager.setCurrentItem(preTabPosition);
            }
        } else if (this.mTabHost.getCurrentTabView() != null && (width = this.mTabHost.getCurrentTabView().getWidth()) > 0) {
            int availableTabCount = IMEApplication.from(this.mContext).getDisplayWidth() / width;
            log.d("onPageScrolled...tab width:  ", Integer.valueOf(width), "...availableTabCount: ", Integer.valueOf(availableTabCount), "...mTabHost.getTabWidget().getChildCount(): ", Integer.valueOf(this.mTabHost.getTabWidget().getChildCount()));
            if (DirectionalUtil.isCurrentlyRtl()) {
                this.hScrollView.scrollTo(((this.mTabHost.getTabWidget().getChildCount() - 1) - pos) * width, 0);
            } else {
                this.hScrollView.scrollTo(pos * width, 0);
            }
            if (this.mTabHost.getCurrentTab() != pos) {
                this.mTabHost.setCurrentTab(pos);
            }
            updateStoreUiProperties();
        } else {
            return;
        }
        setCatlogCallBackListener(pos);
        ThemePageAdapter pageAdapter = (ThemePageAdapter) this.mViewPager.getAdapter();
        if (pageAdapter != null && pageAdapter.getCount() > pos && (fm = pageAdapter.getRegisteredFragment(pos)) != null) {
            if ((fm instanceof ThemeFragmentController.MyThemesFragment) && ((ThemeFragmentController.MyThemesFragment) fm).getFragmentSource() == ThemesFragment.FRAGMENT_SOURCE.MY_THEMES.ordinal()) {
                ((ThemeFragmentController.MyThemesFragment) fm).updateThemes(ThemeManager.MY_THEMES);
            } else if (fm instanceof ThemesCategoryFragment) {
                ((ThemesCategoryFragment) fm).updateThemes();
            }
        }
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageSelected(int pos) {
        log.d("onPageSelected...pos: ", Integer.valueOf(pos));
        if (isMainTabActive() && !doNotLogVisitCount) {
            if (pos == MAIN_TABS_ID.MY_THEMES.ordinal()) {
                UsageData.recordScreenVisited(UsageData.Screen.MY_THEMES);
            } else if (pos == MAIN_TABS_ID.GET_THEMES.ordinal()) {
                UsageData.recordScreenVisited(UsageData.Screen.GET_THEMES);
            }
        }
    }

    private static void AddTab(FragmentActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new ThemeTabFactory(activity.getApplicationContext()));
        tabHost.addTab(tabSpec);
    }

    public void initialiseTabHost() {
        FragmentActivity store = getStore();
        this.mViewPager = (ThemeViewPager) store.findViewById(R.id.viewpager);
        this.mViewPager.setScrollDisabled(true);
        this.mViewPager.setOnPageChangeListener(this);
        this.fragmentArea = (FrameLayout) store.findViewById(R.id.content_frame);
        this.fragmentArea.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        this.fragmentArea.setVisibility(8);
        this.hScrollView = (HorizontalScrollView) store.findViewById(R.id.horizontalScrollView);
        this.mTabHost = (TabHost) store.findViewById(android.R.id.tabhost);
        this.mTabHost.setup();
    }

    private void showLegalRequirements(boolean tosRequired, boolean optInRequired, Bundle resultData) {
        SettingsV11 store = (SettingsV11) getStore();
        if (!store.isTosCurrentlyShown()) {
            log.d("showLegalRequirements...tosRequired: ", Boolean.valueOf(tosRequired), "...optInRequired: ", Boolean.valueOf(optInRequired));
            Intent intent = ConnectLegal.getLegalActivitiesStartIntent(store, tosRequired, optInRequired, resultData);
            if (intent != null) {
                store.startActivityForResult(intent, 200);
                store.setTosCurrentlyShown(true);
            }
        }
    }

    public void refreshMainTabsList() {
        this.mViewPager.setAdapter(this.mainTabPageAdapter);
        this.mainTabPageAdapter.setCurrentFragments(MAIN_TABS_TAG);
        this.mViewPager.invalidate();
    }

    public void showOnlyMyThemesTab(REFRESH_SOURCE source) {
        if (!this.mainTabsList.isEmpty()) {
            this.mViewPager.setAdapter(this.mainTabPageAdapter);
            this.mTabHost.setCurrentTab(0);
            this.mTabHost.clearAllTabs();
            Iterator<TabHost.TabSpec> it = this.mainTabsList.iterator();
            while (it.hasNext()) {
                TabHost.TabSpec spec = it.next();
                this.mTabHost.addTab(spec);
            }
            this.mTabHost.setOnTabChangedListener(this);
            this.mainTabPageAdapter.setCurrentFragments(MAIN_TABS_TAG);
            setCurrentTab(0, source);
            this.mViewPager.setOnPageChangeListener(this);
            this.mTabHost.setCurrentTab(0);
            if (this.mainTabPageAdapter.getRegisteredFragment(0) != null) {
                this.mainTabPageAdapter.getRegisteredFragment(0).setMenuVisibility(true);
            }
            refreshTabTitleColor(0);
            log.d("showOnlyMyThemesTab...mainTabsList not empty...refreshTabTitle");
            updateStoreUiProperties();
            return;
        }
        this.mViewPager.setAdapter(this.mainTabPageAdapter);
        this.mTabHost.setCurrentTab(0);
        this.mTabHost.clearAllTabs();
        TabHost.TabSpec myThemesspec = this.mTabHost.newTabSpec("myThemes").setIndicator(this.mContext.getString(R.string.store_my_themes));
        AddTab(getStore(), this.mTabHost, myThemesspec);
        this.mainTabsList.add(myThemesspec);
        this.mTabHost.setOnTabChangedListener(this);
        List<Fragment> fragments = ThemeFragmentController.newInstance(this.mContext, getStore()).getMyThemesFragment(source);
        this.mainTabPageAdapter.addFragments(MAIN_TABS_TAG, fragments);
        this.mainTabPageAdapter.setCurrentFragments(MAIN_TABS_TAG);
        setCurrentTab(0, source);
        this.mTabHost.setCurrentTab(0);
        refreshTabTitleColor(0);
        log.d("showOnlyMyThemesTab...mainTabsList empty...refreshTabTitle");
        updateStoreUiProperties();
    }

    public void showMainTabsList(int tabId, REFRESH_SOURCE source) {
        int ordinal;
        log.d("showMainTabsList...tabId: ", Integer.valueOf(tabId), "...source: ", source);
        CurrentFragmentInfo currentFragmentInfo2 = currentFragmentInfo;
        if (tabId == 1) {
            ordinal = ThemesFragment.FRAGMENT_SOURCE.BUY_THEMES.ordinal();
        } else {
            ordinal = ThemesFragment.FRAGMENT_SOURCE.MY_THEMES.ordinal();
        }
        currentFragmentInfo2.initialize(ordinal, null, null);
        if (!ThemeManager.isDownloadableThemesEnabled()) {
            showOnlyMyThemesTab(source);
            return;
        }
        if (source == REFRESH_SOURCE.CATALOG_CALLBACK) {
            doNotLogVisitCount = true;
        } else {
            doNotLogVisitCount = false;
        }
        if (source == REFRESH_SOURCE.ON_BACK && !this.mainTabsList.isEmpty() && this.mainTabPageAdapter != null && this.mainTabPageAdapter.getCount() == 2) {
            this.mViewPager.setAdapter(this.mainTabPageAdapter);
            this.mTabHost.setCurrentTab(0);
            this.mTabHost.clearAllTabs();
            Iterator<TabHost.TabSpec> it = this.mainTabsList.iterator();
            while (it.hasNext()) {
                TabHost.TabSpec spec = it.next();
                this.mTabHost.addTab(spec);
            }
            this.mTabHost.setOnTabChangedListener(this);
            List<Fragment> fragments = ThemeFragmentController.newInstance(this.mContext, getStore()).getMainTabThemeFragments(REFRESH_SOURCE.ON_BACK);
            this.mainTabPageAdapter.addFragments(MAIN_TABS_TAG, fragments);
            this.mainTabPageAdapter.setCurrentFragments(MAIN_TABS_TAG);
            this.mTabHost.setCurrentTab(tabId);
            refreshTabTitleColor(tabId);
            log.d("showMainTabsList...mainTabsList not empty...refreshTabTitle");
            updateStoreUiProperties();
            return;
        }
        this.mainTabsList.clear();
        this.mViewPager.setAdapter(this.mainTabPageAdapter);
        this.mTabHost.setCurrentTab(0);
        this.mTabHost.clearAllTabs();
        TabHost.TabSpec myThemesspec = this.mTabHost.newTabSpec("myThemes").setIndicator(this.mContext.getString(R.string.store_my_themes));
        TabHost.TabSpec getThemesspec = this.mTabHost.newTabSpec("getThemes").setIndicator(this.mContext.getString(R.string.get_themes));
        AddTab(getStore(), this.mTabHost, myThemesspec);
        AddTab(getStore(), this.mTabHost, getThemesspec);
        this.mainTabsList.add(myThemesspec);
        this.mainTabsList.add(getThemesspec);
        this.mTabHost.setOnTabChangedListener(this);
        List<Fragment> fragments2 = ThemeFragmentController.newInstance(this.mContext, getStore()).getMainTabThemeFragments(REFRESH_SOURCE.INIT);
        this.mainTabPageAdapter.addFragments(MAIN_TABS_TAG, fragments2);
        this.mainTabPageAdapter.setCurrentFragments(MAIN_TABS_TAG);
        setCurrentTab(tabId, source);
        if (source == REFRESH_SOURCE.CATALOG_CALLBACK) {
            doNotLogVisitCount = false;
        }
        refreshTabTitleColor(tabId);
        log.d("showMainTabsList...mainTabsList empty...refreshTabTitle");
        updateStoreUiProperties();
        setCatlogCallBackListener(tabId);
    }

    private String refreshTabTitleColor(int currentTab) {
        String title = "";
        if (this.mTabHost == null) {
            return "";
        }
        TabWidget widget = this.mTabHost.getTabWidget();
        widget.setDividerDrawable(R.drawable.settings_tab_widget_divider);
        for (int i = 0; i < widget.getChildCount(); i++) {
            View v = widget.getChildAt(i);
            TextView tv = (TextView) v.findViewById(android.R.id.title);
            if (tv != null) {
                if (currentTab == i) {
                    v.setBackgroundResource(R.drawable.settings_tab_widget_background_selected);
                    tv.setTextColor(this.mContext.getResources().getColor(R.color.theme_tab_title_selected));
                    title = new StringBuilder().append((Object) tv.getText()).toString();
                } else {
                    v.setBackgroundResource(R.drawable.settings_tab_widget_background_normal);
                    tv.setTextColor(this.mContext.getResources().getColor(R.color.theme_tab_title_unselected));
                }
            }
        }
        return title;
    }

    private void updateStoreUiProperties() {
        log.d("updateStoreUiProperties...");
        FragmentActivity store = getStore();
        if (this.mViewPager.getAdapter().equals(this.mainTabPageAdapter)) {
            if (this.mViewPager.getCurrentItem() == 0) {
                store.setTitle(store.getString(R.string.pref_menu_themes));
            } else {
                store.setTitle(store.getString(R.string.store));
            }
            this.mViewPager.setScrollDisabled(true);
            return;
        }
        store.setTitle(store.getString(R.string.view_all_content));
        this.mViewPager.setScrollDisabled(false);
    }

    private boolean checkTOS(int tabId) {
        if (!IMEApplication.from(this.mContext).isUserUnlockFinished() || !this.mViewPager.getAdapter().equals(this.mainTabPageAdapter) || !ThemeManager.isDownloadableThemesEnabled() || ConnectLegal.from(this.mContext).isTosAccepted() || tabId != MAIN_TABS_ID.GET_THEMES.ordinal()) {
            return false;
        }
        showLegalRequirements(true, false, null);
        return true;
    }

    public int getCurrentTab() {
        return this.mTabHost.getCurrentTab();
    }

    public CurrentFragmentInfo getCurrentFragmentInfo() {
        return currentFragmentInfo;
    }

    public boolean isStoreActive() {
        return isTabHostShown() && (getCurrentTab() == MAIN_TABS_ID.GET_THEMES.ordinal() || isViewAllTabActive());
    }

    private boolean isViewAllAdapterReusable() {
        if (this.viewAllTabPageAdapter == null) {
            return false;
        }
        for (int i = 0; i < this.viewAllTabPageAdapter.getCount(); i++) {
            Fragment fm = this.viewAllTabPageAdapter.getItem(i);
            if ((fm instanceof ThemesFragment) && ((ThemesFragment) fm).getAdapterThemeCount() == 0) {
                return false;
            }
        }
        return true;
    }

    public void showViewAllTabsList(String category) {
        synchronized (lock) {
            if (currentCategoryTitle == null) {
                currentCategoryTitle = category;
                this.themesStartPointTime = System.currentTimeMillis();
            }
        }
        UsageData.recordScreenVisited(UsageData.Screen.CATEGORY_PAGE_PREVIEW);
        currentFragmentInfo.initialize(ThemesFragment.FRAGMENT_SOURCE.VIEW_ALL_THEMES.ordinal(), category, null);
        ArrayList<CategoryItem> categoryList = ThemeManager.from(this.mContext).getThemeCategoryItemList(this.mContext);
        if (categoryList != null) {
            boolean reuseable = isViewAllAdapterReusable();
            if (reuseable && !this.viewAllTabsList.isEmpty() && this.viewAllTabsList.size() == categoryList.size() && this.viewAllTabPageAdapter != null && this.viewAllTabPageAdapter.getCount() == categoryList.size()) {
                this.mViewPager.setAdapter(this.viewAllTabPageAdapter);
                this.mTabHost.setCurrentTab(0);
                this.mTabHost.clearAllTabs();
                Iterator<TabHost.TabSpec> it = this.viewAllTabsList.iterator();
                while (it.hasNext()) {
                    TabHost.TabSpec spec = it.next();
                    this.mTabHost.addTab(spec);
                }
                this.mTabHost.setOnTabChangedListener(this);
                this.viewAllTabPageAdapter.setCurrentFragments(VIEWALL_TABS_TAG);
                int tabIndex = 0;
                int currentIndex = 0;
                Iterator<CategoryItem> it2 = categoryList.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    if (it2.next().categoryId.equals(category)) {
                        currentIndex = tabIndex;
                        break;
                    }
                    tabIndex++;
                }
                this.mTabHost.setCurrentTab(currentIndex);
                if (this.viewAllTabPageAdapter.getRegisteredFragment(currentIndex) != null) {
                    this.viewAllTabPageAdapter.getRegisteredFragment(currentIndex).setMenuVisibility(true);
                }
                refreshTabTitleColor(currentIndex);
                log.d("showViewAllTabsList...viewAllTabsList not empty...refreshTabTitle");
                updateStoreUiProperties();
                return;
            }
            this.mViewPager.setAdapter(this.viewAllTabPageAdapter);
            this.mTabHost.clearAllTabs();
            this.viewAllTabsList.clear();
            int tabIndex2 = 0;
            int currentIndex2 = 0;
            Iterator<CategoryItem> it3 = categoryList.iterator();
            while (it3.hasNext()) {
                CategoryItem item = it3.next();
                if (item.categoryId.equals(category)) {
                    currentIndex2 = tabIndex2;
                }
                TabHost.TabSpec tab = this.mTabHost.newTabSpec(item.categoryId).setIndicator(item.title);
                AddTab(getStore(), this.mTabHost, tab);
                this.viewAllTabsList.add(tab);
                tabIndex2++;
            }
            this.mTabHost.setOnTabChangedListener(this);
            List<Fragment> fragments = ThemeFragmentController.newInstance(this.mContext, getStore()).getViewAllTabThemeFragments(reuseable);
            this.viewAllTabPageAdapter.addFragments(VIEWALL_TABS_TAG, fragments);
            this.viewAllTabPageAdapter.setCurrentFragments(VIEWALL_TABS_TAG);
            this.viewAllTabPageAdapter.notifyDataSetChanged();
            this.mTabHost.setCurrentTab(currentIndex2);
            refreshTabTitleColor(currentIndex2);
            log.d("showViewAllTabsList...viewAllTabsList empty...refreshTabTitle");
            updateStoreUiProperties();
            setCatlogCallBackListener(currentIndex2);
        }
    }

    public boolean isTabHostShown() {
        LogManager.Log log2 = log;
        Object[] objArr = new Object[2];
        objArr[0] = "isTabHostShown...";
        objArr[1] = Boolean.valueOf(this.mTabHost.getVisibility() == 0);
        log2.d(objArr);
        return this.mTabHost.getVisibility() == 0 && this.mTabHost.isShown();
    }

    public boolean isCurrentTabBuyThemes() {
        if (this.mTabHost.getVisibility() == 0 && this.mTabHost.isShown() && this.mViewPager != null && this.mViewPager.getAdapter() != null && this.mViewPager.getAdapter().equals(this.mainTabPageAdapter)) {
            log.d("current is in buy themes.");
            return this.mViewPager.getCurrentItem() == 1;
        }
        log.d("current is not in buy themes.");
        return false;
    }

    public boolean isMainTabActive() {
        return (this.mViewPager == null || this.mViewPager.getAdapter() == null || !this.mViewPager.getAdapter().equals(this.mainTabPageAdapter)) ? false : true;
    }

    public boolean isViewAllTabActive() {
        return (this.mViewPager == null || this.mViewPager.getAdapter() == null || !this.mViewPager.getAdapter().equals(this.viewAllTabPageAdapter)) ? false : true;
    }

    public void showTabHost() {
        log.d("showTabHost...");
        if (this.mTabHost != null && this.mTabHost.getVisibility() == 8) {
            this.fragmentArea.setVisibility(8);
            this.mTabHost.setVisibility(0);
        }
    }

    public void hideTabHost() {
        log.d("hideTabHost...");
        if (this.mTabHost != null) {
            recordCategoryBrowseTime(null, System.currentTimeMillis());
            this.mTabHost.setVisibility(8);
        }
        this.fragmentArea.setVisibility(0);
        if (this.mHandler.hasMessages(100000)) {
            this.mHandler.removeMessages(100000);
        }
    }

    public void onUserLeaveHint() {
        if (this.mTabHost != null) {
            recordCategoryBrowseTime(null, System.currentTimeMillis());
        }
    }

    public boolean onBackPressed() {
        log.d("onBackPressed...");
        if (this.mHandler.hasMessages(100000)) {
            this.mHandler.removeMessages(100000);
        }
        if (this.mTabHost.getVisibility() == 8) {
            this.fragmentArea.setVisibility(8);
            this.mTabHost.setVisibility(0);
            if (this.mViewPager != null) {
                if ((this.mViewPager.getAdapter() == null || currentFragmentInfo.getType() == ThemesFragment.FRAGMENT_SOURCE.BUNDLE_THEMES.ordinal()) && currentFragmentInfo.getBundleSku() != null && currentFragmentInfo.getType() == ThemesFragment.FRAGMENT_SOURCE.BUNDLE_THEMES.ordinal() && ((this.mViewPager.getAdapter() == null || !this.mViewPager.getAdapter().equals(this.mainTabPageAdapter)) && currentFragmentInfo.getSourceFragmentType() != ThemesFragment.FRAGMENT_SOURCE.BUY_THEMES.ordinal())) {
                    showViewAllTabsList(currentFragmentInfo.getCategory());
                } else {
                    showMainTabsList(1, REFRESH_SOURCE.ON_BACK);
                }
            }
            SettingsV11 store = (SettingsV11) getStore();
            store.setCurrentFragment(null);
            this.themesStartPointTime = System.currentTimeMillis();
            store.getActionbarManager().restoreActionBar();
            return true;
        }
        if (this.mTabHost.getVisibility() == 0 && this.mViewPager.getAdapter() != null) {
            if (this.mViewPager.getAdapter().equals(this.mainTabPageAdapter)) {
                return false;
            }
            if (this.mViewPager.getAdapter().equals(this.viewAllTabPageAdapter)) {
                this.themesStartPointTime = System.currentTimeMillis();
                showMainTabsList(1, REFRESH_SOURCE.ON_BACK);
                recordCategoryBrowseTime(null, System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    public String getCurrentFragmentSource() {
        if (this.mTabHost.getVisibility() == 8 && this.fragmentArea.getVisibility() == 0) {
            return SettingsV11.TAB_BUNDLE_VIEW;
        }
        if (this.mTabHost.getVisibility() == 0 && this.mViewPager.getAdapter() != null) {
            if (this.mViewPager.getAdapter().equals(this.mainTabPageAdapter)) {
                if (isCurrentTabBuyThemes()) {
                    return SettingsV11.TAB_GET_THEMES;
                }
                return SettingsV11.TAB_MY_THEMES;
            }
            if (this.mViewPager.getAdapter().equals(this.viewAllTabPageAdapter)) {
                return SettingsV11.TAB_CATEGORY_VIEW;
            }
        }
        log.d("getCurrentFragmentSource: could not determine source, returning empty string");
        return "";
    }

    /* loaded from: classes.dex */
    public static class CurrentFragmentInfo {
        private String bundleSku;
        private String category;
        private int fragmentType = -1;
        private int sourceFragmentType = -1;

        CurrentFragmentInfo() {
        }

        public void initialize(int type, String category, String bundleSku) {
            this.fragmentType = type;
            this.category = category;
            this.bundleSku = bundleSku;
        }

        public void setSourceFragment(int sourceFragment) {
            this.sourceFragmentType = sourceFragment;
        }

        public int getSourceFragmentType() {
            return this.sourceFragmentType;
        }

        public int getType() {
            return this.fragmentType;
        }

        public String getBundleSku() {
            return this.bundleSku;
        }

        public String getCategory() {
            return this.category;
        }
    }

    public boolean isDownloadableThemesDataAvailable() {
        boolean ret = true;
        CatalogManager mCatalogManager = IMEApplication.from(this.mContext).getCatalogManager();
        if (!mCatalogManager.mServiceEnabled) {
            ret = false;
        }
        if (mCatalogManager.getAllSKUs() == null || mCatalogManager.getAllSKUs().size() <= 0) {
            ret = false;
        }
        log.d("isDownloadableThemesDataAvailable...return: ", Boolean.valueOf(ret));
        return ret;
    }
}
