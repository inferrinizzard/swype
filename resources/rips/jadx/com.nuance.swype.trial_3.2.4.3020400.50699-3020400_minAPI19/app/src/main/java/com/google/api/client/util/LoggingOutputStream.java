package com.google.api.client.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: classes.dex */
public final class LoggingOutputStream extends FilterOutputStream {
    final LoggingByteArrayOutputStream logStream;

    public LoggingOutputStream(OutputStream outputStream, Logger logger, Level loggingLevel, int contentLoggingLimit) {
        super(outputStream);
        this.logStream = new LoggingByteArrayOutputStream(logger, loggingLevel, contentLoggingLimit);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public final void write(int b) throws IOException {
        this.out.write(b);
        this.logStream.write(b);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public final void write(byte[] b, int off, int len) throws IOException {
        this.out.write(b, off, len);
        this.logStream.write(b, off, len);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        this.logStream.close();
        super.close();
    }
}
