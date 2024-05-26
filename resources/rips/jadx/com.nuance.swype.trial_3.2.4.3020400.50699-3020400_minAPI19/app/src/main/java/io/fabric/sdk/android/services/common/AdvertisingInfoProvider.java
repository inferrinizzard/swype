package io.fabric.sdk.android.services.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.persistence.PreferenceStore;
import io.fabric.sdk.android.services.persistence.PreferenceStoreImpl;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class AdvertisingInfoProvider {
    private final Context context;
    final PreferenceStore preferenceStore;

    public AdvertisingInfoProvider(Context context) {
        this.context = context.getApplicationContext();
        this.preferenceStore = new PreferenceStoreImpl(context, "TwitterAdvertisingInfoPreferences");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SuppressLint({"CommitPrefEdits"})
    public final void storeInfoToPreferences(AdvertisingInfo infoToReturn) {
        if (isInfoValid(infoToReturn)) {
            this.preferenceStore.save(this.preferenceStore.edit().putString("advertising_id", infoToReturn.advertisingId).putBoolean("limit_ad_tracking_enabled", infoToReturn.limitAdTrackingEnabled));
        } else {
            this.preferenceStore.save(this.preferenceStore.edit().remove("advertising_id").remove("limit_ad_tracking_enabled"));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isInfoValid(AdvertisingInfo advertisingInfo) {
        return (advertisingInfo == null || TextUtils.isEmpty(advertisingInfo.advertisingId)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final AdvertisingInfo getAdvertisingInfoFromStrategies() {
        AdvertisingInfo infoToReturn = new AdvertisingInfoReflectionStrategy(this.context).getAdvertisingInfo();
        if (!isInfoValid(infoToReturn)) {
            infoToReturn = new AdvertisingInfoServiceStrategy(this.context).getAdvertisingInfo();
            if (!isInfoValid(infoToReturn)) {
                Fabric.getLogger();
            } else {
                Fabric.getLogger();
            }
        } else {
            Fabric.getLogger();
        }
        return infoToReturn;
    }
}
