package com.google.api.client.http;

import java.io.IOException;

/* loaded from: classes.dex */
public final class HttpRequestFactory {
    private final HttpRequestInitializer initializer;
    private final HttpTransport transport;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpRequestFactory(HttpTransport transport, HttpRequestInitializer initializer) {
        this.transport = transport;
        this.initializer = initializer;
    }

    public final HttpTransport getTransport() {
        return this.transport;
    }

    public final HttpRequestInitializer getInitializer() {
        return this.initializer;
    }

    public final HttpRequest buildRequest(String requestMethod, GenericUrl url, HttpContent content) throws IOException {
        HttpRequest request = this.transport.buildRequest();
        if (this.initializer != null) {
            this.initializer.initialize(request);
        }
        request.setRequestMethod(requestMethod);
        if (url != null) {
            request.setUrl(url);
        }
        if (content != null) {
            request.setContent(content);
        }
        return request;
    }

    public final HttpRequest buildDeleteRequest(GenericUrl url) throws IOException {
        return buildRequest("DELETE", url, null);
    }

    public final HttpRequest buildGetRequest(GenericUrl url) throws IOException {
        return buildRequest("GET", url, null);
    }

    public final HttpRequest buildPostRequest(GenericUrl url, HttpContent content) throws IOException {
        return buildRequest("POST", url, content);
    }

    public final HttpRequest buildPutRequest(GenericUrl url, HttpContent content) throws IOException {
        return buildRequest("PUT", url, content);
    }

    public final HttpRequest buildPatchRequest(GenericUrl url, HttpContent content) throws IOException {
        return buildRequest(HttpMethods.PATCH, url, content);
    }

    public final HttpRequest buildHeadRequest(GenericUrl url) throws IOException {
        return buildRequest(HttpMethods.HEAD, url, null);
    }
}
