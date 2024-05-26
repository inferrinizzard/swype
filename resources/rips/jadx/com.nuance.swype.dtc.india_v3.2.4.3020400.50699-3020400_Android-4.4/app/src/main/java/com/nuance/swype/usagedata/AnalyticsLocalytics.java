package com.nuance.swype.usagedata;

import android.content.Context;
import android.provider.Settings;
import com.localytics.android.AnalyticsListenerAdapter;
import com.localytics.android.Localytics;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.License;
import com.nuance.swype.usagedata.CustomDimension;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import java.util.Map;

/* loaded from: classes.dex */
public final class AnalyticsLocalytics implements Analytics {
    private static final LogManager.Log log = LogManager.getLog("AnalyticsLocalytics");
    private final UsageData.AnalyticsSessionManagementStrategy mSessionManager;

    public AnalyticsLocalytics(Context context, UsageData.AnalyticsSessionManagementStrategy sessionManager) {
        this.mSessionManager = sessionManager;
        Localytics.setLoggingEnabled(true);
        Localytics.integrate(context);
        Localytics.setAnalyticsListener(new AnalyticsListener(context));
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void startSession(Context context) {
        if (this.mSessionManager.canOpen()) {
            if (this.mSessionManager.isOpen()) {
                closeLocalyticsSession();
            }
            log.d("Opening localytics session.");
            Localytics.openSession();
            Localytics.upload();
            this.mSessionManager.markSessionOpened();
            return;
        }
        log.d("Skipping starting a new localytics session.");
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void endSession(Context context) {
        if (this.mSessionManager.canClose()) {
            closeLocalyticsSession();
        } else {
            log.d("Skipping closing localytics session.");
        }
    }

    private void closeLocalyticsSession() {
        log.d("Closing localytics session.");
        Localytics.closeSession();
        Localytics.upload();
        this.mSessionManager.markSessionClosed();
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void tagEvent(String event, Map<String, String> attributes) {
        LogManager.Log log2 = log;
        Object[] objArr = new Object[4];
        objArr[0] = "tagEvent... event:";
        objArr[1] = event;
        objArr[2] = "; attributes:";
        objArr[3] = attributes != null ? attributes.toString() : "";
        log2.d(objArr);
        Localytics.tagEvent(event, attributes);
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void tagScreen(String screen) {
        log.d("tagScreen... screen:", screen);
        Localytics.tagScreen(screen);
    }

    @Override // com.nuance.swype.usagedata.Analytics
    public final void setCustomDimension(CustomDimension.Dimension dim, String value) {
        log.d("setCustomDimension... dim:", dim.toString(), "; value:", value);
        Localytics.setCustomDimension(dim.index, value);
    }

    public static boolean isLocalyticsEnabledForThisDevice(Context context) {
        Boolean isEnabledForDevice;
        String androidId = Settings.Secure.getString(context.getContentResolver(), "android_id");
        if (androidId != null) {
            isEnabledForDevice = Boolean.valueOf(androidId.hashCode() % 4 == 0);
            LogManager.Log log2 = log;
            Object[] objArr = new Object[1];
            objArr[0] = "isLocalyticsEnabledForThisDevice: " + (isEnabledForDevice.booleanValue() ? License.DEVICE_STATUS_ENABLED : License.DEVICE_STATUS_DISABLED) + " for Android ID: " + androidId;
            log2.d(objArr);
        } else {
            log.d("isLocalyticsEnabledForThisDevice: localytics DISABLED for null Android ID");
            isEnabledForDevice = false;
        }
        return isEnabledForDevice.booleanValue();
    }

    /* loaded from: classes.dex */
    static class AnalyticsListener extends AnalyticsListenerAdapter {
        private final Context mContext;

        AnalyticsListener(Context context) {
            this.mContext = context;
        }

        @Override // com.localytics.android.AnalyticsListenerAdapter, com.localytics.android.AnalyticsListener
        public final void localyticsSessionWillOpen(boolean isFirst, boolean isUpgrade, boolean isResume) {
            AnalyticsLocalytics.log.d("localyticsSession will be opened... isFirst:", Boolean.valueOf(isFirst), "  isUpgrade:", Boolean.valueOf(isUpgrade), "  isResume:", Boolean.valueOf(isResume));
            AnalyticsLocalytics.log.d("Setting custom dimensions");
            IMEApplication.from(this.mContext).updateCustomDimensions();
        }

        @Override // com.localytics.android.AnalyticsListenerAdapter, com.localytics.android.AnalyticsListener
        public final void localyticsSessionWillClose() {
            super.localyticsSessionWillClose();
            AnalyticsLocalytics.log.d("localyticsSession will be closed...");
        }

        @Override // com.localytics.android.AnalyticsListenerAdapter, com.localytics.android.AnalyticsListener
        public final void localyticsDidTagEvent(String eventName, Map<String, String> attributes, long customerValueIncrease) {
            super.localyticsDidTagEvent(eventName, attributes, customerValueIncrease);
            LogManager.Log log = AnalyticsLocalytics.log;
            Object[] objArr = new Object[6];
            objArr[0] = "localytics did tagEvent... eventName:";
            objArr[1] = eventName;
            objArr[2] = " attributes:";
            objArr[3] = attributes != null ? attributes.toString() : "";
            objArr[4] = " customeValueIncrease:";
            objArr[5] = Long.valueOf(customerValueIncrease);
            log.d(objArr);
        }
    }
}
