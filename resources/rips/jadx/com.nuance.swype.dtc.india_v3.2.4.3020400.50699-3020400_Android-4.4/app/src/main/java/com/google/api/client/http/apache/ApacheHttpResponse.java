package com.google.api.client.http.apache;

import com.google.api.client.http.LowLevelHttpResponse;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpRequestBase;

/* loaded from: classes.dex */
final class ApacheHttpResponse extends LowLevelHttpResponse {
    private final Header[] allHeaders;
    private final HttpRequestBase request;
    private final HttpResponse response;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ApacheHttpResponse(HttpRequestBase request, HttpResponse response) {
        this.request = request;
        this.response = response;
        this.allHeaders = response.getAllHeaders();
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final int getStatusCode() {
        StatusLine statusLine = this.response.getStatusLine();
        if (statusLine == null) {
            return 0;
        }
        return statusLine.getStatusCode();
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final InputStream getContent() throws IOException {
        HttpEntity entity = this.response.getEntity();
        if (entity == null) {
            return null;
        }
        return entity.getContent();
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final String getContentEncoding() {
        Header contentEncodingHeader;
        HttpEntity entity = this.response.getEntity();
        if (entity == null || (contentEncodingHeader = entity.getContentEncoding()) == null) {
            return null;
        }
        return contentEncodingHeader.getValue();
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final long getContentLength() {
        HttpEntity entity = this.response.getEntity();
        if (entity == null) {
            return -1L;
        }
        return entity.getContentLength();
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final String getContentType() {
        Header contentTypeHeader;
        HttpEntity entity = this.response.getEntity();
        if (entity == null || (contentTypeHeader = entity.getContentType()) == null) {
            return null;
        }
        return contentTypeHeader.getValue();
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final String getReasonPhrase() {
        StatusLine statusLine = this.response.getStatusLine();
        if (statusLine == null) {
            return null;
        }
        return statusLine.getReasonPhrase();
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final String getStatusLine() {
        StatusLine statusLine = this.response.getStatusLine();
        if (statusLine == null) {
            return null;
        }
        return statusLine.toString();
    }

    public final String getHeaderValue(String name) {
        return this.response.getLastHeader(name).getValue();
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final int getHeaderCount() {
        return this.allHeaders.length;
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final String getHeaderName(int index) {
        return this.allHeaders[index].getName();
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final String getHeaderValue(int index) {
        return this.allHeaders[index].getValue();
    }

    @Override // com.google.api.client.http.LowLevelHttpResponse
    public final void disconnect() {
        this.request.abort();
    }
}
