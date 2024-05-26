package com.google.api.client.http.javanet;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.util.Preconditions;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/* loaded from: classes.dex */
final class NetHttpRequest extends LowLevelHttpRequest {
    private final HttpURLConnection connection;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NetHttpRequest(HttpURLConnection connection) {
        this.connection = connection;
        connection.setInstanceFollowRedirects(false);
    }

    @Override // com.google.api.client.http.LowLevelHttpRequest
    public final void addHeader(String name, String value) {
        this.connection.addRequestProperty(name, value);
    }

    @Override // com.google.api.client.http.LowLevelHttpRequest
    public final void setTimeout(int connectTimeout, int readTimeout) {
        this.connection.setReadTimeout(readTimeout);
        this.connection.setConnectTimeout(connectTimeout);
    }

    @Override // com.google.api.client.http.LowLevelHttpRequest
    public final LowLevelHttpResponse execute() throws IOException {
        HttpURLConnection connection = this.connection;
        if (getStreamingContent() != null) {
            String contentType = getContentType();
            if (contentType != null) {
                addHeader("Content-Type", contentType);
            }
            String contentEncoding = getContentEncoding();
            if (contentEncoding != null) {
                addHeader("Content-Encoding", contentEncoding);
            }
            long contentLength = getContentLength();
            if (contentLength >= 0) {
                addHeader("Content-Length", Long.toString(contentLength));
            }
            String requestMethod = connection.getRequestMethod();
            if ("POST".equals(requestMethod) || "PUT".equals(requestMethod)) {
                connection.setDoOutput(true);
                if (contentLength >= 0 && contentLength <= 2147483647L) {
                    connection.setFixedLengthStreamingMode((int) contentLength);
                } else {
                    connection.setChunkedStreamingMode(0);
                }
                OutputStream out = connection.getOutputStream();
                try {
                    getStreamingContent().writeTo(out);
                } finally {
                    out.close();
                }
            } else {
                Preconditions.checkArgument(contentLength == 0, "%s with non-zero content length is not supported", requestMethod);
            }
        }
        try {
            connection.connect();
            NetHttpResponse response = new NetHttpResponse(connection);
            return response;
        } catch (Throwable th) {
            if (0 == 0) {
                connection.disconnect();
            }
            throw th;
        }
    }
}
