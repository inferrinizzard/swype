package com.fasterxml.jackson.core;

import com.nuance.connect.sqlite.ChinesePredictionDataSource;
import java.io.Serializable;

/* loaded from: classes.dex */
public final class JsonLocation implements Serializable {
    public static final JsonLocation NA = new JsonLocation("N/A", -1, -1, -1, (byte) 0);
    final int _columnNr;
    final int _lineNr;
    final Object _sourceRef;
    final long _totalBytes;
    final long _totalChars;

    public JsonLocation(Object srcRef, long totalChars, int lineNr, int colNr) {
        this(srcRef, totalChars, lineNr, colNr, (byte) 0);
    }

    private JsonLocation(Object sourceRef, long totalChars, int lineNr, int columnNr, byte b) {
        this._sourceRef = sourceRef;
        this._totalBytes = -1L;
        this._totalChars = totalChars;
        this._lineNr = lineNr;
        this._columnNr = columnNr;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(80);
        sb.append("[Source: ");
        if (this._sourceRef == null) {
            sb.append(ChinesePredictionDataSource.UNKNOWN);
        } else {
            sb.append(this._sourceRef.toString());
        }
        sb.append("; line: ");
        sb.append(this._lineNr);
        sb.append(", column: ");
        sb.append(this._columnNr);
        sb.append(']');
        return sb.toString();
    }

    public final int hashCode() {
        return ((((this._sourceRef == null ? 1 : this._sourceRef.hashCode()) ^ this._lineNr) + this._columnNr) ^ ((int) this._totalChars)) + ((int) this._totalBytes);
    }

    public final boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other != null && (other instanceof JsonLocation)) {
            JsonLocation otherLoc = (JsonLocation) other;
            if (this._sourceRef == null) {
                if (otherLoc._sourceRef != null) {
                    return false;
                }
            } else if (!this._sourceRef.equals(otherLoc._sourceRef)) {
                return false;
            }
            return this._lineNr == otherLoc._lineNr && this._columnNr == otherLoc._columnNr && this._totalChars == otherLoc._totalChars && this._totalBytes == otherLoc._totalBytes;
        }
        return false;
    }
}
