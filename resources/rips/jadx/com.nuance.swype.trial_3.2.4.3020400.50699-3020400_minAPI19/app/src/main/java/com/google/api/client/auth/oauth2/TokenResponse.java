package com.google.api.client.auth.oauth2;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public class TokenResponse extends GenericJson {

    @Key("access_token")
    String accessToken;

    @Key("expires_in")
    Long expiresInSeconds;

    @Key("refresh_token")
    String refreshToken;

    /* JADX INFO: Access modifiers changed from: private */
    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public TokenResponse set(String fieldName, Object value) {
        return (TokenResponse) super.set(fieldName, value);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public /* bridge */ /* synthetic */ GenericJson clone() {
        return (TokenResponse) super.clone();
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public /* bridge */ /* synthetic */ GenericData clone() {
        return (TokenResponse) super.clone();
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return (TokenResponse) super.clone();
    }
}
