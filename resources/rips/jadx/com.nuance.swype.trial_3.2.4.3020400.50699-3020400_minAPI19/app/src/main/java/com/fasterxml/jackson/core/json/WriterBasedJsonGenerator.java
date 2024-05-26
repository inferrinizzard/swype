package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.NumberOutput;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: classes.dex */
public final class WriterBasedJsonGenerator extends JsonGeneratorImpl {
    protected static final char[] HEX_CHARS = CharTypes.copyHexChars();
    protected SerializableString _currentEscape;
    protected char[] _entityBuffer;
    protected char[] _outputBuffer;
    protected int _outputEnd;
    protected int _outputHead;
    protected int _outputTail;
    protected final Writer _writer;

    public WriterBasedJsonGenerator(IOContext ctxt, int features, ObjectCodec codec, Writer w) {
        super(ctxt, features, codec);
        this._outputHead = 0;
        this._outputTail = 0;
        this._writer = w;
        this._outputBuffer = ctxt.allocConcatBuffer();
        this._outputEnd = this._outputBuffer.length;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeFieldName(String name) throws IOException, JsonGenerationException {
        int status = this._writeContext.writeFieldName(name);
        if (status == 4) {
            _reportError("Can not write a field name, expecting a value");
        }
        boolean z = status == 1;
        if (this._cfgPrettyPrinter == null) {
            if (this._outputTail + 1 >= this._outputEnd) {
                _flushBuffer();
            }
            if (z) {
                char[] cArr = this._outputBuffer;
                int i = this._outputTail;
                this._outputTail = i + 1;
                cArr[i] = ',';
            }
            if (!isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
                _writeString(name);
                return;
            }
            char[] cArr2 = this._outputBuffer;
            int i2 = this._outputTail;
            this._outputTail = i2 + 1;
            cArr2[i2] = '\"';
            _writeString(name);
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            char[] cArr3 = this._outputBuffer;
            int i3 = this._outputTail;
            this._outputTail = i3 + 1;
            cArr3[i3] = '\"';
            return;
        }
        if (z) {
            this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
        } else {
            this._cfgPrettyPrinter.beforeObjectEntries(this);
        }
        if (isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            char[] cArr4 = this._outputBuffer;
            int i4 = this._outputTail;
            this._outputTail = i4 + 1;
            cArr4[i4] = '\"';
            _writeString(name);
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            char[] cArr5 = this._outputBuffer;
            int i5 = this._outputTail;
            this._outputTail = i5 + 1;
            cArr5[i5] = '\"';
            return;
        }
        _writeString(name);
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
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '[';
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
            char[] cArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            cArr[i] = ']';
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
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '{';
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
            char[] cArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            cArr[i] = '}';
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
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '\"';
        _writeString(text);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = '\"';
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeRaw(String text) throws IOException, JsonGenerationException {
        int len = text.length();
        int room = this._outputEnd - this._outputTail;
        if (room == 0) {
            _flushBuffer();
            room = this._outputEnd - this._outputTail;
        }
        if (room >= len) {
            text.getChars(0, len, this._outputBuffer, this._outputTail);
            this._outputTail += len;
            return;
        }
        int i = this._outputEnd - this._outputTail;
        text.getChars(0, i, this._outputBuffer, this._outputTail);
        this._outputTail += i;
        _flushBuffer();
        int length = text.length() - i;
        while (length > this._outputEnd) {
            int i2 = this._outputEnd;
            text.getChars(i, i + i2, this._outputBuffer, 0);
            this._outputHead = 0;
            this._outputTail = i2;
            _flushBuffer();
            i += i2;
            length -= i2;
        }
        text.getChars(i, i + length, this._outputBuffer, 0);
        this._outputHead = 0;
        this._outputTail = length;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeRaw(SerializableString text) throws IOException, JsonGenerationException {
        writeRaw(text.getValue());
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeRaw(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        if (len < 32) {
            int room = this._outputEnd - this._outputTail;
            if (len > room) {
                _flushBuffer();
            }
            System.arraycopy(text, 0, this._outputBuffer, this._outputTail, len);
            this._outputTail += len;
            return;
        }
        _flushBuffer();
        this._writer.write(text, 0, len);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeRaw(char c) throws IOException, JsonGenerationException {
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = c;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeNumber(int i) throws IOException, JsonGenerationException {
        _verifyValueWrite("write number");
        if (!this._cfgNumbersAsStrings) {
            if (this._outputTail + 11 >= this._outputEnd) {
                _flushBuffer();
            }
            this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
            return;
        }
        if (this._outputTail + 13 >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr[i2] = '\"';
        this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
        char[] cArr2 = this._outputBuffer;
        int i3 = this._outputTail;
        this._outputTail = i3 + 1;
        cArr2[i3] = '\"';
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
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '\"';
        this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = '\"';
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
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '\"';
        writeRaw(value.toString());
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = '\"';
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeBoolean(boolean state) throws IOException, JsonGenerationException {
        int ptr;
        _verifyValueWrite("write boolean value");
        if (this._outputTail + 5 >= this._outputEnd) {
            _flushBuffer();
        }
        int ptr2 = this._outputTail;
        char[] buf = this._outputBuffer;
        if (state) {
            buf[ptr2] = 't';
            int ptr3 = ptr2 + 1;
            buf[ptr3] = 'r';
            int ptr4 = ptr3 + 1;
            buf[ptr4] = 'u';
            ptr = ptr4 + 1;
            buf[ptr] = 'e';
        } else {
            buf[ptr2] = 'f';
            int ptr5 = ptr2 + 1;
            buf[ptr5] = 'a';
            int ptr6 = ptr5 + 1;
            buf[ptr6] = 'l';
            int ptr7 = ptr6 + 1;
            buf[ptr7] = 's';
            ptr = ptr7 + 1;
            buf[ptr] = 'e';
        }
        this._outputTail = ptr + 1;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeNull() throws IOException, JsonGenerationException {
        _verifyValueWrite("write null value");
        _writeNull();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.GeneratorBase
    public final void _verifyValueWrite(String typeMsg) throws IOException, JsonGenerationException {
        char c;
        int status = this._writeContext.writeValue();
        if (status == 5) {
            _reportError("Can not " + typeMsg + ", expecting field name");
        }
        if (this._cfgPrettyPrinter == null) {
            switch (status) {
                case 1:
                    c = ',';
                    break;
                case 2:
                    c = ':';
                    break;
                case 3:
                    if (this._rootValueSeparator != null) {
                        writeRaw(this._rootValueSeparator.getValue());
                        return;
                    }
                    return;
                default:
                    return;
            }
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            this._outputBuffer[this._outputTail] = c;
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
        if (this._writer != null && isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
            this._writer.flush();
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
        if (this._writer != null) {
            if (this._ioContext.isResourceManaged() || isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
                this._writer.close();
            } else if (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
                this._writer.flush();
            }
        }
        _releaseBuffers();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.GeneratorBase
    public final void _releaseBuffers() {
        char[] buf = this._outputBuffer;
        if (buf != null) {
            this._outputBuffer = null;
            this._ioContext.releaseConcatBuffer(buf);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:118:0x0190  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x019f A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:149:0x020e  */
    /* JADX WARN: Removed duplicated region for block: B:152:0x021d A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0097 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:37:? A[LOOP:2: B:14:0x004f->B:37:?, LOOP_END, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x00eb A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:69:? A[LOOP:4: B:52:0x00b5->B:69:?, LOOP_END, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r1v50, types: [int] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void _writeString(java.lang.String r17) throws java.io.IOException, com.fasterxml.jackson.core.JsonGenerationException {
        /*
            Method dump skipped, instructions count: 673
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.WriterBasedJsonGenerator._writeString(java.lang.String):void");
    }

    private void _writeNull() throws IOException {
        if (this._outputTail + 4 >= this._outputEnd) {
            _flushBuffer();
        }
        int ptr = this._outputTail;
        char[] buf = this._outputBuffer;
        buf[ptr] = 'n';
        int ptr2 = ptr + 1;
        buf[ptr2] = 'u';
        int ptr3 = ptr2 + 1;
        buf[ptr3] = 'l';
        int ptr4 = ptr3 + 1;
        buf[ptr4] = 'l';
        this._outputTail = ptr4 + 1;
    }

    private void _prependOrWriteCharacterEscape(char ch, int escCode) throws IOException, JsonGenerationException {
        String escape;
        int ptr;
        if (escCode >= 0) {
            if (this._outputTail >= 2) {
                int ptr2 = this._outputTail - 2;
                this._outputHead = ptr2;
                this._outputBuffer[ptr2] = '\\';
                this._outputBuffer[ptr2 + 1] = (char) escCode;
                return;
            }
            char[] buf = this._entityBuffer;
            if (buf == null) {
                buf = _allocateEntityBuffer();
            }
            this._outputHead = this._outputTail;
            buf[1] = (char) escCode;
            this._writer.write(buf, 0, 2);
            return;
        }
        if (escCode != -2) {
            if (this._outputTail >= 6) {
                char[] buf2 = this._outputBuffer;
                int ptr3 = this._outputTail - 6;
                this._outputHead = ptr3;
                buf2[ptr3] = '\\';
                int ptr4 = ptr3 + 1;
                buf2[ptr4] = 'u';
                if (ch > 255) {
                    int hi = (ch >> '\b') & 255;
                    int ptr5 = ptr4 + 1;
                    buf2[ptr5] = HEX_CHARS[hi >> 4];
                    ptr = ptr5 + 1;
                    buf2[ptr] = HEX_CHARS[hi & 15];
                    ch = (char) (ch & 255);
                } else {
                    int ptr6 = ptr4 + 1;
                    buf2[ptr6] = '0';
                    ptr = ptr6 + 1;
                    buf2[ptr] = '0';
                }
                int ptr7 = ptr + 1;
                buf2[ptr7] = HEX_CHARS[ch >> 4];
                buf2[ptr7 + 1] = HEX_CHARS[ch & 15];
                return;
            }
            char[] buf3 = this._entityBuffer;
            if (buf3 == null) {
                buf3 = _allocateEntityBuffer();
            }
            this._outputHead = this._outputTail;
            if (ch > 255) {
                int hi2 = (ch >> '\b') & 255;
                int lo = ch & 255;
                buf3[10] = HEX_CHARS[hi2 >> 4];
                buf3[11] = HEX_CHARS[hi2 & 15];
                buf3[12] = HEX_CHARS[lo >> 4];
                buf3[13] = HEX_CHARS[lo & 15];
                this._writer.write(buf3, 8, 6);
                return;
            }
            buf3[6] = HEX_CHARS[ch >> 4];
            buf3[7] = HEX_CHARS[ch & 15];
            this._writer.write(buf3, 2, 6);
            return;
        }
        if (this._currentEscape == null) {
            escape = this._characterEscapes.getEscapeSequence$428277ea().getValue();
        } else {
            escape = this._currentEscape.getValue();
            this._currentEscape = null;
        }
        int len = escape.length();
        if (this._outputTail >= len) {
            int ptr8 = this._outputTail - len;
            this._outputHead = ptr8;
            escape.getChars(0, len, this._outputBuffer, ptr8);
        } else {
            this._outputHead = this._outputTail;
            this._writer.write(escape);
        }
    }

    private int _prependOrWriteCharacterEscape(char[] buffer, int ptr, int end, char ch, int escCode) throws IOException, JsonGenerationException {
        String escape;
        int ptr2;
        if (escCode >= 0) {
            if (ptr > 1 && ptr < end) {
                ptr -= 2;
                buffer[ptr] = '\\';
                buffer[ptr + 1] = (char) escCode;
            } else {
                char[] ent = this._entityBuffer;
                if (ent == null) {
                    ent = _allocateEntityBuffer();
                }
                ent[1] = (char) escCode;
                this._writer.write(ent, 0, 2);
            }
            return ptr;
        }
        if (escCode != -2) {
            if (ptr > 5 && ptr < end) {
                int ptr3 = ptr - 6;
                int ptr4 = ptr3 + 1;
                buffer[ptr3] = '\\';
                int ptr5 = ptr4 + 1;
                buffer[ptr4] = 'u';
                if (ch > 255) {
                    int hi = (ch >> '\b') & 255;
                    int ptr6 = ptr5 + 1;
                    buffer[ptr5] = HEX_CHARS[hi >> 4];
                    ptr2 = ptr6 + 1;
                    buffer[ptr6] = HEX_CHARS[hi & 15];
                    ch = (char) (ch & 255);
                } else {
                    int ptr7 = ptr5 + 1;
                    buffer[ptr5] = '0';
                    ptr2 = ptr7 + 1;
                    buffer[ptr7] = '0';
                }
                int ptr8 = ptr2 + 1;
                buffer[ptr2] = HEX_CHARS[ch >> 4];
                buffer[ptr8] = HEX_CHARS[ch & 15];
                ptr = ptr8 - 5;
            } else {
                char[] ent2 = this._entityBuffer;
                if (ent2 == null) {
                    ent2 = _allocateEntityBuffer();
                }
                this._outputHead = this._outputTail;
                if (ch > 255) {
                    int hi2 = (ch >> '\b') & 255;
                    int lo = ch & 255;
                    ent2[10] = HEX_CHARS[hi2 >> 4];
                    ent2[11] = HEX_CHARS[hi2 & 15];
                    ent2[12] = HEX_CHARS[lo >> 4];
                    ent2[13] = HEX_CHARS[lo & 15];
                    this._writer.write(ent2, 8, 6);
                } else {
                    ent2[6] = HEX_CHARS[ch >> 4];
                    ent2[7] = HEX_CHARS[ch & 15];
                    this._writer.write(ent2, 2, 6);
                }
            }
            return ptr;
        }
        if (this._currentEscape == null) {
            escape = this._characterEscapes.getEscapeSequence$428277ea().getValue();
        } else {
            escape = this._currentEscape.getValue();
            this._currentEscape = null;
        }
        int len = escape.length();
        if (ptr >= len && ptr < end) {
            ptr -= len;
            escape.getChars(0, len, buffer, ptr);
        } else {
            this._writer.write(escape);
        }
        return ptr;
    }

    private char[] _allocateEntityBuffer() {
        char[] buf = {'\\', 0, '\\', 'u', '0', '0', 0, 0, '\\', 'u'};
        this._entityBuffer = buf;
        return buf;
    }

    private void _flushBuffer() throws IOException {
        int len = this._outputTail - this._outputHead;
        if (len > 0) {
            int offset = this._outputHead;
            this._outputHead = 0;
            this._outputTail = 0;
            this._writer.write(this._outputBuffer, offset, len);
        }
    }
}
