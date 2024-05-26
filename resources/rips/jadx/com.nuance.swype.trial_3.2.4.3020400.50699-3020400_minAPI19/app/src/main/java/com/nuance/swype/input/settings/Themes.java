package com.nuance.swype.input.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nuance.android.util.ImageCache;
import com.nuance.android.util.ThemedResources;
import com.nuance.connect.api.CatalogService;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.inapp.CatalogManager;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.PlatformUtil;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.settings.ThemesFragment;
import com.nuance.swype.service.impl.AccountUtil;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.StringUtils;
import com.nuance.swype.util.drawable.ImageViewWrapper;
import com.nuance.swype.util.storage.ThemeData;
import com.nuance.swype.util.storage.ThemeItemSeed;
import com.nuance.swype.widget.directional.DirectionalRelativeLayout;
import com.nuance.swypeconnect.ac.ACException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public abstract class Themes implements ThemeManager.NotifyObserverDataChanged {
    private static DrawableRequestBuilder<ThemeManager.SwypeTheme> fullRequest;
    private static DrawableRequestBuilder<ThemeManager.SwypeTheme> thumbRequest;
    private ThemeListAdapter adapter;
    private final CatalogManager catalogManager;
    private final int columnWidth;
    private final Context context;
    private final int fragmentSource;
    private TextView mBundleDesc;
    private Button mBundleStatusButton;
    private TextView mBundleTitle;
    private long mLastClickedTime;
    private final ThemeManager themeManager;
    protected static final LogManager.Log log = LogManager.getLog("Themes");
    private static final long ITEM_TAP_WAIT_THRESHOLD = ViewConfiguration.getDoubleTapTimeout() * 2;

    protected abstract void navigateBackFromBundlePage();

    protected abstract void restorePurchases();

    protected abstract void showApplyThemeDialog(int i, String str, String str2);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void showBundleView(String str, String str2);

    protected abstract void showGoogleAccountErrorDialog();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void showInstallThemeDialog(int i, String str, String str2);

    protected abstract void showPurchaseFlowDialog(int i, String str, String str2);

    protected abstract void showUninstallThemeDialog(int i, String str, String str2);

    protected abstract void uninstallThemes();

    /* JADX INFO: Access modifiers changed from: protected */
    public Themes(Context context, String category, String bundleSku, int source, boolean isBundle) {
        this.context = context;
        this.themeManager = ThemeManager.from(context);
        if (!ThemeManager.isDownloadableThemesEnabled()) {
            this.themeManager.setCurrentCategoryId(ThemeManager.STR_NO_CATEGORY_ID);
        }
        this.fragmentSource = source;
        IMEApplication app = IMEApplication.from(context);
        this.catalogManager = app.getCatalogManager();
        Resources resources = context.getResources();
        this.columnWidth = resources.getDimensionPixelSize(R.dimen.preference_theme_column_width);
        PlatformUtil util = IMEApplication.from(context).getPlatformUtil();
        if (source == ThemesFragment.FRAGMENT_SOURCE.MY_THEMES.ordinal()) {
            this.themeManager.getThemeDataManager();
            if (ThemeData.isCacheEmpty() && this.catalogManager.hasStoredSkuList() && !util.checkForDataConnection()) {
                this.catalogManager.refreshOfflineCatalogItemsWhenNetworkOff();
            }
        }
    }

    public int getColumnWidth() {
        return this.columnWidth;
    }

    public void setCurrentTheme() {
        if (this.adapter != null) {
            this.adapter.setCurrentTheme(UserPreferences.from(this.context).getCurrentThemeId());
            updateAdapter();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, boolean isBundle, String categoryId, String bundleSku) {
        PlatformUtil util = IMEApplication.from(this.context).getPlatformUtil();
        if (this.fragmentSource == ThemesFragment.FRAGMENT_SOURCE.MY_THEMES.ordinal()) {
            this.themeManager.getThemeDataManager();
            if (ThemeData.isCacheEmpty() && !util.checkForDataConnection()) {
                if (!this.catalogManager.hasStoredSkuList()) {
                    categoryId = ThemeManager.STR_NO_CATEGORY_ID;
                } else {
                    categoryId = ThemeManager.MY_THEMES;
                }
            }
        }
        if (isBundle) {
            this.adapter = new ThemeListAdapter(this.themeManager.getBundleThemes(categoryId, bundleSku), this, this.context, this.columnWidth, this.context.getResources().getDimensionPixelSize(R.dimen.category_theme_column_height), this.fragmentSource);
        } else {
            this.adapter = new ThemeListAdapter(this.themeManager.getCategoryThemes(this.context, categoryId), this, this.context, this.columnWidth, this.context.getResources().getDimensionPixelSize(R.dimen.category_theme_column_height), this.fragmentSource);
        }
        fullRequest = Glide.with(this.context).loadGeneric(ThemeManager.SwypeTheme.class).placeholder(R.drawable.custom_progressbar_indeterminate).centerCrop();
        thumbRequest = Glide.with(this.context).loadGeneric(ThemeManager.SwypeTheme.class).diskCacheStrategy(DiskCacheStrategy.RESULT).override(this.columnWidth, this.context.getResources().getDimensionPixelSize(R.dimen.category_theme_column_height));
        int highlightColor = this.context.getResources().getColor(R.color.preference_theme_highlight);
        this.adapter.setHighlightColor(highlightColor);
        this.adapter.setCurrentTheme(UserPreferences.from(this.context).getCurrentThemeId());
        if (isBundle) {
            View bundleGridView = inflater.inflate(R.layout.theme_bundle_grid_view, container, false);
            bundleGridView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            this.mBundleTitle = (TextView) bundleGridView.findViewById(R.id.bundle_title);
            this.mBundleTitle.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.Themes.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    Themes.this.navigateBackFromBundlePage();
                }
            });
            final ThemeItemSeed seed = IMEApplication.from(this.context).getCatalogManager().getThemeSeed(categoryId, bundleSku);
            if (seed != null) {
                this.mBundleTitle.setText(seed.themeName);
                this.mBundleTitle.setVisibility(0);
                this.mBundleDesc = (TextView) bundleGridView.findViewById(R.id.bundle_description);
                this.mBundleDesc.setText(seed.bundleDesc);
                this.mBundleDesc.setVisibility(0);
                this.mBundleStatusButton = (Button) bundleGridView.findViewById(R.id.bundle_status_button);
                Resources res = this.context.getResources();
                if (seed.isFree) {
                    this.mBundleStatusButton.setText(res.getString(R.string.bunlde_get_all_for_free));
                } else if (seed.isPurchased) {
                    this.mBundleStatusButton.setText(res.getString(R.string.bundle_purchased));
                } else if (seed.isPurchasable) {
                    String buyAllBundle = String.format(res.getString(R.string.bundle_buy_all), seed.price);
                    this.mBundleStatusButton.setText(buyAllBundle);
                }
                this.mBundleStatusButton.setVisibility(0);
                this.mBundleStatusButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.Themes.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        if (seed.isPurchasable) {
                            Themes.this.showPurchaseFlowDialog(0, seed.sku, seed.categoryKey);
                        }
                    }
                });
            }
            ((ImageView) bundleGridView.findViewById(R.id.settings_back)).setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.Themes.3
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    Themes.this.navigateBackFromBundlePage();
                }
            });
            GridView gridView = (GridView) bundleGridView.findViewById(R.id.theme_grid_view);
            log.d("onCreateView...setColumnWidth: ", Integer.valueOf(this.columnWidth));
            gridView.setColumnWidth(this.columnWidth);
            gridView.setNumColumns(-1);
            gridView.setVerticalFadingEdgeEnabled(false);
            gridView.setAdapter((ListAdapter) this.adapter);
            this.adapter.setGridView(gridView);
            gridView.setBackgroundColor(ThemesCategory.theme_color);
            int padding = this.context.getResources().getDimensionPixelSize(R.dimen.preference_theme_preview_padding);
            gridView.setVerticalSpacing(padding);
            gridView.setHorizontalSpacing(padding);
            gridView.setPadding(padding, padding, padding, padding);
            return bundleGridView;
        }
        View themesGridView = inflater.inflate(R.layout.theme_grid_view, container, false);
        int padding2 = this.context.getResources().getDimensionPixelSize(R.dimen.preference_theme_preview_padding);
        GridView gridView2 = (GridView) themesGridView.findViewById(R.id.theme_grid_view);
        log.d("onCreateView...setColumnWidth: ", Integer.valueOf(this.columnWidth));
        gridView2.setColumnWidth(this.columnWidth);
        gridView2.setNumColumns(-1);
        gridView2.setVerticalFadingEdgeEnabled(false);
        gridView2.setAdapter((ListAdapter) this.adapter);
        this.adapter.setGridView(gridView2);
        gridView2.setBackgroundColor(ThemesCategory.theme_color);
        gridView2.setVerticalSpacing(padding2);
        gridView2.setHorizontalSpacing(padding2);
        return themesGridView;
    }

    public void onResume() {
    }

    private void updateAdapter() {
        log.d("Updating adapter");
        this.adapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateStatusInThemesAdapter(String category, String sku, CatalogManager.ThemeStatusChange type, boolean forceUpdateBundleSkus) {
        log.d("updateStatusInThemesAdapter...category:  ", category, "...sku: ", sku, "...type: ", type);
        if (this.adapter != null && this.adapter.updateStatusInThemesAdapter(category, sku, type, forceUpdateBundleSkus)) {
            log.d("Updated status, sku:", sku, " successfully");
        }
    }

    @Override // com.nuance.swype.input.ThemeManager.NotifyObserverDataChanged
    public boolean onDataChanged() {
        this.adapter.notifyDataSetChanged();
        return true;
    }

    public void updateBundleThemes(String bundleId) {
        CatalogManager mCatalogManager = IMEApplication.from(this.context).getCatalogManager();
        if (mCatalogManager != null) {
            ThemeItemSeed seed = mCatalogManager.getThemeSeed(null, bundleId);
            if (this.mBundleTitle != null) {
                this.mBundleTitle.setText(seed.themeName);
            }
            Resources res = this.context.getResources();
            if (this.mBundleDesc != null) {
                this.mBundleDesc.setText(res.getString(R.string.choose_your_theme_desc, seed.bundleDesc));
            }
            if (this.mBundleStatusButton != null) {
                if (seed.isFree) {
                    this.mBundleStatusButton.setText(res.getString(R.string.bunlde_get_all_for_free));
                } else if (seed.isPurchased) {
                    this.mBundleStatusButton.setText(res.getString(R.string.bundle_purchased));
                }
            }
        }
    }

    public void updateThemes(String category) {
        this.adapter.setThemes(this.themeManager.getCategoryThemes(this.context, category), this.context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clickOnPreviewItem(ViewGroup parent, ThemeManager.SwypeTheme theme, int position) {
        String key;
        ThemeItemSeed seed;
        if (!isTapTooQuick()) {
            this.mLastClickedTime = SystemClock.elapsedRealtime();
            if (parent != null) {
                String arg = theme.getSku();
                String currentTheme = IMEApplication.from(this.context).getCurrentThemeId();
                if (currentTheme != null && currentTheme.endsWith(".apk") && currentTheme.contains("/")) {
                    String[] themePath = currentTheme.split("/");
                    if (themePath.length > 2) {
                        currentTheme = themePath[themePath.length - 2];
                    }
                }
                if (!arg.equals(currentTheme)) {
                    if (theme instanceof ThemeManager.ConnectDownloadableThemeWrapper) {
                        if (((ThemeManager.ConnectDownloadableThemeWrapper) theme).getType() == CatalogService.CatalogItem.Type.BUNDLE.ordinal()) {
                            showBundleView(((ThemeManager.ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().categoryKey, theme.getSku());
                            return;
                        } else if (!((ThemeManager.ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().isFree && !((ThemeManager.ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().isInstalled && (AccountUtil.isGoogleAccountMissing() || !AccountUtil.isGoogleAccountSignedIn())) {
                            showGoogleAccountErrorDialog();
                            return;
                        }
                    }
                    if (theme.getCurrentCategoryLabel().equals(ThemeManager.STR_NO_CATEGORY_ID)) {
                        showApplyThemeDialog(position, arg, ThemeManager.STR_NO_CATEGORY_ID);
                        return;
                    }
                    CatalogManager mCatalogManager = IMEApplication.from(this.context).getCatalogManager();
                    if (mCatalogManager != null && (seed = mCatalogManager.getThemeSeed((key = ((ThemeManager.ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().categoryKey), theme.getSku())) != null) {
                        if (seed.isInstalled) {
                            showUninstallThemeDialog(position, arg, key);
                            return;
                        }
                        if (seed.isFree || seed.isPurchased) {
                            if (!seed.isInstalling) {
                                showInstallThemeDialog(position, arg, key);
                                return;
                            }
                            return;
                        }
                        showPurchaseFlowDialog(position, arg, key);
                    }
                }
            }
        }
    }

    private boolean isTapTooQuick() {
        return SystemClock.elapsedRealtime() - this.mLastClickedTime < ITEM_TAP_WAIT_THRESHOLD;
    }

    public boolean checkNetworkConnection(Context ctx) {
        if (IMEApplication.from(ctx).getPlatformUtil().checkForDataConnection()) {
            return true;
        }
        showNoNetworkDialog(ctx);
        return false;
    }

    private void showNoNetworkDialog(Context context) {
        new AlertDialog.Builder(context).setTitle(R.string.no_network_available).setMessage(R.string.no_network_try_again_msg).setNegativeButton(R.string.dismiss_button, (DialogInterface.OnClickListener) null).create().show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyTheme(ThemeManager.SwypeTheme theme, String themeId, Context context) {
        ThemeManager themeManager = IMEApplication.from(context).getThemeManager();
        String oldSku = IMEApplication.from(context).getCurrentThemeId();
        if (StringUtils.isApkCompletePath(oldSku)) {
            String[] themePath = oldSku.split("/");
            if (themePath.length > 2) {
                oldSku = themePath[themePath.length - 2];
            }
        }
        if (oldSku != null && !oldSku.equals(themeId)) {
            StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(context);
            if (sessionScribe != null) {
                sessionScribe.recordSettingsChange("Change keyboard theme", theme.getSku(), XMLResultsHandler.SEP_SPACE + UserPreferences.from(context).getCurrentThemeId());
            }
            log.d("Themes::applyTheme Id:" + themeId);
            if (theme.getSource() == ThemeManager.SwypeTheme.THEME_SOURCE.CONNECT) {
                log.d("Theme is a downloaded theme");
                UserPreferences.from(context).setActiveDownloadedTheme(themeId);
                String themeApkFile = IMEApplication.from(context).getThemeApkPath(themeId);
                UserPreferences.from(context).setCurrentThemeId(themeApkFile);
            } else {
                log.d("Theme is a embedded theme");
                UserPreferences.from(context).setCurrentThemeId(themeId);
                UserPreferences.from(context).setActiveDownloadedTheme("");
            }
            if (!ThemeManager.isDownloadableThemesEnabled()) {
                themeManager.updateThemesOrderIfOem(theme, context.getResources());
            }
            themeManager.onThemeChanged(oldSku, themeId, context);
            ThemedResources.onThemeChanged();
            this.adapter.setCurrentTheme(themeId);
        }
    }

    public void onDestroy() {
        log.d("Recreating theme manager");
        if (this.adapter != null) {
            this.adapter.setGridView(null);
            this.adapter.setDelegate(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getThemeCount() {
        if (this.adapter != null) {
            return this.adapter.getCount();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ThemeListAdapter extends BaseAdapter {
        private Context context;
        private String currentName;
        private ColorStateList defaultTextColors;
        private Themes delegate;
        private GridView gridView;
        private boolean hasDownloadableThem;
        private int highlightColor = -16776961;
        private final List<String> names = new ArrayList();
        private final int previewImageHeight;
        private final int previewImageWidth;
        private List<ThemeManager.SwypeTheme> themes;

        public ThemeListAdapter(List<ThemeManager.SwypeTheme> themes, Themes delegate, Context context, int previewImageWidth, int previewImageHeight, int source) {
            this.context = context;
            setThemes(themes, context);
            this.previewImageWidth = previewImageWidth;
            this.previewImageHeight = previewImageHeight;
            this.delegate = delegate;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.themes.size();
        }

        public void setThemes(List<ThemeManager.SwypeTheme> themes, Context context) {
            this.themes = themes;
            this.names.clear();
            Resources res = context.getResources();
            for (ThemeManager.SwypeTheme theme : themes) {
                this.names.add(theme.getDisplayName(res));
            }
            this.hasDownloadableThem = hasDownloadableTheme();
            notifyDataSetChanged();
        }

        void setGridView(GridView gridView) {
            if (gridView == null && this.gridView != null) {
                this.gridView.setAdapter((ListAdapter) null);
            }
            this.gridView = gridView;
        }

        void setDelegate(Themes delegate) {
            this.delegate = delegate;
        }

        ThemeManager.SwypeTheme getTheme(String sku) {
            for (ThemeManager.SwypeTheme item : this.themes) {
                if (item.getSku().equals(sku)) {
                    return item;
                }
            }
            return null;
        }

        public void updateBundleStatusInThemesAdapter(String category, CatalogManager.ThemeStatusChange type) {
            for (ThemeManager.SwypeTheme item : this.themes) {
                ThemeManager.ConnectDownloadableThemeWrapper theme = (ThemeManager.ConnectDownloadableThemeWrapper) item;
                if (type == CatalogManager.ThemeStatusChange.THEME_PURCHASED) {
                    if (!theme.getThemeItemSeed().isPurchased && !theme.getThemeItemSeed().isFree) {
                        ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().isPurchased = true;
                    }
                } else if (type == CatalogManager.ThemeStatusChange.THEME_INSTALLING) {
                    ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalling(true);
                    updateThemeStatus(item.getSku());
                }
            }
        }

        public boolean updateStatusInThemesAdapter(String category, String sku, CatalogManager.ThemeStatusChange type, boolean forceUpdateBundleSkus) {
            boolean updateStatusDone = false;
            for (ThemeManager.SwypeTheme item : this.themes) {
                if (forceUpdateBundleSkus || (item.getSku() != null && item.getSku().equals(sku))) {
                    if (item instanceof ThemeManager.ConnectDownloadableThemeWrapper) {
                        if (type == CatalogManager.ThemeStatusChange.THEME_INSTALLED) {
                            ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalled(true);
                        } else if (type != CatalogManager.ThemeStatusChange.THEME_PURCHASED) {
                            if (type == CatalogManager.ThemeStatusChange.THEME_INSTALLING) {
                                ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalling(true);
                            } else if (type == CatalogManager.ThemeStatusChange.THEME_INSTALL_CANCELED) {
                                ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalling(false);
                                ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalled(false);
                            } else if (type == CatalogManager.ThemeStatusChange.THEME_UNINSTALLED) {
                                ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalled(false);
                            }
                        } else {
                            ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().isPurchased = true;
                        }
                        updateThemeStatus(item.getSku());
                        updateStatusDone = true;
                        if (!forceUpdateBundleSkus) {
                            break;
                        }
                    } else {
                        continue;
                    }
                }
            }
            return updateStatusDone;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return this.themes.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        public int getHighlightColor() {
            return this.highlightColor;
        }

        public void setHighlightColor(int color) {
            this.highlightColor = color;
            notifyDataSetChanged();
        }

        public void setCurrentTheme(String themeName) {
            if (themeName != null) {
                if (themeName.endsWith(".apk") && themeName.contains("/")) {
                    String[] themePath = themeName.split("/");
                    if (themePath.length > 2) {
                        themeName = themePath[themePath.length - 2];
                    }
                }
                String old = this.currentName;
                this.currentName = themeName;
                updateThemeStatus(this.currentName);
                updateThemeStatus(old);
            }
        }

        void updateThemeStatus(String sku) {
            MyThemeStatus myThemeStatus;
            ThemeManager.SwypeTheme theme;
            if (this.gridView != null) {
                int visibleViews = (this.gridView.getLastVisiblePosition() - this.gridView.getFirstVisiblePosition()) + 1;
                for (int i = 0; i < visibleViews; i++) {
                    View view = this.gridView.getChildAt(i);
                    if (view != null && (myThemeStatus = (MyThemeStatus) view.getTag()) != null && (theme = getTheme(myThemeStatus.sku)) != null && myThemeStatus.sku.equals(sku)) {
                        myThemeStatus.updateStatus(this.context, view, theme, sku.equals(this.currentName));
                        return;
                    }
                }
            }
        }

        private boolean hasDownloadableTheme() {
            Iterator<ThemeManager.SwypeTheme> it = this.themes.iterator();
            while (it.hasNext()) {
                if (it.next().getSource() == ThemeManager.SwypeTheme.THEME_SOURCE.CONNECT) {
                    return true;
                }
            }
            return false;
        }

        @Override // android.widget.Adapter
        @SuppressLint({"InflateParams"})
        public View getView(final int position, View convertView, final ViewGroup parent) {
            Themes.log.d("getView...position: ", Integer.valueOf(position), "...convertView: ", convertView, "...parent: ", parent);
            LayoutInflater inflater = LayoutInflater.from(parent.getContext().getApplicationContext());
            final ThemeManager.SwypeTheme theme = this.themes.get(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.theme_keyboard_preview_download, (ViewGroup) null);
                DirectionalRelativeLayout themeTile = (DirectionalRelativeLayout) convertView.findViewById(R.id.theme_grid_preview);
                ImageViewWrapper image = (ImageViewWrapper) convertView.findViewById(R.id.download_image_preview);
                TextView name = (TextView) convertView.findViewById(R.id.theme_name);
                if (this.defaultTextColors == null) {
                    this.defaultTextColors = name.getTextColors();
                }
                ImageView statusIcon = (ImageView) convertView.findViewById(R.id.theme_status_icon);
                TextView priceView = (TextView) convertView.findViewById(R.id.theme_grid_price);
                LinearLayout progressRow = (LinearLayout) convertView.findViewById(R.id.progressbar_grid_row);
                ProgressBar pBar = (ProgressBar) convertView.findViewById(R.id.progressbar_grid_download);
                ImageView cancel = (ImageView) convertView.findViewById(R.id.cancel_grid_download);
                convertView.setTag(new MyThemeStatus(themeTile, image, name, statusIcon, priceView, progressRow, pBar, cancel, this.defaultTextColors));
            }
            MyThemeStatus myThemeStatus = (MyThemeStatus) convertView.getTag();
            if (myThemeStatus.downloadImage != null) {
                myThemeStatus.downloadImage.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.Themes.ThemeListAdapter.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        ThemeListAdapter.this.delegate.clickOnPreviewItem(parent, theme, position);
                    }
                });
            }
            if (myThemeStatus.tile != null) {
                myThemeStatus.tile.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.Themes.ThemeListAdapter.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        ThemeListAdapter.this.delegate.clickOnPreviewItem(parent, theme, position);
                    }
                });
            }
            myThemeStatus.cancel.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.Themes.ThemeListAdapter.3
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    CatalogManager catalogManager = IMEApplication.from(parent.getContext().getApplicationContext()).getCatalogManager();
                    if (catalogManager != null) {
                        try {
                            Themes.log.d("progress bar cancel button pressed");
                            String categoryKey = ((ThemeManager.ConnectDownloadableThemeWrapper) ThemeListAdapter.this.themes.get(position)).getThemeItemSeed().categoryKey;
                            catalogManager.cancelDownloadTheme(position, categoryKey, ((ThemeManager.SwypeTheme) ThemeListAdapter.this.themes.get(position)).getSku());
                        } catch (ACException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            myThemeStatus.setSku(theme.getSku());
            myThemeStatus.setThemeName(this.names.get(position));
            myThemeStatus.initProgressBar();
            myThemeStatus.setHasDownloadableThem(this.hasDownloadableThem);
            if (myThemeStatus.downloadImage != null) {
                if (theme.getSource() == ThemeManager.SwypeTheme.THEME_SOURCE.CONNECT) {
                    ThemeManager.ConnectDownloadableThemeWrapper connectTheme = (ThemeManager.ConnectDownloadableThemeWrapper) theme;
                    myThemeStatus.downloadImage.setWrapperImageWidth(this.previewImageWidth);
                    myThemeStatus.downloadImage.setWrapperImageHeight(this.previewImageHeight);
                    ImageCache.with(this.context).loadImage(connectTheme.getThumbnailUrl(), R.drawable.custom_progressbar_indeterminate, myThemeStatus.downloadImage);
                } else {
                    myThemeStatus.downloadImage.setTag(R.id.drawable_downloader, null);
                    ImageCache with = ImageCache.with(this.context);
                    theme.getPreviewResId();
                    with.loadImage(theme.getPreviewResId(), R.drawable.custom_progressbar_indeterminate, myThemeStatus.downloadImage);
                }
                myThemeStatus.downloadImage.setContentDescription(this.names.get(position));
            }
            myThemeStatus.updateStatus(parent.getContext().getApplicationContext(), convertView, theme, theme.getSku().equals(this.currentName));
            return convertView;
        }

        public List<ThemeManager.SwypeTheme> getPreloadItems(int position) {
            return this.themes.subList(position, this.themes.size());
        }

        public GenericRequestBuilder getPreloadRequestBuilder(ThemeManager.SwypeTheme item) {
            return Themes.fullRequest.thumbnail(Themes.thumbRequest.load((DrawableRequestBuilder) item)).load((DrawableRequestBuilder) item);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class MyThemeStatus {
            public final ImageView cancel;
            public final ColorStateList defaultTextColors;
            public final ImageViewWrapper downloadImage;
            public boolean hasDownloadableTheme;
            public final TextView name;
            public final TextView price;
            public final ProgressBar progressBar;
            public final LinearLayout progressBarRow;
            public String sku;
            public final ImageView statusIcon;
            public final DirectionalRelativeLayout tile;

            private MyThemeStatus(DirectionalRelativeLayout tile, ImageViewWrapper v, TextView name, ImageView statusIcon, TextView price, LinearLayout progressBarRow, ProgressBar progressBar, ImageView cancel, ColorStateList defaultTextColors) {
                this.tile = tile;
                this.downloadImage = v;
                this.name = name;
                this.statusIcon = statusIcon;
                this.price = price;
                this.progressBarRow = progressBarRow;
                this.progressBar = progressBar;
                this.cancel = cancel;
                this.defaultTextColors = defaultTextColors;
            }

            void setSku(String sku) {
                this.sku = sku;
            }

            void setHasDownloadableThem(boolean hasDownloadableTheme) {
                this.hasDownloadableTheme = hasDownloadableTheme;
            }

            void setThemeName(String themeName) {
                this.name.setText(themeName);
            }

            void initProgressBar() {
                if (this.progressBar != null) {
                    this.progressBar.setMax(100);
                    this.progressBar.setProgress(0);
                }
            }

            void updateStatus(Context context, View convertView, ThemeManager.SwypeTheme theme, boolean isCurrent) {
                Themes.log.d("updateStatus: ", theme.getSku());
                if (theme.getSource() == ThemeManager.SwypeTheme.THEME_SOURCE.CONNECT) {
                    ThemeManager.ConnectDownloadableThemeWrapper connectTheme = (ThemeManager.ConnectDownloadableThemeWrapper) theme;
                    int statusIconId = connectTheme.getStatusIcon(context);
                    if (connectTheme.getThemeItemSeed() != null && connectTheme.getThemeItemSeed().isInstalling) {
                        this.progressBarRow.setVisibility(0);
                        this.price.setVisibility(8);
                        this.statusIcon.setVisibility(8);
                        if (connectTheme.getThemeItemSeed().installingPercentage > 0) {
                            this.progressBar.setIndeterminate(false);
                            this.progressBar.setProgress(connectTheme.getThemeItemSeed().installingPercentage);
                        } else {
                            this.progressBar.setIndeterminate(true);
                        }
                        Themes.log.d("updateStatus: ", theme.getSku(), " installing: ", Integer.valueOf(connectTheme.getThemeItemSeed().installingPercentage), "%");
                    } else if (statusIconId != 0) {
                        this.statusIcon.setVisibility(0);
                        this.progressBarRow.setVisibility(4);
                        this.price.setVisibility(8);
                        this.statusIcon.setBackgroundResource(statusIconId);
                    } else {
                        this.price.setVisibility(0);
                        this.statusIcon.setVisibility(8);
                        this.progressBarRow.setVisibility(4);
                        this.price.setText(connectTheme.getStatusText(context));
                    }
                } else {
                    this.progressBarRow.setVisibility(8);
                    this.price.setVisibility(8);
                    if (this.hasDownloadableTheme) {
                        this.statusIcon.setVisibility(4);
                    } else {
                        this.statusIcon.setVisibility(8);
                    }
                }
                convertView.setBackgroundResource(R.drawable.settings_theme_background);
                this.name.setTextColor(-16777216);
                if (isCurrent) {
                    convertView.setClickable(true);
                    convertView.setBackgroundResource(R.drawable.settings_theme_background_current);
                    this.name.setTextColor(-16777216);
                    if (theme.getSource() == ThemeManager.SwypeTheme.THEME_SOURCE.CONNECT) {
                        this.statusIcon.setVisibility(0);
                        this.statusIcon.setBackgroundResource(((ThemeManager.ConnectDownloadableThemeWrapper) theme).getStatusIcon(context));
                        return;
                    }
                    return;
                }
                if (!ThemeManager.isDownloadableThemesEnabled()) {
                    this.name.setTextColor(this.defaultTextColors);
                }
            }
        }
    }
}
