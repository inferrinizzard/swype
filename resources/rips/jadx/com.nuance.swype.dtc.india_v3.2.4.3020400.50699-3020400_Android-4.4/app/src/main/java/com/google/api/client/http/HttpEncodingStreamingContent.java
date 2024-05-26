package com.google.api.client.http;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.StreamingContent;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class HttpEncodingStreamingContent implements StreamingContent {
    private final StreamingContent content;
    private final HttpEncoding encoding;

    public HttpEncodingStreamingContent(StreamingContent content, HttpEncoding encoding) {
        this.content = (StreamingContent) Preconditions.checkNotNull(content);
        this.encoding = (HttpEncoding) Preconditions.checkNotNull(encoding);
    }

    @Override // com.google.api.client.util.StreamingContent
    public final void writeTo(OutputStream out) throws IOException {
        this.encoding.encode(this.content, out);
    }

    public final StreamingContent getContent() {
        return this.content;
    }

    public final HttpEncoding getEncoding() {
        return this.encoding;
    }
}
