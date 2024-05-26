package com.google.api.client.http;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.Data;
import com.google.api.client.util.FieldInfo;
import com.google.api.client.util.Types;
import com.google.api.client.util.escape.CharEscapers;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class UrlEncodedContent extends AbstractHttpContent {
    private Object data;

    public UrlEncodedContent(Object data) {
        super(UrlEncodedParser.MEDIA_TYPE);
        setData(data);
    }

    @Override // com.google.api.client.http.HttpContent, com.google.api.client.util.StreamingContent
    public void writeTo(OutputStream out) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(out, getCharset()));
        boolean first = true;
        for (Map.Entry<String, Object> nameValueEntry : Data.mapOf(this.data).entrySet()) {
            Object value = nameValueEntry.getValue();
            if (value != null) {
                String name = CharEscapers.escapeUri(nameValueEntry.getKey());
                Class<?> cls = value.getClass();
                if ((value instanceof Iterable) || cls.isArray()) {
                    for (Object repeatedValue : Types.iterableOf(value)) {
                        first = appendParam(first, writer, name, repeatedValue);
                    }
                } else {
                    first = appendParam(first, writer, name, value);
                }
            }
        }
        writer.flush();
    }

    @Override // com.google.api.client.http.AbstractHttpContent
    public UrlEncodedContent setMediaType(HttpMediaType mediaType) {
        super.setMediaType(mediaType);
        return this;
    }

    public final Object getData() {
        return this.data;
    }

    public static UrlEncodedContent getContent(HttpRequest request) {
        HttpContent content = request.getContent();
        if (content != null) {
            return (UrlEncodedContent) content;
        }
        UrlEncodedContent result = new UrlEncodedContent(new HashMap());
        request.setContent(result);
        return result;
    }

    private static boolean appendParam(boolean first, Writer writer, String name, Object value) throws IOException {
        String obj;
        if (value != null && !Data.isNull(value)) {
            if (first) {
                first = false;
            } else {
                writer.write("&");
            }
            writer.write(name);
            if (!(value instanceof Enum)) {
                obj = value.toString();
            } else {
                obj = FieldInfo.of((Enum<?>) value).name;
            }
            String stringValue = CharEscapers.escapeUri(obj);
            if (stringValue.length() != 0) {
                writer.write("=");
                writer.write(stringValue);
            }
        }
        return first;
    }

    public UrlEncodedContent setData(Object data) {
        this.data = Preconditions.checkNotNull(data);
        return this;
    }
}
