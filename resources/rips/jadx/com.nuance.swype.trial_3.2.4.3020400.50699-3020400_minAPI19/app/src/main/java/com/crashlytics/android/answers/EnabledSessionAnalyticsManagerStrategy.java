package com.crashlytics.android.answers;

import android.content.Context;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.events.EnabledEventsStrategy;
import io.fabric.sdk.android.services.events.FilesSender;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;
import java.util.concurrent.ScheduledExecutorService;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class EnabledSessionAnalyticsManagerStrategy extends EnabledEventsStrategy<SessionEvent> implements SessionAnalyticsManagerStrategy<SessionEvent> {
    FilesSender filesSender;
    private final HttpRequestFactory httpRequestFactory;

    public EnabledSessionAnalyticsManagerStrategy(Context context, ScheduledExecutorService executorService, SessionAnalyticsFilesManager filesManager, HttpRequestFactory httpRequestFactory) {
        super(context, executorService, filesManager);
        this.httpRequestFactory = httpRequestFactory;
    }

    @Override // io.fabric.sdk.android.services.events.EventsStrategy
    public final FilesSender getFilesSender() {
        return this.filesSender;
    }

    @Override // com.crashlytics.android.answers.SessionAnalyticsManagerStrategy
    public final void setAnalyticsSettingsData(AnalyticsSettingsData analyticsSettingsData, String protocolAndHostOverride) {
        Answers answers = Answers.getInstance();
        String str = analyticsSettingsData.analyticsURL;
        HttpRequestFactory httpRequestFactory = this.httpRequestFactory;
        new ApiKey();
        this.filesSender = new DefaultSessionAnalyticsFilesSender(answers, protocolAndHostOverride, str, httpRequestFactory, ApiKey.getValue(this.context));
        ((SessionAnalyticsFilesManager) this.filesManager).analyticsSettingsData = analyticsSettingsData;
        configureRollover(analyticsSettingsData.flushIntervalSeconds);
    }
}
