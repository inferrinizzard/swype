package com.fasterxml.jackson.core.io;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class MergedStream extends InputStream {
    byte[] _buffer;
    protected final IOContext _context;
    final int _end;
    final InputStream _in;
    int _ptr;

    public MergedStream(IOContext context, InputStream in, byte[] buf, int start, int end) {
        this._context = context;
        this._in = in;
        this._buffer = buf;
        this._ptr = start;
        this._end = end;
    }

    @Override // java.io.InputStream
    public final int available() throws IOException {
        return this._buffer != null ? this._end - this._ptr : this._in.available();
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        freeMergedBuffer();
        this._in.close();
    }

    @Override // java.io.InputStream
    public final void mark(int readlimit) {
        if (this._buffer == null) {
            this._in.mark(readlimit);
        }
    }

    @Override // java.io.InputStream
    public final boolean markSupported() {
        return this._buffer == null && this._in.markSupported();
    }

    @Override // java.io.InputStream
    public final int read() throws IOException {
        if (this._buffer == null) {
            return this._in.read();
        }
        byte[] bArr = this._buffer;
        int i = this._ptr;
        this._ptr = i + 1;
        int c = bArr[i] & 255;
        if (this._ptr >= this._end) {
            freeMergedBuffer();
            return c;
        }
        return c;
    }

    @Override // java.io.InputStream
    public final int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override // java.io.InputStream
    public final int read(byte[] b, int off, int len) throws IOException {
        if (this._buffer == null) {
            return this._in.read(b, off, len);
        }
        int avail = this._end - this._ptr;
        if (len > avail) {
            len = avail;
        }
        System.arraycopy(this._buffer, this._ptr, b, off, len);
        this._ptr += len;
        if (this._ptr >= this._end) {
            freeMergedBuffer();
        }
        return len;
    }

    @Override // java.io.InputStream
    public final void reset() throws IOException {
        if (this._buffer == null) {
            this._in.reset();
        }
    }

    @Override // java.io.InputStream
    public final long skip(long n) throws IOException {
        long count = 0;
        if (this._buffer != null) {
            int amount = this._end - this._ptr;
            if (amount > n) {
                this._ptr += (int) n;
                return n;
            }
            freeMergedBuffer();
            count = 0 + amount;
            n -= amount;
        }
        if (n > 0) {
            count += this._in.skip(n);
        }
        return count;
    }

    private void freeMergedBuffer() {
        byte[] buf = this._buffer;
        if (buf != null) {
            this._buffer = null;
            if (this._context != null) {
                this._context.releaseReadIOBuffer(buf);
            }
        }
    }
}
