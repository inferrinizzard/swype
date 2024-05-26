package com.localytics.android;

import java.util.Map;

/* loaded from: classes.dex */
public interface AnalyticsListener {
    void localyticsDidTagEvent(String str, Map<String, String> map, long j);

    void localyticsSessionDidOpen(boolean z, boolean z2, boolean z3);

    void localyticsSessionWillClose();

    void localyticsSessionWillOpen(boolean z, boolean z2, boolean z3);
}
