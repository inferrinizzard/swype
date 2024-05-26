package com.fasterxml.jackson.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/* loaded from: classes.dex */
abstract class BaseReader extends Reader {
    protected byte[] _buffer;
    protected final IOContext _context;
    protected InputStream _in;
    protected int _length;
    protected int _ptr;
    protected char[] _tmpBuf = null;

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseReader(IOContext context, InputStream in, byte[] buf, int ptr, int len) {
        this._context = context;
        this._in = in;
        this._buffer = buf;
        this._ptr = ptr;
        this._length = len;
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        InputStream in = this._in;
        if (in != null) {
            this._in = null;
            freeBuffers();
            in.close();
        }
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        if (this._tmpBuf == null) {
            this._tmpBuf = new char[1];
        }
        if (read(this._tmpBuf, 0, 1) <= 0) {
            return -1;
        }
        return this._tmpBuf[0];
    }

    public final void freeBuffers() {
        byte[] buf = this._buffer;
        if (buf != null) {
            this._buffer = null;
            this._context.releaseReadIOBuffer(buf);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void reportStrangeStream() throws IOException {
        throw new IOException("Strange I/O stream, returned 0 bytes on read");
    }
}
