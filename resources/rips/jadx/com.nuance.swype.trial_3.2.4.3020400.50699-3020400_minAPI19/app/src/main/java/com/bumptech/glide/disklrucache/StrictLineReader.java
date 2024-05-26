package com.bumptech.glide.disklrucache;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/* loaded from: classes.dex */
final class StrictLineReader implements Closeable {
    private byte[] buf;
    final Charset charset;
    int end;
    private final InputStream in;
    private int pos;

    public StrictLineReader(InputStream in, Charset charset) {
        this(in, charset, (byte) 0);
    }

    private StrictLineReader(InputStream in, Charset charset, byte b) {
        if (in == null || charset == null) {
            throw new NullPointerException();
        }
        if (!charset.equals(Util.US_ASCII)) {
            throw new IllegalArgumentException("Unsupported encoding");
        }
        this.in = in;
        this.charset = charset;
        this.buf = new byte[8192];
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        synchronized (this.in) {
            if (this.buf != null) {
                this.buf = null;
                this.in.close();
            }
        }
    }

    public final String readLine() throws IOException {
        int i;
        String res;
        synchronized (this.in) {
            if (this.buf == null) {
                throw new IOException("LineReader is closed");
            }
            if (this.pos >= this.end) {
                fillBuf();
            }
            int i2 = this.pos;
            while (true) {
                if (i2 != this.end) {
                    if (this.buf[i2] != 10) {
                        i2++;
                    } else {
                        int lineEnd = (i2 == this.pos || this.buf[i2 + (-1)] != 13) ? i2 : i2 - 1;
                        res = new String(this.buf, this.pos, lineEnd - this.pos, this.charset.name());
                        this.pos = i2 + 1;
                    }
                } else {
                    ByteArrayOutputStream out = new ByteArrayOutputStream((this.end - this.pos) + 80) { // from class: com.bumptech.glide.disklrucache.StrictLineReader.1
                        @Override // java.io.ByteArrayOutputStream
                        public final String toString() {
                            int length = (this.count <= 0 || this.buf[this.count + (-1)] != 13) ? this.count : this.count - 1;
                            try {
                                return new String(this.buf, 0, length, StrictLineReader.this.charset.name());
                            } catch (UnsupportedEncodingException e) {
                                throw new AssertionError(e);
                            }
                        }
                    };
                    loop1: while (true) {
                        out.write(this.buf, this.pos, this.end - this.pos);
                        this.end = -1;
                        fillBuf();
                        i = this.pos;
                        while (i != this.end) {
                            if (this.buf[i] == 10) {
                                break loop1;
                            }
                            i++;
                        }
                    }
                    if (i != this.pos) {
                        out.write(this.buf, this.pos, i - this.pos);
                    }
                    this.pos = i + 1;
                    res = out.toString();
                }
            }
            return res;
        }
    }

    private void fillBuf() throws IOException {
        int result = this.in.read(this.buf, 0, this.buf.length);
        if (result == -1) {
            throw new EOFException();
        }
        this.pos = 0;
        this.end = result;
    }
}