package com.google.api.client.googleapis.media;

import com.facebook.share.internal.ShareConstants;
import com.google.api.client.googleapis.MethodOverride;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.EmptyContent;
import com.google.api.client.http.GZipEncoding;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.MultipartContent;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.Sleeper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class MediaHttpUploader {
    private Byte cachedByte;
    private InputStream contentInputStream;
    private int currentChunkLength;
    private HttpRequest currentRequest;
    private byte[] currentRequestContentBuffer;
    public boolean directUploadEnabled;
    public boolean disableGZipContent;
    private boolean isMediaContentLengthCalculated;
    private final AbstractInputStreamContent mediaContent;
    private long mediaContentLength;
    public HttpContent metadata;
    private final HttpRequestFactory requestFactory;
    private long totalBytesClientSent;
    private long totalBytesServerReceived;
    private final HttpTransport transport;
    public UploadState uploadState = UploadState.NOT_STARTED;
    public String initiationRequestMethod = "POST";
    public HttpHeaders initiationHeaders = new HttpHeaders();
    String mediaContentLengthStr = "*";
    private int chunkSize = 10485760;
    Sleeper sleeper = Sleeper.DEFAULT;

    /* loaded from: classes.dex */
    public enum UploadState {
        NOT_STARTED,
        INITIATION_STARTED,
        INITIATION_COMPLETE,
        MEDIA_IN_PROGRESS,
        MEDIA_COMPLETE
    }

    public MediaHttpUploader(AbstractInputStreamContent mediaContent, HttpTransport transport, HttpRequestInitializer httpRequestInitializer) {
        this.mediaContent = (AbstractInputStreamContent) Preconditions.checkNotNull(mediaContent);
        this.transport = (HttpTransport) Preconditions.checkNotNull(transport);
        this.requestFactory = httpRequestInitializer == null ? transport.createRequestFactory() : transport.createRequestFactory(httpRequestInitializer);
    }

    public final HttpResponse directUpload(GenericUrl initiationRequestUrl) throws IOException {
        this.uploadState = UploadState.MEDIA_IN_PROGRESS;
        HttpContent content = this.mediaContent;
        if (this.metadata != null) {
            content = new MultipartContent().setContentParts(Arrays.asList(this.metadata, this.mediaContent));
            initiationRequestUrl.put("uploadType", "multipart");
        } else {
            initiationRequestUrl.put("uploadType", ShareConstants.WEB_DIALOG_PARAM_MEDIA);
        }
        HttpRequest request = this.requestFactory.buildRequest(this.initiationRequestMethod, initiationRequestUrl, content);
        request.getHeaders().putAll(this.initiationHeaders);
        HttpResponse response = executeCurrentRequest(request);
        try {
            if (isMediaLengthKnown()) {
                this.totalBytesServerReceived = getMediaContentLength();
            }
            this.uploadState = UploadState.MEDIA_COMPLETE;
            return response;
        } catch (Throwable th) {
            response.disconnect();
            throw th;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:64:0x0153, code lost:            r29.totalBytesServerReceived = getMediaContentLength();     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x0167, code lost:            if (r29.mediaContent.getCloseInputStream() == false) goto L30;     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x0169, code lost:            r29.contentInputStream.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x0172, code lost:            r29.uploadState = com.google.api.client.googleapis.media.MediaHttpUploader.UploadState.MEDIA_COMPLETE;     */
    /* JADX WARN: Code restructure failed: missing block: B:69:?, code lost:            return r13;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final com.google.api.client.http.HttpResponse resumableUpload(com.google.api.client.http.GenericUrl r30) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 1057
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.api.client.googleapis.media.MediaHttpUploader.resumableUpload(com.google.api.client.http.GenericUrl):com.google.api.client.http.HttpResponse");
    }

    private boolean isMediaLengthKnown() throws IOException {
        return getMediaContentLength() >= 0;
    }

    private long getMediaContentLength() throws IOException {
        if (!this.isMediaContentLengthCalculated) {
            this.mediaContentLength = this.mediaContent.getLength();
            this.isMediaContentLengthCalculated = true;
        }
        return this.mediaContentLength;
    }

    private HttpResponse executeUploadInitiation(GenericUrl initiationRequestUrl) throws IOException {
        this.uploadState = UploadState.INITIATION_STARTED;
        initiationRequestUrl.put("uploadType", "resumable");
        HttpContent content = this.metadata == null ? new EmptyContent() : this.metadata;
        HttpRequest request = this.requestFactory.buildRequest(this.initiationRequestMethod, initiationRequestUrl, content);
        this.initiationHeaders.set("X-Upload-Content-Type", (Object) this.mediaContent.getType());
        if (isMediaLengthKnown()) {
            this.initiationHeaders.set("X-Upload-Content-Length", (Object) Long.valueOf(getMediaContentLength()));
        }
        request.getHeaders().putAll(this.initiationHeaders);
        HttpResponse response = executeCurrentRequest(request);
        try {
            this.uploadState = UploadState.INITIATION_COMPLETE;
            return response;
        } catch (Throwable th) {
            response.disconnect();
            throw th;
        }
    }

    private static HttpResponse executeCurrentRequestWithoutGZip(HttpRequest request) throws IOException {
        new MethodOverride().intercept(request);
        request.setThrowExceptionOnExecuteError(false);
        return request.execute();
    }

    private HttpResponse executeCurrentRequest(HttpRequest request) throws IOException {
        if (!this.disableGZipContent && !(request.getContent() instanceof EmptyContent)) {
            request.setEncoding(new GZipEncoding());
        }
        return executeCurrentRequestWithoutGZip(request);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void serverErrorCallback() throws IOException {
        com.google.api.client.util.Preconditions.checkNotNull(this.currentRequest, "The current request should not be null");
        this.currentRequest.setContent(new EmptyContent());
        this.currentRequest.getHeaders().setContentRange("bytes */" + this.mediaContentLengthStr);
    }
}
