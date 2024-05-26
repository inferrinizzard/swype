package com.google.api.client.http;

import com.google.api.client.util.Charsets;
import com.google.api.client.util.Data;
import com.google.api.client.util.ObjectParser;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.Throwables;
import com.google.api.client.util.Types;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

/* loaded from: classes.dex */
public class UrlEncodedParser implements ObjectParser {
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String MEDIA_TYPE = new HttpMediaType(CONTENT_TYPE).setCharsetParameter(Charsets.UTF_8).build();

    public static void parse(String content, Object data) {
        if (content != null) {
            try {
                parse(new StringReader(content), data);
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x005b, code lost:            r11 = com.google.api.client.util.escape.CharEscapers.decodeUri(r12.toString());     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0067, code lost:            if (r11.length() == 0) goto L22;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0069, code lost:            r16 = com.google.api.client.util.escape.CharEscapers.decodeUri(r19.toString());        r7 = r3.getFieldInfo(r11);     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0075, code lost:            if (r7 == null) goto L37;     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0077, code lost:            r18 = com.google.api.client.util.Data.resolveWildcardTypeOrTypeVariable(r6, r7.field.getGenericType());     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0089, code lost:            if (com.google.api.client.util.Types.isArray(r18) == false) goto L26;     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x008b, code lost:            r13 = com.google.api.client.util.Types.getRawArrayComponentType(r6, com.google.api.client.util.Types.getArrayComponentType(r18));        r2.put(r7.field, r13, parseValue(r13, r6, r16));     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x00c7, code lost:            if (com.google.api.client.util.Types.isAssignableToOrFrom(com.google.api.client.util.Types.getRawArrayComponentType(r6, r18), java.lang.Iterable.class) == false) goto L36;     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x00c9, code lost:            r5 = (java.util.Collection) r7.getValue(r23);     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x00d1, code lost:            if (r5 != null) goto L31;     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x00d3, code lost:            r5 = com.google.api.client.util.Data.newCollectionInstance(r18);        r7.setValue(r23, r5);     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x00e2, code lost:            if (r18 != java.lang.Object.class) goto L35;     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00e4, code lost:            r17 = null;     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x00e6, code lost:            r5.add(parseValue(r17, r6, r16));     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x00f4, code lost:            r17 = com.google.api.client.util.Types.getIterableParameter(r18);     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00f9, code lost:            r7.setValue(r23, parseValue(r18, r6, r16));     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0109, code lost:            if (r10 == null) goto L22;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x010b, code lost:            r9 = (java.util.ArrayList) r10.get(r11);     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0111, code lost:            if (r9 != null) goto L43;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0113, code lost:            r9 = new java.util.ArrayList<>();     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0118, code lost:            if (r8 == null) goto L44;     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x011a, code lost:            r8.set(r11, r9);     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0123, code lost:            r10.put(r11, r9);     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x011d, code lost:            r9.add(r16);     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00a6, code lost:            r15 = true;        r12 = new java.io.StringWriter();        r19 = new java.io.StringWriter();     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00b5, code lost:            if (r14 != (-1)) goto L54;     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00b7, code lost:            r2.setValues();     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00ba, code lost:            return;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static void parse(java.io.Reader r22, java.lang.Object r23) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 320
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.api.client.http.UrlEncodedParser.parse(java.io.Reader, java.lang.Object):void");
    }

    private static Object parseValue(Type valueType, List<Type> context, String value) {
        return Data.parsePrimitiveValue(Data.resolveWildcardTypeOrTypeVariable(context, valueType), value);
    }

    @Override // com.google.api.client.util.ObjectParser
    public <T> T parseAndClose(InputStream inputStream, Charset charset, Class<T> cls) throws IOException {
        return (T) parseAndClose((Reader) new InputStreamReader(inputStream, charset), (Class) cls);
    }

    @Override // com.google.api.client.util.ObjectParser
    public Object parseAndClose(InputStream in, Charset charset, Type dataType) throws IOException {
        InputStreamReader r = new InputStreamReader(in, charset);
        return parseAndClose(r, dataType);
    }

    public <T> T parseAndClose(Reader reader, Class<T> cls) throws IOException {
        return (T) parseAndClose(reader, (Type) cls);
    }

    public Object parseAndClose(Reader reader, Type dataType) throws IOException {
        Preconditions.checkArgument(dataType instanceof Class, "dataType has to be of type Class<?>");
        Object newInstance = Types.newInstance((Class) dataType);
        parse(new BufferedReader(reader), newInstance);
        return newInstance;
    }
}
