package com.nuance.swype.inapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import com.android.vending.billing.IInAppBillingService;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.inapp.CatalogManager;
import com.nuance.swype.inapp.util.IabHelper;
import com.nuance.swype.inapp.util.IabResult;
import com.nuance.swype.inapp.util.Inventory;
import com.nuance.swype.inapp.util.Purchase;
import com.nuance.swype.inapp.util.SkuDetails;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.service.impl.AccountUtil;
import com.nuance.swype.util.LogManager;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.json.JSONException;

/* loaded from: classes.dex */
public class InAppPurchaser {
    protected static final LogManager.Log log = LogManager.getLog("InAppPurchaser");
    private final Context context;
    private WeakReference<Context> mAppContext;
    private PurchaseRequestBundle mCurrentPurchaseRequestBundle;
    protected IabHelper mIabHelper;
    private List<String> mQuerySkuList;
    IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() { // from class: com.nuance.swype.inapp.InAppPurchaser.4
        @Override // com.nuance.swype.inapp.util.IabHelper.QueryInventoryFinishedListener
        public final void onQueryInventoryFinished(IabResult result, Inventory inv) {
            InAppPurchaser.log.d("query completed with result: " + result.isSuccess());
            CatalogManager catalogManager = IMEApplication.from(InAppPurchaser.this.context).getCatalogManager();
            CatalogManager.PriceMap priceMap = new CatalogManager.PriceMap();
            if (result.isSuccess()) {
                InAppPurchaser.log.d("Updating purchased list with the following: ");
                Set<String> allSkus = new HashSet<>();
                allSkus.addAll(InAppPurchaser.this.mQuerySkuList);
                allSkus.addAll(new ArrayList<>(inv.mPurchaseMap.keySet()));
                for (String id : allSkus) {
                    if (inv.mSkuMap.containsKey(id)) {
                        LogManager.Log log2 = InAppPurchaser.log;
                        Object[] objArr = new Object[1];
                        objArr[0] = "\t\t*** Play results (from Play cache) *** id: " + id + " *** price: " + inv.getSkuDetails(id).mPrice + " *** isPurchased: " + (inv.hasPurchase(id) ? "yes" : "no") + " *** ";
                        log2.d(objArr);
                        InAppPurchaser.this.mSkuDetails.put(id, inv.getSkuDetails(id));
                        priceMap.addSkuWithPrice(id, inv.getSkuDetails(id).mPrice);
                        if (inv.hasPurchase(id)) {
                            priceMap.mPurchasedMap.put(id, true);
                        }
                    } else {
                        InAppPurchaser.this.mSkuDetails.remove(id);
                    }
                }
                if (IMEApplication.from(InAppPurchaser.this.context).getThemeManager().hasNoPriceThemes()) {
                    IMEApplication.from(InAppPurchaser.this.context).resetThemeManagedData();
                }
                catalogManager.sendPurchaseInfoToConnect(priceMap);
            } else {
                if (result.mResponse == 6 && IabHelper.getResponseDetailedDesc("Error refreshing inventory (querying prices of items).", result.mResponse).equals(result.mMessage)) {
                    AccountUtil.setIsGoogleAccountSignedIn(false);
                }
                if (catalogManager.getCatalogService() != null) {
                    catalogManager.getCatalogService().resetCatalogItemPrice();
                }
                catalogManager.triggerShowingThemesWithoutPrices();
                InAppPurchaser.log.d("query failed: " + result.mMessage, " ...show all themes without no prices");
            }
            InAppPurchaser.this.cleanup(result);
        }
    };
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() { // from class: com.nuance.swype.inapp.InAppPurchaser.6
        @Override // com.nuance.swype.inapp.util.IabHelper.OnIabPurchaseFinishedListener
        public final void onIabPurchaseFinished(IabResult result, Purchase info) {
            InAppPurchaser.log.d("mPurchaseFinishedListener called with result: " + result.isSuccess());
            if (result.isSuccess() && InAppPurchaser.this.mCurrentPurchaseRequestBundle != null) {
                InAppPurchaser.log.d("purchase successful");
                InAppPurchaser.log.d("developer token is: " + info.mDeveloperPayload + ", actual is: " + InAppPurchaser.this.mCurrentPurchaseRequestBundle.mDeveloperString);
                InAppPurchaser.log.d("sku is: " + info.mSku + ", actual is " + InAppPurchaser.this.mCurrentPurchaseRequestBundle.mSku);
                if (info.mDeveloperPayload.equals(InAppPurchaser.this.mCurrentPurchaseRequestBundle.mDeveloperString) && info.mSku.equals(InAppPurchaser.this.mCurrentPurchaseRequestBundle.mSku)) {
                    InAppPurchaser.log.d("Adding purchase to the list of purchased items. Item " + info.mSku);
                }
            } else {
                InAppPurchaser.log.d("purchase failed");
            }
            if (InAppPurchaser.this.mCurrentPurchaseRequestBundle.getNextInLine() != null) {
                InAppPurchaser.log.d("calling chain method");
                InAppPurchaser.this.mCurrentPurchaseRequestBundle.getNextInLine().onIabPurchaseFinished(result, info);
            }
            InAppPurchaser.this.cleanup(result);
        }
    };
    protected Map<String, SkuDetails> mSkuDetails = new HashMap();

    public InAppPurchaser(Context context) {
        this.mAppContext = new WeakReference<>(context.getApplicationContext());
        this.context = context;
    }

    private void setup(final IabHelper.OnIabSetupFinishedListener listener) {
        if (this.mAppContext == null || this.mAppContext.get() == null) {
            log.e("in-app setup failure. mAppContext or mAppContext.get() is null");
            return;
        }
        if (this.mIabHelper == null) {
            BuildInfo info = BuildInfo.from(this.context);
            if (info != null && info.isDownloadableThemesEnabled()) {
                this.mIabHelper = new IabHelper(this.mAppContext.get(), info.getGooglePlayPublicKey());
            } else {
                log.e("build type does not allow in-app for Play");
                this.mIabHelper = new IabHelper(this.mAppContext.get(), XMLResultsHandler.SEP_SPACE);
            }
        }
        try {
            IabHelper iabHelper = this.mIabHelper;
            IabHelper.OnIabSetupFinishedListener onIabSetupFinishedListener = new IabHelper.OnIabSetupFinishedListener() { // from class: com.nuance.swype.inapp.InAppPurchaser.1
                @Override // com.nuance.swype.inapp.util.IabHelper.OnIabSetupFinishedListener
                public final void onIabSetupFinished(IabResult result) {
                    if (result.isSuccess()) {
                        InAppPurchaser.log.d("in-app setup complete");
                    } else {
                        InAppPurchaser.log.d("in-app setup failed: ", result.mMessage);
                    }
                    if (InAppPurchaser.this.mIabHelper != null) {
                        InAppPurchaser.this.mIabHelper.flagEndAsync();
                    }
                    if (listener != null) {
                        listener.onIabSetupFinished(result);
                    }
                }
            };
            iabHelper.checkNotDisposed();
            if (iabHelper.mSetupDone) {
                throw new IllegalStateException("IAB helper is already set up.");
            }
            iabHelper.mServiceConn = new ServiceConnection() { // from class: com.nuance.swype.inapp.util.IabHelper.1
                final /* synthetic */ OnIabSetupFinishedListener val$listener;

                public AnonymousClass1(OnIabSetupFinishedListener onIabSetupFinishedListener2) {
                    r2 = onIabSetupFinishedListener2;
                }

                @Override // android.content.ServiceConnection
                public final void onServiceDisconnected(ComponentName name) {
                    IabHelper.this.mService = null;
                }

                @Override // android.content.ServiceConnection
                public final void onServiceConnected(ComponentName name, IBinder service) {
                    if (!IabHelper.this.mDisposed) {
                        IabHelper.this.mService = IInAppBillingService.Stub.asInterface(service);
                        String packageName = IabHelper.this.mContext.getPackageName();
                        try {
                            int response = IabHelper.this.mService.isBillingSupported(3, packageName, "inapp");
                            if (response != 0) {
                                if (r2 != null) {
                                    r2.onIabSetupFinished(new IabResult(response, "inapp_not_supported"));
                                }
                                IabHelper.this.mSubscriptionsSupported = false;
                            } else {
                                if (IabHelper.this.mService.isBillingSupported(3, packageName, "subs") == 0) {
                                    IabHelper.this.mSubscriptionsSupported = true;
                                }
                                IabHelper.this.mSetupDone = true;
                                if (r2 != null) {
                                    r2.onIabSetupFinished(new IabResult(0, "Setup successful."));
                                }
                            }
                        } catch (RemoteException e) {
                            if (r2 != null) {
                                r2.onIabSetupFinished(new IabResult(-1001, "RemoteException while setting up in-app billing."));
                            }
                            e.printStackTrace();
                        }
                    }
                }
            };
            Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
            intent.setPackage("com.android.vending");
            List<ResolveInfo> queryIntentServices = iabHelper.mContext.getPackageManager().queryIntentServices(intent, 0);
            if (queryIntentServices != null && !queryIntentServices.isEmpty()) {
                iabHelper.mIsBound = iabHelper.mContext.bindService(intent, iabHelper.mServiceConn, 1);
            } else {
                onIabSetupFinishedListener2.onIabSetupFinished(new IabResult(3, "Billing_service_unavailable"));
            }
        } catch (IllegalStateException e) {
            log.d("dispose destroyed.");
        }
    }

    public final void cleanup(IabResult result) {
        if (this.mIabHelper != null) {
            if (result.mResponse == 3) {
                IabHelper iabHelper = this.mIabHelper;
                iabHelper.mSetupDone = false;
                if (iabHelper.mServiceConn != null) {
                    iabHelper.mServiceConn = null;
                    iabHelper.mService = null;
                    iabHelper.mPurchaseListener = null;
                }
                iabHelper.mDisposed = true;
            } else {
                IabHelper iabHelper2 = this.mIabHelper;
                iabHelper2.mSetupDone = false;
                if (iabHelper2.mServiceConn != null) {
                    if (iabHelper2.mContext != null) {
                        iabHelper2.mContext.unbindService(iabHelper2.mServiceConn);
                    }
                    iabHelper2.mServiceConn = null;
                    iabHelper2.mService = null;
                    iabHelper2.mPurchaseListener = null;
                }
                iabHelper2.mDisposed = true;
            }
            this.mIabHelper.flagEndAsync();
        }
        this.mIabHelper = null;
    }

    public final void setupInAppBillingService(final IabHelper.OnIabSetupFinishedListener listener) {
        if (isSetupDone()) {
            listener.onIabSetupFinished(new IabResult(0, "Setup successful."));
        } else {
            setup(new IabHelper.OnIabSetupFinishedListener() { // from class: com.nuance.swype.inapp.InAppPurchaser.2
                @Override // com.nuance.swype.inapp.util.IabHelper.OnIabSetupFinishedListener
                public final void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        InAppPurchaser.this.cleanup(result);
                    }
                    if (InAppPurchaser.this.mIabHelper != null) {
                        InAppPurchaser.this.mIabHelper.flagEndAsync();
                    }
                    if (listener != null) {
                        listener.onIabSetupFinished(result);
                    }
                }
            });
        }
    }

    private boolean isSetupDone() {
        return this.mIabHelper != null && this.mIabHelper.checkSetupDone("inAppPurchaser");
    }

    public final void queryItems(final List<String> skuIdList, final IabHelper.OnIabSetupFinishedListener listener) {
        this.mQuerySkuList = skuIdList;
        if (isSetupDone()) {
            try {
                this.mIabHelper.queryInventoryAsync$8e2a111(skuIdList, this.mQueryFinishedListener);
                return;
            } catch (IllegalStateException e) {
                log.d("dispose destroyed.");
                return;
            }
        }
        setup(new IabHelper.OnIabSetupFinishedListener() { // from class: com.nuance.swype.inapp.InAppPurchaser.3
            @Override // com.nuance.swype.inapp.util.IabHelper.OnIabSetupFinishedListener
            public final void onIabSetupFinished(IabResult result) {
                if (result.isSuccess()) {
                    try {
                        InAppPurchaser.this.mIabHelper.queryInventoryAsync$8e2a111(skuIdList, InAppPurchaser.this.mQueryFinishedListener);
                    } catch (IllegalStateException e2) {
                        InAppPurchaser.log.d("dispose destroyed.");
                        return;
                    }
                } else {
                    InAppPurchaser.this.cleanup(result);
                }
                if (InAppPurchaser.this.mIabHelper != null) {
                    InAppPurchaser.this.mIabHelper.flagEndAsync();
                }
                if (listener != null) {
                    listener.onIabSetupFinished(result);
                }
            }
        });
    }

    public final void purchase(final Activity activity, final String sku, final int requestCode, final IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener) {
        if (isSetupDone()) {
            doPurchase(activity, sku, requestCode, purchaseFinishedListener);
        } else {
            setup(new IabHelper.OnIabSetupFinishedListener() { // from class: com.nuance.swype.inapp.InAppPurchaser.5
                @Override // com.nuance.swype.inapp.util.IabHelper.OnIabSetupFinishedListener
                public final void onIabSetupFinished(IabResult result) {
                    if (result.isSuccess()) {
                        InAppPurchaser.this.doPurchase(activity, sku, requestCode, purchaseFinishedListener);
                    } else {
                        purchaseFinishedListener.onIabPurchaseFinished(result, null);
                        InAppPurchaser.this.cleanup(result);
                    }
                    if (InAppPurchaser.this.mIabHelper != null) {
                        InAppPurchaser.this.mIabHelper.flagEndAsync();
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:25:0x00de -> B:36:0x0052). Please report as a decompilation issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:26:0x00e0 -> B:36:0x0052). Please report as a decompilation issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:29:0x0142 -> B:36:0x0052). Please report as a decompilation issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:30:0x0144 -> B:36:0x0052). Please report as a decompilation issue!!! */
    public void doPurchase(Activity activity, String sku, int requestCode, IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener) {
        this.mCurrentPurchaseRequestBundle = new PurchaseRequestBundle(sku, purchaseFinishedListener);
        log.d("Stating purchase flow");
        try {
            IabHelper iabHelper = this.mIabHelper;
            IabHelper.OnIabPurchaseFinishedListener onIabPurchaseFinishedListener = this.mPurchaseFinishedListener;
            String str = this.mCurrentPurchaseRequestBundle.mDeveloperString;
            try {
                iabHelper.checkNotDisposed();
                if (iabHelper.checkSetupDone("launchPurchaseFlow")) {
                    try {
                        iabHelper.flagStartAsync("launchPurchaseFlow");
                        if (!"inapp".equals("subs") || iabHelper.mSubscriptionsSupported) {
                            try {
                                new StringBuilder("Constructing buy intent for ").append(sku).append(", item type: ").append("inapp");
                                Bundle buyIntent = iabHelper.mService.getBuyIntent(3, iabHelper.mContext.getPackageName(), sku, "inapp", str);
                                int responseCodeFromBundle = iabHelper.getResponseCodeFromBundle(buyIntent);
                                if (responseCodeFromBundle != 0) {
                                    iabHelper.logError("Unable to buy item, Error response: " + IabHelper.getResponseDesc(responseCodeFromBundle));
                                    iabHelper.flagEndAsync();
                                    IabResult iabResult = new IabResult(responseCodeFromBundle, "Unable to buy item");
                                    if (onIabPurchaseFinishedListener != null) {
                                        onIabPurchaseFinishedListener.onIabPurchaseFinished(iabResult, null);
                                    }
                                } else {
                                    PendingIntent pendingIntent = (PendingIntent) buyIntent.getParcelable("BUY_INTENT");
                                    new StringBuilder("Launching buy intent for ").append(sku).append(". Request code: ").append(requestCode);
                                    iabHelper.mRequestCode = requestCode;
                                    iabHelper.mPurchaseListener = onIabPurchaseFinishedListener;
                                    iabHelper.mPurchasingItemType = "inapp";
                                    activity.startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, new Intent(), 0, 0, 0);
                                }
                            } catch (IntentSender.SendIntentException e) {
                                iabHelper.logError("SendIntentException while launching purchase flow for sku " + sku);
                                e.printStackTrace();
                                iabHelper.flagEndAsync();
                                IabResult iabResult2 = new IabResult(-1004, "Failed to send intent.");
                                if (onIabPurchaseFinishedListener != null) {
                                    onIabPurchaseFinishedListener.onIabPurchaseFinished(iabResult2, null);
                                }
                            } catch (RemoteException e2) {
                                iabHelper.logError("RemoteException while launching purchase flow for sku " + sku);
                                e2.printStackTrace();
                                iabHelper.flagEndAsync();
                                IabResult iabResult3 = new IabResult(-1001, "Remote exception while starting purchase flow");
                                if (onIabPurchaseFinishedListener != null) {
                                    onIabPurchaseFinishedListener.onIabPurchaseFinished(iabResult3, null);
                                }
                            }
                        } else {
                            IabResult iabResult4 = new IabResult(-1009, "Subscriptions are not available.");
                            iabHelper.flagEndAsync();
                            if (onIabPurchaseFinishedListener != null) {
                                onIabPurchaseFinishedListener.onIabPurchaseFinished(iabResult4, null);
                            }
                        }
                    } catch (IllegalStateException e3) {
                        IabResult iabResult5 = new IabResult(-1001, "Can't start another async operation.");
                        if (onIabPurchaseFinishedListener != null) {
                            onIabPurchaseFinishedListener.onIabPurchaseFinished(iabResult5, null);
                        }
                    }
                }
            } catch (IllegalStateException e4) {
            }
        } catch (IllegalStateException e5) {
            log.d("dispose destroyed.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PurchaseRequestBundle {
        String mDeveloperString = generateNewToken();
        private WeakReference<IabHelper.OnIabPurchaseFinishedListener> mNextInLine;
        String mSku;

        public PurchaseRequestBundle(String sku, IabHelper.OnIabPurchaseFinishedListener listener) {
            this.mSku = sku;
            this.mNextInLine = new WeakReference<>(listener);
        }

        private static String generateNewToken() {
            try {
                return Base64.encodeToString(UUID.randomUUID().toString().getBytes("UTF-8"), 8);
            } catch (UnsupportedEncodingException e) {
                Log.e("InAppPurchaser", "generateNewToken:", e);
                return null;
            }
        }

        public final IabHelper.OnIabPurchaseFinishedListener getNextInLine() {
            if (this.mNextInLine == null) {
                return null;
            }
            return this.mNextInLine.get();
        }
    }

    public final void handlePurchaseResults$40bae86d(int requestCode, int resultCode, Intent data) {
        int responseCode;
        log.d("handlePurchaseResults called with requestcode: " + requestCode);
        if (this.mIabHelper != null) {
            try {
                this.mIabHelper.handleActivityResult$40bae869(requestCode, resultCode, data);
                return;
            } catch (IllegalStateException e) {
                log.d("dispose destroyed.");
                return;
            }
        }
        if (data != null) {
            Object obj = data.getExtras().get("RESPONSE_CODE");
            if (obj == null) {
                responseCode = 0;
            } else if (obj instanceof Integer) {
                responseCode = ((Integer) obj).intValue();
            } else if (obj instanceof Long) {
                responseCode = (int) ((Long) obj).longValue();
            } else {
                throw new RuntimeException("Unexpected type for intent response code: " + obj.getClass().getName());
            }
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
            if (resultCode == -1 && responseCode == 0 && this.mPurchaseFinishedListener != null) {
                IabResult result = new IabResult(0, "Success");
                if (this.mCurrentPurchaseRequestBundle.getNextInLine() != null) {
                    log.d("calling chain method");
                    try {
                        Purchase purchase = new Purchase("inapp", purchaseData, dataSignature);
                        log.d("sku:", purchase.mSku);
                        this.mCurrentPurchaseRequestBundle.getNextInLine().onIabPurchaseFinished(result, purchase);
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                }
                cleanup(result);
                return;
            }
        }
        if (this.mPurchaseFinishedListener != null) {
            this.mPurchaseFinishedListener.onIabPurchaseFinished(new IabResult(-1005, "User canceled."), null);
        }
    }
}
