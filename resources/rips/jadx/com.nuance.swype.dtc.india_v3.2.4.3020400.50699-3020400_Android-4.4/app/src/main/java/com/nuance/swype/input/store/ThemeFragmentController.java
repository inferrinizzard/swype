package com.nuance.swype.input.store;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.facebook.share.internal.ShareConstants;
import com.nuance.swype.inapp.CategoryItem;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.input.settings.SettingsV11;
import com.nuance.swype.input.settings.ThemesCategoryFragment;
import com.nuance.swype.input.settings.ThemesFragment;
import com.nuance.swype.input.store.ThemeTabHostManager;
import com.nuance.swype.usagedata.UsageData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class ThemeFragmentController {
    private static ThemeFragmentController controller;
    private BundleThemeFragment bundleFragment;
    private Context mContext;
    private List<Fragment> mainTabFragmentList = new ArrayList();
    private List<Fragment> viewAllTabFragmentList = new ArrayList();

    private ThemeFragmentController(Context ctx, FragmentActivity activity) {
        this.mContext = ctx;
    }

    public static final ThemeFragmentController newInstance(Context ctx, FragmentActivity activity) {
        if (controller == null) {
            controller = new ThemeFragmentController(ctx, activity);
        }
        return controller;
    }

    public List<Fragment> getMainTabThemeFragments(ThemeTabHostManager.REFRESH_SOURCE source) {
        if (!this.mainTabFragmentList.isEmpty() && source != ThemeTabHostManager.REFRESH_SOURCE.INIT) {
            return this.mainTabFragmentList;
        }
        this.mainTabFragmentList.clear();
        Fragment f1 = MyThemesFragment.createInstance(ThemeManager.MY_THEMES);
        Fragment f2 = ThemesCategoryFragment.newInstance("");
        this.mainTabFragmentList.add(f1);
        this.mainTabFragmentList.add(f2);
        return this.mainTabFragmentList;
    }

    public List<Fragment> getViewAllTabThemeFragments(boolean reuseable) {
        ArrayList<CategoryItem> categoryList = ThemeManager.from(this.mContext).getThemeCategoryItemList(this.mContext);
        if (categoryList == null) {
            return null;
        }
        if (reuseable && !InputMethods.isLocaleChanged() && !this.viewAllTabFragmentList.isEmpty() && categoryList.size() == this.viewAllTabFragmentList.size()) {
            return this.viewAllTabFragmentList;
        }
        this.viewAllTabFragmentList.clear();
        Iterator<CategoryItem> it = categoryList.iterator();
        while (it.hasNext()) {
            CategoryItem item = it.next();
            this.viewAllTabFragmentList.add(ThemesFragment.newInstance(item.categoryId, ThemesFragment.FRAGMENT_SOURCE.VIEW_ALL_THEMES));
        }
        return this.viewAllTabFragmentList;
    }

    public void localeChanged() {
        this.viewAllTabFragmentList.clear();
    }

    public List<Fragment> getMyThemesFragment(ThemeTabHostManager.REFRESH_SOURCE source) {
        if (!this.mainTabFragmentList.isEmpty() && source != ThemeTabHostManager.REFRESH_SOURCE.INIT) {
            return this.mainTabFragmentList;
        }
        this.mainTabFragmentList.clear();
        this.mainTabFragmentList.add(MyThemesFragment.createInstance(ThemeManager.MY_THEMES));
        return this.mainTabFragmentList;
    }

    public static Fragment getGetThemesFragment() {
        return ThemesCategoryFragment.newInstance("");
    }

    public void ShowViewAllTabFragments(String category, FragmentActivity store) {
        ((SettingsV11) store).getTabHostManager().showViewAllTabsList(category);
    }

    public void ShowBundleFragments(String category, String bundlSku, int source, FragmentActivity store) {
        UsageData.recordScreenVisited(UsageData.Screen.BUNDLE_PREVIEW);
        ThemeTabHostManager tabHostManager = ((SettingsV11) store).getTabHostManager();
        tabHostManager.hideTabHost();
        ((SettingsV11) store).getActionbarManager().hideActionBar();
        tabHostManager.getCurrentFragmentInfo().initialize(ThemesFragment.FRAGMENT_SOURCE.BUNDLE_THEMES.ordinal(), category, bundlSku);
        if (source != ThemesFragment.FRAGMENT_SOURCE.FRAGMENT_NONE.ordinal()) {
            tabHostManager.getCurrentFragmentInfo().setSourceFragment(source);
        }
        initializeBundleFragment(category, bundlSku);
        FragmentTransaction ft = store.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, this.bundleFragment);
        ft.commit();
        ((SettingsV11) store).setCurrentFragment(this.bundleFragment);
    }

    public void initializeBundleFragment(String category, String bundleSku) {
        this.bundleFragment = BundleThemeFragment.createInstance(category, bundleSku);
    }

    /* loaded from: classes.dex */
    public static class MyThemesFragment extends ThemesFragment {
        public static final MyThemesFragment createInstance(String arg) {
            MyThemesFragment myThemes = new MyThemesFragment();
            Bundle args = new Bundle();
            args.putString("arg", arg);
            args.putInt(ShareConstants.FEED_SOURCE_PARAM, ThemesFragment.FRAGMENT_SOURCE.MY_THEMES.ordinal());
            myThemes.setArguments(args);
            return myThemes;
        }
    }

    /* loaded from: classes.dex */
    public static class ViewAllTabThemeFragment extends ThemesFragment {
        public static final ViewAllTabThemeFragment createInstance(String arg) {
            ViewAllTabThemeFragment tabThemes = new ViewAllTabThemeFragment();
            Bundle args = new Bundle();
            args.putString("arg", arg);
            args.putInt(ShareConstants.FEED_SOURCE_PARAM, ThemesFragment.FRAGMENT_SOURCE.VIEW_ALL_THEMES.ordinal());
            tabThemes.setArguments(args);
            return tabThemes;
        }
    }

    /* loaded from: classes.dex */
    public static class BundleThemeFragment extends ThemesFragment {
        public static final BundleThemeFragment createInstance(String category, String bundleSku) {
            BundleThemeFragment bundleThemes = new BundleThemeFragment();
            Bundle args = new Bundle();
            args.putString("arg", category);
            args.putString("bundle_sku", bundleSku);
            args.putBoolean("isBundle", true);
            args.putInt(ShareConstants.FEED_SOURCE_PARAM, ThemesFragment.FRAGMENT_SOURCE.BUNDLE_THEMES.ordinal());
            bundleThemes.setArguments(args);
            return bundleThemes;
        }
    }

    public static final void destroyInstance() {
        if (SettingsV11.INSTANCE_COUNT.intValue() == 0 && controller != null) {
            controller.releaseTabsFragment();
            controller = null;
        }
    }

    private void releaseTabsFragment() {
        if (this.mainTabFragmentList != null) {
            this.mainTabFragmentList.clear();
            this.mainTabFragmentList = null;
        }
        if (this.viewAllTabFragmentList != null) {
            this.viewAllTabFragmentList.clear();
            this.viewAllTabFragmentList = null;
        }
    }
}
