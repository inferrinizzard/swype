package com.nuance.swype.input.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.swype.inapp.CatalogManager;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.input.settings.SettingsV11;
import com.nuance.swype.input.settings.ThemesFragment;
import com.nuance.swype.input.store.ThemeFragmentController;
import com.nuance.swype.service.impl.AccountUtil;
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
public class ThemesCategoryFragment extends Fragment implements CatalogManager.OnCatalogCallBackListener, ThemeManager.OnThemePreviewDialogListener, SettingsV11.KeyUpListener {
    private static final LogManager.Log log = LogManager.getLog("ThemesCategoryFragment");
    private ThemesCategory delegate;
    private Dialog dialog;
    private ThemeManager themeManager;

    public static ThemesCategoryFragment newInstance(String arg) {
        ThemesCategoryFragment getThemes = new ThemesCategoryFragment();
        Bundle args = new Bundle();
        args.putString("arg", arg);
        getThemes.setArguments(args);
        return getThemes;
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        log.d("onCreate...");
        super.onCreate(savedInstanceState);
        Context context = getActivity().getApplicationContext();
        this.themeManager = ThemeManager.from(context);
        this.delegate = new ThemesCategory(context) { // from class: com.nuance.swype.input.settings.ThemesCategoryFragment.1
            @Override // com.nuance.swype.input.settings.ThemesCategory
            protected void showApplyThemeDialog(int position, String sku, String category) {
                if (ThemesCategoryFragment.this.getActivity() != null) {
                    Intent intent = new Intent(ThemesCategoryFragment.this.getActivity(), (Class<?>) PopupDialogThemeActivity.class);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_ID, sku);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, category);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_VIEW_INDEX, position);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SOURCE, ((SettingsV11) ThemesCategoryFragment.this.getActivity()).getCurrentFragmentSource());
                    intent.setFlags(603979776);
                    ThemesCategoryFragment.this.startActivityForResult(intent, PopupDialogThemeActivity.REQUEST_APPLY_THEME);
                }
            }

            @Override // com.nuance.swype.input.settings.ThemesCategory
            protected void showPurchaseFlowDialog(int position, String sku, String category) {
                if (ThemesCategoryFragment.this.getActivity() != null && checkNetworkConnection(ThemesCategoryFragment.this.getActivity())) {
                    if (ThemesCategoryFragment.this.dialog == null || !ThemesCategoryFragment.this.dialog.isShowing()) {
                        ThemeManager themeManager = IMEApplication.from(ThemesCategoryFragment.this.getActivity().getApplicationContext()).getThemeManager();
                        if (BuildInfo.from(ThemesCategoryFragment.this.getActivity().getApplicationContext()).isGoogleTrialBuild()) {
                            ThemesCategoryFragment.this.dialog = themeManager.showNotAvailableDialogForGoogleTrial(ThemesCategoryFragment.this.getActivity(), ThemesCategoryFragment.this);
                            ThemesCategoryFragment.this.dialog.show();
                            return;
                        }
                        Intent intent = new Intent(ThemesCategoryFragment.this.getActivity(), (Class<?>) PopupDialogThemeActivity.class);
                        intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SKU, sku);
                        intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, category);
                        intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_VIEW_INDEX, position);
                        intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SOURCE, ((SettingsV11) ThemesCategoryFragment.this.getActivity()).getCurrentFragmentSource());
                        intent.setFlags(603979776);
                        themeManager.setThemesCategory(this);
                        ThemesCategoryFragment.this.startActivityForResult(intent, PopupDialogThemeActivity.REQUEST_PURCHASE);
                    }
                }
            }

            @Override // com.nuance.swype.input.settings.ThemesCategory
            protected void showMoreThemes(String category) {
                if (ThemesCategoryFragment.this.getActivity() != null) {
                    IMEApplication.from(ThemesCategoryFragment.this.getActivity().getApplicationContext()).getThemeManager().setCurrentThemesCategory(this);
                    ThemeFragmentController.newInstance(ThemesCategoryFragment.this.getActivity().getApplicationContext(), ThemesCategoryFragment.this.getActivity()).ShowViewAllTabFragments(category, ThemesCategoryFragment.this.getActivity());
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.swype.input.settings.ThemesCategory
            public void showBundleView(String category, String bundleSku) {
                if (ThemesCategoryFragment.this.getActivity() != null) {
                    ThemeFragmentController.newInstance(ThemesCategoryFragment.this.getActivity().getApplicationContext(), ThemesCategoryFragment.this.getActivity()).ShowBundleFragments(category, bundleSku, ThemesFragment.FRAGMENT_SOURCE.BUY_THEMES.ordinal(), ThemesCategoryFragment.this.getActivity());
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.swype.input.settings.ThemesCategory
            public void showInstallThemeDialog(int position, String sku, String category) {
                if (ThemesCategoryFragment.this.getActivity() != null && checkNetworkConnection(ThemesCategoryFragment.this.getActivity())) {
                    if (ThemesCategoryFragment.this.dialog == null || !ThemesCategoryFragment.this.dialog.isShowing()) {
                        ThemeManager themeManager = IMEApplication.from(ThemesCategoryFragment.this.getActivity().getApplicationContext()).getThemeManager();
                        Intent intent = new Intent(ThemesCategoryFragment.this.getActivity(), (Class<?>) PopupDialogThemeActivity.class);
                        intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SKU, sku);
                        intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, category);
                        intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_VIEW_INDEX, position);
                        intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_REQUEST, PopupDialogThemeActivity.REQUEST_INSTALL_THEME);
                        intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SOURCE, ((SettingsV11) ThemesCategoryFragment.this.getActivity()).getCurrentFragmentSource());
                        intent.setFlags(603979776);
                        themeManager.setThemesCategory(this);
                        ThemesCategoryFragment.this.startActivityForResult(intent, PopupDialogThemeActivity.REQUEST_INSTALL_THEME);
                    }
                }
            }

            @Override // com.nuance.swype.input.settings.ThemesCategory
            protected void showUninstallThemeDialog(int position, String sku, String category) {
                if (ThemesCategoryFragment.this.getActivity() != null) {
                    Intent intent = new Intent(ThemesCategoryFragment.this.getActivity(), (Class<?>) PopupDialogThemeActivity.class);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SKU, sku);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, category);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_VIEW_INDEX, position);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_REQUEST, PopupDialogThemeActivity.REQUEST_UNINSTALL_APPLY_THEME);
                    intent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_SOURCE, ((SettingsV11) ThemesCategoryFragment.this.getActivity()).getCurrentFragmentSource());
                    intent.setFlags(603979776);
                    ThemesCategoryFragment.this.startActivityForResult(intent, PopupDialogThemeActivity.REQUEST_UNINSTALL_APPLY_THEME);
                }
            }

            @Override // com.nuance.swype.input.settings.ThemesCategory
            protected void restorePurchases() {
                ThemeItemSeed seed;
                if (ThemesCategoryFragment.this.getActivity() != null) {
                    Map<String, LinkedHashMap<String, ThemeManager.SwypeTheme>> themes = ThemeManager.from(ThemesCategoryFragment.this.getActivity().getApplicationContext()).getCategoryThemes(ThemesCategoryFragment.this.getActivity().getApplicationContext());
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
                    CatalogManager catalogManager = IMEApplication.from(ThemesCategoryFragment.this.getActivity().getApplicationContext()).getCatalogManager();
                    if (catalogManager != null) {
                        for (ThemeManager.SwypeTheme theme : toBeResored) {
                            try {
                                catalogManager.downloadTheme(0, (ThemeManager.ConnectDownloadableThemeWrapper) theme);
                                log.d("Downloaded theme sku is:" + theme.getSku());
                            } catch (ACException e) {
                                log.e(String.format("Download of theme failed. SKU: %s", theme.getSku()));
                            }
                        }
                    }
                }
            }

            @Override // com.nuance.swype.input.settings.ThemesCategory
            protected void uninstallThemes() {
                ThemeItemSeed seed;
                if (ThemesCategoryFragment.this.getActivity() != null) {
                    Map<String, LinkedHashMap<String, ThemeManager.SwypeTheme>> themes = ThemeManager.from(ThemesCategoryFragment.this.getActivity().getApplicationContext()).getCategoryThemes(ThemesCategoryFragment.this.getActivity().getApplicationContext());
                    List<ThemeManager.SwypeTheme> toBeUninstalled = new ArrayList<>();
                    for (Map.Entry<String, LinkedHashMap<String, ThemeManager.SwypeTheme>> entry : themes.entrySet()) {
                        if (themes.get(entry.getKey()) != null) {
                            for (ThemeManager.SwypeTheme item : themes.get(entry.getKey()).values()) {
                                if ((item instanceof ThemeManager.ConnectDownloadableThemeWrapper) && (seed = ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed()) != null && seed.isInstalled) {
                                    toBeUninstalled.add(item);
                                }
                            }
                        }
                    }
                    log.d("to be removed themes: " + toBeUninstalled);
                    CatalogManager catalogManager = IMEApplication.from(ThemesCategoryFragment.this.getActivity().getApplicationContext()).getCatalogManager();
                    if (catalogManager != null) {
                        for (ThemeManager.SwypeTheme theme : toBeUninstalled) {
                            try {
                                catalogManager.uninstallTheme(0, ((ThemeManager.ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().categoryKey, theme.getSku());
                                log.d("to be removed theme sku is:" + theme.getSku());
                            } catch (ACException e) {
                                log.e(String.format("removable of theme failed. SKU: %s", theme.getSku()));
                            }
                        }
                    }
                }
            }

            @Override // com.nuance.swype.input.settings.ThemesCategory
            protected void showGoogleAccountErrorDialog() {
                if (AccountUtil.isGoogleAccountMissing()) {
                    ((SettingsV11) ThemesCategoryFragment.this.getActivity()).showGoogleAccountMissingDialog();
                } else if (!AccountUtil.isGoogleAccountSignedIn()) {
                    ((SettingsV11) ThemesCategoryFragment.this.getActivity()).showGoogleAccountLoginFailedDialog();
                }
            }
        };
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        log.d("onActivityCreated...");
        super.onActivityCreated(savedInstanceState);
        ThemeManager.setUpdateNotificationAllowed(false);
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        log.d("onResume...");
        super.onResume();
        this.delegate.refreshCategoryListView();
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        log.d("onPause...");
        super.onPause();
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroy() {
        log.d("onDestroy...");
        super.onDestroy();
        ThemeManager.setUpdateNotificationAllowed(false);
        if (this.delegate != null) {
            this.delegate.onDestroy();
            this.delegate = null;
        }
        this.themeManager.setThemesCategory(null);
    }

    @Override // android.support.v4.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        log.d("onCreateOptionsMenu...menu: ", menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String themeId = null;
        String categoryId = null;
        if (data != null) {
            themeId = data.getExtras().getString(PopupDialogThemeActivity.EXTRA_THEME_ID);
            categoryId = data.getExtras().getString(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID);
        }
        if (requestCode == 10001) {
            if (resultCode == -1) {
                log.d("purchased ");
                if (data != null && themeId != null) {
                    this.themeManager.getThemeDataManager();
                    List<String> categoryList = ThemeData.getThemeSeed(themeId, categoryId).themeCategories;
                    if (categoryList != null) {
                        ACCatalogService cs = IMEApplication.from(getActivity()).getConnect().getCatalogService();
                        for (String category : categoryList) {
                            String key = cs.getCategoryKeyForCategoryName(category);
                            this.themeManager.getThemeDataManager();
                            ThemeItemSeed seed = ThemeData.getThemeSeed(themeId, key);
                            if (seed != null) {
                                seed.isPurchased = true;
                                this.themeManager.getThemeDataManager();
                                ThemeData.update(seed);
                                this.themeManager.updateThemeStatus(themeId, CatalogManager.ThemeStatusChange.THEME_PURCHASED);
                            }
                        }
                    }
                    try {
                        CatalogManager cm = IMEApplication.from(getActivity().getApplicationContext()).getCatalogManager();
                        if (cm != null) {
                            cm.getCatalogService().setPurchasedSKU(themeId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.d("Failed to mark a catalog item as purchased.");
                    }
                    this.delegate.updateStatusInThemesAdapter(themeId, CatalogManager.ThemeStatusChange.THEME_PURCHASED);
                    this.delegate.updateThemeThumbListAdapter(themeId, categoryId);
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
                ThemeManager.SwypeTheme theme = this.themeManager.getSwypeTheme(categoryId, themeId);
                this.delegate.applyTheme(theme, themeId, getActivity().getApplicationContext());
            }
        } else if (requestCode == 10005) {
            if (resultCode == -1 && themeId != null) {
                ThemeManager.SwypeTheme theme2 = this.themeManager.getSwypeTheme(categoryId, themeId);
                this.delegate.applyTheme(theme2, themeId, getActivity().getApplicationContext());
            }
        } else if (requestCode == 10003) {
            if (resultCode == -1) {
                if (data != null && themeId != null) {
                    log.d("theme sku:", themeId, "begin to install ");
                }
            } else {
                if (categoryId != null) {
                    this.delegate.notifyThumbAdapterDataSetChanged(categoryId);
                }
                log.d("install cancelled");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return this.delegate.onCreateCategoryListView(inflater, container);
    }

    @Override // com.nuance.swype.input.ThemeManager.OnThemePreviewDialogListener
    public void onThemePreivewDialogClosed() {
    }

    @Override // com.nuance.swype.input.settings.SettingsV11.KeyUpListener
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override // com.nuance.swype.inapp.CatalogManager.OnCatalogCallBackListener
    public void onCatalogItemListChanged() {
        if (this.delegate != null) {
            log.d("onCatalogItemListChanged refresh UI");
            this.delegate.updateThemeCategoryAdapter();
        }
    }

    @Override // com.nuance.swype.inapp.CatalogManager.OnCatalogCallBackListener
    public void onCatalogItemStatusChanged(int index, ThemeManager.ConnectDownloadableThemeWrapper theme, String category, String sku, CatalogManager.ThemeStatusChange state) {
        log.d("onCatalogItemStatusChanged...item: index: ", Integer.valueOf(index), "...categoryId:", category, " sku:", sku, "...state: ", state);
        if (this.delegate != null) {
            if (state != CatalogManager.ThemeStatusChange.THEME_NOSTATE) {
                this.delegate.updateStatusInThemesAdapter(category, sku, state);
            }
            this.delegate.updateThemeThumbListAdapter(sku, category);
            if (state == CatalogManager.ThemeStatusChange.THEME_INSTALLED) {
                Context context = getActivity().getApplicationContext();
                this.delegate.applyTheme(theme, sku, context);
                return;
            }
            return;
        }
        log.d("themesCategoryFragment delegate == null onCatalogItemStatusChanged...item: label:", category, " sku:", sku);
    }

    public void updateThemes() {
        if (this.delegate != null) {
            this.delegate.updateThemeCategoryAdapter();
        }
    }

    public void onCatalogItemSubListChanged() {
        if (this.delegate != null) {
            log.d("onCatalogItemSubListChanged...");
            this.delegate.updateAllThumbListAdapter(null, null);
        }
    }
}
