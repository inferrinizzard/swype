package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class ForwardingAddress extends GenericJson {

    @Key
    private String forwardingEmail;

    @Key
    private String verificationStatus;

    public final String getForwardingEmail() {
        return this.forwardingEmail;
    }

    public final ForwardingAddress setForwardingEmail(String str) {
        this.forwardingEmail = str;
        return this;
    }

    public final String getVerificationStatus() {
        return this.verificationStatus;
    }

    public final ForwardingAddress setVerificationStatus(String str) {
        this.verificationStatus = str;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final ForwardingAddress set(String str, Object obj) {
        return (ForwardingAddress) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final ForwardingAddress clone() {
        return (ForwardingAddress) super.clone();
    }
}
