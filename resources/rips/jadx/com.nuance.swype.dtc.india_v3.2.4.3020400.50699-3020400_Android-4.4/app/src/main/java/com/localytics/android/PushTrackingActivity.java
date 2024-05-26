package com.localytics.android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import java.util.List;

/* loaded from: classes.dex */
public class PushTrackingActivity extends Activity {
    public static final String LAUNCH_INTENT = "ll_launch_intent";

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        Intent deepLinkIntent;
        List<ResolveInfo> resolveInfos;
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String appKey = DatapointHelper.getLocalyticsAppKeyOrNull(this);
        if (!TextUtils.isEmpty(appKey)) {
            Localytics.integrate(getApplicationContext(), appKey);
        }
        Localytics.openSession();
        Localytics.handlePushNotificationOpened(intent);
        finish();
        Intent launchIntent = null;
        String deepLinkUrlString = intent.getStringExtra("ll_deep_link_url");
        if (!TextUtils.isEmpty(deepLinkUrlString) && (resolveInfos = getPackageManager().queryIntentActivities((deepLinkIntent = new Intent("android.intent.action.VIEW", Uri.parse(deepLinkUrlString))), 0)) != null && resolveInfos.size() > 0) {
            launchIntent = deepLinkIntent;
        }
        if (launchIntent == null) {
            Intent customIntent = (Intent) intent.getParcelableExtra(LAUNCH_INTENT);
            if (customIntent != null) {
                launchIntent = customIntent;
                intent.removeExtra(LAUNCH_INTENT);
            } else {
                launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                launchIntent.addFlags(603979776);
            }
        }
        launchIntent.putExtras(intent);
        startActivity(launchIntent);
    }
}
