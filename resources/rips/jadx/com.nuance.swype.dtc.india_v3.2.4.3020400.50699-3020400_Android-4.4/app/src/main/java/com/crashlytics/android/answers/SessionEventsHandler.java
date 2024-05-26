package com.crashlytics.android.answers;

import android.content.Context;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.events.EventsHandler;
import io.fabric.sdk.android.services.events.EventsStrategy;
import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;
import java.util.concurrent.ScheduledExecutorService;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class SessionEventsHandler extends EventsHandler<SessionEvent> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SessionEventsHandler(Context context, EventsStrategy<SessionEvent> strategy, SessionAnalyticsFilesManager filesManager, ScheduledExecutorService executor) {
        super(context, strategy, filesManager, executor);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.fabric.sdk.android.services.events.EventsHandler
    public final EventsStrategy<SessionEvent> getDisabledEventsStrategy() {
        return new DisabledSessionAnalyticsManagerStrategy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setAnalyticsSettingsData(final AnalyticsSettingsData analyticsSettingsData, final String protocolAndHostOverride) {
        super.executeAsync(new Runnable() { // from class: com.crashlytics.android.answers.SessionEventsHandler.1
            @Override // java.lang.Runnable
            public final void run() {
                try {
                    ((SessionAnalyticsManagerStrategy) SessionEventsHandler.this.strategy).setAnalyticsSettingsData(analyticsSettingsData, protocolAndHostOverride);
                } catch (Exception e) {
                    CommonUtils.logControlledError$43da9ce8(Answers.getInstance().context, "Crashlytics failed to set analytics settings data.");
                }
            }
        });
    }
}
