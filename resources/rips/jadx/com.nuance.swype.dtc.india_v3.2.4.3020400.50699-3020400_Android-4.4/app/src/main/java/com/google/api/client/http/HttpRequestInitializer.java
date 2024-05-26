package com.google.api.client.http;

import java.io.IOException;

/* loaded from: classes.dex */
public interface HttpRequestInitializer {
    void initialize(HttpRequest httpRequest) throws IOException;
}
