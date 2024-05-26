package com.fasterxml.jackson.core.io;

import com.nuance.swype.input.R;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/* loaded from: classes.dex */
public final class UTF8Writer extends Writer {
    protected final IOContext _context;
    OutputStream _out;
    byte[] _outBuffer;
    final int _outBufferEnd;
    int _surrogate = 0;
    int _outPtr = 0;

    public UTF8Writer(IOContext ctxt, OutputStream out) {
        this._context = ctxt;
        this._out = out;
        this._outBuffer = ctxt.allocWriteEncodingBuffer();
        this._outBufferEnd = this._outBuffer.length - 4;
    }

    @Override // java.io.Writer, java.lang.Appendable
    public final Writer append(char c) throws IOException {
        write(c);
        return this;
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        if (this._out != null) {
            if (this._outPtr > 0) {
                this._out.write(this._outBuffer, 0, this._outPtr);
                this._outPtr = 0;
            }
            OutputStream out = this._out;
            this._out = null;
            byte[] buf = this._outBuffer;
            if (buf != null) {
                this._outBuffer = null;
                this._context.releaseWriteEncodingBuffer(buf);
            }
            out.close();
            int code = this._surrogate;
            this._surrogate = 0;
            if (code > 0) {
                throwIllegal(code);
            }
        }
    }

    @Override // java.io.Writer, java.io.Flushable
    public final void flush() throws IOException {
        if (this._out != null) {
            if (this._outPtr > 0) {
                this._out.write(this._outBuffer, 0, this._outPtr);
                this._outPtr = 0;
            }
            this._out.flush();
        }
    }

    @Override // java.io.Writer
    public final void write(char[] cbuf) throws IOException {
        write(cbuf, 0, cbuf.length);
    }

    @Override // java.io.Writer
    public final void write(char[] cbuf, int off, int len) throws IOException {
        int outPtr;
        if (len < 2) {
            if (len == 1) {
                write(cbuf[off]);
                return;
            }
            return;
        }
        if (this._surrogate > 0) {
            char second = cbuf[off];
            len--;
            write(convertSurrogate(second));
            off++;
        }
        int outPtr2 = this._outPtr;
        byte[] outBuf = this._outBuffer;
        int outBufLast = this._outBufferEnd;
        int len2 = len + off;
        int off2 = off;
        while (true) {
            if (off2 < len2) {
                if (outPtr2 >= outBufLast) {
                    this._out.write(outBuf, 0, outPtr2);
                    outPtr2 = 0;
                }
                int off3 = off2 + 1;
                char c = cbuf[off2];
                if (c < 128) {
                    outPtr = outPtr2 + 1;
                    outBuf[outPtr2] = (byte) c;
                    int maxInCount = len2 - off3;
                    int maxOutCount = outBufLast - outPtr;
                    if (maxInCount > maxOutCount) {
                        maxInCount = maxOutCount;
                    }
                    int maxInCount2 = maxInCount + off3;
                    off2 = off3;
                    while (off2 < maxInCount2) {
                        int off4 = off2 + 1;
                        c = cbuf[off2];
                        if (c < 128) {
                            outBuf[outPtr] = (byte) c;
                            outPtr++;
                            off2 = off4;
                        } else {
                            off2 = off4;
                        }
                    }
                    outPtr2 = outPtr;
                } else {
                    outPtr = outPtr2;
                    off2 = off3;
                }
                if (c < 2048) {
                    int outPtr3 = outPtr + 1;
                    outBuf[outPtr] = (byte) ((c >> 6) | R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop);
                    outBuf[outPtr3] = (byte) ((c & '?') | 128);
                    outPtr2 = outPtr3 + 1;
                } else if (c < 55296 || c > 57343) {
                    int outPtr4 = outPtr + 1;
                    outBuf[outPtr] = (byte) ((c >> '\f') | 224);
                    int outPtr5 = outPtr4 + 1;
                    outBuf[outPtr4] = (byte) (((c >> 6) & 63) | 128);
                    outPtr2 = outPtr5 + 1;
                    outBuf[outPtr5] = (byte) ((c & '?') | 128);
                } else {
                    if (c > 56319) {
                        this._outPtr = outPtr;
                        throwIllegal(c);
                    }
                    this._surrogate = c;
                    if (off2 >= len2) {
                        outPtr2 = outPtr;
                        break;
                    }
                    int off5 = off2 + 1;
                    int c2 = convertSurrogate(cbuf[off2]);
                    if (c2 > 1114111) {
                        this._outPtr = outPtr;
                        throwIllegal(c2);
                    }
                    int outPtr6 = outPtr + 1;
                    outBuf[outPtr] = (byte) ((c2 >> 18) | R.styleable.ThemeTemplate_pressableBackgroundHighlight);
                    int outPtr7 = outPtr6 + 1;
                    outBuf[outPtr6] = (byte) (((c2 >> 12) & 63) | 128);
                    int outPtr8 = outPtr7 + 1;
                    outBuf[outPtr7] = (byte) (((c2 >> 6) & 63) | 128);
                    outBuf[outPtr8] = (byte) ((c2 & 63) | 128);
                    outPtr2 = outPtr8 + 1;
                    off2 = off5;
                }
            } else {
                break;
            }
        }
        this._outPtr = outPtr2;
    }

    @Override // java.io.Writer
    public final void write(int c) throws IOException {
        int ptr;
        if (this._surrogate > 0) {
            c = convertSurrogate(c);
        } else if (c >= 55296 && c <= 57343) {
            if (c > 56319) {
                throwIllegal(c);
            }
            this._surrogate = c;
            return;
        }
        if (this._outPtr >= this._outBufferEnd) {
            this._out.write(this._outBuffer, 0, this._outPtr);
            this._outPtr = 0;
        }
        if (c < 128) {
            byte[] bArr = this._outBuffer;
            int i = this._outPtr;
            this._outPtr = i + 1;
            bArr[i] = (byte) c;
            return;
        }
        int ptr2 = this._outPtr;
        if (c < 2048) {
            int ptr3 = ptr2 + 1;
            this._outBuffer[ptr2] = (byte) ((c >> 6) | R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop);
            ptr = ptr3 + 1;
            this._outBuffer[ptr3] = (byte) ((c & 63) | 128);
        } else if (c <= 65535) {
            int ptr4 = ptr2 + 1;
            this._outBuffer[ptr2] = (byte) ((c >> 12) | 224);
            int ptr5 = ptr4 + 1;
            this._outBuffer[ptr4] = (byte) (((c >> 6) & 63) | 128);
            this._outBuffer[ptr5] = (byte) ((c & 63) | 128);
            ptr = ptr5 + 1;
        } else {
            if (c > 1114111) {
                throwIllegal(c);
            }
            int ptr6 = ptr2 + 1;
            this._outBuffer[ptr2] = (byte) ((c >> 18) | R.styleable.ThemeTemplate_pressableBackgroundHighlight);
            int ptr7 = ptr6 + 1;
            this._outBuffer[ptr6] = (byte) (((c >> 12) & 63) | 128);
            int ptr8 = ptr7 + 1;
            this._outBuffer[ptr7] = (byte) (((c >> 6) & 63) | 128);
            ptr = ptr8 + 1;
            this._outBuffer[ptr8] = (byte) ((c & 63) | 128);
        }
        this._outPtr = ptr;
    }

    @Override // java.io.Writer
    public final void write(String str) throws IOException {
        write(str, 0, str.length());
    }

    @Override // java.io.Writer
    public final void write(String str, int off, int len) throws IOException {
        int outPtr;
        if (len < 2) {
            if (len == 1) {
                write(str.charAt(off));
                return;
            }
            return;
        }
        if (this._surrogate > 0) {
            char second = str.charAt(off);
            len--;
            write(convertSurrogate(second));
            off++;
        }
        int outPtr2 = this._outPtr;
        byte[] outBuf = this._outBuffer;
        int outBufLast = this._outBufferEnd;
        int len2 = len + off;
        int off2 = off;
        while (true) {
            if (off2 < len2) {
                if (outPtr2 >= outBufLast) {
                    this._out.write(outBuf, 0, outPtr2);
                    outPtr2 = 0;
                }
                int off3 = off2 + 1;
                int c = str.charAt(off2);
                if (c < 128) {
                    outPtr = outPtr2 + 1;
                    outBuf[outPtr2] = (byte) c;
                    int maxInCount = len2 - off3;
                    int maxOutCount = outBufLast - outPtr;
                    if (maxInCount > maxOutCount) {
                        maxInCount = maxOutCount;
                    }
                    int maxInCount2 = maxInCount + off3;
                    off2 = off3;
                    while (off2 < maxInCount2) {
                        int off4 = off2 + 1;
                        c = str.charAt(off2);
                        if (c < 128) {
                            outBuf[outPtr] = (byte) c;
                            outPtr++;
                            off2 = off4;
                        } else {
                            off2 = off4;
                        }
                    }
                    outPtr2 = outPtr;
                } else {
                    outPtr = outPtr2;
                    off2 = off3;
                }
                if (c < 2048) {
                    int outPtr3 = outPtr + 1;
                    outBuf[outPtr] = (byte) ((c >> 6) | R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop);
                    outBuf[outPtr3] = (byte) ((c & 63) | 128);
                    outPtr2 = outPtr3 + 1;
                } else if (c < 55296 || c > 57343) {
                    int outPtr4 = outPtr + 1;
                    outBuf[outPtr] = (byte) ((c >> 12) | 224);
                    int outPtr5 = outPtr4 + 1;
                    outBuf[outPtr4] = (byte) (((c >> 6) & 63) | 128);
                    outPtr2 = outPtr5 + 1;
                    outBuf[outPtr5] = (byte) ((c & 63) | 128);
                } else {
                    if (c > 56319) {
                        this._outPtr = outPtr;
                        throwIllegal(c);
                    }
                    this._surrogate = c;
                    if (off2 >= len2) {
                        outPtr2 = outPtr;
                        break;
                    }
                    int off5 = off2 + 1;
                    int c2 = convertSurrogate(str.charAt(off2));
                    if (c2 > 1114111) {
                        this._outPtr = outPtr;
                        throwIllegal(c2);
                    }
                    int outPtr6 = outPtr + 1;
                    outBuf[outPtr] = (byte) ((c2 >> 18) | R.styleable.ThemeTemplate_pressableBackgroundHighlight);
                    int outPtr7 = outPtr6 + 1;
                    outBuf[outPtr6] = (byte) (((c2 >> 12) & 63) | 128);
                    int outPtr8 = outPtr7 + 1;
                    outBuf[outPtr7] = (byte) (((c2 >> 6) & 63) | 128);
                    outBuf[outPtr8] = (byte) ((c2 & 63) | 128);
                    outPtr2 = outPtr8 + 1;
                    off2 = off5;
                }
            } else {
                break;
            }
        }
        this._outPtr = outPtr2;
    }

    private int convertSurrogate(int secondPart) throws IOException {
        int firstPart = this._surrogate;
        this._surrogate = 0;
        if (secondPart < 56320 || secondPart > 57343) {
            throw new IOException("Broken surrogate pair: first char 0x" + Integer.toHexString(firstPart) + ", second 0x" + Integer.toHexString(secondPart) + "; illegal combination");
        }
        return 65536 + ((firstPart - 55296) << 10) + (secondPart - 56320);
    }

    private static void throwIllegal(int code) throws IOException {
        if (code > 1114111) {
            throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output; max is 0x10FFFF as per RFC 4627");
        }
        if (code >= 55296) {
            if (code <= 56319) {
                throw new IOException("Unmatched first part of surrogate pair (0x" + Integer.toHexString(code) + ")");
            }
            throw new IOException("Unmatched second part of surrogate pair (0x" + Integer.toHexString(code) + ")");
        }
        throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output");
    }
}
