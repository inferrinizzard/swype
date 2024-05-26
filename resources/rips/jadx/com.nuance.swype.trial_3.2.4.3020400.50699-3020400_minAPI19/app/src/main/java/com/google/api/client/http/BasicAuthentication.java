package com.google.api.client.http;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import java.io.IOException;

/* loaded from: classes.dex */
public final class BasicAuthentication implements HttpExecuteInterceptor, HttpRequestInitializer {
    private final String password;
    private final String username;

    public BasicAuthentication(String username, String password) {
        this.username = (String) Preconditions.checkNotNull(username);
        this.password = (String) Preconditions.checkNotNull(password);
    }

    @Override // com.google.api.client.http.HttpRequestInitializer
    public final void initialize(HttpRequest request) throws IOException {
        request.setInterceptor(this);
    }

    @Override // com.google.api.client.http.HttpExecuteInterceptor
    public final void intercept(HttpRequest request) throws IOException {
        request.getHeaders().setBasicAuthentication(this.username, this.password);
    }

    public final String getUsername() {
        return this.username;
    }

    public final String getPassword() {
        return this.password;
    }
}
