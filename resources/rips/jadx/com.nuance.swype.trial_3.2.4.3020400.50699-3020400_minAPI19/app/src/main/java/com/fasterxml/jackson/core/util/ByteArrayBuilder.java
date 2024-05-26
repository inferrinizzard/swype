package com.fasterxml.jackson.core.util;

import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

/* loaded from: classes.dex */
public final class ByteArrayBuilder extends OutputStream {
    private static final byte[] NO_BYTES = new byte[0];
    private final BufferRecycler _bufferRecycler;
    public byte[] _currBlock;
    public int _currBlockPtr;
    private final LinkedList<byte[]> _pastBlocks;
    private int _pastLen;

    public ByteArrayBuilder() {
        this((byte) 0);
    }

    public ByteArrayBuilder(byte b) {
        this((char) 0);
    }

    private ByteArrayBuilder(char c) {
        this._pastBlocks = new LinkedList<>();
        this._bufferRecycler = null;
        this._currBlock = new byte[500];
    }

    public final void reset() {
        this._pastLen = 0;
        this._currBlockPtr = 0;
        if (!this._pastBlocks.isEmpty()) {
            this._pastBlocks.clear();
        }
    }

    public final byte[] toByteArray() {
        int totalLen = this._pastLen + this._currBlockPtr;
        if (totalLen == 0) {
            return NO_BYTES;
        }
        byte[] result = new byte[totalLen];
        int offset = 0;
        Iterator i$ = this._pastBlocks.iterator();
        while (i$.hasNext()) {
            byte[] block = i$.next();
            int len = block.length;
            System.arraycopy(block, 0, result, offset, len);
            offset += len;
        }
        System.arraycopy(this._currBlock, 0, result, offset, this._currBlockPtr);
        int offset2 = offset + this._currBlockPtr;
        if (offset2 != totalLen) {
            throw new RuntimeException("Internal error: total len assumed to be " + totalLen + ", copied " + offset2 + " bytes");
        }
        if (!this._pastBlocks.isEmpty()) {
            reset();
            return result;
        }
        return result;
    }

    public final byte[] finishCurrentSegment() {
        _allocMore();
        return this._currBlock;
    }

    @Override // java.io.OutputStream
    public final void write(byte[] b) {
        write(b, 0, b.length);
    }

    @Override // java.io.OutputStream
    public final void write(byte[] b, int off, int len) {
        while (true) {
            int toCopy = Math.min(this._currBlock.length - this._currBlockPtr, len);
            if (toCopy > 0) {
                System.arraycopy(b, off, this._currBlock, this._currBlockPtr, toCopy);
                off += toCopy;
                this._currBlockPtr += toCopy;
                len -= toCopy;
            }
            if (len > 0) {
                _allocMore();
            } else {
                return;
            }
        }
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public final void flush() {
    }

    private void _allocMore() {
        this._pastLen += this._currBlock.length;
        int newSize = Math.max(this._pastLen >> 1, 1000);
        if (newSize > 262144) {
            newSize = HardKeyboardManager.META_META_RIGHT_ON;
        }
        this._pastBlocks.add(this._currBlock);
        this._currBlock = new byte[newSize];
        this._currBlockPtr = 0;
    }

    @Override // java.io.OutputStream
    public final void write(int b) {
        if (this._currBlockPtr >= this._currBlock.length) {
            _allocMore();
        }
        byte[] bArr = this._currBlock;
        int i = this._currBlockPtr;
        this._currBlockPtr = i + 1;
        bArr[i] = (byte) b;
    }
}
