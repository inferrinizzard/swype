package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

/* loaded from: classes.dex */
public final class ModifyThreadRequest extends GenericJson {

    @Key
    private List<String> addLabelIds;

    @Key
    private List<String> removeLabelIds;

    public final List<String> getAddLabelIds() {
        return this.addLabelIds;
    }

    public final ModifyThreadRequest setAddLabelIds(List<String> list) {
        this.addLabelIds = list;
        return this;
    }

    public final List<String> getRemoveLabelIds() {
        return this.removeLabelIds;
    }

    public final ModifyThreadRequest setRemoveLabelIds(List<String> list) {
        this.removeLabelIds = list;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final ModifyThreadRequest set(String str, Object obj) {
        return (ModifyThreadRequest) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final ModifyThreadRequest clone() {
        return (ModifyThreadRequest) super.clone();
    }
}
