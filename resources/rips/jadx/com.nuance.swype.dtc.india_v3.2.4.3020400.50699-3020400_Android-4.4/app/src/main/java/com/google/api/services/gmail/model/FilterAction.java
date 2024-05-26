package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

/* loaded from: classes.dex */
public final class FilterAction extends GenericJson {

    @Key
    private List<String> addLabelIds;

    @Key
    private String forward;

    @Key
    private List<String> removeLabelIds;

    public final List<String> getAddLabelIds() {
        return this.addLabelIds;
    }

    public final FilterAction setAddLabelIds(List<String> list) {
        this.addLabelIds = list;
        return this;
    }

    public final String getForward() {
        return this.forward;
    }

    public final FilterAction setForward(String str) {
        this.forward = str;
        return this;
    }

    public final List<String> getRemoveLabelIds() {
        return this.removeLabelIds;
    }

    public final FilterAction setRemoveLabelIds(List<String> list) {
        this.removeLabelIds = list;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final FilterAction set(String str, Object obj) {
        return (FilterAction) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final FilterAction clone() {
        return (FilterAction) super.clone();
    }
}
