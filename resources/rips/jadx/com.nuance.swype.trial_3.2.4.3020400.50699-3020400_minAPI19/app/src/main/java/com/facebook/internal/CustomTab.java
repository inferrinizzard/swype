package com.facebook.internal;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import com.facebook.FacebookSdk;

/* loaded from: classes.dex */
public class CustomTab {
    private Uri uri;

    public CustomTab(String action, Bundle parameters) {
        this.uri = Utility.buildUri(ServerProtocol.getDialogAuthority(), FacebookSdk.getGraphApiVersion() + "/dialog/" + action, parameters == null ? new Bundle() : parameters);
    }

    public void openCustomTab(Activity activity, String packageName) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        customTabsIntent.intent.setPackage(packageName);
        customTabsIntent.intent.addFlags(1073741824);
        customTabsIntent.intent.setData(this.uri);
        ActivityCompat.startActivity(activity, customTabsIntent.intent, customTabsIntent.startAnimationBundle);
    }
}
