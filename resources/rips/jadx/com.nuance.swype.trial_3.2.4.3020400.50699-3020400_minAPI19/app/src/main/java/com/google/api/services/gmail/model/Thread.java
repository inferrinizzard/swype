package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Data;
import com.google.api.client.util.Key;
import java.math.BigInteger;
import java.util.List;

/* loaded from: classes.dex */
public final class Thread extends GenericJson {

    @JsonString
    @Key
    private BigInteger historyId;

    @Key
    private String id;

    @Key
    private List<Message> messages;

    @Key
    private String snippet;

    static {
        Data.nullOf(Message.class);
    }

    public final BigInteger getHistoryId() {
        return this.historyId;
    }

    public final Thread setHistoryId(BigInteger bigInteger) {
        this.historyId = bigInteger;
        return this;
    }

    public final String getId() {
        return this.id;
    }

    public final Thread setId(String str) {
        this.id = str;
        return this;
    }

    public final List<Message> getMessages() {
        return this.messages;
    }

    public final Thread setMessages(List<Message> list) {
        this.messages = list;
        return this;
    }

    public final String getSnippet() {
        return this.snippet;
    }

    public final Thread setSnippet(String str) {
        this.snippet = str;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final Thread set(String str, Object obj) {
        return (Thread) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final Thread clone() {
        return (Thread) super.clone();
    }
}
