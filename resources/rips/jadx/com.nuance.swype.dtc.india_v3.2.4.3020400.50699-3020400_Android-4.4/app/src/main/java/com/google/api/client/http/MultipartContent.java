package com.google.api.client.http;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.StreamingContent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/* loaded from: classes.dex */
public class MultipartContent extends AbstractHttpContent {
    static final String NEWLINE = "\r\n";
    private static final String TWO_DASHES = "--";
    private ArrayList<Part> parts;

    public MultipartContent() {
        super(new HttpMediaType("multipart/related").setParameter("boundary", "__END_OF_PART__"));
        this.parts = new ArrayList<>();
    }

    @Override // com.google.api.client.http.HttpContent, com.google.api.client.util.StreamingContent
    public void writeTo(OutputStream out) throws IOException {
        long contentLength;
        Writer writer = new OutputStreamWriter(out, getCharset());
        String boundary = getBoundary();
        Iterator i$ = this.parts.iterator();
        while (i$.hasNext()) {
            Part part = i$.next();
            HttpHeaders headers = new HttpHeaders().setAcceptEncoding(null);
            if (part.headers != null) {
                headers.fromHttpHeaders(part.headers);
            }
            headers.setContentEncoding(null).setUserAgent(null).setContentType(null).setContentLength(null).set("Content-Transfer-Encoding", (Object) null);
            HttpContent content = part.content;
            StreamingContent streamingContent = null;
            if (content != null) {
                headers.set("Content-Transfer-Encoding", (Object) Arrays.asList("binary"));
                headers.setContentType(content.getType());
                HttpEncoding encoding = part.encoding;
                if (encoding == null) {
                    contentLength = content.getLength();
                    streamingContent = content;
                } else {
                    headers.setContentEncoding(encoding.getName());
                    streamingContent = new HttpEncodingStreamingContent(content, encoding);
                    contentLength = AbstractHttpContent.computeLength(content);
                }
                if (contentLength != -1) {
                    headers.setContentLength(Long.valueOf(contentLength));
                }
            }
            writer.write(TWO_DASHES);
            writer.write(boundary);
            writer.write(NEWLINE);
            HttpHeaders.serializeHeadersForMultipartRequests(headers, null, null, writer);
            if (streamingContent != null) {
                writer.write(NEWLINE);
                writer.flush();
                streamingContent.writeTo(out);
            }
            writer.write(NEWLINE);
        }
        writer.write(TWO_DASHES);
        writer.write(boundary);
        writer.write(TWO_DASHES);
        writer.write(NEWLINE);
        writer.flush();
    }

    @Override // com.google.api.client.http.AbstractHttpContent, com.google.api.client.http.HttpContent
    public boolean retrySupported() {
        Iterator i$ = this.parts.iterator();
        while (i$.hasNext()) {
            if (!i$.next().content.retrySupported()) {
                return false;
            }
        }
        return true;
    }

    @Override // com.google.api.client.http.AbstractHttpContent
    public MultipartContent setMediaType(HttpMediaType mediaType) {
        super.setMediaType(mediaType);
        return this;
    }

    public final Collection<Part> getParts() {
        return Collections.unmodifiableCollection(this.parts);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public MultipartContent addPart(Part part) {
        this.parts.add(Preconditions.checkNotNull(part));
        return this;
    }

    public MultipartContent setParts(Collection<Part> parts) {
        this.parts = new ArrayList<>(parts);
        return this;
    }

    public MultipartContent setContentParts(Collection<? extends HttpContent> contentParts) {
        this.parts = new ArrayList<>(contentParts.size());
        for (HttpContent contentPart : contentParts) {
            addPart(new Part(contentPart));
        }
        return this;
    }

    public final String getBoundary() {
        return getMediaType().getParameter("boundary");
    }

    public MultipartContent setBoundary(String boundary) {
        getMediaType().setParameter("boundary", (String) Preconditions.checkNotNull(boundary));
        return this;
    }

    /* loaded from: classes.dex */
    public static final class Part {
        HttpContent content;
        HttpEncoding encoding;
        HttpHeaders headers;

        public Part() {
            this(null);
        }

        public Part(HttpContent content) {
            this(null, content);
        }

        public Part(HttpHeaders headers, HttpContent content) {
            setHeaders(headers);
            setContent(content);
        }

        public final Part setContent(HttpContent content) {
            this.content = content;
            return this;
        }

        public final HttpContent getContent() {
            return this.content;
        }

        public final Part setHeaders(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public final HttpHeaders getHeaders() {
            return this.headers;
        }

        public final Part setEncoding(HttpEncoding encoding) {
            this.encoding = encoding;
            return this;
        }

        public final HttpEncoding getEncoding() {
            return this.encoding;
        }
    }
}
