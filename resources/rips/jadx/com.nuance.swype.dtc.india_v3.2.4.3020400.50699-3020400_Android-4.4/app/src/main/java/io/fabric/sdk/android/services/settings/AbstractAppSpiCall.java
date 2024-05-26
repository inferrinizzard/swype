package io.fabric.sdk.android.services.settings;

import android.content.res.Resources;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.ResponseParser;
import io.fabric.sdk.android.services.network.HttpMethod;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.io.InputStream;
import java.util.Collections;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class AbstractAppSpiCall extends AbstractSpiCall {
    public AbstractAppSpiCall(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory, HttpMethod method) {
        super(kit, protocolAndHostOverride, url, requestFactory, method);
    }

    private HttpRequest applyMultipartDataTo(HttpRequest request, AppRequestData requestData) {
        HttpRequest request2 = request.part$d4ee95d("app[identifier]", null, requestData.appId).part$d4ee95d("app[name]", null, requestData.name).part$d4ee95d("app[display_version]", null, requestData.displayVersion).part$d4ee95d("app[build_version]", null, requestData.buildVersion).part("app[source]", Integer.valueOf(requestData.source)).part$d4ee95d("app[minimum_sdk_version]", null, requestData.minSdkVersion).part$d4ee95d("app[built_sdk_version]", null, requestData.builtSdkVersion);
        if (!CommonUtils.isNullOrEmpty(requestData.instanceIdentifier)) {
            request2.part$d4ee95d("app[instance_identifier]", null, requestData.instanceIdentifier);
        }
        if (requestData.icon != null) {
            InputStream is = null;
            try {
                is = this.kit.context.getResources().openRawResource(requestData.icon.iconResourceId);
                request2.part$d4ee95d("app[icon][hash]", null, requestData.icon.hash).part("app[icon][data]", "icon.png", "application/octet-stream", is).part("app[icon][width]", Integer.valueOf(requestData.icon.width)).part("app[icon][height]", Integer.valueOf(requestData.icon.height));
            } catch (Resources.NotFoundException e) {
                Fabric.getLogger().e("Fabric", "Failed to find app icon with resource ID: " + requestData.icon.iconResourceId, e);
            } finally {
                CommonUtils.closeOrLog(is, "Failed to close app icon InputStream.");
            }
        }
        if (requestData.sdkKits != null) {
            for (Kit kit : requestData.sdkKits) {
                String version = kit.getVersion() == null ? "" : kit.getVersion();
                request2.part$d4ee95d("app[build][libraries][" + (kit.getIdentifier() == null ? "" : kit.getIdentifier()) + "]", null, version);
            }
        }
        return request2;
    }

    public boolean invoke(AppRequestData requestData) {
        HttpRequest httpRequest = applyMultipartDataTo(getHttpRequest(Collections.emptyMap()).header("X-CRASHLYTICS-API-KEY", requestData.apiKey).header("X-CRASHLYTICS-API-CLIENT-TYPE", "android").header("X-CRASHLYTICS-API-CLIENT-VERSION", this.kit.getVersion()), requestData);
        Fabric.getLogger();
        new StringBuilder("Sending app info to ").append(this.url);
        if (requestData.icon != null) {
            Fabric.getLogger();
            new StringBuilder("App icon hash is ").append(requestData.icon.hash);
            Fabric.getLogger();
            new StringBuilder("App icon size is ").append(requestData.icon.width).append("x").append(requestData.icon.height);
        }
        int statusCode = httpRequest.code();
        String kind = "POST".equals(httpRequest.getConnection().getRequestMethod()) ? "Create" : "Update";
        Fabric.getLogger();
        new StringBuilder().append(kind).append(" app request ID: ").append(httpRequest.header("X-REQUEST-ID"));
        Fabric.getLogger();
        return ResponseParser.parse(statusCode) == 0;
    }
}
