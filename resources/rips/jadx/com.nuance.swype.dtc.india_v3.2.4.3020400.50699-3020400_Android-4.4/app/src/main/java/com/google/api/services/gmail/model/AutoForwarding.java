package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class AutoForwarding extends GenericJson {

    @Key
    private String disposition;

    @Key
    private String emailAddress;

    @Key
    private Boolean enabled;

    public final String getDisposition() {
        return this.disposition;
    }

    public final AutoForwarding setDisposition(String str) {
        this.disposition = str;
        return this;
    }

    public final String getEmailAddress() {
        return this.emailAddress;
    }

    public final AutoForwarding setEmailAddress(String str) {
        this.emailAddress = str;
        return this;
    }

    public final Boolean getEnabled() {
        return this.enabled;
    }

    public final AutoForwarding setEnabled(Boolean bool) {
        this.enabled = bool;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final AutoForwarding set(String str, Object obj) {
        return (AutoForwarding) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final AutoForwarding clone() {
        return (AutoForwarding) super.clone();
    }
}
