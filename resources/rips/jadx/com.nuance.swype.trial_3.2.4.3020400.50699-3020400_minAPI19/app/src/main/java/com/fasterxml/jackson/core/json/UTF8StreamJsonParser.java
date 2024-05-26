package com.fasterxml.jackson.core.json;

import com.facebook.internal.ServerProtocol;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.BytesToNameCanonicalizer;
import com.fasterxml.jackson.core.sym.Name;
import com.fasterxml.jackson.core.sym.Name1;
import com.nuance.swype.input.R;
import com.nuance.swypeconnect.ac.ACBuildConfigRuntime;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class UTF8StreamJsonParser extends ParserBase {
    protected boolean _bufferRecyclable;
    protected byte[] _inputBuffer;
    protected InputStream _inputStream;
    protected ObjectCodec _objectCodec;
    private int _quad1;
    protected int[] _quadBuffer;
    protected final BytesToNameCanonicalizer _symbols;
    protected boolean _tokenIncomplete;
    private static final int[] sInputCodesUtf8 = CharTypes.getInputCodeUtf8();
    private static final int[] sInputCodesLatin1 = CharTypes.getInputCodeLatin1();

    public UTF8StreamJsonParser(IOContext ctxt, int features, InputStream in, ObjectCodec codec, BytesToNameCanonicalizer sym, byte[] inputBuffer, int start, int end, boolean bufferRecyclable) {
        super(ctxt, features);
        this._quadBuffer = new int[16];
        this._tokenIncomplete = false;
        this._inputStream = in;
        this._objectCodec = codec;
        this._symbols = sym;
        this._inputBuffer = inputBuffer;
        this._inputPtr = start;
        this._inputEnd = end;
        this._bufferRecyclable = bufferRecyclable;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.ParserBase
    public final boolean loadMore() throws IOException {
        this._currInputProcessed += this._inputEnd;
        this._currInputRowStart -= this._inputEnd;
        if (this._inputStream == null) {
            return false;
        }
        int count = this._inputStream.read(this._inputBuffer, 0, this._inputBuffer.length);
        if (count > 0) {
            this._inputPtr = 0;
            this._inputEnd = count;
            return true;
        }
        _closeInput();
        if (count == 0) {
            throw new IOException("InputStream.read() returned 0 characters when trying to read " + this._inputBuffer.length + " bytes");
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.ParserBase
    public final void _closeInput() throws IOException {
        if (this._inputStream != null) {
            if (this._ioContext.isResourceManaged() || isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
                this._inputStream.close();
            }
            this._inputStream = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.ParserBase
    public final void _releaseBuffers() throws IOException {
        byte[] buf;
        super._releaseBuffers();
        if (this._bufferRecyclable && (buf = this._inputBuffer) != null) {
            this._inputBuffer = null;
            this._ioContext.releaseReadIOBuffer(buf);
        }
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public final String getText() throws IOException, JsonParseException {
        if (this._currToken == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
                this._tokenIncomplete = false;
                _finishString();
            }
            return this._textBuffer.contentsAsString();
        }
        JsonToken jsonToken = this._currToken;
        if (jsonToken == null) {
            return null;
        }
        switch (jsonToken) {
            case FIELD_NAME:
                return this._parsingContext.getCurrentName();
            case VALUE_STRING:
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
                return this._textBuffer.contentsAsString();
            default:
                return jsonToken._serialized;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public final JsonToken nextToken() throws IOException, JsonParseException {
        int i;
        Name n;
        JsonToken t;
        this._numTypesValid = 0;
        if (this._currToken != JsonToken.FIELD_NAME) {
            if (this._tokenIncomplete) {
                this._tokenIncomplete = false;
                int[] iArr = sInputCodesUtf8;
                byte[] bArr = this._inputBuffer;
                while (true) {
                    int i2 = this._inputPtr;
                    int i3 = this._inputEnd;
                    if (i2 >= i3) {
                        loadMoreGuaranteed();
                        i2 = this._inputPtr;
                        i3 = this._inputEnd;
                    }
                    while (true) {
                        if (i2 < i3) {
                            int i4 = i2 + 1;
                            int i5 = bArr[i2] & 255;
                            if (iArr[i5] != 0) {
                                this._inputPtr = i4;
                                if (i5 != 34) {
                                    switch (iArr[i5]) {
                                        case 1:
                                            _decodeEscaped();
                                            break;
                                        case 2:
                                            _skipUtf8_2$13462e();
                                            break;
                                        case 3:
                                            _skipUtf8_3$13462e();
                                            break;
                                        case 4:
                                            _skipUtf8_4$13462e();
                                            break;
                                        default:
                                            if (i5 < 32) {
                                                _throwUnquotedSpace(i5, "string value");
                                                break;
                                            } else {
                                                _reportInvalidChar(i5);
                                                break;
                                            }
                                    }
                                }
                            } else {
                                i2 = i4;
                            }
                        } else {
                            this._inputPtr = i2;
                        }
                    }
                }
            }
            while (true) {
                if (this._inputPtr < this._inputEnd || loadMore()) {
                    byte[] bArr2 = this._inputBuffer;
                    int i6 = this._inputPtr;
                    this._inputPtr = i6 + 1;
                    i = bArr2[i6] & 255;
                    if (i > 32) {
                        if (i == 47) {
                            _skipComment();
                        }
                    } else if (i != 32) {
                        if (i == 10) {
                            _skipLF();
                        } else if (i == 13) {
                            _skipCR();
                        } else if (i != 9) {
                            _throwInvalidSpace(i);
                        }
                    }
                } else {
                    _handleEOF();
                    i = -1;
                }
            }
            if (i < 0) {
                close();
                this._currToken = null;
                return null;
            }
            this._tokenInputTotal = (this._currInputProcessed + this._inputPtr) - 1;
            this._tokenInputRow = this._currInputRow;
            this._tokenInputCol = (this._inputPtr - this._currInputRowStart) - 1;
            this._binaryValue = null;
            if (i == 93) {
                if (!this._parsingContext.inArray()) {
                    _reportMismatchedEndMarker(i, '}');
                }
                this._parsingContext = this._parsingContext.getParent();
                JsonToken jsonToken = JsonToken.END_ARRAY;
                this._currToken = jsonToken;
                return jsonToken;
            }
            if (i == 125) {
                if (!this._parsingContext.inObject()) {
                    _reportMismatchedEndMarker(i, ']');
                }
                this._parsingContext = this._parsingContext.getParent();
                JsonToken jsonToken2 = JsonToken.END_OBJECT;
                this._currToken = jsonToken2;
                return jsonToken2;
            }
            if (this._parsingContext.expectComma()) {
                if (i != 44) {
                    _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.getTypeDesc() + " entries");
                }
                i = _skipWS();
            }
            if (!this._parsingContext.inObject()) {
                if (i == 34) {
                    this._tokenIncomplete = true;
                    JsonToken jsonToken3 = JsonToken.VALUE_STRING;
                    this._currToken = jsonToken3;
                    return jsonToken3;
                }
                switch (i) {
                    case 45:
                    case 48:
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    case 56:
                    case 57:
                        JsonToken parseNumberText = parseNumberText(i);
                        this._currToken = parseNumberText;
                        return parseNumberText;
                    case 91:
                        this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                        JsonToken jsonToken4 = JsonToken.START_ARRAY;
                        this._currToken = jsonToken4;
                        return jsonToken4;
                    case 93:
                    case 125:
                        _reportUnexpectedChar(i, "expected a value");
                        break;
                    case 102:
                        _matchToken(ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED, 1);
                        JsonToken jsonToken5 = JsonToken.VALUE_FALSE;
                        this._currToken = jsonToken5;
                        return jsonToken5;
                    case 110:
                        _matchToken("null", 1);
                        JsonToken jsonToken6 = JsonToken.VALUE_NULL;
                        this._currToken = jsonToken6;
                        return jsonToken6;
                    case 116:
                        break;
                    case 123:
                        this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                        JsonToken jsonToken7 = JsonToken.START_OBJECT;
                        this._currToken = jsonToken7;
                        return jsonToken7;
                    default:
                        JsonToken _handleUnexpectedValue = _handleUnexpectedValue(i);
                        this._currToken = _handleUnexpectedValue;
                        return _handleUnexpectedValue;
                }
                _matchToken(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE, 1);
                JsonToken jsonToken8 = JsonToken.VALUE_TRUE;
                this._currToken = jsonToken8;
                return jsonToken8;
            }
            if (i != 34) {
                n = _handleUnusualFieldName(i);
            } else if (this._inputPtr + 9 <= this._inputEnd) {
                byte[] bArr3 = this._inputBuffer;
                int[] iArr2 = sInputCodesLatin1;
                int i7 = this._inputPtr;
                this._inputPtr = i7 + 1;
                int i8 = bArr3[i7] & 255;
                if (iArr2[i8] == 0) {
                    int i9 = this._inputPtr;
                    this._inputPtr = i9 + 1;
                    int i10 = bArr3[i9] & 255;
                    if (iArr2[i10] == 0) {
                        int i11 = (i8 << 8) | i10;
                        int i12 = this._inputPtr;
                        this._inputPtr = i12 + 1;
                        int i13 = bArr3[i12] & 255;
                        if (iArr2[i13] == 0) {
                            int i14 = (i11 << 8) | i13;
                            int i15 = this._inputPtr;
                            this._inputPtr = i15 + 1;
                            int i16 = bArr3[i15] & 255;
                            if (iArr2[i16] == 0) {
                                int i17 = (i14 << 8) | i16;
                                int i18 = this._inputPtr;
                                this._inputPtr = i18 + 1;
                                int i19 = bArr3[i18] & 255;
                                if (iArr2[i19] == 0) {
                                    this._quad1 = i17;
                                    byte[] bArr4 = this._inputBuffer;
                                    int i20 = this._inputPtr;
                                    this._inputPtr = i20 + 1;
                                    int i21 = bArr4[i20] & 255;
                                    if (iArr2[i21] != 0) {
                                        if (i21 == 34) {
                                            n = findName(this._quad1, i19, 1);
                                        } else {
                                            n = parseFieldName(this._quad1, i19, i21, 1);
                                        }
                                    } else {
                                        int i22 = (i19 << 8) | i21;
                                        byte[] bArr5 = this._inputBuffer;
                                        int i23 = this._inputPtr;
                                        this._inputPtr = i23 + 1;
                                        int i24 = bArr5[i23] & 255;
                                        if (iArr2[i24] != 0) {
                                            if (i24 == 34) {
                                                n = findName(this._quad1, i22, 2);
                                            } else {
                                                n = parseFieldName(this._quad1, i22, i24, 2);
                                            }
                                        } else {
                                            int i25 = (i22 << 8) | i24;
                                            byte[] bArr6 = this._inputBuffer;
                                            int i26 = this._inputPtr;
                                            this._inputPtr = i26 + 1;
                                            int i27 = bArr6[i26] & 255;
                                            if (iArr2[i27] != 0) {
                                                if (i27 == 34) {
                                                    n = findName(this._quad1, i25, 3);
                                                } else {
                                                    n = parseFieldName(this._quad1, i25, i27, 3);
                                                }
                                            } else {
                                                int i28 = (i25 << 8) | i27;
                                                byte[] bArr7 = this._inputBuffer;
                                                int i29 = this._inputPtr;
                                                this._inputPtr = i29 + 1;
                                                int i30 = bArr7[i29] & 255;
                                                if (iArr2[i30] != 0) {
                                                    if (i30 == 34) {
                                                        n = findName(this._quad1, i28, 4);
                                                    } else {
                                                        n = parseFieldName(this._quad1, i28, i30, 4);
                                                    }
                                                } else {
                                                    this._quadBuffer[0] = this._quad1;
                                                    this._quadBuffer[1] = i28;
                                                    n = parseLongFieldName(i30);
                                                }
                                            }
                                        }
                                    }
                                } else if (i19 == 34) {
                                    n = findName(i17, 4);
                                } else {
                                    n = parseFieldName(i17, i19, 4);
                                }
                            } else if (i16 == 34) {
                                n = findName(i14, 3);
                            } else {
                                n = parseFieldName(i14, i16, 3);
                            }
                        } else if (i13 == 34) {
                            n = findName(i11, 2);
                        } else {
                            n = parseFieldName(i11, i13, 2);
                        }
                    } else if (i10 == 34) {
                        n = findName(i8, 1);
                    } else {
                        n = parseFieldName(i8, i10, 1);
                    }
                } else if (i8 != 34) {
                    n = parseFieldName(0, i8, 0);
                } else {
                    n = Name1.getEmptyName();
                }
            } else {
                if (this._inputPtr >= this._inputEnd && !loadMore()) {
                    _reportInvalidEOF(": was expecting closing '\"' for name");
                }
                byte[] bArr8 = this._inputBuffer;
                int i31 = this._inputPtr;
                this._inputPtr = i31 + 1;
                int i32 = bArr8[i31] & 255;
                if (i32 != 34) {
                    n = parseEscapedFieldName(this._quadBuffer, 0, 0, i32, 0);
                } else {
                    n = Name1.getEmptyName();
                }
            }
            this._parsingContext.setCurrentName(n.getName());
            this._currToken = JsonToken.FIELD_NAME;
            int i33 = _skipWS();
            if (i33 != 58) {
                _reportUnexpectedChar(i33, "was expecting a colon to separate field name and value");
            }
            int i34 = _skipWS();
            if (i34 == 34) {
                this._tokenIncomplete = true;
                this._nextToken = JsonToken.VALUE_STRING;
                return this._currToken;
            }
            switch (i34) {
                case 45:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                    t = parseNumberText(i34);
                    break;
                case 91:
                    t = JsonToken.START_ARRAY;
                    break;
                case 93:
                case 125:
                    _reportUnexpectedChar(i34, "expected a value");
                    _matchToken(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE, 1);
                    t = JsonToken.VALUE_TRUE;
                    break;
                case 102:
                    _matchToken(ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED, 1);
                    t = JsonToken.VALUE_FALSE;
                    break;
                case 110:
                    _matchToken("null", 1);
                    t = JsonToken.VALUE_NULL;
                    break;
                case 116:
                    _matchToken(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE, 1);
                    t = JsonToken.VALUE_TRUE;
                    break;
                case 123:
                    t = JsonToken.START_OBJECT;
                    break;
                default:
                    t = _handleUnexpectedValue(i34);
                    break;
            }
            this._nextToken = t;
            return this._currToken;
        }
        this._nameCopied = false;
        JsonToken jsonToken9 = this._nextToken;
        this._nextToken = null;
        if (jsonToken9 == JsonToken.START_ARRAY) {
            this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
        } else if (jsonToken9 == JsonToken.START_OBJECT) {
            this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
        }
        this._currToken = jsonToken9;
        return jsonToken9;
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase, com.fasterxml.jackson.core.JsonParser, java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        super.close();
        this._symbols.release();
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x0095, code lost:            if (r0 == 48) goto L42;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x009b, code lost:            if (r13._inputPtr < r13._inputEnd) goto L46;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00a1, code lost:            if (loadMore() == false) goto L81;     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00a3, code lost:            r0 = r13._inputBuffer[r13._inputPtr] & 255;     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00ab, code lost:            if (r0 < 48) goto L83;     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00ad, code lost:            if (r0 <= 57) goto L50;     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00b1, code lost:            r13._inputPtr++;     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00b7, code lost:            if (r0 == 48) goto L85;     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00af, code lost:            r14 = 48;     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00b9, code lost:            r14 = r0;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private com.fasterxml.jackson.core.JsonToken parseNumberText(int r14) throws java.io.IOException, com.fasterxml.jackson.core.JsonParseException {
        /*
            Method dump skipped, instructions count: 322
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8StreamJsonParser.parseNumberText(int):com.fasterxml.jackson.core.JsonToken");
    }

    private JsonToken _parseFloatText(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength) throws IOException, JsonParseException {
        int outPtr2;
        int fractLen = 0;
        boolean eof = false;
        if (c == 46) {
            int outPtr3 = outPtr + 1;
            outBuf[outPtr] = (char) c;
            while (true) {
                outPtr = outPtr3;
                if (this._inputPtr >= this._inputEnd && !loadMore()) {
                    eof = true;
                    break;
                }
                byte[] bArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                c = bArr[i] & 255;
                if (c < 48 || c > 57) {
                    break;
                }
                fractLen++;
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                outPtr3 = outPtr + 1;
                outBuf[outPtr] = (char) c;
            }
            if (fractLen == 0) {
                reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
            }
        }
        int expLen = 0;
        if (c == 101 || c == 69) {
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int outPtr4 = outPtr + 1;
            outBuf[outPtr] = (char) c;
            if (this._inputPtr >= this._inputEnd) {
                loadMoreGuaranteed();
            }
            byte[] bArr2 = this._inputBuffer;
            int i2 = this._inputPtr;
            this._inputPtr = i2 + 1;
            int c2 = bArr2[i2] & 255;
            if (c2 == 45 || c2 == 43) {
                if (outPtr4 >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr2 = 0;
                } else {
                    outPtr2 = outPtr4;
                }
                int outPtr5 = outPtr2 + 1;
                outBuf[outPtr2] = (char) c2;
                if (this._inputPtr >= this._inputEnd) {
                    loadMoreGuaranteed();
                }
                byte[] bArr3 = this._inputBuffer;
                int i3 = this._inputPtr;
                this._inputPtr = i3 + 1;
                c2 = bArr3[i3] & 255;
                outPtr = outPtr5;
            } else {
                outPtr = outPtr4;
            }
            while (true) {
                if (c2 > 57 || c2 < 48) {
                    break;
                }
                expLen++;
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                int outPtr6 = outPtr + 1;
                outBuf[outPtr] = (char) c2;
                if (this._inputPtr >= this._inputEnd && !loadMore()) {
                    eof = true;
                    outPtr = outPtr6;
                    break;
                }
                byte[] bArr4 = this._inputBuffer;
                int i4 = this._inputPtr;
                this._inputPtr = i4 + 1;
                c2 = bArr4[i4] & 255;
                outPtr = outPtr6;
            }
            if (expLen == 0) {
                reportUnexpectedNumberChar(c2, "Exponent indicator not followed by a digit");
            }
        }
        if (!eof) {
            this._inputPtr--;
        }
        this._textBuffer._currentSize = outPtr;
        return resetFloat(negative, integerPartLength, fractLen, expLen);
    }

    private Name parseLongFieldName(int q) throws IOException, JsonParseException {
        int[] codes = sInputCodesLatin1;
        int qlen = 2;
        while (this._inputEnd - this._inputPtr >= 4) {
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            int i2 = bArr[i] & 255;
            if (codes[i2] != 0) {
                if (i2 == 34) {
                    return findName(this._quadBuffer, qlen, q, 1);
                }
                return parseEscapedFieldName(this._quadBuffer, qlen, q, i2, 1);
            }
            int q2 = (q << 8) | i2;
            byte[] bArr2 = this._inputBuffer;
            int i3 = this._inputPtr;
            this._inputPtr = i3 + 1;
            int i4 = bArr2[i3] & 255;
            if (codes[i4] != 0) {
                if (i4 == 34) {
                    return findName(this._quadBuffer, qlen, q2, 2);
                }
                return parseEscapedFieldName(this._quadBuffer, qlen, q2, i4, 2);
            }
            int q3 = (q2 << 8) | i4;
            byte[] bArr3 = this._inputBuffer;
            int i5 = this._inputPtr;
            this._inputPtr = i5 + 1;
            int i6 = bArr3[i5] & 255;
            if (codes[i6] != 0) {
                if (i6 == 34) {
                    return findName(this._quadBuffer, qlen, q3, 3);
                }
                return parseEscapedFieldName(this._quadBuffer, qlen, q3, i6, 3);
            }
            int q4 = (q3 << 8) | i6;
            byte[] bArr4 = this._inputBuffer;
            int i7 = this._inputPtr;
            this._inputPtr = i7 + 1;
            int i8 = bArr4[i7] & 255;
            if (codes[i8] != 0) {
                if (i8 == 34) {
                    return findName(this._quadBuffer, qlen, q4, 4);
                }
                return parseEscapedFieldName(this._quadBuffer, qlen, q4, i8, 4);
            }
            if (qlen >= this._quadBuffer.length) {
                this._quadBuffer = growArrayBy(this._quadBuffer, qlen);
            }
            this._quadBuffer[qlen] = q4;
            q = i8;
            qlen++;
        }
        return parseEscapedFieldName(this._quadBuffer, qlen, 0, q, 0);
    }

    private Name parseFieldName(int q1, int ch, int lastQuadBytes) throws IOException, JsonParseException {
        return parseEscapedFieldName(this._quadBuffer, 0, q1, ch, lastQuadBytes);
    }

    private Name parseFieldName(int q1, int q2, int ch, int lastQuadBytes) throws IOException, JsonParseException {
        this._quadBuffer[0] = q1;
        return parseEscapedFieldName(this._quadBuffer, 1, q2, ch, lastQuadBytes);
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x0041  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0095  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private com.fasterxml.jackson.core.sym.Name parseEscapedFieldName(int[] r8, int r9, int r10, int r11, int r12) throws java.io.IOException, com.fasterxml.jackson.core.JsonParseException {
        /*
            Method dump skipped, instructions count: 204
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8StreamJsonParser.parseEscapedFieldName(int[], int, int, int, int):com.fasterxml.jackson.core.sym.Name");
    }

    private Name _handleUnusualFieldName(int ch) throws IOException, JsonParseException {
        int[] iArr;
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int[] iArr2;
        int i6;
        int i7;
        char _decodeEscaped;
        int i8;
        int i9;
        int[] iArr3;
        int i10;
        int i11;
        int i12;
        int i13;
        int i14;
        if (ch != 39 || !isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
            if (!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
                _reportUnexpectedChar(ch, "was expecting double-quote to start field name");
            }
            int[] codes = CharTypes.getInputCodeUtf8JsNames();
            if (codes[ch] != 0) {
                _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
            }
            int[] quads = this._quadBuffer;
            int qlen = 0;
            int currQuad = 0;
            int currQuadBytes = 0;
            while (true) {
                int qlen2 = qlen;
                if (currQuadBytes < 4) {
                    currQuadBytes++;
                    currQuad = (currQuad << 8) | ch;
                    qlen = qlen2;
                } else {
                    if (qlen2 >= quads.length) {
                        quads = growArrayBy(quads, quads.length);
                        this._quadBuffer = quads;
                    }
                    qlen = qlen2 + 1;
                    quads[qlen2] = currQuad;
                    currQuad = ch;
                    currQuadBytes = 1;
                }
                if (this._inputPtr >= this._inputEnd && !loadMore()) {
                    _reportInvalidEOF(" in field name");
                }
                ch = this._inputBuffer[this._inputPtr] & 255;
                if (codes[ch] != 0) {
                    break;
                }
                this._inputPtr++;
            }
            if (currQuadBytes > 0) {
                if (qlen >= quads.length) {
                    quads = growArrayBy(quads, quads.length);
                    this._quadBuffer = quads;
                }
                quads[qlen] = currQuad;
                qlen++;
            }
            Name name = this._symbols.findName(quads, qlen);
            if (name == null) {
                name = addName(quads, qlen, currQuadBytes);
            }
            return name;
        }
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(": was expecting closing ''' for name");
        }
        byte[] bArr = this._inputBuffer;
        int i15 = this._inputPtr;
        this._inputPtr = i15 + 1;
        int i16 = bArr[i15] & 255;
        if (i16 != 39) {
            int[] iArr4 = this._quadBuffer;
            int i17 = 0;
            int i18 = 0;
            int i19 = 0;
            int[] iArr5 = sInputCodesLatin1;
            while (i16 != 39) {
                if (i16 == 34 || iArr5[i16] == 0) {
                    i2 = i18;
                    int i20 = i19;
                    i3 = i17;
                    i4 = i16;
                    i5 = i20;
                } else {
                    if (i16 != 92) {
                        _throwUnquotedSpace(i16, "name");
                        _decodeEscaped = i16;
                    } else {
                        _decodeEscaped = _decodeEscaped();
                    }
                    if (_decodeEscaped > 127) {
                        if (i19 >= 4) {
                            if (i17 >= iArr4.length) {
                                iArr4 = growArrayBy(iArr4, iArr4.length);
                                this._quadBuffer = iArr4;
                            }
                            iArr4[i17] = i18;
                            i19 = 0;
                            i9 = i17 + 1;
                            i8 = 0;
                        } else {
                            i8 = i18;
                            i9 = i17;
                        }
                        if (_decodeEscaped < 2048) {
                            int i21 = (_decodeEscaped >> 6) | R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop | (i8 << 8);
                            int i22 = i19 + 1;
                            iArr3 = iArr4;
                            i14 = i22;
                            i13 = i21;
                        } else {
                            int i23 = (_decodeEscaped >> '\f') | 224 | (i8 << 8);
                            int i24 = i19 + 1;
                            if (i24 >= 4) {
                                if (i9 >= iArr4.length) {
                                    iArr4 = growArrayBy(iArr4, iArr4.length);
                                    this._quadBuffer = iArr4;
                                }
                                iArr4[i9] = i23;
                                i11 = 0;
                                i12 = i9 + 1;
                                iArr3 = iArr4;
                                i10 = 0;
                            } else {
                                iArr3 = iArr4;
                                i10 = i24;
                                i11 = i23;
                                i12 = i9;
                            }
                            i13 = (i11 << 8) | ((_decodeEscaped >> 6) & 63) | 128;
                            i14 = i10 + 1;
                            i9 = i12;
                        }
                        i4 = (_decodeEscaped & '?') | 128;
                        i2 = i13;
                        i5 = i14;
                        iArr4 = iArr3;
                        i3 = i9;
                    } else {
                        i5 = i19;
                        i3 = i17;
                        i4 = _decodeEscaped;
                        i2 = i18;
                    }
                }
                if (i5 < 4) {
                    int i25 = i5 + 1;
                    i7 = i4 | (i2 << 8);
                    i17 = i3;
                    iArr2 = iArr4;
                    i6 = i25;
                } else {
                    if (i3 >= iArr4.length) {
                        iArr4 = growArrayBy(iArr4, iArr4.length);
                        this._quadBuffer = iArr4;
                    }
                    int i26 = i3 + 1;
                    iArr4[i3] = i2;
                    iArr2 = iArr4;
                    i6 = 1;
                    i7 = i4;
                    i17 = i26;
                }
                if (this._inputPtr >= this._inputEnd && !loadMore()) {
                    _reportInvalidEOF(" in field name");
                }
                byte[] bArr2 = this._inputBuffer;
                int i27 = this._inputPtr;
                this._inputPtr = i27 + 1;
                int i28 = i6;
                iArr4 = iArr2;
                i19 = i28;
                int i29 = i7;
                i16 = bArr2[i27] & 255;
                i18 = i29;
            }
            if (i19 > 0) {
                if (i17 >= iArr4.length) {
                    iArr4 = growArrayBy(iArr4, iArr4.length);
                    this._quadBuffer = iArr4;
                }
                iArr4[i17] = i18;
                iArr = iArr4;
                i = i17 + 1;
            } else {
                iArr = iArr4;
                i = i17;
            }
            Name findName = this._symbols.findName(iArr, i);
            return findName == null ? addName(iArr, i, i19) : findName;
        }
        return Name1.getEmptyName();
    }

    private Name findName(int q1, int lastQuadBytes) throws JsonParseException {
        Name name = this._symbols.findName(q1);
        if (name == null) {
            this._quadBuffer[0] = q1;
            return addName(this._quadBuffer, 1, lastQuadBytes);
        }
        return name;
    }

    private Name findName(int q1, int q2, int lastQuadBytes) throws JsonParseException {
        Name name = this._symbols.findName(q1, q2);
        if (name == null) {
            this._quadBuffer[0] = q1;
            this._quadBuffer[1] = q2;
            return addName(this._quadBuffer, 2, lastQuadBytes);
        }
        return name;
    }

    private Name findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException {
        if (qlen >= quads.length) {
            quads = growArrayBy(quads, quads.length);
            this._quadBuffer = quads;
        }
        int qlen2 = qlen + 1;
        quads[qlen] = lastQuad;
        Name name = this._symbols.findName(quads, qlen2);
        if (name == null) {
            return addName(quads, qlen2, lastQuadBytes);
        }
        return name;
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x00cb  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00d1 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private com.fasterxml.jackson.core.sym.Name addName(int[] r15, int r16, int r17) throws com.fasterxml.jackson.core.JsonParseException {
        /*
            Method dump skipped, instructions count: 274
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8StreamJsonParser.addName(int[], int, int):com.fasterxml.jackson.core.sym.Name");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.ParserBase
    public final void _finishString() throws IOException, JsonParseException {
        int ptr = this._inputPtr;
        if (ptr >= this._inputEnd) {
            loadMoreGuaranteed();
            ptr = this._inputPtr;
        }
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = sInputCodesUtf8;
        int max = Math.min(this._inputEnd, outBuf.length + ptr);
        byte[] inputBuffer = this._inputBuffer;
        int outPtr = 0;
        while (true) {
            if (ptr >= max) {
                break;
            }
            int c = inputBuffer[ptr] & 255;
            if (codes[c] != 0) {
                if (c == 34) {
                    this._inputPtr = ptr + 1;
                    this._textBuffer._currentSize = outPtr;
                    return;
                }
            } else {
                ptr++;
                outBuf[outPtr] = (char) c;
                outPtr++;
            }
        }
        this._inputPtr = ptr;
        int[] iArr = sInputCodesUtf8;
        byte[] bArr = this._inputBuffer;
        while (true) {
            int i = this._inputPtr;
            if (i >= this._inputEnd) {
                loadMoreGuaranteed();
                i = this._inputPtr;
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int min = Math.min(this._inputEnd, (outBuf.length - outPtr) + i);
            while (true) {
                if (i < min) {
                    int i2 = i + 1;
                    int i3 = bArr[i] & 255;
                    if (iArr[i3] != 0) {
                        this._inputPtr = i2;
                        if (i3 != 34) {
                            switch (iArr[i3]) {
                                case 1:
                                    i3 = _decodeEscaped();
                                    break;
                                case 2:
                                    i3 = _decodeUtf8_2(i3);
                                    break;
                                case 3:
                                    if (this._inputEnd - this._inputPtr >= 2) {
                                        i3 = _decodeUtf8_3fast(i3);
                                        break;
                                    } else {
                                        i3 = _decodeUtf8_3(i3);
                                        break;
                                    }
                                case 4:
                                    int _decodeUtf8_4 = _decodeUtf8_4(i3);
                                    int i4 = outPtr + 1;
                                    outBuf[outPtr] = (char) (55296 | (_decodeUtf8_4 >> 10));
                                    if (i4 >= outBuf.length) {
                                        outBuf = this._textBuffer.finishCurrentSegment();
                                        i4 = 0;
                                    }
                                    outPtr = i4;
                                    i3 = (_decodeUtf8_4 & 1023) | 56320;
                                    break;
                                default:
                                    if (i3 < 32) {
                                        _throwUnquotedSpace(i3, "string value");
                                        break;
                                    } else {
                                        _reportInvalidChar(i3);
                                        break;
                                    }
                            }
                            if (outPtr >= outBuf.length) {
                                outBuf = this._textBuffer.finishCurrentSegment();
                                outPtr = 0;
                            }
                            int i5 = outPtr;
                            outPtr = i5 + 1;
                            outBuf[i5] = (char) i3;
                        } else {
                            this._textBuffer._currentSize = outPtr;
                            return;
                        }
                    } else {
                        outBuf[outPtr] = (char) i3;
                        i = i2;
                        outPtr++;
                    }
                } else {
                    this._inputPtr = i;
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0003. Please report as an issue. */
    private JsonToken _handleUnexpectedValue(int c) throws IOException, JsonParseException {
        int i;
        int i2;
        switch (c) {
            case 39:
                if (isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
                    char[] emptyAndGetCurrentSegment = this._textBuffer.emptyAndGetCurrentSegment();
                    int[] iArr = sInputCodesUtf8;
                    byte[] bArr = this._inputBuffer;
                    int i3 = 0;
                    while (true) {
                        if (this._inputPtr >= this._inputEnd) {
                            loadMoreGuaranteed();
                        }
                        if (i3 >= emptyAndGetCurrentSegment.length) {
                            emptyAndGetCurrentSegment = this._textBuffer.finishCurrentSegment();
                            i3 = 0;
                        }
                        int i4 = this._inputEnd;
                        int length = this._inputPtr + (emptyAndGetCurrentSegment.length - i3);
                        if (length >= i4) {
                            length = i4;
                        }
                        while (this._inputPtr < length) {
                            int i5 = this._inputPtr;
                            this._inputPtr = i5 + 1;
                            int i6 = bArr[i5] & 255;
                            if (i6 != 39 && iArr[i6] == 0) {
                                emptyAndGetCurrentSegment[i3] = (char) i6;
                                i3++;
                            } else if (i6 != 39) {
                                switch (iArr[i6]) {
                                    case 1:
                                        if (i6 != 34) {
                                            i = _decodeEscaped();
                                            break;
                                        }
                                        break;
                                    case 2:
                                        i = _decodeUtf8_2(i6);
                                        break;
                                    case 3:
                                        if (this._inputEnd - this._inputPtr >= 2) {
                                            i = _decodeUtf8_3fast(i6);
                                            break;
                                        } else {
                                            i = _decodeUtf8_3(i6);
                                            break;
                                        }
                                    case 4:
                                        int _decodeUtf8_4 = _decodeUtf8_4(i6);
                                        int i7 = i3 + 1;
                                        emptyAndGetCurrentSegment[i3] = (char) (55296 | (_decodeUtf8_4 >> 10));
                                        if (i7 >= emptyAndGetCurrentSegment.length) {
                                            emptyAndGetCurrentSegment = this._textBuffer.finishCurrentSegment();
                                            i3 = 0;
                                        } else {
                                            i3 = i7;
                                        }
                                        i = 56320 | (_decodeUtf8_4 & 1023);
                                        break;
                                    default:
                                        if (i6 < 32) {
                                            _throwUnquotedSpace(i6, "string value");
                                        }
                                        _reportInvalidChar(i6);
                                        break;
                                }
                                i = i6;
                                if (i3 >= emptyAndGetCurrentSegment.length) {
                                    emptyAndGetCurrentSegment = this._textBuffer.finishCurrentSegment();
                                    i2 = 0;
                                } else {
                                    i2 = i3;
                                }
                                i3 = i2 + 1;
                                emptyAndGetCurrentSegment[i2] = (char) i;
                            } else {
                                this._textBuffer._currentSize = i3;
                                return JsonToken.VALUE_STRING;
                            }
                        }
                    }
                }
                _reportUnexpectedChar(c, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
                return null;
            case 43:
                if (this._inputPtr >= this._inputEnd && !loadMore()) {
                    _reportInvalidEOF(" in a value");
                }
                byte[] bArr2 = this._inputBuffer;
                int i8 = this._inputPtr;
                this._inputPtr = i8 + 1;
                return _handleInvalidNumberStart(bArr2[i8] & 255, false);
            case 78:
                _matchToken("NaN", 1);
                if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
                    return resetAsNaN("NaN", Double.NaN);
                }
                throw _constructError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
            default:
                _reportUnexpectedChar(c, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
                return null;
        }
    }

    private JsonToken _handleInvalidNumberStart(int i, boolean negative) throws IOException, JsonParseException {
        if (i == 73) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(" in a value");
            }
            byte[] bArr = this._inputBuffer;
            int i2 = this._inputPtr;
            this._inputPtr = i2 + 1;
            i = bArr[i2];
            if (i == 78) {
                String match = negative ? "-INF" : "+INF";
                _matchToken(match, 3);
                if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
                    return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
                }
                throw _constructError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
            }
            if (i == 110) {
                String match2 = negative ? "-Infinity" : "+Infinity";
                _matchToken(match2, 3);
                if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
                    return resetAsNaN(match2, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
                }
                throw _constructError("Non-standard token '" + match2 + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
            }
        }
        reportUnexpectedNumberChar(i, "expected digit (0-9) to follow minus sign, for valid numeric value");
        return null;
    }

    private void _matchToken(String matchStr, int i) throws IOException, JsonParseException {
        int ch;
        int len = matchStr.length();
        do {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(" in a value");
            }
            if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
                _reportInvalidToken(matchStr.substring(0, i), "'null', 'true', 'false' or NaN");
            }
            this._inputPtr++;
            i++;
        } while (i < len);
        if ((this._inputPtr < this._inputEnd || loadMore()) && (ch = this._inputBuffer[this._inputPtr] & 255) >= 48 && ch != 93 && ch != 125 && Character.isJavaIdentifierPart((char) _decodeCharForError(ch))) {
            this._inputPtr++;
            _reportInvalidToken(matchStr.substring(0, i), "'null', 'true', 'false' or NaN");
        }
    }

    private void _reportInvalidToken(String matchedPart, String msg) throws IOException, JsonParseException {
        StringBuilder sb = new StringBuilder(matchedPart);
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                break;
            }
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            int i2 = bArr[i];
            char c = (char) _decodeCharForError(i2);
            if (!Character.isJavaIdentifierPart(c)) {
                break;
            } else {
                sb.append(c);
            }
        }
        throw _constructError("Unrecognized token '" + sb.toString() + "': was expecting " + msg);
    }

    private int _skipWS() throws IOException, JsonParseException {
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                byte[] bArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = bArr[i] & 255;
                if (i2 > 32) {
                    if (i2 != 47) {
                        return i2;
                    }
                    _skipComment();
                } else if (i2 != 32) {
                    if (i2 == 10) {
                        _skipLF();
                    } else if (i2 == 13) {
                        _skipCR();
                    } else if (i2 != 9) {
                        _throwInvalidSpace(i2);
                    }
                }
            } else {
                throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.getTypeDesc() + " entries");
            }
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:57:0x008f. Please report as an issue. */
    private void _skipComment() throws IOException, JsonParseException {
        if (!isEnabled(JsonParser.Feature.ALLOW_COMMENTS)) {
            _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
        }
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(" in a comment");
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int c = bArr[i] & 255;
        if (c != 47) {
            if (c != 42) {
                _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
                return;
            }
            int[] inputCodeComment = CharTypes.getInputCodeComment();
            while (true) {
                if (this._inputPtr < this._inputEnd || loadMore()) {
                    byte[] bArr2 = this._inputBuffer;
                    int i2 = this._inputPtr;
                    this._inputPtr = i2 + 1;
                    int i3 = bArr2[i2] & 255;
                    int i4 = inputCodeComment[i3];
                    if (i4 != 0) {
                        switch (i4) {
                            case 2:
                                _skipUtf8_2$13462e();
                                break;
                            case 3:
                                _skipUtf8_3$13462e();
                                break;
                            case 4:
                                _skipUtf8_4$13462e();
                                break;
                            case 10:
                                _skipLF();
                                break;
                            case 13:
                                _skipCR();
                                break;
                            case 42:
                                if (this._inputPtr >= this._inputEnd && !loadMore()) {
                                    break;
                                } else if (this._inputBuffer[this._inputPtr] != 47) {
                                    break;
                                } else {
                                    this._inputPtr++;
                                    return;
                                }
                            default:
                                _reportInvalidChar(i3);
                                break;
                        }
                    }
                }
            }
            _reportInvalidEOF(" in a comment");
            return;
        }
        int[] inputCodeComment2 = CharTypes.getInputCodeComment();
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                byte[] bArr3 = this._inputBuffer;
                int i5 = this._inputPtr;
                this._inputPtr = i5 + 1;
                int i6 = bArr3[i5] & 255;
                int i7 = inputCodeComment2[i6];
                if (i7 != 0) {
                    switch (i7) {
                        case 2:
                            _skipUtf8_2$13462e();
                            break;
                        case 3:
                            _skipUtf8_3$13462e();
                            break;
                        case 4:
                            _skipUtf8_4$13462e();
                            break;
                        case 10:
                            _skipLF();
                            return;
                        case 13:
                            _skipCR();
                            return;
                        case 42:
                            break;
                        default:
                            _reportInvalidChar(i6);
                            break;
                    }
                }
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.ParserBase
    public final char _decodeEscaped() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(" in character escape sequence");
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int c = bArr[i];
        switch (c) {
            case 34:
            case 47:
            case 92:
                return (char) c;
            case 98:
                return '\b';
            case 102:
                return '\f';
            case 110:
                return '\n';
            case 114:
                return '\r';
            case 116:
                return '\t';
            case 117:
                int value = 0;
                for (int i2 = 0; i2 < 4; i2++) {
                    if (this._inputPtr >= this._inputEnd && !loadMore()) {
                        _reportInvalidEOF(" in character escape sequence");
                    }
                    byte[] bArr2 = this._inputBuffer;
                    int i3 = this._inputPtr;
                    this._inputPtr = i3 + 1;
                    int ch = bArr2[i3];
                    int digit = CharTypes.charToHex(ch);
                    if (digit < 0) {
                        _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
                    }
                    value = (value << 4) | digit;
                }
                return (char) value;
            default:
                return _handleUnrecognizedCharacterEscape((char) _decodeCharForError(c));
        }
    }

    private int _decodeCharForError(int firstByte) throws IOException, JsonParseException {
        int needed;
        int c = firstByte;
        if (firstByte < 0) {
            if ((c & 224) == 192) {
                c &= 31;
                needed = 1;
            } else if ((c & R.styleable.ThemeTemplate_pressableBackgroundHighlight) == 224) {
                c &= 15;
                needed = 2;
            } else if ((c & R.styleable.ThemeTemplate_chineseStroke1) == 240) {
                c &= 7;
                needed = 3;
            } else {
                _reportInvalidInitial(c & 255);
                needed = 1;
            }
            int d = nextByte();
            if ((d & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
                _reportInvalidOther(d & 255);
            }
            int c2 = (c << 6) | (d & 63);
            if (needed > 1) {
                int d2 = nextByte();
                if ((d2 & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
                    _reportInvalidOther(d2 & 255);
                }
                int c3 = (c2 << 6) | (d2 & 63);
                if (needed > 2) {
                    int d3 = nextByte();
                    if ((d3 & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
                        _reportInvalidOther(d3 & 255);
                    }
                    return (c3 << 6) | (d3 & 63);
                }
                return c3;
            }
            return c2;
        }
        return c;
    }

    private int _decodeUtf8_2(int c) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int d = bArr[i];
        if ((d & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(d & 255, this._inputPtr);
        }
        return ((c & 31) << 6) | (d & 63);
    }

    private int _decodeUtf8_3(int c1) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        int c12 = c1 & 15;
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int d = bArr[i];
        if ((d & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(d & 255, this._inputPtr);
        }
        int c = (c12 << 6) | (d & 63);
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        int d2 = bArr2[i2];
        if ((d2 & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(d2 & 255, this._inputPtr);
        }
        return (c << 6) | (d2 & 63);
    }

    private int _decodeUtf8_3fast(int c1) throws IOException, JsonParseException {
        int c12 = c1 & 15;
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int d = bArr[i];
        if ((d & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(d & 255, this._inputPtr);
        }
        int c = (c12 << 6) | (d & 63);
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        int d2 = bArr2[i2];
        if ((d2 & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(d2 & 255, this._inputPtr);
        }
        return (c << 6) | (d2 & 63);
    }

    private int _decodeUtf8_4(int c) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int d = bArr[i];
        if ((d & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(d & 255, this._inputPtr);
        }
        int c2 = ((c & 7) << 6) | (d & 63);
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        int d2 = bArr2[i2];
        if ((d2 & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(d2 & 255, this._inputPtr);
        }
        int c3 = (c2 << 6) | (d2 & 63);
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr3 = this._inputBuffer;
        int i3 = this._inputPtr;
        this._inputPtr = i3 + 1;
        int d3 = bArr3[i3];
        if ((d3 & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(d3 & 255, this._inputPtr);
        }
        return ((c3 << 6) | (d3 & 63)) - 65536;
    }

    private void _skipUtf8_2$13462e() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int c = bArr[i];
        if ((c & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(c & 255, this._inputPtr);
        }
    }

    private void _skipUtf8_3$13462e() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int c = bArr[i];
        if ((c & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(c & 255, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        int c2 = bArr2[i2];
        if ((c2 & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(c2 & 255, this._inputPtr);
        }
    }

    private void _skipUtf8_4$13462e() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int d = bArr[i];
        if ((d & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(d & 255, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        int d2 = bArr2[i2];
        if ((d2 & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(d2 & 255, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr3 = this._inputBuffer;
        int i3 = this._inputPtr;
        this._inputPtr = i3 + 1;
        int d3 = bArr3[i3];
        if ((d3 & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 128) {
            _reportInvalidOther(d3 & 255, this._inputPtr);
        }
    }

    private void _skipCR() throws IOException {
        if ((this._inputPtr < this._inputEnd || loadMore()) && this._inputBuffer[this._inputPtr] == 10) {
            this._inputPtr++;
        }
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    private void _skipLF() throws IOException {
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    private int nextByte() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        return bArr[i] & 255;
    }

    private void _reportInvalidChar(int c) throws JsonParseException {
        if (c < 32) {
            _throwInvalidSpace(c);
        }
        _reportInvalidInitial(c);
    }

    private void _reportInvalidInitial(int mask) throws JsonParseException {
        throw _constructError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
    }

    private void _reportInvalidOther(int mask) throws JsonParseException {
        throw _constructError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
    }

    private void _reportInvalidOther(int mask, int ptr) throws JsonParseException {
        this._inputPtr = ptr;
        _reportInvalidOther(mask);
    }

    private static int[] growArrayBy(int[] arr, int more) {
        if (arr == null) {
            return new int[more];
        }
        int len = arr.length;
        int[] arr2 = new int[len + more];
        System.arraycopy(arr, 0, arr2, 0, len);
        return arr2;
    }
}
