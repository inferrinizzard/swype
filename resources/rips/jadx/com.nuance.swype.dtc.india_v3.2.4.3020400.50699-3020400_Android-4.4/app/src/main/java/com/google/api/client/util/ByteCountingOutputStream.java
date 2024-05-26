package com.google.api.client.util;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
final class ByteCountingOutputStream extends OutputStream {
    long count;

    @Override // java.io.OutputStream
    public final void write(byte[] b, int off, int len) throws IOException {
        this.count += len;
    }

    @Override // java.io.OutputStream
    public final void write(int b) throws IOException {
        this.count++;
    }
}
