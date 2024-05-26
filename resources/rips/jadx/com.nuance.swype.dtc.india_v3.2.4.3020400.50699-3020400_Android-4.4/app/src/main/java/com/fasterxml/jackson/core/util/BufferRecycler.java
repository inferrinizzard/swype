package com.fasterxml.jackson.core.util;

/* loaded from: classes.dex */
public final class BufferRecycler {
    protected final byte[][] _byteBuffers = new byte[ByteBufferType.values().length];
    protected final char[][] _charBuffers = new char[CharBufferType.values().length];

    /* loaded from: classes.dex */
    public enum ByteBufferType {
        READ_IO_BUFFER(4000),
        WRITE_ENCODING_BUFFER(4000),
        WRITE_CONCAT_BUFFER(2000),
        BASE64_CODEC_BUFFER(2000);

        protected final int size;

        ByteBufferType(int size) {
            this.size = size;
        }
    }

    /* loaded from: classes.dex */
    public enum CharBufferType {
        TOKEN_BUFFER(2000),
        CONCAT_BUFFER(2000),
        TEXT_BUFFER(200),
        NAME_COPY_BUFFER(200);

        protected final int size;

        CharBufferType(int size) {
            this.size = size;
        }
    }

    public final byte[] allocByteBuffer(ByteBufferType type) {
        int ix = type.ordinal();
        byte[] buffer = this._byteBuffers[ix];
        if (buffer != null) {
            this._byteBuffers[ix] = null;
            return buffer;
        }
        return new byte[type.size];
    }

    public final void releaseByteBuffer(ByteBufferType type, byte[] buffer) {
        this._byteBuffers[type.ordinal()] = buffer;
    }

    public final char[] allocCharBuffer(CharBufferType type) {
        return allocCharBuffer(type, 0);
    }

    public final char[] allocCharBuffer(CharBufferType type, int minSize) {
        if (type.size > minSize) {
            minSize = type.size;
        }
        int ix = type.ordinal();
        char[] buffer = this._charBuffers[ix];
        if (buffer != null && buffer.length >= minSize) {
            this._charBuffers[ix] = null;
            return buffer;
        }
        return new char[minSize];
    }

    public final void releaseCharBuffer(CharBufferType type, char[] buffer) {
        this._charBuffers[type.ordinal()] = buffer;
    }
}
