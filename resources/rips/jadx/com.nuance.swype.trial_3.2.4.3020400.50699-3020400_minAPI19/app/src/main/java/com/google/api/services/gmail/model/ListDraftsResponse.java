package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Data;
import com.google.api.client.util.Key;
import java.util.List;

/* loaded from: classes.dex */
public final class ListDraftsResponse extends GenericJson {

    @Key
    private List<Draft> drafts;

    @Key
    private String nextPageToken;

    @Key
    private Long resultSizeEstimate;

    static {
        Data.nullOf(Draft.class);
    }

    public final List<Draft> getDrafts() {
        return this.drafts;
    }

    public final ListDraftsResponse setDrafts(List<Draft> list) {
        this.drafts = list;
        return this;
    }

    public final String getNextPageToken() {
        return this.nextPageToken;
    }

    public final ListDraftsResponse setNextPageToken(String str) {
        this.nextPageToken = str;
        return this;
    }

    public final Long getResultSizeEstimate() {
        return this.resultSizeEstimate;
    }

    public final ListDraftsResponse setResultSizeEstimate(Long l) {
        this.resultSizeEstimate = l;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final ListDraftsResponse set(String str, Object obj) {
        return (ListDraftsResponse) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final ListDraftsResponse clone() {
        return (ListDraftsResponse) super.clone();
    }
}
