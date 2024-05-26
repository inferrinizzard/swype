package com.crashlytics.android.answers;

import io.fabric.sdk.android.services.events.DisabledEventsStrategy;
import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;

/* loaded from: classes.dex */
final class DisabledSessionAnalyticsManagerStrategy extends DisabledEventsStrategy<SessionEvent> implements SessionAnalyticsManagerStrategy<SessionEvent> {
    @Override // com.crashlytics.android.answers.SessionAnalyticsManagerStrategy
    public final void setAnalyticsSettingsData(AnalyticsSettingsData analyticsSettingsData, String protocolAndHostOverride) {
    }
}
