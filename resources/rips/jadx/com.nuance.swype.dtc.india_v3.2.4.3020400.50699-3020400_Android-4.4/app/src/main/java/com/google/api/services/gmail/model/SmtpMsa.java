package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class SmtpMsa extends GenericJson {

    @Key
    private String host;

    @Key
    private String password;

    @Key
    private Integer port;

    @Key
    private String securityMode;

    @Key
    private String username;

    public final String getHost() {
        return this.host;
    }

    public final SmtpMsa setHost(String str) {
        this.host = str;
        return this;
    }

    public final String getPassword() {
        return this.password;
    }

    public final SmtpMsa setPassword(String str) {
        this.password = str;
        return this;
    }

    public final Integer getPort() {
        return this.port;
    }

    public final SmtpMsa setPort(Integer num) {
        this.port = num;
        return this;
    }

    public final String getSecurityMode() {
        return this.securityMode;
    }

    public final SmtpMsa setSecurityMode(String str) {
        this.securityMode = str;
        return this;
    }

    public final String getUsername() {
        return this.username;
    }

    public final SmtpMsa setUsername(String str) {
        this.username = str;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final SmtpMsa set(String str, Object obj) {
        return (SmtpMsa) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final SmtpMsa clone() {
        return (SmtpMsa) super.clone();
    }
}
