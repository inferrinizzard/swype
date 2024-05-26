package com.crashlytics.android.answers;

import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SessionAnalyticsManager {
    final SessionEventsHandler eventsHandler;
    final SessionEventMetadata metadata;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SessionAnalyticsManager(SessionEventMetadata metadata, SessionEventsHandler eventsHandler) {
        this.metadata = metadata;
        this.eventsHandler = eventsHandler;
    }

    public final void setAnalyticsSettingsData(AnalyticsSettingsData analyticsSettingsData, String protocolAndHostOverride) {
        this.eventsHandler.setAnalyticsSettingsData(analyticsSettingsData, protocolAndHostOverride);
    }

    public void disable() {
        SessionEventsHandler sessionEventsHandler = this.eventsHandler;
        sessionEventsHandler.executeAsync(new Runnable() { // from class: io.fabric.sdk.android.services.events.EventsHandler.4
            public AnonymousClass4() {
            }

            @Override // java.lang.Runnable
            public final void run() {
                try {
                    EventsStrategy<T> prevStrategy = EventsHandler.this.strategy;
                    EventsHandler.this.strategy = EventsHandler.this.getDisabledEventsStrategy();
                    prevStrategy.deleteAllEvents();
                } catch (Exception e) {
                    CommonUtils.logControlledError$43da9ce8(EventsHandler.this.context, "Failed to disable events.");
                }
            }
        });
    }
}
