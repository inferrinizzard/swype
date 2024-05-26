package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

/* loaded from: classes.dex */
public final class ListThreadsResponse extends GenericJson {

    @Key
    private String nextPageToken;

    @Key
    private Long resultSizeEstimate;

    @Key
    private List<Thread> threads;

    public final String getNextPageToken() {
        return this.nextPageToken;
    }

    public final ListThreadsResponse setNextPageToken(String str) {
        this.nextPageToken = str;
        return this;
    }

    public final Long getResultSizeEstimate() {
        return this.resultSizeEstimate;
    }

    public final ListThreadsResponse setResultSizeEstimate(Long l) {
        this.resultSizeEstimate = l;
        return this;
    }

    public final List<Thread> getThreads() {
        return this.threads;
    }

    public final ListThreadsResponse setThreads(List<Thread> list) {
        this.threads = list;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final ListThreadsResponse set(String str, Object obj) {
        return (ListThreadsResponse) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final ListThreadsResponse clone() {
        return (ListThreadsResponse) super.clone();
    }
}
