package com.google.api.client.http.javanet;

import com.google.api.client.http.LowLevelHttpResponse;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class NetHttpResponse extends LowLevelHttpResponse {
    private final HttpURLConnection connection;
    private final ArrayList<String> headerNames = new ArrayList<>();
    private final ArrayList<String> headerValues = new ArrayList<>();
    private final int responseCode;
    private final String responseMessage;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NetHttpResponse(HttpURLConnection connection) throws IOException {
        this.connection = connection;
        int responseCode = connection.getResponseCode();
        this.responseCode = responseCode == -1 ? 0 : responseCode;
        this.responseMessage = connection.getResponseMessage();
        List<String> headerNames = this.headerNames;
        List<String> headerValues = this.headerValues;
        for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
            String key = entry.getKey();
            if (key != null) {
                for (String value : entry.getValue()) {
                    if (value != null) {
                        headerNames.add(key);
                        headerValues.add(value);
                    }
                }
            }
        }
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final int getStatusCode() {
        return this.responseCode;
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final InputStream getContent() throws IOException {
        InputStream in;
        try {
            in = this.connection.getInputStream();
        } catch (IOException e) {
            in = this.connection.getErrorStream();
        }
        if (in == null) {
            return null;
        }
        return new SizeValidatingInputStream(in);
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final String getContentEncoding() {
        return this.connection.getContentEncoding();
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final long getContentLength() {
        String string = this.connection.getHeaderField("Content-Length");
        if (string == null) {
            return -1L;
        }
        return Long.parseLong(string);
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final String getContentType() {
        return this.connection.getHeaderField("Content-Type");
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final String getReasonPhrase() {
        return this.responseMessage;
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final String getStatusLine() {
        String result = this.connection.getHeaderField(0);
        if (result == null || !result.startsWith("HTTP/1.")) {
            return null;
        }
        return result;
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final int getHeaderCount() {
        return this.headerNames.size();
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final String getHeaderName(int index) {
        return this.headerNames.get(index);
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final String getHeaderValue(int index) {
        return this.headerValues.get(index);
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final void disconnect() {
        this.connection.disconnect();
    }

    /* loaded from: classes.dex */
    private final class SizeValidatingInputStream extends FilterInputStream {
        private long bytesRead;

        public SizeValidatingInputStream(InputStream in) {
            super(in);
            this.bytesRead = 0L;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public final int read(byte[] b, int off, int len) throws IOException {
            int n = this.in.read(b, off, len);
            if (n == -1) {
                throwIfFalseEOF();
            } else {
                this.bytesRead += n;
            }
            return n;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public final int read() throws IOException {
            int n = this.in.read();
            if (n == -1) {
                throwIfFalseEOF();
            } else {
                this.bytesRead++;
            }
            return n;
        }

        private void throwIfFalseEOF() throws IOException {
            long contentLength = NetHttpResponse.this.getContentLength();
            if (contentLength != -1 && this.bytesRead != 0 && this.bytesRead < contentLength) {
                throw new IOException("Connection closed prematurely: bytesRead = " + this.bytesRead + ", Content-Length = " + contentLength);
            }
        }
    }
}
