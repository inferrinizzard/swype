package com.localytics.android;

import com.localytics.android.BaseUploadThread;
import com.localytics.android.Localytics;

/* loaded from: classes.dex */
final class ReferralUploader extends BaseUploadThread {
    private String mFirstSessionEvent;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReferralUploader(AnalyticsHandler sessionHandler, String customerId, String firstSessionEvent, LocalyticsDao localyticsDao) {
        super(sessionHandler, null, customerId, localyticsDao);
        this.mFirstSessionEvent = firstSessionEvent;
    }

    @Override // com.localytics.android.BaseUploadThread
    final int uploadData() {
        if (!this.mFirstSessionEvent.isEmpty()) {
            Localytics.Log.i("[REFERRAL] reupload first session: " + this.mFirstSessionEvent);
            String apiKey = getApiKey();
            String url = Constants.getHTTPPreface() + String.format("%s/api/v2/applications/%s/uploads", Constants.ANALYTICS_HOST, apiKey);
            upload(BaseUploadThread.UploadType.ANALYTICS, url, this.mFirstSessionEvent, 0, true);
        }
        return 0;
    }

    @Override // com.localytics.android.BaseUploadThread, java.lang.Thread, java.lang.Runnable
    public final void run() {
        uploadData();
    }
}
