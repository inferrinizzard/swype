package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.InputDecorator;
import com.fasterxml.jackson.core.io.OutputDecorator;
import com.fasterxml.jackson.core.io.UTF8Writer;
import com.fasterxml.jackson.core.json.ByteSourceJsonBootstrapper;
import com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.core.json.WriterBasedJsonGenerator;
import com.fasterxml.jackson.core.sym.BytesToNameCanonicalizer;
import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.lang.ref.SoftReference;

/* loaded from: classes.dex */
public final class JsonFactory implements Serializable {
    protected CharacterEscapes _characterEscapes;
    protected int _factoryFeatures;
    protected int _generatorFeatures;
    protected InputDecorator _inputDecorator;
    protected ObjectCodec _objectCodec;
    protected OutputDecorator _outputDecorator;
    protected int _parserFeatures;
    protected final transient BytesToNameCanonicalizer _rootByteSymbols;
    protected final transient CharsToNameCanonicalizer _rootCharSymbols;
    protected SerializableString _rootValueSeparator;
    protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = Feature.collectDefaults();
    protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
    protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
    private static final SerializableString DEFAULT_ROOT_VALUE_SEPARATOR = DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
    protected static final ThreadLocal<SoftReference<BufferRecycler>> _recyclerRef = new ThreadLocal<>();

    /* loaded from: classes.dex */
    public enum Feature {
        INTERN_FIELD_NAMES,
        CANONICALIZE_FIELD_NAMES;

        private final boolean _defaultState = true;

        public static int collectDefaults() {
            int flags = 0;
            Feature[] arr$ = values();
            for (Feature f : arr$) {
                if (f._defaultState) {
                    flags |= 1 << f.ordinal();
                }
            }
            return flags;
        }

        Feature() {
        }
    }

    public JsonFactory() {
        this(null);
    }

    private JsonFactory(ObjectCodec oc) {
        this._rootCharSymbols = CharsToNameCanonicalizer.createRoot();
        long currentTimeMillis = System.currentTimeMillis();
        this._rootByteSymbols = new BytesToNameCanonicalizer((((int) (currentTimeMillis >>> 32)) + ((int) currentTimeMillis)) | 1);
        this._factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
        this._parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
        this._generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
        this._rootValueSeparator = DEFAULT_ROOT_VALUE_SEPARATOR;
        this._objectCodec = oc;
    }

    protected final Object readResolve() {
        return new JsonFactory(this._objectCodec);
    }

    private boolean isEnabled(Feature f) {
        return (this._factoryFeatures & (1 << f.ordinal())) != 0;
    }

    public final JsonParser createJsonParser(InputStream in) throws IOException, JsonParseException {
        IOContext ctxt = _createContext(in, false);
        if (this._inputDecorator != null) {
            in = this._inputDecorator.decorate$44b83b11();
        }
        return new ByteSourceJsonBootstrapper(ctxt, in).constructParser(this._parserFeatures, this._objectCodec, this._rootByteSymbols, this._rootCharSymbols, isEnabled(Feature.CANONICALIZE_FIELD_NAMES), isEnabled(Feature.INTERN_FIELD_NAMES));
    }

    public final JsonParser createJsonParser(Reader r) throws IOException, JsonParseException {
        IOContext ctxt = _createContext(r, false);
        if (this._inputDecorator != null) {
            r = this._inputDecorator.decorate$6b9cf12f();
        }
        return _createParser(r, ctxt);
    }

    public final JsonParser createJsonParser(String content) throws IOException, JsonParseException {
        Reader r = new StringReader(content);
        IOContext ctxt = _createContext(r, true);
        if (this._inputDecorator != null) {
            r = this._inputDecorator.decorate$6b9cf12f();
        }
        return _createParser(r, ctxt);
    }

    public final JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc) throws IOException {
        Writer w;
        IOContext ctxt = _createContext(out, false);
        ctxt.setEncoding(enc);
        if (enc == JsonEncoding.UTF8) {
            if (this._outputDecorator != null) {
                out = this._outputDecorator.decorate$1fbd8b2f();
            }
            UTF8JsonGenerator uTF8JsonGenerator = new UTF8JsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
            if (this._characterEscapes != null) {
                uTF8JsonGenerator.setCharacterEscapes(this._characterEscapes);
            }
            SerializableString serializableString = this._rootValueSeparator;
            if (serializableString == DEFAULT_ROOT_VALUE_SEPARATOR) {
                return uTF8JsonGenerator;
            }
            uTF8JsonGenerator.setRootValueSeparator(serializableString);
            return uTF8JsonGenerator;
        }
        if (enc == JsonEncoding.UTF8) {
            w = new UTF8Writer(ctxt, out);
        } else {
            w = new OutputStreamWriter(out, enc.getJavaName());
        }
        if (this._outputDecorator != null) {
            w = this._outputDecorator.decorate$390cdb2f();
        }
        return _createGenerator(w, ctxt);
    }

    public final JsonGenerator createJsonGenerator(Writer out) throws IOException {
        IOContext ctxt = _createContext(out, false);
        if (this._outputDecorator != null) {
            out = this._outputDecorator.decorate$390cdb2f();
        }
        return _createGenerator(out, ctxt);
    }

    private static IOContext _createContext(Object srcRef, boolean resourceManaged) {
        SoftReference<BufferRecycler> softReference = _recyclerRef.get();
        BufferRecycler bufferRecycler = softReference == null ? null : softReference.get();
        if (bufferRecycler == null) {
            bufferRecycler = new BufferRecycler();
            _recyclerRef.set(new SoftReference<>(bufferRecycler));
        }
        return new IOContext(bufferRecycler, srcRef, resourceManaged);
    }

    public final JsonFactory configure$3c9917c0(JsonGenerator.Feature f) {
        this._generatorFeatures &= f._mask ^ (-1);
        return this;
    }

    private JsonParser _createParser(Reader r, IOContext ctxt) throws IOException, JsonParseException {
        return new ReaderBasedJsonParser(ctxt, this._parserFeatures, r, this._objectCodec, this._rootCharSymbols.makeChild(isEnabled(Feature.CANONICALIZE_FIELD_NAMES), isEnabled(Feature.INTERN_FIELD_NAMES)));
    }

    private JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
        WriterBasedJsonGenerator writerBasedJsonGenerator = new WriterBasedJsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
        if (this._characterEscapes != null) {
            writerBasedJsonGenerator.setCharacterEscapes(this._characterEscapes);
        }
        SerializableString serializableString = this._rootValueSeparator;
        if (serializableString != DEFAULT_ROOT_VALUE_SEPARATOR) {
            writerBasedJsonGenerator.setRootValueSeparator(serializableString);
        }
        return writerBasedJsonGenerator;
    }
}
