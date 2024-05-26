package com.crashlytics.android;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.common.ResponseParser;
import io.fabric.sdk.android.services.network.HttpMethod;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class DefaultCreateReportSpiCall extends AbstractSpiCall implements CreateReportSpiCall {
    public DefaultCreateReportSpiCall(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory) {
        super(kit, protocolAndHostOverride, url, requestFactory, HttpMethod.POST);
    }

    @Override // com.crashlytics.android.CreateReportSpiCall
    public final boolean invoke(CreateReportRequest requestData) {
        HttpRequest httpRequest;
        HttpRequest header = getHttpRequest(Collections.emptyMap()).header("X-CRASHLYTICS-API-KEY", requestData.apiKey).header("X-CRASHLYTICS-API-CLIENT-TYPE", "android").header("X-CRASHLYTICS-API-CLIENT-VERSION", Crashlytics.getInstance().getVersion());
        Iterator<Map.Entry<String, String>> it = requestData.report.getCustomHeaders().entrySet().iterator();
        while (true) {
            httpRequest = header;
            if (!it.hasNext()) {
                break;
            }
            Map.Entry<String, String> next = it.next();
            header = httpRequest.header(next.getKey(), next.getValue());
        }
        Report report = requestData.report;
        HttpRequest httpRequest2 = httpRequest.part("report[file]", report.getFileName(), "application/octet-stream", report.getFile()).part$d4ee95d("report[identifier]", null, report.getIdentifier());
        Fabric.getLogger();
        new StringBuilder("Sending report to: ").append(this.url);
        int statusCode = httpRequest2.code();
        Fabric.getLogger();
        new StringBuilder("Create report request ID: ").append(httpRequest2.header("X-REQUEST-ID"));
        Fabric.getLogger();
        return ResponseParser.parse(statusCode) == 0;
    }
}
