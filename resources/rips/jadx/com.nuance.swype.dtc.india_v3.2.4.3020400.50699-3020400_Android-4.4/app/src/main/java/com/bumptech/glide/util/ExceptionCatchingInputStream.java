package com.bumptech.glide.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

/* loaded from: classes.dex */
public final class ExceptionCatchingInputStream extends InputStream {
    private static final Queue<ExceptionCatchingInputStream> QUEUE = Util.createQueue(0);
    public IOException exception;
    private InputStream wrapped;

    public static ExceptionCatchingInputStream obtain(InputStream toWrap) {
        ExceptionCatchingInputStream result;
        synchronized (QUEUE) {
            result = QUEUE.poll();
        }
        if (result == null) {
            result = new ExceptionCatchingInputStream();
        }
        result.wrapped = toWrap;
        return result;
    }

    ExceptionCatchingInputStream() {
    }

    @Override // java.io.InputStream
    public final int available() throws IOException {
        return this.wrapped.available();
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        this.wrapped.close();
    }

    @Override // java.io.InputStream
    public final void mark(int readlimit) {
        this.wrapped.mark(readlimit);
    }

    @Override // java.io.InputStream
    public final boolean markSupported() {
        return this.wrapped.markSupported();
    }

    @Override // java.io.InputStream
    public final int read(byte[] buffer) throws IOException {
        try {
            int read = this.wrapped.read(buffer);
            return read;
        } catch (IOException e) {
            this.exception = e;
            return -1;
        }
    }

    @Override // java.io.InputStream
    public final int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        try {
            int read = this.wrapped.read(buffer, byteOffset, byteCount);
            return read;
        } catch (IOException e) {
            this.exception = e;
            return -1;
        }
    }

    @Override // java.io.InputStream
    public final synchronized void reset() throws IOException {
        this.wrapped.reset();
    }

    @Override // java.io.InputStream
    public final long skip(long byteCount) throws IOException {
        try {
            long skipped = this.wrapped.skip(byteCount);
            return skipped;
        } catch (IOException e) {
            this.exception = e;
            return 0L;
        }
    }

    @Override // java.io.InputStream
    public final int read() throws IOException {
        try {
            int result = this.wrapped.read();
            return result;
        } catch (IOException e) {
            this.exception = e;
            return -1;
        }
    }

    public final void release() {
        this.exception = null;
        this.wrapped = null;
        synchronized (QUEUE) {
            QUEUE.offer(this);
        }
    }
}
