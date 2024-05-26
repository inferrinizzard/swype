package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class Filter extends GenericJson {

    @Key
    private FilterAction action;

    @Key
    private FilterCriteria criteria;

    @Key
    private String id;

    public final FilterAction getAction() {
        return this.action;
    }

    public final Filter setAction(FilterAction filterAction) {
        this.action = filterAction;
        return this;
    }

    public final FilterCriteria getCriteria() {
        return this.criteria;
    }

    public final Filter setCriteria(FilterCriteria filterCriteria) {
        this.criteria = filterCriteria;
        return this;
    }

    public final String getId() {
        return this.id;
    }

    public final Filter setId(String str) {
        this.id = str;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final Filter set(String str, Object obj) {
        return (Filter) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final Filter clone() {
        return (Filter) super.clone();
    }
}
