package com.fasterxml.jackson.core;

/* loaded from: classes.dex */
public final class JsonParseException extends JsonProcessingException {
    public JsonParseException(String msg, JsonLocation loc) {
        super(msg, loc);
    }

    public JsonParseException(String msg, JsonLocation loc, Throwable root) {
        super(msg, loc, root);
    }
}
