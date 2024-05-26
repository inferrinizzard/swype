package com.google.api.client.json.jackson2;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

/* loaded from: classes.dex */
public final class JacksonFactory extends JsonFactory {
    private final com.fasterxml.jackson.core.JsonFactory factory = new com.fasterxml.jackson.core.JsonFactory();

    public JacksonFactory() {
        this.factory.configure$3c9917c0(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
    }

    public static JacksonFactory getDefaultInstance() {
        return InstanceHolder.INSTANCE;
    }

    /* loaded from: classes.dex */
    static class InstanceHolder {
        static final JacksonFactory INSTANCE = new JacksonFactory();

        InstanceHolder() {
        }
    }

    @Override // com.google.api.client.json.JsonFactory
    public final com.google.api.client.json.JsonGenerator createJsonGenerator(OutputStream out, Charset enc) throws IOException {
        return new JacksonGenerator(this, this.factory.createJsonGenerator(out, JsonEncoding.UTF8));
    }

    @Override // com.google.api.client.json.JsonFactory
    public final com.google.api.client.json.JsonGenerator createJsonGenerator(Writer writer) throws IOException {
        return new JacksonGenerator(this, this.factory.createJsonGenerator(writer));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static JsonToken convert(com.fasterxml.jackson.core.JsonToken token) {
        if (token == null) {
            return null;
        }
        switch (token) {
            case END_ARRAY:
                return JsonToken.END_ARRAY;
            case START_ARRAY:
                return JsonToken.START_ARRAY;
            case END_OBJECT:
                return JsonToken.END_OBJECT;
            case START_OBJECT:
                return JsonToken.START_OBJECT;
            case VALUE_FALSE:
                return JsonToken.VALUE_FALSE;
            case VALUE_TRUE:
                return JsonToken.VALUE_TRUE;
            case VALUE_NULL:
                return JsonToken.VALUE_NULL;
            case VALUE_STRING:
                return JsonToken.VALUE_STRING;
            case VALUE_NUMBER_FLOAT:
                return JsonToken.VALUE_NUMBER_FLOAT;
            case VALUE_NUMBER_INT:
                return JsonToken.VALUE_NUMBER_INT;
            case FIELD_NAME:
                return JsonToken.FIELD_NAME;
            default:
                return JsonToken.NOT_AVAILABLE;
        }
    }

    @Override // com.google.api.client.json.JsonFactory
    public final JsonParser createJsonParser(Reader reader) throws IOException {
        Preconditions.checkNotNull(reader);
        return new JacksonParser(this, this.factory.createJsonParser(reader));
    }

    @Override // com.google.api.client.json.JsonFactory
    public final JsonParser createJsonParser(InputStream in) throws IOException {
        Preconditions.checkNotNull(in);
        return new JacksonParser(this, this.factory.createJsonParser(in));
    }

    @Override // com.google.api.client.json.JsonFactory
    public final JsonParser createJsonParser(InputStream in, Charset charset) throws IOException {
        Preconditions.checkNotNull(in);
        return new JacksonParser(this, this.factory.createJsonParser(in));
    }

    @Override // com.google.api.client.json.JsonFactory
    public final JsonParser createJsonParser(String value) throws IOException {
        Preconditions.checkNotNull(value);
        return new JacksonParser(this, this.factory.createJsonParser(value));
    }
}
