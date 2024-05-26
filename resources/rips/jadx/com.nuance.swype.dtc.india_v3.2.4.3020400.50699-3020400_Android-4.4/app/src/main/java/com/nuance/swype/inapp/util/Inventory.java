package com.nuance.swype.inapp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class Inventory {
    public Map<String, SkuDetails> mSkuMap = new HashMap();
    public Map<String, Purchase> mPurchaseMap = new HashMap();

    public final SkuDetails getSkuDetails(String sku) {
        return this.mSkuMap.get(sku);
    }

    public final boolean hasPurchase(String sku) {
        return this.mPurchaseMap.containsKey(sku);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List<String> getAllOwnedSkus(String itemType) {
        List<String> result = new ArrayList<>();
        for (Purchase p : this.mPurchaseMap.values()) {
            if (p.mItemType.equals(itemType)) {
                result.add(p.mSku);
            }
        }
        return result;
    }
}
