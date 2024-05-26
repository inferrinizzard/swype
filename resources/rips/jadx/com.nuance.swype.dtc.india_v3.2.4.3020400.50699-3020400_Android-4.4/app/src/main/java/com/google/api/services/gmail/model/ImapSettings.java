package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class ImapSettings extends GenericJson {

    @Key
    private Boolean autoExpunge;

    @Key
    private Boolean enabled;

    @Key
    private String expungeBehavior;

    @Key
    private Integer maxFolderSize;

    public final Boolean getAutoExpunge() {
        return this.autoExpunge;
    }

    public final ImapSettings setAutoExpunge(Boolean bool) {
        this.autoExpunge = bool;
        return this;
    }

    public final Boolean getEnabled() {
        return this.enabled;
    }

    public final ImapSettings setEnabled(Boolean bool) {
        this.enabled = bool;
        return this;
    }

    public final String getExpungeBehavior() {
        return this.expungeBehavior;
    }

    public final ImapSettings setExpungeBehavior(String str) {
        this.expungeBehavior = str;
        return this;
    }

    public final Integer getMaxFolderSize() {
        return this.maxFolderSize;
    }

    public final ImapSettings setMaxFolderSize(Integer num) {
        this.maxFolderSize = num;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final ImapSettings set(String str, Object obj) {
        return (ImapSettings) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final ImapSettings clone() {
        return (ImapSettings) super.clone();
    }
}
