package com.google.api.client.googleapis.services;

import com.facebook.share.internal.ShareConstants;
import com.google.api.client.googleapis.MethodOverride;
import com.google.api.client.googleapis.batch.BatchCallback;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.EmptyContent;
import com.google.api.client.http.GZipEncoding;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpResponseInterceptor;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.GenericData;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public abstract class AbstractGoogleClientRequest<T> extends GenericData {
    public static final String USER_AGENT_SUFFIX = "Google-API-Java-Client";
    private final AbstractGoogleClient abstractGoogleClient;
    private boolean disableGZipContent;
    private MediaHttpDownloader downloader;
    private final HttpContent httpContent;
    private HttpHeaders lastResponseHeaders;
    private String lastStatusMessage;
    private final String requestMethod;
    private Class<T> responseClass;
    private MediaHttpUploader uploader;
    private final String uriTemplate;
    private HttpHeaders requestHeaders = new HttpHeaders();
    private int lastStatusCode = -1;

    public AbstractGoogleClientRequest(AbstractGoogleClient abstractGoogleClient, String requestMethod, String uriTemplate, HttpContent httpContent, Class<T> responseClass) {
        this.responseClass = (Class) Preconditions.checkNotNull(responseClass);
        this.abstractGoogleClient = (AbstractGoogleClient) Preconditions.checkNotNull(abstractGoogleClient);
        this.requestMethod = (String) Preconditions.checkNotNull(requestMethod);
        this.uriTemplate = (String) Preconditions.checkNotNull(uriTemplate);
        this.httpContent = httpContent;
        String applicationName = abstractGoogleClient.getApplicationName();
        if (applicationName != null) {
            this.requestHeaders.setUserAgent(applicationName + " Google-API-Java-Client");
        } else {
            this.requestHeaders.setUserAgent(USER_AGENT_SUFFIX);
        }
    }

    public final boolean getDisableGZipContent() {
        return this.disableGZipContent;
    }

    public AbstractGoogleClientRequest<T> setDisableGZipContent(boolean disableGZipContent) {
        this.disableGZipContent = disableGZipContent;
        return this;
    }

    public final String getRequestMethod() {
        return this.requestMethod;
    }

    public final String getUriTemplate() {
        return this.uriTemplate;
    }

    public final HttpContent getHttpContent() {
        return this.httpContent;
    }

    public AbstractGoogleClient getAbstractGoogleClient() {
        return this.abstractGoogleClient;
    }

    public final HttpHeaders getRequestHeaders() {
        return this.requestHeaders;
    }

    public AbstractGoogleClientRequest<T> setRequestHeaders(HttpHeaders headers) {
        this.requestHeaders = headers;
        return this;
    }

    public final HttpHeaders getLastResponseHeaders() {
        return this.lastResponseHeaders;
    }

    public final int getLastStatusCode() {
        return this.lastStatusCode;
    }

    public final String getLastStatusMessage() {
        return this.lastStatusMessage;
    }

    public final Class<T> getResponseClass() {
        return this.responseClass;
    }

    public final MediaHttpUploader getMediaHttpUploader() {
        return this.uploader;
    }

    public final void initializeMediaUpload(AbstractInputStreamContent mediaContent) {
        HttpRequestFactory requestFactory = this.abstractGoogleClient.getRequestFactory();
        this.uploader = new MediaHttpUploader(mediaContent, requestFactory.getTransport(), requestFactory.getInitializer());
        MediaHttpUploader mediaHttpUploader = this.uploader;
        String str = this.requestMethod;
        Preconditions.checkArgument(str.equals("POST") || str.equals("PUT") || str.equals(HttpMethods.PATCH));
        mediaHttpUploader.initiationRequestMethod = str;
        if (this.httpContent == null) {
            return;
        }
        this.uploader.metadata = this.httpContent;
    }

    public final MediaHttpDownloader getMediaHttpDownloader() {
        return this.downloader;
    }

    protected final void initializeMediaDownload() {
        HttpRequestFactory requestFactory = this.abstractGoogleClient.getRequestFactory();
        this.downloader = new MediaHttpDownloader(requestFactory.getTransport(), requestFactory.getInitializer());
    }

    public GenericUrl buildHttpRequestUrl() {
        return new GenericUrl(UriTemplate.expand(this.abstractGoogleClient.getBaseUrl(), this.uriTemplate, this, true));
    }

    public HttpRequest buildHttpRequest() throws IOException {
        return buildHttpRequest(false);
    }

    public HttpRequest buildHttpRequestUsingHead() throws IOException {
        return buildHttpRequest(true);
    }

    private HttpRequest buildHttpRequest(boolean usingHead) throws IOException {
        Preconditions.checkArgument(this.uploader == null);
        Preconditions.checkArgument(!usingHead || this.requestMethod.equals("GET"));
        String requestMethodToUse = usingHead ? HttpMethods.HEAD : this.requestMethod;
        final HttpRequest httpRequest = getAbstractGoogleClient().getRequestFactory().buildRequest(requestMethodToUse, buildHttpRequestUrl(), this.httpContent);
        new MethodOverride().intercept(httpRequest);
        httpRequest.setParser(getAbstractGoogleClient().getObjectParser());
        if (this.httpContent == null && (this.requestMethod.equals("POST") || this.requestMethod.equals("PUT") || this.requestMethod.equals(HttpMethods.PATCH))) {
            httpRequest.setContent(new EmptyContent());
        }
        httpRequest.getHeaders().putAll(this.requestHeaders);
        if (!this.disableGZipContent) {
            httpRequest.setEncoding(new GZipEncoding());
        }
        final HttpResponseInterceptor responseInterceptor = httpRequest.getResponseInterceptor();
        httpRequest.setResponseInterceptor(new HttpResponseInterceptor() { // from class: com.google.api.client.googleapis.services.AbstractGoogleClientRequest.1
            @Override // com.google.api.client.http.HttpResponseInterceptor
            public final void interceptResponse(HttpResponse response) throws IOException {
                if (responseInterceptor != null) {
                    responseInterceptor.interceptResponse(response);
                }
                if (!response.isSuccessStatusCode() && httpRequest.getThrowExceptionOnExecuteError()) {
                    throw AbstractGoogleClientRequest.this.newExceptionOnError(response);
                }
            }
        });
        return httpRequest;
    }

    public HttpResponse executeUnparsed() throws IOException {
        return executeUnparsed(false);
    }

    protected HttpResponse executeMedia() throws IOException {
        set("alt", (Object) ShareConstants.WEB_DIALOG_PARAM_MEDIA);
        return executeUnparsed();
    }

    public HttpResponse executeUsingHead() throws IOException {
        Preconditions.checkArgument(this.uploader == null);
        HttpResponse response = executeUnparsed(true);
        response.ignore();
        return response;
    }

    private HttpResponse executeUnparsed(boolean usingHead) throws IOException {
        HttpResponse response;
        if (this.uploader == null) {
            response = buildHttpRequest(usingHead).execute();
        } else {
            GenericUrl httpRequestUrl = buildHttpRequestUrl();
            boolean throwExceptionOnExecuteError = getAbstractGoogleClient().getRequestFactory().buildRequest(this.requestMethod, httpRequestUrl, this.httpContent).getThrowExceptionOnExecuteError();
            MediaHttpUploader mediaHttpUploader = this.uploader;
            mediaHttpUploader.initiationHeaders = this.requestHeaders;
            mediaHttpUploader.disableGZipContent = this.disableGZipContent;
            Preconditions.checkArgument(mediaHttpUploader.uploadState == MediaHttpUploader.UploadState.NOT_STARTED);
            if (mediaHttpUploader.directUploadEnabled) {
                response = mediaHttpUploader.directUpload(httpRequestUrl);
            } else {
                response = mediaHttpUploader.resumableUpload(httpRequestUrl);
            }
            response.getRequest().setParser(getAbstractGoogleClient().getObjectParser());
            if (throwExceptionOnExecuteError && !response.isSuccessStatusCode()) {
                throw newExceptionOnError(response);
            }
        }
        this.lastResponseHeaders = response.getHeaders();
        this.lastStatusCode = response.getStatusCode();
        this.lastStatusMessage = response.getStatusMessage();
        return response;
    }

    public IOException newExceptionOnError(HttpResponse response) {
        return new HttpResponseException(response);
    }

    public T execute() throws IOException {
        return (T) executeUnparsed().parseAs((Class) this.responseClass);
    }

    public InputStream executeAsInputStream() throws IOException {
        return executeUnparsed().getContent();
    }

    protected InputStream executeMediaAsInputStream() throws IOException {
        return executeMedia().getContent();
    }

    public void executeAndDownloadTo(OutputStream outputStream) throws IOException {
        executeUnparsed().download(outputStream);
    }

    protected void executeMediaAndDownloadTo(OutputStream outputStream) throws IOException {
        long parseLong;
        if (this.downloader == null) {
            executeMedia().download(outputStream);
            return;
        }
        MediaHttpDownloader mediaHttpDownloader = this.downloader;
        GenericUrl buildHttpRequestUrl = buildHttpRequestUrl();
        HttpHeaders httpHeaders = this.requestHeaders;
        Preconditions.checkArgument(mediaHttpDownloader.downloadState == MediaHttpDownloader.DownloadState.NOT_STARTED);
        buildHttpRequestUrl.put("alt", (Object) ShareConstants.WEB_DIALOG_PARAM_MEDIA);
        if (mediaHttpDownloader.directDownloadEnabled) {
            mediaHttpDownloader.downloadState = MediaHttpDownloader.DownloadState.MEDIA_IN_PROGRESS;
            mediaHttpDownloader.mediaContentLength = mediaHttpDownloader.executeCurrentRequest(mediaHttpDownloader.lastBytePos, buildHttpRequestUrl, httpHeaders, outputStream).getHeaders().getContentLength().longValue();
            mediaHttpDownloader.bytesDownloaded = mediaHttpDownloader.mediaContentLength;
            mediaHttpDownloader.downloadState = MediaHttpDownloader.DownloadState.MEDIA_COMPLETE;
            return;
        }
        while (true) {
            long j = (mediaHttpDownloader.bytesDownloaded + mediaHttpDownloader.chunkSize) - 1;
            if (mediaHttpDownloader.lastBytePos != -1) {
                j = Math.min(mediaHttpDownloader.lastBytePos, j);
            }
            String contentRange = mediaHttpDownloader.executeCurrentRequest(j, buildHttpRequestUrl, httpHeaders, outputStream).getHeaders().getContentRange();
            if (contentRange == null) {
                parseLong = 0;
            } else {
                parseLong = Long.parseLong(contentRange.substring(contentRange.indexOf(45) + 1, contentRange.indexOf(47))) + 1;
            }
            if (contentRange != null && mediaHttpDownloader.mediaContentLength == 0) {
                mediaHttpDownloader.mediaContentLength = Long.parseLong(contentRange.substring(contentRange.indexOf(47) + 1));
            }
            if (mediaHttpDownloader.mediaContentLength <= parseLong) {
                mediaHttpDownloader.bytesDownloaded = mediaHttpDownloader.mediaContentLength;
                mediaHttpDownloader.downloadState = MediaHttpDownloader.DownloadState.MEDIA_COMPLETE;
                return;
            } else {
                mediaHttpDownloader.bytesDownloaded = parseLong;
                mediaHttpDownloader.downloadState = MediaHttpDownloader.DownloadState.MEDIA_IN_PROGRESS;
            }
        }
    }

    public final <E> void queue(BatchRequest batchRequest, Class<E> errorClass, BatchCallback<T, E> callback) throws IOException {
        com.google.api.client.util.Preconditions.checkArgument(this.uploader == null, "Batching media requests is not supported");
        HttpRequest buildHttpRequest = buildHttpRequest();
        Class<T> responseClass = getResponseClass();
        Preconditions.checkNotNull(buildHttpRequest);
        Preconditions.checkNotNull(callback);
        Preconditions.checkNotNull(responseClass);
        Preconditions.checkNotNull(errorClass);
        batchRequest.requestInfos.add(new BatchRequest.RequestInfo<>(callback, responseClass, errorClass, buildHttpRequest));
    }

    @Override // com.google.api.client.util.GenericData
    public AbstractGoogleClientRequest<T> set(String fieldName, Object value) {
        return (AbstractGoogleClientRequest) super.set(fieldName, value);
    }

    public final void checkRequiredParameter(Object value, String name) {
        com.google.api.client.util.Preconditions.checkArgument(this.abstractGoogleClient.getSuppressRequiredParameterChecks() || value != null, "Required parameter %s must be specified", name);
    }
}
