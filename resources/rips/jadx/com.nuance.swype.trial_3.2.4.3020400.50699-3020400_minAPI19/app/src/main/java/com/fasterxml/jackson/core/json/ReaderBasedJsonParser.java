package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
import com.fasterxml.jackson.core.util.TextBuffer;
import java.io.IOException;
import java.io.Reader;

/* loaded from: classes.dex */
public final class ReaderBasedJsonParser extends ParserBase {
    protected final int _hashSeed;
    protected char[] _inputBuffer;
    protected ObjectCodec _objectCodec;
    protected Reader _reader;
    protected final CharsToNameCanonicalizer _symbols;
    protected boolean _tokenIncomplete;

    public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st) {
        super(ctxt, features);
        this._tokenIncomplete = false;
        this._reader = r;
        this._inputBuffer = ctxt.allocTokenBuffer();
        this._objectCodec = codec;
        this._symbols = st;
        this._hashSeed = st._hashSeed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.ParserBase
    public final boolean loadMore() throws IOException {
        this._currInputProcessed += this._inputEnd;
        this._currInputRowStart -= this._inputEnd;
        if (this._reader == null) {
            return false;
        }
        int count = this._reader.read(this._inputBuffer, 0, this._inputBuffer.length);
        if (count > 0) {
            this._inputPtr = 0;
            this._inputEnd = count;
            return true;
        }
        _closeInput();
        if (count == 0) {
            throw new IOException("Reader returned 0 characters when trying to read " + this._inputEnd);
        }
        return false;
    }

    private char getNextChar(String eofMsg) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(eofMsg);
        }
        char[] cArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        return cArr[i];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.ParserBase
    public final void _closeInput() throws IOException {
        if (this._reader != null) {
            if (this._ioContext.isResourceManaged() || isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
                this._reader.close();
            }
            this._reader = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.ParserBase
    public final void _releaseBuffers() throws IOException {
        super._releaseBuffers();
        char[] buf = this._inputBuffer;
        if (buf != null) {
            this._inputBuffer = null;
            this._ioContext.releaseTokenBuffer(buf);
        }
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public final String getText() throws IOException, JsonParseException {
        JsonToken t = this._currToken;
        if (t == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
                this._tokenIncomplete = false;
                _finishString();
            }
            return this._textBuffer.contentsAsString();
        }
        if (t == null) {
            return null;
        }
        switch (t) {
            case FIELD_NAME:
                return this._parsingContext.getCurrentName();
            case VALUE_STRING:
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
                return this._textBuffer.contentsAsString();
            default:
                return t._serialized;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:74:0x0178. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:75:0x017b. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:78:0x0187  */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0229  */
    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final com.fasterxml.jackson.core.JsonToken nextToken() throws java.io.IOException, com.fasterxml.jackson.core.JsonParseException {
        /*
            Method dump skipped, instructions count: 650
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.ReaderBasedJsonParser.nextToken():com.fasterxml.jackson.core.JsonToken");
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase, com.fasterxml.jackson.core.JsonParser, java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        super.close();
        this._symbols.release();
    }

    /*  JADX ERROR: JadxRuntimeException in pass: InitCodeVariables
        jadx.core.utils.exceptions.JadxRuntimeException: Several immutable types in one variable: [int, char], vars: [r23v0 ??, r23v1 ??, r23v10 ??]
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVarType(InitCodeVariables.java:107)
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVar(InitCodeVariables.java:83)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVar(InitCodeVariables.java:74)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVar(InitCodeVariables.java:57)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVars(InitCodeVariables.java:45)
        	at jadx.core.dex.visitors.InitCodeVariables.visit(InitCodeVariables.java:29)
        */
    private com.fasterxml.jackson.core.JsonToken parseNumberText(
    /*  JADX ERROR: JadxRuntimeException in pass: InitCodeVariables
        jadx.core.utils.exceptions.JadxRuntimeException: Several immutable types in one variable: [int, char], vars: [r23v0 ??, r23v1 ??, r23v10 ??]
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVarType(InitCodeVariables.java:107)
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVar(InitCodeVariables.java:83)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVar(InitCodeVariables.java:74)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVar(InitCodeVariables.java:57)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVars(InitCodeVariables.java:45)
        */
    /*  JADX ERROR: Method generation error
        jadx.core.utils.exceptions.JadxRuntimeException: Code variable not set in r23v0 ??
        	at jadx.core.dex.instructions.args.SSAVar.getCodeVar(SSAVar.java:237)
        	at jadx.core.codegen.MethodGen.addMethodArguments(MethodGen.java:223)
        	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:168)
        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:401)
        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:335)
        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$3(ClassGen.java:301)
        	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(Unknown Source)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at java.base/java.util.stream.SortedOps$RefSortingSink.end(Unknown Source)
        	at java.base/java.util.stream.Sink$ChainedReference.end(Unknown Source)
        */

    /*  JADX ERROR: JadxRuntimeException in pass: InitCodeVariables
        jadx.core.utils.exceptions.JadxRuntimeException: Several immutable types in one variable: [int, char], vars: [r10v0 ??, r10v1 ??, r10v2 ??]
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVarType(InitCodeVariables.java:107)
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVar(InitCodeVariables.java:83)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVar(InitCodeVariables.java:74)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVar(InitCodeVariables.java:57)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVars(InitCodeVariables.java:45)
        	at jadx.core.dex.visitors.InitCodeVariables.visit(InitCodeVariables.java:29)
        */
    private com.fasterxml.jackson.core.JsonToken _handleInvalidNumberStart(
    /*  JADX ERROR: JadxRuntimeException in pass: InitCodeVariables
        jadx.core.utils.exceptions.JadxRuntimeException: Several immutable types in one variable: [int, char], vars: [r10v0 ??, r10v1 ??, r10v2 ??]
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVarType(InitCodeVariables.java:107)
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVar(InitCodeVariables.java:83)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVar(InitCodeVariables.java:74)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVar(InitCodeVariables.java:57)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVars(InitCodeVariables.java:45)
        */
    /*  JADX ERROR: Method generation error
        jadx.core.utils.exceptions.JadxRuntimeException: Code variable not set in r10v0 ??
        	at jadx.core.dex.instructions.args.SSAVar.getCodeVar(SSAVar.java:237)
        	at jadx.core.codegen.MethodGen.addMethodArguments(MethodGen.java:223)
        	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:168)
        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:401)
        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:335)
        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$3(ClassGen.java:301)
        	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(Unknown Source)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at java.base/java.util.stream.SortedOps$RefSortingSink.end(Unknown Source)
        	at java.base/java.util.stream.Sink$ChainedReference.end(Unknown Source)
        */

    /* JADX WARN: Code restructure failed: missing block: B:41:0x00af, code lost:            if (r10 < r12) goto L37;     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00b1, code lost:            r14 = r17._inputBuffer[r10];     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00b7, code lost:            if (r14 >= r11) goto L44;     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00bb, code lost:            if (r13[r14] == 0) goto L47;     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0100, code lost:            r9 = (r9 * 33) + r14;        r10 = r10 + 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x0105, code lost:            if (r10 < r12) goto L85;     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x00bd, code lost:            r11 = r17._inputPtr - 1;        r17._inputPtr = r10;     */
    /* JADX WARN: Code restructure failed: missing block: B:52:?, code lost:            return r17._symbols.findSymbol(r17._inputBuffer, r11, r10 - r11, r9);     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00e5, code lost:            if (java.lang.Character.isJavaIdentifierPart(r14) != false) goto L47;     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00e7, code lost:            r11 = r17._inputPtr - 1;        r17._inputPtr = r10;     */
    /* JADX WARN: Code restructure failed: missing block: B:57:?, code lost:            return r17._symbols.findSymbol(r17._inputBuffer, r11, r10 - r11, r9);     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0107, code lost:            r11 = r17._inputPtr - 1;        r17._inputPtr = r10;        r17._textBuffer.resetWithShared(r17._inputBuffer, r11, r17._inputPtr - r11);        r11 = r17._textBuffer.getCurrentSegment();        r10 = r17._textBuffer._currentSize;        r14 = r13.length;        r10 = r11;        r11 = r9;        r9 = r10;     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x013e, code lost:            if (r17._inputPtr < r17._inputEnd) goto L54;     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x0144, code lost:            if (loadMore() == false) goto L86;     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x0156, code lost:            r17._textBuffer._currentSize = r9;        r9 = r17._textBuffer;     */
    /* JADX WARN: Code restructure failed: missing block: B:65:?, code lost:            return r17._symbols.findSymbol(r9.getTextBuffer(), r9.getTextOffset(), r9.size(), r11);     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x0146, code lost:            r15 = r17._inputBuffer[r17._inputPtr];     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x0150, code lost:            if (r15 > r14) goto L59;     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x0154, code lost:            if (r13[r15] == 0) goto L61;     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x017c, code lost:            r17._inputPtr++;        r11 = (r11 * 33) + r15;        r12 = r9 + 1;        r10[r9] = r15;     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x018e, code lost:            if (r12 < r10.length) goto L87;     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x01f1, code lost:            r9 = r12;     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x0190, code lost:            r10 = r17._textBuffer.finishCurrentSegment();        r9 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x017a, code lost:            if (java.lang.Character.isJavaIdentifierPart(r15) == false) goto L89;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.lang.String _parseFieldName(int r18) throws java.io.IOException, com.fasterxml.jackson.core.JsonParseException {
        /*
            Method dump skipped, instructions count: 500
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.ReaderBasedJsonParser._parseFieldName(int):java.lang.String");
    }

    private String _parseFieldName2(int startPtr, int hash, int endChar) throws IOException, JsonParseException {
        this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
        char[] outBuf = this._textBuffer.getCurrentSegment();
        int outPtr = this._textBuffer._currentSize;
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(": was expecting closing '" + ((char) endChar) + "' for name");
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char c = cArr[i];
            if (c <= '\\') {
                if (c == '\\') {
                    c = _decodeEscaped();
                } else if (c <= endChar) {
                    if (c != endChar) {
                        if (c < ' ') {
                            _throwUnquotedSpace(c, "name");
                        }
                    } else {
                        this._textBuffer._currentSize = outPtr;
                        TextBuffer tb = this._textBuffer;
                        char[] buf = tb.getTextBuffer();
                        int start = tb.getTextOffset();
                        int len = tb.size();
                        return this._symbols.findSymbol(buf, start, len, hash);
                    }
                }
            }
            hash = (hash * 33) + c;
            int outPtr2 = outPtr + 1;
            outBuf[outPtr] = c;
            if (outPtr2 >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            } else {
                outPtr = outPtr2;
            }
        }
    }

    private JsonToken _handleApostropheValue() throws IOException, JsonParseException {
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int outPtr = this._textBuffer._currentSize;
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(": was expecting closing quote for a string value");
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char c = cArr[i];
            if (c <= '\\') {
                if (c == '\\') {
                    c = _decodeEscaped();
                } else if (c <= '\'') {
                    if (c != '\'') {
                        if (c < ' ') {
                            _throwUnquotedSpace(c, "string value");
                        }
                    } else {
                        this._textBuffer._currentSize = outPtr;
                        return JsonToken.VALUE_STRING;
                    }
                }
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr] = c;
            outPtr++;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.ParserBase
    public final void _finishString() throws IOException, JsonParseException {
        int ptr = this._inputPtr;
        int inputLen = this._inputEnd;
        if (ptr < inputLen) {
            int[] codes = CharTypes.getInputCodeLatin1();
            int maxCode = codes.length;
            while (true) {
                char c = this._inputBuffer[ptr];
                if (c < maxCode && codes[c] != 0) {
                    if (c == '\"') {
                        this._textBuffer.resetWithShared(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
                        this._inputPtr = ptr + 1;
                        return;
                    }
                } else {
                    ptr++;
                    if (ptr >= inputLen) {
                        break;
                    }
                }
            }
        }
        TextBuffer textBuffer = this._textBuffer;
        char[] cArr = this._inputBuffer;
        int i = this._inputPtr;
        int i2 = ptr - this._inputPtr;
        textBuffer._inputBuffer = null;
        textBuffer._inputStart = -1;
        textBuffer._inputLen = 0;
        textBuffer._resultString = null;
        textBuffer._resultArray = null;
        if (textBuffer._hasSegments) {
            textBuffer.clearSegments();
        } else if (textBuffer._currentSegment == null) {
            textBuffer._currentSegment = textBuffer.findBuffer(i2);
        }
        textBuffer._segmentSize = 0;
        textBuffer._currentSize = 0;
        if (textBuffer._inputStart >= 0) {
            textBuffer.unshare(i2);
        }
        textBuffer._resultString = null;
        textBuffer._resultArray = null;
        char[] cArr2 = textBuffer._currentSegment;
        int length = cArr2.length - textBuffer._currentSize;
        if (length >= i2) {
            System.arraycopy(cArr, i, cArr2, textBuffer._currentSize, i2);
            textBuffer._currentSize = i2 + textBuffer._currentSize;
        } else {
            if (length > 0) {
                System.arraycopy(cArr, i, cArr2, textBuffer._currentSize, length);
                i += length;
                i2 -= length;
            }
            do {
                textBuffer.expand(i2);
                int min = Math.min(textBuffer._currentSegment.length, i2);
                System.arraycopy(cArr, i, textBuffer._currentSegment, 0, min);
                textBuffer._currentSize += min;
                i += min;
                i2 -= min;
            } while (i2 > 0);
        }
        this._inputPtr = ptr;
        char[] currentSegment = this._textBuffer.getCurrentSegment();
        int i3 = this._textBuffer._currentSize;
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(": was expecting closing quote for a string value");
            }
            char[] cArr3 = this._inputBuffer;
            int i4 = this._inputPtr;
            this._inputPtr = i4 + 1;
            char c2 = cArr3[i4];
            if (c2 <= '\\') {
                if (c2 == '\\') {
                    c2 = _decodeEscaped();
                } else if (c2 <= '\"') {
                    if (c2 != '\"') {
                        if (c2 < ' ') {
                            _throwUnquotedSpace(c2, "string value");
                        }
                    } else {
                        this._textBuffer._currentSize = i3;
                        return;
                    }
                }
            }
            if (i3 >= currentSegment.length) {
                currentSegment = this._textBuffer.finishCurrentSegment();
                i3 = 0;
            }
            currentSegment[i3] = c2;
            i3++;
        }
    }

    private void _skipCR() throws IOException {
        if ((this._inputPtr < this._inputEnd || loadMore()) && this._inputBuffer[this._inputPtr] == '\n') {
            this._inputPtr++;
        }
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    private void _skipLF() throws IOException {
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    private int _skipWS() throws IOException, JsonParseException {
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                char[] cArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                char c = cArr[i];
                if (c > ' ') {
                    if (c != '/') {
                        return c;
                    }
                    _skipComment();
                } else if (c != ' ') {
                    if (c == '\n') {
                        _skipLF();
                    } else if (c == '\r') {
                        _skipCR();
                    } else if (c != '\t') {
                        _throwInvalidSpace(c);
                    }
                }
            } else {
                throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.getTypeDesc() + " entries");
            }
        }
    }

    private void _skipComment() throws IOException, JsonParseException {
        if (!isEnabled(JsonParser.Feature.ALLOW_COMMENTS)) {
            _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
        }
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(" in a comment");
        }
        char[] cArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        char c = cArr[i];
        if (c != '/') {
            if (c != '*') {
                _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
                return;
            }
            while (true) {
                if (this._inputPtr >= this._inputEnd && !loadMore()) {
                    break;
                }
                char[] cArr2 = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                char c2 = cArr2[i2];
                if (c2 <= '*') {
                    if (c2 == '*') {
                        if (this._inputPtr >= this._inputEnd && !loadMore()) {
                            break;
                        } else if (this._inputBuffer[this._inputPtr] == '/') {
                            this._inputPtr++;
                            return;
                        }
                    } else if (c2 < ' ') {
                        if (c2 == '\n') {
                            _skipLF();
                        } else if (c2 == '\r') {
                            _skipCR();
                        } else if (c2 != '\t') {
                            _throwInvalidSpace(c2);
                        }
                    }
                }
            }
            _reportInvalidEOF(" in a comment");
            return;
        }
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                char[] cArr3 = this._inputBuffer;
                int i3 = this._inputPtr;
                this._inputPtr = i3 + 1;
                char c3 = cArr3[i3];
                if (c3 < ' ') {
                    if (c3 == '\n') {
                        _skipLF();
                        return;
                    } else if (c3 == '\r') {
                        _skipCR();
                        return;
                    } else if (c3 != '\t') {
                        _throwInvalidSpace(c3);
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
        char[] cArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        char c = cArr[i];
        switch (c) {
            case '\"':
            case '/':
            case '\\':
                return c;
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'u':
                int value = 0;
                for (int i2 = 0; i2 < 4; i2++) {
                    if (this._inputPtr >= this._inputEnd && !loadMore()) {
                        _reportInvalidEOF(" in character escape sequence");
                    }
                    char[] cArr2 = this._inputBuffer;
                    int i3 = this._inputPtr;
                    this._inputPtr = i3 + 1;
                    char c2 = cArr2[i3];
                    int digit = CharTypes.charToHex(c2);
                    if (digit < 0) {
                        _reportUnexpectedChar(c2, "expected a hex-digit for character escape sequence");
                    }
                    value = (value << 4) | digit;
                }
                return (char) value;
            default:
                return _handleUnrecognizedCharacterEscape(c);
        }
    }

    private void _matchToken(String matchStr, int i) throws IOException, JsonParseException {
        char c;
        int len = matchStr.length();
        do {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(" in a value");
            }
            if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
                _reportInvalidToken$16da05f7(matchStr.substring(0, i));
            }
            this._inputPtr++;
            i++;
        } while (i < len);
        if ((this._inputPtr < this._inputEnd || loadMore()) && (c = this._inputBuffer[this._inputPtr]) >= '0' && c != ']' && c != '}' && Character.isJavaIdentifierPart(c)) {
            _reportInvalidToken$16da05f7(matchStr.substring(0, i));
        }
    }

    private void _reportInvalidToken$16da05f7(String matchedPart) throws IOException, JsonParseException {
        StringBuilder sb = new StringBuilder(matchedPart);
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                break;
            }
            char c = this._inputBuffer[this._inputPtr];
            if (!Character.isJavaIdentifierPart(c)) {
                break;
            }
            this._inputPtr++;
            sb.append(c);
        }
        throw _constructError("Unrecognized token '" + sb.toString() + "': was expecting ");
    }
}
