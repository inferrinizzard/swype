package com.localytics.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

/* loaded from: classes.dex */
public class ReferralReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                extras.containsKey(null);
            }
            if (intent.getAction().equals("com.android.vending.INSTALL_REFERRER")) {
                String appKey = DatapointHelper.getLocalyticsAppKeyOrNull(context);
                if (!TextUtils.isEmpty(appKey)) {
                    Localytics.integrate(context.getApplicationContext(), appKey);
                }
                String referrer = intent.getStringExtra("referrer");
                if (!TextUtils.isEmpty(referrer)) {
                    Localytics.setReferrerId(referrer);
                }
            }
        } catch (Exception e) {
        }
    }
}
