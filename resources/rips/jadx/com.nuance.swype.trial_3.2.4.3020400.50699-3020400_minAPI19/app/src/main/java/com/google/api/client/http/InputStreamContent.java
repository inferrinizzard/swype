package com.google.api.client.http;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class InputStreamContent extends AbstractInputStreamContent {
    private final InputStream inputStream;
    private long length;
    private boolean retrySupported;

    public InputStreamContent(String type, InputStream inputStream) {
        super(type);
        this.length = -1L;
        this.inputStream = (InputStream) Preconditions.checkNotNull(inputStream);
    }

    @Override // com.google.api.client.http.HttpContent
    public final long getLength() {
        return this.length;
    }

    @Override // com.google.api.client.http.HttpContent
    public final boolean retrySupported() {
        return this.retrySupported;
    }

    public final InputStreamContent setRetrySupported(boolean retrySupported) {
        this.retrySupported = retrySupported;
        return this;
    }

    @Override // com.google.api.client.http.AbstractInputStreamContent
    public final InputStream getInputStream() {
        return this.inputStream;
    }

    @Override // com.google.api.client.http.AbstractInputStreamContent
    public final InputStreamContent setType(String type) {
        return (InputStreamContent) super.setType(type);
    }

    @Override // com.google.api.client.http.AbstractInputStreamContent
    public final InputStreamContent setCloseInputStream(boolean closeInputStream) {
        return (InputStreamContent) super.setCloseInputStream(closeInputStream);
    }

    public final InputStreamContent setLength(long length) {
        this.length = length;
        return this;
    }
}
