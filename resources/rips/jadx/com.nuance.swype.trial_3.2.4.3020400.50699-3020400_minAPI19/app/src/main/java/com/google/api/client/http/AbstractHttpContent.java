package com.google.api.client.http;

import com.google.api.client.util.Charsets;
import com.google.api.client.util.IOUtils;
import java.io.IOException;
import java.nio.charset.Charset;

/* loaded from: classes.dex */
public abstract class AbstractHttpContent implements HttpContent {
    private long computedLength;
    private HttpMediaType mediaType;

    public AbstractHttpContent(String mediaType) {
        this(mediaType == null ? null : new HttpMediaType(mediaType));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractHttpContent(HttpMediaType mediaType) {
        this.computedLength = -1L;
        this.mediaType = mediaType;
    }

    @Override // com.google.api.client.http.HttpContent
    public long getLength() throws IOException {
        if (this.computedLength == -1) {
            this.computedLength = computeLength();
        }
        return this.computedLength;
    }

    public final HttpMediaType getMediaType() {
        return this.mediaType;
    }

    public AbstractHttpContent setMediaType(HttpMediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public final Charset getCharset() {
        return (this.mediaType == null || this.mediaType.getCharsetParameter() == null) ? Charsets.UTF_8 : this.mediaType.getCharsetParameter();
    }

    @Override // com.google.api.client.http.HttpContent
    public String getType() {
        if (this.mediaType == null) {
            return null;
        }
        return this.mediaType.build();
    }

    protected long computeLength() throws IOException {
        return computeLength(this);
    }

    @Override // com.google.api.client.http.HttpContent
    public boolean retrySupported() {
        return true;
    }

    public static long computeLength(HttpContent content) throws IOException {
        if (content.retrySupported()) {
            return IOUtils.computeLength(content);
        }
        return -1L;
    }
}
