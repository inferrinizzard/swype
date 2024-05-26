package com.crashlytics.android.answers;

import android.content.Context;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.ResponseParser;
import io.fabric.sdk.android.services.events.FilesSender;
import io.fabric.sdk.android.services.network.HttpMethod;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.io.File;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
final class DefaultSessionAnalyticsFilesSender extends AbstractSpiCall implements FilesSender {
    private final String apiKey;

    public DefaultSessionAnalyticsFilesSender(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory, String apiKey) {
        this(kit, protocolAndHostOverride, url, requestFactory, apiKey, HttpMethod.POST);
    }

    private DefaultSessionAnalyticsFilesSender(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory, String apiKey, HttpMethod method) {
        super(kit, protocolAndHostOverride, url, requestFactory, method);
        this.apiKey = apiKey;
    }

    @Override // io.fabric.sdk.android.services.events.FilesSender
    public final boolean send(List<File> files) {
        HttpRequest httpRequest = getHttpRequest(Collections.emptyMap()).header("X-CRASHLYTICS-API-CLIENT-TYPE", "android").header("X-CRASHLYTICS-API-CLIENT-VERSION", Answers.getInstance().getVersion()).header("X-CRASHLYTICS-API-KEY", this.apiKey);
        int i = 0;
        for (File file : files) {
            Context context = Answers.getInstance().context;
            new StringBuilder("Adding analytics session file ").append(file.getName()).append(" to multipart POST");
            CommonUtils.logControlled$5ffc00fd(context);
            httpRequest.part("session_analytics_file_" + i, file.getName(), "application/vnd.crashlytics.android.events", file);
            i++;
        }
        Context context2 = Answers.getInstance().context;
        new StringBuilder("Sending ").append(files.size()).append(" analytics files to ").append(this.url);
        CommonUtils.logControlled$5ffc00fd(context2);
        int statusCode = httpRequest.code();
        CommonUtils.logControlled$5ffc00fd(Answers.getInstance().context);
        return ResponseParser.parse(statusCode) == 0;
    }
}
