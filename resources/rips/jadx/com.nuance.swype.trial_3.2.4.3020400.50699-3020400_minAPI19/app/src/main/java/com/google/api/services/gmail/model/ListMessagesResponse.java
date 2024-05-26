package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

/* loaded from: classes.dex */
public final class ListMessagesResponse extends GenericJson {

    @Key
    private List<Message> messages;

    @Key
    private String nextPageToken;

    @Key
    private Long resultSizeEstimate;

    public final List<Message> getMessages() {
        return this.messages;
    }

    public final ListMessagesResponse setMessages(List<Message> list) {
        this.messages = list;
        return this;
    }

    public final String getNextPageToken() {
        return this.nextPageToken;
    }

    public final ListMessagesResponse setNextPageToken(String str) {
        this.nextPageToken = str;
        return this;
    }

    public final Long getResultSizeEstimate() {
        return this.resultSizeEstimate;
    }

    public final ListMessagesResponse setResultSizeEstimate(Long l) {
        this.resultSizeEstimate = l;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final ListMessagesResponse set(String str, Object obj) {
        return (ListMessagesResponse) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final ListMessagesResponse clone() {
        return (ListMessagesResponse) super.clone();
    }
}
