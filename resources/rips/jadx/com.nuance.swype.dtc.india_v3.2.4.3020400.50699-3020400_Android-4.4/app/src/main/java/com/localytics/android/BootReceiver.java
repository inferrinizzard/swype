package com.localytics.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

/* loaded from: classes.dex */
public class BootReceiver extends BroadcastReceiver {
    /* JADX WARN: Type inference failed for: r1v2, types: [com.localytics.android.BootReceiver$1] */
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String appKey = DatapointHelper.getLocalyticsAppKeyOrNull(context);
        if (!TextUtils.isEmpty(appKey)) {
            Localytics.integrate(context.getApplicationContext(), appKey);
            new AsyncTask<Void, Void, Void>() { // from class: com.localytics.android.BootReceiver.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Void doInBackground(Void... params) {
                    if (Localytics.isLocationMonitoringEnabled()) {
                        Localytics.setLocationMonitoringEnabled(true);
                        return null;
                    }
                    return null;
                }
            }.execute(new Void[0]);
        }
    }
}
