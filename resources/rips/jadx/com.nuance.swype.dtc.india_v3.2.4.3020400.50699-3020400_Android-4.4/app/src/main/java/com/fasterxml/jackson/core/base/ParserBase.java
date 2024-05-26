package com.fasterxml.jackson.core.base;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.json.JsonReadContext;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.TextBuffer;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: classes.dex */
public abstract class ParserBase extends ParserMinimalBase {
    public byte[] _binaryValue;
    protected boolean _closed;
    protected int _expLength;
    protected int _fractLength;
    protected int _intLength;
    public final IOContext _ioContext;
    public JsonToken _nextToken;
    protected BigDecimal _numberBigDecimal;
    protected BigInteger _numberBigInt;
    protected double _numberDouble;
    protected int _numberInt;
    protected long _numberLong;
    protected boolean _numberNegative;
    public JsonReadContext _parsingContext;
    public final TextBuffer _textBuffer;
    static final BigInteger BI_MIN_INT = BigInteger.valueOf(-2147483648L);
    static final BigInteger BI_MAX_INT = BigInteger.valueOf(2147483647L);
    static final BigInteger BI_MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
    static final BigInteger BI_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    static final BigDecimal BD_MIN_LONG = new BigDecimal(BI_MIN_LONG);
    static final BigDecimal BD_MAX_LONG = new BigDecimal(BI_MAX_LONG);
    static final BigDecimal BD_MIN_INT = new BigDecimal(BI_MIN_INT);
    static final BigDecimal BD_MAX_INT = new BigDecimal(BI_MAX_INT);
    public int _inputPtr = 0;
    public int _inputEnd = 0;
    public long _currInputProcessed = 0;
    public int _currInputRow = 1;
    public int _currInputRowStart = 0;
    public long _tokenInputTotal = 0;
    public int _tokenInputRow = 1;
    public int _tokenInputCol = 0;
    protected char[] _nameCopyBuffer = null;
    public boolean _nameCopied = false;
    protected ByteArrayBuilder _byteArrayBuilder = null;
    public int _numTypesValid = 0;

    public abstract void _closeInput() throws IOException;

    public abstract void _finishString() throws IOException, JsonParseException;

    public abstract boolean loadMore() throws IOException;

    public ParserBase(IOContext ctxt, int features) {
        this._features = features;
        this._ioContext = ctxt;
        this._textBuffer = ctxt.constructTextBuffer();
        this._parsingContext = new JsonReadContext(null, 0, 1, 0);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public final String getCurrentName() throws IOException, JsonParseException {
        return (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) ? this._parsingContext.getParent().getCurrentName() : this._parsingContext.getCurrentName();
    }

    @Override // com.fasterxml.jackson.core.JsonParser, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (!this._closed) {
            this._closed = true;
            try {
                _closeInput();
            } finally {
                _releaseBuffers();
            }
        }
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public final JsonLocation getCurrentLocation() {
        int col = (this._inputPtr - this._currInputRowStart) + 1;
        return new JsonLocation(this._ioContext.getSourceReference(), (this._currInputProcessed + this._inputPtr) - 1, this._currInputRow, col);
    }

    public final void loadMoreGuaranteed() throws IOException {
        if (loadMore()) {
            return;
        }
        _reportInvalidEOF(" in " + this._currToken);
    }

    public void _releaseBuffers() throws IOException {
        TextBuffer textBuffer = this._textBuffer;
        if (textBuffer._allocator == null) {
            textBuffer.resetWithEmpty();
        } else if (textBuffer._currentSegment != null) {
            textBuffer.resetWithEmpty();
            char[] cArr = textBuffer._currentSegment;
            textBuffer._currentSegment = null;
            textBuffer._allocator.releaseCharBuffer(BufferRecycler.CharBufferType.TEXT_BUFFER, cArr);
        }
        char[] buf = this._nameCopyBuffer;
        if (buf != null) {
            this._nameCopyBuffer = null;
            this._ioContext.releaseNameCopyBuffer(buf);
        }
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase
    public final void _handleEOF() throws JsonParseException {
        if (!(this._parsingContext._type == 0)) {
            _reportInvalidEOF(": expected close marker for " + this._parsingContext.getTypeDesc() + " (from " + this._parsingContext.getStartLocation(this._ioContext.getSourceReference()) + ")");
        }
    }

    public final void _reportMismatchedEndMarker(int actCh, char expCh) throws JsonParseException {
        String startDesc = new StringBuilder().append(this._parsingContext.getStartLocation(this._ioContext.getSourceReference())).toString();
        throw _constructError("Unexpected close marker '" + ((char) actCh) + "': expected '" + expCh + "' (for " + this._parsingContext.getTypeDesc() + " starting at " + startDesc + ")");
    }

    public final JsonToken reset(boolean negative, int intLen, int fractLen, int expLen) {
        return (fractLen > 0 || expLen > 0) ? resetFloat(negative, intLen, fractLen, expLen) : resetInt(negative, intLen);
    }

    public final JsonToken resetInt(boolean negative, int intLen) {
        this._numberNegative = negative;
        this._intLength = intLen;
        this._fractLength = 0;
        this._expLength = 0;
        this._numTypesValid = 0;
        return JsonToken.VALUE_NUMBER_INT;
    }

    public final JsonToken resetFloat(boolean negative, int intLen, int fractLen, int expLen) {
        this._numberNegative = negative;
        this._intLength = intLen;
        this._fractLength = fractLen;
        this._expLength = expLen;
        this._numTypesValid = 0;
        return JsonToken.VALUE_NUMBER_FLOAT;
    }

    public final JsonToken resetAsNaN(String valueStr, double value) {
        TextBuffer textBuffer = this._textBuffer;
        textBuffer._inputBuffer = null;
        textBuffer._inputStart = -1;
        textBuffer._inputLen = 0;
        textBuffer._resultString = valueStr;
        textBuffer._resultArray = null;
        if (textBuffer._hasSegments) {
            textBuffer.clearSegments();
        }
        textBuffer._currentSize = 0;
        this._numberDouble = value;
        this._numTypesValid = 8;
        return JsonToken.VALUE_NUMBER_FLOAT;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public final int getIntValue() throws IOException, JsonParseException {
        if ((this._numTypesValid & 1) == 0) {
            if (this._numTypesValid == 0) {
                _parseNumericValue(1);
            }
            if ((this._numTypesValid & 1) == 0) {
                if ((this._numTypesValid & 2) != 0) {
                    int i = (int) this._numberLong;
                    if (i == this._numberLong) {
                        this._numberInt = i;
                    } else {
                        throw _constructError("Numeric value (" + getText() + ") out of range of int");
                    }
                } else if ((this._numTypesValid & 4) != 0) {
                    if (BI_MIN_INT.compareTo(this._numberBigInt) > 0 || BI_MAX_INT.compareTo(this._numberBigInt) < 0) {
                        reportOverflowInt();
                    }
                    this._numberInt = this._numberBigInt.intValue();
                } else if ((this._numTypesValid & 8) != 0) {
                    if (this._numberDouble < -2.147483648E9d || this._numberDouble > 2.147483647E9d) {
                        reportOverflowInt();
                    }
                    this._numberInt = (int) this._numberDouble;
                } else if ((this._numTypesValid & 16) != 0) {
                    if (BD_MIN_INT.compareTo(this._numberBigDecimal) > 0 || BD_MAX_INT.compareTo(this._numberBigDecimal) < 0) {
                        reportOverflowInt();
                    }
                    this._numberInt = this._numberBigDecimal.intValue();
                } else {
                    _throwInternal();
                }
                this._numTypesValid |= 1;
            }
        }
        return this._numberInt;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public final long getLongValue() throws IOException, JsonParseException {
        if ((this._numTypesValid & 2) == 0) {
            if (this._numTypesValid == 0) {
                _parseNumericValue(2);
            }
            if ((this._numTypesValid & 2) == 0) {
                if ((this._numTypesValid & 1) != 0) {
                    this._numberLong = this._numberInt;
                } else if ((this._numTypesValid & 4) != 0) {
                    if (BI_MIN_LONG.compareTo(this._numberBigInt) > 0 || BI_MAX_LONG.compareTo(this._numberBigInt) < 0) {
                        reportOverflowLong();
                    }
                    this._numberLong = this._numberBigInt.longValue();
                } else if ((this._numTypesValid & 8) != 0) {
                    if (this._numberDouble < -9.223372036854776E18d || this._numberDouble > 9.223372036854776E18d) {
                        reportOverflowLong();
                    }
                    this._numberLong = (long) this._numberDouble;
                } else if ((this._numTypesValid & 16) != 0) {
                    if (BD_MIN_LONG.compareTo(this._numberBigDecimal) > 0 || BD_MAX_LONG.compareTo(this._numberBigDecimal) < 0) {
                        reportOverflowLong();
                    }
                    this._numberLong = this._numberBigDecimal.longValue();
                } else {
                    _throwInternal();
                }
                this._numTypesValid |= 2;
            }
        }
        return this._numberLong;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public final BigInteger getBigIntegerValue() throws IOException, JsonParseException {
        if ((this._numTypesValid & 4) == 0) {
            if (this._numTypesValid == 0) {
                _parseNumericValue(4);
            }
            if ((this._numTypesValid & 4) == 0) {
                if ((this._numTypesValid & 16) != 0) {
                    this._numberBigInt = this._numberBigDecimal.toBigInteger();
                } else if ((this._numTypesValid & 2) != 0) {
                    this._numberBigInt = BigInteger.valueOf(this._numberLong);
                } else if ((this._numTypesValid & 1) != 0) {
                    this._numberBigInt = BigInteger.valueOf(this._numberInt);
                } else if ((this._numTypesValid & 8) != 0) {
                    this._numberBigInt = BigDecimal.valueOf(this._numberDouble).toBigInteger();
                } else {
                    _throwInternal();
                }
                this._numTypesValid |= 4;
            }
        }
        return this._numberBigInt;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public final float getFloatValue() throws IOException, JsonParseException {
        return (float) getDoubleValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public final double getDoubleValue() throws IOException, JsonParseException {
        if ((this._numTypesValid & 8) == 0) {
            if (this._numTypesValid == 0) {
                _parseNumericValue(8);
            }
            if ((this._numTypesValid & 8) == 0) {
                if ((this._numTypesValid & 16) != 0) {
                    this._numberDouble = this._numberBigDecimal.doubleValue();
                } else if ((this._numTypesValid & 4) != 0) {
                    this._numberDouble = this._numberBigInt.doubleValue();
                } else if ((this._numTypesValid & 2) != 0) {
                    this._numberDouble = this._numberLong;
                } else if ((this._numTypesValid & 1) != 0) {
                    this._numberDouble = this._numberInt;
                } else {
                    _throwInternal();
                }
                this._numTypesValid |= 8;
            }
        }
        return this._numberDouble;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public final BigDecimal getDecimalValue() throws IOException, JsonParseException {
        if ((this._numTypesValid & 16) == 0) {
            if (this._numTypesValid == 0) {
                _parseNumericValue(16);
            }
            if ((this._numTypesValid & 16) == 0) {
                if ((this._numTypesValid & 8) != 0) {
                    this._numberBigDecimal = new BigDecimal(getText());
                } else if ((this._numTypesValid & 4) != 0) {
                    this._numberBigDecimal = new BigDecimal(this._numberBigInt);
                } else if ((this._numTypesValid & 2) != 0) {
                    this._numberBigDecimal = BigDecimal.valueOf(this._numberLong);
                } else if ((this._numTypesValid & 1) != 0) {
                    this._numberBigDecimal = BigDecimal.valueOf(this._numberInt);
                } else {
                    _throwInternal();
                }
                this._numTypesValid |= 16;
            }
        }
        return this._numberBigDecimal;
    }

    private void _parseNumericValue(int expType) throws IOException, JsonParseException {
        BigDecimal bigDecimal;
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            char[] buf = this._textBuffer.getTextBuffer();
            int offset = this._textBuffer.getTextOffset();
            int len = this._intLength;
            if (this._numberNegative) {
                offset++;
            }
            if (len <= 9) {
                int i = NumberInput.parseInt(buf, offset, len);
                if (this._numberNegative) {
                    i = -i;
                }
                this._numberInt = i;
                this._numTypesValid = 1;
                return;
            }
            if (len <= 18) {
                long l = NumberInput.parseLong(buf, offset, len);
                if (this._numberNegative) {
                    l = -l;
                }
                if (len == 10) {
                    if (this._numberNegative) {
                        if (l >= -2147483648L) {
                            this._numberInt = (int) l;
                            this._numTypesValid = 1;
                            return;
                        }
                    } else if (l <= 2147483647L) {
                        this._numberInt = (int) l;
                        this._numTypesValid = 1;
                        return;
                    }
                }
                this._numberLong = l;
                this._numTypesValid = 2;
                return;
            }
            String contentsAsString = this._textBuffer.contentsAsString();
            try {
                if (NumberInput.inLongRange(buf, offset, len, this._numberNegative)) {
                    this._numberLong = Long.parseLong(contentsAsString);
                    this._numTypesValid = 2;
                } else {
                    this._numberBigInt = new BigInteger(contentsAsString);
                    this._numTypesValid = 4;
                }
                return;
            } catch (NumberFormatException e) {
                _wrapError("Malformed numeric value '" + contentsAsString + "'", e);
                return;
            }
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            try {
                if (expType == 16) {
                    TextBuffer textBuffer = this._textBuffer;
                    if (textBuffer._resultArray != null) {
                        bigDecimal = new BigDecimal(textBuffer._resultArray);
                    } else if (textBuffer._inputStart >= 0) {
                        bigDecimal = new BigDecimal(textBuffer._inputBuffer, textBuffer._inputStart, textBuffer._inputLen);
                    } else if (textBuffer._segmentSize == 0) {
                        bigDecimal = new BigDecimal(textBuffer._currentSegment, 0, textBuffer._currentSize);
                    } else {
                        bigDecimal = new BigDecimal(textBuffer.contentsAsArray());
                    }
                    this._numberBigDecimal = bigDecimal;
                    this._numTypesValid = 16;
                    return;
                }
                this._numberDouble = NumberInput.parseDouble(this._textBuffer.contentsAsString());
                this._numTypesValid = 8;
                return;
            } catch (NumberFormatException e2) {
                _wrapError("Malformed numeric value '" + this._textBuffer.contentsAsString() + "'", e2);
                return;
            }
        }
        throw _constructError("Current token (" + this._currToken + ") not numeric, can not use numeric value accessors");
    }

    public final void reportUnexpectedNumberChar(int ch, String comment) throws JsonParseException {
        String msg = "Unexpected character (" + _getCharDesc(ch) + ") in numeric value";
        throw _constructError(msg + ": " + comment);
    }

    public final void reportInvalidNumber(String msg) throws JsonParseException {
        throw _constructError("Invalid numeric value: " + msg);
    }

    private void reportOverflowInt() throws IOException, JsonParseException {
        throw _constructError("Numeric value (" + getText() + ") out of range of int (-2147483648 - 2147483647)");
    }

    private void reportOverflowLong() throws IOException, JsonParseException {
        throw _constructError("Numeric value (" + getText() + ") out of range of long (-9223372036854775808 - 9223372036854775807)");
    }

    public char _decodeEscaped() throws IOException, JsonParseException {
        throw new UnsupportedOperationException();
    }
}
