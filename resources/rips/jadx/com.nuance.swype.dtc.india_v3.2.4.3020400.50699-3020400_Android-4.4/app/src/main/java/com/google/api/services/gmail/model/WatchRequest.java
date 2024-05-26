package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

/* loaded from: classes.dex */
public final class WatchRequest extends GenericJson {

    @Key
    private String labelFilterAction;

    @Key
    private List<String> labelIds;

    @Key
    private String topicName;

    public final String getLabelFilterAction() {
        return this.labelFilterAction;
    }

    public final WatchRequest setLabelFilterAction(String str) {
        this.labelFilterAction = str;
        return this;
    }

    public final List<String> getLabelIds() {
        return this.labelIds;
    }

    public final WatchRequest setLabelIds(List<String> list) {
        this.labelIds = list;
        return this;
    }

    public final String getTopicName() {
        return this.topicName;
    }

    public final WatchRequest setTopicName(String str) {
        this.topicName = str;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final WatchRequest set(String str, Object obj) {
        return (WatchRequest) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final WatchRequest clone() {
        return (WatchRequest) super.clone();
    }
}
