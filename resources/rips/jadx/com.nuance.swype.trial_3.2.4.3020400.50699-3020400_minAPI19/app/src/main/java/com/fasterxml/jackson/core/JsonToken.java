package com.fasterxml.jackson.core;

import com.facebook.internal.ServerProtocol;
import com.nuance.swypeconnect.ac.ACBuildConfigRuntime;

/* loaded from: classes.dex */
public enum JsonToken {
    NOT_AVAILABLE(null),
    START_OBJECT("{"),
    END_OBJECT("}"),
    START_ARRAY("["),
    END_ARRAY("]"),
    FIELD_NAME(null),
    VALUE_EMBEDDED_OBJECT(null),
    VALUE_STRING(null),
    VALUE_NUMBER_INT(null),
    VALUE_NUMBER_FLOAT(null),
    VALUE_TRUE(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE),
    VALUE_FALSE(ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED),
    VALUE_NULL("null");

    public final String _serialized;
    final byte[] _serializedBytes;
    final char[] _serializedChars;

    JsonToken(String token) {
        if (token == null) {
            this._serialized = null;
            this._serializedChars = null;
            this._serializedBytes = null;
            return;
        }
        this._serialized = token;
        this._serializedChars = token.toCharArray();
        int len = this._serializedChars.length;
        this._serializedBytes = new byte[len];
        for (int i = 0; i < len; i++) {
            this._serializedBytes[i] = (byte) this._serializedChars[i];
        }
    }
}
