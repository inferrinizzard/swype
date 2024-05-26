package com.google.api.client.extensions.android.json;

import android.annotation.TargetApi;
import android.util.JsonWriter;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.nuance.swype.input.ThemeManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

@TargetApi(11)
/* loaded from: classes.dex */
class AndroidJsonGenerator extends JsonGenerator {
    private final AndroidJsonFactory factory;
    private final JsonWriter writer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AndroidJsonGenerator(AndroidJsonFactory factory, JsonWriter writer) {
        this.factory = factory;
        this.writer = writer;
        writer.setLenient(true);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void flush() throws IOException {
        this.writer.flush();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void close() throws IOException {
        this.writer.close();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public JsonFactory getFactory() {
        return this.factory;
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeBoolean(boolean state) throws IOException {
        this.writer.value(state);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeEndArray() throws IOException {
        this.writer.endArray();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeEndObject() throws IOException {
        this.writer.endObject();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeFieldName(String name) throws IOException {
        this.writer.name(name);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeNull() throws IOException {
        this.writer.nullValue();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeNumber(int v) throws IOException {
        this.writer.value(v);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeNumber(long v) throws IOException {
        this.writer.value(v);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeNumber(BigInteger v) throws IOException {
        this.writer.value(v);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeNumber(double v) throws IOException {
        this.writer.value(v);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeNumber(float v) throws IOException {
        this.writer.value(v);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeNumber(BigDecimal v) throws IOException {
        this.writer.value(v);
    }

    /* loaded from: classes.dex */
    static final class StringNumber extends Number {
        private final String encodedValue;

        StringNumber(String encodedValue) {
            this.encodedValue = encodedValue;
        }

        @Override // java.lang.Number
        public final double doubleValue() {
            return 0.0d;
        }

        @Override // java.lang.Number
        public final float floatValue() {
            return 0.0f;
        }

        @Override // java.lang.Number
        public final int intValue() {
            return 0;
        }

        @Override // java.lang.Number
        public final long longValue() {
            return 0L;
        }

        public final String toString() {
            return this.encodedValue;
        }
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeNumber(String encodedValue) throws IOException {
        this.writer.value(new StringNumber(encodedValue));
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeStartArray() throws IOException {
        this.writer.beginArray();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeStartObject() throws IOException {
        this.writer.beginObject();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void writeString(String value) throws IOException {
        this.writer.value(value);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public void enablePrettyPrint() throws IOException {
        this.writer.setIndent(ThemeManager.NO_PRICE);
    }
}
