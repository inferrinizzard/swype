package com.bumptech.glide.util;

import com.nuance.connect.common.Integers;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class MarkEnforcingInputStream extends FilterInputStream {
    private int availableBytes;

    public MarkEnforcingInputStream(InputStream in) {
        super(in);
        this.availableBytes = Integers.STATUS_SUCCESS;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final void mark(int readlimit) {
        super.mark(readlimit);
        this.availableBytes = readlimit;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final int read() throws IOException {
        if (getBytesToRead(1L) == -1) {
            return -1;
        }
        int read = super.read();
        updateAvailableBytesAfterRead(1L);
        return read;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int toRead = (int) getBytesToRead(byteCount);
        if (toRead == -1) {
            return -1;
        }
        int read = super.read(buffer, byteOffset, toRead);
        updateAvailableBytesAfterRead(read);
        return read;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final void reset() throws IOException {
        super.reset();
        this.availableBytes = Integers.STATUS_SUCCESS;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final long skip(long byteCount) throws IOException {
        long toSkip = getBytesToRead(byteCount);
        if (toSkip == -1) {
            return -1L;
        }
        long read = super.skip(toSkip);
        updateAvailableBytesAfterRead(read);
        return read;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final int available() throws IOException {
        return this.availableBytes == Integer.MIN_VALUE ? super.available() : Math.min(this.availableBytes, super.available());
    }

    private long getBytesToRead(long targetByteCount) {
        if (this.availableBytes == 0) {
            return -1L;
        }
        if (this.availableBytes != Integer.MIN_VALUE && targetByteCount > this.availableBytes) {
            return this.availableBytes;
        }
        return targetByteCount;
    }

    private void updateAvailableBytesAfterRead(long bytesRead) {
        if (this.availableBytes != Integer.MIN_VALUE && bytesRead != -1) {
            this.availableBytes = (int) (this.availableBytes - bytesRead);
        }
    }
}
