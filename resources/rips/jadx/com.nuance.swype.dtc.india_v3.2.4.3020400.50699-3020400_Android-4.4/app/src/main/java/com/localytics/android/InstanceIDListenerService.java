package com.localytics.android;

import android.content.Context;
import android.text.TextUtils;
import com.localytics.android.Localytics;

/* loaded from: classes.dex */
public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {
    public void onTokenRefresh() {
        try {
            Context context = getApplicationContext();
            String appKey = DatapointHelper.getLocalyticsAppKeyOrNull(context);
            if (!TextUtils.isEmpty(appKey)) {
                Localytics.integrate(context, appKey);
            }
            Localytics.Log.v("InstanceID token is updated");
            Localytics.retrieveTokenFromInstanceId();
        } catch (Exception e) {
            Localytics.Log.w("Something went wrong with GCM token refresh");
        }
    }
}
