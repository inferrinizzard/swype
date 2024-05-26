package com.google.api.services.gmail;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer;
import java.io.IOException;

/* loaded from: classes.dex */
public class GmailRequestInitializer extends CommonGoogleJsonClientRequestInitializer {
    public GmailRequestInitializer() {
    }

    public GmailRequestInitializer(String str) {
        super(str);
    }

    public GmailRequestInitializer(String str, String str2) {
        super(str, str2);
    }

    @Override // com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer
    public final void initializeJsonRequest(AbstractGoogleJsonClientRequest<?> abstractGoogleJsonClientRequest) throws IOException {
        super.initializeJsonRequest(abstractGoogleJsonClientRequest);
        initializeGmailRequest((GmailRequest) abstractGoogleJsonClientRequest);
    }

    protected void initializeGmailRequest(GmailRequest<?> gmailRequest) throws IOException {
    }
}
