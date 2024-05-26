package com.nuance.swype.usagedata;

import android.content.Context;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.nuance.swype.usagedata.CustomDimension;
import com.nuance.swype.util.LogManager;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class AnalyticsFlurry implements Analytics {
    private static final LogManager.Log log = LogManager.getLog("AnalyticsFlurry");
    private static final FlurryAgentListener mListener = new FlurryAgentListener() { // from class: com.nuance.swype.usagedata.AnalyticsFlurry.1
        @Override // com.flurry.android.FlurryAgentListener
        public final void onSessionStarted() {
            AnalyticsFlurry.log.d("onSessionStarted");
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnalyticsFlurry(Context context, String appKey) {
        log.d("constructor");
        FlurryAgent.Builder builder = new FlurryAgent.Builder();
        builder.e = false;
        builder.d = 300000L;
        FlurryAgent.Builder.a = mListener;
        builder.b = false;
        builder.c = 7;
        builder.f = false;
        FlurryAgent.a(FlurryAgent.Builder.a, builder.b, builder.c, builder.d, builder.e, builder.f, context, appKey);
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void startSession(Context context) {
        log.d("startSession: context: " + context);
        FlurryAgent.onStartSession(context);
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void endSession(Context context) {
        log.d("endSession: context: " + context);
        FlurryAgent.onEndSession(context);
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void tagEvent(String event, Map<String, String> attributes) {
        LogManager.Log log2 = log;
        Object[] objArr = new Object[1];
        objArr[0] = "tagEvent: event: " + event + ", attributes: " + (attributes == null ? "null" : attributes.toString());
        log2.d(objArr);
        logEvent(event, attributes);
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void tagScreen(String screen) {
        log.d("tagScreen: screen: " + screen);
        logEvent("Screen: " + screen, null);
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void setCustomDimension(CustomDimension.Dimension dim, String value) {
        log.d("setCustomDimension: dim: " + dim + ", value: " + value);
        FlurryAgent.addSessionProperty(dim.toString(), value);
    }

    private static void logEvent(String eventId, Map<String, String> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            FlurryAgent.logEvent(eventId);
        } else {
            FlurryAgent.logEvent(eventId, attributes);
        }
    }
}
