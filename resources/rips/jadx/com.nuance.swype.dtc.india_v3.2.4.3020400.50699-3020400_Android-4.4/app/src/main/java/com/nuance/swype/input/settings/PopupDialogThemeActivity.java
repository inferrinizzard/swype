package com.nuance.swype.input.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.GraphResponse;
import com.nuance.android.util.ImageCache;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.connect.api.CatalogService;
import com.nuance.swype.inapp.CatalogManager;
import com.nuance.swype.inapp.util.IabHelper;
import com.nuance.swype.inapp.util.IabResult;
import com.nuance.swype.inapp.util.Purchase;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.service.impl.AccountUtil;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.usagedata.UsageDataEventThemesPreview;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.drawable.ImageViewWrapper;
import com.nuance.swype.util.storage.ThemeItemSeed;
import com.nuance.swypeconnect.ac.ACException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class PopupDialogThemeActivity extends Activity {
    public static final String EXTRA_THEME_BUNDLE_SKU = "com.nuance.swype.input.settings.PopupDialogThemeActivity.bundle_sku";
    public static final String EXTRA_THEME_CATEGORY_ID = "com.nuance.swype.input.settings.PopupDialogThemeActivity.category_id";
    public static final String EXTRA_THEME_ID = "com.nuance.swype.input.settings.PopupDialogThemeActivity.theme_id";
    public static final String EXTRA_THEME_REQUEST = "com.nuance.swype.input.settings.PopupDialogThemeActivity.Dialog_request";
    public static final String EXTRA_THEME_SKU = "com.nuance.swype.input.settings.PopupDialogThemeActivity.theme_sku";
    public static final String EXTRA_THEME_SOURCE = "extra_theme_source";
    public static final String EXTRA_THEME_VIEW_INDEX = "com.nuance.swype.input.settings.PopupDialogThemeActivity.theme_view_index";
    public static final int MSG_DELAY_SELF_DESTRUCTION = 100011;
    private static final int MSG_SHOW_BOUNDLE_ONLY_DLG = 100012;
    public static final int REQUEST_APPLY_THEME = 10002;
    public static final int REQUEST_GO_TO_BUNDLE = 10006;
    public static final int REQUEST_INSTALL_THEME = 10003;
    public static final int REQUEST_PURCHASE = 10001;
    public static final int REQUEST_UNINSTALL_APPLY_THEME = 10005;
    public static final int REQUEST_UNINSTALL_THEME = 10004;
    private static final LogManager.Log log = LogManager.getLog("PurchaseThemeActivity");
    private String bundleSku;
    private ThemeManager.ConnectDownloadableThemeWrapper downloadableTheme;
    private Button mCancelButton;
    private Button mConfirmButton;
    private TextView mThemeNameView;
    private ImageViewWrapper mThemePreviewView;
    private TextView mThemePriceView;
    private int requestID;
    private Resources res;
    private ThemeManager.SwypeTheme swypeTheme;
    private String themeCategory;
    private ThemeManager themeManager;
    private int themePreviewHeight;
    private int themePreviewWidth;
    private String themesku = "";
    private String themeSourceFragment = "";
    private int viewIndex = -1;
    private boolean buttonPressed = false;
    private final IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.6
        @Override // com.nuance.swype.inapp.util.IabHelper.OnIabPurchaseFinishedListener
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            UsageDataEventThemesPreview.Result actionResult;
            CatalogManager catalogManager;
            PopupDialogThemeActivity.log.d("in onIabPurchaseFinished, threadId:" + Thread.currentThread().getId());
            if (PopupDialogThemeActivity.this.downloadableTheme == null) {
                PopupDialogThemeActivity.this.setResult(0);
                ThemeManager.SwypeTheme theme = PopupDialogThemeActivity.this.themeManager.getSwypeTheme(PopupDialogThemeActivity.this.themeCategory, PopupDialogThemeActivity.this.themesku);
                if (theme != null && theme.isConnectTheme() && ((ThemeManager.ConnectDownloadableThemeWrapper) theme).getType() == CatalogService.CatalogItem.Type.KEYBOARD.ordinal()) {
                    PopupDialogThemeActivity.log.d("It's KEYBOARD theme, so recordThemesPreview");
                    PopupDialogThemeActivity.this.recordThemesPreview(UsageDataEventThemesPreview.Action.BUY, UsageDataEventThemesPreview.Result.NULL_THEME, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.viewIndex, PopupDialogThemeActivity.this.themeCategory);
                }
                PopupDialogThemeActivity.this.recordStoreTransactionFailed(result.mResponse, IabHelper.getResponseDesc(result.mResponse));
                PopupDialogThemeActivity.this.finish();
                return;
            }
            if (result.isSuccess() && purchase != null) {
                AccountUtil.setIsGoogleAccountSignedIn(true);
                if (PopupDialogThemeActivity.this.downloadableTheme.getSource() == ThemeManager.SwypeTheme.THEME_SOURCE.CONNECT && (catalogManager = IMEApplication.from(PopupDialogThemeActivity.this.getApplicationContext()).getCatalogManager()) != null) {
                    catalogManager.setPurchased(PopupDialogThemeActivity.this.downloadableTheme.getThemeItemSeed().categoryKey, PopupDialogThemeActivity.this.downloadableTheme.getSku());
                    try {
                        catalogManager.downloadTheme(PopupDialogThemeActivity.this.viewIndex, PopupDialogThemeActivity.this.downloadableTheme);
                    } catch (ACException e) {
                        PopupDialogThemeActivity.log.e(String.format("Download of theme failed. SKU: %s", PopupDialogThemeActivity.this.downloadableTheme.getSku()));
                    }
                }
                PopupDialogThemeActivity.log.d("purchased sku is:" + purchase.mSku);
                String sku = PopupDialogThemeActivity.this.downloadableTheme.getSku();
                String category = PopupDialogThemeActivity.this.downloadableTheme.getThemeItemSeed().categoryKey;
                Intent resultIntent = new Intent();
                resultIntent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_ID, sku);
                resultIntent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, PopupDialogThemeActivity.this.downloadableTheme.getThemeItemSeed().categoryKey);
                if (PopupDialogThemeActivity.this.themeManager != null) {
                    PopupDialogThemeActivity.this.themeManager.onPurchaseFinished(0, sku, category);
                }
                actionResult = UsageDataEventThemesPreview.Result.SUCCESS;
                PopupDialogThemeActivity.this.setResult(-1, resultIntent);
            } else if (!result.isSuccess() && result.mResponse == 7) {
                String themeId = PopupDialogThemeActivity.this.downloadableTheme.getSku();
                String category2 = PopupDialogThemeActivity.this.downloadableTheme.getThemeItemSeed().categoryKey;
                Intent resultIntent2 = new Intent();
                resultIntent2.putExtra(PopupDialogThemeActivity.EXTRA_THEME_ID, themeId);
                resultIntent2.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, PopupDialogThemeActivity.this.downloadableTheme.getThemeItemSeed().categoryKey);
                resultIntent2.putExtra(PopupDialogThemeActivity.EXTRA_THEME_REQUEST, PopupDialogThemeActivity.REQUEST_INSTALL_THEME);
                CatalogManager catalogManager2 = IMEApplication.from(PopupDialogThemeActivity.this.getApplicationContext()).getCatalogManager();
                catalogManager2.setPurchased(PopupDialogThemeActivity.this.downloadableTheme.getThemeItemSeed().categoryKey, PopupDialogThemeActivity.this.downloadableTheme.getSku());
                catalogManager2.refetchPurchaseInfoFromGoolgeStore();
                if (PopupDialogThemeActivity.this.themeManager != null) {
                    PopupDialogThemeActivity.this.themeManager.onPurchaseFinished(0, themeId, category2);
                }
                actionResult = UsageDataEventThemesPreview.Result.ALREADY_OWNED;
                PopupDialogThemeActivity.this.setResult(-1, resultIntent2);
            } else if (!result.isSuccess() && result.mResponse == 1) {
                PopupDialogThemeActivity.this.setResult(0);
                actionResult = UsageDataEventThemesPreview.Result.ABORTED;
                PopupDialogThemeActivity.this.recordStoreTransactionFailed(result.mResponse, IabHelper.getResponseDesc(result.mResponse));
            } else {
                try {
                    if (result.mMessage.equals(IabHelper.getResponseDetailedDesc("inapp_not_supported", result.mResponse)) && result.mResponse == 3) {
                        if (PopupDialogThemeActivity.this.getResources().getInteger(R.integer.google_play_versioncode_min) > PopupDialogThemeActivity.this.getPackageManager().getPackageInfo("com.android.vending", 0).versionCode) {
                            PopupDialogThemeActivity.log.d("Google Play should be updated");
                        } else {
                            AccountUtil.setGoogleAccountMissing(PopupDialogThemeActivity.this.getApplicationContext(), true);
                        }
                    } else if (result.mResponse == 2 && IabHelper.getResponseDetailedDesc("User canceled.", result.mResponse).equals(result.mMessage)) {
                        AccountUtil.setIsGoogleAccountSignedIn(false);
                    }
                } catch (PackageManager.NameNotFoundException e2) {
                    PopupDialogThemeActivity.log.e("Google Play is not available:" + e2.getMessage());
                }
                PopupDialogThemeActivity.this.setResult(1);
                actionResult = UsageDataEventThemesPreview.Result.ABORTED;
                PopupDialogThemeActivity.this.recordStoreTransactionFailed(result.mResponse, IabHelper.getResponseDesc(result.mResponse));
            }
            if (PopupDialogThemeActivity.this.downloadableTheme.getType() == CatalogService.CatalogItem.Type.KEYBOARD.ordinal()) {
                PopupDialogThemeActivity.log.d("It's KEYBOARD theme, so recordThemesPreview");
                PopupDialogThemeActivity.this.recordThemesPreview(UsageDataEventThemesPreview.Action.BUY, actionResult, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.viewIndex, PopupDialogThemeActivity.this.themeCategory);
            }
            ThemeManager.from(PopupDialogThemeActivity.this.getApplicationContext()).getThemePurchaser(PopupDialogThemeActivity.this.getApplicationContext()).cleanup(result);
            PopupDialogThemeActivity.this.finish();
        }
    };
    private final Handler.Callback handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.7
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PopupDialogThemeActivity.MSG_DELAY_SELF_DESTRUCTION /* 100011 */:
                    PopupDialogThemeActivity.this.finish();
                    return true;
                case PopupDialogThemeActivity.MSG_SHOW_BOUNDLE_ONLY_DLG /* 100012 */:
                    new AlertDialog.Builder(PopupDialogThemeActivity.this).setTitle(R.string.bundle_only_title).setMessage(R.string.bundle_only_desc_1).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.7.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                    return true;
                default:
                    return true;
            }
        }
    };
    private final Handler mHandler = WeakReferenceHandler.create(this.handlerCallback);

    @Override // android.app.Activity
    @SuppressLint({"InflateParams"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        IMEApplication app = IMEApplication.from(getApplicationContext());
        LayoutInflater inflater = app.getThemedLayoutInflater(LayoutInflater.from(app));
        this.res = getApplicationContext().getResources();
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.pop_up_dialog_theme_activity, (ViewGroup) null);
        setContentView(view);
        this.themePreviewWidth = this.res.getDimensionPixelSize(R.dimen.pop_up_theme_column_width);
        this.themePreviewHeight = this.res.getDimensionPixelSize(R.dimen.pop_up_theme_column_height);
        this.mThemeNameView = (TextView) findViewById(R.id.purchase_theme_name);
        this.mThemePriceView = (TextView) findViewById(R.id.purchase_theme_price);
        this.mThemePreviewView = (ImageViewWrapper) findViewById(R.id.purchase_theme_preview_image);
        this.themeManager = IMEApplication.from(getApplicationContext()).getThemeManager();
        final String mySku = getIntent().getExtras().getString(EXTRA_THEME_ID);
        this.themeCategory = getIntent().getExtras().getString(EXTRA_THEME_CATEGORY_ID);
        this.themesku = app.getCurrentThemeId();
        this.buttonPressed = false;
        this.bundleSku = getIntent().getExtras().getString(EXTRA_THEME_BUNDLE_SKU);
        boolean onCreateSuccessful = true;
        this.themeSourceFragment = getIntent().getExtras().getString(EXTRA_THEME_SOURCE);
        this.viewIndex = getIntent().getExtras().getInt(EXTRA_THEME_VIEW_INDEX);
        if (this.themeSourceFragment == null) {
            this.themeSourceFragment = "";
            log.d("onCreate: themeSourceFragment not provided!");
        }
        if (mySku != null) {
            this.swypeTheme = this.themeManager.getSwypeTheme(this.themeCategory, mySku);
            updateFreeThemeScreenContent();
            if (this.swypeTheme != null) {
                this.themesku = mySku;
            }
            if (this.themeSourceFragment.equals(SettingsV11.TAB_MY_THEMES)) {
                UsageData.recordScreenVisited(UsageData.Screen.MY_THEMES_PREVIEW);
            } else {
                UsageData.recordScreenVisited(UsageData.Screen.GET_THEMES_PREVIEW);
            }
            this.mConfirmButton = (Button) findViewById(R.id.purchase_theme_confirm);
            this.mConfirmButton.setText(R.string.apply);
            this.mConfirmButton.setContentDescription(this.res.getString(R.string.apply));
            this.mConfirmButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    PopupDialogThemeActivity.this.buttonPressed = true;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_ID, mySku);
                    resultIntent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, PopupDialogThemeActivity.this.themeCategory);
                    PopupDialogThemeActivity.this.setResult(-1, resultIntent);
                    PopupDialogThemeActivity.this.recordThemesPreview(UsageDataEventThemesPreview.Action.APPLY, UsageDataEventThemesPreview.Result.SUCCESS, mySku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.viewIndex, PopupDialogThemeActivity.this.themeCategory);
                    PopupDialogThemeActivity.this.finish();
                }
            });
            this.mCancelButton = (Button) findViewById(R.id.purchase_theme_cancel);
            this.mCancelButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    PopupDialogThemeActivity.this.buttonPressed = true;
                    PopupDialogThemeActivity.this.setResult(0);
                    PopupDialogThemeActivity.this.recordThemesPreview(UsageDataEventThemesPreview.Action.CANCEL, UsageDataEventThemesPreview.Result.SUCCESS, mySku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.viewIndex, PopupDialogThemeActivity.this.themeCategory);
                    PopupDialogThemeActivity.this.finish();
                }
            });
        } else {
            String sku = getIntent().getExtras().getString(EXTRA_THEME_SKU);
            this.requestID = getIntent().getExtras().getInt(EXTRA_THEME_REQUEST);
            log.d("SKU selected: ", sku, " category id:", this.themeCategory);
            if (this.themeManager.getSwypeTheme(this.themeCategory, sku) != null && (this.themeManager.getSwypeTheme(this.themeCategory, sku) instanceof ThemeManager.ConnectDownloadableThemeWrapper)) {
                this.downloadableTheme = (ThemeManager.ConnectDownloadableThemeWrapper) this.themeManager.getSwypeTheme(this.themeCategory, sku);
            }
            if (this.downloadableTheme == null) {
                onCreateSuccessful = false;
            }
            if (this.downloadableTheme != null && this.downloadableTheme.getSource() == ThemeManager.SwypeTheme.THEME_SOURCE.CONNECT) {
                updateConnectDownloadableScreenContent();
            }
            if (this.downloadableTheme != null) {
                this.themesku = this.downloadableTheme.getSku();
            }
            if (this.downloadableTheme != null && this.downloadableTheme.getSource() == ThemeManager.SwypeTheme.THEME_SOURCE.CONNECT) {
                ThemeManager.ConnectDownloadableThemeWrapper connectTheme = this.downloadableTheme;
                setupDialog(this.viewIndex, connectTheme, this.requestID);
                if (connectTheme.getType() == CatalogService.CatalogItem.Type.BUNDLE.ordinal()) {
                    WindowManager.LayoutParams params = getWindow().getAttributes();
                    params.alpha = 0.0f;
                    getWindow().setAttributes(params);
                    purchaseItem();
                }
            }
        }
        WindowManager.LayoutParams params2 = getWindow().getAttributes();
        params2.width = this.themePreviewWidth;
        getWindow().setAttributes(params2);
        if (!onCreateSuccessful) {
            if (this.mHandler.hasMessages(MSG_DELAY_SELF_DESTRUCTION)) {
                this.mHandler.removeMessages(MSG_DELAY_SELF_DESTRUCTION);
            }
            this.mHandler.sendEmptyMessageDelayed(MSG_DELAY_SELF_DESTRUCTION, 1L);
        }
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        log.d("onStop: buttonPressed: " + this.buttonPressed + ", isChangingConfigurations: " + isChangingConfigurations() + ", isFinishing: " + isFinishing());
        if (!this.buttonPressed && !isChangingConfigurations() && isFinishing()) {
            ThemeManager.SwypeTheme theme = this.downloadableTheme != null ? this.downloadableTheme : this.themeManager.getSwypeTheme(this.themeCategory, this.themesku);
            if (theme != null && theme.isConnectTheme() && ((ThemeManager.ConnectDownloadableThemeWrapper) theme).getType() == CatalogService.CatalogItem.Type.KEYBOARD.ordinal()) {
                log.d("It's KEYBOARD theme, so recordThemesPreview");
                recordThemesPreview(UsageDataEventThemesPreview.Action.CANCEL, UsageDataEventThemesPreview.Result.SUCCESS, this.themesku, this.themeSourceFragment, this.viewIndex, this.themeCategory);
            }
        }
    }

    private void setupDialog(final int index, final ThemeManager.ConnectDownloadableThemeWrapper theme, int requestId) {
        ThemeItemSeed seed = theme.getThemeItemSeed();
        this.mConfirmButton = (Button) findViewById(R.id.purchase_theme_confirm);
        this.mConfirmButton.setText(R.string.theme_buy_theme);
        this.mConfirmButton.setContentDescription(this.res.getString(R.string.theme_buy_theme));
        if (seed.isInstalled) {
            if (requestId == 10005) {
                this.mConfirmButton.setText(R.string.apply);
                this.mConfirmButton.setContentDescription(this.res.getString(R.string.apply));
            } else {
                this.mConfirmButton.setText(R.string.uninstall_button);
                this.mConfirmButton.setContentDescription(this.res.getString(R.string.uninstall_button));
            }
        } else if (seed.isFree || seed.isPurchased) {
            this.mConfirmButton.setText(R.string.install_button);
            this.mConfirmButton.setContentDescription(this.res.getString(R.string.install_button));
        } else if (!seed.isFree && !seed.isPurchasable && (seed.price == null || seed.price.isEmpty())) {
            this.mConfirmButton.setText(R.string.buy_bundle);
            this.mConfirmButton.setContentDescription(this.res.getString(R.string.buy_bundle_desc));
            this.mHandler.removeMessages(MSG_SHOW_BOUNDLE_ONLY_DLG);
            this.mHandler.sendEmptyMessageDelayed(MSG_SHOW_BOUNDLE_ONLY_DLG, 100L);
        }
        if (this.themeSourceFragment.equals(SettingsV11.TAB_MY_THEMES)) {
            UsageData.recordScreenVisited(UsageData.Screen.MY_THEMES_PREVIEW);
        } else if (theme.getType() == CatalogService.CatalogItem.Type.KEYBOARD.ordinal()) {
            UsageData.recordScreenVisited(UsageData.Screen.GET_THEMES_PREVIEW);
        }
        this.mConfirmButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                PopupDialogThemeActivity.this.buttonPressed = true;
                String btnText = PopupDialogThemeActivity.this.mConfirmButton.getText().toString();
                if (PopupDialogThemeActivity.this.requestID != 10003) {
                    if (PopupDialogThemeActivity.this.requestID != 10004) {
                        if (!btnText.equals(PopupDialogThemeActivity.this.res.getString(R.string.apply))) {
                            if (btnText.equals(PopupDialogThemeActivity.this.res.getString(R.string.buy_bundle))) {
                                if (PopupDialogThemeActivity.this.bundleSku != null && !PopupDialogThemeActivity.this.bundleSku.isEmpty()) {
                                    PopupDialogThemeActivity.this.downloadableTheme = (ThemeManager.ConnectDownloadableThemeWrapper) PopupDialogThemeActivity.this.themeManager.getSwypeTheme(PopupDialogThemeActivity.this.bundleSku);
                                    if (PopupDialogThemeActivity.this.downloadableTheme != null) {
                                        PopupDialogThemeActivity.this.themesku = PopupDialogThemeActivity.this.downloadableTheme.getSku();
                                        UsageData.recordThemeUpsell(UsageData.ThemeUpsellUserAction.BUY_BUNDLE, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.themeCategory);
                                        PopupDialogThemeActivity.this.purchaseItem();
                                        return;
                                    }
                                    return;
                                }
                                return;
                            }
                            List<String> bundleOfTheme = PopupDialogThemeActivity.this.themeManager.getBundleOfTheme(theme.getSku());
                            if (bundleOfTheme == null || bundleOfTheme.size() <= 0) {
                                PopupDialogThemeActivity.this.purchaseItem();
                                return;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(PopupDialogThemeActivity.this);
                            builder.setIcon(R.drawable.swype_logo);
                            builder.setTitle(PopupDialogThemeActivity.this.res.getString(R.string.buy_bundle_title));
                            Resources res = PopupDialogThemeActivity.this.getApplicationContext().getResources();
                            if (PopupDialogThemeActivity.this.themeSourceFragment.equals(SettingsV11.TAB_BUNDLE_VIEW)) {
                                builder.setMessage(String.format(res.getString(R.string.buy_bundle_desc), ((ThemeManager.ConnectDownloadableThemeWrapper) PopupDialogThemeActivity.this.themeManager.getSwypeTheme(PopupDialogThemeActivity.this.bundleSku)).getPrice()));
                                builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.3.1
                                    @Override // android.content.DialogInterface.OnCancelListener
                                    public void onCancel(DialogInterface dialog) {
                                        UsageData.recordThemeUpsell(UsageData.ThemeUpsellUserAction.CANCEL, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.themeCategory);
                                    }
                                });
                                builder.setNegativeButton(res.getString(R.string.buy_single_theme), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.3.2
                                    @Override // android.content.DialogInterface.OnClickListener
                                    public void onClick(DialogInterface dialog, int which) {
                                        UsageData.recordThemeUpsell(UsageData.ThemeUpsellUserAction.BUY_THEME, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.themeCategory);
                                        dialog.dismiss();
                                        PopupDialogThemeActivity.this.purchaseItem();
                                    }
                                });
                                builder.setPositiveButton(res.getString(R.string.buy_bundle), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.3.3
                                    @Override // android.content.DialogInterface.OnClickListener
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (PopupDialogThemeActivity.this.bundleSku != null && !PopupDialogThemeActivity.this.bundleSku.isEmpty()) {
                                            PopupDialogThemeActivity.this.downloadableTheme = (ThemeManager.ConnectDownloadableThemeWrapper) PopupDialogThemeActivity.this.themeManager.getSwypeTheme(PopupDialogThemeActivity.this.bundleSku);
                                            if (PopupDialogThemeActivity.this.downloadableTheme != null) {
                                                PopupDialogThemeActivity.this.themesku = PopupDialogThemeActivity.this.downloadableTheme.getSku();
                                                UsageData.recordThemeUpsell(UsageData.ThemeUpsellUserAction.BUY_BUNDLE, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.themeCategory);
                                                PopupDialogThemeActivity.this.purchaseItem();
                                                dialog.dismiss();
                                            }
                                        }
                                    }
                                });
                            } else {
                                ThemeManager.ConnectDownloadableThemeWrapper bundleToGo = null;
                                for (String bundle : bundleOfTheme) {
                                    if (bundleToGo != null) {
                                        ThemeManager.ConnectDownloadableThemeWrapper bundleTemp = (ThemeManager.ConnectDownloadableThemeWrapper) PopupDialogThemeActivity.this.themeManager.getSwypeTheme(bundle);
                                        if (bundleTemp != null && PopupDialogThemeActivity.this.parseThemePriceToDouble(bundleToGo.getPrice()) < PopupDialogThemeActivity.this.parseThemePriceToDouble(bundleTemp.getPrice())) {
                                            bundleToGo = bundleTemp;
                                        }
                                    } else {
                                        bundleToGo = (ThemeManager.ConnectDownloadableThemeWrapper) PopupDialogThemeActivity.this.themeManager.getSwypeTheme(bundle);
                                    }
                                }
                                final ThemeManager.ConnectDownloadableThemeWrapper bundleTheme = bundleToGo;
                                if (bundleToGo != null) {
                                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.3.4
                                        @Override // android.content.DialogInterface.OnCancelListener
                                        public void onCancel(DialogInterface dialog) {
                                            UsageData.recordThemeUpsell(UsageData.ThemeUpsellUserAction.CANCEL, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.themeCategory);
                                        }
                                    });
                                    builder.setMessage(String.format(res.getString(R.string.go_to_bundle_desc), bundleTheme.getPrice()));
                                    builder.setNegativeButton(res.getString(R.string.buy_single_theme), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.3.5
                                        @Override // android.content.DialogInterface.OnClickListener
                                        public void onClick(DialogInterface dialog, int which) {
                                            UsageData.recordThemeUpsell(UsageData.ThemeUpsellUserAction.BUY_THEME, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.themeCategory);
                                            dialog.dismiss();
                                            PopupDialogThemeActivity.this.purchaseItem();
                                        }
                                    });
                                    builder.setPositiveButton(res.getString(R.string.go_to_bundle), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.3.6
                                        @Override // android.content.DialogInterface.OnClickListener
                                        public void onClick(DialogInterface dialog, int which) {
                                            UsageData.recordThemeUpsell(UsageData.ThemeUpsellUserAction.GO_TO_BUNDLE, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.themeCategory);
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_ID, bundleTheme.getSku());
                                            resultIntent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, bundleTheme.getThemeItemSeed().categoryKey);
                                            PopupDialogThemeActivity.this.setResult(PopupDialogThemeActivity.REQUEST_GO_TO_BUNDLE, resultIntent);
                                            dialog.dismiss();
                                            PopupDialogThemeActivity.this.finish();
                                        }
                                    });
                                } else {
                                    return;
                                }
                            }
                            builder.create().show();
                            UsageData.recordScreenVisited(UsageData.Screen.BUNDLE_UPSELL);
                            return;
                        }
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_ID, theme.getSku());
                        resultIntent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, theme.getThemeItemSeed().categoryKey);
                        PopupDialogThemeActivity.this.setResult(-1, resultIntent);
                        PopupDialogThemeActivity.this.recordThemesPreview(UsageDataEventThemesPreview.Action.APPLY, UsageDataEventThemesPreview.Result.SUCCESS, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.viewIndex, PopupDialogThemeActivity.this.themeCategory);
                        PopupDialogThemeActivity.this.finish();
                        return;
                    }
                    CatalogManager catalogManager = IMEApplication.from(PopupDialogThemeActivity.this.getApplicationContext()).getCatalogManager();
                    UsageDataEventThemesPreview.Result eventResult = UsageDataEventThemesPreview.Result.SUCCESS;
                    if (catalogManager != null) {
                        try {
                            catalogManager.uninstallTheme(index, theme.getThemeItemSeed().categoryKey, theme.getSku());
                            PopupDialogThemeActivity.log.d("Uninstalled theme sku is:" + theme.getSku());
                        } catch (ACException e) {
                            eventResult = UsageDataEventThemesPreview.Result.AC_EXCEPTION;
                            PopupDialogThemeActivity.log.e(String.format("Uninstall of theme failed. SKU: %s", theme.getSku()));
                        }
                    }
                    PopupDialogThemeActivity.this.setResult(-1);
                    PopupDialogThemeActivity.this.recordThemesPreview(UsageDataEventThemesPreview.Action.UNINSTALL, eventResult, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.viewIndex, PopupDialogThemeActivity.this.themeCategory);
                    if (PopupDialogThemeActivity.this.themeSourceFragment.equals(SettingsV11.TAB_BUNDLE_VIEW)) {
                        PopupDialogThemeActivity.this.recordBundleThemesPreview("", GraphResponse.SUCCESS_KEY, "uninstall", "previous theme", PopupDialogThemeActivity.this.themeSourceFragment, String.valueOf(PopupDialogThemeActivity.this.viewIndex), PopupDialogThemeActivity.this.themeCategory);
                    }
                    PopupDialogThemeActivity.this.finish();
                    return;
                }
                CatalogManager catalogManager2 = IMEApplication.from(PopupDialogThemeActivity.this.getApplicationContext()).getCatalogManager();
                UsageDataEventThemesPreview.Result eventResult2 = UsageDataEventThemesPreview.Result.SUCCESS;
                if (catalogManager2 != null) {
                    try {
                        catalogManager2.downloadTheme(index, theme);
                        PopupDialogThemeActivity.log.d("Downloaded theme sku is:" + theme.getSku());
                    } catch (ACException e2) {
                        eventResult2 = UsageDataEventThemesPreview.Result.AC_EXCEPTION;
                        PopupDialogThemeActivity.log.e(String.format("Download of theme failed. SKU: %s", theme.getSku()));
                    }
                }
                PopupDialogThemeActivity.this.setResult(-1);
                PopupDialogThemeActivity.this.recordThemesPreview(UsageDataEventThemesPreview.Action.INSTALL, eventResult2, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.viewIndex, PopupDialogThemeActivity.this.themeCategory);
                if (PopupDialogThemeActivity.this.themeSourceFragment.equals(SettingsV11.TAB_BUNDLE_VIEW)) {
                    PopupDialogThemeActivity.this.recordBundleThemesPreview("", GraphResponse.SUCCESS_KEY, "install", "previous theme", PopupDialogThemeActivity.this.themeSourceFragment, String.valueOf(PopupDialogThemeActivity.this.viewIndex), PopupDialogThemeActivity.this.themeCategory);
                }
                PopupDialogThemeActivity.this.finish();
            }
        });
        this.mCancelButton = (Button) findViewById(R.id.purchase_theme_cancel);
        this.mCancelButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                PopupDialogThemeActivity.this.buttonPressed = true;
                Intent resultIntent = new Intent();
                resultIntent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_ID, theme.getSku());
                resultIntent.putExtra(PopupDialogThemeActivity.EXTRA_THEME_CATEGORY_ID, theme.getThemeItemSeed().categoryKey);
                PopupDialogThemeActivity.this.setResult(0, resultIntent);
                PopupDialogThemeActivity.this.recordThemesPreview(UsageDataEventThemesPreview.Action.CANCEL, UsageDataEventThemesPreview.Result.SUCCESS, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.viewIndex, PopupDialogThemeActivity.this.themeCategory);
                if (PopupDialogThemeActivity.this.themeSourceFragment.equals(SettingsV11.TAB_BUNDLE_VIEW)) {
                    PopupDialogThemeActivity.this.recordBundleThemesPreview("user left", "canceled", "cancel", "previous theme", PopupDialogThemeActivity.this.themeSourceFragment, String.valueOf(PopupDialogThemeActivity.this.viewIndex), PopupDialogThemeActivity.this.themeCategory);
                }
                PopupDialogThemeActivity.this.finish();
            }
        });
        Button uninstallButton = (Button) findViewById(R.id.purchase_theme_uninstall);
        if (!seed.isInstalled || requestId != 10005) {
            uninstallButton.setVisibility(8);
        } else {
            uninstallButton.setVisibility(0);
            uninstallButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.PopupDialogThemeActivity.5
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    PopupDialogThemeActivity.this.buttonPressed = true;
                    CatalogManager catalogManager = IMEApplication.from(PopupDialogThemeActivity.this.getApplicationContext()).getCatalogManager();
                    UsageDataEventThemesPreview.Result eventResult = UsageDataEventThemesPreview.Result.SUCCESS;
                    if (catalogManager != null) {
                        try {
                            catalogManager.uninstallTheme(index, theme.getThemeItemSeed().categoryKey, theme.getSku());
                            PopupDialogThemeActivity.log.d("Uninstalled theme sku is:" + theme.getSku());
                        } catch (ACException e) {
                            eventResult = UsageDataEventThemesPreview.Result.AC_EXCEPTION;
                            PopupDialogThemeActivity.log.e(String.format("Uninstall of theme failed. SKU: %s", theme.getSku()));
                        }
                    }
                    PopupDialogThemeActivity.this.setResult(-1);
                    PopupDialogThemeActivity.this.recordThemesPreview(UsageDataEventThemesPreview.Action.UNINSTALL, eventResult, PopupDialogThemeActivity.this.themesku, PopupDialogThemeActivity.this.themeSourceFragment, PopupDialogThemeActivity.this.viewIndex, PopupDialogThemeActivity.this.themeCategory);
                    PopupDialogThemeActivity.this.finish();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public double parseThemePriceToDouble(String price) {
        String priceWithoutSign = price.replaceAll("[^0-9.]+", "");
        if (priceWithoutSign == null || priceWithoutSign.isEmpty()) {
            return 0.0d;
        }
        try {
            double result = Double.valueOf(priceWithoutSign).doubleValue();
            return result;
        } catch (NumberFormatException e) {
            log.e(e.getMessage(), e);
            return 0.0d;
        }
    }

    private void setupPreviewImage() {
        this.mThemePreviewView.setWrapperImageWidth(this.themePreviewWidth);
        this.mThemePreviewView.setWrapperImageHeight(this.themePreviewHeight);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.mThemePreviewView.getLayoutParams();
        lp.width = this.themePreviewWidth;
        lp.height = this.themePreviewHeight;
    }

    private void updateConnectDownloadableScreenContent() {
        if (this.downloadableTheme != null) {
            this.mThemeNameView.setText(this.downloadableTheme.getDisplayName(getResources()));
            if (this.downloadableTheme.getThemeItemSeed().isPurchased || this.downloadableTheme.getThemeItemSeed().isFree) {
                this.mThemePriceView.setVisibility(8);
            } else {
                this.mThemePriceView.setVisibility(0);
            }
            this.mThemePriceView.setText(this.downloadableTheme.getPrice());
            setupPreviewImage();
            log.d("pop up dialog width:", Integer.valueOf(this.themePreviewWidth), "  height:", Integer.valueOf(this.themePreviewHeight));
            this.mThemePreviewView.setWrapperImageWidth(this.themePreviewWidth);
            this.mThemePreviewView.setWrapperImageHeight(this.themePreviewHeight);
            ImageCache.with(getApplicationContext()).loadImage(this.downloadableTheme.getPreviewImageUrl(), R.drawable.custom_progressbar_indeterminate, this.mThemePreviewView);
        }
    }

    protected void purchaseItem() {
        if (this.downloadableTheme != null) {
            this.downloadableTheme.purchase(this, REQUEST_PURCHASE, this.mPurchaseFinishedListener, getApplicationContext());
        }
    }

    private void recordPurchaseDoneInfo(String price, UsageData.PurchaseType type) {
        UsageData.recordStoreTransactionComplete(this.themesku, price, type, UsageData.PaymentProvider.GOOGLE_PLAY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recordStoreTransactionFailed(int errorCode, String desc) {
        UsageData.recordStoreTransactionFailed(this.themesku, UsageData.PaymentProvider.GOOGLE_PLAY, desc, errorCode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recordThemesPreview(UsageDataEventThemesPreview.Action action, UsageDataEventThemesPreview.Result result, String sku, String from, int position, String category) {
        UsageDataEventThemesPreview usageDataEventThemesPreview = new UsageDataEventThemesPreview();
        usageDataEventThemesPreview.mAction = action;
        usageDataEventThemesPreview.mResult = result;
        usageDataEventThemesPreview.mThemeName = sku;
        usageDataEventThemesPreview.mPreviewedFrom = from;
        usageDataEventThemesPreview.mPosition = Integer.valueOf(position);
        usageDataEventThemesPreview.mCategory = category;
        usageDataEventThemesPreview.commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recordBundleThemesPreview(String errors, String purchase, String action, String apply, String from, String position, String category) {
        Map<String, String> usageData = new HashMap<>();
        if (errors != null && !errors.isEmpty()) {
            usageData.put("Errors", errors);
        }
        usageData.put("Theme Name", category);
        usageData.put("Apply", apply);
        usageData.put("Purchase", purchase);
        usageData.put("Action", action);
        usageData.put("Previewed From", from);
        usageData.put("Position", position);
        usageData.put("Category", category);
        UsageData.recordEvent(UsageData.Event.BUNDLE_PREVIEW, usageData);
    }

    private void updateFreeThemeScreenContent() {
        if (this.swypeTheme != null) {
            this.mThemeNameView.setText(this.swypeTheme.getDisplayName(getResources()));
            this.mThemePriceView.setVisibility(8);
            setupPreviewImage();
            ImageCache.with(getApplicationContext()).loadImage(this.swypeTheme.getKeyboardPreviewResId() != 0 ? this.swypeTheme.getKeyboardPreviewResId() : this.swypeTheme.getPreviewResId() + 255, R.drawable.custom_progressbar_indeterminate, this.mThemePreviewView);
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10001) {
            if (resultCode == -1) {
                recordPurchaseDoneInfo(this.downloadableTheme.getPrice(), this.downloadableTheme.getType() == CatalogService.CatalogItem.Type.BUNDLE.ordinal() ? UsageData.PurchaseType.BUNDLE : UsageData.PurchaseType.THEME);
            }
            ThemeManager.from(getApplicationContext()).getThemePurchaser(getApplicationContext()).handlePurchaseResults$40bae86d(requestCode, resultCode, data);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
