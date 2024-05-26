package com.fasterxml.jackson.core.base;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class GeneratorBase extends JsonGenerator {
    protected boolean _closed;
    protected int _features;
    protected ObjectCodec _objectCodec;
    public JsonWriteContext _writeContext = new JsonWriteContext(0, null);
    public boolean _cfgNumbersAsStrings = isEnabled(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);

    public abstract void _releaseBuffers();

    public abstract void _verifyValueWrite(String str) throws IOException, JsonGenerationException;

    public GeneratorBase(int features, ObjectCodec codec) {
        this._features = features;
        this._objectCodec = codec;
    }

    public final boolean isEnabled(JsonGenerator.Feature f) {
        return (this._features & f._mask) != 0;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this._closed = true;
    }

    public static void _reportError(String msg) throws JsonGenerationException {
        throw new JsonGenerationException(msg);
    }

    public static void _cantHappen() {
        throw new RuntimeException("Internal error: should never end up through this code path");
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final JsonGenerator useDefaultPrettyPrinter() {
        if (this._cfgPrettyPrinter == null) {
            this._cfgPrettyPrinter = new DefaultPrettyPrinter();
        }
        return this;
    }
}
