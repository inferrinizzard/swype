package com.google.api.client.util;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public interface StreamingContent {
    void writeTo(OutputStream outputStream) throws IOException;
}
