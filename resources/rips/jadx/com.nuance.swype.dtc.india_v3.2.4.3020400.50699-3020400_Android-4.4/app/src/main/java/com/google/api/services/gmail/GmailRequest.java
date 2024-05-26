package com.google.api.services.gmail;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public abstract class GmailRequest<T> extends AbstractGoogleJsonClientRequest<T> {

    @Key
    private String alt;

    @Key
    private String fields;

    @Key
    private String key;

    @Key("oauth_token")
    private String oauthToken;

    @Key
    private Boolean prettyPrint;

    @Key
    private String quotaUser;

    @Key
    private String userIp;

    public GmailRequest(Gmail gmail, String str, String str2, Object obj, Class<T> cls) {
        super(gmail, str, str2, obj, cls);
    }

    public String getAlt() {
        return this.alt;
    }

    /* renamed from: setAlt */
    public GmailRequest<T> setAlt2(String str) {
        this.alt = str;
        return this;
    }

    public String getFields() {
        return this.fields;
    }

    /* renamed from: setFields */
    public GmailRequest<T> setFields2(String str) {
        this.fields = str;
        return this;
    }

    public String getKey() {
        return this.key;
    }

    /* renamed from: setKey */
    public GmailRequest<T> setKey2(String str) {
        this.key = str;
        return this;
    }

    public String getOauthToken() {
        return this.oauthToken;
    }

    /* renamed from: setOauthToken */
    public GmailRequest<T> setOauthToken2(String str) {
        this.oauthToken = str;
        return this;
    }

    public Boolean getPrettyPrint() {
        return this.prettyPrint;
    }

    /* renamed from: setPrettyPrint */
    public GmailRequest<T> setPrettyPrint2(Boolean bool) {
        this.prettyPrint = bool;
        return this;
    }

    public String getQuotaUser() {
        return this.quotaUser;
    }

    /* renamed from: setQuotaUser */
    public GmailRequest<T> setQuotaUser2(String str) {
        this.quotaUser = str;
        return this;
    }

    public String getUserIp() {
        return this.userIp;
    }

    /* renamed from: setUserIp */
    public GmailRequest<T> setUserIp2(String str) {
        this.userIp = str;
        return this;
    }

    @Override // com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest
    public final Gmail getAbstractGoogleClient() {
        return (Gmail) super.getAbstractGoogleClient();
    }

    @Override // com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest
    public GmailRequest<T> setDisableGZipContent(boolean z) {
        return (GmailRequest) super.setDisableGZipContent(z);
    }

    @Override // com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest
    public GmailRequest<T> setRequestHeaders(HttpHeaders httpHeaders) {
        return (GmailRequest) super.setRequestHeaders(httpHeaders);
    }

    @Override // com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
    public GmailRequest<T> set(String str, Object obj) {
        return (GmailRequest) super.set(str, obj);
    }
}
