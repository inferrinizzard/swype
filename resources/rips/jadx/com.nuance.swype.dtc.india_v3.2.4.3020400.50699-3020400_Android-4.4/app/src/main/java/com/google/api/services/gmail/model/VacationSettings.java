package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class VacationSettings extends GenericJson {

    @Key
    private Boolean enableAutoReply;

    @JsonString
    @Key
    private Long endTime;

    @Key
    private String responseBodyHtml;

    @Key
    private String responseBodyPlainText;

    @Key
    private String responseSubject;

    @Key
    private Boolean restrictToContacts;

    @Key
    private Boolean restrictToDomain;

    @JsonString
    @Key
    private Long startTime;

    public final Boolean getEnableAutoReply() {
        return this.enableAutoReply;
    }

    public final VacationSettings setEnableAutoReply(Boolean bool) {
        this.enableAutoReply = bool;
        return this;
    }

    public final Long getEndTime() {
        return this.endTime;
    }

    public final VacationSettings setEndTime(Long l) {
        this.endTime = l;
        return this;
    }

    public final String getResponseBodyHtml() {
        return this.responseBodyHtml;
    }

    public final VacationSettings setResponseBodyHtml(String str) {
        this.responseBodyHtml = str;
        return this;
    }

    public final String getResponseBodyPlainText() {
        return this.responseBodyPlainText;
    }

    public final VacationSettings setResponseBodyPlainText(String str) {
        this.responseBodyPlainText = str;
        return this;
    }

    public final String getResponseSubject() {
        return this.responseSubject;
    }

    public final VacationSettings setResponseSubject(String str) {
        this.responseSubject = str;
        return this;
    }

    public final Boolean getRestrictToContacts() {
        return this.restrictToContacts;
    }

    public final VacationSettings setRestrictToContacts(Boolean bool) {
        this.restrictToContacts = bool;
        return this;
    }

    public final Boolean getRestrictToDomain() {
        return this.restrictToDomain;
    }

    public final VacationSettings setRestrictToDomain(Boolean bool) {
        this.restrictToDomain = bool;
        return this;
    }

    public final Long getStartTime() {
        return this.startTime;
    }

    public final VacationSettings setStartTime(Long l) {
        this.startTime = l;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final VacationSettings set(String str, Object obj) {
        return (VacationSettings) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final VacationSettings clone() {
        return (VacationSettings) super.clone();
    }
}
