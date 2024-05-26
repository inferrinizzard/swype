package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import java.math.BigInteger;

/* loaded from: classes.dex */
public final class WatchResponse extends GenericJson {

    @JsonString
    @Key
    private Long expiration;

    @JsonString
    @Key
    private BigInteger historyId;

    public final Long getExpiration() {
        return this.expiration;
    }

    public final WatchResponse setExpiration(Long l) {
        this.expiration = l;
        return this;
    }

    public final BigInteger getHistoryId() {
        return this.historyId;
    }

    public final WatchResponse setHistoryId(BigInteger bigInteger) {
        this.historyId = bigInteger;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final WatchResponse set(String str, Object obj) {
        return (WatchResponse) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final WatchResponse clone() {
        return (WatchResponse) super.clone();
    }
}
