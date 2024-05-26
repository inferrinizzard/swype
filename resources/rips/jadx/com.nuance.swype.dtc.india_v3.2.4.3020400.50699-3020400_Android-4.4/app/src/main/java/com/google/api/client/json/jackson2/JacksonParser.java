package com.google.api.client.json.jackson2;

import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: classes.dex */
final class JacksonParser extends JsonParser {
    private final JacksonFactory factory;
    private final com.fasterxml.jackson.core.JsonParser parser;

    @Override // com.google.api.client.json.JsonParser
    public final JacksonFactory getFactory() {
        return this.factory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JacksonParser(JacksonFactory factory, com.fasterxml.jackson.core.JsonParser parser) {
        this.factory = factory;
        this.parser = parser;
    }

    @Override // com.google.api.client.json.JsonParser
    public final void close() throws IOException {
        this.parser.close();
    }

    @Override // com.google.api.client.json.JsonParser
    public final JsonToken nextToken() throws IOException {
        return JacksonFactory.convert(this.parser.nextToken());
    }

    @Override // com.google.api.client.json.JsonParser
    public final String getCurrentName() throws IOException {
        return this.parser.getCurrentName();
    }

    @Override // com.google.api.client.json.JsonParser
    public final JsonToken getCurrentToken() {
        return JacksonFactory.convert(this.parser.getCurrentToken());
    }

    @Override // com.google.api.client.json.JsonParser
    public final JsonParser skipChildren() throws IOException {
        this.parser.skipChildren();
        return this;
    }

    @Override // com.google.api.client.json.JsonParser
    public final String getText() throws IOException {
        return this.parser.getText();
    }

    @Override // com.google.api.client.json.JsonParser
    public final byte getByteValue() throws IOException {
        com.fasterxml.jackson.core.JsonParser jsonParser = this.parser;
        int intValue = jsonParser.getIntValue();
        if (intValue < -128 || intValue > 255) {
            throw jsonParser._constructError("Numeric value (" + jsonParser.getText() + ") out of range of Java byte");
        }
        return (byte) intValue;
    }

    @Override // com.google.api.client.json.JsonParser
    public final float getFloatValue() throws IOException {
        return this.parser.getFloatValue();
    }

    @Override // com.google.api.client.json.JsonParser
    public final int getIntValue() throws IOException {
        return this.parser.getIntValue();
    }

    @Override // com.google.api.client.json.JsonParser
    public final short getShortValue() throws IOException {
        com.fasterxml.jackson.core.JsonParser jsonParser = this.parser;
        int intValue = jsonParser.getIntValue();
        if (intValue < -32768 || intValue > 32767) {
            throw jsonParser._constructError("Numeric value (" + jsonParser.getText() + ") out of range of Java short");
        }
        return (short) intValue;
    }

    @Override // com.google.api.client.json.JsonParser
    public final BigInteger getBigIntegerValue() throws IOException {
        return this.parser.getBigIntegerValue();
    }

    @Override // com.google.api.client.json.JsonParser
    public final BigDecimal getDecimalValue() throws IOException {
        return this.parser.getDecimalValue();
    }

    @Override // com.google.api.client.json.JsonParser
    public final double getDoubleValue() throws IOException {
        return this.parser.getDoubleValue();
    }

    @Override // com.google.api.client.json.JsonParser
    public final long getLongValue() throws IOException {
        return this.parser.getLongValue();
    }
}
