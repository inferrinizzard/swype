package io.fabric.sdk.android.services.common;

import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.network.HttpMethod;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.util.Map;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public abstract class AbstractSpiCall {
    private static final Pattern PROTOCOL_AND_HOST_PATTERN = Pattern.compile("http(s?)://[^\\/]+", 2);
    public final Kit kit;
    private final HttpMethod method;
    private final String protocolAndHostOverride;
    private final HttpRequestFactory requestFactory;
    public final String url;

    public AbstractSpiCall(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory, HttpMethod method) {
        if (url == null) {
            throw new IllegalArgumentException("url must not be null.");
        }
        if (requestFactory == null) {
            throw new IllegalArgumentException("requestFactory must not be null.");
        }
        this.kit = kit;
        this.protocolAndHostOverride = protocolAndHostOverride;
        this.url = CommonUtils.isNullOrEmpty(this.protocolAndHostOverride) ? url : PROTOCOL_AND_HOST_PATTERN.matcher(url).replaceFirst(this.protocolAndHostOverride);
        this.requestFactory = requestFactory;
        this.method = method;
    }

    public final HttpRequest getHttpRequest(Map<String, String> queryParams) {
        HttpRequest buildHttpRequest = this.requestFactory.buildHttpRequest(this.method, this.url, queryParams);
        buildHttpRequest.getConnection().setUseCaches(false);
        buildHttpRequest.getConnection().setConnectTimeout(10000);
        return buildHttpRequest.header("User-Agent", "Crashlytics Android SDK/" + this.kit.getVersion()).header("X-CRASHLYTICS-DEVELOPER-TOKEN", "bca6990fc3c15a8105800c0673517a4b579634a1");
    }
}
