package com.google.api.client.http;

import com.google.api.client.util.Charsets;
import com.google.api.client.util.IOUtils;
import com.google.api.client.util.LoggingInputStream;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/* loaded from: classes.dex */
public final class HttpResponse {
    private InputStream content;
    private final String contentEncoding;
    private int contentLoggingLimit;
    private boolean contentRead;
    private final String contentType;
    private boolean loggingEnabled;
    private final HttpMediaType mediaType;
    private final HttpRequest request;
    LowLevelHttpResponse response;
    private final int statusCode;
    private final String statusMessage;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpResponse(HttpRequest request, LowLevelHttpResponse response) throws IOException {
        this.request = request;
        this.contentLoggingLimit = request.getContentLoggingLimit();
        this.loggingEnabled = request.isLoggingEnabled();
        this.response = response;
        this.contentEncoding = response.getContentEncoding();
        int code = response.getStatusCode();
        this.statusCode = code < 0 ? 0 : code;
        String message = response.getReasonPhrase();
        this.statusMessage = message;
        Logger logger = HttpTransport.LOGGER;
        boolean loggable = this.loggingEnabled && logger.isLoggable(Level.CONFIG);
        StringBuilder logbuf = null;
        if (loggable) {
            logbuf = new StringBuilder();
            logbuf.append("-------------- RESPONSE --------------").append(StringUtils.LINE_SEPARATOR);
            String statusLine = response.getStatusLine();
            if (statusLine != null) {
                logbuf.append(statusLine);
            } else {
                logbuf.append(this.statusCode);
                if (message != null) {
                    logbuf.append(' ').append(message);
                }
            }
            logbuf.append(StringUtils.LINE_SEPARATOR);
        }
        request.getResponseHeaders().fromHttpResponse(response, loggable ? logbuf : null);
        String contentType = response.getContentType();
        contentType = contentType == null ? request.getResponseHeaders().getContentType() : contentType;
        this.contentType = contentType;
        this.mediaType = contentType != null ? new HttpMediaType(contentType) : null;
        if (loggable) {
            logger.config(logbuf.toString());
        }
    }

    public final int getContentLoggingLimit() {
        return this.contentLoggingLimit;
    }

    public final HttpResponse setContentLoggingLimit(int contentLoggingLimit) {
        Preconditions.checkArgument(contentLoggingLimit >= 0, "The content logging limit must be non-negative.");
        this.contentLoggingLimit = contentLoggingLimit;
        return this;
    }

    public final boolean isLoggingEnabled() {
        return this.loggingEnabled;
    }

    public final HttpResponse setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
        return this;
    }

    public final String getContentEncoding() {
        return this.contentEncoding;
    }

    public final String getContentType() {
        return this.contentType;
    }

    public final HttpMediaType getMediaType() {
        return this.mediaType;
    }

    public final HttpHeaders getHeaders() {
        return this.request.getResponseHeaders();
    }

    public final boolean isSuccessStatusCode() {
        return HttpStatusCodes.isSuccess(this.statusCode);
    }

    public final int getStatusCode() {
        return this.statusCode;
    }

    public final String getStatusMessage() {
        return this.statusMessage;
    }

    public final HttpTransport getTransport() {
        return this.request.getTransport();
    }

    public final HttpRequest getRequest() {
        return this.request;
    }

    public final InputStream getContent() throws IOException {
        if (!this.contentRead) {
            InputStream lowLevelResponseContent = this.response.getContent();
            if (lowLevelResponseContent != null) {
                try {
                    String contentEncoding = this.contentEncoding;
                    InputStream lowLevelResponseContent2 = (contentEncoding == null || !contentEncoding.contains("gzip")) ? lowLevelResponseContent : new GZIPInputStream(lowLevelResponseContent);
                    try {
                        Logger logger = HttpTransport.LOGGER;
                        lowLevelResponseContent = (this.loggingEnabled && logger.isLoggable(Level.CONFIG)) ? new LoggingInputStream(lowLevelResponseContent2, logger, Level.CONFIG, this.contentLoggingLimit) : lowLevelResponseContent2;
                        this.content = lowLevelResponseContent;
                    } catch (EOFException e) {
                        lowLevelResponseContent = lowLevelResponseContent2;
                        lowLevelResponseContent.close();
                        this.contentRead = true;
                        return this.content;
                    } catch (Throwable th) {
                        th = th;
                        lowLevelResponseContent = lowLevelResponseContent2;
                        lowLevelResponseContent.close();
                        throw th;
                    }
                } catch (EOFException e2) {
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            this.contentRead = true;
        }
        return this.content;
    }

    public final void download(OutputStream outputStream) throws IOException {
        IOUtils.copy(getContent(), outputStream, true);
    }

    public final void ignore() throws IOException {
        InputStream content = getContent();
        if (content != null) {
            content.close();
        }
    }

    public final void disconnect() throws IOException {
        ignore();
        this.response.disconnect();
    }

    public final <T> T parseAs(Class<T> cls) throws IOException {
        if (hasMessageBody()) {
            return (T) this.request.getParser().parseAndClose(getContent(), getContentCharset(), (Class) cls);
        }
        return null;
    }

    private boolean hasMessageBody() throws IOException {
        int statusCode = getStatusCode();
        if (!getRequest().getRequestMethod().equals(HttpMethods.HEAD) && statusCode / 100 != 1 && statusCode != 204 && statusCode != 304) {
            return true;
        }
        ignore();
        return false;
    }

    public final Object parseAs(Type dataType) throws IOException {
        if (hasMessageBody()) {
            return this.request.getParser().parseAndClose(getContent(), getContentCharset(), dataType);
        }
        return null;
    }

    public final String parseAsString() throws IOException {
        InputStream content = getContent();
        if (content == null) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(content, out, true);
        return out.toString(getContentCharset().name());
    }

    public final Charset getContentCharset() {
        return (this.mediaType == null || this.mediaType.getCharsetParameter() == null) ? Charsets.ISO_8859_1 : this.mediaType.getCharsetParameter();
    }
}
