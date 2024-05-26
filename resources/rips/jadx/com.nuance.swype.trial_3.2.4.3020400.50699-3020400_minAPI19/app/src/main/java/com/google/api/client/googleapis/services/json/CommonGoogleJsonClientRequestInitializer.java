package com.google.api.client.googleapis.services.json;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer;
import java.io.IOException;

/* loaded from: classes.dex */
public class CommonGoogleJsonClientRequestInitializer extends CommonGoogleClientRequestInitializer {
    public CommonGoogleJsonClientRequestInitializer() {
    }

    public CommonGoogleJsonClientRequestInitializer(String key) {
        super(key);
    }

    public CommonGoogleJsonClientRequestInitializer(String key, String userIp) {
        super(key, userIp);
    }

    @Override // com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer, com.google.api.client.googleapis.services.GoogleClientRequestInitializer
    public final void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
        super.initialize(request);
        initializeJsonRequest((AbstractGoogleJsonClientRequest) request);
    }

    public void initializeJsonRequest(AbstractGoogleJsonClientRequest<?> request) throws IOException {
    }
}
