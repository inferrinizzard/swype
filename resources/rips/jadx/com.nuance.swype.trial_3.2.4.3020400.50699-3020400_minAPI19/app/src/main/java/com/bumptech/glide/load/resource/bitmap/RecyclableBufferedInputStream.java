package com.bumptech.glide.load.resource.bitmap;

import android.util.Log;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class RecyclableBufferedInputStream extends FilterInputStream {
    private volatile byte[] buf;
    private int count;
    private int marklimit;
    private int markpos;
    private int pos;

    public RecyclableBufferedInputStream(InputStream in, byte[] buffer) {
        super(in);
        this.markpos = -1;
        if (buffer == null || buffer.length == 0) {
            throw new IllegalArgumentException("buffer is null or empty");
        }
        this.buf = buffer;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final synchronized int available() throws IOException {
        InputStream localIn;
        localIn = this.in;
        if (this.buf == null || localIn == null) {
            throw streamClosed();
        }
        return (this.count - this.pos) + localIn.available();
    }

    private static IOException streamClosed() throws IOException {
        throw new IOException("BufferedInputStream is closed");
    }

    public final synchronized void fixMarkLimit() {
        this.marklimit = this.buf.length;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        this.buf = null;
        InputStream localIn = this.in;
        this.in = null;
        if (localIn != null) {
            localIn.close();
        }
    }

    private int fillbuf(InputStream localIn, byte[] localBuf) throws IOException {
        if (this.markpos == -1 || this.pos - this.markpos >= this.marklimit) {
            int result = localIn.read(localBuf);
            if (result > 0) {
                this.markpos = -1;
                this.pos = 0;
                this.count = result;
            }
            return result;
        }
        if (this.markpos == 0 && this.marklimit > localBuf.length && this.count == localBuf.length) {
            int newLength = localBuf.length * 2;
            if (newLength > this.marklimit) {
                newLength = this.marklimit;
            }
            Log.isLoggable("BufferedIs", 3);
            byte[] newbuf = new byte[newLength];
            System.arraycopy(localBuf, 0, newbuf, 0, localBuf.length);
            this.buf = newbuf;
            localBuf = newbuf;
        } else if (this.markpos > 0) {
            System.arraycopy(localBuf, this.markpos, localBuf, 0, localBuf.length - this.markpos);
        }
        this.pos -= this.markpos;
        this.markpos = 0;
        this.count = 0;
        int bytesread = localIn.read(localBuf, this.pos, localBuf.length - this.pos);
        this.count = bytesread <= 0 ? this.pos : this.pos + bytesread;
        return bytesread;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final synchronized void mark(int readlimit) {
        this.marklimit = Math.max(this.marklimit, readlimit);
        this.markpos = this.pos;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final boolean markSupported() {
        return true;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final synchronized int read() throws IOException {
        int i = -1;
        synchronized (this) {
            byte[] localBuf = this.buf;
            InputStream localIn = this.in;
            if (localBuf == null || localIn == null) {
                throw streamClosed();
            }
            if (this.pos < this.count || fillbuf(localIn, localBuf) != -1) {
                if (localBuf != this.buf && (localBuf = this.buf) == null) {
                    throw streamClosed();
                }
                if (this.count - this.pos > 0) {
                    int i2 = this.pos;
                    this.pos = i2 + 1;
                    i = localBuf[i2] & 255;
                }
            }
        }
        return i;
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x0094 A[Catch: all -> 0x000b, TRY_LEAVE, TryCatch #0 {, blocks: (B:4:0x0002, B:6:0x0006, B:7:0x000a, B:13:0x0013, B:15:0x0017, B:16:0x001b, B:17:0x001c, B:19:0x0022, B:22:0x002a, B:24:0x0036, B:26:0x003c, B:28:0x003f, B:30:0x0043, B:32:0x0046, B:46:0x0059, B:34:0x0088, B:36:0x0094, B:47:0x005c, B:64:0x0066, B:49:0x0069, B:51:0x006d, B:54:0x0071, B:55:0x0075, B:56:0x0076, B:59:0x007e, B:60:0x008d, B:65:0x0050), top: B:3:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x008b A[SYNTHETIC] */
    @Override // java.io.FilterInputStream, java.io.InputStream
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final synchronized int read(byte[] r9, int r10, int r11) throws java.io.IOException {
        /*
            r8 = this;
            r5 = -1
            monitor-enter(r8)
            byte[] r1 = r8.buf     // Catch: java.lang.Throwable -> Lb
            if (r1 != 0) goto Le
            java.io.IOException r5 = streamClosed()     // Catch: java.lang.Throwable -> Lb
            throw r5     // Catch: java.lang.Throwable -> Lb
        Lb:
            r5 = move-exception
            monitor-exit(r8)
            throw r5
        Le:
            if (r11 != 0) goto L13
            r0 = 0
        L11:
            monitor-exit(r8)
            return r0
        L13:
            java.io.InputStream r2 = r8.in     // Catch: java.lang.Throwable -> Lb
            if (r2 != 0) goto L1c
            java.io.IOException r5 = streamClosed()     // Catch: java.lang.Throwable -> Lb
            throw r5     // Catch: java.lang.Throwable -> Lb
        L1c:
            int r6 = r8.pos     // Catch: java.lang.Throwable -> Lb
            int r7 = r8.count     // Catch: java.lang.Throwable -> Lb
            if (r6 >= r7) goto L57
            int r6 = r8.count     // Catch: java.lang.Throwable -> Lb
            int r7 = r8.pos     // Catch: java.lang.Throwable -> Lb
            int r6 = r6 - r7
            if (r6 < r11) goto L50
            r0 = r11
        L2a:
            int r6 = r8.pos     // Catch: java.lang.Throwable -> Lb
            java.lang.System.arraycopy(r1, r6, r9, r10, r0)     // Catch: java.lang.Throwable -> Lb
            int r6 = r8.pos     // Catch: java.lang.Throwable -> Lb
            int r6 = r6 + r0
            r8.pos = r6     // Catch: java.lang.Throwable -> Lb
            if (r0 == r11) goto L11
            int r6 = r2.available()     // Catch: java.lang.Throwable -> Lb
            if (r6 == 0) goto L11
            int r10 = r10 + r0
            int r4 = r11 - r0
        L3f:
            int r6 = r8.markpos     // Catch: java.lang.Throwable -> Lb
            if (r6 != r5) goto L5c
            int r6 = r1.length     // Catch: java.lang.Throwable -> Lb
            if (r4 < r6) goto L5c
            int r3 = r2.read(r9, r10, r4)     // Catch: java.lang.Throwable -> Lb
            if (r3 != r5) goto L88
            if (r4 != r11) goto L59
            r0 = r5
            goto L11
        L50:
            int r6 = r8.count     // Catch: java.lang.Throwable -> Lb
            int r7 = r8.pos     // Catch: java.lang.Throwable -> Lb
            int r0 = r6 - r7
            goto L2a
        L57:
            r4 = r11
            goto L3f
        L59:
            int r0 = r11 - r4
            goto L11
        L5c:
            int r6 = r8.fillbuf(r2, r1)     // Catch: java.lang.Throwable -> Lb
            if (r6 != r5) goto L69
            if (r4 != r11) goto L66
            r0 = r5
            goto L11
        L66:
            int r0 = r11 - r4
            goto L11
        L69:
            byte[] r6 = r8.buf     // Catch: java.lang.Throwable -> Lb
            if (r1 == r6) goto L76
            byte[] r1 = r8.buf     // Catch: java.lang.Throwable -> Lb
            if (r1 != 0) goto L76
            java.io.IOException r5 = streamClosed()     // Catch: java.lang.Throwable -> Lb
            throw r5     // Catch: java.lang.Throwable -> Lb
        L76:
            int r6 = r8.count     // Catch: java.lang.Throwable -> Lb
            int r7 = r8.pos     // Catch: java.lang.Throwable -> Lb
            int r6 = r6 - r7
            if (r6 < r4) goto L8d
            r3 = r4
        L7e:
            int r6 = r8.pos     // Catch: java.lang.Throwable -> Lb
            java.lang.System.arraycopy(r1, r6, r9, r10, r3)     // Catch: java.lang.Throwable -> Lb
            int r6 = r8.pos     // Catch: java.lang.Throwable -> Lb
            int r6 = r6 + r3
            r8.pos = r6     // Catch: java.lang.Throwable -> Lb
        L88:
            int r4 = r4 - r3
            if (r4 != 0) goto L94
            r0 = r11
            goto L11
        L8d:
            int r6 = r8.count     // Catch: java.lang.Throwable -> Lb
            int r7 = r8.pos     // Catch: java.lang.Throwable -> Lb
            int r3 = r6 - r7
            goto L7e
        L94:
            int r6 = r2.available()     // Catch: java.lang.Throwable -> Lb
            if (r6 != 0) goto L9e
            int r0 = r11 - r4
            goto L11
        L9e:
            int r10 = r10 + r3
            goto L3f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream.read(byte[], int, int):int");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final synchronized void reset() throws IOException {
        if (this.buf == null) {
            throw new IOException("Stream is closed");
        }
        if (-1 == this.markpos) {
            throw new InvalidMarkException("Mark has been invalidated");
        }
        this.pos = this.markpos;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final synchronized long skip(long byteCount) throws IOException {
        byte[] localBuf = this.buf;
        InputStream localIn = this.in;
        if (localBuf == null) {
            throw streamClosed();
        }
        if (byteCount < 1) {
            byteCount = 0;
        } else {
            if (localIn == null) {
                throw streamClosed();
            }
            if (this.count - this.pos >= byteCount) {
                this.pos = (int) (this.pos + byteCount);
            } else {
                long read = this.count - this.pos;
                this.pos = this.count;
                if (this.markpos != -1 && byteCount <= this.marklimit) {
                    if (fillbuf(localIn, localBuf) == -1) {
                        byteCount = read;
                    } else if (this.count - this.pos >= byteCount - read) {
                        this.pos = (int) (this.pos + (byteCount - read));
                    } else {
                        this.pos = this.count;
                        byteCount = (this.count + read) - this.pos;
                    }
                } else {
                    byteCount = read + localIn.skip(byteCount - read);
                }
            }
        }
        return byteCount;
    }

    /* loaded from: classes.dex */
    public static class InvalidMarkException extends RuntimeException {
        public InvalidMarkException(String detailMessage) {
            super(detailMessage);
        }
    }
}
