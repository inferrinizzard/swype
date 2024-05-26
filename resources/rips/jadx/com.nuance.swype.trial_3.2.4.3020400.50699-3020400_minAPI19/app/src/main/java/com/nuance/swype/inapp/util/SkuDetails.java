package com.nuance.swype.inapp.util;

import com.facebook.share.internal.ShareConstants;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class SkuDetails {
    String mDescription;
    String mItemType;
    String mJson;
    public String mPrice;
    String mSku;
    String mTitle;
    String mType;

    public SkuDetails(String itemType, String jsonSkuDetails) throws JSONException {
        this.mItemType = itemType;
        this.mJson = jsonSkuDetails;
        JSONObject o = new JSONObject(this.mJson);
        this.mSku = o.optString("productId");
        this.mType = o.optString("type");
        this.mPrice = o.optString("price");
        this.mTitle = o.optString(ShareConstants.WEB_DIALOG_PARAM_TITLE);
        this.mDescription = o.optString("description");
    }

    public final String toString() {
        return "SkuDetails:" + this.mJson;
    }
}
