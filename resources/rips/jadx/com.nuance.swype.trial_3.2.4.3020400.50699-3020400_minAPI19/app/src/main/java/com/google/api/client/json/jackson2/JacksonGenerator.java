package com.google.api.client.json.jackson2;

import com.google.api.client.json.JsonGenerator;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: classes.dex */
final class JacksonGenerator extends JsonGenerator {
    private final JacksonFactory factory;
    private final com.fasterxml.jackson.core.JsonGenerator generator;

    @Override // com.google.api.client.json.JsonGenerator
    public final JacksonFactory getFactory() {
        return this.factory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JacksonGenerator(JacksonFactory factory, com.fasterxml.jackson.core.JsonGenerator generator) {
        this.factory = factory;
        this.generator = generator;
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void flush() throws IOException {
        this.generator.flush();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void close() throws IOException {
        this.generator.close();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeBoolean(boolean state) throws IOException {
        this.generator.writeBoolean(state);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeEndArray() throws IOException {
        this.generator.writeEndArray();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeEndObject() throws IOException {
        this.generator.writeEndObject();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeFieldName(String name) throws IOException {
        this.generator.writeFieldName(name);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeNull() throws IOException {
        this.generator.writeNull();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeNumber(int v) throws IOException {
        this.generator.writeNumber(v);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeNumber(long v) throws IOException {
        this.generator.writeNumber(v);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeNumber(BigInteger v) throws IOException {
        this.generator.writeNumber(v);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeNumber(double v) throws IOException {
        this.generator.writeNumber(v);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeNumber(float v) throws IOException {
        this.generator.writeNumber(v);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeNumber(BigDecimal v) throws IOException {
        this.generator.writeNumber(v);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeNumber(String encodedValue) throws IOException {
        this.generator.writeNumber(encodedValue);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeStartArray() throws IOException {
        this.generator.writeStartArray();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeStartObject() throws IOException {
        this.generator.writeStartObject();
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void writeString(String value) throws IOException {
        this.generator.writeString(value);
    }

    @Override // com.google.api.client.json.JsonGenerator
    public final void enablePrettyPrint() throws IOException {
        this.generator.useDefaultPrettyPrinter();
    }
}
