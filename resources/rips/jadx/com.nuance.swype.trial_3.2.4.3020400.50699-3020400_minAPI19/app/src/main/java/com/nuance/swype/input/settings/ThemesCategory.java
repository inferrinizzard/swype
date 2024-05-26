package com.nuance.swype.input.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.nuance.swype.inapp.CategoryItem;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.PlatformUtil;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.service.impl.AccountUtil;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.StringUtils;
import com.nuance.swype.util.drawable.ImageViewWrapper;
import com.nuance.swype.util.storage.ThemeData;
import com.nuance.swype.util.storage.ThemeItemSeed;
import com.nuance.swype.widget.directional.DirectionalUtil;
import com.nuance.swypeconnect.ac.ACException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class ThemesCategory implements AdapterView.OnItemClickListener, ThemeManager.NotifyObserverDataChanged {
    private static DrawableRequestBuilder<ThemeManager.SwypeTheme> fullRequest = null;
    protected static final LogManager.Log log = LogManager.getLog("ThemesCategory");
    public static final int theme_color = -1184275;
    private static DrawableRequestBuilder<ThemeManager.SwypeTheme> thumbRequest;
    private ListView categoryListView;
    private View categoryView;
    private final Context context;
    private View progressView;
    private ThemeCategoryAdapter themeCategoryAdapter;
    private final ThemeManager themeManager;

    protected abstract void restorePurchases();

    protected abstract void showApplyThemeDialog(int i, String str, String str2);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void showBundleView(String str, String str2);

    protected abstract void showGoogleAccountErrorDialog();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void showInstallThemeDialog(int i, String str, String str2);

    protected abstract void showMoreThemes(String str);

    protected abstract void showPurchaseFlowDialog(int i, String str, String str2);

    protected abstract void showUninstallThemeDialog(int i, String str, String str2);

    protected abstract void uninstallThemes();

    /* JADX INFO: Access modifiers changed from: protected */
    public ThemesCategory(Context context) {
        this.context = context;
        this.themeManager = ThemeManager.from(context);
        HorizontalListView.setHorizontalRTL(DirectionalUtil.isCurrentlyRtl());
    }

    public View onCreateCategoryListView(LayoutInflater inflater, ViewGroup container) {
        PlatformUtil util = IMEApplication.from(this.context).getPlatformUtil();
        this.themeManager.getThemeDataManager();
        if (ThemeData.isCacheEmpty() && !util.checkForDataConnection()) {
            this.categoryView = inflater.inflate(R.layout.theme_main_page, container, false);
            this.categoryView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            View v = this.categoryView.findViewById(R.id.theme_no_data_page);
            v.setBackgroundColor(theme_color);
            v.setVisibility(0);
        } else {
            fullRequest = Glide.with(this.context).loadGeneric(ThemeManager.SwypeTheme.class).placeholder(R.drawable.custom_progressbar_indeterminate).centerCrop();
            thumbRequest = Glide.with(this.context).loadGeneric(ThemeManager.SwypeTheme.class).diskCacheStrategy(DiskCacheStrategy.RESULT).override(this.context.getResources().getDimensionPixelSize(R.dimen.category_theme_column_width), this.context.getResources().getDimensionPixelSize(R.dimen.category_theme_column_height));
            this.categoryView = inflater.inflate(R.layout.theme_main_page, container, false);
            this.categoryView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            this.categoryListView = (ListView) this.categoryView.findViewById(R.id.theme_category_list);
            this.categoryListView.setVerticalFadingEdgeEnabled(false);
            this.categoryListView.setOnItemClickListener(this);
            this.progressView = this.categoryView.findViewById(R.id.no_data_progress_layout);
            this.categoryView.findViewById(R.id.theme_main_page_lineLayout).setBackgroundColor(theme_color);
            this.themeManager.getThemeDataManager();
            if (ThemeData.isCacheEmpty()) {
                refreshUI(true);
            } else {
                this.themeCategoryAdapter = new ThemeCategoryAdapter(this.context, R.layout.theme_category_item, new ArrayList(this.themeManager.getThemeCategoryItemList(this.context)), this);
                this.categoryListView.setAdapter((ListAdapter) this.themeCategoryAdapter);
                refreshUI(false);
            }
        }
        return this.categoryView;
    }

    private void refreshUI(boolean hideCategoryList) {
        if (hideCategoryList) {
            if (this.categoryListView != null) {
                this.categoryListView.setVisibility(8);
            }
            if (this.progressView != null) {
                this.progressView.setVisibility(0);
                return;
            }
            return;
        }
        if (this.categoryListView != null) {
            this.categoryListView.setVisibility(0);
        }
        if (this.progressView != null) {
            this.progressView.setVisibility(8);
        }
    }

    public void onDestroy() {
        if (this.themeCategoryAdapter != null) {
            this.themeCategoryAdapter.onDestroy();
            this.themeCategoryAdapter = null;
        }
        if (this.categoryListView != null) {
            this.categoryListView.setOnItemClickListener(null);
            this.categoryListView.setAdapter((ListAdapter) null);
            this.categoryListView = null;
        }
    }

    public void updateThemeCategoryAdapter() {
        if (this.themeCategoryAdapter != null) {
            log.d("Updating updateThemeCategoryAdapter");
            this.themeCategoryAdapter.clear();
            this.themeCategoryAdapter.addAll(new ArrayList(this.themeManager.getThemeCategoryItemList(this.context)));
            this.themeCategoryAdapter.notifyDataSetChanged();
        }
    }

    public void notifyThumbAdapterDataSetChanged(String categoryId) {
        this.themeCategoryAdapter.notifyThumbListAdapter(categoryId);
    }

    public void updateAllThumbListAdapter(String sku, String oldSku) {
        if (this.themeCategoryAdapter != null) {
            log.d("Updating updateAllThumbListAdapter...sku: ", sku, "...oldSku: ", oldSku);
            this.themeCategoryAdapter.updateAllThumbListAdapter(sku, oldSku);
        }
    }

    private void applyCurrentTheme(String sku, String oldSku) {
        if (this.themeCategoryAdapter != null) {
            log.d("Updating applyCurrentTheme...sku: ", sku, "...oldSku: ", oldSku);
            this.themeCategoryAdapter.applyCurrentTheme(sku, oldSku);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateStatusInThemesAdapter(String category, String sku, CatalogManager.ThemeStatusChange type) {
        if (this.themeCategoryAdapter != null) {
            log.d("Updating updateStatusInThemesAdapter...category: ", category, "...sku: ", sku, "...status type: ", type);
            this.themeCategoryAdapter.updateStatusInThemesAdapter(category, sku, type);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateStatusInThemesAdapter(String sku, CatalogManager.ThemeStatusChange type) {
        if (this.themeCategoryAdapter != null) {
            log.d("Updating updateStatusInThemesAdapter...sku: ", sku, "...type: ", type);
            this.themeCategoryAdapter.updateStatusInThemesAdapter(sku, type);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateThemeThumbListAdapter(String sku, String categoryId) {
        if (this.themeCategoryAdapter != null) {
            this.themeCategoryAdapter.updateThemeThumbListAdapter(sku, categoryId);
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ThemeManager.SwypeTheme theme = (ThemeManager.SwypeTheme) parent.getItemAtPosition(position);
        log.d("onItemClick...parent: ", parent, "...view: ", view, "...position: ", Integer.valueOf(position), "...id: ", Long.valueOf(id));
        ThemeThumbListAdapter.DownloadableThemeStatus hd = (ThemeThumbListAdapter.DownloadableThemeStatus) view.getTag();
        log.d("onItemClick...view name: ", hd.name.getText());
        String arg = theme.getSku();
        String currentTheme = IMEApplication.from(this.context).getCurrentThemeId();
        if (StringUtils.isApkCompletePath(currentTheme)) {
            String[] themePath = currentTheme.split("/");
            if (themePath.length > 2) {
                currentTheme = themePath[themePath.length - 2];
            }
        }
        if (currentTheme != null && !arg.equals(currentTheme)) {
            if (theme.getCurrentCategoryLabel().equals(ThemeManager.STR_NO_CATEGORY_ID)) {
                showApplyThemeDialog(position, arg, ThemeManager.STR_NO_CATEGORY_ID);
                return;
            }
            if (theme.isConnectTheme()) {
                if (((ThemeManager.ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().type == CatalogService.CatalogItem.Type.BUNDLE.ordinal()) {
                    showBundleView(((ThemeManager.ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().categoryKey, theme.getSku());
                    return;
                } else if (!((ThemeManager.ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().isFree && !((ThemeManager.ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().isInstalled && (AccountUtil.isGoogleAccountMissing() || !AccountUtil.isGoogleAccountSignedIn())) {
                    showGoogleAccountErrorDialog();
                    return;
                }
            }
            CatalogManager mCatalogManager = IMEApplication.from(this.context).getCatalogManager();
            String key = ((ThemeManager.ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().categoryKey;
            if (mCatalogManager != null) {
                ThemeItemSeed seed = mCatalogManager.getThemeSeed(((ThemeManager.ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().categoryKey, theme.getSku());
                log.d("onItemClick...sku: ", theme.getSku(), "...category label: ", theme.getCurrentCategoryLabel());
                if (seed != null) {
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

    @Override // com.nuance.swype.input.ThemeManager.NotifyObserverDataChanged
    public boolean onDataChanged() {
        log.d("onDataChanged");
        updateThemeCategoryAdapter();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyTheme(ThemeManager.SwypeTheme theme, String themeId, Context context) {
        log.d("Applying theme: ", themeId);
        if (themeId.equals("android.test.purchased")) {
            log.d("Cancelled applying theme");
            return;
        }
        String oldSku = IMEApplication.from(context).getCurrentThemeId();
        if (StringUtils.isApkCompletePath(oldSku)) {
            String[] themePath = oldSku.split("/");
            if (themePath.length > 2) {
                oldSku = themePath[themePath.length - 2];
            }
        }
        if (oldSku != null && !oldSku.equals(themeId)) {
            ThemeManager themeManager = IMEApplication.from(context).getThemeManager();
            StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(context);
            if (sessionScribe != null) {
                sessionScribe.recordSettingsChange("Change keyboard theme", theme.getSku(), XMLResultsHandler.SEP_SPACE + UserPreferences.from(context).getCurrentThemeId());
            }
            log.d("ThemesCategory::applyTheme Id:" + themeId);
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
            themeManager.onThemeChanged(oldSku, themeId, context);
            ThemedResources.onThemeChanged();
            applyCurrentTheme(theme.getSku(), oldSku);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ThemeCategoryAdapter extends ArrayAdapter<CategoryItem> {
        private final int resource;
        private final HashMap<String, ThemeThumbListAdapter> themeThumbListAdapterMap;
        private final int themeThumbnailColumnWidth;
        private final WeakReference<ThemesCategory> themesCategoryWeakReference;

        public ThemeCategoryAdapter(Context context, int resourceId, ArrayList<CategoryItem> items, ThemesCategory themesCategoryWeakReference) {
            super(context, resourceId, items);
            this.themeThumbListAdapterMap = new LinkedHashMap();
            this.resource = resourceId;
            this.themesCategoryWeakReference = new WeakReference<>(themesCategoryWeakReference);
            this.themeThumbnailColumnWidth = context.getResources().getDimensionPixelSize(R.dimen.category_theme_column_width);
        }

        void onDestroy() {
            for (Map.Entry<String, ThemeThumbListAdapter> entry : this.themeThumbListAdapterMap.entrySet()) {
                this.themeThumbListAdapterMap.get(entry.getKey()).sethListView(null);
            }
            this.themesCategoryWeakReference.clear();
            this.themeThumbListAdapterMap.clear();
        }

        void updateAllThumbListAdapter(String sku, String oldSku) {
            if (sku == null && oldSku == null) {
                for (Map.Entry<String, ThemeThumbListAdapter> entry : this.themeThumbListAdapterMap.entrySet()) {
                    this.themeThumbListAdapterMap.get(entry.getKey()).notifyDataSetChanged();
                }
            }
        }

        void applyCurrentTheme(String sku, String oldSku) {
            Iterator<Map.Entry<String, ThemeThumbListAdapter>> it = this.themeThumbListAdapterMap.entrySet().iterator();
            while (it.hasNext()) {
                this.themeThumbListAdapterMap.get(it.next().getKey()).setCurrentThemeSku(null);
            }
            for (Map.Entry<String, ThemeThumbListAdapter> entry : this.themeThumbListAdapterMap.entrySet()) {
                for (ThemeManager.SwypeTheme item : this.themeThumbListAdapterMap.get(entry.getKey()).getThemes()) {
                    if (item.getSku().equals(sku) || item.getSku().equals(oldSku)) {
                        this.themeThumbListAdapterMap.get(entry.getKey()).setCurrentThemeSku(sku);
                        break;
                    }
                }
            }
        }

        void updateStatusInThemesAdapter(String category, String sku, CatalogManager.ThemeStatusChange type) {
            if (this.themeThumbListAdapterMap.get(category) != null) {
                for (ThemeManager.SwypeTheme item : this.themeThumbListAdapterMap.get(category).getThemes()) {
                    if (item.getSku().equals(sku)) {
                        if (type == CatalogManager.ThemeStatusChange.THEME_INSTALLED) {
                            ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalled(true);
                        } else if (type != CatalogManager.ThemeStatusChange.THEME_PURCHASED) {
                            if (type == CatalogManager.ThemeStatusChange.THEME_INSTALLING) {
                                ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalling(true);
                            } else if (type == CatalogManager.ThemeStatusChange.THEME_UNINSTALLED) {
                                ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalled(false);
                            } else if (type == CatalogManager.ThemeStatusChange.THEME_INSTALL_CANCELED) {
                                ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalling(false);
                                ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalled(false);
                            }
                        } else {
                            ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().isPurchased = true;
                        }
                    }
                }
            }
        }

        void updateStatusInThemesAdapter(String sku, CatalogManager.ThemeStatusChange type) {
            for (Map.Entry<String, ThemeThumbListAdapter> entry : this.themeThumbListAdapterMap.entrySet()) {
                Iterator<ThemeManager.SwypeTheme> it = this.themeThumbListAdapterMap.get(entry.getKey()).getThemes().iterator();
                while (true) {
                    if (it.hasNext()) {
                        ThemeManager.SwypeTheme item = it.next();
                        if (item.getSku().equals(sku)) {
                            if (type == CatalogManager.ThemeStatusChange.THEME_INSTALLED) {
                                ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().setInstalled(true);
                            } else if (type == CatalogManager.ThemeStatusChange.THEME_PURCHASED) {
                                ((ThemeManager.ConnectDownloadableThemeWrapper) item).getThemeItemSeed().isPurchased = true;
                            }
                        }
                    }
                }
            }
        }

        void updateThemeThumbListAdapter(String sku, String categoryId) {
            ThemeThumbListAdapter adapter = this.themeThumbListAdapterMap.get(categoryId);
            if (adapter != null) {
                adapter.updateThemeStatus(sku);
            }
        }

        public void notifyThumbListAdapter(String categoryId) {
            ThemeThumbListAdapter adapter = this.themeThumbListAdapterMap.get(categoryId);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView;
            ThemesCategory.log.d("ThemeCategoryAdapter...getView...position: ", Integer.valueOf(position), "...convertView: ", convertView);
            if (convertView == null) {
                rowView = LayoutInflater.from(getContext()).inflate(this.resource, (ViewGroup) null);
            } else {
                rowView = convertView;
            }
            TextView categoryTitle = (TextView) rowView.findViewById(R.id.theme_category_title);
            categoryTitle.setText(getItem(position).title);
            Button more = (Button) rowView.findViewById(R.id.theme_more_view_btn);
            more.setTextColor(Color.parseColor("#FF9000"));
            categoryTitle.setTextColor(-16777216);
            more.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.ThemesCategory.ThemeCategoryAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    ThemesCategory category = (ThemesCategory) ThemeCategoryAdapter.this.themesCategoryWeakReference.get();
                    if (category != null) {
                        category.themeManager.setCurrentCategoryId(ThemeCategoryAdapter.this.getItem(position).categoryId);
                        category.showMoreThemes(ThemeCategoryAdapter.this.getItem(position).categoryId);
                    }
                }
            });
            HorizontalListView horizontalListView = (HorizontalListView) rowView.findViewById(R.id.theme_horizontal_listview);
            List<ThemeManager.SwypeTheme> themeList = getItem(position).themes;
            ThemeThumbListAdapter themeThumbListAdapter = new ThemeThumbListAdapter(themeList, getContext(), getItem(position).categoryId, this.themeThumbnailColumnWidth);
            themeThumbListAdapter.sethListView(horizontalListView);
            this.themeThumbListAdapterMap.put(getItem(position).categoryId, themeThumbListAdapter);
            AdapterView.OnItemClickListener category = (ThemesCategory) this.themesCategoryWeakReference.get();
            horizontalListView.setOnItemClickListener(category);
            horizontalListView.setAdapter((ListAdapter) themeThumbListAdapter);
            return rowView;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getCurrentThemeSku(Context ctx) {
        String sku = UserPreferences.from(ctx).getCurrentThemeId();
        if (StringUtils.isApkCompletePath(sku)) {
            String[] themePath = sku.split("/");
            if (themePath.length > 2) {
                return themePath[themePath.length - 2];
            }
            return sku;
        }
        return sku;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ThemeThumbListAdapter extends BaseAdapter {
        private final String categoryId;
        private final Context context;
        private String currentSku;
        private ColorStateList defaultTextColors;
        private HorizontalListView hListView;
        private final List<String> names = new ArrayList();
        private List<ThemeManager.SwypeTheme> themes;

        public ThemeThumbListAdapter(List<ThemeManager.SwypeTheme> themes, Context context, String categoryId, int maxImageWidth) {
            this.themes = themes;
            this.categoryId = categoryId;
            this.context = context;
            Resources res = context.getResources();
            for (ThemeManager.SwypeTheme theme : themes) {
                this.names.add(theme.getDisplayName(res));
            }
            this.currentSku = ThemesCategory.getCurrentThemeSku(context);
        }

        void sethListView(HorizontalListView hListView) {
            if (hListView == null && this.hListView != null) {
                this.hListView.removeAllViewsInLayout();
                this.hListView.setOnItemClickListener(null);
            }
            this.hListView = hListView;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.themes.size();
        }

        public List<ThemeManager.SwypeTheme> getThemes() {
            return this.themes;
        }

        public void setThemes(List<ThemeManager.SwypeTheme> themes) {
            this.themes = themes;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return this.themes.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        public void setCurrentThemeSku(String sku) {
            if (StringUtils.isApkCompletePath(sku)) {
                String[] themePath = sku.split("/");
                if (themePath.length > 2) {
                    sku = themePath[themePath.length - 2];
                }
            }
            if (sku == null && this.currentSku != null) {
                String old = this.currentSku;
                this.currentSku = null;
                updateThemeStatus(old);
            } else {
                this.currentSku = sku;
                updateThemeStatus(this.currentSku);
            }
        }

        @Override // android.widget.Adapter
        @SuppressLint({"NewApi"})
        public View getView(final int position, View convertView, ViewGroup parent) {
            ThemesCategory.log.i("ThemeThumbListAdapter...categoryId: " + this.categoryId + "...getView...position: " + position + "...convertView: " + convertView);
            final ThemeManager.SwypeTheme theme = this.themes.get(position);
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext().getApplicationContext());
                if (theme.isConnectTheme()) {
                    convertView = inflater.inflate(R.layout.theme_category_preview_download, (ViewGroup) null);
                    ImageViewWrapper logo = (ImageViewWrapper) convertView.findViewById(R.id.download_preview_logo);
                    TextView name = (TextView) convertView.findViewById(R.id.theme_name_category);
                    if (this.defaultTextColors == null) {
                        this.defaultTextColors = name.getTextColors();
                    }
                    ImageView statusIcon = (ImageView) convertView.findViewById(R.id.logo_status_icon);
                    TextView priceView = (TextView) convertView.findViewById(R.id.theme_price);
                    LinearLayout progressRow = (LinearLayout) convertView.findViewById(R.id.progressbar_row);
                    ProgressBar pBar = (ProgressBar) convertView.findViewById(R.id.progressbar_theme_download);
                    ImageView cancel = (ImageView) convertView.findViewById(R.id.cancel_theme_download);
                    View indetermineView = convertView.findViewById(R.id.theme_category_no_data_view);
                    convertView.setTag(new DownloadableThemeStatus(logo, name, statusIcon, priceView, progressRow, pBar, cancel, indetermineView));
                }
            }
            if (convertView != null) {
                DownloadableThemeStatus downloadableThemeStatus = (DownloadableThemeStatus) convertView.getTag();
                downloadableThemeStatus.cancel.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.ThemesCategory.ThemeThumbListAdapter.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        CatalogManager catalogManager = IMEApplication.from(ThemeThumbListAdapter.this.context).getCatalogManager();
                        if (catalogManager != null) {
                            try {
                                catalogManager.cancelDownloadTheme(position, ((ThemeManager.ConnectDownloadableThemeWrapper) theme).getThemeItemSeed().categoryKey, theme.getSku());
                            } catch (ACException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                downloadableThemeStatus.setThemeName(Html.fromHtml(this.names.get(position)));
                downloadableThemeStatus.setSku(theme.getSku());
                downloadableThemeStatus.initProgressBar();
                downloadableThemeStatus.name.setTextColor(-16777216);
                convertView.setBackgroundResource(R.drawable.settings_theme_background);
                ThemeManager.ConnectDownloadableThemeWrapper connectTheme = (ThemeManager.ConnectDownloadableThemeWrapper) theme;
                downloadableThemeStatus.downloadedLogo.setWrapperImageWidth(this.context.getResources().getDimensionPixelSize(R.dimen.category_theme_column_width));
                downloadableThemeStatus.downloadedLogo.setWrapperImageHeight(this.context.getResources().getDimensionPixelSize(R.dimen.category_theme_column_height));
                ImageCache.with(this.context).loadImage(connectTheme.getThumbnailUrl(), R.drawable.custom_progressbar_indeterminate, downloadableThemeStatus.downloadedLogo);
                downloadableThemeStatus.updateStatus(this.context, connectTheme, convertView, this.currentSku);
            }
            return convertView;
        }

        public List<ThemeManager.SwypeTheme> getPreloadItems(int position) {
            return this.themes.subList(position, this.themes.size());
        }

        public GenericRequestBuilder getPreloadRequestBuilder(ThemeManager.SwypeTheme item) {
            return ThemesCategory.fullRequest.thumbnail(ThemesCategory.thumbRequest.load((DrawableRequestBuilder) item)).load((DrawableRequestBuilder) item);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class DownloadableThemeStatus {
            public final ImageView cancel;
            public final ImageViewWrapper downloadedLogo;
            public final View indetermineView;
            public final TextView name;
            public final TextView price;
            public final ProgressBar progressBar;
            public final LinearLayout progressBarRow;
            private String sku;
            public final ImageView statusIcon;

            private DownloadableThemeStatus(ImageViewWrapper logo, TextView name, ImageView statusIcon, TextView price, LinearLayout progressBarRow, ProgressBar progressBar, ImageView cancel, View indetermineView) {
                this.downloadedLogo = logo;
                this.name = name;
                this.statusIcon = statusIcon;
                this.price = price;
                this.progressBarRow = progressBarRow;
                this.progressBar = progressBar;
                this.cancel = cancel;
                this.indetermineView = indetermineView;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void setSku(String sku) {
                this.sku = sku;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void initProgressBar() {
                if (this.progressBar != null) {
                    this.progressBar.setMax(100);
                    this.progressBar.setProgress(0);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void setThemeName(CharSequence themeName) {
                this.name.setText(themeName);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void updateStatus(Context context, ThemeManager.ConnectDownloadableThemeWrapper theme, View convertView, String currentSku) {
                ThemesCategory.log.i("updateStatus: " + convertView);
                int statusIconId = theme.getStatusIcon(context);
                if (theme.getThemeItemSeed() != null && theme.getThemeItemSeed().isInstalling) {
                    this.price.setVisibility(8);
                    this.statusIcon.setVisibility(4);
                    if (this.progressBarRow.getVisibility() != 0) {
                        this.progressBarRow.setVisibility(0);
                    }
                    int progress = theme.getThemeItemSeed().installingPercentage;
                    if (progress > 0) {
                        this.progressBar.setIndeterminate(false);
                        this.progressBar.setProgress(progress);
                        return;
                    } else {
                        this.progressBar.setIndeterminate(true);
                        return;
                    }
                }
                if (statusIconId != 0) {
                    this.progressBarRow.setVisibility(4);
                    this.statusIcon.setVisibility(0);
                    this.price.setVisibility(8);
                    this.statusIcon.setBackgroundResource(statusIconId);
                } else {
                    this.price.setVisibility(0);
                    this.statusIcon.setVisibility(4);
                    this.progressBarRow.setVisibility(4);
                    this.price.setText(theme.getStatusText(context));
                }
                if (currentSku != null && currentSku.equals(theme.getSku())) {
                    convertView.setBackgroundResource(R.drawable.settings_theme_background_current);
                } else {
                    convertView.setBackgroundResource(R.drawable.settings_theme_background);
                }
            }
        }

        void updateThemeStatus(String sku) {
            DownloadableThemeStatus downloadableThemeStatus;
            ThemesCategory.log.i("updateStatus: " + sku);
            if (this.hListView != null) {
                int visibleViews = (this.hListView.getLastVisiblePosition() - this.hListView.getFirstVisiblePosition()) + 1;
                for (int i = 0; i < visibleViews; i++) {
                    View view = this.hListView.getChildAt(i);
                    if (view != null && (downloadableThemeStatus = (DownloadableThemeStatus) view.getTag()) != null && downloadableThemeStatus.sku.equals(sku)) {
                        Iterator<ThemeManager.SwypeTheme> it = this.themes.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                ThemeManager.SwypeTheme item = it.next();
                                if (item.getSku().equals(downloadableThemeStatus.sku)) {
                                    downloadableThemeStatus.updateStatus(this.context, (ThemeManager.ConnectDownloadableThemeWrapper) item, view, this.currentSku);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void onPurchaseFinished(int resultCode, String sku, String categoryId) {
        if (resultCode == 0) {
            log.d("purchased finish succeed");
        } else {
            log.d("purchase finish failed");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void refreshCategoryListView() {
        this.themeManager.getThemeDataManager();
        if (ThemeData.isCacheEmpty() || this.themeManager.getThemeCategoryItemList(this.context).size() == 0) {
            refreshUI(true);
            return;
        }
        if (this.themeCategoryAdapter == null) {
            this.themeCategoryAdapter = new ThemeCategoryAdapter(this.context, R.layout.theme_category_item, new ArrayList(this.themeManager.getThemeCategoryItemList(this.context)), this);
            if (this.categoryListView == null) {
                this.categoryListView = (ListView) this.categoryView.findViewById(R.id.theme_category_list);
                this.categoryListView.setVerticalFadingEdgeEnabled(false);
                this.categoryListView.setOnItemClickListener(this);
            }
            this.categoryListView.setAdapter((ListAdapter) this.themeCategoryAdapter);
            refreshUI(false);
        }
    }
}
