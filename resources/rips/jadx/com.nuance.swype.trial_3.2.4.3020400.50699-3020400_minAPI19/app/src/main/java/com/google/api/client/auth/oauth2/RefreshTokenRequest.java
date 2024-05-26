package com.google.api.client.auth.oauth2;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class RefreshTokenRequest extends TokenRequest {

    @Key("refresh_token")
    private String refreshToken;

    public RefreshTokenRequest(HttpTransport transport, JsonFactory jsonFactory, GenericUrl tokenServerUrl, String refreshToken) {
        super(transport, jsonFactory, tokenServerUrl, "refresh_token");
        this.refreshToken = (String) Preconditions.checkNotNull(refreshToken);
    }

    @Override // com.google.api.client.auth.oauth2.TokenRequest
    public final RefreshTokenRequest setRequestInitializer(HttpRequestInitializer requestInitializer) {
        return (RefreshTokenRequest) super.setRequestInitializer(requestInitializer);
    }

    @Override // com.google.api.client.auth.oauth2.TokenRequest
    public final RefreshTokenRequest setClientAuthentication(HttpExecuteInterceptor clientAuthentication) {
        return (RefreshTokenRequest) super.setClientAuthentication(clientAuthentication);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // com.google.api.client.auth.oauth2.TokenRequest, com.google.api.client.util.GenericData
    public RefreshTokenRequest set(String fieldName, Object value) {
        return (RefreshTokenRequest) super.set(fieldName, value);
    }

    @Override // com.google.api.client.auth.oauth2.TokenRequest
    public final /* bridge */ /* synthetic */ TokenRequest setGrantType(String x0) {
        return (RefreshTokenRequest) super.setGrantType(x0);
    }

    @Override // com.google.api.client.auth.oauth2.TokenRequest
    public final /* bridge */ /* synthetic */ TokenRequest setTokenServerUrl(GenericUrl x0) {
        return (RefreshTokenRequest) super.setTokenServerUrl(x0);
    }
}
