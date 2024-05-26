package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import java.math.BigInteger;

/* loaded from: classes.dex */
public final class Profile extends GenericJson {

    @Key
    private String emailAddress;

    @JsonString
    @Key
    private BigInteger historyId;

    @Key
    private Integer messagesTotal;

    @Key
    private Integer threadsTotal;

    public final String getEmailAddress() {
        return this.emailAddress;
    }

    public final Profile setEmailAddress(String str) {
        this.emailAddress = str;
        return this;
    }

    public final BigInteger getHistoryId() {
        return this.historyId;
    }

    public final Profile setHistoryId(BigInteger bigInteger) {
        this.historyId = bigInteger;
        return this;
    }

    public final Integer getMessagesTotal() {
        return this.messagesTotal;
    }

    public final Profile setMessagesTotal(Integer num) {
        this.messagesTotal = num;
        return this;
    }

    public final Integer getThreadsTotal() {
        return this.threadsTotal;
    }

    public final Profile setThreadsTotal(Integer num) {
        this.threadsTotal = num;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final Profile set(String str, Object obj) {
        return (Profile) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final Profile clone() {
        return (Profile) super.clone();
    }
}
