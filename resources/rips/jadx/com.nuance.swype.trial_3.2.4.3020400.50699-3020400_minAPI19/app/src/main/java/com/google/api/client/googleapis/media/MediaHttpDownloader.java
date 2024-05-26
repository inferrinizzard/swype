package com.google.api.client.googleapis.media;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.IOUtils;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class MediaHttpDownloader {
    public long bytesDownloaded;
    public long mediaContentLength;
    private final HttpRequestFactory requestFactory;
    private final HttpTransport transport;
    public boolean directDownloadEnabled = false;
    public int chunkSize = 33554432;
    public DownloadState downloadState = DownloadState.NOT_STARTED;
    public long lastBytePos = -1;

    /* loaded from: classes.dex */
    public enum DownloadState {
        NOT_STARTED,
        MEDIA_IN_PROGRESS,
        MEDIA_COMPLETE
    }

    public MediaHttpDownloader(HttpTransport transport, HttpRequestInitializer httpRequestInitializer) {
        this.transport = (HttpTransport) Preconditions.checkNotNull(transport);
        this.requestFactory = httpRequestInitializer == null ? transport.createRequestFactory() : transport.createRequestFactory(httpRequestInitializer);
    }

    public final HttpResponse executeCurrentRequest(long currentRequestLastBytePos, GenericUrl requestUrl, HttpHeaders requestHeaders, OutputStream outputStream) throws IOException {
        HttpRequest request = this.requestFactory.buildGetRequest(requestUrl);
        if (requestHeaders != null) {
            request.getHeaders().putAll(requestHeaders);
        }
        if (this.bytesDownloaded != 0 || currentRequestLastBytePos != -1) {
            StringBuilder rangeHeader = new StringBuilder();
            rangeHeader.append("bytes=").append(this.bytesDownloaded).append(XMLResultsHandler.SEP_HYPHEN);
            if (currentRequestLastBytePos != -1) {
                rangeHeader.append(currentRequestLastBytePos);
            }
            request.getHeaders().setRange(rangeHeader.toString());
        }
        HttpResponse response = request.execute();
        try {
            IOUtils.copy(response.getContent(), outputStream, true);
            return response;
        } finally {
            response.disconnect();
        }
    }
}
