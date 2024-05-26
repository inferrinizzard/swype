package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class Draft extends GenericJson {

    @Key
    private String id;

    @Key
    private Message message;

    public final String getId() {
        return this.id;
    }

    public final Draft setId(String str) {
        this.id = str;
        return this;
    }

    public final Message getMessage() {
        return this.message;
    }

    public final Draft setMessage(Message message) {
        this.message = message;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final Draft set(String str, Object obj) {
        return (Draft) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final Draft clone() {
        return (Draft) super.clone();
    }
}
