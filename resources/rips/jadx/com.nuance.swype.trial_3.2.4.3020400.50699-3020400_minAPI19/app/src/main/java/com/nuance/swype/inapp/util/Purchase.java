package com.nuance.swype.inapp.util;

import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class Purchase {
    public String mDeveloperPayload;
    String mItemType;
    String mOrderId;
    String mOriginalJson;
    String mPackageName;
    int mPurchaseState;
    long mPurchaseTime;
    String mSignature;
    public String mSku;
    String mToken;

    public Purchase(String itemType, String jsonPurchaseInfo, String signature) throws JSONException {
        this.mItemType = itemType;
        this.mOriginalJson = jsonPurchaseInfo;
        JSONObject o = new JSONObject(this.mOriginalJson);
        this.mOrderId = o.optString("orderId");
        this.mPackageName = o.optString("packageName");
        this.mSku = o.optString("productId");
        this.mPurchaseTime = o.optLong("purchaseTime");
        this.mPurchaseState = o.optInt("purchaseState");
        this.mDeveloperPayload = o.optString("developerPayload");
        this.mToken = o.optString("token", o.optString("purchaseToken"));
        this.mSignature = signature;
    }

    public final String toString() {
        return "PurchaseInfo(type:" + this.mItemType + "):" + this.mOriginalJson;
    }
}
