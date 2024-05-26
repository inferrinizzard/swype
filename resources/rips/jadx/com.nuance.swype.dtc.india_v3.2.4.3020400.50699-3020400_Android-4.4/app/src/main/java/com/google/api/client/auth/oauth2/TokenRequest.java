package com.google.api.client.auth.oauth2;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.io.IOException;

/* loaded from: classes.dex */
public class TokenRequest extends GenericData {
    HttpExecuteInterceptor clientAuthentication;

    @Key("grant_type")
    private String grantType;
    private final JsonFactory jsonFactory;
    HttpRequestInitializer requestInitializer;
    private GenericUrl tokenServerUrl;
    private final HttpTransport transport;

    public TokenRequest(HttpTransport transport, JsonFactory jsonFactory, GenericUrl tokenServerUrl, String grantType) {
        this.transport = (HttpTransport) Preconditions.checkNotNull(transport);
        this.jsonFactory = (JsonFactory) Preconditions.checkNotNull(jsonFactory);
        setTokenServerUrl(tokenServerUrl);
        setGrantType(grantType);
    }

    public TokenRequest setRequestInitializer(HttpRequestInitializer requestInitializer) {
        this.requestInitializer = requestInitializer;
        return this;
    }

    public TokenRequest setClientAuthentication(HttpExecuteInterceptor clientAuthentication) {
        this.clientAuthentication = clientAuthentication;
        return this;
    }

    public TokenRequest setTokenServerUrl(GenericUrl tokenServerUrl) {
        this.tokenServerUrl = tokenServerUrl;
        Preconditions.checkArgument(tokenServerUrl.getFragment() == null);
        return this;
    }

    @Override // com.google.api.client.util.GenericData
    public TokenRequest set(String fieldName, Object value) {
        return (TokenRequest) super.set(fieldName, value);
    }

    public TokenRequest setGrantType(String grantType) {
        this.grantType = (String) Preconditions.checkNotNull(grantType);
        return this;
    }

    public final TokenResponse execute() throws IOException {
        HttpRequest buildPostRequest = this.transport.createRequestFactory(new HttpRequestInitializer() { // from class: com.google.api.client.auth.oauth2.TokenRequest.1
            @Override // com.google.api.client.http.HttpRequestInitializer
            public final void initialize(HttpRequest request) throws IOException {
                if (TokenRequest.this.requestInitializer != null) {
                    TokenRequest.this.requestInitializer.initialize(request);
                }
                final HttpExecuteInterceptor interceptor = request.getInterceptor();
                request.setInterceptor(new HttpExecuteInterceptor() { // from class: com.google.api.client.auth.oauth2.TokenRequest.1.1
                    @Override // com.google.api.client.http.HttpExecuteInterceptor
                    public final void intercept(HttpRequest request2) throws IOException {
                        if (interceptor != null) {
                            interceptor.intercept(request2);
                        }
                        if (TokenRequest.this.clientAuthentication != null) {
                            TokenRequest.this.clientAuthentication.intercept(request2);
                        }
                    }
                });
            }
        }).buildPostRequest(this.tokenServerUrl, new UrlEncodedContent(this));
        buildPostRequest.setParser(new JsonObjectParser(this.jsonFactory));
        buildPostRequest.setThrowExceptionOnExecuteError(false);
        HttpResponse execute = buildPostRequest.execute();
        if (execute.isSuccessStatusCode()) {
            return (TokenResponse) execute.parseAs(TokenResponse.class);
        }
        throw TokenResponseException.from(this.jsonFactory, execute);
    }
}
