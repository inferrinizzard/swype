package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class HistoryMessageDeleted extends GenericJson {

    @Key
    private Message message;

    public final Message getMessage() {
        return this.message;
    }

    public final HistoryMessageDeleted setMessage(Message message) {
        this.message = message;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final HistoryMessageDeleted set(String str, Object obj) {
        return (HistoryMessageDeleted) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final HistoryMessageDeleted clone() {
        return (HistoryMessageDeleted) super.clone();
    }
}
