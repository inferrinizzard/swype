package com.localytics.android;

import com.localytics.android.BaseUploadThread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

/* loaded from: classes.dex */
final class MarketingDownloader extends BaseUploadThread {
    private static final String MANIFEST_HOST_PATH_FORMAT = "%s/api/v4/applications/%s";
    private static final String MARKETING_HOST_PATH_FORMAT = "%s/api/v2/applications/%s/amp";
    private BaseUploadThread.UploadType mUploadType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MarketingDownloader(BaseUploadThread.UploadType uploadType, BaseHandler sessionHandler, TreeMap<Integer, Object> data, String customerId, LocalyticsDao localyticsDao) {
        super(sessionHandler, data, customerId, localyticsDao);
        this.mUploadType = uploadType;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:3:0x0014, code lost:            return 1;     */
    @Override // com.localytics.android.BaseUploadThread
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    final int uploadData() {
        /*
            r8 = this;
            r5 = 2
            r7 = 1
            r6 = 0
            java.lang.String r0 = r8.getApiKey()
            int[] r3 = com.localytics.android.MarketingDownloader.AnonymousClass1.$SwitchMap$com$localytics$android$BaseUploadThread$UploadType
            com.localytics.android.BaseUploadThread$UploadType r4 = r8.mUploadType
            int r4 = r4.ordinal()
            r3 = r3[r4]
            switch(r3) {
                case 1: goto L15;
                case 2: goto L42;
                default: goto L14;
            }
        L14:
            return r7
        L15:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = com.localytics.android.Constants.getHTTPPreface()
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r4 = "%s/api/v2/applications/%s/amp"
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r5 = com.localytics.android.Constants.MARKETING_HOST
            r4[r6] = r5
            r4[r7] = r0
            java.lang.String r2 = java.lang.String.format(r3, r4)
            com.localytics.android.BaseUploadThread$UploadType r3 = com.localytics.android.BaseUploadThread.UploadType.MARKETING
            java.lang.String r4 = ""
            r8.upload(r3, r2, r4, r6)
            goto L14
        L42:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = com.localytics.android.Constants.getHTTPPreface()
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r4 = "%s/api/v4/applications/%s"
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r5 = com.localytics.android.Constants.MANIFEST_HOST
            r4[r6] = r5
            r4[r7] = r0
            java.lang.String r1 = java.lang.String.format(r3, r4)
            com.localytics.android.BaseUploadThread$UploadType r3 = com.localytics.android.BaseUploadThread.UploadType.MANIFEST
            java.lang.String r4 = ""
            r8.upload(r3, r1, r4, r6)
            goto L14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.localytics.android.MarketingDownloader.uploadData():int");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String request(String urlString) throws IOException {
        InputStreamReader reader;
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestProperty("Accept-Encoding", "gzip");
        BufferedReader bufferedReader = null;
        try {
            if ("gzip".equals(connection.getContentEncoding())) {
                GZIPInputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream, "UTF-8");
            } else {
                reader = new InputStreamReader(connection.getInputStream());
            }
            BufferedReader bufferedReader2 = new BufferedReader(reader);
            try {
                StringBuilder builder = new StringBuilder();
                while (true) {
                    String line = bufferedReader2.readLine();
                    if (line == null) {
                        break;
                    }
                    builder.append(line);
                }
                String sb = builder.toString();
                try {
                    bufferedReader2.close();
                } catch (IOException e) {
                }
                return sb;
            } catch (Throwable th) {
                th = th;
                bufferedReader = bufferedReader2;
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }
}
