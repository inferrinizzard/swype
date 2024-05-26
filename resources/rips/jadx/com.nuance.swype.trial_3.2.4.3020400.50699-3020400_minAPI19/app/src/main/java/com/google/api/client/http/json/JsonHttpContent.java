package com.google.api.client.http.json;

import com.google.api.client.http.AbstractHttpContent;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class JsonHttpContent extends AbstractHttpContent {
    private final Object data;
    private final JsonFactory jsonFactory;
    private String wrapperKey;

    public JsonHttpContent(JsonFactory jsonFactory, Object data) {
        super(Json.MEDIA_TYPE);
        this.jsonFactory = (JsonFactory) Preconditions.checkNotNull(jsonFactory);
        this.data = Preconditions.checkNotNull(data);
    }

    @Override // com.google.api.client.http.HttpContent, com.google.api.client.util.StreamingContent
    public void writeTo(OutputStream out) throws IOException {
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, getCharset());
        if (this.wrapperKey != null) {
            generator.writeStartObject();
            generator.writeFieldName(this.wrapperKey);
        }
        generator.serialize(this.data);
        if (this.wrapperKey != null) {
            generator.writeEndObject();
        }
        generator.flush();
    }

    @Override // com.google.api.client.http.AbstractHttpContent
    public JsonHttpContent setMediaType(HttpMediaType mediaType) {
        super.setMediaType(mediaType);
        return this;
    }

    public final Object getData() {
        return this.data;
    }

    public final JsonFactory getJsonFactory() {
        return this.jsonFactory;
    }

    public final String getWrapperKey() {
        return this.wrapperKey;
    }

    public JsonHttpContent setWrapperKey(String wrapperKey) {
        this.wrapperKey = wrapperKey;
        return this;
    }
}
