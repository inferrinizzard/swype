package com.google.api.client.json;

import com.google.api.client.util.ObjectParser;
import com.google.api.client.util.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public class JsonObjectParser implements ObjectParser {
    private final JsonFactory jsonFactory;
    private final Set<String> wrapperKeys;

    public JsonObjectParser(JsonFactory jsonFactory) {
        this(new Builder(jsonFactory));
    }

    protected JsonObjectParser(Builder builder) {
        this.jsonFactory = builder.jsonFactory;
        this.wrapperKeys = new HashSet(builder.wrapperKeys);
    }

    @Override // com.google.api.client.util.ObjectParser
    public <T> T parseAndClose(InputStream inputStream, Charset charset, Class<T> cls) throws IOException {
        return (T) parseAndClose(inputStream, charset, (Type) cls);
    }

    @Override // com.google.api.client.util.ObjectParser
    public Object parseAndClose(InputStream in, Charset charset, Type dataType) throws IOException {
        JsonParser parser = this.jsonFactory.createJsonParser(in, charset);
        initializeParser(parser);
        return parser.parse(dataType, true);
    }

    public <T> T parseAndClose(Reader reader, Class<T> cls) throws IOException {
        return (T) parseAndClose(reader, (Type) cls);
    }

    public Object parseAndClose(Reader reader, Type dataType) throws IOException {
        JsonParser parser = this.jsonFactory.createJsonParser(reader);
        initializeParser(parser);
        return parser.parse(dataType, true);
    }

    public final JsonFactory getJsonFactory() {
        return this.jsonFactory;
    }

    public Set<String> getWrapperKeys() {
        return Collections.unmodifiableSet(this.wrapperKeys);
    }

    private void initializeParser(JsonParser parser) throws IOException {
        if (!this.wrapperKeys.isEmpty()) {
            try {
                Preconditions.checkArgument((parser.skipToKey(this.wrapperKeys) == null || parser.getCurrentToken() == JsonToken.END_OBJECT) ? false : true, "wrapper key(s) not found: %s", this.wrapperKeys);
            } catch (Throwable th) {
                parser.close();
                throw th;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class Builder {
        final JsonFactory jsonFactory;
        Collection<String> wrapperKeys = new HashSet();

        public Builder(JsonFactory jsonFactory) {
            this.jsonFactory = (JsonFactory) com.google.api.client.repackaged.com.google.common.base.Preconditions.checkNotNull(jsonFactory);
        }

        public JsonObjectParser build() {
            return new JsonObjectParser(this);
        }

        public final JsonFactory getJsonFactory() {
            return this.jsonFactory;
        }

        public final Collection<String> getWrapperKeys() {
            return this.wrapperKeys;
        }

        public Builder setWrapperKeys(Collection<String> wrapperKeys) {
            this.wrapperKeys = wrapperKeys;
            return this;
        }
    }
}
