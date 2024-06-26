package com.google.api.services.gmail.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

/* loaded from: classes.dex */
public final class MessagePart extends GenericJson {

    @Key
    private MessagePartBody body;

    @Key
    private String filename;

    @Key
    private List<MessagePartHeader> headers;

    @Key
    private String mimeType;

    @Key
    private String partId;

    @Key
    private List<MessagePart> parts;

    public final MessagePartBody getBody() {
        return this.body;
    }

    public final MessagePart setBody(MessagePartBody messagePartBody) {
        this.body = messagePartBody;
        return this;
    }

    public final String getFilename() {
        return this.filename;
    }

    public final MessagePart setFilename(String str) {
        this.filename = str;
        return this;
    }

    public final List<MessagePartHeader> getHeaders() {
        return this.headers;
    }

    public final MessagePart setHeaders(List<MessagePartHeader> list) {
        this.headers = list;
        return this;
    }

    public final String getMimeType() {
        return this.mimeType;
    }

    public final MessagePart setMimeType(String str) {
        this.mimeType = str;
        return this;
    }

    public final String getPartId() {
        return this.partId;
    }

    public final MessagePart setPartId(String str) {
        this.partId = str;
        return this;
    }

    public final List<MessagePart> getParts() {
        return this.parts;
    }

    public final MessagePart setParts(List<MessagePart> list) {
        this.parts = list;
        return this;
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
    public final MessagePart set(String str, Object obj) {
        return (MessagePart) super.set(str, obj);
    }

    @Override // com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
    public final MessagePart clone() {
        return (MessagePart) super.clone();
    }
}
