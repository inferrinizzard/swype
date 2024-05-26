package com.localytics.android;

import android.net.Uri;
import com.localytics.android.BaseUploadThread;
import java.util.Iterator;
import java.util.TreeMap;

/* loaded from: classes.dex */
final class ProfileUploadHandler extends BaseUploadThread {
    private static final String PROFILE_URL = "v1/apps/%s/profiles";

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProfileUploadHandler(BaseHandler sessionHandler, TreeMap<Integer, Object> data, String customerId, LocalyticsDao localyticsDao) {
        super(sessionHandler, data, customerId, localyticsDao);
    }

    @Override // com.localytics.android.BaseUploadThread
    final int uploadData() {
        int rowsToDelete = 0;
        try {
            if (!this.mData.isEmpty()) {
                Iterator it = this.mData.entrySet().iterator();
                StringBuilder builder = new StringBuilder();
                String apiKey = getApiKey();
                String customerID = null;
                while (it.hasNext()) {
                    Object[] params = (Object[]) it.next().getValue();
                    if (builder.length() == 0) {
                        customerID = (String) params[0];
                        builder.append('{').append('\"').append("customer_id\":\"").append(customerID).append('\"').append(',').append('\"').append("database\":\"").append((String) params[1]).append('\"').append(',').append('\"').append("changes\":[");
                    }
                    builder.append((String) params[2]);
                    if (it.hasNext()) {
                        builder.append(',');
                    }
                }
                builder.append("]}");
                String url = new Uri.Builder().scheme(Constants.USE_HTTPS ? "https" : "http").encodedAuthority(Constants.PROFILES_HOST).encodedPath(String.format(PROFILE_URL, apiKey)).appendPath(customerID).build().toString();
                if (upload(BaseUploadThread.UploadType.PROFILES, url, builder.toString(), 0)) {
                    rowsToDelete = this.mData.lastKey().intValue();
                }
            }
            return rowsToDelete;
        } catch (Throwable th) {
            return 0;
        }
    }
}
