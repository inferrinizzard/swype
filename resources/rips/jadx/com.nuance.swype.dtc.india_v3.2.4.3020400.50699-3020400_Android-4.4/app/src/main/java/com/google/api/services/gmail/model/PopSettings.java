package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/* loaded from: classes.dex */
public final class PopSettings extends GenericJson {

    @Key
    private String accessWindow;

    @Key
    private String disposition;

    public final String getAccessWindow() {
        return this.accessWindow;
    }

    public final PopSettings setAccessWindow(String str) {
        this.accessWindow = str;
        return this;
    }

    public final String getDisposition() {
        return this.disposition;
    }

    public final PopSettings setDisposition(String str) {
        this.disposition = str;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final PopSettings set(String str, Object obj) {
        return (PopSettings) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final PopSettings clone() {
        return (PopSettings) super.clone();
    }
}
