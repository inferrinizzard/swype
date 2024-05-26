package com.localytics.android;

import com.localytics.android.BaseUploadThread;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
final class AnalyticsUploadHandler extends BaseUploadThread {
    static final String ANALYTICS_URL_PATH = "%s/api/v2/applications/%s/uploads";

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnalyticsUploadHandler(BaseHandler sessionHandler, TreeMap<Integer, Object> data, String customerId, LocalyticsDao localyticsDao) {
        super(sessionHandler, data, customerId, localyticsDao);
    }

    @Override // com.localytics.android.BaseUploadThread
    final int uploadData() {
        int rowsToDelete = 0;
        try {
            if (!this.mData.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                String apiKey = getApiKey();
                for (Map.Entry<Integer, Object> eventBlob : this.mData.entrySet()) {
                    builder.append(eventBlob.getValue());
                    builder.append('\n');
                }
                String url = Constants.getHTTPPreface() + String.format(ANALYTICS_URL_PATH, Constants.ANALYTICS_HOST, apiKey);
                if (upload(BaseUploadThread.UploadType.ANALYTICS, url, builder.toString(), 0)) {
                    rowsToDelete = this.mData.lastKey().intValue();
                }
            }
            return rowsToDelete;
        } catch (Throwable th) {
            return 0;
        }
    }
}
