package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class Label extends GenericJson {

    @Key
    private String id;

    @Key
    private String labelListVisibility;

    @Key
    private String messageListVisibility;

    @Key
    private Integer messagesTotal;

    @Key
    private Integer messagesUnread;

    @Key
    private String name;

    @Key
    private Integer threadsTotal;

    @Key
    private Integer threadsUnread;

    @Key
    private String type;

    public final String getId() {
        return this.id;
    }

    public final Label setId(String str) {
        this.id = str;
        return this;
    }

    public final String getLabelListVisibility() {
        return this.labelListVisibility;
    }

    public final Label setLabelListVisibility(String str) {
        this.labelListVisibility = str;
        return this;
    }

    public final String getMessageListVisibility() {
        return this.messageListVisibility;
    }

    public final Label setMessageListVisibility(String str) {
        this.messageListVisibility = str;
        return this;
    }

    public final Integer getMessagesTotal() {
        return this.messagesTotal;
    }

    public final Label setMessagesTotal(Integer num) {
        this.messagesTotal = num;
        return this;
    }

    public final Integer getMessagesUnread() {
        return this.messagesUnread;
    }

    public final Label setMessagesUnread(Integer num) {
        this.messagesUnread = num;
        return this;
    }

    public final String getName() {
        return this.name;
    }

    public final Label setName(String str) {
        this.name = str;
        return this;
    }

    public final Integer getThreadsTotal() {
        return this.threadsTotal;
    }

    public final Label setThreadsTotal(Integer num) {
        this.threadsTotal = num;
        return this;
    }

    public final Integer getThreadsUnread() {
        return this.threadsUnread;
    }

    public final Label setThreadsUnread(Integer num) {
        this.threadsUnread = num;
        return this;
    }

    public final String getType() {
        return this.type;
    }

    public final Label setType(String str) {
        this.type = str;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final Label set(String str, Object obj) {
        return (Label) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final Label clone() {
        return (Label) super.clone();
    }
}
