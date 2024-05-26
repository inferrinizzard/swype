package com.google.api.client.repackaged.org.apache.commons.codec.binary;

/* loaded from: classes.dex */
public abstract class BaseNCodec {
    protected byte[] buffer;
    private final int chunkSeparatorLength;
    protected int currentLinePos;
    protected boolean eof;
    protected final int lineLength;
    protected int modulus;
    protected int pos;
    private int readPos;
    protected final byte PAD = 61;
    private final int unencodedBlockSize = 3;
    private final int encodedBlockSize = 4;

    abstract void decode(byte[] bArr, int i, int i2);

    abstract void encode(byte[] bArr, int i, int i2);

    protected abstract boolean isInAlphabet(byte b);

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseNCodec(int lineLength, int chunkSeparatorLength) {
        this.lineLength = (lineLength <= 0 || chunkSeparatorLength <= 0) ? 0 : (lineLength / 4) * 4;
        this.chunkSeparatorLength = chunkSeparatorLength;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void ensureBufferSize(int size) {
        if (this.buffer == null || this.buffer.length < this.pos + size) {
            if (this.buffer == null) {
                this.buffer = new byte[8192];
                this.pos = 0;
                this.readPos = 0;
            } else {
                byte[] bArr = new byte[this.buffer.length * 2];
                System.arraycopy(this.buffer, 0, bArr, 0, this.buffer.length);
                this.buffer = bArr;
            }
        }
    }

    private int readResults$1cf967b1(byte[] b, int bAvail) {
        if (this.buffer == null) {
            return this.eof ? -1 : 0;
        }
        int len = Math.min(this.buffer != null ? this.pos - this.readPos : 0, bAvail);
        System.arraycopy(this.buffer, this.readPos, b, 0, len);
        this.readPos += len;
        if (this.readPos >= this.pos) {
            this.buffer = null;
            return len;
        }
        return len;
    }

    private void reset() {
        this.buffer = null;
        this.pos = 0;
        this.readPos = 0;
        this.currentLinePos = 0;
        this.modulus = 0;
        this.eof = false;
    }

    public final byte[] decode(byte[] pArray) {
        reset();
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        decode(pArray, 0, pArray.length);
        decode(pArray, 0, -1);
        byte[] result = new byte[this.pos];
        readResults$1cf967b1(result, result.length);
        return result;
    }

    public final byte[] encode(byte[] pArray) {
        reset();
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        encode(pArray, 0, pArray.length);
        encode(pArray, 0, -1);
        byte[] buf = new byte[this.pos - this.readPos];
        readResults$1cf967b1(buf, buf.length);
        return buf;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean containsAlphabetOrPad(byte[] arrayOctet) {
        if (arrayOctet == null) {
            return false;
        }
        for (byte element : arrayOctet) {
            if (61 == element || isInAlphabet(element)) {
                return true;
            }
        }
        return false;
    }

    public final long getEncodedLength(byte[] pArray) {
        long len = (((pArray.length + this.unencodedBlockSize) - 1) / this.unencodedBlockSize) * this.encodedBlockSize;
        if (this.lineLength > 0) {
            return len + ((((this.lineLength + len) - 1) / this.lineLength) * this.chunkSeparatorLength);
        }
        return len;
    }
}
