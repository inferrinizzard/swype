package com.fasterxml.jackson.core.io;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class UTF32Reader extends BaseReader {
    protected final boolean _bigEndian;
    protected int _byteCount;
    protected int _charCount;
    protected final boolean _managedBuffers;
    protected char _surrogate;

    @Override // com.fasterxml.jackson.core.io.BaseReader, java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public final /* bridge */ /* synthetic */ void close() throws IOException {
        super.close();
    }

    @Override // com.fasterxml.jackson.core.io.BaseReader, java.io.Reader
    public final /* bridge */ /* synthetic */ int read() throws IOException {
        return super.read();
    }

    public UTF32Reader(IOContext ctxt, InputStream in, byte[] buf, int ptr, int len, boolean isBigEndian) {
        super(ctxt, in, buf, ptr, len);
        this._surrogate = (char) 0;
        this._charCount = 0;
        this._byteCount = 0;
        this._bigEndian = isBigEndian;
        this._managedBuffers = in != null;
    }

    @Override // java.io.Reader
    public final int read(char[] cbuf, int start, int len) throws IOException {
        int outPtr;
        int outPtr2;
        int ch;
        if (this._buffer == null) {
            return -1;
        }
        if (len <= 0) {
            return len;
        }
        if (start >= 0 && start + len <= cbuf.length) {
            int len2 = len + start;
            if (this._surrogate != 0) {
                int outPtr3 = start + 1;
                cbuf[start] = this._surrogate;
                this._surrogate = (char) 0;
                outPtr = outPtr3;
            } else {
                int left = this._length - this._ptr;
                if (left < 4 && !loadMore(left)) {
                    return -1;
                }
                outPtr = start;
            }
            while (outPtr < len2) {
                int ptr = this._ptr;
                if (this._bigEndian) {
                    ch = (this._buffer[ptr] << 24) | ((this._buffer[ptr + 1] & 255) << 16) | ((this._buffer[ptr + 2] & 255) << 8) | (this._buffer[ptr + 3] & 255);
                } else {
                    ch = (this._buffer[ptr] & 255) | ((this._buffer[ptr + 1] & 255) << 8) | ((this._buffer[ptr + 2] & 255) << 16) | (this._buffer[ptr + 3] << 24);
                }
                this._ptr += 4;
                if (ch <= 65535) {
                    outPtr2 = outPtr;
                } else if (ch <= 1114111) {
                    int ch2 = ch - 65536;
                    outPtr2 = outPtr + 1;
                    cbuf[outPtr] = (char) (55296 + (ch2 >> 10));
                    ch = 56320 | (ch2 & 1023);
                    if (outPtr2 >= len2) {
                        this._surrogate = (char) ch;
                        break;
                    }
                } else {
                    throw new CharConversionException("Invalid UTF-32 character 0x" + Integer.toHexString(ch) + ("(above " + Integer.toHexString(1114111) + ") ") + " at char #" + ((outPtr - start) + this._charCount) + ", byte #" + ((this._byteCount + this._ptr) - 1) + ")");
                }
                outPtr = outPtr2 + 1;
                cbuf[outPtr2] = (char) ch;
                if (this._ptr >= this._length) {
                    break;
                }
            }
            outPtr2 = outPtr;
            int len3 = outPtr2 - start;
            this._charCount += len3;
            return len3;
        }
        throw new ArrayIndexOutOfBoundsException("read(buf," + start + "," + len + "), cbuf[" + cbuf.length + "]");
    }

    private boolean loadMore(int available) throws IOException {
        this._byteCount += this._length - available;
        if (available > 0) {
            if (this._ptr > 0) {
                for (int i = 0; i < available; i++) {
                    this._buffer[i] = this._buffer[this._ptr + i];
                }
                this._ptr = 0;
            }
            this._length = available;
        } else {
            this._ptr = 0;
            int count = this._in == null ? -1 : this._in.read(this._buffer);
            if (count <= 0) {
                this._length = 0;
                if (count < 0) {
                    if (!this._managedBuffers) {
                        return false;
                    }
                    freeBuffers();
                    return false;
                }
                reportStrangeStream();
            }
            this._length = count;
        }
        while (this._length < 4) {
            int count2 = this._in == null ? -1 : this._in.read(this._buffer, this._length, this._buffer.length - this._length);
            if (count2 <= 0) {
                if (count2 < 0) {
                    if (this._managedBuffers) {
                        freeBuffers();
                    }
                    int i2 = this._length;
                    throw new CharConversionException("Unexpected EOF in the middle of a 4-byte UTF-32 char: got " + i2 + ", needed 4, at char #" + this._charCount + ", byte #" + (this._byteCount + i2) + ")");
                }
                reportStrangeStream();
            }
            this._length += count2;
        }
        return true;
    }
}
