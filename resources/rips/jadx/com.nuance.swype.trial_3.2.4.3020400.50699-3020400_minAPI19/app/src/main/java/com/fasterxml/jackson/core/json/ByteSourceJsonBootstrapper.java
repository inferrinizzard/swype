package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.io.IOContext;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class ByteSourceJsonBootstrapper {
    protected final IOContext _context;
    protected final InputStream _in;
    protected final byte[] _inputBuffer;
    protected boolean _bigEndian = true;
    protected int _bytesPerChar = 0;
    private int _inputPtr = 0;
    private int _inputEnd = 0;
    protected int _inputProcessed = 0;
    private final boolean _bufferRecyclable = true;

    public ByteSourceJsonBootstrapper(IOContext ctxt, InputStream in) {
        this._context = ctxt;
        this._in = in;
        this._inputBuffer = ctxt.allocReadIOBuffer();
    }

    private boolean checkUTF16(int i16) {
        if ((65280 & i16) == 0) {
            this._bigEndian = true;
        } else {
            if ((i16 & 255) != 0) {
                return false;
            }
            this._bigEndian = false;
        }
        this._bytesPerChar = 2;
        return true;
    }

    private static void reportWeirdUCS4(String type) throws IOException {
        throw new CharConversionException("Unsupported UCS-4 endianness (" + type + ") detected");
    }

    private boolean ensureLoaded(int minimum) throws IOException {
        int count;
        int gotten = this._inputEnd - this._inputPtr;
        while (gotten < minimum) {
            if (this._in == null) {
                count = -1;
            } else {
                count = this._in.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
            }
            if (count <= 0) {
                return false;
            }
            this._inputEnd += count;
            gotten += count;
        }
        return true;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00dd  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0106  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x00a5  */
    /* JADX WARN: Removed duplicated region for block: B:7:0x003f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final com.fasterxml.jackson.core.JsonParser constructParser(int r16, com.fasterxml.jackson.core.ObjectCodec r17, com.fasterxml.jackson.core.sym.BytesToNameCanonicalizer r18, com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer r19, boolean r20, boolean r21) throws java.io.IOException, com.fasterxml.jackson.core.JsonParseException {
        /*
            Method dump skipped, instructions count: 514
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.ByteSourceJsonBootstrapper.constructParser(int, com.fasterxml.jackson.core.ObjectCodec, com.fasterxml.jackson.core.sym.BytesToNameCanonicalizer, com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer, boolean, boolean):com.fasterxml.jackson.core.JsonParser");
    }
}
