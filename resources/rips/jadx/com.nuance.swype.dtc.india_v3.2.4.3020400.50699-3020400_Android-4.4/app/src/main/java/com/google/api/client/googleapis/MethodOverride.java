package com.google.api.client.googleapis;

import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;

/* loaded from: classes.dex */
public final class MethodOverride implements HttpExecuteInterceptor, HttpRequestInitializer {
    private final boolean overrideAllMethods;

    public MethodOverride() {
        this((byte) 0);
    }

    private MethodOverride(byte b) {
        this.overrideAllMethods = false;
    }

    @Override // com.google.api.client.http.HttpRequestInitializer
    public final void initialize(HttpRequest request) {
        request.setInterceptor(this);
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x006a, code lost:            if (r6.getTransport().supportsMethod(r2) == false) goto L8;     */
    /* JADX WARN: Removed duplicated region for block: B:18:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0029  */
    @Override // com.google.api.client.http.HttpExecuteInterceptor
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void intercept(com.google.api.client.http.HttpRequest r6) throws java.io.IOException {
        /*
            r5 = this;
            r1 = 1
            java.lang.String r2 = r6.getRequestMethod()
            java.lang.String r3 = "POST"
            boolean r3 = r2.equals(r3)
            if (r3 != 0) goto L6c
            java.lang.String r3 = "GET"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L5e
            com.google.api.client.http.GenericUrl r3 = r6.getUrl()
            java.lang.String r3 = r3.build()
            int r3 = r3.length()
            r4 = 2048(0x800, float:2.87E-42)
            if (r3 <= r4) goto L62
        L27:
            if (r1 == 0) goto L5d
            java.lang.String r0 = r6.getRequestMethod()
            java.lang.String r1 = "POST"
            r6.setRequestMethod(r1)
            com.google.api.client.http.HttpHeaders r1 = r6.getHeaders()
            java.lang.String r2 = "X-HTTP-Method-Override"
            r1.set(r2, r0)
            java.lang.String r1 = "GET"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L6e
            com.google.api.client.http.UrlEncodedContent r1 = new com.google.api.client.http.UrlEncodedContent
            com.google.api.client.http.GenericUrl r2 = r6.getUrl()
            com.google.api.client.http.GenericUrl r2 = r2.clone()
            r1.<init>(r2)
            r6.setContent(r1)
            com.google.api.client.http.GenericUrl r1 = r6.getUrl()
            r1.clear()
        L5d:
            return
        L5e:
            boolean r3 = r5.overrideAllMethods
            if (r3 != 0) goto L27
        L62:
            com.google.api.client.http.HttpTransport r3 = r6.getTransport()
            boolean r2 = r3.supportsMethod(r2)
            if (r2 == 0) goto L27
        L6c:
            r1 = 0
            goto L27
        L6e:
            com.google.api.client.http.HttpContent r1 = r6.getContent()
            if (r1 != 0) goto L5d
            com.google.api.client.http.EmptyContent r1 = new com.google.api.client.http.EmptyContent
            r1.<init>()
            r6.setContent(r1)
            goto L5d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.api.client.googleapis.MethodOverride.intercept(com.google.api.client.http.HttpRequest):void");
    }
}
