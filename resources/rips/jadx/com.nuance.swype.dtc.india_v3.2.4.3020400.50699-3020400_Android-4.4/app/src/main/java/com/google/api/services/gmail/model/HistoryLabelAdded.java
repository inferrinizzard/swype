package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

/* loaded from: classes.dex */
public final class HistoryLabelAdded extends GenericJson {

    @Key
    private List<String> labelIds;

    @Key
    private Message message;

    public final List<String> getLabelIds() {
        return this.labelIds;
    }

    public final HistoryLabelAdded setLabelIds(List<String> list) {
        this.labelIds = list;
        return this;
    }

    public final Message getMessage() {
        return this.message;
    }

    public final HistoryLabelAdded setMessage(Message message) {
        this.message = message;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final HistoryLabelAdded set(String str, Object obj) {
        return (HistoryLabelAdded) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final HistoryLabelAdded clone() {
        return (HistoryLabelAdded) super.clone();
    }
}
