package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class FilterCriteria extends GenericJson {

    @Key
    private Boolean excludeChats;

    @Key
    private String from;

    @Key
    private Boolean hasAttachment;

    @Key
    private String negatedQuery;

    @Key
    private String query;

    @Key
    private Integer size;

    @Key
    private String sizeComparison;

    @Key
    private String subject;

    @Key
    private String to;

    public final Boolean getExcludeChats() {
        return this.excludeChats;
    }

    public final FilterCriteria setExcludeChats(Boolean bool) {
        this.excludeChats = bool;
        return this;
    }

    public final String getFrom() {
        return this.from;
    }

    public final FilterCriteria setFrom(String str) {
        this.from = str;
        return this;
    }

    public final Boolean getHasAttachment() {
        return this.hasAttachment;
    }

    public final FilterCriteria setHasAttachment(Boolean bool) {
        this.hasAttachment = bool;
        return this;
    }

    public final String getNegatedQuery() {
        return this.negatedQuery;
    }

    public final FilterCriteria setNegatedQuery(String str) {
        this.negatedQuery = str;
        return this;
    }

    public final String getQuery() {
        return this.query;
    }

    public final FilterCriteria setQuery(String str) {
        this.query = str;
        return this;
    }

    public final Integer getSize() {
        return this.size;
    }

    public final FilterCriteria setSize(Integer num) {
        this.size = num;
        return this;
    }

    public final String getSizeComparison() {
        return this.sizeComparison;
    }

    public final FilterCriteria setSizeComparison(String str) {
        this.sizeComparison = str;
        return this;
    }

    public final String getSubject() {
        return this.subject;
    }

    public final FilterCriteria setSubject(String str) {
        this.subject = str;
        return this;
    }

    public final String getTo() {
        return this.to;
    }

    public final FilterCriteria setTo(String str) {
        this.to = str;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final FilterCriteria set(String str, Object obj) {
        return (FilterCriteria) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final FilterCriteria clone() {
        return (FilterCriteria) super.clone();
    }
}
