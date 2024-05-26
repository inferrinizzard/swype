package com.google.api.client.http.apache;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.StreamingContent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.entity.AbstractHttpEntity;

/* loaded from: classes.dex */
final class ContentEntity extends AbstractHttpEntity {
    private final long contentLength;
    private final StreamingContent streamingContent;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ContentEntity(long contentLength, StreamingContent streamingContent) {
        this.contentLength = contentLength;
        this.streamingContent = (StreamingContent) Preconditions.checkNotNull(streamingContent);
    }

    @Override // org.apache.http.HttpEntity
    public final InputStream getContent() {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.http.HttpEntity
    public final long getContentLength() {
        return this.contentLength;
    }

    @Override // org.apache.http.HttpEntity
    public final boolean isRepeatable() {
        return false;
    }

    @Override // org.apache.http.HttpEntity
    public final boolean isStreaming() {
        return true;
    }

    @Override // org.apache.http.HttpEntity
    public final void writeTo(OutputStream out) throws IOException {
        if (this.contentLength != 0) {
            this.streamingContent.writeTo(out);
        }
    }
}
