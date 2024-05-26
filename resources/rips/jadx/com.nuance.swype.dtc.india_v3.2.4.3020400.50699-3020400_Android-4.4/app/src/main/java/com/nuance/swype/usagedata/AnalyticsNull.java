package com.nuance.swype.usagedata;

import android.content.Context;
import com.nuance.swype.usagedata.CustomDimension;
import com.nuance.swype.util.LogManager;
import java.util.Map;

/* loaded from: classes.dex */
final class AnalyticsNull implements Analytics {
    private static final LogManager.Log log = LogManager.getLog("AnalyticsNull");

    @Override // com.nuance.swype.usagedata.Analytics
    public final void startSession(Context context) {
        log.d("startSession");
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void endSession(Context context) {
        log.d("endSession");
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void tagEvent(String event, Map<String, String> attributes) {
        log.d("tagEvent");
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void tagScreen(String screen) {
        log.d("tagScreen");
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void setCustomDimension(CustomDimension.Dimension dim, String value) {
        log.d("setCustomDimension");
    }
}
