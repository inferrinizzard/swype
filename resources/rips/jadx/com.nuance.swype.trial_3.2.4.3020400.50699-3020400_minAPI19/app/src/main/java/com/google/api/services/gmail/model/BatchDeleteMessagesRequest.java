package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

/* loaded from: classes.dex */
public final class BatchDeleteMessagesRequest extends GenericJson {

    @Key
    private List<String> ids;

    public final List<String> getIds() {
        return this.ids;
    }

    public final BatchDeleteMessagesRequest setIds(List<String> list) {
        this.ids = list;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final BatchDeleteMessagesRequest set(String str, Object obj) {
        return (BatchDeleteMessagesRequest) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final BatchDeleteMessagesRequest clone() {
        return (BatchDeleteMessagesRequest) super.clone();
    }
}
