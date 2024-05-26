package com.google.api.client.auth.oauth2;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequest;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class BearerToken {
    static final Pattern INVALID_TOKEN_ERROR = Pattern.compile("\\s*error\\s*=\\s*\"?invalid_token\"?");

    /* loaded from: classes.dex */
    static final class AuthorizationHeaderAccessMethod implements Credential.AccessMethod {
        AuthorizationHeaderAccessMethod() {
        }

        @Override // com.google.api.client.auth.oauth2.Credential.AccessMethod
        public final void intercept(HttpRequest request, String accessToken) throws IOException {
            request.getHeaders().setAuthorization("Bearer " + accessToken);
        }

        @Override // com.google.api.client.auth.oauth2.Credential.AccessMethod
        public final String getAccessTokenFromRequest(HttpRequest request) {
            List<String> authorizationAsList = request.getHeaders().getAuthorizationAsList();
            if (authorizationAsList != null) {
                for (String header : authorizationAsList) {
                    if (header.startsWith("Bearer ")) {
                        return header.substring(7);
                    }
                }
            }
            return null;
        }
    }

    public static Credential.AccessMethod authorizationHeaderAccessMethod() {
        return new AuthorizationHeaderAccessMethod();
    }
}
