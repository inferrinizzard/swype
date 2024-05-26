package com.localytics.android;

import java.util.Map;

/* loaded from: classes.dex */
public class AnalyticsListenerAdapter implements AnalyticsListener {
    @Override // com.localytics.android.AnalyticsListener
    public void localyticsSessionWillOpen(boolean isFirst, boolean isUpgrade, boolean isResume) {
    }

    @Override // com.localytics.android.AnalyticsListener
    public void localyticsSessionDidOpen(boolean isFirst, boolean isUpgrade, boolean isResume) {
    }

    @Override // com.localytics.android.AnalyticsListener
    public void localyticsSessionWillClose() {
    }

    @Override // com.localytics.android.AnalyticsListener
    public void localyticsDidTagEvent(String eventName, Map<String, String> attributes, long customerValueIncrease) {
    }
}
