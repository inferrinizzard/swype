package io.fabric.sdk.android.services.settings;

import com.facebook.share.internal.ShareConstants;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.network.HttpMethod;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class DefaultSettingsSpiCall extends AbstractSpiCall implements SettingsSpiCall {
    public DefaultSettingsSpiCall(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory) {
        this(kit, protocolAndHostOverride, url, requestFactory, HttpMethod.GET);
    }

    private DefaultSettingsSpiCall(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory, HttpMethod method) {
        super(kit, protocolAndHostOverride, url, requestFactory, method);
    }

    @Override // io.fabric.sdk.android.services.settings.SettingsSpiCall
    public final JSONObject invoke(SettingsRequest requestData) {
        HttpRequest httpRequest = null;
        try {
            try {
                Map<String, String> queryParams = new HashMap<>();
                queryParams.put("build_version", requestData.buildVersion);
                queryParams.put("display_version", requestData.displayVersion);
                queryParams.put(ShareConstants.FEED_SOURCE_PARAM, Integer.toString(requestData.source));
                if (requestData.iconHash != null) {
                    queryParams.put("icon_hash", requestData.iconHash);
                }
                String str = requestData.instanceId;
                if (!CommonUtils.isNullOrEmpty(str)) {
                    queryParams.put("instance", str);
                }
                httpRequest = getHttpRequest(queryParams).header("X-CRASHLYTICS-API-KEY", requestData.apiKey).header("X-CRASHLYTICS-API-CLIENT-TYPE", "android").header("X-CRASHLYTICS-D", requestData.deviceId).header("X-CRASHLYTICS-API-CLIENT-VERSION", this.kit.getVersion()).header("Accept", "application/json");
                Fabric.getLogger();
                new StringBuilder("Requesting settings from ").append(this.url);
                Fabric.getLogger();
                new StringBuilder("Settings query params were: ").append(queryParams);
                httpRequest.code();
                Fabric.getLogger();
                String httpRequestBody = httpRequest.body();
                JSONObject toReturn = new JSONObject(httpRequestBody);
                if (httpRequest == null) {
                    return toReturn;
                }
                Fabric.getLogger();
                new StringBuilder("Settings request ID: ").append(httpRequest.header("X-REQUEST-ID"));
                return toReturn;
            } catch (Exception e) {
                Fabric.getLogger().e("Fabric", "Failed to retrieve settings from " + this.url, e);
                Fabric.getLogger();
                if (httpRequest == null) {
                    return null;
                }
                Fabric.getLogger();
                new StringBuilder("Settings request ID: ").append(httpRequest.header("X-REQUEST-ID"));
                return null;
            }
        } catch (Throwable th) {
            if (httpRequest != null) {
                Fabric.getLogger();
                new StringBuilder("Settings request ID: ").append(httpRequest.header("X-REQUEST-ID"));
            }
            throw th;
        }
    }
}
