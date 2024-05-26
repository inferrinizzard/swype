package com.crashlytics.android.beta;

import com.facebook.share.internal.ShareConstants;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.common.Strings;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.network.HttpMethod;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class CheckForUpdatesRequest extends AbstractSpiCall {
    private final CheckForUpdatesResponseTransform responseTransform;

    public CheckForUpdatesRequest(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory, CheckForUpdatesResponseTransform responseTransform) {
        super(kit, protocolAndHostOverride, url, requestFactory, HttpMethod.GET);
        this.responseTransform = responseTransform;
    }

    public final CheckForUpdatesResponse invoke(String apiKey, String idHeaderValue, BuildProperties buildProps) {
        HttpRequest httpRequest = null;
        try {
            try {
                Map<String, String> queryParams = new HashMap<>();
                queryParams.put("build_version", buildProps.versionCode);
                queryParams.put("display_version", buildProps.versionName);
                queryParams.put("instance", buildProps.buildId);
                queryParams.put(ShareConstants.FEED_SOURCE_PARAM, MessageAPI.SESSION_ID);
                httpRequest = getHttpRequest(queryParams).header("Accept", "application/json").header("User-Agent", "Crashlytics Android SDK/" + this.kit.getVersion()).header("X-CRASHLYTICS-DEVELOPER-TOKEN", "bca6990fc3c15a8105800c0673517a4b579634a1").header("X-CRASHLYTICS-API-CLIENT-TYPE", "android").header("X-CRASHLYTICS-API-CLIENT-VERSION", this.kit.getVersion()).header("X-CRASHLYTICS-API-KEY", apiKey).header("X-CRASHLYTICS-D", idHeaderValue);
                Fabric.getLogger();
                new StringBuilder("Checking for updates from ").append(this.url);
                Fabric.getLogger();
                new StringBuilder("Checking for updates query params are: ").append(queryParams);
            } catch (Exception e) {
                Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Error while checking for updates from " + this.url, e);
                if (httpRequest != null) {
                    httpRequest.header("X-REQUEST-ID");
                    Fabric.getLogger();
                }
            }
            if (200 == httpRequest.code()) {
                Fabric.getLogger();
                JSONObject jSONObject = new JSONObject(httpRequest.body());
                CheckForUpdatesResponse checkForUpdatesResponse = new CheckForUpdatesResponse(jSONObject.optString("url", null), jSONObject.optString("version_string", null), jSONObject.optString("display_version", null), jSONObject.optString("build_version", null), jSONObject.optString("identifier", null), jSONObject.optString("instance_identifier", null));
            }
            Fabric.getLogger().e(Strings.BUILD_TYPE_BETA, "Checking for updates failed. Response code: " + httpRequest.code());
            if (httpRequest != null) {
                httpRequest.header("X-REQUEST-ID");
                Fabric.getLogger();
            }
            return null;
        } finally {
            if (httpRequest != null) {
                httpRequest.header("X-REQUEST-ID");
                Fabric.getLogger();
            }
        }
    }
}
