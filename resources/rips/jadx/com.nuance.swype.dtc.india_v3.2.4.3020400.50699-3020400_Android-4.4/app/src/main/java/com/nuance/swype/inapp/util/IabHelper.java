package com.nuance.swype.inapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.android.vending.billing.IInAppBillingService;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;

/* loaded from: classes.dex */
public final class IabHelper {
    public Context mContext;
    public OnIabPurchaseFinishedListener mPurchaseListener;
    public String mPurchasingItemType;
    public int mRequestCode;
    public IInAppBillingService mService;
    public ServiceConnection mServiceConn;
    String mSignatureBase64;
    boolean mDebugLog = false;
    String mDebugTag = "IabHelper";
    public boolean mSetupDone = false;
    public boolean mIsBound = false;
    public boolean mDisposed = false;
    public boolean mSubscriptionsSupported = false;
    boolean mAsyncInProgress = false;
    String mAsyncOperation = "";

    /* loaded from: classes.dex */
    public interface OnIabPurchaseFinishedListener {
        void onIabPurchaseFinished(IabResult iabResult, Purchase purchase);
    }

    /* loaded from: classes.dex */
    public interface OnIabSetupFinishedListener {
        void onIabSetupFinished(IabResult iabResult);
    }

    /* loaded from: classes.dex */
    public interface QueryInventoryFinishedListener {
        void onQueryInventoryFinished(IabResult iabResult, Inventory inventory);
    }

    public IabHelper(Context ctx, String base64PublicKey) {
        this.mSignatureBase64 = null;
        this.mContext = ctx.getApplicationContext();
        this.mSignatureBase64 = base64PublicKey;
    }

    public final void checkNotDisposed() {
        if (this.mDisposed) {
            throw new IllegalStateException("IabHelper was disposed of, so it cannot be used.");
        }
    }

    public final boolean handleActivityResult$40bae869(int requestCode, int resultCode, Intent data) {
        int responseCode;
        if (requestCode != this.mRequestCode) {
            return false;
        }
        checkNotDisposed();
        if (!checkSetupDone("handleActivityResult")) {
            return false;
        }
        flagEndAsync();
        if (data == null) {
            logError("Null data in IAB activity result.");
            IabResult result = new IabResult(-1002, "Null data in IAB result");
            if (this.mPurchaseListener != null) {
                this.mPurchaseListener.onIabPurchaseFinished(result, null);
            }
            return true;
        }
        Object obj = data.getExtras().get("RESPONSE_CODE");
        if (obj == null) {
            logError("Intent with no response code, assuming OK (known issue)");
            responseCode = 0;
        } else if (obj instanceof Integer) {
            responseCode = ((Integer) obj).intValue();
        } else {
            if (!(obj instanceof Long)) {
                logError("Unexpected type for intent response code.");
                logError(obj.getClass().getName());
                throw new RuntimeException("Unexpected type for intent response code: " + obj.getClass().getName());
            }
            responseCode = (int) ((Long) obj).longValue();
        }
        String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
        String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
        if (resultCode == -1 && responseCode == 0) {
            new StringBuilder("Extras: ").append(data.getExtras());
            new StringBuilder("Expected item type: ").append(this.mPurchasingItemType);
            if (purchaseData == null || dataSignature == null) {
                logError("BUG: either purchaseData or dataSignature is null.");
                new StringBuilder("Extras: ").append(data.getExtras().toString());
                IabResult result2 = new IabResult(-1008, "IAB returned null purchaseData or dataSignature");
                if (this.mPurchaseListener != null) {
                    this.mPurchaseListener.onIabPurchaseFinished(result2, null);
                }
                return true;
            }
            try {
                Purchase purchase = new Purchase(this.mPurchasingItemType, purchaseData, dataSignature);
                String sku = purchase.mSku;
                if (!Security.verifyPurchase(this.mSignatureBase64, purchaseData, dataSignature)) {
                    logError("Purchase signature verification FAILED for sku " + sku);
                    IabResult result3 = new IabResult(-1003, "Signature verification failed for sku " + sku);
                    if (this.mPurchaseListener != null) {
                        this.mPurchaseListener.onIabPurchaseFinished(result3, purchase);
                    }
                    return true;
                }
                if (this.mPurchaseListener != null) {
                    this.mPurchaseListener.onIabPurchaseFinished(new IabResult(0, "Success"), purchase);
                }
            } catch (JSONException e) {
                logError("Failed to parse purchase data.");
                e.printStackTrace();
                IabResult result4 = new IabResult(-1002, "Failed to parse purchase data.");
                if (this.mPurchaseListener != null) {
                    this.mPurchaseListener.onIabPurchaseFinished(result4, null);
                }
                return true;
            }
        } else if (resultCode == -1) {
            logError("Result code was OK but in-app billing response was not OK: " + getResponseDesc(responseCode));
            if (this.mPurchaseListener != null) {
                IabResult result5 = new IabResult(responseCode, "Problem purchashing item.");
                this.mPurchaseListener.onIabPurchaseFinished(result5, null);
            }
        } else if (resultCode == 0) {
            logError("Purchase canceled - Response: " + getResponseDesc(responseCode));
            IabResult result6 = new IabResult(responseCode, "User canceled.");
            if (this.mPurchaseListener != null) {
                this.mPurchaseListener.onIabPurchaseFinished(result6, null);
            }
        } else {
            logError("Purchase failed. Result code: " + Integer.toString(resultCode) + ". Response: " + getResponseDesc(responseCode));
            IabResult result7 = new IabResult(responseCode, "Unknown purchase response.");
            if (this.mPurchaseListener != null) {
                this.mPurchaseListener.onIabPurchaseFinished(result7, null);
            }
        }
        return true;
    }

    public final Inventory queryInventory$4f5be2a0(boolean querySkuDetails, List<String> moreItemSkus) throws IabException {
        int r;
        int r2;
        checkNotDisposed();
        if (!checkSetupDone("queryInventory")) {
            throw new IabException(6, "Error refreshing inventory (setup not done).");
        }
        try {
            Inventory inv = new Inventory();
            int r3 = queryPurchases(inv, "inapp");
            if (r3 != 0) {
                throw new IabException(r3, "Error refreshing inventory (querying owned items).");
            }
            if (querySkuDetails && (r2 = querySkuDetails("inapp", inv, moreItemSkus)) != 0) {
                throw new IabException(r2, "Error refreshing inventory (querying prices of items).");
            }
            if (this.mSubscriptionsSupported) {
                int r4 = queryPurchases(inv, "subs");
                if (r4 != 0) {
                    throw new IabException(r4, "Error refreshing inventory (querying owned subscriptions).");
                }
                if (querySkuDetails && (r = querySkuDetails("subs", inv, moreItemSkus)) != 0) {
                    throw new IabException(r, "Error refreshing inventory (querying prices of subscriptions).");
                }
            }
            return inv;
        } catch (RemoteException e) {
            throw new IabException(-1001, "Remote exception while refreshing inventory.", e);
        } catch (JSONException e2) {
            throw new IabException(-1002, "Error parsing JSON response while refreshing inventory.", e2);
        }
    }

    public final void queryInventoryAsync$8e2a111(final List<String> moreSkus, final QueryInventoryFinishedListener listener) {
        final Handler handler = new Handler();
        checkNotDisposed();
        if (!checkSetupDone("queryInventory")) {
            if (listener != null) {
                listener.onQueryInventoryFinished(new IabResult(-1001, "RemoteException while setting up in-app billing."), null);
                return;
            }
            return;
        }
        try {
            flagStartAsync("refresh inventory");
            new Thread(new Runnable() { // from class: com.nuance.swype.inapp.util.IabHelper.2
                final /* synthetic */ boolean val$querySkuDetails = true;

                @Override // java.lang.Runnable
                public final void run() {
                    IabResult result = new IabResult(0, "Inventory refresh successful.");
                    Inventory inv = null;
                    try {
                        inv = IabHelper.this.queryInventory$4f5be2a0(this.val$querySkuDetails, moreSkus);
                    } catch (IabException e) {
                        result = e.mResult;
                    } catch (IllegalStateException e2) {
                        return;
                    }
                    IabHelper.this.flagEndAsync();
                    final IabResult result_f = result;
                    final Inventory inv_f = inv;
                    if (!IabHelper.this.mDisposed && listener != null) {
                        handler.post(new Runnable() { // from class: com.nuance.swype.inapp.util.IabHelper.2.1
                            @Override // java.lang.Runnable
                            public final void run() {
                                listener.onQueryInventoryFinished(result_f, inv_f);
                            }
                        });
                    }
                }
            }).start();
        } catch (IllegalStateException e) {
            IabResult r = new IabResult(-1001, "Can't start another async operation.");
            if (listener != null) {
                listener.onQueryInventoryFinished(r, null);
            }
        }
    }

    public static String getResponseDesc(int code) {
        String[] iab_msgs = "0:OK/1:User Canceled/2:Unknown/3:Billing Unavailable/4:Item unavailable/5:Developer Error/6:Error/7:Item Already Owned/8:Item not owned".split("/");
        String[] iabhelper_msgs = "0:OK/-1001:Remote exception during initialization/-1002:Bad response received/-1003:Purchase signature verification failed/-1004:Send intent failed/-1005:User cancelled/-1006:Unknown purchase response/-1007:Missing token/-1008:Unknown error/-1009:Subscriptions not available/-1010:Invalid consumption attempt".split("/");
        if (code <= -1000) {
            int index = (-1000) - code;
            return (index < 0 || index >= iabhelper_msgs.length) ? String.valueOf(code) + ":Unknown IAB Helper Error" : iabhelper_msgs[index];
        }
        if (code < 0 || code >= iab_msgs.length) {
            return String.valueOf(code) + ":Unknown";
        }
        return iab_msgs[code];
    }

    public static String getResponseDetailedDesc(String errorStr, int errorCode) {
        return errorStr + XMLResultsHandler.SEP_SPACE + getResponseDesc(errorCode);
    }

    public final boolean checkSetupDone(String operation) {
        if (this.mSetupDone) {
            return true;
        }
        logError("Illegal state for operation (" + operation + "): IAB helper is not set up.");
        return false;
    }

    public final int getResponseCodeFromBundle(Bundle b) {
        Object o = b.get("RESPONSE_CODE");
        if (o == null) {
            return 0;
        }
        if (o instanceof Integer) {
            return ((Integer) o).intValue();
        }
        if (o instanceof Long) {
            return (int) ((Long) o).longValue();
        }
        logError("Unexpected type for bundle response code.");
        logError(o.getClass().getName());
        throw new RuntimeException("Unexpected type for bundle response code: " + o.getClass().getName());
    }

    public final void flagStartAsync(String operation) {
        if (this.mAsyncInProgress) {
            throw new IllegalStateException("Can't start async operation (" + operation + ") because another async operation(" + this.mAsyncOperation + ") is in progress.");
        }
        this.mAsyncOperation = operation;
        this.mAsyncInProgress = true;
    }

    public final void flagEndAsync() {
        new StringBuilder("Ending async operation: ").append(this.mAsyncOperation);
        this.mAsyncOperation = "";
        this.mAsyncInProgress = false;
    }

    private int queryPurchases(Inventory inv, String itemType) throws JSONException, RemoteException {
        new StringBuilder("Package name: ").append(this.mContext.getPackageName());
        boolean verificationFailed = false;
        String continueToken = null;
        while (this.mService != null) {
            Bundle ownedItems = this.mService.getPurchases(3, this.mContext.getPackageName(), itemType, continueToken);
            int response = getResponseCodeFromBundle(ownedItems);
            new StringBuilder("Owned items response: ").append(String.valueOf(response));
            if (response != 0) {
                new StringBuilder("getPurchases() failed: ").append(getResponseDesc(response));
                return response;
            }
            if (!ownedItems.containsKey("INAPP_PURCHASE_ITEM_LIST") || !ownedItems.containsKey("INAPP_PURCHASE_DATA_LIST") || !ownedItems.containsKey("INAPP_DATA_SIGNATURE_LIST")) {
                logError("Bundle returned from getPurchases() doesn't contain required fields.");
                return -1002;
            }
            ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
            for (int i = 0; i < purchaseDataList.size(); i++) {
                String purchaseData = purchaseDataList.get(i);
                String signature = signatureList.get(i);
                ownedSkus.get(i);
                if (Security.verifyPurchase(this.mSignatureBase64, purchaseData, signature)) {
                    Purchase purchase = new Purchase(itemType, purchaseData, signature);
                    if (TextUtils.isEmpty(purchase.mToken)) {
                        logWarn("BUG: empty/null token!");
                    }
                    inv.mPurchaseMap.put(purchase.mSku, purchase);
                } else {
                    logWarn("Purchase signature verification **FAILED**. Not adding item.");
                    verificationFailed = true;
                }
            }
            continueToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");
            if (TextUtils.isEmpty(continueToken)) {
                return verificationFailed ? -1003 : 0;
            }
        }
        return -1002;
    }

    private int querySkuDetails(String itemType, Inventory inv, List<String> moreSkus) throws RemoteException, JSONException {
        if (this.mService == null || this.mContext == null) {
            return 6;
        }
        ArrayList<String> skuList = new ArrayList<>();
        skuList.addAll(inv.getAllOwnedSkus(itemType));
        if (moreSkus != null) {
            for (String sku : moreSkus) {
                if (!skuList.contains(sku)) {
                    skuList.add(sku);
                }
            }
        }
        if (skuList.size() == 0) {
            return 0;
        }
        while (skuList.size() > 0) {
            ArrayList<String> skuSubList = new ArrayList<>(skuList.subList(0, Math.min(19, skuList.size())));
            skuList.removeAll(skuSubList);
            Bundle querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", skuSubList);
            if (this.mService == null || this.mContext == null) {
                logError("mService and/or mContext are null.  Exiting.");
                return 6;
            }
            Bundle skuDetails = this.mService.getSkuDetails(3, this.mContext.getPackageName(), itemType, querySkus);
            if (!skuDetails.containsKey("DETAILS_LIST")) {
                int response = getResponseCodeFromBundle(skuDetails);
                if (response != 0) {
                    new StringBuilder("getSkuDetails() failed: ").append(getResponseDesc(response));
                    return response;
                }
                logError("getSkuDetails() returned a bundle with neither an error nor a detail list.");
                return -1002;
            }
            Iterator<String> it = skuDetails.getStringArrayList("DETAILS_LIST").iterator();
            while (it.hasNext()) {
                String thisResponse = it.next();
                SkuDetails d = new SkuDetails(itemType, thisResponse);
                new StringBuilder("Got sku details: ").append(d);
                inv.mSkuMap.put(d.mSku, d);
            }
        }
        return 0;
    }

    public final void logError(String msg) {
        Log.e(this.mDebugTag, "In-app billing error: " + msg);
    }

    private void logWarn(String msg) {
        Log.w(this.mDebugTag, "In-app billing warning: " + msg);
    }
}
