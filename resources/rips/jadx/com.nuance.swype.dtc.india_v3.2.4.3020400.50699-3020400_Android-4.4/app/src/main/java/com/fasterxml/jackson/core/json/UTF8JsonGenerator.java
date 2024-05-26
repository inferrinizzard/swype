package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.NumberOutput;
import com.nuance.nmsp.client.sdk.common.protocols.ProtocolDefines;
import com.nuance.swype.input.R;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: classes.dex */
public final class UTF8JsonGenerator extends JsonGeneratorImpl {
    protected boolean _bufferRecyclable;
    protected char[] _charBuffer;
    protected final int _charBufferLength;
    protected byte[] _outputBuffer;
    protected final int _outputEnd;
    protected final int _outputMaxContiguous;
    protected final OutputStream _outputStream;
    protected int _outputTail;
    static final byte[] HEX_CHARS = CharTypes.copyHexBytes();
    private static final byte[] NULL_BYTES = {110, 117, 108, 108};
    private static final byte[] TRUE_BYTES = {116, 114, 117, 101};
    private static final byte[] FALSE_BYTES = {102, 97, 108, 115, 101};

    public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out) {
        super(ctxt, features, codec);
        this._outputTail = 0;
        this._outputStream = out;
        this._bufferRecyclable = true;
        this._outputBuffer = ctxt.allocWriteEncodingBuffer();
        this._outputEnd = this._outputBuffer.length;
        this._outputMaxContiguous = this._outputEnd >> 3;
        this._charBuffer = ctxt.allocConcatBuffer();
        this._charBufferLength = this._charBuffer.length;
        if (isEnabled(JsonGenerator.Feature.ESCAPE_NON_ASCII)) {
            setHighestNonEscapedChar$1549bd1();
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeFieldName(String name) throws IOException, JsonGenerationException {
        int status = this._writeContext.writeFieldName(name);
        if (status == 4) {
            _reportError("Can not write a field name, expecting a value");
        }
        if (this._cfgPrettyPrinter == null) {
            if (status == 1) {
                if (this._outputTail >= this._outputEnd) {
                    _flushBuffer();
                }
                byte[] bArr = this._outputBuffer;
                int i = this._outputTail;
                this._outputTail = i + 1;
                bArr[i] = 44;
            }
            if (!isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
                _writeStringSegments(name);
                return;
            }
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr2 = this._outputBuffer;
            int i2 = this._outputTail;
            this._outputTail = i2 + 1;
            bArr2[i2] = ProtocolDefines.XMODE_VERSION_BCP;
            int length = name.length();
            if (length <= this._charBufferLength) {
                name.getChars(0, length, this._charBuffer, 0);
                if (length <= this._outputMaxContiguous) {
                    if (this._outputTail + length > this._outputEnd) {
                        _flushBuffer();
                    }
                    _writeStringSegment(this._charBuffer, 0, length);
                } else {
                    _writeStringSegments(this._charBuffer, 0, length);
                }
            } else {
                _writeStringSegments(name);
            }
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr3 = this._outputBuffer;
            int i3 = this._outputTail;
            this._outputTail = i3 + 1;
            bArr3[i3] = ProtocolDefines.XMODE_VERSION_BCP;
            return;
        }
        if (status == 1) {
            this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
        } else {
            this._cfgPrettyPrinter.beforeObjectEntries(this);
        }
        if (isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr4 = this._outputBuffer;
            int i4 = this._outputTail;
            this._outputTail = i4 + 1;
            bArr4[i4] = ProtocolDefines.XMODE_VERSION_BCP;
            int length2 = name.length();
            if (length2 <= this._charBufferLength) {
                name.getChars(0, length2, this._charBuffer, 0);
                if (length2 <= this._outputMaxContiguous) {
                    if (this._outputTail + length2 > this._outputEnd) {
                        _flushBuffer();
                    }
                    _writeStringSegment(this._charBuffer, 0, length2);
                } else {
                    _writeStringSegments(this._charBuffer, 0, length2);
                }
            } else {
                _writeStringSegments(name);
            }
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr5 = this._outputBuffer;
            int i5 = this._outputTail;
            this._outputTail = i5 + 1;
            bArr5[i5] = ProtocolDefines.XMODE_VERSION_BCP;
            return;
        }
        _writeStringSegments(name);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeStartArray() throws IOException, JsonGenerationException {
        _verifyValueWrite("start an array");
        this._writeContext = this._writeContext.createChildArrayContext();
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartArray(this);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = 91;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeEndArray() throws IOException, JsonGenerationException {
        if (!this._writeContext.inArray()) {
            _reportError("Current context not an ARRAY but " + this._writeContext.getTypeDesc());
        }
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeEndArray(this, this._writeContext._index + 1);
        } else {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            bArr[i] = 93;
        }
        this._writeContext = this._writeContext.getParent();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeStartObject() throws IOException, JsonGenerationException {
        _verifyValueWrite("start an object");
        this._writeContext = this._writeContext.createChildObjectContext();
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartObject(this);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = 123;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeEndObject() throws IOException, JsonGenerationException {
        if (!this._writeContext.inObject()) {
            _reportError("Current context not an object but " + this._writeContext.getTypeDesc());
        }
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeEndObject(this, this._writeContext._index + 1);
        } else {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            bArr[i] = 125;
        }
        this._writeContext = this._writeContext.getParent();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeString(String text) throws IOException, JsonGenerationException {
        _verifyValueWrite("write text value");
        if (text == null) {
            _writeNull();
            return;
        }
        int len = text.length();
        if (len <= this._charBufferLength) {
            text.getChars(0, len, this._charBuffer, 0);
            if (len <= this._outputMaxContiguous) {
                if (this._outputTail + len >= this._outputEnd) {
                    _flushBuffer();
                }
                byte[] bArr = this._outputBuffer;
                int i = this._outputTail;
                this._outputTail = i + 1;
                bArr[i] = ProtocolDefines.XMODE_VERSION_BCP;
                _writeStringSegment(this._charBuffer, 0, len);
                if (this._outputTail >= this._outputEnd) {
                    _flushBuffer();
                }
                byte[] bArr2 = this._outputBuffer;
                int i2 = this._outputTail;
                this._outputTail = i2 + 1;
                bArr2[i2] = ProtocolDefines.XMODE_VERSION_BCP;
                return;
            }
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr3 = this._outputBuffer;
            int i3 = this._outputTail;
            this._outputTail = i3 + 1;
            bArr3[i3] = ProtocolDefines.XMODE_VERSION_BCP;
            _writeStringSegments(this._charBuffer, 0, len);
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr4 = this._outputBuffer;
            int i4 = this._outputTail;
            this._outputTail = i4 + 1;
            bArr4[i4] = ProtocolDefines.XMODE_VERSION_BCP;
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr5 = this._outputBuffer;
        int i5 = this._outputTail;
        this._outputTail = i5 + 1;
        bArr5[i5] = ProtocolDefines.XMODE_VERSION_BCP;
        _writeStringSegments(text);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr6 = this._outputBuffer;
        int i6 = this._outputTail;
        this._outputTail = i6 + 1;
        bArr6[i6] = ProtocolDefines.XMODE_VERSION_BCP;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeRaw(String text) throws IOException, JsonGenerationException {
        int start = 0;
        int len = text.length();
        while (len > 0) {
            char[] buf = this._charBuffer;
            int blen = buf.length;
            int len2 = len < blen ? len : blen;
            text.getChars(start, start + len2, buf, 0);
            writeRaw(buf, 0, len2);
            start += len2;
            len -= len2;
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeRaw(SerializableString text) throws IOException, JsonGenerationException {
        byte[] raw = text.asUnquotedUTF8();
        if (raw.length > 0) {
            _writeBytes(raw);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeRaw(char[] cbuf, int offset, int len) throws IOException, JsonGenerationException {
        int len3 = len + len + len;
        if (this._outputTail + len3 > this._outputEnd) {
            if (this._outputEnd < len3) {
                int i = 0;
                int i2 = this._outputEnd;
                byte[] bArr = this._outputBuffer;
                while (i < len) {
                    do {
                        char c = cbuf[i];
                        if (c < 128) {
                            if (this._outputTail >= i2) {
                                _flushBuffer();
                            }
                            int i3 = this._outputTail;
                            this._outputTail = i3 + 1;
                            bArr[i3] = (byte) c;
                            i++;
                        } else {
                            if (this._outputTail + 3 >= this._outputEnd) {
                                _flushBuffer();
                            }
                            int i4 = i + 1;
                            char c2 = cbuf[i];
                            if (c2 < 2048) {
                                int i5 = this._outputTail;
                                this._outputTail = i5 + 1;
                                bArr[i5] = (byte) ((c2 >> 6) | R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop);
                                int i6 = this._outputTail;
                                this._outputTail = i6 + 1;
                                bArr[i6] = (byte) ((c2 & '?') | 128);
                                i = i4;
                            } else {
                                _outputRawMultiByteChar(c2, cbuf, i4, len);
                                i = i4;
                            }
                        }
                    } while (i < len);
                    return;
                }
                return;
            }
            _flushBuffer();
        }
        int len2 = len + 0;
        while (offset < len2) {
            do {
                char c3 = cbuf[offset];
                if (c3 <= 127) {
                    byte[] bArr2 = this._outputBuffer;
                    int i7 = this._outputTail;
                    this._outputTail = i7 + 1;
                    bArr2[i7] = (byte) c3;
                    offset++;
                } else {
                    int offset2 = offset + 1;
                    char ch = cbuf[offset];
                    if (ch < 2048) {
                        byte[] bArr3 = this._outputBuffer;
                        int i8 = this._outputTail;
                        this._outputTail = i8 + 1;
                        bArr3[i8] = (byte) ((ch >> 6) | R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop);
                        byte[] bArr4 = this._outputBuffer;
                        int i9 = this._outputTail;
                        this._outputTail = i9 + 1;
                        bArr4[i9] = (byte) ((ch & '?') | 128);
                        offset = offset2;
                    } else {
                        _outputRawMultiByteChar(ch, cbuf, offset2, len2);
                        offset = offset2;
                    }
                }
            } while (offset < len2);
            return;
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeRaw(char ch) throws IOException, JsonGenerationException {
        if (this._outputTail + 3 >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bbuf = this._outputBuffer;
        if (ch <= 127) {
            int i = this._outputTail;
            this._outputTail = i + 1;
            bbuf[i] = (byte) ch;
        } else {
            if (ch < 2048) {
                int i2 = this._outputTail;
                this._outputTail = i2 + 1;
                bbuf[i2] = (byte) ((ch >> 6) | R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop);
                int i3 = this._outputTail;
                this._outputTail = i3 + 1;
                bbuf[i3] = (byte) ((ch & '?') | 128);
                return;
            }
            _outputRawMultiByteChar(ch, null, 0, 0);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeNumber(int i) throws IOException, JsonGenerationException {
        _verifyValueWrite("write number");
        if (this._outputTail + 11 >= this._outputEnd) {
            _flushBuffer();
        }
        if (!this._cfgNumbersAsStrings) {
            this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
            return;
        }
        if (this._outputTail + 13 >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr[i2] = ProtocolDefines.XMODE_VERSION_BCP;
        this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
        byte[] bArr2 = this._outputBuffer;
        int i3 = this._outputTail;
        this._outputTail = i3 + 1;
        bArr2[i3] = ProtocolDefines.XMODE_VERSION_BCP;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeNumber(long l) throws IOException, JsonGenerationException {
        _verifyValueWrite("write number");
        if (!this._cfgNumbersAsStrings) {
            if (this._outputTail + 21 >= this._outputEnd) {
                _flushBuffer();
            }
            this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
            return;
        }
        if (this._outputTail + 23 >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = ProtocolDefines.XMODE_VERSION_BCP;
        this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = ProtocolDefines.XMODE_VERSION_BCP;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeNumber(BigInteger value) throws IOException, JsonGenerationException {
        _verifyValueWrite("write number");
        if (value == null) {
            _writeNull();
        } else if (this._cfgNumbersAsStrings) {
            _writeQuotedRaw(value);
        } else {
            writeRaw(value.toString());
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeNumber(double d) throws IOException, JsonGenerationException {
        if (this._cfgNumbersAsStrings || ((Double.isNaN(d) || Double.isInfinite(d)) && isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))) {
            writeString(String.valueOf(d));
        } else {
            _verifyValueWrite("write number");
            writeRaw(String.valueOf(d));
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeNumber(float f) throws IOException, JsonGenerationException {
        if (this._cfgNumbersAsStrings || ((Float.isNaN(f) || Float.isInfinite(f)) && isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))) {
            writeString(String.valueOf(f));
        } else {
            _verifyValueWrite("write number");
            writeRaw(String.valueOf(f));
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeNumber(BigDecimal value) throws IOException, JsonGenerationException {
        _verifyValueWrite("write number");
        if (value == null) {
            _writeNull();
        } else if (this._cfgNumbersAsStrings) {
            _writeQuotedRaw(value);
        } else {
            writeRaw(value.toString());
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeNumber(String encodedValue) throws IOException, JsonGenerationException {
        _verifyValueWrite("write number");
        if (this._cfgNumbersAsStrings) {
            _writeQuotedRaw(encodedValue);
        } else {
            writeRaw(encodedValue);
        }
    }

    private void _writeQuotedRaw(Object value) throws IOException {
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = ProtocolDefines.XMODE_VERSION_BCP;
        writeRaw(value.toString());
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = ProtocolDefines.XMODE_VERSION_BCP;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeBoolean(boolean state) throws IOException, JsonGenerationException {
        _verifyValueWrite("write boolean value");
        if (this._outputTail + 5 >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] keyword = state ? TRUE_BYTES : FALSE_BYTES;
        int len = keyword.length;
        System.arraycopy(keyword, 0, this._outputBuffer, this._outputTail, len);
        this._outputTail += len;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeNull() throws IOException, JsonGenerationException {
        _verifyValueWrite("write null value");
        _writeNull();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.GeneratorBase
    public final void _verifyValueWrite(String typeMsg) throws IOException, JsonGenerationException {
        byte b;
        int status = this._writeContext.writeValue();
        if (status == 5) {
            _reportError("Can not " + typeMsg + ", expecting field name");
        }
        if (this._cfgPrettyPrinter == null) {
            switch (status) {
                case 1:
                    b = 44;
                    break;
                case 2:
                    b = 58;
                    break;
                case 3:
                    if (this._rootValueSeparator != null) {
                        byte[] raw = this._rootValueSeparator.asUnquotedUTF8();
                        if (raw.length > 0) {
                            _writeBytes(raw);
                            return;
                        }
                        return;
                    }
                    return;
                default:
                    return;
            }
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            this._outputBuffer[this._outputTail] = b;
            this._outputTail++;
            return;
        }
        switch (status) {
            case 0:
                if (this._writeContext.inArray()) {
                    this._cfgPrettyPrinter.beforeArrayValues(this);
                    return;
                } else {
                    if (this._writeContext.inObject()) {
                        this._cfgPrettyPrinter.beforeObjectEntries(this);
                        return;
                    }
                    return;
                }
            case 1:
                this._cfgPrettyPrinter.writeArrayValueSeparator(this);
                return;
            case 2:
                this._cfgPrettyPrinter.writeObjectFieldValueSeparator(this);
                return;
            case 3:
                this._cfgPrettyPrinter.writeRootValueSeparator(this);
                return;
            default:
                _cantHappen();
                return;
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator, java.io.Flushable
    public final void flush() throws IOException {
        _flushBuffer();
        if (this._outputStream != null && isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
            this._outputStream.flush();
        }
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator, java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        super.close();
        if (this._outputBuffer != null && isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)) {
            while (true) {
                JsonStreamContext ctxt = this._writeContext;
                if (ctxt.inArray()) {
                    writeEndArray();
                } else if (!ctxt.inObject()) {
                    break;
                } else {
                    writeEndObject();
                }
            }
        }
        _flushBuffer();
        if (this._outputStream != null) {
            if (this._ioContext.isResourceManaged() || isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
                this._outputStream.close();
            } else if (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
                this._outputStream.flush();
            }
        }
        _releaseBuffers();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.GeneratorBase
    public final void _releaseBuffers() {
        byte[] buf = this._outputBuffer;
        if (buf != null && this._bufferRecyclable) {
            this._outputBuffer = null;
            this._ioContext.releaseWriteEncodingBuffer(buf);
        }
        char[] cbuf = this._charBuffer;
        if (cbuf != null) {
            this._charBuffer = null;
            this._ioContext.releaseConcatBuffer(cbuf);
        }
    }

    private final void _writeBytes(byte[] bytes) throws IOException {
        int len = bytes.length;
        if (this._outputTail + len > this._outputEnd) {
            _flushBuffer();
            if (len > 512) {
                this._outputStream.write(bytes, 0, len);
                return;
            }
        }
        System.arraycopy(bytes, 0, this._outputBuffer, this._outputTail, len);
        this._outputTail += len;
    }

    private final void _writeStringSegments(String text) throws IOException, JsonGenerationException {
        int left = text.length();
        int offset = 0;
        char[] cbuf = this._charBuffer;
        while (left > 0) {
            int len = Math.min(this._outputMaxContiguous, left);
            text.getChars(offset, offset + len, cbuf, 0);
            if (this._outputTail + len > this._outputEnd) {
                _flushBuffer();
            }
            _writeStringSegment(cbuf, 0, len);
            offset += len;
            left -= len;
        }
    }

    private final void _writeStringSegments(char[] cbuf, int offset, int totalLen) throws IOException, JsonGenerationException {
        do {
            int len = Math.min(this._outputMaxContiguous, totalLen);
            if (this._outputTail + len > this._outputEnd) {
                _flushBuffer();
            }
            _writeStringSegment(cbuf, offset, len);
            offset += len;
            totalLen -= len;
        } while (totalLen > 0);
    }

    private final void _writeStringSegment(char[] cbuf, int offset, int len) throws IOException, JsonGenerationException {
        int outputPtr;
        char c;
        int len2 = len + offset;
        int outputPtr2 = this._outputTail;
        byte[] outputBuffer = this._outputBuffer;
        int[] escCodes = this._outputEscapes;
        while (true) {
            outputPtr = outputPtr2;
            if (offset >= len2 || (c = cbuf[offset]) > 127 || escCodes[c] != 0) {
                break;
            }
            outputPtr2 = outputPtr + 1;
            outputBuffer[outputPtr] = (byte) c;
            offset++;
        }
        this._outputTail = outputPtr;
        if (offset < len2) {
            if (this._characterEscapes == null) {
                if (this._maximumNonEscapedChar == 0) {
                    _writeStringSegment2(cbuf, offset, len2);
                    return;
                } else {
                    _writeStringSegmentASCII2(cbuf, offset, len2);
                    return;
                }
            }
            if (this._outputTail + ((len2 - offset) * 6) > this._outputEnd) {
                _flushBuffer();
            }
            int i = this._outputTail;
            byte[] bArr = this._outputBuffer;
            int[] iArr = this._outputEscapes;
            int i2 = this._maximumNonEscapedChar <= 0 ? 65535 : this._maximumNonEscapedChar;
            CharacterEscapes characterEscapes = this._characterEscapes;
            while (offset < len2) {
                int i3 = offset + 1;
                char c2 = cbuf[offset];
                if (c2 <= 127) {
                    if (iArr[c2] == 0) {
                        bArr[i] = (byte) c2;
                        i++;
                        offset = i3;
                    } else {
                        int i4 = iArr[c2];
                        if (i4 > 0) {
                            int i5 = i + 1;
                            bArr[i] = 92;
                            i = i5 + 1;
                            bArr[i5] = (byte) i4;
                            offset = i3;
                        } else if (i4 == -2) {
                            SerializableString escapeSequence$428277ea = characterEscapes.getEscapeSequence$428277ea();
                            if (escapeSequence$428277ea == null) {
                                _reportError("Invalid custom escape definitions; custom escape not found for character code 0x" + Integer.toHexString(c2) + ", although was supposed to have one");
                            }
                            i = _writeCustomEscape(bArr, i, escapeSequence$428277ea, len2 - i3);
                            offset = i3;
                        } else {
                            i = _writeGenericEscape(c2, i);
                            offset = i3;
                        }
                    }
                } else if (c2 > i2) {
                    i = _writeGenericEscape(c2, i);
                    offset = i3;
                } else {
                    SerializableString escapeSequence$428277ea2 = characterEscapes.getEscapeSequence$428277ea();
                    if (escapeSequence$428277ea2 != null) {
                        i = _writeCustomEscape(bArr, i, escapeSequence$428277ea2, len2 - i3);
                        offset = i3;
                    } else if (c2 <= 2047) {
                        int i6 = i + 1;
                        bArr[i] = (byte) ((c2 >> 6) | R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop);
                        i = i6 + 1;
                        bArr[i6] = (byte) ((c2 & '?') | 128);
                        offset = i3;
                    } else {
                        i = _outputMultiByteChar(c2, i);
                        offset = i3;
                    }
                }
            }
            this._outputTail = i;
        }
    }

    private final void _writeStringSegment2(char[] cbuf, int offset, int end) throws IOException, JsonGenerationException {
        if (this._outputTail + ((end - offset) * 6) > this._outputEnd) {
            _flushBuffer();
        }
        int outputPtr = this._outputTail;
        byte[] outputBuffer = this._outputBuffer;
        int[] escCodes = this._outputEscapes;
        int outputPtr2 = outputPtr;
        int offset2 = offset;
        while (offset2 < end) {
            int offset3 = offset2 + 1;
            char c = cbuf[offset2];
            if (c <= 127) {
                if (escCodes[c] == 0) {
                    outputBuffer[outputPtr2] = (byte) c;
                    outputPtr2++;
                    offset2 = offset3;
                } else {
                    int escape = escCodes[c];
                    if (escape > 0) {
                        int outputPtr3 = outputPtr2 + 1;
                        outputBuffer[outputPtr2] = 92;
                        outputPtr2 = outputPtr3 + 1;
                        outputBuffer[outputPtr3] = (byte) escape;
                        offset2 = offset3;
                    } else {
                        outputPtr2 = _writeGenericEscape(c, outputPtr2);
                        offset2 = offset3;
                    }
                }
            } else if (c <= 2047) {
                int outputPtr4 = outputPtr2 + 1;
                outputBuffer[outputPtr2] = (byte) ((c >> 6) | R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop);
                outputPtr2 = outputPtr4 + 1;
                outputBuffer[outputPtr4] = (byte) ((c & '?') | 128);
                offset2 = offset3;
            } else {
                outputPtr2 = _outputMultiByteChar(c, outputPtr2);
                offset2 = offset3;
            }
        }
        this._outputTail = outputPtr2;
    }

    private final void _writeStringSegmentASCII2(char[] cbuf, int offset, int end) throws IOException, JsonGenerationException {
        if (this._outputTail + ((end - offset) * 6) > this._outputEnd) {
            _flushBuffer();
        }
        int outputPtr = this._outputTail;
        byte[] outputBuffer = this._outputBuffer;
        int[] escCodes = this._outputEscapes;
        int maxUnescaped = this._maximumNonEscapedChar;
        int outputPtr2 = outputPtr;
        int offset2 = offset;
        while (offset2 < end) {
            int offset3 = offset2 + 1;
            char c = cbuf[offset2];
            if (c <= 127) {
                if (escCodes[c] == 0) {
                    outputBuffer[outputPtr2] = (byte) c;
                    outputPtr2++;
                    offset2 = offset3;
                } else {
                    int escape = escCodes[c];
                    if (escape > 0) {
                        int outputPtr3 = outputPtr2 + 1;
                        outputBuffer[outputPtr2] = 92;
                        outputPtr2 = outputPtr3 + 1;
                        outputBuffer[outputPtr3] = (byte) escape;
                        offset2 = offset3;
                    } else {
                        outputPtr2 = _writeGenericEscape(c, outputPtr2);
                        offset2 = offset3;
                    }
                }
            } else if (c > maxUnescaped) {
                outputPtr2 = _writeGenericEscape(c, outputPtr2);
                offset2 = offset3;
            } else if (c <= 2047) {
                int outputPtr4 = outputPtr2 + 1;
                outputBuffer[outputPtr2] = (byte) ((c >> 6) | R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop);
                outputPtr2 = outputPtr4 + 1;
                outputBuffer[outputPtr4] = (byte) ((c & '?') | 128);
                offset2 = offset3;
            } else {
                outputPtr2 = _outputMultiByteChar(c, outputPtr2);
                offset2 = offset3;
            }
        }
        this._outputTail = outputPtr2;
    }

    private int _writeCustomEscape(byte[] outputBuffer, int outputPtr, SerializableString esc, int remainingChars) throws IOException, JsonGenerationException {
        int i;
        byte[] raw = esc.asUnquotedUTF8();
        int len = raw.length;
        if (len > 6) {
            int i2 = this._outputEnd;
            int length = raw.length;
            if (outputPtr + length > i2) {
                this._outputTail = outputPtr;
                _flushBuffer();
                int i3 = this._outputTail;
                if (length > outputBuffer.length) {
                    this._outputStream.write(raw, 0, length);
                    return i3;
                }
                System.arraycopy(raw, 0, outputBuffer, i3, length);
                i = i3 + length;
            } else {
                i = outputPtr;
            }
            if ((remainingChars * 6) + i > i2) {
                _flushBuffer();
                return this._outputTail;
            }
            return i;
        }
        System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
        return outputPtr + len;
    }

    private int _outputRawMultiByteChar(int ch, char[] cbuf, int inputOffset, int inputLen) throws IOException {
        if (ch >= 55296 && ch <= 57343) {
            if (inputOffset >= inputLen) {
                _reportError("Split surrogate on writeRaw() input (last character)");
            }
            char c = cbuf[inputOffset];
            if (c < 56320 || c > 57343) {
                _reportError("Incomplete surrogate pair: first char 0x" + Integer.toHexString(ch) + ", second 0x" + Integer.toHexString(c));
            }
            int i = (c - 56320) + 65536 + ((ch - 55296) << 10);
            if (this._outputTail + 4 > this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr = this._outputBuffer;
            int i2 = this._outputTail;
            this._outputTail = i2 + 1;
            bArr[i2] = (byte) ((i >> 18) | R.styleable.ThemeTemplate_pressableBackgroundHighlight);
            int i3 = this._outputTail;
            this._outputTail = i3 + 1;
            bArr[i3] = (byte) (((i >> 12) & 63) | 128);
            int i4 = this._outputTail;
            this._outputTail = i4 + 1;
            bArr[i4] = (byte) (((i >> 6) & 63) | 128);
            int i5 = this._outputTail;
            this._outputTail = i5 + 1;
            bArr[i5] = (byte) ((i & 63) | 128);
            return inputOffset + 1;
        }
        byte[] bbuf = this._outputBuffer;
        int i6 = this._outputTail;
        this._outputTail = i6 + 1;
        bbuf[i6] = (byte) ((ch >> 12) | 224);
        int i7 = this._outputTail;
        this._outputTail = i7 + 1;
        bbuf[i7] = (byte) (((ch >> 6) & 63) | 128);
        int i8 = this._outputTail;
        this._outputTail = i8 + 1;
        bbuf[i8] = (byte) ((ch & 63) | 128);
        return inputOffset;
    }

    private int _outputMultiByteChar(int ch, int outputPtr) throws IOException {
        byte[] bbuf = this._outputBuffer;
        if (ch >= 55296 && ch <= 57343) {
            int outputPtr2 = outputPtr + 1;
            bbuf[outputPtr] = 92;
            int outputPtr3 = outputPtr2 + 1;
            bbuf[outputPtr2] = 117;
            int outputPtr4 = outputPtr3 + 1;
            bbuf[outputPtr3] = HEX_CHARS[(ch >> 12) & 15];
            int outputPtr5 = outputPtr4 + 1;
            bbuf[outputPtr4] = HEX_CHARS[(ch >> 8) & 15];
            int outputPtr6 = outputPtr5 + 1;
            bbuf[outputPtr5] = HEX_CHARS[(ch >> 4) & 15];
            int outputPtr7 = outputPtr6 + 1;
            bbuf[outputPtr6] = HEX_CHARS[ch & 15];
            return outputPtr7;
        }
        int outputPtr8 = outputPtr + 1;
        bbuf[outputPtr] = (byte) ((ch >> 12) | 224);
        int outputPtr9 = outputPtr8 + 1;
        bbuf[outputPtr8] = (byte) (((ch >> 6) & 63) | 128);
        int outputPtr10 = outputPtr9 + 1;
        bbuf[outputPtr9] = (byte) ((ch & 63) | 128);
        return outputPtr10;
    }

    private void _writeNull() throws IOException {
        if (this._outputTail + 4 >= this._outputEnd) {
            _flushBuffer();
        }
        System.arraycopy(NULL_BYTES, 0, this._outputBuffer, this._outputTail, 4);
        this._outputTail += 4;
    }

    private int _writeGenericEscape(int charToEscape, int outputPtr) throws IOException {
        int outputPtr2;
        byte[] bbuf = this._outputBuffer;
        int outputPtr3 = outputPtr + 1;
        bbuf[outputPtr] = 92;
        int outputPtr4 = outputPtr3 + 1;
        bbuf[outputPtr3] = 117;
        if (charToEscape > 255) {
            int hi = (charToEscape >> 8) & 255;
            int outputPtr5 = outputPtr4 + 1;
            bbuf[outputPtr4] = HEX_CHARS[hi >> 4];
            outputPtr2 = outputPtr5 + 1;
            bbuf[outputPtr5] = HEX_CHARS[hi & 15];
            charToEscape &= 255;
        } else {
            int outputPtr6 = outputPtr4 + 1;
            bbuf[outputPtr4] = 48;
            outputPtr2 = outputPtr6 + 1;
            bbuf[outputPtr6] = 48;
        }
        int outputPtr7 = outputPtr2 + 1;
        bbuf[outputPtr2] = HEX_CHARS[charToEscape >> 4];
        int outputPtr8 = outputPtr7 + 1;
        bbuf[outputPtr7] = HEX_CHARS[charToEscape & 15];
        return outputPtr8;
    }

    private void _flushBuffer() throws IOException {
        int len = this._outputTail;
        if (len > 0) {
            this._outputTail = 0;
            this._outputStream.write(this._outputBuffer, 0, len);
        }
    }
}
