package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class MessagePartBody extends GenericJson {

    @Key
    private String attachmentId;

    @Key
    private String data;

    @Key
    private Integer size;

    public final String getAttachmentId() {
        return this.attachmentId;
    }

    public final MessagePartBody setAttachmentId(String str) {
        this.attachmentId = str;
        return this;
    }

    public final String getData() {
        return this.data;
    }

    public final byte[] decodeData() {
        return Base64.decodeBase64(this.data);
    }

    public final MessagePartBody setData(String str) {
        this.data = str;
        return this;
    }

    public final Integer getSize() {
        return this.size;
    }

    public final MessagePartBody setSize(Integer num) {
        this.size = num;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final MessagePartBody set(String str, Object obj) {
        return (MessagePartBody) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final MessagePartBody clone() {
        return (MessagePartBody) super.clone();
    }

    public final MessagePartBody encodeData(byte[] bArr) {
        this.data = Base64.encodeBase64URLSafeString(bArr);
        return this;
    }
}
