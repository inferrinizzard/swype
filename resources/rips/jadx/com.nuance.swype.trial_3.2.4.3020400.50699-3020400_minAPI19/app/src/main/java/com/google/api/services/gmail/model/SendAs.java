package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class SendAs extends GenericJson {

    @Key
    private String displayName;

    @Key
    private Boolean isDefault;

    @Key
    private Boolean isPrimary;

    @Key
    private String replyToAddress;

    @Key
    private String sendAsEmail;

    @Key
    private String signature;

    @Key
    private SmtpMsa smtpMsa;

    @Key
    private Boolean treatAsAlias;

    @Key
    private String verificationStatus;

    public final String getDisplayName() {
        return this.displayName;
    }

    public final SendAs setDisplayName(String str) {
        this.displayName = str;
        return this;
    }

    public final Boolean getIsDefault() {
        return this.isDefault;
    }

    public final SendAs setIsDefault(Boolean bool) {
        this.isDefault = bool;
        return this;
    }

    public final Boolean getIsPrimary() {
        return this.isPrimary;
    }

    public final SendAs setIsPrimary(Boolean bool) {
        this.isPrimary = bool;
        return this;
    }

    public final String getReplyToAddress() {
        return this.replyToAddress;
    }

    public final SendAs setReplyToAddress(String str) {
        this.replyToAddress = str;
        return this;
    }

    public final String getSendAsEmail() {
        return this.sendAsEmail;
    }

    public final SendAs setSendAsEmail(String str) {
        this.sendAsEmail = str;
        return this;
    }

    public final String getSignature() {
        return this.signature;
    }

    public final SendAs setSignature(String str) {
        this.signature = str;
        return this;
    }

    public final SmtpMsa getSmtpMsa() {
        return this.smtpMsa;
    }

    public final SendAs setSmtpMsa(SmtpMsa smtpMsa) {
        this.smtpMsa = smtpMsa;
        return this;
    }

    public final Boolean getTreatAsAlias() {
        return this.treatAsAlias;
    }

    public final SendAs setTreatAsAlias(Boolean bool) {
        this.treatAsAlias = bool;
        return this;
    }

    public final String getVerificationStatus() {
        return this.verificationStatus;
    }

    public final SendAs setVerificationStatus(String str) {
        this.verificationStatus = str;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final SendAs set(String str, Object obj) {
        return (SendAs) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final SendAs clone() {
        return (SendAs) super.clone();
    }
}
