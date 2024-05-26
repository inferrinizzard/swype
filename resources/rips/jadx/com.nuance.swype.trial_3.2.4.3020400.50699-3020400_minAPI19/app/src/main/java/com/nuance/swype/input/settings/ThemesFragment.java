package com.nuance.swype.input.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.share.internal.ShareConstants;
import com.nuance.connect.api.CatalogService;
import com.nuance.swype.inapp.CatalogManager;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.input.store.ThemeFragmentController;
import com.nuance.swype.input.store.ThemeTabHostManager;
import com.nuance.swype.service.impl.AccountUtil;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.storage.ThemeData;
import com.nuance.swype.util.storage.ThemeItemSeed;
import com.nuance.swypeconnect.ac.ACCatalogService;
import com.nuance.swypeconnect.ac.ACException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class ThemesFragment extends Fragment implements CatalogManager.OnCatalogCallBackListener, ThemeManager.OnThemePreviewDialogListener {
    private static final LogManager.Log log = LogManager.getLog("ThemesFragment");
    private String bundleSku;
    private Themes delegate;
    private String fragmentCategory;
    private int fragmentSource;
    private boolean isThemePreviewDialogShowing;

    /* loaded from: classes.dex */
    public enum FRAGMENT_SOURCE {
        MY_THEMES,
        VIEW_ALL_THEMES,
        BUNDLE_THEMES,
        BUY_THEMES,
        FRAGMENT_NONE
    }

    public static ThemesFragment newInstance(String arg, FRAGMENT_SOURCE source) {
        log.d("newInstance...arg: ", arg, "...source: ", source);
        ThemesFragment myThemes = new ThemesFragment();
        Bundle args = new Bundle();
        args.putString("arg", arg);
        args.putInt(ShareConstants.FEED_SOURCE_PARAM, source.ordinal());
        myThemes.setArguments(args);
        return myThemes;
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        log.d("onCreate...this: ", this);
        super.onCreate(savedInstanceState);
    }

    public int getFragmentSource() {
        return this.fragmentSource;
    }

    public String getFragmentCategory() {
        return this.fragmentCategory;
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CatalogManager catalogManager;
        log.d("onCreateView...this: ", this);
        setHasOptionsMenu(true);
        String category = getArguments().getString("arg");
        this.bundleSku = getArguments().getString("bundle_sku");
        boolean isBundle = getArguments().getBoolean("isBundle");
        this.fragmentSource = getArguments().getInt(ShareConstants.FEED_SOURCE_PARAM);
        this.fragmentCategory = category;
        this.delegate = new Themes(getActivity().getApplicationContext(), category, this.bundleSku, this.fragmentSource, isBundle) { // from class: com.nuance.swype.input.settings.ThemesFragment.1
            @Override // com.nuance.swype.input.settings.Themes
            protected void showApplyThemeDialog(int position, String sku, String category2) {
                if (!ThemesFragment.this.isThemePreviewDialogShowing) {
                    ThemesFragment.this.isThemePreviewDialogShowing = true;
                    Intent intent = new Intent(ThemesFragment.this.getActivity(), (Class<?>) PopupDialogThemeActivity.class);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_ID, sku);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, category2);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_VIEW_INDEX, position);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SOURCE, ((SettingsV11) ThemesFragment.this.getActivity()).getCurrentFragmentSource());
                    ThemesFragment.this.startActivityForResult(intent, PopupDialogThemeActivity.REQUEST_APPLY_THEME);
                }
            }

            @Override // com.nuance.swype.input.settings.Themes
            protected void showPurchaseFlowDialog(int position, String sku, String category2) {
                if (checkNetworkConnection(ThemesFragment.this.getActivity()) && !ThemesFragment.this.isThemePreviewDialogShowing) {
                    ThemesFragment.this.isThemePreviewDialogShowing = true;
                    if (BuildInfo.from(ThemesFragment.this.getActivity().getApplicationContext()).isGoogleTrialBuild()) {
                        IMEApplication.from(ThemesFragment.this.getActivity().getApplicationContext()).getThemeManager().showNotAvailableDialogForGoogleTrial(ThemesFragment.this.getActivity(), ThemesFragment.this).show();
                        return;
                    }
                    Intent intent = new Intent(ThemesFragment.this.getActivity(), (Class<?>) PopupDialogThemeActivity.class);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SKU, sku);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, category2);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_VIEW_INDEX, position);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SOURCE, ((SettingsV11) ThemesFragment.this.getActivity()).getCurrentFragmentSource());
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_BUNDLE_SKU, ThemesFragment.this.bundleSku);
                    ThemesFragment.this.startActivityForResult(intent, PopupDialogThemeActivity.REQUEST_PURCHASE);
                }
            }

            @Override // com.nuance.swype.input.settings.Themes
            protected void showBundleView(String category2, String bundleSku) {
                ThemeFragmentController.newInstance(ThemesFragment.this.getActivity().getApplicationContext(), ThemesFragment.this.getActivity()).ShowBundleFragments(category2, bundleSku, FRAGMENT_SOURCE.VIEW_ALL_THEMES.ordinal(), ThemesFragment.this.getActivity());
            }

            @Override // com.nuance.swype.input.settings.Themes
            protected void navigateBackFromBundlePage() {
                ThemeTabHostManager tabHostManager = ((SettingsV11) ThemesFragment.this.getActivity()).getTabHostManager();
                if (tabHostManager != null) {
                    tabHostManager.onBackPressed();
                }
            }

            @Override // com.nuance.swype.input.settings.Themes
            protected void showInstallThemeDialog(int position, String sku, String category2) {
                if (checkNetworkConnection(ThemesFragment.this.getActivity()) && !ThemesFragment.this.isThemePreviewDialogShowing) {
                    ThemesFragment.this.isThemePreviewDialogShowing = true;
                    Intent intent = new Intent(ThemesFragment.this.getActivity(), (Class<?>) PopupDialogThemeActivity.class);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SKU, sku);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, category2);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_VIEW_INDEX, position);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_REQUEST, PopupDialogThemeActivity.REQUEST_INSTALL_THEME);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SOURCE, ((SettingsV11) ThemesFragment.this.getActivity()).getCurrentFragmentSource());
                    ThemesFragment.this.startActivityForResult(intent, PopupDialogThemeActivity.REQUEST_INSTALL_THEME);
                }
            }

            @Override // com.nuance.swype.input.settings.Themes
            protected void showUninstallThemeDialog(int position, String sku, String category2) {
                Intent intent = new Intent(ThemesFragment.this.getActivity(), (Class<?>) PopupDialogThemeActivity.class);
                intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SKU, sku);
                intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, category2);
                intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_VIEW_INDEX, position);
                intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SOURCE, ((SettingsV11) ThemesFragment.this.getActivity()).getCurrentFragmentSource());
                intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_REQUEST, PopupDialogThemeActivity.REQUEST_UNINSTALL_APPLY_THEME);
                ThemesFragment.this.startActivityForResult(intent, PopupDialogThemeActivity.REQUEST_UNINSTALL_APPLY_THEME);
            }

            @Override // com.nuance.swype.input.settings.Themes
            protected void restorePurchases() {
                ThemeItemSeed seed;
                Map<String, LinkedHashMap<String, ThemeManager.SwypeTheme>> themes = ThemeManager.from(ThemesFragment.this.getActivity().getApplicationContext()).getCategoryThemes(ThemesFragment.this.getActivity().getApplicationContext());
                List<ThemeManager.SwypeTheme> toBeResored = new ArrayList<>();
                for (Map.Entry<String, LinkedHashMap<String, ThemeManager.SwypeTheme>> entry : themes.entrySet()) {
                    if (themes.get(entry.getKey()) != null) {
                        for (ThemeManager.SwypeTheme item : themes.get(entry.getKey()).values()) {
                            if ((item instanceof ThemeManager.ConnectDownloadableThemeWrapper) && (seed = ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed()) != null && seed.isPurchased && !seed.isInstalled) {
                                toBeResored.add(item);
                            }
                        }
                    }
                }
                log.d("to be restored themes: " + toBeResored);
                CatalogManager catalogManager2 = IMEApplication.from(ThemesFragment.this.getActivity().getApplicationContext()).getCatalogManager();
                if (catalogManager2 != null) {
                    for (ThemeManager.SwypeTheme theme : toBeResored) {
                        try {
                            catalogManager2.downloadTheme(0, (ThemeManager.ConnectDownloadableThemeWrapper) theme);
                            log.d("Downloaded theme sku is:" + theme.getSku());
                        } catch (ACException e) {
                            log.e(String.format("Download of theme failed. SKU: %s", theme.getSku()));
                        }
                    }
                }
            }

            @Override // com.nuance.swype.input.settings.Themes
            protected void uninstallThemes() {
                ThemeItemSeed seed;
                Map<String, LinkedHashMap<String, ThemeManager.SwypeTheme>> themes = ThemeManager.from(ThemesFragment.this.getActivity().getApplicationContext()).getCategoryThemes(ThemesFragment.this.getActivity().getApplicationContext());
                List<ThemeManager.SwypeTheme> toBeUninstalled = new ArrayList<>();
                for (Map.Entry<String, LinkedHashMap<String, ThemeManager.SwypeTheme>> entry : themes.entrySet()) {
                    if (themes.get(entry.getKey()) != null) {
                        for (ThemeManager.SwypeTheme item : themes.get(entry.getKey()).values()) {
                            if ((item instanceof ThemeManager.ConnectDownloadableThemeWrapper) && (seed = ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed()) != null && seed.isPurchased && seed.isInstalled) {
                                toBeUninstalled.add(item);
                            }
                        }
                    }
                }
                log.d("to be removed themes: " + toBeUninstalled);
                CatalogManager catalogManager2 = IMEApplication.from(ThemesFragment.this.getActivity().getApplicationContext()).getCatalogManager();
                if (catalogManager2 != null) {
                    for (ThemeManager.SwypeTheme theme : toBeUninstalled) {
                        try {
                            String categoryKey = ((ThemeManager.ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().categoryKey;
                            catalogManager2.uninstallTheme(0, categoryKey, theme.getSku());
                            log.d("to be removed theme sku is:" + theme.getSku());
                        } catch (ACException e) {
                            log.e(String.format("removable of theme failed. SKU: %s", theme.getSku()));
                        }
                    }
                }
            }

            @Override // com.nuance.swype.input.settings.Themes
            protected void showGoogleAccountErrorDialog() {
                if (AccountUtil.isGoogleAccountMissing()) {
                    ((SettingsV11) ThemesFragment.this.getActivity()).showGoogleAccountMissingDialog();
                } else if (!AccountUtil.isGoogleAccountSignedIn()) {
                    ((SettingsV11) ThemesFragment.this.getActivity()).showGoogleAccountLoginFailedDialog();
                }
            }
        };
        if (isBundle && (catalogManager = IMEApplication.from(getActivity().getApplicationContext()).getCatalogManager()) != null) {
            catalogManager.setCatalogCallBackListener(this, UsageData.DownloadLocation.BUNDLE);
        }
        return this.delegate.onCreateView(inflater, container, isBundle, category, this.bundleSku);
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        log.d("onActivityCreated...this: ", this);
        super.onActivityCreated(savedInstanceState);
        ThemeManager.setIapObserver(this.delegate);
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        log.d("onResume...isHidden(): ", Boolean.valueOf(isHidden()), "...hasOptionsMenu(): ", Boolean.valueOf(hasOptionsMenu()), "...isMenuVisible(): ", Boolean.valueOf(isMenuVisible()));
        super.onResume();
        if (this.delegate != null) {
            this.delegate.onResume();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        log.d("onPause...this: ", this);
        super.onPause();
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        log.d("onStop...this: ", this);
        super.onStop();
    }

    public void updateThemes(String category) {
        if (this.delegate != null) {
            this.delegate.updateThemes(category);
        }
    }

    private void updateBundleThemes(String category) {
        if (this.delegate != null) {
            this.delegate.updateBundleThemes(category);
        }
    }

    public int getAdapterThemeCount() {
        if (this.delegate != null) {
            return this.delegate.getThemeCount();
        }
        return 0;
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ((SettingsV11) getActivity()).setActivityResulted(true);
        String themeId = null;
        String categoryId = null;
        if (data != null) {
            themeId = data.getExtras().getString(PopupDialogThemeActivity.EXTRA_THEME_ID);
            categoryId = data.getExtras().getString(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID);
        }
        if (requestCode == 10001) {
            if (resultCode == -1) {
                log.d("purchased ");
                if (data != null) {
                    if (themeId != null) {
                        ThemeManager themeManager = ThemeManager.from(getActivity().getApplicationContext());
                        themeManager.getThemeDataManager();
                        ThemeItemSeed seed = ThemeData.getThemeSeed(themeId, categoryId);
                        List<String> categoryList = seed.themeCategories;
                        if (categoryList != null) {
                            ACCatalogService cs = IMEApplication.from(getActivity()).getConnect().getCatalogService();
                            for (String category : categoryList) {
                                String key = cs.getCategoryKeyForCategoryName(category);
                                themeManager.getThemeDataManager();
                                seed = ThemeData.getThemeSeed(themeId, key);
                                if (seed != null) {
                                    seed.isPurchased = true;
                                    themeManager.getThemeDataManager();
                                    ThemeData.update(seed);
                                    ThemeManager.from(getActivity().getApplicationContext()).updateThemeStatus(themeId, CatalogManager.ThemeStatusChange.THEME_PURCHASED);
                                }
                            }
                        }
                        CatalogManager cm = IMEApplication.from(getActivity().getApplicationContext()).getCatalogManager();
                        if (seed != null) {
                            if (cm != null) {
                                try {
                                    cm.getCatalogService().setPurchasedSKU(themeId);
                                } catch (Exception e) {
                                    log.d("Failed to mark a catalog item as purchased. sku:", themeId);
                                }
                            }
                            if (seed.type == 1) {
                                updateBundleThemes(seed.sku);
                                ThemeManager.from(getActivity().getApplicationContext()).updateBundleThemeStatus(categoryId, themeId, CatalogManager.ThemeStatusChange.THEME_PURCHASED);
                            }
                        }
                        this.delegate.updateStatusInThemesAdapter(categoryId, themeId, CatalogManager.ThemeStatusChange.THEME_PURCHASED, seed.type == CatalogService.CatalogItem.Type.BUNDLE.ordinal());
                    }
                    if (data.getIntExtra(PopupDialogThemeActivity.EXTRA_THEME_REQUEST, 0) == 10003) {
                        this.delegate.showInstallThemeDialog(0, themeId, categoryId);
                    }
                }
            } else if (resultCode == 10006) {
                if (categoryId != null && themeId != null && !themeId.isEmpty()) {
                    this.delegate.showBundleView(categoryId, themeId);
                }
            } else if (resultCode == 1 && (AccountUtil.isGoogleAccountMissing() || !AccountUtil.isGoogleAccountSignedIn())) {
                CatalogManager catalogManager = IMEApplication.from(getActivity().getApplicationContext()).getCatalogManager();
                if (catalogManager != null) {
                    catalogManager.triggerShowingThemesWithoutPrices();
                }
                if (AccountUtil.isGoogleAccountMissing()) {
                    ((SettingsV11) getActivity()).showGoogleAccountMissingDialog();
                }
            } else {
                log.d("purchase cancelled");
            }
        } else if (requestCode == 10002) {
            if (resultCode == -1 && themeId != null) {
                ThemeManager.SwypeTheme theme = IMEApplication.from(getActivity().getApplicationContext()).getThemeManager().getSwypeTheme(categoryId, themeId);
                this.delegate.applyTheme(theme, themeId, getActivity().getApplicationContext());
            }
        } else if (requestCode == 10005 && resultCode == -1 && themeId != null) {
            ThemeManager.SwypeTheme theme2 = IMEApplication.from(getActivity().getApplicationContext()).getThemeManager().getSwypeTheme(categoryId, themeId);
            this.delegate.applyTheme(theme2, themeId, getActivity().getApplicationContext());
        }
        if (requestCode == 10003) {
            if (resultCode == -1) {
                if (data != null && themeId != null) {
                    log.d("theme sku:", themeId, "...category: ", categoryId, "begin to install");
                }
            } else {
                log.d("install cancelled");
            }
        }
        this.isThemePreviewDialogShowing = false;
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override // com.nuance.swype.input.ThemeManager.OnThemePreviewDialogListener
    public void onThemePreivewDialogClosed() {
        this.isThemePreviewDialogShowing = false;
    }

    @Override // com.nuance.swype.inapp.CatalogManager.OnCatalogCallBackListener
    public void onCatalogItemListChanged() {
    }

    @Override // com.nuance.swype.inapp.CatalogManager.OnCatalogCallBackListener
    public void onCatalogItemStatusChanged(int index, ThemeManager.ConnectDownloadableThemeWrapper theme, String category, String sku, CatalogManager.ThemeStatusChange state) {
        log.d("onCatalogItemStatusChanged...item: categoryId:", category, " sku:", sku, "...state: ", state);
        if (this.delegate == null) {
            log.d("themesFragment delegate == null onCatalogItemStatusChanged...item: label:", category, " sku:", sku);
            return;
        }
        if (state != CatalogManager.ThemeStatusChange.THEME_NOSTATE) {
            this.delegate.updateStatusInThemesAdapter(category, sku, state, false);
        }
        if (state == CatalogManager.ThemeStatusChange.THEME_UNINSTALLED && this.fragmentSource == FRAGMENT_SOURCE.MY_THEMES.ordinal()) {
            updateThemes(ThemeManager.MY_THEMES);
        }
        if (state == CatalogManager.ThemeStatusChange.THEME_INSTALLED) {
            Context context = getActivity().getApplicationContext();
            this.delegate.applyTheme(theme, sku, context);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroy() {
        log.d("onDestroy...this: ", this);
        super.onDestroy();
        this.delegate.onDestroy();
        this.delegate = null;
        ThemeManager.setIapObserver(null);
    }

    public void setCurrentTheme() {
        if (this.delegate != null) {
            this.delegate.setCurrentTheme();
        }
    }

    public void onCatalogItemSubListChanged() {
    }
}
