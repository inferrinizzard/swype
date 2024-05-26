package com.localytics.android;

import android.text.TextUtils;
import com.google.api.client.http.ExponentialBackOffPolicy;
import com.localytics.android.Localytics;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;
import org.json.JSONObject;

/* loaded from: classes.dex */
abstract class BaseUploadThread extends Thread {
    String customerID;
    final TreeMap<Integer, Object> mData;
    LocalyticsDao mLocalyticsDao;
    private final BaseHandler mSessionHandler;
    private boolean mSuccessful;
    private String uploadResponseString = null;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public enum UploadType {
        ANALYTICS,
        PROFILES,
        MARKETING,
        MANIFEST
    }

    abstract int uploadData();

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseUploadThread(BaseHandler sessionHandler, TreeMap<Integer, Object> data, String customerId, LocalyticsDao localyticsDao) {
        this.mSessionHandler = sessionHandler;
        this.mData = data;
        this.customerID = customerId;
        this.mLocalyticsDao = localyticsDao;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getApiKey() {
        String apiKey = this.mLocalyticsDao.getAppKey();
        String rollupKey = DatapointHelper.getLocalyticsRollupKeyOrNull(this.mLocalyticsDao.getAppContext());
        if (rollupKey != null && !TextUtils.isEmpty(rollupKey)) {
            return rollupKey;
        }
        return apiKey;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0, types: [com.localytics.android.BaseHandler] */
    /* JADX WARN: Type inference failed for: r3v3, types: [com.localytics.android.BaseHandler] */
    /* JADX WARN: Type inference failed for: r4v0, types: [java.lang.Object[], java.lang.Object] */
    /* JADX WARN: Type inference failed for: r4v3, types: [java.lang.Object[], java.lang.Object] */
    /* JADX WARN: Type inference failed for: r5v11, types: [java.lang.Boolean] */
    /* JADX WARN: Type inference failed for: r5v4, types: [java.lang.Boolean] */
    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        int i = 3;
        i = 3;
        try {
            try {
                int uploadData = uploadData();
                BaseHandler baseHandler = this.mSessionHandler;
                ?? r3 = this.mSessionHandler;
                ?? valueOf = Boolean.valueOf(this.mSuccessful);
                baseHandler.sendMessage(r3.obtainMessage(4, new Object[]{Integer.valueOf(uploadData), this.uploadResponseString, valueOf}));
                i = valueOf;
            } catch (Exception e) {
                Localytics.Log.e("Exception", e);
                BaseHandler baseHandler2 = this.mSessionHandler;
                ?? r32 = this.mSessionHandler;
                ?? valueOf2 = Boolean.valueOf(this.mSuccessful);
                baseHandler2.sendMessage(r32.obtainMessage(4, new Object[]{0, this.uploadResponseString, valueOf2}));
                i = valueOf2;
            }
        } catch (Throwable th) {
            BaseHandler baseHandler3 = this.mSessionHandler;
            BaseHandler baseHandler4 = this.mSessionHandler;
            Object[] objArr = new Object[i];
            objArr[0] = 0;
            objArr[1] = this.uploadResponseString;
            objArr[2] = Boolean.valueOf(this.mSuccessful);
            baseHandler3.sendMessage(baseHandler4.obtainMessage(4, objArr));
            throw th;
        }
    }

    private static String formatUploadBody(String body) {
        try {
            return new JSONObject(body).toString(3);
        } catch (Exception e) {
            return body;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean upload(UploadType uploadType, String url, String body, int attempt) {
        this.mSuccessful = upload(uploadType, url, body, attempt, false);
        return this.mSuccessful;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean upload(UploadType uploadType, String url, String body, int attempt, boolean noDelay) {
        byte[] uploadData;
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null");
        }
        if (body == null) {
            throw new IllegalArgumentException("body cannot be null");
        }
        if (uploadType == UploadType.ANALYTICS) {
            Localytics.Log.v(String.format("Analytics upload body before compression is: \n%s", body));
        } else if (uploadType == UploadType.PROFILES) {
            Localytics.Log.v(String.format("Profile upload body is: \n%s", formatUploadBody(body)));
        }
        GZIPOutputStream gos = null;
        try {
            try {
                byte[] originalBytes = body.getBytes("UTF-8");
                if (uploadType == UploadType.ANALYTICS) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(originalBytes.length);
                    GZIPOutputStream gos2 = new GZIPOutputStream(baos);
                    try {
                        gos2.write(originalBytes);
                        gos2.finish();
                        uploadData = baos.toByteArray();
                        gos = gos2;
                    } catch (UnsupportedEncodingException e) {
                        e = e;
                        gos = gos2;
                        Localytics.Log.w("UnsupportedEncodingException", e);
                        if (gos != null) {
                            try {
                                gos.close();
                            } catch (IOException e2) {
                                Localytics.Log.w("Caught exception", e2);
                                return false;
                            }
                        }
                        return false;
                    } catch (IOException e3) {
                        e = e3;
                        gos = gos2;
                        Localytics.Log.w("IOException", e);
                        if (gos != null) {
                            try {
                                gos.close();
                            } catch (IOException e4) {
                                Localytics.Log.w("Caught exception", e4);
                                return false;
                            }
                        }
                        return false;
                    } catch (Throwable th) {
                        th = th;
                        gos = gos2;
                        if (gos != null) {
                            try {
                                gos.close();
                            } catch (IOException e5) {
                                Localytics.Log.w("Caught exception", e5);
                                return false;
                            }
                        }
                        throw th;
                    }
                } else {
                    uploadData = originalBytes;
                }
                if (gos != null) {
                    try {
                        gos.close();
                    } catch (IOException e6) {
                        Localytics.Log.w("Caught exception", e6);
                        return false;
                    }
                }
                HttpURLConnection connection = null;
                try {
                    try {
                        try {
                            try {
                                Proxy proxy = this.mLocalyticsDao.getProxy();
                                HttpURLConnection connection2 = (HttpURLConnection) createURLConnection(new URL(url), proxy);
                                connection2.setConnectTimeout(ExponentialBackOffPolicy.DEFAULT_MAX_INTERVAL_MILLIS);
                                connection2.setReadTimeout(ExponentialBackOffPolicy.DEFAULT_MAX_INTERVAL_MILLIS);
                                connection2.setDoOutput((uploadType == UploadType.MARKETING || uploadType == UploadType.MANIFEST) ? false : true);
                                if (uploadType == UploadType.ANALYTICS) {
                                    connection2.setRequestProperty("Content-Type", "application/x-gzip");
                                    connection2.setRequestProperty("Content-Encoding", "gzip");
                                    connection2.setRequestProperty("X-DONT-SEND-AMP", "1");
                                } else {
                                    connection2.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                                }
                                if ((uploadType == UploadType.MARKETING || uploadType == UploadType.MANIFEST) && Constants.isTestModeEnabled()) {
                                    connection2.setRequestProperty("AMP-Test-Mode", "1");
                                }
                                if (noDelay) {
                                    connection2.setRequestProperty("X-NO-DELAY", "1");
                                }
                                connection2.setRequestProperty("Accept-Encoding", "");
                                connection2.setRequestProperty("x-upload-time", Long.toString(Math.round(this.mLocalyticsDao.getCurrentTimeMillis() / 1000.0d)));
                                connection2.setRequestProperty("x-install-id", this.mLocalyticsDao.getInstallationId());
                                connection2.setRequestProperty("x-app-id", this.mLocalyticsDao.getAppKey());
                                connection2.setRequestProperty("x-client-version", Constants.LOCALYTICS_CLIENT_LIBRARY_VERSION);
                                connection2.setRequestProperty("x-app-version", DatapointHelper.getAppVersion(this.mLocalyticsDao.getAppContext()));
                                connection2.setRequestProperty("x-customer-id", this.customerID);
                                if (uploadType != UploadType.MARKETING && uploadType != UploadType.MANIFEST) {
                                    connection2.setFixedLengthStreamingMode(uploadData.length);
                                    OutputStream stream = null;
                                    try {
                                        stream = connection2.getOutputStream();
                                        stream.write(uploadData);
                                    } finally {
                                        if (stream != null) {
                                            stream.flush();
                                            stream.close();
                                        }
                                    }
                                }
                                int statusCode = connection2.getResponseCode();
                                Localytics.Log.v(String.format("%s upload complete with status %d", this.mSessionHandler.siloName, Integer.valueOf(statusCode)));
                                if (statusCode == 429) {
                                    if (connection2 != null) {
                                        connection2.disconnect();
                                    }
                                    return false;
                                }
                                if (statusCode >= 400 && statusCode <= 499) {
                                    if (connection2 != null) {
                                        connection2.disconnect();
                                    }
                                    return true;
                                }
                                if (statusCode >= 500 && statusCode <= 599) {
                                    if (connection2 != null) {
                                        connection2.disconnect();
                                    }
                                    return false;
                                }
                                retrieveHttpResponse(connection2.getInputStream());
                                if (connection2 != null) {
                                    connection2.disconnect();
                                }
                                return true;
                            } catch (EOFException e7) {
                                if (attempt == 2) {
                                    Localytics.Log.w("ClientProtocolException", e7);
                                    if (0 != 0) {
                                        connection.disconnect();
                                    }
                                    return false;
                                }
                                boolean upload = upload(uploadType, url, body, attempt + 1, noDelay);
                                if (0 == 0) {
                                    return upload;
                                }
                                connection.disconnect();
                                return upload;
                            }
                        } catch (MalformedURLException e8) {
                            Localytics.Log.w("ClientProtocolException", e8);
                            if (0 != 0) {
                                connection.disconnect();
                            }
                            return false;
                        }
                    } catch (IOException e9) {
                        Localytics.Log.w("ClientProtocolException", e9);
                        if (0 != 0) {
                            connection.disconnect();
                        }
                        return false;
                    }
                } catch (Throwable th2) {
                    if (0 != 0) {
                        connection.disconnect();
                    }
                    throw th2;
                }
            } catch (UnsupportedEncodingException e10) {
                e = e10;
            } catch (IOException e11) {
                e = e11;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    private void retrieveHttpResponse(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        StringBuilder builder = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            } else {
                builder.append(line);
            }
        }
        String response = builder.toString();
        if (!TextUtils.isEmpty(response)) {
            onUploadResponded(response);
        }
        reader.close();
    }

    void onUploadResponded(String response) {
        Localytics.Log.w(String.format("%s upload response: \n%s", this.mSessionHandler.siloName, response));
        this.uploadResponseString = response;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static URLConnection createURLConnection(URL url, Proxy proxy) throws IOException {
        return proxy == null ? url.openConnection() : url.openConnection(proxy);
    }
}
