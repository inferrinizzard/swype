package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Data;
import com.google.api.client.util.Key;
import java.util.List;

/* loaded from: classes.dex */
public final class ListFiltersResponse extends GenericJson {

    @Key
    private List<Filter> filter;

    static {
        Data.nullOf(Filter.class);
    }

    public final List<Filter> getFilter() {
        return this.filter;
    }

    public final ListFiltersResponse setFilter(List<Filter> list) {
        this.filter = list;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final ListFiltersResponse set(String str, Object obj) {
        return (ListFiltersResponse) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final ListFiltersResponse clone() {
        return (ListFiltersResponse) super.clone();
    }
}
