package com.localytics.android;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.localytics.android.Localytics;

/* loaded from: classes.dex */
public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {
    public void onMessageReceived(String from, Bundle data) {
        Context context = getApplicationContext();
        try {
            String appKey = DatapointHelper.getLocalyticsAppKeyOrNull(context);
            if (!TextUtils.isEmpty(appKey)) {
                Localytics.integrate(context, appKey);
            }
            if (!data.isEmpty()) {
                Localytics.handleNotificationReceived(data);
            }
        } catch (Exception e) {
            Localytics.Log.w("Something went wrong with GCM", e);
        }
    }
}
