package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import java.math.BigInteger;
import java.util.List;

/* loaded from: classes.dex */
public final class History extends GenericJson {

    @JsonString
    @Key
    private BigInteger id;

    @Key
    private List<HistoryLabelAdded> labelsAdded;

    @Key
    private List<HistoryLabelRemoved> labelsRemoved;

    @Key
    private List<Message> messages;

    @Key
    private List<HistoryMessageAdded> messagesAdded;

    @Key
    private List<HistoryMessageDeleted> messagesDeleted;

    public final BigInteger getId() {
        return this.id;
    }

    public final History setId(BigInteger bigInteger) {
        this.id = bigInteger;
        return this;
    }

    public final List<HistoryLabelAdded> getLabelsAdded() {
        return this.labelsAdded;
    }

    public final History setLabelsAdded(List<HistoryLabelAdded> list) {
        this.labelsAdded = list;
        return this;
    }

    public final List<HistoryLabelRemoved> getLabelsRemoved() {
        return this.labelsRemoved;
    }

    public final History setLabelsRemoved(List<HistoryLabelRemoved> list) {
        this.labelsRemoved = list;
        return this;
    }

    public final List<Message> getMessages() {
        return this.messages;
    }

    public final History setMessages(List<Message> list) {
        this.messages = list;
        return this;
    }

    public final List<HistoryMessageAdded> getMessagesAdded() {
        return this.messagesAdded;
    }

    public final History setMessagesAdded(List<HistoryMessageAdded> list) {
        this.messagesAdded = list;
        return this;
    }

    public final List<HistoryMessageDeleted> getMessagesDeleted() {
        return this.messagesDeleted;
    }

    public final History setMessagesDeleted(List<HistoryMessageDeleted> list) {
        this.messagesDeleted = list;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final History set(String str, Object obj) {
        return (History) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final History clone() {
        return (History) super.clone();
    }
}
