package com.fasterxml.jackson.core.base;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class ParserMinimalBase extends JsonParser {
    public JsonToken _currToken;

    protected abstract void _handleEOF() throws JsonParseException;

    @Override // com.fasterxml.jackson.core.JsonParser
    public abstract String getText() throws IOException, JsonParseException;

    @Override // com.fasterxml.jackson.core.JsonParser
    public abstract JsonToken nextToken() throws IOException, JsonParseException;

    @Override // com.fasterxml.jackson.core.JsonParser
    public final JsonToken getCurrentToken() {
        return this._currToken;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public final JsonParser skipChildren() throws IOException, JsonParseException {
        if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
            int open = 1;
            while (true) {
                JsonToken t = nextToken();
                if (t == null) {
                    _handleEOF();
                } else {
                    switch (t) {
                        case START_OBJECT:
                        case START_ARRAY:
                            open++;
                            break;
                        case END_OBJECT:
                        case END_ARRAY:
                            open--;
                            if (open != 0) {
                                break;
                            } else {
                                break;
                            }
                    }
                }
            }
        }
        return this;
    }

    public final void _reportUnexpectedChar(int ch, String comment) throws JsonParseException {
        String msg = "Unexpected character (" + _getCharDesc(ch) + ")";
        if (comment != null) {
            msg = msg + ": " + comment;
        }
        throw _constructError(msg);
    }

    public final void _reportInvalidEOF(String msg) throws JsonParseException {
        throw _constructError("Unexpected end-of-input" + msg);
    }

    public final void _throwInvalidSpace(int i) throws JsonParseException {
        char c = (char) i;
        String msg = "Illegal character (" + _getCharDesc(c) + "): only regular white space (\\r, \\n, \\t) is allowed between tokens";
        throw _constructError(msg);
    }

    public final void _throwUnquotedSpace(int i, String ctxtDesc) throws JsonParseException {
        if (!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS) || i >= 32) {
            char c = (char) i;
            String msg = "Illegal unquoted character (" + _getCharDesc(c) + "): has to be escaped using backslash to be included in " + ctxtDesc;
            throw _constructError(msg);
        }
    }

    public final char _handleUnrecognizedCharacterEscape(char ch) throws JsonProcessingException {
        if (isEnabled(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER) || (ch == '\'' && isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES))) {
            return ch;
        }
        throw _constructError("Unrecognized character escape " + _getCharDesc(ch));
    }

    public static final String _getCharDesc(int ch) {
        char c = (char) ch;
        if (Character.isISOControl(c)) {
            return "(CTRL-CHAR, code " + ch + ")";
        }
        if (ch > 255) {
            return "'" + c + "' (code " + ch + " / 0x" + Integer.toHexString(ch) + ")";
        }
        return "'" + c + "' (code " + ch + ")";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void _throwInternal() {
        throw new RuntimeException("Internal error: this code path should never get executed");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void _wrapError(String msg, Throwable t) throws JsonParseException {
        throw new JsonParseException(msg, getCurrentLocation(), t);
    }
}
