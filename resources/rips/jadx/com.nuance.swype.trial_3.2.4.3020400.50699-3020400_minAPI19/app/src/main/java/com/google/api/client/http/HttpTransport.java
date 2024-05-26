package com.google.api.client.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

/* loaded from: classes.dex */
public abstract class HttpTransport {
    static final Logger LOGGER = Logger.getLogger(HttpTransport.class.getName());
    private static final String[] SUPPORTED_METHODS;

    public abstract LowLevelHttpRequest buildRequest(String str, String str2) throws IOException;

    static {
        String[] strArr = {"DELETE", "GET", "POST", "PUT"};
        SUPPORTED_METHODS = strArr;
        Arrays.sort(strArr);
    }

    public final HttpRequestFactory createRequestFactory() {
        return createRequestFactory(null);
    }

    public final HttpRequestFactory createRequestFactory(HttpRequestInitializer initializer) {
        return new HttpRequestFactory(this, initializer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpRequest buildRequest() {
        return new HttpRequest(this, null);
    }

    public boolean supportsMethod(String method) throws IOException {
        return Arrays.binarySearch(SUPPORTED_METHODS, method) >= 0;
    }

    public void shutdown() throws IOException {
    }
}
