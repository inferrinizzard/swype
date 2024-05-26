package com.google.api.client.auth.oauth2;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.Clock;
import com.google.api.client.util.Objects;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: classes.dex */
public class Credential implements HttpExecuteInterceptor, HttpRequestInitializer, HttpUnsuccessfulResponseHandler {
    static final Logger LOGGER = Logger.getLogger(Credential.class.getName());
    private String accessToken;
    public final HttpExecuteInterceptor clientAuthentication;
    public final Clock clock;
    private Long expirationTimeMilliseconds;
    public final JsonFactory jsonFactory;
    private final Lock lock = new ReentrantLock();
    private final AccessMethod method;
    private final Collection<Object> refreshListeners;
    private String refreshToken;
    private final HttpRequestInitializer requestInitializer;
    public final String tokenServerEncodedUrl;
    public final HttpTransport transport;

    /* loaded from: classes.dex */
    public interface AccessMethod {
        String getAccessTokenFromRequest(HttpRequest httpRequest);

        void intercept(HttpRequest httpRequest, String str) throws IOException;
    }

    public Credential(Builder builder) {
        this.method = (AccessMethod) Preconditions.checkNotNull(builder.method);
        this.transport = builder.transport;
        this.jsonFactory = builder.jsonFactory;
        this.tokenServerEncodedUrl = builder.tokenServerUrl == null ? null : builder.tokenServerUrl.build();
        this.clientAuthentication = builder.clientAuthentication;
        this.requestInitializer = builder.requestInitializer;
        this.refreshListeners = Collections.unmodifiableCollection(builder.refreshListeners);
        this.clock = (Clock) Preconditions.checkNotNull(builder.clock);
    }

    @Override // com.google.api.client.http.HttpExecuteInterceptor
    public void intercept(HttpRequest request) throws IOException {
        this.lock.lock();
        try {
            Long expiresIn = getExpiresInSeconds();
            if (this.accessToken == null || (expiresIn != null && expiresIn.longValue() <= 60)) {
                refreshToken();
                if (this.accessToken == null) {
                    return;
                }
            }
            this.method.intercept(request, this.accessToken);
        } finally {
            this.lock.unlock();
        }
    }

    @Override // com.google.api.client.http.HttpUnsuccessfulResponseHandler
    public boolean handleResponse(HttpRequest request, HttpResponse response, boolean supportsRetry) {
        boolean z = true;
        boolean refreshToken = false;
        boolean bearer = false;
        List<String> authenticateList = response.getHeaders().getAuthenticateAsList();
        if (authenticateList != null) {
            Iterator i$ = authenticateList.iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                String authenticate = i$.next();
                if (authenticate.startsWith("Bearer ")) {
                    bearer = true;
                    refreshToken = BearerToken.INVALID_TOKEN_ERROR.matcher(authenticate).find();
                    break;
                }
            }
        }
        if (!bearer) {
            refreshToken = response.getStatusCode() == 401;
        }
        if (refreshToken) {
            try {
                this.lock.lock();
                try {
                    if (Objects.equal(this.accessToken, this.method.getAccessTokenFromRequest(request))) {
                        if (!refreshToken()) {
                            z = false;
                        }
                    }
                    return z;
                } finally {
                    this.lock.unlock();
                }
            } catch (IOException exception) {
                LOGGER.log(Level.SEVERE, "unable to refresh token", (Throwable) exception);
            }
        }
        return false;
    }

    @Override // com.google.api.client.http.HttpRequestInitializer
    public void initialize(HttpRequest request) throws IOException {
        request.setInterceptor(this);
        request.setUnsuccessfulResponseHandler(this);
    }

    public Credential setAccessToken(String accessToken) {
        this.lock.lock();
        try {
            this.accessToken = accessToken;
            return this;
        } finally {
            this.lock.unlock();
        }
    }

    public Credential setRefreshToken(String refreshToken) {
        this.lock.lock();
        if (refreshToken != null) {
            try {
                com.google.api.client.util.Preconditions.checkArgument((this.jsonFactory == null || this.transport == null || this.clientAuthentication == null || this.tokenServerEncodedUrl == null) ? false : true, "Please use the Builder and call setJsonFactory, setTransport, setClientAuthentication and setTokenServerUrl/setTokenServerEncodedUrl");
            } finally {
                this.lock.unlock();
            }
        }
        this.refreshToken = refreshToken;
        return this;
    }

    public Credential setExpirationTimeMilliseconds(Long expirationTimeMilliseconds) {
        this.lock.lock();
        try {
            this.expirationTimeMilliseconds = expirationTimeMilliseconds;
            return this;
        } finally {
            this.lock.unlock();
        }
    }

    private Long getExpiresInSeconds() {
        this.lock.lock();
        try {
            if (this.expirationTimeMilliseconds != null) {
                return Long.valueOf((this.expirationTimeMilliseconds.longValue() - this.clock.currentTimeMillis()) / 1000);
            }
            this.lock.unlock();
            return null;
        } finally {
            this.lock.unlock();
        }
    }

    public Credential setExpiresInSeconds(Long expiresIn) {
        return setExpirationTimeMilliseconds(expiresIn == null ? null : Long.valueOf(this.clock.currentTimeMillis() + (expiresIn.longValue() * 1000)));
    }

    private boolean refreshToken() throws IOException {
        this.lock.lock();
        try {
            try {
                TokenResponse tokenResponse = executeRefreshToken();
                if (tokenResponse != null) {
                    setFromTokenResponse(tokenResponse);
                    Iterator i$ = this.refreshListeners.iterator();
                    while (i$.hasNext()) {
                        i$.next();
                    }
                    return true;
                }
            } catch (TokenResponseException e) {
                boolean statusCode4xx = 400 <= e.getStatusCode() && e.getStatusCode() < 500;
                if (e.details != null && statusCode4xx) {
                    setAccessToken(null);
                    setExpiresInSeconds(null);
                }
                Iterator i$2 = this.refreshListeners.iterator();
                while (i$2.hasNext()) {
                    i$2.next();
                }
                if (statusCode4xx) {
                    throw e;
                }
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    public TokenResponse executeRefreshToken() throws IOException {
        if (this.refreshToken == null) {
            return null;
        }
        return new RefreshTokenRequest(this.transport, this.jsonFactory, new GenericUrl(this.tokenServerEncodedUrl), this.refreshToken).setClientAuthentication(this.clientAuthentication).setRequestInitializer(this.requestInitializer).execute();
    }

    /* loaded from: classes.dex */
    public static class Builder {
        HttpExecuteInterceptor clientAuthentication;
        JsonFactory jsonFactory;
        final AccessMethod method;
        HttpRequestInitializer requestInitializer;
        GenericUrl tokenServerUrl;
        HttpTransport transport;
        Clock clock = Clock.SYSTEM;
        Collection<Object> refreshListeners = new ArrayList();

        public Builder(AccessMethod method) {
            this.method = (AccessMethod) Preconditions.checkNotNull(method);
        }

        /* renamed from: setTokenServerEncodedUrl */
        public Builder mo30setTokenServerEncodedUrl(String tokenServerEncodedUrl) {
            this.tokenServerUrl = tokenServerEncodedUrl == null ? null : new GenericUrl(tokenServerEncodedUrl);
            return this;
        }
    }

    public Credential setFromTokenResponse(TokenResponse tokenResponse) {
        setAccessToken(tokenResponse.accessToken);
        if (tokenResponse.refreshToken != null) {
            setRefreshToken(tokenResponse.refreshToken);
        }
        setExpiresInSeconds(tokenResponse.expiresInSeconds);
        return this;
    }
}
