package com.google.api.client.googleapis.auth.oauth2;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.json.webtoken.JsonWebToken;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.Joiner;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Collection;
import java.util.Collections;

/* loaded from: classes.dex */
public class GoogleCredential extends Credential {
    private static DefaultCredentialProvider defaultCredentialProvider = new DefaultCredentialProvider();
    private String serviceAccountId;
    private PrivateKey serviceAccountPrivateKey;
    private String serviceAccountPrivateKeyId;
    private Collection<String> serviceAccountScopes;
    private String serviceAccountUser;

    public GoogleCredential() {
        this(new Builder());
    }

    private GoogleCredential(Builder builder) {
        super(builder);
        if (builder.serviceAccountPrivateKey != null) {
            this.serviceAccountId = (String) Preconditions.checkNotNull(builder.serviceAccountId);
            this.serviceAccountScopes = Collections.unmodifiableCollection(builder.serviceAccountScopes);
            this.serviceAccountPrivateKey = builder.serviceAccountPrivateKey;
            this.serviceAccountPrivateKeyId = builder.serviceAccountPrivateKeyId;
            this.serviceAccountUser = builder.serviceAccountUser;
            return;
        }
        Preconditions.checkArgument(builder.serviceAccountId == null && builder.serviceAccountScopes == null && builder.serviceAccountUser == null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.api.client.auth.oauth2.Credential
    public final TokenResponse executeRefreshToken() throws IOException {
        if (this.serviceAccountPrivateKey == null) {
            return super.executeRefreshToken();
        }
        JsonWebSignature.Header header = new JsonWebSignature.Header();
        header.setAlgorithm("RS256");
        header.setType("JWT");
        header.setKeyId(this.serviceAccountPrivateKeyId);
        JsonWebToken.Payload payload = new JsonWebToken.Payload();
        long currentTime = this.clock.currentTimeMillis();
        payload.setIssuer(this.serviceAccountId);
        payload.setAudience(this.tokenServerEncodedUrl);
        payload.setIssuedAtTimeSeconds(Long.valueOf(currentTime / 1000));
        payload.setExpirationTimeSeconds(Long.valueOf((currentTime / 1000) + 3600));
        payload.setSubject(this.serviceAccountUser);
        Joiner joiner = new Joiner(new com.google.api.client.repackaged.com.google.common.base.Joiner(XMLResultsHandler.SEP_SPACE));
        Collection<String> collection = this.serviceAccountScopes;
        payload.put("scope", (Object) joiner.wrapped.appendTo(new StringBuilder(), collection.iterator()).toString());
        try {
            String assertion = JsonWebSignature.signUsingRsaSha256(this.serviceAccountPrivateKey, this.jsonFactory, header, payload);
            TokenRequest request = new TokenRequest(this.transport, this.jsonFactory, new GenericUrl(this.tokenServerEncodedUrl), "urn:ietf:params:oauth:grant-type:jwt-bearer");
            request.put("assertion", (Object) assertion);
            return request.execute();
        } catch (GeneralSecurityException exception) {
            IOException e = new IOException();
            e.initCause(exception);
            throw e;
        }
    }

    /* loaded from: classes.dex */
    public static class Builder extends Credential.Builder {
        String serviceAccountId;
        PrivateKey serviceAccountPrivateKey;
        String serviceAccountPrivateKeyId;
        Collection<String> serviceAccountScopes;
        String serviceAccountUser;

        public Builder() {
            super(BearerToken.authorizationHeaderAccessMethod());
            mo30setTokenServerEncodedUrl("https://accounts.google.com/o/oauth2/token");
        }

        /* JADX INFO: Access modifiers changed from: private */
        @Override // com.google.api.client.auth.oauth2.Credential.Builder
        /* renamed from: setTokenServerEncodedUrl, reason: merged with bridge method [inline-methods] */
        public Builder mo30setTokenServerEncodedUrl(String tokenServerEncodedUrl) {
            return (Builder) super.mo30setTokenServerEncodedUrl(tokenServerEncodedUrl);
        }
    }

    @Override // com.google.api.client.auth.oauth2.Credential
    public final /* bridge */ /* synthetic */ Credential setFromTokenResponse(TokenResponse x0) {
        return (GoogleCredential) super.setFromTokenResponse(x0);
    }

    @Override // com.google.api.client.auth.oauth2.Credential
    public final /* bridge */ /* synthetic */ Credential setExpiresInSeconds(Long x0) {
        return (GoogleCredential) super.setExpiresInSeconds(x0);
    }

    @Override // com.google.api.client.auth.oauth2.Credential
    public final /* bridge */ /* synthetic */ Credential setExpirationTimeMilliseconds(Long x0) {
        return (GoogleCredential) super.setExpirationTimeMilliseconds(x0);
    }

    @Override // com.google.api.client.auth.oauth2.Credential
    public final /* bridge */ /* synthetic */ Credential setRefreshToken(String x0) {
        if (x0 != null) {
            com.google.api.client.util.Preconditions.checkArgument((this.jsonFactory == null || this.transport == null || this.clientAuthentication == null) ? false : true, "Please use the Builder and call setJsonFactory, setTransport and setClientSecrets");
        }
        return (GoogleCredential) super.setRefreshToken(x0);
    }

    @Override // com.google.api.client.auth.oauth2.Credential
    public final /* bridge */ /* synthetic */ Credential setAccessToken(String x0) {
        return (GoogleCredential) super.setAccessToken(x0);
    }
}
