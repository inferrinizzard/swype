package com.google.api.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/* loaded from: classes.dex */
public interface ObjectParser {
    <T> T parseAndClose(InputStream inputStream, Charset charset, Class<T> cls) throws IOException;

    Object parseAndClose(InputStream inputStream, Charset charset, Type type) throws IOException;
}
