package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Data;
import com.google.api.client.util.Key;
import java.math.BigInteger;
import java.util.List;

/* loaded from: classes.dex */
public final class ListHistoryResponse extends GenericJson {

    @Key
    private List<History> history;

    @JsonString
    @Key
    private BigInteger historyId;

    @Key
    private String nextPageToken;

    static {
        Data.nullOf(History.class);
    }

    public final List<History> getHistory() {
        return this.history;
    }

    public final ListHistoryResponse setHistory(List<History> list) {
        this.history = list;
        return this;
    }

    public final BigInteger getHistoryId() {
        return this.historyId;
    }

    public final ListHistoryResponse setHistoryId(BigInteger bigInteger) {
        this.historyId = bigInteger;
        return this;
    }

    public final String getNextPageToken() {
        return this.nextPageToken;
    }

    public final ListHistoryResponse setNextPageToken(String str) {
        this.nextPageToken = str;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final ListHistoryResponse set(String str, Object obj) {
        return (ListHistoryResponse) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final ListHistoryResponse clone() {
        return (ListHistoryResponse) super.clone();
    }
}
