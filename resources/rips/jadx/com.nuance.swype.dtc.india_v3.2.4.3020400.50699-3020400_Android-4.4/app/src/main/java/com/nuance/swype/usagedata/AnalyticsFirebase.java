package com.nuance.swype.usagedata;

import android.content.Context;
import android.os.Bundle;
import com.nuance.connect.internal.common.Document;
import com.nuance.swype.usagedata.CustomDimension;
import com.nuance.swype.util.LogManager;
import java.util.Map;

/* loaded from: classes.dex */
public final class AnalyticsFirebase implements Analytics {
    private static final LogManager.Log log = LogManager.getLog("AnalyticsFirebase");

    @Override // com.nuance.swype.usagedata.Analytics
    public final void startSession(Context context) {
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void endSession(Context context) {
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void tagEvent(String event, Map<String, String> attributes) {
        LogManager.Log log2 = log;
        Object[] objArr = new Object[4];
        objArr[0] = "tagEvent... eventId:";
        objArr[1] = event;
        objArr[2] = "; attributes:";
        objArr[3] = attributes != null ? attributes.toString() : "";
        log2.d(objArr);
        logEvent(event, attributes);
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void tagScreen(String screen) {
        log.d("Screen:", screen);
        logEvent("Screen_" + screen, null);
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void setCustomDimension(CustomDimension.Dimension dim, String value) {
    }

    private static void logEvent(String eventId, Map<String, String> attributes) {
        LogManager.Log log2 = log;
        Object[] objArr = new Object[4];
        objArr[0] = "logEvent... eventId:";
        objArr[1] = eventId;
        objArr[2] = "; attributes:";
        objArr[3] = attributes != null ? attributes.toString() : "";
        log2.d(objArr);
        if (attributes != null && attributes.size() > 0) {
            Bundle data = new Bundle();
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                String key = entry.getKey().replaceAll("[^A-Za-z]", Document.ID_SEPARATOR);
                String value = entry.getValue();
                data.putString(key.substring(0, Math.min(key.length(), 24)), value.substring(0, Math.min(value.length(), 36)));
            }
        }
    }
}
