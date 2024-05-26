package com.fasterxml.jackson.core;

import java.io.IOException;

/* loaded from: classes.dex */
public class JsonProcessingException extends IOException {
    protected JsonLocation _location;

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonProcessingException(String msg, JsonLocation loc, Throwable rootCause) {
        super(msg);
        if (rootCause != null) {
            initCause(rootCause);
        }
        this._location = loc;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonProcessingException(String msg, JsonLocation loc) {
        this(msg, loc, null);
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        String msg = super.getMessage();
        if (msg == null) {
            msg = "N/A";
        }
        JsonLocation loc = this._location;
        if (loc != null) {
            StringBuilder sb = new StringBuilder(100);
            sb.append(msg);
            if (loc != null) {
                sb.append('\n');
                sb.append(" at ");
                sb.append(loc.toString());
            }
            return sb.toString();
        }
        return msg;
    }

    @Override // java.lang.Throwable
    public String toString() {
        return getClass().getName() + ": " + getMessage();
    }
}
