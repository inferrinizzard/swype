package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonString;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.Key;
import java.math.BigInteger;
import java.util.List;

/* loaded from: classes.dex */
public final class Message extends GenericJson {

    @JsonString
    @Key
    private BigInteger historyId;

    @Key
    private String id;

    @JsonString
    @Key
    private Long internalDate;

    @Key
    private List<String> labelIds;

    @Key
    private MessagePart payload;

    @Key
    private String raw;

    @Key
    private Integer sizeEstimate;

    @Key
    private String snippet;

    @Key
    private String threadId;

    public final BigInteger getHistoryId() {
        return this.historyId;
    }

    public final Message setHistoryId(BigInteger bigInteger) {
        this.historyId = bigInteger;
        return this;
    }

    public final String getId() {
        return this.id;
    }

    public final Message setId(String str) {
        this.id = str;
        return this;
    }

    public final Long getInternalDate() {
        return this.internalDate;
    }

    public final Message setInternalDate(Long l) {
        this.internalDate = l;
        return this;
    }

    public final List<String> getLabelIds() {
        return this.labelIds;
    }

    public final Message setLabelIds(List<String> list) {
        this.labelIds = list;
        return this;
    }

    public final MessagePart getPayload() {
        return this.payload;
    }

    public final Message setPayload(MessagePart messagePart) {
        this.payload = messagePart;
        return this;
    }

    public final String getRaw() {
        return this.raw;
    }

    public final byte[] decodeRaw() {
        return Base64.decodeBase64(this.raw);
    }

    public final Message setRaw(String str) {
        this.raw = str;
        return this;
    }

    public final Integer getSizeEstimate() {
        return this.sizeEstimate;
    }

    public final Message setSizeEstimate(Integer num) {
        this.sizeEstimate = num;
        return this;
    }

    public final String getSnippet() {
        return this.snippet;
    }

    public final Message setSnippet(String str) {
        this.snippet = str;
        return this;
    }

    public final String getThreadId() {
        return this.threadId;
    }

    public final Message setThreadId(String str) {
        this.threadId = str;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final Message set(String str, Object obj) {
        return (Message) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final Message clone() {
        return (Message) super.clone();
    }

    public final Message encodeRaw(byte[] bArr) {
        this.raw = Base64.encodeBase64URLSafeString(bArr);
        return this;
    }
}
