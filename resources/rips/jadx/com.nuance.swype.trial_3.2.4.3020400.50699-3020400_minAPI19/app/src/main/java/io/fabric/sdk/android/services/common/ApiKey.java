package io.fabric.sdk.android.services.common;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import io.fabric.sdk.android.Fabric;

/* loaded from: classes.dex */
public final class ApiKey {
    public static String getValue(Context context) {
        String apiKey = getApiKeyFromManifest(context);
        if (TextUtils.isEmpty(apiKey)) {
            apiKey = null;
            int resourcesIdentifier = CommonUtils.getResourcesIdentifier(context, "io.fabric.ApiKey", "string");
            if (resourcesIdentifier == 0) {
                Fabric.getLogger();
                resourcesIdentifier = CommonUtils.getResourcesIdentifier(context, "com.crashlytics.ApiKey", "string");
            }
            if (resourcesIdentifier != 0) {
                apiKey = context.getResources().getString(resourcesIdentifier);
            }
        }
        if (TextUtils.isEmpty(apiKey)) {
            if (Fabric.isDebuggable() || CommonUtils.isAppDebuggable(context)) {
                throw new IllegalArgumentException("Fabric could not be initialized, API key missing from AndroidManifest.xml. Add the following tag to your Application element \n\t<meta-data android:name=\"io.fabric.ApiKey\" android:value=\"YOUR_API_KEY\"/>");
            }
            Fabric.getLogger().e("Fabric", "Fabric could not be initialized, API key missing from AndroidManifest.xml. Add the following tag to your Application element \n\t<meta-data android:name=\"io.fabric.ApiKey\" android:value=\"YOUR_API_KEY\"/>");
        }
        return apiKey;
    }

    private static String getApiKeyFromManifest(Context context) {
        try {
            String packageName = context.getPackageName();
            Bundle bundle = context.getPackageManager().getApplicationInfo(packageName, 128).metaData;
            if (bundle == null) {
                return null;
            }
            String apiKey = bundle.getString("io.fabric.ApiKey");
            if (apiKey == null) {
                Fabric.getLogger();
                return bundle.getString("com.crashlytics.ApiKey");
            }
            return apiKey;
        } catch (Exception e) {
            Fabric.getLogger();
            new StringBuilder("Caught non-fatal exception while retrieving apiKey: ").append(e);
            return null;
        }
    }
}
