package com.google.api.client.extensions.android.json;

import android.annotation.TargetApi;
import android.util.JsonReader;
import android.util.JsonWriter;
import com.google.api.client.extensions.android.AndroidUtils;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.util.Charsets;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;

@TargetApi(11)
/* loaded from: classes.dex */
public class AndroidJsonFactory extends JsonFactory {
    public static AndroidJsonFactory getDefaultInstance() {
        return InstanceHolder.INSTANCE;
    }

    /* loaded from: classes.dex */
    static class InstanceHolder {
        static final AndroidJsonFactory INSTANCE = new AndroidJsonFactory();

        InstanceHolder() {
        }
    }

    public AndroidJsonFactory() {
        AndroidUtils.checkMinimumSdkLevel(11);
    }

    @Override // com.google.api.client.json.JsonFactory
    public JsonParser createJsonParser(InputStream in) {
        return createJsonParser(new InputStreamReader(in, Charsets.UTF_8));
    }

    @Override // com.google.api.client.json.JsonFactory
    public JsonParser createJsonParser(InputStream in, Charset charset) {
        return charset == null ? createJsonParser(in) : createJsonParser(new InputStreamReader(in, charset));
    }

    @Override // com.google.api.client.json.JsonFactory
    public JsonParser createJsonParser(String value) {
        return createJsonParser(new StringReader(value));
    }

    @Override // com.google.api.client.json.JsonFactory
    public JsonParser createJsonParser(Reader reader) {
        return new AndroidJsonParser(this, new JsonReader(reader));
    }

    @Override // com.google.api.client.json.JsonFactory
    public JsonGenerator createJsonGenerator(OutputStream out, Charset enc) {
        return createJsonGenerator(new OutputStreamWriter(out, enc));
    }

    @Override // com.google.api.client.json.JsonFactory
    public JsonGenerator createJsonGenerator(Writer writer) {
        return new AndroidJsonGenerator(this, new JsonWriter(writer));
    }
}
